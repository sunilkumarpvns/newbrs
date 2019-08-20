/*
*  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.aaa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import com.elitecore.aaa.alert.AAASystemAlertManager;
import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.alert.conf.impl.AAAAlertConfigurable;
import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.DetailLocalAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.conf.impl.AAAPluginConfManagerConfigurable;
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.ClusterGroupData;
import com.elitecore.aaa.core.conf.impl.DHCPKeysConfigurable;
import com.elitecore.aaa.core.conf.impl.IMDGConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.KpiServiceConfiguration;
import com.elitecore.aaa.core.conf.impl.MemberData;
import com.elitecore.aaa.core.conf.impl.VSAInClassConfigurable;
import com.elitecore.aaa.core.conf.impl.WimaxConfigurable;
import com.elitecore.aaa.core.config.RadiusSessionCleanupDetail;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.plugins.PluginDetailProvider;
import com.elitecore.aaa.core.plugins.conf.PluginDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.EliteWebServiceServer;
import com.elitecore.aaa.core.server.axixserver.HTTPSEliteWebServiceServer;
import com.elitecore.aaa.core.server.axixserver.WebServiceConfiguration;
import com.elitecore.aaa.core.util.cli.AAALicenseCommand;
import com.elitecore.aaa.core.util.cli.AlertCommand;
import com.elitecore.aaa.core.util.cli.CdrFlushCommand;
import com.elitecore.aaa.core.util.cli.CdrFlushCommand.FlushEventHandler;
import com.elitecore.aaa.core.util.cli.ClearKeysCommand;
import com.elitecore.aaa.core.util.cli.ClearSessionCommand;
import com.elitecore.aaa.core.util.cli.ClearWimaxKeysCommand;
import com.elitecore.aaa.core.util.cli.ClearWimaxSessionCommand;
import com.elitecore.aaa.core.util.cli.ConfigCommand;
import com.elitecore.aaa.core.util.cli.DBDetailProvider;
import com.elitecore.aaa.core.util.cli.DriverProfileProvider;
import com.elitecore.aaa.core.util.cli.ESIScanCommand;
import com.elitecore.aaa.core.util.cli.ESIStatisticsCommand;
import com.elitecore.aaa.core.util.cli.ESISummaryCommand;
import com.elitecore.aaa.core.util.cli.EapSessionDetailProvider;
import com.elitecore.aaa.core.util.cli.KpiServiceCommand;
import com.elitecore.aaa.core.util.cli.MiscellaneousConfigDetailProvider;
import com.elitecore.aaa.core.util.cli.PolicyProfileProvider;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SNMPServCommand;
import com.elitecore.aaa.core.util.cli.ServerHomeCommand;
import com.elitecore.aaa.core.util.cli.ServicesCommand;
import com.elitecore.aaa.core.util.cli.SessionDetailProvider;
import com.elitecore.aaa.core.util.cli.SessionMgrCommand;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.core.util.cli.ShowKeysCommand;
import com.elitecore.aaa.core.util.cli.ShowWimaxKeysCommand;
import com.elitecore.aaa.core.util.cli.SubscriberDetailProvider;
import com.elitecore.aaa.core.util.cli.VersionCommand;
import com.elitecore.aaa.core.util.cli.WimaxSessionDetailProvider;
import com.elitecore.aaa.core.util.cli.data.ServiceDataProvider;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.core.wimax.keys.KeyManagerImpl;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterTGPPServerConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.DiameterCCServiceConfigurable;
import com.elitecore.aaa.diameter.service.DiameterService;
import com.elitecore.aaa.diameter.service.application.EliteDiameterStack;
import com.elitecore.aaa.diameter.service.application.EliteDiameterStack.DiameterServiceConfigurationSetter;
import com.elitecore.aaa.diameter.service.drivers.DiameterDriverFactory;
import com.elitecore.aaa.exprlib.function.ConnectionProviderImpl;
import com.elitecore.aaa.exprlib.function.FunctionBaseValue;
import com.elitecore.aaa.exprlib.function.diameter.FunctionDBSession;
import com.elitecore.aaa.license.AAALicenseManager;
import com.elitecore.aaa.license.EvaluationLicenseManager;
import com.elitecore.aaa.license.LicenseExpiryListener;
import com.elitecore.aaa.license.LicenseManagerFactory;
import com.elitecore.aaa.radius.conf.impl.ClientsConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadAcctServiceConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadAuthServiceConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadClientPolicyConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadDynAuthServiceConfigurable;
import com.elitecore.aaa.radius.conf.impl.RadHotlineConfigurable;
import com.elitecore.aaa.radius.drivers.RadiusDriverFactory;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.service.acct.RadAcctService;
import com.elitecore.aaa.radius.service.acct.plugins.conf.impl.UniversalAcctPluginConfigurable;
import com.elitecore.aaa.radius.service.auth.RadAuthService;
import com.elitecore.aaa.radius.service.auth.plugins.conf.impl.UniversalAuthPluginCofigurable;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthService;
import com.elitecore.aaa.radius.session.SessionsFactory;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSessionFactory;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.CounterAwareConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyContainer;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicatorManager;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommunicatorManagerImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusESIGroupFactory;
import com.elitecore.aaa.radius.translators.RadiusTranslator;
import com.elitecore.aaa.radius.translators.copypacket.RadiusCopyPacketTranslator;
import com.elitecore.aaa.radius.util.cli.ClientsCommand;
import com.elitecore.aaa.radius.util.cli.RadClientCommand;
import com.elitecore.aaa.radius.util.cli.RadiusCertificateCommand;
import com.elitecore.aaa.radius.util.mbean.RadUserAccountController;
import com.elitecore.aaa.snmp.service.EliteAAASNMPAgent;
import com.elitecore.aaa.snmp.service.NullSnmpAgent;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.mbean.AAAClientController;
import com.elitecore.aaa.util.mbean.AAASupportedRFCController;
import com.elitecore.aaa.util.mbean.BaseServerController;
import com.elitecore.aaa.util.mbean.ConfigurationDetailController;
import com.elitecore.aaa.util.mbean.MiscellaneousConfigMbeanImpl;
import com.elitecore.aaa.ws.config.AAARestServer;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.exception.StartupFailedException;
import com.elitecore.commons.kpi.handler.ConnectionProvider;
import com.elitecore.commons.kpi.handler.DatabaseMIBSerializer;
import com.elitecore.commons.kpi.handler.MIBSerializer;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadLogKeyResolver;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.sync.SMConfigurationSynchronizer;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.StoreConfigurationException;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.commons.utilx.mbean.SystemDetailController;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.imdg.ImdgInstanceFailedException;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.server.data.EliteSystemDetail;
import com.elitecore.core.server.data.LiveServerSummary;
import com.elitecore.core.server.data.ServerReloadResponse;
import com.elitecore.core.serverx.BaseEliteServer;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheContainer;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.CacheManager;
import com.elitecore.core.serverx.manager.cache.CacheStatistics;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.serverx.snmp.mib.jvm.extended.JVMThreadStatisticsProvider;
import com.elitecore.core.serverx.snmp.mib.jvm.extended.JVM_MANAGEMENT_MIBImpl;
import com.elitecore.core.serverx.snmp.mib.jvm.extended.JvmMemoryStatisticsProvider;
import com.elitecore.core.serverx.snmp.mib.jvm.extended.JvmRunTimeStatisticsProvider;
import com.elitecore.core.serverx.snmp.mib.mib2.extended.SnmpImpl;
import com.elitecore.core.services.data.LiveServiceSummary;
import com.elitecore.core.servicex.EliteAdminService;
import com.elitecore.core.servicex.EliteService;
import com.elitecore.core.servicex.LicensedService;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.ClearCommand;
import com.elitecore.core.util.cli.cmd.DataSourceCommand;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.cli.cmd.LDAPDetailProvider;
import com.elitecore.core.util.cli.cmd.LogMonitorCommandExt;
import com.elitecore.core.util.cli.cmd.ReInitDetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.cli.cmd.ShowCommand;
import com.elitecore.core.util.cli.cmd.SysInfoCommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.dictionary.EliteDictionaryData;
import com.elitecore.core.util.mbean.data.live.EliteNetServerDetails;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.ILicenseValidator;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.diameterapi.core.common.PolicyDataRegistrationFailedException;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.stack.StackInitializationFailedException;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslatorPolicyData;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslatorPolicyDataImpl;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.impl.FunctionDBLookup;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;
import com.elitecore.license.publickey.ElitePublickeyGenerator;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import com.sun.management.snmp.agent.SnmpMib;


/**
 * 
 * @author baiju
 * 
 */
public final class EliteAAAServer extends BaseEliteServer {

	private static final String MODULE = "ELITE-AAA";
	private static final String DEVELOPMENT_VERSION_NO = "Development Version";
	private static final int NANO_TO_MILLI = 1000000;
	private static final String SERVER_HOME = "ELITEAAA_HOME";

	@Nonnull private final AAAServerContext serverContext;
	private EliteRollingFileLogger serverLevelLogger;
	private EliteAdminService adminService = null;
	private AAALicenseManager licenseManager;


	@Nullable private WimaxSessionManager wimaxSessionManager = null;
	@Nullable private KeyManager keyManager = null;
	@Nonnull private final AtomicInteger tpsCounter;
	private long lastResetTime;
	@Nullable private AAAServerConfigurationImpl serverConfiguration;
	@Nonnull private final AAASystemAlertManager systemAlertManager;
	@Nullable private ExternalScriptsManager externalScriptsManager = null;
	@Nonnull private final RadUDPCommunicatorManager radUDPCommunicatorManager;
	@Nonnull private final CacheManager cacheManager;
	@Nonnull private final CacheContainer cacheContainer;	
	@Nonnull private final LiveServerSummary liveServerSummary;
	@Nonnull private final SMConfigurationSynchronizer smConfigurationSynchronizer;

	/* Context for reading the configuration */
	@Nonnull private final AAAConfigurationContext configurationContext;
	private long aaaServerUPTime;
	private String aaaServerInstanceId = "AAA";
	private String serverInstanceID;
	private String serverInstanceName;
	private static Thread startUPThread;
	private boolean bServerStartedWithLastConf;

	private void registerDiameterServicesForSetCommand() {
		ConfigurationSetter diameterServiceConfigurationSetter = eliteDiameterStack.new DiameterServiceConfigurationSetter();
		SetCommand.registerConfigurationSetter(diameterServiceConfigurationSetter);
	}

	/*
	 * NOTE: Elite Diameter Stack will be null, 
	 * if Diameter Stack is Disabled in Server Configuration.
	 */
	@Nullable private EliteDiameterStack eliteDiameterStack;
	@Nullable private DriverManager diameterDriverManager;
	@Nullable private DriverManager radiusDriverManager;
	@Nullable private RadPluginManager radiusPluginManager;
	@Nullable private DiameterPluginManager diameterPluginManager;
	@Nonnull private EliteAAASNMPAgent snmpAgent;
	@Nullable private MIBSerializer mibSerializer;

	private AtomicLong totalResponseTimeInNano;
	private AtomicInteger totalRequestCount;
	@Nullable private RadiusLogMonitor radiusLogMonitor;
	@Nullable private HazelcastImdgInstance hazelcastImdgInstance;
	private SessionsFactory radiusSessionFactory;
	private RadiusESIGroupFactory radiusEsiGroupFactory;
	private AAARestServer aaaRestServer ;
	
	/**
	 * 
	 * @param serverHome
	 */
	EliteAAAServer(String strServerHome) {
		super(strServerHome, "Elite AAA Server");

		serverContext = new AAAServerContextImpl();
		snmpAgent = new NullSnmpAgent(serverContext,AAAServerConstants.AAA_ENTERPRISE_OID);
		tpsCounter= new AtomicInteger(0);
		this.totalResponseTimeInNano = new AtomicLong();
		this.totalRequestCount = new AtomicInteger();
		radUDPCommunicatorManager = new RadUDPCommunicatorManagerImpl(serverContext);
		liveServerSummary = new LiveServerSummary();
		cacheManager = new CacheManager();
		systemAlertManager = new AAASystemAlertManager(serverContext);
		smConfigurationSynchronizer = new SMConfigurationSynchronizer(serverContext);
		configurationContext = new AAAConfigurationContext(serverContext.getServerHome(), serverContext);
		radiusEsiGroupFactory = new RadiusESIGroupFactory();
		cacheContainer = new CacheContainer() {			
			@Override
			public void register(Cacheable cacheableObj) {
				cacheManager.load(cacheableObj);			
			}
		};
		
		LogManager.getLogger().info(MODULE,"EliteAAA server instance from " + getServerHome() + " location");
	}

	/**
	 * @throws Exception 
	 * 
	 */
	private void readServerConfiguration() throws LoadConfigurationException {
		serverLevelLogger = new EliteRollingFileLogger.Builder(
				getServerInstanceName(), 
				getServerHome() + File.separator + "logs" + File.separator + "eliteaaa-server")
		.build();

		setLoggingParameters();
		
		if (isLicenseValid()) {
			try {
				serverConfiguration = (AAAServerConfigurationImpl) configurationContext.read(AAAServerConfigurationImpl.class);
				//as soon as the configuration is read then need to write the configuration
				writeConfigurationQuietly();
				
			} catch (LoadConfigurationException e1) {
				LogManager.getLogger().error(MODULE, e1.getMessage());
				LogManager.getLogger().warn(MODULE, "Loading last known good configuration");
				LogManager.getLogger().trace(MODULE, e1);
				this.bServerStartedWithLastConf = true;
				try{
					serverConfiguration = (AAAServerConfigurationImpl) configurationContext.read(AAAServerConfigurationImpl.class);
				}catch (LoadConfigurationException e) {
					this.bServerStartedWithLastConf = false;
					LogManager.getLogger().error(MODULE, "Failed to load last know good configuration, reason: "+e.getMessage());
					LogManager.getLogger().trace(MODULE, e1);
					throw e;
				}	
			}
			setServerLogParameters();
			
			readConcurrentLoginPolicies();
		}
	}

	private void readConcurrentLoginPolicies() {
		ConcurrentLoginPolicyContainer concurrentLoginPolicyContainer = new ConcurrentLoginPolicyContainer(getServerContext());
		serverConfiguration.setLoginPolicyConfigurable(concurrentLoginPolicyContainer);
		concurrentLoginPolicyContainer.readConfiguration();
		getServerContext().registerCacheable(concurrentLoginPolicyContainer);
	}
	
	private void setLoggingParameters() {
		LogManager.setDefaultLogger(serverLevelLogger);
		LogManager.setLogKeyResolver(new EliteThreadLogKeyResolver());
	}
	
	private void writeConfigurationQuietly(){
		try{
			configurationContext.write(serverConfiguration);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Successfully created last good configuration.");
			}
		}catch (StoreConfigurationException ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in creating last good configuration.");
			}
			LogManager.getLogger().trace(MODULE, ex);
		}
	}
	
	private void loadServerLicense(){
		setValidLicense(true);
		if (getServerContext().getServerMajorVersion().equalsIgnoreCase(DEVELOPMENT_VERSION_NO)) {
			LogManager.getLogger().warn(MODULE, "Development version binaries detected");
		} else {
			LicenseExpiryListener licenseExpiryListener = new LicenseExpiryListener() {
					
					@Override
					public void execute() {
						setValidLicense(false);
						stopServices();
					}
			};
			try {
				licenseManager = LicenseManagerFactory.createLicenseManager(getServerContext(), licenseExpiryListener);
				licenseManager.init();
			} catch (InvalidLicenseException e) {

				LogManager.getLogger().error(MODULE,"Invalid license, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);

				LogManager.getLogger().warn(MODULE, "Providing EliteAAA Evaluation license with 100 TPS for 24 hours now onwards.");
				
				licenseManager = new EvaluationLicenseManager(getServerContext(), licenseExpiryListener);
			}
		}
	}
	
	@Override
	public String getLicenseKey() {
		return licenseManager.getLicenseKey();
	}

	private void loadDictionary() {
		loadRadiusDictionaryQuietly();
		loadDiameterDictionaryQuietly();
	}

	private void loadDiameterDictionaryQuietly() {
		try {
			loadDiameterDictionary();
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, "Failed to load diameter dictionary, Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
	}

	private void loadRadiusDictionaryQuietly() {
		try {
			loadRadiusDictionary();
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
	}
	
	private void loadRadiusDictionary() throws Exception {
		File radiusDictionaryFolder = new File(getServerHome() + File.separator + "dictionary" 
				+ File.separator + "radius");
		Dictionary.getInstance().loadDictionary(radiusDictionaryFolder, new ILicenseValidator() {
			@Override
			public boolean isVendorSupported(String vendorId) {
				return getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_SUPPORTED_VENDOR, vendorId);
			}
		});
	}

	private void loadDiameterDictionary() throws Exception {
		File diameterDictionaryFolder = new File(getServerHome() + File.separator + "dictionary" 
				+ File.separator + "diameter");
		
		DiameterDictionary.getInstance().load(diameterDictionaryFolder, 
				new com.elitecore.diameterapi.diameter.common.util.ILicenseValidator() {
			
			@Override
			public boolean isVendorSupported(String vendorId) {
				return getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_SUPPORTED_VENDOR, vendorId);
			}
		});
	}
	
	private void initServer() {
		LogManager.getLogger().trace(MODULE, "Server initialization process started");
		if (isLicenseValid()) {
			super.setInternalSchedulerThreadSize(serverConfiguration.getSchedularMaxThread());
			if (!getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_CPU, String.valueOf(SystemUtil.getAvailableProcessor()))) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE,"License warning, number of available processor available is not matching with licensed number (licensed: " +getLicenseValues(LicenseNameConstants.SYSTEM_CPU)+ ", available: "+ SystemUtil.getAvailableProcessor()+")");
			}

			if (!getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_NODE, new ElitePublickeyGenerator(
					ElitePublickeyGenerator.PLAIN_TEXT_FORMAT).generatePublicKey(serverContext.getServerHome(), 
							LicenseConstants.DEFAULT_ADDITIONAL_KEY, getServerInstanceID(), getServerInstanceName()))) {
				setValidLicense(false);
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().error(MODULE, "Invalid license, either IP Address or Home Location of EliteAAA server instance is not matching with license issued.");
			} else {
				wimaxSessionManager = new WimaxSessionManager();
				wimaxSessionManager.init(getServerContext());
				try {
					ClearSessionCommand.getInstance().registerDetailProvider(new ClearWimaxSessionCommand(wimaxSessionManager));
				} catch (RegistrationFailedException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
				
				keyManager = KeyManagerImpl.newInstance(serverContext);
				
				try {
					ShowKeysCommand showKeysCommand = new ShowKeysCommand();
					showKeysCommand.registerDetailProvider(new ShowWimaxKeysCommand(keyManager));
					ShowCommand.registerDetailProvider(showKeysCommand);
					
					ClearKeysCommand clearKeysCommand = new ClearKeysCommand();
					clearKeysCommand.registerDetailProvider(new ClearWimaxKeysCommand(keyManager));
					ClearCommand.registerDetailProvider(clearKeysCommand);
				} catch (RegistrationFailedException e) {
					LogManager.getLogger().trace(MODULE, e);
				}

				this.cacheContainer.register(new Cacheable() {
					@Override
					public CacheDetail reloadCache(){
						CacheDetailProvider cacheDetail = new CacheDetailProvider();
						cacheDetail.setName("RADIUS-DICTIONARY");
						cacheDetail.setSource(getServerHome() + File.separator + "dictionary" + File.separator + "radius");
						
						try {
							loadRadiusDictionary();
							cacheDetail.setResultCode(CacheConstants.SUCCESS);
							LogManager.getLogger().debug(getName(), "Reloaded radius dictionary cache successfully");
						} catch (Exception ex) {
							cacheDetail.setResultCode(CacheConstants.FAIL);
							LogManager.getLogger().error(getName(), ex.getMessage());
							LogManager.getLogger().trace(getName(), ex);
						}	
						return cacheDetail;
					}

					@Override
					public String getName() {							
						return "RADIUS-DICTIONARY";
					}
				});
				
				this.cacheContainer.register(new Cacheable() {
					
					@Override
					public CacheDetail reloadCache() {
						CacheDetailProvider cacheDetail = new CacheDetailProvider();
						cacheDetail.setName("DIAMETER-DICTIONARY");
						cacheDetail.setSource(getServerHome() + File.separator + "dictionary" + File.separator + "diameter");
						
						try {
							loadDiameterDictionary();
							cacheDetail.setResultCode(CacheConstants.SUCCESS);
							LogManager.getLogger().debug(getName(), "Reloaded diameter dictionary cache successfully");
						} catch (Exception ex) {
							cacheDetail.setResultCode(CacheConstants.FAIL);
							LogManager.getLogger().error(getName(), "Reload cache failed for diameter dictionary, Reason: " + ex.getMessage());
							LogManager.getLogger().trace(getName(), ex);
						}
						
						return cacheDetail;
					}
					
					@Override
					public String getName() {
						return "DIAMETER-DICTIONARY";
					}
				});
			}

			LogManager.getLogger().info(MODULE, "Scheduling server level internal task (TPSCounter Validator).");			
			TPSManager tpsManager = new TPSManager(60, 60);
			getServerContext().getTaskScheduler().scheduleIntervalBasedTask(tpsManager);

			if (!getServerContext().getServerMajorVersion().equalsIgnoreCase(DEVELOPMENT_VERSION_NO)) {
				LogManager.getLogger().info(MODULE, "Scheduling server level internal task (Expiry Date Validation Task).");
				licenseManager.startLicenseValidationTask();
			}

			//initializing External Scripts Manager
			initExternalScriptsManager();
			radUDPCommunicatorManager.init();
			
			registerRadiusTranslator();
			
			createRadiusSessionsFactory();
			
			initDiameterStack();
		}
		
		writeSysInitDetails();
		
	}

	private void registerRadiusTranslator() {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Registering Radius Translations");
		}
		TranslationAgent.getInstance().registerTranslator(new RadiusTranslator(getServerContext()));
		registerRadiusTranslationMappingPolicyData();
		registerRadiusCopyPacketMappingTranslators();
	}
	
	
	private void registerRadiusCopyPacketMappingTranslators() {

		List<CopyPacketTranslatorPolicyDataImpl>  policyDataList = getServerContext().getServerConfiguration().getCopyPacketTranslationConfiguration().getCopyPacketMappingList();
		if (Collectionz.isNullOrEmpty(policyDataList)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No Copy Packet mapping policy is configured.");
			}
			return;
		}
		
		for (CopyPacketTranslatorPolicyData policyData : policyDataList) {
			if (AAATranslatorConstants.RADIUS_TRANSLATOR.equalsIgnoreCase(policyData.getToTranslatorId())
					&&
				AAATranslatorConstants.RADIUS_TRANSLATOR.equalsIgnoreCase(policyData.getFromTranslatorId())) {

				TranslationAgent.getInstance().registerCopyPacketTranslator(new RadiusCopyPacketTranslator(policyData, getServerContext()));
			}
		}
	}
	
	private void registerRadiusTranslationMappingPolicyData() {
		
		Map<String, TranslatorPolicyData> policyDataMap = getServerContext().getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyDataMap();
		
		if (Maps.isNullOrEmpty(policyDataMap)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No Translation mapping policy is configured.");
			}
			return;
		}
		
		for (TranslatorPolicyData policyData : policyDataMap.values()) {
			
			if( policyData.getFromTranslatorId().equalsIgnoreCase(AAATranslatorConstants.RADIUS_TRANSLATOR)  
					&& policyData.getToTranslatorId().equalsIgnoreCase(AAATranslatorConstants.RADIUS_TRANSLATOR)) {
				try {
					TranslationAgent.getInstance().registerPolicyData(policyData);
				} catch (PolicyDataRegistrationFailedException e) {
					LogManager.getLogger().warn(MODULE, "Translation mapping policy registraton failed for: " 
							+ policyData.getName() + ", Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
	}
	
	private void initDiameterStack() {
		
		if(!serverConfiguration.getDiameterStackConfiguration().isDiameterStackEnabled()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Not Initialising Diameter Stack, Reason: Diameter Stack is not Enabled");
			return; 
		}
		try {
			eliteDiameterStack = new EliteDiameterStack(serverContext, systemAlertManager, createOrGetDiameterPluginManager());
			eliteDiameterStack.initStack();
			adminService.addCliCommand(eliteDiameterStack.getCliCommands());
			diameterDriverManager = new DriverManager(serverConfiguration.getDiameterDriverConfiguration(),
					new DiameterDriverFactory(serverContext));
			diameterDriverManager.init();
			
			registerDiameterCdrFlushHandler();
			
		} catch (StackInitializationFailedException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().error(MODULE, "Error initializing Diameter Stack, Reason: " + e.getMessage());
		}
	}

	private void registerDiameterCdrFlushHandler() {
		CdrFlushCommand.getInstance().registerFlushEventHandler(new FlushEventHandler() {
			
			@Override
			public void onEvent(Predicate<DriverTypes> driverTypePredicate,
					TableFormatter tableFormatter) {
				Collection<DriverConfiguration> diameterDriverConfigurations = getServerContext()
				.getServerConfiguration().getDiameterDriverConfiguration()
				.getDriverConfigurations();
				
				for (DriverConfiguration driverConfiguration : diameterDriverConfigurations) {
					if (driverTypePredicate.apply(driverConfiguration.getDriverType())) {
						String[] result = tryFlush(driverConfiguration, diameterDriverManager);
						tableFormatter.addRecord(result);
					}
				}
			}
		});
	}

	@Override
	protected void reInitServer(){
		LogManager.getLogger().trace(MODULE, "Server Re-Initialization process started");
		reInitLogger();
//		reInitRadUDPCommunicationManager();
//		reInitExternalScriptsManager();
//		reInitConcurrentSessionManager();
		reInitDiameterStack();
//		reInitDiameterDriverManager();
	}

	private void reInitLogger() {
		serverLevelLogger.setLogLevel(serverConfiguration.getLogLevel());
	}

	private void reInitConcurrentSessionManager() {
		for(Entry<String, ConcurrencySessionManager> entry:serverContext.getLocalSessionManagerMap().entrySet()){
			try {
				entry.getValue().reInit();
			} catch (InitializationFailedException e) {
				LogManager.getLogger().warn(MODULE, "Error in re-initializing Concurrent Session Manager: "+entry.getValue().getSmInstanceName()+", Reason: "+e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

	private void reInitExternalScriptsManager() {
		
	}

	private void reInitRadUDPCommunicationManager(){
		try{
			radUDPCommunicatorManager.reInit();
		}catch (InitializationFailedException ex) {
			LogManager.getLogger().warn(MODULE, "Error in re-initializing UDP Communication manager");
			LogManager.getLogger().trace(ex);
		}
	}
	
	
	private void reInitDiameterDriverManager() {
		
		if(diameterDriverManager != null)
			diameterDriverManager.reInit();
	}

	private void reInitDiameterStack() {
		
		if(eliteDiameterStack == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Not Re-Intialising Diameter Stack, Reason: Stack is not Enabled");
			return;
		}
		eliteDiameterStack.reInit();
	}

	private void startServices() throws ImdgInstanceFailedException {
		// Only if the license is valid than start the services else only admin
		// Service will be active.

		
		if (isLicenseValid()) {
// DO NOT CHANGE THE ORDER OF METHODS CALL BELOW
			initKPIService();
			startSNMPAgent();
			EliteAAAServiceExposerManager.getInstance().setServerContext(serverContext);
			startSystemAlertManager();
			
			startRadiusServices();
			startWebService();

			/** start REST Service in EliteAAA
			 * 
			 * currently if 'rest.service.enable' property is enable in misc-config.xml(eliteaaa/system/misc),
			 * then only REST service will be start. In future when configuration driven REST Service will be developed, 
			 * kindly remove this property that time.
			 *  
			 *  */
			
			boolean isRestServiceEnabled = Strings.toBoolean(System.getProperty("rest.service.enable", "true"));
			
			if(isRestServiceEnabled) {
				startAAARestService();
			}
			
			
			/*
			 * In current framework it is mandate that
			 * Before starting Diameter Stack 
			 * everything should be initialized.
			 * 
			 * So, starting Diameter Services, this will register
			 * DIA-NAS, CC and EAP Applications in Diameter Stack
			 * After Application Registration,
			 * Diameter Stack will be started.
			 * 
			 * If Diameter Stack fails to start, 
			 * i.e. any problem is encountered and Stack is not started,
			 * then Diameter Services will be stopped.  
			 */
			startDiameterServices();
			if(startDiameterStack() == false) {
				stopDiameterServices();
			} else {
				registerDiameterServicesForSetCommand();
			}
			/*
			 * Do not change the place of this method as hazelcast data structure can be used by 
			 * above modules for different reasons. 
			 * The other reason behind keeping the below method at this place is,
			 * in case of hazelcast instance failure services depends on it gets stopped. 
			 */
			startHazelcastIMDG();
			startKPIService();
		}

	}

	/**
	 * This starts REST service
	 */
	
	private void startAAARestService() {
		/**
		 * Currently used port from misc-config.xml(eliteaaa/system/misc) file's 'rest.service.port' property, In future need to read configuration from server instance,
		 * based on configured IP and port REST service should be started 
		 */
		int restServicePort = Integer.parseInt(System.getProperty("rest.service.port", "true"));
		
		aaaRestServer = new AAARestServer(getServerContext(), new InetSocketAddress("127.0.0.1", restServicePort));
		aaaRestServer.start();
	}
	
	/**
	 * This stops REST service
	 */
	private void stopAAARestService() {
		
		if(aaaRestServer != null) {
			aaaRestServer.stop();
			LogManager.getLogger().info(MODULE, "Stopped REST Service successfully");
		}
	}

	/*
	 * Above all modules can require hazelcast's data-structures for 
	 * different reasons. Instantiation of hazelcast should be done 
	 * after that which will make all the data structure configured 
	 * by different modules available.
	 * 
	 * If any module configure any hazelcast's data structure after
	 * this method it won't come into effect. 
	 */
	private void startHazelcastIMDG() throws ImdgInstanceFailedException {
		if (hazelcastImdgInstance != null) {
			hazelcastImdgInstance.start();
			hazelcastImdgInstance.initIMGDMIB();
			hazelcastImdgInstance.generatIMDGStats();
		}
	}

	/**
	 * This will call shut down hook for Diameter Services.
	 * Stopping Diameter Service should not interrupt
	 * regular Shut down Process.
	 * This should be used when there is need to stop All Diameter Services Only.
	 * Eg. When Diameter Stack is unable to start.
	 */
	private void stopDiameterServices() {
		for(EliteService service: getServices()){
			
			if(AAAServerConstants.DIA_NAS_SERVICE_ID.equalsIgnoreCase(service.getServiceIdentifier()) 
					|| AAAServerConstants.DIA_EAP_SERVICE_ID.equalsIgnoreCase(service.getServiceIdentifier())
					|| AAAServerConstants.DIA_CC_SERVICE_ID.equalsIgnoreCase(service.getServiceIdentifier())
					|| AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID.equalsIgnoreCase(service.getServiceIdentifier())) {
				LogManager.getLogger().warn(MODULE, "Stopping Service: " + service.getServiceIdentifier() + 
						", Reason: Unable to start Diameter Stack");
				service.setRemark(ServiceRemarks.DIAMETER_STACK_ERROR);
				service.stop();
			}
		}
	}

	private void startDiameterServices() {
		startDiameterNASService();
		startDiameterCCService();
		startDiameterEAPService();
		startTGPPServerService();
	}

	private void startRadiusServices() {
		startRadAuthService();
		startRadAcctService();
		startDynAuthService();
	}

	/**
	 * Maintains the singletonness of RadiusDriverManager
	 */
	private DriverManager createOrGetRadiusDriverManager() {
		if (radiusDriverManager == null) {
			radiusDriverManager = new DriverManager(serverConfiguration.getDriverConfigurationProvider(),
					new RadiusDriverFactory(serverContext));
			radiusDriverManager.init();
			
			registerRadiusCdrFlushHandler();
		}
		return radiusDriverManager;
	}

	private RadPluginManager createOrGetRadiusPluginManager() {
		if (radiusPluginManager == null) {
			radiusPluginManager = new RadPluginManager(serverContext, serverConfiguration.getPluginConfiguration());
			radiusPluginManager.init();
		}
		return radiusPluginManager;
	}
	
	private DiameterPluginManager createOrGetDiameterPluginManager() {
		if (diameterPluginManager == null) {
			diameterPluginManager = new DiameterPluginManager(serverConfiguration.getPluginConfiguration());
			diameterPluginManager.init();
		}
		return diameterPluginManager;
	}

	private void registerRadiusCdrFlushHandler() {
		CdrFlushCommand.getInstance().registerFlushEventHandler(new FlushEventHandler() {

			@Override
			public void onEvent(Predicate<DriverTypes> driverTypePredicate,
					TableFormatter tableFormatter) {
				Collection<DriverConfiguration> driverConfigurations = getServerContext()
				.getServerConfiguration().getDriverConfigurationProvider()
				.getDriverConfigurations();

				for (DriverConfiguration driverConfiguration : driverConfigurations) {
					if (driverTypePredicate.apply(driverConfiguration.getDriverType())) {
						String[] result = tryFlush(driverConfiguration, radiusDriverManager);
						tableFormatter.addRecord(result);
					}
				}
			}
		});
	}
	
	private void initKPIService() {

		if(serverConfiguration == null) {
			LogManager.getLogger().error(MODULE, "cannot init KPIService, Reason: server configuration is not found");
			return;
		}

		if(!isKPIServiceEnabled()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Datasource for KPISservice is not configured. So, will not be initialized.");
			}
			return;
		}

		boolean isLicenseValid = serverContext.isLicenseValid(LicenseNameConstants.KPI_SERVICE, String.valueOf(System.currentTimeMillis()));
		if (isLicenseValid == false) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, LicenseNameConstants.KPI_SERVICE + " will not start, " +
						" Reason: License for " + LicenseNameConstants.KPI_SERVICE + " is not acquired or invalid.");
			}
			return;
		}
		
		final KpiServiceConfiguration kpiServiceConfiguration = serverConfiguration.getKpiServiceConfiguration();
		
		KpiConfiguration kpiConfig = new KpiConfiguration() {

			@Override
			public long getQueryInterval() {
				return kpiServiceConfiguration.getQueryInterval();
			}

			@Override
			public int getMaxNoOfThreads() {
				return kpiServiceConfiguration.getMaxNoOfThreads();
			}

			@Override
			public long getDumpInterval() {
				return kpiServiceConfiguration.getDumpInterval();
			}

			@Override
			public int getBatchSize() {
				return kpiServiceConfiguration.getBatchSize();
			}

			@Override
			public String getDSName() {
				return kpiServiceConfiguration.getDSName();
			}

		};
		
		try {
		
			final DBConnectionManager connectionManager = DBConnectionManager.getInstance(kpiConfig.getDSName());
			
			DBDataSource dataSource = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(kpiConfig.getDSName());
			connectionManager.init(dataSource, serverContext.getTaskScheduler());

			ConnectionProvider connectionProvider = new ConnectionProvider() {

				@Override
				public Connection getConnection() throws SQLException {
					return connectionManager.getConnection();
				}
			};

			mibSerializer = new DatabaseMIBSerializer(getServerInstanceID(),kpiConfig, connectionProvider); 

			ILogger kpisLogger = new EliteRollingFileLogger.Builder("KPI-Service", 
					getServerHome() + File.separator + "logs" + File.separator + "kpi-service")
					.rollingType(EliteRollingFileLogger.SIZE_BASED_ROLLING_TYPE)
					.rollingUnit(KpiConfiguration.KPI_ROLLING_UNIT_IN_KB)
					.maxRolledUnits(KpiConfiguration.KPI_MAX_ROLLED_UNIT_IN_KB)
					.compressRolledUnits(true)
					.build();
			
			LogManager.setLogger(KpiConfiguration.KPI_THREAD_KEY, kpisLogger);

			mibSerializer.init();
			adminService.addCliCommand(new KpiServiceCommand(mibSerializer, kpiConfig));

		} catch (DatabaseInitializationException e) {
			LogManager.getLogger().error(MODULE, "Unable to initialize datasource for KPIService, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		} catch (DatabaseTypeNotSupportedException e) {
			LogManager.getLogger().error(MODULE, "Unable to create KPI Service, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		} catch (com.elitecore.commons.kpi.exception.InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Unable to initialize KPI Service, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	private void startKPIService() {
		if(isKPIServiceEnabled() && mibSerializer != null) {
			try {
				mibSerializer.start();
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "KPIService started successfully using datasource: " + serverConfiguration.getKpiServiceConfiguration().getDSName());
				}
			} catch (StartupFailedException e) {
				LogManager.getLogger().error(MODULE, "Failed to start KPI Service, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
	
	private boolean isKPIServiceEnabled() {
		return !RadiusUtility.isNullOrEmpty(serverContext.getServerConfiguration().getKpiServiceConfiguration().getDSName());
	}

	private boolean startDiameterStack() {
		
		if(eliteDiameterStack == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Diameter Stack is not enabled, hence will not be started.");
			}
			return false;
		}
		return eliteDiameterStack.start();
	}

	private void startSystemAlertManager(){
		try {
			this.systemAlertManager.initSevice();
			this.systemAlertManager.startService();
		} catch (InitializationFailedException e) {			
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}				
	}

	/**
	 * @throws Exception 
	 * 
	 */
	private void startServer() throws Exception {
		currentState = LifeCycleState.STARTUP_IN_PROGRESS;
		if(isServerShutdownAbnormally())
			LogManager.getLogger().warn(MODULE,"Unexpected state of system resources detected, possible cause of this is abnormal shutdown of system, please ensure proper shutdown through the interfaces provided");
		
		LogManager.getLogger().info(MODULE,"Starting EliteAAA server, home location: " + this.getServerHome());
		LogManager.getLogger().info(MODULE, "JVM: " + readJVMDetails());
		loadServerLicense();
		loadDictionary();
		registerExprLibFunction();
		try{
			readServerConfiguration();
			startAdminService();
			startHtmlAdaptor(snmpAgent.getHttpAdaptorPort());
			registerServerLevelCommandsDependentOnConfiguration();
			createHazelcastImdgInstance();
			initServer();
 			EliteSystemDetail.loadSystemDetail(serverContext);
			startServices();
			writeToStartupInfoFile();
			scheduleStdFileRoller();
			addShutdownHook();
			String startupMessage = formStartupMessageForAlert();
			serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.SERVERUP,MODULE, "EliteAAA server: " + 
						getServerInstanceName() + " started successfully with services: " + startupMessage);
			updateLogLevelToServerLogLevel();
			
			
			
			
			currentState = LifeCycleState.RUNNING;
//		}catch (LoadConfigurationException ex) {
			//TODO stream line all the exceptions from configuration reading code and then un-comment above line 
		}catch (Exception ex) {	
			LogManager.getLogger().trace(ex);
			//unrecoverable configuraiton state. Only admin service and alert service will start
			EliteSystemDetail.loadSystemDetail(serverContext);
			startAdminService();
			startSystemAlertManager();
			writeToStartupInfoFile();
			addShutdownHook();
			currentState = LifeCycleState.RUNNING;
			serverContext.generateSystemAlert(AlertSeverity.CRITICAL,Alerts.SERVERUP,MODULE, "EliteAAA server unable to start as configuration is improper and last good configuration not found or improper");
		}
		
	}

	private void createHazelcastImdgInstance() {
		if (serverConfiguration.getImdgConfigurable() == null) {
			return;
		}
		ImdgConfigData imdgConfigData = serverConfiguration.getImdgConfigurable().getImdgConfigData();
		if (imdgConfigData.isActive()) {
			ClusterGroupData selectedGroupData = selectGroupWithCurrentInstance(imdgConfigData);
			if (selectedGroupData != null) {
				IMDGConfigurationImpl imdgConfiguration = new IMDGConfigurationImpl(imdgConfigData, selectedGroupData);
				hazelcastImdgInstance = new HazelcastImdgInstance(serverContext, imdgConfiguration, serverInstanceName);
			} else {
				LogManager.getLogger().warn(MODULE, "Disabling IMDG, Reason: this instance does not belong to any configured cluster groups");
			}
		}
	}

	private ClusterGroupData selectGroupWithCurrentInstance(
			ImdgConfigData imdgConfigData) {
		List<ClusterGroupData> clusterGroups = imdgConfigData.getClusterGroups();
		
		ClusterGroupData selectedGroupData = null;
		for(int i=clusterGroups.size()-1; i>=0; i--) {
			List<MemberData> memberDatas = clusterGroups.get(i).getMemberDatas();
			for (MemberData data : memberDatas) {
				if (serverInstanceName.equals(data.getName())) {
					selectedGroupData = clusterGroups.get(i);
					break;
				}
			}
		}
		return selectedGroupData;
	}

	/**
	 * ELITEAAA-2614 replace ,the ALL log level to 
	 * server log level configured in Server Configuration.
	 */
	private void updateLogLevelToServerLogLevel() {
		if(isLicenseValid()){
			AAAServerConfiguration serverConfig = getServerContext().getServerConfiguration();
			if (serverConfig != null) {
				String serverLogLevel = serverConfig.getLogLevel();
				LogManager.getLogger().info(MODULE, "Updating server log level to: "+serverConfig.getLogLevel() + " level.");
				EliteRollingFileLogger logger = (EliteRollingFileLogger)serverLevelLogger;
				logger.setLogLevel(serverLogLevel);
			}
		}
	}
	
	private String formStartupMessageForAlert(){
		String startupMessage = "";
		boolean anyServiceStarted = false;
		
		if(snmpAgent != null && EliteSnmpAgent.RUNNING.equalsIgnoreCase(snmpAgent.getState())){
			anyServiceStarted = true;
			startupMessage += "SNMP,";
		}
		
		for(int index = 0 ; index < getServices().size() ; index++){
			EliteService service = getServices().get(index);
			if(service.getStatus().equalsIgnoreCase("Running") || service.getStatus().equalsIgnoreCase("Running**")){				
				anyServiceStarted = true;
				startupMessage+=  service.getServiceIdentifier() + ",";					
			}				
		}

		if(!anyServiceStarted )
			startupMessage += "NONE";
		else
			startupMessage = startupMessage.substring(0,startupMessage.length()-1);

		return startupMessage;
	}

	private void initExternalScriptsManager(){
		ExternalScriptsManager scriptsManager = new ExternalScriptsManager(serverContext,serverConfiguration.getScriptConfigurable().getExternalScript());
		scriptsManager.init();
		this.externalScriptsManager = scriptsManager;
	}
	
	private void registerMBean(BaseMBeanController baseMBeanImpl) {
		try {

			adminService.registerMbean(baseMBeanImpl);

		} catch (InstanceAlreadyExistsException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (MBeanRegistrationException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (NotCompliantMBeanException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (MalformedObjectNameException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (NullPointerException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

	}

	private void registerServerCommands() {
		List<ICommand> serverCommandList = new LinkedList<ICommand>();
		serverCommandList.add(new VersionCommand(getServerContext().getServerName(), getServerContext().getServerVersion()));
		serverCommandList.add(new ShutdownCommand(this));
		serverCommandList.add(new ServerHomeCommand(getServerContext().getServerHome()));
		serverCommandList.add(new ServicesCommand(new ServiceDataProviderImpl(),getServerContext()));
		serverCommandList.add(new RadiusCertificateCommand(getServerHome()));
		serverCommandList.add(new RestartCommand());
	
		serverCommandList.add(new SysInfoCommand());

		serverCommandList.add(new AAALicenseCommand() {
			@Override
			protected String readLicense() {
				return getLicenseKey();
			}

			@Override
			public String getServerHomeLocation() {
				return getServerHome();
			}

			@Override
			public String getPublicKeyFileName() {
				return "aaaserver.pubkey";
			}

			@Override
			public String getModuleName() {
				return "AAALicense";
			}

			@Override
			protected String getInstanceName() {
				return serverInstanceName;
			}

			@Override
			protected String getInstanceId() {
				return serverInstanceID;
			}
			
		});
		
		adminService.addCliCommand(serverCommandList);
		serverCommandList.clear();
	}
	
	private void registerServerLevelCommandsDependentOnConfiguration(){
		if(!isLicenseValid())
			return;
		//this method is used to register commands that require configuration to execute
		List<ICommand> serverCommandList = new LinkedList<ICommand>();
		
		DataSourceCommand dsCommand = new DataSourceCommand();

		try{
			dsCommand.registerDetailProvider(new DBDetailProvider() {

				@Override
				protected Map<String, DBDataSource> getDBDatasourceMap() {
					return serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasourceNameMap();
				}
			});

		}catch(RegistrationFailedException e){
			LogManager.getLogger().error(MODULE, "Failed to register DB Detail Provider in datasource command. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		try{
			dsCommand.registerDetailProvider(new LDAPDetailProvider() {
				
				@Override
				protected Map<String, LDAPDataSource> getLDAPDatasourceMap() {
					return serverContext.getServerConfiguration().getLDAPDSConfiguration().getDatasourceNameMap();
				}
			});
			
		}catch(RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register LDAP Detail Provider in datasource command. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		try {
			dsCommand.registerDetailProvider(new ReInitDetailProvider(serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasourceNameMap()));
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register re-init detail provider. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		serverCommandList.add(dsCommand);
		serverCommandList.add(new ESIStatisticsCommand());
		serverCommandList.add(new SessionMgrCommand(serverContext));
		serverCommandList.add(CdrFlushCommand.getInstance());
		serverCommandList.add(new RadClientCommand());
		serverCommandList.add(new ReloadCacheCommand());
		serverCommandList.add(new ClientsCommand(serverContext));
		serverCommandList.add(new ESIScanCommand());
		serverCommandList.add(new ClearCommand());
		serverCommandList.add(new AlertCommand() {
			
			@Override
			public IAlertData getSystemAlertData(String alertId) {
				return systemAlertManager.getAlertData(alertId);
			}
		});
		SetCommand setCommand = new SetCommand(serverContext);
		ServerConfigurationSetter serverConfigurationSetter = new ServerConfigurationSetter();
		SetCommand.registerConfigurationSetter(serverConfigurationSetter);
		serverCommandList.add(setCommand);
		serverCommandList.add(new ESISummaryCommand(serverContext));
		ShowCommand showCommand=new ShowCommand();
		try {	
			SessionDetailProvider.getInstance(serverContext).registerDetailProvider(EapSessionDetailProvider.getInstance(serverContext));
			SessionDetailProvider.getInstance(serverContext).registerDetailProvider(WimaxSessionDetailProvider.getInstance(serverContext));
			ShowCommand.registerDetailProvider(SessionDetailProvider.getInstance(serverContext));
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Session Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		
		try {	
			SubscriberDetailProvider.getInstance().registerDetailProvider(DriverProfileProvider.getInstance());
			SubscriberDetailProvider.getInstance().registerDetailProvider(PolicyProfileProvider.getInstance());
			ShowCommand.registerDetailProvider(SubscriberDetailProvider.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Session Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		try {	
			ShowCommand.registerDetailProvider(MiscellaneousConfigDetailProvider.getInstance(serverContext));
			ShowCommand.registerDetailProvider(PluginDetailProvider.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Miscellaneous Configuration Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		serverCommandList.add(showCommand);

		
		serverCommandList.add(new LogMonitorCommandExt());
		
		serverCommandList.add(new ReloadConfigurationCommand());

		//Added Config command
		ConfigCommand configCommand = new ConfigCommand(serverContext);
		configCommand.init(serverConfiguration);		
		serverCommandList.add(configCommand);
		
		try {
			ClearCommand.registerDetailProvider(ClearSessionCommand.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().trace(e);
		}
		
		//submitting the command list to the admin service 
		adminService.addCliCommand(serverCommandList);
	}
	
	private class AAAServerContextImpl extends BaseServerContext implements AAAServerContext {

		private EAPSessionManager eapSessionManager = new EAPSessionManager();
		private Map<String,ConcurrencySessionManager> localSessionManagerMap = new HashMap<String, ConcurrencySessionManager>();

		private PluginDetail pluginDetail = new PluginDetail();
		
		@Override
		public AAAServerConfiguration getServerConfiguration() {
			return serverConfiguration;
		}

		public ILogger getLogger() {
			return serverLevelLogger;
		}

		@Override
		public String getServerName() {
			return EliteAAAServer.this.getServerName();
		}

		@Override
		public String getServerVersion() {
			return Version.getVersion();
		}

		@Override
		public String getServerMajorVersion() {
			return Version.getMajorVersion();
		}

		@Override
		public String getServerDescription() {
			return "";
		}

		@Override
		public void generateSystemAlert(AlertSeverity severity,IAlertEnum alert, String alertGeneratorIdentity, String alertMessage) {
			generateSystemAlert(severity.name(), alert, alertGeneratorIdentity, alertMessage);
		}
		
		@Override
		public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage) {
			systemAlertManager.scheduleAlert(alertEnum, getAAAServerInstanceId() +"/"+ alertGeneratorIdentity,severity, alertMessage);
		}
		
		@Override
		public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue) {
			generateSystemAlert(severity.name(), alertEnum, alertGeneratorIdentity, alertMessage, alertIntValue, alertStringValue);
		}
		
		@Override
		public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage,
				int alertIntValue, String alertStringValue) {
			systemAlertManager.scheduleAlert(alertEnum, getAAAServerInstanceId() +"/"+ alertGeneratorIdentity, severity, alertMessage, alertIntValue, alertStringValue);
		}


		@Override
		public EAPSessionManager getEapSessionManager() {
			return eapSessionManager;
		}
		
		@Override
		public KeyManager getKeyManager() {
			return keyManager;
		}

		@Override
		public WimaxSessionManager getWimaxSessionManager() {
			return wimaxSessionManager;
		}

		@Override
		public RadUDPCommunicatorManager getRadUDPCommunicatorManager() {
			return radUDPCommunicatorManager;
		}

		@Override
		public boolean isLicenseValid(String key, String value) {
			return validateLicenses(key, value);
		}
		
		@Override
		public Map<String,ConcurrencySessionManager> getLocalSessionManagerMap(){
			return localSessionManagerMap;
		}
		
		@Override
		public long getTPSCounter(){
			long elapsedTimeInMillis = System.currentTimeMillis() - lastResetTime;
			if (elapsedTimeInMillis == 0) {
				// have to be defensive about / 0
				return 0;
			}
			return (long)Math.round(((float)tpsCounter.get() / elapsedTimeInMillis) * 1000);
		}

		@Override
		public void incrementTPSCounter(){
			tpsCounter.incrementAndGet();
		}

		@Override
		public Optional<ConcurrencySessionManager> getLocalSessionManager(String sessionManagerId) {
			if (sessionManagerId == null) {
				return Optional.absent();
			}
			
			ConcurrencySessionManager concurrencySessionManager = this.localSessionManagerMap.get(sessionManagerId);
			if (concurrencySessionManager == null) {
				
				SessionManagerData sessionManagerData = serverConfiguration.getSessionManagerConfiguration().getSessionManagerConfigById(sessionManagerId);

				if (SessionManagerData.TYPE_LOCAL.equals(sessionManagerData.getType()) == false) {
					throw new IllegalArgumentException("Session manager: " 
							+ sessionManagerData.getInstanceName() 
							+ ", is not a local session manager");
				}

				concurrencySessionManager = new CounterAwareConcurrencySessionManager(snmpAgent.incrementAndGetLocalSMIndex(),getServerContext(),(LocalSessionManagerData) sessionManagerData);
				try {
					concurrencySessionManager.init();
					this.localSessionManagerMap.put(sessionManagerId, concurrencySessionManager);
				} catch (InitializationFailedException e) {
					getLogger().warn(MODULE, "Session Manager initialization failed for Session manager: " + concurrencySessionManager.getSmInstanceName() + ", Reason : " + e.getMessage());
					getLogger().trace(MODULE,e);
					return Optional.absent();
				}
			}			

			return Optional.of(concurrencySessionManager);
		}

		@Override
		public long getAAAServerUPTime() {
			return aaaServerUPTime;
		}

		public String getAAAServerInstanceId() {
			return aaaServerInstanceId;
		}

		@Override
		public boolean isServerStartedWithLastConf() {
			return bServerStartedWithLastConf;
		}

		@Override
		public ExternalScriptsManager getExternalScriptsManager() {
			return externalScriptsManager;
		}

		@Override
		public String getReleaseDate() {
			return Version.getReleaseDate();
		}
		
		@Override
		public String getSVNRevision() {
			return Version.getSVNRevision();
		}

		@Override
		public long getServerStartUpTime() {
			return aaaServerUPTime;
		}

		@Override
		public String getLocalHostName() {
			return EliteAAAServer.this.getLocalHostName();
		}

		@Override
		public String getContact() {
			return "support@eliteaaa.com";
		}

		@Override
		public void registerCacheable(Cacheable cacheable) {
			EliteAAAServer.this.cacheContainer.register(cacheable);
		}

		@Override
		public void registerMBean(BaseMBeanController baseMBeanImpl) {
			EliteAAAServer.this.registerMBean(baseMBeanImpl);
		}

		@Override
		public AAAConfigurationState getConfigurationState() {
			return configurationContext.state();
		}

		@Override
		public void sendSnmpTrap(SystemAlert alert, SnmpAlertProcessor alertProcessor) {
			snmpAgent.sendTrap(alert, alertProcessor);
		}

		@Override
		public ESCommunicator getDiameterDriver(String driverInstanceId) {
			if (diameterDriverManager != null) {
				return diameterDriverManager.getDriver(driverInstanceId);
			}
			return null;
		}
		
		@Override
		public void registerSnmpMib(SnmpMib snmpMib) {
			snmpAgent.registerMib(snmpMib);
			if (isKPIServiceEnabled() && mibSerializer != null) {
				try {
					mibSerializer.registerMib(snmpMib);
				} catch (com.elitecore.commons.kpi.exception.RegistrationFailedException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Failed to register SNMP MIB: " + snmpMib.getMibName() + ", Reason: " + e.getMessage());
					}
					LogManager.getLogger().trace(e);
				}
			}
		}

		@Override
		public VirtualInputStream registerVirtualPeer(PeerDataImpl peerData,
				VirtualOutputStream virtualOutputStream) throws ElementRegistrationFailedException {
			if (eliteDiameterStack == null) {
				throw new ElementRegistrationFailedException("Diameter Stack is not enabled.");
			}
			return eliteDiameterStack.registerVirtualPeer(peerData, virtualOutputStream).getInputStream();
		}
		
		@Override
		public void registerPriorityRoutingEntry(RoutingEntryData entryData) throws ElementRegistrationFailedException {
			if (eliteDiameterStack == null) {
				throw new ElementRegistrationFailedException("Diameter Stack is not enabled.");
			}
			eliteDiameterStack.registerPriorityRoutingEntry(entryData);
		} 

		@Override
		public EliteAAASNMPAgent getSNMPAgent() {
			return EliteAAAServer.this.snmpAgent;
		}



		@Override
		public long addTotalResponseTime(long responseTimeInNano) {
			totalResponseTimeInNano.addAndGet(responseTimeInNano);
			return (tpsCounter.get() == 0) ? 0 : ((totalResponseTimeInNano.get() / NANO_TO_MILLI) / tpsCounter.get());
		}

		@Override
		@Nonnegative public int addAndGetAverageRequestCount(@Nonnegative int value) {
			long elapsedTimeInMillis = System.currentTimeMillis() - lastResetTime;
			if (elapsedTimeInMillis == 0) {
				return 0;
			}
			
			return Math.round(((float)totalRequestCount.addAndGet(value) / elapsedTimeInMillis) * 1000);
		}

		@Override
		public PluginDetail getPluginDetail() {
			return pluginDetail;
		}

		@Override
		public void registerPlugins(List<PluginEntryDetail> names) {
			this.pluginDetail.addPlugins(names);
		}

		@Override
		public DiameterPluginManager getDiameterPluginManager() {
			return diameterPluginManager;
		}
		
		@Override
		public HazelcastImdgInstance getHazelcastImdgInstance() {
			return hazelcastImdgInstance;
		}
		
		@Override
		public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
				String alertMessage, Map<com.elitecore.core.serverx.alert.Alerts,Object> alertData) {
			generateSystemAlert(severity.name(), alertEnum, alertGeneratorIdentity, alertMessage,  alertData);

			}
		
		private void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
				String alertMessage, Map<com.elitecore.core.serverx.alert.Alerts,Object> alertData) {
			systemAlertManager.scheduleAlert(alertEnum, getAAAServerInstanceId() +"/"+ alertGeneratorIdentity,severity, alertMessage, alertData);
		}

		@Override
		public boolean hasRadiusSession(String sessionId) {
			return radiusSessionFactory.hasSession(sessionId);
		}

		@Override
		public ISession getOrCreateRadiusSession(String sessionId) {
			return radiusSessionFactory.getOrCreateSession(sessionId);
		}

		@Override
		public Set<String> search(String attributeName, String attributeValue) {
			return radiusSessionFactory.search(attributeName, attributeValue);
		}

		@Override
		public RadiusESIGroupFactory getRadiusESIGroupFactory() {
			return radiusEsiGroupFactory;
		}

	}

	@Override
	public AAAServerContext getServerContext() {
		return serverContext;
	}

	// -------------------------------------------------------------------------
	//
	// EliteAAA startup method
	//
	// -------------------------------------------------------------------------

	public static void main(String args[]) throws Exception{
		startUPThread = Thread.currentThread();
		
		String strServerHome = System.getenv(SERVER_HOME);
		if (strServerHome != null) {
			if(strServerHome.trim().equals("..")){
				File serverHome = new File(strServerHome);
				try {
					strServerHome = serverHome.getCanonicalPath();
				} catch (IOException e) {
					System.out.println("ELITEAAA_HOME not valid : " + strServerHome);
				}
			}
			EliteAAAServer aaaServer = new EliteAAAServer(strServerHome);
			
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			aaaServer.aaaServerUPTime = runtimeMXBean.getStartTime();
			
			aaaServer.startServer();
			try {
				startUPThread.join();
			} catch(Exception e) {
				
			}
			
		} else {
			System.out.println("ELITEAAA_HOME not defined");
		}
	}
	
	private void startAdminService() {
		LogManager.getLogger().debug(MODULE, "Starting EliteAAA admin service");
		adminService = new EliteAdminService(serverContext);
		
		try {
			adminService.init();
			boolean serviceStarted = adminService.start();
			if (serviceStarted) {
				LogManager.getLogger().info(MODULE,"Admin service started sucessfully");
			} else {
				LogManager.getLogger().error(MODULE,"Error in starting Admin service");
			}
		} catch (ServiceInitializationException e) {
			LogManager.getLogger().warn(MODULE, "Error during initializing Admin service: Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		OnlineReportController onlineReportController = new OnlineReportController();
		OfflineReportController offlineReportController = new OfflineReportController();

		registerMBean(offlineReportController);
		registerMBean(onlineReportController);
		registerMBean(new AAALicenseController());
		registerMBean(new AAAClientController(serverContext));
		registerMBean(new AAASupportedRFCController());
		registerMBean(new ConfigurationDetailController());
		registerMBean(new SystemDetailController());

		liveServerSummary.setName(getServerContext().getServerName());
		liveServerSummary.setVersion(getServerContext().getServerVersion());
		liveServerSummary.setServerStartupTime(new Date());

		EliteAAAController eliteAAAController = new EliteAAAController(serverContext, smConfigurationSynchronizer);
		eliteAAAController.registerConfigurations();
		registerMBean(eliteAAAController);
		
		if (serverConfiguration != null && serverConfiguration.getMiscellaneousConfigurable() != null) {
			registerMBean(new MiscellaneousConfigMbeanImpl(serverContext));
		}

		registerServerCommands();
	}

	private void stopAdminService(){
		adminService.stopService();
	}

	// PRIVATE start service methods

	private void startRadAuthService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RAD_AUTH_SERVICE_ID)) {
			RadAuthService radiusAuthService = new RadAuthService(serverContext, 
					createOrGetRadiusDriverManager(),
					createOrGetRadiusPluginManager(),
					createRadiusLogMonitor());
			
			LicensedService licensedService = new LicensedService(serverContext, radiusAuthService, LicenseNameConstants.AUTHENTICATION_MODULE);
			
			if (registerService(licensedService)) {
				adminService.addCliCommand(radiusAuthService.getCliCommands());
			}

			RadUserAccountController radiusUserAccountController = new RadUserAccountController(serverContext);
			radiusUserAccountController.readAllEntities();
			registerMBean(radiusUserAccountController);
			
		}
	}

	//PRIVATE start service methods

	/**
	 * Maintains the singletonness of RadiusLogMonitor
	 */
	private RadiusLogMonitor createRadiusLogMonitor() {
		if (radiusLogMonitor == null) {
			radiusLogMonitor = new RadiusLogMonitor(serverContext.getTaskScheduler());
			try {
				LogMonitorManager.getInstance().registerMonitor(radiusLogMonitor);
			} catch (RegistrationFailedException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		return radiusLogMonitor;
	}

	private void startDynAuthService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RAD_DYNAUTH_SERVICE_ID)) {
			RadDynAuthService radiusDynAuthService = new RadDynAuthService(serverContext,
					createOrGetRadiusDriverManager(),
					createOrGetRadiusPluginManager(),
					createRadiusLogMonitor());
			
			LicensedService licensedService = new LicensedService(serverContext, radiusDynAuthService, LicenseNameConstants.DYNA_AUTH_MODULE);
			
			if (registerService(licensedService)) {
				adminService.addCliCommand(radiusDynAuthService.getCliCommands());
			}
		}
	}
	private void startWebService() {
		if(serverConfiguration.getWebServiceConfiguration().getIsEnabled()){
			start_HTTP_WebService();
			start_HTTPS_WebService();
		}	
	}
	

	private void startDiameterEAPService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.DIA_EAP_SERVICE_ID)) {
			if(eliteDiameterStack == null){
				LogManager.getLogger().error(MODULE, "Unable to Start Diameter EAP Applicaion, " +
						"Reason: Diameter Stack is not enabled");
				return;
			}
			
			DiameterServiceConfigurationDetail serviceConfigurationDetail = serverConfiguration.getDiameterServiceConfiguration(
					AAAServerConstants.DIA_EAP_SERVICE_ID);
					
			DiameterService eapService = new DiameterService(serverContext, AAAServerConstants.DIA_EAP_SERVICE_ID, 
					this.eliteDiameterStack, 
					serviceConfigurationDetail, serviceConfigurationDetail.getKey());
			
			LicensedService licensedService = new LicensedService(serverContext, eapService, LicenseNameConstants.DIAMETER_EAP_MODULE); 
			
			if (registerService(licensedService)) {
				adminService.addCliCommand(eapService.getCliCommands());
			}
		}		
	}

	private void startDiameterCCService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.DIA_CC_SERVICE_ID)) {
			if(eliteDiameterStack == null){
				LogManager.getLogger().error(MODULE, "Unable to Start Diameter CC Applicaion, " +
						"Reason: Diameter Stack is not enabled");
				return;
			}
			
			DiameterServiceConfigurationDetail serviceConfigurationDetail = serverConfiguration.getDiameterServiceConfiguration(
					AAAServerConstants.DIA_CC_SERVICE_ID);
			
			DiameterService ccService = new DiameterService(serverContext, AAAServerConstants.DIA_CC_SERVICE_ID, 
					this.eliteDiameterStack, 
					serviceConfigurationDetail,serviceConfigurationDetail.getKey());
			
			LicensedService licensedService = new LicensedService(serverContext, ccService, LicenseNameConstants.DIAMETER_CC_MODULE);
			
			if (registerService(licensedService)) {
				adminService.addCliCommand(ccService.getCliCommands());
			}
		}		
	}

	private void startDiameterNASService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.DIA_NAS_SERVICE_ID)) {
			if(eliteDiameterStack == null){
				LogManager.getLogger().error(MODULE, "Unable to Start Diameter NAS Applicaion, " +
						"Reason: Diameter Stack is not enabled");
				return;
			}
			
			DiameterServiceConfigurationDetail serviceConfigurationDetail = serverConfiguration.getDiameterServiceConfiguration(
					AAAServerConstants.DIA_NAS_SERVICE_ID);
			
			DiameterService nasService = new DiameterService(serverContext, AAAServerConstants.DIA_NAS_SERVICE_ID, 
					this.eliteDiameterStack,
					serviceConfigurationDetail,serviceConfigurationDetail.getKey());
		
			LicensedService licensedService = new LicensedService(serverContext, nasService, LicenseNameConstants.DIAMETER_NAS_MODULE);
			
			if (registerService(licensedService)) {
				adminService.addCliCommand(nasService.getCliCommands());
			}
		}
	}
	
	private void startTGPPServerService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID) == false) {
			return;
		}
		
		if (eliteDiameterStack == null) {
			LogManager.getLogger().error(MODULE, "Unable to Start Diameter TGPP Server, " +
					"Reason: Diameter Stack is not enabled");
			return;
		}
		
		DiameterServiceConfigurationDetail serviceConfigurationDetail = serverConfiguration.getDiameterServiceConfiguration(
				AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID);
		
		DiameterService tgppServerService = new DiameterService(serverContext, AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID, 
				this.eliteDiameterStack,
				serviceConfigurationDetail, serviceConfigurationDetail.getKey());
		
		LicensedService licensedService = new LicensedService(serverContext, tgppServerService, LicenseNameConstants.TGPP_AAA_SERVER);
		
		if (registerService(licensedService)) {
			adminService.addCliCommand(tgppServerService.getCliCommands());
		}
	}

	// PRIVATE start service methods

	private void start_HTTP_WebService() {
		WebServiceConfiguration webServiceConfiguration = serverConfiguration.getWebServiceConfiguration();
		EliteWebServiceServer eliteWebServiceServer = new EliteWebServiceServer(serverContext,webServiceConfiguration.getIpAddress(),webServiceConfiguration.getPort(),webServiceConfiguration.getThreadPoolSize(),webServiceConfiguration.getMaxSession());
		
		LicensedService licensedService = new LicensedService(serverContext, eliteWebServiceServer, LicenseNameConstants.WEB_SERVICE);
		
		if (registerService(licensedService)) { 
			EliteAAAServiceExposerManager.getInstance().init();
			adminService.addCliCommand(eliteWebServiceServer.getCliCommands());
		}
	}

	private void start_HTTPS_WebService() {
	WebServiceConfiguration webServiceConfiguration = serverConfiguration.getWebServiceConfiguration();
		if(webServiceConfiguration.getHttpsServiceAddress()!=null && webServiceConfiguration.getHttpsServiceAddress().length() > 0){
			EliteWebServiceServer eliteWebServiceServer = new HTTPSEliteWebServiceServer(serverContext,webServiceConfiguration.getHttpsIPAddress(),webServiceConfiguration.getHttpsPort(),webServiceConfiguration.getThreadPoolSize(),webServiceConfiguration.getMaxSession(), webServiceConfiguration.getServerCertificateProfileId());
			
			LicensedService licensedService = new LicensedService(serverContext, eliteWebServiceServer, LicenseNameConstants.WEB_SERVICE);
			
			if (registerService(licensedService)) {
				EliteAAAServiceExposerManager.getInstance().init();
				adminService.addCliCommand(eliteWebServiceServer.getCliCommands());
			}
		}	
	}

	private void startRadAcctService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RAD_ACCT_SERVICE_ID)) {
			RadAcctService radiusAcctService = new RadAcctService(serverContext,
					createOrGetRadiusDriverManager(),
					createOrGetRadiusPluginManager(),
					createRadiusLogMonitor());
			
			LicensedService licensedService = new LicensedService(serverContext, radiusAcctService, LicenseNameConstants.ACCOUNTING_MODULE);
			
			if (registerService(licensedService)) {
				adminService.addCliCommand(radiusAcctService.getCliCommands());
			}
		}
	}


	/**
	 * This method will initialize snmpAgent which uses jdmk
	 */
	private void startSNMPAgent() {
		try {
			boolean isLicenseValid = getServerContext().isLicenseValid(LicenseNameConstants.SNMP_AGENT, String.valueOf(System.currentTimeMillis()));
			
			if (isLicenseValid == false) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, LicenseNameConstants.SNMP_AGENT + 
							" will not start, Reason: License for "+LicenseNameConstants.SNMP_AGENT+" is not acquired or invalid.");
				}
				return;
			}
			
			snmpAgent = new EliteAAASNMPAgent(getServerContext(),AAAServerConstants.AAA_ENTERPRISE_OID,
					serverConfiguration.getSNMPAddress(),serverConfiguration.getSNMPPort());

			snmpAgent.init();
			
			/***
			* EliteSystemDetail should be initialized before service startup. 
			*/
			snmpAgent.loadRFC1213Mib(EliteSystemDetail.getDescription(), AAAServerConstants.AAA_ENTERPRISE_OID,
									EliteSystemDetail.getStartUpTime(), EliteSystemDetail.getContact(),
									EliteSystemDetail.getHostName(),EliteSystemDetail.getLocation());
			
			if(isKPIServiceEnabled() && mibSerializer != null) {
				initAndRegisterJVMMib();
			}
			SnmpImpl snmpimpl = snmpAgent.getSnmpGroup();
			SNMPServCommand snmpCommand = new SNMPServCommand(snmpimpl);
			adminService.addCliCommand(snmpCommand);
			
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in initializing SNMP Agent. Reason:" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			snmpAgent = new NullSnmpAgent(getServerContext(), AAAServerConstants.AAA_ENTERPRISE_OID,
					serverConfiguration.getSNMPAddress(),serverConfiguration.getSNMPPort());
		}
	}

	@Override
	protected void stopServer() {
		LogManager.getLogger().info(MODULE, "Stop server operation started");
		stopDiameterDriverManager();
		stopDiameterStack();
		stopRadiusDriverManager();
		stopWimaxSessionManager();
		super.stopServer();
		if (hazelcastImdgInstance != null) {
			hazelcastImdgInstance.stop();
		}
		EliteAAAServiceExposerManager.getInstance().stop();
		stopRadUDPCommunicatorManager();
		systemAlertManager.generateAlert(AlertSeverity.INFO.name(),Alerts.SERVERDOWN, MODULE, "EliteAAA server: " + 
						getServerInstanceName() + " shutdown successfully");
		snmpAgent.stop();
		stopKPIService();
		stopAdminService();
		writeToShutdownInfoFile();
		
		/** Stop AAA REST Service */
		stopAAARestService();
		
		LogManager.getLogger().info(MODULE, "EliteAAA server shutdown successfully");
		startUPThread.interrupt();
	}

	private void stopWimaxSessionManager() {
		if (this.wimaxSessionManager != null) {
			this.wimaxSessionManager.stop();
		}
	}

	private void stopRadUDPCommunicatorManager() {
		radUDPCommunicatorManager.shutdown();
		while (radUDPCommunicatorManager.isShutdown() == false) {
			LogManager.getLogger().info(MODULE, "Waiting for UDP Communicator to shutdown");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
	
	@Override
	protected final void cleanupResources() {
		super.cleanupResources();
		LogManager.getLogger().info(MODULE, "Cleaning up resources for server: " + getServerName());
		
		if (eliteDiameterStack != null) {
			eliteDiameterStack.cleanupResources();
		}
		
		Closeables.closeQuietly(serverLevelLogger);
		//Do not log anything here as the server level logger is closed
	}
	
	private void stopKPIService() {
		if (mibSerializer != null) {
			mibSerializer.stop();
		}
	}

	private void stopDiameterStack() {
		if (eliteDiameterStack != null) {
			eliteDiameterStack.stop();
		}
	}

	private void stopDiameterDriverManager() {
		if (diameterDriverManager != null) {
			diameterDriverManager.stop();
		}
	}
	
	private void stopRadiusDriverManager() {
		if (radiusDriverManager != null) {
			radiusDriverManager.stop();
		}
	}

	public class ServerConfigurationSetter implements ConfigurationSetter{

		private static final String REALTIME = "realtime";
		@Override
		public String execute(String... parameters) {
			if(parameters[1].equalsIgnoreCase("log")){
				if(parameters.length >= 3){
					if(serverLevelLogger instanceof EliteRollingFileLogger){
						EliteRollingFileLogger logger = serverLevelLogger;
						if (logger.isValidLogLevel(parameters[2]) == false) {
							return "Invalid log level: " + parameters[2];
						}					
						logger.setLogLevel(parameters[2]);
						return "Configuration Changed Successfully";

				}
			}
			}
			return getHelpMsg();
		}

		@Override
		public boolean isEligible(String... parameters) {
			if(parameters.length == 0){
				return false;
			}
			if(!parameters[0].equalsIgnoreCase("server")){
				return false;
			}
			if(parameters[1].equalsIgnoreCase("log")){
				return true;
			}else if(parameters[1].equalsIgnoreCase("-help")){
				return true;				
			}
			return false;
		}

		@Override
		public String getHelpMsg() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : set server [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the AAA Server. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVER_TYPE;
		}

	}

	private class ServiceDataProviderImpl implements ServiceDataProvider {
		@Override
		public List<ServiceDescription> getServiceDataList() {
			ArrayList<ServiceDescription> serviceList = new ArrayList<ServiceDescription>();

			for (EliteService service : getServices()) {
				serviceList.add(service.getDescription());
			}
			
			Date date = snmpAgent.getStartDate();
			ServiceDescription serviceData = 
					new ServiceDescription("SNMP", snmpAgent.getState(), ServiceRemarks.INVALID_LICENSE.remark.equals(snmpAgent.getRemarks()) ? "---" : snmpAgent.getIPAddress() + ":" + snmpAgent.getPort(),
							date, snmpAgent.getRemarks());
			serviceList.add(serviceData);

			if(eliteDiameterStack != null) {
				serviceData = new ServiceDescription(AAAServerConstants.DIAMETER_STACK, 
						eliteDiameterStack.getStatusMessage(), 
						eliteDiameterStack.getSocketDetail() == null ? "---" : eliteDiameterStack.getSocketDetail().toString(),
								Parameter.getInstance().getStackUpTime(),
								eliteDiameterStack.getRemarks());
				serviceList.add(serviceData);
			}
			return serviceList;
		}
	}

	@Override
	public String getLicenseValues(String key) {
		if (!getServerContext().getServerMajorVersion().equalsIgnoreCase(DEVELOPMENT_VERSION_NO))
			return licenseManager.getLicenseValue(key);

		return null;
	}

	@Override
	public boolean validateLicenses(String key, String value) {
		if (!getServerContext().getServerMajorVersion().equalsIgnoreCase(DEVELOPMENT_VERSION_NO)) {
			boolean bLicense = licenseManager.validateLicense(key, value);
			if(!bLicense) {
				if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_EXPIRY)) {
					getServerContext().generateSystemAlert(AlertSeverity.ERROR,Alerts.LICENSE_EXPIRED, MODULE, 
							"License for server instance is expired, Licensed: " + getLicenseValues(key) +", Actual: " + value ,0,
							"EliteAAA server(Licensed: " + getLicenseValues(key) +", Actual: " + value + ")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_CPU)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_CPU_EXCEEDED, MODULE, "Invalid license , number of available processor available is not matching with licensed number (licensed: " +getLicenseValues(LicenseNameConstants.SYSTEM_CPU)+ ", available: "+ SystemUtil.getAvailableProcessor()+")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_CLIENTS)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_CLIENT_EXCEEDED, MODULE, 
							"Invalid license for Max Supported Client, Licensed: " + getLicenseValues(key) + ", Actual: " + value,0,
							"(Licensed: " + getLicenseValues(key) + ", Actual: " + value + ")" );
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_CONCURRENT_SESSION)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_CONCURRENT_USER_EXCEEDED, MODULE, 
							"Invalid License for Max Councurrent User Supported, Licensed: " + getLicenseValues(key) + ", Actual: " + value, 0,
							"(Licensed: " + getLicenseValues(key) + ", Actual: " + value + ")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_TPS)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_TPS_EXCEEDED, MODULE, 
							"Invalid License for Max TPS Supported, Licensed: " + getLicenseValues(key) + ", Actual: " + value,
							Integer.parseInt(value), "Licensed: " + getLicenseValues(key));
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_VENDOR_TYPE)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.NOT_LICENSED_VENDOR, MODULE, 
							"Invalid License for Vendor Client, Licensed: " + getLicenseValues(key) + ", Actual: " + value, 0,
							"(Licensed: " + getLicenseValues(key) + ", Actual: " + value + ")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_SUPPORTED_VENDOR)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.NOT_LICENSED_SUPPORTED_VENDOR, MODULE, 
							"Invalid License for Supported Vendor, Licensed: " + getLicenseValues(key) + " Actual: " + value, 0,
							"(Licensed: " + getLicenseValues(key) + ", Actual: " + value + ")");
				}
			}
			return bLicense;
		}else
			return true;
	}

	public class ReloadCacheCommand extends EliteBaseCommand {
		private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		@Override
		public synchronized String execute(String parameter) {
			String result = "";			
			if("?".equals(parameter) || "-help".equalsIgnoreCase(parameter)){
				result = getHelp(); 
			}else if("-d".equalsIgnoreCase(parameter)){			
				StringBuilder sb = new StringBuilder();		
				Map<String,CacheStatistics> cacheStatisticsMap= cacheManager.getDetailedStatistics();
				sb.append(fillChar("", 185, '-')+"\n");
				sb.append(fillChar("Display Name", 35) + "|"
						+ fillChar("Source" , 95) + "|"
						+ fillChar("Last Cached Updated Time", 25) + "|"  
						+ fillChar("Last Reload Attempt Time",25) + "\n"); 

				sb.append(fillChar("", 185, '-')+"\n");		
				if(cacheStatisticsMap.size() > 0){
					for(Entry< String, CacheStatistics> entry : cacheStatisticsMap.entrySet()){
						CacheStatistics cacheStatistics = entry.getValue();						
						sb.append(fillChar(cacheStatistics.getName(),35) + "|");
						sb.append(fillChar(cacheStatistics.getSource(),95) + "|");
						String date = "";
						if(cacheStatistics.getLastSuccessfulRefreshDate() != null){
							date = formatter.format(cacheStatistics.getLastSuccessfulRefreshDate());
						}else{
							date = "NOT CACHED";
						}
						sb.append(fillChar(date,25) + "|");
						if(cacheStatistics.getLastRefreshDate() != null){
							date = formatter.format(cacheStatistics.getLastRefreshDate());
						}else{
							date = "NOT CACHED";
						}
						sb.append(fillChar(date,25)+ "\n");

					}					
				}else{
					sb.append(fillChar("Reload cache not called yet", 185, ' ')+"\n");
				}

				sb.append(fillChar("", 185, '-')+"\n");
				result = sb.toString();				
			}else{
				reloadServerCache();
				result =  "Server cache for "+getServerName()+" is successfully reloaded.\n" + cacheManager.getCacheStatistics();
			}
			return result;
		}

		@Override
		public String getCommandName() {			
			return "rcache";
		}

		@Override
		public String getDescription() {			
			return "Reload Cache for server instance";
		}

		@Override
		public String getHotkeyHelp() {			
			return "{'rcache':{'-help':{}}}";
		}

		private String getHelp(){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage       : " + getCommandName() + "[option]");
			out.println("Options ");
			out.println("   -d  	 : " + "Shows details of cache statistics" );
			out.println("Description : " + getDescription() );
			return stringWriter.toString();
		}

	}


	public class ReloadConfigurationCommand extends EliteBaseCommand {

		@Override
		public synchronized String execute(String parameter) {
			String responseMessage = "";
			if(parameter!=null && parameter.length() > 0){
				responseMessage = getHelp();
				return responseMessage;
			}else{
				if(configurationContext.state() != AAAConfigurationState.NORMAL){
					return "Server configuration cannot be reloaded, as running in Last Good Configuration mode";
				}
				
				if (reloadConfiguration()) {
					reInit();
					liveServerSummary.setConfigurationReloadTime(new Date());
					return "Configurations reloaded successfully";
				} else {
					return "Reload configuration failed";
				}
			}
		}

		@Override
		public String getCommandName() {
			return "rconf";
		}

		@Override
		public String getDescription() {
			return "Reload Configurations";
		}
		private String getHelp(){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : " + getCommandName() );
			out.println("Description : " + getDescription() );
			return stringWriter.toString();
		}
		@Override
		public String getHotkeyHelp() {
			return "{'rconf':{'-help':{}}}";
		}
	}
	
	@Override
	public boolean reloadServerConfiguration() {
		try {
			AAAServerConfiguration aaaServerConfiguration = (AAAServerConfiguration) configurationContext.reload(serverConfiguration);
			LogManager.getLogger().info(MODULE, "Server configuration reloaded successfully");
			this.serverConfiguration = (AAAServerConfigurationImpl)aaaServerConfiguration;
			return true;
		} catch (LoadConfigurationException e) {
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}
	}
	public final LiveServerSummary getLiveServerSummary() {
		//liveServerSummary.setInternalTasks(getInternalTaskData());
		liveServerSummary.setId(serverContext.getServerConfiguration().getKey());
		ServiceDataProviderImpl serviceDataProviderImpl = new ServiceDataProviderImpl();
		List<ServiceDescription> serviceDataList = serviceDataProviderImpl.getServiceDataList();
		List<LiveServiceSummary> liveServiceSummaries = new ArrayList<LiveServiceSummary>();
		final int len = serviceDataList.size();
		for(int i=0;i<len;i++){
			LiveServiceSummary serviceSummary = new LiveServiceSummary();

			serviceSummary.setInstanceName(serviceDataList.get(i).getName());
			//serviceSummary.setInstanceId(serviceDataList.get(i).getServiceID());
			//serviceSummary.setNextExecutionTime(new Date(serviceDataList.get(i).getNextExecutionTime()));
			serviceSummary.setServiceStartupTime(serviceDataList.get(i).getStartDate());			
			serviceSummary.setStatus(serviceDataList.get(i).getStatus());			
			serviceSummary.setRemarks(serviceDataList.get(i).getRemarks());		
			serviceSummary.setSocketAddress(serviceDataList.get(i).getSocketAddress());

			liveServiceSummaries.add(serviceSummary);

		}
		liveServerSummary.setServiceSummaryList(liveServiceSummaries);
		return liveServerSummary;
	}
	public interface EliteAAAControllerMBean {

		/* Attributes */
		public String getVersionInformation();
		public String getServerState();

		/* Methods */
		public String serverHome();

		public String javaHome();

		public boolean shutdownServer();

		public String readServerInstanceId();

		public EliteNetServerDetails readServerDetails();

		public EliteNetServerData readServerConfiguration();

		public boolean reloadServerConfiguration();

		public List<Map<String,String>> retriveReloadedCacheDetails();

		public ServerReloadResponse reloadCache();

		public void writeServerInstanceDetails(String serverID, String serverName);

		public void updateServerConfiguration(EliteNetServerData eliteNetServerData, String version) throws Exception;

		public List<String> updateDictionary(List<EliteDictionaryData> eliteDictionaryList) throws Exception;

		public  Map<String,Object> retriveServerLogFileList() throws Exception;

		public  Map<String,Object> retriveServiceLogFileList() throws Exception;

		public  Map<String,Object> retriveCDRFileList() throws Exception;

		public  byte[] retriveFileData(String file, Integer offset) throws Exception;
		
		public void writeWebServiceDetail(String userName, String password, String address, Integer port, String contextPath) throws MBeanException;
		

	}

	public interface OnlineReportControllerMBean {

		public List<Long[]> retrieveLastUpdatedData(String graph,
				String sessionId);
	}

	public interface OfflineReportControllerMBean {
		public List<Long[]> retrieveLastUpdatedData(String graph,
				Date startDate, Date endDate);

	}
	private void reloadServerCache(){
		cacheManager.reload();	
		liveServerSummary.setCacheReloadTime(new Date());
	}

	public interface AAALicenseControllerMBean{
		public String readLicensePublicKey() throws Exception;
		public void saveLicense(String fileData) throws Exception;
		public String readLicense();
		public void deregisterLicense();

	}

	public class EliteAAAController extends BaseServerController implements
	EliteAAAControllerMBean {

		private static final String MODULE = "AAA Controller";

		public EliteAAAController(ServerContext serverContext, SMConfigurationSynchronizer smConfigurationSynchronizer) {
			super(serverContext, smConfigurationSynchronizer);
		}

		@Override
		public String serverHome() {
			return EliteAAAServer.this.getServerHome();
		}

		@Override
		public String javaHome(){
			return System.getenv("JAVA_HOME");
		}


		@Override
		protected Map<String,List<Class<? extends Configurable>>> getServiceConfigurationClasses() {

			Map<String,List<Class<? extends Configurable>>> serviceConfigList = new HashMap<String,List<Class<? extends Configurable>>>();

			List<Class<? extends Configurable>> authServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			authServiceConfClassList.add(RadAuthServiceConfigurable.class);
			authServiceConfClassList.add(UniversalAuthPluginCofigurable.class);
			serviceConfigList.put(AAAServerConstants.RAD_AUTH_SERVICE_ID, authServiceConfClassList);

			List<Class<? extends Configurable>> acctServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			acctServiceConfClassList.add(RadAcctServiceConfigurable.class);
			acctServiceConfClassList.add(UniversalAcctPluginConfigurable.class);
			serviceConfigList.put(AAAServerConstants.RAD_ACCT_SERVICE_ID, acctServiceConfClassList);

			List<Class<? extends Configurable>> dynAuthServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			dynAuthServiceConfClassList.add(RadDynAuthServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.RAD_DYNAUTH_SERVICE_ID, dynAuthServiceConfClassList);

			List<Class<? extends Configurable>> nasServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			nasServiceConfClassList.add(DiameterNasServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.DIA_NAS_SERVICE_ID, nasServiceConfClassList);

			List<Class<? extends Configurable>> eapServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			eapServiceConfClassList.add(DiameterEAPServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.DIA_EAP_SERVICE_ID, eapServiceConfClassList);

			List<Class<? extends Configurable>> ccServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			ccServiceConfClassList.add(DiameterCCServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.DIA_CC_SERVICE_ID, ccServiceConfClassList);


			List<Class<? extends Configurable>> tgppServerConfClassList = new ArrayList<Class<? extends Configurable>>();
			tgppServerConfClassList.add(DiameterTGPPServerConfigurable.class);
			serviceConfigList.put(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID, tgppServerConfClassList);

			return serviceConfigList;
		}

		@Override
		protected List<Class<? extends Configurable>> getServerConfigurationClasses() {
			List<Class<? extends Configurable>> confClassList = new ArrayList<Class<? extends Configurable>>();
			confClassList.add(AAAServerConfigurable.class);
			confClassList.add(AAAAlertConfigurable.class);
			confClassList.add(ClientsConfigurable.class);
			confClassList.add(RadHotlineConfigurable.class);
			confClassList.add(RadClientPolicyConfigurable.class);
			confClassList.add(DHCPKeysConfigurable.class);
			confClassList.add(WimaxConfigurable.class);
			confClassList.add(VSAInClassConfigurable.class);
			confClassList.add(AAAPluginConfManagerConfigurable.class);
			confClassList.add(DiameterStackConfigurable.class);
			return confClassList;
		}

		@Override
		protected Map<String, List<Class<? extends Configurable>>> getPluginConfigurationClasses()  {
			Map<String, List<Class<? extends Configurable>>> pluginConfigList = new HashMap<String, List<Class<? extends Configurable>>>();

			List<PluginInfo> pluginList = new ArrayList<PluginInfo>();
			
			if(isLicenseValid()){
				if(serverConfiguration!=null && serverConfiguration.getPluginManagerConfiguration()!=null){
					AAAPluginConfManager aaaPluginManager =  serverConfiguration.getPluginManagerConfiguration();
					if(!Collectionz.isNullOrEmpty(aaaPluginManager.getRadPluginInfoList())){
						pluginList.addAll(aaaPluginManager.getRadPluginInfoList());
					}
					if(!Collectionz.isNullOrEmpty(aaaPluginManager.getDiameterPluginInfoList())){
						pluginList.addAll(aaaPluginManager.getDiameterPluginInfoList());
					}
				}
			}else{
				//read plugin configuration irrespective of license failure for synchronization from SM
				try {
					AAAPluginConfManagerConfigurable aaaPluginManagerConfigurable = (AAAPluginConfManagerConfigurable) configurationContext.read(AAAPluginConfManagerConfigurable.class);
					
					if(!Collectionz.isNullOrEmpty(aaaPluginManagerConfigurable.getRadPluginInfoList())){
						pluginList.addAll(aaaPluginManagerConfigurable.getRadPluginInfoList());
					}
					if(!Collectionz.isNullOrEmpty(aaaPluginManagerConfigurable.getDiameterPluginInfoList())){
						pluginList.addAll(aaaPluginManagerConfigurable.getDiameterPluginInfoList());
					}
				} catch (LoadConfigurationException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Error in reading system mapping configuration for plugins. Plugins will not be synchronized. Reason: " + e.getMessage());
						LogManager.getLogger().trace(e);
					}
				}
			}

			if(!pluginList.isEmpty()){
				for (Iterator<PluginInfo> iterator = pluginList.iterator(); iterator.hasNext();) {
					PluginInfo pluginInfo = iterator.next();
					List<Class<? extends Configurable>> pluginConfClassList = new ArrayList<Class<? extends Configurable>>();
					if(pluginInfo.getPluginConfClass()!=null){
						try {
							if(pluginInfo.getPluginConfClass() != null && pluginInfo.getPluginConfClass().trim().length() >0){
								pluginConfClassList.add(createConfigurationObject(pluginInfo.getPluginConfClass(),pluginInfo.getPluginName()).getClass());
								pluginConfigList.put(pluginInfo.getPluginName(), pluginConfClassList);
							}
						} catch (SecurityException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						} catch (IllegalArgumentException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						} catch (ClassNotFoundException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						} catch (NoSuchMethodException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						} catch (InstantiationException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()
									+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						} catch (IllegalAccessException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						} catch (InvocationTargetException e) {
							LogManager.getLogger().warn(MODULE,"Configuration Class: " + pluginInfo.getPluginConfClass()+ " could not be loaded");
							LogManager.getLogger().debug(MODULE, e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						}
						pluginConfigList.put(pluginInfo.getPluginName(), pluginConfClassList);
					}
					LogManager.getLogger().info(MODULE, "Name: "+pluginInfo.getPluginName()+","+pluginInfo.getPluginConfClass());
				}
			}
			return pluginConfigList;
		}
		@Override
		public boolean reloadServerConfiguration() {			
			if(reloadConfiguration()){
				reInit();
				liveServerSummary.setConfigurationReloadTime(new Date());
				return true;
			}else{
				return false;
			}
		}

		@Override
		public ServerReloadResponse reloadCache() {
			LogManager.getLogger().debug(MODULE, "Going to reload cache for "+getServerName());
			ServerReloadResponse serverReloadResponse = new ServerReloadResponse();
			try{
				reloadServerCache();
				serverReloadResponse.setConfigurationName(getServerName());
				serverReloadResponse.setReload(true);
				serverReloadResponse.setErrorMessage("Server cache for "+getServerName()+" is successfully reloaded.\n" + cacheManager.getCacheStatistics());
			}catch(Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Loading of cache failed for "+getServerName()+" ,reason: "+e.getMessage());
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, e);
				serverReloadResponse.setErrorMessage("Loading of cache failed for "+getServerName()+" ,reason: "+e.getMessage());
			}	    	
			return serverReloadResponse;
		}

		@Override
		public EliteNetServerDetails readServerDetails() {
			EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
			LiveServerSummary liveServerSummary = getLiveServerSummary();

			if(liveServerSummary.getCacheReloadTime() != null)
				eliteServerDetails.setCacheReloadTime(new Timestamp(liveServerSummary.getCacheReloadTime().getTime()));
			eliteServerDetails.setIdentification(liveServerSummary.getId());
			eliteServerDetails.setName(liveServerSummary.getName());
			eliteServerDetails.setVersion(liveServerSummary.getVersion());
			eliteServerDetails.setIdentification(liveServerSummary.getId());
			if(liveServerSummary.getConfigurationReloadTime() != null)
				eliteServerDetails.setServerReloadTime(new Timestamp(liveServerSummary.getConfigurationReloadTime().getTime()));

			if(liveServerSummary.getServerStartupTime() != null)
				eliteServerDetails.setServerStartUpTime(new Timestamp(liveServerSummary.getServerStartupTime().getTime()));

			if(liveServerSummary.getSoftRestartTime() != null)
				eliteServerDetails.setSoftRestartTime(new Timestamp(liveServerSummary.getSoftRestartTime().getTime()));
			eliteServerDetails.setServiceSummaryList(liveServerSummary.getServiceSummaryList());
			eliteServerDetails.setInternalTasks(liveServerSummary.getInternalTasks());

			return eliteServerDetails;
		}

		@Override
		public boolean shutdownServer() {
			LogManager.getLogger().info(MODULE, "Stop server signal received");
			shutdown();
			return true;
		}

		@Override
		public List<Map<String, String>> retriveReloadedCacheDetails() {			
			return cacheManager.getRegisteredCacheObjectList();
		}

		@Override
		public String getModuleName() {
			return MODULE;
		}

		@Override
		protected String getServerKey() {
			return getServerContext().getServerConfiguration().getKey();
		}

		@Override
		public String getSystemPath() {
			return SYSTEM_PATH;
		}

		@Override
		public Map<String, Object> retriveServiceLogFileList() throws Exception {


			Map<String, Object> fileMap = new LinkedHashMap<String,Object>();
			if(serverConfiguration.getAuthConfiguration()!=null && serverConfiguration.getAuthConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getAuthConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			if(serverConfiguration.getAcctConfiguration()!=null && serverConfiguration.getAcctConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getAcctConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			if(serverConfiguration.getDynAuthConfiguration()!=null && serverConfiguration.getDynAuthConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getDynAuthConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			
			if(serverConfiguration.getDiameterStackConfiguration()!=null && serverConfiguration.getDiameterStackConfiguration().getLogger().getIsLoggerEnabled()){
				String location = serverConfiguration.getDiameterStackConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}


			return fileMap;

		}


		@Override
		public Map<String, Object> retriveCDRFileList() throws Exception {
			Map<String, Object> fileMap = new LinkedHashMap<String,Object>();
			Collection<DriverConfiguration> driverConfigurations =  
				serverConfiguration.getDriverConfigurationProvider().getDriverConfigurations();

			cdrFilesFromDriverConfiguration(driverConfigurations, fileMap);
			
			if (serverConfiguration.getDiameterDriverConfiguration() != null) {
				cdrFilesFromDriverConfiguration(serverConfiguration.getDiameterDriverConfiguration().getDriverConfigurations(),
						fileMap);
			}

			return fileMap;
		}
		
		private void cdrFilesFromDriverConfiguration(Collection<DriverConfiguration> driverConfigurations, 
				Map<String, Object> fileMap) throws Exception {
			
			if (Collectionz.isNullOrEmpty(driverConfigurations)) {
				return;
			}
			
			for (DriverConfiguration driverConfiguration : driverConfigurations) {
				if (driverConfiguration.getDriverType() == DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER) {
					ClassicCSVAcctDriverConfiguration    classicCSVAcctConfiguration = (ClassicCSVAcctDriverConfiguration)driverConfiguration;
					String location = classicCSVAcctConfiguration.getFileLocation();
					fillDirectoryFiles(location, fileMap);
				}
				
				if (driverConfiguration.getDriverType() == DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER) {
					DetailLocalAcctDriverConfiguration    detailLocalAcctConfiguration = (DetailLocalAcctDriverConfiguration)driverConfiguration;
					String location = detailLocalAcctConfiguration.getFileLocation();
					fillDirectoryFiles(location, fileMap);
				}

				if (driverConfiguration.getDriverType() == DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER) {
					ClassicCSVAcctDriverConfiguration    classicCSVAcctConfiguration = (ClassicCSVAcctDriverConfiguration)driverConfiguration;
					String location = classicCSVAcctConfiguration.getFileLocation();
					fillDirectoryFiles(location, fileMap);
				}

				if (driverConfiguration.getDriverType() == DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER) {
					DetailLocalAcctDriverConfiguration    detailLocalAcctConfiguration = (DetailLocalAcctDriverConfiguration)driverConfiguration;
					String location = detailLocalAcctConfiguration.getFileLocation();
					fillDirectoryFiles(location, fileMap);
				}
			}
		}

		@Override
		public String getServerState() {
			return currentState.message;
		}
}

	public class OnlineReportController extends BaseMBeanController implements
	OnlineReportControllerMBean {

		private RadAuthService radAuthService;
		private RadAcctService radAcctService;

		@Override
		public synchronized List<Long[]> retrieveLastUpdatedData(String graph,
				String sessionId) {
			List<Long[]> data = null;

			if ("AUTHRESPONSETIME".equals(graph) || "AUTHERRORS".equals(graph) || "AUTHSUMMARY".equals(graph)|| "AUTHREJECTREASONS".equals(graph)) {

				synchronized (this) {
					if (radAuthService == null) {
						radAuthService = getRadAuthServiceObject();
					}
				}

				if (radAuthService != null) {
					if ("AUTHRESPONSETIME".equals(graph)) {
						data = radAuthService.retrieveAuthResponsTimeData(sessionId);
					} else if ("AUTHERRORS".equals(graph)) {
						data = radAuthService.retrieveAuthErrorsData(sessionId);
					} else if ("AUTHSUMMARY".equals(graph)) {
						data = radAuthService.retrieveAuthSummaryData(sessionId);
					} else if ("AUTHREJECTREASONS".equals(graph)) {
						data = radAuthService.retrieveAuthRejectReasonsData(sessionId);
					}
				}
			}

			if ("ACCTRESPONSETIME".equals(graph) || "ACCTERRORS".equals(graph)|| "ACCTSUMMARY".equals(graph)) {
				synchronized (this) {
					if (radAcctService == null) {
						radAcctService = getRadAcctServiceObject();
					}
				}

				if (radAcctService != null) {
					if ("ACCTRESPONSETIME".equals(graph)) {
						data = radAcctService.retrieveAcctResponsTimeData(sessionId);
					} else if ("ACCTERRORS".equals(graph)) {
						data = radAcctService.retrieveAcctErrorsData(sessionId);
					} else if ("ACCTSUMMARY".equals(graph)) {
						data = radAcctService.retrieveAcctSummaryData(sessionId);
					}
				}
			}

			return data;
		}

		@Override
		public String getName() {
			return MBeanConstants.ONLINE_REPORT;
		}

	}

	public class AAALicenseController extends BaseMBeanController implements AAALicenseControllerMBean{

		private static final String MODULE = "AAA-LICENSE-CONTROLLER";

		@Override
		public String getName() {
			return MBeanConstants.LICENSE;
		}

		@Override
		public String readLicensePublicKey() throws Exception {
			ElitePublickeyGenerator elitePublickeyGen = new ElitePublickeyGenerator();
			return elitePublickeyGen.generatePublicKey(getServerHome(),LicenseConstants.DEFAULT_ADDITIONAL_KEY, serverInstanceID, serverInstanceName);
		}
		@Override
		public void saveLicense(String fileData) throws Exception{
			saveLicense(fileData, LicenseConstants.LICENSE_FILE_NAME+ LicenseConstants.LICESE_FILE_EXT);
		}

		public void saveLicense(String fileData, String fileName)
		throws Exception {
			String SERVER_HOME = getServerHome();

			if(SERVER_HOME == null ||fileData == null || fileName == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Problem uploading license, reason: required Parameter not found");
				throw new Exception("Required Parameter not found");
			}

			byte[] licenseFileBytes = null;  

			try{
				licenseFileBytes = fileData.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				licenseFileBytes = fileData.getBytes();
			}
			FileOutputStream fileOutputStream = null;

			try{
				File licenseDir = new File(SERVER_HOME + File.separator + LicenseConstants.LICENSE_DIRECTORY);
				File licenseFile = new File(SERVER_HOME + File.separator + LicenseConstants.LICENSE_DIRECTORY + File.separator + fileName);

				if (!licenseDir.exists())
					licenseDir.mkdir();

				if (licenseFile.exists())
					licenseFile.delete();

				licenseFile.createNewFile();
				fileOutputStream = new FileOutputStream(licenseFile);
				fileOutputStream.write(licenseFileBytes);


			}catch (IOException e) {
				throw e;
			}
			finally {
				try {
					if (fileOutputStream != null)
						fileOutputStream.close();
				}catch (Exception e) {
					fileOutputStream = null;
				}
			}

		}

		@Override
		public String readLicense() {
			return getLicenseKey();
		}

		@Override
		public void deregisterLicense() {
			LogManager.getLogger().info(MODULE, "Received command to deregister license. All Services will be stopped and license will be purged.");
			serverContext.generateSystemAlert(AlertSeverity.INFO, Alerts.NFV_LICENSE_DEREGISTERED, MODULE, 
					"Received Command to deregister license. License will be deregistered and services will be stopped.");
			licenseManager.removeLicenseFile();
			stopServices();
			
		}

	}

	public class OfflineReportController extends BaseMBeanController implements
	OfflineReportControllerMBean {

		private RadAuthService radAuthService;
		private RadAcctService radAcctService;

		@Override
		public List<Long[]> retrieveLastUpdatedData(String graph,
				Date startDate, Date endDate) {
			List<Long[]> data = null;

			try {

				if ("AUTHRESPONSETIME".equals(graph) || "AUTHERRORS".equals(graph) || "AUTHSUMMARY".equals(graph) || "AUTHREJECTREASONS".equals(graph)) {

					synchronized (this) {
						if (radAuthService == null) {
							radAuthService = getRadAuthServiceObject();
						}
					}

					if (radAuthService != null) {
						data = radAuthService.retrieveAuthResponsTimeData(startDate, endDate);
						if ("AUTHRESPONSETIME".equals(graph)) {
						} else if ("AUTHERRORS".equals(graph)) {
							data = radAuthService.retrieveAuthErrorsData(startDate, endDate);
						} else if ("AUTHSUMMARY".equals(graph)) {
							data = radAuthService.retrieveAuthSummaryData(startDate, endDate);
						} else if ("AUTHREJECTREASONS".equals(graph)) {
							data = radAuthService.retrieveAuthRejectReasonsData(startDate,endDate);
						}
					}
				}

				if ("ACCTRESPONSETIME".equals(graph) || "ACCTERRORS".equals(graph)|| "ACCTSUMMARY".equals(graph)) {
					synchronized (this) {
						if (radAcctService == null) {
							radAcctService = getRadAcctServiceObject();
						}
					}

					if (radAcctService != null) {
						if ("ACCTRESPONSETIME".equals(graph)) {
							data = radAcctService.retrieveAcctResponsTimeData(startDate, endDate);
						} else if ("ACCTERRORS".equals(graph)) {
							data = radAcctService.retrieveAcctErrorsData(startDate, endDate);
						} else if ("ACCTSUMMARY".equals(graph)) {
							data = radAcctService.retrieveAcctSummaryData(startDate, endDate);
						}
					}
				}

			} catch (Exception e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE,"Error while fetching data for [" + graph + "]:"+ e.getMessage());

			}
			return data;

		}

		@Override
		public String getName() {
			return MBeanConstants.OFFLINE_REPORT;
		}

	}

	private void setServerLogParameters() {
		LogManager.getLogger().info(MODULE, "Updating server log parameters, log level = " + LogLevel.ALL
				+ ", rolling type = " + serverConfiguration.getLogRollingType() + ", rolling unit = "
				+ serverConfiguration.getLogRollingUnit() + ", max rolled units = "
				+ serverConfiguration.getLogMaxRolledUnits() + ", compress rolled units = "
				+ serverConfiguration.isCompressLogRolledUnits());

		serverLevelLogger.close();

		serverLevelLogger = new EliteRollingFileLogger.Builder(getServerInstanceName(),
				getServerHome() + File.separator + "logs" + File.separator + "eliteaaa-server")
				.rollingType(serverConfiguration.getLogRollingType())
				.rollingUnit(serverConfiguration.getLogRollingUnit())
				.maxRolledUnits(serverConfiguration.getLogMaxRolledUnits())
				.compressRolledUnits(serverConfiguration.isCompressLogRolledUnits())
				//.sysLogParameters(serverConfiguration.getSysLogConfiguration().getHostIp(), serverConfiguration.getSysLogConfiguration().getFacility())
				.build();
		
		LogManager.setDefaultLogger(serverLevelLogger);

	}

	private RadAuthService getRadAuthServiceObject() {
		return (RadAuthService) getServiceObject("RAD-AUTH");
	}

	private RadAcctService getRadAcctServiceObject() {
		return (RadAcctService) getServiceObject("RAD-ACCT");
	}

	private RadDynAuthService getRadDynAuthServiceObject() {
		return (RadDynAuthService) getServiceObject("RAD-DYNAUTH");
	}

	private EliteService getServiceObject(String identifier) {
		List<EliteService> serviceList = getServices();
		if (serviceList != null && !serviceList.isEmpty()) {
			int serviceListSize = serviceList.size();
			for (int index = 0; index < serviceListSize; index++) {
				EliteService service = serviceList.get(index);
				if (service.getServiceIdentifier().equalsIgnoreCase(identifier)) {
					return service;
				}
			}
		}
		return null;
	}
	
	/**
	 *   This class will run to see that within the scheduled time slot, 
	 *   whether the TPS Counter has exceeded than License issued. 
	 *   Whenever the task executes, the TPS Counter is reset to 0. 
	 **/

	protected class TPSManager extends BaseIntervalBasedTask {

		private static final String MODULE = "TPS Counter Manager";
		private long initialDelay;
		private long intervalSeconds;

		public TPSManager(long initialDelay, long intervalSeconds) {		
			this.initialDelay = initialDelay;
			this.intervalSeconds = intervalSeconds;
			EliteAAAServer.this.lastResetTime = System.currentTimeMillis();
		}

		@Override
		public long getInitialDelay() {
			return initialDelay;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public long getInterval() {
			return intervalSeconds;
		}


		@Override
		public void execute(AsyncTaskContext context) {
			long currentTPS = getServerContext().getTPSCounter();
			
			boolean licenseValid = getServerContext().isLicenseValid("SYSTEM_TPS", String.valueOf(currentTPS));
			
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn("RAD-SERV", "For last 1 minute: Average MPS = " + getServerContext().addAndGetAverageRequestCount(0)
						+ ", Average TPS  = " + currentTPS
						+ ", License Valid = " + licenseValid + ", Average request processing time = " 
						+ getServerContext().addTotalResponseTime(0) + "ms");
			}
			
			if (licenseValid == false) {
				
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Request per second on server exceeds recommended limit, "
							+ "please consider upgrading the license");
				}
			}
			
			resetCounters();
		}

		private void resetCounters() {
			tpsCounter.set(0);
			totalResponseTimeInNano.set(0);
			totalRequestCount.set(0);
			lastResetTime = System.currentTimeMillis();
		}
	}
	
	@Override
	@Nullable 
	public String getServerInstanceID() {
		if (serverInstanceID != null) {
			return serverInstanceID;
		}

		String encryptedInstanceId = readServerInstanceId();

		if (Strings.isNullOrEmpty(encryptedInstanceId)) {
			return null;
		} else {
			serverInstanceID = decryptedServerInstanceId(encryptedInstanceId);
			return serverInstanceID;
		}
	}

	private String decryptedServerInstanceId(String encryptedInstanceId) {
		try {
			return PasswordEncryption.getInstance().decrypt(encryptedInstanceId, PasswordEncryption.ELITECRYPT);
		} catch (NoSuchEncryptionException e) {          // NOSONAR - Exception handlers should preserve the original exceptions
			LogManager.getLogger().warn(MODULE,"Problem reading server instance id, reason: " + e.getMessage());
		} catch (DecryptionNotSupportedException e) {	// NOSONAR - Exception handlers should preserve the original exceptions
			LogManager.getLogger().warn(MODULE,"Problem reading server instance id, reason: " + e.getMessage());
		} catch (DecryptionFailedException e) {
			LogManager.getLogger().warn(MODULE,"Problem reading server instance id, reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}

	@Override
	public String getServerInstanceName() {
		if(serverInstanceName != null)
			return serverInstanceName;
		
		try {
			serverInstanceName = PasswordEncryption.getInstance().decrypt(readServerInstanceName(), PasswordEncryption.ELITECRYPT);
			
		} catch (NoSuchEncryptionException e) {		// NOSONAR - Exception handlers should preserve the original exceptions
			//getLogger().trace(e);
			LogManager.getLogger().warn(MODULE,"Problem reading server instance name, reason: " + e.getMessage());
		} catch (DecryptionNotSupportedException e) {		// NOSONAR - Exception handlers should preserve the original exceptions
			//getLogger().trace(e);
			LogManager.getLogger().warn(MODULE,"Problem reading server instance name, reason: " + e.getMessage());
		} catch (DecryptionFailedException e) {
			LogManager.getLogger().warn(MODULE,"Problem reading server instance name, reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		if(serverInstanceName == null || serverInstanceName.trim().length() == 0){
			serverInstanceName = null;
			return getLocalHostName();
		}
		
		return serverInstanceName;
	}
	private Configurable createConfigurationObject(String confClassName,String pluginName)
	
	throws ClassNotFoundException, SecurityException,
	NoSuchMethodException, IllegalArgumentException,
	InstantiationException, IllegalAccessException,
	InvocationTargetException {
		Class<?> classParameters[];

		Constructor<?> constructor = null;
		Configurable pluginConfigurationObj = null;

		classParameters = new Class[1];
		classParameters[0] = String.class;

		Class<? extends Configurable> c = Class.forName(confClassName).asSubclass(Configurable.class);
		constructor = c.getConstructor();
		Object obj[] = new Object[1];
		obj[0] = pluginName;

		pluginConfigurationObj = (Configurable) constructor.newInstance();

		return pluginConfigurationObj;
	}

	@Override
	protected String getStdLogFileName() {
		return "eliteaaa-std";
	}
	
	private void registerExprLibFunction(){
		try{
			Compiler compiler = Compiler.getDefaultCompiler();
			compiler.addFunction(new FunctionDBLookup(new ConnectionProviderImpl()));
			compiler.addFunction(new FunctionBaseValue());
			compiler.addFunction(new FunctionDBSession());
			compiler.addFunction(new FunctionDBSession.FunctionWhere());
			
		}catch(NullPointerException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Fail to register function DBLookup, Reason: "+ex.toString());
				LogManager.getLogger().trace(ex);
			}
		}
	}
	
	private void initAndRegisterJVMMib(){
		try {
			JVM_MANAGEMENT_MIBImpl jvmMIB = new JVM_MANAGEMENT_MIBImpl();
			jvmMIB.init();
			serverContext.getTaskScheduler().scheduleIntervalBasedTask(new JVMThreadStatisticsProvider(jvmMIB));
			JvmMemoryStatisticsProvider jvmMemoryStatisticsProvider = new JvmMemoryStatisticsProvider(jvmMIB);
			jvmMemoryStatisticsProvider.addMemoryStatistics();
			JvmRunTimeStatisticsProvider jvmRuntimeStatisticsProvider = new JvmRunTimeStatisticsProvider(jvmMIB);
			jvmRuntimeStatisticsProvider.addJvmRunTimeStatistics();
			serverContext.registerSnmpMib(jvmMIB);
		} catch (IllegalAccessException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to Initialize the JVM Mib.");
			}
			LogManager.getLogger().trace(e);
		}
	}
	public class RestartCommand extends EliteBaseCommand {
		private static final String MODULE = "RESTART";
		private static final String RESTART_SCRIPT = "restart.sh";
		private static final String PID_SEPERATOR = "@";
		
		private final String SHELL = File.separator + "bin" + File.separator + "sh";
		private final String serverHome;
		private final String bin;
		
		private AtomicBoolean restartCalled;
		
		private String[] command = new String[3];
		
		public RestartCommand() {
			this.serverHome = EliteAAAServer.this.getServerHome();
			this.bin = serverHome + File.separator + "bin" + File.separator;
			command[0] = SHELL;
			command[1] = bin + RESTART_SCRIPT;
			restartCalled = new AtomicBoolean(false);
		}
		
		@Override
		public String execute(String parameter) {
			if (restartCalled.get()) {
				return LifeCycleState.RESTART_IN_PROGRESS.message;
			}
			
			if (Strings.isNullOrBlank(parameter)){
				File subProcessDir = new File(bin);
				if (new File(bin + RESTART_SCRIPT).exists() == false) {
					LogManager.getLogger().warn(MODULE, "Unable to execute restart command, Reason: restart script missing from " + bin);
					return "Unable to restart, Reason: Restart script missing from " + bin;
				} else if (new File(bin + RESTART_SCRIPT).canRead() == false ) {
					LogManager.getLogger().warn(MODULE, "Unable to execute restart command, Reason: " + bin + RESTART_SCRIPT 
							+ ": Permission Denied. Requires read access");
					return "Unable to execute restart command, Reason: " + bin + RESTART_SCRIPT +
							": Permission Denied. Requires read access";
				}
				command[2] = ManagementFactory.getRuntimeMXBean().getName().split(PID_SEPERATOR)[0];
				try {
					Runtime.getRuntime().exec(command,null,subProcessDir);
				} catch (IOException e) {
					LogManager.getLogger().trace(e);
					return "Unable to restart, Reason : " + e.getMessage() ;
				}
				EliteAAAServer.this.currentState = LifeCycleState.RESTART_CALLED;
				restartCalled.set(true);
				return LifeCycleState.RESTART_CALLED.message;
			} else {
				return getHelp();
			}
		}

		private String getHelp() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : " + getCommandName() );
			out.println("Description : " + getDescription() );
			return stringWriter.toString();
		}

		@Override
		public String getCommandName() {
			return "restart" ;
		}

		@Override
		public String getDescription() {
			return "Restarts server after trying to shut it down using 'shutdown immediate' command";
		}

		@Override
		public String getHotkeyHelp() {
			return "{'restart':{'-help':{}}}";
		}
		
	}
	
	private void createRadiusSessionsFactory() {

		if (getServerContext().getHazelcastImdgInstance() != null) {
			radiusSessionFactory = new HazelcastRadiusSessionFactory(serverContext.getHazelcastImdgInstance(),
					serverConfiguration.getImdgConfigurable().getImdgConfigData().getImdgRadiusConfig()
							.getRadiusIMDGFieldMapping(),
					serverConfiguration.getImdgConfigurable().getImdgConfigData().getImdgRadiusConfig()
							.getRadiusSessionFieldMappingList());
			LogManager.getLogger().info(MODULE, "Hazelcast session factory created for RADIUS.");
			initRadiusCleanupSessionTask((HazelcastRadiusSessionFactory)radiusSessionFactory);
		} else {
			radiusSessionFactory = SessionsFactory.NO_SESSION_FACTORY;
			LogManager.getLogger().info(MODULE, "In-Memory Radius session will not be created, as hazelcast instance is disble or server is not configured in IMDG configuration.");
		}
	}
	
	private void initRadiusCleanupSessionTask(HazelcastRadiusSessionFactory radiusSessionFactory) {

		RadiusSessionCleanupDetail radiusSessionCleanupDetail = getServerContext().getServerConfiguration().getRadiusSessionCleanupDetail();
		
		long sessionCleanupInterval = radiusSessionCleanupDetail.getSessionCleanupInterval();
		long sessionTimeOut = radiusSessionCleanupDetail.getSessionTimeOut();

		// For Cleaning up Radius Sessions.
		if (sessionCleanupInterval > 0) {
			getServerContext().getTaskScheduler().scheduleIntervalBasedTask(radiusSessionFactory.new RadiusSessionCleanupTask(sessionTimeOut * 1000, sessionCleanupInterval));
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Radius Session Cleanup is disabled. Configured value: " + sessionCleanupInterval);
		}

	}
	
}

