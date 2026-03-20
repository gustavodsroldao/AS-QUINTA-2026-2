import domain.Product;

void main() {

    Product product1 = new Product();

    System.out.println(" -------------");
    product1.setName("Celular");
    product1.setSku("SMS-GLX-BR-18L-SM");
    product1.setPrice(new BigDecimal("1000"));
    product1.setPrice(new BigDecimal("999"));
    System.out.println(" -------------");

    System.out.println(product1);
    System.out.println(" -------------");

    Product product2 = new Product();

    System.out.println(" -------------");
    product2.setName("Computador");
    product2.setSku("SMS-GLX-BR-18L-SM");
    product2.setPrice(new BigDecimal("1000"));
    product2.setPrice(new BigDecimal("999"));
    System.out.println(" -------------");

    System.out.println(product2);
    System.out.println(" -------------");


    Product product3 = new Product();

    System.out.println(" -------------");
    product3.setName("Tablet");
    product3.setSku("SMS-GLX-BR-18L-SM");
    product3.setPrice(new BigDecimal("1000"));
    product3.setPrice(new BigDecimal("999"));
    System.out.println(" -------------");

    System.out.println(product3);
    System.out.println(" -------------");
}
