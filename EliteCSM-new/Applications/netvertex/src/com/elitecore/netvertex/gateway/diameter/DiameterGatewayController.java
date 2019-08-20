package com.elitecore.netvertex.gateway.diameter;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.util.cli.cmd.ClearCommand;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.cli.cmd.ShowCommand;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.data.GatewayInfo;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.diameterapi.core.commands.DiameterPeerStatisticDetailProvider;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException.ListenerRegFailResultCode;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertEnum;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertManager;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.session.ApplicationListener;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.transport.DiameterNetworkConnector;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.base.extended.DIAMETER_BASE_PROTOCOL_MIBimpl;
import com.elitecore.diameterapi.mibs.cc.extended.DIAMETER_CC_APPLICATION_MIBImpl;
import com.elitecore.diameterapi.mibs.custom.extended.DIAMETER_STACK_MIBImpl;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.cli.*;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.core.session.SessionHandler;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.core.transaction.TransactionFactory;
import com.elitecore.netvertex.core.transaction.TransactionType;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.GatewayEventListener;
import com.elitecore.netvertex.gateway.GatewayMediator;
import com.elitecore.netvertex.gateway.Mediator;
import com.elitecore.netvertex.gateway.diameter.af.PCCRuleNameGenerator;
import com.elitecore.netvertex.gateway.diameter.application.ApplicationListenerFactory;
import com.elitecore.netvertex.gateway.diameter.application.NetvertexApplication;
import com.elitecore.netvertex.gateway.diameter.application.SyApplication;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.cisco.CiscoGxAppHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.GyTransactionType;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.TGPPGxAppHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.TGPPGyAppHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.TGPPRxAppHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.TGPPSyAppHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterStackConfiguration;
import com.elitecore.netvertex.gateway.diameter.data.impl.RoutingEntryDataImpl;
import com.elitecore.netvertex.gateway.diameter.dictionary.DiameterDictionaryLoader;
import com.elitecore.netvertex.gateway.diameter.function.MatchMCCMNC;
import com.elitecore.netvertex.gateway.diameter.logmonitor.DiameterMonitor;
import com.elitecore.netvertex.gateway.diameter.plugin.MCCMNCDiameterPlugin;
import com.elitecore.netvertex.gateway.diameter.plugin.conf.impl.MCCMNCDiameterPluginConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.gateway.diameter.transaction.GyEventStartTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.GySessionStartTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.GySessionStopTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.GySessionUpdateTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.NewServiceTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.RuleRemoveTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.ServiceRegistrationTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.SessionDisconnectTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.SessionReAuthTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.SessionStartTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.SessionStopTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.SessionTerminationTransaction;
import com.elitecore.netvertex.gateway.diameter.transaction.SessionUpdateTransaction;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalators;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMapping;
import com.elitecore.netvertex.gateway.diameter.utility.S9Utility;
import com.elitecore.netvertex.service.pcrf.PCCRuleExpiryListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseListnerImpl;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.GroovyObject;
import org.hibernate.SessionFactory;

import javax.annotation.Nullable;
import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

/**
 * Performs activity related to Diameter Peer Management
 *
 * @author Subhash Punani
 *
 */
public class DiameterGatewayController {
	private static final String MODULE = "DIA-GTW-CTRL";

	private NetVertexServerContext serverContext;
	private DiameterStack diameterStack;
	private DiameterGatewayControllerContext gatewayControllerContext;
	private GatewayEventListener eventListener;
	private Map<String,TGPPDiameterGateway> tgppGateways;
	private Map<String,CiscoGxGateway> ciscoGxGateways;
	private DiameterStackConfiguration stackConfiguration;
	private TransactionFactory transactionFactory;
	private PCRFResponseListner pcrfResponseListner;
	private Date stackStartDate;
	/**
	 * store the transaction which are incomplete
	 */
	private ConcurrentHashMap<String, Transaction> currentTransactionsMap;
	private PCCRuleExpiryListener pccRuleExpiryListener;
	private S9Utility s9Utility;
	private Map<String, NetvertexApplication> appListeners;
	private EnumMap<Application, Map<SupportedStandard, ApplicationHandler>> appHandlers;
	private HashMap<String, List<DiameterGroovyScript>> groovyScriptsMap;
	private boolean initialized;
	private DiameterMonitor diameterMonitor;

	private Mediator mediator;

	private boolean isGxInterfaceEnabled = true;
	private boolean isGyInterfaceEnabled = true;
	private boolean isRxInterfaceEnabled = true;
	private boolean isS9InterfaceEnabled = true;
	private boolean isCiscoGxInterfaceEnabled = true;


	private OverloadAction actionOnOverload;
	private volatile int resultCodeOnOverload = ResultCode.DIAMETER_TOO_BUSY.code;
	private boolean isEnabled;
	private String remarks;
	private DiameterPeerGroupFactory peerGroupFactory;
	private final SessionLocator sessionLocator;
	private SessionHandler sessionHandler;
	private SessionFactory sessionFactory;

	public DiameterGatewayController(NetVertexServerContext serverContext, GatewayEventListener eventListener,
									 SessionLocator sessionLocator,
									 Mediator mediator, SessionHandler sessionHandler, SessionFactory sessionFactory) {
		this.serverContext = serverContext;
		this.eventListener = eventListener;
		this.sessionLocator = sessionLocator;
		this.sessionHandler = sessionHandler;
		this.sessionFactory = sessionFactory;

		tgppGateways = new HashMap<>();
		ciscoGxGateways = new HashMap<>();
		gatewayControllerContext = new DiameterGatewayControllerContextImpl();
		diameterMonitor = new DiameterMonitor(serverContext);

		String nameGenerator = System.getProperty(PCCRuleNameGenerator.KEY);
		if(nameGenerator != null) {
			if(nameGenerator.equalsIgnoreCase(PCCRuleNameGenerator.COUNTER)) {
				PCCRuleNameGenerator.register(new PCCRuleNameGenerator.CounterBaseNameGenerator(serverContext));
			} else if(nameGenerator.equalsIgnoreCase(PCCRuleNameGenerator.UUID)) {
				PCCRuleNameGenerator.register(new PCCRuleNameGenerator.UUIDBaseNameGenerator());
			} else if(nameGenerator.equalsIgnoreCase(PCCRuleNameGenerator.SESSION_ID)) {
				PCCRuleNameGenerator.register(new PCCRuleNameGenerator.SessionIdBaseNameGenerator());
			}
		}

		diameterStack = new DiameterStack(){
			@Override
			public void applyMonitoryLogLevel(DiameterPacket diameterPacket) {


				try{
					if(diameterMonitor.evaluate(diameterPacket)){
						LogManager.getLogger().addThreadName(Thread.currentThread().getName());
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Adding Thread: " + Thread.currentThread().getName()+ " to log monitor");
						diameterPacket.setParameter(MonitorLogger.MONITORED, true);
					}



				}catch(Exception ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in applying diameter monitor log. Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}
			}
		};
		stackConfiguration = serverContext.getServerConfiguration().getDiameterStackConfiguration();
		currentTransactionsMap = new ConcurrentHashMap<String, Transaction>(1024,0.75f,stackConfiguration.getMaximumThread());
		pccRuleExpiryListener = new PCCRuleExpiryListenerImpl();
		s9Utility = new S9Utility(gatewayControllerContext, serverContext.getServerConfiguration().getMCCMNCRoutingConfiguration());
		appListeners = new HashMap<>(1, 1);
		appHandlers = new EnumMap<>(Application.class);
		groovyScriptsMap = new HashMap<>(1, 1);

		this.actionOnOverload = OverloadAction.DROP;
		this.mediator = mediator;
		this.peerGroupFactory = new DiameterPeerGroupFactory(diameterStack.getStackContext());

	}

	public void init() throws InitializationFailedException{
		LogManager.getLogger().info(MODULE, "Initializing Diameter Gateway Controller");
		if (stackConfiguration.isEnabled() == false) {
			isEnabled = false;
			remarks = "Diameter Stack is disabled";
			LogManager.getLogger().info(MODULE, "Diameter Stack is disabled");
			return;
		}
		if (Strings.isNullOrBlank(stackConfiguration.getOriginHost())) {
			LogManager.getLogger().warn(MODULE, "Origin Host is not configured in Diameter Stack");
			remarks = "Origin Host is not configured in Diameter Stack";
			return;
		}
		if(Strings.isNullOrBlank(stackConfiguration.getOriginRealm())){
			LogManager.getLogger().warn(MODULE, "Origin Realm is not configured in Diameter Stack");
			remarks = "Origin Realm is not configured in Diameter Stack";
			return;
		}
		
		serverContext.registerLicenseObserver(()->{
			isGxInterfaceEnabled = serverContext.isLicenseValid(LicenseNameConstants.NV_GX_INTERFACE,String.valueOf(System.currentTimeMillis()));
			isCiscoGxInterfaceEnabled = serverContext.isLicenseValid(LicenseNameConstants.NV_CISCOSCE_INTERFACE,String.valueOf(System.currentTimeMillis()));
			isGyInterfaceEnabled = serverContext.isLicenseValid(LicenseNameConstants.NV_GY_INTERFACE,String.valueOf(System.currentTimeMillis()));
			isRxInterfaceEnabled = serverContext.isLicenseValid(LicenseNameConstants.NV_RX_INTERFACE,String.valueOf(System.currentTimeMillis()));
			isS9InterfaceEnabled = serverContext.isLicenseValid(LicenseNameConstants.NV_S9_INTERFACE,String.valueOf(System.currentTimeMillis()));
		});

		TGPPGxAppHandler  gxAppHandler = new TGPPGxAppHandler(gatewayControllerContext);
		CiscoGxAppHandler ciscoGxAppHandler = new CiscoGxAppHandler(gatewayControllerContext, eventListener);
		EnumMap<SupportedStandard, ApplicationHandler> gxAppHandlerMap = new EnumMap<>(SupportedStandard.class);
		gxAppHandlerMap.put(SupportedStandard.CISCOSCE, ciscoGxAppHandler);
		gxAppHandlerMap.put(SupportedStandard.RELEASE_9, gxAppHandler);
		appHandlers.put(Application.TGPP_GX_29_212_18, gxAppHandlerMap);


		TGPPRxAppHandler  rxAppHandler = new TGPPRxAppHandler(gatewayControllerContext);
		EnumMap<SupportedStandard, ApplicationHandler> rxAppHandlerMap = new EnumMap<>(SupportedStandard.class);
		rxAppHandlerMap.put(SupportedStandard.RELEASE_9, rxAppHandler);
		appHandlers.put(Application.TGPP_RX_29_214_18, rxAppHandlerMap);

		TGPPSyAppHandler  syAppHandler = new TGPPSyAppHandler(gatewayControllerContext);
		EnumMap<SupportedStandard, ApplicationHandler> syAppHandlerMap = new EnumMap<>(SupportedStandard.class);
		syAppHandlerMap.put(SupportedStandard.RELEASE_9, syAppHandler);
		appHandlers.put(Application.TGPP_SY, syAppHandlerMap);

		TGPPGyAppHandler gyAppHandler = new TGPPGyAppHandler(gatewayControllerContext);
		EnumMap<SupportedStandard, ApplicationHandler> gyAppHandlerMap = new EnumMap<>(SupportedStandard.class);
		gyAppHandlerMap.put(SupportedStandard.RELEASE_9, gyAppHandler);
		appHandlers.put(Application.CC, gyAppHandlerMap);

		try {
			DictionaryDetailProvider.getInstance().registerDetailProvider(new DiameterDictionaryDetailProvider(serverContext.getServerHome()));
			ShowCommand.registerDetailProvider(DictionaryDetailProvider.getInstance());
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Diameter dictionary Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		loadDictionary();
		serverContext.getServerConfiguration().getDiameterGatewayConfigurable().init();

		try{

			GatewayStatusCommand.registerDetailProvider(new DiameterGatewayStatusDetailProvider(new DiameterGatewayStatus(){

				@Override
				public Map<String, IStateEnum> getGatewaysState() {
					return diameterStack.getPeersState();
				}

				@Override
				public boolean closeGateway(String hostIdentity) {
					return diameterStack.closePeer(hostIdentity);
				}

				@Override
				public boolean forceCloseGateway(String hostIdentity) {
					return diameterStack.forceClosePeer(hostIdentity);
				}

				@Override
				public boolean startGateway(String hostIdentity) {
					return diameterStack.startPeer(hostIdentity);
				}

			}));

		}catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Failed to register Diameter Gateway Status Detail Provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		pcrfResponseListner = new PCRFResponseListnerImpl(gatewayControllerContext);
		transactionFactory = new TransactionFactory(gatewayControllerContext);
		transactionFactory.registerTransaction(TransactionType.NEW_SERVICE_REQUEST, new NewServiceTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SERVICE_REG, new ServiceRegistrationTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SESSION_START, new SessionStartTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SESSION_STOP, new SessionStopTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SESSION_UPDATE, new SessionUpdateTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.RULE_REMOVE, new RuleRemoveTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SESSION_TERMINATION, new SessionTerminationTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SESSION_RE_AUTH, new SessionReAuthTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(TransactionType.SESSION_DISCONNECT, new SessionDisconnectTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(GyTransactionType.SESSION_START, new GySessionStartTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(GyTransactionType.SESSION_UPDATE, new GySessionUpdateTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(GyTransactionType.SESSION_STOP, new GySessionStopTransaction(gatewayControllerContext));
		transactionFactory.registerTransaction(GyTransactionType.EVENT_REQUEST, new GyEventStartTransaction(gatewayControllerContext));

		serverContext.getTaskScheduler().scheduleIntervalBasedTask(new TransactionCleanupTask());

		LogManager.getLogger().info(MODULE, "Initializing Diameter Stack");



		try{
			diameterStack.setDiameterStackRealm(stackConfiguration.getOriginRealm());
			diameterStack.setDiameterStackIdentity(stackConfiguration.getOriginHost());
			diameterStack.setServerInstanceId(serverContext.getServerInstanceId());
			diameterStack.setSessionInterval(stackConfiguration.getSessionCleanupInterval());
			diameterStack.setSessionTimeOut(stackConfiguration.getSessionTimeOut());
			diameterStack.setDuplicateDetectionEnabled(stackConfiguration.getDuplicateRequestCheckEnabled());
			diameterStack.setDuplicatePacketPurgeInterval(stackConfiguration.getDuplicateRequestPurgeInterval());

			diameterStack.setWorkerThreadPriority(stackConfiguration.getWorkerThreadPriority());
			diameterStack.setMaxRequestQueueSize(stackConfiguration.getQueueSize());
			diameterStack.setMaxThreadPoolSize(stackConfiguration.getMaximumThread());
			diameterStack.setMinThreadPoolSize(stackConfiguration.getMinimumThread());
			diameterStack.setMainThreadPriority(stackConfiguration.getMainThreadPriority());

			DiameterNetworkConnector diameterNetworkConnector = new DiameterNetworkConnector(diameterStack);
			diameterNetworkConnector.setSocketReceiveBufferSize(stackConfiguration.getSocketReceiveBufferSize());
			diameterNetworkConnector.setSocketSendBufferSize(stackConfiguration.getSocketSendBufferSize());
			diameterNetworkConnector.setNetworkAddress(stackConfiguration.getIPAddress());
			diameterNetworkConnector.setNetworkPort(stackConfiguration.getPort());

			diameterStack.addNetworkConnector(diameterNetworkConnector);

			setOverloadParameter(diameterStack);

			registerMIBIndexRecorder();

			registerDiameterGateways();

			initGroovyScripts();

			registerRoutingConfiguration();

			registerRealms();

			ApplicationListenerFactory applicationListenerFactory = new ApplicationListenerFactory(peerGroupFactory, gatewayControllerContext, appHandlers);
			appListeners = applicationListenerFactory.createApplicationListenerFromConfiguration(serverContext.getServerConfiguration().getDiameterGatewayConfigurations());

			registerApplicationListener();



			getLogger().info(MODULE, "Registering Alert to Diameter Stack");

			diameterStack.registerStackAlertManager(new DiameterStackAlertManager());

			LogMonitorManager.getInstance().registerMonitor(diameterMonitor);

			getLogger().info(MODULE, "Alert Registration completed");
			diameterStack.init();
			getLogger().info(MODULE, "Diameter Stack initialization completed");

			createDiameterStatisticsDetailProvider();

			createDiameterClearDetailProvider();

			getLogger().info(MODULE, "Starting Diameter Stack");
			if(!diameterStack.start()){
				isEnabled = false;
				throw new InitializationFailedException("Failed to start Diameter Stack");
			}

			PluginInfo mccmncPluginInfo = new PluginInfo();
			mccmncPluginInfo.setPluginName("MCC_MNC_DIA_PLUGIN");
			mccmncPluginInfo.setPluginClass(MCCMNCDiameterPlugin.class.getName());
			mccmncPluginInfo.setPluginConfClass(MCCMNCDiameterPluginConfigurationImpl.class.getName());
			List<PluginInfo> plugInInfoLst = new ArrayList<PluginInfo>();

			plugInInfoLst.add(mccmncPluginInfo);


			DiameterPluginManager.createAndRegisterPlugin(mccmncPluginInfo, new MCCMNCDiameterPluginConfigurationImpl(serverContext, mccmncPluginInfo.getPluginName()),serverContext);


			List<String> tempInPlugins = new ArrayList<>();
			//add MCC-MNC plugin
			tempInPlugins.add(mccmncPluginInfo.getPluginName());

			String [] inPlugins = new String [0];
			inPlugins = tempInPlugins.toArray(inPlugins);

			String [] outPlugins = new String [0];
			outPlugins = new ArrayList<String>().toArray(outPlugins);


			List<PluginEntryDetail> inPlugin = new ArrayList<PluginEntryDetail>();
			List<PluginEntryDetail> outPlugin = new ArrayList<PluginEntryDetail>();

			for (int i = 0; i < inPlugins.length; i++) {
				PluginEntryDetail data = new PluginEntryDetail();
				data.setPluginName(inPlugins[i]);
				inPlugin.add(data);
			}

			for (int i = 0; i < outPlugins.length; i++) {
				PluginEntryDetail data = new PluginEntryDetail();
				data.setPluginName(outPlugins[i]);
				outPlugin.add(data);
			}

			diameterStack.registerInOutPlugins(inPlugin,outPlugin);

			if(getDiameterStackStatus() != Status.RUNNING) {
				isEnabled = false;
			} else {
				isEnabled = true;
			}

			if (diameterStack.getRemarks() != null) {

				if (diameterStack.getRemarks().equals(ServiceRemarks.STARTED_ON_UNIVERSAL_IP.remark)) {

					stackConfiguration.setIpAddress(CommonConstants.UNIVERSAL_IP);
					stackConfiguration.setPort(diameterNetworkConnector.getBondSocketDetail().getPort());
				}
			}
			stackStartDate = new Date();


			//Initializing result code mapping
			ResultCodeMapping.getInstance().init(serverContext);

			SendPacketCommand.registerDetailProvider(new DiameterPacketSendCommandImpl());

			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			registerBaseProtocolMib(server);
			registerStackMib(server);
			registerCcAppMib(server);

		}catch(Exception ex){
			throw new InitializationFailedException(" Initialization failed for Diameter Gateway Controller. Reason : " + ex.getMessage() , ex);
		}

		initialized = true;
		LogManager.getLogger().info(MODULE, "Diameter Gateway Controller initialization completed.");

	}

	private void createDiameterStatisticsDetailProvider() {
		try {

			DiameterStatisticsDetailProvider diameterStatisticsDetailProvider = new DiameterStatisticsDetailProvider();
			DiameterShowStatisticDetailProvider statisticDetailProvider = new DiameterShowStatisticDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider());
			statisticDetailProvider.registerDetailProvider(new DiameterApplicationDetailProvider(
					diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider(),
					diameterStack.getDiameterStatisticListner().getDiameterConfigProvider()));
			statisticDetailProvider.registerDetailProvider(new DiameterCommandCodeDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider()));
			statisticDetailProvider.registerDetailProvider(new DiameterRealmStatisticDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider()));
			statisticDetailProvider.registerDetailProvider(new DiameterResultCodeDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider()));
			statisticDetailProvider.registerDetailProvider(new DiameterPeerStatisticDetailProvider(
					diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider(),
					diameterStack.getDiameterStatisticListner().getDiameterConfigProvider()));

			diameterStatisticsDetailProvider.registerDetailProvider(statisticDetailProvider);
			diameterStatisticsDetailProvider.registerDetailProvider(new DiameterShowConfigDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterConfigProvider()));
			diameterStatisticsDetailProvider.registerDetailProvider(new DiameterShowSessionDetailProvider(diameterStack.getSessionFactoryManager()));
			ShowCommand.registerDetailProvider(diameterStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Diameter Statistics Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void createDiameterClearDetailProvider() {
		try {
            DiameterClearDetailProvider diameterClearDetailProvider = new DiameterClearDetailProvider();

            DiameterClearStatisticDetailProvider clearStatisticDetailProvider = new DiameterClearStatisticDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticResetter());
            clearStatisticDetailProvider.registerDetailProvider(new DiameterClearPeerStatisticDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticResetter()));
            clearStatisticDetailProvider.registerDetailProvider(new DiameterClearApplicationStatisticDetailProvider(
                    diameterStack.getDiameterStatisticListner().getDiameterStatisticResetter(),
                    diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider()));
            clearStatisticDetailProvider.registerDetailProvider(new DiameterClearRealmStatisticDetailProvider(diameterStack.getDiameterStatisticListner().getDiameterStatisticResetter()));

            diameterClearDetailProvider.registerDetailProvider(clearStatisticDetailProvider);
            diameterClearDetailProvider.registerDetailProvider(new DiameterClearSessionDetailProvider(diameterStack.getSessionFactoryManager()));
            ClearCommand.registerDetailProvider(diameterClearDetailProvider);
        } catch (RegistrationFailedException e) {
            LogManager.getLogger().error(MODULE, "Failed to register Diameter clear Detail Provider. Reason : " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }
	}

	private void registerCcAppMib(MBeanServer server) {
		try{
			DIAMETER_CC_APPLICATION_MIBImpl diameterCcApplicationMib = new DIAMETER_CC_APPLICATION_MIBImpl(diameterStack.getDiameterStatisticListner());
			diameterCcApplicationMib.populate(server, null);
			serverContext.registerSnmpMib(diameterCcApplicationMib);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Diameter CC Applocation MIB registration failed. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
	}

	private void registerStackMib(MBeanServer server) {
		try{
			DIAMETER_STACK_MIBImpl diameterStackMibimpl = new DIAMETER_STACK_MIBImpl(diameterStack.getDiameterStatisticListner(), diameterStack.getStackStatus());
			diameterStackMibimpl.populate(server, null);
			serverContext.registerSnmpMib(diameterStackMibimpl);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Diameter stack  MIB registration failed. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
	}

	private void registerBaseProtocolMib(MBeanServer server) {
		try{
			DIAMETER_BASE_PROTOCOL_MIBimpl diameterBaseProtocolMiBimpl = new DIAMETER_BASE_PROTOCOL_MIBimpl(diameterStack.getStackContext() ,
					diameterStack.getDiameterStatisticListner());
			diameterBaseProtocolMiBimpl.populate(server, null);
			serverContext.registerSnmpMib(diameterBaseProtocolMiBimpl);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Diameter Base Protocol MIB registration failed. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
	}

	private void registerApplicationListener() throws ElementRegistrationFailedException {
		ArrayList<ApplicationListener> applicationListeners = new ArrayList<ApplicationListener>();
		for(NetvertexApplication netvertexApplication : appListeners.values()) {
			applicationListeners.add((ApplicationListener) netvertexApplication);
		}

		diameterStack.registerApplicationListener(applicationListeners);
	}

	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	/**
	 * registerRoutingConfiguration register MCCMNC configuration to diameter stack for routing for Gx Request.
	 */
	private void registerRoutingConfiguration() {
		MCCMNCRoutingConfiguration routingConfiguration = serverContext.getServerConfiguration().getMCCMNCRoutingConfiguration();

		if(routingConfiguration == null || routingConfiguration.getRoutingEntries() == null || routingConfiguration.getRoutingEntries().isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Skipping realm registration for routing. Reason: Routing entries not found in MCCMNCRoutingConfiguration");
			return;
		}

		Compiler.getDefaultCompiler().addFunction(new MatchMCCMNC(routingConfiguration));

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Registering Realms for routing to Diameter Stack");

		for(final RoutingEntry routingEntry : routingConfiguration.getRoutingEntries()){
			List<PeerInfoImpl> peerInfoes = new ArrayList<PeerInfoImpl>();
			if(routingEntry.getRoutingAction() == RoutingActions.PROXY &&
					(routingEntry.getGatewayNames() == null || routingEntry.getGatewayNames().isEmpty())){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Skipping configuring for routing entry = " + routingEntry.getName()
							+". Reason: No gateway is defined for routing entry");
				continue;
			}



			for(String peerName : routingEntry.getGatewayNames()){
				PeerInfoImpl peerInfo = new PeerInfoImpl();
				peerInfo.setPeerName(peerName);
				int weight = routingEntry.getGatewayWeightage(peerName);
				if(weight < 0){
					LogManager.getLogger().warn(MODULE, "Skip to register "+ peerName +" for routing entry" + routingEntry.getName()
							+". Reason: weightage is less than 0");
					continue;
				}
				peerInfo.setLoadFactor(weight);
				peerInfoes.add(peerInfo);
			}
			PeerGroupImpl peerGroup = new PeerGroupImpl();
			peerGroup.setPeerInfoList(peerInfoes);
			List<PeerGroupImpl> peerGroups = new ArrayList<PeerGroupImpl>();
			peerGroups.add(peerGroup);

			DiameterFailoverConfigurationImpl diameterFailoverConfigurationImpl = new DiameterFailoverConfigurationImpl();
			diameterFailoverConfigurationImpl.setAction(DiameterFailureConstants.TRANSLATE.failureAction);
			diameterFailoverConfigurationImpl.setErrorCodes(ResultCodeCategory.RC3XXX.value + "," +ResultCodeCategory.RC4XXX.value + "," + ResultCodeCategory.RC5XXX.value);
			try{

				RoutingEntryDataImpl realmData = new RoutingEntryDataImpl("*", routingEntry.getRoutingAction(), "0", peerGroups);
				realmData.setAdvancedCondition(routingEntry.getExpression());
				realmData.setRoutingName(routingEntry.getName());
				diameterStack.registerRoutingEntry(realmData);
			}catch(ElementRegistrationFailedException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "registration fail for MCC MNC Routing Entry" + routingEntry.getName() +". Reason:" +ex.getMessage());
				getLogger().trace(MODULE, ex);

			}

		}


		try {
			diameterStack.registerRoutingEntry(new RoutingEntryDataImpl(stackConfiguration.getOriginRealm(),RoutingActions.LOCAL,"0",new ArrayList<PeerGroupImpl>()));
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Realms registration completed");
		} catch (ElementRegistrationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Realm Registration fail for realm = " + stackConfiguration.getOriginRealm() + ". Reason:" +e.getMessage());
			ignoreTrace(e);
		}
	}

	@Nullable
	public String getSyGatewayName(String sySessionId) {

		ISession session = diameterStack.getStackContext().readOnlySession(sySessionId, ApplicationIdentifier.TGPP_SY.applicationId);
		if(session == null) {
			if(getLogger().isInfoLogLevel())
				getLogger().info(MODULE, "Session not found for sySessionId: "+ sySessionId);
			return null;
		}

		return (String) session.getParameter(DiameterAVPConstants.DESTINATION_HOST);
	}

	/**
	 * handleSessionDisconnectRequest send session disconnect request (ASR, RAR) based on diameter gateway type.
	 *
	 */
	public void handleSessionDisconnectRequest(PCRFRequest pcrfRequest){

		pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.DIAMETER.getVal());

		if(SessionTypeConstant.CISCO_GX.getVal().equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))){
			disconnectCiscoGxSession(pcrfRequest);
		}else{
			Transaction transaction = DiameterGatewayController.this.gatewayControllerContext.createTransaction(TransactionType.SESSION_DISCONNECT);
			transaction.process(EventTypes.SESSION_DISCONNECT, pcrfRequest);
		}
	}




	public boolean sendSyRequest(PCRFResponse pcrfResponse,
								 DiameterPeerGroupParameter diameterPeerGroupParameter,
								 @Nullable String primaryGatewayName,
								 PCRFResponseListner responseListener,
								 CommandCode commandCode) {
		String nameOrHostIdentity;
		if(primaryGatewayName != null) {
			nameOrHostIdentity = primaryGatewayName;
		} else {
			nameOrHostIdentity = diameterPeerGroupParameter.getPeers().keySet().iterator().next();
		}

		DiameterGatewayConfiguration diameterGatewayConfiguration = gatewayControllerContext.getGatewayConfigurationByHostId(nameOrHostIdentity);

		if(diameterGatewayConfiguration == null) {
			diameterGatewayConfiguration = gatewayControllerContext.getGatewayConfigurationByName(nameOrHostIdentity);
			if(diameterGatewayConfiguration == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Unable to send "+ commandCode.displayName
							+ " request. Reason: Gateway Configuration not found for group:" + diameterPeerGroupParameter.getName());

				return false;
			}
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Gateway Configuration found for group: " + diameterPeerGroupParameter.getName());

		DiameterRequest diameterRequest = gatewayControllerContext.buildSyRequest(pcrfResponse, commandCode, diameterGatewayConfiguration);

		if(diameterRequest == null){
			LogManager.getLogger().error(MODULE, "Unable to send "+ commandCode.displayName +" to " + diameterPeerGroupParameter.getName()
					+". Reason: DiameterRequest not created from PCRFResponse");
			return false;
		}

		NetvertexApplication applicationListener = getApplicationListener(diameterRequest);
		if(applicationListener != null){
			try{
				((SyApplication)applicationListener).sendRequest(diameterRequest,
						pcrfResponse,
						diameterPeerGroupParameter,
						primaryGatewayName,
						responseListener);

				return true;
			}catch(Exception ex){
				LogManager.getLogger().error(MODULE, "Unable to send "+ commandCode.displayName +" to "+ diameterPeerGroupParameter.getName() +". Reason: "+ ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
				return false;
			}
		} else{
			LogManager.getLogger().error(MODULE, "Unable to send "+ commandCode.displayName +" to "+ diameterPeerGroupParameter.getName() +". Reason: Sy Application not found");
			return false;
		}
	}

	private void disconnectCiscoGxSession(PCRFRequest pcrfResquest){

		HashSet<PCRFEvent> events = new HashSet<PCRFEvent>(1,1);
		events.add(PCRFEvent.SESSION_STOP);
		pcrfResquest.setPCRFEvents(events);

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "PCRF Request for Session Disconnect: " + pcrfResquest);


		RequestStatus requestStatus = eventListener.eventReceived(pcrfResquest);
		if(requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Cisco Gx Session deletion failed. Reason: PCRF Request Status: " + requestStatus.getVal());
		}

		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		PCRFPacketUtil.buildPCRFResponse(pcrfResquest, pcrfResponse);
		DiameterRequest diameterRequest = gatewayControllerContext.buildASR(pcrfResponse);
		gatewayControllerContext.sendRequest(diameterRequest, null, null);
	}

	private class DiameterGatewayControllerContextImpl implements DiameterGatewayControllerContext{
		
		@Override
		public PolicyRepository getPolicyManager(){
			return serverContext.getPolicyRepository();
		}

		@Override
		public SessionLocator getSessionLocator() {
			return sessionLocator;
		}

		@Override
		public Transaction createTransaction(String transactionType) {
			return transactionFactory.createTransaction(transactionType);
		}


		@Override
		public Transaction removeTransaction(String transactionId) {
			return currentTransactionsMap.remove(transactionId);
		}


		@Override
		public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest) {
			return eventListener.eventReceived(pcrfRequest, pcrfResponseListner, pccRuleExpiryListener);
		}

		@Override
		public boolean sendAnswer(DiameterAnswer packet, DiameterRequest diameterRequest) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Diameter Answer :" + packet);

			NetvertexApplication applicationListener = getApplicationListener(packet);

			if (applicationListener == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,
							"Unable to send Diameter Answer. Reason :"
									+ "No application found for diameter packet with Session-ID = "
									+ packet.getAVPValue(DiameterAVPConstants.SESSION_ID));
				return false;
			}

			String sessionId = packet.getAVPValue(DiameterAVPConstants.SESSION_ID);
			DiameterSession session = (DiameterSession)diameterStack.getStackContext().getOrCreateSession(sessionId, packet.getApplicationID());

			try {
				applicationListener.sendAnswer(session, diameterRequest, packet.getAsDiameterAnswer());
				return true;
			} catch (CommunicationException e) {
				getLogger().error(MODULE,
						"Unable to send Diameter request with Session-ID = "
								+ packet.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason :" + e.getMessage());

				getLogger().trace(MODULE, e);

			}
			return false;

		}


		@Override
		public boolean sendRequest(DiameterRequest packet,
								   @Nullable String preferredPeerHostIdOrName,
								   @Nullable Transaction transaction) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Diameter request :" + packet);

			NetvertexApplication applicationListener = getApplicationListener(packet);

			if (applicationListener == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,
							"Unable to send Diameter request with Session-ID = "
									+ packet.getAVPValue(DiameterAVPConstants.SESSION_ID)
									+ ". Reason: No application found for diameter packet");
				return false;
			}

			/**
			 * if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			 * LogManager.getLogger().debug(MODULE,
			 * "Application Listener found with Vendor-ID:Application-ID pairs = "
			 * + Arrays.toString(applicationListener.getApplicationEnum()));
			 */

			String sessionId = packet.getAVPValue(DiameterAVPConstants.SESSION_ID);
			DiameterSession session = (DiameterSession)diameterStack.getStackContext().getOrCreateSession(sessionId, packet.getApplicationID());

			if (session != null) {
				String sessionPeerHostIdentity = (String) session.getParameter(DiameterAVPConstants.ORIGIN_HOST);
				if (sessionPeerHostIdentity != null) {
					preferredPeerHostIdOrName = sessionPeerHostIdentity;
				} else {
					sessionPeerHostIdentity = (String) session.getParameter(DiameterAVPConstants.DESTINATION_HOST);
					if (sessionPeerHostIdentity != null) {
						preferredPeerHostIdOrName = sessionPeerHostIdentity;
					}
				}
			}

			if (preferredPeerHostIdOrName == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,
							"Unable to send Diameter request with Session-ID:" + packet.getSessionID()
									+ ". Reason: preferred peer not found from session");
				return false;
			}


			DiameterGatewayConfiguration diameterGatewayConfiguration = null;
			/*
			 * here, we can received either name or host identity of preferred
			 * peer. Now, we need to send group parameter for fail-over. so we
			 * try to fetch by host-identity, then name. We try to fetch
			 * configuration by host-identity because in most of the case we
			 * provide host-identity of preferred peer from PCRFRespose We store
			 * host-identity of preferred peer in "CS.SOURCE_GATEWAY" key.
			 */


			diameterGatewayConfiguration = getGatewayConfigurationByHostId(preferredPeerHostIdOrName);
			if (diameterGatewayConfiguration == null) {
				diameterGatewayConfiguration = getGatewayConfigurationByName(preferredPeerHostIdOrName);

				if (diameterGatewayConfiguration == null) {
					getLogger().warn(MODULE,
							"Unable to send Diameter Answer. Reason :"
									+ "No application found for diameter packet with Session-ID = "
									+ packet.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason: Gateway configuration not found for preferred peer:" + preferredPeerHostIdOrName);

					return false;
				}
			}


			try {
				applicationListener.sendRequest(session, packet.getAsDiameterRequest(), diameterGatewayConfiguration.getDiameterPeerGroupParameter(), preferredPeerHostIdOrName);

				if(transaction != null) {
					registerTransaction(sessionId + String.valueOf(packet.getHop_by_hopIdentifier()), transaction);
				}
				return true;
			} catch (CommunicationException e) {
				getLogger().error(MODULE,
						"Unable to send Diameter request with Session-ID = "
								+ packet.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason :" + e.getMessage());

				getLogger().trace(MODULE, e);
			}

			return false;

		}



		@Override
		public DiameterRequest buildSyRequest(PCRFResponse pcrfResponse, CommandCode commandCode, DiameterGatewayConfiguration configuration){

			DiameterRequest diameterPacket = new DiameterRequest(true);
			diameterPacket.setProxiableBit();
			diameterPacket.setCommandCode(commandCode.code);


			String previousSessionType = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val);
			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.SY.val);

			DiameterPacketMappingValueProvider valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterPacket, configuration, gatewayControllerContext);
			if(commandCode.code ==CommandCode.SPENDING_LIMIT.code){
				configuration.getSLRMappings().apply(valueProvider, AvpAccumalators.of(diameterPacket));
			} else if(commandCode.code ==CommandCode.SPENDING_STATUS_NOTIFICATION.code){
				configuration.getSNAMappings().apply(valueProvider, AvpAccumalators.of(diameterPacket));
			} else if(commandCode.code ==CommandCode.SESSION_TERMINATION.code){
				configuration.getSySTRMapping().apply(valueProvider, AvpAccumalators.of(diameterPacket));
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to create sy packet. Reason: Unsupported Command-Code: " + commandCode.displayName);
				return null;
			}


			applyGroovyScript(diameterPacket,pcrfResponse,configuration);

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, previousSessionType);

			return diameterPacket;

		}

		@Override
		public DiameterRequest buildASR(PCRFResponse pcrfResponse){

			DiameterGatewayConfiguration configuration = getGatewayConfigurationByName(pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if(configuration == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Can't build ASR. Reason: Gateway Configuration not found");
				}
				return null;
			}

			DiameterRequest asrRequest = new DiameterRequest();
			asrRequest.setCommandCode(CommandCode.ABORT_SESSION.code);
			asrRequest.setProxiableBit();

			configuration.getASRMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, asrRequest, configuration, gatewayControllerContext), AvpAccumalators.of(asrRequest));


			applyGroovyScript(asrRequest, pcrfResponse, configuration);

			return asrRequest;
		}


		@Override
		public DiameterRequest buildRAR(PCRFResponse pcrfResponse) {

			DiameterGatewayConfiguration configuration = getGatewayConfigurationByName(pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if(configuration == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Can't build RAR. Reason: Gateway Configuration not found");
				}
				return null;
			}

			DiameterRequest rarRequest = new DiameterRequest();
			rarRequest.setCommandCode(CommandCode.RE_AUTHORIZATION.code);
			rarRequest.setProxiableBit();

			if (GatewayComponent.OCS == configuration.getGatewayType()) {
				configuration.getGyRARMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, rarRequest, configuration, gatewayControllerContext), AvpAccumalators.of(rarRequest));
			} else {
				configuration.getGxRARMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, rarRequest, configuration, gatewayControllerContext), AvpAccumalators.of(rarRequest));
			}

			applyGroovyScript(rarRequest, pcrfResponse, configuration);
			return rarRequest;
		}


		@Override
		public void buildAAA(PCRFResponse pcrfResponse,DiameterAnswer answer) {

			DiameterGatewayConfiguration configuration = getGatewayConfigurationByName(pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if(configuration == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Can't build AAA. Reason: Gateway Configuration not found");
				}
				return;
			}

			configuration.getAAAMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, answer, configuration, gatewayControllerContext), AvpAccumalators.of(answer));

			applyGroovyScript(answer, pcrfResponse, configuration);

		}

		@Override
		public void buildCCA(PCRFResponse pcrfResponse,DiameterAnswer answer) {

			DiameterGatewayConfiguration configuration = getGatewayConfigurationByName(pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if (configuration == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
					LogManager.getLogger().error(MODULE, "Can't build CCA. Reason: Gateway Configuration not found");
				}
				return;
			}


			if (answer.getApplicationID() == configuration.getGyApplicationId()) {
				configuration.getGyCCAMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, answer, configuration, gatewayControllerContext), AvpAccumalators.of(answer));
			} else {
				configuration.getGxCCAMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, answer, configuration, gatewayControllerContext), AvpAccumalators.of(answer));
			}

			applyGroovyScript(answer, pcrfResponse, configuration);

		}

		@Override
		public void registerTransaction(String id,Transaction transaction) {
			transaction.updateLastAccessTime();
			currentTransactionsMap.put(id, transaction);
		}

		@Override
		public void sendASRtoCiscoGx(PCRFRequest pcrfResponse) {
			disconnectCiscoGxSession(pcrfResponse);
		}

		@Override
		public void buildSTA(PCRFResponse pcrfResponse, DiameterAnswer answer) {

			DiameterGatewayConfiguration configuration = getGatewayConfigurationByName(pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
			if(configuration == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Can't build STA. Reason: Gateway Configuration not found");
				}
				return;
			}

			configuration.getSTAMappings().apply(new DiameterPacketMappingValueProvider(pcrfResponse, answer, configuration, gatewayControllerContext), AvpAccumalators.of(answer));

			applyGroovyScript(answer, pcrfResponse, configuration);

		}

		@Override
		public DiameterStackContext getStackContext() {
			return diameterStack.getStackContext();
		}

		@Override
		public boolean isGxInterfaceEnable() {
			return isGxInterfaceEnabled;
		}

		@Override
		public boolean isRxInterfaceEnable() {
			return isRxInterfaceEnabled;
		}

		@Override
		public boolean isS9InterfaceEnable() {
			return isS9InterfaceEnabled;
		}

		@Override
		public boolean isGyInterfaceEnable() {
			return isGyInterfaceEnabled;
		}

		@Override
		public boolean isSubscrberRoming(DiameterPacket diameterPacket) {

			if(diameterPacket.getApplicationID() == ApplicationIdentifier.TGPP_S9.getApplicationId()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Considering subscriber as roaming with Session-Id: " +
							diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID)
							+ ".Reason: DiameterPacket is from S9Application");
				}

				return true;
			}

			return s9Utility.isSubscrberRoaming(diameterPacket);
		}

		@Override
		public DiameterGatewayConfiguration getGatewayConfiguration(String originatorGatewayName, String proxyAgentName) {

			DiameterGatewayConfiguration configuration = serverContext.getServerConfiguration().getDiameterGatewayConfByName(originatorGatewayName);
			if(configuration == null){
				configuration = serverContext.getServerConfiguration().getDiameterGatewayConfByName(proxyAgentName);
			}

			if(configuration != null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Diameter Gateway Configuration found for Gateway: " + configuration.getName());
				return configuration;
			} else {
				LogManager.getLogger().error(MODULE, "Diameter Gateway Configuration not found for" +
						(originatorGatewayName !=null ? " Originator gateway: " + originatorGatewayName : "" ) +
						(proxyAgentName !=null ? "   Proxy Gateway: " + proxyAgentName : "" ));
			}
			return null;
		}

		@Override
		public DiameterGatewayConfiguration getGatewayConfigurationByHostId(String hostIdentity) {
			DiameterGatewayConfiguration diameterGatewayConfiguration = serverContext.getServerConfiguration().getDiameterGatewayConfByHostIdentity(hostIdentity);
			/*
			 * It may happen that "host-identity" is provided in argument and during gateway configuration user has not provided host-identity.
			 * In such case, server configuration does not have entry based on host-idenity, so we try to find name from "peer-data".
			 * We fetch peer data by hostIdentity and if peer-data found we try to fetch configuration by name.
			 */
			if(diameterGatewayConfiguration == null) {
				PeerData peerData = diameterStack.getPeerData(hostIdentity);
				if(peerData != null) {
					diameterGatewayConfiguration = getGatewayConfigurationByName(peerData.getPeerName());
				}
			}

			return diameterGatewayConfiguration;
		}

		@Override
		public DiameterGatewayConfiguration getGatewayConfigurationByName(String gatewayName) {
			return serverContext.getServerConfiguration().getDiameterGatewayConfByName(gatewayName);
		}

		@Override
		public ApplicationHandler getApplicationHandler(Application diameterApplication,SupportedStandard supportedStandard) {
			Map<SupportedStandard, ApplicationHandler> appHandlerMap = appHandlers.get(diameterApplication);
			if(appHandlerMap == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No application Handlers found for Application = " + diameterApplication.name());
				return null;
			}


			ApplicationHandler applicationHandler = appHandlerMap.get(supportedStandard);

			if(applicationHandler == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No application Handler found for Application = " + diameterApplication.name()
							+ " and SupportedStandard = " + supportedStandard.getName() + " provide application handler for "
							+ SupportedStandard.RELEASE_9.getName());
				applicationHandler = appHandlerMap.get(SupportedStandard.RELEASE_9);
			}

			return applicationHandler;
		}

		@Override
		public List<DiameterGroovyScript> getDiameterGroovyScripts(String gatewayName) {
			return groovyScriptsMap.get(gatewayName);
		}

		@Override
		public NetVertexServerContext getServerContext() {
			return serverContext;
		}


		@Override
		public int getOverloadResultCode() {
			return resultCodeOnOverload;
		}

		@Override
		public OverloadAction getActionOnOverload() {
			return actionOnOverload;
		}

		@Override
		public boolean isCiscoGxEnabled() {
			return isCiscoGxInterfaceEnabled;
		}


		@Override
		public long getCurrentMessagePerMinute() {
			return diameterStack.getDiameterStatisticListner().getDiameterStatisticProvider().getMessagePerMinute();
		}

		@Override
		public GatewayMediator.ResultCodes reauthorizeSesion(PCRFKeyConstants lookUpKey,
															 String lookUpValue,
															 String reAuthCause,
															 boolean forcefulReAuth,
															 Map<PCRFKeyConstants,String> additionalParmas) {
			return mediator.reauthorize(lookUpKey, lookUpValue, reAuthCause, forcefulReAuth, additionalParmas);
		}

		@Override
		public void handleSession(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
			sessionHandler.handle(pcrfRequest, pcrfResponse);
		}

		@Override
		public Transaction createTransaction(GyTransactionType transactionType) {
			return transactionFactory.createTransaction(transactionType);
		}

		private void applyGroovyScript(DiameterPacket diameterPacket,
									   PCRFResponse pcrfResponse,
									   DiameterGatewayConfiguration diameterGatewayConfiguration){
			List<DiameterGroovyScript> scripts = groovyScriptsMap.get(diameterGatewayConfiguration.getName());
			if(scripts == null || scripts.isEmpty()){
				return;
			}

			for(DiameterGroovyScript script : scripts){
				try{
					script.preSend(diameterPacket, pcrfResponse);
				}catch(Exception ex){
					LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for Diameter-Packet with Session-ID= "
							+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) +" for gateway = " + diameterGatewayConfiguration.getName()
							+". Reason: "+ ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}

			}
		}

		@Override
		public void registerSessionFactoryType(long appId, SessionFactoryType sessionFactoryType) throws InitializationFailedException {
			diameterStack.getSessionFactoryManager().register(appId, sessionFactoryType, Optional.ofNullable(null));
		}
	}






	/**
	 * 	 register own realm to diameter stack
	 */
	private void registerRealms(){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Registering Realms to Diameter Stack");
		try {
			diameterStack.registerRoutingEntry(new RoutingEntryDataImpl(stackConfiguration.getOriginRealm(),RoutingActions.LOCAL,"0",new ArrayList<PeerGroupImpl>()));
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Realms registration completed");
		} catch (ElementRegistrationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Realm Registration fail for realm = " + stackConfiguration.getOriginRealm()+". Reason:" +e.getMessage());
			ignoreTrace(e);
		}

	}


	/**
	 * registerDiameterGateways register configured gateways as Peer in diameter stack
	 * @throws ElementRegistrationFaildException
	 */
	/**
	 * register configured gateways as Peer in diameter stack<br /><br />
	 *
	 * NetVertex Diameter Gateway map to Peer of diameter stack
	 * Peer should have host-Identity, realm, InetAddress, initConnectionDuration and ApplicationListener etc.<br /><br />
	 * StandardApplication like GxApplication (10415:16777238) is created by default
	 * and registered to diameter-stack so it is supported by all peers of diameter stack<br />
	 *
	 * For Application if custom application-Id is configured for any gateway then
	 * new application is created with custom Vendor-ID and Application-ID and
	 * register to that peer so that application is applicable to that peer Only
	 *
	 * @throws ElementRegistrationFailedException when fail to register peer or application listener
	 */
	private void registerDiameterGateways() throws ElementRegistrationFailedException{
		if(getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Registering Diameter gateways");

		Collection<DiameterGatewayConfiguration> diameterGatewayConfigurations = serverContext.getServerConfiguration().getDiameterGatewayConfigurations();

		ArrayList<PeerData> peers = new ArrayList<PeerData>();
		List<String> gatewayNames =  stackConfiguration.getGatewayList();
		for(DiameterGatewayConfiguration diameterGatewayConf : diameterGatewayConfigurations){
			try {

				/**
				 * if connectionURL List is null or Empty then we should understood that "ALL GATEWAY should be registered"
				 */
				if(gatewayNames != null && !gatewayNames.contains(diameterGatewayConf.getName())){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Skipping registration of " + diameterGatewayConf.getName() +
								". Reason: gateway is not registered in diameter-stack");
					continue;
				}

				PeerDataImpl peer = new PeerDataImpl();
				peer.setPeerName(diameterGatewayConf.getName());
				peer.setHostIdentity(diameterGatewayConf.getHostIdentity());
				peer.setInitiateConnectionDuration(diameterGatewayConf.getInitConnectionDuration() * 1000);
				peer.setRealmName(diameterGatewayConf.getRealm());
				peer.setRemoteInetAddress(diameterGatewayConf.getHostInetAddress());
				peer.setRemotePort(diameterGatewayConf.getHostPort());
				peer.setRemoteIPAddress(diameterGatewayConf.getHostIPAddress());
				peer.setLocalIPAddress(diameterGatewayConf.getLocalIPAddress());
				peer.setLocalPort(diameterGatewayConf.getLocalPort());
				peer.setWatchdogInterval(diameterGatewayConf.getDWInterval() * 1000);

				peer.setRequestTimeout(diameterGatewayConf.getRequestTimeout());
				peer.setRetransmissionCount(diameterGatewayConf.getRetransmissionCount());
				peer.setCERAVPString(diameterGatewayConf.getAdditionalCERAVPs());
				peer.setDPRAVPString(diameterGatewayConf.getAdditionalDPRAVPs());
				peer.setDWRAVPString(diameterGatewayConf.getAdditionalDWRAVPs());
				peer.setSendDPRonCloseEvent(diameterGatewayConf.isSendDPROnCloseEvent());
				peer.setSessionCleanUpOnCER(diameterGatewayConf.isSessCleanupOnCER());
				peer.setSessionCleanUpOnDPR(diameterGatewayConf.isSessCleanupOnDPR());
				peer.setPeerTimeout(diameterGatewayConf.getTimeout());
				peer.setTransportProtocol(diameterGatewayConf.getTransportProtocol());
				peer.setSocketReceiveBufferSize(diameterGatewayConf.getSocketReceiveBufferSize());
				peer.setSocketSendBufferSize(diameterGatewayConf.getSocketSendBufferSize());
				peer.setTCPNagleAlgo(diameterGatewayConf.isTCPNagalAlgo());
				peer.setSecondaryPeerName(diameterGatewayConf.getAlternateHostName());

				if(diameterGatewayConf.getSupportedStandard() == SupportedStandard.CISCOSCE){
					CiscoGxGateway ciscoGxGateway = ciscoGxGateways.get(diameterGatewayConf.getName());
					if(ciscoGxGateway != null){
						ciscoGxGateway.reInit();
					} else {
						ciscoGxGateway = new CiscoGxGateway(diameterGatewayConf);
						ciscoGxGateway.init();
						ciscoGxGateways.put(diameterGatewayConf.getName(), ciscoGxGateway);
					}
				}else{
					TGPPDiameterGateway diameterGateway = tgppGateways.get(diameterGatewayConf.getName());
					if(diameterGateway != null){
						diameterGateway.reInit();
					}else{
						diameterGateway = new TGPPDiameterGateway(gatewayControllerContext,diameterGatewayConf);
						diameterGateway.init();
						tgppGateways.put(diameterGatewayConf.getName(), diameterGateway);
					}

				}

				peers.add(peer);
			}catch (InitializationFailedException initializationFailEx) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, initializationFailEx.getMessage());
				LogManager.getLogger().trace(MODULE, initializationFailEx);
			}

		}


		if(!peers.isEmpty()){
			peers.trimToSize();

			diameterStack.registerPeers(peers);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Registered Peers : ");
			StringBuilder stringBuilder = new StringBuilder();

			for(PeerData peer : peers){
				stringBuilder.append(peer.getPeerName());
				stringBuilder.append(',');
			}

			stringBuilder.delete(stringBuilder.length() -1, stringBuilder.length());

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, stringBuilder.toString());
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE , "No diameter peer registred to diameter stack");
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Diameter gateways initialization completed");
	}


	private void initGroovyScripts(){

		HashMap<String, List<DiameterGroovyScript>> tempDiameterGroovyScriptsMap = new HashMap<String, List<DiameterGroovyScript>>(1,1);
		for(DiameterGatewayConfiguration diameterGatewayConfiguration : serverContext.getServerConfiguration().getDiameterGatewayConfigurations()){

			List<ScriptData> scriptsDatas = diameterGatewayConfiguration.getScriptsConfigs();

			if(scriptsDatas == null || scriptsDatas.isEmpty()){
				getLogger().debug(MODULE, "Groovy script creation for diameter gateway = " + diameterGatewayConfiguration.getName()
						+ " is skipped. Reason: no groovy script configured for gateway");
				continue;
			}

			ArrayList<DiameterGroovyScript> diameterGroovyScripts = new ArrayList<DiameterGroovyScript>();
			for(ScriptData groovyScriptData : scriptsDatas){
				File scriptFile = new File(groovyScriptData.getScriptName());

				if(!scriptFile.isAbsolute()){
					scriptFile = new File(serverContext.getServerHome() + File.separator + "scripts" + File.separator , groovyScriptData.getScriptName());
				}


				try{
					GroovyObject object =  ExternalScriptsManager.createGroovyObject(scriptFile, new Class<?> []{DiameterGatewayControllerContext.class, DiameterGatewayConfiguration.class}, new Object[]{gatewayControllerContext, diameterGatewayConfiguration});

					if(object == null){
						getLogger().warn(MODULE, "Can't add Groovy Object for script file \""+scriptFile.getName()+"\" for Diameter gateway = "
								+ diameterGatewayConfiguration.getName() + ". Reason: groovy scripts object not created for script file");
						continue;
					}

					if(object instanceof DiameterGroovyScript){
						DiameterGroovyScript diameterGroovyScript = (DiameterGroovyScript) object;
						diameterGroovyScript.init(groovyScriptData.getScriptArgumet());
						diameterGroovyScripts.add(diameterGroovyScript);
					}else {
						getLogger().warn(MODULE, "Can't add Groovy Object for script file \""+scriptFile.getName()+"\" for Diameter gateway = "
								+ diameterGatewayConfiguration.getName() + ". Reason: groovy scripts object is not instance of Diameter Groovy Script");
					}
				}catch(Exception ex){
					getLogger().error(MODULE, "Can't add Groovy Object for script file \""+scriptFile.getName()+"\" for Diameter gateway = "
							+ diameterGatewayConfiguration.getName() + ". Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}
			}
			diameterGroovyScripts.trimToSize();
			tempDiameterGroovyScriptsMap.put(diameterGatewayConfiguration.getName(), diameterGroovyScripts);

		}

		groovyScriptsMap = tempDiameterGroovyScriptsMap;
	}

	public List<GatewayInfo> getAllGatewayInformation() {
		List<GatewayInfo> gatewayInfos = new ArrayList<GatewayInfo>();
		for(TGPPDiameterGateway gateway : tgppGateways.values())
			gatewayInfos.add(new GatewayInfo(GatewayTypeConstant.DIAMETER.getVal(), null, null, gateway.getConfiguration().getConnectionURL(), gateway.getStatus(), getActiveSessionCount(gateway.getConfiguration().getHostIdentity())));
		for(CiscoGxGateway gateway : ciscoGxGateways.values())
			gatewayInfos.add(new GatewayInfo(GatewayTypeConstant.DIAMETER.getVal(), null,null, gateway.getConfiguration().getConnectionURL(), gateway.getStatus(), getActiveSessionCount(gateway.getConfiguration().getHostIdentity())));

		return gatewayInfos;
	}

	private long getActiveSessionCount(String hostIdentity) {
		List<SessionData> sessionDatas = gatewayControllerContext.getSessionLocator().getCoreSessionByGatewayAddress(hostIdentity);
		return sessionDatas != null ? sessionDatas.size() : 0;
	}

	/**
	 * Task to remove {@linkplain Transaction} older than 2 min
	 * @author harsh
	 *
	 */

	private class TransactionCleanupTask extends BaseIntervalBasedTask{

		@Override
		public long getInterval() {
			return 60;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Processing Transaction Cleanup Task");

			try{
				long staleTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2);
				Set<Entry<String, Transaction>> transactionEntries = currentTransactionsMap.entrySet();
				int totalRemoved = 0;
				for (Entry<String, Transaction> transactionEntry : transactionEntries){
					Transaction transaction = transactionEntry.getValue();
					if (transaction.getLastAccessTime() <= staleTime){
						if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Removing Transaction with Transaction-ID = "+ transaction.getTransactionId()
									+" of Type = " + transaction.getType());
						if (currentTransactionsMap.remove(transactionEntry.getKey()) != null) {
							totalRemoved++;
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Failed to Remove Transaction with Transaction-ID = "+ transaction.getTransactionId()
										+" of Type = " + transaction.getType()+ ". Reason: Transaction already removed");

						}
					}
				}

				long newSize = transactionEntries.size();

				LogManager.getLogger().warn(MODULE,"Total Transaction removed = " + totalRemoved
						+ ", No. of Remaining Transaction after Cleanup Task = " + newSize);
			}catch(Exception ex){
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Unable to Process Cleanup Task. Reason:" + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}

		}

	}

	/**
	 *
	 * Class for Time-Base Policy
	 * @author harsh
	 *
	 */
	private class PCCRuleExpiryListenerImpl extends PCCRuleExpiryListener{

		@Override
		public void reAuthSession(PCRFRequest request) {
			request.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.DIAMETER.getVal());

			if(SessionTypeConstant.RX.getVal().equals(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))){
				Transaction transaction = gatewayControllerContext.createTransaction(TransactionType.SESSION_DISCONNECT);
				transaction.process(EventTypes.SESSION_DISCONNECT, request);
			}else{
				handleSessionReAuthorization(request);
			}


		}

	}

	public Status getDiameterStackStatus(){
		return diameterStack.getStackStatus();
	}

	public Date getDiameterStackStartDate(){
		return stackStartDate;
	}

	public DiameterStatisticListener getDiameterStatisticListener(){
		return diameterStack.getDiameterStatisticListner();
	}

	public boolean stop(){
		if(diameterStack!=null){
			return diameterStack.stop();
		}
		return true;
	}

	private NetvertexApplication getApplicationListener(DiameterPacket packet){
		String vendorId = null;
		String appId = null;
		NetvertexApplication applisApplicationListener = null;

		IDiameterAVP vendorSpecificAppIdAVP = packet.getAVP(DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID);
		if(vendorSpecificAppIdAVP != null){
			List<IDiameterAVP> diameterAVPs =  vendorSpecificAppIdAVP.getGroupedAvp();
			if(diameterAVPs != null && diameterAVPs.size() >= 2) {
				for(IDiameterAVP diameterAVP :  diameterAVPs){
					if(DiameterAVPConstants.VENDOR_ID_INT == diameterAVP.getAVPCode()){
						vendorId = diameterAVP.getStringValue();
					} else if(DiameterAVPConstants.AUTH_APPLICATION_ID_INT == diameterAVP.getAVPCode()){
						appId = diameterAVP.getStringValue();
					}
				}

				applisApplicationListener = appListeners.get(vendorId+":"+appId);
			} else {
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Invalid value of for Vendor-Specific-Application-ID("
							+ DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID + ") AVP. "
							+ "fetching application based on Auth-Application-ID(" + DiameterAVPConstants.AUTH_APPLICATION_ID + ")");
			}
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Vendor-Specific-Application-ID(" + DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID
						+ ") not found. fetching application based on Auth-Application-ID("
						+ DiameterAVPConstants.AUTH_APPLICATION_ID + ")");
		}

		if(applisApplicationListener != null){
			return applisApplicationListener;
		}


		// if application listener not found from Vendor-Specific-Application-ID for any Reason. check for Auth-Application-ID
		IDiameterAVP authAppAvp = packet.getAVP(DiameterAVPConstants.AUTH_APPLICATION_ID);
		if(authAppAvp != null){
			appId = authAppAvp.getStringValue();
			vendorId = Long.toString(DiameterConstants.VENDOR_STANDARD);
			applisApplicationListener = appListeners.get(vendorId+":"+appId);
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Auth-Application-ID(" + DiameterAVPConstants.AUTH_APPLICATION_ID
						+ ") not found. fetching application based on Vendor-ID:Application-ID = "
						+ DiameterConstants.VENDOR_3GPP_ID + ":" + packet.getApplicationID());
		}

		if(applisApplicationListener != null){
			return applisApplicationListener;
		}


		//If applicationListener not found from Auth-Application-ID for any reason. check for Application-ID found from header
		appId = Long.toString(packet.getApplicationID());
		vendorId = Long.toString(DiameterConstants.VENDOR_3GPP_ID);
		applisApplicationListener = appListeners.get(vendorId + ":" + appId);

		return applisApplicationListener;
	}

	/**
	 * handleRequestForRAR perform session authorization by sending RAR to diameter gateway
	 * @param request
	 */
	public void handleSessionReAuthorization(PCRFRequest request) {

	    String sessionTypeVal = request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val);
        SessionTypeConstant sessionType = SessionTypeConstant.fromValue(sessionTypeVal);
		if (SessionTypeConstant.GY == sessionType) {
			handleGySessionReAuth(request);
			return;
		}

		String gatewayName = request.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);

		if (gatewayName == null) {
			getLogger().error(MODULE, "Unable to reAuthorize. Reason: Gateway name not found from diameter session for Session Id: "
					+ request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
			return;
		}

		DiameterGatewayConfiguration configuration = gatewayControllerContext.getGatewayConfigurationByName(gatewayName);
		if (configuration == null) {
			getLogger().error(MODULE, "Unable to process SNR. "
					+ "Reason: Gateway configuration not found for diameter session for gateway name: "
					+ gatewayName);
			return;
		}

		/*
		 * below attribute should be gateway specific during sending RAR or COA
		 */
		request.setAttribute(PCRFKeyConstants.USAGE_REPORTING_TYPE.getVal(), configuration.getUsageReportingType().val);
		request.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, configuration.getRevalidationMode().val);
		request.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, configuration.getName());

		request.addPCRFEvent(PCRFEvent.REAUTHORIZE);
		request.addPCRFEvent(PCRFEvent.AUTHENTICATE);
		request.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
		request.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.DIAMETER.getVal());

		Transaction transaction = DiameterGatewayController.this.gatewayControllerContext.createTransaction(TransactionType.SESSION_RE_AUTH);
		transaction.process(EventTypes.SESSION_RE_AUTH, request);
	}

    private void handleGySessionReAuth(PCRFRequest request) {
		PCRFResponse response = new PCRFResponseImpl();
		PCRFPacketUtil.buildPCRFResponse(request, response);
		DiameterRequest diameterRequest = gatewayControllerContext.buildRAR(response);

		gatewayControllerContext.sendRequest(diameterRequest, request.getAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.val), null);
	}


    public DiameterPeerState registerPeerStatusListener(String gatewayName, DiameterPeerStatusListener listener) throws StatusListenerRegistrationFailException{

		try{
			if(stackConfiguration.isEnabled() == false){
				LogManager.getLogger().error(MODULE, "Unable to register peer status listen to gatewayId: "+ gatewayName + ".Reason: Diameter Stack is disabled");
				throw new StatusListenerRegistrationFailException("Diameter Stack is disabled", ListenerRegFailResultCode.OTHER);
			}

			if(diameterStack == null || diameterStack.getStackContext() == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to register peer status listen to gatewayId: "+ gatewayName + ".Reason: Diameter Stack starup in progress");
				throw new StatusListenerRegistrationFailException("Diameter Stack starup in progress", ListenerRegFailResultCode.STARTUP_IN_PROGRESS);
			}

			DiameterGatewayConfiguration diameterGatewayConfiguration = serverContext.getServerConfiguration().getDiameterGatewayConfByName(gatewayName);

			if(diameterGatewayConfiguration == null){
				LogManager.getLogger().error(MODULE, "Unable to add peerStatusListener. Reason: gateway not found for gatewayId: " + gatewayName);
				throw new StatusListenerRegistrationFailException("Diameter Gateway not found for gatewayId: " + gatewayName, ListenerRegFailResultCode.PEER_NOT_FOUND);
			}

			return diameterStack.getStackContext().registerPeerStatusListener(diameterGatewayConfiguration.getName(), listener);

		}catch(StatusListenerRegistrationFailException ex){
			throw ex;
		}catch(Exception ex){
			throw new StatusListenerRegistrationFailException("Registration fail for gatewayId: " + gatewayName + ".Reason: " + ex.getMessage(),ex, ListenerRegFailResultCode.UNKNOWN);
		}
	}

	private boolean loadDictionary() {
		//WARNING: IF YOU PROVIDE THIS METHOD IN RELOAD THEN PLEASE TEST DIAMETERPACKET BUILD FOR SET USAGE METERING AVP WITH DEFAULT MAPPING
		//PLEASE CHECK DIAMETERPACKETBUILDER.setUsageMonitoringAVP method AND DIAMETERDEFALUTMAPPING CLASS

		try {
			LogManager.getLogger().info(MODULE, "Initializing Diameter Dictionary");
			DiameterDictionaryLoader diameterDictionaryLoader = new DiameterDictionaryLoader(sessionFactory, serverContext);
			diameterDictionaryLoader.load(DiameterDictionary.getInstance());

			LogManager.getLogger().info(MODULE, "Diameter Dictionary initialization completed.");
			return true;
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error in initializing Diameter Dictionary. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}

	}

	public void reInit(){
		HashMap<String, List<DiameterGroovyScript>> tempGroovyScriptsMap = groovyScriptsMap;
		try{
			if(stackConfiguration.isEnabled() == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Diameter Stack is disabled");
				return;
			}

			if(initialized == false){
				getLogger().warn(MODULE, "Unable to reload Diameter gateway controller. Reason: controller not initialized properly");
				return;
			}

			setOverloadParameter(diameterStack);
			registerDiameterGateways();

			initGroovyScripts();
			diameterStack.reload();


		}catch(Exception ex){
			groovyScriptsMap = tempGroovyScriptsMap;
			getLogger().error(MODULE, "Error while realoading Diameter gateway controller. Reason: " + ex.getMessage());
			getLogger().trace(MODULE, ex);
		}
	}



	private void setOverloadParameter(DiameterStack diameterStack){

		int resultCodeOnOverload = serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().getResultCodeOnOverload();


		if (resultCodeOnOverload == 0) {
			if(ResultCode.isValid(resultCodeOnOverload)){
				diameterStack.setResultCodeOnOverload(resultCodeOnOverload);
				this.resultCodeOnOverload = resultCodeOnOverload;
			}
			this.actionOnOverload = OverloadAction.DROP;
			diameterStack.setActionOnOverload(actionOnOverload);
		} else {
			this.actionOnOverload = OverloadAction.REJECT;
			this.resultCodeOnOverload = resultCodeOnOverload;
			diameterStack.setActionOnOverload(actionOnOverload);
			diameterStack.setResultCodeOnOverload(resultCodeOnOverload);

		}

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "OverloadAction For Netvertex: "+ this.actionOnOverload +" and for Diameter Stack: "+ diameterStack.getStackContext().getActionOnOverload());
			if (resultCodeOnOverload != 0) {
				LogManager.getLogger().info(MODULE, "Overload Result-Code For Netvertex: "+ this.resultCodeOnOverload +" and for Diameter Stack: "+ diameterStack.getStackContext().getOverloadResultCode());
			}
		}
	}

	private void registerMIBIndexRecorder() {
		try {
			diameterStack.registerMIBIndexRecorder(gatewayControllerContext.getServerContext().getServerHome() + File.separator + "system");
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Diameter MIB Index Recorder could not be registered, Reason: " +
					e.getMessage() + ". This will effect SNMP Index Management");
			LogManager.getLogger().trace(e);
		}
	}

	public Map<String,String> getSyCounter(String sySessionID) {
		///Add old Sy counters from DiameterSession,
		Session session = diameterStack.getStackContext().getOrCreateSession(sySessionID, ApplicationIdentifier.TGPP_SY.applicationId);

		String jsonString = (String)session.getParameter(SyApplication.SESSION_KEY_FOR_COUNTER);
		if(jsonString == null){
			if(getLogger().isLogLevel(LogLevel.WARN))
				getLogger().warn(MODULE, "Unable to add previous Sy counters from Diameter Session. Reason: Policy Counter not found in Sy Session:" + sySessionID);

			return null;
		}else {
			Map<String,String> syCounters = new LinkedHashMap<String, String>();
			try{
				JsonElement jsonElement = GsonFactory.defaultInstance().fromJson(jsonString, JsonElement.class);
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				for(Entry<String,JsonElement> entry: jsonObject.entrySet()){
					syCounters.put(PCRFKeyConstants.SY_COUNTER_PREFIX.val+ entry.getKey(), entry.getValue().getAsString());
				}
			}catch(Exception ex){
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Unable to add previous Sy counters from Diameter Session. Reason: Error while parsing json string:"+jsonString);
				getLogger().trace(MODULE, ex);
			}
			return syCounters;
		}

	}

	public String getDiameterStackRemarks() {
		if(Strings.isNullOrBlank(remarks)){
			return diameterStack.getRemarks();
		}
		return remarks;

	}

	public DiameterStack getDiameterStack(){
		return diameterStack;
	}


	public boolean isEnabled() {
		return isEnabled;
	}

	private class DiameterStackAlertManager implements IStackAlertManager {

		@Override
		public void scheduleAlert(StackAlertSeverity severity,
								  IStackAlertEnum alert, String alertGeneratorIdentity,
								  String alertMessage) {

			Alerts netvertexAlert = Alerts.fromStackAlert(alert);
			if (netvertexAlert == null) {
				LogManager.getLogger().warn(MODULE, "Alert relation not define for : " + alert.name());
			} else {
				serverContext.generateSystemAlert(severity.name(), netvertexAlert, alertGeneratorIdentity, alertMessage);
			}

		}

		@Override
		public void scheduleAlert(StackAlertSeverity severity,
								  IStackAlertEnum alert, String alertGeneratorIdentity,
								  String alertMessage, int alertIntValue, String alertStringValue) {

			Alerts netvertexAlert = Alerts.fromStackAlert(alert);
			if (netvertexAlert == null) {
				LogManager.getLogger().warn(MODULE, "Alert relation not define for : " + alert.name());
			} else {
				serverContext.generateSystemAlert(severity.name(), netvertexAlert, alertGeneratorIdentity, alertMessage,
						alertIntValue, alertStringValue);
			}

		}

	}

	private class DiameterPacketSendCommandImpl extends DiameterPacketSendCommand {

		@Override
		public void sendDiameterRequest(DiameterRequest request, String destinationPeer) throws Exception {
			ApplicationListener diameterApplicationListeners = null;
			long applicationId = request.getApplicationID();

			for (ApplicationListener applicationListener : diameterStack.getDiameterApplicationListeners()) {
				if (applicationListener.isSupportedApplication(applicationId)) {
					diameterApplicationListeners = applicationListener;
					break;
				}
			}

			if (diameterApplicationListeners != null) {
				DiameterPeerCommunicator destination = diameterStack.getStackContext().getPeerCommunicator(destinationPeer);
				if (destination == null) {
					throw new Exception("Unknown peer: " + destinationPeer);
				}
				IDiameterAVP sessionIdAVP = request.getAVP(DiameterAVPConstants.SESSION_ID);
				if (sessionIdAVP == null) {
					throw new Exception("No session id found in request.");
				}
				if (request.isServerInitiated() == false) {
					destination.sendClientInitiatedRequest((DiameterSession)diameterStack.getStackContext().getOrCreateSession(sessionIdAVP.getStringValue(), request.getApplicationID()),
							request, ResponseListener.NO_RESPONSE_LISTENER);
				} else {
					destination.sendServerInitiatedRequest((DiameterSession)diameterStack.getStackContext().getOrCreateSession(sessionIdAVP.getStringValue(), request.getApplicationID()),
							request, ResponseListener.NO_RESPONSE_LISTENER);
				}
			} else {
				throw new Exception("Unsupported Application Id : " + request.getApplicationID());
			}

		}

		@Override
		public void sendDiameterAnswer(DiameterAnswer answer, String destinationPeer) throws Exception {
			ApplicationListener diameterApplicationListeners = null;
			long applicationId = answer.getApplicationID();

			for (ApplicationListener applicationListener : diameterStack.getDiameterApplicationListeners()) {
				if (applicationListener.isSupportedApplication(applicationId)) {
					diameterApplicationListeners = applicationListener;
					break;
				}
			}

			if (diameterApplicationListeners != null) {
				DiameterPeerCommunicator destination = diameterStack.getStackContext().getPeerCommunicator(destinationPeer);
				if (destination == null) {
					throw new Exception("Unknown peer: " + destinationPeer);
				}

				PeerData peerData = DiameterGatewayController.this.diameterStack.getPeerData(destinationPeer);


				DiameterRequest diameterRequest = new DiameterRequest();
				diameterRequest.setCommandCode(answer.getCommandCode());
				diameterRequest.setApplicationID(answer.getApplicationID());
				diameterRequest.setEnd_to_endIdentifier(answer.getEnd_to_endIdentifier());
				diameterRequest.setHop_by_hopIdentifier(answer.getHop_by_hopIdentifier());
				diameterRequest.setRequestingHost(destinationPeer);


				diameterRequest.addAvps(answer.getAVPList());
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_HOST, diameterRequest, destination.getHostIdentity());
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_REALM, diameterRequest, peerData.getRealmName());
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_REALM, diameterRequest, Parameter.getInstance().getOwnDiameterRealm());
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, diameterRequest, Parameter.getInstance().getOwnDiameterIdentity());


				destination.sendAnswer(diameterRequest, answer);
			} else {
				throw new Exception("Unsupported Application Id : " + answer.getApplicationID());
			}

		}
	}

}