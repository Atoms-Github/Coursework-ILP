package orders;

public class OrderItem {
    public final String name;

    // We're having a 2 way connection here. It is messier, but its faster, as we don't need to re-search all the cafes every
    // time we want to work out where to go for an order. This is performance in the important loop, so this is worth it.
    public final Cafe shop;
    public final Integer price;

    public OrderItem(String name, Cafe shop, Integer price) {
        this.name = name;
        this.shop = shop;
        this.price = price;
    }
}
