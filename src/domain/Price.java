package domain;

import java.util.Date;
import java.util.UUID;

public class Price implements EntityInterface {
    private UUID uuid;
    private Float price;
    private Date date;

    public Price(Date date, Float price) {
        this.price = price;
        this.date = date;
    }

    public Price(Float price, UUID uuid, Date date) {
        this.price = price;
        this.uuid = uuid;
        this.date = date;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "Price{" +
                ", price=" + price +
                ", date=" + date +
                '}';
    }

}
