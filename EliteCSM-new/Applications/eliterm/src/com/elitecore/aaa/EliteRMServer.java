/*
 *  EliteRM Server
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

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.alert.RMSystemAlertManager;
import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.DetailLocalAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.conf.impl.AAAPluginConfManagerConfigurable;
import com.elitecore.aaa.core.conf.impl.RMServerConfigurable;
import com.elitecore.aaa.core.conf.impl.RMServerConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.ParamsDetail;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.plugins.conf.PluginDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.EliteWebServiceServer;
import com.elitecore.aaa.core.server.axixserver.HTTPSEliteWebServiceServer;
import com.elitecore.aaa.core.server.axixserver.WebServiceConfiguration;
import com.elitecore.aaa.core.util.cli.AAALicenseCommand;
import com.elitecore.aaa.core.util.cli.AlertCommand;
import com.elitecore.aaa.core.util.cli.DBDetailProvider;
import com.elitecore.aaa.core.util.cli.ESIScanCommand;
import com.elitecore.aaa.core.util.cli.ESIStatisticsCommand;
import com.elitecore.aaa.core.util.cli.ESISummaryCommand;
import com.elitecore.aaa.core.util.cli.MiscellaneousConfigDetailProvider;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SNMPServCommand;
import com.elitecore.aaa.core.util.cli.ServerHomeCommand;
import com.elitecore.aaa.core.util.cli.ServicesCommand;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.core.util.cli.VersionCommand;
import com.elitecore.aaa.core.util.cli.data.ServiceDataProvider;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager;
import com.elitecore.aaa.diameter.service.application.EliteDiameterStack;
import com.elitecore.aaa.exprlib.function.ConnectionProviderImpl;
import com.elitecore.aaa.license.AAALicenseManager;
import com.elitecore.aaa.license.EvaluationLicenseManager;
import com.elitecore.aaa.license.LicenseExpiryListener;
import com.elitecore.aaa.license.LicenseManagerFactory;
import com.elitecore.aaa.mibs.server.autogen.ELITEAAA_TRAP_MIBOidTable;
import com.elitecore.aaa.radius.conf.impl.ClientsConfigurable;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.strop.conf.impl.StringOperationPluginConfigurable;
import com.elitecore.aaa.radius.session.SessionsFactory;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.CounterAwareConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyContainer;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicatorManager;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommunicatorManagerImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusESIGroupFactory;
import com.elitecore.aaa.radius.util.cli.ClientsCommand;
import com.elitecore.aaa.radius.util.cli.RadClientCommand;
import com.elitecore.aaa.rm.conf.impl.GTPPrimeConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMChargingServiceConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMConcurrentServiceConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMCrestelAttributeMappingConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMIPPoolServiceConfigurable;
import com.elitecore.aaa.rm.conf.impl.RMPrepaidChargingServiceConfigurable;
import com.elitecore.aaa.rm.conf.impl.RmCrestelAttributeMappingDriverConfigurable;
import com.elitecore.aaa.rm.drivers.RMDriverFactory;
import com.elitecore.aaa.rm.service.chargingservice.RMChargingService;
import com.elitecore.aaa.rm.service.concurrentloginservice.RMConcurrentLoginService;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeUDPService;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolService;
import com.elitecore.aaa.rm.service.prepaidchargingservice.RMPrepaidChargingService;
import com.elitecore.aaa.rm.service.rdr.RDRTCPService;
import com.elitecore.aaa.rm.util.cli.RMSessionMgrCommand;
import com.elitecore.aaa.snmp.service.EliteAAASNMPAgent;
import com.elitecore.aaa.snmp.service.NullSnmpAgent;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.mbean.AAAClientController;
import com.elitecore.aaa.util.mbean.AAASupportedRFCController;
import com.elitecore.aaa.util.mbean.BaseServerController;
import com.elitecore.aaa.util.mbean.ConfigurationDetailController;
import com.elitecore.aaa.util.mbean.MiscellaneousConfigMbeanImpl;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadLogKeyResolver;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.sync.SMConfigurationSynchronizer;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.commons.utilx.mbean.SystemDetailController;
import com.elitecore.core.imdg.HazelcastImdgInstance;
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
import com.elitecore.core.serverx.snmp.mib.mib2.extended.SnmpImpl;
import com.elitecore.core.services.data.LiveServiceSummary;
import com.elitecore.core.servicex.EliteAdminService;
import com.elitecore.core.servicex.EliteService;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.cli.cmd.ClearCommand;
import com.elitecore.core.util.cli.cmd.DataSourceCommand;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.cli.cmd.LDAPDetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.cli.cmd.ShowCommand;
import com.elitecore.core.util.cli.cmd.SysInfoCommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.core.util.mbean.data.dictionary.EliteDictionaryData;
import com.elitecore.core.util.mbean.data.live.EliteNetServerDetails;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.ILicenseValidator;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.stack.StackInitializationFailedException;
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
public final class EliteRMServer extends BaseEliteServer {

	private static final String MODULE = "ELITE-RM";
	protected static final String DEVELOPMENT_VERSION_NO = "Development Version";
	private static final int NANO_TO_MILLI = 1000000;
	private AAAServerContext serverContext;
	private EliteRollingFileLogger serverLevelLogger = null;
	private EliteAdminService adminService = null;
	@Nonnull private AAALicenseManager licenseManager;

	private static final String SERVER_HOME = "ELITERM_HOME";

	@Nonnull private AtomicInteger tpsCounter;
	private RMServerConfigurationImpl serverConfiguration;
	private RMSystemAlertManager systemAlertManager = null;
	private ExternalScriptsManager externalScriptsManager = null;
	private RadUDPCommunicatorManager radUDPCommunicatorManager;
	private CacheManager cacheManager;
	private CacheContainer cacheContainer;	
	private LiveServerSummary liveServerSummary;	
	private SMConfigurationSynchronizer smConfigurationSynchronizer;

	private AAAConfigurationContext configurationContext;

	private static Thread startUPThread;
	String serverInstanceID;
	private String serverInstanceName;
	private long rmServerUPTime;
	private AtomicLong totalResponseTimeInNano;
	private AtomicInteger totalRequestCount;
	private long lastResetTime;

	@Nonnull private EliteAAASNMPAgent snmpAgent;

	/*
	 * NOTE: Elite Diameter Stack will be null, 
	 * if Diameter Stack is Disabled in Server Configuration.
	 */
	@Nullable private EliteDiameterStack eliteDiameterStack;

	@Nullable private DriverManager rmDriversManager;
	@Nullable private RadiusLogMonitor radiusMonitor;
	@Nullable private DiameterPluginManager diameterPluginManager;
	@Nullable private RadPluginManager radiusPluginManager;
	/**
	 * 
	 * @param serverHome
	 */
	EliteRMServer(String strServerHome) {
		super(strServerHome, "Elite RM Server");

		serverContext = new AAAServerContextImpl();
		tpsCounter= new AtomicInteger(0);
		this.totalResponseTimeInNano = new AtomicLong();
		this.totalRequestCount = new AtomicInteger();
		radUDPCommunicatorManager = new RadUDPCommunicatorManagerImpl(serverContext);
		liveServerSummary = new LiveServerSummary();
		cacheManager = new CacheManager();
		this.systemAlertManager = new RMSystemAlertManager(serverContext);
		smConfigurationSynchronizer = new SMConfigurationSynchronizer(serverContext);
		configurationContext = new AAAConfigurationContext(serverContext.getServerHome(), serverContext);
		cacheContainer = new CacheContainer() {			
			@Override
			public void register(Cacheable cacheableObj) {
				cacheManager.load(cacheableObj);			
			}
		};
		snmpAgent = new NullSnmpAgent(serverContext,AAAServerConstants.RM_ENTERPRISE_OID);
		LogManager.getLogger().info(MODULE,"EliteRM server instance from " + getServerHome() + " location");
	}

	/**
	 * 
	 */
	private void readServerConfiguration() throws Exception {
		serverLevelLogger = 
			new EliteRollingFileLogger.Builder(getServerInstanceName(),
					getServerHome() + File.separator + "logs" + File.separator + "eliterm-server")
			.build();

		setLoggingParameters();

		if(isLicenseValid()){
			try {
				serverConfiguration = (RMServerConfigurationImpl) configurationContext.read(RMServerConfigurationImpl.class);
				configurationContext.write(serverConfiguration);
			} catch (LoadConfigurationException e1) {
				LogManager.getLogger().error(MODULE, e1.getMessage());
				LogManager.getLogger().warn(MODULE, "Loading last known good configuration");
				LogManager.getLogger().trace(MODULE, e1);
				try {
					serverConfiguration = (RMServerConfigurationImpl) configurationContext.read(RMServerConfigurationImpl.class);
				} catch (LoadConfigurationException e) {
					throw e;
				}
				LogManager.getLogger().error(MODULE, e1.getMessage());
				LogManager.getLogger().trace(MODULE, e1);
			}
			setServerLogParameters();
			readConcurrentLoginPolicies();
		}

		EliteAAAServiceExposerManager.getInstance().setServerContext(serverContext);

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
	
	private void loadDictionary() {
		loadRadiusDictionaryQuietly();
		loadDiameterDictionaryQuietly();
	}
	
	private void loadDiameterDictionaryQuietly() {

		try {
			loadDiameterDictionary();
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, "Failed to load diameter dictionary, Reason: " + ex.getMessage());
			LogManager.getLogger().trace(ex);
		}
	}

	private void loadRadiusDictionaryQuietly() {
		try {
			loadRadiusDictionary();
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, ex.getMessage());
			LogManager.getLogger().trace(ex);
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
				
				LogManager.getLogger().warn(MODULE,"Providing EliteRM Evaluation license with 100 TPS for 24 hours now onwards.");
				licenseManager = new EvaluationLicenseManager(getServerContext(), licenseExpiryListener);
			}
		} 
	}
	
	@Override
	public String getLicenseKey() {
		return licenseManager.getLicenseKey();
	}

	private void initServer() {
		LogManager.getLogger().trace(MODULE, "Server initialization process started");
		if (isLicenseValid()) {
			super.setInternalSchedulerThreadSize(serverConfiguration.getSchedularMaxThread());
			if (!getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_CPU,String.valueOf(SystemUtil.getAvailableProcessor()))) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE,"Invalid License! The Number of Available Processor's found is "+ SystemUtil.getAvailableProcessor()+ " and that recommended as per License is "+ getLicenseValues(LicenseNameConstants.SYSTEM_CPU));
			}

			if (!getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_NODE,new ElitePublickeyGenerator(
					ElitePublickeyGenerator.PLAIN_TEXT_FORMAT).generatePublicKey(serverContext.getServerHome(),
							LicenseConstants.DEFAULT_ADDITIONAL_KEY,getServerInstanceID(), getServerInstanceName()))) {
				setValidLicense(false);
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().error(MODULE, "Invalid License! IP or ELITERM_HOME is invalid.");
			} else {
				this.cacheContainer.register(new Cacheable() {
					@Override
					public CacheDetail reloadCache(){
						CacheDetailProvider cacheDetail = new CacheDetailProvider();
						cacheDetail.setName("RADIUS-DICTIONARY");
						cacheDetail.setSource(getServerHome() + File.separator + "dictionary" + File.separator + "radius");

						try {
							loadRadiusDictionary();
							cacheDetail.setResultCode(CacheConstants.SUCCESS);
							LogManager.getLogger().debug(getName(), "Reloading cache successfull for Dictionary");
						} catch(Exception ex) {
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
							LogManager.getLogger().debug(getName(), "Reload diameter dictionary cache successfully");
						} catch(Exception ex) {
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
			diameterPluginManager = new DiameterPluginManager(serverConfiguration.getPluginConfiguration());
			diameterPluginManager.init();
			
			initDiameterStack();
			
			rmDriversManager = new DriverManager(serverConfiguration.getDriverConfigurationProvider(),
					new RMDriverFactory(serverContext));
			rmDriversManager.init();
		}

		writeSysInitDetails();
	}

	private void initDiameterStack() {

		if(!serverConfiguration.getDiameterStackConfiguration().isDiameterStackEnabled()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Not Initialising Diameter Stack, Reason: Diameter Stack is not Enabled");
			}
			return; 
		}
		try {
			eliteDiameterStack = new EliteDiameterStack(serverContext,systemAlertManager, diameterPluginManager);
			eliteDiameterStack.initStack();
			adminService.addCliCommand(eliteDiameterStack.getCliCommands());
		} catch (StackInitializationFailedException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().error(MODULE, "Error initializing Diameter Stack, Reason: " + e.getMessage());
		}
	}


	@Override
	public void reInitServer() {
		LogManager.getLogger().trace(MODULE, "Server Re-Initialization process started");
		reInitLogger();
		setMiscellaneousConfigurationAsSystemProperties();
		reInitRadUDPCommunicationManager();
		reInitExternalScriptsManager();
		reInitConcurrentSessionManager();
		reInitDiameterStack();
	}

	private void reInitDiameterStack() {
		if(eliteDiameterStack == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Not Re-Intialising Diameter Stack, Reason: Stack not Found");
			}
			return;
		}
		eliteDiameterStack.reInit();
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
		// Auto-generated method stub
	}

	private void reInitRadUDPCommunicationManager(){
		try{
			radUDPCommunicatorManager.reInit();
		}catch (InitializationFailedException ex) {
			LogManager.getLogger().warn(MODULE, "Error in re-initializing UDP Communication manager");
			LogManager.getLogger().trace(ex);
		}
	}

	private void startServices() {
		// Only if the license is valid than start the services else only admin
		// Service will be active.

		if (isLicenseValid()) {
			startSNMPAgent();
			startSystemAlertManager();
			startWebService();
			startRDRService();
			startRmConcurrentLoginService();
			startRmPrepaidChargingService();
			startGTPPrimeService();
			startRMIPPoolService();
			startDiameterStack();
			startRMChargingService();
		}
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

	private void startRDRService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RDR_SERVICE_ID)) {
			RDRTCPService rdrService = new RDRTCPService(serverContext);
			registerService(rdrService);
		}
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
	 * 
	 */
	private void startServer() throws Exception{
		currentState = LifeCycleState.STARTUP_IN_PROGRESS;
		if(isServerShutdownAbnormally())
			LogManager.getLogger().error(MODULE,"Previously EliteRM server shutdown abnormally");
		LogManager.getLogger().info(MODULE,"Starting EliteRM server, home location: " + this.getServerHome());
		LogManager.getLogger().info(MODULE, "JVM: " + readJVMDetails());
		startAdminService();
		startHtmlAdaptor(snmpAgent.getHttpAdaptorPort());
		loadServerLicense();
		loadDictionary();
		registerExprLibFunction();
		try{
			readServerConfiguration();
			setMiscellaneousConfigurationAsSystemProperties();
			registerServerLevelCommandsDependentOnConfiguration();
			initServer();
			EliteSystemDetail.loadSystemDetail(serverContext);
			startServices();
			writeToStartupInfoFile();
			scheduleStdFileRoller();
			addShutdownHook();
			String startupMessage = formStartupMessageForAlert();
			serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.SERVERUP,MODULE, "EliteRM server: " + 
						getServerInstanceName() + " started successfully with services: " + startupMessage);
			updateLogLevelToServerLogLevel();
			currentState = LifeCycleState.RUNNING;

//			}catch (LoadConfigurationException ex) {
				//TODO stream line all the exceptions from configuration reading code and then un-comment above line 
			}catch (Exception ex) {	
			//unrecoverable configuraiton state. Only admin service and alert service will start
			LogManager.getLogger().trace(ex);
			EliteSystemDetail.loadSystemDetail(serverContext);
			startSystemAlertManager();
			writeToStartupInfoFile();
			addShutdownHook();
			currentState = LifeCycleState.NOT_STARTED;
			serverContext.generateSystemAlert(AlertSeverity.CRITICAL,Alerts.SERVERUP,MODULE, "EliteRM server unable to start as configuration is improper and last good configuration not found or improper");
		}
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
				LogManager.getLogger().info(MODULE, "Updating server log parameters to configured log level = " + serverLogLevel);
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

	private void initExternalScriptsManager(){
		ExternalScriptsManager extScriptsManager = new ExternalScriptsManager(serverContext,serverConfiguration.getScriptConfigurable().getExternalScript());
			extScriptsManager.init();
			this.externalScriptsManager = extScriptsManager;
	}

	private void registerServerCommands() {
		List<ICommand> serverCommandList = new LinkedList<ICommand>();
		serverCommandList.add(new VersionCommand(getServerContext().getServerName(), getServerContext().getServerVersion()));
		serverCommandList.add(new ShutdownCommand(this));
		serverCommandList.add(new ServerHomeCommand(getServerContext().getServerHome()));
		serverCommandList.add(new ServicesCommand(new ServiceDataProviderImpl(),getServerContext()));

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
				return "rmserver.pubkey";
			}

			@Override
			public String getModuleName() {
				return "RMLicense";
			}

			@Override
			protected String getInstanceName() {
				return EliteRMServer.this.getServerInstanceName();
			}

			@Override
			protected String getInstanceId() {
				return EliteRMServer.this.getServerInstanceID();
			}
		});
		serverCommandList.add(new SysInfoCommand());
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


		serverCommandList.add(dsCommand);
		serverCommandList.add(new ESIStatisticsCommand());
		serverCommandList.add(new RMSessionMgrCommand(serverContext));		
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
		ServerConfigurationSetter serverConfigurationSetter = new ServerConfigurationSetter(serverContext);
		SetCommand.registerConfigurationSetter(serverConfigurationSetter);
		serverCommandList.add(setCommand);
		serverCommandList.add(new ESISummaryCommand(serverContext));

		serverCommandList.add(new ReloadConfigurationCommand());
		
		ShowCommand showCommand = new ShowCommand();
		try {	
			ShowCommand.registerDetailProvider(MiscellaneousConfigDetailProvider.getInstance(serverContext));
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Miscellaneous Configuration Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		serverCommandList.add(showCommand);
		//submitting the command list to the admin service 
		adminService.addCliCommand(serverCommandList);
	}

	private class AAAServerContextImpl extends BaseServerContext implements AAAServerContext {

		private Map<String,ConcurrencySessionManager> sessionManagerMap = new HashMap<String, ConcurrencySessionManager>();

		private PluginDetail pluginDetail = new PluginDetail();
		
		@Override
		public RMServerConfiguration getServerConfiguration() {
			return serverConfiguration;
		}

		@Override
		public String getServerName() {
			return EliteRMServer.this.getServerName();
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
			systemAlertManager.scheduleAlert(alertEnum, alertGeneratorIdentity, severity,alertMessage);
		}

		@Override
		public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue) {
			generateSystemAlert(severity.name(), alertEnum, alertGeneratorIdentity, alertMessage, alertIntValue, alertStringValue);
		}

		@Override
		public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage,
				int alertIntValue, String alertStringValue) {
			systemAlertManager.scheduleAlert(alertEnum, alertGeneratorIdentity, severity, alertMessage, alertIntValue, alertStringValue);
		}

		@Override
		public EAPSessionManager getEapSessionManager() {
			return null;
		}

		@Override
		public KeyManager getKeyManager() {
			return null;
		}

		@Override
		public WimaxSessionManager getWimaxSessionManager() {
			return null;
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
			if(sessionManagerId == null) {
				return Optional.absent();
			}

			ConcurrencySessionManager concurrencySessionManager = this.sessionManagerMap.get(sessionManagerId);
			if(concurrencySessionManager == null) {

				SessionManagerData sessionManagerData = serverConfiguration.getSessionManagerConfiguration().getSessionManagerConfigById(sessionManagerId);

				if(!SessionManagerData.TYPE_LOCAL.equals(sessionManagerData.getType())){
					throw new IllegalArgumentException("Session manager: " + sessionManagerData.getInstanceName() + ", is not a local session manager");
				}

				concurrencySessionManager = new CounterAwareConcurrencySessionManager(snmpAgent.incrementAndGetLocalSMIndex(),getServerContext(),(LocalSessionManagerData) sessionManagerData);
				try {
					concurrencySessionManager.init();
					this.sessionManagerMap.put(sessionManagerId, concurrencySessionManager);
				} catch (InitializationFailedException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, 
								"Session Manager initialization failed for Session manager: " 
								+ concurrencySessionManager.getSmInstanceName() 
								+ ", Reason : " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);

					return Optional.absent();
				}
			}			

			return Optional.of(concurrencySessionManager);
		}

		@Override
		public long getAAAServerUPTime() {
			return rmServerUPTime;
		}

		@Override
		public ExternalScriptsManager getExternalScriptsManager() {
			return externalScriptsManager;
		}

		@Override
		public Map<String, ConcurrencySessionManager> getLocalSessionManagerMap() {
			return sessionManagerMap;
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
			return rmServerUPTime;
		}

		@Override
		public String getLocalHostName() {
			return EliteRMServer.this.getLocalHostName();
		}
		@Override
		public String getContact() {
			return "support@eliteaaa.com";
		}
		@Override
		public void registerCacheable(Cacheable cacheable) {
			EliteRMServer.this.cacheContainer.register(cacheable);
		}

		@Override
		public void registerMBean(BaseMBeanController baseMBeanImpl) {
			EliteRMServer.this.registerMBean(baseMBeanImpl);
		}

		@Override
		public AAAConfigurationState getConfigurationState() {
			return AAAConfigurationState.NORMAL;
		}

		@Override
		public void sendSnmpTrap(SystemAlert alert, SnmpAlertProcessor alertProcessor) {
			snmpAgent.sendTrap(alert, alertProcessor);
		}

		@Override
		public ESCommunicator getDiameterDriver(String driverInstanceId) {
			//Will be called by Diameter Service, -NA- for RM
			return null;
		}

		@Override
		public void registerSnmpMib(SnmpMib snmpMib) {
			snmpAgent.registerMib(snmpMib);			
		}

		@Override
		public VirtualInputStream registerVirtualPeer(PeerDataImpl peerData,
				VirtualOutputStream virtualOutputStream) throws ElementRegistrationFailedException {
			if(eliteDiameterStack == null){
				throw new ElementRegistrationFailedException("Diameter Stack Not Found.");
			}
			return eliteDiameterStack.registerVirtualPeer(peerData, virtualOutputStream).getInputStream();
		}

		@Override
		public EliteAAASNMPAgent getSNMPAgent() {
			return EliteRMServer.this.snmpAgent;
		}

		@Override
		public void registerPriorityRoutingEntry(RoutingEntryData entryData)
		throws ElementRegistrationFailedException {
			if(eliteDiameterStack == null){
				throw new ElementRegistrationFailedException("Diameter Stack Not Found.");
			}
			eliteDiameterStack.registerPriorityRoutingEntry(entryData);
		}

		@Override
		public long addTotalResponseTime(long responseTime) {
			totalResponseTimeInNano.addAndGet(responseTime);
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
			return null;
		}

		@Override
		public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
				String alertMessage, Map<com.elitecore.core.serverx.alert.Alerts, Object> alertData) {
			// No-op 			
		}

		@Override
		public boolean hasRadiusSession(String sessionId) {
			return SessionsFactory.NO_SESSION_FACTORY.hasSession(sessionId);
		}

		@Override
		public ISession getOrCreateRadiusSession(String sessionId) {
			return SessionsFactory.NO_SESSION_FACTORY.getOrCreateSession(sessionId);
		}

		@Override
		public Set<String> search(String attributeName, String attributeValue) {
			return SessionsFactory.NO_SESSION_FACTORY.search(attributeName, attributeValue);
		}

		@Override
		public RadiusESIGroupFactory getRadiusESIGroupFactory() {
			return null;
		}
	}
	


	
	@Override
	public AAAServerContext getServerContext() {
		return serverContext;
	}
	// -------------------------------------------------------------------------
	//
	// EliteRM startup method
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
					System.out.println("ELITERM_HOME not valid : " + strServerHome);
				}
			}
			EliteRMServer rmServer = new EliteRMServer(strServerHome);

			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			rmServer.rmServerUPTime = runtimeMXBean.getStartTime();

			rmServer.startServer();
			try {
				startUPThread.join();
			}catch(Exception e){
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");	
				Date date = new Date();
				System.out.println("EliteRMServer shutdown successfully at " + sdf.format(date));
				rmServer.serverLevelLogger.info(MODULE,"EliteRMServer shutdown successfully at " + sdf.format(date));
			}

		} else {
			System.out.println("ELITERM_HOME not defined");
		}
	}

	// -------------------------------------------------------------------------
	// 
	// -------------------------------------------------------------------------

	private void startAdminService() {
		LogManager.getLogger().debug(MODULE, "Starting EliteRM admin service");
		adminService = new EliteAdminService(serverContext);

		try {
			adminService.init();
			boolean serviceStarted = adminService.start();
			if(serviceStarted)
				LogManager.getLogger().info(MODULE,"Admin service started successfully");
			else
				LogManager.getLogger().error(MODULE,"Error in starting Admin service");
		} catch (ServiceInitializationException e) {
			LogManager.getLogger().warn(MODULE, "Error during initializing service: Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
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


	//PRIVATE start service methods

	private void startRMIPPoolService(){
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RM_IPPOOL_SERVICE_ID)) {
			RMIPPoolService rMIPPoolService = new RMIPPoolService(serverContext,
					createOrGetRadiusPluginManager(),
					createRadiusLogMonitor());
			if (registerService(rMIPPoolService)) {
				adminService.addCliCommand(rMIPPoolService.getCliCommands());
			}
		}
	}
	private void startRMChargingService(){
		if(serverConfiguration.isServiceEnabled(AAAServerConstants.RM_CHARGING_SERVICE_ID)){
			
			RMChargingService rmChargingService = new RMChargingService(serverContext, 
					rmDriversManager,
					createOrGetRadiusPluginManager(),
					createRadiusLogMonitor());
			
			if (registerService(rmChargingService)) {
				adminService.addCliCommand(rmChargingService.getCliCommands());
			}
		}
	}

	private void startGTPPrimeService(){
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.GTP_SERVICE_ID)) {
			GTPPrimeUDPService gtpPrimeService = new GTPPrimeUDPService(serverContext);
			if (registerService(gtpPrimeService)) {
				adminService.addCliCommand(gtpPrimeService.getCliCommands());
			}
		}
	}



	private void startWebService() {
		if(serverConfiguration.getWebServiceConfiguration().getIsEnabled()){
			start_HTTP_WebService();
			start_HTTPS_WebService();			
		}	
	}

	private void start_HTTP_WebService() {
		WebServiceConfiguration webServiceConfiguration = serverConfiguration.getWebServiceConfiguration();
		EliteWebServiceServer eliteWebServiceServer = new EliteWebServiceServer(serverContext,webServiceConfiguration.getIpAddress(),webServiceConfiguration.getPort(),webServiceConfiguration.getThreadPoolSize(),webServiceConfiguration.getMaxSession());
		if (registerService(eliteWebServiceServer)) {
			EliteAAAServiceExposerManager.getInstance().init();
			adminService.addCliCommand(eliteWebServiceServer.getCliCommands());
		}

	}

	private void start_HTTPS_WebService() {
		WebServiceConfiguration webServiceConfiguration = serverConfiguration.getWebServiceConfiguration();
		if(webServiceConfiguration.getHttpsServiceAddress()!=null){
			EliteWebServiceServer eliteWebServiceServer = new HTTPSEliteWebServiceServer(serverContext,webServiceConfiguration.getHttpsIPAddress(),webServiceConfiguration.getHttpsPort(),webServiceConfiguration.getThreadPoolSize(),webServiceConfiguration.getMaxSession(), webServiceConfiguration.getServerCertificateProfileId());
			if (registerService(eliteWebServiceServer)) {
				EliteAAAServiceExposerManager.getInstance().init();
				adminService.addCliCommand(eliteWebServiceServer.getCliCommands());
			}
		}	
	}

	private void startRmConcurrentLoginService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RM_CONCURRENT_LOGIN_SERVICE_ID)) {						

			RMConcurrentLoginService rmConcurrentLoginService = 
				new RMConcurrentLoginService(serverContext, createOrGetRadiusPluginManager(), createRadiusLogMonitor());
			if (registerService(rmConcurrentLoginService)) {
				adminService.addCliCommand(rmConcurrentLoginService.getCliCommands());
			}
		}

	}
	
	/**
	 * Maintains the singletonness of RadiusLogMonitor
	 */
	private RadiusLogMonitor createRadiusLogMonitor() {
		if (radiusMonitor == null) {
			radiusMonitor = new RadiusLogMonitor(serverContext.getTaskScheduler());
			try {
				LogMonitorManager.getInstance().registerMonitor(radiusMonitor);
			} catch (RegistrationFailedException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		return radiusMonitor;
	}

	private RadPluginManager createOrGetRadiusPluginManager() {
		if (radiusPluginManager == null) {
			radiusPluginManager = new RadPluginManager(serverContext, serverConfiguration.getPluginConfiguration());
			radiusPluginManager.init();
		}
		return radiusPluginManager;
	}

	private void startRmPrepaidChargingService() {
		if (serverConfiguration.isServiceEnabled(AAAServerConstants.RM_PREPAID_CHARGING_SERVICE_ID)) {

			RMPrepaidChargingService prepaidChargingService = 
				new RMPrepaidChargingService(serverContext, createOrGetRadiusPluginManager(), createRadiusLogMonitor());
			registerService(prepaidChargingService);

		}
	}

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
			
			snmpAgent = new EliteAAASNMPAgent(getServerContext(),AAAServerConstants.RM_ENTERPRISE_OID,
					serverConfiguration.getSNMPAddress(),serverConfiguration.getSNMPPort());
			
			snmpAgent.init();
			
			/***
			* EliteSystemDetail should be initialized before service startup. 
			*/
			snmpAgent.loadRFC1213Mib(EliteSystemDetail.getDescription(), AAAServerConstants.RM_ENTERPRISE_OID,
									EliteSystemDetail.getStartUpTime(), EliteSystemDetail.getContact(),
									EliteSystemDetail.getHostName(),EliteSystemDetail.getLocation());
			
			SnmpImpl snmpimpl = snmpAgent.getSnmpGroup();
			SNMPServCommand snmpCommand = new SNMPServCommand(snmpimpl);
			adminService.addCliCommand(snmpCommand);
			registerServerTrapTable();
			
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in initializing SNMP Agent. Reason:" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			snmpAgent = new NullSnmpAgent(getServerContext(),AAAServerConstants.RM_ENTERPRISE_OID,
					serverConfiguration.getSNMPAddress(),serverConfiguration.getSNMPPort());
		}
	}

	private void registerServerTrapTable(){
		snmpAgent.registetSnmpTrapTable(new ELITEAAA_TRAP_MIBOidTable());
	}

	private void setMiscellaneousConfigurationAsSystemProperties() {
		if (isLicenseValid() == false) {
			return;
		}
		
		List<ParamsDetail> paramsList = serverConfiguration.getMiscellaneousConfigurable().getParamsList();
		for (ParamsDetail paramsDetail : paramsList) {
			if(System.getProperty(paramsDetail.getName()) != null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Overwriting old value: " + System.getProperty(paramsDetail.getName()) + " with new value: " + paramsDetail.getValue() + " for Miscellaneous configuration: " + paramsDetail.getName());
				}
			}
			System.setProperty(paramsDetail.getName(), paramsDetail.getValue());
		}
	}
	
	@Override
	protected void stopServer() {
		LogManager.getLogger().info(MODULE, "Stop server operation started");

		/*
		 * Here first Diameter Stack is stopped, followed by Diameter Stack
		 * UseCase: Auth Request is served local with the help of RM Dia Charging Driver.
		 * First step should be closing service (which closes listening for Charging service), so as to save message drops.
		 * Later Dia Stack would be stopped
		 */

		EliteRMServer.super.stopServer();
		stopDiameterStack();
		stopRadiusDriverManager();
		stopRadUDPCommunicatorManager();
		systemAlertManager.generateAlert(AlertSeverity.INFO.name(),Alerts.SERVERDOWN, MODULE, "EliteRM server: " + 
					getServerInstanceName() + " shutdown successfully");
		snmpAgent.stop();
		stopAdminService();
		writeToShutdownInfoFile();
		LogManager.getLogger().info(MODULE, "EliteRM server shutdown successfully");
		startUPThread.interrupt();
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
	
	private void stopRadiusDriverManager() {
		if (rmDriversManager != null) {
			rmDriversManager.stop();
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
	
	private void stopDiameterStack() {
		if (eliteDiameterStack != null) {
			eliteDiameterStack.stop();
		}
	}

	public class ServerConfigurationSetter implements ConfigurationSetter{
		
		private static final String REALTIME = "realtime";
		
		public ServerConfigurationSetter(AAAServerContext serverContext){
		}

		@Override
		public String execute(String... parameters) {
			if(parameters[1].equalsIgnoreCase("log")){
				if(parameters.length >= 3){
					if(serverLevelLogger instanceof EliteRollingFileLogger){
						EliteRollingFileLogger logger = (EliteRollingFileLogger)serverLevelLogger;
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
			
			ServiceDescription serviceData = 
					new ServiceDescription("SNMP", snmpAgent.getState(), ServiceRemarks.INVALID_LICENSE.remark.equals(snmpAgent.getRemarks()) ? "---" : snmpAgent.getIPAddress() + ":" + snmpAgent.getPort(),
							snmpAgent.getStartDate(), snmpAgent.getRemarks());
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
							"Licence for Server is expired: Actual: " + value + " Licensed: " + getLicenseValues(key),0,
							"RM server(Actual: " + value + " Licensed: " + getLicenseValues(key) + ")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_CPU)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_CPU_EXCEEDED, MODULE, "Invalid License! The Number of Available Processor's found is "+ SystemUtil.getAvailableProcessor()+ " and that recommended as per License is "+ getLicenseValues(LicenseNameConstants.SYSTEM_CPU));
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_CLIENTS)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_CLIENT_EXCEEDED, MODULE, 
							"Invalid License for Max Client Supported, Actual: " + value + " Licensed: " + getLicenseValues(key),0,
							"(Actual: " + value + " Licensed: " + getLicenseValues(key) + ")" );
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_CONCURRENT_SESSION)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_CONCURRENT_USER_EXCEEDED, MODULE, 
							"Invalid License for Max Councurrent User Supported, Actual: " + value + " Licensed: " + getLicenseValues(key), 0,
							"(Actual: " + value + " Licensed: " + getLicenseValues(key) + ")" );
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_TPS)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LICENSED_TPS_EXCEEDED, MODULE, 
							"Invalid License for Max TPS Supported, Actual: " + value + " Licensed: " + getLicenseValues(key),0,
							"(Actual: " + value + " Licensed: " + getLicenseValues(key) + ")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_VENDOR_TYPE)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.NOT_LICENSED_VENDOR, MODULE, 
							"Invalid License for Vendor Client, Actual: " + value + " Licensed: " + getLicenseValues(key), 0,
							"(Actual: " + value + " Licensed: " + getLicenseValues(key) + ")");
				}else if(key.equalsIgnoreCase(LicenseNameConstants.SYSTEM_SUPPORTED_VENDOR)) {
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.NOT_LICENSED_SUPPORTED_VENDOR, MODULE, 
							"Invalid License for Supported Vendor, Actual: " + value + " Licensed: " + getLicenseValues(key), 0,
							"(Actual: " + value + " Licensed: " + getLicenseValues(key) + ")");
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
				if (reloadConfiguration()){
					reInit();
					liveServerSummary.setConfigurationReloadTime(new Date());
					return "Configurations Reloaded Successfully";
				}else{
					return "Reloading Configuration Failed";
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
			RMServerConfiguration rmServerConfiguration = (RMServerConfiguration) configurationContext.reload(serverConfiguration);
			LogManager.getLogger().info(MODULE, "Server configuration reloaded successfully");
			this.serverConfiguration = (RMServerConfigurationImpl)rmServerConfiguration;
			serverContext.generateSystemAlert(AlertSeverity.INFO , Alerts.OTHER_GENERIC , 
					MODULE, "RM Server Configuration reloaded successfully", 0,
					"RM Server Configuration reloaded successfully");
			
		} catch (LoadConfigurationException e) {
			serverContext.generateSystemAlert(AlertSeverity.WARN , Alerts.OTHER_GENERIC , 
					MODULE, "RM Server Configuration reloading failed", 0,
					"RM Server Configuration reloading failed");
			
			LogManager.getLogger().error(MODULE, "Error while reloading RM Server configuration. Reason:" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}
		liveServerSummary.setConfigurationReloadTime(new Date());
		return true;
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
		
		public void writeWebServiceDetail(String userName, String password, String address, Integer port, String contextPath) throws MBeanException;

		public void updateServerConfiguration(EliteNetServerData eliteNetServerData, String version) throws Exception;

		public List<String> updateDictionary(List<EliteDictionaryData> eliteDictionaryList) throws Exception;

		public  Map<String,Object> retriveServerLogFileList() throws Exception;

		public  Map<String,Object> retriveServiceLogFileList() throws Exception;

		public  Map<String,Object> retriveCDRFileList() throws Exception;

		public  byte[] retriveFileData(String file, Integer offset) throws Exception;

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

		String cacheAlertsMsg = "";
		Map<String, CacheStatistics> cacheStatistics = cacheManager.getDetailedStatistics();
		for(CacheStatistics cs : cacheStatistics.values()){
			if(cs.getResultCode() == CacheConstants.FAIL || cs.getResultCode() == CacheConstants.INTRIM){
				cacheAlertsMsg += cs.getName() + ",";
			}
		}

		if(cacheAlertsMsg.length() == 0){
			serverContext.generateSystemAlert(AlertSeverity.INFO , Alerts.OTHER_GENERIC, 
					MODULE, "RM Server Cache reloaded successfully", 0,
					"RM Server Cache reloaded successfully");
		}else{
			cacheAlertsMsg = cacheAlertsMsg.substring(0, cacheAlertsMsg.length()-1);
			serverContext.generateSystemAlert(AlertSeverity.WARN , Alerts.OTHER_GENERIC, 
					MODULE, "RM Server Cache reloading failed for : " + cacheAlertsMsg, 0, 
					"RM Server Cache reloading failed for : " + cacheAlertsMsg);
		}
	}

	public interface AAALicenseControllerMBean {
		public String readLicensePublicKey() throws Exception;
		public void saveLicense(String fileData) throws Exception;
		public String readLicense();
		public void deregisterLicense();
	}

	public class EliteAAAController extends BaseServerController implements
	EliteAAAControllerMBean {

		private static final String MODULE = "RM Controller";
		private AAAConfigurationContext configurationContext = new AAAConfigurationContext(serverContext.getServerHome(), serverContext);

		public EliteAAAController(ServerContext serverContext, SMConfigurationSynchronizer smConfigurationSynchronizer) {
			super(serverContext, smConfigurationSynchronizer);
		}

		@Override
		public String serverHome() {
			return EliteRMServer.this.getServerHome();
		}

		@Override
		public String javaHome(){
			return System.getProperty("java.home");
		}

		@Override
		protected Map<String,List<Class<? extends Configurable>>> getServiceConfigurationClasses() {
			Map<String,List<Class<? extends Configurable>>> serviceConfigList = new HashMap<String,List<Class<? extends Configurable>>>();

			List<Class<? extends Configurable>> concurrentLoginServiceClassList = new ArrayList<Class<? extends Configurable>>();
			concurrentLoginServiceClassList.add(RMConcurrentServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.RM_CONCURRENT_LOGIN_SERVICE_ID,concurrentLoginServiceClassList);

			List<Class<? extends Configurable>> rmIPPoolServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			rmIPPoolServiceConfClassList.add(RMIPPoolServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.RM_IPPOOL_SERVICE_ID, rmIPPoolServiceConfClassList);

			List<Class<? extends Configurable>> rmPrepaidServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			rmPrepaidServiceConfClassList.add(RMPrepaidChargingServiceConfigurable.class);
			rmPrepaidServiceConfClassList.add(RmCrestelAttributeMappingDriverConfigurable.class);
			rmPrepaidServiceConfClassList.add(RMCrestelAttributeMappingConfigurable.class);
			serviceConfigList.put(AAAServerConstants.RM_PREPAID_CHARGING_SERVICE_ID,rmPrepaidServiceConfClassList);


			List<Class<? extends Configurable>> rmChargingGatewayServiceConfClassList = new ArrayList<Class<? extends Configurable>>();
			rmChargingGatewayServiceConfClassList.add(RMChargingServiceConfigurable.class);
			serviceConfigList.put(AAAServerConstants.RM_CHARGING_SERVICE_ID,rmChargingGatewayServiceConfClassList);


			List<Class<? extends Configurable>> gtpPrimeConfigClasses= new ArrayList<Class<? extends Configurable>>();
			gtpPrimeConfigClasses.add(GTPPrimeConfigurable.class);
			serviceConfigList.put(AAAServerConstants.GTP_SERVICE_ID, gtpPrimeConfigClasses);

			return serviceConfigList;
		}

		@Override
		protected List<Class<? extends Configurable>> getServerConfigurationClasses() {
			List<Class<? extends Configurable>> confClassList = new ArrayList<Class<? extends Configurable>>();
			confClassList.add(RMServerConfigurable.class);
			confClassList.add(ClientsConfigurable.class);
			confClassList.add(AAAPluginConfManagerConfigurable.class);
			confClassList.add(DiameterStackConfigurable.class);
			return confClassList;
		}

		@Override
		protected Map<String, List<Class<? extends Configurable>>> getPluginConfigurationClasses() {

			Map<String, List<Class<? extends Configurable>>> pluginConfigList = new HashMap<String, List<Class<? extends Configurable>>>();
			List<Class<? extends Configurable>> stringOperationPluginConfClassList = new ArrayList<Class<? extends Configurable>>();
			stringOperationPluginConfClassList.add(StringOperationPluginConfigurable.class);
			pluginConfigList.put("STRING_OPERATION_PLUGIN", stringOperationPluginConfClassList);

			// Adding Diameter Plugins
			List<PluginInfo> pluginList = new ArrayList<PluginInfo>();

			if(isLicenseValid()){
				if(serverConfiguration!=null && serverConfiguration.getPluginManagerConfiguration()!=null){
					AAAPluginConfManager aaaPluginManager =  serverConfiguration.getPluginManagerConfiguration();
					if(!Collectionz.isNullOrEmpty(aaaPluginManager.getDiameterPluginInfoList())){
						pluginList.addAll(aaaPluginManager.getDiameterPluginInfoList());
					}
				}
			}else{
				//read plugin configuration irrespective of license failure for synchronization from SM
				try {
					AAAPluginConfManagerConfigurable aaaPluginManagerConfigurable = (AAAPluginConfManagerConfigurable) configurationContext.read(AAAPluginConfManagerConfigurable.class);

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
					}
					LogManager.getLogger().info(MODULE, "Name: "+pluginInfo.getPluginName()+","+pluginInfo.getPluginConfClass());
				}
			}
			
			return pluginConfigList;

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
			LiveServerSummary serverSummary = getLiveServerSummary();

			if(serverSummary.getCacheReloadTime() != null)
				eliteServerDetails.setCacheReloadTime(new Timestamp(serverSummary.getCacheReloadTime().getTime()));
			eliteServerDetails.setIdentification(serverSummary.getId());
			eliteServerDetails.setName(serverSummary.getName());
			eliteServerDetails.setVersion(serverSummary.getVersion());
			eliteServerDetails.setIdentification(serverSummary.getId());
			if(serverSummary.getConfigurationReloadTime() != null)
				eliteServerDetails.setServerReloadTime(new Timestamp(serverSummary.getConfigurationReloadTime().getTime()));

			if(serverSummary.getServerStartupTime() != null)
				eliteServerDetails.setServerStartUpTime(new Timestamp(serverSummary.getServerStartupTime().getTime()));

			if(serverSummary.getSoftRestartTime() != null)
				eliteServerDetails.setSoftRestartTime(new Timestamp(serverSummary.getSoftRestartTime().getTime()));
			eliteServerDetails.setServiceSummaryList(serverSummary.getServiceSummaryList());
			eliteServerDetails.setInternalTasks(serverSummary.getInternalTasks());

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
			if(serverConfiguration.getRMPrepaidChargingServiceConfiguration()!=null && serverConfiguration.getRMPrepaidChargingServiceConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getRMPrepaidChargingServiceConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			if(serverConfiguration.getIPPoolConfiguration()!=null && serverConfiguration.getIPPoolConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getIPPoolConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			if(serverConfiguration.getRMConcurrentLoginServiceConfiguration()!=null && serverConfiguration.getRMConcurrentLoginServiceConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getRMConcurrentLoginServiceConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			if(serverConfiguration.getGTPPrimeConfiguration()!=null && serverConfiguration.getGTPPrimeConfiguration().isServiceLevelLoggerEnabled()){
				String location = serverConfiguration.getGTPPrimeConfiguration().getLogLocation();
				fillDirectoryFiles(location, fileMap);
			}
			return fileMap;
		}

		@Override
		public Map<String, Object> retriveCDRFileList() throws Exception {
			Map<String, Object> fileMap = new LinkedHashMap<String,Object>();
			if(serverConfiguration.getRMPrepaidChargingServiceConfiguration()!=null){
				Collection<DriverConfiguration> driverConfigurations =  serverConfiguration.getRMPrepaidChargingServiceConfiguration().getDriverConfigurations();
				cdrFilesFromDriverConfiguration(driverConfigurations,fileMap);

			}
			if(serverConfiguration.getIPPoolConfiguration()!=null){
				Collection<DriverConfiguration> driverConfigurations =  serverConfiguration.getIPPoolConfiguration().getDriverConfigurations();
				cdrFilesFromDriverConfiguration(driverConfigurations,fileMap);

			}
			if(serverConfiguration.getRMConcurrentLoginServiceConfiguration()!=null){
				Collection<DriverConfiguration> driverConfigurations =  serverConfiguration.getRMConcurrentLoginServiceConfiguration().getDriverConfigurations();
				cdrFilesFromDriverConfiguration(driverConfigurations,fileMap);

			}
			return fileMap;
		}
		private void cdrFilesFromDriverConfiguration(Collection<DriverConfiguration> driverConfigurations, Map<String, Object> fileMap) throws Exception {
			if (Collectionz.isNullOrEmpty(driverConfigurations)) {
				return;
			}

			for (DriverConfiguration driverConfiguration : driverConfigurations) {
				if (driverConfiguration.getDriverType() == DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER) {
					ClassicCSVAcctDriverConfiguration    classicCSVAcctConfiguration    = (ClassicCSVAcctDriverConfiguration)driverConfiguration;
					String location = classicCSVAcctConfiguration.getFileLocation();
					fillDirectoryFiles(location, fileMap);
				}
				
				if (driverConfiguration.getDriverType() == DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER) {
					DetailLocalAcctDriverConfiguration    detailLocalAcctConfiguration    = (DetailLocalAcctDriverConfiguration)driverConfiguration;
					String location = detailLocalAcctConfiguration.getFileLocation();
					fillDirectoryFiles(location, fileMap);
				}
			}
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
			saveLicense(fileData, LicenseConstants.LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT);
		}

		public void saveLicense(String fileData, String fileName)
		throws Exception {
			String SERVER_HOME = getServerHome();

			if(SERVER_HOME == null ||fileData == null || fileName == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Problem during uploading license Reason : required Parameter not found");
				throw new Exception("Required Parameter not found");
			}

			byte[] licenseFileBytes = null;  

			try{
				licenseFileBytes = fileData.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){       		// NOSONAR - Exception handlers should preserve the original exceptions 
				licenseFileBytes = fileData.getBytes();
			}
			FileOutputStream fileOutputStream = null;

			try{
				File licenseDir = new File(SERVER_HOME + File.separator + LicenseConstants.LICENSE_DIRECTORY);
				File licenseFile = new File(SERVER_HOME + File.separator + LicenseConstants.LICENSE_DIRECTORY + File.separator + fileName);

				if (!licenseDir.exists())
					licenseDir.mkdir();

				if (licenseFile.exists())
					if(licenseFile.delete() == false)
						throw new IOException("Failed to save license file, Reason: Unable to delete old file " + licenseFile.getAbsolutePath());

				if(licenseFile.createNewFile() == false)
					throw new IOException("Failed to save license file, Reason: Unable to create license file " + licenseFile.getAbsolutePath());
			
				fileOutputStream = new FileOutputStream(licenseFile);
				fileOutputStream.write(licenseFileBytes);


			}catch (IOException e) {
				throw e;
			}
			finally {
				try {
					if (fileOutputStream != null)
						fileOutputStream.close();
				}catch (Exception e) {				// NOSONAR - Exception handlers should preserve the original exceptions
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

	private void setServerLogParameters() {
		LogManager.getLogger().info(MODULE, "Updating server log parameters, log level = " + LogLevel.ALL
				+ ", rolling type = " + serverConfiguration.getLogRollingType() + ", rolling unit = "
				+ serverConfiguration.getLogRollingUnit() + ", max rolled units = "
				+ serverConfiguration.getLogMaxRolledUnits() + ", compress rolled units = "
				+ serverConfiguration.isCompressLogRolledUnits());

		serverLevelLogger.close();

		serverLevelLogger = new EliteRollingFileLogger.Builder(getServerInstanceName(),
				getServerHome() + File.separator + "logs" + File.separator + "eliterm-server")
				.rollingType(serverConfiguration.getLogRollingType())
				.rollingUnit(serverConfiguration.getLogRollingUnit())
				.maxRolledUnits(serverConfiguration.getLogMaxRolledUnits())
				.compressRolledUnits(serverConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(serverConfiguration.getSysLogConfiguration().getHostIp(), serverConfiguration.getSysLogConfiguration().getFacility())
				.build();
	}

	

	/**
	 *   This class will run to see that within the scheduled time slot, 
	 *   whether the TPS Counter has exceeded than License issued. 
	 *   Whenever the task executes, the TPS Counter is reset to 0. 
	 **/

	protected class TPSManager extends BaseIntervalBasedTask{

		private static final String MODULE = "TPS Counter Manager";
		private long initialDelay;
		private long intervalSeconds;

		public TPSManager(long initialDelay,long intervalSeconds){		
			this.initialDelay = initialDelay;
			this.intervalSeconds = intervalSeconds;
			lastResetTime=new Date().getTime();
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
			return (int)intervalSeconds;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			long currentTPS = getServerContext().getTPSCounter();
			
			boolean licenseValid = getServerContext().isLicenseValid("SYSTEM_TPS", String.valueOf(currentTPS));
			
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn("RM-SERV", "For last 1 minute: Average MPS = " + getServerContext().addAndGetAverageRequestCount(0)
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

		} catch (NoSuchEncryptionException e) {			// NOSONAR - Exception handlers should preserve the original exceptions
			LogManager.getLogger().warn(MODULE,"Problem reading server instance name, reason: " + e.getMessage());
		} catch (DecryptionNotSupportedException e) {	// NOSONAR - Exception handlers should preserve the original exceptions
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

	@Override
	protected String getStdLogFileName() {
		return "eliterm-std";
	}
	private void registerExprLibFunction(){
		try{
			Compiler compiler = Compiler.getDefaultCompiler();
			compiler.addFunction(new FunctionDBLookup(new ConnectionProviderImpl()));
		}catch(NullPointerException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Fail to register function DBLookup, Reason: "+ex.getMessage());
				LogManager.getLogger().trace(ex);
			}
		}
	}
}