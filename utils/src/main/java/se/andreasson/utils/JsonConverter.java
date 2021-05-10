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

    //Generisk metod
    public <T> T convertToObject(String jsonObject, Class<T> classOfT) {

        return gson.fromJson(jsonObject,
                classOfT);
    }
}
