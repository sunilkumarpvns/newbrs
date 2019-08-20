package com.sterlite.pcc.serverstatus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;

public class ResponsePredicates {

    public static Predicate<HttpResponse> successful() {
        return HttpResponse::isSuccessful;
    }

    public static Predicate<HttpResponse> responseEquals(String value) {
        return response  -> {
            boolean result = value.equalsIgnoreCase(response.responseBody());
            if(result) {
                System.out.println("Response check pass. Expected:" + value + ", Actual:" + response.responseBody());
            } else {
                System.out.println("Response check fail. Expected:" + value + ", Actual:" + response.responseBody());
            }
            return result;
        };
    }

    public static Predicate<HttpResponse> responseContains(String value) {
        return response  -> {
            boolean result = response.responseBody().contains(value);
            if(result) {
                System.out.println("Response check pass. Expected:" + value + ", Actual:" + response.responseBody());
            } else {
                System.out.println("Response check fail. Expected:" + value + ", Actual:" + response.responseBody());
            }
            return result;
        };
    }

    public static Predicate<HttpResponse> responseJsonContains(String key, String value) {
        return response -> {

            try {
                JsonElement element = new Gson().fromJson(response.responseBody(), JsonElement.class);

                if(element.isJsonObject() == false) {
                    System.out.println("Response check fail. Expected:" + value + ", Actual:" + element.toString());
                    return false;
                }

                JsonElement jsonElement = element.getAsJsonObject().get(key);

                if(Objects.nonNull(jsonElement) && jsonElement.isJsonPrimitive()) {
                    boolean result = value.equalsIgnoreCase(jsonElement.getAsString());
                    if(result) {
                        System.out.println("Response check pass. Expected:" + value + ", Actual:" + jsonElement.getAsString());
                    } else {
                        System.out.println("Response check fail. Expected:" + value + ", Actual:" + jsonElement.getAsString());
                    }
                    return result;
                } else {
                    System.out.println("key " + key + " not found");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            return false;
        };

    }
}
