package com.elitecore.netvertex.core.util;

import com.elitecore.corenetvertex.util.GsonFactory;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;

public class DefaultJsonReaderAndWriter implements JsonReaderAndWriter
{
    public void write(FileWriter fw, Object obj) throws IOException{
        JsonWriter jsonWriter = new JsonWriter(fw);
        jsonWriter.jsonValue(GsonFactory.defaultInstance().toJson(obj));
    }
}
