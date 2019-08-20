package com.elitecore.core.serverx;

import static com.elitecore.commons.base.Strings.isNullOrBlank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.servicex.EliteService;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.TableFormatter;
import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public abstract class BaseEliteServer implements ReInitializable{

	private List<EliteService> services = null;
    private static final String MODULE = "BASE_SERVER";
	private boolean validLicense = false;
	private String licenseKey = null;

	protected static final String SYSTEM_PATH = "system";
	protected static final String SYS_INIT_FILE = "_sys.init";
	protected static final String SYS_INFO_FILE = "_sys.info";
	private static String SYS_STARTUP_INFO_FILE = "_sys.start";
	private static String SYS_SHUTDOWN_INFO_FILE = "_sys.shutdown";
	private static final String LOG_FILE_EXT = ".log";

	private String serverHome;
	private String serverName;
	
	protected volatile LifeCycleState currentState = LifeCycleState.NOT_STARTED;
	
	private String localHostName = "unknown_host";
	
	private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private final TaskScheduler taskScheduler;
	private HtmlAdaptorServer htmlAdaptor;
	
	private List<Stopable> stopableComponants;
	
	/*
	 * It is used to signal the cleanup process. Once signaled, any thread that tries to acquire
	 * the permission to cleanup will be permitted.
	 */
	private final CountDownLatch cleanupSignal = new CountDownLatch(1);
	
	public BaseEliteServer(String serverHome, String serverName) {
		stopableComponants = new ArrayList<Stopable>();
		services = new LinkedList<EliteService>();
		this.serverHome = serverHome;
		this.serverName = serverName;
		setStdOutAndErr();
		this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(15, new EliteThreadFactory("SVR-SCH", "SVR-SCH", Thread.NORM_PRIORITY));
		this.taskScheduler = new TaskSchedulerImpl(); 
		//setting the localhost name
		try {
			localHostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex){}
	}
	
	protected void setInternalSchedulerThreadSize(int threadSize) {
		if(scheduledThreadPoolExecutor != null && threadSize > 15)
			scheduledThreadPoolExecutor.setCorePoolSize(threadSize);
	}
	protected final String getServerHome() {
		return serverHome;
	}
	
	protected final String getServerName() {
		return serverName;
	}
	
	protected final boolean registerService(EliteService service) {
		boolean serviceStarted = false;
		services.add(service);
		try {
			serviceStarted = service.init().start();
			if (serviceStarted) {
				LogManager.getLogger().info(MODULE, service.getServiceIdentifier()+" service started successfully");
			} else {
				LogManager.getLogger().error(MODULE, "Error while starting " + service.getServiceIdentifier() + " service");
			}
		} catch (ServiceInitializationException se) {
			LogManager.getLogger().warn(MODULE, "Error while initializing service: " + service.getServiceIdentifier() + ". Reason: " + se.getMessage());
			LogManager.getLogger().trace(MODULE, se);
			service.stop();
			service.doFinalShutdown();
		} catch (Throwable e) {
			LogManager.getLogger().warn(MODULE, "Unknown Error while initializing service: " + service.getServiceIdentifier() + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			service.stop();
			service.doFinalShutdown();
		}		
		return serviceStarted;
	}

	public abstract boolean validateLicenses(String key, String value);
    
    public abstract String getLicenseValues(String key);
    

    public final List<EliteService> getServices(){
    	return services;
    }

    public boolean isLicenseValid() {
    	return this.validLicense;
    }
    
    protected void startHtmlAdaptor(int htmlPort){

		if(htmlPort <= 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to start HTML Adaptor . Reason: Invalid port: " + htmlPort);
			}
			return;
		}
		
		try {
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Starting HTML Adaptor with port: " + htmlPort);
			}
			
			ObjectName htmlObjName = new ObjectName(MBeanConstants.HTML_PROTOCOL);
			htmlAdaptor = new HtmlAdaptorServer(htmlPort);
			
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(htmlAdaptor, htmlObjName);
			htmlAdaptor.start();
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "HTML Adaptor started successfully");
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to start HTML Adaptor. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	 public void setValidLicense(boolean status) {
		this.validLicense = status;
	 }
	 protected final void setLicenseKey(String licenseFile) throws FileNotFoundException {

	    	BufferedReader reader =null;
	    	try {
				reader = new BufferedReader(new FileReader(licenseFile));
	    		licenseKey = reader.readLine();
	    	} catch(Exception e){
	    		LogManager.getLogger().error(MODULE, "Problem while Reading LicenseKey : "+e.getMessage());
	    		LogManager.getLogger().trace(MODULE, e);
	    	}finally{
	    		try{
	    			if(reader!=null)
	    				reader.close();
	    		}catch(IOException ioExc){
	    			LogManager.getLogger().error(MODULE, "Problem while Reading LicenseKey "+ioExc.getMessage());
	    		}
	    	}
	    }  
	 
	 public String getLicenseKey() {
		 return licenseKey;
	 }        
	    

	public final boolean reloadConfiguration(){
		return reloadServerConfiguration();
	}
	
	private final void reInitServices(){
		for(EliteService service: services){
			try {
				service.reInit();
			} catch (InitializationFailedException ex) {
				LogManager.getLogger().trace(ex);
			}
		}
	}
	
	@Override
	public final void reInit(){
		reInitServer();
		reInitServices();
	}
	
	public abstract boolean reloadServerConfiguration();
	
	protected void reInitServer(){};
	
	protected String readJVMDetails() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();
		out.println();
		out.println("    java.home: " + System.getenv("JAVA_HOME"));
		out.println("    java.vendor: " + System.getProperty("java.vendor"));
		out.println("    java.version: " + System.getProperty("java.version"));
		out.println("    java.vm.name: " + System.getProperty("java.vm.name"));
		out.println("    java.vm.version: " + System.getProperty("java.vm.version"));
		out.println("    os.name: " + System.getProperty("os.name"));
		out.println("    os.arch: " + System.getProperty("os.arch"));
		out.println("    os.version: " + System.getProperty("os.version"));
		out.println("");
		
		return stringBuffer.toString();
	}
	   /**
     *   This class will run to see that within the scheduled time slot, 
     *   whether the TPS Counter has exceeded than License issued. 
     *   Whenever the task executes, the TPS Counter is reset to 0. 
     **/
    
   /* protected class TPSManager extends EliteInternalTask {

    	private static final String MODULE = "TPS Counter Manager";
    	private long lastResetTime;

    	public TPSManager(){
    		super(MODULE);
    		lastResetTime=new Date().getTime();
    	}

    	public void run() {
    		long currentTime=new Date().getTime();
    		long diff = (currentTime - lastResetTime)/1000;

    		if(!getServerContext().validateLicense("SYSTEM_TPS",String.valueOf(getServerContext().getTPSCounter()/diff))){

    			if(Logger.isLogLevel(LogLevel.WARN.LEVEL))
    				Logger.logWarn(MODULE, "The TPS has exceeded than License Taken!");

    		}
    		lastResetTime=currentTime;
    		//System.out.println("TPS  Conter : "+ getServerContext().getTPSCounter());
    		getServerContext().resetTPSCounter();

    	}
    }
    
    *//**
     * Server level scheduled task for performing server internal tasks. Current
     * implementation validates server license at regular interval and if 
     * license is found invalid, it stops all the active services.
     * 
     * @author Eltiecore Technologies Ltd.
     *
     *//*
    protected class EliteExpiryDateValidationTask extends EliteInternalTask {

    	boolean isValidLicence = true;

    	public EliteExpiryDateValidationTask() {
    		super("Server-Internal");
    	}

    	public void run() {

    		if(isValidLicence) {
    			if(! validateLicense("SYSTEM_EXPIRY",String.valueOf(System.currentTimeMillis()))){
    				if(Logger.isLogLevel(LogLevel.ERROR.LEVEL))
    					Logger.logError(MODULE,"Stopping all Services, Reason: Licence for Server is expired.");
    				stopAllServices();
    				isValidLicence = false;
    			}
    		}else {
    			if(Logger.isLogLevel(LogLevel.WARN.LEVEL))
    				Logger.logWarn(MODULE,"Licence for "+ getName() +" is expired.");	
    		}
    	}
    }*/
	
	class TaskSchedulerImpl implements TaskScheduler {

		@Override
		public Future<?> scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
			if (task == null) {
	    		return null;
	    	}
	    
	    	Future<?> future = null;
	    	if (task.getInitialDelay() > 0) {
	    		future = scheduledThreadPoolExecutor.schedule(new Runnable(){
	    			@Override
	    			public void run() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
	    				try {
	    					task.execute(taskContext);
	    				} catch(Throwable t) {
	    					LogManager.getLogger().trace(MODULE, t);
	    				}
	    			}}, task.getInitialDelay(), task.getTimeUnit());
	    	} else {
	    		future = scheduledThreadPoolExecutor.submit(new Runnable() {

	    			@Override
	    			public void run() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
	    				try {
	    					task.execute(taskContext);
	    				} catch(Throwable t) {
	    					LogManager.getLogger().trace(MODULE, t);
	    				}
	    			}});
	    	}
	    	
	    	return future;

		}

		@Override
		public Future<?> scheduleIntervalBasedTask(final IntervalBasedTask task) {
			if (task == null) {
	    		return null;
	    	}
	    	
	    	Future<?> future = null;
	    	if (task.isFixedDelay()) {
	    		try{
	    			future = scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
	    				@Override
	    				public void run() {
	    					AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();

	    					try {
	    						task.preExecute(taskContext);
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.execute(taskContext);
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.postExecute(taskContext);
	    					}catch(Throwable t) {

	    					}

	    				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());

	    		}catch(Exception e){
	    			LogManager.getLogger().error(MODULE, "Error in scheduling Fixed Delay task reason: " + e.getMessage());
	    			LogManager.getLogger().trace(MODULE,e);
	    		}

	    	}else {
	    		try{
	    			future = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
	    				@Override
	    				public void run() {
	    					AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl();
	    					try {
	    						task.preExecute(taskContext);
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.execute(taskContext);
	    					}catch(Throwable t) {

	    					}

	    					try {
	    						task.postExecute(taskContext);
	    					}catch(Throwable t) {

	    					}

	    				}}, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
	    		}catch(Exception e){
	    			LogManager.getLogger().error(MODULE, "Error in scheduling task reason: " + e.getMessage());
	    			LogManager.getLogger().trace(MODULE,e);
	    		}

	    	}
	    	
	    	return future;
		}

		@Override
		public void execute(Runnable command) {
			scheduledThreadPoolExecutor.execute(command);
		}
	}
	
	protected abstract class BaseServerContext implements ServerContext {

		@Override
		public String getServerHome() {
			return BaseEliteServer.this.getServerHome();
		}

		@Override
		public boolean isLicenseValid(String key, String value) {
			return false;
		}

		@Override
		public TaskScheduler getTaskScheduler() {
			return taskScheduler;
		}
		
		@Override
		public long getTPSCounter() {
			return 0;
		}

		@Override
		public void incrementTPSCounter() {
			
		}
		
		public void generateSystemAlert(IAlertEnum alert, String alertGeneratorIdentity, String alertMessage) {

		}
		
	    @Override
	    public String getServerInstanceId(){
	    	return BaseEliteServer.this.getServerInstanceID();
	    }
	    
	    @Override
		public String getServerInstanceName() {
			return BaseEliteServer.this.getServerInstanceName();
		}
	    
		public boolean isServerStartedWithLastConf() {
			return false;
		}
		
		@Override
		public void registerStopable(Stopable stopable) {
			BaseEliteServer.this.stopableComponants.add(stopable);
		}
	}

	protected class AsyncTaskContextImpl implements AsyncTaskContext {
		
		private Map<String, Object> attributes;
		
		public AsyncTaskContextImpl() {
		}

		@Override
		public synchronized void setAttribute(String key, Object attribute) {
			if (attributes == null) {
				attributes = new HashMap<String, Object>();
			}
			
			attributes.put(key, attribute);
		}

		@Override
		public Object getAttribute(String key) {
			if (attributes != null) {
				return attributes.get(key);
			}
			return null;
		}
		
	}
	
	/**
	 * Writes server state information given to system state file.
	 * 
	 * @param startupDetails
	 *            the state of the server to be written.
	 */
	protected void writeToStartupInfoFile() {
		String startupDetails = getServerName() + " started on " + new Date();
		File systemFolder = new File( getServerHome() + File.separator + SYSTEM_PATH);
		PrintWriter detailWriter = null;
		try {
			if (!systemFolder.exists())
				systemFolder.mkdirs();

			detailWriter = new PrintWriter(new FileWriter(new File(systemFolder, SYS_STARTUP_INFO_FILE), false));
			detailWriter.println(startupDetails);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Problem writing server startup information to sys file, reason: "+ e.getMessage());
		} finally {
			if (detailWriter != null)
				detailWriter.close();
		}
		
		//Update server shutdown file to intimate that server is startup successfully
		String shutdownDetails = getServerName() + " started on " + new Date();
		systemFolder = new File( getServerHome() + File.separator + SYSTEM_PATH);
		detailWriter = null;
		try {
			if (!systemFolder.exists())
				systemFolder.mkdirs();

			detailWriter = new PrintWriter(new FileWriter(new File(systemFolder, SYS_SHUTDOWN_INFO_FILE), false)); //NOSONAR - Reason: Resources should be closed
			detailWriter.println(shutdownDetails);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Problem writing server shutdown status information to sys file, reason: "+ e.getMessage());
		} finally {
			if (detailWriter != null)
				detailWriter.close();
		}
	
	}

	protected final void writeSysInitDetails() {
		String serverHome = getServerHome();
		File systemFolder = new File(serverHome + File.separator + SYSTEM_PATH);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(new File(systemFolder,SYS_INIT_FILE)));
			List<String> list = ManagementFactory.getRuntimeMXBean().getInputArguments();
			String port = null;
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					String arg = list.get(i);
					if (arg.contains("-Dcom.sun.management.jmxremote.port")) {
						port = arg.substring(arg.lastIndexOf("=") + 1);
						break;
					}
				}
			}
			if (port != null) {
				out.println(port.trim());
			} else {
				LogManager.getLogger().debug(MODULE, "Could not find JMX service port.");
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Problem writing server init information, reason : "+ e.getMessage());
		} finally {
			Closeables.closeQuietly(out);
		}
	}

	// FIXME when services are stopped due to reason of invalid license, then the remark is not set
	protected boolean stopServices(){
		LogManager.getLogger().info(MODULE, "Stop services operation started");
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		
		out.println("    Total service: " + services.size());
		
		for(EliteService service : services) {
			out.println( "        "+service.getServiceIdentifier() +  ":" + service.getStatus());
		}
		out.close();
		LogManager.getLogger().info(MODULE, buffer.toString());
		
		for (EliteService service : services) {
			LogManager.getLogger().info(MODULE, "Stopping service: " + service.getServiceIdentifier());
			service.stop();
		}
		
		for (EliteService service : services) {
			LogManager.getLogger().info(MODULE, "Final shutdown called for " + service.getServiceIdentifier());
			service.doFinalShutdown();
		}
		return true;
	}
	
	/**
	 * Stops all the services and scheduled tasks. After this call server will not accept
	 * any new tasks for execution.
	 * 
	 * @see ServerContext#getTaskScheduler()
	 */
	protected void stopServer() {
		stopServices();
		LogManager.getLogger().info(MODULE, "Stopping server level Scheduled async task executor");
		
		if (scheduledThreadPoolExecutor != null) {
			scheduledThreadPoolExecutor.shutdown();
			try {
				LogManager.getLogger().info(MODULE, "Waiting for server level Scheduled async task executor to complete execution");
				if (!scheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
					LogManager.getLogger().info(MODULE, "Shutting down thread pool executer forcefully, Reason: Async task taking more time to complete");
					scheduledThreadPoolExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduledThreadPoolExecutor.shutdownNow();
			}
		}
		
		if (htmlAdaptor != null) {
			htmlAdaptor.stop();
		}
		
		/*
		 *  It is possible that a component and it's sub-component both are 
		 *  registered for stopping.
		 *  
		 *  So it's required that sub-component is stopped before stopping 
		 *  the component.
		 *  
		 *  Assuming sub-component will be always registered after component,
		 *  the collection is reversed.
		 */
		Collections.reverse(stopableComponants);
		for (Stopable stop : stopableComponants) {
			stop.stop();
		}
	}
	
	/**
	 * All the resources, such as {@link ILogger}, that are used by the system 
	 * should be closed in this hook.
	 * <p>
	 * This hook is called by system at last when the server shutdown process has either 
	 * completed gracefully or is being aborted. In both cases the resources that are
	 * occupied must be closed.
	 * <p>
	 * NOTE: It is possible that server shutdown might be in progress while cleanup hook
	 * is called.
	 */
	protected void cleanupResources() {
		for (EliteService service : services) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Cleaning up resources for service: " + service.getServiceIdentifier());
			}
			service.cleanupResources();
		}
	}
	
	/**
	 * Writes server shutdown information given to system shutdown file.
	 * 
	 * @param shutdownDetails
	 *            the state of the server to be written.
	 */
	protected void writeToShutdownInfoFile() {
		String shutdownDetails = getServerName() + " shutdown on " + new Date();
		File systemFolder = new File( getServerHome() + File.separator + SYSTEM_PATH);
		PrintWriter detailWriter = null;
		try {
			if (!systemFolder.exists())
				systemFolder.mkdirs();

			detailWriter = new PrintWriter(new FileWriter(new File(systemFolder, SYS_SHUTDOWN_INFO_FILE), false)); //NOSONAR - Reason: Resources should be closed
			detailWriter.println(shutdownDetails);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Problem writing server shutdown status information to sys file, reason: "+ e.getMessage());
		} finally {
			if (detailWriter != null)
				detailWriter.close();
			
		}
	}

	protected boolean isServerShutdownAbnormally(){
		File shutdownFile = new File( getServerHome() + File.separator + SYSTEM_PATH + File.separator + SYS_SHUTDOWN_INFO_FILE);
		if(!shutdownFile.exists())
			return false;
		
		BufferedReader fileReader = null;
		try {

			fileReader = new BufferedReader(new FileReader(shutdownFile)); //NOSONAR - Reason: Resources should be closed
			String shutdownStatus = fileReader.readLine();
			if(shutdownStatus == null)
				return true;
			if(shutdownStatus.contains("shutdown"))
				return false;
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Problem closing server shutdown status information to sys file, reason: "+ e.getMessage());
		} finally {
			if (fileReader != null)
				try {
					fileReader.close();
				} catch (IOException e) {
					LogManager.getLogger().error(MODULE,"Problem closing server shutdown status information to sys file, reason: "+ e.getMessage());
				}
		}		
		return true;
	}
	
	protected void addShutdownHook() {
        ShutdownHook shutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	/**
	 * <p>
	 * This thread is registered to the JVM as a shutdown hook. It will be executed
	 * when JVM is shutting down. So, we can perform some clean up operations in case
	 * VM is shutting down.
	 * <p>
	 * We can signal JVM to execute the shutdown hook by <code>System.exit()</code> or 
	 * firing OS <code>kill</code> command. 
	 * <p>
	 * Note: Shutdown hook will not execute in case kill -9 (SIGKILL) is fired on this
	 * process. It may also not execute in case the JVM crashes.
	 * 
	 * @author Kuldeep.panchal
	 *
	 */
    private class ShutdownHook extends Thread {
    	
    	public void run() {
    		if (isServerRunning()) {
    			shutdown();
    			awaitShutdown();
    		}
    		
    		cleanupResources();
    	}
		
    	private void awaitShutdown() {
			try {
				awaitCleanup();
			} catch (InterruptedException e) {}
		}

		private boolean isServerRunning() {
			return currentState == LifeCycleState.RUNNING;
		}
    }
    
    /**
     * Awaits forever for cleanup signal.
     * <p>
     * This method is blocking and returns as soon as cleanup is signaled.
     * 
     * @see BaseEliteServer#signalCleanup()
     */
	protected void awaitCleanup() throws InterruptedException {
		cleanupSignal.await();
	}
	
	/**
	 * Awaits cleanup signal for specified milliseconds.
	 * <p>
	 * This method is blocking and returns as soon as cleanup is signaled or timeout
	 * occurs.
	 *   
	 * @param millis timeout in milliseconds
	 */
	protected synchronized void awaitCleanup(long millis) throws InterruptedException {
		cleanupSignal.await(millis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Signals cleanup so that resources can be closed. Any threads awaiting for cleanup
	 * will be able to proceed.
	 * 
	 * @see BaseEliteServer#awaitCleanup()
	 * @see BaseEliteServer#awaitCleanup(long)
	 */
	protected void signalCleanup() {
		cleanupSignal.countDown();
	}
    
	public final String readServerInstanceId() {
		String strID = null;
		String serverHome = getServerHome();
		BufferedReader bufferedReader = null;
		try {
			File infoFile = new File(serverHome + File.separator + SYSTEM_PATH + File.separator + SYS_INFO_FILE);
			if (infoFile.exists()) {
				bufferedReader = new BufferedReader(new FileReader(infoFile)); //NOSONAR - Reason: Resources should be closed
				String strTemp = bufferedReader.readLine();
				if (strTemp != null && strTemp.indexOf('=') != -1) {
					int index = strTemp.indexOf('=');
					strID = strTemp.substring(index + 1);
				} else {
					LogManager.getLogger().warn(MODULE,"Server ID is : " + strTemp+ ", which is wrong.");
				}
			} else {
				LogManager.getLogger().warn(MODULE, SYS_INFO_FILE + " file not found.");
			}
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE,"Error occured while reading server info, reason: "+ e.getMessage());
		} finally {
			
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		return strID;
	}
	
	final public String readServerInstanceName() {
		String strName = null;
		String serverHome = getServerHome();
		BufferedReader bufferedReader = null;
		try {
			File infoFile = new File(serverHome + File.separator + SYSTEM_PATH + File.separator + SYS_INFO_FILE);
			if (infoFile.exists()) {
				bufferedReader = new BufferedReader(new FileReader(infoFile)); //NOSONAR - Reason: Resource should be closed
				String strTemp = bufferedReader.readLine();
				if (strTemp != null && strTemp.indexOf('=') != -1) {
				} else {
					LogManager.getLogger().warn(MODULE,"Server ID is : " + strTemp+ ", which is wrong.");
				}
				strTemp = bufferedReader.readLine();
				if (strTemp != null && strTemp.indexOf('=') != -1) {
					int index = strTemp.indexOf('=');
					strName = strTemp.substring(index + 1);
				} else {
					LogManager.getLogger().warn(MODULE,"Server Name is : " + strTemp+ ", which is invalid. Manage this server using Server Manager.");
				}
			} else {
				LogManager.getLogger().warn(MODULE, SYS_INFO_FILE + " file not found.");
			}
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE,"Error occured while reading server info, reason: "+ e.getMessage());
		} finally {
 			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		return strName;
	}

	
	public abstract String getServerInstanceID();
	public abstract String getServerInstanceName();
	public abstract ServerContext getServerContext();
	
	protected abstract String getStdLogFileName();
	
	protected final String milliToHourTimeFormat(long millis) {
		StringBuffer stringBuffer = new StringBuffer();
		
		if( (millis / 1000 / 60 / 60) > 0) {
			stringBuffer.append(millis / 1000 / 60 / 60);
			stringBuffer.append("h:");
		}
		
		if( (millis / 1000 / 60) > 0) {
			stringBuffer.append((millis / 1000 / 60 ) % 60);
			stringBuffer.append("m:");
		}		

		if( (millis / 1000) > 0) {
			stringBuffer.append(millis / 1000 % (60 * 60));
			stringBuffer.append("s:");
		}
		
		stringBuffer.append(millis % 1000);
		stringBuffer.append("ms");
		
		return stringBuffer.toString();
	}
	
	public String getLocalHostName(){
		return this.localHostName;
	}
	
	private boolean setStdOutAndErr(){
		boolean isRedirected = false;		
		File stdLogFilePathDir = new File(getServerHome()  + File.separator + "logs");

		try {
			if (!stdLogFilePathDir.exists()) {
				if (!stdLogFilePathDir.mkdirs()) {
					LogManager.getLogger().warn(MODULE, "Could not create standard output directory: " + stdLogFilePathDir.getAbsolutePath());
					return false;
				}
			}

			if (!stdLogFilePathDir.isDirectory()) {
				LogManager.getLogger().warn(MODULE, stdLogFilePathDir.getAbsolutePath() + " is not a directory");
				return false; 
			}

			File stdFile = new File(stdLogFilePathDir, getStdLogFileName() + LOG_FILE_EXT);

			boolean isFileCreated = true;
			if (!stdFile.exists())
				isFileCreated = stdFile.createNewFile();

			if (isFileCreated) {
				// Creating file output stream in append mode 
				PrintStream ps = new PrintStream(new FileOutputStream(stdFile, true)); //NOSONAR
				System.setOut(ps);
				System.setErr(ps);
				isRedirected = true;
			} else {
				LogManager.getLogger().warn(MODULE, "Could not create standard output file: " + getServerHome()  + File.separator + "logs" + File.separator + getStdLogFileName() + LOG_FILE_EXT);
			}

		} catch (SecurityException se) {
			LogManager.getLogger().warn(MODULE, "Could not redirect standard out and standard err stream.Reason: " + se.getMessage());
		} catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Could not redirect standard out and standard err stream. Reason: " + e.getMessage());
		}

		return isRedirected;
	}
	
	/**
	 *  Schedules a task to roll standard log file daily
	 */
	public void scheduleStdFileRoller(){
		
		StdlogFileRollerTask stdlogFileRollerTask = new StdlogFileRollerTask();
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(stdlogFileRollerTask);
		
	}
	
	class StdlogFileRollerTask extends BaseIntervalBasedTask {
		
		private static final int ONE_DAY_SECONDS = 86400;
		
		StdlogFileRollerTask(){
		}
		
		@Override
		public long getInterval() {
			return ONE_DAY_SECONDS;
		}

		@Override
		public void execute(AsyncTaskContext context) {

			File stdLogFilePathDir = new File(getServerHome()  + File.separator + "logs");
			if (!stdLogFilePathDir.exists()) {
				LogManager.getLogger().warn(MODULE, getServerHome()  + File.separator + "logs" + " directory does not exists. Skipping roll over of standard log file.");
				return;
			}

			File stdLogFile = new File(stdLogFilePathDir, getStdLogFileName() + LOG_FILE_EXT);
			if (!stdLogFile.exists()) {
				LogManager.getLogger().warn(MODULE, "Standard log file does not exists. Skipping roll over of standard log file.");
				setStdOutAndErr();
				return;
			}

			SimpleDateFormat dateFormat = null;
			try {
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			} catch (IllegalArgumentException e) {
				dateFormat = new SimpleDateFormat();
			}
			File newFile = new File(stdLogFilePathDir, getStdLogFileName() + "_"+ dateFormat.format(new Date()) + LOG_FILE_EXT);
			stdLogFile.renameTo(newFile);

			setStdOutAndErr();

		}
		
		@Override
		public long getInitialDelay() {
			
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int date = cal.get(Calendar.DATE);
			
			Calendar cal1 = Calendar.getInstance();
			// This task will execute at 4:16 AM
			// Purpose of not scheduling at 4:00 AM is, Admin of system may have scheduled other things at sharp 4 
			cal1.set(year, month, date, 4, 16, 0);
			
			long initialDelay = (cal1.getTimeInMillis() - cal.getTimeInMillis()) / 1000;
			
			if (initialDelay < 0) {
				initialDelay += ONE_DAY_SECONDS;
			}
			
			return initialDelay;
		}
		
	}
	
	public LifeCycleState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Starts server shutdown process in background.
	 * <p>
	 * Note that this method returns immediately.
	 */
	public final void shutdown() {
		currentState = LifeCycleState.SHUTDOWN_IN_PROGRESS;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				stopServer();
				signalCleanup();
				currentState = LifeCycleState.STOPPED;
			}
		}, "Shutdown-Server-Thread").start();
	}
	
	
	/**<p>
	 * Command to shut down the server. It can shut down server gracefully, immediate or abort.
	 * <p>
	 * In case of graceful shutdown, it will wait until server is not shutdown.
	 * <p> 
	 * In case of immediate, if server is not shutdown within few seconds, it will terminate the server.
	 * <p> 
	 * In case of abort, terminate the server without waiting.
	 *   
	 * @author kuldeep.panchal
	 *
	 */
	public class ShutdownCommand extends EliteBaseCommand {
		
		private final String ABORT = "abort";
		private final String IMMEDIATE = "immediate";
		private final String[] columns =   {"", ""};
		private final int[] columnsWidth = {12, 66};
		
		private static final String MESSAGE_FOR_STARTUP_IN_PROGRESS = "Cannot stop server as server startup is in progress";
		private static final String MESSAGE_FOR_STOPPED = "Server shutdown successfully";
		private static final String MESSAGE_FOR_SHUTDOWN_IN_PROGRESS = "Shutdown is already in progress";
		private static final String TERMINATE_THREAD_NAME = "Terminate-Server-Thread";
		
		private final BaseEliteServer server;

		public ShutdownCommand(BaseEliteServer server) {
			this.server = server;
		}

		public synchronized String execute(String parameter) {
			if (isNullOrBlank(parameter)) {
				return shutdownGracefully();
			} 

			if (isAbort(parameter)) {
				return shutdownAbort();
			}
			
			if (isImmediate(parameter)) {
				return shutdownImmediate();
			}
			
			return getHelp();
		}
		
		private String shutdownAbort() {
			cleanupAndExitInBackground();
			return "Server aborted successfully";
		}
		
		private void cleanupAndExitInBackground() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					delayToDisplayCommandOutput();
					server.signalCleanup();
					System.exit(-1); //NOSONAR - Reason: Exit methods should not be called
				}

			}, TERMINATE_THREAD_NAME).start();
		}
		
		private void delayToDisplayCommandOutput() {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//no problem even if someone interrupts we need to exit
			}
		}
		
		private String shutdownGracefully() {
			if (isServerStartupInProgress()) {
				return MESSAGE_FOR_STARTUP_IN_PROGRESS; 
			}

			if (isServerShutdownInProgress()) {
				return MESSAGE_FOR_SHUTDOWN_IN_PROGRESS;
			}

			if (isServerStopped()) {
				return MESSAGE_FOR_STOPPED;
			}

			server.shutdown();

			return "Stop signal sent to server instance";
		}
		
		private String shutdownImmediate() {
			if (isServerStartupInProgress()) {
				return MESSAGE_FOR_STARTUP_IN_PROGRESS; 
			}

			if (isServerShutdownInProgress()) {
				return MESSAGE_FOR_SHUTDOWN_IN_PROGRESS;
			}

			if (isServerStopped()) {
				return MESSAGE_FOR_STOPPED;
			}

			shutdownImmediateInBackground();
			
			return "Shutdown immediate signal sent to server.";
		}

		private void shutdownImmediateInBackground() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					shutdownGracefullyWithTimeoutOf(15);
					System.exit(-1); //NOSONAR - Reason: Exit methods should not be called
				}

			}, TERMINATE_THREAD_NAME).start();
		}
		
		private void shutdownGracefullyWithTimeoutOf(long seconds) {
			server.shutdown();
			try {
				server.awaitCleanup(TimeUnit.SECONDS.toMillis(seconds));
			} catch (InterruptedException e) {
				//no problem even if someone interrupts we need to exit
			}
		}
		
		private boolean isServerStopped() {
			return server.getCurrentState() == LifeCycleState.STOPPED;
		}

		private boolean isServerShutdownInProgress() {
			return server.getCurrentState() == LifeCycleState.SHUTDOWN_IN_PROGRESS;
		}

		private boolean isServerStartupInProgress() {
			return server.getCurrentState() == LifeCycleState.STARTUP_IN_PROGRESS;
		}

		private boolean isImmediate(String parameter) {
			return IMMEDIATE.equalsIgnoreCase(parameter);
		}

		private boolean isAbort(String parameter) {
			return ABORT.equalsIgnoreCase(parameter);
		}

		public String getCommandName() {
			return "shutdown";
		}

		public String getDescription() {
			return "Tries to shutdown the server gracefully";
		}

		private String getHelp(){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : " + getCommandName() + " [<option>]");
			out.println("Description : " + getDescription() );
			out.print("Possible options:");
			TableFormatter tableFormatter = new TableFormatter(columns, columnsWidth,
					TableFormatter.NO_BORDERS);

			tableFormatter.addRecord(new String[] {IMMEDIATE, 
					"Tries to shutdown the server gracefully within the " +
							"timeframe of   15 seconds, aborts the server after that " +
			"interval"});

			tableFormatter.addRecord(new String[] {ABORT, 
					"Aborts the server immediately. It is NOT recommended " +
							"to use this  option in production environment. It will not " +
							"shutdown the server gracefully which may leave some tasks " +
			"incomplete"});

			out.println();
			out.println(tableFormatter.getFormattedValues());
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return String.format("{'shutdown':{'%s':{},'%s':{},'-help':{}}}", ABORT, IMMEDIATE);
		}
	}
}