/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.aaa.core.conf.impl;




import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.alert.conf.impl.BaseAlertConfigurable;
import com.elitecore.aaa.alert.conf.impl.RMAlertConfigurable;
import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.aaa.core.conf.DatabaseDSConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.EAPConfigurations;
import com.elitecore.aaa.core.conf.LDAPDSConfiguration;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
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
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.TranslationMappingConfigurationImpl;
import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.conf.RadDynAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.conf.impl.RadClientConfigurationImpl;
import com.elitecore.aaa.radius.conf.impl.RadESIConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadiusESIGroupConfigurable;
import com.elitecore.aaa.radius.esi.radius.conf.impl.CorrelatedRadiusConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.SessionManagerConfigurationImpl;
import com.elitecore.aaa.rm.conf.GTPPrimeConfiguration;
import com.elitecore.aaa.rm.conf.RDRConfiguration;
import com.elitecore.aaa.rm.conf.RMChargingServiceConfiguration;
import com.elitecore.aaa.rm.conf.RMConcurrentLoginServiceConfiguration;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.aaa.rm.conf.RMPrepaidChargingServiceConfiguration;
import com.elitecore.aaa.rm.conf.RdrDetailLocalConfiguration;
import com.elitecore.aaa.rm.conf.impl.GTPPrimeConfigurationImpl;
import com.elitecore.aaa.rm.conf.impl.RDRConfigurationImpl;
import com.elitecore.aaa.rm.conf.impl.RMChargingServiceConfigurationImpl;
import com.elitecore.aaa.rm.conf.impl.RMConcurrentLoginServiceConfigurationImpl;
import com.elitecore.aaa.rm.conf.impl.RMDiameterCompositeConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMIPPoolConfigurationImpl;
import com.elitecore.aaa.rm.conf.impl.RMPrepaidChargingServiceConfigImpl;
import com.elitecore.aaa.rm.service.rdr.drivers.confg.impl.RdrDetailLocalConfigurationImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.cache.CacheContainer;

@ReadOrder(order = {"rmServerConfigurable", "miscellaneousConfigurable",
		 "dsConfiguration", "ldapDsConfiguration",
		 "trustedCAConfiguration", "serverCertificateConfigurable"	,"crlConfigurable",
		 "radESIConfiguration","radEsiGroupConfigurable","radClientConfigurationImpl",
		 "aaaalertConfigurable","gtpConfiguration",
		 "rdrConfiguration","rdrDetailLocalConfiguration",
		 "translationMappingConfiguration","ippoolServiceConfiguration",
		 "copyPacketTranslationConfigurable",
		 "diameterCompositeConfigurable", "rmDriversConfigurable",
		 "sessionManagerConfiguration",
		 "rmConcurrentLoginServiceConfiguration","rmPrepaidChargingServiceConfiguration",
		 "rmChargingServiceConfiguration","pluginManagerConfiguration", "pluginConfigurable","scriptConfigurable"})

public final class RMServerConfigurationImpl extends CompositeConfigurable implements RMServerConfiguration{

	private final String MODULE = "RM-CNF";
	private int adminPort = 4434;
	private boolean compRollingUnit ;
	private int schedulerMaxThread = 15;	
	@Configuration private RMServerConfigurable rmServerConfigurable;
	@Configuration (required = false) private MiscellaneousConfigurable miscellaneousConfigurable;
	@Configuration private RMAlertConfigurable aaaalertConfigurable;
	@Configuration private RadClientConfigurationImpl radClientConfigurationImpl;
	@Configuration private DatabaseDSConfigurationImpl dsConfiguration;
	@Configuration private CopyPacketTranslationConfigurable copyPacketTranslationConfigurable;
	@Configuration private TranslationMappingConfigurationImpl translationMappingConfiguration;
	@Configuration private LDAPDSConfigurationImpl ldapDsConfiguration;
	@Configuration private SessionManagerConfigurationImpl sessionManagerConfiguration;
	@Configuration private RadESIConfigurable radESIConfiguration;
	@Configuration private RadEsiGroupConfigurable radEsiGroupConfigurable;
	@Configuration private AAAPluginConfManagerImpl pluginManagerConfiguration;
	@Configuration (conditional = true) private RDRConfigurationImpl rdrConfiguration;
	@Configuration private RdrDetailLocalConfigurationImpl rdrDetailLocalConfiguration;
	@Configuration(required = false)private ServerCertificateConfigurable serverCertificateConfigurable;
	@Configuration(required = false) private TrustedCAConfigurable trustedCAConfiguration;
	@Configuration(required = false)private CRLConfigurable crlConfigurable;
	@Configuration(conditional = true) private RMIPPoolConfigurationImpl ippoolServiceConfiguration;
	@Configuration(conditional = true) private RMPrepaidChargingServiceConfigImpl rmPrepaidChargingServiceConfiguration;
	@Configuration(conditional = true) private RMChargingServiceConfigurationImpl rmChargingServiceConfiguration;
	@Configuration(conditional = true) private GTPPrimeConfigurationImpl gtpConfiguration;
	@Configuration(conditional = true) private RMConcurrentLoginServiceConfigurationImpl rmConcurrentLoginServiceConfiguration;
	
	//Added For Diameter Stack
	@Configuration private RMDiameterCompositeConfigurable diameterCompositeConfigurable;
	
	@Configuration private RMDriversConfigurable rmDriversConfigurable;
	
	@Nullable private ConcurrentLoginPolicyConfiguration loginPolicyConfiguration;
	
	@Configuration private PluginConfigurable pluginConfigurable;
	@Configuration private ScriptConfigurable scriptConfigurable;

	@Deprecated
	public RMServerConfigurationImpl(ServerContext serverContext,CacheContainer cacheContainer) {		
		rmServerConfigurable = new RMServerConfigurable();
		aaaalertConfigurable = new RMAlertConfigurable();
		ldapDsConfiguration = new LDAPDSConfigurationImpl(serverContext);
		rmConcurrentLoginServiceConfiguration = new RMConcurrentLoginServiceConfigurationImpl(serverContext);
		gtpConfiguration = new GTPPrimeConfigurationImpl(serverContext);
		this.translationMappingConfiguration = new TranslationMappingConfigurationImpl(serverContext);
	}
	@Deprecated
	public RMServerConfigurationImpl(ServerContext serverContext) {
		this(serverContext,null);
	}
	public RMServerConfigurationImpl() {
		
	}
	
	public String getConnectionUrlForAAADB(){
		return rmServerConfigurable.getAAADatasource().getConnectionUrlForAAADB();
	}
	public String getUsernameForAAADB(){
		return rmServerConfigurable.getAAADatasource().getUsernameForAAADB();
	}
	public String getPasswordForAAADB(){
		return rmServerConfigurable.getAAADatasource().getPassword();
	}
	
	public String getPlainTextPasswordForAAADB(){
		return rmServerConfigurable.getAAADatasource().getPlainTextPassword();
	}
	
	public String getLogLevel() {
		return rmServerConfigurable.getLoggerDetail().getLogLevel();
	}

	public int getLogRollingType() {
		return rmServerConfigurable.getLoggerDetail().getLogRollingType();
	}

	public int getLogRollingUnit() { 
		return rmServerConfigurable.getLoggerDetail().getLogRollingUnit();
	}

	public int getLogMaxRolledUnits() {
		return rmServerConfigurable.getLoggerDetail().getLogMaxRolledUnits();
	}

	public boolean isCompressLogRolledUnits() {
		return rmServerConfigurable.getLoggerDetail().getIsbCompressRolledUnit();
	}
	
	public boolean isServiceEnabled(String serviceId){
		
		boolean bIsServiceEnabled = false;
		if(rmServerConfigurable.getServiceMap().get(serviceId)!=null)
			bIsServiceEnabled = rmServerConfigurable.getServiceMap().get(serviceId);
		return bIsServiceEnabled;		
	}

	
	public String getSNMPAddress() {
		return rmServerConfigurable.getSnmpIpAddress();
	}

	public int getSNMPPort() {
		return rmServerConfigurable.getSnmpPort();
	}

	public String getSNMPCommunity() {
		return rmServerConfigurable.getSnmpCommunity();
	}
	
	public int getSchedularMaxThread() {
		return schedulerMaxThread;
	}

	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();		
		out.println();
		out.println(" -- EliteRM Server Configuration -- ");
		out.println();
		out.println("    RM Prepaid Service Enabled = " + isServiceEnabled(AAAServerConstants.RM_PREPAID_CHARGING_SERVICE_ID));
		out.println("    RM IP Pool Service Enabled = " + isServiceEnabled(AAAServerConstants.RM_IPPOOL_SERVICE_ID));
		out.println("    RM Concurrent Service Enabled = " + isServiceEnabled(AAAServerConstants.RM_CONCURRENT_LOGIN_SERVICE_ID));
		out.println("    AAA Database");
		out.println("      Connection URL = " + getConnectionUrlForAAADB());
		out.println("      Username/Password = " + getUsernameForAAADB() + "/*****");
		out.println("    Logging"); 
		out.println("      Level = " + getLogLevel()); //TODO also concatenate the string form of level value
		out.println("      Rolling Type = " + getLogRollingType());
		out.println("      Rolling Unit = " + getLogRollingUnit());
		out.println("      Compress Rolled Unit = " + compRollingUnit);
		out.println("      Max Rolled Unit = " + getLogMaxRolledUnits());
		out.println("    SNMP");
		out.println("      Service Address = " + getSNMPAddress() + ":" + getSNMPPort());
		out.println("      Commuinity = " + getSNMPCommunity() );
		out.println("    ");

		
		out.close();
		return stringBuffer.toString();
	}

	public int adminPort() {
		return adminPort;
	}

	public LDAPDSConfiguration getLDAPDSConfiguration() {
		return this.ldapDsConfiguration;
	}
	public DatabaseDSConfiguration getDatabaseDSConfiguration() {		
		return this.dsConfiguration;
	}
	
	public String getModule(){
		return MODULE;
	}
		
	public RadClientConfiguration getRadClientConfiguration(){
		return this.radClientConfigurationImpl;
	}

	public void reloadConfiguration() throws LoadConfigurationException {}
	
	@Override
	public String getKey() {
		return "";
	}


	@Override
	public SessionManagerConfiguration getSessionManagerConfiguration() {
		return this.sessionManagerConfiguration;
	}	
	@Override
	public @Nullable ConcurrentLoginPolicyConfiguration getConcurrentLoginPolicyConfiguration() {
		return this.loginPolicyConfiguration;
	}
	
	public void setLoginPolicyConfigurable(ConcurrentLoginPolicyConfiguration loginPolicyConfiguration) {
		this.loginPolicyConfiguration = loginPolicyConfiguration;
	}
	
	@Override
	public RadESConfiguration getRadESConfiguration() {
		return (RadESConfiguration) radESIConfiguration;
	}
	
	@Override
	public RadEsiGroupConfigurable getRadEsiGroupConfigurable() {
		return radEsiGroupConfigurable;
	}
	
	@Override
	public AAAPluginConfManager getPluginManagerConfiguration() {
		return pluginManagerConfiguration;
	}


	@Override
	public RMConcurrentLoginServiceConfiguration getRMConcurrentLoginServiceConfiguration() {
		return this.rmConcurrentLoginServiceConfiguration;
	}

	@Override
	public RMPrepaidChargingServiceConfiguration getRMPrepaidChargingServiceConfiguration() {
		return this.rmPrepaidChargingServiceConfiguration;
	}

	public GTPPrimeConfiguration getGTPPrimeConfiguration() {
		return this.gtpConfiguration;
	}
	
	@Override
	public WebServiceConfiguration getWebServiceConfiguration() {
		return this.rmServerConfigurable.getWebServiceConfiguration();
	}
	@Override
	public RMIPPoolConfiguration getIPPoolConfiguration() {
		return this.ippoolServiceConfiguration;
	}
	@Override
	public WimaxConfiguration getWimaxConfiguration() {
		// This is AAA Specific
		return null;
	}
	@Override
	public DHCPKeysConfiguration getDhcpKeysConfiguration() {
		// This is AAA Specific
		return null;
	}
	@Override
	public SPIKeyConfiguration getSpiKeysConfiguration() {
		// This is AAA Specific
		return null;
	}
	@Override
	public VSAInClassConfiguration getVSAInClassConfiguration() {
		// This is AAA Specific
		return null;
	}
	@Override
	public RadAuthConfiguration getAuthConfiguration() {
		// This is AAA Specific
		return null;
	}
	@Override
	public RadDynAuthConfiguration getDynAuthConfiguration() {
		// This is AAA Specific
		return null;
	}
	@Override
	public RadAcctConfiguration getAcctConfiguration() {
		// This is AAA Specific, -NA-
		return null;
	}
	@Override
	public RDRConfiguration getRDRConfiguration() {
		return rdrConfiguration;
		
	}

	@Override
	public RMChargingServiceConfiguration getRmChargingServiceConfiguration() {		
		return rmChargingServiceConfiguration;
	}
	@Override
	public RdrDetailLocalConfiguration getRdrDetailLocalConfiguration() {
		return rdrDetailLocalConfiguration;
		
	}
	@Override
	public boolean isNAIEnabled() {
		//AAA Auth Acct Proxy Specific param, -NA-
		return false;
	}
	@Override
	public List<String> getRealmNames() {
		//AAA Auth Acct Proxy Specific param, -NA-
		return Collections.emptyList();
	}
	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return this.rmServerConfigurable.getLoggerDetail().getSysLogConfiguration();
	}
	@Override
	public TranslationMappingConfiguration getTranslationMappingConfiguration() {
		return this.translationMappingConfiguration;
	}
	@Override
	public BaseAlertConfigurable getAAAAlertManagerConfiguration() {
		return aaaalertConfigurable;
	}

	@Override
	public EAPConfigurations getEAPConfigurations() {
		//AAA Specific RM does not EAP Module
		return null;
	}
	@Override
	public AlertListnersDetail getAlertListenersDetail() {
		return rmServerConfigurable.getAlertListners();
	}
	@Override
	public boolean isEligible(
			Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		
		if(GTPPrimeConfiguration.class.isAssignableFrom(configurableClass)){
			return isServiceEnabled(AAAServerConstants.GTP_SERVICE_ID);
		}else if(RDRConfiguration.class.isAssignableFrom(configurableClass)){
			return isServiceEnabled(AAAServerConstants.RDR_SERVICE_ID);
		}else if(RMIPPoolConfiguration.class.isAssignableFrom(configurableClass)){
			return isServiceEnabled(AAAServerConstants.RM_IPPOOL_SERVICE_ID);
		}else if(RMChargingServiceConfiguration.class.isAssignableFrom(configurableClass)){
			return isServiceEnabled(AAAServerConstants.RM_CHARGING_SERVICE_ID);
		}else if(RMConcurrentLoginServiceConfiguration.class.isAssignableFrom(configurableClass)){
			return isServiceEnabled(AAAServerConstants.RM_CONCURRENT_LOGIN_SERVICE_ID);
		}else if(RMPrepaidChargingServiceConfigImpl.class.isAssignableFrom(configurableClass)){
			return isServiceEnabled(AAAServerConstants.RM_PREPAID_CHARGING_SERVICE_ID);
		} 
		return true;
	}
	@PostRead
	public void postReadProcessing(){
		// No sonar :	Methods should not be empty		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		// No sonar :	Methods should not be empty		
	}
	
	@PostReload
	public void postReloadProcessing(){
		// No sonar :	Methods should not be empty		
	}

	//FIXME: This method will be removed
	public void readConfiguration() throws LoadConfigurationException{
		// No sonar :	Methods should not be empty		
	}
	@Override
	public CRLConfiguration getCRLConfiguration() {
		return crlConfigurable;
	}

	@Override
	public TrustedCAConfiguration getTrustedCAConfiguration() {
		return trustedCAConfiguration;
	}

	@Override
	public ServerCertificateConfiguration getServerCertificateConfiguration() {
		return serverCertificateConfigurable;
	}
	
	@Override
	public String getServerName() {
		return rmServerConfigurable.getServerName();
	}

	@Override
	public String getDomainName() {
		return rmServerConfigurable.getDomainName();
	}
	
	@Override
	public DiameterStackConfigurable getDiameterStackConfiguration() {
		return diameterCompositeConfigurable.getDiameterStackConfiguration();
	}
	@Override
	public DiameterPeerConfiguration getDiameterPeerConfiguration() {
		return diameterCompositeConfigurable.getDiameterPeerConfiguration();
	}
	@Override
	public RoutingTableConfigurable getDiameterRoutingConfiguration() {
		return this.diameterCompositeConfigurable.getDiameterRoutingConfiguration();
	}
	
	@Override
	public DiameterServiceConfigurationDetail getDiameterServiceConfiguration(String serviceId) {
		return null;
	}
	
	@Override
	public DriverConfigurationProvider getDiameterDriverConfiguration() {
		return diameterCompositeConfigurable.getDiameterDriverConfiguration();
	}
	@Override
	public KpiServiceConfiguration getKpiServiceConfiguration() {
		return rmServerConfigurable.getKpiServiceConfiguration();
	}
	@Override
	public CopyPacketTranslationConfigurable getCopyPacketTranslationConfiguration() {
		return copyPacketTranslationConfigurable;
	}
	
	@Override
	public MiscellaneousConfigurable getMiscellaneousConfigurable() {
		return miscellaneousConfigurable;
	}
	@Override
	public PriorityConfigurable getPriorityConfigurable() {
		return diameterCompositeConfigurable.getPriorityConfigurable();
	}
	@Override
	public DiameterSessionManagerConfigurable getDiameterSessionManagerConfiguration() {
		return diameterCompositeConfigurable.getDiameterSessionManagerConfiguration();
	}
	@Override
	public RadiusServicePolicyConfigurable getRadiusServicePolicyConfiguration() {
		// do nothing feature not supported
		return null;
	}
	@Override
	public DriverConfigurationProvider getDriverConfigurationProvider() {
		return rmDriversConfigurable;
	}
	@Override
	public ImsiBasedRoutingTableConfiguration getImsiBasedRoutingTableConfiguration() {
		return diameterCompositeConfigurable.getImsiBasedRoutingTableConfiguration();
	}

	/*
	 * No need to implement here anything as RM does not require grace policy
	 * @see com.elitecore.aaa.core.conf.AAAServerConfiguration#getGracePolicyConfiguration(java.lang.String)
	 */
	@Override
	public int[] getGracePolicyConfiguration(String name) {
		return null;
	}
	@Override
	public DiameterConcurrencyConfigurable getDiameterConcurrencyConfigurable() {
		return diameterCompositeConfigurable.getDiameterConcurrencyConfigurable();
	}
	
	@Override
	public MsisdnBasedRoutingTableConfiguration getMsisdnBasedRoutingTableConfiguration() {
		return diameterCompositeConfigurable.getMsisdnBasedRoutingTableConfiguration();
	}

	@Override
	public DiameterPeerGroupConfigurable getDiameterPeerGroupConfigurable() {
		return diameterCompositeConfigurable.getDiameterPeerGroupConfigurable();
	}

	@Override
	public PluginConfigurable getPluginConfiguration() {
		return pluginConfigurable;
	}
	@Override
	public IMDGConfigurable getImdgConfigurable() {
		return null;
	}
	@Override
	public ScriptConfigurable getScriptConfigurable() {
		return scriptConfigurable;
	}
	@Override
	public RadiusSessionCleanupDetail getRadiusSessionCleanupDetail() {
		return null;
	}
	
	@Override
	public RadiusESIGroupConfigurable getRadiusESIGroupConfigurable() {
		return null;
	}
	
	@Override
	public CorrelatedRadiusConfigurable getCorrelatedRadiusConfigurable() {
		return null;
	}
}
