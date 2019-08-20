package com.elitecore.aaa.core.wimax.keys;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.keys.HAKeyManagerImpl.HAKeyStatistics.HAKeyCounter;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.coreradius.util.wimax.WiMAXUtility;

public abstract class HAKeyManagerImpl implements HAKeyManager {
	
	private static final String MODULE = "WIMAX-KEY-MGR";
	
	protected HAKeyStatistics haKeyStatistics = new HAKeyStatistics();

	private WimaxConfiguration wimaxConfiguration;
	
	public class HAKeyStatistics {
		private ConcurrentHashMap<String, HAKeyCounter> haIpToHaKeyCounter = new ConcurrentHashMap<String, HAKeyCounter>();
		
		public class HAKeyCounter {
			private long cacheMissCount = 0L;
			private long keyCreationCount = 0L;
			
			public long getCacheMissCount() {
				return cacheMissCount;
			}
			public void incrementCacheMissCount() {
				this.cacheMissCount++;
			}
			
			public long getKeyCreationCount() {
				return keyCreationCount;
			}
			
			public void incrementKeyCreationCount() {
				this.keyCreationCount++;
			}
			
			@Override
			public String toString() {
				return String.format("Cache Missed: %d, Key creation count: %d", cacheMissCount, keyCreationCount); 
			}
		}
		
		public HAKeyCounter getHAKeyCounter(String haIpAddress) {
			HAKeyCounter haKeyCounter = haIpToHaKeyCounter.get(haIpAddress);
			if (haKeyCounter == null) {
				synchronized (this) {
					haKeyCounter = haIpToHaKeyCounter.get(haIpAddress);
					if (haKeyCounter == null) {
						haKeyCounter = new HAKeyCounter();
						haIpToHaKeyCounter.put(haIpAddress, haKeyCounter);
					}
				}
			}
			return haKeyCounter;
		}

		public ConcurrentHashMap<String, HAKeyCounter> getAll() {
			return haIpToHaKeyCounter;
		}
	}
	
	public HAKeyManagerImpl(WimaxConfiguration wimaxConfiguration) {		
		this.wimaxConfiguration = wimaxConfiguration;
	}
	
	public static HAKeyManager newInstance(AAAServerContext serverContext) {
		if (haTableExists()) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "HA key management will be performed using DB with in-memory cache");
			}
			return new HADatabaseKeyManager(serverContext.getServerConfiguration().getWimaxConfiguration());
		}
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "HA key management will be performed using in-memory cache as HA key table does not exist.");
		}
		return new HAInMemoryKeyManager(serverContext.getServerConfiguration().getWimaxConfiguration());
	}
	
	private static boolean haTableExists() {
		Connection connection = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet tables = metaData.getTables(null, null, HADatabaseKeyManager.HA_TABLE, null);
			return tables.next();
		} catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage());
			}
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to check the existance of HA keys table, Reason: " + e.getMessage());
		} finally {
			DBUtility.closeQuietly(connection);
		}
		return false;
	}
	
	protected final HAKeyDetails generateHaKeyDetails(String haIpAddress, WimaxRequest wimaxRequest,
			WimaxResponse wimaxResponse) {
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE," Generating new HA keys for ha-ip: " + haIpAddress) ;
		}
		
		haKeyStatistics.getHAKeyCounter(haIpAddress).incrementKeyCreationCount();

		HAKeyDetails haKeyDetails = new HAKeyDetails();
		byte[] ha_rk_key = WiMAXUtility.generateHA_RK_KEY();
		haKeyDetails.setHa_rk_key(ha_rk_key);
		int ha_rk_spi = WiMAXUtility.generateHA_RK_SPI();
		haKeyDetails.setHa_rk_spi(ha_rk_spi);
		int ha_rk_lifetime_in_seconds = wimaxConfiguration.getHaRkLifetimeInSeconds();
		
		if(wimaxResponse.getSessionTimeoutInSeconds() != null){
			if(ha_rk_lifetime_in_seconds < wimaxResponse.getSessionTimeoutInSeconds()){
				// TODO ask here this is a tricy place
				ha_rk_lifetime_in_seconds = wimaxResponse.getSessionTimeoutInSeconds().intValue();
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE,"HA-RK-Lifetime is less than Session-Timeout, using Session-Timeout value = " + ha_rk_lifetime_in_seconds);
			}
		}
		if(ha_rk_lifetime_in_seconds < 86400){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE,"HA-RK-Lifetime is less than 86400 Seconds(24 Hours)");
		}
		haKeyDetails.setHa_rk_lifetime_in_seconds(ha_rk_lifetime_in_seconds);
		long generationTimeInMillis = wimaxRequest.getRequestReceivedTimeInMillis();
		haKeyDetails.setHa_rk_genereation_time_in_millis(generationTimeInMillis);
		
		return haKeyDetails;
	}

	@Override
	public ConcurrentHashMap<String, HAKeyCounter> getHAKeyStatistics() {
		return haKeyStatistics.getAll(); 
	}
}

