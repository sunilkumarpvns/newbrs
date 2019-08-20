package com.elitecore.diameterapi.mibs.base.extended.localcfgs;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpAppAdvToPeerEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpAppAdvToPeerRowStatus;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpAppAdvToPeerServices;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpAppAdvToPeerStorageType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerAppAdvTable;

public class DbpAppAdvToPeerEntryImpl extends DbpAppAdvToPeerEntry {
	
	private static final String MODULE = "APP-ADV-TO-PEER-ENTRY";
	
	private static EnumDbpAppAdvToPeerRowStatus enumDbpAppAdvToPeerRowStatus;
	private static EnumDbpAppAdvToPeerStorageType enumDbpAppAdvToPeerStorageType;
	private String peerIdentity;
	transient private DiameterConfigProvider configProvider;
	transient private DiameterBasePeerAppAdvTable dbpAppAdvToPeer;
	
	static {
		enumDbpAppAdvToPeerRowStatus = new EnumDbpAppAdvToPeerRowStatus(RowStatus.ACTIVE.code); // Active
		enumDbpAppAdvToPeerStorageType = new EnumDbpAppAdvToPeerStorageType(StorageTypes.PARMANENT.code); // Permanent
	}
	
	
	public DbpAppAdvToPeerEntryImpl(String peerIdentity, 
			DiameterConfigProvider configProvider, 
			DiameterBasePeerAppAdvTable dbpAppAdvFromPeer) {
				this.peerIdentity = peerIdentity;
				this.configProvider = configProvider;
				this.dbpAppAdvToPeer = dbpAppAdvFromPeer;
	}

	@Override
	public EnumDbpAppAdvToPeerRowStatus getDbpAppAdvToPeerRowStatus() {
		return enumDbpAppAdvToPeerRowStatus;
	}

	@Override
	public void setDbpAppAdvToPeerRowStatus(EnumDbpAppAdvToPeerRowStatus x) {}

	@Override
	public EnumDbpAppAdvToPeerStorageType getDbpAppAdvToPeerStorageType() {
		return enumDbpAppAdvToPeerStorageType;
	}

	@Override
	public void setDbpAppAdvToPeerStorageType(EnumDbpAppAdvToPeerStorageType x){}

	@Override
	public void checkDbpAppAdvToPeerStorageType(EnumDbpAppAdvToPeerStorageType x) {}

	@Override
	public EnumDbpAppAdvToPeerServices getDbpAppAdvToPeerServices() {
		return new EnumDbpAppAdvToPeerServices(dbpAppAdvToPeer.getDbpAppAdvFromPeerType());
	}

	@Override
	public Long getDbpAppAdvToPeerIndex()  {
		return dbpAppAdvToPeer.getDbpAppId();
	}

	@Override
	public Long getDbpAppAdvToPeerVendorId()  {
		return dbpAppAdvToPeer.getDbpAppAdvFromPeerVendorId();
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
		DiameterPeerConfig peerConfig = configProvider.getPeerConfig(peerIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Base Peer Config not found for peer:" + peerIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Config not found for peer:" + peerIdentity);
		}
		return peerConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.APP_ADV_TO_PEER_TABLE + peerIdentity+ "-" + getDiameterPeerConfig().getPeerIpAddresses() + "-" + dbpAppAdvToPeer.getAppAdvName();
	}

}
