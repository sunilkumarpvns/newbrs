package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

public class SessionRuleData {
	private long srID;
	private String sessionID;
	private String afApplicationId;
	private String mediaType;
	private String pccRule;
	private Timestamp startTime;
	private Timestamp lastUpdateTime;
	private String uplinkFlow;
	private String downlinkFlow;
	private PCCRuleData pccRuleData;
	private MediaTypeData mediaTypeData;
	
	public long getSrID() {
		return srID;
	}
	
	public void setSrID(long srID) {
		this.srID = srID;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getAfApplicationId() {
		return afApplicationId;
	}
	
	public void setAfApplicationId(String afApplicationId) {
		this.afApplicationId = afApplicationId;
	}
	
	public String getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	
	public String getPccRule() {
		return pccRule;
	}
	
	public void setPccRule(String pccRule) {
		this.pccRule = pccRule;
	}
	
	public Timestamp getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getUplinkFlow() {
		return uplinkFlow;
	}

	public void setUplinkFlow(String uplinkFlow) {
		this.uplinkFlow = uplinkFlow;
	}

	public String getDownlinkFlow() {
		return downlinkFlow;
	}

	public void setDownlinkFlow(String downlinkFlow) {
		this.downlinkFlow = downlinkFlow;
	}

	public MediaTypeData getMediaTypeData() {
		return mediaTypeData;
	}

	public void setMediaTypeData(MediaTypeData mediaTypeData) {
		this.mediaTypeData = mediaTypeData;
	}

	public PCCRuleData getPccRuleData() {
		return pccRuleData;
	}

	public void setPccRuleData(PCCRuleData pccRuleData) {
		this.pccRuleData = pccRuleData;
	}
	
	
}