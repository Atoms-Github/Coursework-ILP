package data;

public class DroneAction {
    public static final int DRONE_ANGLE_HOVER = -999;
    public final ProcessedOrder order;
    public final int angle;
    public final MapPoint from;
    public final MapPoint to;

    private DroneAction(ProcessedOrder order, int angle, MapPoint from, MapPoint to) {
        this.order = order;
        this.angle = angle;
        this.from = from;
        this.to = to;
    }

    public static DroneAction moveActionAppleton(int angle, MapPoint from, MapPoint to){
        return new DroneAction(null, angle, from, to);
    }
    public static DroneAction moveActionOrder(ProcessedOrder order, int angle, MapPoint from, MapPoint to){
        return new DroneAction(order, angle, from, to);
    }
    public static DroneAction hoverAction(ProcessedOrder order, MapPoint location){
        return new DroneAction(order, DRONE_ANGLE_HOVER, location, location);
    }
}
