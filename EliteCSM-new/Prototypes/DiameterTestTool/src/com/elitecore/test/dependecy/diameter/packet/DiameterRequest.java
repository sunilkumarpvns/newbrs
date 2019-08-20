package com.elitecore.test.dependecy.diameter.packet;

import com.elitecore.test.dependecy.*;
import com.elitecore.test.dependecy.diameter.DiameterAVPConstants;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.EndToEndPool;
import com.elitecore.test.dependecy.diameter.HopByHopPool;
import com.elitecore.test.dependecy.diameter.Parameter;
import com.elitecore.test.dependecy.diameter.SessionData;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiameterRequest extends DiameterPacket {	
	
	private Map<String, Object> parameterMap;
	private List<String> failedPeerList;
	private List<SessionData> locatedSessionData;
	
	public DiameterRequest(boolean isLocal) {
		setRequestBit();
		parameterMap = new HashMap<String, Object>();
		if(isLocal) {
			setHop_by_hopIdentifier(HopByHopPool.get());
			setEnd_to_endIdentifier(EndToEndPool.get());
			// Adding Origin Host as must for all types of Diameter Message
			IDiameterAVP originHost = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_HOST));
			originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
			addAvp(originHost);
	
			// Adding Origin Host as must for all types of Diameter Message
			IDiameterAVP originRealm = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_REALM));
			originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
			addAvp(originRealm);
		}
	}
	public DiameterRequest() {
		this(true);
	}

	public Object getParameter(String str) {			
		return parameterMap.get(str);
	}

	public void setParameter(String key, Object parameterValue) {			
		parameterMap.put(key, parameterValue);
	}
	
	/*
	 * add peer to failed list for this request
	 */
	public void addFailedPeer(String hostIdentity){
		if(failedPeerList == null)
			this.failedPeerList = new ArrayList<String>(1);
		failedPeerList.add(hostIdentity);
	}
	public List<String> getFailedPeerList(){
		return failedPeerList;
	}
	public void setLocatedSessionData(List<SessionData> locatedSessionData) {
		this.locatedSessionData = locatedSessionData;
	}
	
	public List<SessionData> getLocatedSessionData(){
		return locatedSessionData;
	}
}
