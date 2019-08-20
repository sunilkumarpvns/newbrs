package com.elitecore.aaa.core.wimax.keys;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.coreradius.util.wimax.WiMAXUtility;

public abstract class DhcpKeyManagerImpl implements DhcpKeyManager {

	private static final String MODULE = "DHCP-KEY-MGR";

	protected AAAServerContext serverContext;
	private boolean isKeysCleanupEnabled = false;
	private long keysCleanupInterval = 86400;

	private final long keysThresholdInSeconds;
	private final long dhcpRkLifetimeInSeconds;

	public DhcpKeyManagerImpl(DHCPKeysConfiguration dhcpKeysConfiguration, WimaxConfiguration wimaxConfiguration) {
		isKeysCleanupEnabled = dhcpKeysConfiguration.getIsDhcpKeysCleanupEnabled();
		keysCleanupInterval = dhcpKeysConfiguration.getDhcpKeysCleanupInterval();
		this.keysThresholdInSeconds = dhcpKeysConfiguration.getDhcpRkthresholdTime();
		dhcpRkLifetimeInSeconds = wimaxConfiguration.getDhcpRkLifetimeInSeconds();
	}

	public static DhcpKeyManager newInstance(AAAServerContext serverContext) {
		if (dhcpTableExists()) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "DHCP key management will be performed using DB with in-memory cache");
			}
			return new DhcpDatabaseKeyManager(serverContext.getServerConfiguration().getDhcpKeysConfiguration(), 
					serverContext.getServerConfiguration().getWimaxConfiguration()).init();
		}
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "DHCP key management will be performed using in-memory cache as DHCP key table does not exist.");
		}
		return new DhcpInMemoryKeyManager(serverContext.getServerConfiguration().getDhcpKeysConfiguration(), 
				serverContext.getServerConfiguration().getWimaxConfiguration()).init();
	}

	protected final DhcpKeyManager init() {

		LogManager.getLogger().trace(MODULE,"Init key manager started");

		//TODO 0006 Use central scheduler instead
		ExpiredKeysCleanupTask expiredKeysCleanupTask = null;

		if(isKeysCleanupEnabled){
			expiredKeysCleanupTask = new ExpiredKeysCleanupTask(keysCleanupInterval,keysCleanupInterval);
			serverContext.getTaskScheduler().scheduleIntervalBasedTask(expiredKeysCleanupTask);
			LogManager.getLogger().info(MODULE,"Keys clean-up process enabled, execution interval: " + keysCleanupInterval + " seconds");
		} else {
			LogManager.getLogger().info(MODULE,"Keys clean-up process disabled");
		}

		LogManager.getLogger().trace(MODULE,"Init key manager completed");

		return this;
	}

	private static boolean dhcpTableExists() {
		Connection connection = null;
		ResultSet tables = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			tables = metaData.getTables(null, null, DhcpDatabaseKeyManager.DHCP_TABLENAME, null);
			return tables.next();
		} catch (DataSourceException e) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " 
						+ EliteAAADBConnectionManager.ELITE_AAADB_CACHE 
						+ " is unavailable, Reason: " + e.getMessage());
			}
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to check the existance of DHCP keys table, Reason: " + e.getMessage());
		} finally {
			DBUtility.closeQuietly(tables);
			DBUtility.closeQuietly(connection);
		}
		return false;
	}


	class ExpiredKeysCleanupTask extends BaseIntervalBasedTask{

		private long initialDelay;
		private long intervalSeconds;

		public ExpiredKeysCleanupTask(long initialDelay, long intervalSeconds){
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
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"Cleaning up the expired keys.");
			}
			removeExpiredKeys();			
		}

	}

	protected abstract void removeExpiredKeys();

	protected final DhcpKeys getDhcpKeyDetailsWithHighestLifetime(
			long currentTimeInMillis, List<DhcpKeys> dhcpKeyDetailsList){
		
		if (Collectionz.isNullOrEmpty(dhcpKeyDetailsList)) {
			return null;
		}
		long remainingLifetimeInSeconds = 0;
		long highestRemainingLifetimeInSeconds = 0;
		DhcpKeys dhcpKeyDetailsWithHighestLifetime = null;

		for (int i = 0; i < dhcpKeyDetailsList.size(); i++) {
			DhcpKeys dhcpKeyDetails = dhcpKeyDetailsList.get(i);
			remainingLifetimeInSeconds = dhcpKeyDetails.getRemainingLifetimeInSeconds(currentTimeInMillis);							

			if (remainingLifetimeInSeconds > highestRemainingLifetimeInSeconds 
					&& remainingLifetimeInSeconds > keysThresholdInSeconds) {						
				highestRemainingLifetimeInSeconds = remainingLifetimeInSeconds;
				dhcpKeyDetailsWithHighestLifetime = dhcpKeyDetails;
			}
		}
		return dhcpKeyDetailsWithHighestLifetime;
	}
	
	protected final DhcpKeys generateDhcpKey(String dhcpIpAddress,
			long currentTimeInMillis) {
		DhcpKeys dhcpKeys;
		dhcpKeys = new DhcpKeys();
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE," No keys cached for " + dhcpIpAddress 
					+ ",generating new DHCP-RK.") ;
		}

		byte[] dhcp_rk_key = WiMAXUtility.generateDhcp_RK();
		dhcpKeys.setDhcp_rk(dhcp_rk_key);

		int dhcp_rk_id = WiMAXUtility.generateDHCP_RK_KEY_ID();
		dhcpKeys.setDhcp_rk_id(dhcp_rk_id);
		if(dhcpRkLifetimeInSeconds < 86400){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"DHCP-RK-Lifetime is less than 86400 Seconds(24 Hours)");
			}
		}
		dhcpKeys.setDhcp_rk_lifetime_in_seconds(dhcpRkLifetimeInSeconds);
		dhcpKeys.setDhcp_rk_genereation_time_in_millis(currentTimeInMillis);
		return dhcpKeys;
	}


	protected final DhcpKeys getDhcpKeyForRKId(int dhcpKeyId, long currentTimeInMillis,
			List<DhcpKeys> dhcpKeyDetailsList) {
		if (dhcpKeyDetailsList == null) {
			return null;
		}
		DhcpKeys dhcpKeyDetails;
		for (int i = 0 ; i < dhcpKeyDetailsList.size() ; i++) {
			dhcpKeyDetails = dhcpKeyDetailsList.get(i);
			if (dhcpKeyId == dhcpKeyDetails.getDhcp_rk_id() 
					&& (dhcpKeyDetails.isExpired(currentTimeInMillis) == false)) {
				return dhcpKeyDetails;
			}
		}		
		return null;
	}

}

