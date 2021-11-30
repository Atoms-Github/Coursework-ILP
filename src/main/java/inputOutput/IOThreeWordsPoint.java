package inputOutput;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class IOThreeWordsPoint {
    public JsonPoint coordinates;
    public String words;
    public static class JsonPoint{
        public float lng;
        public float lat;
    }


    public static IOThreeWordsPoint parseFromString(String jsonString){
        Type listType = new TypeToken<IOThreeWordsPoint>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }
}
