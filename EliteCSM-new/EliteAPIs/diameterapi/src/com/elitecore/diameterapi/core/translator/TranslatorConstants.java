package com.elitecore.diameterapi.core.translator;

import com.elitecore.core.commons.util.MathOperationUtility;
import com.elitecore.core.commons.util.StringOperationUtility;

public interface TranslatorConstants {
	
	/** Translator Constants **/
	String DIAMETER_TRANSLATOR = "TTI0001";
	String RATING_TRANSLATOR = "TTI0002";
	String WEBSERVICE_TRANSLATOR = "TTI0003";
	String CRESTEL_OCSv2_TRANSLATOR = "TTI0006";
	
	
	String DIAMETER_REQUEST = "DIAMETER_REQUEST";
	String DIAMETER_RESPONSE = "DIAMETER_RESPONSE";
	
	String CRESTEL_OCSv2_REQUEST = "CRESTEL_OCSv2_REQUEST";
	String CRESTEL_OCSv2_RESPONSE = "CRESTEL_OCSv2_RESPONSE";
	
	String STACK_CONTEXT  = "Stack_Context";
	
	/** Diameter Routing Constans **/
	String DIAMETER_SESSION = "DIAMETER_SESSION";
			
	/* Web Service Supported constants */
	String DIAMETER_RE_AUTH = "diameter-re-auth";
	String DIAMETER_ABORT_SESSION = "diameter-abort-session";
	String DIAMETER_GENERIC_REQUEST = "diameter-generic-request";
	
	/** Translator Mapping Key constants **/
	String RESULT_CODE = "RESULT_CODE";
	
	/** Translator Mapping Supported Keywords **/
	String SOURCE_REQUEST = "${SRCREQ}";
	String DESTINATION_REQUEST = "${DSTREQ}";
	String TIMESTAMP = "${TIMESTAMP}";
	String MATHOP = MathOperationUtility.MATHOP;
	String RANDOM  = "${RANDOM}";
	String STROPT  = StringOperationUtility.STROP;
	String SEQSESS = "${SEQSESS}";
	String SEQPEER  = "${SEQPEER}";
	String SEQSERV  = "${SEQSERV}";
	String DBSESSION = "${DBSESSION}";
	String DUMMY_MAPPING = "DUMMY_MAPPING";
	String RATING_SEPERATOR = ":";
	String MAC2TGPP = "${MAC2TGPP}";
	String SRCRES = "${SRCRES}";
	
	/** Translation Request-Response Mapping Constant  **/
	boolean REQUEST_TRANSLATION = true;
	boolean RESPONSE_TRANSLATION = false;

	/**Added For the Translation Mapping Request/Respose Constants**/
	String TO_PACKET = "TO_PACKET";
	String FROM_PACKET = "FROM_PACKET";
	String SELECTED_REQUEST_MAPPING ="SELECTED_REQUEST_MAPPING";
	String SELECTED_TRANSLATION_POLICY = "SELECTED_TRANSLATION_POLICY";
	
	/**
	 *  Diameter Header Properties
	 */
	String APPLICATION_ID = "ApplicationId";
	String COMMAND_CODE = "CommandCode";
	String PROXY_FLAG = "ProxyFlag";
	String ERROR_FLAG = "ErrorFlag";
	String RETRANSMITTED_FLAG = "RetransmittedFlag";
	
	/**
	 *  Radius Header Properties
	 */
	String PACKET_TYPE = "PacketType";
	
	
	/**
	 * 
	 */
	String COPY_PACKET_MAPPING_NAME = "COPY_PACKET_MAPPING";
	
	String SHARED_SECRET = "SHARED_SECRET" ;
	
}
