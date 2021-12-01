package world.drone;

import world.MapPoint;

import java.util.Objects;

public class DroneTaskPoint {
    public MapPoint point;
    public boolean mustHover;

    public DroneTaskPoint(MapPoint point, boolean mustHover) {
        this.point = point;
        this.mustHover = mustHover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroneTaskPoint that = (DroneTaskPoint) o;
        return mustHover == that.mustHover && Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, mustHover);
    }
}
