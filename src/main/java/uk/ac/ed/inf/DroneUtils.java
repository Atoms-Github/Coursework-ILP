package uk.ac.ed.inf;

import java.util.List;

public class DroneUtils {
    public static final double SHORT_MOVE_LENGTH = 0.00015;
    public static final double UNLUCKY_ZIG_ZAG_MULTIPLIER = 1.15; // See report for where this comes from. // TODO: Real calculation.


    public static MapPoint getClosestPoint(List<MapPoint> points, MapPoint start){
        return start; // TODO
    }
}
