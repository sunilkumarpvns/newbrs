package com.elitecore.core.driverx;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;

public abstract class BaseDriver extends ESCommunicatorImpl implements IEliteDriver {
	ServerContext serverContext;
	
	private static final String MODULE = "BASE-DRVR";
	
	public BaseDriver(final ServerContext serverContext){
		super(serverContext.getTaskScheduler());
		this.serverContext = serverContext;
	}
			
	public String getServerHome() {
		return this.serverContext.getServerHome();
	}
	
	public final void init() throws DriverInitializationFailedException {
		try {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Initializing driver: " + getName());
			}
			super.init();
			initInternal();
		} catch (TransientFailureException tfe) {
			markDead();
			LogManager.getLogger().warn(MODULE, "Driver: " + getName() + " failed due" 
					+ " to transient failure: " + tfe.getMessage() + ".");
			LogManager.getLogger().trace(MODULE, tfe);
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException(e);
		}
	}
	
	/**
	 * 
	 * This method is called once the driver instance has been created by the system.
	 * 
	 * <p>The implementation can perform following type of operations such as: initializing the
	 * database, validating the configuration, creating or reserving some resources such
	 * as threads.
	 * 
	 * <p>If driver implementation faces some error while creating externally dependent
	 * resources such as database connection, then {@link TransientFailureException} 
	 * should be thrown. These errors are treated as <i>temporary</i> failures by the 
	 * system. The state of the driver will be marked as dead in case of such failure by
	 * calling method {@link #markDead()}.
	 * 
	 * <p>If there is any permanent error such as invalid configuration which renders the
	 * driver unusable then {@link DriverInitializationFailedException} should be thrown.
	 * These errors are treated as <i>permanent</i> failures by the system. 
	 * 
	 * @throws TransientFailureException if there is any temporary failure due to external
	 * dependency such as database connectivity
	 * @throws DriverInitializationFailedException if there is any permanent failure such
	 * as configuration fault
	 */
	protected abstract void initInternal() throws TransientFailureException, DriverInitializationFailedException;
	
	protected ServerContext getServerContext(){
		return this.serverContext;
	}
}
