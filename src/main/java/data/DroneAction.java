package data;

import world.MapPoint;

public class DroneAction {
    public static final int DRONE_ANGLE_HOVER = -999;
    public final String orderNo;
    public final int angle;
    public final MapPoint from;
    public final MapPoint to;

    private DroneAction(String orderNo, int angle, MapPoint from, MapPoint to) {
        this.orderNo = orderNo;
        this.angle = angle;
        this.from = from;
        this.to = to;
    }

    public static DroneAction moveActionAppleton(int angle, MapPoint from, MapPoint to){
        return new DroneAction(null, angle, from, to);
    }
    public static DroneAction moveActionOrder(String orderNo, int angle, MapPoint from, MapPoint to){
        return new DroneAction(orderNo, angle, from, to);
    }
    public static DroneAction hoverAction(String orderNo, MapPoint location){
        return new DroneAction(orderNo, DRONE_ANGLE_HOVER, location, location);
    }
}
