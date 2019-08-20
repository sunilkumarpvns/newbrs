
package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.spr.util.SPRUtil.toStringSQLException;

/**
 * {@link NVDBSPInterface} provides profile information from DB. This will used
 * for Local Profile Operation.
 * 
 * @author chetan.sankhala
 */
public class NVDBSPInterface implements SPInterface {

	private static final String SEVERITY = "";
	private static final String MODULE = "NV-DB-SP-INTERFACE";
	private static final String TABLE_NAME = "TBLM_SUBSCRIBER";
	private static final String FETCH_DELETE_MARKED_SUBSCRIBERS = "SELECT * FROM " + TABLE_NAME + " WHERE "
			+ SPRFields.STATUS.columnName + "='" + SubscriberStatus.DELETED.name() + "'";
	private static final String MARK_DELETE_QUERY = "UPDATE " + TABLE_NAME + " SET STATUS = '" + SubscriberStatus.DELETED.name() + "', MODIFIED_DATE= ? WHERE SUBSCRIBERIDENTITY = ?";
	
	private static final String RESTORE_PROFILE_QUERY = "UPDATE " + TABLE_NAME + " SET STATUS = '" + SubscriberStatus.INACTIVE.name() +"', MODIFIED_DATE= ? "
														+ " WHERE SUBSCRIBERIDENTITY = ? AND "+ SPRFields.STATUS.columnName + "='" + SubscriberStatus.DELETED.name() + "'";

	private final AlertListener alertListener;
	private final TransactionFactory transactionFactory;
	private final AtomicInteger totalQueryTimeoutCount;
	private final String insertQuery;
	private final String deleteQuery;

	public NVDBSPInterface(AlertListener alertListener, TransactionFactory transactionFactory) {
		this.alertListener = alertListener;
		this.transactionFactory = transactionFactory;
		this.totalQueryTimeoutCount = new AtomicInteger(0);
		this.insertQuery = buildInsertQuery();
		this.deleteQuery = buildDeleteQuery();
	}

	private String buildDeleteQuery() {
		return new StringBuilder("DELETE FROM ").append(TABLE_NAME).append(" WHERE ")
				.append(SPRFields.SUBSCRIBER_IDENTITY.columnName).append("=? AND ")
				.append(SPRFields.STATUS.columnName).append(" ='").append(SubscriberStatus.DELETED.name()).append("'").toString();
	}

	private String buildInsertQuery() {
		StringBuilder queryBuilder = new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" ").append(CommonConstants.OPENING_PARENTHESES);

		StringBuilder valueBuilder = new StringBuilder(" values ").append(CommonConstants.OPENING_PARENTHESES);

		for (SPRFields field : SPRFields.values()) {
			queryBuilder.append(field.columnName).append(CommonConstants.COMMA).append(" ");
			valueBuilder.append(CommonConstants.QUESTION_MARK).append(CommonConstants.COMMA).append(" ");
		}

		queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
		valueBuilder.delete(valueBuilder.length() - 2, valueBuilder.length());

		queryBuilder.append(CommonConstants.CLOSING_PARENTHESES);
		queryBuilder.append(valueBuilder.toString()).append(CommonConstants.CLOSING_PARENTHESES);

		return queryBuilder.toString();
	}

	@Override
	public @Nullable SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException, DBDownException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching subscriber profile for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		if (transactionFactory.isAlive() == false) {
			throw new DBDownException("Unable to fetch profile for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available");
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();

		if (dbTransaction == null) {
			throw new DBDownException("Unable to fetch profile for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available");
		}

		try {
			dbTransaction.begin();

			if (getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "DB Query for subscriber profile: " + getSelectQuery());
			}

			preparedStatement = dbTransaction.prepareStatement(getSelectQuery());
			preparedStatement.setString(1, subscriberIdentity);
			preparedStatement.setQueryTimeout(getDbQueryTimeout());

			long queryExecutionTime = System.currentTimeMillis();
			resultSet = preparedStatement.executeQuery();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + dbTransaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB Query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " ms.");
			}

			totalQueryTimeoutCount.set(0);

			if (resultSet.next()) {
				long sprReadStartTime = System.currentTimeMillis();
				SPRInfo sprInfo = createProfileData(resultSet);
				long sprReadTime = System.currentTimeMillis() - sprReadStartTime;
				sprInfo.setSprReadTime(sprReadTime);
				sprInfo.setSprLoadTime(queryExecutionTime);
				return sprInfo;
			} else {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Subscriber profile does not exist with subscriber ID: " + subscriberIdentity);
				}
				return null;
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger()
								.warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					dbTransaction.markDead();
					totalQueryTimeoutCount.set(0);

				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while fetching profile from DB with data source name: "
								+ dbTransaction.getDataSourceName());
				
				throw new DBDownException("Error while fetching profile for subscriber ID: "
						+ subscriberIdentity + " from DB with datasource name: " + dbTransaction.getDataSourceName(), e);

			} else if (dbTransaction.isDBDownSQLException(e)) {
				dbTransaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
							+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				}
				
				throw new DBDownException("Error while fetching profile for subscriber ID: "
						+ subscriberIdentity + " from DB with datasource name: " + dbTransaction.getDataSourceName(), e);
			} else {
				throw new OperationFailedException("Error while fetching profile for subscriber ID: "
						+ subscriberIdentity
						+ ". Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
			}

		} catch (TransactionException e) {
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE , MODULE,
						"Unable to fetch profile for subscriber identity: " + subscriberIdentity + " from DB with data source name: "
								+ dbTransaction.getDataSourceName() + ". Reason: Connection not available");
				
				throw new DBDownException("Error while fetching profile for subscriber ID: "
						+ subscriberIdentity + " from DB with datasource name: " + dbTransaction.getDataSourceName(), e);

			} else {
				throw new OperationFailedException("Error while fetching profile for subscriber ID: "
						+ subscriberIdentity+ ". Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
			}

		} catch (Exception e) {
			throw new OperationFailedException("Error while fetching profile for subscriber ID: "
					+ subscriberIdentity + ". Reason: " + e.getMessage(), e);
		} finally {
			closeQuietly(resultSet);
			closeQuietly(preparedStatement);
			endTransaction(dbTransaction);
		}
	}

	protected String getSelectQuery() {
		return "SELECT * FROM " + getTablename() + " WHERE " + SPRFields.SUBSCRIBER_IDENTITY.columnName + " = ?";
	}

	protected String getIdentityField() {
		return SPRFields.SUBSCRIBER_IDENTITY.columnName;
	}

	protected String getTablename() {
		return TABLE_NAME;
	}

	protected int getDbQueryTimeout() {
		return CommonConstants.QUERY_TIMEOUT_DEFAULT;
	}

	protected long getMaxQueryTimeoutCount() {
		return CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT;
	}

	// FIXME. can use Predicate for local and external createProfileData
	protected SPRInfo createProfileData(ResultSet rsForProfile) throws SQLException, OperationFailedException {

		SPRInfoImpl sprInfo = new SPRInfoImpl();

		String stringValue;
		Long numericValue;
		Timestamp timestampValue;
		boolean validate = false;
		for (SPRFields sprField : SPRFields.values()) {

			if (sprField.type == Types.NUMERIC) {
				numericValue = rsForProfile.getLong(sprField.columnName);
				if(rsForProfile.wasNull() == false){
					sprField.setNumericValue(sprInfo, numericValue, validate);
				}
			} else if (sprField.type == Types.VARCHAR) {
				stringValue = rsForProfile.getString(sprField.columnName);
				sprField.setStringValue(sprInfo, stringValue, validate);
			} else if (sprField.type == Types.TIMESTAMP) {
				timestampValue = rsForProfile.getTimestamp(sprField.columnName);
				sprField.setTimestampValue(sprInfo, timestampValue, validate);
			}
		}

		return sprInfo;
	}

	/**
	 * Adds subscriber profile in SPR.
	 * 
	 * @throws OperationFailedException
	 *             when identity field not found from profile or database is
	 *             dead or any DB error occurs
	 * @author chetan
	 */
	@Override
	public void addProfile(SPRInfo sprInfo) throws OperationFailedException {

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to add subscriber profile for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to add subscriber profile for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();

			addProfile(sprInfo, transaction);

		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to add profile for subscriber identity: " + sprInfo.getSubscriberIdentity() + " to DB with Data Source name "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			
			throw new OperationFailedException("Error while adding profile for subscriber ID: "
					+ sprInfo.getSubscriberIdentity() + ". Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			endTransaction(transaction);
		}
	}

	private void setProfileAttributes(PreparedStatement psForInsertProfile, SPRInfo sprInfo, String subscriberIdentity) throws SQLException {

		short columnIndex = 1;
		for (SPRFields sprField : SPRFields.values()) {

			if (sprField.type == Types.NUMERIC) {
				Long numericValue = sprField.getNumericValue(sprInfo);
				if (numericValue == null) {
					psForInsertProfile.setNull(columnIndex++, sprField.type);
				} else {
					psForInsertProfile.setLong(columnIndex++, numericValue);
				}
			} else if (sprField.type == Types.VARCHAR) {

				if (sprField == SPRFields.SUBSCRIBER_IDENTITY) {
					psForInsertProfile.setString(columnIndex++, subscriberIdentity);
				} else {
					psForInsertProfile.setString(columnIndex++, sprField.getStringValue(sprInfo));
				}
			} else if (sprField.type == Types.TIMESTAMP) {
				psForInsertProfile.setTimestamp(columnIndex++, sprField.getTimestampValue(sprInfo));
			}
		}
	}

	@Override
	public int purgeProfile(String subscriberIdentity) throws OperationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Purging subscriber profile for subscriber(" + subscriberIdentity + ")");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + ")"
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + ")"
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			return purgeProfile(subscriberIdentity, transaction);
		} catch (TransactionException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to purge profile for subscriber identity: " + subscriberIdentity + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + ")."
					+ " Reason. " + e.getMessage(), resultCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		} finally {
			endTransaction(transaction);
		}

	}

	private void endTransaction(DBTransaction transaction) {
		try {
			if (transaction != null) {
				transaction.end();
			}
		} catch (TransactionException e) {
			getLogger().trace(MODULE, e);
		}
	}

	@Override
	public int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

		if (getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "DB Query for purge profile: " + deleteQuery);
		}

		PreparedStatement psForDeleteProfile = null;
		try {

			psForDeleteProfile = transaction.prepareStatement(deleteQuery);
			psForDeleteProfile.setString(1, subscriberIdentity);
			psForDeleteProfile.setQueryTimeout(getDbQueryTimeout());

			long queryExecutionTime = System.currentTimeMillis();
			int purgeCount = psForDeleteProfile.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time getting high, Last query execution time = "
							+ queryExecutionTime + " ms.");
				}
			}

			totalQueryTimeoutCount.set(0);

			if (purgeCount > 0) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Subscriber profile purged successfully for subscriber(" + subscriberIdentity + ")");
				}
			} else {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Subscriber profile not purged. Reason. Subscriber(" + subscriberIdentity + ") not found");
				}
			}

			return purgeCount;
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger()
								.warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while purging profile from DB with data source name: "
								+ transaction.getDataSourceName());
				resultCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is down, SPR marked as DEAD. Reason: " + e.getMessage());
				}

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException("Purging Profile", subscriberIdentity, e), resultCode, e);

		} finally {
			closeQuietly(psForDeleteProfile);
		}
	}

	public void addProfile(SPRInfo sprInfo, Transaction transaction) throws OperationFailedException, TransactionException {

		String subscriberIdentity = sprInfo.getSubscriberIdentity();

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to add subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Adding subscriber profile for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement psForInsertProfile = null;

		try {
			psForInsertProfile = transaction.prepareStatement(insertQuery);

			if (getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "Insert query for subscriber profile: " + insertQuery);
			}

			setProfileAttributes(psForInsertProfile, sprInfo, subscriberIdentity);

			psForInsertProfile.setQueryTimeout(getDbQueryTimeout());
			long queryExecutionTime = System.currentTimeMillis();
			psForInsertProfile.execute();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " milliseconds.");
			}

			totalQueryTimeoutCount.set(0);

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber profile added successfully for Subscriber ID: " + subscriberIdentity);
			}
		} catch (SQLException e) {

			ResultCode errorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger()
								.warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while adding profile for subscriber ID: " + subscriberIdentity + " to DB with data source name: "
								+ transaction.getDataSourceName());

				errorCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
						+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				errorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			if (e.getErrorCode() == CommonConstants.DUPLICATE_ENTRY_ERRORCODE) {
				errorCode = ResultCode.ALREADY_EXIST;
			}

			throw new OperationFailedException(toStringSQLException("adding profile", subscriberIdentity, e), errorCode, e);
		} finally {
			closeQuietly(psForInsertProfile);
		}
	}

	@Override
	public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException {

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to mark delete subscriber(" + subscriberIdentity + ")."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to mark delete subscriber(" + subscriberIdentity + ")."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		try {
			transaction.begin();
			return markForDeleteProfile(subscriberIdentity, transaction);
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to mark delete subscriber(" + subscriberIdentity + ") to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			
			throw new OperationFailedException("Unable to mark delete subscriber(" + subscriberIdentity + ")."
					+ " Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			endTransaction(transaction);
		}

	}

	private int markForDeleteProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to mark subscriber profile 'deleted'. Reason: Subscriber Identity: " + subscriberIdentity
					+ " not found from subscriber profile", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Marking subscriber profile as 'deleted' for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement psForMarkDeleteQuery = null;

		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Mark subscriber profile as 'deleted' query: " + MARK_DELETE_QUERY);
			}

			psForMarkDeleteQuery = transaction.prepareStatement(MARK_DELETE_QUERY);
			Timestamp modifiedDate = new Timestamp(System.currentTimeMillis());
			psForMarkDeleteQuery.setTimestamp(1, modifiedDate);
			psForMarkDeleteQuery.setString(2, subscriberIdentity);
			psForMarkDeleteQuery.setQueryTimeout(getDbQueryTimeout());

			long queryExecutionTime = System.currentTimeMillis();
			int updateRowCount = psForMarkDeleteQuery.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " milliseconds");
			}

			totalQueryTimeoutCount.set(0);

			if (getLogger().isInfoLogLevel()) {
				if (updateRowCount > 0) {
					getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") marked 'deleted'");
				} else {
					getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") not marked 'deleted'. Reason. Subscriber not exist");
				}
			}

			return updateRowCount;

		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger()
								.warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while marking subscriber profile as 'deleted' for subscriber ID: " + subscriberIdentity
								+ " to DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				}

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException("marking delete", subscriberIdentity, e), resultCode, e);

		} finally {
			closeQuietly(psForMarkDeleteQuery);
		}
	}

	/**
	 *
	 * @return List, empty list in case of no records found
	 */
	@Override
	public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching delete marked subscribers");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to fetch delete marked subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();

		if (dbTransaction == null) {
			throw new OperationFailedException("Unable to fetch delete marked subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		PreparedStatement psForDeleteMarkedSubcribers = null;
		ResultSet rsForDeleteMarkedSubcribers = null;
		try {
			dbTransaction.begin();

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Query: " + FETCH_DELETE_MARKED_SUBSCRIBERS);
			}

			psForDeleteMarkedSubcribers = dbTransaction.prepareStatement(FETCH_DELETE_MARKED_SUBSCRIBERS);
			psForDeleteMarkedSubcribers.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch watch = new Stopwatch();
			watch.start();
			rsForDeleteMarkedSubcribers = psForDeleteMarkedSubcribers.executeQuery();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + dbTransaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " milliseconds");
			}

			totalQueryTimeoutCount.set(0);

			List<SPRInfo> deleteMarkedSubscribers = new ArrayList<SPRInfo>();

			while (rsForDeleteMarkedSubcribers.next()) {
				deleteMarkedSubscribers.add(createProfileData(rsForDeleteMarkedSubcribers));
			}

			return deleteMarkedSubscribers;
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger()
								.warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					dbTransaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while fetching delete marked subscribers from DB with data source name: "
								+ dbTransaction.getDataSourceName());
				resultCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (dbTransaction.isDBDownSQLException(e)) {
				dbTransaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
							+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				}
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException("fetching delete marked subscribers", e), resultCode, e);

		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to fetch delete marked subscribers to DB with Data Source name: "
								+ dbTransaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException("Unable to fetch delete marked subscribers."
					+ " Reason: " + e.getMessage(), sprErrorCode, e);
		} finally {
			closeQuietly(psForDeleteMarkedSubcribers);
			closeQuietly(rsForDeleteMarkedSubcribers);
			endTransaction(dbTransaction);
		}
	}


	@Override
	public int restoreProfile(String subscriberIdentity) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Restoring subscriber(" + subscriberIdentity + ")");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to restore subscriber(" + subscriberIdentity + ")."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to restore subscriber(" + subscriberIdentity + ")."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			return restoreProfile(subscriberIdentity, transaction);
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to restore subscriber(" + subscriberIdentity + ") to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			throw new OperationFailedException("Unable to restore subscriber(" + subscriberIdentity + ")."
					+ " Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		} finally {
			endTransaction(transaction);
		}
	}

	private int restoreProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Restore subscriber query: " + RESTORE_PROFILE_QUERY);
		}
		PreparedStatement psForRestoreProfileQuery = null;

		try {
			psForRestoreProfileQuery = transaction.prepareStatement(RESTORE_PROFILE_QUERY);
			Timestamp modifiedDate = new Timestamp(System.currentTimeMillis());
			psForRestoreProfileQuery.setTimestamp(1, modifiedDate);
			psForRestoreProfileQuery.setString(2, subscriberIdentity);
			psForRestoreProfileQuery.setQueryTimeout(getDbQueryTimeout());

			Stopwatch watch = new Stopwatch();
			watch.start();
			int updateRowCount = psForRestoreProfileQuery.executeUpdate();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " ms");
			}

			totalQueryTimeoutCount.set(0);

			if (getLogger().isInfoLogLevel()) {
				if (updateRowCount > 0) {
					getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") restored");
				} else {
					getLogger().info(MODULE, "Subscriber(" + subscriberIdentity + ") not restored. Reason. Subscriber not exist");
				}
			}

			return updateRowCount;
		} catch (SQLException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger()
								.warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while restoring subscriber(" + subscriberIdentity
								+ ") to DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, SPR marked as dead. Reason: " + e.getMessage());
				}
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException("restore profile", subscriberIdentity, e), resultCode, e);
		} finally {
			closeQuietly(psForRestoreProfileQuery);
		}
	}

	@Override
	public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Restoring subscribers");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to restore subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to restore subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			Map<String, Integer> subscriberIdToRestoreCountMap = new HashMap<String, Integer>();

			for (String subscriberIdentity : subscriberIdentities) {
				subscriberIdToRestoreCountMap.put(subscriberIdentity, restoreProfile(subscriberIdentity, transaction));
			}
			
			return subscriberIdToRestoreCountMap;
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to restore subscribers to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			throw new OperationFailedException("Unable to restore subscribers."
					+ " Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		} finally {
			endTransaction(transaction);
		}
	}

	@Override
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException {
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Updating subscriber profile for subscriber ID: " + subscriberIdentity);
		}
		
		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to update subscriber profile for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to update subscriber profile for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement psForUpdateQuery = null;
		
		try {
			transaction.begin();
			String updateQuery = createUpdateQuery(updatedProfile);
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Update query for subscriber profile: " + updateQuery);
			}

			psForUpdateQuery = transaction.prepareStatement(updateQuery);
			setPrepareStatementForUpdate(psForUpdateQuery, updatedProfile, subscriberIdentity);
			psForUpdateQuery.setQueryTimeout(getDbQueryTimeout());

			long queryExecutionTime = System.currentTimeMillis();
			int updateRowCount = psForUpdateQuery.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " milliseconds");
			}

			totalQueryTimeoutCount.set(0);

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber profile updated successfully for Subscriber ID: " + subscriberIdentity);
			}

			return updateRowCount;

		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while updating profile for subscriber ID: " + subscriberIdentity + " to DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				}

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			
			throw new OperationFailedException(toStringSQLException("updating profile", subscriberIdentity, e), resultCode, e);

		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to update profile for subscriber ID: " + subscriberIdentity + " to DB with Data Source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			throw new OperationFailedException("Error while updating profile for subscriber ID: " + 
					subscriberIdentity + " from DB with datasource name: " + transaction.getDataSourceName() + ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (Exception e) {
			transaction.rollback();
			throw new OperationFailedException("Error while updating profile for subscriber ID: " + 
					subscriberIdentity + ". Reason: " + e.getMessage(), e);
		} finally {
			closeQuietly(psForUpdateQuery);
			endTransaction(transaction);
		}
	}

	@Override
	public int changeIMSpackage(String subscriberIdentity, String newPackageName) throws OperationFailedException {
		EnumMap<SPRFields, String> updateProfileMap = new EnumMap<SPRFields, String>(SPRFields.class);
		updateProfileMap.put(SPRFields.IMS_PACKAGE, newPackageName);
		return updateProfile(subscriberIdentity, updateProfileMap);
	}


	//TODO call this method from updateProfile(String subscriberIdentity, LinkedHashMap<SPRFields, String> updatedProfile) throws OperationFailedException
	@Override
	public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, Transaction transaction) throws OperationFailedException, TransactionException {
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Updating subscriber profile for subscriber ID: " + subscriberIdentity);
		}
		
		PreparedStatement psForUpdateQuery = null;
		
		try {
			String updateQuery = createUpdateQuery(updatedProfile);
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Update query for subscriber profile: " + updateQuery);
			}

			psForUpdateQuery = transaction.prepareStatement(updateQuery);
			setPrepareStatementForUpdate(psForUpdateQuery, updatedProfile, subscriberIdentity);
			psForUpdateQuery.setQueryTimeout(getDbQueryTimeout());

			long queryExecutionTime = System.currentTimeMillis();
			int updateRowCount = psForUpdateQuery.executeUpdate();
			queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				getLogger().warn(MODULE, "DB query execution time getting high, Last query execution time = "
						+ queryExecutionTime + " milliseconds");
			}

			totalQueryTimeoutCount.set(0);

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber profile updated successfully for Subscriber ID: " + subscriberIdentity);
			}

			return updateRowCount;

		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > getMaxQueryTimeoutCount()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while updating profile for subscriber ID: " + subscriberIdentity + " to DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				}

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			
			throw new OperationFailedException(toStringSQLException("updating profile", subscriberIdentity, e), resultCode, e);

		} finally {
			closeQuietly(psForUpdateQuery);
		}
	}

	
	private void setPrepareStatementForUpdate(PreparedStatement psForUpdateQuery, EnumMap<SPRFields, String> updatedProfile, String subscriberIdentity)
			throws SQLException, OperationFailedException {
		
		int columnIndex = 1;
		for (Entry<SPRFields, String> columnValueEntry : updatedProfile.entrySet()) {
			
			SPRFields sprField = columnValueEntry.getKey();
			String value = columnValueEntry.getValue();
			
			if (SPRFields.SUBSCRIBER_IDENTITY == sprField) {
				continue;
			}
			
			if (sprField.type == Types.NUMERIC) {
				if (value == null) {
					psForUpdateQuery.setNull(columnIndex, Types.NUMERIC);
				} else {
					Long numericValue = parseLong(sprField, value);
					psForUpdateQuery.setLong(columnIndex, numericValue);
				}
			} else if (sprField.type == Types.VARCHAR) {
				psForUpdateQuery.setString(columnIndex, value);
			} else if (sprField.type == Types.TIMESTAMP) {
				if (value == null) {
					psForUpdateQuery.setNull(columnIndex, Types.TIMESTAMP);
				} else {
					Long time = parseLong(sprField, value);
					psForUpdateQuery.setTimestamp(columnIndex, new Timestamp(time));
				}
			}
			
			columnIndex++;
		}
		
		psForUpdateQuery.setString(columnIndex, subscriberIdentity);
	}

	/*
	 * parse String to Long, if NumberFormatException occurs, throws OperationFailedException with proper SPRField name and message
	 */
	private long parseLong(SPRFields sprField, String value) throws OperationFailedException {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			throw new OperationFailedException("Invalid value(" + value + ") for SPR Field: " + sprField.name(), nfe);
		}
	}

	private String createUpdateQuery(EnumMap<SPRFields, String> updatedProfile) {
		
		StringBuilder updateQueryBuilder = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET ");
		
		for (Entry<SPRFields, String> entry : updatedProfile.entrySet()) {
			if (entry.getKey() == SPRFields.SUBSCRIBER_IDENTITY) {
				continue;
			}
			updateQueryBuilder.append(entry.getKey().columnName)
					.append(CommonConstants.EQUAL)
					.append(CommonConstants.QUESTION_MARK)
					.append(CommonConstants.COMMA);
		}
		
		updateQueryBuilder.delete(updateQueryBuilder.length() - 1, updateQueryBuilder.length());
		updateQueryBuilder.append(" WHERE ")
			.append(SPRFields.SUBSCRIBER_IDENTITY.columnName)
			.append(CommonConstants.EQUAL)
			.append(CommonConstants.QUESTION_MARK);
		
		return updateQueryBuilder.toString();
	}
	
	@Override
	public Transaction createTransaction() throws OperationFailedException {
		
		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		
		return transaction;
	}
}
