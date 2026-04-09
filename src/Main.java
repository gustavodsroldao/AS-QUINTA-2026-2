import domain.Product;

void main() {

    Product product1 = new Product();
    product1.setPrice(new BigDecimal("1000"));

    Product product2 = new Product();
    product2.setPrice(new BigDecimal("1000"));

    Product product3 = new Product();
    product3.setPrice(new BigDecimal("1000"));

    System.out.println(" -------------");
    product1.setName("Celular");
    product1.setSku("SMS-GLX-BR-18L-SM");
    product1.setPrice(new BigDecimal("599"));
    System.out.println(" -------------");

    System.out.println(product1);
    System.out.println(" -------------");


    System.out.println(" -------------");
    product2.setName("Computador");
    product2.setSku("SMS-GLX-BR-18L-SM");
    product2.setPrice(new BigDecimal("1999"));
    System.out.println(" -------------");

    System.out.println(product2);
    System.out.println(" -------------");


    System.out.println(" -------------");
    product3.setName("Tablet");
    product3.setSku("SMS-GLX-BR-18L-SM");
    product3.setPrice(new BigDecimal("3999"));
    System.out.println(" -------------");
    System.out.println(product3);
    System.out.println(" -------------");
}
