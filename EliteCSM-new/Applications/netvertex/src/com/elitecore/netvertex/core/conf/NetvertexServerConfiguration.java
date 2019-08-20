package com.elitecore.netvertex.core.conf;

import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceConfigurationImpl;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfigurable;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterStackConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.offlinernc.guiding.conf.GuidingConfiguration;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface NetvertexServerConfiguration {


	/**
	 * This method return the radius gateway configuration on the basis of gateway id
	 *
	 * @param gatewayId
	 * @return <code>RadiusGatewayConfiguration</code>
	 */
	public RadiusGatewayConfiguration getRadiusGatewayConfiguration(int gatewayId);

	/**
	 * This method return the radius gateway configuration on the basis of gateway Address
	 *
	 * @param gatewayAddress
	 * @return <code>RadiusGatewayConfiguration</code>
	 */
	public RadiusGatewayConfiguration getRadiusGatewayConfiguration(String gatewayAddress);

	/**
	 * This method return the radius gateway configuration on the basis of gateway name
	 *
	 * @param name
	 * @return <code>RadiusGatewayConfiguration</code>
	 */
	public RadiusGatewayConfiguration getRadiusGatewayConfigurationByName(String name);

	public Collection<RadiusGatewayConfiguration> getRadiusGatewayConfigurations();

	/**
	 * This method return the diameter gateway configuration on the basis of gateway id
	 *
	 * @param gatewayId
	 * @return <code>DiameterGatewayConfiguration</code>
	 */
	public DiameterGatewayConfiguration getDiameterGatewayConfiguration(int gatewayId);

	/**
	 * This method return the diameter gateway configuration on the basis of gateway host-identity
	 *
	 * @param hostIdentity
	 * @return <code>DiameterGatewayConfiguration</code>
	 */
	public DiameterGatewayConfiguration getDiameterGatewayConfByHostIdentity(String hostIdentity);

	/**
	 * This method return the diameter gateway configuration on the basis of gateway name
	 *
	 * @param gatewayName
	 * @return <code>DiameterGatewayConfiguration</code>
	 */
	public DiameterGatewayConfiguration getDiameterGatewayConfByName(String gatewayName);

	/**
	 * This method return the diameter gateway configuration on the basis of gateway connection URL
	 *
	 * @param connUrl
	 * @return <code>DiameterGatewayConfiguration</code>
	 */
	public DiameterGatewayConfiguration getDiameterGatewayConfByConnUrl(String connUrl);

	/**
	 * This method return the pcrf service policy configuration on the basis of pcrf service policy id
	 *
	 * @param pcrfServicePolicyId
	 * @return <code>PCRFServicePolicyConfiguration</code>
	 */
	public PccServicePolicyConfiguration getPCRFServicePolicyConfiguration(int pcrfServicePolicyId);

	/**
	 * This method return the pcrf service policy configuration on the basis of pcrf service policy name
	 *
	 * @param pcrfServicePolicyName
	 * @return <code>PCRFServicePolicyConfiguration</code>
	 */
	public PccServicePolicyConfiguration getPCRFServicePolicyConfByName(String pcrfServicePolicyName);

	/**
	 * This method return the names of pcrf service policy configuration
	 *
	 * @return <code>PCRFServicePolicyConfiguration</code>
	 */
	public Set<String> getPCRFServicePolicyNames();

	/**
	 * This method returns the LDAP data source configuration
	 *
	 * @return LDAPDSConfiguration
	 */
	public LDAPDSConfiguration getLDAPDSConfiguration();

	/**
	 * This method returns the DB data source configuration
	 *
	 * @return DatabaseDSConfiguration
	 */

	public DatabaseDSConfiguration getDatabaseDSConfiguration();


	/**
	 * This method returns PCRF service configuration
	 *
	 * @return <code>PCRFServiceConfiguration</code>
	 */
	public PCRFServiceConfiguration getPCRFServiceConfiguration();
	
	/**
	 * @return<code>DiameterGatewayConfiguration</code>
	 */
	Collection<DiameterGatewayConfiguration> getDiameterGatewayConfigurations();


	/**
	 * returns the configuration for Alert Listeners .
	 *
	 * @return <code>AlertListnersConfiguration</code>
	 */
	public AlertListenerConfiguration getAlertListenersConfiguration();

	public SessionManagerConfiguration getSessionManagerConfiguration();

	public NotificationServiceConfigurationImpl getNotificationServiceConfiguration();

	public Set<String> getRadiusGatewayNames();

	public Set<String> getDiameterGatewayNames();

	public DiameterGatewayConfigurable getDiameterGatewayConfigurable();

	MiscellaneousConfiguration getMiscellaneousParameterConfiguration();

	MCCMNCRoutingConfiguration getMCCMNCRoutingConfiguration();

	public DDFConfiguration getDDFTableData();


	@Nullable
	NetvertexServerGroupConfiguration getNetvertexServerGroupConfiguration();

	RadiusListenerConfiguration getRadiusGatewayEventListenerConfiguration();

	NetvertexServerInstanceConfigurationImpl getNetvertexServerInstanceConfiguration();

	DiameterStackConfiguration getDiameterStackConfiguration();

	List<PDInstanceConfiguration> getPDInstanceConfiguration();

	List<GuidingConfiguration> getOfflineRnCServiceGuidingConfiguration();
	
	List<PrefixConfiguration> getOfflineRnCServicePrefixConfiguration();

	PCCToRadiusMapping getPCCToRadiusMappingsByPacketType(String gatewayName, String packetType);

	RadiusToPCCMapping getRadiusToPCCMappingsByPacketType(String gatewayName, String packetType);

	PCCToDiameterMapping getPCCToDiameterMappingsByPacketType(String gatewayName, String packetType);

	DiameterToPCCMapping getDiameterToPCCMappingsByPacketType(String gatewayName, String packetType);

	SystemParameterConfiguration getSystemParameterConfiguration();

	public DriverConfiguration getDriverConfigurationByName(String name);

	public DriverConfiguration getDriverConfigurationById(String id);

}

