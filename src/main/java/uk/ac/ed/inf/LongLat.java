package uk.ac.ed.inf;

/**
 * A point in the world, represented as longitude, latitude.
 */
public class LongLat {
    public double longitude;
    public double latitude;

    /**
     * The bottom left corner of the drone's movement bounds (Top of the Meadows).
     */
    public static final LongLat BOUNDS_BOTTOM_LEFT = new LongLat(-3.192473, 55.942617);
    /**
     * The top right corner of the drone's movement bounds (KFC).
     */
    public static final LongLat BOUNDS_TOP_RIGHT = new LongLat(-3.184319, 55.946233);

    /**
     * Creates a new point on the world from longitude and latitude.
     */
    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Checks whether this point is within the drone's movement bounds.
     * @return Whether the point is within the drone's movement bounds.
     */
    public boolean isConfined(){
        // On the boundary is considered to not be confined.
        return this.longitude > BOUNDS_BOTTOM_LEFT.longitude
                && this.longitude < BOUNDS_TOP_RIGHT.longitude
                && this.latitude > BOUNDS_BOTTOM_LEFT.latitude
                && this.latitude < BOUNDS_TOP_RIGHT.latitude;

    }

    /**
     * Calculates the distance been this point and another.
     * This uses pythagorean distance, so doesn't compensate for world curvature.
     * @param other The point to check distance to.
     * @return Distance to the other point, in longitude/latidude degrees.
     */
    public double distanceTo(LongLat other){
        // Standard pythagoras formula.
        double diff_lng = this.longitude - other.longitude;
        double diff_lat = this.latitude - other.latitude;
        double dist_squared = diff_lng * diff_lng + diff_lat * diff_lat;
        return Math.sqrt(dist_squared);
    }

    /**
     * @param other Point to check close to.
     * @return Whether the points are close to each other (i.e. within 0.00015 degrees).
     */
    public boolean closeTo(LongLat other){
        return this.distanceTo(other) < 0.00015;
    }

    /**
     * Calculates the position the drone would be after moving at the specified angle, or after hovering.
     * @param angle The angle to move the drone at, in degrees. Must be between 0 and 350, and a multiple of 10.
     *              0 degrees means east, then going counter clockwise. Can also use -999 to represent hovering - no movement.
     * @return The new position that the drone would be after taking the specified move.
     */
    public LongLat nextPosition(int angle){
        // Special 'hover' command.
        if (angle == -999){
            return new LongLat(this.longitude, this.latitude);
        }
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
        return new LongLat(this.longitude + diff_long, this.latitude + diff_lat);
    }
}
