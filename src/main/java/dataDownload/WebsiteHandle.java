package dataDownload;

import com.mapbox.geojson.FeatureCollection;
import routing.NamedMapPoint;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebsiteHandle {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final String machineName;
    private final String port;

    /**
     * The price in pence of a delivery.
     */
    public static final int DELIVERY_COST = 50;
    /**
     * HTTP OK response code.
     */
    public static final int RESPONSE_CODE_OK = 200;


    /**
     * @param machineName Name of website to connect to, e.g. "localhost".
     * @param port Port on target machine that website is running on, e.g. "9898".
     */
    public WebsiteHandle(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }

    public FeatureCollection fetchNoFlyZones(){
        String noFlyString = fetchWebsiteFile("buildings/no-fly-zones.geojson");
        return FeatureCollection.fromJson(noFlyString);
    }
    public FeatureCollection fetchLandmarks(){
        String noFlyString = fetchWebsiteFile("buildings/landmarks.geojson");
        return FeatureCollection.fromJson(noFlyString);
    }
    public NamedMapPoint fetchWhatThreeWordsBox(String wtwString){
        String[] split = wtwString.split("\\.");
        if (split.length != 3){
            throw new IllegalArgumentException("Invalid what three words address " + wtwString);
        }
        String wordsAsFilepath = wtwString.replace('.', '/');
        String fullFilepath = "words/" + wordsAsFilepath + "/details.json";
        String json = fetchWebsiteFile(fullFilepath);

        ParsedWTW parsedWTW = ParsedWTW.parseFromString(json);

        return new NamedMapPoint(parsedWTW.coordinates.lng, parsedWTW.coordinates.lat, parsedWTW.words);
    }
    public CafeMenus fetchParsedMenus(){
        return new CafeMenus(fetchWebsiteFile("menus/menus.json"));
    }
    private String fetchWebsiteFile(String filename){
        // Request for fetching menus.json. This defaults to a 'GET' request.
        HttpRequest request = HttpRequest.newBuilder().uri(getWebsiteURI(filename)).build();
        // Send the request.
        HttpResponse<String> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            // This is most likely an issue with the website, so not recoverable.
            throw new RuntimeException(e);
        }
        if (response.statusCode() != RESPONSE_CODE_OK) {
            throw new RuntimeException("Error getting string from website. Response status code: " + response.statusCode());
        }
        return response.body();
    }
    private URI getWebsiteURI(String filename){
        return URI.create("http://" + machineName + ":" + port + "/" + filename);
    }
}
