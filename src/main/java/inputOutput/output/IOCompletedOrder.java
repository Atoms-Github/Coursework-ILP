package inputOutput.output;

public class IOCompletedOrder {
    public String orderNo;
    public String deliveredTo;
    public int costPence;
    public IOCompletedOrder(String orderNo, String deliveredTo, int costPence) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
        this.costPence = costPence;
    }
}
