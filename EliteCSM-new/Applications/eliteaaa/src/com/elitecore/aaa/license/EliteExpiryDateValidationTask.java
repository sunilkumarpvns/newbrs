package com.elitecore.aaa.license;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.license.base.MultiLicenseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Server level scheduled task for performing server internal tasks. Current
 * implementation validates server license at regular interval and if 
 * license is found invalid, it stops all the active services.
 * 
 * @author vicky.singh
 *
 */
public class EliteExpiryDateValidationTask extends BaseIntervalBasedTask {
	private static final String MODULE = "ExpiryDate Validator";
	private static final String UPDATE_EXPIRY_QUERY_STRING = "UPDATE TBLMNETSERVERINSTANCE SET LICENSEEXPIRYDAYS=?,LICENSECHECKDATE=CURRENT_TIMESTAMP WHERE NETSERVERCODE=?";
	
	private final long initialDelay = 60; //Initial delay is kept at 60 so that the first check occurs after the server has completed it's startup process
	private final long intervalSeconds = 3600;

	protected boolean isValidLicence = true;
	protected final LicenseExpiryListener expiryListener;
	protected final ServerContext serverContext;
	protected final MultiLicenseManager licenseManager;

	public EliteExpiryDateValidationTask(AAAServerContext serverContext, LicenseExpiryListener expiryListener, MultiLicenseManager licenseManager){
		this.serverContext = serverContext;
		this.expiryListener = expiryListener;
		this.licenseManager = licenseManager;
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
		return (int)intervalSeconds;
	}


	@Override
	public void execute(AsyncTaskContext context) {
		if(isValidLicence) {
			if(!this.serverContext.isLicenseValid("SYSTEM_EXPIRY",String.valueOf(System.currentTimeMillis()))){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().warn(MODULE,"Stopping all services, licence expiry detected");
				expiryListener.execute();
				isValidLicence = false;
			}
		}

		if(!isValidLicence){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE,"License for EliteAAA server instance expired, kindly renew the license");
			this.serverContext.generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_EXPIRED, MODULE, 
					"License for EliteAAA server instance is expired, kindly renew the license",0,
					"EliteAAA server(renew the license)");
		}
		licenseExpiryCheckAlert();
	}
	
	public void licenseExpiryCheckAlert() {	
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		long remainedDaysToExpireLicense = 0l;

		if(licenseManager != null) {
			try{
				connection = EliteAAADBConnectionManager.getInstance().getConnection();
				remainedDaysToExpireLicense = licenseManager.getRemainedDaysToExpire();
				preparedStatement = connection.prepareStatement(UPDATE_EXPIRY_QUERY_STRING);
				preparedStatement.setLong(1, remainedDaysToExpireLicense);
				preparedStatement.setString(2, serverContext.getServerInstanceId());
				resultSet =  preparedStatement.executeQuery();
				connection.commit();
			} catch (DataSourceException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage());
				}
			}catch(ParseException e){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().trace(MODULE,e.getMessage());
			}catch(SQLException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().trace(MODULE,e.getMessage());
			}catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().trace(MODULE,e.getMessage());
			}finally{
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(connection);
			}
		}
	}
	
}
