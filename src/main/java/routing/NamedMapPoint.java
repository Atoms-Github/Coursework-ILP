package routing;

public class NamedMapPoint {
    public MapPoint point;
    public String whatThreeWordsLoc;

    public NamedMapPoint(MapPoint point, String whatThreeWordsLoc) {
        this.point = point;
        this.whatThreeWordsLoc = whatThreeWordsLoc;
    }
    public NamedMapPoint(double x, double y, String whatThreeWordsLoc){
        this.point = new MapPoint(x, y);
        this.whatThreeWordsLoc = whatThreeWordsLoc;
    }
}
