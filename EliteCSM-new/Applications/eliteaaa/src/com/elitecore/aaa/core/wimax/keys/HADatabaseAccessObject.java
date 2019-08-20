package com.elitecore.aaa.core.wimax.keys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class HADatabaseAccessObject {
	
	private static final String MODULE = "HA-KEYS-DB-DAO";
	
	public HAKeyDetails get(String haIpAddress) throws Exception {
		Connection connection = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			return get(haIpAddress, connection);
		} catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage());
			}
			throw e;
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}
	
	public HAKeyDetails get(String haIpAddress, Connection connection) throws Exception {
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM TBLMHAKEYS WHERE HAIPADDRESS = ? " 
						+ "ORDER BY GENERATIONTIME DESC");
			preparedStatement.setString(1, haIpAddress);
			result = preparedStatement.executeQuery();
			if (result.next() == false) {
				return null;
			}
			
			return deserializeFrom(result);
		}  catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to fetch HA keys for ha-ip: "
					+ haIpAddress + ", Reason: " + e.getMessage());
			throw e;
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(preparedStatement);
		}
	}

	public boolean delete(String haIpAddress) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			statement = connection.prepareStatement("DELETE FROM TBLMHAKEYS WHERE HAIPADDRESS = ?");
			statement.setString(1, haIpAddress);
			int rowsAffected = statement.executeUpdate();
			connection.commit();
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, rowsAffected + " row(s) deleted for ha-ip-address: " + haIpAddress);
			}
			return true;
		} catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage());
			}
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to delete HA keys for ha-ip: " + haIpAddress + ", Reason: " + e.getMessage());
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
		}
		return false;
	}

	public void save(String haIpAddress, HAKeyDetails details, Connection connection) throws Exception {
		LogManager.getLogger().debug(MODULE, "Saving HA key details in DB for HA IP Address: " + haIpAddress);
	
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO TBLMHAKEYS (HAIPADDRESS, LIFETIME, SPI, GENERATIONTIME, KEY) VALUES (?,?,?,?,?)");
			preparedStatement.setString(1, haIpAddress);
			preparedStatement.setLong(2, details.getHa_rk_lifetime_in_seconds());
			preparedStatement.setInt(3, details.getHa_rk_spi());
			preparedStatement.setLong(4, details.getHa_rk_genereation_time_in_millis());
			preparedStatement.setString(5, RadiusUtility.bytesToHex(details.getHa_rk_key()));
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().trace(MODULE, "Unable to save HA keys for ha-ip: " 
					+ haIpAddress + ", Reason: " + e.getMessage());
			throw e;
		} finally {
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
	
	HAKeyDetails deserializeFrom(ResultSet result) throws SQLException {
		HAKeyDetails haKeyDetails = new HAKeyDetails();
		haKeyDetails.setHa_rk_genereation_time_in_millis(result.getLong("GENERATIONTIME"));
		haKeyDetails.setHa_rk_key(RadiusUtility.getBytesFromHexValue(result.getString("KEY")));
		haKeyDetails.setHa_rk_lifetime_in_seconds(result.getInt("LIFETIME"));
		haKeyDetails.setHa_rk_spi(result.getInt("SPI"));
		return haKeyDetails;
	}
}
