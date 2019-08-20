package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpAppAdvFromPeerEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpAppAdvFromPeerType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerAppAdvTable;

public class DbpAppAdvFromPeerEntryImpl extends DbpAppAdvFromPeerEntry {
	
	private static final String MODULE = "DBP-APP-ADV-FRM-PEER-ENTRY";

	private static final long serialVersionUID = 1L;
	transient private DiameterBasePeerAppAdvTable appEntry;
	private String hostIdentity;
	transient private DiameterConfigProvider diameterConfigProvider;
	
	public DbpAppAdvFromPeerEntryImpl(String peerIdentity, DiameterConfigProvider diameterConfigProvider, 
			DiameterBasePeerAppAdvTable dbpAppAdvFromPeer) {
		this.appEntry = dbpAppAdvFromPeer;
		this.hostIdentity = peerIdentity;
		this.diameterConfigProvider = diameterConfigProvider;
	}

	@Override
	public EnumDbpAppAdvFromPeerType getDbpAppAdvFromPeerType(){
		return new EnumDbpAppAdvFromPeerType(appEntry.getDbpAppAdvFromPeerType());
	}

	@Override
	public Long getDbpAppAdvFromPeerIndex() {
		return appEntry.getDbpAppId();
	}

	@Override
	public Long getDbpAppAdvFromPeerVendorId() {
		return appEntry.getDbpAppAdvFromPeerVendorId();
	}

	@Override
	public Long getDbpPeerIndex()  {
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return peerConfig.getDbpPeerIndex();
	}
	private DiameterPeerConfig getDiameterPeerConfig(){
		DiameterPeerConfig peerConfig = diameterConfigProvider.getPeerConfig(hostIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Base Peer Config not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Config not found for peer:" + hostIdentity);
		}
		return peerConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.APP_ADV_FROM_PEER_TABLE + hostIdentity+ "-" + getDiameterPeerConfig().getPeerIpAddresses() + "-" + appEntry.getAppAdvName();
	}

}
