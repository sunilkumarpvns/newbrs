package com.elitecore.aaa.diameter.service.application;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.management.MBeanServer;

import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.config.ServiceLoggerDetail;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.DiameterLogMonitor;
import com.elitecore.aaa.core.util.cli.PeerStateCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.conf.DiameterPeerConfiguration;
import com.elitecore.aaa.diameter.conf.DiameterRoutingTableConfiguration;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.conf.PriorityConfigurable;
import com.elitecore.aaa.diameter.conf.PriorityEntryConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterRoutingTableConfigurationImpl;
import com.elitecore.aaa.diameter.conf.impl.PeerSecurityParameters;
import com.elitecore.aaa.diameter.conf.impl.RoutingEntryDataImpl;
import com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.service.drivers.DiameterCDRDriverFactoryImpl;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.translators.DiameterRatingTranslator;
import com.elitecore.aaa.diameter.translators.DiameterToCrestelOCSv2Translator;
import com.elitecore.aaa.diameter.translators.DiameterToRadiusTranslator;
import com.elitecore.aaa.diameter.translators.DiameterTranslator;
import com.elitecore.aaa.diameter.translators.DiameterWebServiceTranslator;
import com.elitecore.aaa.diameter.util.cli.DiameterApplicationDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterClearApplicationStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterClearPeerStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterClearRealmStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterClearSessionDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterClearStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterDictionaryDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterPeerDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterPeerGroupDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterRealmStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterResultCodeDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterRoutingTableDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterShowConfigDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterShowSessionDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterShowStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.DiameterStackStatisticDetailProvider;
import com.elitecore.aaa.diameter.util.cli.SendPacketCommand;
import com.elitecore.aaa.script.TranslationMappingScript;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.util.cli.cmd.ClearCommand;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.cli.cmd.ShowCommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.diameterapi.core.commands.DiameterPeerStatisticDetailProvider;
import com.elitecore.diameterapi.core.common.PolicyDataRegistrationFailedException;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityTable;
import com.elitecore.diameterapi.core.common.transport.sctp.SCTPNetworkConnector;
import com.elitecore.diameterapi.core.common.transport.stats.DiameterNetworkStatisticsProvider;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertManager;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.ApplicationListener;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.transport.DiameterNetworkConnector;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;
import com.elitecore.diameterapi.diameter.stack.StackInitializationFailedException;
import com.elitecore.diameterapi.diameter.translator.DiameterCopyPacketTranslator;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslatorPolicyData;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslatorPolicyDataImpl;
import com.elitecore.diameterapi.diameter.translator.function.FunctionPeerSequence;
import com.elitecore.diameterapi.diameter.translator.function.FunctionServerSequence;
import com.elitecore.diameterapi.diameter.translator.function.FunctionSessionSequence;
import com.elitecore.diameterapi.mibs.base.extended.DIAMETER_BASE_PROTOCOL_MIBimpl;
import com.elitecore.diameterapi.mibs.cc.extended.DIAMETER_CC_APPLICATION_MIBImpl;
import com.elitecore.diameterapi.mibs.custom.extended.DIAMETER_STACK_MIBImpl;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;
import com.elitecore.diameterapi.script.manager.DiameterScriptsManager;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.FunctionExpression;
import com.elitecore.license.base.commons.LicenseNameConstants;

public class EliteDiameterStack extends DiameterStack implements ReInitializable {
	
	private static final String MODULE = "ELT-DIA-STCK";	
	private DiameterStackConfigurable diameterStackConfiguration;
	private DiameterLogMonitor logMonitor;
	private int count =1;
	private IStackAlertManager stackAlertManager;
	private EliteRollingFileLogger stackLogger;
	
	private String remarks;
	private boolean initialized;
	private DiameterSessionManager diameterSessionManager;
	private final AAAServerContext serverContext;
	private com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager aaaDiameterPluginManager;
	
	
	public EliteDiameterStack(AAAServerContext serverContext,IStackAlertManager systemAlertManager, 
			com.elitecore.aaa.diameter.plugins.core.DiameterPluginManager aaaDiameterPluginManager) {		
		this.serverContext = serverContext;
		this.stackAlertManager = systemAlertManager;
		this.aaaDiameterPluginManager = aaaDiameterPluginManager;
		this.remarks = "";
	}

	public void initStack()throws StackInitializationFailedException {
		
		boolean isLicenseValid = getServerContext().isLicenseValid
				(LicenseNameConstants.DIAMETER_MODULE, String.valueOf(System.currentTimeMillis()));
		
		if (isLicenseValid == false) {
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Diameter Stack will not start, " +
						"Reason: License for "+LicenseNameConstants.DIAMETER_MODULE+" is not acquired or invalid."); 
			}
			remarks = ServiceRemarks.INVALID_LICENSE.remark;
			throw new StackInitializationFailedException(LicenseNameConstants.DIAMETER_MODULE_DISPLAY + " is not Licensed.");
		}
		
		diameterStackConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterStackConfiguration();
		
		LogManager.getLogger().info(MODULE, "Initializing Diameter Stack: " + diameterStackConfiguration);
		
		registerStackAlertManager(this.stackAlertManager);		
		Parameter.getInstance().setOriginStateId(readOriginStateId());
		
		if(diameterStackConfiguration.getLogger().getIsLoggerEnabled()){
			ServiceLoggerDetail serviceLoggerDetail = diameterStackConfiguration.getLogger();
			stackLogger = 
				new EliteRollingFileLogger.Builder(
					getServerContext().getServerInstanceName(),
					diameterStackConfiguration.getLogLocation())
				.rollingType(serviceLoggerDetail.getLogRollingType())
				.rollingUnit(serviceLoggerDetail.getLogRollingUnit())
				.maxRolledUnits(serviceLoggerDetail.getLogMaxRolledUnits())
				.compressRolledUnits(serviceLoggerDetail.getIsbCompressRolledUnit())
				.sysLogParameters(serviceLoggerDetail.getSysLogConfiguration().getHostIp(),
						serviceLoggerDetail.getSysLogConfiguration().getFacility())
				.build();
			stackLogger.setLogLevel(serviceLoggerDetail.getLogLevel());

			LogManager.setLogger(CommonConstants.DIAMETER_STACK_IDENTIFIER, stackLogger);
		}
		
		createLogMonitor();
		//this method submits the call to driver manager for creation of plugins
//		registerPluginsForCreation(((AAAServerContext)getServerContext()).getServerConfiguration().getPluginManagerConfiguration());
		
//		registerInOutPlugins(diameterStackConfiguration.getInPluginList(), diameterStackConfiguration.getOutPluginList());
		setIsNAIEnabled(diameterStackConfiguration.isNAIEnabled());
		setIsRealmVerificationRequired(diameterStackConfiguration.isRealmVerificationRequired());
		setNAIRealms(diameterStackConfiguration.getNaiRealmNames());
		
		// Duplicate Detection Parameters
		setDuplicateDetectionEnabled(diameterStackConfiguration.getIsDuplicateRequestDetectionEnabled());
		setDuplicatePacketPurgeInterval(diameterStackConfiguration.getDupicateRequestQueuePurgeInterval());
		
		String stackIdentity = ((AAAServerContext)getServerContext()).getServerConfiguration().getServerName();
		if (stackIdentity == null || stackIdentity.trim().length() == 0) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Diameter Stack Host Identity is not configured. Stack will not be started");
			this.remarks = ServiceRemarks.SERVER_NAME_OR_DOMAIN_NAME_NOT_CONFIGURED.remark;
			throw new StackInitializationFailedException("Diameter Stack Host Identity not configured in Server Configuration"); 
		}
		String stackRealm = ((AAAServerContext)getServerContext()).getServerConfiguration().getDomainName();
		if (stackRealm == null || stackRealm.trim().length() == 0) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Diameter Stack Realm is not configured. Stack will not be started");
			this.remarks = ServiceRemarks.SERVER_NAME_OR_DOMAIN_NAME_NOT_CONFIGURED.remark;
			throw new StackInitializationFailedException("Diameter Stack Realm not configured in Server Configuration");
		}

		// FIXME use this configuration
//		this.setMaxWorkerThreads(diameterStackConfiguration.getMaxThreadPoolSize());
		this.setDiameterStackIdentity(stackIdentity);
		this.setDiameterStackURI(diameterStackConfiguration.getOwnURI());
		this.setDiameterStackRealm(stackRealm);
		this.setRoutingTableName(diameterStackConfiguration.getRoutingTableName());
		// Added For Diameter Session Clean Up
		this.setSessionInterval(diameterStackConfiguration.getDiameterSessionCleanupDetail().getSessionCleanupInterval());
		this.setSessionTimeOut(diameterStackConfiguration.getDiameterSessionCleanupDetail().getSessionTimeOut());
		this.setServerInstanceId(getServerContext().getServerInstanceId());
		registerExpressionLibraryFunctions();
		
		validateAndRegisterDPAModules();
		validateAndRegisterDTAModules();
		
		registerSSLContextFactory();
		registerDiameterCDRDriverFactory(new DiameterCDRDriverFactoryImpl((AAAServerContext) getServerContext()));
		registerMIBIndexRecorder();
		
		this.initDiameterSessionManager();
		this.initDiameterPeers();
		this.initDiameterRealms();
		
		setWorkerThreadPriority(diameterStackConfiguration.getWorkerThreadPriority());
		setMaxRequestQueueSize(diameterStackConfiguration.getMaxRequestQueueSize());
		setMaxThreadPoolSize(diameterStackConfiguration.getMaxThreadPoolSize());
		setMinThreadPoolSize(diameterStackConfiguration.getMinThreadPoolSize());
		setMainThreadPriority(diameterStackConfiguration.getMainThreadPriority());
		setFairnessPolicy(createPriorityTable());
		
		this.addNetworkConnector(createSCTPNetworkConnector());
		this.addNetworkConnector(createTCPNetworkConnector());
		
		this.initDiameterScriptsManager();
		registerDiameterAndAccessPolicies();
		try {
			registerDiameterWSRequestHandler();
		} catch (ElementRegistrationFailedException e) {
			LogManager.getLogger().warn(MODULE, "Error in registering diameter web service request handler, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Scheduling Concurrent Diameter Session Calculator Task");
		}
		
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(
				new ConcurrentSessionLicenseTask(getSessionFactoryManager(), getServerContext()));
		
		super.init();
		
		initialized = true;
	}
	
	private void validateAndRegisterDPAModules() {
		boolean isLicenseValid = getServerContext().isLicenseValid(LicenseNameConstants.DPA_MODULE, 
					String.valueOf(System.currentTimeMillis()));

		/**
		 * License check for DPA module.
		 */
		if (isLicenseValid == false) {
			if(LogManager.getLogger().isWarnLogLevel())
				LogManager.getLogger().info(MODULE, "Diameter Translator will not register, " +
						"Reason: License for "+LicenseNameConstants.DPA_MODULE+" is not acquired or invalid.");
			return;
		}
		TranslationAgent.getInstance().registerTranslator(new DiameterTranslator(getServerContext(),getStackContext()));
		registerDPATypeTranslatorPolicyData();
		registerCopyPacketMappingPolicyData();
	}
	
	private void validateAndRegisterDTAModules() {
		/**
		 * License check for DTA module.
		 */
		boolean isLicenseValid = getServerContext().isLicenseValid(LicenseNameConstants.DTA_MODULE, 
				String.valueOf(System.currentTimeMillis()));
		
		if (isLicenseValid == false) {
			if(LogManager.getLogger().isWarnLogLevel())
				LogManager.getLogger().warn(MODULE, "Diameter to Rating, Diameter to WS, Diameter to OCSV2 Translator, Diameter to Radius " +
						"will not register, Reason: License for "+LicenseNameConstants.DTA_MODULE+
						" is not acquired or invalid.");
			return;
		}
		TranslationAgent.getInstance().registerTranslator(new DiameterRatingTranslator(getServerContext(),getStackContext()));
		TranslationAgent.getInstance().registerTranslator(new DiameterWebServiceTranslator(getServerContext(),getStackContext()));
		TranslationAgent.getInstance().registerTranslator(new DiameterToCrestelOCSv2Translator(getServerContext(),getStackContext()));
		TranslationAgent.getInstance().registerTranslator(new DiameterToRadiusTranslator(getServerContext()));
		registerDTATypeTranslationMappingPolicyData();
	}
	
	private void registerExpressionLibraryFunctions() {
		FunctionExpression functionExpression = new FunctionServerSequence(getStackContext());
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Registering " + functionExpression.getName() + " function in Expression Library");
		}
		Compiler.getDefaultCompiler().addFunction(functionExpression);
		
		functionExpression = new FunctionSessionSequence(getStackContext());
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Registering " + functionExpression.getName() + " function in Expression Library");
		}
		Compiler.getDefaultCompiler().addFunction(functionExpression);
		functionExpression = new FunctionPeerSequence(getStackContext());
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Registering " + functionExpression.getName() + " function in Expression Library");
		}
		Compiler.getDefaultCompiler().addFunction(functionExpression);
	}

	private void registerMIBIndexRecorder() {
		try {
			this.registerMIBIndexRecorder(getServerContext().getServerHome() + File.separator + "system");
		} catch (FileAllocatorException e) {
			LogManager.getLogger().warn(MODULE, "Diameter MIB Index Recorder could not be registered, Reason: " + 
					e.getMessage() + ". This may effect SNMP Index Management");
			LogManager.getLogger().trace(e);
		}
	}
	
	private void registerDiameterAndAccessPolicies() {
		
		try {
			DiameterPolicyManager.getInstance(DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY).initCache(getServerContext(), false);
		} catch (ManagerInitialzationException e1) {
			LogManager.getLogger().error(MODULE, "Diameter Policy Manager Could not be Initialized for Diameter Stack. Reason " + e1.getMessage());
			LogManager.getLogger().trace(e1);
		}
		
		try {
			AccessPolicyManager.getInstance().initCache(getServerContext().getServerHome());
			getServerContext().registerCacheable(AccessPolicyManager.getInstance());
		} catch (ManagerInitialzationException e1) {
			LogManager.getLogger().error(MODULE, "Access Policy Manager Could not be Initialized. Reason " + e1.getMessage());
			LogManager.getLogger().trace(e1);
		}
		
	}

	private void initDiameterScriptsManager(){
		DiameterRoutingTableConfigurationImpl routingTableConfiguration = (DiameterRoutingTableConfigurationImpl) ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterRoutingConfiguration().getDiameterRoutingTableConfiguration(diameterStackConfiguration.getRoutingTableName());
		if (routingTableConfiguration == null) {
			return;	
		}
		registerRouterGroovy(routingTableConfiguration.getScriptName());

		try {
			DiameterScriptsManager.getInstance().init(getStackContext(), serverContext.getServerConfiguration().getScriptConfigurable().getRouterScript());
		} catch (InitializationFailedException e) {
			LogManager.getLogger().debug(MODULE,e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public boolean start() {
		
		if(this.initialized == false) {
			LogManager.getLogger().error(MODULE, "Could not start Diameter stack, " +
					"Reason: Error in initializing Stack, " + getRemarks());
			return false;
		}
		
		boolean isStarted =  super.start();

		if (isStarted) {
			
			registerInOutPlugins(diameterStackConfiguration.getInPluginList(), diameterStackConfiguration.getOutPluginList());
			
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			try{
				DIAMETER_BASE_PROTOCOL_MIBimpl diameter_BASE_PROTOCOL_MIBimpl = 
						new DIAMETER_BASE_PROTOCOL_MIBimpl(getStackContext() ,
								getDiameterStatisticListner());
				
				diameter_BASE_PROTOCOL_MIBimpl.populate(mBeanServer, null);
				((AAAServerContext)getServerContext()).registerSnmpMib(diameter_BASE_PROTOCOL_MIBimpl);
			}catch(Throwable t){
				LogManager.getLogger().error(MODULE, "Diameter Base Protocol MIB registration failed. Reason: " + t.getMessage());
				LogManager.getLogger().trace(MODULE, t);
			}

			try{
				DIAMETER_CC_APPLICATION_MIBImpl cc_APPLICATION_MIBImpl = new DIAMETER_CC_APPLICATION_MIBImpl(getDiameterStatisticListner());
				cc_APPLICATION_MIBImpl.populate(mBeanServer, null);
				((AAAServerContext)getServerContext()).registerSnmpMib(cc_APPLICATION_MIBImpl);
			}catch(Throwable t){
				LogManager.getLogger().error(MODULE, "Diameter CC Applocation MIB registration failed. Reason: " + t.getMessage());
				LogManager.getLogger().trace(MODULE, t);
			}
			
			try {
				DIAMETER_STACK_MIBImpl diameterStackMib = new DIAMETER_STACK_MIBImpl(getDiameterStatisticListner(),getStackStatus());
				diameterStackMib.populate(mBeanServer, null);
				((AAAServerContext)getServerContext()).registerSnmpMib(diameterStackMib);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Diameter Stack MIB registration failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		this.remarks = super.getRemarks();
		return isStarted;
	}

	private void registerTranslationMappingPolicies(){
		registerDTATypeTranslationMappingPolicyData();
		registerCopyPacketMappingPolicyData();
	}

	private void registerDTATypeTranslationMappingPolicyData(){
		Map<String, TranslatorPolicyData> policyData = ((AAAServerContext)getServerContext()).getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyDataMap();
		for(Entry<String,TranslatorPolicyData> entry: policyData.entrySet()){

			try {
				TranslatorPolicyData policyData2 = entry.getValue();
				if ((AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(policyData2.getFromTranslatorId()) &&
						AAATranslatorConstants.CRESTEL_OCSv2_TRANSLATOR.equalsIgnoreCase(policyData2.getToTranslatorId())) 
						||
					(AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(policyData2.getFromTranslatorId()) &&
						AAATranslatorConstants.RATING_TRANSLATOR.equalsIgnoreCase(policyData2.getToTranslatorId()))
						||
					(AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(policyData2.getFromTranslatorId()) &&
							AAATranslatorConstants.RADIUS_TRANSLATOR .equalsIgnoreCase(policyData2.getToTranslatorId()))
						||
					(AAATranslatorConstants.WEBSERVICE_TRANSLATOR.equalsIgnoreCase(policyData2.getFromTranslatorId()) &&
						AAATranslatorConstants.RATING_TRANSLATOR.equalsIgnoreCase(policyData2.getToTranslatorId()))
						||
					(AAATranslatorConstants.WEBSERVICE_TRANSLATOR.equalsIgnoreCase(policyData2.getFromTranslatorId()) &&
							AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(policyData2.getToTranslatorId()))
				) {

					TranslationAgent.getInstance().registerPolicyData(entry.getValue());
				}
			} catch (PolicyDataRegistrationFailedException e) {
				LogManager.getLogger().error(MODULE, "PolicyData Registraton Failed for: " + entry.getValue().getName() + 
						", reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
	private void registerDPATypeTranslatorPolicyData() {
		
		Map<String, TranslatorPolicyData> policyData = ((AAAServerContext)getServerContext()).getServerConfiguration().
																getTranslationMappingConfiguration().getTranslatorPolicyDataMap();
		
		for(Entry<String,TranslatorPolicyData> entry: policyData.entrySet()){

			try {
				TranslatorPolicyData policyData2 = entry.getValue();
				if (AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(policyData2.getFromTranslatorId())
						&&
						AAATranslatorConstants.DIAMETER_TRANSLATOR.equalsIgnoreCase(policyData2.getToTranslatorId()))			
				{
					
					TranslationAgent.getInstance().registerPolicyData(entry.getValue());
				}
			} catch (PolicyDataRegistrationFailedException e) {
				LogManager.getLogger().error(MODULE, "PolicyData Registraton Failed for: " + entry.getValue().getName() + 
						", reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
	private void registerCopyPacketMappingPolicyData(){
		List<CopyPacketTranslatorPolicyDataImpl> policyDataList = ((AAAServerContext)getServerContext())
																	.getServerConfiguration()
																	.getCopyPacketTranslationConfiguration()
																	.getCopyPacketMappingList();
		if(Collectionz.isNullOrEmpty(policyDataList)){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "No Copy Packet Mapping Configuration is configured.");
			}
			return;
		}
		for(CopyPacketTranslatorPolicyData mapping : policyDataList){

			if( (mapping.getFromTranslatorId().equalsIgnoreCase(AAATranslatorConstants.DIAMETER_TRANSLATOR))
					&&
				(mapping.getToTranslatorId().equalsIgnoreCase(AAATranslatorConstants.DIAMETER_TRANSLATOR))
				
			  ){
				TranslationAgent.getInstance().registerCopyPacketTranslator(new DiameterCopyPacketTranslator(mapping, 
									getServerContext().getExternalScriptsManager(), 
									TranslationMappingScript.class));
			}
		}
	}

	
	private void registerSSLContextFactory(){
		TrustedCAConfiguration trustStore = ((AAAServerContext)getServerContext()).getServerConfiguration().getTrustedCAConfiguration();
		CRLConfiguration crlStore = ((AAAServerContext)getServerContext()).getServerConfiguration().getCRLConfiguration();

		registerEliteSSLContextFactory(new EliteSSLContextFactory(trustStore, crlStore));
	}


	private void registerPluginsForCreation(AAAPluginConfManager pluginManagerConfiguration) {
		//registering the external plugins
		DiameterPluginManager.createAndRegisterExternalPlugins(getServerContext());
		
		//Guard statement
		if(pluginManagerConfiguration == null){
			LogManager.getLogger().warn(MODULE, "Plugin Manager configuration not found. No plugins will be registered.");
			return;
		}
		
		List<PluginInfo> pluginInfos = pluginManagerConfiguration.getDiameterPluginInfoList();
		for(PluginInfo pluginInfo : pluginInfos){
			PluginConfiguration pluginConfiguration = pluginManagerConfiguration.getDiameterPluginConfiguration(pluginInfo.getPluginName());
			if(pluginConfiguration != null){
				try {
					DiameterPluginManager.createAndRegisterPlugin(pluginInfo, pluginConfiguration, getServerContext());
				} catch (Exception e) {
					LogManager.getLogger().trace(e);
					LogManager.getLogger().error(MODULE, "Error in instantiating plugin : " + pluginInfo.getPluginName());
				}
			}
		}
	}

	private void initDiameterPeers() {
		DiameterPeerConfiguration peerConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterPeerConfiguration();
		List<PeerData> peersData = peerConfiguration.getPeerDataList();

		try {
			registerPeers(peersData);
		} catch (ElementRegistrationFailedException e) {
			LogManager.getLogger().trace(e);
		}
	}

	private void initDiameterRealms() {

		String configuredRoutingTable = diameterStackConfiguration.getRoutingTableName();
		if(configuredRoutingTable == null || configuredRoutingTable.trim().length() == 0) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Routing Table configuration not found");
			}
			return;
		}
		DiameterRoutingTableConfiguration routingTableConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterRoutingConfiguration().getDiameterRoutingTableConfiguration(configuredRoutingTable);
		if(routingTableConfiguration  == null){
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Routing Table configuration not found Routing Table: " + configuredRoutingTable);
			}
			return;
		}
		
		setActionOnOverload(routingTableConfiguration.getOverloadAction());
		setResultCodeOnOverload(routingTableConfiguration.getResultCode());

		try {
			registerRoutingEntries(getLicensedRoutingEntryDataList());
		} catch (ElementRegistrationFailedException e) {
			LogManager.getLogger().warn(MODULE, "Error in Routing Entry Registration, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}
	
	private INetworkConnector createTCPNetworkConnector() {
		DiameterNetworkConnector diameterNetworkConnector = new DiameterNetworkConnector(this);
		diameterNetworkConnector.setSocketReceiveBufferSize(diameterStackConfiguration.getSocketReceiveBufferSize());
		diameterNetworkConnector.setSocketSendBufferSize(diameterStackConfiguration.getSocketSendBufferSize());
		diameterNetworkConnector.setNetworkAddress(diameterStackConfiguration.getIpAddress());
		diameterNetworkConnector.setNetworkPort(diameterStackConfiguration.getPort());
		diameterNetworkConnector.setSecurityStandard(diameterStackConfiguration.getSecurityStandard());
		diameterNetworkConnector.setDefalutSSLParameter(createSecutiryParameter(diameterStackConfiguration.getSecurityParameters()));
		return diameterNetworkConnector;
	}
	
	private INetworkConnector createSCTPNetworkConnector() {
		SCTPNetworkConnector connector = new SCTPNetworkConnector(this);
		connector.setSocketReceiveBufferSize(diameterStackConfiguration.getSocketReceiveBufferSize());
		connector.setSocketSendBufferSize(diameterStackConfiguration.getSocketSendBufferSize());
		connector.setNetworkAddress(diameterStackConfiguration.getIpAddress());
		connector.setNetworkPort(diameterStackConfiguration.getPort());
		connector.setSecurityStandard(diameterStackConfiguration.getSecurityStandard());
		connector.setDefalutSSLParameter(createSecutiryParameter(diameterStackConfiguration.getSecurityParameters()));
		return connector;
	}
	
	
	/**
	 * This method creates PriorityTable based on priority configuration,
	 * 
	 * IF priority configuration not found THEN
	 * 		it returns null
	 * 
	 * IF priority entry not found THEN
	 * 		it returns null
	 * 
	 * IF priority entry found but all invalid value THEN	
	 * 		it return null
	 * 
	 * @author Chetan.Sankhala
	 * @return PriorityTable
	 */
	private PriorityTable createPriorityTable() {
		
		PriorityConfigurable priorityConfigurable = ((AAAServerContext)getServerContext()).getServerConfiguration().getPriorityConfigurable();
		if(priorityConfigurable == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Skipping priority table reading. Reason: priority table configuration not found");
			}
			//FIXME when 6.5 merges to 6.6 then we have to use Optional<PriorityTable>
			return null;
		}

		List<PriorityEntryConfigurable> priorityEntryConfigurables = priorityConfigurable.getPriorityEntryConfigurables();
		if(Collectionz.isNullOrEmpty(priorityEntryConfigurables)) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Skipping priority table reading. Reason: no priority details found");
			}
			return null;
		}
		
		List<PriorityEntry> priorityEntries = new ArrayList<PriorityEntry>();
		PriorityEntry priorityEntry = null;
		for(PriorityEntryConfigurable detail : priorityEntryConfigurables){
			try {
				priorityEntry = new PriorityEntry.PriorityEntryBuilder(getSessionFactoryManager())
								.withApplicationIds(detail.getApplicationIds())
								.withCommandCodes(detail.getCommandCodes())
								.withIpAddresses(detail.getIpAddresses())
								.withDiameterSessionType(detail.getDiameterSessionType())
								.withPriority(detail.getPriority())
								.build();
				priorityEntries.add(priorityEntry);
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Skipping Priority: " + detail.getPriorityId() + ". Reason: " + e.getMessage());
					LogManager.getLogger().trace(e);
				}
			}
		}
		
		
		if(priorityEntries.isEmpty()){
			return null;
		}
		return new PriorityTable(priorityEntries);
	}
	private EliteSSLParameter createSecutiryParameter(PeerSecurityParameters securityParameters) {
		if(securityParameters == null){
			LogManager.getLogger().warn(MODULE, "Unable to create security parameter. Reason: stack level security parameter is not configured");
			return null;
		}
		
		
		AAAServerContext aaaServerContext = (AAAServerContext)getServerContext();
		ServerCertificateProfile serverCertificateProfile = aaaServerContext.getServerConfiguration().getServerCertificateConfiguration().getServerCertificateProfileById(securityParameters.getServerCertificateId());
		EliteSSLParameter eliteSSLParameter = new EliteSSLParameter(serverCertificateProfile, securityParameters.getMinTlsVersion(), securityParameters.getMaxTlsVersion());
		eliteSSLParameter.setClientCertificateRequestRequired(securityParameters.getClientCertificateRequest());
		eliteSSLParameter.setValidateCertificateExpiry(securityParameters.getValidateCertificateExpiry());
		eliteSSLParameter.setValidateCertificateRevocation(securityParameters.getValidateCertificateRevocation());
		eliteSSLParameter.setValidateCertificateCA(securityParameters.getValidateCertificateCA());
		eliteSSLParameter.setValidateSubjectCN(securityParameters.getValidateHost());
		eliteSSLParameter.setHandshakeTimeout(securityParameters.getHandShakeTimeout());
		String cipherSuitesStr= securityParameters.getEnabledCiphersuites();
	
		if(cipherSuitesStr != null){
			ArrayList<CipherSuites> cipherSuites = new ArrayList<CipherSuites>();
			
			for(String cipherSuiteId : cipherSuitesStr.split(",")){
				try{
					CipherSuites cipherSuite = CipherSuites.fromCipherCode(Integer.parseInt(cipherSuiteId));
					if(cipherSuite == null){
						LogManager.getLogger().debug(MODULE, "Invalid value: " + cipherSuiteId + " for cipher suites. Value is not in the supported cipher suite list");
						continue;
					}
					
					cipherSuites.add(cipherSuite);
				}catch(NumberFormatException ex){
					LogManager.getLogger().debug(MODULE, "Invalid value: " + cipherSuiteId + " for cipher suites. Value should be in numeric");
				}
			}
			
			eliteSSLParameter.addEnabledCiphersuites(cipherSuites);
		}
		return eliteSSLParameter;
	}
	
	public List<ICommand> getCliCommands() {
		List<ICommand> cmdList = new ArrayList<ICommand>();
		
		cmdList.add(new PeerStateCommand(){

			@Override
			public Map<String, IStateEnum> getPeersState() {
				return EliteDiameterStack.this.getPeersState();
			}

			@Override
			protected boolean closePeer(String peerHostIdentity) {
				return EliteDiameterStack.this.closePeer(peerHostIdentity);
			}

			@Override
			protected boolean forceClosePeer(String peerHostId) {
				return EliteDiameterStack.this.forceClosePeer(peerHostId);
			}

			@Override
			protected boolean startPeer(String peerHostIdentity) {
				return EliteDiameterStack.this.startPeer(peerHostIdentity);
			}
			
		});

		try {
			DiameterDetailProvider diameterDetailProvider = new DiameterDetailProvider();
			DiameterShowStatisticDetailProvider showStatsDetailProvider = new DiameterShowStatisticDetailProvider();

			showStatsDetailProvider.registerDetailProvider(
					new DiameterStackStatisticDetailProvider(getDiameterStatisticListner().getDiameterStatisticProvider()) {

						@Override
						protected DiameterNetworkStatisticsProvider getDiameterNetworkStatisticsProvider() {
							return getStatisticsProvider();
						}
						
					});
			showStatsDetailProvider.registerDetailProvider(new DiameterApplicationDetailProvider(
					getDiameterStatisticListner().getDiameterStatisticProvider(),
					getDiameterStatisticListner().getDiameterConfigProvider()));
			showStatsDetailProvider.registerDetailProvider(new DiameterRealmStatisticDetailProvider(getDiameterStatisticListner().getDiameterStatisticProvider()));
			showStatsDetailProvider.registerDetailProvider(new DiameterResultCodeDetailProvider(getDiameterStatisticListner().getDiameterStatisticProvider()));
			showStatsDetailProvider.registerDetailProvider(new DiameterPeerStatisticDetailProvider(
					getDiameterStatisticListner().getDiameterStatisticProvider(),
					getDiameterStatisticListner().getDiameterConfigProvider()));

			diameterDetailProvider.registerDetailProvider(showStatsDetailProvider);
			
			DiameterShowConfigDetailProvider showConfigDetailProvider = DiameterShowConfigDetailProvider.getInstance();
			showConfigDetailProvider.registerDetailProvider(new DiameterPeerGroupDetailProvider(getServerContext()));
			showConfigDetailProvider.registerDetailProvider(new DiameterPeerDetailProvider(getDiameterStatisticListner().getDiameterConfigProvider()));
			diameterDetailProvider.registerDetailProvider(showConfigDetailProvider);
			
			diameterDetailProvider.registerDetailProvider(new DiameterDictionaryDetailProvider(getServerContext().getServerHome()));
			diameterDetailProvider.registerDetailProvider(new DiameterRoutingTableDetailProvider((AAAServerContext)getServerContext()));
			diameterDetailProvider.registerDetailProvider(new DiameterShowSessionDetailProvider(getSessionFactoryManager()));
			ShowCommand.registerDetailProvider(diameterDetailProvider);
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Diameter show Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		try {
			DiameterDetailProvider diameterClearDetailProvider = new DiameterDetailProvider();

			DiameterClearStatisticDetailProvider clearStatisticDetailProvider = new DiameterClearStatisticDetailProvider(getDiameterStatisticListner().getDiameterStatisticResetter());
			clearStatisticDetailProvider.registerDetailProvider(new DiameterClearPeerStatisticDetailProvider(getDiameterStatisticListner().getDiameterStatisticResetter()));
			clearStatisticDetailProvider.registerDetailProvider(new DiameterClearApplicationStatisticDetailProvider(
					getDiameterStatisticListner().getDiameterStatisticResetter(), 
					getDiameterStatisticListner().getDiameterStatisticProvider()));
			clearStatisticDetailProvider.registerDetailProvider(new DiameterClearRealmStatisticDetailProvider(getDiameterStatisticListner().getDiameterStatisticResetter()));
			
			diameterClearDetailProvider.registerDetailProvider(clearStatisticDetailProvider);
			diameterClearDetailProvider.registerDetailProvider(new DiameterClearSessionDetailProvider(getSessionFactoryManager()));

			ClearCommand.registerDetailProvider(diameterClearDetailProvider);

		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to register Diameter show Detail Provider. Reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		ICommand sendCommand = new SendPacketCommand(){

			@Override
			public void sendDiameterRequest(DiameterRequest request, String destinationPeer) throws Exception {
				DiameterPeerCommunicator destination = getStackContext().getPeerCommunicator(destinationPeer);
				if (destination == null) {
					throw new Exception("Unknown peer: " + destinationPeer);
				}
				IDiameterAVP sessionIdAVP = request.getAVP(DiameterAVPConstants.SESSION_ID);
				if (sessionIdAVP == null) {
					throw new Exception("No session id found in request.");
			}
				if (request.isServerInitiated() == false) {
					destination.sendClientInitiatedRequest((DiameterSession)getStackContext().getOrCreateSession(sessionIdAVP.getStringValue(), request.getApplicationID()),
						request, ResponseListener.NO_RESPONSE_LISTENER);
				} else {
					destination.sendServerInitiatedRequest((DiameterSession)getStackContext().getOrCreateSession(sessionIdAVP.getStringValue(), request.getApplicationID()),
							request, ResponseListener.NO_RESPONSE_LISTENER);
				}
			}
			
			@Override
			public void sendDiameterAnswer(DiameterAnswer answer, String destinationPeer) throws Exception {
				DiameterPeerCommunicator destination = getStackContext().getPeerCommunicator(destinationPeer);
				if (destination == null) {
					throw new Exception("Unknown peer: " + destinationPeer);
				}
				
				PeerData peerData = getPeerData(destinationPeer);
				
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
			}
		};
		cmdList.add(sendCommand);
		
		return cmdList;
	}
	
	private ApplicationListener getDiameterApplicationListener(long applicationId) {
		ApplicationListener diameterApplicationListener = null;
		for (ApplicationListener applicationListener : getDiameterApplicationListeners()) {
			if (applicationListener.isSupportedApplication(applicationId)) {
				diameterApplicationListener = applicationListener;
				break;
			}
		}
		return diameterApplicationListener;
	}
	
	@Override
	public void applyMonitoryLogLevel(DiameterPacket diameterPacket) {
		if (logMonitor.evaluate(diameterPacket, null)) {
    		LogManager.getLogger().addThreadName(Thread.currentThread().getName());
			diameterPacket.setParameter(MonitorLogger.MONITORED, true);
    	}
    }

	private void createLogMonitor() {
		logMonitor = new DiameterLogMonitor(getServerContext().getTaskScheduler());
		try {
			LogMonitorManager.getInstance().registerMonitor(logMonitor);
		} catch (RegistrationFailedException ex) {
			LogManager.getLogger().trace(MODULE, ex);
		}
	}

	@Override
	public String getRemarks() {
		return remarks;
	}
	
	@Override
	public void reInit(){
		
		if(getStackStatus() == Status.STOPPED || 
				getStackStatus() == Status.STOPPING ||
				getStackStatus() == Status.NOT_INITIALIZE) {
			LogManager.getLogger().error(MODULE, "Inorder to Re-Initialize Stack, " +
					"Diameter Stack should successfully start atleast once. " +
					"Unable to Re-Initialize due to: " + remarks);
			return;
		}
		
		//refreshing the reference to service configuration
		this.diameterStackConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterStackConfiguration();
		
		// re initializing stack level logger
		reInitLogLevel();
		
		//re registering the translation mapping policies
//		registerTranslationMappingPolicies();

		//re registering Elite SSL Context
//		registerSSLContextFactory();
		
		//re initializing diameter peers
		initDiameterPeers();
		
		//re initializing diameter routes
		reloadRoutingEntries();
		
		//Reloading the stack to bring all the reloaded configuration into effect
		reload();
	}

	private void reloadRoutingEntries() {


		String configuredRoutingTable = diameterStackConfiguration.getRoutingTableName();
		setRoutingTableName(diameterStackConfiguration.getRoutingTableName());
		if(configuredRoutingTable == null || configuredRoutingTable.trim().length() == 0) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Routing Table configuration not found");
			}
		} else {
			DiameterRoutingTableConfiguration routingTableConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterRoutingConfiguration().getDiameterRoutingTableConfiguration(configuredRoutingTable);
			if(routingTableConfiguration  == null){
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Routing Table configuration not found Routing Table: " + configuredRoutingTable);
				}
			} else {
				/*
				 * Updating Routing Table Configuration Params
				 */
				setActionOnOverload(routingTableConfiguration.getOverloadAction());
				setResultCodeOnOverload(routingTableConfiguration.getResultCode());
			}
		}
		/*
		 * Updating Routing Entries after Reload
		 */
		try {
			reloadRoutingEntries(getLicensedRoutingEntryDataList());
		} catch (ElementRegistrationFailedException e) {
			LogManager.getLogger().warn(MODULE, "Error in Routing Entry Registration, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}

	}

	private List<RoutingEntryData> getLicensedRoutingEntryDataList() {


		boolean isLicenseValid = getServerContext().isLicenseValid(LicenseNameConstants.ELITEDSC_MODULE, 
				String.valueOf(System.currentTimeMillis()));

		List<RoutingEntryData> validRoutingEntryDataList = new ArrayList<RoutingEntryData>();

		String configuredRoutingTable = diameterStackConfiguration.getRoutingTableName();
		if(configuredRoutingTable == null || configuredRoutingTable.trim().length() == 0) {
			return validRoutingEntryDataList;
		}
		DiameterRoutingTableConfiguration routingTableConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterRoutingConfiguration().getDiameterRoutingTableConfiguration(configuredRoutingTable);
		if(routingTableConfiguration  == null){
			return validRoutingEntryDataList;
		}
		List<RoutingEntryDataImpl> routingEntryDatas = routingTableConfiguration.getRoutingEntryDataList();

		for (RoutingEntryData entryData : routingEntryDatas) {
			if ((entryData.getRoutingAction() == RoutingActions.LOCAL.routingAction ||
					entryData.getRoutingAction() == RoutingActions.VIRTUAL.routingAction ||
					entryData.getRoutingAction() == RoutingActions.REDIRECT.routingAction )  
					&& isLicenseValid == false) {

				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Routing Entry: "+ entryData.getRoutingName() +
							" with routing action: "+ RoutingActions.getActionString(entryData.getRoutingAction())+" will not register" +
							" Reason: License for " + LicenseNameConstants.ELITEDSC_MODULE + " is not acquired or invalid.");
				continue;
			}
			validRoutingEntryDataList.add(entryData);
		}
		return validRoutingEntryDataList;
	}

	private void reInitLogLevel() {
		if(diameterStackConfiguration.getLogger().getIsLoggerEnabled()){
			stackLogger.setLogLevel(diameterStackConfiguration.getLogger().getLogLevel());
		}
	}
	
	// To Read and Write Origin State Id
	private int readOriginStateId(){

		String serverHome = getServerContext().getServerHome() + File.separator + AAAServerConstants.SYSTEM_PATH;
		BufferedReader bufferedReader = null;
		PrintWriter detailWriter = null;
		try {
			File infoFile = new File(serverHome + File.separator + AAAServerConstants.SYS_ORIGIN_STATE_ID_FILE);

			if (infoFile.exists()) {
				bufferedReader = new BufferedReader(new FileReader(infoFile));
				String strTemp = bufferedReader.readLine();
				try{
					if (strTemp != null && strTemp.trim().length()>0) {
						count =  Integer.parseInt(strTemp);
						if(count >= Integer.MAX_VALUE){
							count = 1;
						}else{
							count++;
						}
					}
				}catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE,"Problem in reading Origin-state-id to sys file, reason: "+ e.getMessage()+", Setting up a Default value");
					LogManager.getLogger().trace(e);
				}
			}
			File systemFile = new File(serverHome);
			try {
				detailWriter = new PrintWriter(new FileWriter(new File(systemFile, AAAServerConstants.SYS_ORIGIN_STATE_ID_FILE), false)); //NOSONAR - Reason: Resources should be closed
				detailWriter.println(count);
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE,"Problem writing Origin-state-id to sys file, reason: "+ e.getMessage());
				LogManager.getLogger().trace(e);
			}
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().trace(MODULE,"Error occured while reading server info, reason: "+ e.getMessage());
				LogManager.getLogger().trace(e);
			}
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
			if (detailWriter != null)
				detailWriter.close();
		}
		return count;
	}
	
	private void registerDiameterWSRequestHandler() throws ElementRegistrationFailedException {
		EliteAAAServiceExposerManager.getInstance()
			.registerDiameterServiceInMemoryRequestHandler(
					new DiameterWebServiceRequestHandler(this, diameterStackConfiguration));
	}
	
	private void initDiameterSessionManager() {
		DiameterSessionManagerConfigurable diameterSessionManagarConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterSessionManagerConfiguration();
		if (diameterSessionManagarConfiguration == null) {
			return;
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Diameter Session Manager Configuration: \n" + diameterSessionManagarConfiguration);
		}
		DiameterSessionManager diaSessionManager = new DiameterSessionManager((AAAServerContext)getServerContext(), diameterSessionManagarConfiguration);
		try {
			diaSessionManager.init();
		} catch (InitializationFailedException e) {
			getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, MODULE, "Diameter Session Manager: " 
					+ diameterSessionManagarConfiguration.getName() + " initialization failed, session management will not occur");
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Diameter Session Manager initialization failed, Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}
		this.diameterSessionManager = diaSessionManager;
		registerDiameterSessionManager(diaSessionManager);
	}
	
	@Override
	public boolean isThresholdForLicenceTPSReached() {
		long messagePerMinute = getDiameterStatisticListner().getDiameterStatisticProvider().getMessagePerMinute()/60;
		return getServerContext().isLicenseValid(LicenseNameConstants.SYSTEM_TPS, 
				String.valueOf(messagePerMinute)) == false;
	}

	/**
	 * Mapped Stack Status w.r.t. EliteCSM Specific Status. 
	 * @return stack status message
	 */
	public final String getStatusMessage() {
		
		switch (getStackStatus()) {
		case NOT_INITIALIZE:
		case INITIALIZING:
			return LifeCycleState.NOT_STARTED.message;
		case INITIALIZED:
			return LifeCycleState.STARTUP_IN_PROGRESS.message;
		case RUNNING:
			if(getServerContext().isServerStartedWithLastConf()){
				return LifeCycleState.RUNNING_WITH_LAST_CONF.message;
			}
			return LifeCycleState.RUNNING.message;
		case STOPPING:
			return LifeCycleState.SHUTDOWN_IN_PROGRESS.message;
		case STOPPED:
		default:
			return LifeCycleState.STOPPED.message;
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public final void cleanupResources() {
		Closeables.closeQuietly(stackLogger);
	}

	public @Nullable DiameterSessionManager getDiameterSessionManager() {
		return this.diameterSessionManager;
	}


	@Override
	protected void addInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
		super.addInfoAVPs(diameterPacket, connectionHandler);
		addEAPPayLoadAVP(diameterPacket);
	}

	private void addEAPPayLoadAVP(DiameterPacket diameterPacket) {
		IDiameterAVP eapPayloadAVP = diameterPacket.getAVP(DiameterAVPConstants.EAP_PAYLOAD);
		if(eapPayloadAVP != null){
			try {
				EAPPacket eapPacket = new EAPPacket(eapPayloadAVP.getValueBytes());
				IDiameterAVP ecEAPCodeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ELITE_EAP_CODE);
				if(ecEAPCodeAVP != null){
					ecEAPCodeAVP.setStringValue(String.valueOf(eapPacket.getCode()));
					diameterPacket.addInfoAvp(ecEAPCodeAVP);
				}

				IDiameterAVP ecEAPMethodAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ELITE_EAP_METHOD);
				if(ecEAPMethodAVP != null){
					ecEAPMethodAVP.setStringValue(String.valueOf(eapPacket.getEAPType().getType()));
					diameterPacket.addInfoAvp(ecEAPMethodAVP);
				}
			} catch (InvalidEAPPacketException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid EAP Payload AVP, so ELITE-EAP-CODE and ELITE-EAP-METHOD will not be added to Diameter Request.");
				ignoreTrace(e);
			}
		}
	}

	
//	@Override
//	
//	public boolean isServerInitiatedMessage(int commandCode) {
//		return false;
//	}
	
	@Override
	protected DiameterPluginManager createDiameterPluginManager() {
		return new DiameterPluginManager(aaaDiameterPluginManager.getNameToPluginMap());
	}

	public AAAServerContext getServerContext() {
		return serverContext;
	}
	
	public class DiameterServiceConfigurationSetter implements ConfigurationSetter {
		private static final String REALTIME = "realtime";

		@Override
		public String execute(String... parameters) {
			if (parameters[2].equalsIgnoreCase("log")) {
				if (parameters.length >= 4){
					if (diameterStackConfiguration.getLogger().getIsLoggerEnabled()) {
						EliteRollingFileLogger logger = (EliteRollingFileLogger) stackLogger;
						if (logger.isValidLogLevel(parameters[3]) == false) {
							return "Invalid log level: " + parameters[3];
						}
						logger.setLogLevel(parameters[3]);
						return "Configuration Changed Successfully";

					} else {
						return "Error : Diameter service log are disabled";
					}
				}			

			}
			return getHelpMsg();
		}

		@Override
		public boolean isEligible(String... parameters) {
			if (parameters.length == 0){
				return false;
			}
			if (!"service".equalsIgnoreCase(parameters[0])) {
				return false;
			}
			if (!"diameter".equalsIgnoreCase(parameters[1])) {
				return false;
			}
			if ("log".equalsIgnoreCase(parameters[2])) {
				return true;
			} else if ("-help".equalsIgnoreCase(parameters[2])) {
				return true;
			}
			return false;
		}

		@Override
		public String getHelpMsg() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : set service diameter [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the Diameter Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'diameter':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}

	}
	
}
