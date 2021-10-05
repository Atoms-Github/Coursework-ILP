package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String machine_name;
    private final String port;

    public static final int DELIVERY_COST = 50;
    public static final int RESPONSE_CODE_OK = 200;


    public Menus(String machine_name, String port) {
        this.machine_name = machine_name;
        this.port = port;
    }
    private ParsedMenus cachedMenus = null;
    private ParsedMenus getParsedMenus(){ // TODO: Should I be caching?
        if (cachedMenus == null){
            cachedMenus = this.fetchMenus();
        }
        return cachedMenus;
    }
    private ParsedMenus fetchMenus(){
        HttpRequest request = HttpRequest.newBuilder().uri(this.getURI("menus/menus.json")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e); // TODO: Tidy?
        }

        if (response.statusCode() != RESPONSE_CODE_OK) { // TODO: Tidy somehow.
            throw new RuntimeException("Bad request return code: " + response.statusCode());
        }
        String unparsed = response.body();
        return new ParsedMenus(unparsed);
    }
    private URI getURI(String filename){
        return URI.create("http://" + machine_name + ":" + port + "/" + filename);
    }
    public int getDeliveryCost(String ... args) {
        ParsedMenus parsedMenu = this.getParsedMenus();
        int totalCost = 0;
        for(String requestedItem : args){
            ParsedMenus.MenuItem item = parsedMenu.getItem(requestedItem);
            if (item == null){
                throw new IllegalArgumentException("Invalid item requested: " + requestedItem);
            }
            totalCost += item.pence;
        }

        return totalCost + DELIVERY_COST;
    }
}






