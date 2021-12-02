package inputOutput.input;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import inputOutput.WebsiteHandle;
import orders.Cafe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsed representation of all the items on the website.
 */
public class IOMenus {
    public List<IOCafe> cafes;

    public static class MenuItem{
        /**
         * The full long item name.
         */
        public String item;
        /**
         * The cost of the item.
         */
        public Integer pence;
    }

    public static IOMenus parseFromString(String jsonString){
        IOMenus menus = new IOMenus();
        Type listType = new TypeToken<ArrayList<IOCafe>>() {}.getType();
        menus.cafes = new Gson().<ArrayList<IOCafe>>fromJson(jsonString, listType);
        return menus;
    }

    public ArrayList<Cafe> processCafes(WebsiteHandle website) throws IOException, InterruptedException {
        ArrayList<Cafe> cafes = new ArrayList<>();
        for (IOCafe cafe: this.cafes){
            cafes.add(cafe.process(website));
        }
        return cafes;
    }
}
