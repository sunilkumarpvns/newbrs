package com.elitecore.core.servicex.base;


import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.servicex.EliteService;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.url.SocketDetail;
/**
 * 
 * @author Elitecore Technologies Ltd.
 * 
 */
public abstract class BaseEliteService implements EliteService {
	private static final String MODULE = "BS-EL-SER";
	protected static final int NANO_TO_MILLI = 1000000;
	private Date startDate = null;
	private LifeCycleState currentState = LifeCycleState.NOT_STARTED;
	private volatile boolean stopRequested = false;
	private final ServerContext serverContext;
	protected String remark; // to indicate why the service is stopped or not started..
	private boolean isInitialized = false;
	@Nonnull private TimeSource timesource;

	public BaseEliteService(ServerContext ctx) {
		this(ctx, TimeSource.systemTimeSource());
	}

	public BaseEliteService(ServerContext ctx, @Nonnull TimeSource timesource) {
		this.serverContext = ctx;
		this.timesource = checkNotNull(timesource, "timesource is null");
	}
	/**
	 * 
	 * @return
	 */
	protected final boolean isStopRequested() {
		return stopRequested;
	}

	protected String getThreadIdentifier() {
		return getServiceIdentifier();
	}

	/**
	 * 
	 * @see com.elitecore.core.servicex.EliteService#getCliCommands()
	 */
	public List<ICommand> getCliCommands() {
		return null;
	}

	public final String getStatus() {
		return currentState.message;
	}
	
	public String getRemarks(){
		return this.remark;
	}

	public void setRemark(ServiceRemarks remark){
		this.remark = remark.remark;
	}

	public Date getStartDate() {
		return startDate;
	}

	protected final void setStartDate(Date value) {
		this.startDate = value;		
	}

	protected ServerContext getServerContext() {
		return serverContext;
	}
	protected final void registerCacheable(Cacheable cacheableObj){
		serverContext.registerCacheable(cacheableObj);
	}
	
	protected abstract ServiceContext getServiceContext();

	public abstract void readConfiguration() throws LoadConfigurationException;

	public abstract String getKey();

	protected abstract void initService() throws ServiceInitializationException;
	
	protected abstract boolean startService();
	
	public final BaseEliteService init() throws ServiceInitializationException {
		try {
			initService();
			isInitialized = true;
		} catch (ServiceInitializationException ex) {
			setRemark(ex.getRemark());
			throw ex;
		} catch (Throwable t) {
			setRemark(ServiceRemarks.UNKNOWN_PROBLEM);
			throw new ServiceInitializationException(ServiceRemarks.UNKNOWN_PROBLEM, t);
		}
		return this;
	}
	
	public final boolean start(){
		if (isInitialized == false) {
			throw new IllegalStateException("start() called without calling init() or even when init() failed");
		}
		
		if (currentState != LifeCycleState.NOT_STARTED && currentState != LifeCycleState.STOPPED) {
			LogManager.getLogger().warn(MODULE, "Ignoring the start request received for already running service " + getServiceIdentifier());
			return false;
		}
		
		currentState = LifeCycleState.STARTUP_IN_PROGRESS;
		
		try {
			boolean isServiceStarted = startService();
			if (isServiceStarted) {
				setStartDate(new Date(timesource.currentTimeInMillis()));
				currentState = LifeCycleState.RUNNING;
				if (getServerContext().isServerStartedWithLastConf()) {
					currentState = LifeCycleState.RUNNING_WITH_LAST_CONF;
				}
			} else {
				currentState = LifeCycleState.NOT_STARTED;
			}
			return isServiceStarted;
		} catch (RuntimeException t) {
			currentState = LifeCycleState.NOT_STARTED;
			setRemark(ServiceRemarks.UNKNOWN_PROBLEM);
			throw t;
		}
	}

	@Override
	public final boolean stop() {
		stopRequested = true;
		currentState = LifeCycleState.SHUTDOWN_IN_PROGRESS;
		if (stopService()) {
			currentState = LifeCycleState.STOPPED;
			return true;
		}
		return false;
	}
	
	protected abstract boolean stopService();
	
	public boolean isInitialized(){
		return isInitialized;
	}
	
	public final void doFinalShutdown() {
		shutdownService();
	}
	
	protected abstract void shutdownService();
	
	protected void shutdownLogger() {
		
	}
	
	public boolean isDuplicateDetectionEnabled(){
		return false;
	}
	
	public int getDuplicateDetectionQueuePurgeInterval(){
		return 15;
	}
	
	@Override
	public void reInit(){
		
	}
	
	public final void cleanupResources() {
		shutdownLogger();
	}

	public List<SocketDetail> getListeningSocketDetails() {
		return null;
	}
}
