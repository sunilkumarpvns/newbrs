package com.elitecore.aaa.core.wimax.keys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;

class HADatabaseKeyManager extends HAKeyManagerImpl {
	private static final String MODULE = "WIMAX-DB-KEY-MGR";
	static final String HA_TABLE = "TBLMHAKEYS";
	
	HADatabaseAccessObject dbDAO = new HADatabaseAccessObject();
	HAInMemoryAccessObject inMemoryCache = new HAInMemoryAccessObject(haKeyStatistics);
	
	public HADatabaseKeyManager(WimaxConfiguration wimaxConfiguration) {
		super(wimaxConfiguration);
	}

	@Override
	public HAKeyDetails getOrCreateHaKeyDetails(String ha_Ip_address,
			WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) throws Exception {
		
		HAKeyDetails haKeyDetails = getHaKeyDetails(ha_Ip_address);
		
		if (haKeyDetails == null) {
			haKeyDetails = createKeysExclusively(ha_Ip_address,
					wimaxRequest, wimaxResponse);
		} else if (haKeyDetails.isKeyExpired(wimaxRequest.getRequestReceivedTimeInMillis())) {
			haKeyDetails = checkAndCreateNewKeysIfExpiredExclusively(ha_Ip_address,
					wimaxRequest, wimaxResponse);
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE," Keys already cached for " + ha_Ip_address) ;
			}
		}
		return haKeyDetails;
	}

	private synchronized HAKeyDetails checkAndCreateNewKeysIfExpiredExclusively(String ha_Ip_address,
			WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) throws Exception {
		
		/// I have the local lock
		HAKeyDetails retriedHaKeyDetails = getInMemoryKeyDetails(ha_Ip_address);
		// some thread cached new key
		if (retriedHaKeyDetails != null
				&& retriedHaKeyDetails.isKeyExpired(wimaxRequest.getRequestReceivedTimeInMillis()) == false) {
			return retriedHaKeyDetails;
		}
		
		// other AAA cached new key in DB
		retriedHaKeyDetails = dbDAO.get(ha_Ip_address);
		if (retriedHaKeyDetails != null
				&& retriedHaKeyDetails.isKeyExpired(wimaxRequest.getRequestReceivedTimeInMillis()) == false) {
			// saving to local cache
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "New HA-RK-Keys found from DB for ha-ip: " + ha_Ip_address
						+ ". Saving in local cache.");
			}
			inMemoryCache.save(ha_Ip_address, retriedHaKeyDetails);
			return retriedHaKeyDetails;
		}
		
		// still expired
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "HA-RK-Keys are expired for ha-ip: " + ha_Ip_address
					+ ". Trying to acquire exclusive lock to create new keys.");
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement("SELECT * FROM TBLMHAKEYS WHERE HAIPADDRESS = ? ORDER BY GENERATIONTIME DESC FOR UPDATE");
			preparedStatement.setString(1, ha_Ip_address);
			result = preparedStatement.executeQuery();
			
			/// I have the distributed lock
			if (result.next() == false) {
				return createKeysExclusively(ha_Ip_address, wimaxRequest, wimaxResponse);
			}
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Acquired exclusive lock to create new keys for ha-ip: " + ha_Ip_address);
			}

			HAKeyDetails haKeyDetails = dbDAO.deserializeFrom(result);
			if (haKeyDetails.isKeyExpired(wimaxRequest.getRequestReceivedTimeInMillis()) == false) {
				// while I got the lock somebody else inserted a newly generated key
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Acquired exclusive lock to create new keys for ha-ip: " + ha_Ip_address
							+ ", but got a new key from DB may have been created by other AAA while waiting for lock");
				}
				inMemoryCache.save(ha_Ip_address, haKeyDetails);
				return haKeyDetails;
			}
			
			// I am the master, can create new key
			haKeyDetails = generateHaKeyDetails(ha_Ip_address, wimaxRequest, wimaxResponse);
			dbDAO.save(ha_Ip_address, haKeyDetails, connection);
			inMemoryCache.save(ha_Ip_address, haKeyDetails);
			
			//release acquired lock gracefully
			connection.commit();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "New HA keys for ha-ip: " + ha_Ip_address 
						+ " are generated successfully and also cached in memory and saved in DB ");
			}
			return haKeyDetails;
		} catch (DataSourceException ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + ex.getMessage());
			}
		} catch (SQLException ex) {
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().trace(MODULE, "Failed to create new keys on expiry for ha-ip: " + ha_Ip_address + ", Reason: " + ex.getMessage());
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(preparedStatement);
			// release lock forcefully
			try {
				connection.rollback();
			} catch (SQLException e) {
				LogManager.getLogger().trace(e);
				LogManager.getLogger().trace(MODULE, "Unable to rollback transaction for ha-ip: " + ha_Ip_address + ", Reason: " + e.getMessage());
			}
			DBUtility.closeQuietly(connection);
		}
		return null;
	}

	private synchronized HAKeyDetails createKeysExclusively(String ha_Ip_address,
			WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) throws Exception {
		
		/// I have the local lock
		final HAKeyDetails retriedHaKeyDetails = getHaKeyDetails(ha_Ip_address);
		if (retriedHaKeyDetails != null) {
			return retriedHaKeyDetails;
		}
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "HA Key details not found in DB as well as in cache for ha-ip: " + ha_Ip_address 
					+ ". Trying to acquire exclusive lock to create new keys.");
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement("LOCK TABLE TBLMHAKEYS IN EXCLUSIVE MODE");
			preparedStatement.executeUpdate();

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Acquired exclusive lock to create new keys for ha-ip: " + ha_Ip_address);
			}
			
			/// I have the distributed lock
			HAKeyDetails haKeyDetails = dbDAO.get(ha_Ip_address, connection);

			if (haKeyDetails != null) {
				// while I got the lock somebody else inserted a newly generated key
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "HA Key details found in DB for ha-ip: " + ha_Ip_address 
							+ ". So, saving in local cache.");
				}
				inMemoryCache.save(ha_Ip_address, haKeyDetails);
				return haKeyDetails;
			}
			
			// I am the master, can create new key
			haKeyDetails = generateHaKeyDetails(ha_Ip_address, wimaxRequest, wimaxResponse);
			dbDAO.save(ha_Ip_address, haKeyDetails, connection);
			inMemoryCache.save(ha_Ip_address, haKeyDetails);

			// release the lock gracefully
			connection.commit();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "New HA keys for ha-ip: " + ha_Ip_address 
						+ " are generated successfully and also cached in memory and saved in DB ");
			}
			return haKeyDetails;
		} catch (DataSourceException ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + ex.getMessage());
			}
		} catch (SQLException ex) {
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().trace(MODULE, "Failed to create new keys exclusively for ha-ip: " 
				+ ha_Ip_address + ", Reason: " + ex.getMessage());
		} finally {
			DBUtility.closeQuietly(preparedStatement);
			// release lock forcefully
			try {
				connection.rollback();
			} catch (SQLException e) {
				LogManager.getLogger().trace(e);
				LogManager.getLogger().trace(MODULE, "Unable to rollback transaction for ha-ip: " + ha_Ip_address + ", Reason: " + e.getMessage());
			}
			DBUtility.closeQuietly(connection);
		}
		return null;
	}

	@Override
	public boolean removeHAKey(String string) {
		// Don't need short circuit here. So don't change this
		return inMemoryCache.delete(string) | dbDAO.delete(string);
	}

	@Override
	@Nullable
	public HAKeyDetails getHaKeyDetails(String haIp) throws Exception {
		HAKeyDetails haKeyDetails = getInMemoryKeyDetails(haIp);
		if (haKeyDetails != null) {
			return haKeyDetails;
		}
		
		// In Memory Key not found --> try to fetch from DB
		haKeyDetails = getDBKeyDetails(haIp);
		
		return haKeyDetails;
	}

	private synchronized HAKeyDetails getDBKeyDetails(String haIp) throws Exception {
		
		//if key is created by other thread
		HAKeyDetails haKeyDetails = getInMemoryKeyDetails(haIp);
		
		// cached key found
		if (haKeyDetails != null) {
			return haKeyDetails;
		}
		
		// key not found --> get from DB
		haKeyDetails = dbDAO.get(haIp);
		if (haKeyDetails != null) {
			
			// save in cache
			inMemoryCache.save(haIp, haKeyDetails);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "HA Key details not found in cache and found in DB. " +
						"So, saving it in cache for ha-ip: " + haIp);
			}
		}
		return haKeyDetails;
	}

	private HAKeyDetails getInMemoryKeyDetails(String haIp) {
		return inMemoryCache.get(haIp);
	}
}
