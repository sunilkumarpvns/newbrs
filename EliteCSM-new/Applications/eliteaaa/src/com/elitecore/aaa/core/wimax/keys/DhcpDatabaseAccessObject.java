package com.elitecore.aaa.core.wimax.keys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class DhcpDatabaseAccessObject {
	
	private static final String MODULE = "DHCP-KEYS-DB-DAO";

	private static final String SELECT_FROM_DHCPKEYS = "SELECT * FROM TBLMDHCPKEYS WHERE DHCPIPADDRESS = ? AND GENERATIONTIME > ? ORDER BY GENERATIONTIME DESC";
	private static final String INSERT_INTO_TBLMDHCPKEYS = "INSERT INTO TBLMDHCPKEYS (DHCPIPADDRESS, DHCPKEY, RKID, GENERATIONTIME) VALUES (?,?,?,?)";
	private static final String DELETE_FROM_TBLMDHCPKEYS = "DELETE FROM TBLMDHCPKEYS WHERE DHCPIPADDRESS = ?";
	
	private static final String GENERATIONTIME = "GENERATIONTIME";
	private static final String RKID = "RKID";
	private static final String DHCPKEY = "DHCPKEY";

	private final long dhcpRkLifetimeInSeconds;
	
	DhcpDatabaseAccessObject(WimaxConfiguration wimaxConfiguration) {
		this.dhcpRkLifetimeInSeconds = wimaxConfiguration.getDhcpRkLifetimeInSeconds();
	}

	public List<DhcpKeys> get(String dhcpIpAddress) throws Exception {
		Connection connection = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			return get(dhcpIpAddress, connection);
		} catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Connection to datasource: " 
						+ EliteAAADBConnectionManager.ELITE_AAADB_CACHE 
						+ " is unavailable, Reason: " + e.getMessage());
			}
			throw e;
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}
	
	public List<DhcpKeys> get(String dhcpIpAddress, Connection connection) throws Exception {
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			preparedStatement = connection.prepareStatement(SELECT_FROM_DHCPKEYS);
			preparedStatement.setString(1, dhcpIpAddress);
			preparedStatement.setLong(2, (System.currentTimeMillis() - (dhcpRkLifetimeInSeconds*1000)));
			result = preparedStatement.executeQuery();
			if (result.next() == false) {
				return null;
			}
			return deserializeFrom(result);
		}  catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to fetch Dhcp keys for dhcp-ip: "
					+ dhcpIpAddress + ", Reason: " + e.getMessage());
			throw e;
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(preparedStatement);
		}
	}

	public boolean delete(String dhcpIpAddress) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			statement = connection.prepareStatement(DELETE_FROM_TBLMDHCPKEYS);
			statement.setString(1, dhcpIpAddress);
			int rowsAffected = statement.executeUpdate();
			connection.commit();
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, rowsAffected + " row(s) deleted for dhcp-ip-address: " + dhcpIpAddress);
			}
			return true;
		} catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage());
			}
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to delete Dhcp keys for dhcp-ip: " + dhcpIpAddress + ", Reason: " + e.getMessage());
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
		}
		return false;
	}

	public void save(String dhcpIpAddress, DhcpKeys dhcpKey, Connection connection) throws Exception {
		LogManager.getLogger().debug(MODULE, "Saving DHCP key details in DB for DHCP IP Address: " + dhcpIpAddress);
	
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			preparedStatement = connection.prepareStatement(INSERT_INTO_TBLMDHCPKEYS);
			preparedStatement.setString(1, dhcpIpAddress);
			preparedStatement.setString(2, RadiusUtility.bytesToHex(dhcpKey.getDhcp_rk()));
			preparedStatement.setInt(3, dhcpKey.getDhcp_rk_id());
			preparedStatement.setLong(4, dhcpKey.getDhcp_rk_genereation_time_in_millis());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to save DHCP keys for dhcp-ip: " 
					+ dhcpIpAddress + ", Reason: " + e.getMessage());
			throw e;
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
	
	private List<DhcpKeys> deserializeFrom(ResultSet result) throws SQLException {
		
		List<DhcpKeys> dhcpKeyList = new ArrayList<DhcpKeys>();
		dhcpKeyList.add(new DhcpKeys(
				RadiusUtility.getBytesFromHexValue(result.getString(DHCPKEY)), 
				result.getInt(RKID), 
				dhcpRkLifetimeInSeconds, 
				result.getLong(GENERATIONTIME)));
		while (result.next()) {
			dhcpKeyList.add(new DhcpKeys(
					RadiusUtility.getBytesFromHexValue(result.getString(DHCPKEY)), 
					result.getInt(RKID), 
					dhcpRkLifetimeInSeconds, 
					result.getLong(GENERATIONTIME)));
		}
		return dhcpKeyList;
	}

}
