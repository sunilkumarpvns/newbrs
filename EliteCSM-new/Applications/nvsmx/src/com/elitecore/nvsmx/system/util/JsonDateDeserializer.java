package com.elitecore.nvsmx.system.util;

import com.elitecore.commons.logging.LogManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

/**
 * A Deserializer that will convert the Date configured as string to Timestamp data-type of SQL
 * @author ishani.bhatt
 *
 */
public class JsonDateDeserializer implements JsonDeserializer<Timestamp> {
	   private static final String MODULE = "JSON-DESERIALIZER";

	public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	    String s = json.getAsJsonPrimitive().getAsString();
		try {
			Date date = NVSMXUtil.simpleDateFormatPool.get().parse(s);
    		return new Timestamp(date.getTime());
		} catch (ParseException e) {
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Invalid Date Format configred: " + s);
			}
			return null;
		}
	}
}
