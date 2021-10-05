package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParsedMenus {
    public List<Cafe> cafes;
    public static class Cafe{
        public String name;
        public String location;
        public List<MenuItem> menu;
    }
    public static class MenuItem{
        public String item;
        public Integer pence;
    }
    public ParsedMenus(String jsonString){
        Type listType = new TypeToken<ArrayList<Cafe>>() {}.getType();
        this.cafes = new Gson().<ArrayList<Cafe>>fromJson(jsonString, listType);
    }
    public MenuItem getItem(String itemName){
        for (ParsedMenus.Cafe cafe: cafes){
            for (ParsedMenus.MenuItem item: cafe.menu){
                if (item.item.equals(itemName)){
                    return item;
                }
            }
        }
        return null;
    }
}
