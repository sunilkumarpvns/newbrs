package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.core.transaction.EventTypes;

public enum GyTransactionType {
	
	SESSION_START(DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST, EventTypes.SESSION_START),
	SESSION_UPDATE(DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST,  EventTypes.SESSION_UPDATE),
	SESSION_STOP(DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST,  EventTypes.SESSION_STOP),
	EVENT_REQUEST(DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST, EventTypes.EVENT_REQUEST_RCVD);
	
	public final int requestType;
	public final String eventType;
	
	
	private GyTransactionType(int requestType, String eventType) {
		this.requestType = requestType;
		this.eventType = eventType;
	}
	
	public static GyTransactionType from(int requestType) {
		
		if (SESSION_START.requestType == requestType) {
			return GyTransactionType.SESSION_START;
		} else if (SESSION_UPDATE.requestType == requestType) {
			return GyTransactionType.SESSION_UPDATE;
		} else if (SESSION_STOP.requestType == requestType) {
			return GyTransactionType.SESSION_STOP;
		} else if (EVENT_REQUEST.requestType == requestType) {
			return GyTransactionType.EVENT_REQUEST;
		}
		
		return null;
	}

}
