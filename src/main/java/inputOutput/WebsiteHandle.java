package inputOutput;

import com.mapbox.geojson.FeatureCollection;
import inputOutput.input.IOMenus;
import inputOutput.input.IOThreeWordsPoint;
import orders.Cafe;
import world.NamedMapPoint;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class WebsiteHandle {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String machineName;
    private final String port;

    public static final int RESPONSE_CODE_OK = 200;

    public WebsiteHandle(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }

    /**
     * Downloads no-fly zones from web server.
     * @throws IOException Problem with website.
     * @throws InterruptedException Problem with website.
     */
    public FeatureCollection fetchNoFlyZones() throws IOException, InterruptedException {
        String noFlyString = fetchWebsiteFile("buildings/no-fly-zones.geojson");
        return FeatureCollection.fromJson(noFlyString);
    }
    /**
     * Downloads landmarks zones from web server.
     * @throws IOException Problem with website.
     * @throws InterruptedException Problem with website.
     */
    public FeatureCollection fetchLandmarks() throws IOException, InterruptedException {
        String noFlyString = fetchWebsiteFile("buildings/landmarks.geojson");
        return FeatureCollection.fromJson(noFlyString);
    }

    /**
     * Downloads information about a what three words space from web server.
     * @return A NamedMapPoint, containing the WhatThreeWords name, and the coordinates.
     * @throws IOException Problem with website.
     * @throws InterruptedException Problem with website.
     */
    public NamedMapPoint fetchWhatThreeWordsBox(String wtwString) throws IOException, InterruptedException {
        // Invalid if not 3 '.'s in it.
        if (wtwString.split("\\.").length != 3){
            throw new IllegalArgumentException("Invalid what three words address " + wtwString);
        }
        // Replace . for / to make filepath.
        String wordsAsFilepath = wtwString.replace('.', '/');
        String fullFilepath = "words/" + wordsAsFilepath + "/details.json";
        // Download as json.
        String json = fetchWebsiteFile(fullFilepath);

        IOThreeWordsPoint parsedWTW = IOThreeWordsPoint.parseFromString(json);
        return new NamedMapPoint(parsedWTW.coordinates.lng, parsedWTW.coordinates.lat, parsedWTW.words);
    }

    /**
     * Downloads list of cafes from web server.
     * @return List of cafes.
     * @throws IOException If problem with web server.
     * @throws InterruptedException If problem with web server.
     */
    public ArrayList<Cafe> getCafes() throws IOException, InterruptedException{
        IOMenus menus = IOMenus.parseFromString(fetchWebsiteFile("menus/menus.json"));
        return menus.processCafes(this);
    }

    private String fetchWebsiteFile(String filename) throws IOException, InterruptedException{
        // Request for fetching menus.json. This defaults to a 'GET' request.
        HttpRequest request = HttpRequest.newBuilder().uri(getWebsiteURI(filename)).build();
        // Send the request.
        HttpResponse<String> response;
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != RESPONSE_CODE_OK) {
            throw new IOException("Error getting string from website. Response status code: " + response.statusCode());
        }
        return response.body();
    }
    private URI getWebsiteURI(String filename){
        return URI.create("http://" + machineName + ":" + port + "/" + filename);
    }
}
