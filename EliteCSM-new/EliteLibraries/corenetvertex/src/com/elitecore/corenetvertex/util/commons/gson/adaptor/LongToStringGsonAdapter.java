package com.elitecore.corenetvertex.util.commons.gson.adaptor;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LongToStringGsonAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {

        if(src == null) {
            context.serialize("0");
        }


        return context.serialize(String.valueOf(src));
    }

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonNull()) {
            return 0l;
        } else {
            return json.getAsJsonPrimitive().getAsLong();
        }
    }
}
