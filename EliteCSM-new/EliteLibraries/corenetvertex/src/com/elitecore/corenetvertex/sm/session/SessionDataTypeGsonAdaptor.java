package com.elitecore.corenetvertex.sm.session;

import com.elitecore.corenetvertex.constants.FieldMappingDataType;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * This Class will be used to serialize and deserialize Data Type Field of SessionConfigurationFieldMapping
 * @author dhyani.raval
 */

public class SessionDataTypeGsonAdaptor implements JsonSerializer<Integer> , JsonDeserializer<Integer> {


    @Override
    public JsonElement serialize(Integer integer, Type typeOfSrc, JsonSerializationContext context) {

        JsonArray jsonElement = new JsonArray();
        if(integer != null) {

            jsonElement.add(FieldMappingDataType.fetchDisplayValueFromValue(integer));
        }
        return jsonElement;
    }

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.getAsInt();
    }
}
