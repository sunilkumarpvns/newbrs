package com.elitecore.diameterapi.diameter.common.peers.capabilityexchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public class PeerApplicationProvider {

	private static final String MODULE = "PEER-APP-PROVIDER";

	private ApplicationContainer authApplicationContainer;
	private ApplicationContainer acctApplicationContainer;

	private Set<ApplicationEnum> remoteAuthApplications;
	private Set<ApplicationEnum> remoteAcctApplications;

	private boolean isRelayAgent;
	private PeerData peerData;

	private ApplicationProviderFactory applicationProviderFactory;
	
	public PeerApplicationProvider(IDiameterStackContext stackContext,
			PeerData peerData) {
		this(peerData, ApplicationProviderFactory.getInstance(stackContext));
	}
	
	public PeerApplicationProvider(PeerData peerData, ApplicationProviderFactory applicationProviderFactory) {
		this.peerData = peerData;
		this.applicationProviderFactory = applicationProviderFactory;
		this.remoteAcctApplications = new HashSet<ApplicationEnum>();
		this.remoteAuthApplications = new HashSet<ApplicationEnum>();
		
	}

	public void init () {
		
		authApplicationContainer = applicationProviderFactory.createApplicationContainer(
				peerData.getExclusiveAuthAppIDs(), ServiceTypes.AUTH);
		acctApplicationContainer = applicationProviderFactory.createApplicationContainer(
				peerData.getExclusiveAcctAppIDs(), ServiceTypes.ACCT);
	}
	
	/**
	 * @return Set of Enabled Applications for Peer 
	 */
	public Set<ApplicationEnum> getApplications(){
		
		Set<ApplicationEnum> enabledApplications = new HashSet<ApplicationEnum>();
		
		enabledApplications.addAll(authApplicationContainer.getApplications());
		enabledApplications.addAll(acctApplicationContainer.getApplications());
		
		return enabledApplications;
	}
	
	/**
	 * @return  Set of Common Applications for Peer
	 */
	public Set<ApplicationEnum> getCommonApplications() {
		
		Set<ApplicationEnum> commonApplications = new HashSet<ApplicationEnum>();
		
		if(isRelayAgent) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Adding All Enabled Applications, As Peer: " + peerData.getHostIdentity() + " is Relay Agent");
			}
			commonApplications.addAll(authApplicationContainer.getApplications());
			commonApplications.addAll(acctApplicationContainer.getApplications());
		} else {
			commonApplications.addAll(authApplicationContainer.getCommonApplications(remoteAuthApplications));
			commonApplications.addAll(acctApplicationContainer.getCommonApplications(remoteAcctApplications));
		}
		return commonApplications;
	}

	public void clear() {
		remoteAuthApplications = new HashSet<ApplicationEnum>();
		remoteAcctApplications = new HashSet<ApplicationEnum>();
		isRelayAgent = false;
	}

	public void addRemoteApplication(DiameterPacket diameterPacket){
		remoteAuthApplications = buildRemoteApplication(diameterPacket, DiameterAVPConstants.AUTH_APPLICATION_ID, ServiceTypes.AUTH);
		remoteAcctApplications = buildRemoteApplication(diameterPacket, DiameterAVPConstants.ACCT_APPLICATION_ID, ServiceTypes.ACCT);
	}
	
	public Set<ApplicationEnum> getRemoteApplications() {
		Set<ApplicationEnum> remoteApplications = new HashSet<ApplicationEnum>();
		remoteApplications.addAll(remoteAuthApplications);
		remoteApplications.addAll(remoteAcctApplications);
		return remoteApplications;
	}
	
	private Set<ApplicationEnum> buildRemoteApplication(DiameterPacket diameterPacket, 
			String applicationIdAVPCode, ServiceTypes serviceType){
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Adding Remote " + serviceType.serviceTypeStr + 
					" Applications for Peer: " + peerData.getHostIdentity());
		}
		if(isRelayAgent){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Peer: " + peerData.getHostIdentity() + " is Relay Agent");
			}
			return Collections.emptySet();
		}
		StringBuilder remoteAppLst = new StringBuilder();
		Set<ApplicationEnum> remoteApplicationIdsNew = new HashSet<ApplicationEnum>();

		ArrayList<IDiameterAVP> authApplications = diameterPacket.getAVPList(applicationIdAVPCode);
		if (authApplications != null && authApplications.size() > 0) {
			for (IDiameterAVP authApplicationId : authApplications) {
				if(authApplicationId.getInteger() == DiameterConstants.RELAY_APPLICATION_ID){
					isRelayAgent = true;
					break;
				}
				ApplicationEnum applicationEnum = DiameterUtility.createApplicationEnumStrictly(
								authApplicationId.getInteger(), 
								ApplicationIdentifier.BASE.getVendorId(), 
								serviceType); 
				remoteAppLst.append(applicationEnum.getVendorId());
				remoteAppLst.append(':');
				remoteAppLst.append(applicationEnum.getApplicationId());
				remoteAppLst.append(',');
				remoteApplicationIdsNew.add(applicationEnum);
			}
		}
		if(isRelayAgent){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Peer: " + peerData.getHostIdentity() + " is Relay Agent");
			}
			return Collections.emptySet();
		}
		ArrayList<IDiameterAVP> vendorSpecificApplicationIds = diameterPacket.getAVPList(DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID);
		if (vendorSpecificApplicationIds != null && vendorSpecificApplicationIds.size() > 0) {
			for(IDiameterAVP vendorSpecificAttr : vendorSpecificApplicationIds) {
				long vendorId = 0;
				long appId = -1;
				for (IDiameterAVP attr : vendorSpecificAttr.getGroupedAvp()) {
					if (attr.getAVPId().equals(DiameterAVPConstants.VENDOR_ID)) {
						vendorId = attr.getInteger();
					} else if (attr.getAVPId().equals(applicationIdAVPCode)) {
						appId = attr.getInteger();
					}
				}
				if(appId >= 0){
					ApplicationEnum applicationEnum = DiameterUtility.createApplicationEnumStrictly(appId, vendorId, serviceType); 
					remoteAppLst.append(applicationEnum.getVendorId());
					remoteAppLst.append(':');
					remoteAppLst.append(applicationEnum.getApplicationId());
					remoteAppLst.append(',');
					remoteApplicationIdsNew.add(applicationEnum);	
				}
			}
		}
		if(!remoteApplicationIdsNew.isEmpty()){
			remoteAppLst.deleteCharAt(remoteAppLst.length() -1);
		}
		remoteAppLst.append(']');
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Remote " + serviceType.serviceTypeStr + " Applications: [" + remoteAppLst.toString());
		}
		return remoteApplicationIdsNew;
	}
	
}
