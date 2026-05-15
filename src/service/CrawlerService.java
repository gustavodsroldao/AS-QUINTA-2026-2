package service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import domain.EntityInterface;
import domain.Link;
import domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

public class CrawlerService implements AutoCloseable {

    private final ProductService productService;
    private final Playwright playwright;
    private final Browser browser;

    private static final Pattern PRICE_PATTERN =
            Pattern.compile("R\\$\\s*([\\d.]+),([\\d]{2})");

    private static final Pattern JSON_PRICE_PATTERN =
            Pattern.compile("\"price\"\\s*:\\s*\"?([\\d]+(?:\\.[\\d]+)?)\"?");

    private static final String[] PLAYWRIGHT_SELECTORS = {
        "span.a-offscreen",
        "h4.finalPrice",
        ".finalPrice",
        "[data-testid='price-value']",
        "[class*='sc-'][class*='price']",
        "[class*='Price'] span",
        "span.price-tag-fraction",
        ".andes-money-amount__fraction",
        ".price-template__text",
        "[class*='priceBox']",
        "[class*='product-price']",
    };

    public CrawlerService() {
        this.productService = new ProductService();
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setArgs(List.of(
                                "--disable-blink-features=AutomationControlled",
                                "--no-sandbox",
                                "--disable-dev-shm-usage"
                        ))
        );
    }

    @Override
    public void close() {
        browser.close();
        playwright.close();
    }

    public void crawlAll() {
        ArrayList<EntityInterface> entities = productService.getAll();
        if (entities.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (EntityInterface e : entities) {
            Product product = (Product) e;
            System.out.println("\n=== Buscando precos para: " + product.getName() + " ===");
            crawlProduct(product);
        }
    }

    private void crawlProduct(Product product) {
        List<Link> links = product.getLinks();
        if (links.isEmpty()) {
            System.out.println("  Nenhum link cadastrado para este produto.");
            return;
        }

        Float minPrice = null;
        String minStore = null;

        for (Link link : links) {
            System.out.print("  Buscando em " + link.getStore() + "... ");
            try {
                Float price = fetchPrice(link.getUrl());
                if (price != null) {
                    System.out.printf("R$ %.2f%n", price);
                    product.adicionarAoHistorico(price, link.getStore());
                    if (minPrice == null || price < minPrice) {
                        minPrice = price;
                        minStore = link.getStore();
                    }
                } else {
                    System.out.println("preco nao encontrado");
                }
            } catch (Exception e) {
                System.out.println("erro: " + e.getMessage());
            }
        }

        if (minPrice != null) {
            System.out.printf("  Menor preco: R$ %.2f em %s%n", minPrice, minStore);
            product.atualizarPrecoAtual(minPrice, minStore);
            productService.create(product);
            System.out.println("  Historico atualizado.");
        } else {
            System.out.println("  Nenhum preco encontrado para " + product.getName());
        }
    }

    private static final String STEALTH_SCRIPT =
        "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
        "Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3,4,5]});" +
        "Object.defineProperty(navigator, 'languages', {get: () => ['pt-BR','pt','en-US','en']});";

    private Float fetchPrice(String url) {
        try (BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .setLocale("pt-BR")
                        .setViewportSize(1366, 768)
                        .setExtraHTTPHeaders(Map.of(
                                "Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7",
                                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
                        ))
        )) {
            context.addInitScript(STEALTH_SCRIPT);
            Page page = context.newPage();

            page.navigate(url, new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(30000));

            // Lê window.__NEXT_DATA__ diretamente do JS (Next.js injeta preço aqui)
            try {
                Object nextData = page.evaluate("JSON.stringify(window.__NEXT_DATA__)");
                if (nextData != null) {
                    Matcher mj = JSON_PRICE_PATTERN.matcher(nextData.toString());
                    while (mj.find()) {
                        try {
                            float v = Float.parseFloat(mj.group(1));
                            if (v > 1) return v;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (Exception ignored) {}

            for (String sel : PLAYWRIGHT_SELECTORS) {
                try {
                    var locator = page.locator(sel).first();
                    if (locator.count() > 0) {
                        String texto = locator.textContent(new com.microsoft.playwright.Locator.TextContentOptions().setTimeout(3000));
                        Float preco = parsePrice(texto);
                        if (preco != null && preco > 1) return preco;
                    }
                } catch (Exception ignored) {}
            }

            String textoCompleto = page.innerText("body");
            Matcher m = PRICE_PATTERN.matcher(textoCompleto);
            if (m.find()) return parsePrice(m.group(0));

            return null;
        }
    }

    private Float parsePrice(String text) {
        if (text == null || text.isBlank()) return null;
        Matcher m = PRICE_PATTERN.matcher(text);
        if (m.find()) {
            String intPart = m.group(1).replace(".", "");
            String decPart = m.group(2);
            try {
                return Float.parseFloat(intPart + "." + decPart);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
