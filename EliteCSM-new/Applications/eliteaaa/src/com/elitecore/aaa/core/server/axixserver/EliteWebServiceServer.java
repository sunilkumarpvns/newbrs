package com.elitecore.aaa.core.server.axixserver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.server.AxisServer;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.utils.NetworkUtils;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.WebServCommand;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.servicex.UniSocketService;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.url.SocketDetail;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EliteWebServiceServer extends BaseEliteService implements WebserviceSessionEventListener, UniSocketService<ServiceRequest, ServiceResponse>{

	protected AAAServerContext serverContext;
	protected static final String MODULE = "WEB-SERVICE ";
	private ConcurrentHashMap<String, EliteSimpleSession> sessions = new ConcurrentHashMap<String, EliteWebServiceServer.EliteSimpleSession>();
	
	private SocketDetail listeningSocketDetail;

	protected ThreadPoolExecutor synchronousTaskExecutor;
	protected SynchronousQueue<Runnable> synchronousQueue;
	protected InetSocketAddress boundAddress;
	protected HttpServer server;
	private SocketDetail socketDetails;

	private Object requestListenerLockObject;
	
	private int threadPoolSize;
	private int maxSession;

	/**
	 * per thread socket information
	 */

	// Axis server (shared between instances)
	private static AxisServer myAxisServer = null;

	/**
	 * Are we doing sessions? Set this to false if you don't want any session
	 * overhead.
	 */
	private static boolean doSessions = true;

	// What is our current session index?
	// This is a monotonically increasing, non-thread-safe integer
	// (thread safety not considered crucial here)

	public static int sessionIndex = 0;

	protected EngineConfiguration myConfig;

	public EliteWebServiceServer(ServerContext ctx,String serviceIPAddress,int servicePort,int threadPoolSize,int maxSession) {
		super(ctx);
		this.serverContext = (AAAServerContext)ctx;
		requestListenerLockObject = new Object();
		this.socketDetails = new SocketDetail(serviceIPAddress, servicePort);
		this.threadPoolSize = threadPoolSize;
		this.maxSession = maxSession;
	}

	@Override
	public ServiceRequest formServiceSpecificRequest(
			InetAddress sourceAddress, int sourcePort, byte[] requestBytes) {
		return null;
	}

	@Override
	public ServiceResponse formServiceSpecificResposne(
			ServiceRequest serviceRequest) {
		return null;
	}

	@Override
	public String getKey() {
		return MODULE;
	}

	private String getListeningSocketAddress() {
		return socketDetails.toString();
	}
	
	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceIdentifier(), getStatus(), 
				getListeningSocketAddress(), getStartDate(), getRemarks());
	}

	@Override
	protected ServiceContext getServiceContext() {
		return new ServiceContext(){

			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}

		};
	}

	@Override
	public SocketDetail getSocketDetail() {
		return socketDetails;
	}

	@Override
	protected void initService() throws ServiceInitializationException {

		/*
		 *@ Port
		 *@HostIp
		 *@ Max Pool Size
		 *@ Max Session
		*/
		listeningSocketDetail = new SocketDetail(socketDetails.getIPAddress(), socketDetails.getPort());
		


		LogManager.getLogger().info(MODULE, "Initializing " + getServiceName());



		LogManager.getLogger().debug(MODULE, "Configured IP :"+socketDetails.getIPAddress() + " and configured port " + socketDetails.getPort()); 
		LogManager.getLogger().debug(MODULE, "Configured Max Thread Pool Size :" +threadPoolSize+" and configured max Session size " +maxSession);

		setMaxThreadMaxSession(maxSession, threadPoolSize);

		synchronousQueue = new SynchronousQueue<Runnable>();
		synchronousTaskExecutor = new ThreadPoolExecutor(5, threadPoolSize,getThreadKeepAliveTime(), TimeUnit.MILLISECONDS,synchronousQueue);
		synchronousTaskExecutor.setThreadFactory(new EliteThreadFactory("WS", "WS", 7));

	}

	class WebServiceSessionCleanupTask extends BaseIntervalBasedTask{

		@Override
		public long getInterval() {
			return 2;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MINUTES;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			int removedSessionCount = 0;
			long idleEpochTime = System.currentTimeMillis() - (getInterval() * 60000);
			for (EliteSimpleSession session : sessions.values()){
				long sessionLastAccessedTime = session.getLastAccessTime();
				if (sessionLastAccessedTime < idleEpochTime){
					session.releaseSession();
					removedSessionCount++;
				}
			}
			if(removedSessionCount > 0 ){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "No of Webservice Sessions removed : " + removedSessionCount);
			}
		}

	}

	public interface SessionEventListener {

		public boolean removeSession(String sessionId);
	}

	@Override
	public boolean removeSession(String cooky) {
		if (cooky != null && sessions.remove(cooky) != null ){
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Session for cooky: " + cooky+ " removed successfully. No of Active Sessions: " + sessions.size());
			return true;
		}
		return false;
	}

	public class EliteSimpleSession extends SimpleSession{
		private String cooky;
		private WebserviceSessionEventListener sessionEventListener;

		public EliteSimpleSession(String cooky,WebserviceSessionEventListener sessionEventListener){
			this.cooky=cooky;
			this.sessionEventListener = sessionEventListener;
		}

		public void releaseSession(){
			this.sessionEventListener.removeSession(cooky);
		}
	}
	
	@Override
	protected boolean startService() {
		try {
			
			boundAddress = new InetSocketAddress(socketDetails.getIPAddress(), socketDetails.getPort());
			
			server = HttpServer.create(boundAddress, 0);
			server.createContext("/", new RequestHandler());

			server.setExecutor(synchronousTaskExecutor);

			server.start();
			
			setStartDate(new Date(TimeSource.systemTimeSource().currentTimeInMillis()));

			serverContext.getTaskScheduler().scheduleIntervalBasedTask(new WebServiceSessionCleanupTask()); 
			listeningSocketDetail = new SocketDetail(server.getAddress().getAddress().getHostAddress(), server.getAddress().getPort());
			
			// Hard coding web service file name and file directory for the time being 
           	this.myConfig = new FileProvider(getServerContext().getServerHome()+File.separator+"system"+File.separator+"ws","server-config.wsdd");
           	
		} catch (IOException e) {
			remark = ServiceRemarks.PROBLEM_BINDING_IP_PORT.remark + " (" + e.getMessage() + ")";
			LogManager.getLogger().error(MODULE, "Problem starting " + getServiceIdentifier() + ", reason: " + e);
			LogManager.getLogger().trace(e);
			return false;
		}
		return true;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
	}

	@Override
	public String getServiceIdentifier() {
		return "WEB-SERVICE ";
	}

	@Override
	public boolean stopService() {
		
		if (server == null) {
			return true;
		}

		synchronized (requestListenerLockObject) {
			server.stop(3);
		}
		return true;
	}

	

	/**
	 * set maximum Thread pool, maximum Session size
	 * 
	 * @return
	 */
	private void setMaxThreadMaxSession(int sessionsize, int threadsize) {
		sessions = new ConcurrentHashMap<String, EliteSimpleSession>(
				threadsize, CommonConstants.DEFAULT_LOAD_FACTOR, threadsize);
	}

	/**
	 * demand create an axis server; return an existing one if one exists. The
	 * configuration for the axis server is derived from #myConfig if not null,
	 * the default config otherwise.
	 * 
	 * @return
	 */
	public synchronized AxisServer getAxisServer(){
		if (myAxisServer == null){

			if (myConfig == null) {
				myConfig = EngineConfigurationFactoryFinder.newFactory().getServerEngineConfig();
			}
			try {
				myAxisServer = new AxisServer(myConfig);
				ServiceAdmin.setEngine(myAxisServer, NetworkUtils.getLocalHostname() + "@"+ socketDetails.getPort());
			} catch (Exception e){
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		return myAxisServer;
	}

	protected boolean isSessionUsed() {
		return doSessions;
	}

	/**
	 * Obtain the serverSocket that that EliteWebServiceServer is listening on.
	 */
	public int getPort(){
		return socketDetails.getPort();
	}

	/**
	 * demand create a session if there is not already one for the string
	 * 
	 * @param cooky
	 * @return a session.
	 */
	protected Session createSession(String cooky) {

		// is there a session already?
		EliteSimpleSession session = null;
		if (sessions.containsKey(cooky)) {
			session = (EliteSimpleSession) sessions.get(cooky);
		} else {
			// no session for this cooky, bummer
			session = new EliteSimpleSession(cooky, this);

			// ADD CLEANUP LOGIC HERE if needed
			sessions.put(cooky, session);
		}
		return session;
	}

	@Override
	public String getServiceName() {
		return  "Web Service";

	}

	@Override
	public List<ICommand> getCliCommands() {
		List<ICommand> cmdList = new ArrayList<ICommand>();

		cmdList.add(new WebServCommand() {
			@Override
			public String getServiceThreadSummary() {
				return getThreadSummary();
			}

		});

		return cmdList;
	}

	private String getThreadSummary(){

		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n" + getServiceName()+" Thread Summary");
		responseBuilder.append("\n-------------------------------------------");
		responseBuilder.append("\n Service");
		responseBuilder.append("\n   Thread Pool              : ["+ getMinThreadCount() + "-" + getMaxThreadCount() + "]");
		responseBuilder.append("\n   Active Threads           : "+ getActiveThreadCount());
		responseBuilder.append("\n   Current Threads in Pool  : "+ getCurrentPoolSize());
		responseBuilder.append("\n   Peak Threads             : " + getPeakThreadCount());
		return responseBuilder.toString();

	}

	private int getThreadKeepAliveTime(){
		return 1000 * 60 * 60;
	}

	/* --- Service Sync Thread Summary -- */
	private final int getMinThreadCount(){
		if (synchronousTaskExecutor != null)
			return synchronousTaskExecutor.getCorePoolSize();
		return 0;
	}

	private final int getMaxThreadCount(){
		if (synchronousTaskExecutor != null)
			return synchronousTaskExecutor.getMaximumPoolSize();
		return 0;
	}

	private final int getActiveThreadCount(){
		if (synchronousTaskExecutor != null)
			return synchronousTaskExecutor.getActiveCount();
		return 0;
	}

	private final int getCurrentPoolSize(){
		if (synchronousTaskExecutor != null)
			return synchronousTaskExecutor.getPoolSize();
		return 0;
	}

	private final int getPeakThreadCount(){
		if (synchronousTaskExecutor != null)
			return synchronousTaskExecutor.getLargestPoolSize();
		return 0;
	}

	class RequestHandler implements HttpHandler{
		public void handle(HttpExchange exchange) throws IOException {
			EliteWebServiceWorker worker = new EliteWebServiceWorker(EliteWebServiceServer.this, exchange, serverContext);
			String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
			LogManager.getLogger().debug(MODULE, "Request Received from " + clientIP);
			worker.handleRequest();
			
		}
	}
	protected String getProtocol() {
		return "http";
	}

	@Override
	protected void shutdownService() {
		
	}

}
