package com.elitecore.corenetvertex.spr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BillingCycleResetStatus;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.spr.SubscriberUsage.SubscriberUsageBuilder;
import com.elitecore.corenetvertex.spr.UMBatchOperation.BatchOperationData;
import com.elitecore.corenetvertex.spr.balance.QuotaProfileBalance;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.balance.Usage;
import com.elitecore.corenetvertex.spr.balance.UsageInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;


import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.spr.util.SPRUtil.toStringSQLException;

public class UMOperation implements SingleRecordOperation<BatchOperationData>{

	private static final long UNLIMITED_USAGE = -1;
	private static final String MODULE = "UM-OPR";
    protected static final int QUERY_TIMEOUT_ERROR_CODE = 1013;
	private static final String TABLE_TBLT_USAGE = "TBLT_USAGE";
	private static final String TABLE_TBLT_USAGE_HISTORY = "TBLT_USAGE_HISTORY";
        private static final String TABLE_TBLM_RESET_USAGE_REQ = "TBLM_RESET_USAGE_REQ";
    private int queryTimeout;
    private int maxQueryTimeoutCount;
    protected AlertListener alertListener;
	private PolicyRepository policyRepository;
	private AtomicInteger iTotalQueryTimeoutCount;
	private volatile long nextWeeklyResetTime;
	private volatile long nextDailyResetTime;
	private final String queryForDeleteUsage;
	private final String queryForScheduleResetUsage;
	private final String queryForDeleteUsageByPackageId;
	protected final String usageHistoryInsertQuery;
	protected final String usageHistoryInsertQueryBysubscriberId;
	private Object lockDailyResetTime;
	private Object lockWeeklyResetTime;
	

	public UMOperation(AlertListener alertListener, PolicyRepository policyRepository) {
		this.alertListener = alertListener;
		this.policyRepository = policyRepository;
		this.maxQueryTimeoutCount = 200;
		this.queryTimeout = 100;
		this.iTotalQueryTimeoutCount = new AtomicInteger(0);
		long currentTime = getCurrentTime();
		this.nextDailyResetTime = calculateDailyResetTime(currentTime);
		this.nextWeeklyResetTime = calculateWeeklyResetTime(currentTime);
		this.queryForDeleteUsage = getQueryForDeleteUsage();
		this.queryForScheduleResetUsage = getQueryForScheduleResetUsage();
		this.queryForDeleteUsageByPackageId = getQueryForDeleteUsageByPackageId();
		this.usageHistoryInsertQuery = getUsageHistoryInsertQuery();
		this.usageHistoryInsertQueryBysubscriberId = getUsageHistoryInsertQueryBySubscriberId();
		this.lockDailyResetTime = new Object();
		this.lockWeeklyResetTime = new Object();
	}

	public void init() {
		//NOT REQUIRED IN SYNC OPERATION ONLY REQUIRED IN BATCH
	}


	private long calculateWeeklyResetTime(long currentTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		
		return calendar.getTimeInMillis();
	}


	private long calculateDailyResetTime(long currentTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTimeInMillis();
	}


	private String getUsageQuery() {

		return "SELECT * FROM " + TABLE_TBLT_USAGE + " WHERE SUBSCRIBER_ID = ? ";
	}
	
	private String getQueryForDeleteUsage() {
		return "DELETE FROM " + TABLE_TBLT_USAGE + " WHERE SUBSCRIBER_ID = ? AND SUBSCRIPTION_ID IS NULL";
	}

	public String getQueryForScheduleResetUsage() {
		return "INSERT INTO "+ TABLE_TBLM_RESET_USAGE_REQ +
				" (BILLING_CYCLE_ID, SUBSCRIBER_IDENTITY, ALTERNATE_IDENTITY, BILLING_CYCLE_DATE, CREATED_DATE, STATUS, SERVER_INSTANCE_ID, PACKAGE_ID, RESET_REASON, PARAM1, PARAM2, PARAM3, PRODUCT_OFFER_ID)" +
				" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}

	public String getQueryForDeleteUsageByPackageId() {
		return "DELETE FROM " + TABLE_TBLT_USAGE + " WHERE SUBSCRIBER_ID = ? AND PACKAGE_ID = ?";
	}
	
	protected String getUsageHistoryInsertQuery() {

		return "INSERT INTO "
				+ TABLE_TBLT_USAGE_HISTORY
				+ " (CREATE_DATE,"
				+ " ID,"
				+ " SUBSCRIBER_ID,"
				+ "	PACKAGE_ID,"
				+ " SUBSCRIPTION_ID,"
				+ " QUOTA_PROFILE_ID,"
				+ " SERVICE_ID,"
				+ " DAILY_TOTAL,"
				+ " DAILY_UPLOAD,"
				+ " DAILY_DOWNLOAD,"
				+ " DAILY_TIME,"
				+ " WEEKLY_TOTAL,"
				+ " WEEKLY_UPLOAD,"
				+ " WEEKLY_DOWNLOAD,"
				+ " WEEKLY_TIME,"
				+ " BILLING_CYCLE_TOTAL,"
				+ " BILLING_CYCLE_UPLOAD,"
				+ " BILLING_CYCLE_DOWNLOAD,"
				+ " BILLING_CYCLE_TIME,"
				+ " CUSTOM_TOTAL,"
				+ " CUSTOM_UPLOAD,"
				+ " CUSTOM_DOWNLOAD,"
				+ " CUSTOM_TIME,"
				+ " DAILY_RESET_TIME,"
				+ " WEEKLY_RESET_TIME,"
				+ " BILLING_CYCLE_RESET_TIME,"
				+ " CUSTOM_RESET_TIME,"
				+ " LAST_UPDATE_TIME,"
				+ " PRODUCT_OFFER_ID"
				+ ")"
				+ "SELECT " 
				+ " CURRENT_TIMESTAMP,"
				+ " ?,"
				+ " SUBSCRIBER_ID,"
				+ "	PACKAGE_ID,"
				+ " SUBSCRIPTION_ID,"
				+ " QUOTA_PROFILE_ID,"
				+ " SERVICE_ID,"
				+ " DAILY_TOTAL,"
				+ " DAILY_UPLOAD,"
				+ " DAILY_DOWNLOAD,"
				+ " DAILY_TIME,"
				+ " WEEKLY_TOTAL,"
				+ " WEEKLY_UPLOAD,"
				+ " WEEKLY_DOWNLOAD,"
				+ " WEEKLY_TIME,"
				+ " BILLING_CYCLE_TOTAL,"
				+ " BILLING_CYCLE_UPLOAD,"
				+ " BILLING_CYCLE_DOWNLOAD,"
				+ " BILLING_CYCLE_TIME,"
				+ " CUSTOM_TOTAL,"
				+ " CUSTOM_UPLOAD,"
				+ " CUSTOM_DOWNLOAD,"
				+ " CUSTOM_TIME,"
				+ " DAILY_RESET_TIME,"
				+ " WEEKLY_RESET_TIME,"
				+ " BILLING_CYCLE_RESET_TIME,"
				+ " CUSTOM_RESET_TIME,"
				+ " LAST_UPDATE_TIME,"
				+ " PRODUCT_OFFER_ID"
				+ " FROM "
				+ TABLE_TBLT_USAGE
				+ " WHERE ID = ?";
	}

	protected String getUsageHistoryInsertQueryBySubscriberId() {

		return "INSERT INTO "
				+ TABLE_TBLT_USAGE_HISTORY
				+ " (CREATE_DATE,"
				+ " ID,"
				+ " SUBSCRIBER_ID,"
				+ "	PACKAGE_ID,"
				+ " SUBSCRIPTION_ID,"
				+ " QUOTA_PROFILE_ID,"
				+ " SERVICE_ID,"
				+ " DAILY_TOTAL,"
				+ " DAILY_UPLOAD,"
				+ " DAILY_DOWNLOAD,"
				+ " DAILY_TIME,"
				+ " WEEKLY_TOTAL,"
				+ " WEEKLY_UPLOAD,"
				+ " WEEKLY_DOWNLOAD,"
				+ " WEEKLY_TIME,"
				+ " BILLING_CYCLE_TOTAL,"
				+ " BILLING_CYCLE_UPLOAD,"
				+ " BILLING_CYCLE_DOWNLOAD,"
				+ " BILLING_CYCLE_TIME,"
				+ " CUSTOM_TOTAL,"
				+ " CUSTOM_UPLOAD,"
				+ " CUSTOM_DOWNLOAD,"
				+ " CUSTOM_TIME,"
				+ " DAILY_RESET_TIME,"
				+ " WEEKLY_RESET_TIME,"
				+ " BILLING_CYCLE_RESET_TIME,"
				+ " CUSTOM_RESET_TIME,"
				+ " LAST_UPDATE_TIME,"
				+ " PRODUCT_OFFER_ID"
				+ ")"
				+ "SELECT " 
				+ " CURRENT_TIMESTAMP,"
				+ " ?,"
				+ " SUBSCRIBER_ID,"
				+ "	PACKAGE_ID,"
				+ " SUBSCRIPTION_ID,"
				+ " QUOTA_PROFILE_ID,"
				+ " SERVICE_ID,"
				+ " DAILY_TOTAL,"
				+ " DAILY_UPLOAD,"
				+ " DAILY_DOWNLOAD,"
				+ " DAILY_TIME,"
				+ " WEEKLY_TOTAL,"
				+ " WEEKLY_UPLOAD,"
				+ " WEEKLY_DOWNLOAD,"
				+ " WEEKLY_TIME,"
				+ " BILLING_CYCLE_TOTAL,"
				+ " BILLING_CYCLE_UPLOAD,"
				+ " BILLING_CYCLE_DOWNLOAD,"
				+ " BILLING_CYCLE_TIME,"
				+ " CUSTOM_TOTAL,"
				+ " CUSTOM_UPLOAD,"
				+ " CUSTOM_DOWNLOAD,"
				+ " CUSTOM_TIME,"
				+ " DAILY_RESET_TIME,"
				+ " WEEKLY_RESET_TIME,"
				+ " BILLING_CYCLE_RESET_TIME,"
				+ " CUSTOM_RESET_TIME,"
				+ " LAST_UPDATE_TIME,"
				+ " PRODUCT_OFFER_ID"
				+ " FROM "
				+ TABLE_TBLT_USAGE
				+ " WHERE SUBSCRIBER_ID = ? AND PRODUCT_OFFER_ID = ?";
	}

	protected String getReplaceQuery() {

		return "UPDATE "
				+ TABLE_TBLT_USAGE
				+ " SET"
				+ " DAILY_TOTAL=?,"
				+ " DAILY_UPLOAD=?,"
				+ " DAILY_DOWNLOAD = ?,"
				+ " DAILY_TIME = ?,"
				+ " WEEKLY_TOTAL = ?,"
				+ " WEEKLY_UPLOAD = ?,"
				+ " WEEKLY_DOWNLOAD = ?,"
				+ " WEEKLY_TIME = ?,"
				+ " BILLING_CYCLE_TOTAL = ?,"
				+ " BILLING_CYCLE_UPLOAD = ?,"
				+ " BILLING_CYCLE_DOWNLOAD = ?,"
				+ " BILLING_CYCLE_TIME = ?,"
				+ " CUSTOM_TOTAL = ?,"
				+ " CUSTOM_UPLOAD = ?,"
				+ " CUSTOM_DOWNLOAD = ?,"
				+ " CUSTOM_TIME = ?,"
				+ " DAILY_RESET_TIME = ?,"
				+ " WEEKLY_RESET_TIME = ?,"
				+ " BILLING_CYCLE_RESET_TIME = ?,"
				+ " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
				+ " WHERE ID=?";

	}
	
	protected String getResetQuery() {

		return "UPDATE "
				+ TABLE_TBLT_USAGE
				+ " SET"
				+ " DAILY_TOTAL=?,"
				+ " DAILY_UPLOAD=?,"
				+ " DAILY_DOWNLOAD = ?,"
				+ " DAILY_TIME = ?,"
				+ " WEEKLY_TOTAL = ?,"
				+ " WEEKLY_UPLOAD = ?,"
				+ " WEEKLY_DOWNLOAD = ?,"
				+ " WEEKLY_TIME = ?,"
				+ " BILLING_CYCLE_TOTAL = ?,"
				+ " BILLING_CYCLE_UPLOAD = ?,"
				+ " BILLING_CYCLE_DOWNLOAD = ?,"
				+ " BILLING_CYCLE_TIME = ?,"
				+ " CUSTOM_TOTAL = ?,"
				+ " CUSTOM_UPLOAD = ?,"
				+ " CUSTOM_DOWNLOAD = ?,"
				+ " CUSTOM_TIME = ?,"
				+ " DAILY_RESET_TIME = ?,"
				+ " WEEKLY_RESET_TIME = ?,"
				+ " BILLING_CYCLE_RESET_TIME = ?,"
				+ " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
				+ " WHERE SUBSCRIBER_ID=? AND PRODUCT_OFFER_ID=? AND QUOTA_PROFILE_ID=?";

	}
	
	protected String getAddToExistingUsageUpdateQuery() {

		return "UPDATE "
				+ TABLE_TBLT_USAGE
				+ " SET"
				+ " DAILY_TOTAL = DAILY_TOTAL + ?,"
				+ " DAILY_UPLOAD = DAILY_UPLOAD + ?,"
				+ " DAILY_DOWNLOAD = DAILY_DOWNLOAD + ?,"
				+ " DAILY_TIME = DAILY_TIME + ?,"
				+ " WEEKLY_TOTAL = WEEKLY_TOTAL + ?,"
				+ " WEEKLY_UPLOAD = WEEKLY_UPLOAD + ?,"
				+ " WEEKLY_DOWNLOAD = WEEKLY_DOWNLOAD + ?,"
				+ " WEEKLY_TIME = WEEKLY_TIME + ?,"
				+ " BILLING_CYCLE_TOTAL = BILLING_CYCLE_TOTAL + ?,"
				+ " BILLING_CYCLE_UPLOAD = BILLING_CYCLE_UPLOAD + ?,"
				+ " BILLING_CYCLE_DOWNLOAD = BILLING_CYCLE_DOWNLOAD + ?,"
				+ " BILLING_CYCLE_TIME = BILLING_CYCLE_TIME + ?,"
				+ " CUSTOM_TOTAL = CUSTOM_TOTAL + ?,"
				+ " CUSTOM_UPLOAD = CUSTOM_UPLOAD + ?,"
				+ " CUSTOM_DOWNLOAD = CUSTOM_DOWNLOAD + ?,"
				+ " CUSTOM_TIME = CUSTOM_TIME + ?,"
				+ " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
				+ " WHERE ID=?";

	}

	protected String getUsageInsertQuery() {

		return "INSERT INTO "
				+ TABLE_TBLT_USAGE
				+ "( ID,"
				+ " SUBSCRIBER_ID,"
				+ "PACKAGE_ID,"
				+ " SUBSCRIPTION_ID,"
				+ " QUOTA_PROFILE_ID,"
				+ " SERVICE_ID,"
				+ " DAILY_TOTAL,"
				+ " DAILY_UPLOAD,"
				+ " DAILY_DOWNLOAD,"
				+ " DAILY_TIME,"
				+ " WEEKLY_TOTAL,"
				+ " WEEKLY_UPLOAD,"
				+ " WEEKLY_DOWNLOAD,"
				+ " WEEKLY_TIME,"
				+ " BILLING_CYCLE_TOTAL,"
				+ " BILLING_CYCLE_UPLOAD,"
				+ " BILLING_CYCLE_DOWNLOAD,"
				+ " BILLING_CYCLE_TIME,"
				+ " CUSTOM_TOTAL,"
				+ " CUSTOM_UPLOAD,"
				+ " CUSTOM_DOWNLOAD,"
				+ " CUSTOM_TIME,"
				+ " DAILY_RESET_TIME,"
				+ " WEEKLY_RESET_TIME,"
				+ " BILLING_CYCLE_RESET_TIME,"
				+ " CUSTOM_RESET_TIME,"
				+ " LAST_UPDATE_TIME,"
				+ "PRODUCT_OFFER_ID"
				+ ")"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?)";

	}

	/**
	 * <PRE>
	 * 		Fetches usage for provided subscriber identity.
	 * if usage not exist for provided subscriber identity then
	 * 	returns emptyMap
	 * else
	 * 	usage map with key as aggregation key
	 * 
	 * </PRE>
	 * 
	 * @throws OperationFailedException
	 *             when Datasource not available or any exception occurs
	 * @param subscriberIdentity identity of the subscriber
	 * @param transactionFactory source from which NetVertex need to fetch subscriber
	 */
	public Map<String, Map<String, SubscriberUsage>> getUsage(String subscriberIdentity, Map<String, Subscription> addOnSubscriptions, 
			TransactionFactory transactionFactory) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching usage metering information for subscriber ID: " + subscriberIdentity);
		}

		Map<String, Map<String, SubscriberUsage>> subscriptionToSubscriberUsage = new HashMap<String, Map<String, SubscriberUsage>>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to fetch usage for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
		if (dbTransaction == null) {
			throw new OperationFailedException("Unable to fetch usage for subscriber ID: " + subscriberIdentity +
					". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		
		try {
			dbTransaction.begin();
			preparedStatement = dbTransaction.prepareStatement(getUsageQuery());
			preparedStatement.setString(1, subscriberIdentity);
			preparedStatement.setQueryTimeout(queryTimeout);

			long currentTime = getCurrentTime();
			resultSet = preparedStatement.executeQuery();
			long queryExecutionTime = getCurrentTime() - currentTime;

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while usage information. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB select query execution time getting high, Last Query execution time = " + queryExecutionTime
							+ " ms.");
				}
			}

			createUsage(subscriberIdentity, addOnSubscriptions, subscriptionToSubscriberUsage, resultSet);
			if (subscriptionToSubscriberUsage.isEmpty() == false) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Usage found with subscriber ID: " + subscriberIdentity);
				}
				return subscriptionToSubscriberUsage;
			}
			
			iTotalQueryTimeoutCount.set(0);
			
		} catch (SQLException e) {
			handleSQLExceptionException("getting usage", e, subscriberIdentity, dbTransaction);
		} catch (TransactionException e) {
			handleTransactionException("getting usage", e, subscriberIdentity, dbTransaction);
		} catch (Exception e) {
			throw new OperationFailedException("Error while getting usage for subscriber ID: " + subscriberIdentity
					+ ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			closeQuietly(resultSet);
			closeQuietly(preparedStatement);
			endTransaction(dbTransaction);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Usage not exist with subscriber ID: " + subscriberIdentity);
		}

		return Maps.newHashMap();
	}

	public Map<String, Map<String, SubscriberUsage>> getUsage(SPRInfo sprInfo,
															  TransactionFactory transactionFactory) throws OperationFailedException {

		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		Map<String, Subscription> activeSubscriptions = sprInfo.getActiveSubscriptions(System.currentTimeMillis());

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching usage metering information for subscriber ID: " + subscriberIdentity);
		}

		Map<String, Map<String, SubscriberUsage>> subscriptionToSubscriberUsage = new HashMap<String, Map<String, SubscriberUsage>>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to fetch usage for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
		if (dbTransaction == null) {
			throw new OperationFailedException("Unable to fetch usage for subscriber ID: " + subscriberIdentity +
					". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			dbTransaction.begin();
			preparedStatement = dbTransaction.prepareStatement(getUsageQuery());
			preparedStatement.setString(1, subscriberIdentity);
			preparedStatement.setQueryTimeout(queryTimeout);

			long currentTime = getCurrentTime();
			resultSet = preparedStatement.executeQuery();
			long queryExecutionTime = getCurrentTime() - currentTime;
			sprInfo.setUsageLoadTime(queryExecutionTime);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while usage information. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB select query execution time getting high, Last Query execution time = " + queryExecutionTime
							+ " ms.");
				}
			}

			long queryReadStartTime = getCurrentTime();
			createUsage(subscriberIdentity, activeSubscriptions, subscriptionToSubscriberUsage, resultSet);
			long queryReadTime = getCurrentTime() - queryReadStartTime;
			sprInfo.setUsageReadTime(queryReadTime);

			if (subscriptionToSubscriberUsage.isEmpty() == false) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Usage found with subscriber ID: " + subscriberIdentity);
				}
				return subscriptionToSubscriberUsage;
			}

			iTotalQueryTimeoutCount.set(0);

		} catch (SQLException e) {
			handleSQLExceptionException("getting usage", e, subscriberIdentity, dbTransaction);
		} catch (TransactionException e) {
			handleTransactionException("getting usage", e, subscriberIdentity, dbTransaction);
		} catch (Exception e) {
			throw new OperationFailedException("Error while getting usage for subscriber ID: " + subscriberIdentity
					+ ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			closeQuietly(resultSet);
			closeQuietly(preparedStatement);
			endTransaction(dbTransaction);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Usage not exist with subscriber ID: " + subscriberIdentity);
		}

		return Maps.newHashMap();
	}

	private void createUsage(String subscriberIdentity, Map<String, Subscription> activeSubscriptions, Map<String, Map<String, SubscriberUsage>> subscriptionToSubscriberUsage, ResultSet resultSet) throws SQLException {
			while (resultSet.next()) {

				String id = resultSet.getString("ID");
				String subscriptionId = resultSet.getString("SUBSCRIPTION_ID");
				String quotaProfileId = resultSet.getString("QUOTA_PROFILE_ID");
				String serviceId = resultSet.getString("SERVICE_ID");
				String packageId = resultSet.getString("PACKAGE_ID");
				String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");

				boolean isAddOnUsage = isAddOnSubscriptionUsage(subscriptionId);
				if (isAddOnUsage) {


                Subscription addOnSubscription = activeSubscriptions.get(subscriptionId);

					if (addOnSubscription == null) {
						if (getLogger().isInfoLogLevel()) {
							getLogger().info(MODULE, "Skip to add usage for subscription id:" + subscriptionId
									+ ". Reason: subscription is not active");
						}

						continue;
					}

				} else {
						//FIXME FETCH BASE PACKAGE FORM POLICY REPOSITORY FOR VALIDATION
				}

				Timestamp dailyResetTime = resultSet.getTimestamp("DAILY_RESET_TIME");
				long dailyTotalUsage = resultSet.getLong("DAILY_TOTAL");
				long dailyUploadUsage = resultSet.getLong("DAILY_UPLOAD");
				long dailyDownloadUsage = resultSet.getLong("DAILY_DOWNLOAD");
				long dailyTime = resultSet.getLong("DAILY_TIME");

				Timestamp weeklyResetTime = resultSet.getTimestamp("WEEKLY_RESET_TIME");
				long weeklyTotalUsage = resultSet.getLong("WEEKLY_TOTAL");
				long weeklyUploadUsage = resultSet.getLong("WEEKLY_UPLOAD");
				long weeklyDownloadUsage = resultSet.getLong("WEEKLY_DOWNLOAD");
				long weeklyTime = resultSet.getLong("WEEKLY_TIME");
				
				Timestamp billingCycleResetTime = resultSet.getTimestamp("BILLING_CYCLE_RESET_TIME");
				long billingCycleTotalUsage = resultSet.getLong("BILLING_CYCLE_TOTAL");
				long billingCycleUploadUsage = resultSet.getLong("BILLING_CYCLE_UPLOAD");
				long billingCycleDownloadUsage = resultSet.getLong("BILLING_CYCLE_DOWNLOAD");
				long billingCycleTime = resultSet.getLong("BILLING_CYCLE_TIME");

				Timestamp customResetTime = resultSet.getTimestamp("CUSTOM_RESET_TIME");
				long customTotalUsage = resultSet.getLong("CUSTOM_TOTAL");
				long customUploadUsage = resultSet.getLong("CUSTOM_UPLOAD");
				long customDownloadUsage = resultSet.getLong("CUSTOM_DOWNLOAD");
				long customTime = resultSet.getLong("CUSTOM_TIME");
				

				SubscriberUsage subscriberUsage = new SubscriberUsage(id,
						quotaProfileId,
						subscriberIdentity,
						serviceId,
						subscriptionId,
						packageId, productOfferId, billingCycleTotalUsage,
						billingCycleDownloadUsage,
						billingCycleUploadUsage,
						billingCycleTime,
						dailyTotalUsage,
						dailyDownloadUsage,
						dailyUploadUsage,
						dailyTime,
						weeklyTotalUsage,
						weeklyDownloadUsage,
						weeklyUploadUsage,
						weeklyTime,
						customTotalUsage,
						customDownloadUsage,
						customUploadUsage,
						customTime,
						customResetTime.getTime(),
						dailyResetTime.getTime(),
						weeklyResetTime.getTime(),
						billingCycleResetTime.getTime());


				Map<String, SubscriberUsage> quotaProfileAndServiceIdToUsage;
				if (isAddOnUsage) {
					quotaProfileAndServiceIdToUsage = subscriptionToSubscriberUsage.get(subscriptionId);
				} else {
					quotaProfileAndServiceIdToUsage = subscriptionToSubscriberUsage.get(packageId);
				}


				if (quotaProfileAndServiceIdToUsage == null) {
					quotaProfileAndServiceIdToUsage = new HashMap<String, SubscriberUsage>();
					if(isAddOnUsage) {
						subscriptionToSubscriberUsage.put(subscriptionId, quotaProfileAndServiceIdToUsage);
					} else {
						subscriptionToSubscriberUsage.put(packageId, quotaProfileAndServiceIdToUsage);
					}

				}

				quotaProfileAndServiceIdToUsage.put(quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId, subscriberUsage);

			}
				}
			
	private void endTransaction(DBTransaction transaction) {
		try {
			if (transaction != null) {
				transaction.end();
			}
		} catch (TransactionException e) {
			getLogger().error(MODULE, "Error in ending transaction. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	private boolean isAddOnSubscriptionUsage(String subscriptionId) {
		return subscriptionId != null;
	}

	protected long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/*
	 * Overwrite the existing usage with usage passed in the argument 
	 * so if current usage of subscriber is 100 and usage passed in argument is 150 then usage after add operation is 150
	 */
	public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
			throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Performing operation to replace usage for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement psForUpdate = null;
		
		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to perform replace usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();
		
		if (transaction == null) {
			throw new OperationFailedException("Unable to perform replace usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {

			transaction.begin();

			psForUpdate = transaction.prepareStatement(getReplaceQuery());
			
			psForUpdate.setQueryTimeout(queryTimeout);
			for (SubscriberUsage usage : usages) {
				
				// Adding previous usage record to history table before replacing, do not change sequence
				setPreviousUsagesToUsageHistory(transaction, usage.getId());
				setUsageToPSForReplace(psForUpdate, getCurrentTime(), usage);

				long queryExecutionTime = getCurrentTime();
				psForUpdate.executeUpdate();
				queryExecutionTime = getCurrentTime() - queryExecutionTime;
				if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

					alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
							"DB Query execution time is high while replacing usage information. "
									+ "Last Query execution time: " + queryExecutionTime + " ms");

					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
								+ queryExecutionTime + " milliseconds.");
					}
				}

			}

			iTotalQueryTimeoutCount.set(0);
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Replace usage operation successfully completed");
			}

		} catch (SQLException e) {
			transaction.rollback();
			handleSQLExceptionException("replace usage", e, subscriberIdentity, transaction);
		} catch (TransactionException e) {
			transaction.rollback();
			handleTransactionException("replace usage", e, subscriberIdentity, transaction);
		} catch (Exception e) {
			transaction.rollback();
			throw new OperationFailedException("Error while replacing usage operation for subscriber ID: " + subscriberIdentity
					+ " . Reason: " + e.getMessage(), e);
		} finally {
			closeQuietly(psForUpdate);
			endTransaction(transaction);
		}
	}

	public void resetUsage(String subscriberIdentity, String productOfferId, TransactionFactory transactionFactory) throws OperationFailedException {
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Performing operation to reset usage for subscriber ID: " + subscriberIdentity);
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to perform reset usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();
		
		if (transaction == null) {
			throw new OperationFailedException("Unable to perform reset usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {

			transaction.begin();

			resetUsage(subscriberIdentity, productOfferId, transaction);

		} catch (TransactionException e) {
			transaction.rollback();
			handleTransactionException("reset usage", e, subscriberIdentity, transaction);
		}catch (SQLException e) {
			transaction.rollback();
			handleSQLExceptionException("reset usage", e, subscriberIdentity, transaction);
		} catch (Exception e) {
			transaction.rollback();
			throw new OperationFailedException("Error while reset usage operation for subscriber ID: " + subscriberIdentity
					+ " . Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			endTransaction(transaction);
		}
	}

	public void resetUsage(String subscriberIdentity, String productOfferId, Transaction transaction) throws OperationFailedException, TransactionException, SQLException {
		PreparedStatement psForUpdate = null;

		try {
			// Adding previous usage record to history table before replacing, do not change sequence
			setPreviousUsagesToUsageHistory(transaction, subscriberIdentity, productOfferId);

			ProductOffer productOffer = policyRepository.getProductOffer().byId(productOfferId);
			UserPackage userPackage = productOffer.getDataServicePkgData();

			if (userPackage != null) {

				if (QuotaProfileType.USAGE_METERING_BASED != userPackage.getQuotaProfileType()) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Skipping usage reset operation for subscriber ID: " + subscriberIdentity
								+ ". Reason: Data Package: " + userPackage.getName()+ " is not UM Based");
					}
					return;
				}

				List<QuotaProfile> quotaProfiles = userPackage.getQuotaProfiles();

				for (QuotaProfile quotaProfile : quotaProfiles) {

					psForUpdate = transaction.prepareStatement(getResetQuery());

					psForUpdate.setQueryTimeout(queryTimeout);

					setUsageToPSForReset(psForUpdate, getCurrentTime(), subscriberIdentity, productOfferId, quotaProfile);
					long queryExecutionTime = getCurrentTime();
					psForUpdate.executeUpdate();
					queryExecutionTime = getCurrentTime() - queryExecutionTime;
					if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

						alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
								"DB Query execution time is high while reseting usage information. "
										+ "Last Query execution time: " + queryExecutionTime + " ms");

						if (getLogger().isWarnLogLevel()) {
							getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
									+ queryExecutionTime + " milliseconds.");
						}
					}
				}
				iTotalQueryTimeoutCount.set(0);
			}

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Reset usage operation successfully completed for subscriber ID: " + subscriberIdentity);
			}
		} finally {
			closeQuietly(psForUpdate);
		}

	}

	private void setPreviousUsagesToUsageHistory(Transaction transaction, String subscriberIdentity, String productOfferId) throws TransactionException, SQLException {

	    if (getLogger().isDebugLogLevel()) {
	        getLogger().debug(MODULE, "Moving previous usage to history table for subscriber Id: "
                    + subscriberIdentity + ", product offer Id: " + productOfferId);
        }

		
		PreparedStatement psForUsageHistory = null;
		try{
			psForUsageHistory = transaction.prepareStatement(usageHistoryInsertQueryBysubscriberId);
			psForUsageHistory.setString(1, generateId());
			psForUsageHistory.setString(2, subscriberIdentity);
			psForUsageHistory.setString(3, productOfferId);
			
			long queryExecutionTime = getCurrentTime();
			psForUsageHistory.executeUpdate();
			queryExecutionTime = getCurrentTime() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while adding usage information in history table. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
							+ queryExecutionTime + " milliseconds.");
				}
			}

		}finally{
			closeQuietly(psForUsageHistory);
		}
	}

	private void setPreviousUsagesToUsageHistory(Transaction transaction, String usageId) throws TransactionException, SQLException {
		
		PreparedStatement psForUsageHistory = null;
		try{
			psForUsageHistory = transaction.prepareStatement(usageHistoryInsertQuery);
			psForUsageHistory.setString(1, generateId());
			psForUsageHistory.setString(2, usageId);
			long queryExecutionTime = getCurrentTime();
			psForUsageHistory.executeQuery();
			queryExecutionTime = getCurrentTime() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while adding usage information in history table. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
							+ queryExecutionTime + " milliseconds.");
				}
			}

		}finally{
			closeQuietly(psForUsageHistory);
		}		
	}

	private void setUsageToPSForReset(PreparedStatement psForUpdate, long currentTime, String subscriberIdentity, String productOfferId, QuotaProfile quotaProfile) throws SQLException {

		long billingCycleResetTime = getFutureDate();

		if(quotaProfile.getRenewalInterval()>0 && quotaProfile.getRenewalIntervalUnit()!=null){
			billingCycleResetTime = quotaProfile.getRenewalIntervalUnit().addTime(System.currentTimeMillis(), quotaProfile.getRenewalInterval());
		}

		psForUpdate.setLong(1, 0);
		psForUpdate.setLong(2, 0);
		psForUpdate.setLong(3, 0);
		psForUpdate.setLong(4, 0);

		psForUpdate.setLong(5, 0);
		psForUpdate.setLong(6, 0);
		psForUpdate.setLong(7, 0);
		psForUpdate.setLong(8, 0);

		psForUpdate.setLong(9, 0);
		psForUpdate.setLong(10, 0);
		psForUpdate.setLong(11, 0);
		psForUpdate.setLong(12, 0);

		psForUpdate.setLong(13, 0);
		psForUpdate.setLong(14, 0);
		psForUpdate.setLong(15, 0);
		psForUpdate.setLong(16, 0);

		psForUpdate.setTimestamp(17, new Timestamp(getResetTime(currentTime)));
		psForUpdate.setTimestamp(18, new Timestamp(getWeeklyTime(currentTime)));
		psForUpdate.setTimestamp(19, new Timestamp(billingCycleResetTime));
		psForUpdate.setString(20, subscriberIdentity);
		psForUpdate.setString(21, productOfferId);
		psForUpdate.setString(22, quotaProfile.getId());
	}
	
	void setUsageToPSForReplace(PreparedStatement psForUpdate, long currentTime, SubscriberUsage usage) throws SQLException {
		psForUpdate.setLong(1, usage.getDailyTotal());
		psForUpdate.setLong(2, usage.getDailyUpload());
		psForUpdate.setLong(3, usage.getDailyDownload());
		psForUpdate.setLong(4, usage.getDailyTime());

		psForUpdate.setLong(5, usage.getWeeklyTotal());
		psForUpdate.setLong(6, usage.getWeeklyUpload());
		psForUpdate.setLong(7, usage.getWeeklyDownload());
		psForUpdate.setLong(8, usage.getWeeklyTime());

		psForUpdate.setLong(9, usage.getBillingCycleTotal());
		psForUpdate.setLong(10, usage.getBillingCycleUpload());
		psForUpdate.setLong(11, usage.getBillingCycleDownload());
		psForUpdate.setLong(12, usage.getBillingCycleTime());

		psForUpdate.setLong(13, usage.getCustomTotal());
		psForUpdate.setLong(14, usage.getCustomUpload());
		psForUpdate.setLong(15, usage.getCustomDownload());
		psForUpdate.setLong(16, usage.getCustomTime());

		Timestamp dailyResetTime;
		if(usage.getDailyResetTime()  > currentTime){
            dailyResetTime = new Timestamp(usage.getDailyResetTime());
        } else {
            dailyResetTime = new Timestamp(getResetTime(currentTime));
        }
		psForUpdate.setTimestamp(17, dailyResetTime);

		Timestamp weeklyResetTime;
		if(usage.getWeeklyResetTime()  > currentTime){
            weeklyResetTime = new Timestamp(usage.getWeeklyResetTime());
        } else {
            weeklyResetTime = new Timestamp(getWeeklyTime(currentTime));
        }

		psForUpdate.setTimestamp(18, weeklyResetTime);
		psForUpdate.setTimestamp(19, new Timestamp(usage.getBillingCycleResetTime()));
		psForUpdate.setString(20, usage.getId());
	}

	/*
	 * Add usage passed in argument to existing usage.
	 * so if current usage of subscriber is 100 and usage passed in argument is 10 then usage after add operation is 110
	 */
	public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
			throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Add usage operation started for subscriber ID: " + subscriberIdentity);
		}
		
		PreparedStatement psForUpdate = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to perform add usage operation for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();
		
		if (transaction == null) {
			throw new OperationFailedException("Unable to perform add usage operation for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();

			psForUpdate = transaction.prepareStatement(getAddToExistingUsageUpdateQuery());
			psForUpdate.setQueryTimeout(queryTimeout);
			for (SubscriberUsage info : usages) {

				setUsageToPSForAddToExisting(psForUpdate, info);

				long queryExecutionTime = getCurrentTime();
				psForUpdate.executeUpdate();
				queryExecutionTime = getCurrentTime() - queryExecutionTime;
				if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

					alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
							"DB Query execution time is high while adding usage information. "
									+ "Last Query execution time: " + queryExecutionTime + " ms");

					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
								+ queryExecutionTime + " milliseconds.");
					}
				}

			}

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Add usage operation completed for subscriber ID: " + subscriberIdentity);
			}
			iTotalQueryTimeoutCount.set(0);
		} catch (SQLException e) {
			transaction.rollback();
			handleSQLExceptionException("adding usage", e, subscriberIdentity, transaction);
		} catch (TransactionException e) {
			transaction.rollback();
			handleTransactionException("adding usage", e, subscriberIdentity, transaction);
		} catch (Exception e) {
			transaction.rollback();
			throw new OperationFailedException("Error while adding usage operation for subscriber ID: " + subscriberIdentity
					+ " . Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			closeQuietly(psForUpdate);
			endTransaction(transaction);
		}
	}

	private void handleTransactionException(String operationName, TransactionException e, String subscriberIdentity, DBTransaction transaction) throws OperationFailedException {
		ResultCode resultCode = ResultCode.INTERNAL_ERROR;

		if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

			alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
					"Unable to " + operationName + " metering information in " + transaction.getDataSourceName() +
							" database. Reason: connection not available");
			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		}

		throw new OperationFailedException("Error while " + operationName + "operation for subscriber ID: " + subscriberIdentity
				+ " . Reason: " + e.getMessage(), resultCode, e);
	}

	private void handleSQLExceptionException(String operationName, SQLException e, String subscriberId, DBTransaction transaction)
			throws OperationFailedException {
		ResultCode resultCode = ResultCode.INTERNAL_ERROR;
		if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
			if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
				if (getLogger().isWarnLogLevel()) {
					getLogger()
							.warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
				}
				transaction.markDead();
			}
			alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
					"DB Query Timeout while " + operationName + " information in table name: "
							+ TABLE_TBLT_USAGE, 0, "table name: " + TABLE_TBLT_USAGE);
			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		} else if (transaction.isDBDownSQLException(e)) {
			transaction.markDead();
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
						+ " is Down, SPR marked as dead. Reason: " + e.getMessage());
			}

			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		}

		throw new OperationFailedException(toStringSQLException(operationName, subscriberId, e), resultCode, e);
	}

	protected  void setUsageToPSForAddToExisting(PreparedStatement psForUpdate, SubscriberUsage usage) throws SQLException {
		psForUpdate.setLong(1, usage.getDailyTotal());
		psForUpdate.setLong(2, usage.getDailyUpload());
		psForUpdate.setLong(3, usage.getDailyDownload());
		psForUpdate.setLong(4, usage.getDailyTime());

		psForUpdate.setLong(5, usage.getWeeklyTotal());
		psForUpdate.setLong(6, usage.getWeeklyUpload());
		psForUpdate.setLong(7, usage.getWeeklyDownload());
		psForUpdate.setLong(8, usage.getWeeklyTime());

		psForUpdate.setLong(9, usage.getBillingCycleTotal());
		psForUpdate.setLong(10, usage.getBillingCycleUpload());
		psForUpdate.setLong(11, usage.getBillingCycleDownload());
		psForUpdate.setLong(12, usage.getBillingCycleTime());

		psForUpdate.setLong(13, usage.getCustomTotal());
		psForUpdate.setLong(14, usage.getCustomUpload());
		psForUpdate.setLong(15, usage.getCustomDownload());
		psForUpdate.setLong(16, usage.getCustomTime());

		psForUpdate.setString(17, usage.getId());
	}

	public void insert(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
			throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Performing operation to insert usage operation for subscriber ID: " + subscriberIdentity);
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to perform insert usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();
		
		if (transaction == null) {
			throw new OperationFailedException("Unable to perform insert usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: Datasource not available",ResultCode.SERVICE_UNAVAILABLE);
		}
		try {
			transaction.begin();
			insert(subscriberIdentity, usages, transaction);
		} catch (TransactionException e) {
			transaction.rollback();
			handleTransactionException("insert usage", e, subscriberIdentity, transaction);
		} catch (OperationFailedException e) {
            transaction.rollback();
            throw e;
        } catch (Exception e) {
			transaction.rollback();
			throw new OperationFailedException("Error while insert usage operation for subscriber ID: " + subscriberIdentity
					+ ". Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			endTransaction(transaction);
		}
	}


	public void insert(String subscriberId, Collection<SubscriberUsage> usages, Transaction transaction) throws TransactionException,
			OperationFailedException {

		long currentTime = getCurrentTime();
		PreparedStatement psforInsert = null;
		try {

			psforInsert = transaction.prepareStatement(getUsageInsertQuery());
			psforInsert.setQueryTimeout(queryTimeout);
			for (SubscriberUsage info : usages) {

				setUsageToPSForInsert(psforInsert, currentTime, info);

				long queryExecutionTime = getCurrentTime();
				psforInsert.executeUpdate();
				queryExecutionTime = getCurrentTime() - queryExecutionTime;
				if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

					alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
							"DB Query execution time is high while inserting usage information. "
									+ "Last Query execution time: " + queryExecutionTime + " ms");

					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
								+ queryExecutionTime + " milliseconds.");
					}
				}

			}
			iTotalQueryTimeoutCount.set(0);
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Insert usage operation successfully completed");
			}
		} catch (SQLException e) {
			handleSQLExceptionException("inserting usage", e, subscriberId, transaction);
		} finally {
			closeQuietly(psforInsert);
		}

	}

	protected  void setUsageToPSForInsert(PreparedStatement psforInsert, long currentTime, SubscriberUsage usage) throws SQLException {
		psforInsert.setString(1, generateId());
		psforInsert.setString(2, usage.getSubscriberIdentity());
		psforInsert.setString(3, usage.getPackageId());
		psforInsert.setString(4, usage.getSubscriptionId());
		psforInsert.setString(5, usage.getQuotaProfileId());
		psforInsert.setString(6, usage.getServiceId());

		psforInsert.setLong(7, usage.getDailyTotal());
		psforInsert.setLong(8, usage.getDailyUpload());
		psforInsert.setLong(9, usage.getDailyDownload());
		psforInsert.setLong(10, usage.getDailyTime());

		psforInsert.setLong(11, usage.getWeeklyTotal());
		psforInsert.setLong(12, usage.getWeeklyUpload());
		psforInsert.setLong(13, usage.getWeeklyDownload());
		psforInsert.setLong(14, usage.getWeeklyTime());

		psforInsert.setLong(15, usage.getBillingCycleTotal());
		psforInsert.setLong(16, usage.getBillingCycleUpload());
		psforInsert.setLong(17, usage.getBillingCycleDownload());
		psforInsert.setLong(18, usage.getBillingCycleTime());

		psforInsert.setLong(19, usage.getCustomTotal());
		psforInsert.setLong(20, usage.getCustomUpload());
		psforInsert.setLong(21, usage.getCustomDownload());
		psforInsert.setLong(22, usage.getCustomTime());

		long billingCycleResetTime = usage.getBillingCycleResetTime();
		if(billingCycleResetTime == 0) {
            billingCycleResetTime = getFutureDate();
        }

		long customResetTime = usage.getCustomResetTime();
		if(customResetTime == 0) {
            customResetTime = getFutureDate();
        }

		psforInsert.setTimestamp(23, new Timestamp(getResetTime(currentTime)));
		psforInsert.setTimestamp(24, new Timestamp(getWeeklyTime(currentTime)));
		psforInsert.setTimestamp(25, new Timestamp(billingCycleResetTime));
		psforInsert.setTimestamp(26, new Timestamp(customResetTime));
		psforInsert.setString(27, usage.getProductOfferId());
	}


	protected String generateId() {
		return UUID.randomUUID().toString();
	}


	protected long getWeeklyTime(long currentTime) {

		if (currentTime > nextWeeklyResetTime) {
			synchronized (lockWeeklyResetTime) {
				nextWeeklyResetTime = calculateWeeklyResetTime(currentTime);
			}
		}

		return nextWeeklyResetTime;
	}


	protected long getResetTime(long currentTime) {

		if (currentTime > nextDailyResetTime) {
			synchronized (lockDailyResetTime) {
				nextDailyResetTime = calculateDailyResetTime(currentTime);
			}
		}
		return nextDailyResetTime;
	}


	protected long getFutureDate() {
		return CommonConstants.FUTURE_DATE;
	}
	
	/**
	 * 
	 * @param subscriberIdentity
	 * @param transaction, NonNull
	 * @return
	 * @throws OperationFailedException
	 */
	public int deleteBaseAndPromotionalPackageUsage(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Delete usage operation for subscriber(" + subscriberIdentity + ") started");
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Delete usage query: " + queryForDeleteUsage);
		}

		PreparedStatement psDeleteUsageStatement = null;

		try {
			psDeleteUsageStatement = transaction.prepareStatement(queryForDeleteUsage);
			psDeleteUsageStatement.setString(1, subscriberIdentity);
			psDeleteUsageStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch watch = new Stopwatch();
			watch.start();
			int deleteCount = psDeleteUsageStatement.executeUpdate();
			watch.stop();
			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time getting high, Last query execution time = "
							+ queryExecutionTime + " ms.");
				}
			}

			if (getLogger().isInfoLogLevel()) {
				if (deleteCount > 0) {
					getLogger().info(MODULE, "Total(" + deleteCount + ") Base package usage deleted for subscriber(" + subscriberIdentity + ")");
				} else {
					getLogger().info(MODULE, "Base package usage not deleted for subscriber(" + subscriberIdentity
							+ "). Reason. Subscriber or base package usage not found");
				}
				
			}
			iTotalQueryTimeoutCount.set(0);
			return deleteCount;
		} catch (SQLException e) {
			handleSQLExceptionException("deleting base and promotional package usage", e, subscriberIdentity, transaction);
		} finally {
			closeQuietly(psDeleteUsageStatement);
		}

		return 0;
	}
	
	public int deleteBasePackageUsage(String subscriberIdentity, String packageId, Transaction transaction) throws OperationFailedException, TransactionException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Delete usage operation for subscriber(" + subscriberIdentity + ") started");
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Delete usage query: " + queryForDeleteUsageByPackageId);
		}

		PreparedStatement psDeleteUsageStatement = null;

		try {
			psDeleteUsageStatement = transaction.prepareStatement(queryForDeleteUsageByPackageId);
			psDeleteUsageStatement.setString(1, subscriberIdentity);
			psDeleteUsageStatement.setString(2, packageId);
			psDeleteUsageStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch watch = new Stopwatch();
			watch.start();
			int deleteCount = psDeleteUsageStatement.executeUpdate();
			watch.stop();
			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high for datasource: " + transaction.getDataSourceName() +
								". Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time getting high, Last query execution time = "
							+ queryExecutionTime + " ms.");
				}
			}

			if (getLogger().isInfoLogLevel()) {
				if (deleteCount > 0) {
					getLogger().info(MODULE, "Total(" + deleteCount + ") Base package usage deleted for subscriber(" + subscriberIdentity + ")");
				} else {
					getLogger().info(MODULE, "Base package usage not deleted for subscriber(" + subscriberIdentity
							+ "). Reason. Subscriber or base package usage not found");
				}
				
			}
			iTotalQueryTimeoutCount.set(0);
			return deleteCount;
		} catch (SQLException e) {
			handleSQLExceptionException("deleting base package usage", e, subscriberIdentity, transaction);
		} finally {
			closeQuietly(psDeleteUsageStatement);
		}

		return 0;
	}

	public void resetBillingCycle(String subscriberID, String alternateID, String productOfferId,
			long resetBillingCycleDate, String resetReason, String parameter1,
			String parameter2, String parameter3, TransactionFactory transactionFactory)
			throws OperationFailedException {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Performing insert reset usage operation for subscriber ID: " + subscriberID);
		}

		PreparedStatement psforInsert = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to perform insert reset usage operation for subscriber ID: " + subscriberID
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to perform insert reset usage operation for subscriber ID: " + subscriberID
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();
			insertIntoResetTable(subscriberID, alternateID, productOfferId, new Timestamp(resetBillingCycleDate), resetReason, parameter1, parameter2, parameter3, transaction);
		} catch (TransactionException e) {
			transaction.rollback();
			handleTransactionException("resetting billing cycle usage", e, subscriberID, transaction);
		} catch (OperationFailedException e) {
			transaction.rollback();
			throw e;
		} finally {
			closeQuietly(psforInsert);
			endTransaction(transaction);
		}
	}
	
	public void scheduleForUsageDelete(String subscriberID, String alternateID, String productOfferId, String resetReason, String parameter1,
                                       String parameter2, String parameter3, Transaction transaction)
			throws OperationFailedException, TransactionException {

        ProductOffer productOffer = policyRepository.getProductOffer().byId(productOfferId);

        if (productOffer.getDataServicePkgData() == null
                || productOffer.getDataServicePkgData().getQuotaProfiles().isEmpty()) {

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping schedule usage delete for subscriber Id: " + subscriberID + ", product offer: " + productOffer.getName() +
                            ". Reason: Data quota profile not configured");
                }

                return;
        }
        insertIntoResetTable(subscriberID, alternateID, productOfferId, null, resetReason, parameter1, parameter2, parameter3, transaction);
	}

	private void insertIntoResetTable(String subscriberID, String alternateID, String productOfferId,
			@Nullable Timestamp resetBillingCycleDate, String resetReason, String parameter1,
			String parameter2, String parameter3, Transaction transaction) throws OperationFailedException, TransactionException {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Performing insert operation in " + TABLE_TBLM_RESET_USAGE_REQ);
		}

		PreparedStatement psforInsert = null;

		try {

			psforInsert = transaction.prepareStatement(queryForScheduleResetUsage);
			psforInsert.setQueryTimeout(queryTimeout);
			setValuesForUsageResetTable(subscriberID, alternateID, productOfferId,
					resetBillingCycleDate, resetReason, parameter1, parameter2,
					parameter3, psforInsert);

			long queryExecutionTime = getCurrentTime();
			psforInsert.executeUpdate();

			queryExecutionTime = getCurrentTime() - queryExecutionTime;
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while inserting usage reset information. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "OPEN-DB insert query execution time getting high, Last Query execution time = "
							+ queryExecutionTime + " milliseconds.");
				}
			}
			iTotalQueryTimeoutCount.set(0);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Insert reset usage operation successfully completed");
			}

		} catch (SQLException e) {
			handleSQLExceptionException("inserting usage for reset", e, subscriberID, transaction);
		} finally {
			closeQuietly(psforInsert);
		}
	}
	
	private void setValuesForUsageResetTable(String subscriberID,
			String alternateID, String productOfferId, Timestamp billingCycleDate,
			String resetReason, String parameter1, String parameter2,
			String parameter3, PreparedStatement psforInsert)
			throws SQLException {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		psforInsert.setString(1, UUID.randomUUID().toString());
		psforInsert.setString(2, subscriberID);
		psforInsert.setString(3, alternateID);
		psforInsert.setTimestamp(4, billingCycleDate);
		psforInsert.setTimestamp(5, currentTime);
		psforInsert.setString(6, BillingCycleResetStatus.PENDING.getVal());
		psforInsert.setString(7, CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE);
		psforInsert.setString(8, policyRepository.getProductOffer().byId(productOfferId).getDataServicePkgId());
		psforInsert.setString(9, resetReason);
		psforInsert.setString(10, parameter1);
		psforInsert.setString(11, parameter2);
		psforInsert.setString(12, parameter3);
		psforInsert.setString(13, productOfferId);
	}
	
	public List<SubscriptionInformation> getBalance(SPRInfo sprInfo, String pkgId, String subscriptionId,
			Map<String, Subscription> addOnSubscriptions, TransactionFactory transactionFactory) throws OperationFailedException {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Fetching balance information for subscriber ID: " + sprInfo.getSubscriberIdentity());
		}

		ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());

		if (Objects.isNull(productOffer)) {
			throw new OperationFailedException("No product offer found for subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
		}

		UserPackage pkg = null;
		if (Strings.isNullOrBlank(pkgId) == false) {
			pkg = policyRepository.getPkgDataById(pkgId);

			if (pkg == null) {
				throw new OperationFailedException("No package found with pkgId: " + pkgId + " for subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
			}
		}
		
		Subscription addOnSubscription = null;
		if (Strings.isNullOrBlank(subscriptionId) == false) {
			addOnSubscription = addOnSubscriptions.get(subscriptionId);
			if (addOnSubscription == null) {
				throw new OperationFailedException("No subscription found for subscription ID: " + subscriptionId,ResultCode.NOT_FOUND);
			}
		}

		Map<String, Map<String, SubscriberUsage>> usage = getUsage(sprInfo.getSubscriberIdentity(), addOnSubscriptions, transactionFactory);

		if (Maps.isNullOrEmpty(usage)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "No usage found for subscriber ID: " + sprInfo.getSubscriberIdentity());
			}
		} else {
			if (getLogger().isDebugLogLevel()) {
				StringBuilder builder = new StringBuilder();
				builder.append("Usage for subscriber ID: " + sprInfo.getSubscriberIdentity() + CommonConstants.LINE_SEPARATOR);
				for (Entry<String, Map<String, SubscriberUsage>> usageEntry : usage.entrySet()) {
					builder.append("Package or SubscriptionId: " + usageEntry.getKey() + CommonConstants.LINE_SEPARATOR);
					for (Entry<String, SubscriberUsage> subscriberUsage : usageEntry.getValue().entrySet()) {
						builder.append("Key: " + subscriberUsage.getKey() + CommonConstants.LINE_SEPARATOR);
						builder.append("SubscriberUsage: " + subscriberUsage.getValue() + CommonConstants.LINE_SEPARATOR);
					}
				}
				getLogger().debug(MODULE, builder.toString());
			}
		}

		

		List<SubscriptionInformation> subscriptionInformations = new ArrayList<SubscriptionInformation>();
		long currentTimeInMillis = System.currentTimeMillis();
		if (pkg != null && addOnSubscription != null) {
			if (PkgType.BASE == pkg.getPackageType() || PkgType.PROMOTIONAL == pkg.getPackageType()) {
				throw new OperationFailedException("Package : " + pkg.getName() + " is of Base/Promotional type for subscriber Id: " + sprInfo.getSubscriberIdentity()+". It cannot contain subscription Id" ,ResultCode.INVALID_INPUT_PARAMETER);
			} else {
				
				SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());
				
				if (subscriptionPackage == null) {
					throw new OperationFailedException("Subscription package not found for package id: " + addOnSubscription.getPackageId()
							+  ", subscription id: " + addOnSubscription.getId()+ ", subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
				}
				
				if (addOnSubscription.getPackageId().equals(pkgId) == false) {
					throw new OperationFailedException("SubscriptionId: " + subscriptionId + " and addOnId: " + pkgId + " are not related for subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
				}
				subscriptionInformations.add(createAddOnSubscriptionInformation(addOnSubscription, usage, currentTimeInMillis, subscriptionPackage, productOffer.getId()));
			}

		} else if (pkg != null && addOnSubscription == null) {

			if (PkgType.BASE == pkg.getPackageType()) {
				if(Objects.isNull(productOffer.getDataServicePkgData()) || (Objects.nonNull(productOffer.getDataServicePkgData()) && productOffer.getDataServicePkgData().getId().equals(pkg.getId()) == false)){
					throw new OperationFailedException("Base Package :"+pkg.getName()+" is not associated with subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
				}
				subscriptionInformations.add(createPkgSubscriptionInformation(usage, pkg, currentTimeInMillis,productOffer.getId()));
			} else if (PkgType.PROMOTIONAL == pkg.getPackageType()) {
				subscriptionInformations.add(createPkgSubscriptionInformation(usage, pkg, currentTimeInMillis,productOffer.getId()));
			} else {
				if(Maps.isNullOrEmpty(addOnSubscriptions)){
					throw new OperationFailedException("No subscription found for subscriber identity: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
				}
				
				boolean isFound = false;
				for (Subscription subscription : addOnSubscriptions.values()) {
					
					if (subscription.getPackageId().equals(pkg.getId())) {

						SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(subscription.getPackageId());

						if (subscriptionPackage == null) {
							getLogger().warn(MODULE, "Skipping subscription(id:"+subscription.getId()+") for subscriber ID: " + subscription.getSubscriberIdentity() 
									+ ". Reason: Subscription package not found for package id: " + subscription.getPackageId());
							continue;
						}
						
						subscriptionInformations.add(createAddOnSubscriptionInformation(subscription,
								usage, currentTimeInMillis, subscriptionPackage,productOffer.getId()));
						isFound = true;
					}
				}
				if (isFound == false) {
					throw new OperationFailedException("No subscription found for package: " + pkg.getName() + "(" + pkgId + ") for subscriber ID: "+sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
				}
			}

		} else if (pkg == null && addOnSubscription != null) {
			
			SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());
			
			if (subscriptionPackage == null) {
				throw new OperationFailedException("Subscription package not found for package id: " + addOnSubscription.getPackageId()
						+  ", subscription id: " + addOnSubscription.getId()+ ", subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
			}
			
			subscriptionInformations.add(createAddOnSubscriptionInformation(addOnSubscription, usage, currentTimeInMillis, subscriptionPackage,productOffer.getId()));
		} else {
			throw new OperationFailedException("No criteria provided for fetching balance", ResultCode.INVALID_INPUT_PARAMETER);
		}

		return subscriptionInformations;
	}

	/**
	 * This method is used when balance from all subscribed package is needed.
	 * 
	 */

	public List<SubscriptionInformation> getBalance(SPRInfo sprInfo, Map<String, Subscription> addOnSubscriptions,
			TransactionFactory transactionFactory) throws OperationFailedException {

		String productOfferName = sprInfo.getProductOffer();
		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		List<SubscriptionInformation> subscriptionInformations = new ArrayList<SubscriptionInformation>();
		Map<String, Map<String, SubscriberUsage>> usage = getUsage(subscriberIdentity, addOnSubscriptions, transactionFactory);

		if (Maps.isNullOrEmpty(usage)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "No usage found for subscriber ID: " + subscriberIdentity);
			}
		} else {
			if (getLogger().isDebugLogLevel()) {
				StringBuilder builder = new StringBuilder();
				builder.append("Usage for subscriber ID: " + subscriberIdentity + CommonConstants.LINE_SEPARATOR);
				for (Entry<String, Map<String, SubscriberUsage>> usageEntry : usage.entrySet()) {
					builder.append("Package or SubscriptionId: " + usageEntry.getKey() + CommonConstants.LINE_SEPARATOR);
					for (Entry<String, SubscriberUsage> subscriberUsage : usageEntry.getValue().entrySet()) {
						builder.append("Key: " + subscriberUsage.getKey() + CommonConstants.LINE_SEPARATOR);
						builder.append("SubscriberUsage: " + subscriberUsage.getValue() + CommonConstants.LINE_SEPARATOR);
					}
				}
				getLogger().debug(MODULE, builder.toString());
			}
		}

		long currentTimeInMillis = System.currentTimeMillis();
		ProductOffer productOffer = policyRepository.getProductOffer().byName(productOfferName);

		if (productOffer == null) {
			throw new OperationFailedException(
					"No product offer found with pkgName: " + productOfferName+" for subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
		}

		UserPackage pkg = productOffer.getDataServicePkgData();

		if (pkg == null) {
			throw new OperationFailedException(
					"No package associated with product offer: " + productOfferName+" for subscriber ID: " + sprInfo.getSubscriberIdentity(),ResultCode.NOT_FOUND);
		}

		subscriptionInformations.add(createPkgSubscriptionInformation(usage, pkg, currentTimeInMillis,productOffer.getId()));


		for (Subscription addOnSubscription : addOnSubscriptions.values()) {
			SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());
			
			if (subscriptionPackage == null) {
				getLogger().warn(MODULE, "Skipping subscription(id:" + addOnSubscription.getId() +
						") for subscriber ID: " + subscriberIdentity + ". Reason: Subscription package not found for id: "
						+ addOnSubscription.getPackageId());
				continue;
			}
			
			subscriptionInformations.add(createAddOnSubscriptionInformation(addOnSubscription, usage, currentTimeInMillis, subscriptionPackage,productOffer.getId()));
		}
		
		//Adding promotional package balance information
		for (PromotionalPackage promotionalPackage : policyRepository.getPromotionalPackages()) {
			subscriptionInformations.add(createPkgSubscriptionInformation(usage, promotionalPackage, currentTimeInMillis,productOffer.getId()));
		}

		return subscriptionInformations;
	}

	private SubscriptionInformation createPkgSubscriptionInformation(@Nullable Map<String, Map<String, SubscriberUsage>> usage,
																	 UserPackage pkg, long currentTimeInMillis,String productOfferId) {

		SubscriptionInformation subscriptionInformation = new SubscriptionInformation();
		subscriptionInformation.setPackageId(pkg.getId());
		subscriptionInformation.setPackageName(pkg.getName());
		subscriptionInformation.setPackageType(pkg.getPackageType().name());
		subscriptionInformation.setQuotaProfileType(pkg.getQuotaProfileType());

		if (pkg.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {

			List<QuotaProfileBalance> profileBalance;
			if (usage == null) {
				profileBalance = createQuotaBalance(pkg, null, currentTimeInMillis,productOfferId);
			} else {
				profileBalance = createQuotaBalance(pkg, usage.get(pkg.getId()), currentTimeInMillis,productOfferId);
			}
			subscriptionInformation.setQuotaProfileBalances(profileBalance);
		}

		return subscriptionInformation;
	}

	private SubscriptionInformation createAddOnSubscriptionInformation(Subscription addOnSubscription,
																	   @Nullable Map<String, Map<String, SubscriberUsage>> usage, long currentTimeInMillis, SubscriptionPackage subscriptionPackage, String productOfferId) {

		SubscriptionInformation subscriptionInformation = new SubscriptionInformation();
		subscriptionInformation.setPackageName(subscriptionPackage.getName());
		subscriptionInformation.setPackageDescription(subscriptionPackage.getDescription());
		subscriptionInformation.setPackageId(subscriptionPackage.getId());
		subscriptionInformation.setPackageType(subscriptionPackage.getPackageType().name());
		subscriptionInformation.setAddonSubscriptionId(addOnSubscription.getId());
		subscriptionInformation.setStartTime(addOnSubscription.getStartTime());
		subscriptionInformation.setEndTime(addOnSubscription.getEndTime());
		subscriptionInformation.setAddOnStatus(addOnSubscription.getStatus());
		subscriptionInformation.setQuotaProfileType(subscriptionPackage.getQuotaProfileType());

		if (subscriptionPackage.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
			if (usage == null) {
				subscriptionInformation
						.setQuotaProfileBalances(createQuotaBalance(subscriptionPackage, null, currentTimeInMillis, productOfferId));
			} else {
				subscriptionInformation.setQuotaProfileBalances(createQuotaBalance(subscriptionPackage, usage
						.get(addOnSubscription.getId()), currentTimeInMillis, productOfferId));
			}
		}
		return subscriptionInformation;
	}

	private List<QuotaProfileBalance> createQuotaBalance(UserPackage pkg, @Nullable Map<String, SubscriberUsage> usage,
														 long currentTimeInMillis, String productOfferId) {

		List<QuotaProfileBalance> quotaProfileBalanceList = new ArrayList<QuotaProfileBalance>();
		List<QuotaProfile> quotaProfileDataList = pkg.getQuotaProfiles();
		for (QuotaProfile quotaProfile : quotaProfileDataList) {
			QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
			quotaProfileBalance.setQuotaProfileId(quotaProfile.getId());
			quotaProfileBalance.setQuotaProfileName(quotaProfile.getName());

			Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = ((UMBaseQuotaProfile)quotaProfile).getBalanceLevel().getBalanceLevelQuotaProfileDetail(quotaProfile);
			if (Maps.isNullOrEmpty(fupLevelServiceWiseQuotaProfileDetails)) {
				getLogger().warn(MODULE, "Skipping creating balance for quota profile: " + quotaProfile.getName() + ". Reason: FUP quota detail not configured for mentio");
				continue;
			}
			
			for (QuotaProfileDetail quotaProfileDetail : fupLevelServiceWiseQuotaProfileDetails.values()) {

				SubscriberUsage subscriberUsage = null;
				if (usage != null) {
					subscriberUsage = usage.get(((UMBaseQuotaProfileDetail) quotaProfileDetail).getUsageKey());
				}

				if (subscriberUsage == null) {
					SubscriberUsageBuilder usageBuilder = new SubscriberUsage.SubscriberUsageBuilder("-1", quotaProfile.getId(), "", quotaProfileDetail
							.getServiceId(), pkg.getId(),productOfferId);
					subscriberUsage = usageBuilder.withAllTypeUsage(0, 0, 0, 0).build();
				} else {
					validateAndSetSubscriberUsage(subscriberUsage, currentTimeInMillis);
				}

				createBalance(quotaProfileBalance, quotaProfileDetail, subscriberUsage);
			}
			
			quotaProfileBalanceList.add(quotaProfileBalance);
		}

		return quotaProfileBalanceList;
	}

	private void createBalance(QuotaProfileBalance quotaProfileBalance, QuotaProfileDetail quotaProfileDetail, SubscriberUsage subscriberUsage) {
	
		if (((UMBaseQuotaProfileDetail)quotaProfileDetail).getDailyAllowedUsage() != null) {
			
			quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail)quotaProfileDetail).getDailyAllowedUsage()
					, AggregationKey.DAILY));
		}
		
		if (((UMBaseQuotaProfileDetail)quotaProfileDetail).getWeeklyAllowedUsage() != null) {
			
			quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail)quotaProfileDetail).getWeeklyAllowedUsage()
					, AggregationKey.WEEKLY));
		}
		
		if (((UMBaseQuotaProfileDetail)quotaProfileDetail).getBillingCycleAllowedUsage() != null) {
			
			quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail)quotaProfileDetail).getBillingCycleAllowedUsage()
					, AggregationKey.BILLING_CYCLE));
		}
		
		if ( ((UMBaseQuotaProfileDetail)quotaProfileDetail).getCustomAllowedUsage() != null) {
			
			quotaProfileBalance.addBalanceInformation(createUsageInformationDetails(subscriberUsage, quotaProfileDetail, ((UMBaseQuotaProfileDetail)quotaProfileDetail).getCustomAllowedUsage()
					, AggregationKey.CUSTOM));
		}
		
	}

	private UsageInfo createUsageInformationDetails(SubscriberUsage subscriberUsage, QuotaProfileDetail quotaProfileDetail, AllowedUsage allowedUsage, AggregationKey aggregationKey) {
		
					UsageInfo usageInformation = new UsageInfo();
		usageInformation.setServiceId(quotaProfileDetail.getServiceId());
		usageInformation.setServiceName(((UMBaseQuotaProfileDetail)quotaProfileDetail).getServiceName());

		Usage allowedUsageWS = getAllowedUsage(allowedUsage);
		usageInformation.setAllowedUsage(allowedUsageWS);
		usageInformation.setBalance(getBalance(allowedUsageWS, subscriberUsage, aggregationKey));
					usageInformation.setCurretUsage(getCurrentUsage(subscriberUsage, aggregationKey));
		usageInformation.setAggregationKey(aggregationKey);

		return usageInformation;
				}

	private Usage getAllowedUsage(AllowedUsage allowedUsage) {
		
		long uploadInBytes = allowedUsage.getUploadInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getUploadInBytes();
		long downloadInBytes = allowedUsage.getDownloadInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getDownloadInBytes();
		long totalInBytes = allowedUsage.getTotalInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getTotalInBytes();
		long timeInSeconds = allowedUsage.getTimeInSeconds()== 0 ? UNLIMITED_USAGE : allowedUsage.getTimeInSeconds();
		
		return new Usage(uploadInBytes, downloadInBytes, totalInBytes
				, timeInSeconds);
	}

	private void validateAndSetSubscriberUsage(SubscriberUsage subscriberUsage, long currentTimeInMillis) {
		if (subscriberUsage.getDailyResetTime() < currentTimeInMillis) {
			subscriberUsage.setDailyTotal(0);
			subscriberUsage.setDailyDownload(0);
			subscriberUsage.setDailyUpload(0);
			subscriberUsage.setDailyTime(0);
		}

		if (subscriberUsage.getWeeklyResetTime() < currentTimeInMillis) {
			subscriberUsage.setWeeklyTotal(0);
			subscriberUsage.setWeeklyDownload(0);
			subscriberUsage.setWeeklyUpload(0);
			subscriberUsage.setWeeklyTime(0);
		}
		if (subscriberUsage.getBillingCycleResetTime() < currentTimeInMillis) {
			subscriberUsage.setBillingCycleTotal(0);
			subscriberUsage.setBillingCycleDownload(0);
			subscriberUsage.setBillingCycleUpload(0);
			subscriberUsage.setBillingCycleTime(0);
		}
		if (subscriberUsage.getCustomResetTime() < currentTimeInMillis) {
			subscriberUsage.setCustomTotal(0);
			subscriberUsage.setCustomDownload(0);
			subscriberUsage.setCustomUpload(0);
			subscriberUsage.setCustomTime(0);
		}
	}

	private Usage getCurrentUsage(SubscriberUsage subscriberUsage, AggregationKey aggregationKey) {

		switch (aggregationKey) {
			case DAILY:

				return new Usage(subscriberUsage.getDailyUpload(), subscriberUsage.getDailyDownload(), subscriberUsage.getDailyTotal()
						, subscriberUsage.getDailyTime());
			case WEEKLY:

				return new Usage(subscriberUsage.getWeeklyUpload(), subscriberUsage.getWeeklyDownload(), subscriberUsage.getWeeklyTotal()
						, subscriberUsage.getWeeklyTime());
			case CUSTOM:

				return new Usage(subscriberUsage.getCustomUpload(), subscriberUsage.getCustomDownload(), subscriberUsage.getCustomTotal()
						, subscriberUsage.getCustomTime());
			case BILLING_CYCLE:

				return new Usage(subscriberUsage.getBillingCycleUpload(), subscriberUsage.getBillingCycleDownload(), subscriberUsage.getBillingCycleTotal()
						, subscriberUsage.getBillingCycleTime());

		}
		return null;
	}

	private Usage getBalance(Usage allowedUsage,
			SubscriberUsage subscriberUsage,
			AggregationKey aggregationKey) {

		long usedTotal = 0;
		long usedUpload = 0;
		long usedDownload = 0;
		long usedTime = 0;

		switch (aggregationKey) {
			case DAILY:

				usedTotal = subscriberUsage.getDailyTotal();
				usedUpload = subscriberUsage.getDailyUpload();
				usedDownload = subscriberUsage.getDailyDownload();
				usedTime = subscriberUsage.getDailyTime();
				break;
			case WEEKLY:

				usedTotal = subscriberUsage.getWeeklyTotal();
				usedUpload = subscriberUsage.getWeeklyUpload();
				usedDownload = subscriberUsage.getWeeklyDownload();
				usedTime = subscriberUsage.getWeeklyTime();
				break;
			case BILLING_CYCLE:

				usedTotal = subscriberUsage.getBillingCycleTotal();
				usedUpload = subscriberUsage.getBillingCycleUpload();
				usedDownload = subscriberUsage.getBillingCycleDownload();
				usedTime = subscriberUsage.getBillingCycleTime();
				break;
			case CUSTOM:

				usedTotal = subscriberUsage.getCustomTotal();
				usedUpload = subscriberUsage.getCustomUpload();
				usedDownload = subscriberUsage.getCustomDownload();
				usedTime = subscriberUsage.getCustomTime();
				break;
		}

		long totalOctets = allowedUsage.getTotalOctets() == UNLIMITED_USAGE ? UNLIMITED_USAGE
				: allowedUsage.getTotalOctets() - usedTotal;
		long uploadOctets = allowedUsage.getUploadOctets() == UNLIMITED_USAGE ? UNLIMITED_USAGE
				: allowedUsage.getUploadOctets() - usedUpload;
		long downloadOctets = allowedUsage.getDownloadOctets() == UNLIMITED_USAGE ? UNLIMITED_USAGE
				: allowedUsage.getDownloadOctets() - usedDownload;
		long time = allowedUsage.getTime() == UNLIMITED_USAGE ? UNLIMITED_USAGE
				: allowedUsage.getTime() - usedTime;

		return new Usage(uploadOctets, downloadOctets, totalOctets, time);
	}

	
	public void stop() {
		//NOT REQUIRED IN SYNC OPERATION ONLY REQUIRED IN BATCH
	}

	@Override
	public void process(BatchOperationData batchOperationData, TransactionFactory transactionFactory) throws OperationFailedException {

		if (batchOperationData.getOperation() == BatchOperationData.REPLACE) {
			
			replace(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getUsage()), transactionFactory);
			
		} else  if (batchOperationData.getOperation() == BatchOperationData.INSERT) {
			
			insert(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getUsage()), transactionFactory);
			
		} else  if (batchOperationData.getOperation() == BatchOperationData.ADD_TO_EXISTING) {
			
			addToExisting(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getUsage()), transactionFactory);
		}
	}

}

