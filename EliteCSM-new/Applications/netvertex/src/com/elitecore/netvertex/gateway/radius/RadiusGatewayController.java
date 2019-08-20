package com.elitecore.netvertex.gateway.radius;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.EliteUDPService;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.servicex.UDPServiceRequest;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GatewayInfo;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrValueConstants;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.cli.AcctStatisticsDetailProvider;
import com.elitecore.netvertex.cli.AuthStatisticsDetailProvider;
import com.elitecore.netvertex.cli.DynaAuthStatisticsDetailProvider;
import com.elitecore.netvertex.cli.GatewayStatusCommand;
import com.elitecore.netvertex.cli.RadiusGatewayStatusDetailProvider;
import com.elitecore.netvertex.cli.RadiusPacketSendCommand;
import com.elitecore.netvertex.cli.RadiusStatisticsDetailProvider;
import com.elitecore.netvertex.cli.SendPacketCommand;
import com.elitecore.netvertex.cli.StatisticsDetailProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.GatewayEventListener;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;
import com.elitecore.netvertex.gateway.radius.dictionary.RadiusDictionaryLoader;
import com.elitecore.netvertex.gateway.radius.logmonitor.RadiusMonitor;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
import com.elitecore.netvertex.gateway.radius.mapping.PCRFRequestRadiusMappingValuProvider;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;
import com.elitecore.netvertex.gateway.radius.packet.impl.RadServiceRequestImpl;
import com.elitecore.netvertex.gateway.radius.packet.impl.RadServiceResponseImpl;
import com.elitecore.netvertex.gateway.radius.scripts.RadiusGroovyScript;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerClientStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerCounters;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.acct.RADIUS_ACC_SERVER_MIBImpl;
import com.elitecore.netvertex.gateway.radius.snmp.acct.RadiusAccClientEntryImpl;
import com.elitecore.netvertex.gateway.radius.snmp.acct.TableRadiusAccClientTableImpl;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.RADIUS_ACC_SERVER_MIB;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.TableRadiusAccClientTable;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerClientStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerCounters;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.auth.RADIUS_AUTH_SERVER_MIBImpl;
import com.elitecore.netvertex.gateway.radius.snmp.auth.RadiusAuthClientEntryImpl;
import com.elitecore.netvertex.gateway.radius.snmp.auth.TableRadiusAuthClientTableImpl;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.RADIUS_AUTH_SERVER_MIB;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.TableRadiusAuthClientTable;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientCounters;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientServerStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.RADIUS_DYNAUTH_CLIENT_MIBImpl;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.RadiusDynAuthServerEntryImpl;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.TableRadiusDynAuthServerTableImpl;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.RADIUS_DYNAUTH_CLIENT_MIB;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.TableRadiusDynAuthServerTable;
import com.elitecore.netvertex.gateway.radius.utility.AvpAccumalators;
import com.elitecore.netvertex.service.pcrf.PCCRuleExpiryListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.sun.management.snmp.SnmpStatusException;
import groovy.lang.GroovyObject;
import org.apache.logging.log4j.ThreadContext;
import org.hibernate.SessionFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.apache.logging.log4j.ThreadContext.put;


/**
 * Radius Gateway Controller is responsible to manage all Radius Gateways and Radius Global Listeners.
 *
 * @author Subhash Punani
 *
 */
public class RadiusGatewayController {

	private static final String STATUS_RUNNING = "Running";
	private static final String MODULE = "RAD-GTW-CTRL";
	private static final String DELAY = "DELAY";
	private static final String NAS_ADDRESS = "NASAddress";
	private static final String CLIENT_IP = "ClientIP";
	private static final String CLIENT_PORT = "ClientPort";
	private static final String PACKET_TYPE = "PacketType";
	private static final String ACCT_STATUS_VALUE = "Acct-Status-Value";
	private static final String ACCT_SESSION_ID = "Acct-Session-ID";
	private NetVertexServerContext serverContext;
	private SessionFactory sessionFactory;
	private AcctServerCounters radiusAcctServCounters;
	private AcctServerStatisticsProvider acctServerStatisticsProvider;
	private AcctServerClientStatisticsProvider acctServerClientStatisticsProvider;
	private TableRadiusAccClientTable radiusAccClientTable;
	private RADIUS_ACC_SERVER_MIB radiusAccServerMIB;


	private DynaAuthClientCounters dynaAuthClientCounters;
	private DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider;
	private DynaAuthClientServerStatisticsProvider dynaAuthClientServerStatisticsProvider;
	private TableRadiusDynAuthServerTable radiusDynaAuthServerTable;
	private RADIUS_DYNAUTH_CLIENT_MIB radiusDynauthClientMIB;


	private AuthServerCounters authServCounters;
	private AuthServerStatisticsProvider authServerStatisticsProvider;
	private AuthServerClientStatisticsProvider authServerClientStatisticsProvider;
	private TableRadiusAuthClientTable radiusAuthClientTable;
	private RADIUS_AUTH_SERVER_MIB radiusAuthServerMib;

	private RadiusStatisticsDetailProvider radiusStatisticsDetailProvider;

	private RadiusListenerConfiguration configuration;
	private GatewayEventListener eventListener;
	private RadiusGatewayEventListner radGatewayEventListener;
	private PCCRuleExpiryListenerImpl ruleExpiryListener;
	private Map<String, RadiusGateway> radiusGateways;
	private PCRFResponseListner acctPCRFResponseListner;
	private PCRFResponseListner authPCRFResponseListner;
	private SessionLocator sessionLocator;
	private AsyncRequestResponseCache requestReaponseCache;
	private RadiusGatewayControllerContext gatewayControllerContext;
	private HashMap<String, List<RadiusGroovyScript>> groovyScriptsMap;
	private RadiusMonitor radiusMonitor;

	private boolean initialized;

	private UsageConverter usageConverter;

	private boolean isEnabled;
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private boolean isLicenseValid=true;

	public RadiusGatewayController(NetVertexServerContext serverContext, GatewayEventListener eventListener,
								   SessionLocator sessionLocator, SessionFactory sessionFactory) {

		this.serverContext = serverContext;
		this.eventListener = eventListener;
		this.sessionFactory = sessionFactory;
		radGatewayEventListener = new RadiusGatewayEventListner(serverContext);
		radiusGateways = new HashMap<String, RadiusGateway>();
		this.sessionLocator = sessionLocator;
		ruleExpiryListener = new PCCRuleExpiryListenerImpl();
		configuration = serverContext.getServerConfiguration().getRadiusGatewayEventListenerConfiguration();
		requestReaponseCache = new AsyncRequestResponseCache(serverContext);
		gatewayControllerContext = new RadiusGatewayControllerContextImpl();
		radiusAcctServCounters = new AcctServerCounters(serverContext);
		acctServerStatisticsProvider = new AcctServerStatisticsProvider(radiusAcctServCounters);
		radiusAccServerMIB = new RADIUS_ACC_SERVER_MIBImpl(acctServerStatisticsProvider);
		acctServerClientStatisticsProvider =new AcctServerClientStatisticsProvider(radiusAcctServCounters);

		dynaAuthClientCounters = new DynaAuthClientCounters(serverContext);
		dynaAuthClientStatisticsProvider = new DynaAuthClientStatisticsProvider(dynaAuthClientCounters);
		dynaAuthClientServerStatisticsProvider = new DynaAuthClientServerStatisticsProvider(dynaAuthClientCounters);
		radiusDynauthClientMIB = new RADIUS_DYNAUTH_CLIENT_MIBImpl(dynaAuthClientStatisticsProvider);

		authServCounters = new AuthServerCounters(serverContext);
		authServerStatisticsProvider = new AuthServerStatisticsProvider(authServCounters);
		authServerClientStatisticsProvider = new AuthServerClientStatisticsProvider(authServCounters);
		radiusAuthServerMib = new RADIUS_AUTH_SERVER_MIBImpl(authServerStatisticsProvider);

		radiusStatisticsDetailProvider = new RadiusStatisticsDetailProvider(acctServerStatisticsProvider ,
				dynaAuthClientStatisticsProvider ,
				authServerStatisticsProvider );

		groovyScriptsMap = new HashMap<String, List<RadiusGroovyScript>>(1,1);

		usageConverter = new UsageConverter(gatewayControllerContext);
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10, new EliteThreadFactory(MODULE ,DELAY + "-SCH", Thread.NORM_PRIORITY));

		serverContext.registerLicenseObserver(this::checkLicenseValidity);

	}

	private void checkLicenseValidity(){
		isLicenseValid
				= serverContext.isLicenseValid(LicenseNameConstants.NV_RADIUS_INTERFACE,String.valueOf(System.currentTimeMillis()));

		if(isLicenseValid==false && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, " License for Radius Application is either not acquired or has expired.");
		}
	}

	public void init() throws InitializationFailedException{

		try{


			LogManager.getLogger().info(MODULE, "Initializing Radius Gateway Controller");

			if(configuration.isEnabled()  == false){
				LogManager.getLogger().info(MODULE, "Radius Gateway event listener is disabled");
				isEnabled = false;
				return;
			}

			loadDictionary();

			LogManager.getLogger().info(MODULE, "Initializing Radius Gateway event listener");
			radGatewayEventListener.init();

			if(STATUS_RUNNING.equalsIgnoreCase(radGatewayEventListener.radiusService.getStatus()) == false) {
				isEnabled = false;
			} else {
				isEnabled = true;
			}
			LogManager.getLogger().info(MODULE, "Radius Gateway event listener initialization completed");

			LogManager.getLogger().info(MODULE, "Initializing Radius Gateways");

			//@reloadable
			for(RadiusGatewayConfiguration gatewayConfiguration : serverContext.getServerConfiguration().getRadiusGatewayConfigurations()){
				RadiusGateway radiusGateway = new RadiusGateway(serverContext, gatewayConfiguration , dynaAuthClientCounters, scheduledThreadPoolExecutor);

				initRadiusGateway(gatewayConfiguration, radiusGateway);
			}
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();


			requestReaponseCache.init();

			acctPCRFResponseListner = new AcctPCRFResponseListnerImpl(gatewayControllerContext,radGatewayEventListener);
			authPCRFResponseListner = new AuthPCRFResponseListnerImpl(gatewayControllerContext,radGatewayEventListener,eventListener, requestReaponseCache,authServCounters);

			radiusAcctServCounters.init();
			dynaAuthClientCounters.init();
			authServCounters.init();

			checkLicenseValidity();
			registerDetailProviders();
			Collection<RadiusGatewayConfiguration> configurations = serverContext.getServerConfiguration().getRadiusGatewayConfigurations();
			registerAccServerMIB(server, configurations);
			registerDynaauthClientMIB(server, configurations);
			registerAuthServerMIB(server,configurations);
			registerDetailProvider();
			registerRadiusMonitor();
			initGroovyScripts();

			LogManager.getLogger().info(MODULE, "Radius Gateway Controller initialization completed");
			initialized = true;
		}catch(InitializationFailedException ex){
			throw ex;
		} catch(Exception ex) {
			throw new InitializationFailedException("Error while initializing radius gateway controlle.Reason: "+ex.getMessage(), ex);
		}

		try{
			GatewayStatusCommand.registerDetailProvider(new RadiusGatewayStatusDetailProvider( this::getRadiusGatewaysState));
		}catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Failed to register Radius Gateway Status Detail Provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	private void registerDetailProviders() {
		try {
			radiusStatisticsDetailProvider.registerDetailProvider(new AcctStatisticsDetailProvider(acctServerStatisticsProvider,
					acctServerClientStatisticsProvider));
			radiusStatisticsDetailProvider.registerDetailProvider(new AuthStatisticsDetailProvider(authServerStatisticsProvider,
					authServerClientStatisticsProvider));
			radiusStatisticsDetailProvider.registerDetailProvider(new DynaAuthStatisticsDetailProvider(dynaAuthClientStatisticsProvider,
					dynaAuthClientServerStatisticsProvider));
			StatisticsDetailProvider.getInstance().registerDetailProvider(radiusStatisticsDetailProvider);
		}catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Radius Statistics Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}

	private void registerAccServerMIB(MBeanServer server, Collection<RadiusGatewayConfiguration> configurations) throws Exception {
		try {
			radiusAccServerMIB.populate(server, null);
			radiusAccClientTable = new TableRadiusAccClientTableImpl(serverContext , radiusAccServerMIB, server);
			int clientIndex = 1;
			for(RadiusGatewayConfiguration configuration : configurations){
				clientIndex = addEntryIntoAccClientTable(clientIndex, configuration);
			}
			serverContext.registerSnmpMib(radiusAccServerMIB);
		} catch (IllegalAccessException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Radius Accounting Mib to Snmp Agent. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}

	private int addEntryIntoAccClientTable(int clientIndex, RadiusGatewayConfiguration configuration) {
		try {
			RadiusAccClientEntryImpl accClientEntryImpl = new RadiusAccClientEntryImpl(configuration.getIPAddress() ,acctServerClientStatisticsProvider , clientIndex);
			radiusAccClientTable.addEntry(accClientEntryImpl , new ObjectName(accClientEntryImpl.getObjectName()));
			clientIndex++;
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to register Radius Accounting Client Entry in Client Table. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
		return clientIndex;
	}

	private void registerDynaauthClientMIB(MBeanServer server, Collection<RadiusGatewayConfiguration> configurations) throws Exception {
		try {
			radiusDynauthClientMIB.populate(server, null);
			radiusDynaAuthServerTable = new TableRadiusDynAuthServerTableImpl(serverContext , radiusDynauthClientMIB, server);
			int serverIndex = 1;
			for(RadiusGatewayConfiguration configuration : configurations){
				serverIndex = addEntryIntoAuthServerTable(serverIndex, configuration);
			}
			serverContext.registerSnmpMib(radiusDynauthClientMIB);
		} catch (IllegalAccessException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Radius Dyna Auth Client Mib to Snmp Agent. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}

	private int addEntryIntoAuthServerTable(int serverIndex, RadiusGatewayConfiguration configuration) throws MalformedObjectNameException {
		try {
			RadiusDynAuthServerEntryImpl dynAuthServerEntry = new RadiusDynAuthServerEntryImpl(configuration.getIPAddress() , dynaAuthClientServerStatisticsProvider , serverIndex);
			radiusDynaAuthServerTable.addEntry(dynAuthServerEntry , new ObjectName(dynAuthServerEntry.getObjectName()));
			serverIndex++;
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Dyna Auth Server Entry(" + configuration.getIPAddress()
					+ ") not added in Server Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return serverIndex;
	}

	private void initRadiusGateway(RadiusGatewayConfiguration gatewayConfiguration, RadiusGateway radiusGateway) {
		try{
			radiusGateway.init();
			radiusGateways.put(gatewayConfiguration.getIPAddress(), radiusGateway);
		}catch(InitializationFailedException e){
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}

	public void registerAuthServerMIB(MBeanServer server, Collection<RadiusGatewayConfiguration> configurations) throws Exception{
		try {
			radiusAuthServerMib.populate(server, null);
			radiusAuthClientTable = new TableRadiusAuthClientTableImpl(serverContext , radiusAuthServerMib, server);
			int clientIndex = 1;
			for(RadiusGatewayConfiguration configuration : configurations){
				clientIndex = addEntryIntoAuthClientTable(clientIndex, configuration);
			}
			serverContext.registerSnmpMib(radiusAuthServerMib);
		} catch (IllegalAccessException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Radius Authentication Mib to Snmp Agent. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}

	private int addEntryIntoAuthClientTable(int clientIndex, RadiusGatewayConfiguration configuration) throws MalformedObjectNameException{
		try {
			RadiusAuthClientEntryImpl authClientEntry = new RadiusAuthClientEntryImpl(configuration.getIPAddress() ,authServerClientStatisticsProvider , clientIndex);
			radiusAuthClientTable.addEntry(authClientEntry , new ObjectName(authClientEntry.getObjectName()));
			return ++clientIndex;
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Auth Client Entry(" + configuration.getIPAddress()
					+ ") not added in Client Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return clientIndex;
		}
	}

	private void registerRadiusMonitor(){
		try {
			radiusMonitor = new RadiusMonitor(gatewayControllerContext);
			LogMonitorManager.getInstance().registerMonitor(radiusMonitor);
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE,"Radius Log Monitor Command registration failed. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void registerDetailProvider(){
		try {
			SendPacketCommand.registerDetailProvider(new RadiusPacketSendCommand());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE,"Radius Packet Send Command registration failed. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	public Map<String, Boolean> getRadiusGatewaysState() {
		Map<String, Boolean> gatewaysStateMap = new HashMap<String, Boolean>();
		for(Entry<String, RadiusGateway> entry: radiusGateways.entrySet()){
			gatewaysStateMap.put(entry.getKey(), entry.getValue().isAlive());
		}
		return gatewaysStateMap;
	}

	private void initGroovyScripts(){

		HashMap<String, List<RadiusGroovyScript>> tempRadiusGroovyScriptsMap = new HashMap<String, List<RadiusGroovyScript>>(1,1);
		for(RadiusGatewayConfiguration radiusGatewayConfiguration : serverContext.getServerConfiguration().getRadiusGatewayConfigurations()){

			List<ScriptData> scriptsDatas = radiusGatewayConfiguration.getScriptsConfigs();

			if(scriptsDatas == null || scriptsDatas.isEmpty()){
				LogManager.getLogger().debug(MODULE, "Groovy Scrip creation for radius gateway = " + radiusGatewayConfiguration.getIPAddress()
						+ " skipped. Reason: no groovy script configured for gateway");
				continue;
			}

			ArrayList<RadiusGroovyScript> radiusGroovyScripts = new ArrayList<RadiusGroovyScript>();
			for(ScriptData groovyScriptData : scriptsDatas){
				File scriptFile = new File(groovyScriptData.getScriptName());

				if(!scriptFile.isAbsolute()){
					scriptFile = new File(serverContext.getServerHome() + File.separator + "scripts" + File.separator , groovyScriptData.getScriptName());
				}

				try{
					GroovyObject object =  ExternalScriptsManager.createGroovyObject(scriptFile, new Class<?> []{RadiusGatewayControllerContext.class, RadiusGatewayConfiguration.class}, new Object[]{gatewayControllerContext, radiusGatewayConfiguration});

					if(object == null){
						getLogger().warn(MODULE, "Can't add Groovy Object for script file \""+scriptFile.getName()+"\" for RADIUS gateway = "
								+ radiusGatewayConfiguration.getIPAddress() + ". Reason: groovy scripts object not created for script file");
						continue;
					}

					if(object instanceof RadiusGroovyScript){
						RadiusGroovyScript radiusGroovyScript = (RadiusGroovyScript) object;
						radiusGroovyScript.init(groovyScriptData.getScriptArgumet());
						radiusGroovyScripts.add(radiusGroovyScript);
					} else {
						getLogger().warn(MODULE, "Can't add Groovy Object for script file \"" + scriptFile.getName() + "\" for RADIUS gateway = "
								+ radiusGatewayConfiguration.getIPAddress() + ". Reason: groovy scripts object is not instance of Radius Groovy Script");
					}
				}catch(Exception th){
					getLogger().error(MODULE, "Can't add Groovy Object for script file \"" + scriptFile.getName() + "\" for RADIUS gateway = "
							+ radiusGatewayConfiguration.getIPAddress() + ". Reason: " + th.getMessage());
					LogManager.getLogger().trace(MODULE, th);
				}
			}
			radiusGroovyScripts.trimToSize();
			tempRadiusGroovyScriptsMap.put(radiusGatewayConfiguration.getIPAddress(), radiusGroovyScripts);

		}

		groovyScriptsMap = tempRadiusGroovyScriptsMap;

	}

	/**
	 * Method is used to clean-up user sessions exist with NAS in following cases:
	 *  1. Accounting Request with Acct-Status-Type set to Acct-On / Acct-Off
	 *  2. NAS-Reboot-Reques
	 */


	class RadiusGatewayEventListner {
		private static final String MODULE = "RAD-EVENT-LSNR";
		private RadiusService radiusService;

		public RadiusGatewayEventListner(NetVertexServerContext serverContext) {
			radiusService = new RadiusService(serverContext);
		}

		public void init() throws InitializationFailedException {
			try {
				radiusService.init();
				boolean serviceStarted = radiusService.start();
				if(serviceStarted) {
					LogManager.getLogger().info(MODULE,"Radius Gateway event listener started successfully");
				}
				else{
					throw new InitializationFailedException("Unable to start Radius Gateway event listener");
				}

				if (Collectionz.isNullOrEmpty(radiusService.getListeningSocketDetails()) == false) {

					if (radiusService.getRemarks() != null) {
						if (radiusService.getRemarks().equals(ServiceRemarks.STARTED_ON_UNIVERSAL_IP.remark)) {
							configuration.setIpAddress(CommonConstants.UNIVERSAL_IP);

							for (SocketDetail socketDetail : radiusService.getListeningSocketDetails()) {
								configuration.setPort(socketDetail.getPort());
								break;
							}
						}
					}
				}
			} catch (ServiceInitializationException e) {
				throw new InitializationFailedException("Error in initializing Radius Gateway event listener, reason: " + e.getMessage(),e);
			}
		}

		private class RadiusService extends EliteUDPService<RadServiceRequest, RadServiceResponse> {

			private static final int DEFAULT_RADIUS_SERVICE_PORT = 2813;

			public RadiusService(ServerContext serverContext) {
				super(serverContext);
			}

			public void submitAsyncRequest(RadServiceRequest radServiceRequest,	RadServiceResponse radiusResponse) {

				RadiusService.this.submitAsyncRequest(radServiceRequest, radiusResponse, new AsyncRequestExecutorImpl());
			}

			@Override
			public String getServiceIdentifier() {
				return "RAD-LSNR";
			}

			@Override
			protected int getMinThreadPoolSize() {
				return configuration.getMinimumThread();
			}

			@Override
			protected int getMaxThreadPoolSize() {
				return configuration.getMaximumThread();
			}

			@Override
			protected int getMainThreadPriority() {
				return configuration.getMainThreadPriority();
			}

			@Override
			protected int getWorkerThreadPriority() {
				return configuration.getWorkerThreadPriority();
			}

			@Override
			protected int getThreadKeepAliveTime() {
				return (1000 * 60 * 60);
			}

			@Override
			protected int getMaxRequestQueueSize() {
				return configuration.getQueueSize();
			}

			@Override
			protected int getSocketReceiveBufferSize() {
				return configuration.getSocketReceiveBufferSize();
			}

			@Override
			protected int getSocketSendBufferSize() {
				return configuration.getSocketSendBufferSize();
			}

			@Override
			public RadServiceRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
				return new RadServiceRequestImpl(requestBytes,sourceAddress,  sourcePort, serverSocketDetail);
			}

			@Override
			public RadServiceResponse formServiceSpecificResposne(RadServiceRequest serviceRequest) {
				RadServiceResponseImpl response = new RadServiceResponseImpl(serviceRequest.getAuthenticator(), serviceRequest.getIdentifier());

				if(serviceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
				}else if (serviceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
					response.setResponseMessage("Authentication Rejected");
				}else if(serviceRequest.getPacketType() == RadiusConstants.NAS_REBOOT_REQUEST){
					response.setPacketType(RadiusConstants.NAS_REBOOT_RESPONSE);
				}

				Collection<IRadiusAttribute> proxyStateAttr = serviceRequest.getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);
				if(proxyStateAttr != null){
					for (IRadiusAttribute radiusAttribute : proxyStateAttr) {
						response.addAttribute(radiusAttribute);
					}
				}

				return response;
			}

			@Override
			public void handleServiceRequest(RadServiceRequest request, RadServiceResponse response) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(RadiusGatewayEventListner.MODULE, "Request Received from : "+request.getSourceAddress()+":"+request.getSourcePort());
					LogManager.getLogger().info(RadiusGatewayEventListner.MODULE, request.toString());
				}

				if(isLicenseValid == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Application: Radius" +
								" is not supported. Marking the request with id "+request.getIdentifier()+" for drop action");
					}
					response.markForDropRequest();
					return;
				}

				RadServiceRequest radServiceRequest = request;
				String gtwAddress = null;

				IRadiusAttribute gatewayAddress = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
				if(gatewayAddress != null){
					gtwAddress = gatewayAddress.getStringValue();
				}
				if(gtwAddress == null){
					gatewayAddress = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
					if(gatewayAddress != null){
						gtwAddress = gatewayAddress.getStringValue();
					}
				}
				if(gtwAddress == null){
					gatewayAddress = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IPV6_ADDRESS);
					if(gatewayAddress != null){
						gtwAddress = gatewayAddress.getStringValue();
					}
				}
				if(gtwAddress == null){
					LogManager.getLogger().error(MODULE, "Invalid radius request. Reason: " +
							"None of NAS_IP_ADDRESS, NAS_IPV6_ADDRESS, NAS_IDENTIFIER found in request packet");
					radiusAcctServCounters.incMalformedReqCntr(radServiceRequest.getClientIp());
					response.markForDropRequest();
					return;
				}
				(response).setNASAddress(gtwAddress);

				put(NAS_ADDRESS, gtwAddress);
				if(Strings.isNullOrBlank(radServiceRequest.getClientIp()) == false){
					put(CLIENT_IP, radServiceRequest.getClientIp());
				}
				put(CLIENT_PORT, String.valueOf(radServiceRequest.getClientPort()));
				put(PACKET_TYPE, String.valueOf(radServiceRequest.getPacketType()));


				RadiusGateway radiusGateway = radiusGateways.get(radServiceRequest.getClientIp());
				if(radiusGateway == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))

						LogManager.getLogger().warn(MODULE, "Unknown RADIUS client: " + radServiceRequest.getClientIp() + ":" + radServiceRequest.getClientPort() + ". So dropping this request");

					if(radServiceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
						radiusAcctServCounters.incTotalInvalidReqCntr();
					}else if(radServiceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
						authServCounters.incTotalInvalidReqCntr();
					}
					response.markForDropRequest();
					return;
				}

				(response).setRadiusGateway(radiusGateway);

				if(radServiceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					if(!radiusGateway.isAccountingResponseEnable()){
						response.markForDropRequest();
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))

							LogManager.getLogger().info(MODULE, "Accounting Response is Disabled. So marking for drop response");

					}

					handleAcctRequest(radServiceRequest , ((RadServiceResponse) response));

				}else if(radServiceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					handleAuthRequest(radServiceRequest,((RadServiceResponse) response));
				}else if(radServiceRequest.getPacketType() == RadiusConstants.NAS_REBOOT_REQUEST){
					handleGatewayRebootRequest(radServiceRequest,((RadServiceResponse) response));
				}
			}

			private void handleAuthRequest(RadServiceRequest request , RadServiceResponse response) {
				try {

					RadiusGatewayConfiguration gatewayConfig;
					gatewayConfig = serverContext.getServerConfiguration().getRadiusGatewayConfiguration(response.getNASAddress());
					if(gatewayConfig == null){
						gatewayConfig = response.getRadiusGateway().getConfiguration();
					}

					applyScriptsForReceivedPacket(request, response, gatewayConfig.getIPAddress());

					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Selecting packet mapping of gateway: " + gatewayConfig.getConnectionURL());

					RadiusToPCCMapping arMappings = gatewayConfig.getARMappings();

					if (arMappings == null) {
						if(LogManager.getLogger().isErrorLogLevel()){
							LogManager.getLogger().error(MODULE, "Sending Access Reject. Reason: No mapping found for " +
									"Access Request in gateway: " + gatewayConfig.getConnectionURL());
						}
						return;
					}

					PCCToRadiusMapping aaMappings = gatewayConfig.getAAMappings();

					if(aaMappings == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
							LogManager.getLogger().error(MODULE, "Sending Access Reject. Reason: No mapping found for " +
									"Access Response in gateway: " + gatewayConfig.getConnectionURL());
						}
						return;
					}

					PCRFRequest pcrfRequest = new PCRFRequestImpl();

					if (arMappings.apply(new PCRFRequestRadiusMappingValuProvider(request, response, pcrfRequest, gatewayConfig)) == false) {
						if (LogManager.getLogger().isErrorLogLevel()) {
							LogManager.getLogger().error(MODULE, "PCRF Request generation skipped. Reason: No mapping found for received " +
									"Access Request in gateway: " + gatewayConfig.getConnectionURL());
						}
						return;
					}

					(response).setFurtherProcessingRequired(false);
					(response).setProcessingCompleted(false);

					applyScriptsForReceivedPacket(request, response, pcrfRequest, gatewayConfig.getIPAddress());
					RequestStatus requestStatus = eventListener.eventReceived(pcrfRequest, authPCRFResponseListner, ruleExpiryListener);

					if(RequestStatus.SUBMISSION_SUCCESSFUL != requestStatus){
						response.markForDropRequest();
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Radius Auth request dropped. Reason:" +
									" PCRF Request status: " + requestStatus.getVal());
						}
					}else{
						requestReaponseCache.addRequestResponse(request, response , pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
					}

				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, "Error while processing RADIUS request. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}

			private void applyScriptsForReceivedPacket(RadServiceRequest rasServiceReq, RadServiceResponse radServiceRes, PCRFRequest pcrfRequest, String ipAddress){
				List<RadiusGroovyScript> scripts = groovyScriptsMap.get(ipAddress);
				if(scripts != null && !scripts.isEmpty()){
					for(RadiusGroovyScript script : scripts){
						try{
							script.postReceived(rasServiceReq, radServiceRes, pcrfRequest);
						}catch(Exception ex){
							LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + ipAddress +". Reason: "+ ex.getMessage());
							LogManager.getLogger().trace(MODULE, ex);
						}

					}
				}
			}

			private void applyScriptsForReceivedPacket(RadServiceRequest rasServiceReq, RadServiceResponse radServiceRes, String ipAddress){
				List<RadiusGroovyScript> scripts = groovyScriptsMap.get(ipAddress);
				if(scripts != null && !scripts.isEmpty()){
					for(RadiusGroovyScript script : scripts){
						try{
							script.postReceived(rasServiceReq, radServiceRes);
						}catch(Exception ex){
							LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + ipAddress +". Reason: "+ ex.getMessage());
							LogManager.getLogger().trace(MODULE, ex);
						}

					}
				}
			}

			private void handleGatewayRebootRequest(RadServiceRequest request, RadServiceResponse response){


				PCRFRequest pcrfRequest = new PCRFRequestImpl();
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, response.getNASAddress());
				pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.val, GatewayTypeConstant.RADIUS.val);
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RADIUS.val);
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(),request.getClientIp()+":"+request.getClientPort()+":"+request.getIdentifier()+ ":"+SessionTypeConstant.RADIUS.getVal()+":"+pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
				pcrfRequest.addPCRFEvent(PCRFEvent.GATEWAY_REBOOT);

				eventListener.eventReceived(pcrfRequest, (PCRFResponse pcrfResponse)->{
					//ignore
				});
			}

			private void handleAcctRequest(RadServiceRequest request , RadServiceResponse response){

				try {

					IRadiusAttribute acctStatusType = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
					if(acctStatusType == null) {
						LogManager.getLogger().error(MODULE, "Invalid radius accounting request. Reason: Acct-Status-Type attribute not found");
						radiusAcctServCounters.incMalformedReqCntr(request.getClientIp());
						return;
					}

					int acctStatusValue = acctStatusType.getIntValue();
					put(ACCT_STATUS_VALUE, String.valueOf(acctStatusValue));

					if(RadiusAttributeValuesConstants.ACCOUNTING_ON == acctStatusValue ||
							RadiusAttributeValuesConstants.ACCOUNTING_OFF == acctStatusValue){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Handling accounting request with Acct-Status-Type: " + acctStatusValue);

						handleGatewayRebootRequest(request, response);
						return;
					}


					IRadiusAttribute acctSessionId = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);
					if(acctSessionId == null) {
						LogManager.getLogger().error(MODULE, "Invalid radius accounting request. Reason: Acct-Session-Id attribute not found");
						radiusAcctServCounters.incMalformedReqCntr(request.getClientIp());
						return;
					}

					put(ACCT_SESSION_ID, acctSessionId.getStringValue());

					RadiusGatewayConfiguration gatewayConfig;
					gatewayConfig = serverContext.getServerConfiguration().getRadiusGatewayConfiguration(response.getNASAddress());
					if(gatewayConfig == null){
						gatewayConfig = response.getRadiusGateway().getConfiguration();
					}



					applyScriptsForReceivedPacket(request, response, gatewayConfig.getIPAddress());

					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Selecting packet mapping of gateway: " + gatewayConfig.getConnectionURL());


					RadiusToPCCMapping acrMappings = gatewayConfig.getACRMappings();

					if(acrMappings == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
							LogManager.getLogger().error(MODULE, "PCRF Request generation skipped. Reason: No mapping found for received " +
									"Accounting Request in gateway: " + gatewayConfig.getConnectionURL());
						}
						return;
					}

					if(PolicyEnforcementMethod.COA == gatewayConfig.getPolicyEnforcementMethod()) {
						PCCToRadiusMapping coaMappings = gatewayConfig.getCOAMappings();
						if(coaMappings == null) {
							if(LogManager.getLogger().isErrorLogLevel()){
								LogManager.getLogger().error(MODULE, "PCRF Request generation skipped. Reason: No mapping found for " +
										"CoA Request in gateway: " + gatewayConfig.getConnectionURL());
							}
							return;
						}
					}

					PCRFRequest pcrfRequest = new PCRFRequestImpl();

					if(acrMappings.apply(new PCRFRequestRadiusMappingValuProvider(request, response, pcrfRequest, gatewayConfig)) == false){
						if(LogManager.getLogger().isErrorLogLevel()) {
							LogManager.getLogger().error(MODULE, "PCRF Request generation skipped. Reason: No mapping found for received " +
									"Accounting Request in gateway: " + gatewayConfig.getConnectionURL());
						}
						return;
					}

					locateCoreSession(pcrfRequest, gatewayConfig);
					if(acctStatusValue == RadiusAttributeValuesConstants.START){

						if(pcrfRequest.isSessionFound() == true){
							Set<PCRFEvent> pcrfEvents =  pcrfRequest.getPCRFEvents();
							pcrfEvents.remove(PCRFEvent.SESSION_START);
							pcrfEvents.add(PCRFEvent.SESSION_UPDATE);
						}

						IRadiusAttribute wimaxBeginningOfSession = request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID,WimaxAttrConstants.BEGINNING_OF_SESSION.getIntValue());
						if(wimaxBeginningOfSession != null && wimaxBeginningOfSession.getIntValue() == WimaxAttrValueConstants.BEGINNING_OF_SESSION_FALSE){
							if(pcrfRequest.isSessionFound() == true){
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
									LogManager.getLogger().debug(MODULE, "Skipping further processing for Radius request. Reason : Session already exists for Session Id : "+
											pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
								}
								return;
							}
						}
					}

					if(gatewayConfig.getUsageReportingType() == PCRFKeyValueConstants.USAGE_REPORTING_TYPE_CUMULATIVE){
						usageConverter.convert(pcrfRequest);
					}

					pcrfRequest.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, gatewayConfig.getRevalidationMode().val);
					if(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "PCRF request: " + pcrfRequest.toString());
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Dropping request. Reason: Cs.Session_Id attribute not found.");
						return;
					}
					applyScriptsForReceivedPacket(request, response, pcrfRequest, gatewayConfig.getIPAddress());
					RequestStatus requestStatus = eventListener.eventReceived(pcrfRequest, acctPCRFResponseListner, ruleExpiryListener);
					if(RequestStatus.SUBMISSION_SUCCESSFUL != requestStatus){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Radius Acct request dropped. Reason:" +
									" PCRF Request status: " + requestStatus.getVal());
						}
					}

				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, "Error while processing RADIUS request. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}

			private void locateCoreSession(PCRFRequest request, RadiusGatewayConfiguration radiusGatewayConfiguration) {
				if(request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()) == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Core session ID not found, skipping further processing.");
					return;
				}

				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Locating Core Session for CoreSessionID = " + request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));

				String coreSessionId = request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
				SessionData session = sessionLocator.getCoreSessionByCoreSessionID(coreSessionId, request, radiusGatewayConfiguration.getInterimIntervalPredicate());
				if(session != null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "core Session Located.");
					}
					PCRFPacketUtil.buildPCRFRequest(session, request, false);
				} else {
					((PCRFRequestImpl)request).setSessionFound(false);
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Radius session not found");
				}
			}

			@Override
			protected ServiceContext getServiceContext() {
				return null;
			}

			@Override
			public void readConfiguration() throws LoadConfigurationException {
				//Sonar ignore
			}

			@Override
			public String getKey() {
				return null;
			}

			@Override
			public boolean isDuplicateDetectionEnabled() {
				return configuration.isDuplicateRequestDetectionEnabled();
			}

			@Override
			public int getDuplicateDetectionQueuePurgeInterval(){
				return configuration.getDupicateRequestQueuePurgeInterval();
			}

			@Override
			protected void incrementResponseCounter(ServiceResponse response) {
				RadServiceResponse radServiceResponse = (RadServiceResponse)response;
				incrementResponseCounter(radServiceResponse.getClintIp(),
						radServiceResponse.getPacketType());
			}

			@Override
			protected void incrementResponseCounter(String sourceAddress,
													byte[] responseBytes) {
				incrementResponseCounter(sourceAddress,
						RadiusPacket.parsePacketType(responseBytes));
			}

			private void incrementResponseCounter(String sourceAddress,
												  int packetType) {
				switch (packetType) {
					case RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE:
						radiusAcctServCounters.incResCntr(sourceAddress);
						break;
					case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
						authServCounters.incAccessAcceptCntr(sourceAddress);
						break;
					case RadiusConstants.ACCESS_REJECT_MESSAGE:
						authServCounters.incAccessRejectCntr(sourceAddress);
						break;
				}
			}

			@Override
			protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
				incrementRequestDroppedCounter(request);
			}

			@Override
			protected void incrementRequestDroppedCounter(ServiceRequest request){
				RadServiceRequest radServiceRequest = (RadServiceRequest)request;
				if(radServiceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					radiusAcctServCounters.incPackDropCntr(radServiceRequest.getClientIp());
				}else if(radServiceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					authServCounters.incPackDropCntr(radServiceRequest.getClientIp());
				}
			}

			@Override
			protected void incrementDuplicateRequestReceivedCounter(ServiceRequest request){
				RadServiceRequest radServiceRequest = (RadServiceRequest)request;
				if(radServiceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					radiusAcctServCounters.incDupReqCntr(radServiceRequest.getClientIp());
				}else if(radServiceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					authServCounters.incDupReqCntr(radServiceRequest.getClientIp());
				}
			}

			@Override
			public String getServiceName() {
				return "Radius Listner";
			}


			@Override
			protected void applyMonitoryLogLevel(RadServiceRequest udpServiceRequest,
												 RadServiceResponse udpServiceResponse) {
				try{
					if(radiusMonitor != null && radiusMonitor.evaluate(udpServiceRequest, udpServiceResponse)){
						LogManager.getLogger().addThreadName(Thread.currentThread().getName());
						udpServiceRequest.setParameter(MonitorLogger.MONITORED, true);
						udpServiceResponse.setParameter(MonitorLogger.MONITORED, true);
					}
				}catch(Exception ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in applying radius monitor log. Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}


			}

			@Override
			protected void removeMonitoryLogLevel(RadServiceRequest udpServiceRequest,
												  RadServiceResponse udpServiceResponse) {
				try{
					if(udpServiceRequest.getParameter(MonitorLogger.MONITORED) != null)
						LogManager.getLogger().removeThreadName(Thread.currentThread().getName());
				}catch(Exception ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in removing radius monitor log. Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}finally {
					clearContextInformation();
				}
			}

			private void clearContextInformation(){
				if(ThreadContext.isEmpty() == false){
					ThreadContext.clearAll();
				}
			}


			@Override
			public void handleAsyncServiceRequest(RadServiceRequest request, RadServiceResponse response) {
				//SONAR ignore
			}


			@Override
			protected void incrementRequestReceivedCounter(
					UDPServiceRequest serviceRequest) {
				RadServiceRequest radServiceRequest = (RadServiceRequest)serviceRequest;
				if(radServiceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					radiusAcctServCounters.incReqCntr(radServiceRequest.getClientIp());

					IRadiusAttribute acctStatusType = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
					if(acctStatusType!=null){
						if(acctStatusType.getIntValue() == RadiusAttributeValuesConstants.START){
							radiusAcctServCounters.incStartReqCntr(radServiceRequest.getClientIp());
						}else if(acctStatusType.getIntValue() == RadiusAttributeValuesConstants.INTERIM_UPDATE){
							radiusAcctServCounters.incIntrUpdateReqCntr(radServiceRequest.getClientIp());
						}else if(acctStatusType.getIntValue() == RadiusAttributeValuesConstants.STOP){
							radiusAcctServCounters.incStopReqCntr(radServiceRequest.getClientIp());
						}
					}
				}else if(radServiceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					authServCounters.incReqCntr(radServiceRequest.getClientIp());
				}
			}

			@Override
			public List<SocketDetail> getSocketDetails() {
				ArrayList<SocketDetail> socketDetails = new ArrayList<SocketDetail>();
				socketDetails.add(new SocketDetail(configuration.getIPAddress(), configuration.getPort()));
				return socketDetails;
			}

			@Override
			public int getDefaultServicePort() {
				return DEFAULT_RADIUS_SERVICE_PORT;
			}
		}

		public boolean stop(){
			if(radiusService!=null){
				radiusService.stop();
				radiusService.doFinalShutdown();
			}
			return true;
		}

		public void submitAsyncRequest(RadServiceRequest radServiceRequest,
									   RadServiceResponse radServiceResponse) {
			radiusService.submitAsyncRequest(radServiceRequest, radServiceResponse);
		}

		public void sendDisconnectMessage(PCRFResponse response){
			RadiusGatewayController.this.sendDisconnectMessage(response);
		}
	}

	public Date getRadiusStartDate(){
		return radGatewayEventListener.radiusService.getStartDate();
	}
	public String getRadiusStatus(){
		return radGatewayEventListener.radiusService.getStatus();
	}
	public String getRadiusRemarks(){
		return radGatewayEventListener.radiusService.getRemarks();
	}

	public void handleSessionReAuthorization(PCRFRequest request) {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "PCRF request for Session Re-Authorization" + request.toString());

		RadiusGateway gateway = radiusGateways.get(request.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
		if(gateway == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Gateway configuration not found for Address: " +
						"" + request.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
			}
			gateway = radiusGateways.get(request.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
		}

		if(gateway == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Dropping Session Re-Authorization request. Reason: Gateway configuration not " +
						"found for Address: " + request.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
			}
			return;
		}



		PolicyEnforcementMethod enforcementMethod = gateway.getConfiguration().getPolicyEnforcementMethod();
		switch(enforcementMethod){
			case COA :
			case ACCESS_ACCEPT :
				request.addPCRFEvent(PCRFEvent.AUTHENTICATE);
				request.addPCRFEvent(PCRFEvent.REAUTHORIZE);
				request.addPCRFEvent(PCRFEvent.SESSION_UPDATE);

				break;

			case Cisco_SCE_API :
			case NONE :
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().warn(MODULE, "Dropping Session Re-Authorization request. Reason: Policy Enforcement " +
							"Method for Gateway " + gateway.getConfiguration().getConnectionURL() + " is: " + enforcementMethod.getVal()+" So, Skipping further processing");
				}
				return;
		}

		request.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.RADIUS.getVal());

		RequestStatus requestStatus = eventListener.eventReceived(request, acctPCRFResponseListner, ruleExpiryListener);
		if(RequestStatus.SUBMISSION_SUCCESSFUL != requestStatus){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Session Re-Authorization for Radius session failed. " +
						"Reason: PCRF Request status: " + requestStatus.getVal());
			}
		}
	}

	public void handleSessionDisconnectRequest(PCRFRequest pcrfRequest){
		PCRFResponse pcrfResponse = new PCRFResponseImpl();

		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);
		sendDisconnectMessage(pcrfResponse);
	}

	private void sendDisconnectMessage(PCRFResponse response) {
		try {

			RadiusGateway gateway = radiusGateways.get(response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
			if(gateway == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Gateway configuration not found for Address: " +
							response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
				}
				gateway = radiusGateways.get(response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
			}

			if(gateway == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Dropping Disconnection Request. Gateway configuration not found for " +
							"Address: " + response.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal()));
				}
				return;
			}

			applyScriptsForSendPacket(response, gateway.getIPAddress());

			PolicyEnforcementMethod enforcementMethod = gateway.getConfiguration().getPolicyEnforcementMethod();
			if(PolicyEnforcementMethod.COA == enforcementMethod || PolicyEnforcementMethod.ACCESS_ACCEPT == enforcementMethod){

				RadiusPacket radiusPacket = new RadiusPacket();
				boolean findMapping = false;
				RadiusGatewayConfiguration configuration = gateway.getConfiguration();
				PCCToRadiusMapping dcRequestMappings = configuration.getDCRMappings();
				if(dcRequestMappings!=null){
					if (dcRequestMappings.apply(new PCCtoRadiusMappingValueProvider(response, radiusPacket, configuration, gatewayControllerContext),
							AvpAccumalators.of(radiusPacket))) {
						findMapping = true;
					}
				}

				if(!findMapping){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Dropping Disconnection Request. Reason: No mapping found for " +
								"PCRF response in gateway = " + gateway.getIPAddress());
					}
					return;
				}


				if(radiusPacket != null) {
					applyScriptsForSendPacket(radiusPacket, response, gateway.getIPAddress());
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, radiusPacket.toString());
					gateway.sendRequest(radiusPacket);
				}

			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Dropping Disconnection Request. Reason: Policy Enforcement " +
							"Method for Radius Gateway: " + gateway.getConfiguration().getConnectionURL() +" is " + enforcementMethod.getVal()+" So, Skipping further processing");
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while processing disconnect request. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private class PCCRuleExpiryListenerImpl extends PCCRuleExpiryListener {

		@Override
		public void reAuthSession(PCRFRequest request) {
			request.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.RADIUS.getVal());
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "PCRF request for Re-Authorization : " + request.toString());
			RequestStatus requestStatus = eventListener.eventReceived(request, acctPCRFResponseListner, ruleExpiryListener);
			if(RequestStatus.SUBMISSION_SUCCESSFUL != requestStatus){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Session Re-Authorization for Radius session failed. " +
							"Reason: PCRF Request status: " + requestStatus.getVal());
				}
			}
		}

	}

	public List<GatewayInfo> getAllGatewayInformation() {
		List<GatewayInfo> gatewayInfos = new ArrayList<GatewayInfo>();
		for(RadiusGateway gateway : radiusGateways.values())
			gatewayInfos.add(new GatewayInfo(GatewayTypeConstant.RADIUS.getVal(), null, null, gateway.getConfiguration().getConnectionURL(), gateway.isAlive() == true ? "Active" : "In Active", getActiveSessionCount(gateway.getIPAddress())));
		return gatewayInfos;
	}

	private long getActiveSessionCount(String ipAddress) {
		List<SessionData> sessionDatas = sessionLocator.getCoreSessionByGatewayAddress(ipAddress);
		return sessionDatas != null ? sessionDatas.size() : 0;
	}

	private boolean loadDictionary() {
		try {
			// Supported Vendors License Check
			getLogger().info(MODULE, "Initializing Radius Dictionary");

			RadiusDictionaryLoader radiusDictionaryLoader = new RadiusDictionaryLoader(sessionFactory, serverContext);
			radiusDictionaryLoader.load(Dictionary.getInstance());

			getLogger().info(MODULE, "Initializing Radius Dictionary successfully");
			return true;
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Error in initializing Radius Dictionary. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}
	}

	public boolean stop() {
		if (radiusGateways != null) {
			for (Entry<String, RadiusGateway> entry : radiusGateways.entrySet()) {
				entry.getValue().stop();
			}
		}
		if (radGatewayEventListener != null) {
			radGatewayEventListener.stop();
		}

		scheduledThreadPoolExecutor.shutdown();
		try {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Waiting for server level Scheduled async task executor to complete execution");
			}
			if (scheduledThreadPoolExecutor.awaitTermination(2, TimeUnit.SECONDS) == false) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Shutting down delaying COA  thread pool executor forcefully, Reason: Async task is taking more time to complete");
				}
				scheduledThreadPoolExecutor.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduledThreadPoolExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		}

		return true;
	}
	private class AsyncRequestExecutorImpl implements AsyncRequestExecutor<RadServiceRequest, RadServiceResponse> {
		@Override
		public void handleServiceRequest(RadServiceRequest serviceRequest,
										 RadServiceResponse serviceResponse) {
			//SONAR ignore
		}
	}


	public class RadiusGatewayControllerContextImpl implements RadiusGatewayControllerContext{

		@Override
		public RadiusGateway getRadiusGateway(String ipAddress) {
			return radiusGateways.get(ipAddress);
		}

		@Override
		public List<RadiusGroovyScript> getRadiusGroovyScripts(String ipAddress) {
			return groovyScriptsMap.get(ipAddress);
		}

		@Override
		public NetVertexServerContext getServerContext() {
			return serverContext;
		}


	}



	private void applyScriptsForSendPacket(RadiusPacket radiusPacket, PCRFResponse pcrfResponse, String ipAddress){
		List<RadiusGroovyScript> scripts = groovyScriptsMap.get(ipAddress);
		if(scripts != null && !scripts.isEmpty()){
			for(RadiusGroovyScript script : scripts){
				try{
					script.preSend(pcrfResponse, radiusPacket);
				}catch(Exception ex){
					LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + ipAddress +". Reason: "+ ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}

			}
		}
	}

	private void applyScriptsForSendPacket(PCRFResponse pcrfResponse, String ipAddress){
		List<RadiusGroovyScript> scripts = groovyScriptsMap.get(ipAddress);
		if(scripts != null && !scripts.isEmpty()){
			for(RadiusGroovyScript script : scripts){
				try{
					script.preSend(pcrfResponse);
				}catch(Exception ex){
					LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for gateway = " + ipAddress +". Reason: "+ ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}

			}
		}
	}

	public void reInit(){
		if(configuration.isEnabled() == false){
			LogManager.getLogger().info(MODULE, "Unable to reload radius gateway controller. Reason: Radius Gateway event listener is disabled");
			return;
		}

		if(initialized == false){
			LogManager.getLogger().warn(MODULE, "Unable to reload  radius gateway controller. Reason: controller not initialized properly");
			return;
		}

		Map<String, RadiusGateway> radiusGateways = new HashMap<String, RadiusGateway>();
		HashMap<String, List<RadiusGroovyScript>> tempGroovyScriptsMap = this.groovyScriptsMap;
		try{
			List<RadiusAccClientEntryImpl> radiusAccClientEntries = new ArrayList<RadiusAccClientEntryImpl>();
			List<RadiusDynAuthServerEntryImpl> radiusDynAuthServerEntries = new ArrayList<RadiusDynAuthServerEntryImpl>();
			List<RadiusAuthClientEntryImpl> radiusAuthClientEntries = new ArrayList<RadiusAuthClientEntryImpl>();
			for(RadiusGatewayConfiguration gatewayConfiguration : serverContext.getServerConfiguration().getRadiusGatewayConfigurations()){

				RadiusGateway radiusGateway = new RadiusGateway(serverContext, gatewayConfiguration , dynaAuthClientCounters, scheduledThreadPoolExecutor);
				radiusGateway.init();
				radiusGateways.put(gatewayConfiguration.getIPAddress(), radiusGateway);

				/*
				 * ID of existing client entry should not be changed. So add only new gateways
				 */
				if(this.radiusGateways.containsKey(gatewayConfiguration.getIPAddress())){
					continue;
				}
				int radiusAccClientTableIndex = radiusAccClientTable.getSize();
				int radiusAuthClientTableIndex = radiusAuthClientTable.getSize();
				int radiusDynaAuthServerTableIndex = radiusDynaAuthServerTable.getSize();

				radiusAccClientEntries.add(new RadiusAccClientEntryImpl(gatewayConfiguration.getIPAddress() ,acctServerClientStatisticsProvider, radiusAccClientTableIndex+1));
				radiusAuthClientEntries.add(new RadiusAuthClientEntryImpl(gatewayConfiguration.getIPAddress() ,authServerClientStatisticsProvider, radiusAuthClientTableIndex+1));
				radiusDynAuthServerEntries.add(new RadiusDynAuthServerEntryImpl(gatewayConfiguration.getIPAddress() , dynaAuthClientServerStatisticsProvider, radiusDynaAuthServerTableIndex+1));

			}

			for(RadiusAccClientEntryImpl radiusAccClientEntry : radiusAccClientEntries){
				addEntryIntoAccountClientTable(radiusAccClientEntry);

			}


			for(RadiusAuthClientEntryImpl radiusAuthClientEntry : radiusAuthClientEntries){
				addEntryIntoAuthClientTable(radiusAuthClientEntry);
			}

			for(RadiusDynAuthServerEntryImpl radiusDynAuthServerEntry : radiusDynAuthServerEntries){
				addEntryIntoAuthServer(radiusDynAuthServerEntry);
			}

			loadDictionary();
			initGroovyScripts();
			Map<String, RadiusGateway> oldGateways = this.radiusGateways;
			this.radiusGateways = radiusGateways;

			for(RadiusGateway oldGateway : oldGateways.values()) {
				oldGateway.stop();
			}
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error while realoading radius gateway configuration. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			this.groovyScriptsMap = tempGroovyScriptsMap;
			for(RadiusGateway newGateway : radiusGateways.values()) {
				newGateway.stop();
			}

		}
	}

	public void addEntryIntoAccountClientTable(RadiusAccClientEntryImpl radiusAccClientEntry){
		try {

			this.radiusAccClientTable.addEntry(radiusAccClientEntry);
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Acct Client Entry(" + radiusAccClientEntry.getRadiusAccClientAddress()
					+ ") not added in Client Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void addEntryIntoAuthClientTable(RadiusAuthClientEntryImpl radiusAuthClientEntry){
		try {

			this.radiusAuthClientTable.addEntry(radiusAuthClientEntry);
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Acct Client Entry(" + radiusAuthClientEntry.getRadiusAuthClientAddress()
					+ ") not added in Client Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void addEntryIntoAuthServer(RadiusDynAuthServerEntryImpl radiusDynAuthServerEntry){
		try {

			this.radiusDynaAuthServerTable.addEntry(radiusDynAuthServerEntry);
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Acct Client Entry(" + radiusDynAuthServerEntry.getRadiusDynAuthServerIPAddress()
					+ ") not added in Client Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}
}
