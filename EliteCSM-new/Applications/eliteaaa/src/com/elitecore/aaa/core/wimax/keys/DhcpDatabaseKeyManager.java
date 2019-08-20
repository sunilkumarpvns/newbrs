package com.elitecore.aaa.core.wimax.keys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;

class DhcpDatabaseKeyManager extends DhcpKeyManagerImpl {
	private static final String LOCK_TABLE_TBLMDHCPKEYS_IN_EXCLUSIVE_MODE = "LOCK TABLE TBLMDHCPKEYS IN EXCLUSIVE MODE";
	private static final String MODULE = "DHCP-DB-KEY-MGR";
	static final String DHCP_TABLENAME = "TBLMDHCPKEYS";

	private DhcpDatabaseAccessObject dbDAO;
	private DhcpInMemoryAccessObject inMemoryCache;

	public DhcpDatabaseKeyManager(DHCPKeysConfiguration dhcpKeysConfiguration, WimaxConfiguration wimaxConfiguration) {
		super(dhcpKeysConfiguration, wimaxConfiguration);
		dbDAO = new DhcpDatabaseAccessObject(wimaxConfiguration);
		inMemoryCache = new DhcpInMemoryAccessObject();
	}

	@Override
	@Nonnull
	public DhcpKeys getOrCreateDhcpKeyDetails(String dhcpIpAddress,
			long currentTimeInMillis) throws Exception {

		List<DhcpKeys> dhcpKeyDetailsList = getDhcpKeyDetails(dhcpIpAddress);

		if (dhcpKeyDetailsList != null) {
			DhcpKeys dhcpKeys = getDhcpKeyDetailsWithHighestLifetime(currentTimeInMillis, 
					dhcpKeyDetailsList);
			if (dhcpKeys != null) {
				return dhcpKeys;
			}
		}
		return createKeysExclusively(dhcpIpAddress, currentTimeInMillis);
		
	}

	private synchronized DhcpKeys createKeysExclusively(String dhcpIpAddress,
			long currentTimeInMillis) throws Exception {
		
		/// I have the local lock now
		// some other thread just created keys
		List<DhcpKeys> dhcpKeyDetailsList = getDhcpKeyDetails(dhcpIpAddress);

		// cached keys found
		if (dhcpKeyDetailsList != null) {
			DhcpKeys dhcpKeys = getDhcpKeyDetailsWithHighestLifetime(currentTimeInMillis, 
					dhcpKeyDetailsList);
			if (dhcpKeys != null) {
				return dhcpKeys;
			}
		}
		// no key found
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "DHCP Key details not found in DB as well as in cache for dhcp-ip: " + dhcpIpAddress 
					+ ". Trying to acquire exclusive lock to create new keys.");
		}		

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(LOCK_TABLE_TBLMDHCPKEYS_IN_EXCLUSIVE_MODE);
			preparedStatement.executeUpdate();

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Acquired exclusive lock to create new keys for dhcp-ip: " + dhcpIpAddress);
			}
			
			/// I have the distributed lock
			dhcpKeyDetailsList = dbDAO.get(dhcpIpAddress, connection);
			
			if (dhcpKeyDetailsList != null) {
				// while I got the lock somebody else inserted a newly generated key
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "DHCP Key details found in DB for dhcp-ip: " + dhcpIpAddress 
							+ ". saved keys in memory.");
				}
				// saving in cache
				inMemoryCache.save(dhcpIpAddress, dhcpKeyDetailsList);
				return getDhcpKeyDetailsWithHighestLifetime(currentTimeInMillis, dhcpKeyDetailsList);
			}
			
			// I am the master, can create new key
			DhcpKeys dhcpKey = generateDhcpKey(dhcpIpAddress, currentTimeInMillis);
			dbDAO.save(dhcpIpAddress, dhcpKey, connection);

			dhcpKeyDetailsList = new ArrayList<DhcpKeys>(); 
			dhcpKeyDetailsList.add(dhcpKey);
			inMemoryCache.save(dhcpIpAddress, dhcpKeyDetailsList);

			// release the lock gracefully
			connection.commit();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "New DHCP keys for dhcp-ip: " + dhcpIpAddress 
						+ " are generated successfully and also cached in memory and saved in DB ");
			}
			return dhcpKey;
		} catch (DataSourceException ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " 
						+ EliteAAADBConnectionManager.ELITE_AAADB_CACHE + 
						" is unavailable, Reason: " + ex.getMessage());
			}
			throw ex;
		} catch (SQLException ex) {
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().trace(MODULE, "Failed to create new keys exclusively for dhcp-ip: " 
				+ dhcpIpAddress + ", Reason: " + ex.getMessage());
			throw ex;
		} finally {
			DBUtility.closeQuietly(preparedStatement);
			// release lock forcefully
			try {
				connection.rollback();
			} catch (SQLException e) {
				LogManager.getLogger().trace(e);
				LogManager.getLogger().trace(MODULE, "Unable to rollback transaction for dhcp-ip: " + 
				dhcpIpAddress + ", Reason: " + e.getMessage());
			}
			DBUtility.closeQuietly(connection);
		}
	}

	private synchronized List<DhcpKeys> getDBKeyDetails(String dhcpIpAddress) throws Exception {

		//if key is created by other thread
		List<DhcpKeys> dhcpKeyDetailsList = inMemoryCache.get(dhcpIpAddress);

		// cached key found
		if (dhcpKeyDetailsList != null) {
			return dhcpKeyDetailsList;
		}

		// key not found --> get from DB
		dhcpKeyDetailsList = dbDAO.get(dhcpIpAddress);
		if (dhcpKeyDetailsList != null) {

			// save in cache
			inMemoryCache.save(dhcpIpAddress, dhcpKeyDetailsList);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "DHCP Key details not found in cache and found in DB. " +
						"So, saving it in cache for dhcp-ip: " + dhcpIpAddress);
			}
		}
		return dhcpKeyDetailsList;
	}


	@Override
	public DhcpKeys getDhcpKeyDetails(String dhcpIpAddress, int dhcpKeyId,
			long currentTimeInMillis) {
		try {
			return getDhcpKeyForRKId(dhcpKeyId, currentTimeInMillis, getDhcpKeyDetails(dhcpIpAddress));
		} catch (Exception e) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Error occured while getting DHCP Keys for IP-Address: " 
						+ dhcpIpAddress + ", Reason: " + e.getMessage());
			}
			return null;
		}
	}

	@Override
	public boolean removeDHCPKey(String dhcpIP) {
		return inMemoryCache.delete(dhcpIP) | dbDAO.delete(dhcpIP);
	}

	@Override
	@Nullable
	public List<DhcpKeys> getDhcpKeyDetails(String dhcpIpAddress) throws Exception {
		List<DhcpKeys> dhcpKeyDetailsList = inMemoryCache.get(dhcpIpAddress);

		if (dhcpKeyDetailsList != null) {
			return dhcpKeyDetailsList;
		}
		return getDBKeyDetails(dhcpIpAddress);
	}

	@Override
	protected void removeExpiredKeys() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Starting the removal of expired DHCP keys");
		}	
		inMemoryCache.deleteAllExpiredKeys();
	}

}
