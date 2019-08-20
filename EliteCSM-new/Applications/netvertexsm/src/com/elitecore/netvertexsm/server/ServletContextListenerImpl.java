package com.elitecore.netvertexsm.server;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.PasswordUtility;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.*;
import java.util.UUID;

public class ServletContextListenerImpl implements ServletContextListener {

	private static final String MODULE = "SERVLET-CNTX-LSTR";
	private final int ONE_MINUTES_IN_MILLISECONDS = 60000;
	private final int THIRTY_SECONDS = 30000;

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		try{
			EliteScheduler.getInstance().shutdown();
		}catch(Throwable t){
			Logger.logError(MODULE, "Error while stopping Elite Schedular. Reason: " + t.getMessage());
			Logger.logTrace(MODULE, t);
		}
		/*try{
			EliteAddOnCache.getInstance().stop();
		}catch(Throwable t){
			Logger.logError(MODULE, "Error while stopping Add On Cache. Reason: " + t.getMessage());
			Logger.logTrace(MODULE, t);
		}*/

		try {
			EliteScheduler.getInstance().shutdown();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while closing Scheduler. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		try {
			DBConnectionManager.getInstance().closeAllDatasource();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while closing data sources. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);

		}

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try{
			String contextAbsolutePath = servletContextEvent.getServletContext().getRealPath("");
			EliteUtility.setSMHome(contextAbsolutePath);
			String contextPath = servletContextEvent.getServletContext().getContextPath();
			EliteUtility.setContextPath(contextPath);
			HibernateSessionFactory.setDBPropsLocation(contextAbsolutePath+"/WEB-INF/database.properties");

			//WSConfig.init();
			DBConnectionManager.getInstance().initSMDatasurce();
			readFieldsForPasswordEncryption(servletContextEvent);
			DBConnectionManager.getInstance().initSessionLookupDataSource();
			/*DBConnectionManager.getInstance().initDynamicSPRDatasurce();
				        DBConnectionManager.getInstance().initUsageMonitringDatasurce();
				        DBConnectionManager.getInstance().initSubscriberProfileDatasurce();
				        EliteAddOnCache.getInstance().init();*/



			String pdContextPath = servletContextEvent.getServletContext().getInitParameter("pdContextPath");
			if(Strings.isNullOrBlank(pdContextPath)){
				return ;
			}
			ServletContext pdContext = servletContextEvent.getServletContext().getContext("/"+pdContextPath);
			if(pdContext==null){
				return ;
			}
			pdContext.setAttribute("smContexPathFromCtxInit", servletContextEvent.getServletContext().getContextPath());

		}catch(Throwable t){
			Logger.logError(MODULE, "Error while initializing servlet Context. Reason: " + t.getMessage());
			Logger.logTrace(MODULE, t);
		}
	}

	/**
	 * This method will be use to encrypt plain text password for all the tables which are defined in the TBLM_PASSWORD_ENCRYPT_STATUS Table
	 * @param servletContextEvent
	 */
	private void readFieldsForPasswordEncryption(ServletContextEvent servletContextEvent) throws SQLException, LoadConfigurationException, InterruptedException, InitializationFailedException {
		Connection connection = null;
		boolean isProcessCompleted = false;

		try {
			connection = DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection == null) {
				throw new LoadConfigurationException("No connection available while reading password fields for encryption");
			}

			while(isProcessCompleted == false) {
				String uuid = UUID.randomUUID().toString();

				if (updateUUIDAndModifiedDate(connection, uuid) == false) {
					int counterForOneMinute = 0;

					while (counterForOneMinute < ONE_MINUTES_IN_MILLISECONDS) {
                        if(isEncryptionIsCompleted(connection)) {
							isProcessCompleted = true;
							break;
                        } else {
							counterForOneMinute = counterForOneMinute + 100;
							Thread.sleep(100);
                        }
                    }
				} else {
					processToEncryptPassword(connection, servletContextEvent);
					isProcessCompleted = true;
				}
			}


		} catch (SQLException e) {
			Logger.logError(MODULE, "Error in SQL Operation. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;

		} catch (LoadConfigurationException e) {
			Logger.logError(MODULE, "Error in configuration. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;

		} catch (InterruptedException e) {
			Logger.logError(MODULE, "Error occurred due to interrupting. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;

		} catch (InitializationFailedException e) {
			Logger.logError(MODULE, "Error occurred while initializing NetVertex Context. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;
		}finally {
			DBUtility.closeQuietly(connection);
		}

	}


	private void processToEncryptPassword(Connection connection,ServletContextEvent servletContextEvent) throws SQLException, InitializationFailedException {

		final String SELECT_MODULE = "SELECT MODULE, STATUS FROM TBLM_PASSWORD_ENCRYPT_STATUS";
		final String TBLMNETCONFIGURATIONVALUES_TABLE = "TBLMNETCONFIGURATIONVALUES";
		final String PENDING = "PENDING";
		PreparedStatement preparedStatement = null;
		ResultSet resultSetForSelect = null;
		long startTime = System.currentTimeMillis();
		try {

			preparedStatement = connection.prepareStatement(SELECT_MODULE,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSetForSelect = preparedStatement.executeQuery();
			while (resultSetForSelect.next()) {
				String module = resultSetForSelect.getString("MODULE");
				String status = resultSetForSelect.getString("STATUS");
				if (status.equals(PENDING)) {
					if (TBLMNETCONFIGURATIONVALUES_TABLE.equals(module)) {
						updateServerAndServiceConfigurationPasswords(connection,startTime);
						servletContextEvent.getServletContext().setAttribute("SyncTo", true);
					} else {
						updatePasswords(connection,module,startTime);
					}
					resultSetForSelect.updateString("STATUS","COMPLETED");
					resultSetForSelect.updateRow();
					connection.commit();
				}
			}
		} finally {
			DBUtility.closeQuietly(resultSetForSelect);
			DBUtility.closeQuietly(preparedStatement);
		}
	}

	/**
	 * This method will used to update UUID and Modified date based on defined conditions
	 * @param connection
	 * @param uuid
	 * @throws SQLException
	 */
	private boolean updateUUIDAndModifiedDate(Connection connection, String uuid) throws SQLException {
		Logger.logInfo(MODULE, "Updating UUID and modified date");
		final String UPDATE_UUID = "UPDATE TBLM_PASSWORD_ENCRYPT_STATUS SET SM_INSTANCE_ID = ? , MODIFIED_DATE = CURRENT_TIMESTAMP  WHERE (STATUS = 'PENDING') AND (MODIFIED_DATE IS NULL OR MODIFIED_DATE < (current_timestamp - numToDSInterval( 60, 'second' )))";
		final String SELECT_BY_UUID =  "SELECT * FROM TBLM_PASSWORD_ENCRYPT_STATUS WHERE SM_INSTANCE_ID = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(UPDATE_UUID);
			preparedStatement.setString(1, uuid);
			preparedStatement.executeUpdate();
			connection.commit();
		} finally {
			DBUtility.closeQuietly(preparedStatement);
		}

		PreparedStatement preparedStatementForSelect = null ;
		ResultSet resultSetForSelect = null;
		try {
			preparedStatementForSelect = connection.prepareStatement(SELECT_BY_UUID);
			preparedStatementForSelect.setString(1, uuid);
			resultSetForSelect = preparedStatementForSelect.executeQuery();

			return resultSetForSelect.next();
		} finally {
			DBUtility.closeQuietly(resultSetForSelect);
			DBUtility.closeQuietly(preparedStatementForSelect);
		}
	}

	/**
	 * This method will be used to check table is reserved or completed by other server or not.
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	private boolean isEncryptionIsCompleted(Connection connection) throws SQLException, InterruptedException {
		final String SELECT_BY_STATUS = "SELECT * FROM TBLM_PASSWORD_ENCRYPT_STATUS WHERE STATUS = 'PENDING'";
		PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_STATUS);
		ResultSet resultSet = preparedStatement.executeQuery();
		try {
			if(resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}

	}


	/**
	 * This Method is used to encrypt password.
	 * @param connection
	 * @param module
	 * @throws SQLException
	 */
	private void updatePasswords(Connection connection , String module, long startTime) throws SQLException, InitializationFailedException {

		String moduleQuery = "SELECT PASSWORD FROM " + module;
		ResultSet resultSetUpdate = null;
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		try {
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSetUpdate = statement.executeQuery(moduleQuery);


			while (resultSetUpdate.next()) {
				if((System.currentTimeMillis() - startTime) < THIRTY_SECONDS) {
					String password = resultSetUpdate.getString("PASSWORD");
					resultSetUpdate.updateString("PASSWORD", PasswordUtility.getEncryptedPassword(password));
					resultSetUpdate.updateRow();
				}else {
					String updateQuery = "UPDATE TBLM_PASSWORD_ENCRYPT_STATUS SET SM_INSTANCE_ID = NULL, MODIFIED_DATE = NULL WHERE STATUS != 'COMPLETED'";
					preparedStatement = connection.prepareStatement(updateQuery,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					preparedStatement.executeUpdate();
					connection.commit();

					throw new InitializationFailedException("Halting the context startup. Reason: Time taken to encrypt password is high");
				}
			}

		} catch (NoSuchEncryptionException e) {
			Logger.logError(MODULE, "Error while encrypt string. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (EncryptionFailedException e) {
			Logger.logError(MODULE, "Error while encrypt string. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} finally {
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(resultSetUpdate);
			DBUtility.closeQuietly(statement);
		}
	}

	/**
	 * This Method is used to encrypt password of 'TBLMNETCONFIGURATIONVALUES' table
	 * @param connection
	 * @throws SQLException
	 */
	private void updateServerAndServiceConfigurationPasswords(Connection connection, long startTime) throws SQLException, InitializationFailedException {

		String query = "SELECT VALUE from TBLMNETCONFIGURATIONVALUES WHERE NETCONFIGID IN  ('CNF0001','CNF0012') AND PARAMETERID IN ('CPM000009','CPM000409','CPM000207','CPM000394')";
		ResultSet resultSetForUpdate = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementForUpdate = null;

		try {
			preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSetForUpdate = preparedStatement.executeQuery();


			while (resultSetForUpdate.next()) {
				if ((System.currentTimeMillis() -startTime) < THIRTY_SECONDS) {
					String password = resultSetForUpdate.getString("VALUE");

					resultSetForUpdate.updateString("VALUE", PasswordUtility.getEncryptedPassword(password));
					resultSetForUpdate.updateRow();
				}  else {
					String updateQuery = "UPDATE TBLM_PASSWORD_ENCRYPT_STATUS SET SM_INSTANCE_ID = NULL, MODIFIED_DATE = NULL WHERE STATUS != 'COMPLETED'";
					preparedStatementForUpdate = connection.prepareStatement(updateQuery,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					preparedStatementForUpdate.executeUpdate();
					connection.commit();

					throw new InitializationFailedException("Halting the context startup. Reason: Time taken to encrypt password is high");
				}
			}

		} catch (NoSuchEncryptionException e) {
			Logger.logError(MODULE, "Error while encrypt string. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (EncryptionFailedException e) {
			Logger.logError(MODULE, "Error while encrypt string. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally {
			DBUtility.closeQuietly(preparedStatementForUpdate);
			DBUtility.closeQuietly(resultSetForUpdate);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
}