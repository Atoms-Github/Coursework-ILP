package drone;

import world.MapPoint;

import java.util.Objects;

/**
 * A single waypoint in the list of actions that a drone can do.
 */
public class DroneTaskPoint {
    /**
     * Where the drone should go.
     */
    private final MapPoint point;
    /**
     * Whether the drone should hover at the point.
     */
    private boolean mustHover;

    public DroneTaskPoint(MapPoint point, boolean mustHover) {
        this.point = point;
        this.mustHover = mustHover;
    }

    /**
     * Auto-generated implementation.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroneTaskPoint that = (DroneTaskPoint) o;
        return mustHover == that.mustHover && Objects.equals(point, that.point);
    }
    /**
     * Auto-generated implementation.
     */
    @Override
    public int hashCode() {
        return Objects.hash(point, mustHover);
    }

    public MapPoint getPoint() {
        return point;
    }

    public boolean getMustHover() {
        return mustHover;
    }

    public void setMustHover(boolean mustHover) {
        this.mustHover = mustHover;
    }
}
