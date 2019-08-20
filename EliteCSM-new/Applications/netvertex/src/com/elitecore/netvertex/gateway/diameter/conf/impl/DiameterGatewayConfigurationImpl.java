package com.elitecore.netvertex.gateway.diameter.conf.impl;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.constants.UMStandard;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.netvertex.core.conf.impl.DiameterGatewayFactory.ConnectionURL;
import com.elitecore.netvertex.core.conf.impl.DiameterGatewayFactory.LocalAddress;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayProfileConfiguration;

/**
 * 
 * @author Harsh Patel
 * 
 */
public class DiameterGatewayConfigurationImpl implements DiameterGatewayConfiguration {

	// PCC Rule INSTALL MODE
	public static final int ALL_ON_NETWORK_ENTRY = 0;
	public static final int FIRST_ON_NETWORK_ENTRY = 1;
	public static final int NONE_ON_NETWORK_ENTRY = 2;

	private String name;
	private String gatewayId;
	private String profileId;
	private String alternateHostName;
	private DiameterPeerGroupParameter diameterPeerGroupParameter;
	private CommunicationProtocol commProtocol;
	private String description;
	private ConnectionURL connectionURL;
	private String realm;
	private LocalAddress localAddress;
	private String hostIdentity;
	private PolicyEnforcementMethod enforcementMethod = PolicyEnforcementMethod.STANDARD;
	private int retransmissionCount;
	private long requestTimeout;
	
	private DiameterGatewayProfileConfiguration profile;
	
	
	public DiameterGatewayConfigurationImpl(String name, String gatewayId, String profileId
			, String alternateHostName, DiameterPeerGroupParameter diameterPeerGroupParameter, CommunicationProtocol commProtocol
			, String description, ConnectionURL connectionURL, String realm, LocalAddress localAddress, String hostIdentity
			, PolicyEnforcementMethod enforcementMethod, int retransmissionCount, long requestTimeout, DiameterGatewayProfileConfiguration diameterGatewayProfileConfiguration) {

		this.name = name;
		this.gatewayId = gatewayId;
		this.profileId = profileId;
		this.alternateHostName = alternateHostName;
		this.diameterPeerGroupParameter = diameterPeerGroupParameter;
		this.commProtocol = commProtocol;
		this.description = description;
		this.connectionURL = connectionURL;
		this.realm = realm;
		this.localAddress = localAddress;
		this.hostIdentity = hostIdentity;
		this.enforcementMethod = enforcementMethod;
		this.retransmissionCount = retransmissionCount;
		this.requestTimeout = requestTimeout;
		this.profile = diameterGatewayProfileConfiguration;
		
	}

	public DiameterGatewayConfigurationImpl() {

	}

	public DiameterGatewayConfigurationImpl setName(String name) {
		this.name = name;
		return this;
	}

	public DiameterGatewayConfigurationImpl setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
		return this;
	}

	public DiameterGatewayConfigurationImpl setAlternateHostName(String alternateHostName) {
		this.alternateHostName = alternateHostName;
		return this;
	}

	public DiameterGatewayConfigurationImpl setDiameterPeerGroupParameter(DiameterPeerGroupParameter diameterPeerGroupParameter) {
		this.diameterPeerGroupParameter = diameterPeerGroupParameter;
		return this;
	}

	public DiameterGatewayConfigurationImpl setCommProtocol(CommunicationProtocol commProtocol) {
		this.commProtocol = commProtocol;
		return this;
	}

	public DiameterGatewayConfigurationImpl setConnectionURL(ConnectionURL connectionURL) {
		this.connectionURL = connectionURL;
		return this;
	}

	public DiameterGatewayConfigurationImpl setRealm(String realm) {
		this.realm = realm;
		return this;
	}

	public DiameterGatewayConfigurationImpl setLocalAddress(LocalAddress localAddress) {
		this.localAddress = localAddress;
		return this;
	}

	public DiameterGatewayConfigurationImpl setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
		return this;
	}

	public DiameterGatewayConfigurationImpl setRetransmissionCount(int retransmissionCount) {
		this.retransmissionCount = retransmissionCount;
		return this;
	}

	public DiameterGatewayConfigurationImpl setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
		return this;
	}

	@Override
	public void setProfileId (String profileId) {
		this.profileId = profileId;
	}
	
	@Override
	public void setDescription (String description) {
		this.description = description;
	}
	
	public void setProfile(DiameterGatewayProfileConfiguration diameterGatewayProfileConfiguration) {
		this.profile = diameterGatewayProfileConfiguration;
	}
	
	@Override
	public void setEnforcementMethod (PolicyEnforcementMethod policyEnforcementMethod) {
		this.enforcementMethod = policyEnforcementMethod;
	}

	@Override
	public PCCToDiameterMapping getGxCCAMappings() {
		return profile.getGxCCAMappings();
	}

	@Override
	public PCCToDiameterMapping getGyCCAMappings() {
		return profile.getGyCCAMappings();
	}

	@Override
	public PCCToDiameterMapping getGxRARMappings() {
		return profile.getGxRARMappings();
	}

	@Override
	public PCCToDiameterMapping getGyRARMappings() {
		return profile.getGyRARMappings();
	}

	@Override
	public PCCToDiameterMapping getSNAMappings() {
		return profile.getSNAMappings();
	}

	@Override
	public PCCToDiameterMapping getSLRMappings() {
		return profile.getSLRMappings();
	}

	@Override
	public PCCToDiameterMapping getSTAMappings() {
		return profile.getSTAMappings();
	}

	@Override
	public PCCToDiameterMapping getASRMappings() {
		return profile.getASRMappings();
	}

	@Override
	public DiameterToPCCMapping getGxCCRMappings() {
		return profile.getGxCCRMappings();
	}

	@Override
	public DiameterToPCCMapping getGyCCRMappings() {
		return profile.getGyCCRMappings();
	}

	@Override
	public DiameterToPCCMapping getGxRAAMappings() {
		return profile.getGxRAAMappings();
	}

	@Override
	public DiameterToPCCMapping getGyRAAMappings() {
		return profile.getGyRAAMappings();
	}

	@Override
	public DiameterToPCCMapping getAARMappings() {
		return profile.getAARMappings();
	}

	@Override
	public DiameterToPCCMapping getASAMappings() {
		return profile.getASAMappings();
	}

	@Override
	public DiameterToPCCMapping getSLAMappings() {
		return profile.getSLAMappings();
	}

	@Override
	public DiameterToPCCMapping getSNRMappings() {
		return profile.getSNRMappings();
	}

	@Override
	public DiameterToPCCMapping getSTRMappings() {
		return profile.getSTRMappings();
	}

	@Override
	public PCCToDiameterMapping getAAAMappings() {
		return profile.getAAAMappings();
	}

	@Override
	public PCCToDiameterMapping getSySTRMapping() {
		return profile.getSySTRMapping();
	}

	@Override
	public long getS9ApplicationId() {
		return profile.getS9ApplicationId();
	}

	@Override
	public long getS9VendorId() {
		return profile.getS9VendorId();
	}

	@Override
	public CommunicationProtocol getCommProtocolId() {
		return commProtocol;
	}

	@Override
	public String getConnectionURL() {
		return connectionURL.getConfiguredURL();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getGatewayId() {
		return gatewayId;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getHostIdentity() {
		return hostIdentity;
	}

	
	@Override
	public String getProfileId() {
		return profileId;
	}

	@Override
	public String getRealm() {
		return realm;
	}

	


	
	@Override
	public InetAddress getHostInetAddress(){
		return connectionURL.getRemoteInetAddress();
	}

	@Override
	public Vendor getVendor() {
		return profile.getVendor();
	}
	
	@Override
	public int getHostPort() {
		return connectionURL.getPort();
	}
	
	@Override
	public String getHostIPAddress() {
		return connectionURL.getIPAddress();
	}
	
	@Override
	public int getLocalPort() {
		return localAddress.getLocalPort();
	}
	
	@Override
	public String getLocalIPAddress() {
		return localAddress.getLocalIpAddress();
	}
	
	@Override
	public long getRequestTimeout() {
		return requestTimeout;
	}

	@Override
	public int getRetransmissionCount() {
		return retransmissionCount;
	}

	
	@Override
	public SupportedStandard getSupportedStandard() {
		return profile.getSupportedStandard();
	}

	@Override
	public ChargingRuleInstallMode getChargingRuleInstallMode() {
		return profile.getChargingRuleInstallMode();
	}
	
	@Override
	public int getTimeout() {
		return profile.getTimeout();
	}


	@Override
	public long getGxApplicationId() {
		return profile.getGxApplicationId();
	}

	@Override
	public long getRxApplicationId() {
		return profile.getRxApplicationId();
	}

	@Override
	public long getGxVendorId() {
		return profile.getGxVendorId();
	}

	@Override
	public long getRxVendorId() {
		return profile.getRxVendorId();
	}

	@Override
	public int getInitConnectionDuration() {
		return profile.getInitConnectionDuration();
	}
		
	@Override
	public PCRFKeyValueConstants getUsageReportingType() {
		return profile.getUsageReportingType();
	}


	@Override
	public int getDWInterval() {
		return profile.getDWInterval();
	}

	@Override
	public GatewayComponent getGatewayType() {
		return profile.getGatewayType();
	}

	@Override
	public long getGyApplicationId() {
		return profile.getGyApplicationId();
	}

	@Override
	public long getGyVendorId() {
		return profile.getGyVendorId();
	}
	
	@Override
	public boolean isSessCleanupOnCER() {
		return profile.isSessCleanupOnCER();
	}

	@Override
	public boolean isSessCleanupOnDPR() {
		return profile.isSessCleanupOnDPR();
	}

	@Override
	public boolean isSendDPROnCloseEvent() {
		return profile.isSendDPROnCloseEvent();
	}

	@Override
	public String getAdditionalCERAVPs() {
		return profile.getAdditionalCERAVPs();
	}

	@Override
	public String getAdditionalDPRAVPs() {
		return profile.getAdditionalDPRAVPs();
	}

	@Override
	public int getSocketReceiveBufferSize() {
		return profile.getSocketReceiveBufferSize();
	}

	@Override
	public int getSocketSendBufferSize() {
		return profile.getSocketSendBufferSize();
	}

	@Override
	public boolean isTCPNagalAlgo() {
		return profile.isTCPNagleAlgo();
	}

	@Override
	public String getAdditionalDWRAVPs() {
		return profile.getAdditionalDWRAVPs();
	}

	@Override
	public TransportProtocols getTransportProtocol() {
		return profile.getTransportProtocol();
	}
	
	@Override
	public PCRFKeyValueConstants getRevalidationMode() {
		return profile.getRevalidationMode();
	}
	
	@Override
	public PolicyEnforcementMethod getEnforcementMethod(){
		return enforcementMethod;
	}

	@Override
	public List<ScriptData> getScriptsConfigs() {
		return profile.getScriptsConfigs();
	}

	@Override
	public long getSyApplicationId() {
		return profile.getSyApplicationId();
	}

	@Override
	public long getSyVendorId() {
		return profile.getSyVendorId();
	}


	@Override
	public UMStandard getUsageMeteringStandard() {
		return profile.getUsageMeteringStandard();
	}

	@Override
	public List<String> getSessionLookupKey() {
		return profile.getSessionLookupKey();
	}

	@Override
	public boolean isPCCLevelMonitoringSupported() {
		return profile.isPCCLevelMonitoringSupported();
	}
	
	@Override
	public String getAlternateHostName() {
		return alternateHostName;
	}
	
	@Override
	public DiameterPeerGroupParameter getDiameterPeerGroupParameter() {
		return diameterPeerGroupParameter;
	}

	@Override
	public Map<ApplicationPacketType, PCCToDiameterMapping> getPCCToDiameterPacketMappings() {
		return profile.getPCCToDiameterPacketMappings();
	}

	@Override
	public Map<ApplicationPacketType, DiameterToPCCMapping> getDiameterToPCCPacketMappings() {
		return profile.getDiameterToPCCPacketMappings();
	}

	@Override
	public List<ServiceGuide> getServiceGuides(){
		return profile.getServiceGuides();
	}

	public SessionTypeConstant getSessionType(long applicationId) {
		return profile.getSessionType(applicationId);
	}

	@Override
	public String toString(){

		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Diameter Gateway Configuration -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		String localAddressVal = this.localAddress.getLocalIpAddress() == null ? "ANY_LOCAL_ADDRESS" : this.localAddress.getLocalIpAddress();
		String localPort = this.localAddress.getLocalPort() == 0 ? "ANY_PORT" : String.valueOf(this.localAddress.getLocalPort());
		builder.incrementIndentation();
		builder.append("Id", gatewayId);
		builder.append("Name", name);
		builder.append("Description", description);
		builder.append("Policy Enforcement Method", enforcementMethod);
		builder.appendChildObject("Profile", profile);
		builder.append("Alternate Host", alternateHostName);

		if(connectionURL.isConfigured()) {
			builder.append("Connection URL",connectionURL.getIPAddress() + ":" + connectionURL.getPort());
		}

		builder.append("HostIdentity", hostIdentity);
		builder.append("Realm", realm);
		builder.append("Local Address", localAddressVal + ":" + localPort);
		builder.append("Request Timeout", requestTimeout + " ms");
		builder.append("Retransmission Count", retransmissionCount);
		builder.append("Communication Protocol", commProtocol.getName());
		builder.decrementIndentation();
	}
}
