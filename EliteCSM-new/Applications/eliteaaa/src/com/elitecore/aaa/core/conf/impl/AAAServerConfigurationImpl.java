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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.alert.conf.impl.AAAAlertConfigurable;
import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.aaa.core.conf.DatabaseDSConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
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
import com.elitecore.aaa.diameter.conf.DiameterCompositeConfigurable;
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
import com.elitecore.aaa.radius.conf.impl.GracePolicyConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadAcctConfigurationImpl;
import com.elitecore.aaa.radius.conf.impl.RadAuthConfigurationImpl;
import com.elitecore.aaa.radius.conf.impl.RadClientConfigurationImpl;
import com.elitecore.aaa.radius.conf.impl.RadDynAuthConfigurationImpl;
import com.elitecore.aaa.radius.conf.impl.RadESIConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadiusESIGroupConfigurable;
import com.elitecore.aaa.radius.esi.radius.conf.impl.CorrelatedRadiusConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.SessionManagerConfigurationImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;

@ReadOrder(order = { "aaaConfigurable", "imdgConfigurable", "miscellaneousConfigurable",
					 "dsConfiguration", "ldapDsConfiguration",
					 "trustedCAConfiguration", "crlConfiguration","serverCertificateConfigurable",
					 "radESIConfiguration", "correlatedRadiusConfigurable", "diaToRadProxyEsiGroupConfigurable", "radEsiGroupConfigurable", "radClientConfigurationImpl",
					 "aaaAlertConfigurable", "translationMappingConfiguration",
					 "copyPacketTranslationConfigurable",
					 "eapConfiguration", "gracePolicyConfigurable", "authServiceConfiguration",
					 "acctServiceConfiguration", "dynAuthConfiguration",
					 "radiusServicePolicyConfigurable", "radiusDriversConfigurable",
					 "sessionManagerConfiguration",
					 "vsaInclassConfigurable", "diameterCompositeConfiguration",
					 "wimaxConfigurable", "dhcpKeysConfigurable",
					 "spiKeyConfigurable", "pluginManagerConfiguration",
					 "pluginConfigurable", "scriptConfigurable"})
public final class AAAServerConfigurationImpl extends CompositeConfigurable implements AAAServerConfiguration {

	private final String MODULE = "AAA-CNF";
	private static final String KEY = "ELITE_AAA_SERVER";

	@Configuration private RadClientConfigurationImpl radClientConfigurationImpl;
	@Configuration private DatabaseDSConfigurationImpl dsConfiguration;
	@Configuration private LDAPDSConfigurationImpl ldapDsConfiguration;
	
	@Configuration private ServerCertificateConfigurable serverCertificateConfigurable;
	@Configuration private TrustedCAConfigurable trustedCAConfiguration;
	@Configuration private CRLConfigurable crlConfiguration;
	@Configuration private GracePolicyConfigurable gracePolicyConfigurable;
	@Configuration(conditional = true) @Nullable private RadAuthConfigurationImpl authServiceConfiguration;
	@Configuration(conditional = true) @Nullable private RadAcctConfigurationImpl acctServiceConfiguration;
	@Configuration(conditional = true) @Nullable private RadDynAuthConfigurationImpl dynAuthConfiguration;
	@Configuration private SessionManagerConfigurationImpl sessionManagerConfiguration;
	@Configuration private RadESIConfigurable radESIConfiguration;
	@Configuration private CorrelatedRadiusConfigurable correlatedRadiusConfigurable;
	@Configuration private RadEsiGroupConfigurable diaToRadProxyEsiGroupConfigurable;
	@Configuration private RadiusESIGroupConfigurable radEsiGroupConfigurable;
	@Configuration private EAPConfigurable eapConfiguration;
	
	@Configuration private CopyPacketTranslationConfigurable copyPacketTranslationConfigurable;
	@Configuration private TranslationMappingConfigurationImpl translationMappingConfiguration;
	@Configuration(required = false) @Nullable private AAAPluginConfManagerImpl pluginManagerConfiguration;
	@Configuration private AAAServerConfigurable aaaConfigurable;
	@Configuration(required = false) @Nullable private MiscellaneousConfigurable miscellaneousConfigurable;
	@Configuration private AAAAlertConfigurable aaaAlertConfigurable;
	@Configuration private VSAInClassConfigurable vsaInclassConfigurable;
	
	@Configuration private WimaxConfigurable wimaxConfigurable;
	@Configuration private DHCPKeysConfigurable dhcpKeysConfigurable;
	@Configuration(required = false) @Nullable private SPIKeyConfigurable spiKeyConfigurable;
	@Configuration private RadiusServicePolicyConfigurable radiusServicePolicyConfigurable;
	@Configuration @Nonnull private RadiusDriversConfigurable radiusDriversConfigurable;
	@Configuration private DiameterCompositeConfigurable diameterCompositeConfiguration;
	
	@Nullable private ConcurrentLoginPolicyConfiguration loginPolicyConfiguration;
	@Configuration private PluginConfigurable pluginConfigurable;
	
	@Configuration(required = false) @Nonnull private IMDGConfigurable imdgConfigurable;
	@Configuration private ScriptConfigurable scriptConfigurable;
	
	public AAAServerConfigurationImpl() {
		//This is required by Configuration framework
		imdgConfigurable = new IMDGConfigurable();
	}
	
	@Override
	public String getConnectionUrlForAAADB(){
		return aaaConfigurable.getAAADatasource().getConnectionUrlForAAADB();
	}
	
	@Override
	public String getUsernameForAAADB(){
		return aaaConfigurable.getAAADatasource().getUsernameForAAADB();
	}
	
	@Override
	public String getPasswordForAAADB(){
		return aaaConfigurable.getAAADatasource().getPassword();
	}
	
	@Override
	public String getPlainTextPasswordForAAADB(){
		return aaaConfigurable.getAAADatasource().getPlainTextPassword();
	}

	@Override
	public String getLogLevel() {
		return aaaConfigurable.getLoggerDetail().getLogLevel();
	}

	@Override
	public int getLogRollingType() {
		return aaaConfigurable.getLoggerDetail().getLogRollingType();
	}

	@Override
	public int getLogRollingUnit() {
		return aaaConfigurable.getLoggerDetail().getLogRollingUnit();
	}

	@Override
	public int getLogMaxRolledUnits() {
		return aaaConfigurable.getLoggerDetail().getLogMaxRolledUnits();
	}

	@Override
	public boolean isCompressLogRolledUnits() {
		return aaaConfigurable.getLoggerDetail().getIsbCompressRolledUnit();
	}
	
	@Override
	public boolean isServiceEnabled(String serviceId){
		return aaaConfigurable.isServiceEnabled(serviceId);
	}

	@Override
	public String getSNMPAddress() {
		return aaaConfigurable.getSnmpIpAddress();
	}

	@Override
	public int getSNMPPort() {
		return aaaConfigurable.getSNMPPort();
	}

	@Override
	public String getSNMPCommunity() {
		return aaaConfigurable.getSNMPCommunity();
	}

	public int getSchedularMaxThread() {
		return aaaConfigurable.getInternalSchedulerMaxThread();
	}

	@Override
	public AlertListnersDetail getAlertListenersDetail() {
		return aaaConfigurable.getAlertListners();
	}
	
	@Override
	public DiameterSessionManagerConfigurable getDiameterSessionManagerConfiguration() {
		return diameterCompositeConfiguration.getDiameterSessionManagerConfiguration();
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();		
		out.println();
		out.println(" -- EliteAAA Server Configuration -- ");
		out.println();
		out.println("    Rad Auth Service Enabled = " + isServiceEnabled(AAAServerConstants.RAD_AUTH_SERVICE_ID));
		out.println("    Rad Acct Service Enabled = " + isServiceEnabled(AAAServerConstants.RAD_ACCT_SERVICE_ID));
		out.println("    AAA Database");
		out.println("      Connection URL = " + getConnectionUrlForAAADB());
		out.println("      Username/Password = " + getUsernameForAAADB() + "/*****");
		out.println("    Logging"); 
		out.println("      Level = " + getLogLevel()); //TODO also concatenate the string form of level value
		out.println("      Rolling Type = " + getLogRollingType());
		out.println("      Rolling Unit = " + getLogRollingUnit());
		out.println("      Compress Rolled Unit = " + isCompressLogRolledUnits());
		out.println("      Max Rolled Unit = " + getLogMaxRolledUnits());
		out.println("    SNMP");
		out.println("      Service Address = " + getSNMPAddress() + ":" + getSNMPPort());
		out.println("      Commuinity = " + getSNMPCommunity() );
		out.println("    ");


		out.close();
		return stringBuffer.toString();
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

	public void reloadConfiguration() throws LoadConfigurationException {
		//	No sonar : 	Methods should not be empty
	}

	@Override
	public String getKey() {
		return KEY;
	}


	@Override
	public RadAuthConfiguration getAuthConfiguration() {
		return this.authServiceConfiguration;
	}

	@Override
	public RadAcctConfiguration getAcctConfiguration() {
		return this.acctServiceConfiguration;
	}
	
	@Override
	public DHCPKeysConfiguration getDhcpKeysConfiguration() {
		return this.dhcpKeysConfigurable;
	}
	@Override
	public WimaxConfiguration getWimaxConfiguration() {
		return this.wimaxConfigurable;
	}
	@Override
	public SPIKeyConfiguration getSpiKeysConfiguration() {
		return this.spiKeyConfigurable;
	}
	@Override
	public SessionManagerConfiguration getSessionManagerConfiguration() {
		return this.sessionManagerConfiguration;
	}	
	
	@Override
	public ConcurrentLoginPolicyConfiguration getConcurrentLoginPolicyConfiguration() {
		return this.loginPolicyConfiguration;
	}

	public void setLoginPolicyConfigurable(ConcurrentLoginPolicyConfiguration loginPolicyConfigurable) {
		this.loginPolicyConfiguration = loginPolicyConfigurable;
	}
	
	@Override
	public RadESConfiguration getRadESConfiguration() {
		return  radESIConfiguration;
	}
	
	@Override
	public RadEsiGroupConfigurable getRadEsiGroupConfigurable() {
		return diaToRadProxyEsiGroupConfigurable;
	}

	@Override
	public AAAAlertConfigurable getAAAAlertManagerConfiguration() {
		return aaaAlertConfigurable;
	}

	@Override
	public AAAPluginConfManager getPluginManagerConfiguration() {
		return pluginManagerConfiguration;
	}

	@Override
	public MiscellaneousConfigurable getMiscellaneousConfigurable() {
		return miscellaneousConfigurable;
	}

	@Override
	public VSAInClassConfiguration getVSAInClassConfiguration() {
		return vsaInclassConfigurable;
	}
	@Override
	public RadDynAuthConfiguration getDynAuthConfiguration() {
		return dynAuthConfiguration;
	}
	
	@Override
	public WebServiceConfiguration getWebServiceConfiguration() {
		return this.aaaConfigurable.getWebServiceConfiguration();
	}
	
	@Override
	public SysLogConfiguration getSysLogConfiguration() {
		return aaaConfigurable.getLoggerDetail().getSysLogConfiguration();
	}
	@Override
	public List<String> getRealmNames() {
		return aaaConfigurable.getRealmNames();
	}
	@Override
	public boolean isNAIEnabled() {
		return aaaConfigurable.getRfcNai().getEnabled();
	}

	@Override
	public TranslationMappingConfiguration getTranslationMappingConfiguration() {
		return translationMappingConfiguration;
	}
	
	@Override
	public EAPConfigurable getEAPConfigurations() {
		return this.eapConfiguration;
	}
	
	public RadiusSessionCleanupDetail getRadiusSessionCleanupDetail() {
		return aaaConfigurable.getRadiusSessionCleanupDetail();
	}
	
	@PostRead
	public void postReadProcessing(){
		// No sonar : Methods should not be empty
	}
	
	@PostWrite
	public void postWriteProcessing(){
		// No sonar : Methods should not be empty
	}
	
	@PostReload
	public void postReloadProcessing(){
		// No sonar : Methods should not be empty
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		if (RadAuthConfiguration.class.isAssignableFrom(configurableClass)) {
			return isServiceEnabled(AAAServerConstants.RAD_AUTH_SERVICE_ID);
		} else if (RadAcctConfiguration.class.isAssignableFrom(configurableClass)) {
			return isServiceEnabled(AAAServerConstants.RAD_ACCT_SERVICE_ID);
		} else if (RadDynAuthConfiguration.class.isAssignableFrom(configurableClass)) {
			return isServiceEnabled(AAAServerConstants.RAD_DYNAUTH_SERVICE_ID);
		}
		
		return true;
	}

	@Override
	public CRLConfiguration getCRLConfiguration() {
		return crlConfiguration;
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
		return aaaConfigurable.getServerName();
	}

	@Override
	public String getDomainName() {
		return aaaConfigurable.getDomainName();
	}
	
	@Override
	public DiameterStackConfigurable getDiameterStackConfiguration() {
		return diameterCompositeConfiguration.getDiameterStackConfiguration();
	}
	
	@Override
	public PriorityConfigurable getPriorityConfigurable() {
		return diameterCompositeConfiguration.getPriorityConfigurable();
	}
	
	@Override
	public DiameterPeerConfiguration getDiameterPeerConfiguration() {
		return diameterCompositeConfiguration.getDiameterPeerConfiguration();
	}

	@Override
	public RoutingTableConfigurable getDiameterRoutingConfiguration() {
		return diameterCompositeConfiguration.getDiameterRoutingConfiguration();
	}

	@Override
	public DiameterServiceConfigurationDetail getDiameterServiceConfiguration(String serviceId) {
		return diameterCompositeConfiguration.getDiameterServiceConfiguration(serviceId);
	}
	
	@Override
	public @Nullable DriverConfigurationProvider getDiameterDriverConfiguration() {
		return diameterCompositeConfiguration.getDiameterDriverConfiguration();
	}

	@Override
	public KpiServiceConfiguration getKpiServiceConfiguration() {
		return aaaConfigurable.getKpiServiceConfiguration();
	}

	@Override
	public CopyPacketTranslationConfigurable getCopyPacketTranslationConfiguration() {
		return copyPacketTranslationConfigurable;
	}

	@Override
	public RadiusServicePolicyConfigurable getRadiusServicePolicyConfiguration() {
		return radiusServicePolicyConfigurable;
	}

	@Override
	public @Nonnull DriverConfigurationProvider getDriverConfigurationProvider() {
		return radiusDriversConfigurable;
	}

	@Override
	public ImsiBasedRoutingTableConfiguration getImsiBasedRoutingTableConfiguration() {
		return diameterCompositeConfiguration.getImsiBasedRoutingTableConfiguration();
	}
	
	@Override
	public int[] getGracePolicyConfiguration(String name) {		
		if (name != null) {
			return this.gracePolicyConfigurable.getGracePolicyConfigurationMap().get(name);
		}
		return null;
	}

	@Override
	public DiameterConcurrencyConfigurable getDiameterConcurrencyConfigurable() {
		return diameterCompositeConfiguration.getDiameterConcurrencyConfigurable();
	}

	@Override
	public MsisdnBasedRoutingTableConfiguration getMsisdnBasedRoutingTableConfiguration() {
		return diameterCompositeConfiguration.getMsisdnBasedRoutingTableConfiguration();
	}

	@Override
	public DiameterPeerGroupConfigurable getDiameterPeerGroupConfigurable() {
		return diameterCompositeConfiguration.getDiameterPeerGroupConfigurable();
	}
	
	@Override
	public PluginConfigurable getPluginConfiguration() {
		return pluginConfigurable;
	}
	
	@Override
	public IMDGConfigurable getImdgConfigurable() {
		return imdgConfigurable;
	}

	@Override
	public ScriptConfigurable getScriptConfigurable() {
		return scriptConfigurable;
	}
	
	@Override
	public RadiusESIGroupConfigurable getRadiusESIGroupConfigurable() {
		return radEsiGroupConfigurable;
	}
	
	@Override
	public CorrelatedRadiusConfigurable getCorrelatedRadiusConfigurable() {
		return correlatedRadiusConfigurable;
	}

}
