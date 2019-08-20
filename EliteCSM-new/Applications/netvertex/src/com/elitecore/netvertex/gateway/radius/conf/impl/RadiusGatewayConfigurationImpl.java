package com.elitecore.netvertex.gateway.radius.conf.impl;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayProfileConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Harsh Patel
 *
 */
public class RadiusGatewayConfigurationImpl implements RadiusGatewayConfiguration {

	private String name;
	private String gatewayId;
	private String profileId;
	private String description;
	private String connectionURL;
	private String sharedSecret;
	private String ipAddress;
	private int port = 3799;
	private int minimumLocalPort;
	private PolicyEnforcementMethod enforcementMethod;
	private RadiusGatewayProfileConfiguration radiusGatewayProfileConfiguration;

	public RadiusGatewayConfigurationImpl(String name, String gatewayId,
			String profileId, String description, String connectionURL, String sharedSecret, String ipAddress, int port, int minimumLocalPort,
			PolicyEnforcementMethod enforcementMethod, RadiusGatewayProfileConfiguration radiusGatewayProfileConfiguration) {

		this.name = name;
		this.gatewayId = gatewayId;
		this.profileId = profileId;
		this.description = description;
		this.connectionURL = connectionURL;
		this.sharedSecret = sharedSecret;
		this.ipAddress = ipAddress;
		this.port = port;
		this.minimumLocalPort = minimumLocalPort;
		this.enforcementMethod = enforcementMethod;
		this.radiusGatewayProfileConfiguration = radiusGatewayProfileConfiguration;
	}

	@Override
	public String getConnectionURL() {
		return connectionURL;
	}

	@Override
	public String getIPAddress() {
		return ipAddress;
	}
	
	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getGatewayId() {
		return gatewayId;
	}

	@Override
	public int getMaxRequestTimeout() {
		return radiusGatewayProfileConfiguration.getMaxRequestTimeout();
	}

	@Override
	public String getProfileId() {
		return profileId;
	}

	@Override
	public String getSharedSecret() {
		return sharedSecret;
	}

	@Override
	public int getTimeout() {
		return radiusGatewayProfileConfiguration.getTimeout();
	}
	
	@Override
	public PolicyEnforcementMethod getPolicyEnforcementMethod() {
		return enforcementMethod;
	}

	@Override
	public Vendor getVendor() {
		return radiusGatewayProfileConfiguration.getVendor();
	}


	@Override
	public boolean isAccountingResponseEnable() {
		return radiusGatewayProfileConfiguration.isAccountingResponseEnable();
	}
	
	@Override
	public PCRFKeyValueConstants getUsageReportingType() {
		return radiusGatewayProfileConfiguration.getUsageReportingType();
	}
	
	@Override
	public int getMinimumLocalPort() {
		return minimumLocalPort;
	}

	@Override
	public int getRetryCount() {
		return radiusGatewayProfileConfiguration.getRetryCount();
	}

	@Override
	public int getStatusCheckDuration() {
		return radiusGatewayProfileConfiguration.getStatusCheckDuration();
	}

	@Override
	public boolean isICMPPingEnabled() {
		return radiusGatewayProfileConfiguration.isICMPPingEnabled();
	}
	
	@Override
	public PCRFKeyValueConstants getRevalidationMode() {
		return radiusGatewayProfileConfiguration.getRevalidationMode();
	}
	
	@Override
	public List<ScriptData> getScriptsConfigs() {
		return radiusGatewayProfileConfiguration.getScriptsConfigs();
	}
	
	@Override
	public boolean isPCCLevelMonitoringSupported() {
		return radiusGatewayProfileConfiguration.isPCCLevelMonitoringSupported();
	}


	@Override
	public Predicate<PCRFRequest, SessionData> getInterimIntervalPredicate() {
		return radiusGatewayProfileConfiguration.getInterimIntervalPredicate();
	}
	
	@Override
	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	@Override
	public RadiusToPCCMapping getARMappings() {
		return radiusGatewayProfileConfiguration.getARMappings();
	}

	@Override
	public PCCToRadiusMapping getAAMappings() {
		return radiusGatewayProfileConfiguration.getAAMappings();
	}

	@Override
	public PCCToRadiusMapping getCOAMappings() {
		return radiusGatewayProfileConfiguration.getCOAMappings();
	}

	@Override
	public RadiusToPCCMapping getACRMappings() {
		return radiusGatewayProfileConfiguration.getACRMappings();
	}

	@Override
	public PCCToRadiusMapping getDCRMappings() {
		return radiusGatewayProfileConfiguration.getDCRMappings();
	}

	@Override
	public Map<RadApplicationPacketType, PCCToRadiusMapping> getPCCToRADIUSPacketMappings() {
		return radiusGatewayProfileConfiguration.getPCCToRADIUSPacketMappings();
	}

	@Override
	public Map<RadApplicationPacketType, RadiusToPCCMapping> getRadiusToPCCPacketMappings() {
		return radiusGatewayProfileConfiguration.getRadiusToPCCPacketMappings();
	}

	@Override
	public List<ServiceGuide> getServiceGuides(){
		return radiusGatewayProfileConfiguration.getServiceGuides();
	}

	@Override
	public GatewayComponent getGatewayType(){
		return radiusGatewayProfileConfiguration.getGatewayType();
	}

	@Override
	public void toString(IndentingToStringBuilder builder){
		builder.newline()
				.incrementIndentation()
				.append("Id", gatewayId)
				.append("Gateway Name", name)
				.append("Description", description)
				.append("Policy Enforcement Method", enforcementMethod)
				.append("Connection URL", connectionURL)
				.append("Shared Secret", sharedSecret)
				.append("Minimum Local Port", minimumLocalPort)
				.appendChildObject("Profile", radiusGatewayProfileConfiguration);
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Radius Gateway Configuration -- ");
		toString(builder);
		return builder.toString();
	}

}
