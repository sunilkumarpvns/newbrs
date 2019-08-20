package com.elitecore.aaa.diameter.commons;

import com.elitecore.diameterapi.core.translator.TranslatorConstants;

public interface AAATranslatorConstants extends TranslatorConstants {
	/** Translator Constants **/
	String RADIUS_TRANSLATOR = "TTI0004";
	String MAP_TRANSLATOR = "TTI0005";
	
	/** Translator PARAMS constants **/
	String RADIUS_IN_MESSAGE = "RADIUS_IN_MESSAGE";
	String RADIUS_OUT_MESSAGE = "RADIUS_OUT_MESSAGE";

	String RADIUS_REQUEST = "RADIUS_REQUEST";
	String MAP_REQUEST = "MAP_REQUEST";
	
	String RADIUS_RESPONSE = "RADIUS_RESPONSE";
	String MAP_RESPONSE = "MAP_RESPONSE";
	
	/** Translator Mapping Key constants **/
	String RATING_METHOD_NAME = "RATING-METHOD-CALL";

}
