package inputOutput;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ParsedThreeWords {
    public JsonPoint coordinates;
    public String words;
    public static class JsonPoint{
        public float lng;
        public float lat;
    }


    public static ParsedThreeWords parseFromString(String jsonString){
        Type listType = new TypeToken<ParsedThreeWords>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }
}
