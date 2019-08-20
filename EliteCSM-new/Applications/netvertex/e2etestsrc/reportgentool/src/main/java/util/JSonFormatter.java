package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.concurrent.TimeUnit;

public class JSonFormatter {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JsonParser parser = new JsonParser();

    public static String format(String data) {
        JsonElement jsonElement = parser.parse(data);
        String s = gson.toJson(jsonElement);
        s = "    " + s.replace(System.lineSeparator(), System.lineSeparator() + "    ");
        return s;
    }




}
