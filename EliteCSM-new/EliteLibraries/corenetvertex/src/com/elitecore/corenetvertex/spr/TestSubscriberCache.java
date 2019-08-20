package com.elitecore.corenetvertex.spr;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public class TestSubscriberCache {

	private static final String MODULE = "TESTSUBSCRIBER-CACHE";
	private static final String FETCH_ALL_TESTSUBSCRIBER_QUERY = "SELECT SUBSCRIBER_IDENTITY FROM TBLM_TEST_SUBSCRIBER";
	private static final String FETCH_TEST_SUBSCRIBER_QUERY = "SELECT SUBSCRIBER_IDENTITY FROM TBLM_TEST_SUBSCRIBER WHERE SUBSCRIBER_IDENTITY = ?";
	private static final String INSERT_TEST_SUBSCRIBER_QUERY = "INSERT INTO TBLM_TEST_SUBSCRIBER (SUBSCRIBER_IDENTITY) values(?)";
	private static final String DELETE_TEST_SUBSCRIBER_QUERY = "DELETE FROM TBLM_TEST_SUBSCRIBER WHERE SUBSCRIBER_IDENTITY = ?";
	private static final String SEVERITY = null;

	private TransactionFactory transactionFactory;
	private AlertListener alertListener;
	private Set<String> testSubscribers;

	public TestSubscriberCache(TransactionFactory transactionFactory, AlertListener alertListener) {
		this.transactionFactory = transactionFactory;
		this.alertListener = alertListener;
		this.testSubscribers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	}

	public void init() {

		try {
			refresh();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while loading testSubscriber list. Reason: " + e.getMessage());
			getLogger().trace(e);
		}
	}

	/**
	 * fetch all TestSubscriber and add it in cache
	 * 
	 * @throws OperationFailedException
	 */
	public void refresh() throws OperationFailedException {

		if (transactionFactory.isAlive() == false) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unable to load test subscriber list."
						+ " Reason: Datasource not available");
			}
			throw new OperationFailedException("Unable to load test subscriber list. "
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unable to load test subscriber list."
						+ " Reason: Datasource not available");
			}
			throw new OperationFailedException("Unable to load test subscriber list. "
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Statement statementForTestSubscriber = null;
		ResultSet rsForTestSubscriber = null;
		try {
			transaction.begin();
			Set<String> tempTestSubscribers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
			statementForTestSubscriber = transaction.statement();
			rsForTestSubscriber = statementForTestSubscriber.executeQuery(FETCH_ALL_TESTSUBSCRIBER_QUERY);

			while (rsForTestSubscriber.next()) {
				tempTestSubscribers.add(rsForTestSubscriber.getString(1));
			}

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Total " + tempTestSubscribers.size() + " test subscriber cached");
			}

			this.testSubscribers = tempTestSubscribers;
		} catch (SQLException e) {
			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while adding test subscriber from DB with data source name: "
								+ transaction.getDataSourceName());
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
			}

			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to load test subscriber list. Reason: " + e.getMessage());
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to load test subscriber list. "
					+ " Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR);
		} catch (TransactionException e) { 
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to load test subscriber list from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to load test subscriber list. Reason: " + e.getMessage());
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to load test subscriber list. "
					+ " Reason: " + e.getMessage(), resultCode, e);
		} finally {
			closeQuietly(statementForTestSubscriber);
			closeQuietly(rsForTestSubscriber);
			endTransaction(transaction);
		}

	}

	/**
	 * <PRE>
	 * IF provided subscriber identity is of TestSubscriber THEN
	 * 	add it in cache
	 * ELSE 
	 * 	remove from cache
	 * </PRE>
	 * 
	 * @param subscriberIdentity
	 * @throws OperationFailedException
	 */
	private void refresh(String subscriberIdentity) throws OperationFailedException {

		if (transactionFactory.isAlive() == false) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unable to load test subscriber(" + subscriberIdentity + ")."
						+ " Reason: Datasource not available");
			}
			throw new OperationFailedException("Unable to load test subscriber(" + subscriberIdentity + ")."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Unable to load test subscriber(" + subscriberIdentity + ")."
						+ " Reason: Datasource not available");
			}
			throw new OperationFailedException("Unable to load test subscriber(" + subscriberIdentity + ")."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		PreparedStatement psForTestSubscriber = null;
		ResultSet rsForTestSubscriber = null;
		try {
			transaction.begin();
			psForTestSubscriber = transaction.prepareStatement(FETCH_TEST_SUBSCRIBER_QUERY);
			psForTestSubscriber.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			psForTestSubscriber.setString(1, subscriberIdentity);
			rsForTestSubscriber = psForTestSubscriber.executeQuery();

			if (rsForTestSubscriber.next()) {
				this.testSubscribers.add(rsForTestSubscriber.getString(1));

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Test subscriber(" + subscriberIdentity + ") cached successfuly");
				}
			} else {
				this.testSubscribers.remove(subscriberIdentity);

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Test subscriber not found with subscriber identity(" + subscriberIdentity + ")");
				}
			}

		} catch (SQLException e) { 
			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while adding test subscriber(" + subscriberIdentity + ") from DB with data source name: "
								+ transaction.getDataSourceName());
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
			}

			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to load test subscriber(" + subscriberIdentity + "). Reason: " + e.getMessage());
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to load test subscriber(" + subscriberIdentity + "). Reason: "
					+ e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} catch (TransactionException e) { 
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to load test subscriber(" + subscriberIdentity + ") from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Unable to load test subscriber(" + subscriberIdentity + "). Reason: " + e.getMessage());
			}
			transaction.rollback();
			throw new OperationFailedException("Unable to load test subscriber(" + subscriberIdentity + "). Reason: "
					+ " Reason: " + e.getMessage(), resultCode, e);
		} finally {
			closeQuietly(psForTestSubscriber);
			closeQuietly(rsForTestSubscriber);
			endTransaction(transaction);
		}

	}

	public void add(String subscriberIdentity) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Adding test subscriber ID: " + subscriberIdentity);
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to add test subscriber: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to add test subscriber: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			add(subscriberIdentity, transaction);
			transaction.commit();
			testSubscribers.add(subscriberIdentity);
		} catch (TransactionException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to add test subscriber: " + subscriberIdentity + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			throw new OperationFailedException("Unable to add test subscriber: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), resultCode, e);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		} finally {
			endTransaction(transaction);
		}

	}

	private void add(String subscriberIdentity, Transaction transaction) throws TransactionException, OperationFailedException {

		if (getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Insert query for test subscriber: " + INSERT_TEST_SUBSCRIBER_QUERY);
		}

		PreparedStatement psForInsertTestSubscriber = null;
		try {
			psForInsertTestSubscriber = transaction.prepareStatement(INSERT_TEST_SUBSCRIBER_QUERY);
			psForInsertTestSubscriber.setString(1, subscriberIdentity);
			psForInsertTestSubscriber.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			Stopwatch watch = new Stopwatch();
			watch.start();
			psForInsertTestSubscriber.execute();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while adding test subscriber ID: " + subscriberIdentity
								+ ". Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while adding test subscriber ID: " + subscriberIdentity
							+ ". Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Test subscriber: " + subscriberIdentity + " added successfully");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while adding test subscriber from DB with data source name: "
								+ transaction.getDataSourceName());
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
			}
			throw new OperationFailedException("Unable to add test subscriber: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			closeQuietly(psForInsertTestSubscriber);
		}
	}

	private void endTransaction(Transaction transaction) {
		try {
			transaction.end();
		} catch (Exception e) {
			getLogger().trace(MODULE, e);
		}
	}

	public boolean refreshAndExist(String subscriberIdentity) throws OperationFailedException {
		refresh(subscriberIdentity);
		return exists(subscriberIdentity);
	}
	
	public boolean exists(String subscriberIdentity) {
		return testSubscribers.contains(subscriberIdentity);
	}

	public int remove(String subscriberIdentity) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Removing test subscriber ID: " + subscriberIdentity);
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to remove test subscriber: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to remove test subscriber: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		PreparedStatement psForDeleteTestSubscriber = null;
		
		try {
			transaction.begin();

			if (getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "DB Query for delete test Subscriber: " + DELETE_TEST_SUBSCRIBER_QUERY);
			}

			psForDeleteTestSubscriber = transaction.prepareStatement(DELETE_TEST_SUBSCRIBER_QUERY);
			psForDeleteTestSubscriber.setString(1, subscriberIdentity);
			psForDeleteTestSubscriber.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch watch = new Stopwatch();
			watch.start();
			int removeCount = psForDeleteTestSubscriber.executeUpdate();
			watch.stop();
			transaction.commit();
			testSubscribers.remove(subscriberIdentity);

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while removing test subscriber ID: " + subscriberIdentity
								+ ". Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while removing test subscriber ID: " + subscriberIdentity
							+ ". Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			
			if (removeCount > 0) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Test subscriber(" + subscriberIdentity + ") removed successfully");
				}
			} else {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Test subscriber(" + subscriberIdentity + ") not removed. Reason. Test subscriber not found");
				}
			}

			return removeCount;
		} catch (SQLException e) {
			if(e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while removing test subscriber from DB with data source name: "
								+ transaction.getDataSourceName());
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
			}

			transaction.rollback();
			
			throw new OperationFailedException("Unable to remove test subscriber: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);

		} catch (TransactionException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to remove test subscriber: " + subscriberIdentity + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException("Unable to remove test subscriber: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), resultCode, e);
		} finally {
			closeQuietly(psForDeleteTestSubscriber);
			endTransaction(transaction);
		}
	}

	public Iterator<String> getTestSubscriberIterator() {

		final Iterator<String> iterator2 = testSubscribers.iterator();
		Iterator<String> unModifiableIterator = new Iterator<String>() {

			@Override
			public boolean hasNext() {
				return iterator2.hasNext();
			}

			@Override
			public String next() {
				return iterator2.next();
			}

			@Override
			public void remove() {

			}
		};
		return unModifiableIterator;
	}

	/**
	 * IF remove from DB success for all ids THEN and only THEN remove from cache
	 */
	public int remove(List<String> subscriberIdentities) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Removing test subscribers");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to remove test subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to remove test subscribers."
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		PreparedStatement psForDeleteTestSubscriber = null;
		try {
			transaction.begin();
			if (getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "DB Query for delete test Subscriber: " + DELETE_TEST_SUBSCRIBER_QUERY);
			}
			psForDeleteTestSubscriber = transaction.prepareStatement(DELETE_TEST_SUBSCRIBER_QUERY);
			psForDeleteTestSubscriber.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			int removeCount = 0;
			
			for (String subscriberIdentity : subscriberIdentities) {
				psForDeleteTestSubscriber.setString(1, subscriberIdentity);
				Stopwatch watch = new Stopwatch();
				watch.start();
				removeCount += psForDeleteTestSubscriber.executeUpdate();
				watch.stop();

				long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
				if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

					alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
							"DB Query execution time is high while removing test subscriber ID: " + subscriberIdentity
									+ ". Last Query execution time: " + queryExecutionTime + " ms");

					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "DB Query execution time is high while removing test subscriber ID: " + subscriberIdentity
								+ ". Last Query execution time: " + queryExecutionTime + " ms");
					}
				}
			}

			transaction.commit();
			
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Total(" + removeCount + ") Test subscribers removed");
			}
			
			testSubscribers.removeAll(subscriberIdentities);

			return removeCount;
		} catch (SQLException e) {
			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while removing test subscriber from DB with data source name: "
								+ transaction.getDataSourceName());
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
			}
			
			transaction.rollback();

			throw new OperationFailedException("Unable to remove test subscribers."
					+ ". Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);

		} catch (TransactionException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

				alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to remove test subscribers from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException("Unable to remove test subscribers."
					+ ". Reason: " + e.getMessage(), resultCode, e);
		} finally {
			closeQuietly(psForDeleteTestSubscriber);
			endTransaction(transaction);
		}

	}
}
