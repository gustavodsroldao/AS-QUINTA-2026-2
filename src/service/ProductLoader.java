package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Link;
import domain.Product;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductLoader {

    private static class LinkConfig {
        String loja;
        String url;
    }

    private static class ProdutoConfig {
        String sku;
        String nome;
        List<LinkConfig> links;
    }

    private final ProductService productService;

    public ProductLoader() {
        this.productService = new ProductService();
    }

    public void sincronizar() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("products.json");
        if (is == null) {
            System.out.println("products.json nao encontrado no classpath.");
            return;
        }

        Gson gson = new Gson();
        Type tipo = new TypeToken<List<ProdutoConfig>>() {}.getType();
        List<ProdutoConfig> configs;
        try (InputStreamReader reader = new InputStreamReader(is)) {
            configs = gson.fromJson(reader, tipo);
        } catch (Exception e) {
            System.out.println("Erro ao ler products.json: " + e.getMessage());
            return;
        }

        Map<String, Product> existentes = productService.getAll().stream()
                .map(e -> (Product) e)
                .collect(Collectors.toMap(Product::getSku, p -> p));

        for (ProdutoConfig config : configs) {
            if (existentes.containsKey(config.sku)) {
                Product produto = existentes.get(config.sku);
                produto.getLinks().clear();
                for (LinkConfig lc : config.links) {
                    produto.addLink(new Link(lc.loja, lc.url));
                }
                productService.edit(produto);
                System.out.println("Links atualizados: " + config.nome);
            } else {
                Product produto = new Product(config.sku, config.nome);
                for (LinkConfig lc : config.links) {
                    produto.addLink(new Link(lc.loja, lc.url));
                }
                productService.create(produto);
                System.out.println("Produto cadastrado: " + config.nome);
            }
        }
    }
}
