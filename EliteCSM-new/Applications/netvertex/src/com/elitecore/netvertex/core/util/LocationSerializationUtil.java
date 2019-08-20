package com.elitecore.netvertex.core.util;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class LocationSerializationUtil {
	private static final JsonParser jsonParser = new JsonParser();
	private static final String MODULE = "LOCATION-SERIALIZER";

	public static String serialize(PCRFResponse pcrfResponse) {
		JsonObject location = new JsonObject();
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_TYPE);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_MCC);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_MNC);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_LAC);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_CI);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_SAC);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_RAC);
		setValueInJson(pcrfResponse, location, PCRFKeyConstants.LOCATION_TAC);
		return location.toString();
	}

	private static void setValueInJson(PCRFResponse pcrfResponse, JsonObject location, PCRFKeyConstants key) {
		String value = pcrfResponse.getAttribute(key.val);
		if(value!=null){
			location.addProperty(key.val, value);
		}
	}

	public static void deSerialize(PCRFRequest pcrfRequest, String value) {

		try{
			JsonElement location = jsonParser.parse(value);
			JsonObject locationJSON = location.getAsJsonObject();

			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_TYPE);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_MCC);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_MNC);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_LAC);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_CI);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_SAC);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_RAC);
			setValueInRequest(pcrfRequest, locationJSON, PCRFKeyConstants.LOCATION_TAC);
		}catch(JsonSyntaxException e){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Could not parse location stored in session. Reason:" + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
	}

	private static void setValueInRequest(PCRFRequest pcrfRequest, JsonObject locationJSON, PCRFKeyConstants key) {
		String value = pcrfRequest.getAttribute(key.val);
		if(value==null) {
			pcrfRequest.setAttribute(key.val, getValueFromJson(locationJSON, key.val));
		}
	}

	public static void deSerialize(PCRFResponse pcrfResponse, String value) {

		try{
			JsonElement location = jsonParser.parse(value);
			JsonObject locationJSON = location.getAsJsonObject();

			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_TYPE);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_MCC);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_MNC);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_LAC);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_CI);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_SAC);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_RAC);
			setValueInResponse(pcrfResponse, locationJSON, PCRFKeyConstants.LOCATION_TAC);
		}catch(JsonSyntaxException e){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Could not parse location stored in session. Reason:" + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

	}

	private static void setValueInResponse(PCRFResponse pcrfResponse, JsonObject locationJSON, PCRFKeyConstants key) {
		String value = getValueFromJson(locationJSON, key.val);
		if(value!=null){
			pcrfResponse.setAttribute(key.val, value);
		}
	}

	private static String getValueFromJson(JsonObject object, String key){
		return (object.get(key)==null || object.get(key).isJsonNull())?null:object.get(key).getAsString();
	}
}
