
package com.elitecore.netvertex;


import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.net.AddressResolver;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.EliteDBConnectionProperty;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.commons.utilx.mbean.SystemDetailController;
import com.elitecore.core.server.data.EliteSystemDetail;
import com.elitecore.core.serverx.BaseEliteServer;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.SnmpAlertProcessor;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.snmp.EliteSnmpAgent;
import com.elitecore.core.serverx.snmp.NullSnmpAgent;
import com.elitecore.core.servicex.EliteAdminService;
import com.elitecore.core.servicex.EliteService;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.core.systemx.esix.http.EndPointManager;
import com.elitecore.core.systemx.esix.http.HTTPClientFactory;
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
import com.elitecore.corenetvertex.GlobalListenersInfo;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.ServiceInfo;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.constants.TimeBasedRollingUnit;
import com.elitecore.corenetvertex.core.mbean.ServerStatusMbean;
import com.elitecore.corenetvertex.data.GatewayInfo;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateDataReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.ReloadRestService;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException.ListenerRegFailResultCode;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.license.base.LicenseManager;
import com.elitecore.license.base.LicenseObserver;
import com.elitecore.license.base.SingleLicenseManager;
import com.elitecore.license.base.commons.LicenseMessages;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.netvertex.cli.AlertCommand;
import com.elitecore.netvertex.cli.AlertCountersDetailProvider;
import com.elitecore.netvertex.cli.AlertStatisticsDetailProvider;
import com.elitecore.netvertex.cli.CacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearCacheDetailProvider;
import com.elitecore.netvertex.cli.ClearCacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ConfigDetailProvider;
import com.elitecore.netvertex.cli.GatewayStatusCommand;
import com.elitecore.netvertex.cli.GlobalListnersCommand;
import com.elitecore.netvertex.cli.LicenseDetailProvider;
import com.elitecore.netvertex.cli.NetvertexLicenseCommand;
import com.elitecore.netvertex.cli.PolicyCommand;
import com.elitecore.netvertex.cli.ReloadDetailProvider;
import com.elitecore.netvertex.cli.SendPacketCommand;
import com.elitecore.netvertex.cli.ServerHomeCommand;
import com.elitecore.netvertex.cli.ServicesCommand;
import com.elitecore.netvertex.cli.SetCommand;
import com.elitecore.netvertex.cli.SetCommand.ConfigurationSetter;
import com.elitecore.netvertex.cli.StatisticsDetailProvider;
import com.elitecore.netvertex.cli.data.ServiceDataProvider;
import com.elitecore.netvertex.cli.db.DBDetailProvider;
import com.elitecore.netvertex.cli.db.ScanDetailProvider;
import com.elitecore.netvertex.cli.db.VoltDBDetailProvider;
import com.elitecore.netvertex.cli.db.VoltDBReInitDetailProvider;
import com.elitecore.netvertex.cli.db.VoltDBScanDetailProvider;
import com.elitecore.netvertex.core.HTTPConnectorFactory;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.NotificationAgent;
import com.elitecore.netvertex.core.ServerStatusScanner;
import com.elitecore.netvertex.core.alerts.AlertProcessorFactory;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.alerts.NetVertexAlertManager;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.conf.impl.NetVertexServerConfigurationImpl;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceConfigurationImpl;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.driver.cdr.CDRDriverFactory;
import com.elitecore.netvertex.core.driver.cdr.DBCDRDriverAlertListener;
import com.elitecore.netvertex.core.groovy.GroovyContext;
import com.elitecore.netvertex.core.groovy.ServerGroovyScript;
import com.elitecore.netvertex.core.locationmanagement.LocationInfoManager;
import com.elitecore.netvertex.core.locationmanagement.LocationRepository;
import com.elitecore.netvertex.core.lrn.LRNConfigurable;
import com.elitecore.netvertex.core.lrn.data.LRNConfigurationRepository;
import com.elitecore.netvertex.core.notification.NotificationServiceFactory;
import com.elitecore.netvertex.core.serverinstance.ServerInstanceDBInfoWriter;
import com.elitecore.netvertex.core.serverinstance.ServerInstanceReaderAndWriter;
import com.elitecore.netvertex.core.serverinstance.ServerInstanceRegistrationCall;
import com.elitecore.netvertex.core.serverinstance.ServerInstanceRegistrationProcess;
import com.elitecore.netvertex.core.serverinstance.ServerInstanceRequestData;
import com.elitecore.netvertex.core.session.NetvertexSessionManager;
import com.elitecore.netvertex.core.session.NetvertexSessionManagerFactory;
import com.elitecore.netvertex.core.session.SessionCacheStatisticsProvider;
import com.elitecore.netvertex.core.session.SessionDaoFactory;
import com.elitecore.netvertex.core.session.SessionHandler;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.NETVERTEX_PCRF_MIBOidTable;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.extended.NETVERTEX_PCRF_MIBImpl;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.extended.UsageMonitoringStatisticsProvider;
import com.elitecore.netvertex.core.transaction.TransactionFactoryAdapter;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.core.util.DefaultEliteCryptEncryptAndDecrypt;
import com.elitecore.netvertex.core.util.DefaultJsonReaderAndWriter;
import com.elitecore.netvertex.core.util.JsonReaderAndWriter;
import com.elitecore.netvertex.core.util.PasswordEncryptAndDecrypt;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import com.elitecore.netvertex.gateway.GatewayEventListener;
import com.elitecore.netvertex.gateway.GatewayMediator;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayController;
import com.elitecore.netvertex.gateway.file.FileGatewayController;
import com.elitecore.netvertex.gateway.file.FileGatewayEventListener;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController;
import com.elitecore.netvertex.license.EvaluationLicenseManager;
import com.elitecore.netvertex.license.LicenseRequester;
import com.elitecore.netvertex.pm.PackageFactory;
import com.elitecore.netvertex.pm.rnc.RnCFactory;
import com.elitecore.netvertex.restapi.DataSourceInfoProviderImpl;
import com.elitecore.netvertex.restapi.DiameterGatewayStatusInfoProviderImpl;
import com.elitecore.netvertex.restapi.RadiusGatewayStatusInfoProviderImpl;
import com.elitecore.netvertex.service.notification.NotificationSenderFactory;
import com.elitecore.netvertex.service.notification.NotificationService;
import com.elitecore.netvertex.service.notification.NotificationServiceStatisticsProvider;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCService;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.pcrf.DeviceManager;
import com.elitecore.netvertex.service.pcrf.PCCRuleExpiryListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.PCRFService;
import com.elitecore.netvertex.service.pcrf.PCRFServiceStatisticsProvider;
import com.elitecore.netvertex.service.pcrf.prefix.PrefixRepository;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfigurable;
import com.elitecore.netvertex.ws.ReAuthorizationController;
import com.elitecore.netvertex.ws.RestServer;
import com.elitecore.netvertex.ws.SessionDisconnectController;
import com.elitecore.netvertex.ws.cli.WebServiceCommandInterface;
import com.elitecore.netvertex.ws.license.LicenseWebService;
import com.elitecore.netvertex.ws.server.ServerInstanceWebService;
import com.elitecore.netvertex.ws.session.SessionWebService;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.agent.SnmpMib;
import groovy.lang.GroovyObject;
import org.apache.http.client.HttpClient;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.annotation.Nullable;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

/**
 *  Startup class of netvertex server implementation.
 *
 */
public class EliteNetVertexServer extends BaseEliteServer {

	private static final long EVALUATION_LICENSE_TPS = 10;
	private static final int EVALUATION_LICENSE_DAYS = 1;
	private static final String MODULE = "NVSVR";
	public static final String DS_ID ="0";
	public static final String DS_NAME ="NetvertexServerDB";
	protected static final String DEVELOPMENT_VERSION_NO = "Development Version";

	public static final String SERVER_HOME = "NETVERTEX_HOME";
	private static final String NETVERTEX_OID = "1.3.6.1.4.1.21067.4";

	private static final int MAX_POOL=100;
	private static final int CONNECTION_TIME_OUT=3000;
	private static final int READ_TIME_OUT=3000;
	public static final String SERVER_NAME = "NetVertex";
	public static final int TASK_CHECK_DURATION = 10;

	private EliteAdminService adminService;
	private PCRFService pcrfService;
	private NotificationService notificationService;
	private OfflineRnCService offlineRnCService;
	private NetvertexGatewayController gatewayController;
	private LicenseManager licenseManager = null;
	private PolicyManager policyManager = null;
	private NetVertexServerContext serverContext;
	private NetVertexServerConfigurationImpl serverConfiguration;
	private SessionFactory sessionFactory;

	private NetVertexAlertManager alertManager;
	private EliteSnmpAgent snmpAgent;
	private NetvertexSessionManager sessionManager;

	private NotificationAgent notificationAgent;


	private String strMessage = "";
	private String serverInstanceID;
	private String serverInstanceName;
	private static Thread startUPThread;
	private volatile  boolean isPrimaryServer=true;
	private LocationInfoManager locationInfoManager;
	private DeviceManager deviceManager;
	private PrefixConfigurable prefixConfigurable;
	private LRNConfigurable lrnConfigurable;
	private long licencedMPM = -1;
	private EliteRollingFileLogger serverLevelLogger;
	private RestServer restApiServer;
	private ServerInfo serverInfo;
	private ReAuthorizationController reAuthorizationController;

	private Future<?> licenseTaskFuture;
	private boolean isLocationBasedServiceEnabled=true;
	private List<LicenseObserver> licenseObservers;
	private ServerInstanceWebService serverInstanceWebService;
	private FileGatewayController fileGatewayController;
	private VoltDBClientManager voltDBClientManager;
	private HTTPClientFactory httpClientFactory;


	private EliteNetVertexServer(String serverHome) {
		super(serverHome, SERVER_NAME);
		serverInfo = new ServerInfo(serverHome);
		httpClientFactory = new HTTPClientFactory();
		serverInfo.setServerName(SERVER_NAME);
		restApiServer= new RestServer();
		licenseObservers = new CopyOnWriteArrayList<>();

	}

	private void initServer() {
		LogManager.getLogger().trace(MODULE, "Server initialization process started");

		super.setInternalSchedulerThreadSize(15);

		LogManager.getLogger().info(MODULE, "Scheduling server level internal task (Expiry Date Validation Task).");
		EliteExpiryDateValidationTask eliteExpiryDateValidationTask = new EliteExpiryDateValidationTask(3600,3600);
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(eliteExpiryDateValidationTask);
		String snmpAddress = serverConfiguration.getNetvertexServerGroupConfiguration().getRunningServerInstance().getNetvertexServerInstanceConfiguration().getSnmpAddress();
		int snmpPort = serverConfiguration.getNetvertexServerGroupConfiguration().getRunningServerInstance().getNetvertexServerInstanceConfiguration().getSnmpPort();
		/// /Initialize SNMP Agent
		try {
			snmpAgent = new EliteSnmpAgent(serverContext,new SnmpOid(NETVERTEX_OID) , snmpAddress,
							snmpPort);
			snmpAgent.init();

			snmpAgent.loadRFC1213Mib("Elite NetVertex Server , Version: " + Version.getVersion() , NETVERTEX_OID,
					serverInfo.getServerStartUpTime().getTime(), "netvertex@elitecore.com", getServerInstanceName(), getServerIp());

			registerServerTrapsTables();
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in initializing SNMP Agent. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);

			snmpAddress = serverConfiguration.getNetvertexServerInstanceConfiguration().getSnmpAddress();
			snmpPort = serverConfiguration.getNetvertexServerInstanceConfiguration().getSnmpPort();
			snmpAgent = new NullSnmpAgent(serverContext, new SnmpOid(NETVERTEX_OID),
					snmpAddress, snmpPort);
		}

		//Initialize Alert Manager
		try {
			alertManager.init();
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in initializing NetVertex Alert Manager. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}


		//Initialize Notification Agent


		initGroovyScripts();

	}

	private void initDDFTable(){

		getLogger().info(MODULE, "Initializing DDF Table");

		DDFConfiguration ddfConfiguration = serverContext.getServerConfiguration().getDDFTableData();

		TransactionFactoryGroupImpl transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
		transactionFactoryGroup.addCommunicator(NetVertexDBConnectionManager.getInstance().getTransactionFactory());

		CacheAwareDDFTable.init(ddfConfiguration, serverContext, new TransactionFactoryAdapter(transactionFactoryGroup));

		getLogger().info(MODULE, "DDF Table initialization completed");
	}

	private void initGroovyScripts(){

		GroovyContext groovyContext = new GroovyContext() {

			@Override
			public NetVertexServerContext getServerContext() {
				return serverContext;
			}

			@Override
			public void doForcefullyReAuthorization(PCRFKeyConstants pcrfKey,String value) {
				EliteNetVertexServer.this.reAuthorizationController.doReAuthorization(pcrfKey,value,true);

			}

			@Override
			public void doReAuthorization(PCRFKeyConstants pcrfKey, String value) {
				EliteNetVertexServer.this.reAuthorizationController.doReAuthorization(pcrfKey,value,false);
			}

			@Override
			public ILogger getLogger() {
				return LogManager.getLogger();
			}

		};

		List<ScriptData> scriptsDatas = serverConfiguration.getScriptsConfigs();

		if(scriptsDatas == null || scriptsDatas.isEmpty()){
			if(LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Scheduled Groovy script is not configured");
			}
			return;
		}

		for(ScriptData groovyScriptData : scriptsDatas){

			if(LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Initializing groovy script: " + groovyScriptData.getScriptName());
			}

			File scriptFile = new File(groovyScriptData.getScriptName());

			if(!scriptFile.isAbsolute()){

				scriptFile = new File(serverContext.getServerHome() + File.separator + "scripts" + File.separator , groovyScriptData.getScriptName());
			}

			try{
				GroovyObject object =  ExternalScriptsManager.createGroovyObject(scriptFile, new Class<?> []{GroovyContext.class}, new Object[]{groovyContext});

				if(object == null){
					LogManager.getLogger().warn(MODULE, "Can't add Scheduled Groovy Object for script file \""+scriptFile.getName()+"\". Reason: groovy script's object not created for Script File");
					continue;
				}

				if(object instanceof ServerGroovyScript){

					ServerGroovyScript serverGroovyScript = (ServerGroovyScript) object;
					serverGroovyScript.init(groovyScriptData.getScriptArgumet());
					serverContext.getTaskScheduler().scheduleIntervalBasedTask(serverGroovyScript);

				}else {
					LogManager.getLogger().warn(MODULE, "Can't add Scheduled Groovy Object for script file \""+scriptFile.getName()+"\". Reason: groovy script's object is not an instance of Server Groovy Script");
				}

				if(LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Groovy script: " + groovyScriptData.getScriptName() + " initialization completed");
				}
			}catch(Exception ex){

				LogManager.getLogger().error(MODULE, "Can't add Scheduled Groovy Object for script file \""+scriptFile.getName()+"\". Reason: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
	}

	private void createPDEndPointsInEndpointManager(){
		List<PDInstanceConfiguration> pdInstanceConfigurationList =serverContext.getServerConfiguration().getPDInstanceConfiguration();

		HttpClient poolableHttpClient = httpClientFactory.getPoolableHttpClient(MAX_POOL,CONNECTION_TIME_OUT,READ_TIME_OUT);

		for(PDInstanceConfiguration pdInstanceConfiguration: pdInstanceConfigurationList){
			EndPointManager.getInstance().addEndPoint(pdInstanceConfiguration, poolableHttpClient, serverContext.getTaskScheduler());
		}
	}

	private void startLicenseScheduler(){
		LicenseRequester licenseRequester  = new LicenseRequester(serverContext);
		licenseRequester.initPDConnectorGroup();
		AsyncTaskContext taskContext = new BaseEliteServer.AsyncTaskContextImpl();
		licenseRequester.preExecute(taskContext);
		licenseRequester.execute(taskContext);
		licenseRequester.postExecute(taskContext);
		if(licenseManager instanceof EvaluationLicenseManager == false){
			licenseTaskFuture = getServerContext().getTaskScheduler().scheduleIntervalBasedTask(licenseRequester);
		} else {
			getLogger().warn(MODULE,"Server started in evaluation license with 24 hours validity.");
		}

	}

	/**
	 *  NetVertex server startup point.
	 *
	 */
	public static void main(String [] args) {
		startUPThread = Thread.currentThread();
		String strServerHome = System.getenv(SERVER_HOME);
		if(strServerHome != null) {
			if(strServerHome.indexOf('.') != -1){
				File serverHome = new File(strServerHome);
				try {
					strServerHome = serverHome.getCanonicalPath();
				} catch (IOException e) {//NOSONAR
					System.out.println(SERVER_HOME + " not valid : " + strServerHome);//NOSONAR
				}
			}

			System.out.println("Starting NetVertex server from " + new File(strServerHome).getAbsolutePath());//NOSONAR
			Thread.currentThread().setName("NVS-MAIN");
			EliteNetVertexServer netVertexServer = new EliteNetVertexServer(strServerHome);

			netVertexServer.startServer();
			try{
				startUPThread.join();
			}catch(Exception e){//NOSONAR
				e.printStackTrace();//NOSONAR
			}
		} else {
			System.out.println(SERVER_HOME + " environment variable must be set before starting NetVertex server. Please refer Installation/Configuration guide for more details."); //NOSONAR
		}
	}

	private void  startServer() {

		startServerInstance();
		serverInfo.setServerInstanceId(getServerInstanceID());
		createServerContext();
		serverLevelLogger =
				new EliteRollingFileLogger.Builder(getServerInstanceName(),
						getServerHome() + File.separator + "logs" + File.separator + "netvertex-server")
						.build();
		serverLevelLogger.setLogLevel(LogLevel.ALL.name());

		setLoggingParameters();

		initializeConfigurationDB();

		adminService = new EliteAdminService(serverContext);
		gatewayController = new NetvertexGatewayController();
		alertManager = new  NetVertexAlertManager(serverContext, new AlertProcessorFactory(serverContext));

		LogManager.getLogger().info(MODULE,"Created NetVertex server instance for Server Home:  " + getServerHome());

		//Initailizing server config logger to log all the configuration Logs into this file.
		initializeConfigLogger(getServerHome());

		long processStartTimeMillis = System.currentTimeMillis();

		currentState = LifeCycleState.STARTUP_IN_PROGRESS;
		if(isServerShutdownAbnormally())
			LogManager.getLogger().warn(MODULE,"Unexpected state of system resources detected, possible cause of this is abnormal shutdown of system, please ensure proper shutdown through the interfaces provided");

		LogManager.getLogger().info(MODULE,"Starting NetVertex ["+ Version.getVersion() +"], home location: " +  new File(this.getServerHome()).getAbsolutePath());

        ConfigLogger.getInstance().info(MODULE, "JVM: " + readJVMDetails());
		startAdminService();
		writeSysInitDetails();
		try{
			sessionFactory = createHibernateSessionFactory();

			readServerConfiguration();
			setServerLogParameters(false);
			createVoltDBClientManager();
			createPDEndPointsInEndpointManager();

			/*startLicenseScheduler instantiates licenseRequester*/
			startLicenseScheduler();
			registerLicenseObserverForServer();

			createSessionManager();
			registerLicenseDetailProvider();

			registerServerLevelCommands();
			initServer();

			if(serverConfiguration.getNetvertexServerGroupConfiguration() != null &&
					serverConfiguration.getNetvertexServerGroupConfiguration().getRunningServerInstance().isPrimaryServer() == false) {

				createScanner();
			} else {
				if (getLogger().isInfoLogLevel())
					getLogger().info(MODULE, "Skipping server status scanner. Reason: Group configuration not found or running instance is primary server");
			}

			EliteSystemDetail.loadSystemDetail(serverContext);
			startServices();

			reAuthorizationController = new ReAuthorizationController(sessionManager, gatewayController, serverContext);
			startRestApiListener();
			initDDFTable();

			//Initialize Gateway Controller
			gatewayController.init();

			startFileGatewayController();

			PCRFServiceStatisticsProvider pcrfServiceStatisticsProvider = pcrfService.getStatisticsProvider();
			UsageMonitoringStatisticsProvider monitoringStatisticsProvider = pcrfService.getUsageMonitoringStatisticsProvider();
			SessionCacheStatisticsProvider sessionCacheStatisticsProvider = sessionManager.getSessionCacheStatisticsProvider();

			if(pcrfServiceStatisticsProvider == null){
				LogManager.getLogger().error(MODULE, "Can't register pcrfStatistics SNMP Group in NETVERTEX PCRF MIB. Reason: PCRF service statistics provider is null");
			}

			if(monitoringStatisticsProvider == null){
				LogManager.getLogger().warn(MODULE, "Can't register usage monitoring statistics SNMP Group in NETVERTEX PCRF MIB. Reason: Usage monitoring statistics provider is null");
			}

			if (sessionCacheStatisticsProvider == null) {
				getLogger().warn(MODULE, "Can't register session cache statistics SNMP Group in NETVERTEX PCRF MIB. Reason: Session cache statistics provider is null");
			}

			NotificationServiceStatisticsProvider notificationServiceStatisticsProvider = null;
			if (notificationService != null && LifeCycleState.NOT_STARTED.message.equals(notificationService.getStatus()) == false){
				notificationServiceStatisticsProvider = notificationService.getNotificationServiceStatisticsProvider();
				if(notificationServiceStatisticsProvider == null){
					LogManager.getLogger().error(MODULE, "Can't register notification service statistics SNMP Group in NETVERTEX PCRF MIB. Reason: Notification Service Statistics Provider is null");
				}
			}else{
				LogManager.getLogger().warn(MODULE, "Can't register notification service statistics SNMP Group in NETVERTEX PCRF MIB. Reason: Notification Service not started");
			}

			registerPCRFMIB(pcrfServiceStatisticsProvider, monitoringStatisticsProvider, sessionCacheStatisticsProvider, notificationServiceStatisticsProvider);

			registerAlertListerForDBDatasource();

			serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.SERVER_UP,MODULE, "NetVertex server instance: " +
						getServerInstanceName() +" started successfully with Sevices :  " + strMessage);

		} finally {
			 if(restApiServer.isStarted() == false) {
			 	restApiServer.startRestApiListener();
			 }
			currentState = LifeCycleState.RUNNING;
		}

		writeToStartupInfoFile();
		scheduleStdFileRoller();
		addShutdownHook();

		LogManager.getLogger().info(MODULE, "NetVertex Server [" + Version.getVersion() + "] started in " + milliToHourTimeFormat((System.currentTimeMillis() - processStartTimeMillis)));

		(serverLevelLogger).setLogLevel(serverConfiguration.getNetvertexServerInstanceConfiguration().getLogLevel());
	}

	private void startServerInstance(){
		//NETVERTEX-3514 , Server instance registration without restart

		ServerInstanceRequestData serverInstanceReqData = new ServerInstanceRequestData();
		if(serverInstanceReqData.validateRequestData()) {
			if(getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Server instance registration can't be made. Reason: environment variables are not set.");
			}
		}else {
			String infoFilePath = getServerHome() + File.separator + SYSTEM_PATH + File.separator + SYS_INFO_FILE;
			PasswordEncryptAndDecrypt passwordEncryptDecrypt = new DefaultEliteCryptEncryptAndDecrypt();
			ServerInstanceReaderAndWriter instanceReaderAndWriter = new ServerInstanceReaderAndWriter(infoFilePath,passwordEncryptDecrypt);
			JsonReaderAndWriter jsonReaderAndWriter = new DefaultJsonReaderAndWriter();
			ServerInstanceDBInfoWriter serverInstanceDbInfoWriter = new ServerInstanceDBInfoWriter(getServerHome(),jsonReaderAndWriter);
			HTTPConnectorFactory httpConnectorFactory = new HTTPConnectorFactory(null,AddressResolver.defaultAddressResolver(),httpClientFactory,serverInstanceReqData.getContextPath());
			ServerInstanceRegistrationCall instanceRegCall = new ServerInstanceRegistrationCall(Integer.parseInt(serverInstanceReqData.getSmPort()),serverInstanceReqData.getSmIp(),httpConnectorFactory);
			ServerInstanceRegistrationProcess instanceRegProcess = new ServerInstanceRegistrationProcess(getServerHome(), serverInstanceReqData, instanceReaderAndWriter,serverInstanceDbInfoWriter,instanceRegCall, String.valueOf(serverInfo.getJmxPort()));
			instanceRegProcess.calltoSMToGetDBAndServerInstance();
			serverInfo.setServerName(readServerInstanceName());
			serverInfo.setServerInstanceId(readServerInstanceId());
		}


	}

	private void createVoltDBClientManager() {
		voltDBClientManager =  new VoltDBClientManager();
	}

	private void startFileGatewayController() {
		if (offlineRnCService.isInitialized() == false) {
			LogManager.getLogger().warn(MODULE, offlineRnCService.getServiceName() + " service not initialized, "
					+ "so file gateways will not be started.");
			return;
		}

		fileGatewayController = new FileGatewayController(serverContext, new FileGatewayEventListener() {

			@Override
			public void received(RnCRequest request, RnCResponse response, PacketOutputStream out) throws Exception {
				offlineRnCService.handleRequest(request, response, out);
			}

			@Override
			public void receivedIntermediate(RnCRequest intermediateRequest, RnCResponse rncResponse,
					PacketOutputStream outputStream) throws Exception {
				outputStream.writeSuccessful(intermediateRequest, rncResponse);
			}
		});
		fileGatewayController.init();
	}

	private void createScanner() {


		if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Scheduled server status scanner. Reason: Running instance is secondary server");
        }


        try {
			ServerStatusScanner serverStatusScanner = ServerStatusScanner.create(serverContext,
                    status -> isPrimaryServer = status,
					new HTTPConnectorFactory(serverContext.getTaskScheduler(), AddressResolver.defaultAddressResolver(), new HTTPClientFactory(),"netvertex"),
					Runtime.getRuntime());
			isPrimaryServer = false;
			serverContext.getTaskScheduler().scheduleIntervalBasedTask(serverStatusScanner);
		} catch (InitializationFailedException ex) {
			getLogger().error(MODULE, "Error initializing rest connector. Reason: " + ex.getMessage());
			getLogger().trace(MODULE, ex);
			isPrimaryServer = true;
		}

	}

	private void registerLicenseObserverForServer(){
		serverContext.registerLicenseObserver(()->{
			validateLicenseForSystemCpu();
			validateLicenseForLoctionService();
			setLicencdedMPM();
		});
		validateLicenseForSystemCpu();
		validateLicenseForLoctionService();
		setLicencdedMPM();
	}

	private void validateLicenseForSystemCpu(){
		if (getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_CPU, String.valueOf(SystemUtil.getAvailableProcessor()))==false) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE,"License warning, number of available processor available is not matching with licensed number (licensed: " +licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_CPU)+ ", available: "+ SystemUtil.getAvailableProcessor()+")");
		}
	}
	private void validateLicenseForLoctionService(){
		isLocationBasedServiceEnabled = getServerContext().isLicenseValid(LicenseNameConstants.NV_LOCATIONBASED_SERVICE,String.valueOf(System.currentTimeMillis()));
	}

	private void registerLicenseDetailProvider() {
		try {
            ShowCommand.registerDetailProvider(new LicenseDetailProvider(licenseManager.getLicenseKey()));
        } catch (RegistrationFailedException e1) {
            LogManager.getLogger().error(MODULE, "Failed to register License Detail Provider. Reason : " + e1.getMessage());
            LogManager.getLogger().trace(MODULE, e1);
        }
	}

	private void setLicencdedMPM() {
		try{
			String licensedMPMString = licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_TPS);

			if(Strings.isNullOrBlank(licensedMPMString) == false){
				long licencedMPM = Long.parseLong(licensedMPMString);
				if(licencedMPM > 0){
					this.licencedMPM = licencedMPM * 60;
					LogManager.getLogger().info(MODULE, "Set Licensed MPS as " + this.licencedMPM);
				} else {
					LogManager.getLogger().warn(MODULE, "No limit on message per minute. Reason: system TPS in license is " + licencedMPM);
				}
			}
        }catch(Exception ex){
            LogManager.getLogger().error(MODULE, "Error while setting limit on message per minute, no limit on Message Per Minute. Reason: " + ex.getMessage());
            LogManager.getLogger().trace(MODULE, ex);
        }
	}

	private void registerAlertListerForDBDatasource() {
		for(final String dataSource : DBConnectionManager.getDataSources()){
            TransactionFactory transactionFactory = DBConnectionManager.getInstance(dataSource).getTransactionFactory();
            if(transactionFactory != null){
                transactionFactory.addESIEventListener(new ESIEventListener<ESCommunicator>() {
                    @Override
                    public void alive(ESCommunicator esCommunicator) {
                        if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                            LogManager.getLogger().debug(MODULE, "generating alert for datasource up alert for datasource:" + dataSource);
                        serverContext.generateSystemAlert(AlertSeverity.INFO, Alerts.DS_UP, MODULE, "Datasource " + dataSource +" is up");
                    }

                    @Override
                    public void dead(ESCommunicator esCommunicator) {
                        LogManager.getLogger().warn(MODULE, "generating alert for datasource down alert for datasource:" + dataSource);
                        serverContext.generateSystemAlert(AlertSeverity.ERROR, Alerts.DS_DOWN, MODULE, "Datasource " + dataSource +" is down");

                    }
                });
            } else{
                LogManager.getLogger().debug(MODULE, "Unable to generate data source alert for datasource:" + dataSource + ". Reason: TransactionFactory not created for datasource");
            }
        }
	}

	private void registerPCRFMIB(PCRFServiceStatisticsProvider pcrfServiceStatisticsProvider,
								 UsageMonitoringStatisticsProvider monitoringStatisticsProvider,
								 SessionCacheStatisticsProvider sessionCacheStatisticsProvider,
								 NotificationServiceStatisticsProvider notificationServiceStatisticsProvider) {

		NETVERTEX_PCRF_MIBImpl netvertexPCRFMib = new NETVERTEX_PCRF_MIBImpl(pcrfServiceStatisticsProvider , monitoringStatisticsProvider
                ,notificationServiceStatisticsProvider, sessionCacheStatisticsProvider);
		try{
            LogManager.getLogger().info(MODULE,"Initializing NetVertex PCRF MIB");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            netvertexPCRFMib.populate(server, null);
            LogManager.getLogger().debug(MODULE,"NetVertex PCRF MIB initialized successfully");
            LogManager.getLogger().info(MODULE,"Registering NetVertex PCRF MIB in SNMP Agent");
            snmpAgent.registerMib(netvertexPCRFMib);
        }catch(Exception e){
            LogManager.getLogger().error(MODULE, "Error in initializing NetVertex PCRF MIB. Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }
	}

	private void initializeConfigurationDB() {

		Path path = Paths.get(serverInfo.getServerHome() + File.separator + SYSTEM_PATH + File.separator + "database.json");
		try (BufferedReader fileWriter = Files.newBufferedReader(path)){

			ConfigurationDatabase configurationDatabase = GsonFactory.defaultInstance().fromJson(fileWriter, ConfigurationDatabase.class);

			if(Objects.isNull(configurationDatabase)) {
				getLogger().warn(MODULE, "No Configuration DB configured, continue with no configuration DB");
				return;
			}


			DBDataSourceImpl dbDataSource = new DBDataSourceImpl(DS_ID,
					DS_NAME,
					configurationDatabase.getUrl(),
					configurationDatabase.getUsername(),
					configurationDatabase.getPassword(),
					configurationDatabase.getMaxIdle(),
					configurationDatabase.getMaxTotal(),
					DBDataSourceImpl.DEFAULT_STATUS_CHECK_DURATION,
					EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue,
					configurationDatabase.getValidationQueryTimeout());

			NetVertexDBConnectionManager.getInstance().init(dbDataSource, this.serverContext.getTaskScheduler());
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			getLogger().error(MODULE, "Error while reading DB detail. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	private SessionFactory createHibernateSessionFactory() {

		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.connection.provider_class", "com.elitecore.netvertex.core.conf.ConfigurationDBConnectionProvider");

		Configuration configuration = new Configuration();

		configuration.setProperties(hibernateProperties);

		HibernateConfigurationUtil.setConfigurationClasses(configuration);

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
				applySettings(configuration.getProperties()).build();

		return configuration.buildSessionFactory(serviceRegistry);
	}

	private void createSessionManager() {
		try {
			sessionManager = new NetvertexSessionManagerFactory().create(serverContext, new SessionDaoFactory().create(serverContext));
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in initializing Session Manager, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}


    private void setLoggingParameters() {
    	LogManager.setDefaultLogger(serverLevelLogger);
	}

	//Initializing location Manager
	private void initLocationinformationManager(){
		try {
			locationInfoManager = new LocationInfoManager(sessionFactory);
			locationInfoManager.init();
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error in initializing Location Information Manager, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	//Initializing Device Manager
	private void initDeviceManager(){
		try {
			deviceManager = new DeviceManager();
			deviceManager.init(sessionFactory);
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error while initializing device details, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void readPrefixConfigurable() {
		try {
			prefixConfigurable = new PrefixConfigurable(sessionFactory);
			prefixConfigurable.read();
		} catch (LoadConfigurationException e) {
			getLogger().error(MODULE, "Error while reading prefix configuration, Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	private void readLRNConfigurations(){
		try{
            lrnConfigurable = new LRNConfigurable(sessionFactory,locationInfoManager);
            lrnConfigurable.read();
		}catch(LoadConfigurationException e){
			getLogger().error(MODULE, "Error while reading LRN configuration, Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

    private boolean reloadLocationInformation(){

		 LocationInfoManager locationInfoManager = new LocationInfoManager(sessionFactory);

		 getLogger().info(MODULE, "Reloading location information started");
		 try{
			 locationInfoManager.init();
			 this.locationInfoManager = locationInfoManager;
			 getLogger().info(MODULE, "Reloading location information completed");
			 return true ;
		 } catch(Exception e){
				getLogger().error(MODULE, "Failed to reload location information, " +
						"Continuing with previous configuration of location information.");
				getLogger().trace(MODULE,e);
		 }

		 return false;
	}



	private boolean reloadDeviceInformation(){

		DeviceManager deviceManager = new DeviceManager();

		getLogger().info(MODULE, "Reloading device information started");
		try{
			deviceManager.init(sessionFactory);
			this.deviceManager = deviceManager;
			getLogger().info(MODULE, "Reloading device information completed");
			return true ;
		} catch(Exception e){
			getLogger().error(MODULE, "Failed to reload device information, " +
					"Continuing with previous configuration of device information.");
			getLogger().trace(MODULE,e);
		}

		return false;
	}

	private boolean reloadPrefixConfigurable(){

		getLogger().info(MODULE, "Reloading prefix configuration manager started");
		try {
			prefixConfigurable.reload();
			getLogger().info(MODULE, "Reloading prefix configuration manager completed");
			return true ;
		} catch (Exception e) {
			getLogger().error(MODULE, "Failed to reload prefix configuration manager. Reason: " + e.getMessage() +
					". Continuing with previous prefix configuration.");
			getLogger().trace(MODULE,e);
		}

		return false;
	}

	private boolean reloadLRNConfigurations(){

		getLogger().info(MODULE, "Reloading LRN configuration started");
		try {
			lrnConfigurable.reload();
			getLogger().info(MODULE, "Reloading LRN configuration completed");
			return true ;
		} catch (Exception e) {
			getLogger().error(MODULE, "Failed to reload LRN configurations. Reason: " + e.getMessage() +
					". Continuing with previous LRN configuration.");
			getLogger().trace(MODULE,e);
		}
		return false;
	}


	private void initPolicyManager() {
		policyManager = PolicyManager.getInstance();
		try {


			RnCFactory rnCFactory = new RnCFactory();
			RateCardVersionFactory rateCardVersionFactory = new com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory(rnCFactory);
			RateCardFactory rateCardFactory = new com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory(rateCardVersionFactory, rnCFactory);
			RateCardGroupFactory rateCardGroupFactory = new com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory(rateCardFactory, rnCFactory);
			ThresholdNotificationSchemeFactory thresholdNotificationSchemeFactory = new ThresholdNotificationSchemeFactory(rnCFactory);

			this.policyManager.init(getServerHome(), sessionFactory, new HibernateDataReader()
					, new PackageFactory(),
					new RnCPackageFactory(rateCardGroupFactory, rnCFactory,thresholdNotificationSchemeFactory),
					serverConfiguration.getSystemParameterConfiguration().getDeploymentMode());
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error in initializing Policy Manager. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		//Adding Policy Command
		PolicyCommand policyCommand = new PolicyCommand(serverContext);
		adminService.addCliCommand(policyCommand);
		//Registering Reload Detail provider
		try{
			policyCommand.registerDetailProvider(new ReloadDetailProvider(serverContext));
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Reload Detail Provider. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		serverInstanceWebService.setPolicyManager(policyManager);
	}

	private void readServerConfiguration() {
		serverConfiguration = new NetVertexServerConfigurationImpl(serverContext,sessionFactory);
		try {
			serverConfiguration.readConfiguration();
		} catch (LoadConfigurationException e1) {
			getLogger().error(MODULE, "Problem reading configuration from database. Reason: " + e1.getMessage());
			getLogger().trace(MODULE, e1);
		}
	}

	private void setServerLogParameters(boolean isReload) {

		LogManager.getLogger().info(MODULE, "Setting server log parameters");
		RollingType tempRollingType = serverConfiguration.getNetvertexServerInstanceConfiguration().getRollingType();
		String tempRollingUnit = serverConfiguration.getNetvertexServerInstanceConfiguration().getRollingUnit();
		final int TEN_MB_IN_KB = 10240;
		int rollingUnit;

		if(tempRollingType == null){
			if(isReload == true){
				LogManager.getLogger().warn(MODULE,"Using previous configuration. Reason: Invalid RollingType");
				return;
			}else{
				tempRollingType = RollingType.TIME_BASED;
				LogManager.getLogger().warn(MODULE, "Using Default Rolling-Type " + RollingType.TIME_BASED.type
						+". Reason: Invalid Configuration for Rolling-Type");

				rollingUnit = TimeBasedRollingUnit.DAILY.value;
				LogManager.getLogger().warn(MODULE, "Using Default Rolling-Unit: "+ TimeBasedRollingUnit.DAILY.unit
						+". Reason: Invalid Configuration for the Rolling-Type");
			}

		}else if (tempRollingType == RollingType.TIME_BASED) {

			TimeBasedRollingUnit timeBasedRollingUnit = TimeBasedRollingUnit.fromValue(tempRollingUnit);

			if(timeBasedRollingUnit == null){
				if(isReload == true){
					LogManager.getLogger().warn(MODULE,"Using previous configuration. " +
							"Reason: Invalid Rolling Unit for RollingType: "+tempRollingType.type);
					return;
				}else{
					LogManager.getLogger().warn(MODULE, "Using Default Rolling-Unit: "+ TimeBasedRollingUnit.DAILY.unit
							+". Reason: Invalid Configuration for the Rolling-Unit: "+ tempRollingUnit);
							rollingUnit  = TimeBasedRollingUnit.DAILY.value;
				}

			}else{
				rollingUnit = timeBasedRollingUnit.value;
			}
		}else {
			try {
				rollingUnit = Integer.parseInt(tempRollingUnit.trim());

				if(rollingUnit < TEN_MB_IN_KB){
					if(isReload == true){
						LogManager.getLogger().warn(MODULE,"Using previous configuration. " +
								"Reason: Invalid Rolling Unit for RollingType: "+tempRollingType.type);
						return;
					}else{
						rollingUnit = TEN_MB_IN_KB;
						LogManager.getLogger().warn(MODULE, "Using Default size: " + TEN_MB_IN_KB +
								". Reason: Rolling unit for "+ tempRollingType.type +" should be greater than " +TEN_MB_IN_KB);
					}
				}
			} catch (Exception e) {
				if(isReload == true){
					LogManager.getLogger().trace(MODULE, e);
					LogManager.getLogger().warn(MODULE,"Using previous configuration. " +
							"Reason: Invalid Rolling Unit for RollingType: "+tempRollingType.type);
					return;
				}else{
					rollingUnit = TEN_MB_IN_KB;
					LogManager.getLogger().trace(MODULE, e);
					LogManager.getLogger().warn(MODULE, "Using Default Rolling-Unit: "+ TEN_MB_IN_KB +" for Rolling-Type: "+tempRollingType.type
							+". Reason Invalid Configuration for Rolling-Type: "+tempRollingType.type);

				}

			}
		}

		serverLevelLogger.close();


		NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConf = serverConfiguration.getNetvertexServerInstanceConfiguration();
		boolean appendDiagnosticInformation = Boolean.parseBoolean(System.getProperty("AppendDiagnosticInformation"));
		EliteRollingFileLogger logger = new EliteRollingFileLogger.Builder(getServerInstanceName(),
                getServerHome() + File.separator + "logs" + File.separator + "netvertex-server")
                .rollingUnit(rollingUnit)
                .maxRolledUnits(netvertexServerInstanceConf.getMaxRollingUnit())
                .compressRolledUnits(netvertexServerInstanceConf.isCompRollingUnit())
                .rollingType(tempRollingType.value)
				.appendDiagnosticInformation(appendDiagnosticInformation)
                .build();

		logger.setLogLevel(LogLevel.ALL.name());
        serverLevelLogger = logger;
        LogManager.setDefaultLogger(serverLevelLogger);


	}

	private void stopAdminService(){
		adminService.stopService();
	}

	@Override
	protected void stopServer() {
		getLogger().warn(MODULE, "Server shutdown operation started");
		super.stopServer();
		if (policyManager != null) {
			policyManager.stop();
		}

		if (gatewayController != null) {
			gatewayController.stop();
		}

		if (fileGatewayController != null) {
			fileGatewayController.stop();
		}

		CacheAwareDDFTable.stop();
		stopAdminService();

		if (restApiServer != null) {
			restApiServer.stop();
		}

		writeToShutdownInfoFile();
		getLogger().info(MODULE, "Server shutdown success");
		if (alertManager != null){
			alertManager.generateAlert(AlertSeverity.INFO.name(),Alerts.SERVER_DOWN, MODULE, "Netvertex server instance: " +
						getServerInstanceName() + " shutdown successfully");
		}
		if (snmpAgent != null) {
			snmpAgent.stop();
		}

		voltDBClientManager.stop();

		serverLevelLogger.close();

		EndPointManager.getInstance().stop();

		if(Objects.nonNull(notificationAgent)){
			notificationAgent.stop();
		}


		DBConnectionManager.getDataSources().forEach(dataSource -> DBConnectionManager.getInstance(dataSource).close());

		httpClientFactory.shutdown();

		startUPThread.interrupt();

	}

	@Override
	protected final void cleanupResources() {
		super.cleanupResources();
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Cleaning up resources for server: " + getServerName());
		}

		Closeables.closeQuietly(serverLevelLogger);
		//Do not log anything here as the server level logger is closed
	}

	public class ReloadConfigurationCommand extends EliteBaseCommand {

		private static final String LOCATION = "-location";
		private static final String DEVICE = "-device";

		@Override
		public String execute(String parameter) {
				String result = "";
				if(HELP.equals(parameter) || HELP_OPTION.equalsIgnoreCase(parameter)){
					result = getHelp();
				} else if(LOCATION.equalsIgnoreCase(parameter)){
					if( reloadLocationInformation() == true){
						result = "Location information reloaded successfully" ;
					} else {
						result = "Failed to reload location information" ;
					}
				} else if (DEVICE.equalsIgnoreCase(parameter)) {
					if(reloadDeviceInformation() == true){
						result = "Device information reloaded successfully";
					} else {
						result = "Failed to reload device information";
					}
				} else if("".equalsIgnoreCase(parameter)){
					if(reloadServerConfiguration() == true) {
						result =  "Server Configuration Successfully Reloaded for " + getServerName();
					} else {
						result =  "Failed to Reload Server Configuration for " + getServerName();
					}
				} else{
					return "Invalid Argument" +  getHelp() ;
				}

			return result;
		}

		@Override
		public String getCommandName() {
			return "rconf";
		}

		@Override
		public String getDescription() {
			return "Reload Configuration for server instance";
		}

		@Override
		public String getHotkeyHelp() {
			return "{'" + getCommandName() + "':{'" + HELP + "' :{},'" + LOCATION + "':{}}}";
		}

		private String getHelp(){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println(" Description	: " + getDescription() );
			out.println(" Usage      	: " + getCommandName() + " [option]" );
			out.println(" Possible Options: ");
			out.println();
			out.println(fillChar(" " + LOCATION,19) +"Reload location information" );
 			return stringWriter.toString();
		}

	}
	public class ServiceDataProviderImpl implements ServiceDataProvider{

		@Override
		public List<ServiceDescription> getServiceDescriptionList() {
			List<ServiceDescription> serviceList = new ArrayList<ServiceDescription>();
			for(EliteService service : getServices()){
				serviceList.add(service.getDescription());
			}
			return serviceList;
		}

	}


	private void startPCRFService(){
		LogManager.getLogger().info(MODULE, "Staring NetVertex PCRF Service");
		pcrfService = new PCRFService(serverContext,sessionManager.getSessionOperation(), new CDRDriverFactory(serverContext, new DBCDRDriverAlertListener(serverContext)));
		if(registerService(pcrfService)){
			strMessage = strMessage + " PCRF , ";
		}
	}
	private void startNotificationService() {

		NotificationServiceFactory notificationServiceFactory = new NotificationServiceFactory(serverContext, new NotificationSenderFactory());

		try {
			notificationAgent = notificationServiceFactory.createAgent();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error in initializing notification agent. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		if (serverConfiguration.getNotificationServiceConfiguration().isEmailNotificationEnabled()
				|| serverConfiguration.getNotificationServiceConfiguration().isSMSNotificationEnabled()) {


			LogManager.getLogger().info(MODULE, "Starting NetVertex Notification service");

			this.notificationService = notificationServiceFactory.createDbService();

			if (registerService(this.notificationService)) {
				strMessage = strMessage + " Notification , ";
			}
		} else {
			LogManager.getLogger().info(MODULE, "Notification service is disabled");
		}
	}

	private void startOfflineRnCService() {
		offlineRnCService = new OfflineRnCService(serverContext, sessionFactory);

		if (registerService(offlineRnCService)) {
			strMessage = strMessage + " OfflineRnC , ";
		}
	}

	private void startServices() {
		LogManager.getLogger().info(MODULE, "Starting services");
		initPolicyManager();
		initLocationinformationManager();
		initDeviceManager();
		readPrefixConfigurable();
		readLRNConfigurations();
		startPCRFService();
		startNotificationService();
		startOfflineRnCService();
	}


	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	private void startAdminService() {
		LogManager.getLogger().info(MODULE, "Starting NetVertex admin service");

		try {
			adminService.init();
			boolean serviceStarted = adminService.start();
			if(serviceStarted)
				LogManager.getLogger().info(MODULE,"admin service started successfully");
			else
				LogManager.getLogger().error(MODULE,"Error in starting admin service");
		} catch (ServiceInitializationException e) {
			LogManager.getLogger().warn(MODULE, "Error during initializing service: Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		registerMBean(new SystemDetailController());
		ServerStatusControllerMbean serverStatusControllerMbean = new ServerStatusControllerMbean(new ServerStatusProviderImpl());
		registerStandardMBean(serverStatusControllerMbean, ServerStatusMbean.class, serverStatusControllerMbean.getName());
		registerServerCommands();

		serverInfo.setVersion(serverContext.getServerVersion());

		serverInstanceWebService = new ServerInstanceWebService(serverInfo, new ServerStatusProviderImpl(), new DataSourceInfoProviderImpl(serverContext), new InstanceWebServiceContextImpl() );
		restApiServer.addService(serverInstanceWebService);
	}

	public interface InstanceWebServiceContext {
		boolean reloadConfiguration();
	}

	private class InstanceWebServiceContextImpl implements InstanceWebServiceContext {
		@Override
		public boolean reloadConfiguration() {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Reloading server configuration");
			}
			return reloadServerConfiguration();
		}
	}

	private void registerServerCommands() {
		List<ICommand> serverCommandList = new LinkedList<ICommand>();
		serverCommandList.add(new ServicesCommand(new ServiceDataProviderImpl()));
		serverCommandList.add(new GlobalListenerCommandImpl());
		SetCommand setCommand = new SetCommand();
		ServerConfigurationSetter serverConfigurationSetter = new ServerConfigurationSetter();
		SetCommand.registerConfigurationSetter(serverConfigurationSetter);
		serverCommandList.add(setCommand);
		serverCommandList.add(new LogMonitorCommandExt());
		serverCommandList.add(new ShutdownCommand(this));
		serverCommandList.add(new ServerHomeCommand(getServerHome()));

		serverCommandList.add(new ReloadConfigurationCommand());
		serverCommandList.add(new NetvertexLicenseCommand() {
			@Override
			protected String readLicense() {
				return licenseManager.getLicenseKey();
			}
		});

		try{
			ShowCommand.registerDetailProvider(StatisticsDetailProvider.getInstance());
		}catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Statistics Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		try {
			StatisticsDetailProvider.getInstance().registerDetailProvider(CacheStatisticsDetailProvider.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Cache Statistics Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}


		try {
			ClearStatisticsDetailProvider.getInstance().registerDetailProvider(ClearCacheStatisticsDetailProvider.getInstance());
			ClearCommand.registerDetailProvider(ClearStatisticsDetailProvider.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Clear Cache Statistics Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		try {
			ClearCommand.registerDetailProvider(ClearCacheDetailProvider.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Clear Cache Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		try {
			StatisticsDetailProvider.getInstance().registerDetailProvider(new AlertStatisticsDetailProvider(alertManager));
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Alert Statistics Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		serverCommandList.add(new ShowCommand());
		serverCommandList.add(new ClearCommand());
		serverCommandList.add(new SendPacketCommand());
		serverCommandList.add(new SysInfoCommand());
		serverCommandList.add(new GatewayStatusCommand());
		adminService.addCliCommand(serverCommandList);
		serverCommandList.clear();



	}

	public class ServerConfigurationSetter implements ConfigurationSetter{

		private static final String LOG = "log";
		private static final String TRACE_LOG = "tracelog";
		public static final String ON = "ON";
		public static final String OFF = "OFF";
		public static final String SERVER = "server";
		public static final String QUESTION_MARK = "?";
		public static final String HELP = "-help";

		@Override
		public String execute(String... parameters) {
			if(parameters[1].equalsIgnoreCase(LOG)){
				if(parameters.length == 3){
					if(serverLevelLogger instanceof EliteRollingFileLogger){
						if((serverLevelLogger).isValidLogLevel(parameters[2])){
							(serverLevelLogger).setLogLevel(parameters[2]);
							return "Configuration Changed Successfully";
						}else{
							return "Invalid log level: " + parameters[2];
						}
					}
				}
			} else if(parameters[1].equalsIgnoreCase(TRACE_LOG)){
				if(parameters.length == 3){
					if(serverLevelLogger instanceof EliteRollingFileLogger){
						if(ON.equalsIgnoreCase(parameters[2])){
							serverLevelLogger.setTraceLogLevelOn();
							return "Trace log on successfully";
						}else if(OFF.equalsIgnoreCase(parameters[2])){
							serverLevelLogger.setTraceLogLevelOFF();
							return "Trace log off successfully";
						}
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
			if(!parameters[0].equalsIgnoreCase(SERVER)){
				return false;
			}

			if(parameters.length > 1){
				if(LOG.equalsIgnoreCase(parameters[1]) || TRACE_LOG.equalsIgnoreCase(parameters[1])
						|| HELP.equalsIgnoreCase(parameters[1]) || QUESTION_MARK.equalsIgnoreCase(parameters[1])){
					return true;
				}
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
			out.println("Possible Options:");
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the NetVertex Server. ");
			out.println("     tracelog { on | off }");
			out.println("     		On/Off the trace log of the NetVertex Server. ");
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

	private void registerServerLevelCommands(){
		//this method is used to register commands that require configuration to execute
		List<ICommand> serverCommandList = new LinkedList<ICommand>();

		DataSourceCommand dsCommand = new DataSourceCommand();

		DBDetailProvider dbDetailProvider = new DBDetailProvider(serverContext);
		VoltDBDetailProvider voltDBDetailProvider = new VoltDBDetailProvider(serverContext);
		try {
			dsCommand.registerDetailProvider(dbDetailProvider);
			dsCommand.registerDetailProvider(voltDBDetailProvider);
			dsCommand.registerDetailProvider(new LDAPDetailProvider() {

				@Override
				protected Map<String, LDAPDataSource> getLDAPDatasourceMap() {
                    return serverContext.getServerConfiguration().getLDAPDSConfiguration()
                            .getDatasourceNameMap().values().stream()
                            .map(t -> (LDAPDataSource)t)
                            .collect(Collectors.toMap(LDAPDataSource::getDataSourceName,Function.identity()));
				}
			});
		} catch (RegistrationFailedException e) {
			getLogger().error( MODULE, "Failed to register detail provider in DataSource Command. Reason: " + e.getMessage());
			getLogger().trace(e);
		}

		try {
			dbDetailProvider.registerDetailProvider(new com.elitecore.netvertex.cli.db.StatisticsDetailProvider(serverContext));
			voltDBDetailProvider.registerDetailProvider(new com.elitecore.netvertex.cli.db.VoltDBStatisticsDetailProvider(serverContext));
		} catch (RegistrationFailedException e) {
			getLogger().error( MODULE, "Failed to register Statistics Detail Provider. Reason: "	+ e.getMessage());
			getLogger().trace(e);
		}

		try {
			dbDetailProvider.registerDetailProvider(new ScanDetailProvider(serverContext));
			voltDBDetailProvider.registerDetailProvider(new VoltDBScanDetailProvider(serverContext));
		} catch (RegistrationFailedException e) {
			getLogger().error( MODULE, "Failed to register Scan Detail Provider. Reason: " + e.getMessage());
			getLogger().trace(e);
		}

		try {
			dbDetailProvider.registerDetailProvider(new ReInitDetailProvider(getAllDataSources()));
			voltDBDetailProvider.registerDetailProvider(new VoltDBReInitDetailProvider(getAllDataSources(), serverContext));
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Failed to register re-init detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		serverCommandList.add(dsCommand);

		ConfigDetailProvider configDetailProvider = new ConfigDetailProvider(serverContext);

		try {
			ShowCommand.registerDetailProvider(configDetailProvider);
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register configuration detail provider. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		try {
			AlertCommand.registerDetailProvider(new AlertCountersDetailProvider() {

				@Override
				public IAlertData getSystemAlertData(String alertId) {
					return alertManager.getAlertData(alertId);
				}
			});
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Failed to register Alert counters detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		serverCommandList.add(new AlertCommand());

		adminService.addCliCommand(serverCommandList);
	}

	private Map<String, DBDataSource> getAllDataSources() {

		Map<String, DBDataSource> dataSourceMap = new HashMap<String, DBDataSource>();
		DBDataSource defaultDataSource = NetVertexDBConnectionManager.getInstance().getDataSource();
		dataSourceMap.put(defaultDataSource.getDataSourceName(), defaultDataSource);

		Map<String, DBDataSourceImpl> tempDataSourceMap = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasourceNameMap();
		dataSourceMap.putAll(tempDataSourceMap);

		return dataSourceMap;

	}

	@Override
	public NetVertexServerContext getServerContext() {
		return serverContext;
	}

	/**
	 * Server level scheduled task for performing server internal tasks. Current
	 * implementation validates server license at regular interval and if
	 * license is found invalid, it stops all the active services.
	 *
	 * @author Eltiecore Technologies Ltd.
	 *
	 */
	protected class EliteExpiryDateValidationTask extends BaseIntervalBasedTask{
		private static final String MODULE = "ExpiryDate Validator";
		private long initialDelay;
		private long intervalSeconds;

		public EliteExpiryDateValidationTask(long initialDelay,long intervalSeconds){
			this.initialDelay = initialDelay;
			this.intervalSeconds = intervalSeconds;
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
			if(isLicenseValid())
				if(!getServerContext().isLicenseValid("SYSTEM_EXPIRY",String.valueOf(System.currentTimeMillis()))){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Stopping all services, licence expiry detected");
					stopServices();
					setValidLicense(false);
				}

			if(!isLicenseValid()){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "License for NetVertex server instance is either not acquired or has expired.");
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_EXPIRED, MODULE, "License for NetVertex server instance is either not acquired or has expired.");
			}
		}
	}

	@Override
	public boolean validateLicenses(String key, String value) {
		if(licenseManager!=null) {
			return licenseManager.validateLicense(key, value);
		}
		return false;
	}

	@Override
	public String getLicenseValues(String key) {
		return null;
	}

	@Override
	public synchronized boolean reloadServerConfiguration() {
		try{

			serverConfiguration.reloadConfiguration();
			if(sessionManager != null){
				sessionManager.reloadSessionManagerConfiguration();
			}

			reloadPrefixConfigurable();

			if(pcrfService != null){
				pcrfService.reInit();
			}

			reloadLocationInformation();
			reloadDeviceInformation();
			reloadLRNConfigurations();
			this.gatewayController.reLoad();
			serverInfo.updateConfigurationReloadTime();

			return true;
		} catch(Exception ex){
			LogManager.getLogger().error(MODULE, ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
		return false;

	}

	public void initializeConfigLogger(String serverHome){
		try {
			ConfigLogger.getInstance().init(serverHome);
		} catch (InitializationFailedException e) {
			LogManager.getLogger().debug(MODULE,"Failed to initialize config logger.Reason"+ e.getMessage());
			LogManager.ignoreTrace(e);
		}
	}

	private void createServerContext() {
		serverContext = new NetVertexServerContextImpl();
	}

	private class NetVertexServerContextImpl extends BaseServerContext implements NetVertexServerContext {

		@Override
		public String getServerName() {
			return EliteNetVertexServer.this.getServerName();
		}

		@Override
		public String getServerVersion() {
			return Version.getVersion();
		}

		@Override
		public String getServerDescription() {
			return EliteNetVertexServer.this.getServerName();
		}

		@Override
		public boolean isLicenseValid(String key, String value) {
			return validateLicenses(LicenseNameConstants.SYSTEM_EXPIRY, String.valueOf(System.currentTimeMillis()))
					&& validateLicenses(key, value);
		}

		@Override
		public String getServerMajorVersion() {
			return Version.getMajorVersion();
		}

		@Override
		public NetvertexServerConfiguration getServerConfiguration() {
			return serverConfiguration;
		}


		@Override
		public PolicyRepository getPolicyRepository() {
			return policyManager;
		}

		@Override
		public VoltDBClientManager getVoltDBClientManager() {
			return voltDBClientManager;
		}

		@Override
		public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage) {
			generateSystemAlert(severity.name(),alertEnum,alertGeneratorIdentity,alertMessage);
		}

        @Override
        public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, Map<com.elitecore.core.serverx.alert.Alerts, Object> alertData) {
            generateSystemAlert(severity.name(), alertEnum, alertGeneratorIdentity, alertMessage, alertData);
        }

        private void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity,
                                         String alertMessage, Map<com.elitecore.core.serverx.alert.Alerts,Object> alertData) {
            alertManager.scheduleAlert(alertEnum, getServerInstanceID() +"/"+ alertGeneratorIdentity,severity, alertMessage, alertData);
        }

        @Override
		public void generateSystemAlert(AlertSeverity severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue) {
			generateSystemAlert(severity.name(),alertEnum,alertGeneratorIdentity,alertMessage, alertIntValue, alertStringValue);
		}

		@Override
		public void generateSystemAlert(String severity, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage) {

			Alerts netVertexAlert = null;
			if(alertEnum instanceof Alerts){
				netVertexAlert = (Alerts)alertEnum;

			}else if(alertEnum instanceof com.elitecore.core.serverx.alert.Alerts){
				netVertexAlert = Alerts.fromCoreAlert((com.elitecore.core.serverx.alert.Alerts)alertEnum);
				if(netVertexAlert == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Unable to generate Alert with EVENT: " + alertMessage
								+ ". Reason: NetVertex Alert is not mapped to any Core Alert: "
								+ alertEnum.getName());
					}
					return;
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Core Alert: " + alertEnum.getName() +
								" is mapped to Netvertex Alert: " + netVertexAlert.getName());
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Unable to resolve NetVertex Alert. Reason: Alert generated from Unknown Class: " + alertEnum.getClass());
				}
				return;
			}


			alertMessage = alertMessage.replaceAll(System.getProperty("line.separator"), " ").replaceAll("\n", " ");
			if(EliteNetVertexServer.this.currentState == LifeCycleState.SHUTDOWN_IN_PROGRESS)
				alertManager.generateAlert(netVertexAlert.getSeverity(), netVertexAlert, alertGeneratorIdentity, alertMessage);
			else
				alertManager.scheduleAlert(netVertexAlert, alertGeneratorIdentity,netVertexAlert.getSeverity(), alertMessage);

		}

		@Override
		public void generateSystemAlert(String name, IAlertEnum alertEnum, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue) {

			Alerts netVertexAlert = null;
			if(alertEnum instanceof Alerts){
				netVertexAlert = (Alerts)alertEnum;
			}else if(alertEnum instanceof com.elitecore.core.serverx.alert.Alerts){
				netVertexAlert = Alerts.fromCoreAlert((com.elitecore.core.serverx.alert.Alerts)alertEnum);
				if(netVertexAlert == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Unable to generate Alert with EVENT: " + alertMessage
								+ ". Reason: NetVertex Alert is not mapped to any Core Alert: "
								+ alertEnum.getName());
					}
					return;
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Core Alert: " + alertEnum.getName() +
								" is mapped to Netvertex Alert: " + netVertexAlert.getName());
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Unable to resolve NetVertex Alert. Reason: Alert generated from Unknown Class: " + alertEnum.getClass());
				}
				return;
			}

			alertMessage = alertMessage.replaceAll(System.getProperty("line.separator"), " ").replaceAll("\n", " ");
			if(EliteNetVertexServer.this.currentState == LifeCycleState.SHUTDOWN_IN_PROGRESS)
				alertManager.generateAlert(netVertexAlert.getSeverity(), netVertexAlert, alertGeneratorIdentity, alertMessage, alertIntValue, alertStringValue);
			else
				alertManager.scheduleAlert(netVertexAlert, alertGeneratorIdentity,netVertexAlert.getSeverity(), alertMessage, alertIntValue, alertStringValue);
		}

		@Override
		public boolean sendNotification(Template emailTemplate,	Template smsTemplate, PCRFResponse response) {

			return notificationAgent.sendNotification(emailTemplate, smsTemplate,response, null, NotificationRecipient.SELF);
		}

		@Override
		public boolean sendNotification(Template emailTemplate, Template smsTemplate, PCRFResponse response, Timestamp validityDate, NotificationRecipient recipient) {

			return notificationAgent.sendNotification(emailTemplate, smsTemplate, response, validityDate, recipient);
		}


		@Override
		public ExternalScriptsManager getExternalScriptsManager() {
			return null;
		}

		@Override
		public boolean isPrimaryServer() {
			return isPrimaryServer;
		}

		@Override
		public SPRInfo getSPRInfo(String subscriberIdentity) {
			return EliteNetVertexServer.this.getSPR(subscriberIdentity);
		}

		@Override
		public String getReleaseDate() {
			return Version.getReleaseDate();
		}

		@Override
		public void registerCacheable(Cacheable cacheable) {
			throw new UnsupportedOperationException("Register cacheable is not supported");
		}

		@Override
		public String getSVNRevision() {
			return Version.getSVNRevision();
		}

		@Override
		public long getServerStartUpTime() {
			return serverInfo.getServerStartUpTime().getTime();
		}

		@Override
		public String getLocalHostName() {
			return EliteNetVertexServer.this.getLocalHostName();
		}
		@Override
		public String getContact() {
			return "support@netvertex.com";
		}

		@Override
		public void sendSnmpTrap(SystemAlert systemAlert,
				SnmpAlertProcessor snmpTrapProcessor) {
			snmpAgent.sendTrap(systemAlert, snmpTrapProcessor);
		}

		@Override
		public void registerSnmpMib(SnmpMib snmpMib) {
			snmpAgent.registerMib(snmpMib);
	}


		@Override
		public boolean sendSyRequest(PCRFResponse pcrfResponse, DiameterPeerGroupParameter diameterPeerGroupParameter, @Nullable String primaryGatewayName, PCRFResponseListner responseListner,CommandCode commandCode) {
			return gatewayController.diameterGatewayController.sendSyRequest(pcrfResponse, diameterPeerGroupParameter, primaryGatewayName, responseListner, commandCode);
		}

		@Override
		public LocationRepository getLocationRepository() {
			return locationInfoManager;
		}

		@Override
		public DeviceManager getDeviceManager() {
			return deviceManager;
		}

        @Override
        public LRNConfigurationRepository getLRNConfigurationRepository() {
            return lrnConfigurable;
        }

        @Override
        public PrefixRepository getPrefixRepository() {
            return prefixConfigurable;
        }

        @Override
		public boolean isLocationBasedServicesEnabled() {
			return isLocationBasedServiceEnabled;
		}

		@Override
		public DiameterPeerState registerPeerStatusListener(String gatewayName, DiameterPeerStatusListener diameterPeerStatusListener) throws StatusListenerRegistrationFailException {
			if(gatewayController == null || gatewayController.diameterGatewayController == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to register peer status listen to gatewayId: "+ gatewayName + ".Reason: Netvertex Server startup in progress");
				throw new StatusListenerRegistrationFailException("Netvertex Server startup in progress", ListenerRegFailResultCode.STARTUP_IN_PROGRESS);
			}

			return gatewayController.diameterGatewayController.registerPeerStatusListener(gatewayName,diameterPeerStatusListener);

		}

		@Override
		public void registerCliCommand(List<ICommand> commandList) {
			if(adminService==null){
				return;
			}else{
				adminService.addCliCommand(commandList);
			}
		}

		@Override
		public long getLicencedMessagePerMinute() {
			return licencedMPM;
		}
		@Override
		public DBConnectionManager getDBConnMgr(String name) {
			return DBConnectionManager.getInstance(name);
		}

		/*
		 * Used for EliteAAA
		 * (non-Javadoc)
		 * @see com.elitecore.core.serverx.ServerContext#addTotalResponseTime(long)
		 */
		@Override
		public long addTotalResponseTime(long responseTime) {
			return 0;
		}

		/*
		 * Used for keeping track for request received in EliteAAA and EliteRM
		 * (non-Javadoc)
		 * @see com.elitecore.core.serverx.ServerContext#incrementTotalRequestCount()
		 */
		@Override
		public int addAndGetAverageRequestCount(int delta) {
			return 0;
		}

        @Override
        public EliteSnmpAgent getSNMPAgent() {
            return snmpAgent;
        }

        @Override
		public @Nullable
		String getSyGatewayName(String sySessionId) {
			if(gatewayController == null || gatewayController.diameterGatewayController == null){
				if(getLogger().isLogLevel(LogLevel.ERROR))
					getLogger().error(MODULE, "Unable to fetch Sy Counters.Reason: diameter module is disabled");
				return null;
			}

			return gatewayController.diameterGatewayController.getSyGatewayName(sySessionId);
		}

		@Override
		public Map<String, String> getSyCounter(String sySessionID) {
			if(gatewayController == null || gatewayController.diameterGatewayController == null){
				if(getLogger().isLogLevel(LogLevel.ERROR))
					getLogger().error(MODULE, "Unable to fetch Sy Counters.Reason: diameter module is disabled");
				return null;
			}

			return gatewayController.diameterGatewayController.getSyCounter(sySessionID);
		}

		@Override
		public long getTPSCounter() {

			if(pcrfService != null) {
				return (long)Math.ceil(pcrfService.getTotalReqPerMin() / 60.0);
			} else {
				return 0;
			}
		}

		private void writeCentralLicenseToFile(String licenseKey){
			File file = new File(getServerHome() + File.separator
					+ SYSTEM_PATH+ File.separator +"local_node.lic");
			try(FileWriter writer = new FileWriter(file,false)) {
				file.getParentFile().mkdirs();
				writer.write(licenseKey);
			} catch (IOException e){
				LogManager.getLogger().error(MODULE, "IOException while writing central license file. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		private String readCentralLicenseFromFile(){
			String licenseKey=null;
			try(BufferedReader reader = new BufferedReader(new FileReader(getServerHome() + File.separator
					+ SYSTEM_PATH+ File.separator +"local_node.lic"))) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Reading serialized license");
				}
				licenseKey = reader.readLine();
			} catch(IOException e){
				LogManager.getLogger().error(MODULE, "IOException while reading central LicenseKey. Reason: "+e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			return licenseKey;
		}

		@Override
		public void uploadLicense(int resultCode, String message, String licenseKey){
			if(resultCode == LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode() ||
					resultCode == LicenseMessages.LICENSE_DOES_NOT_EXIST.getMessageCode()){

				// Run in evaluation mode if and only if SM is not accessible or SM it self is not having the license to give.
				if(licenseManager == null){
					licenseManager = new EvaluationLicenseManager(EVALUATION_LICENSE_TPS,EVALUATION_LICENSE_DAYS);
				}
			} else {
				if(licenseManager == null){
					licenseManager = new SingleLicenseManager(licenseObservers);

					if(resultCode == LicenseMessages.INVALID_LICENSE_ON_SERVER_MANAGER.getMessageCode()){
						licenseManager.uploadLicense(readCentralLicenseFromFile(),this.getServerMajorVersion());
					}
				}
				if(resultCode == LicenseMessages.SUCCESS.getMessageCode()){
					licenseManager.uploadLicense(licenseKey,this.getServerMajorVersion());
					writeCentralLicenseToFile(licenseKey);
					setValidLicense(true);
				} else {
					getLogger().error(MODULE,"Error while requesting new license. Message Code: " + resultCode + ", Message: "+message );
				}
			}
		}
		@Override
		public void registerLicenseObserver(LicenseObserver licenseObserver){
			licenseObservers.add(licenseObserver);
		}
		@Override
		public long getLicenseTPS(){
			return licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_TPS)==null
				?0:Long.parseLong(licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_TPS));
		}
	}

	public class NetvertexGatewayController{
		private DiameterGatewayController diameterGatewayController;
		private RadiusGatewayController radiusGatewayController;

		public void init() {
			GatewayEventListener eventListener = new GatewayEventListener(serverContext) {

				@Override
				public RequestStatus eventReceived(PCRFRequest request, PCRFResponseListner responseListner) {
					return pcrfService.submitRequest(request, responseListner);
				}

				@Override
				public RequestStatus eventReceived(PCRFRequest request,
						PCRFResponseListner responseListner,
						PCCRuleExpiryListener pccRuleExpiryListener) {
					return pcrfService.submitRequest(request, responseListner,pccRuleExpiryListener);
				}

				@Override
				public RequestStatus eventReceived(PCRFRequest request) {
					return pcrfService.submitRequest(request);
				}
			};

			GatewayMediator mediator = new GatewayMediator(sessionManager.getSessionLookup());

			SessionHandler sessionHandler = new SessionHandler(sessionManager.getSessionOperation(), (PolicyRepository)policyManager);

			radiusGatewayController = new RadiusGatewayController(serverContext,
					eventListener,
					sessionManager.getSessionLookup(), sessionFactory);
			try{
				radiusGatewayController.init();
				mediator.setRadiusGatewayController(radiusGatewayController);
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Error in initializing Radius Gateway Controller. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			diameterGatewayController = new DiameterGatewayController(serverContext,
					eventListener,
					sessionManager.getSessionLookup(),
					mediator, sessionHandler, sessionFactory);

			try{
				diameterGatewayController.init();
				mediator.setDiameterGatewayController(diameterGatewayController);
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Error in initializing Diameter Gateway Controller. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			serverInstanceWebService.setGatewayStatusInfoProvider(new RadiusGatewayStatusInfoProviderImpl(radiusGatewayController), new DiameterGatewayStatusInfoProviderImpl(diameterGatewayController.getDiameterStack()));
			serverInfo.setGlobalListeners(createGlobalListnersInfo());
		}

		private List<GlobalListenersInfo> createGlobalListnersInfo() {
			List<GlobalListenersInfo> globalListenersInfoList = new ArrayList<>();
			globalListenersInfoList.add(new GlobalListenersInfo(CommunicationProtocol.RADIUS.name(),
					serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getRadiusListnerConf().getIPAddress(),
					serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getRadiusListnerConf().getPort(),
					gatewayController.getRadiusStartDate(),
					gatewayController.getRadiusStatus(),
					gatewayController.getRadiusRemarks()));
			globalListenersInfoList.add(new GlobalListenersInfo(CommunicationProtocol.DIAMETER.name(),
					serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getDiameterListenerConf().getIPAddress(),
					serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getDiameterListenerConf().getPort(),
					gatewayController.diameterGatewayController.getDiameterStackStartDate(),
					gatewayController.getDiameterStackStatus().toString(),
					gatewayController.diameterGatewayController.getDiameterStackRemarks()));
			return globalListenersInfoList;
		}

		public DiameterGatewayController getDiameterGatewayController() {
			return diameterGatewayController;
		}

		public RadiusGatewayController getRadiusGatewayController() {
			return radiusGatewayController;
		}



		public void reLoad() {
			radiusGatewayController.reInit();
			diameterGatewayController.reInit();
		}

		public List<GatewayInfo> getGatewayInformations(String gatewayType) {
			List<GatewayInfo> gatewayInfos = new ArrayList<GatewayInfo>();
			if(GatewayTypeConstant.DIAMETER.getVal().equals(gatewayType)) {
				gatewayInfos.addAll(diameterGatewayController.getAllGatewayInformation());
			} else if(GatewayTypeConstant.RADIUS.getVal().equals(gatewayType)) {
				gatewayInfos.addAll(radiusGatewayController.getAllGatewayInformation());
			} else {
				gatewayInfos.addAll(diameterGatewayController.getAllGatewayInformation());
				gatewayInfos.addAll(radiusGatewayController.getAllGatewayInformation());
			}
			return gatewayInfos;
		}

		public Status getDiameterStackStatus(){
			return diameterGatewayController.getDiameterStackStatus();
		}
		public Date getRadiusStartDate(){
			return radiusGatewayController.getRadiusStartDate();
		}
		public String getRadiusStatus(){
			return radiusGatewayController.getRadiusStatus();
		}
		public String getRadiusRemarks(){
			return radiusGatewayController.getRadiusRemarks();
		}

		public boolean stop(){
			if(diameterGatewayController != null)
				diameterGatewayController.stop();
			if(radiusGatewayController != null)
				radiusGatewayController.stop();
			return true;
		}
	}

	private static ILogger getLogger() {
		return LogManager.getLogger();
	}

	private void registerMBean(BaseMBeanController baseMBeanImpl) {
		try {

			adminService.registerMbean(baseMBeanImpl);

		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException | NullPointerException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private <T> void registerStandardMBean(T baseMBeanImpl, Class<T> clazz, String name) {
		try {
			adminService.registerStandardMbean(new StandardMBean(baseMBeanImpl, clazz), name);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException | NullPointerException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public String getServerInstanceID() {
		if(serverInstanceID != null) {
			return serverInstanceID;
		}
		setServerInstanceId(readServerInstanceId());
		return serverInstanceID;
	}

	private void setServerInstanceId(String encryptedInstanceId) {

		try {
			serverInstanceID = PasswordEncryption.getInstance().decrypt(encryptedInstanceId, PasswordEncryption.ELITECRYPT);
		} catch (NoSuchEncryptionException | DecryptionNotSupportedException | DecryptionFailedException e) {
			ignoreTrace(e);
			LogManager.getLogger().warn(MODULE,"Problem reading server instance id, reason: " + e.getMessage());
		}
	}

	public SPRInfo getSPR(String subscriberIdentity) {
		SPRInfo sprInfo = pcrfService.getSPR(subscriberIdentity);
		if(sprInfo != null)
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE,"SPR for subscriberIdentity:" + subscriberIdentity + " is fetched");
			}
		else
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE,"No SPR found for subscriberIdentity:" + subscriberIdentity);
			}
	  	return sprInfo;
	}

	public class ServerStatusControllerMbean extends BaseMBeanController implements ServerStatusMbean {

		private ServerStatusProvider serverStatusProvider;

		public ServerStatusControllerMbean(ServerStatusProvider serverStatusProvider) {
			this.serverStatusProvider = serverStatusProvider;
		}

		@Override
		public String getName() {
			return MBeanConstants.SERVER_INFO;
		}

		@Override
		public String whatIsYourStatus() {
			return serverStatusProvider.getStatus();
		}

	}

	public class GlobalListenerCommandImpl extends GlobalListnersCommand{

		@Override
		public String getDiameterStackAddress(){
			return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getDiameterListenerConf().getIPAddress();
		}
		@Override
		public int getDiameterStackPort(){
			return  serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getDiameterListenerConf().getPort();
		}
		@Override
		public Status getDiameterStackStatus(){
			return gatewayController.getDiameterStackStatus();
		}
		@Override
		public String getRadiusAddress(){
			return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getRadiusListnerConf().getIPAddress();
		}
		@Override
		public int getRadiusPort(){
			return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getRadiusListnerConf().getPort();
		}
		@Override
		public Date getRadiusStartDate(){
			return gatewayController.getRadiusStartDate();
		}
		@Override
		public String getRadiusStatus(){
			return gatewayController.getRadiusStatus();
		}
		@Override
		public String getRadiusRemarks(){
			return gatewayController.getRadiusRemarks();
		}
		@Override
		public Date getDiameterStackStartDate() {
			return gatewayController.diameterGatewayController.getDiameterStackStartDate();
		}
		@Override
		public String getDiameterStackRemarks() {
			return gatewayController.diameterGatewayController.getDiameterStackRemarks();
		}
	}

	@Override
	public String getServerInstanceName() {
		if(serverInstanceName != null)
			return serverInstanceName;

		try {
			serverInstanceName = PasswordEncryption.getInstance().decrypt(readServerInstanceName(), PasswordEncryption.ELITECRYPT);

		} catch (NoSuchEncryptionException | DecryptionNotSupportedException | DecryptionFailedException e) {
			ignoreTrace(e);
			LogManager.getLogger().warn(MODULE,"Problem reading server instance name, reason: " + e.getMessage());
		}

		if(serverInstanceName == null || serverInstanceName.trim().length() == 0){
			serverInstanceName = null;
			return getLocalHostName();
		}

		return serverInstanceName;
	}

	private String getServerIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LogManager.getLogger().error(MODULE,"Local Ip Address not found. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE , e);
		}

		return getServerInstanceName();
	}

	private void registerServerTrapsTables(){
		snmpAgent.registetSnmpTrapTable(new NETVERTEX_PCRF_MIBOidTable());
	}

	@Override
	protected String getStdLogFileName() {
		return "netvertex-std";
	}

	/**
	 * checks for port availability.
	 *
	 * IF port is available THEN
	 * 	take it
	 * ELSE
	 * 	check for free port
	 */
	@Override
	protected void startHtmlAdaptor(int httpPort) {

		try (ServerSocket socket = new ServerSocket(httpPort)){
			// This socket is created to check whether Port is already in use or not

		} catch (IOException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "configured HTTP port " + httpPort + " is busy. checking for available port");
				ignoreTrace(e);
			}
			try (ServerSocket socket = new ServerSocket(0)){
				// binds to any free port
				httpPort = socket.getLocalPort();
			} catch (IOException ioException) {
				LogManager.getLogger().warn(MODULE, "Skipping statistics expose to HTTP port. Reason: no free port available for HTTP");
				LogManager.getLogger().trace(ioException);
				return;
			}
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Statistics will be exposed to HTTP port: " + httpPort + " for HTML browser");
		}

		super.startHtmlAdaptor(httpPort);
	}

	public interface ServerStatusProvider {
		String getStatus();
	}

	private class ServerStatusProviderImpl implements ServerStatusProvider {

		@Override
		public String getStatus() {
			getLogger().debug(MODULE, "Providing status: " + currentState.message);
			return currentState.message;
		}
	}

	private void startRestApiListener() {

		getLogger().info(MODULE, "Starting Rest API Listener");
		SessionWebService sessionWebService = new SessionWebService(sessionManager.getSessionOperation(),
				reAuthorizationController,
				new SessionDisconnectController(sessionManager, gatewayController));


        ReloadRestService reloadRestService = new ReloadRestService(policyManager);
        LicenseWebService licenseWebService = new LicenseWebService(licenseManager,
				()->{
        	if(licenseTaskFuture!=null) {
				licenseTaskFuture.cancel(false);
			}
        });

		WebServiceCommandInterface webServiceCommandInterface = new WebServiceCommandInterface((command, parameters) -> {

            try {
                return adminService.executeCliCommand(command, parameters);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error while executing CLI command " + command
						+ ". Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
                return e.getMessage();
            }
        });

		restApiServer.addService(sessionWebService);
        restApiServer.addService(reloadRestService);
        restApiServer.addService(licenseWebService);
		restApiServer.addService(webServiceCommandInterface);

		ServiceDataProviderImpl serviceDataProvider = new ServiceDataProviderImpl();
		serverInfo.setServiceDescription(createServicesInfo(serviceDataProvider.getServiceDescriptionList()));

		restApiServer.startRestApiListener();
	}

	private List<ServiceInfo> createServicesInfo(List<ServiceDescription> serviceDescriptionList) {

		List<ServiceInfo> serviceInfoLists = new ArrayList<>();
		for(ServiceDescription serviceInfo: serviceDescriptionList){
			serviceInfoLists.add(new ServiceInfo(serviceInfo.getName(), serviceInfo.getStatus(), serviceInfo.getStartDate(), serviceInfo.getRemarks()));
		}
		return serviceInfoLists;
	}

}
