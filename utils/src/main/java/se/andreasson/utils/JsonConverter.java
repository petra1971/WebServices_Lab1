package se.andreasson.utils;

import com.google.gson.Gson;

public class JsonConverter {

    private Gson gson;

    public JsonConverter() {
        gson = new Gson();
    }

    public String convertToJson(Object obj) {
        return gson.toJson(obj);
    }

    //Bra sätt? I såna fall byt ut convert i ArtistHandlern
    public Object convertToObject(String jsonObject) {
        var object
                = gson.fromJson(jsonObject,
                Object.class);

        // return object
        return object;
    }
}
