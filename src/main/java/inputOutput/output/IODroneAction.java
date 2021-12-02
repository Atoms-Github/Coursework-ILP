package inputOutput.output;

import world.MapPoint;

/**
 * A representation of a drone action, in the same structure as it needs to appear in the output database.
 */
public class IODroneAction {
    public static final int DRONE_ANGLE_HOVER = -999;
    public static final String NO_ORDER_STRING = "NoOrders";

    public final String orderNo;
    public final int angle;
    public final MapPoint from;
    public final MapPoint to;

    private IODroneAction(String orderNo, int angle, MapPoint from, MapPoint to) {
        this.orderNo = orderNo;
        this.angle = angle;
        this.from = from;
        this.to = to;
    }

    /**
     * Creates a drone action instance which is a movement action.
     * @param orderNo The order that this move is working towards, or NO_ORDER_STRING for none.
     */
    public static IODroneAction moveAction(String orderNo, int angle, MapPoint from, MapPoint to){
        return new IODroneAction(orderNo, angle, from, to);
    }
    /**
     * Creates a drone action instance which is a hover action.
     * @param orderNo The order that this move is working towards, or NO_ORDER_STRING for none.
     */
    public static IODroneAction hoverAction(String orderNo, MapPoint location){
        return new IODroneAction(orderNo, DRONE_ANGLE_HOVER, location, location);
    }
}
