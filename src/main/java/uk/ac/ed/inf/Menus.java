package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A class used to connect to the website and provide information about the website's menu.
 */
public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();
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
     * @param port Port on target machine that website is running on.
     */
    public Menus(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }

    /**
     * Caching is good for the menu because we may need to request a large number of prices if this is used for students to check prices before buying.
     * Also, this menu may be used by the best drone route algorithm, in which case we definitely don't want to be re-fetching and re-parsing each step.
     */
    private ParsedMenus cachedMenus = null;
    private ParsedMenus getParsedMenus(){
        if (cachedMenus == null){
            try {
                // Fetch, then cache for next time.
                cachedMenus = this.fetchParsedMenus();
            } catch (IOException | InterruptedException e) {
                // This is most likely an issue with the website, so not recoverable.
                throw new RuntimeException(e);
            }
        }
        return cachedMenus;
    }

    /**
     * Downloads the latest menu from the website.
     */
    private ParsedMenus fetchParsedMenus() throws IOException, InterruptedException {
        // Request for fetching menus.json. This defaults to a 'GET' request.
        HttpRequest request = HttpRequest.newBuilder().uri(this.getWebsiteURI("menus/menus.json")).build();
        // Send the request.
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != RESPONSE_CODE_OK) {
            throw new IOException("Bad request. Response status code: " + response.statusCode());
        }
        String unparsed = response.body();
        return new ParsedMenus(unparsed);
    }
    private URI getWebsiteURI(String filename){
        return URI.create("http://" + machineName + ":" + port + "/" + filename);
    }

    /**
     * Calculates the total cost of a list of items from the menu, including delivery charge.
     * @param args The list of items to sum the cost of.
     * @return The total price in pence, including delivery.
     */
    public int getDeliveryCost(String ... args) {
        ParsedMenus parsedMenu = this.getParsedMenus();
        int totalCost = 0;
        // For all the items, add their price to the total.
        for(String requestedItem : args){
            ParsedMenus.MenuItem item = parsedMenu.getItem(requestedItem);
            if (item == null){
                // We're assuming that all items should exist in the menu -> We don't have much of an alternative, since we can't change this method signature.
                throw new IllegalArgumentException("Invalid item requested: " + requestedItem);
            }
            totalCost += item.pence;
        }

        return totalCost + DELIVERY_COST;
    }
}






