package com.elitecore.diameterapi.diameter.common.packet;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;

public class DiameterRequest extends DiameterPacket {	
	
	public static final String LOCATED_SESSION_DATA = "Located-Session-Data";
	
	private List<String> failedPeerList;
	
	private String requestingHost;
	private RoutingActions routingAction = RoutingActions.LOCAL;
	
	public DiameterRequest(boolean isLocal) {
		setRequestBit();
		if (isLocal) {
			setHop_by_hopIdentifier(HopByHopPool.get());
			setEnd_to_endIdentifier(EndToEndPool.get());
			// Adding Origin Host as must for all types of Diameter Message
			IDiameterAVP originHostAVP = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_HOST));
			originHostAVP.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
			addAvp(originHostAVP);
	
			// Adding Origin Host as must for all types of Diameter Message
			IDiameterAVP originRealm = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_REALM));
			originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
			addAvp(originRealm);
		}
	}
	public DiameterRequest() {
		this(true);
	}

	@Override
	public DiameterRequest getAsDiameterRequest() {
		return this;
	}

	@Override
	public DiameterAnswer getAsDiameterAnswer() {
		return null;
	}

	@Override
	public void parsePacketHeaderBytes(byte[] headerBytes) {
		super.parsePacketHeaderBytes(headerBytes);
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
		setParameter(LOCATED_SESSION_DATA, locatedSessionData);
	}
	
	@SuppressWarnings("unchecked")
	public List<SessionData> getLocatedSessionData(){
		return (List<SessionData>) getParameter(LOCATED_SESSION_DATA);
	}

	public String getRequestingHost() {
		return requestingHost;
	}
	
	public void setRequestingHost(String requestingHost) {
		this.requestingHost = requestingHost;
	}
	
	public RoutingActions getRoutingAction() {
		return routingAction;
	}
	
	public void setRoutingAction(RoutingActions routingAction) {
		this.routingAction = routingAction;
	}
}
