package inputOutput.output;

/**
 * A representation of an order that the drone has completed, in the same structure as it needs to appear in the output database.
 */
public class IOCompletedOrder {
    public final String orderNo;
    public final String deliveredTo;
    public final int costPence;
    public IOCompletedOrder(String orderNo, String deliveredTo, int costPence) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
        this.costPence = costPence;
    }
}
