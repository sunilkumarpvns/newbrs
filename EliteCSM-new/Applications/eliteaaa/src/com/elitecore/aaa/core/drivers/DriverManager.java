package com.elitecore.aaa.core.drivers;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.driverx.IEliteDriver;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.statistics.PermanentFailureStatistics;
import com.elitecore.core.util.cli.cmd.BaseESIScanCommand;
import com.elitecore.core.util.cli.cmd.BaseESIScanCommand.ESIStatus;

/**
 * This class serves the purpose of initializing and managing all the drivers
 * created using {@link DriverFactory}. Responsibilities of this class
 * include initializing drivers, registering the driver statistics to various
 * interested parties, stopping all the managed drivers.
 * 
 * @author narendra.pathai
 * 
 */
public final class DriverManager implements ReInitializable {
	private static final String MODULE = "DRVR-MGR";
	/* ------ Entries of statistics of all drivers  --------- */
	private static final Map<String, ESIStatistics> driverStatisticsByName;
	
	static {
		driverStatisticsByName = new ConcurrentHashMap<String, ESIStatistics>();
	}
	
	private Map<String, Optional<IEliteDriver>> driverIdToOptionalDriver = null;
	private @Nonnull final DriverConfigurationProvider configurationProvider;
	private @Nonnull final DriverFactory driverFactory;
	
	/**
	 * Creates a driver manager using provided driver factory to create drivers.
	 * 
	 * @param configurationProvider a non-null configuration provider
	 * @param driverFactory a non-null driver factory
	 * @throws NullPointerException if any argument is null
	 */
	public DriverManager(DriverConfigurationProvider configurationProvider,
			DriverFactory driverFactory) {
		this.configurationProvider = checkNotNull(configurationProvider, 
				"configurationProvider is null");
		this.driverFactory = checkNotNull(driverFactory, "driverFactory is null");
		this.driverIdToOptionalDriver = new ConcurrentHashMap<String, Optional<IEliteDriver>>();
	}
	
	public static Collection<ESIStatistics> getAllDriverStatistics() {
		return Collections.unmodifiableCollection(driverStatisticsByName.values());
	}
	
	public static ESIStatistics getDriverStatisticsByName(String driverName) {
		return driverStatisticsByName.get(driverName);
	}
	
	/**
	 * Eagerly prepares drivers by initializing them and storing the references as well as
	 * statistics
	 */
	public void init() {
		LogManager.getLogger().debug(MODULE, "Initializing driver manager");
		
		for (DriverConfiguration driverConfiguration : configurationProvider.getDriverConfigurations()) {
			driverIdToOptionalDriver.put(driverConfiguration.getDriverInstanceId(),
					Optional.of(createDriver(driverConfiguration)));
		}
		
		LogManager.getLogger().debug(MODULE, "Completed initialization of driver manager");
	}
	
	/**
	 * Returns the driver corresponding to the driverInstanceId passed if it
	 * exists or null.
	 * 
	 * @param driverInstanceId instance id of the driver
	 * @return driver corresponding to driverInstanceId passed. May return
	 * null if the id is unknown or driver could not be created. 
	 */
	public @Nullable IEliteDriver getDriver(String driverInstanceId) {
		Optional<IEliteDriver> optionalDriver = this.driverIdToOptionalDriver.get(driverInstanceId);
		if (optionalDriver == null) {
			return null;
		}
		return optionalDriver.orNull();
	}
	
	/**
	 * Creates a new instance of the driver corresponding to the driverInstaceId.
	 * 
	 * @return newly created driver or null if driver fails to initialize
	 */
	private @Nullable IEliteDriver createDriver(@Nonnull DriverConfiguration driverConfiguration) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE,
					"Creating driver with instance id: " + driverConfiguration.getDriverInstanceId());
		}

		IEliteDriver driver = driverFactory.createDriver(driverConfiguration.getDriverType(),
				driverConfiguration.getDriverInstanceId());

		if (driver == null) {
			return null;
		}

		try {
			driver.init();

			BaseESIScanCommand.registerESI(new ESIStatusImpl(driver));
			// registering the driver statistics
			driverStatisticsByName.put(driver.getName(),
					driver.getStatistics());

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Successfully created driver: "
						+ driver.getName());
			}
			return driver;
		} catch (InitializationFailedException ex) {
			BaseESIScanCommand.registerESI(new PermanentFailureStatus(driverConfiguration));
			driverStatisticsByName.put(driver.getName(),
					new PermanentFailureStatistics(driver.getName(), driver.getTypeName()));
			LogManager.getLogger().trace(MODULE, ex);
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Error in initializing driver reason: "
						+ ex.getMessage());
			}
			return null;
		}
	}
	
	public void stop() {
		for (Optional<IEliteDriver> communicator : this.driverIdToOptionalDriver.values()) {
			if (communicator.isPresent() == false) {
				continue;
			}
			communicator.get().stop();
		}
	}
	
	@Override
	public void reInit() {
		for (Optional<IEliteDriver> communicator : this.driverIdToOptionalDriver.values()) {
			if (communicator.isPresent() == false) {
				continue;
			}
			try {
				communicator.get().reInit();
			} catch (InitializationFailedException e) {
				LogManager.getLogger().trace(e);
				LogManager.getLogger().warn(MODULE, "Failed to re-initialize driver: " 
						+ communicator.get().getName() + ", Reason: " + e.getMessage());
			}
		}
	}
	
	private class ESIStatusImpl implements BaseESIScanCommand.ESIStatus {
		@Nonnull IEliteDriver driver;
		
		private ESIStatusImpl(@Nonnull IEliteDriver driver) {
			this.driver = driver;
		}

		@Override
		public String getESStatus() {
			driver.scan();
			if (driver.isAlive()) {
				return BaseESIScanCommand.ESIStatus.ALIVE;
			} else {
				return BaseESIScanCommand.ESIStatus.DEAD;
			}
		}

		@Override
		public String getESName() {
			return driver.getName();
		}

		@Override
		public String getConfiguredESType() {
			return DriverTypes.getDriverTypeStr(driver.getType());
		}
	}
	
	private class PermanentFailureStatus implements BaseESIScanCommand.ESIStatus {
		private final DriverConfiguration driverConfiguration;

		public PermanentFailureStatus(DriverConfiguration driverConfiguration) {
			this.driverConfiguration = driverConfiguration;
		}
		
		@Override
		public String getESStatus() {
			return ESIStatus.FAIL;
		}

		@Override
		public String getESName() {
			return driverConfiguration.getDriverName();
		}

		@Override
		public String getConfiguredESType() {
			return driverConfiguration.getDriverType().name();
		}
	}
}