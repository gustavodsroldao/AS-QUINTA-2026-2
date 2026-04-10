import domain.Product;
import service.ProductService;
import utils.GenerateValue;

void main() {

    ProductService productService = new ProductService();

    Product produto = new Product(
            GenerateValue.uuid(),
            "SKU",
            "asas",
            2f
    );

    produto.setPrice(3f);
    produto.setPrice(4f);

    productService.create(produto);
    productService.listAll();
}
