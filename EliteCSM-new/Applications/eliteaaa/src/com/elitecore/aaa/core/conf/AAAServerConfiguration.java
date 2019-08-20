/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.aaa.core.conf;


import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.alert.conf.impl.BaseAlertConfigurable;
import com.elitecore.aaa.core.conf.impl.IMDGConfigurable;
import com.elitecore.aaa.core.conf.impl.KpiServiceConfiguration;
import com.elitecore.aaa.core.conf.impl.MiscellaneousConfigurable;
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


/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface AAAServerConfiguration {
	
	/**
	 * @return Server Name configured in elite-aaa-server.xml
	 */
	public String getServerName();
	/**
	 * @return Domain Name configured in elite-aaa-server.xml
	 */
	public String getDomainName();
	
	public String getConnectionUrlForAAADB();
	public String getUsernameForAAADB();
	public String getPasswordForAAADB();
	public String getPlainTextPasswordForAAADB();
	
	public String getLogLevel();
	public int getLogRollingType();
	public int getLogRollingUnit();
	public int getLogMaxRolledUnits();
	public boolean isCompressLogRolledUnits();
	public LDAPDSConfiguration getLDAPDSConfiguration();
	public DatabaseDSConfiguration getDatabaseDSConfiguration();
	public RadClientConfiguration getRadClientConfiguration();
	public WimaxConfiguration getWimaxConfiguration();
	public DHCPKeysConfiguration getDhcpKeysConfiguration();
	public SPIKeyConfiguration getSpiKeysConfiguration();
	public SessionManagerConfiguration getSessionManagerConfiguration();
	public @Nullable ConcurrentLoginPolicyConfiguration getConcurrentLoginPolicyConfiguration();
	public WebServiceConfiguration getWebServiceConfiguration();
	public SysLogConfiguration getSysLogConfiguration();
	public KpiServiceConfiguration getKpiServiceConfiguration();
		
	public boolean isNAIEnabled();
	public List<String> getRealmNames();
	
	public String getSNMPAddress(); 
	public int getSNMPPort();
	public String getSNMPCommunity();
	
	public String getKey();
	public boolean isServiceEnabled(String serviceId);
	public VSAInClassConfiguration getVSAInClassConfiguration();
	
	public RadAuthConfiguration getAuthConfiguration();
	
	public DiameterServiceConfigurationDetail getDiameterServiceConfiguration(String serviceId);

	public RadDynAuthConfiguration getDynAuthConfiguration() ;
		

	public RadAcctConfiguration getAcctConfiguration();
	public DiameterStackConfigurable getDiameterStackConfiguration();
	public RadESConfiguration getRadESConfiguration();
	public RadEsiGroupConfigurable getRadEsiGroupConfigurable();
	
	public BaseAlertConfigurable getAAAAlertManagerConfiguration();
	public AAAPluginConfManager getPluginManagerConfiguration();
	
	public TranslationMappingConfiguration getTranslationMappingConfiguration();
	public CopyPacketTranslationConfigurable getCopyPacketTranslationConfiguration();
	public EAPConfigurations getEAPConfigurations();
	
	public AlertListnersDetail getAlertListenersDetail();
	
	public CRLConfiguration getCRLConfiguration();
	public TrustedCAConfiguration getTrustedCAConfiguration();
	public ServerCertificateConfiguration getServerCertificateConfiguration();
	public DiameterPeerConfiguration getDiameterPeerConfiguration();
	public RoutingTableConfigurable getDiameterRoutingConfiguration();
	public @Nullable DriverConfigurationProvider getDiameterDriverConfiguration();
	public PriorityConfigurable getPriorityConfigurable();
	public DiameterSessionManagerConfigurable getDiameterSessionManagerConfiguration();
	public RadiusServicePolicyConfigurable getRadiusServicePolicyConfiguration();
	public @Nonnull DriverConfigurationProvider getDriverConfigurationProvider();
	public MiscellaneousConfigurable getMiscellaneousConfigurable();
	public ImsiBasedRoutingTableConfiguration getImsiBasedRoutingTableConfiguration();
	public int[] getGracePolicyConfiguration(String name);
	public DiameterConcurrencyConfigurable getDiameterConcurrencyConfigurable();
	public MsisdnBasedRoutingTableConfiguration getMsisdnBasedRoutingTableConfiguration();
	public DiameterPeerGroupConfigurable getDiameterPeerGroupConfigurable();
	public PluginConfigurable getPluginConfiguration();
	IMDGConfigurable getImdgConfigurable();
	public ScriptConfigurable getScriptConfigurable();
	public RadiusSessionCleanupDetail getRadiusSessionCleanupDetail();
	RadiusESIGroupConfigurable getRadiusESIGroupConfigurable();
	CorrelatedRadiusConfigurable getCorrelatedRadiusConfigurable();
}
