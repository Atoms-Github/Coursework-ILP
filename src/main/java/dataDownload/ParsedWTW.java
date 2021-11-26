package dataDownload;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ParsedWTW {
    public JsonPoint coordinates;
    public String words;
    public static class JsonPoint{
        public float lng;
        public float lat;
    }


    public static ParsedWTW parseFromString(String jsonString){
        Type listType = new TypeToken<ParsedWTW>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }
}
