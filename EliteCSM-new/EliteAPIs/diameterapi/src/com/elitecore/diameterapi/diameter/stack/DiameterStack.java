/**
 * 
 */
package com.elitecore.diameterapi.diameter.stack;


import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicatorFactory;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException.ListenerRegFailResultCode;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.transport.stats.DiameterNetworkStatisticsProvider;
import com.elitecore.diameterapi.core.common.transport.stats.ThreadPoolDetails;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.core.common.transport.tcp.collections.FairnessPolicy;
import com.elitecore.diameterapi.core.common.transport.tcp.collections.WeightedFairBlockingQueue;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.drivers.DiameterCDRDriverFactory;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUserEquipmentInfoValue;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeersTable;
import com.elitecore.diameterapi.diameter.common.peers.DiameterVirtualPeer;
import com.elitecore.diameterapi.diameter.common.peers.PeerProvider;
import com.elitecore.diameterapi.diameter.common.peers.PeerProviderImpl;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.session.ApplicationListener;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.transport.ConnectionFactoryImpl;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DisconnectionCause;
import com.elitecore.diameterapi.diameter.common.util.constant.PacketValidationCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.MIBIndexRecorder;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;
import org.apache.logging.log4j.ThreadContext;

/**
 * @author pulindani
 * 
 */
public class DiameterStack extends Stack{

	public static final String H2H = "H2H";
	public static final String E2E = "E2E";
	public static final String CC = "CC";
	public static final String SID = "SID";
	public static final String APP_ID = "AppId";
	private final String MODULE = "DIAMETER-STACK";
	private final SessionFactoryManager sessionFactoryManager;

	private List<RoutingEntryData> routingEntryDataList;
	private List<PeerData> peerDataList;
	private List<ApplicationListener> applicationListeners;
	private DiameterStackContext stackContext;
	private DiameterAppMessageHandler diameterAppMessageHandler;

	private String ownDiameterIdentity = "diameter.elitecore.com";
	private boolean isNAIEnabled ;
	public boolean isRealmVerificationRequired;
	public List<String> naiRealmNames;
	private String ownDiameterURI = "aaa://localhost:3868";
	private String ownDiameterRealm = "elitecore.com";
	private String serverInstanceId;
	
	private String routingTableName = "";
	private Status currentStackStatus = Status.NOT_INITIALIZE;
	private DiameterStatisticListener diameterStatisticListener;
	private DiameterPeersTable diameterPeersTable;
	private PeerProvider peerProvider;
	private DiameterRouter diameterRouter;
	private AtomicInteger sessHigherBit;
	private AtomicInteger sessLowerBit;
	private boolean rfcValidation = true;
	private EliteSSLContextFactory eliteSSLContextFactory;
	
	// This Set maintains application identifiers that we support locally as well as configured in realms 
	private Set<ApplicationEnum> supportedApplicationIdentifiers = new HashSet<ApplicationEnum>();
	

	private ExplicitRoutingHandler explicitRoutingHandler;
	//used for session clean up
	private @Nullable ScheduledExecutorService scheduledExecutorService;
	private OverloadAction actionOnOverload;
	private int resultCodeOnOverload;
	private MIBIndexRecorder mibIndexRecorder;
	private @Nullable IDiameterSessionManager diameterSessionManager;

	
	/*
	 * Thread model
	 */
	private static final int BASE_MESSAGE_QUEUE_SIZE = 100;
	private static final int BASE_MESSAGE_THREADS = 2;

    public final static long DEFAULT_THREAD_KEEP_ALIVE_TIME = 1000 * 60 * 60;
	private ThreadPoolExecutor subProcessThreadExecutor;
    private BlockingQueue <Runnable> subProcessThreadTaskQueue;
    
    private ThreadPoolExecutor baseThreadExecutor;
    private BlockingQueue <Runnable> baseThreadTaskQueue;
    private DiameterNetworkStatisticsProvider statisticsProvider;
    private int workerThreadPriority = 6;
    private int mainThreadPriority = 8;
    private int maxThreadPoolSize = 10;
    private int minThreadPoolSize = 5;
    private long threadKeepAliveTime = DEFAULT_THREAD_KEEP_ALIVE_TIME;
    private int maxRequestQueueSize = 1500;
    @Nullable private FairnessPolicy<Runnable> fairnessPolicy;

    
    private DiameterPluginManager diameterPluginManager;
    private long sessionCleanupInterval = 0;
    private long sessionTimeOut = 0;

    private DuplicateDetectionHandler duplicateMessageHandler;

    private DiameterCDRDriverFactory diameterCDRDriverFactory;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private TaskScheduler taskScheduler;

	private DiameterPeerCommunicatorFactory diameterPeerCommunicatorFactory;



	/**
	 * 
	 */
	public DiameterStack(){

		/*
		 * Please DO NOT change below object creation sequence unless you are fully aware of order significance
		 */
		
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(15, new EliteThreadFactory(CommonConstants.DIAMETER_STACK_IDENTIFIER ,CommonConstants.DIAMETER_STACK_IDENTIFIER + "-SCH", Thread.NORM_PRIORITY));
		taskScheduler = new TaskSchedulerImpl();
		
		stackContext = new DiameterStackContext();
		this.diameterPeersTable = new DiameterPeersTable();
		peerProvider = new PeerProviderImpl(diameterPeersTable);
		
		diameterAppMessageHandler = new DiameterAppMessageHandler(stackContext);
		/*
		 * Please DO NOT change below object creation sequence unless you are fully aware of order significance
		 */



		mibIndexRecorder = new MIBIndexRecorder();
		this.diameterStatisticListener = new DiameterStatisticListener(mibIndexRecorder, diameterPeersTable, supportedApplicationIdentifiers,new TaskScheduler() {
			
			@Override
			public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
				return DiameterStack.this.scheduleSingleExecutionTask(task);
			}
			
			@Override
			public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
				return DiameterStack.this.scheduleIntervalBasedTask(task);
			}

			@Override
			public void execute(Runnable command) {
				DiameterStack.this.scheduleSingleExecutionTask(command);
			}
		});
		

		routingEntryDataList = new ArrayList<RoutingEntryData>();
		peerDataList = new ArrayList<PeerData>();
		applicationListeners = new ArrayList<ApplicationListener>();
		diameterRouter = new DiameterRouter(stackContext, routingEntryDataList);
		this.naiRealmNames = new ArrayList<String>();
		if(System.getProperty("diameter.rfc.validation") != null) {
			rfcValidation = Boolean.parseBoolean(System.getProperty("diameter.rfc.validation"));
		}
		sessHigherBit = new AtomicInteger((int) (System.currentTimeMillis() & Integer.MAX_VALUE));
		sessLowerBit = new AtomicInteger(0);
		explicitRoutingHandler = new ExplicitRoutingHandler();
		duplicateMessageHandler = new DuplicateDetectionHandler(stackContext);
		this.actionOnOverload = OverloadAction.REJECT;
		this.resultCodeOnOverload = ResultCode.DIAMETER_TOO_BUSY.code;
		diameterPeerCommunicatorFactory = new DiameterPeerCommunicatorFactory(stackContext, peerProvider);
		
		sessionFactoryManager = new SessionFactoryManagerImpl(stackContext);
	}
	
	protected DiameterPluginManager createDiameterPluginManager() {
		return new DiameterPluginManager();
	}

	public void registerEliteSSLContextFactory(EliteSSLContextFactory eliteSSLContextFactory){
		this.eliteSSLContextFactory = eliteSSLContextFactory;
	}


	public void registerRoutingEntry(RoutingEntryData routingEntryData) throws ElementRegistrationFailedException{
		if(routingEntryData != null) {
			routingEntryDataList.add(routingEntryData);
		}else {
			throw new ElementRegistrationFailedException("Empty Routing Entry Data found");
		}
	}
	
	public void registerPriorityRoutingEntry(RoutingEntryData routingEntryData) throws ElementRegistrationFailedException{
		if(routingEntryData != null) {
			diameterRouter.registerPriorityRoutingEntry(routingEntryData);
		}else {
			throw new ElementRegistrationFailedException("Empty Routing Entry Data found");
		}
	}
	
	public void reloadRoutingEntries(List<RoutingEntryData> routingEntryDataList) throws ElementRegistrationFailedException{
		this.routingEntryDataList = routingEntryDataList;
	}
	
	public void registerRoutingEntries(List<RoutingEntryData> routingEntryDatas) throws ElementRegistrationFailedException{
		if(routingEntryDatas != null) {
			for(RoutingEntryData realmData : routingEntryDatas){ 
				this.registerRoutingEntry(realmData);
			}
		}
	}
	
	public VirtualConnectionHandler registerVirtualPeer(PeerData peerData,  VirtualOutputStream outpurStream) throws ElementRegistrationFailedException{
		try {
			VirtualConnectionHandler virtualConnectionHandler = new VirtualConnectionHandler(this, peerData, outpurStream);
			DiameterVirtualPeer diameterVirtualPeer = new DiameterVirtualPeer(peerData, virtualConnectionHandler, stackContext, 
					diameterRouter, sessionFactoryManager, diameterAppMessageHandler, 
					explicitRoutingHandler, duplicateMessageHandler);
			diameterVirtualPeer.init();
			diameterPeersTable.addPeer(diameterVirtualPeer);
			diameterStatisticListener.addDiameterPeer(diameterVirtualPeer);
			return virtualConnectionHandler;
		} catch (InitializationFailedException ex) {
			throw new ElementRegistrationFailedException(ex);
		}
	}
	
	public void registerInOutPlugins(List<PluginEntryDetail> inPlugins, List<PluginEntryDetail> outPlugins){
		this.diameterPluginManager.registerInPlugins(inPlugins);
		this.diameterPluginManager.registerOutPlugins(outPlugins);
	}

	public void registerApplicationListener(Collection<ApplicationListener> applicationListeners) throws ElementRegistrationFailedException {
		for (ApplicationListener applicationListener : applicationListeners) {
			registerApplicationListener(applicationListener);
		}
	}
	
	public void registerApplicationListener(ApplicationListener applicationListener) throws ElementRegistrationFailedException {
		if (applicationListener == null) {
			throw new ElementRegistrationFailedException("application listener is null");
		}
		
		LogManager.getLogger().info(MODULE, "Registering Application Listener for Application(s): " + 
				Arrays.asList(applicationListener.getApplicationEnum()));
		
		this.supportedApplicationIdentifiers.addAll(Arrays.asList(applicationListener.getApplicationEnum()));
		this.applicationListeners.add(applicationListener);
	}

	public void registerPeers(List<PeerData> peersDataList) throws ElementRegistrationFailedException{
		
		if(peersDataList == null) {
			throw new ElementRegistrationFailedException("Provided Peers Data List is null"); 
		}
		List<PeerData> peerList = null;
		synchronized (this.peerDataList) {			
			peerList = new ArrayList<PeerData>(this.peerDataList);
		}
		
		for(PeerData peerData : peersDataList) {
			
			if(peerData.getPeerName() == null) {
				LogManager.getLogger().error(MODULE, "Error while registrating peer " + peerData.getHostIdentity()
				+ "(" + peerData.getRemoteIPAddress() + "). Reason: PeerName is null");
				continue;
			}
			
			/*
			 * Search peer on peer name from previous peer list. 
			 * It may possible that more than one peer found with same name as stack supports IP-range in peer data.
			 * 
			 * If peer found then stack reload configuration of previous peer data.
			 *  
			 * Create peer data only when previous peer data not found.
			 */
			boolean alreadyRegistered = false;
			for(PeerData oldPeerData : peerList) {
				if(oldPeerData.getPeerName().equals(peerData.getPeerName())) {
					oldPeerData.reload(peerData);
					alreadyRegistered = true;
				}
			}

			
			if(alreadyRegistered) {
				continue;
			}
			
			String remoteIPAddress = peerData.getRemoteIPAddress();
			if(DiameterUtility.isIPRange(remoteIPAddress)) {
				
				if(peerData.getHostIdentity() != null && !peerData.getHostIdentity().trim().isEmpty()) {
					LogManager.getLogger().error(MODULE, "Error while adding available addresses from range " + remoteIPAddress + ". Reason: HostIdentity not allowed for IPRange/Netmask");
					continue;
				}
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Producing available addresses from range " + remoteIPAddress + " for Peer " + peerData.getPeerName());
				
				List<InetAddress> inetAddresses = getAvailableInetAddresses(remoteIPAddress);
				if(inetAddresses == null || inetAddresses.isEmpty()) {
					LogManager.getLogger().warn(MODULE, "No available address found from range " + remoteIPAddress + " for Peer " + peerData.getPeerName());
					continue;
				}
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Total available addresses found = " + inetAddresses.size() + " for Peer " + peerData.getPeerName());
				
				for(InetAddress inetAddress : inetAddresses) {
					try {
						PeerData data = (PeerData) peerData.clone();
						data.setPeerIndex(-1);
						data.setPeerName(peerData.getPeerName());
						data.setRemoteInetAddress(inetAddress);
						data.setRemoteIPAddress(inetAddress.getHostAddress());
						peerList.add(data);
					} catch (CloneNotSupportedException e) {
						LogManager.getLogger().trace(MODULE, e);
					}
				}
			} else {
				peerList.add(peerData);
			}
		}
		this.peerDataList = peerList;
	}

	public synchronized void init() {
		initDiameterPeers();
	}
	
	/**
	 * 
	 */
	public synchronized boolean start() {

		/*
		 * Please DO NOT change below object initialization sequence unless you are fully aware of order significance
		 */
		
		if(currentStackStatus == Status.NOT_INITIALIZE) {
			currentStackStatus = Status.INITIALIZING;
			Parameter.getInstance().setHostIPAddress(getNetworkAddress());
			Parameter.getInstance().setHostListeningPort(getNetworkPort());
			Parameter.getInstance().setOwnDiameterIdentity(getDiameterStackIdentity());
			Parameter.getInstance().setOwnDiameterRealm(getDiameterStackRealm());
			Parameter.getInstance().setOwnDiameterURI(getDiameterStackURI());
			Parameter.getInstance().setRoutingTableName(getRoutingTable());
			Parameter.getInstance().setStackUpTimeStamp();
			
			if (super.start(new ConnectionFactoryImpl(peerProvider)) == false) {
				currentStackStatus = Status.STOPPED;
				return false;
			}
			
			
			startWorkerThreads();
			
			
			//FIXME -monica.lulla Check for some other approach.
			duplicateMessageHandler.init();
			
			initApplicationListener();

			diameterRouter.init();
			
			startDiameterPeers();
			
			supportedApplicationIdentifiers.addAll(diameterRouter.getSupportedRemoteApplications());
			this.diameterStatisticListener.init();
			
			currentStackStatus = Status.INITIALIZED;
			// For Cleaning up Diameter Sessions.
			if(getSessionInterval() > 0){
				scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new EliteThreadFactory(CommonConstants.DIAMETER_STACK_IDENTIFIER, CommonConstants.DIAMETER_STACK_IDENTIFIER + "-SESS-CLEANUP-TH", Thread.MAX_PRIORITY));
				DiameterSessionCleanupTask diameterSessionCleanupTask = new DiameterSessionCleanupTask(getSessionTimeOut() * 1000);
				scheduledExecutorService.scheduleWithFixedDelay(diameterSessionCleanupTask, getSessionInterval(), getSessionInterval(), TimeUnit.SECONDS);
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Diameter Session Cleanup is disabled. Configured value: " + getSessionInterval());
			}

			this.diameterPluginManager = createDiameterPluginManager();
			
			currentStackStatus = Status.RUNNING;
			LogManager.getLogger().info(MODULE, "Diameter Stack started successfully.");
			Stack.generateAlert(StackAlertSeverity.INFO, DiameterStackAlerts.DIAMETER_STACK_UP, MODULE, "Diameter Stack UP");
			
		}else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Stack already Started");
		}

		return true;
		
	}
	

	private void startWorkerThreads() {
		
		if (getFairnessPolicy() == null) {
			subProcessThreadTaskQueue = new LinkedBlockingQueue<Runnable>(getMaxRequestQueueSize());
		} else {
			subProcessThreadTaskQueue = new WeightedFairBlockingQueue<Runnable>(getMaxRequestQueueSize(), getFairnessPolicy());
		}
		
		baseThreadTaskQueue = new LinkedBlockingQueue<Runnable>(BASE_MESSAGE_QUEUE_SIZE);
		
		RejectedExecutionHandler rejectedExecutionHandler = getRejectedExecutionHandler();
		if(rejectedExecutionHandler == null){
			baseThreadExecutor = new ThreadPoolExecutor(BASE_MESSAGE_THREADS, BASE_MESSAGE_THREADS, 
														getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, baseThreadTaskQueue); 
			subProcessThreadExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, subProcessThreadTaskQueue);
		}else {

			baseThreadExecutor = new  ThreadPoolExecutor(BASE_MESSAGE_THREADS, BASE_MESSAGE_THREADS, 
														getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, 
														baseThreadTaskQueue, rejectedExecutionHandler);
			subProcessThreadExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, subProcessThreadTaskQueue, rejectedExecutionHandler);
		}
		baseThreadExecutor.setThreadFactory(new EliteThreadFactory(com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER,getThreadIdentifier()+"-BASE-POOL-THR",getMainThreadPriority()));
		baseThreadExecutor.prestartAllCoreThreads();
		
        subProcessThreadExecutor.setThreadFactory(new EliteThreadFactory(com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER,getThreadIdentifier()+"-POOL-THR",getWorkerThreadPriority()));
        subProcessThreadExecutor.prestartAllCoreThreads();
		
		
		this.statisticsProvider = new DiameterNetworkStatisticsProvider() {

			@Override
			public ThreadPoolDetails getMessageThreadPoolDetails() {
				return new ThreadPoolDetails() {

					@Override
					public int getQueueSize() {
						return subProcessThreadTaskQueue.size();
					}

					@Override
					public int getPoolSize() {
						return subProcessThreadExecutor.getPoolSize();
					}

					@Override
					public int getMinSize() {
						return subProcessThreadExecutor.getCorePoolSize();
					}

					@Override
					public int getMaxSize() {
						return subProcessThreadExecutor.getMaximumPoolSize();
					}

					@Override
					public int getPeakPoolSize() {
						return subProcessThreadExecutor.getLargestPoolSize();
					}

					@Override
					public int getActiveCount() {
						return subProcessThreadExecutor.getActiveCount();
					}
				};
			}

			@Override
			public ThreadPoolDetails getBaseThreadPoolDetails() {
				return new ThreadPoolDetails() {

					@Override
					public int getQueueSize() {
						return baseThreadTaskQueue.size();
					}

					@Override
					public int getPoolSize() {
						return baseThreadExecutor.getPoolSize();
					}

					@Override
					public int getMinSize() {
						return baseThreadExecutor.getCorePoolSize();
					}

					@Override
					public int getMaxSize() {
						return baseThreadExecutor.getMaximumPoolSize();
					}

					@Override
					public int getPeakPoolSize() {
						return baseThreadExecutor.getLargestPoolSize();
					}

					@Override
					public int getActiveCount() {
						return baseThreadExecutor.getActiveCount();
					}
				};
			}
		};
	}

	public int getWorkerThreadPriority() {
		return this.workerThreadPriority;
	}
	
	public void setWorkerThreadPriority(int workerThreadPriority) {
		this.workerThreadPriority = workerThreadPriority;
	}

	public int getMainThreadPriority() {
		return mainThreadPriority;
	}
	
	public void setMainThreadPriority(int mainThreadPriority) {
		this.mainThreadPriority = mainThreadPriority;
	}

	private String getThreadIdentifier() {
		return CommonConstants.DIAMETER_STACK_IDENTIFIER;
	}

	public int getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}
	
	public void setMaxThreadPoolSize(int maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	public int getMinThreadPoolSize() {
		return minThreadPoolSize;
	}
	
	public void setMinThreadPoolSize(int minThreadPoolSize) {
		this.minThreadPoolSize = minThreadPoolSize;
	}

	public long getThreadKeepAliveTime() {
		return threadKeepAliveTime;
	}
	
	public void setThreadKeepAliveTime(long threadKeepAliveTime) {
		this.threadKeepAliveTime = threadKeepAliveTime;
	}

	protected @Nullable RejectedExecutionHandler getRejectedExecutionHandler() {
		return new RejectionHandler();
	}

	public void addMDC(DiameterPacket packet) {
		ThreadContext.put(H2H, String.valueOf(packet.getHop_by_hopIdentifier()));
		ThreadContext.put(E2E, String.valueOf(packet.getEnd_to_endIdentifier()));
		ThreadContext.put(CC, String.valueOf(packet.getCommandCode()));
		String sessionID = packet.getSessionID();
		if (sessionID != null) {
			ThreadContext.put(SID, sessionID);
		}
		ThreadContext.put(APP_ID, String.valueOf(packet.getApplicationID()));
	}

	public void clearMDC() {
		ThreadContext.clearAll();
	}

	private class RejectionHandler implements RejectedExecutionHandler {
		
		private AppDefaultSessionReleaseIndicator appDefaultSessionReleaseIndicator = new AppDefaultSessionReleaseIndicator();

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				
			PacketProcess packetProcess = null;
			try{
				packetProcess = (PacketProcess) r;
			}catch(Exception ex){
				LogManager.getLogger().trace(MODULE, ex);
				return;
			}
				
			
			DiameterPacket diameterPacket = (DiameterPacket) packetProcess.getPacket();
			
			NetworkConnectionHandler connectionHandler = packetProcess.getConnectionHandler();
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Fetching Peer from Source-IP-Address: " + connectionHandler.getSourceIpAddress());
			PeerData peerData = getStackContext().getPeerData(connectionHandler.getSourceIpAddress());
			
			String hostIdentity = null;
			if(peerData != null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Peer:"+peerData.getPeerName() +" found from Source-IP-Address: "+connectionHandler.getSourceIpAddress());
				hostIdentity = peerData.getHostIdentity();
			} else {
				LogManager.getLogger().warn(MODULE, "Use Origin-Host as host-Identity for updating statistics. Reason:Peer not found from Source-IP-Address:" 
				+connectionHandler.getSourceIpAddress() + ", Package-Type:" + diameterPacket.getCommandCode());
				hostIdentity = connectionHandler.getHostName();
			}
			
			
			getStackContext().updateInputStatistics(diameterPacket, hostIdentity);
			
			
			if(diameterPacket.isResponse()){
				if(diameterPacket.getApplicationID() == ApplicationIdentifier.BASE.getApplicationId()){
					LogManager.getLogger().warn(MODULE, "Dropping rejected reponse," 
					+ " Packet-Type:" + diameterPacket.getCommandCode());
				} else {
					LogManager.getLogger().warn(MODULE, "Dropping rejected reponse, Session-ID = "
					+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) 
					+ " Packet-Type:" + diameterPacket.getCommandCode());
				}
				
				
				getStackContext().updateDiameterStatsPacketDroppedStatistics(diameterPacket, hostIdentity);
				
				return;
			}
				
			int resultCodeOnOverload = getStackContext().getOverloadResultCode();
			
			
			
			if(getStackContext().getActionOnOverload() == OverloadAction.DROP){
				LogManager.getLogger().warn(MODULE, "Dropping request," 
				+ " Package-Type:" + diameterPacket.getCommandCode() +". Reason: Overload action is Drop");
				getStackContext().updateDiameterStatsPacketDroppedStatistics(diameterPacket, hostIdentity);
				return;
			}
				
			
			
			try {
				IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
				if(diameterPacket.getApplicationID() == ApplicationIdentifier.BASE.getApplicationId()){
					
					if(diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.code){
						LogManager.getLogger().info(MODULE, "Sending Diameter-Success for received DWR request");
						resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
						
					} else {
						LogManager.getLogger().warn(MODULE, "Sending "+resultCodeOnOverload +" for rejected request,"
						+ " Package-Type:" + diameterPacket.getCommandCode());
					}
				
					
				} else {
					LogManager.getLogger().warn(MODULE, "Sending "+resultCodeOnOverload +" for rejected request, Session-ID = "
					+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) 
					+ " Package-Type:" + diameterPacket.getCommandCode());
					resultCode.setInteger(resultCodeOnOverload);
				}
				
				DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket);
				diameterAnswer.addAvp(resultCode);
				
				if (resultCode.getInteger() != ResultCode.DIAMETER_SUCCESS.code) {
					DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
							diameterAnswer, DiameterErrorMessageConstants.SYSTEM_OVERLOAD);
				}
				
				if(appDefaultSessionReleaseIndicator.isEligible(diameterAnswer)){
					if (getStackContext().hasSession(diameterAnswer.getSessionID(), diameterAnswer.getApplicationID())) {
						getStackContext()
							.getOrCreateSession(diameterPacket.getSessionID(), diameterPacket.getApplicationID())
							.release();
					}
				}
				
				connectionHandler.send(diameterAnswer);
				
				getStackContext().updateOutputStatistics(diameterAnswer, hostIdentity);
				
			}catch(Exception exp){
		        	LogManager.getLogger().error(MODULE, exp.getMessage());
		        	LogManager.getLogger().trace(MODULE, exp);
		    }
		}
				
	}
	

	public int getMaxRequestQueueSize() {
		return maxRequestQueueSize;
	}
	
	public void setMaxRequestQueueSize(int maxRequestQueueSize) {
		this.maxRequestQueueSize = maxRequestQueueSize;
	}

	public FairnessPolicy<Runnable> getFairnessPolicy() {
		return fairnessPolicy;
	}
	
	public void setFairnessPolicy(FairnessPolicy<Runnable> fairnessPolicy) {
		this.fairnessPolicy = fairnessPolicy;
	}

	public void reload(){
		if(currentStackStatus != Status.STOPPED && currentStackStatus != Status.STOPPING){
			Parameter.getInstance().setRoutingTableName(getRoutingTable());
			reloadDiameterPeers();
			diameterStatisticListener.reload(diameterPeersTable.getPeerList());
			diameterRouter.reInit(routingEntryDataList);
		}
	}

	private void reloadDiameterPeers() {
		
		 
		
		for(PeerData peerData : this.peerDataList) {
			DiameterPeer peer = null; 
			
			/*
			 * In Stack, Host-Identity of peer is unique.
			 * 
			 * so try to find peer from host-identity and then from remote Inet-address.
			 * 
			 * As either host-identity or remote address is required in peer, If peer is not found then we considered as new peer
			 * 
			 */
			if (Strings.isNullOrBlank(peerData.getHostIdentity()) == false) {
				peer = diameterPeersTable.getPeer(peerData.getHostIdentity());
			}
			
			if (peer == null && peerData.getRemoteInetAddress() != null) {
				peer = diameterPeersTable.getPeer(peerData.getRemoteInetAddress().getHostAddress());
			}
			
			if (peer == null) {
				try {
					DiameterPeer diameterPeer = new DiameterPeer(peerData, stackContext, diameterRouter, 
							sessionFactoryManager, diameterAppMessageHandler, explicitRoutingHandler, 
							duplicateMessageHandler);
					diameterPeer.init();
					diameterPeersTable.addPeer(diameterPeer);
				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, "Error while creating DiameterPeer from PeerData: "+ peerData.getPeerName() + ". Reason:" + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			} else {
				peer.reloadDiameterPeer();
			}
			
		}
	}

	private void initApplicationListener() {
		if(this.applicationListeners == null || applicationListeners.isEmpty()){
			LogManager.getLogger().error(MODULE, "Fail to initilized application listers. reason: applicationListerner list is empty");
			return;
		}

		for(int index = 0; index < applicationListeners.size(); index++) {
			try {
				applicationListeners.get(index).init();
				diameterAppMessageHandler.addApplicationListener(applicationListeners.get(index));
			} catch (AppListenerInitializationFaildException e) {
				LogManager.getLogger().error(MODULE, "Application(" + Arrays.toString(applicationListeners.get(index).getApplicationEnum()) + ") initialization failed, reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}		
		}
	}				  

	private PacketValidationCode validatePacket(DiameterPacket packet){

		if(DiameterUtility.isBaseProtocolPacket(packet.getCommandCode()) == false){
			
			IDiameterAVP sessionIDAvp = packet.getAVP(DiameterAVPConstants.SESSION_ID);
			if(sessionIDAvp == null || Strings.isNullOrBlank(sessionIDAvp.getStringValue())){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().error(MODULE, "Invalid Diameter Packet Received with E2E: "+packet.getEnd_to_endIdentifier()+", Reason: Session-Id Avp not found or Contain blank value");
				return PacketValidationCode.SESSION_ID_MISSING;
			}
		}
		
		if (rfcValidation == false) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Diameter Rfc Validation is disabled");
			}
			return PacketValidationCode.VALID_PACKET;
		}
		
		// basic validations
		if (packet.getAVP(DiameterAVPConstants.ORIGIN_HOST) == null){
			return PacketValidationCode.ORIGIN_HOST_MISSING;
	}
		if (packet.getAVP(DiameterAVPConstants.ORIGIN_REALM) == null) {
			return PacketValidationCode.ORIGIN_REALM_MISSING;
		}
		
		if(DiameterUtility.isBaseProtocolPacket(packet.getCommandCode()) != 
				(packet.getApplicationID() == ApplicationIdentifier.BASE.applicationId)) {
			return PacketValidationCode.INVALID_BASE_COMMAND_CODE;
		}
		
		return PacketValidationCode.VALID_PACKET;
	}

	private void handleReceivedInvalidMessage(DiameterPacket packet, NetworkConnectionHandler connectionHandler, PacketValidationCode invalidityReason) 
			throws IOException {
		this.stackContext.updateDiameterStatsMalformedPacket(packet, connectionHandler.getHostName());
		
		if (packet.isResponse()){
			LogManager.getLogger().error(MODULE, "Discarding invalid Diameter Answer. Reason: " + invalidityReason);
			return;
		}

		DiameterAnswer answerpacket = new DiameterAnswer((DiameterRequest) packet);
		
		IDiameterAVP resultCode = null;
		LogManager.getLogger().warn(MODULE, "Invalid Diameter request received. Reason: " + invalidityReason);
		resultCode = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCode.setInteger(invalidityReason.resultCode);
		answerpacket.addAvp(resultCode);
		
		switch (invalidityReason) {
		case ORIGIN_HOST_MISSING:
			DiameterUtility.addFailedAVP(answerpacket, 
					DiameterDictionary.getInstance()
						.getAttribute(DiameterAVPConstants.ORIGIN_HOST));
			break;
		case ORIGIN_REALM_MISSING:
			DiameterUtility.addFailedAVP(answerpacket, 
					DiameterDictionary.getInstance()
						.getAttribute(DiameterAVPConstants.ORIGIN_REALM));
			break;
		case SESSION_ID_MISSING:
			DiameterUtility.addFailedAVP(answerpacket, 
					DiameterDictionary.getInstance()
						.getAttribute(DiameterAVPConstants.SESSION_ID));
			break;
		case INVALID_BASE_COMMAND_CODE:
			
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Command-Code: " + packet.getCommandCode() + 
						" is not comaptible with Base Application-ID: " + packet.getApplicationID());
			}
			break;

		default:
			// This should never happen
			LogManager.getLogger().error(MODULE, "Failed to interpret Invalidity reason " + invalidityReason + " of Diameter Request. Dropping request.");
			return;
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, answerpacket.toString());
		}
		connectionHandler.send(answerpacket);

	}
	
	public void handleReceivedMessage(Packet packet, NetworkConnectionHandler connectionHandler) {
		try {
			DiameterPacket diameterPacket = (DiameterPacket)packet;
			if(diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()){
				if(diameterPacket.isRequest()){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Received DWR from host name: " + connectionHandler.getHostName() + " source IP: "
								+ connectionHandler.getSourceIpAddress() + " origin host: " + diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Received DWA from host name: " + connectionHandler.getHostName() + " source IP: "
								+ connectionHandler.getSourceIpAddress() + " origin host: " + diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Received Packet detail: " + diameterPacket.toString());
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Received Packet from host name: " + connectionHandler.getHostName() + " source IP: "
							+ connectionHandler.getSourceIpAddress() + " session-Id: " + diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
					LogManager.getLogger().info(MODULE, "Received Packet detail: " + diameterPacket.toString());

				}	
			}

			IPeerListener peerListener =  null;
			// When first CER will be received from any peer, its origin host AVP value will be used as its host name 
			// in further communication with that peer. 
			if (diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.code && (connectionHandler.getHostName() == null || connectionHandler.getHostName().trim().length() == 0)) {  
				connectionHandler.setHostName(diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST)); 
			} 
			peerListener =  this.diameterPeersTable.getPeer(connectionHandler.getHostName()); 
			
			if (peerListener == null){
				peerListener = this.diameterPeersTable.getPeer(connectionHandler.getSourceIpAddress());
				if (peerListener != null){
					connectionHandler.setHostName(diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
				}
			}
			
			if(peerListener != null){
				if(diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.code){
					if(peerListener.getRemoteInetAddress() != null){
						
						if(isLegitPeer(connectionHandler, peerListener) == false){
							if (diameterPacket.isRequest()) {
								LogManager.getLogger().error(MODULE, "Recieved Connection Request from Unknown Peer: " + connectionHandler.getSourceIpAddress() + " for HostIdentity : "+peerListener.getHostIdentity() + ". Sending DIAMETER_UNKNOWN_PEER");
								DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNKNOWN_PEER);
								IDiameterAVP diameterAVP;
								diameterAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DISCONNECT_CAUSE);
								if(diameterAVP!=null){			
									diameterAVP.setInteger(DisconnectionCause.DO_NOT_WANT_TO_TALK_TO_YOU.code);
									answerPacket.addAvp(diameterAVP);
								}
								try {
									if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
										LogManager.getLogger().info(MODULE, "Sending Packet: " + answerPacket.toString());
									connectionHandler.send(answerPacket);
								} catch (IOException e) { 
									LogManager.getLogger().error(MODULE, "Error while sending answer, reason: " + e.getMessage());
									ignoreTrace(e);
								}
							} else{
								LogManager.getLogger().error(MODULE, "Recieved Answer from Unknown Peer: " + connectionHandler.getSourceIpAddress() +
								" with HbH-ID: "+ diameterPacket.getHop_by_hopIdentifier() + ". Dropping Answer");
								sendDPRtoUnknownPeer(connectionHandler, DisconnectionCause.DO_NOT_WANT_TO_TALK_TO_YOU);
							}
							LogManager.getLogger().warn(MODULE, "Closing Connection for IP Address " +
							connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
							connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
							return;
						}

					}else{
						peerListener.setRemoteInetAddress(InetAddress.getByName(connectionHandler.getSourceIpAddress()));
						peerListener.setRemotePort(connectionHandler.getSourcePort());
					}
				}
				if(peerListener.getHostIdentity() == null || peerListener.getHostIdentity().trim().isEmpty()) {
					peerListener.setHostIdentity(diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
					diameterPeersTable.addPeer((DiameterPeer) peerListener);
					diameterStatisticListener.addDiameterPeer((DiameterPeer) peerListener);
				}

				PacketValidationCode validationCode = validatePacket(diameterPacket);
				if (validationCode == PacketValidationCode.VALID_PACKET){
					
					addInfoAVPs(diameterPacket,connectionHandler);
					SessionsFactory diameterSessionFactory = sessionFactoryManager.getSessionFactory(diameterPacket.getApplicationID());
					ISession session = null;
					String sessionID = diameterPacket.getSessionID();
					if (sessionID != null) {
						session = diameterSessionFactory.readOnlySession(sessionID);
					}
					if(diameterPacket.isRequest()) {
						diameterPacket.getAsDiameterRequest().setRequestingHost(peerListener.getHostIdentity());
						diameterPluginManager.applyInPlugins((DiameterRequest)diameterPacket, null, session);
					} else {
						diameterPacket.getAsDiameterAnswer().setAnsweringHost(peerListener.getHostIdentity());
						diameterPluginManager.applyInPlugins(null, (DiameterAnswer)diameterPacket, session);
					}
					applyMonitoryLogLevel(diameterPacket);
					
					peerListener.processReceivedDiameterPacket(diameterPacket, connectionHandler);

				} else {
					handleReceivedInvalidMessage(diameterPacket, connectionHandler,validationCode);
				}
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unknown Peer: " + diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
				if(diameterPacket.isRequest()) {
					DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNKNOWN_PEER);
					try {
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Sending Answer: " + answerPacket.toString());
						connectionHandler.send(answerPacket);
					} catch (IOException e) {
						LogManager.getLogger().error(MODULE, "Error while sending UNKNOWN_PEER answer, reason: " + e.getMessage());
					}
				} else {
					LogManager.getLogger().error(MODULE, "Recieved Answer from Unknown IP Address: " + connectionHandler.getSourceIpAddress() +
					" with HbH-ID: "+ diameterPacket.getHop_by_hopIdentifier() + ". Dropping Answer");
					sendDPRtoUnknownPeer(connectionHandler, DisconnectionCause.DO_NOT_WANT_TO_TALK_TO_YOU);
				}
				LogManager.getLogger().warn(MODULE, "Closing Connection for IP Address " +
				connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
				connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
			}
		}catch(Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}finally{
				DiameterPacket diameterPacket = (DiameterPacket)packet;
				try{
					if(diameterPacket.getParameter(MonitorLogger.MONITORED) != null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Remove thread: " + Thread.currentThread().getName() + " from log monitor");
						LogManager.getLogger().removeThreadName(Thread.currentThread().getName());
		}
				}catch(Exception ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in removing diameter monitor log. Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
	}
				
			
			
		}
	}

	private boolean isLegitPeer(NetworkConnectionHandler connectionHandler,
			IPeerListener peerListener) {
		
		
		if(connectionHandler.getSourceIpAddress().equals(peerListener.getRemoteInetAddress().getHostAddress()) == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Connection IPAddress: " + connectionHandler.getSourceIpAddress() +
				" compared to Peer Address: " + peerListener.getRemoteInetAddress().getHostAddress());
			
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().debug(MODULE, "Packet from Unknown-Ip Address received: " + connectionHandler.getSourceIpAddress());
			}
			return false;
			
		}else if( peerListener.getHostIdentity() != null && peerListener.getHostIdentity().trim().length() > 0 
				&& peerListener.getHostIdentity().equals(connectionHandler.getHostName()) == false ){
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Connection HostName: " + connectionHandler.getHostName() +
				" compared to Peer Host Identity: " + peerListener.getHostIdentity());
			
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().debug(MODULE, "Packet from Peer with Unknown Host-Identity received: " + connectionHandler.getHostName());
			}
			return false;
		}
		return true;
	}

	private void sendDPRtoUnknownPeer(NetworkConnectionHandler connectionHandler,
			DisconnectionCause disconnectionCause) {

		LogManager.getLogger().info(MODULE, "Sending DPR to Unknown IP Address: " +
		connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setRequestingHost(Parameter.getInstance().getOwnDiameterIdentity());
		diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
		diameterRequest.setRequestBit();
		// FIXME These are not required now as are already set from the constructor in case request is local
		diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
		
		IDiameterAVP disconnectionCauseAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DISCONNECT_CAUSE);
		if(disconnectionCauseAVP == null){
			LogManager.getLogger().warn(MODULE, "Not Adding " + DiameterAVPConstants.DISCONNECT_CAUSE_STR +
			" AVP to DPR, Reason: AVP not found in dictionary");
		}else{
			disconnectionCauseAVP.setInteger(disconnectionCause.code);
			diameterRequest.addAvp(disconnectionCauseAVP);
		}
		try {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, diameterRequest.toString());
			connectionHandler.send(diameterRequest);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error in sending DPR to Unknown-Peer from Conncetion: " +
			connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	protected void addInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
		addHeaderInfoAVPs(diameterPacket,connectionHandler);
		addElitecoreInfoAVP(diameterPacket);
		addSubscriptionIdInfoAvp(diameterPacket);
		addUserEquipmentInfoAvp(diameterPacket);
		addPeerInfoAVPs(diameterPacket, connectionHandler);
	}
	



	private void addPeerInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
		
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME);
		if (diameterAVP == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Originator-Peer-Name(" + DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME +
				") not added in Diameter packet. Reason: attribute not found in dictionary");
		} else {
			PeerData peerData = diameterPeersTable.getPeerData(diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
			if (peerData == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Info Attribute EC-Originator-Peer-Name(" + DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME +
					") not added in Diameter packet. Reason: Peer data not found for " + diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
			} else {
				diameterAVP.setStringValue(peerData.getPeerName());
				diameterPacket.addInfoAvp(diameterAVP);
			}
		}
		
		PeerData peerData = diameterPeersTable.getPeerData(connectionHandler.getHostName());
		if (peerData == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Proxy-Agent-Name(" + DiameterAVPConstants.EC_PROXY_AGENT_NAME +
				") not added in Diameter packet. Reason: Peer data not found for " + connectionHandler.getHostName());
		} else {
			if (peerData.getPeerName().equals(diameterPacket.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME)) == false) {
				diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROXY_AGENT_NAME);
				if (diameterAVP == null) {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Info Attribute EC-Proxy-Agent-Name(" + DiameterAVPConstants.EC_PROXY_AGENT_NAME +
						") not added in Diameter packet. Reason: attribute not found in dictionary");
				} else {
					diameterAVP.setStringValue(peerData.getPeerName());
					diameterPacket.addInfoAvp(diameterAVP);
				}
			}
		}
		
	}


	private void addElitecoreInfoAVP(DiameterPacket diameterPacket){

		IDiameterAVP serverName = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SERVER_NAME);
		if (serverName != null ) {
			serverName.setStringValue(getDiameterStackIdentity());
			diameterPacket.addInfoAvp(serverName);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "EC-Server Name attribute not found in elitecore dictionary");
		}

		IDiameterAVP domainName = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_DOMAIN_NAME);
		if (domainName != null ) {
			domainName.setStringValue(getDiameterStackRealm());
			diameterPacket.addInfoAvp(domainName);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "EC-Domain Name attribute not found in elitecore dictionary");
		}

		IDiameterAVP serverInstanceID = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SERVER_INSTANCE_ID);
		if (serverInstanceID != null ) {
			serverInstanceID.setStringValue(getServerInstanceId());
			diameterPacket.addInfoAvp(serverInstanceID);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "EC-Server Instance Id attribute not found in elitecore dictionary");
			}
		}

	}

	private void addSubscriptionIdInfoAvp(DiameterPacket diameterPacket) {
		List<IDiameterAVP> subscriptionAvps = diameterPacket.getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
		if(subscriptionAvps!=null && subscriptionAvps.size()>0){
			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIPTION_ID);
			if(diameterAVP!=null){
				int numOfSubscriptionAvps = subscriptionAvps.size();
				IDiameterAVP subscriptionAvp =null;
				IDiameterAVP subscriptionType =null;
				IDiameterAVP subscriptionData =null;
				IDiameterAVP subIdDataValueAvp;
				for(int i=0;i<numOfSubscriptionAvps;i++){
					try{
						subIdDataValueAvp = null;
						subscriptionAvp = subscriptionAvps.get(i);
						subscriptionType = ((AvpGrouped)subscriptionAvp).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
						subscriptionData = ((AvpGrouped)subscriptionAvp).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
						if(subscriptionType!=null && subscriptionData!=null){
							long intSubType =subscriptionType.getInteger();  
							if(intSubType == DiameterAttributeValueConstants.END_USER_E164){
								subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIPTION_ID_E164);
							}else if (intSubType == DiameterAttributeValueConstants.END_USER_IMSI) {
								subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIPTION_ID_IMSI);
							}else if (intSubType == DiameterAttributeValueConstants.END_USER_SIP_URI) {
								subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIPTION_ID_SIP_URI);
							}else if (intSubType == DiameterAttributeValueConstants.END_USER_NAI) {
								subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIPTION_ID_NAI);
							}else if (intSubType == DiameterAttributeValueConstants.END_USER_PRIVATE) {
								subIdDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SUBSCRIPTION_ID_PRIVATE);
							}
							if(subIdDataValueAvp!=null){
								subIdDataValueAvp.setStringValue(subscriptionData.getStringValue());
								((AvpGrouped)diameterAVP).addSubAvp(subIdDataValueAvp);
							}
						}
					}catch (Exception e){
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Skipping adition of Subscription-ID AVP, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				diameterPacket.addInfoAvp(diameterAVP);
			}	

		}
	}

	private void addUserEquipmentInfoAvp(DiameterPacket diameterPacket) {
		List<IDiameterAVP> userEquipmenetAvps = diameterPacket.getAVPList(DiameterAVPConstants.USER_EQUIPMENT_INFO);
		if(userEquipmenetAvps!=null && userEquipmenetAvps.size()>0){
			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_INFO);
			if(diameterAVP!=null){
				int numOfUserEquipmenetAvpss = userEquipmenetAvps.size();
				IDiameterAVP userEquipmenetAvp =null;
				IDiameterAVP euipmentType =null;
				AvpUserEquipmentInfoValue euipmentData =null;
				IDiameterAVP euipmentDataValueAvp;
				for(int i=0;i<numOfUserEquipmenetAvpss;i++){
					try{
						euipmentDataValueAvp = null;
						userEquipmenetAvp = userEquipmenetAvps.get(i);
						euipmentType = ((AvpGrouped)userEquipmenetAvp).getSubAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE);
						try{
							euipmentData = (AvpUserEquipmentInfoValue)((AvpGrouped)userEquipmenetAvp).getSubAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO_VALUE);
						}catch(ClassCastException e){
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, "Skipping adition of User-Equipment-Info AVP, Reason: " + e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
							return;
						}
						if(euipmentType!=null && euipmentData!=null){
							long intEquipmentType =euipmentType.getInteger();
							if(intEquipmentType == DiameterAttributeValueConstants.IMEISV){
								String val = null;
								Map<String,String>imeisvMap = euipmentData.getIMEISV();
								euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_IMEISV);
								StringBuilder strIMEISV = new StringBuilder();
								IDiameterAVP userEquipmentTAC = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_IMEISV_TAC);
								if(userEquipmentTAC != null){
									val = imeisvMap.get(AvpUserEquipmentInfoValue.TAC);
									if(val != null){
										userEquipmentTAC.setStringValue(val);
										strIMEISV.append(val);
										((AvpGrouped)diameterAVP).addSubAvp(userEquipmentTAC);
									}else
										if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
											LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV_TAC AVP, Reason : TAC value is null");
								}else
									if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
										LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV_TAC AVP, Reason : AVP not found in dictionary");

								IDiameterAVP userEquipmentSNR = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_IMEISV_SNR);
								if(userEquipmentSNR != null){
									val = imeisvMap.get(AvpUserEquipmentInfoValue.SNR);
									if(val != null){
										userEquipmentSNR.setStringValue(val);
										strIMEISV.append(val);
										((AvpGrouped)diameterAVP).addSubAvp(userEquipmentSNR);
									}else
										if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
											LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SNR AVP, Reason : SNR value is null");
								}else
									if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
										LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SNR AVP, Reason : AVP not found in dictionary");

								IDiameterAVP userEquipmentSVN = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_IMEISV_SVN);
								if(userEquipmentSVN != null){
									val = imeisvMap.get(AvpUserEquipmentInfoValue.SVN);
									if(val != null){
										userEquipmentSVN.setStringValue(val);
										strIMEISV.append(val);
										((AvpGrouped)diameterAVP).addSubAvp(userEquipmentSVN);
									}else
										if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
											LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SVN AVP, Reason : SVN value is null");
								}else
									if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
										LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV_SVN AVP, Reason : AVP not found in dictionary");

								if(euipmentDataValueAvp!=null){
									euipmentDataValueAvp.setStringValue(strIMEISV.toString());
									((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
								}else
									if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
										LogManager.getLogger().debug(MODULE, "Skipping adition of EC_USER_EQUIPMENT_IMEISV AVP, Reason : AVP not found in dictionary");

							}else if (intEquipmentType == DiameterAttributeValueConstants.MAC){
								euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_MAC);
								if(euipmentDataValueAvp!=null){
									euipmentDataValueAvp.setStringValue(euipmentData.getStringValue());
									((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
								}
							}else if (intEquipmentType == DiameterAttributeValueConstants.EUI64){
								euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_EUI64);
								if(euipmentDataValueAvp!=null){
									euipmentDataValueAvp.setStringValue(euipmentData.getStringValue());
									((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
								}
							}else if (intEquipmentType == DiameterAttributeValueConstants.MODIFIED_EUI64){
								euipmentDataValueAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_USER_EQUIPMENT_MODIFIED_EUI64);
								if(euipmentDataValueAvp!=null){
									euipmentDataValueAvp.setStringValue(euipmentData.getStringValue());
									((AvpGrouped)diameterAVP).addSubAvp(euipmentDataValueAvp);
								}
							}
						}
					}catch(Exception e){
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Skipping adition of User-Equipment-Info AVP, Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
				diameterPacket.addInfoAvp(diameterAVP);
			}
		}
	}

	public void setDiameterStackURI(String ownDiameterURI) {
		this.ownDiameterURI = ownDiameterURI;
	}

	public String getDiameterStackURI() {
		return ownDiameterURI;
	}

	public void setDiameterStackIdentity(String ownDiameterIdentity) {
		this.ownDiameterIdentity = ownDiameterIdentity;
	}
	protected void setIsNAIEnabled(boolean isNAIEnabled) {
		this.isNAIEnabled = isNAIEnabled;
	}
	protected void setNAIRealms(List<String> naiRealmNames) {
		this.naiRealmNames = naiRealmNames;
	}
	protected void setIsRealmVerificationRequired(boolean isRealmVerificationRequired) {
		this.isRealmVerificationRequired = isRealmVerificationRequired;
	}

	public String getDiameterStackIdentity() {
		return ownDiameterIdentity;
	}

	public void setDiameterStackRealm(String ownDiameterRealm) {
		this.ownDiameterRealm = ownDiameterRealm;
	}
	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;
	}
	public String getRoutingTable() {
		return routingTableName;
	}
	public String getDiameterStackRealm() {
		return ownDiameterRealm;
	}
	
	public String getServerInstanceId() {
		return serverInstanceId;
	}
	
	public void setServerInstanceId(String serverInstanceId) {
		this.serverInstanceId = serverInstanceId;
	}
	
	private void initDiameterPeers() {
		for(PeerData peerData : peerDataList) {
			try{
				DiameterPeer diameterPeer = new DiameterPeer(peerData, stackContext, diameterRouter, 
						sessionFactoryManager, diameterAppMessageHandler, explicitRoutingHandler, 
						duplicateMessageHandler);
				diameterPeer.init();
				diameterPeersTable.addPeer(diameterPeer);
			}catch(Exception ex){
				LogManager.getLogger().error(MODULE, "Error while creating DiameterPeer from PeerData: "+ peerData.getPeerName() + ". Reason:" + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}
			
		}
	}
	
	private void startDiameterPeers() {
		for (DiameterPeer peer : diameterPeersTable.getPeerList()) {
			peer.start();
		}
	}

	private List<InetAddress> getAvailableInetAddresses(String remoteIPAddress) {
		try {
			List<String> availableIPs = DiameterUtility.getAvailableIPs(remoteIPAddress);
			if(availableIPs == null || availableIPs.isEmpty()) {
				return null;
			}
			
			List<InetAddress> inetAddresses = new ArrayList<InetAddress>();
			for(String address : availableIPs) {
				try {
					InetAddress inetAddr = InetAddress.getByName(address);
					inetAddresses.add(inetAddr);
				} catch (UnknownHostException e) { 
					LogManager.getLogger().debug(MODULE, "Unknown host address " + address + ". Reason: " + e.getMessage());
					ignoreTrace(e);
					continue;
				}
			}
			return inetAddresses;
		} catch (NumberFormatException e) {
			LogManager.getLogger().error(MODULE, "Error while getting available IP address from range " + remoteIPAddress + ". Reason: " + e.getMessage());
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while getting available IP address from range " + remoteIPAddress + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}


	@Override
	public synchronized boolean stop() {

		if (currentStackStatus == Status.STOPPED) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Diameter Stack is already Stopped, Rejecting Stop Operation");
			}
			return false;
		}
		currentStackStatus = Status.STOPPING;

		if(scheduledExecutorService != null){
			try{
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Stop session clean up task");
				scheduledExecutorService.shutdownNow();
			}catch(Exception ex){
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
		Collection<DiameterPeer>peers = this.diameterPeersTable.getPeerList();
		Iterator <DiameterPeer>peerItr = peers.iterator();
		while(peerItr.hasNext()) {
			DiameterPeer diameterPeer = peerItr.next();
			diameterPeer.stop();
		}
		
		if(scheduledThreadPoolExecutor != null){
			scheduledThreadPoolExecutor.shutdown();
			try {
				LogManager.getLogger().info(MODULE, "Waiting for server level Scheduled async task executor to complete execution");
				if(!scheduledThreadPoolExecutor.awaitTermination(2, TimeUnit.SECONDS)){
					LogManager.getLogger().info(MODULE, "Shutting down thread pool executer forcefully, Reason: Async task taking more time to complete");
					scheduledThreadPoolExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduledThreadPoolExecutor.shutdownNow();
			}
		}

		if(!super.stop()){
			LogManager.getLogger().error(MODULE, "Unknown Problem in Stopping Diameter Stack");
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Diameter Stack stopped successfully");
		}
		
		stopWorkerThreads();
		
		try {
			mibIndexRecorder.store();
		} catch (FileAllocatorException e) { 
			LogManager.getLogger().error(MODULE, "Unable to serialize Diameter MIB Indices, Reason " +
					e.getMessage() + ". This may effect SNMP Index Management");
			ignoreTrace(e);
		}
		Stack.generateAlert(StackAlertSeverity.CRITICAL, DiameterStackAlerts.DIAMETER_STACK_DOWN, MODULE, "Diameter Stack Down");
		currentStackStatus = Status.STOPPED;
		return true;
	}
	
	private void stopWorkerThreads() {
		try{
			if(subProcessThreadExecutor != null){
				subProcessThreadExecutor.shutdown();
				try {
					LogManager.getLogger().info(MODULE, "Waiting for connection level Scheduled async task executor to complete execution");
					if(!subProcessThreadExecutor.awaitTermination(2, TimeUnit.SECONDS)){
						LogManager.getLogger().info(MODULE, "Shutting down connection level Scheduled forcefully, Reason: Async task taking more time to complete");
						subProcessThreadExecutor.shutdownNow();
					}
				} catch (InterruptedException e) {
					subProcessThreadExecutor.shutdownNow();
				}
			}
			if (baseThreadExecutor != null) {
				baseThreadExecutor.shutdown();
				try {
					LogManager.getLogger().info(MODULE, "Waiting for Base message connection level Scheduled async task executor to complete execution");
					if(!baseThreadExecutor.awaitTermination(2, TimeUnit.SECONDS)){
						LogManager.getLogger().info(MODULE, "Shutting down Base message connection level Scheduled forcefully, Reason: Async task taking more time to complete");
						baseThreadExecutor.shutdownNow();
					}
				} catch (InterruptedException e) {
					baseThreadExecutor.shutdownNow();
				}
			}
		}catch(Exception ex){
			LogManager.getLogger().trace(MODULE, ex);
		}
	}

	public DiameterStatisticListener getDiameterStatisticListner() {
		return diameterStatisticListener;
	}

	
	public class DiameterStackContext implements IDiameterStackContext{

		TranslationSequencer translationSequencer  = new TranslationSequencer();
		
		@Override
		public void updateInputStatistics(DiameterPacket packet, String hostIdentity){
			diameterStatisticListener.updateInputStatistics(packet, hostIdentity);
		}
		
		@Override
		public void updateOutputStatistics(DiameterPacket packet, String hostIdentity){
			diameterStatisticListener.updateOutputStatistics(packet, hostIdentity);
		}
		
		@Override
		public void updateRealmInputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction){
			diameterStatisticListener.updateRealmInputStatistics(packet, realmName, routeAction);
		}
		
		@Override
		public void updateRealmOutputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction){
			diameterStatisticListener.updateRealmOutputStatistics(packet, realmName, routeAction);
		}
			
		@Override
		public void updateTimeoutRequestStatistics(DiameterRequest diameterRequest, String hostIdentity){
			diameterStatisticListener.updateTimeoutRequestStatistics(diameterRequest, hostIdentity);
		}

		@Override
		public void updateRealmTimeoutRequestStatistics(DiameterRequest diameterRequest, String realmName, RoutingActions routingAction) {
			diameterStatisticListener.updateRealmTimeoutRequestStatistics(diameterRequest, realmName, routingAction);
		}
		
		@Override
		public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity) {
			diameterStatisticListener.updateUnknownH2HDropStatistics(answer, hostIdentity);
		}
		@Override
		public void updateUnknownH2HDropStatistics(DiameterAnswer answer,
				String hostIdentity, String realmName, RoutingActions routeAction) {
			diameterStatisticListener.updateUnknownH2HDropStatistics(answer, hostIdentity, realmName, routeAction);
		}
		@Override
		public void updateDiameterStatsMalformedPacket(DiameterPacket packet, String hostIdentity) {
			diameterStatisticListener.updateMalformedPacketCount(packet, hostIdentity);
		}
		
		@Override
		public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity) {
			diameterStatisticListener.updatePacketDroppedStatistics(packet, hostIdentity);
		}
		
		@Override
		public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity, String realmName, RoutingActions routeAction) {
			diameterStatisticListener.updatePacketDroppedStatistics(packet, hostIdentity, realmName, routeAction);
		}
		
		@Override
		public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity) {
			diameterStatisticListener.updateDuplicatePacketStatistics(packet, hostIdentity);
		}
		
		@Override
		public ScheduledFuture<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
			return DiameterStack.this.scheduleIntervalBasedTask(task);
		}

		@Override
		public ScheduledFuture<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
			return DiameterStack.this.scheduleSingleExecutionTask(task);
		}
		
		@Override
		public boolean hasSession(String sessionId, long appId) {
			return sessionFactoryManager.getSessionFactory(appId).hasSession(sessionId);
		}
		
		@Override
		public ISession readOnlySession(String sessionId, long appId) {
			return sessionFactoryManager.getSessionFactory(appId).readOnlySession(sessionId);
		}
		
		@Override
		public Session getOrCreateSession(String sessionId, long appId) {
			return sessionFactoryManager.getSessionFactory(appId).getOrCreateSession(sessionId);
		}
		
		@Override
		public Session generateSession(long appId) {
			return generateSession(null, appId);
		}
		
		@Override
		@Nullable
		public Session generateSession(@Nullable String sessionIdSuffix, long appId) {
			String newSessionId;
			if(sessionIdSuffix != null) {
				newSessionId = getNextSessionID(sessionIdSuffix);
			} else {
				newSessionId = getNextSessionID();
			}
			return sessionFactoryManager.getSessionFactory(appId).getOrCreateSession(newSessionId);
		}

		@Override
		public TaskScheduler getTaskScheduler() {
			return DiameterStack.this.getTaskScheduler();
		}

		@Override
		public Set<ApplicationEnum> getApplicationsIdentifiersList() {
			return supportedApplicationIdentifiers;
		}

		@Override
		public void finalPreResponseProcess(DiameterPacket packet) {
			if(packet!=null){
				DiameterStack.this.finalPreResponseProcess(packet);
			}
				
		}

		@Override
		public PeerData getPeerData(String hostIdentity) {
			return DiameterStack.this.getPeerData(hostIdentity);
		}

		@Override
		public boolean validate() {
			return rfcValidation;
		}

		
		@Override
		public DiameterPeerState registerPeerStatusListener(String peerName,DiameterPeerStatusListener listener) throws StatusListenerRegistrationFailException {
			
			
			if(currentStackStatus == Status.NOT_INITIALIZE){
				throw new StatusListenerRegistrationFailException("Peer Not found", ListenerRegFailResultCode.STACK_NOT_INITIALIZED);
			}
			
			if(currentStackStatus == Status.STOPPED || currentStackStatus == Status.STOPPING){
				throw new StatusListenerRegistrationFailException("Peer Not found", ListenerRegFailResultCode.STOP_CALLED);
			}
			
			DiameterPeer peer = diameterPeersTable.getPeerByName(peerName);
			
			
			/*
			 * below condition will be true when...
			 * 
			 *  Case 1) Peer is registered but DiameterStack is initializing and IPeerListener is not registered to DiameterPeerTable
			 *  Case 2) Peer is not registered
			 *  
			 *  
			 * 
			 */
			if (peer == null){
				
				/*
				 * check that peer is in the registered peer list
				 * 
				 */
				for(PeerData peerData: DiameterStack.this.peerDataList){
					
					/*
					 *this may be true in Case 1 
					 */
					if(peerData.getPeerName().equals(peerName)){
						
						/*
						 * here we are doing best effor to register the peer, because it may happen that during this loop peer is registered in  DiameterPeerTable
						 * so check again that peer is exits in the table
						 */
						peer = diameterPeersTable.getPeerByName(peerName);
						if(peer == null){
							throw new StatusListenerRegistrationFailException("Peer Not found", ListenerRegFailResultCode.STARTUP_IN_PROGRESS);
			} 
		}
				}

				if(peer == null){
					throw new StatusListenerRegistrationFailException("Peer Not found", ListenerRegFailResultCode.PEER_NOT_FOUND);
				}
				
			}
			
			return peer.registerStatusListener(listener);
		}

		@Override
		public INetworkConnector getNetworkConnector(TransportProtocols transportProtocol) {
			return DiameterStack.this.getNetworkConnector(transportProtocol);
		}

		@Override
		public long getNextServerSequence() {
			return translationSequencer.getNextServerSequence();
		}
		@Override
		public long getNextPeerSequence(String hostIdentity) {
			return translationSequencer.getNextPeerSequence(hostIdentity);
		}
		
		@Override
		public String getNextSessionID(){
			int higherBit;
			if(sessLowerBit.compareAndSet(Integer.MAX_VALUE, 0)){
				if(!sessHigherBit.compareAndSet(Integer.MAX_VALUE, 1)){
					sessHigherBit.getAndIncrement();
				}
			}
			
			higherBit = sessHigherBit.get();

			return Parameter.getInstance().getOwnDiameterIdentity() + ";" + higherBit + ";" + sessLowerBit.getAndIncrement();
		}
		
		@Override
		public String getNextSessionID(String optionalVal){
			String sessionID = getNextSessionID();
			
			if(optionalVal != null){
				sessionID += ";" + optionalVal;
			}
			
			return sessionID;
		}
		
		@Override
		public boolean isNAIEnabled() {
			return isNAIEnabled;
		}
		@Override
		public boolean isValidNAIRealm(String realm) {
			if(!isRealmVerificationRequired)
				return true;
			else {
				return naiRealmNames.contains(realm);
			}
		}
		
		@Override
		public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> task) {
			return DiameterStack.this.scheduleCallableSingleExecutionTask(task);
		}
		@Override
		public EliteSSLContextFactory getEliteSSLContextFactory() {
			return eliteSSLContextFactory;
		}
		
		public VirtualConnectionHandler registerVirtualPeer(PeerData peerData, VirtualOutputStream outpurStream) 
				throws ElementRegistrationFailedException {
			return DiameterStack.this.registerVirtualPeer(peerData, outpurStream);
		}
		
		@Override
		public void purgeCancelledTasks() {
			DiameterStack.this.purgeCancelledTasks();
		}
		@Override
		public boolean isEREnabled() {
			// TODO Configuration
			return true;
		}
		@Override
		public int getTotalActiveSessionCount() {
			return sessionFactoryManager.getSessionCount();
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
		public boolean isOverLoad(DiameterRequest diameterRequest) {
			return isThresholdForLicenceTPSReached();
		}

		@Override
		public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name) throws 
				DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException {
			if(diameterCDRDriverFactory == null){
				throw new DriverNotFoundException("Unable to Create Driver configured with the Name: " +name);
			}
			
			return diameterCDRDriverFactory.getDriver(name);
		}

		public void scheduleSingleExecutionTask(Runnable command) {
			DiameterStack.this.scheduleSingleExecutionTask(command);
		}

		@Override
		public DiameterStatisticsProvider getDiameterStatisticsProvider() {
			return diameterStatisticListener.getDiameterStatisticProvider();
		}

		@Override
		public long releasePeerSessions(DiameterRequest request) {
			
			if (diameterSessionManager != null) {
				diameterSessionManager.delete(request, new DiameterAnswer(request));
			}
			SessionsFactory diameterSessionFactory = sessionFactoryManager.getSessionFactory(request.getApplicationID());
			return diameterSessionFactory.removeAllSessions(request.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}
		
		@Override
		public boolean isServerInitiatedMessage(int commandCode) {
			return DiameterStack.this.isServerInitiatedMessage(commandCode);
		}

		@Override
		public void submitToWorker(PacketProcess packetProcess) {
			DiameterStack.this.submitToWorker(packetProcess);
		}

		@Override
		public int getMaxWorkerThreads() {
			return DiameterStack.this.getMaxThreadPoolSize();
		}

		@Override
		public DiameterPeerCommunicator getPeerCommunicator(String hostIdentityOrName) {
			return diameterPeerCommunicatorFactory.createInstance(hostIdentityOrName);
		}

	}
	
	public boolean isThresholdForLicenceTPSReached(){
		return false;
	}


	public boolean isServerInitiatedMessage(int commandCode) {
		return CommandCode.getCommandCode(commandCode).isServerInitiated;
	}


	public TaskScheduler getTaskScheduler() {
		return this.taskScheduler;
	}


	public PeerData getPeerData(String hostIdentityOrIP) {
		if (hostIdentityOrIP == null)
			return null;
		return diameterPeersTable.getPeerData(hostIdentityOrIP);
	}
	
	public Map<String, IStateEnum> getPeersState(){
		return diameterPeersTable.getPeersState();
	}

	public boolean isValidPeer(String hostIdentity){
		return diameterPeersTable.getPeersState().containsKey(hostIdentity);
	}

	private void finalPreResponseProcess(DiameterPacket diameterPacket){
		ISession session = null;
		String sessionID = diameterPacket.getSessionID();
		SessionsFactory diameterSessionFactory = sessionFactoryManager.getSessionFactory(diameterPacket.getApplicationID());
		if (sessionID != null) {
			session = diameterSessionFactory.readOnlySession(sessionID);
		}
		if(diameterPacket.isRequest())
			diameterPluginManager.applyOutPlugins((DiameterRequest)diameterPacket, null, session);
		else {
			diameterPluginManager.applyOutPlugins(null, (DiameterAnswer)diameterPacket, session);
			
			IDiameterAVP resultCodeAvp = diameterPacket.getAVP(DiameterAVPConstants.RESULT_CODE);
			if(resultCodeAvp!=null){
				if((ResultCodeCategory.getResultCodeCategory(resultCodeAvp.getInteger()))== ResultCodeCategory.RC3XXX){
					diameterPacket.setErrorBit();
				}
			}
		}
	}

	public String getRealm(String hostIdentity){
		DiameterPeer diameterPeer = diameterPeersTable.getPeer(hostIdentity);
		if(diameterPeer!=null)
			return diameterPeer.getRealm();
		else {
			return null;
		}	
	}

	public DiameterStackContext getStackContext(){
		return this.stackContext;
	}


	public class TranslationSequencer{
		
		private AtomicLong serverSequence = new AtomicLong(0);
		private AtomicLong ownIdentitySequence = new AtomicLong(0);
		
		private long getNextServerSequence() {
			return serverSequence.getAndAdd(1);
		}
		
		private long getNextPeerSequence(String hostIdentity) {
			DiameterPeer diameterPeer = diameterPeersTable.getPeer(hostIdentity);
			if(diameterPeer!=null){
				return diameterPeer.getNextSequence();
			}else {
				return ownIdentitySequence.getAndAdd(1);
			}
			
		}
		
	}
	
	private void addHeaderInfoAVPs(DiameterPacket diameterPacket, NetworkConnectionHandler connectionHandler) {
		IDiameterAVP diameterAVP;
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_DIAMETER_VERSION);
		if(diameterAVP!=null){
			diameterAVP.setStringValue(String.valueOf(diameterPacket.getVersion()));
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Diameter-version("+DiameterAVPConstants.EC_DIAMETER_VERSION+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_COMMAND_FLAGES);
		if(diameterAVP!=null){
			if(diameterAVP.isGrouped()){
				AvpGrouped avpGrouped = (AvpGrouped)diameterAVP;
				IDiameterAVP subAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_REQUEST);
				if(subAvp!=null){
					subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isRequest()));
					avpGrouped.addSubAvp(subAvp);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Info Attribute EC-Request("+DiameterAVPConstants.EC_REQUEST+
						") not added in Diameter packet. Reason: attribute not found in dictionary");
					}
				}

				subAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_PROXY);
				if(subAvp!=null){
					subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isProxiable()));
					avpGrouped.addSubAvp(subAvp);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Info Attribute EC-Proxy("+DiameterAVPConstants.EC_PROXY+
						") not added in Diameter packet. Reason: attribute not found in dictionary");
					}
				}

				subAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_ERROR);
				if(subAvp!=null){
					subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isError()));
					avpGrouped.addSubAvp(subAvp);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Info Attribute EC-Error("+DiameterAVPConstants.EC_ERROR+
						") not added in Diameter packet. Reason: attribute not found in dictionary");
					}
				}

				subAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_RE_TRANSMITTED);
				if(subAvp!=null){
					subAvp.setInteger(DiameterUtility.booleanToInt(diameterPacket.isReTransmitted()));
					avpGrouped.addSubAvp(subAvp);
				}
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Info Attribute EC-Re-Transmitted("+DiameterAVPConstants.EC_RE_TRANSMITTED+
						") not added in Diameter packet. Reason: attribute not found in dictionary");
					}
				}
				if(avpGrouped.getGroupedAvp().size() > 1){
					diameterPacket.addInfoAvp(diameterAVP);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Info Attribute EC-Command-Flags("+DiameterAVPConstants.EC_COMMAND_FLAGES+
						") not added in Diameter packet. Reason: No sub attribute added in grouped attribute");
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Info Attribute EC-Command-Flags("+DiameterAVPConstants.EC_COMMAND_FLAGES+
					") not added in Diameter packet. Reason: attribute must be grouped");
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Command-Flags("+DiameterAVPConstants.EC_COMMAND_FLAGES+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_COMMAND_CODE);
		if(diameterAVP!=null){
			diameterAVP.setStringValue(String.valueOf(diameterPacket.getCommandCode()));
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Command-Code("+DiameterAVPConstants.EC_COMMAND_CODE+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_APPLICATION_ID);
		if(diameterAVP!=null){
			diameterAVP.setStringValue(String.valueOf(diameterPacket.getApplicationID()));
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Application-Id("+DiameterAVPConstants.EC_APPLICATION_ID+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_HOP_BY_HOP_IDENTIFIER);
		if(diameterAVP!=null){
			diameterAVP.setValueBytes(DiameterUtility.intToByteArray(diameterPacket.getHop_by_hopIdentifier()));
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Hop-by-Hop-Identifier("+DiameterAVPConstants.EC_HOP_BY_HOP_IDENTIFIER+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_END_TO_END_IDENTIFIER);
		if(diameterAVP!=null){			
			diameterAVP.setValueBytes(DiameterUtility.intToByteArray(diameterPacket.getEnd_to_endIdentifier()));
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-End-to-End-Identifier("+DiameterAVPConstants.EC_END_TO_END_IDENTIFIER+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SOURCE_IP_ADDRESS);
		if(diameterAVP!=null){			
			diameterAVP.setStringValue(connectionHandler.getSourceIpAddress());
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Source-Ip-Address("+DiameterAVPConstants.EC_SOURCE_IP_ADDRESS+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SOURCE_PORT);
		if(diameterAVP!=null){			
			diameterAVP.setStringValue(String.valueOf(connectionHandler.getSourcePort()));
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Source-Port("+DiameterAVPConstants.EC_SOURCE_PORT+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
		
		diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_REQUESTER_ID);
		if(diameterAVP!=null){			
			diameterAVP.setStringValue(connectionHandler.getHostName());
			diameterPacket.addInfoAvp(diameterAVP);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Info Attribute EC-Reqeuster-Id("+DiameterAVPConstants.EC_REQUESTER_ID+
				") not added in Diameter packet. Reason: attribute not found in dictionary");
			}
		}
	}

	private class AsyncTaskContextImpl implements AsyncTaskContext{
		private Map<String, Object> attributes;

		public synchronized void setAttribute(String key, Object attribute) {
			if (attributes == null) {
				attributes = new HashMap<String, Object>();
			}
			
			attributes.put(key, attribute);
		}

		public Object getAttribute(String key) {
			if (attributes != null) {
				return attributes.get(key);
			}
			return null;
		}
		
	}

	public void scheduleSingleExecutionTask(Runnable command) {
		scheduledThreadPoolExecutor.execute(command);
	}
	
	private <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(final CallableSingleExecutionAsyncTask<T> task){
		if(task == null){
    		return null;
    	}

		if (task.getInitialDelay() > 0) {
			return scheduledThreadPoolExecutor.schedule(new Callable<T>() {
				@Override
				public T call() {
    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
    				return task.execute(taskContext);
    				
				}}, task.getInitialDelay(), task.getTimeUnit());
		} else {
			return scheduledThreadPoolExecutor.schedule(new Callable<T>() {

					@Override
					public T call() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
	    				return task.execute(taskContext);
	    					
					}
				}, 0, TimeUnit.SECONDS);
		}
	}
	
    private ScheduledFuture<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
    	if(task == null){
    		return null;
    	}

		if (task.getInitialDelay() > 0) {
			return scheduledThreadPoolExecutor.schedule(new Runnable(){
				@Override
				public void run() {
    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
    				try {
    					task.execute(taskContext);
    				}catch(Throwable t) {
    					ignoreTrace(t);
    				} 
    				
				}}, task.getInitialDelay(), task.getTimeUnit());
		} else {
			return scheduledThreadPoolExecutor.schedule(new Runnable() {

				@Override
				public void run() {
    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
    				try {
    					task.execute(taskContext);
    				}catch(Throwable t) {
    					ignoreTrace(t);
    				} 
    				
				}}, 0, TimeUnit.SECONDS);
		}
	 
    }
    
    private ScheduledFuture<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {
    	
    	if(task == null)
    		return null;

		if (task.isFixedDelay()) {
    		return scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
    			@Override
				public void run() {
    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
    				
    				try {
    					task.preExecute(taskContext);
    				}catch(Throwable t) { 
    					ignoreTrace(t);
    				}
					
    				try {
    					task.execute(taskContext);
    				}catch(Throwable t) { 
    					ignoreTrace(t);
    				}
					
    				try {
    					task.postExecute(taskContext);
    				}catch(Throwable t) {  
    					ignoreTrace(t);
    				}
					
				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
		}else {
			return scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
    			@Override
				public void run() {
    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
    				try {
    					task.preExecute(taskContext);
    				}catch(Throwable t) { 
    					ignoreTrace(t);
    				}
					
    				try {
    					task.execute(taskContext);
    				}catch(Throwable t) { 
    					ignoreTrace(t);
    				}
					
    				try {
    					task.postExecute(taskContext);
    				}catch(Throwable t) { 
    					ignoreTrace(t);
    				}
					
				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
		}
	
    	
    	
    }
    
    
    private class TaskSchedulerImpl implements TaskScheduler {

		@Override
		public void execute(Runnable command) {
			scheduledExecutorService.execute(command);
		}

		@Override
		public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
			return DiameterStack.this.scheduleSingleExecutionTask(task);
		}

		@Override
		public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
			return DiameterStack.this.scheduleIntervalBasedTask(task);
		}
    	
    }
    
    private void purgeCancelledTasks() {
    	scheduledThreadPoolExecutor.purge();
    }

	
	// Diameter Session CleanUp Task.
	public class DiameterSessionCleanupTask implements Runnable{
		long sessionTimeOut ;
		public DiameterSessionCleanupTask (long sessionTimeOut){
			this.sessionTimeOut = sessionTimeOut;
		}

		@Override
		public void run() {
			sessionFactoryManager.removeIdleSession(sessionTimeOut);				
		}
		}
	// Setter And Getter Methods for Diameter Session Clean Up.
	public void setSessionInterval(long sessionCleanupInterval){
		this.sessionCleanupInterval=sessionCleanupInterval;
	}
	public long getSessionInterval(){
		return sessionCleanupInterval;
	}
	public void setSessionTimeOut(long sessionTimeOut){
		this.sessionTimeOut = sessionTimeOut;
	}
	public long getSessionTimeOut(){
		return sessionTimeOut;
	}

	public @Nonnull List<ApplicationListener> getDiameterApplicationListeners() {
		return applicationListeners;
	}
	
	

	public void setActionOnOverload(OverloadAction actionOnOverload) {
		this.actionOnOverload = actionOnOverload;
	}

	

	public void setResultCodeOnOverload(int resultCodeOnOverload) {
		if(ResultCode.isValid(resultCodeOnOverload)){
			this.resultCodeOnOverload = resultCodeOnOverload;
		}
	}
	
	public SessionFactoryManager getSessionFactoryManager() {
		return this.sessionFactoryManager;
	}
	
	public void applyMonitoryLogLevel(DiameterPacket diameterPacket) {
    }
	
	public boolean closePeer(String peerHostIdentity) {
		
		DiameterPeer peer = peerProvider.getPeer(peerHostIdentity);
		if(peer == null){
			return false;
		}
		peer.handleEvent(DiameterPeerEvent.Stop, ConnectionEvents.CONNECTION_DPR);
		return true;
	}

	public boolean forceClosePeer(String peerHostIdentity) {
		DiameterPeer peer = peerProvider.getPeer(peerHostIdentity);
		if(peer == null){
			return false;
		}
		peer.closeConnection(ConnectionEvents.FORCE_CLOSE);
		peer.handleEvent(DiameterPeerEvent.RPeerDisc, ConnectionEvents.FORCE_CLOSE);
		return true;
	}

	public boolean startPeer(String peerHostIdentity) {

		DiameterPeer peer = peerProvider.getPeer(peerHostIdentity);
		if(peer == null){
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Unable to start peer. Reason: Peer " + peerHostIdentity + " not found");
			}
			return false;
		}

		if(peer.getRemoteInetAddress() == null){
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Unable to start peer. Reason: Remote address not found for peer " + peerHostIdentity);
			}
			return false;
		}
		peer.handleEvent(DiameterPeerEvent.Start, null, null);		
		return true;
	}
	
	public void registerRouterGroovy(String routerGroovyScript){
		diameterRouter.setGroovy(routerGroovyScript);
	}
	
	public void registerMIBIndexRecorder(String basePath) throws FileAllocatorException {
		mibIndexRecorder.build(basePath);
	}
	
	public void registerDiameterSessionManager(IDiameterSessionManager diameterSessionManager){
		this.diameterSessionManager = diameterSessionManager;
		diameterRouter.registerDiameterSessionManager(diameterSessionManager);
	}
	public void setDuplicatePacketPurgeInterval(int dupicatePacketPurgeIntervalInSec) {
		duplicateMessageHandler.setDupicatePacketPurgeIntervalInSec(dupicatePacketPurgeIntervalInSec);
	}

	public void setDuplicateDetectionEnabled(boolean duplicateDetectionEnabled) {
		duplicateMessageHandler.duplicateDetectionEnabled(duplicateDetectionEnabled);
	}
	
	public void registerDiameterCDRDriverFactory(DiameterCDRDriverFactory diameterCDRDriverFactory) {
		this.diameterCDRDriverFactory = diameterCDRDriverFactory;
	}

	public Status getStackStatus() {
		return currentStackStatus;
	}

	public DiameterNetworkStatisticsProvider getStatisticsProvider() {
		return statisticsProvider;
	}
	
	/**
	 * Represents a Unit of work to be performed on {@link Packet} in the context of Stack
	 * worker threads.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public interface PacketProcess extends Runnable {

		public NetworkConnectionHandler getConnectionHandler();
		public Packet getPacket();

		/**
		 * Unit of work that is performed from the context of the submitting thread before the process is submitted
		 * to the processing queue.
		 * WARNING: Implementation should not be heavy.
		 */
		void preSubmit();

		/**
		 * Unit of work that is performed from the context of the submitting thread after the process has been submitted
		 * to the processing queue. Note that this method is called even if attempt to submit the process failed i.e. the
		 * attempt was rejected.
		 *
		 * WARNING: Implementation should not be heavy.
		 */
		void postSubmit();
		/**
		 * The unit of work should be performed in this method.
		 */
		@Override
		public void run();
	}
	
	/**
	 * Submits the packet process to stack thread pool for handling. This is a non-blocking method.
	 * 
	 * @param packetProcess a non-null unit of work to perform on packet.
	 */
	public void submitToWorker(PacketProcess packetProcess) {
		packetProcess.preSubmit();
		try {
			if (DiameterUtility.isBaseProtocolPacket(((DiameterPacket) packetProcess.getPacket()).getCommandCode())) {
				baseThreadExecutor.execute(packetProcess);
			} else {
				subProcessThreadExecutor.execute(packetProcess);
			}
		} finally {
			packetProcess.postSubmit();
		}
	}
}
