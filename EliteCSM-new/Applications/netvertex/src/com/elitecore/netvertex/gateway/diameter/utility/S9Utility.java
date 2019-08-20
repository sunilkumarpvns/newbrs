package com.elitecore.netvertex.gateway.diameter.utility;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;

public class S9Utility {
	private final static String MODULE = "S9-UTILITY";
	private DiameterGatewayControllerContext diameterGatewayControllerContext;
	private MCCMNCRoutingConfiguration mccmncRoutingConfiguration;
	
	public S9Utility(DiameterGatewayControllerContext diameterGatewayControllerContext, MCCMNCRoutingConfiguration mccmncRoutingConfiguration) {
		this.diameterGatewayControllerContext = diameterGatewayControllerContext;
		this.mccmncRoutingConfiguration = mccmncRoutingConfiguration;
	}

	
	/**
	 * 
	 * isSubscriberroaming check that subscriber for whom diameter request is received, is currently roaming or not.
	 * @param diameterPacket
	 * @return
	 *
	 */
	public boolean isSubscrberRoaming(DiameterPacket diameterPacket) {
		
		if(diameterPacket == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().info(MODULE, "Considering subscriber as non-roaming. Reason: DiameterPacket is null");
			return false;
		}

		
		List<IDiameterAVP> mccmncInfoAVPs = diameterPacket.getInfoAVPList(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		if(mccmncInfoAVPs == null || mccmncInfoAVPs.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Considering subscriber as non-roaming with Session-iD " + diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason: "  + DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC + " info attribute not found in diameter packet.");
			return false;
		}
		
		RoutingEntry  subscriberMCCMNCRoutingEntry = null; 
		for(IDiameterAVP mccmncInfoAVP : mccmncInfoAVPs){
			subscriberMCCMNCRoutingEntry = mccmncRoutingConfiguration.getRoutingEntryByMCCMNC(mccmncInfoAVP.getStringValue());
			if(subscriberMCCMNCRoutingEntry != null){
				break; 
			}
		}
		
		if(subscriberMCCMNCRoutingEntry == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Considering subscriber as non-roaming with Session-iD =" + diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID)+
						". Reason: Routing Entry not found for subscriber MCC-MNC");
			return false;
		}
	
		
		String sgsnMCCMNC = diameterPacket.getAVPValue(DiameterAVPConstants.TGPP_SGSN_MCC_MNC);
		if(sgsnMCCMNC == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Considering subscriber as non-roaming with Session-ID= " + diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) +
						". Reason: "  + DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC + " info attribute not found in diameter packet");
			return false;
		}
		
		RoutingEntry  sgsnMCCMNCroutingEntry = mccmncRoutingConfiguration.getRoutingEntryByMCCMNC(sgsnMCCMNC);
		if(sgsnMCCMNCroutingEntry == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Routing entry not found for SGSN-MCC-MNC = " + sgsnMCCMNC + ". Considering Subscriber as non-roaming");
			return false;
		}
		
		
		if(subscriberMCCMNCRoutingEntry.equals(sgsnMCCMNCroutingEntry)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().info(MODULE, "Considering subscriber as Local. Reason: Both Subscriber RoutingEntry and SGSN RotingEntry are same");
			return false;
		}
		
		return true;
		
	}

}
