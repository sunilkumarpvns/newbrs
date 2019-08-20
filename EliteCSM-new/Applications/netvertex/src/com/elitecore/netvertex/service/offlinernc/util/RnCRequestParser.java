package com.elitecore.netvertex.service.offlinernc.util;

import java.text.ParseException;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RnCRequestParser {
	
	private static final String ATTRIBUTES = "Attributes";

	private static final String STATUS_MESSAGE = "StatusMessage";

	private static final String SOURCE_UNIT_NAME = "SourceUnitName";

	private static final String SERIAL_NUMBER = "SerialNumber";

	@Nonnull private final TimeSource timesource;
	@Nonnull private final SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;
	
	public RnCRequestParser(TimeSource timesource, SimpleDateFormatThreadLocal format) {
		this.timesource = timesource;
		this.simpleDateFormatThreadLocal = format;
	}
	
	public String serialize(RnCResponse rncResponse){
		Preconditions.checkNotNull(rncResponse, "rncRequest is null");
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append(CommonConstants.OPENING_BRACES);
		jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
		jsonBuilder.append(RnCRequestParser.ATTRIBUTES);
		jsonBuilder.append(CommonConstants.DOUBLE_QUOTES).append(CommonConstants.COLON);
		jsonBuilder.append(CommonConstants.OPENING_BRACES);
		
		for (String attribute : rncResponse.getAttributes().keySet()) {
			String value="";
			if(rncResponse.getAttributes().get(attribute)!=null){
				value=rncResponse.getAttributes().get(attribute);
			}
			jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
			jsonBuilder.append(attribute);
			jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
			jsonBuilder.append(CommonConstants.COLON);
			jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
			jsonBuilder.append(value);
			jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
			jsonBuilder.append(CommonConstants.COMMA);
		}
		
		jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
		jsonBuilder.append(CommonConstants.CLOSING_BRACES);
		
		toCreateJSONElement(jsonBuilder, SERIAL_NUMBER, rncResponse.getSerialNumber());
		toCreateJSONElement(jsonBuilder, SOURCE_UNIT_NAME, rncResponse.getSourceUnitName());
		toCreateJSONElement(jsonBuilder, STATUS_MESSAGE, rncResponse.getStatusMessage());
		jsonBuilder.append(CommonConstants.CLOSING_BRACES);

		return jsonBuilder.toString();
	}
	
	
	public void toCreateJSONElement(StringBuilder jsonBuilder, @Nonnull String key, @Nullable String value) {
		if (value==null) {
			value = "";
		}
		
		jsonBuilder.append(CommonConstants.COMMA);
		jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
		jsonBuilder.append(key);
		jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
		jsonBuilder.append(CommonConstants.COLON);
		jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
		jsonBuilder.append(value);
		jsonBuilder.append(CommonConstants.DOUBLE_QUOTES);
	}
	
	
	public RnCRequest deserialize(String rncRequestStr) throws ParseException {
		Preconditions.checkNotNull(rncRequestStr, "rncRequestStr is null");
		
		JsonObject jsonObject = GsonFactory.defaultInstance().fromJson(rncRequestStr, JsonObject.class);
		RnCRequest rncRequest = new RnCRequest(timesource);
		
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getKey().equals(RnCRequestParser.ATTRIBUTES)) {
				JsonObject attributeObject=entry.getValue().getAsJsonObject();
				for (Entry<String, JsonElement> attribute : attributeObject.entrySet()) {
					rncRequest.setAttribute(attribute.getKey(), attribute.getValue().getAsString());

					if (attribute.getKey().equals(OfflineRnCKeyConstants.PROCESS_DATE.getName())) {
						rncRequest.setProcessDate(attribute.getValue().getAsString());
					}
				}
			} else if (entry.getKey().equals(RnCRequestParser.SERIAL_NUMBER)) {
				rncRequest.setSerialNumber(entry.getValue().getAsString());
				
			} else if(entry.getKey().equals(RnCRequestParser.SOURCE_UNIT_NAME)) {
				rncRequest.setSourceUnitName(entry.getValue().getAsString());
				
			} else if (entry.getKey().equals(RnCRequestParser.STATUS_MESSAGE)) {
				rncRequest.setStatusMessage(entry.getValue().getAsString());
				
			}
		}		
		return rncRequest;
	}

	
	
}