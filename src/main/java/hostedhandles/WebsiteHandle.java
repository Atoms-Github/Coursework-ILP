package hostedhandles;

import uk.ac.ed.inf.ParsedMenus;

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

    /*
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
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != RESPONSE_CODE_OK) {
            throw new IOException("Bad request. Response status code: " + response.statusCode());
        }
        String unparsed = response.body();
        return new ParsedMenus(unparsed);
    }
    private URI getWebsiteURI(String filename){
        return URI.create("http://" + machineName + ":" + port + "/" + filename);
    }
}
