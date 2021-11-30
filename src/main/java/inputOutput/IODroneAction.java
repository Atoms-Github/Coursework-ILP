package inputOutput;

import world.MapPoint;

public class IODroneAction {
    public static final int DRONE_ANGLE_HOVER = -999;
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

    public static IODroneAction moveActionAppleton(int angle, MapPoint from, MapPoint to){
        return new IODroneAction(null, angle, from, to);
    }
    public static IODroneAction moveActionOrder(String orderNo, int angle, MapPoint from, MapPoint to){
        return new IODroneAction(orderNo, angle, from, to);
    }
    public static IODroneAction hoverAction(String orderNo, MapPoint location){
        return new IODroneAction(orderNo, DRONE_ANGLE_HOVER, location, location);
    }
}
