package com.elitecore.netvertex.core.util;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

public class PCRFRequestBuilder {

	@Nonnull private final PCRFRequestImpl pcrfRequest;


	public PCRFRequestBuilder(Collection<PCRFEvent> pcrfEvents) {
		pcrfRequest = new PCRFRequestImpl();
		for (PCRFEvent pcrfEvent : pcrfEvents) {			
			pcrfRequest.addPCRFEvent(pcrfEvent);
		}
	}

	public PCRFRequestBuilder(Collection<PCRFEvent> pcrfEvents, TimeSource timeSource) {
		pcrfRequest = new PCRFRequestImpl(timeSource);
		for (PCRFEvent pcrfEvent : pcrfEvents) {			
			pcrfRequest.addPCRFEvent(pcrfEvent);
		}
	}
	
	public PCRFRequestBuilder(TimeSource timeSource) {
		pcrfRequest = new PCRFRequestImpl(timeSource);
	}
	
	public PCRFRequestBuilder(PCRFEvent... pcrfEvents) {
		pcrfRequest = new PCRFRequestImpl();
		for (PCRFEvent pcrfEvent : pcrfEvents) {			
			pcrfRequest.addPCRFEvent(pcrfEvent);
		}
	}
	
	public PCRFRequestBuilder(PCRFEvent pcrfEvent, TimeSource timeSource) {
		pcrfRequest = new PCRFRequestImpl(timeSource);
		pcrfRequest.addPCRFEvent(pcrfEvent);
	}
	
	public PCRFRequestBuilder(PCRFEvent pcrfEvent, PCRFEvent pcrfEvent2) {
		pcrfRequest = new PCRFRequestImpl();
		pcrfRequest.addPCRFEvent(pcrfEvent);
		pcrfRequest.addPCRFEvent(pcrfEvent2);
	}
	
	public PCRFRequestBuilder(PCRFEvent pcrfEvent,PCRFEvent pcrfEvent2, PCRFEvent pcrfEvent3, TimeSource timeSource) {
		pcrfRequest = new PCRFRequestImpl(timeSource);
		pcrfRequest.addPCRFEvent(pcrfEvent);
		pcrfRequest.addPCRFEvent(pcrfEvent2);
		pcrfRequest.addPCRFEvent(pcrfEvent3);
	}
	
	
	public PCRFRequestBuilder addSubscriberIdentity(@Nonnull String subscriberIdentity){
		addAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		return this;
	}
	
	public PCRFRequestBuilder addCoreSessionID(@Nonnull String coreSessionId) {
		addAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId);
		return this;
	}
	
	public PCRFRequestBuilder addAttribute(@Nonnull String attribute, @Nonnull String val) {
		pcrfRequest.setAttribute(attribute, val);
		return this;
	}
	
	public PCRFRequestBuilder addAttribute(@Nonnull PCRFKeyConstants attribute, @Nonnull String value) {
		addAttribute(attribute.val,value);
		return this;
	}
	
	public PCRFRequestBuilder addSessionType(@Nonnull SessionTypeConstant sessionType) {
		addAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, sessionType.val);
		return this;
	} 
	
	
	public PCRFRequest build(){
		return pcrfRequest;
	}

	public PCRFRequestBuilder addSessionID(String sessionID) {
		addAttribute(PCRFKeyConstants.CS_SESSION_ID.val, sessionID);
		return this;
	}

	public PCRFRequestBuilder withSubscriberProfile(SPRInfo sprInfo) {
		pcrfRequest.setSPRInfo(sprInfo);
		return this;
	}

    public PCRFRequestBuilder setReportedMSCC(ArrayList<MSCC> reportedMSCCS) {
        pcrfRequest.setReportedMSCCs(reportedMSCCS);
		return this;
    }

	public PCRFRequestBuilder addEvents(PCRFEvent... sessionUpdate) {
		for (PCRFEvent pcrfEvent : sessionUpdate) {
			pcrfRequest.addPCRFEvent(pcrfEvent);
		}

		return this;
	}
}
