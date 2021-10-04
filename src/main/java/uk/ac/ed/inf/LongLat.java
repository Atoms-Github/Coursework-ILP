package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public static final LongLat BOUNDS_BOTTOM_LEFT = new LongLat(-3.192473, 55.942617); // Top of the Meadows.
    public static final LongLat BOUNDS_TOP_RIGHT = new LongLat(-3.184319, 55.946233); // KFC.

    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public boolean isConfined(){
        // On the boundary is considered to not be confined.
        return this.longitude > BOUNDS_BOTTOM_LEFT.longitude
                && this.longitude < BOUNDS_TOP_RIGHT.longitude
                && this.latitude > BOUNDS_BOTTOM_LEFT.latitude
                && this.latitude < BOUNDS_TOP_RIGHT.latitude;

    }
    public double distanceTo(LongLat other){
        // Standard pythagoras formula.
        double diff_lng = this.longitude - other.longitude;
        double diff_lat = this.latitude - other.latitude;
        double dist_squared = diff_lng * diff_lng + diff_lat * diff_lat;
        return Math.sqrt(dist_squared);
    }
    public boolean closeTo(LongLat other){
        return this.distanceTo(other) < 0.00015;
    }
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
        // This is a little counterintuitive, but works if we use cos for x and sin for y.
        double angle_radians = (angle / 180.0) * Math.PI;
        double diff_x = Math.cos(angle_radians) * move_distance;
        double diff_y = Math.sin(angle_radians) * move_distance;
        return new LongLat(this.longitude + diff_x, this.latitude + diff_y);
    }



}
