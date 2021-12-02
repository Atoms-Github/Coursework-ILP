package inputOutput.input;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class IOThreeWordsPoint {
    public JsonPoint coordinates;
    /**
     * WhatThreeWords representation of this point.
     */
    public String words;
    public static class JsonPoint{
        public float lng;
        public float lat;
    }


    /**
     * Parses a point from json.
     */
    public static IOThreeWordsPoint parseFromString(String jsonString){
        Type listType = new TypeToken<IOThreeWordsPoint>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }
}
