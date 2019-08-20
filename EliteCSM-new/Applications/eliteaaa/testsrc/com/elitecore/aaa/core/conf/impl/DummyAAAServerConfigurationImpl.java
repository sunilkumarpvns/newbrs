package com.elitecore.aaa.core.conf.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.alert.conf.impl.BaseAlertConfigurable;
import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.aaa.core.conf.DatabaseDSConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.EAPConfigurations;
import com.elitecore.aaa.core.conf.LDAPDSConfiguration;
import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.conf.ServerCertificateConfiguration;
import com.elitecore.aaa.core.conf.VSAInClassConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.config.AlertListnersDetail;
import com.elitecore.aaa.core.config.RadiusSessionCleanupDetail;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable;
import com.elitecore.aaa.core.scripts.conf.ScriptConfigurable;
import com.elitecore.aaa.core.server.axixserver.WebServiceConfiguration;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterPeerConfiguration;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.conf.ImsiBasedRoutingTableConfiguration;
import com.elitecore.aaa.diameter.conf.MsisdnBasedRoutingTableConfiguration;
import com.elitecore.aaa.diameter.conf.PriorityConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.RadEsiGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.RoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable;
import com.elitecore.aaa.diameter.service.application.drivers.conf.TranslationMappingConfiguration;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.CopyPacketTranslationConfigurable;
import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.conf.RadDynAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.conf.impl.RadiusESIGroupConfigurable;
import com.elitecore.aaa.radius.esi.radius.conf.impl.CorrelatedRadiusConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerConfiguration;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;

public class DummyAAAServerConfigurationImpl implements AAAServerConfiguration {

	private Map<String, int[]> gracePolicyConfigurationMap = new HashMap<String, int[]>();
	private WimaxConfigurable wimaxConfiguration;
	private DiameterPeerGroupConfigurable diameterPeerGroupConfigurable;
	private DiameterPeerConfiguration diameterPeerConfiguration;
	private RadESConfiguration radESConfiguration;
	private CorrelatedRadiusConfigurable correlatedRadiusConfigurable;
	private RadiusESIGroupConfigurable radiusEsiGroupConfigurable;
	
	@Override
	public String getServerName() {

		return null;
	}

	@Override
	public String getDomainName() {

		return null;
	}

	@Override
	public String getConnectionUrlForAAADB() {

		return null;
	}

	@Override
	public String getUsernameForAAADB() {

		return null;
	}

	@Override
	public String getPasswordForAAADB() {

		return null;
	}

	@Override
	public String getPlainTextPasswordForAAADB() {

		return null;
	}

	@Override
	public String getLogLevel() {

		return null;
	}

	@Override
	public int getLogRollingType() {

		return 0;
	}

	@Override
	public int getLogRollingUnit() {

		return 0;
	}

	@Override
	public int getLogMaxRolledUnits() {

		return 0;
	}

	@Override
	public boolean isCompressLogRolledUnits() {

		return false;
	}

	@Override
	public LDAPDSConfiguration getLDAPDSConfiguration() {

		return null;
	}

	@Override
	public DatabaseDSConfiguration getDatabaseDSConfiguration() {

		return null;
	}

	@Override
	public RadClientConfiguration getRadClientConfiguration() {

		return null;
	}

	@Override
	public WimaxConfiguration getWimaxConfiguration() {
		return wimaxConfiguration;
	}

	@Override
	public DHCPKeysConfiguration getDhcpKeysConfiguration() {

		return null;
	}

	@Override
	public SPIKeyConfiguration getSpiKeysConfiguration() {

		return null;
	}

	@Override
	public SessionManagerConfiguration getSessionManagerConfiguration() {

		return null;
	}

	@Override
	public ConcurrentLoginPolicyConfiguration getConcurrentLoginPolicyConfiguration() {

		return null;
	}

	@Override
	public WebServiceConfiguration getWebServiceConfiguration() {

		return null;
	}

	@Override
	public SysLogConfiguration getSysLogConfiguration() {

		return null;
	}

	@Override
	public KpiServiceConfiguration getKpiServiceConfiguration() {

		return null;
	}

	@Override
	public boolean isNAIEnabled() {

		return false;
	}

	@Override
	public List<String> getRealmNames() {

		return Collections.emptyList();
	}

	@Override
	public String getSNMPAddress() {

		return null;
	}

	@Override
	public int getSNMPPort() {

		return 0;
	}

	@Override
	public String getSNMPCommunity() {

		return null;
	}

	@Override
	public String getKey() {

		return null;
	}

	@Override
	public boolean isServiceEnabled(String serviceId) {

		return false;
	}

	@Override
	public VSAInClassConfiguration getVSAInClassConfiguration() {

		return null;
	}

	@Override
	public RadAuthConfiguration getAuthConfiguration() {

		return null;
	}

	@Override
	public DiameterServiceConfigurationDetail getDiameterServiceConfiguration(String serviceId) {

		return null;
	}

	@Override
	public RadDynAuthConfiguration getDynAuthConfiguration() {

		return null;
	}

	@Override
	public RadAcctConfiguration getAcctConfiguration() {

		return null;
	}

	@Override
	public DiameterStackConfigurable getDiameterStackConfiguration() {

		return null;
	}

	@Override
	public RadESConfiguration getRadESConfiguration() {
		return radESConfiguration;
	}

	@Override
	public RadEsiGroupConfigurable getRadEsiGroupConfigurable() {

		return null;
	}

	@Override
	public BaseAlertConfigurable getAAAAlertManagerConfiguration() {

		return null;
	}

	@Override
	public AAAPluginConfManager getPluginManagerConfiguration() {

		return null;
	}

	@Override
	public TranslationMappingConfiguration getTranslationMappingConfiguration() {

		return null;
	}

	@Override
	public CopyPacketTranslationConfigurable getCopyPacketTranslationConfiguration() {

		return null;
	}

	@Override
	public EAPConfigurations getEAPConfigurations() {

		return null;
	}

	@Override
	public AlertListnersDetail getAlertListenersDetail() {

		return null;
	}

	@Override
	public CRLConfiguration getCRLConfiguration() {

		return null;
	}

	@Override
	public TrustedCAConfiguration getTrustedCAConfiguration() {

		return null;
	}

	@Override
	public ServerCertificateConfiguration getServerCertificateConfiguration() {

		return null;
	}

	@Override
	public DiameterPeerConfiguration getDiameterPeerConfiguration() {
		return this.diameterPeerConfiguration;
	}

	public void setDiameterPeerConfiguration(DiameterPeerConfiguration diameterPeerConfiguration) {
		this.diameterPeerConfiguration = diameterPeerConfiguration;
	}

	@Override
	public RoutingTableConfigurable getDiameterRoutingConfiguration() {

		return null;
	}

	@Override
	public DriverConfigurationProvider getDiameterDriverConfiguration() {

		return null;
	}

	@Override
	public PriorityConfigurable getPriorityConfigurable() {

		return null;
	}

	@Override
	public DiameterSessionManagerConfigurable getDiameterSessionManagerConfiguration() {

		return null;
	}

	@Override
	public RadiusServicePolicyConfigurable getRadiusServicePolicyConfiguration() {

		return null;
	}

	@Override
	public DriverConfigurationProvider getDriverConfigurationProvider() {

		return null;
	}

	@Override
	public MiscellaneousConfigurable getMiscellaneousConfigurable() {

		return null;
	}

	@Override
	public ImsiBasedRoutingTableConfiguration getImsiBasedRoutingTableConfiguration() {

		return null;
	}

	@Override
	public int[] getGracePolicyConfiguration(String name) {
		return this.gracePolicyConfigurationMap .get(name);
	}

	public void setGracePolicy(String name, int[] values) {
		this.gracePolicyConfigurationMap.put(name, values);
	}

	@Override
	public DiameterConcurrencyConfigurable getDiameterConcurrencyConfigurable() {

		return null;
	}

	@Override
	public MsisdnBasedRoutingTableConfiguration getMsisdnBasedRoutingTableConfiguration() {

		return null;
	}

	@Override
	public DiameterPeerGroupConfigurable getDiameterPeerGroupConfigurable() {
		return this.diameterPeerGroupConfigurable;
	}

	public void setDiameterPeerGroupConfigurable(DiameterPeerGroupConfigurable diameterPeerGroupConfigurable) {
		this.diameterPeerGroupConfigurable = diameterPeerGroupConfigurable;
	}

	@Override
	public PluginConfigurable getPluginConfiguration() {

		return null;
	}


	public void setWimaxConfiguration(WimaxConfigurable wimaxConfiguration) {
		this.wimaxConfiguration = wimaxConfiguration; 
	}

	@Override
	public IMDGConfigurable getImdgConfigurable() {
		return null;
	}

	@Override
	public ScriptConfigurable getScriptConfigurable() {
		return null;
	}

	@Override
	public RadiusSessionCleanupDetail getRadiusSessionCleanupDetail() {
		return null;
	}

	@Override
	public RadiusESIGroupConfigurable getRadiusESIGroupConfigurable() {
		return radiusEsiGroupConfigurable;
	}

	public void setRadESConfiguration(RadESConfiguration radESConfiguration) {
		this.radESConfiguration = radESConfiguration;
	}

	public void setCorrelatedRadiusConfiguration(CorrelatedRadiusConfigurable correlatedRadiusConfiguration) {
		this.correlatedRadiusConfigurable = correlatedRadiusConfiguration;
	}

	@Override
	public CorrelatedRadiusConfigurable getCorrelatedRadiusConfigurable() {
		return this.correlatedRadiusConfigurable;
	}
	
	public void setRadiusEsiGroupConfigurable(RadiusESIGroupConfigurable radiusEsiGroupConfigurable) {
		this.radiusEsiGroupConfigurable = radiusEsiGroupConfigurable;
	}
	
}