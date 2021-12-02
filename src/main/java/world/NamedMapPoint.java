package world;

public class NamedMapPoint {
    public MapPoint point;
    public String whatThreeWordsLoc;

    public NamedMapPoint(double x, double y, String whatThreeWordsLoc){
        this.point = new MapPoint(x, y);
        this.whatThreeWordsLoc = whatThreeWordsLoc;
    }
    // TODO: Ammend this with a 'process' method.
}
