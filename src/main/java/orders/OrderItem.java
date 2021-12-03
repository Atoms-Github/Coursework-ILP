package orders;

public class OrderItem {
    public final String name;

    // We're having a 2 way connection here. It is messier, but its faster, as we don't need to re-search all the shops every
    // time we want to work out where to go for an order. This is performance in the important loop, so this is worth it.

    /**
     * Order item references shop so don't need to re-search all shops each time.
     */
    public final Shop shop; // TODO: Shops or shops? What're they called?
    public final Integer price;

    public OrderItem(String name, Shop shop, Integer price) {
        this.name = name;
        this.shop = shop;
        this.price = price;
    }
}
