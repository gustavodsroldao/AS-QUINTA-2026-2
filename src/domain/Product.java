package domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
public class Product implements EntityInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "uuid", length = 36)
    private UUID uuid;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price")
    private Float price;

    @Column(name = "store")
    private String store;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_price")
    private Date datePrice;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Price> historicalPrice = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Link> links = new ArrayList<>();

    public Product() {
    }

    public Product(String sku, String name) {
        this.sku = sku;
        this.name = name;
    }

    public Product(String sku, String name, Float price) {
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public Product(UUID uuid, String sku, String name, Float price) {
        this.uuid = uuid;
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        setPrice(price, null);
    }

    public void setPrice(Float price, String store) {
        if (this.price != null && this.datePrice != null) {
            Price oldPrice = new Price(this.price, this.datePrice, this.store);
            oldPrice.setProduct(this);
            historicalPrice.add(oldPrice);
        }
        this.price = price;
        this.store = store;
        this.datePrice = new Date();
    }

    public void atualizarPrecoAtual(Float price, String store) {
        this.price = price;
        this.store = store;
        this.datePrice = new Date();
    }

    public void adicionarAoHistorico(Float price, String store) {
        Price registro = new Price(price, new Date(), store);
        registro.setProduct(this);
        historicalPrice.add(registro);
    }

    public String getStore() {
        return store;
    }

    public Date getDatePrice() {
        return datePrice;
    }

    public void setDatePrice(Date datePrice) {
        this.datePrice = datePrice;
    }

    public List<Price> getHistoricalPrice() {
        return historicalPrice;
    }

    public void setHistoricalPrice(List<Price> historicalPrice) {
        this.historicalPrice = historicalPrice;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(Link link) {
        link.setProduct(this);
        links.add(link);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Produto: ").append(name).append(" (SKU: ").append(sku).append(")\n");
        sb.append("  Preco atual: ").append(price != null ? String.format("R$ %.2f", price) : "nao coletado");
        if (store != null) sb.append(" em ").append(store);
        sb.append("\n");
        sb.append("  Links cadastrados:\n");
        for (Link link : links) {
            sb.append("    - ").append(link.getStore()).append(": ").append(link.getUrl()).append("\n");
        }
        sb.append("  Historico de precos:\n");
        if (historicalPrice.isEmpty()) {
            sb.append("    (nenhum registro)\n");
        } else {
            for (Price p : historicalPrice) {
                sb.append("    - R$ ").append(String.format("%.2f", p.getPrice()));
                if (p.getStore() != null) sb.append(" em ").append(p.getStore());
                if (p.getDate() != null) sb.append(" | ").append(p.getDate());
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
