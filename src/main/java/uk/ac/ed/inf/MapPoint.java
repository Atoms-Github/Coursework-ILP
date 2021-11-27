package uk.ac.ed.inf;


import java.util.Objects;

public class MapPoint {
    /**
     * X represents the point's longitude.
     */
    public double x;

    /**
     * Y represents the point's Latitude.
     */
    public double y;



    /**
     * The bottom left corner of the drone's movement bounds (Top of the Meadows).
     */
    public static final MapPoint BOUNDS_BOTTOM_LEFT = new MapPoint(-3.192473, 55.942617);
    /**
     * The top right corner of the drone's movement bounds (KFC).
     */
    public static final MapPoint BOUNDS_TOP_RIGHT = new MapPoint(-3.184319, 55.946233);
    public static final MapPoint APPLETON_TOWER = new MapPoint(-3.186874, 55.944494);

    /**
     * Creates a new point on the world from longitude and latitude.
     */
    public MapPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "MapPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapPoint mapPoint = (MapPoint) o;
        return Double.compare(mapPoint.x, x) == 0 && Double.compare(mapPoint.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Checks whether this point is within the drone's movement bounds.
     * @return Whether the point is within the drone's movement bounds.
     */
    public boolean isConfined(){
        // On the boundary is considered to not be confined.
        return this.x > BOUNDS_BOTTOM_LEFT.x
                && this.x < BOUNDS_TOP_RIGHT.x
                && this.y > BOUNDS_BOTTOM_LEFT.y
                && this.y < BOUNDS_TOP_RIGHT.y;

    }

    /**
     * Calculates the distance been this point and another.
     * This uses pythagorean distance, so doesn't compensate for world curvature.
     * @param other The point to check distance to.
     * @return Distance to the other point, in longitude/latidude degrees.
     */
    public double distanceTo(MapPoint other){
        // Standard pythagoras formula.
        double diff_lng = this.x - other.x;
        double diff_lat = this.y - other.y;
        double dist_squared = diff_lng * diff_lng + diff_lat * diff_lat;
        return Math.sqrt(dist_squared);
    }

    /**
     * @param other Point to check close to.
     * @return Whether the points are close to each other (i.e. within 0.00015 degrees).
     */
    public boolean closeTo(MapPoint other){
        return this.distanceTo(other) < 0.00015;
    }

    /**
     * Calculates the position the drone would be after moving at the specified angle, or after hovering.
     * @param angle The angle to move the drone at, in degrees. Must be between 0 and 350, and a multiple of 10.
     *              0 degrees means east, then going counter clockwise. Can also use -999 to represent hovering - no movement.
     * @return The new position that the drone would be after taking the specified move.
     */
    public MapPoint nextPosition(int angle){
        // Special 'hover' command.
        if (angle == -999){
            return new MapPoint(this.x, this.y);
        }
        // Angle must be a multiple of 10, and between 0 and 350.
        if (angle % 10 != 0 || angle < 0 || angle >= 360){
            throw new IllegalArgumentException("Invalid angle " + angle);
        }
        double move_distance = 0.00015;
        // We need to go 1 move in the 'angle' direction.
        // 0 degrees means east, 90 means north. This means we start right, and go counterclockwise.
        // This is a little counterintuitive, but works if we use cos for x (long) and sin for y (lat).
        double angle_radians = (angle / 180.0) * Math.PI;
        double diff_long = Math.cos(angle_radians) * move_distance;
        double diff_lat = Math.sin(angle_radians) * move_distance;
        return new MapPoint(this.x + diff_long, this.y + diff_lat);
    }
}
