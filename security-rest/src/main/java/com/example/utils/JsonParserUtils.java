package com.example.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonParserUtils {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public static <T> T toObject(String json, Type T) {
        return gson.fromJson(json, T);
    }

    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }

}
