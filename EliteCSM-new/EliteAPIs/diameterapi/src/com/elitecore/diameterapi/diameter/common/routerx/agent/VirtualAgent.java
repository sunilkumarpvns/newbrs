package com.elitecore.diameterapi.diameter.common.routerx.agent;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

/**
 * Virtual Agent is just a Relay Agent that does Topology Hiding.<br />
 * LHS and RHS peer will not be Knowing about each other.<br />
 * So as per, Topology Hiding, Virtual Agent will remove all Route-Record AVPs.
 * 
 * @author monica.lulla
 *
 */
public class VirtualAgent extends RelayAgent {

	public static final String MODULE = "VIRTUAL-AGNT" ;
	
	public VirtualAgent(RouterContext routerContext, IDiameterSessionManager diameterSessionManager) {
		super(routerContext, diameterSessionManager);
	}
	
	/**
	 * This will update Origin and Destination AVPs, if available in Request message.
	 * <br />
	 * Dest-Host Not Arrived in Req. --> Dont Update 
	 * <br />
	 * Dest-Host Arrived in Req. --> Dest-Host will be updated if Dest-Host is Own Identity
	 * <br />
	 * Dest-Realm Not Arrived in Req. --> Dont Update 
	 * <br />
	 * Dest-Realm Arrived in Req. --> Dest-Realm will be updated if Dest-Realm is Own Realm
	 * <br />
	 * Remove Route Records
	 */
	@Override
	protected void postRequestProcessing(DiameterRequest destinationRequest) {

		IDiameterAVP originHostAvp = destinationRequest.getAVP(DiameterAVPConstants.ORIGIN_HOST);
		if(originHostAvp != null){
			originHostAvp.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.ORIGIN_HOST_STR + 
						" AVP replaced for Packet with Session-ID=" + 
						destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
		IDiameterAVP originRealm = destinationRequest.getAVP(DiameterAVPConstants.ORIGIN_REALM);
		if(originRealm != null){
			originRealm.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.ORIGIN_REALM_STR + 
						" AVP replaced for Packet with Session-ID=" + 
						destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
		IDiameterAVP destRealmAVP = destinationRequest.getAVP(DiameterAVPConstants.DESTINATION_REALM);
		
		//No Destination Realm
		if(destRealmAVP == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.DESTINATION_REALM_STR + 
						" AVP not replaced for Request with Session-ID=" + 
						destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
						", Reason: " + DiameterAVPConstants.DESTINATION_REALM_STR + " AVP not Arrived in Request");
			}
		}else{
			if(Parameter.getInstance().getOwnDiameterRealm().equals(destRealmAVP.getStringValue())){

				destRealmAVP.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, DiameterAVPConstants.DESTINATION_REALM_STR + 
							" AVP replaced for Request with Session-ID=" + 
							destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
			}
		}
		IDiameterAVP destHostAVP = destinationRequest.getAVP(DiameterAVPConstants.DESTINATION_HOST);
		
		//No Destination Host
		if(destHostAVP == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.DESTINATION_HOST_STR + 
						" AVP not replaced for Request with Session-ID=" + 
						destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
						", Reason: " + DiameterAVPConstants.DESTINATION_HOST_STR + " AVP not Arrived in Request");
			}
		}else{
			if(Parameter.getInstance().getOwnDiameterIdentity().equals(destHostAVP.getStringValue())){

				destHostAVP.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, DiameterAVPConstants.DESTINATION_HOST_STR + 
							" AVP replaced for Request with Session-ID=" + 
							destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
			}
		}
		List<IDiameterAVP> routeRecords = destinationRequest.getAVPList(DiameterAVPConstants.ROUTE_RECORD);
		if(routeRecords == null)
			return;
		for(IDiameterAVP routeRecord : routeRecords){
			destinationRequest.removeAVP(routeRecord);
		}
	}
	
	/**
	 * This will Update Origin AVPs to Own Identity and Realm. 
	 * This will not update Destination AVPs, acc. to RFC 6733
	 * Destination-Host and Destination-Realm AVPs MUST NOT be present in Answer messages.
	 * Refer Section:  6.5, 6.6
	 */
	@Override
	protected void postAnswerProcessing(DiameterAnswer diameterAnswer) {
		
		IDiameterAVP originHostAvp = diameterAnswer.getAVP(DiameterAVPConstants.ORIGIN_HOST);
		if(originHostAvp != null){
			originHostAvp.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.ORIGIN_HOST_STR + 
						" AVP replaced for Packet with Session-ID=" + 
						diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
		IDiameterAVP originRealm = diameterAnswer.getAVP(DiameterAVPConstants.ORIGIN_REALM);
		if(originRealm != null){
			originRealm.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, DiameterAVPConstants.ORIGIN_REALM_STR + 
						" AVP replaced for Packet with Session-ID=" + 
						diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
	}
	
}
