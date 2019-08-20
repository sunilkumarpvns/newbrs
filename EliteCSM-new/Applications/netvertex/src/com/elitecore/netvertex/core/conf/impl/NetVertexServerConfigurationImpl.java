package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfigurableImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.DatabaseDSConfiguration;
import com.elitecore.netvertex.core.conf.LDAPDSConfiguration;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfigurable;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.driver.cdr.conf.CDRDriverConfigurable;
import com.elitecore.netvertex.core.driver.spr.DDFConfigurable;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.driver.spr.NVDataSourceProvider;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfigurable;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfigurable;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.escommunication.PDInstanceConfigurable;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfigurable;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterStackConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.impl.RadiusGatewayConfigurable;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.offlinernc.guiding.conf.GuidingConfigurable;
import com.elitecore.netvertex.service.offlinernc.guiding.conf.GuidingConfiguration;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfigurable;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.conf.PccServicePolicyConfigurable;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import org.hibernate.SessionFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class NetVertexServerConfigurationImpl extends
		BaseConfigurationImpl implements NetvertexServerConfiguration {

	private static final String MODULE = "NVSVR-CNF";
	private static final String KEY = "NETVERTEX_SERVER";

	private MiscellaneousConfigurationImpl miscellaneousConfiguration;
	private LDAPDSConfigurationImpl ldapdsConfiguration;
	private DatabaseDSConfigurationImpl databaseDSConfiguration;
	private final NetVertexServerContext serverContext;
	private final AlertListenerConfigurableImpl alertListenersConf;
	private final SessionManagerConfigurable sessionManagerConfigurable;
	private final MCCMNCRoutingConfigurable mccmncRoutingConfigurable;
	private DDFConfigurable ddfConfigurable;
	private NetvertexServerGroupConfigurable netvertexServerGroupConfigurable;
	private DiameterGatewayConfigurable diameterGatewayConfigurable;
	private RadiusGatewayConfigurable radiusGatewayConfigurable;
	private PDInstanceConfigurable pdInstanceConfigurable;
	private PccServicePolicyConfigurable pcrfServicePolicyConfigurable;
	private DataSourceProvider dataSourceProvider;
	private GuidingConfigurable guidingConfigurable;
	private PrefixConfigurable prefixConfigurable;
	private SystemParameterConfigurable systemParameterConfigurable;
	private CDRDriverConfigurable cdrDriverConfigurable;

	public NetVertexServerConfigurationImpl(NetVertexServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.serverContext = serverContext;
		this.netvertexServerGroupConfigurable = new NetvertexServerGroupConfigurable(serverContext, sessionFactory);
        miscellaneousConfiguration = new MiscellaneousConfigurationImpl(serverContext);
        ldapdsConfiguration = new LDAPDSConfigurationImpl(serverContext, sessionFactory);
        databaseDSConfiguration = new DatabaseDSConfigurationImpl(serverContext, sessionFactory);
        this.dataSourceProvider = new NVDataSourceProvider(databaseDSConfiguration, ldapdsConfiguration);
        ddfConfigurable = new DDFConfigurable(serverContext, sessionFactory, dataSourceProvider);
        sessionManagerConfigurable = new SessionManagerConfigurable(serverContext, sessionFactory);
		alertListenersConf = new AlertListenerConfigurableImpl(this.serverContext, sessionFactory);
		mccmncRoutingConfigurable = new MCCMNCRoutingConfigurable(serverContext, sessionFactory);
		diameterGatewayConfigurable = new DiameterGatewayConfigurable(serverContext, sessionFactory);
		radiusGatewayConfigurable = new RadiusGatewayConfigurable(serverContext, sessionFactory);
		pdInstanceConfigurable = new PDInstanceConfigurable(serverContext, sessionFactory);
		pcrfServicePolicyConfigurable = new PccServicePolicyConfigurable(serverContext, sessionFactory);
		guidingConfigurable = new GuidingConfigurable(serverContext, sessionFactory);
		prefixConfigurable = new PrefixConfigurable(serverContext, sessionFactory);
		systemParameterConfigurable = new SystemParameterConfigurable(serverContext, sessionFactory);
		cdrDriverConfigurable = new CDRDriverConfigurable(serverContext, sessionFactory);
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {

		try {
			readNetVertexGroupConfiguration();
			readMiscellaneousConfiguration();
            readSystemParamConfiguration();
            readDatabaseDSConfiguration();
            readLDAPDSConfiguration();
            readDDFConfiguration();
            readSessionManagerConfiguration();
            readDiameterGatewayConfiguration();
            readRadiusGatewayConfiguration();
            readPCRFServicePolicyConfiguration();
            readMCCMNCConfiguration();
            readAlertListenersConfiguration();
            readPDConfiguration();
            readGuidingConfiguration();
            readPrefixConfiguration();
            readDriverConfiguration();
		} catch (Exception e) {
			throw new LoadConfigurationException("Failed to read server configuration. Reason: " + e.getMessage(), e);
		}
	}

	private void readDriverConfiguration() throws LoadConfigurationException {
		cdrDriverConfigurable.readConfiguration();
	}

	private void readSystemParamConfiguration() throws LoadConfigurationException {
		systemParameterConfigurable.readConfiguration();
	}

	private void readNetVertexGroupConfiguration() {
		try {
			netvertexServerGroupConfigurable.readConfiguration();
		} catch (Exception ex) {
			getLogger().error(MODULE, "Error while reading group configuration. Reason: " + ex.getMessage());
			getLogger().trace(MODULE, ex);
		}
	}

	private void readDDFConfiguration() throws LoadConfigurationException {
		ddfConfigurable.readConfiguration();
	}

	private void readPCRFServicePolicyConfiguration() throws LoadConfigurationException {
		pcrfServicePolicyConfigurable.readConfiguration();
	}

	private void readDiameterGatewayConfiguration() throws LoadConfigurationException {
		diameterGatewayConfigurable.readConfiguration();
	}

	private void reloadDiameterGatewayConfiguration() throws LoadConfigurationException, InitializationFailedException {
		diameterGatewayConfigurable.reloadConfiguration();
	}

	private void readRadiusGatewayConfiguration() throws LoadConfigurationException {
		radiusGatewayConfigurable.readConfiguration();
	}
	
	private void readGuidingConfiguration() throws LoadConfigurationException {
		guidingConfigurable.readConfiguration();
	}
	
	private void readPrefixConfiguration() throws LoadConfigurationException {
		prefixConfigurable.readConfiguration();
		
	}

	private void reloadRadiusGatewayConfiguration() throws LoadConfigurationException {
		radiusGatewayConfigurable.reloadConfiguration();
	}

	private void readMiscellaneousConfiguration() {
		miscellaneousConfiguration.readConfiguration();
	}

	private void readMCCMNCConfiguration() throws LoadConfigurationException{
		mccmncRoutingConfigurable.readConfiguration();
	}


	private void readSessionManagerConfiguration() throws LoadConfigurationException {
		sessionManagerConfigurable.readConfiguration();
	}

	private void reloadSessionManagerConfiguration() {
		sessionManagerConfigurable.reloadConfiguration();
	}

	private void readAlertListenersConfiguration() throws LoadConfigurationException {
		alertListenersConf.readConfiguration();
	}


	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	/**
	 * This method reads the database datasource configuration
	 *
	 * @throws LoadConfigurationException
	 */
	private void readDatabaseDSConfiguration() throws LoadConfigurationException {
		databaseDSConfiguration.reloadConfiguration();
	}

	/**
	 * This method reads the LDAP datasource configuration
	 *
	 * @throws LoadConfigurationException
	 */
	private void readLDAPDSConfiguration() throws LoadConfigurationException {
        ldapdsConfiguration.reloadConfiguration();
	}
	/**
	 * This method reads PD instance configuration
	 *
	 * @throws LoadConfigurationException
	 */

	private void readPDConfiguration() throws LoadConfigurationException {
		pdInstanceConfigurable.readConfiguration();
	}
	
	@Override
	public EliteNetConfigurationData getNetConfigurationData() {
		return null;
	}

	public String getModule() {
		return MODULE;
	}

	public void reloadConfiguration() throws LoadConfigurationException {
		try{
			ConfigLogger.getInstance().info(MODULE, "Reloading NetVertex server configuration Started");
			LogManager.getLogger().info(MODULE, "Reloading NetVertex server configuration started");
			reloadDatabaseDSConfiguration();
			reloadLDAPDSConfiguration();
			reloadSessionManagerConfiguration();
			reloadDDFConfiguration();
			reloadDiameterGatewayConfiguration();
			reloadRadiusGatewayConfiguration();
			reloadMiscellaneousConfiguration();
			LogManager.getLogger().debug(MODULE, "Reloading NetVertex server configuration completed");
			ConfigLogger.getInstance().info(MODULE, "Reloading NetVertex server configuration successfully Completed");
		}catch(LoadConfigurationException ex){
			throw ex;
		}catch(Exception ex){
			throw new LoadConfigurationException(ex);
		}
	}

	private void reloadDDFConfiguration() {
		ddfConfigurable.reloadConfiguration();
	}

	private void reloadMiscellaneousConfiguration() {
		miscellaneousConfiguration.reloadConfiguration();
	}

	private void reloadDatabaseDSConfiguration() throws LoadConfigurationException{
		databaseDSConfiguration.reloadConfiguration();
	}

	private void reloadLDAPDSConfiguration() throws LoadConfigurationException{
		ldapdsConfiguration.reloadConfiguration();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public LDAPDSConfiguration getLDAPDSConfiguration() {
		return ldapdsConfiguration;
	}

	@Override
	public DatabaseDSConfiguration getDatabaseDSConfiguration() {
		return databaseDSConfiguration;
	}

	@Override
	public DiameterGatewayConfiguration getDiameterGatewayConfiguration(int gatewayId) {
		return diameterGatewayConfigurable.getById(gatewayId);
	}

	@Override
	public DiameterGatewayConfiguration getDiameterGatewayConfByHostIdentity(String hostIdentity) {
		return diameterGatewayConfigurable.getByHostIdentity(hostIdentity);
	}

	@Override
	public DiameterGatewayConfiguration getDiameterGatewayConfByConnUrl(String connUrl) {
		return diameterGatewayConfigurable.getByConnectionUrl(connUrl);
	}


	@Override
	public DiameterGatewayConfiguration getDiameterGatewayConfByName(String gatewayName) {
		return diameterGatewayConfigurable.getByName(gatewayName);
	}

	@Override
	public Set<String> getDiameterGatewayNames() {
		return diameterGatewayConfigurable.getGatewayNames();
	}

	@Override
	public DiameterGatewayConfigurable getDiameterGatewayConfigurable() { return diameterGatewayConfigurable; }

	@Override
	public RadiusGatewayConfiguration getRadiusGatewayConfiguration(int gatewayId) {
		return radiusGatewayConfigurable.getRadiusGatewayConfigurationById(gatewayId);
	}

	@Override
	public RadiusGatewayConfiguration getRadiusGatewayConfiguration(String gatewayIpAddress) {
		return radiusGatewayConfigurable.getRadiusGatewayConfigurationByIpAddress(gatewayIpAddress);
	}

	public RadiusGatewayConfiguration getRadiusGatewayConfigurationByName(String gatewayName) {
		return radiusGatewayConfigurable.getRadiusGatewayConfigurationByName(gatewayName);
	}

	@Override
	public Set<String> getRadiusGatewayNames() {
		return radiusGatewayConfigurable.getRadiusGatewayNames();
	}

	@Override
	public Collection<RadiusGatewayConfiguration> getRadiusGatewayConfigurations() {
		return radiusGatewayConfigurable.getRadiusGatewayConfigurations();
	}

	@Override
	public PCRFServiceConfiguration getPCRFServiceConfiguration() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration().getRunningServerInstance().getPcrfServiceConfiguration();
	}



	@Override
	public Collection<DiameterGatewayConfiguration> getDiameterGatewayConfigurations() {
		return diameterGatewayConfigurable.getAllGatewayConfiguration();
	}

	@Override
	public AlertListenerConfiguration getAlertListenersConfiguration() {
		return alertListenersConf.getAlertListenerConfigurations();
	}


	@Override
	public SessionManagerConfiguration getSessionManagerConfiguration() {
		return sessionManagerConfigurable.getConfiguration();
	}


	@Override
	public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration().getRunningServerInstance().getNotificationServiceConfiguration();
	}


	@Override
	public MCCMNCRoutingConfiguration getMCCMNCRoutingConfiguration() {
		return mccmncRoutingConfigurable.getMCCMNCRoutingConfiguration();
	}


	@Override
	public MiscellaneousConfiguration getMiscellaneousParameterConfiguration() {
		return miscellaneousConfiguration;
	}

	public List<ScriptData> getScriptsConfigs() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration().getRunningServerInstance().getNetvertexServerInstanceConfiguration().getScriptDatas();
	}

	@Override
	public DDFConfiguration getDDFTableData() {
		return ddfConfigurable.getDdfTableData();
	}

	@Override
	@Nullable
	public NetvertexServerGroupConfiguration getNetvertexServerGroupConfiguration() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration();
	}

	@Override
	public RadiusListenerConfiguration getRadiusGatewayEventListenerConfiguration() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration().getRunningServerInstance().getRadiusListnerConf();
	}

	@Override
	public List<PDInstanceConfiguration> getPDInstanceConfiguration(){
		return pdInstanceConfigurable.getPDInstanceConfigurations();
	}


	@Override
	public NetvertexServerInstanceConfigurationImpl getNetvertexServerInstanceConfiguration() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration().getRunningServerInstance().getNetvertexServerInstanceConfiguration();
	}

	@Override
	public DiameterStackConfiguration getDiameterStackConfiguration() {
		return netvertexServerGroupConfigurable.getNetvertexServerGroupConfiguration().getRunningServerInstance().getDiameterListenerConf();
	}

	@Override
	public PccServicePolicyConfiguration getPCRFServicePolicyConfiguration(int pcrfServicePolicyId) {
		return pcrfServicePolicyConfigurable.getById(pcrfServicePolicyId);
	}

	@Override
	public PccServicePolicyConfiguration getPCRFServicePolicyConfByName(String pcrfServicePolicyName) {
		return pcrfServicePolicyConfigurable.getByName(pcrfServicePolicyName);
	}

	@Override
	public Set<String> getPCRFServicePolicyNames() {
		return pcrfServicePolicyConfigurable.getPCRFServicePolicyNames();
	}

	@Override
	public List<GuidingConfiguration> getOfflineRnCServiceGuidingConfiguration() {
		return guidingConfigurable.getGuidingConfigurations();
	}
	
	@Override
	public List<PrefixConfiguration> getOfflineRnCServicePrefixConfiguration() {
		return prefixConfigurable.getPrefixConfigurations();
	}

	@Override
	public PCCToDiameterMapping getPCCToDiameterMappingsByPacketType(String gatewayName, String packetType) {
		return diameterGatewayConfigurable.getPCCToDiameterMappings(gatewayName, packetType);
	}

	@Override
	public DiameterToPCCMapping getDiameterToPCCMappingsByPacketType(String gatewayName, String packetType) {
		return diameterGatewayConfigurable.getDiameterToPCCMappings(gatewayName, packetType);
	}

	@Override
	public PCCToRadiusMapping getPCCToRadiusMappingsByPacketType(String gatewayName, String packetType) {
		return radiusGatewayConfigurable.getPCCToRadiusMappings(gatewayName, packetType);
	}

	@Override
	public RadiusToPCCMapping getRadiusToPCCMappingsByPacketType(String gatewayName, String packetType) {
		return radiusGatewayConfigurable.getRadiusToPCCMappings(gatewayName, packetType);
	}

	@Override
	public SystemParameterConfiguration getSystemParameterConfiguration() {
		return systemParameterConfigurable.getSystemParameterConfiguration();
	}

	@Override
	public DriverConfiguration getDriverConfigurationByName(String name) {
		return cdrDriverConfigurable.getDriverConfigurationByName(name);
	}

	@Override
	public DriverConfiguration getDriverConfigurationById(String id) {
		return cdrDriverConfigurable.getDriverConfigurationById(id);
	}


}
