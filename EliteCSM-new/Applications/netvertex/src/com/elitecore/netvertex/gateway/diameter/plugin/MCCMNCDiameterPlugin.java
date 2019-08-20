package com.elitecore.netvertex.gateway.diameter.plugin;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.plugins.BaseDiameterPlugin;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.roaming.MCCMNCEntry;

import java.util.List;

public class MCCMNCDiameterPlugin extends BaseDiameterPlugin {
	
	private static final String MODULE = "MCC-MNC-DIA-PLUG-IN";

	public MCCMNCDiameterPlugin(PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}

	@Override
	public void handleOutMessage(DiameterPacket diameterRequest, DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		//no need to handle

	}

	@Override
	public void handleInMessage(DiameterPacket diameterRequest, DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {

		if(diameterRequest == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Applying "+ getName() +" plugin skipped.Reason:  MCC-MNC Diameter Plugin is only apply on diameter request.");
			return;
		}
		
		if(diameterRequest.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.code ||
				diameterRequest.getCommandCode() == CommandCode.DEVICE_WATCHDOG.code ||
				diameterRequest.getCommandCode() == CommandCode.DISCONNECT_PEER.code){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Applying "+ getName() +" plugin skipped.Reason: MCCMNC plugin is not applicable on Capability-Exchange, Device-Watchdog, disconnect-peer messages");
			return;
			
		}
			
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Applying " + getName() + " plugin on diameter request with Session ID: " + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		
		List<IDiameterAVP> diameterAVPs = diameterRequest.getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
		if(diameterAVPs == null || diameterAVPs.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Applying "+ getName() +" plugin skipped. Reason:  Subscription ID("+ DiameterAVPConstants.SUBSCRIPTION_ID +") not found for diameter packet with Session ID  = " + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) );
			return;
		}
		
		IDiameterAVP imsiDiaAVP = null;
		for(IDiameterAVP diameterAVP : diameterAVPs){
			IDiameterAVP subscriptionIdType = ((AvpGrouped) diameterAVP).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
			
			if(subscriptionIdType.getInteger() == DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI){
				imsiDiaAVP = ((AvpGrouped) diameterAVP).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
				break;
			}
				
		}
		
		if(imsiDiaAVP == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Applying "+ getName() +" plugin skipped. Reason: IMSI Subscription ID("+ DiameterAVPConstants.SUBSCRIPTION_ID +") not found for diameter packet with Session ID  = " + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) );
			return;
		}
		
		String imsi = imsiDiaAVP.getStringValue();
		
		if(imsi.length() < 5){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Applying "+ getName() +" plugin skipped. Reason: Length of IMSI Subscription ID is less than 5, IMSI: " + imsi);
			return;
		}
		
		NetVertexServerContext serverContext = (NetVertexServerContext)getPluginContext().getServerContext();
		
		String mccmnc = null;
		MCCMNCEntry  mccmncEntry = null;
		if(imsi.length() >= 6){
			mccmnc = imsi.substring(0, 6);
			mccmncEntry = serverContext.getServerConfiguration().getMCCMNCRoutingConfiguration().getMCCMNCEntry(mccmnc);
		if(mccmncEntry != null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "MCCMNC "+ mccmnc +" found from routing configuration");
				IDiameterAVP subscriberMCCMNC = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
			subscriberMCCMNC.setStringValue(mccmnc);
			diameterRequest.addInfoAvp(subscriberMCCMNC);
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "MCCMNC "+ mccmnc +" not found from routing configuration");
		}
		}else {
			//log??
		}
		
		
		mccmnc = imsi.substring(0, 5);
		mccmncEntry = serverContext.getServerConfiguration().getMCCMNCRoutingConfiguration().getMCCMNCEntry(mccmnc);
		if(mccmncEntry != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "MCCMNC "+ mccmnc +" found from routing configuration");
			IDiameterAVP subscriberMCCMNC = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
			subscriberMCCMNC.setStringValue(mccmnc);
			diameterRequest.addInfoAvp(subscriberMCCMNC);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "MCCMNC "+ mccmnc +" not found from routing configuration");
		}
		
		
	}

	@Override
	public void init() throws InitializationFailedException {
		//no need to handle
	}

}
