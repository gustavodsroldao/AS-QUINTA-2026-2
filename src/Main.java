import service.CrawlerService;
import service.ProductLoader;
import service.ProductService;

void main() {
    new ProductLoader().sincronizar();

    System.out.println("\n--- Iniciando crawler ---");
    try (CrawlerService crawler = new CrawlerService()) {
        crawler.crawlAll();
    }

    System.out.println("\n--- Estado atual dos produtos ---");
    new ProductService().listAll();
}
