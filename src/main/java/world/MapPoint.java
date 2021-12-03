package world;


import com.mapbox.geojson.Point;


import java.util.List;
import java.util.Objects;

public class MapPoint {
    private static final double CLOSE_TOLERANCE = 0.00015;
    // X and Y can be final, as new instances of MapPoint are created for new points instead of changing these.
    // This prevents bugs where we accidentally change a value that the calling code wasn't expecting.
    /**
     * X represents the point's longitude.
     */
    public final double x;
    /**
     * Y represents the point's Latitude.
     */
    public final double y;



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

    /**
     * @return This point's longitude, i.e. its x value.
     */
    public double getLongitude() {
        return x;
    }
    /**
     * @return This point's latidude, i.e. its y value.
     */
    public double getLatitude() {
        return y;
    }

    /**
     * Finds the closest point to 'this' point, in a list of points.
     * @param points What points to consider.
     * @return The closest point.
     */
    public MapPoint getClosestPoint(List<MapPoint> points){
        // Standard 'findMin' algorithm:
        MapPoint closest = null;
        double bestDistance = Double.MAX_VALUE;
        for (MapPoint point : points){
            double distance = this.distanceTo(point);
            if (distance < bestDistance){
                bestDistance = distance;
                closest = point;
            }
        }
        return closest;
    }

    /**
     * Auto-generated implementation.
     */
    @Override
    public String toString() {
        return "MapPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    /**
     * Auto-generated implementation.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapPoint mapPoint = (MapPoint) o;
        return Double.compare(mapPoint.x, x) == 0 && Double.compare(mapPoint.y, y) == 0;
    }
    /**
     * Auto-generated implementation.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Calculates the distance been this point and another.
     * This uses pythagorean distance, so doesn't compensate for world curvature.
     * @param other The point to check distance to.
     * @return Distance to the other point, in longitude/latidude degrees.
     */
    public double distanceTo(MapPoint other){
        return Math.sqrt(distanceToSquared(other));
    }
    private double distanceToSquared(MapPoint other){
        // Standard pythagoras formula.
        double diff_lng = this.x - other.x;
        double diff_lat = this.y - other.y;
        return diff_lng * diff_lng + diff_lat * diff_lat;
    }

    /**
     * @param other Point to check close to.
     * @return Whether the points are close to each other (i.e. within 0.00015 degrees).
     */
    public boolean closeTo(MapPoint other){
        return this.distanceTo(other) < CLOSE_TOLERANCE;
    }

    /**
     * Calculates the angle from 'this' point to 'other' point.
     * @param other Point to check angle between.
     * @return The angle, between 0.0 and 360.0. 0.0 meaning east.
     */
    public double angleTo(MapPoint other){
        double diffY = other.y - y;
        double diffX = other.x - x;
        double angleRadians = Math.atan2(diffY, diffX);
        double angleDegrees = angleRadians / Math.PI * 180;
        if (angleDegrees < 0.0){
            angleDegrees += 360.0;
        }
        return angleDegrees;
    }

    /**
     * Calculates the position the drone would be after moving at the specified angle, or after hovering.
     * @param angle The angle to move the drone at, in degrees. Must be between 0 and 350, and a multiple of 10.
     *              0 degrees means east, then going counter clockwise. Can also use -999 to represent hovering - no movement.
     * @return The new position that the drone would be after taking the specified move.
     */
    public MapPoint nextPosition(int angle, double move_distance){
        // Special 'hover' command.
        if (angle == -999){
            return new MapPoint(this.x, this.y);
        }

        // We need to go 1 move in the 'angle' direction.
        // 0 degrees means east, 90 means north. This means we start right, and go counterclockwise.
        // This is a little counterintuitive, but works if we use cos for x (long) and sin for y (lat).
        double angle_radians = (angle / 180.0) * Math.PI;
        double diff_long = Math.cos(angle_radians) * move_distance;
        double diff_lat = Math.sin(angle_radians) * move_distance;
        return new MapPoint(this.x + diff_long, this.y + diff_lat);
    }

    /**
     * @return This point, as a geojson point.
     */
    public Point toGeoPoint() {
        return Point.fromLngLat(x, y);
    }
}
