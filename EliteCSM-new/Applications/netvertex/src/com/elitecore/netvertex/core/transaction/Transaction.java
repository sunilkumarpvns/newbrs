package com.elitecore.netvertex.core.transaction;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.DiameterAnswerListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


public interface Transaction extends Cloneable{
	
	public String getType();
	
	public void process(String eventType,DiameterRequest request);
	
	public void process(String eventType,PCRFRequest request);
	
	public void resume(DiameterAnswer answer);
	
	public void resume(PCRFResponse response);
	
	public String getTransactionId();
	
	public long getStartTime();

	public Object clone() throws CloneNotSupportedException;
	
	long getLastAccessTime();
	void updateLastAccessTime();

	void resume(DiameterRequest request);

	public default void process(String eventType, DiameterRequest request, DiameterAnswerListener diameterAnswerListener) {
		process(eventType, request);
	}	
}
