package com.elitecore.corenetvertex.util.commons.gson.adaptor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class BigDecimalToStringGsonAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonNull()) {
            return null;
        } else {
            return json.getAsJsonPrimitive().getAsBigDecimal();
        }
    }


    @Override
    public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return context.serialize("");

        }
        return context.serialize(src.toPlainString());
    }

}
