package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.util.RenewalIntervalUtility;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.ZERO_VALUE;
import static com.elitecore.corenetvertex.spr.util.SPRUtil.toStringSQLException;

public class ABMFOperation implements SingleRecordOperation<ABMFBatchOperation.BatchOperationData> {

    protected static final int QUERY_TIMEOUT_ERROR_CODE = 1013;
    protected static final String RESERVE_BALANCE_QUERY = "UPDATE TBLM_DATA_BALANCE SET"
            + " RESERVATION_VOLUME = RESERVATION_VOLUME + ?,"
            + " RESERVATION_TIME = RESERVATION_TIME + ? ,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";
    protected static final String REPORT_BALANCE_QUERY = "UPDATE TBLM_DATA_BALANCE SET"
            + " DAILY_VOLUME = DAILY_VOLUME + ?,"
            + " DAILY_TIME = DAILY_TIME + ?,"
            + " WEEKLY_VOLUME = WEEKLY_VOLUME + ?,"
            + " WEEKLY_TIME = WEEKLY_TIME + ?,"
            + " BILLING_CYCLE_AVAIL_VOLUME = BILLING_CYCLE_AVAIL_VOLUME - ?,"
            + " BILLING_CYCLE_AVAIL_TIME = BILLING_CYCLE_AVAIL_TIME - ?,"
            + " RESERVATION_VOLUME = RESERVATION_VOLUME - ?,"
            + " RESERVATION_TIME = RESERVATION_TIME - ? ,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";
    protected static final String REPORT_AND_RESERVE_BALANCE_QUERY = "UPDATE TBLM_DATA_BALANCE SET"
            + " DAILY_VOLUME = DAILY_VOLUME + ?,"
            + " DAILY_TIME = DAILY_TIME + ?,"
            + " WEEKLY_VOLUME = WEEKLY_VOLUME + ?,"
            + " WEEKLY_TIME = WEEKLY_TIME + ?,"
            + " BILLING_CYCLE_AVAIL_VOLUME = BILLING_CYCLE_AVAIL_VOLUME - ?,"
            + " BILLING_CYCLE_AVAIL_TIME = BILLING_CYCLE_AVAIL_TIME - ?,"
            + " RESERVATION_VOLUME = RESERVATION_VOLUME + ?,"
            + " RESERVATION_TIME = RESERVATION_TIME + ? ,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";
    protected static final String RESET_BALANCE_QUERY = "UPDATE TBLM_DATA_BALANCE SET"
            + " DAILY_VOLUME=?,"
            + " DAILY_TIME = ?,"
            + " WEEKLY_VOLUME = ?,"
            + " WEEKLY_TIME = ?,"
            + " BILLING_CYCLE_AVAIL_VOLUME = ?,"
            + " BILLING_CYCLE_AVAIL_TIME = ?,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " QUOTA_EXPIRY_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";
    protected static final String DIRECT_DEBIT_QUERY = "UPDATE TBLM_DATA_BALANCE SET"
            + " DAILY_VOLUME = DAILY_VOLUME + ?,"
            + " DAILY_TIME = DAILY_TIME + ?,"
            + " WEEKLY_VOLUME = WEEKLY_VOLUME + ?,"
            + " WEEKLY_TIME = WEEKLY_TIME + ?,"
            + " BILLING_CYCLE_AVAIL_VOLUME = BILLING_CYCLE_AVAIL_VOLUME - ?,"
            + " BILLING_CYCLE_AVAIL_TIME = BILLING_CYCLE_AVAIL_TIME - ?,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID=?";
    protected static final String RESET_QUOTA = "UPDATE TBLM_DATA_BALANCE"
            + " SET"
            + " DAILY_VOLUME=?,"
            + " DAILY_TIME=?,"
            + " WEEKLY_VOLUME = ?,"
            + " WEEKLY_TIME = ?,"
            + " BILLING_CYCLE_TOTAL_VOLUME = ?,"
            + " BILLING_CYCLE_AVAIL_VOLUME = ?,"
            + " BILLING_CYCLE_TOTAL_TIME = ?,"
            + " BILLING_CYCLE_AVAIL_TIME = ?,"
            + " DAILY_RESET_TIME = ?,"
            + " WEEKLY_RESET_TIME = ?,"
            + " QUOTA_EXPIRY_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE SUBSCRIBER_ID=? AND PRODUCT_OFFER_ID=? AND QUOTA_PROFILE_ID=?";
    protected static final String EXPIRE_QUOTA_QUERY = "UPDATE TBLM_DATA_BALANCE"
            + " SET"
            + " QUOTA_EXPIRY_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE SUBSCRIBER_ID=? AND PRODUCT_OFFER_ID=?";
    protected static final String DELETE_DATA_BALANCE_QUERY = "DELETE FROM TBLM_DATA_BALANCE WHERE SUBSCRIBER_ID = ?";
    protected static final String DATA_BALANCE_HISTORY_QUERY = "INSERT INTO " +
            "TBLM_DATA_BALANCE_HISTORY " +
            "(CREATE_DATE, ID," +
            "  SUBSCRIBER_ID," +
            "  PACKAGE_ID," +
            "  SUBSCRIPTION_ID," +
            "  QUOTA_PROFILE_ID," +
            "  PRODUCT_OFFER_ID," +
            "  DATA_SERVICE_TYPE_ID," +
            "  RATING_GROUP_ID," +
            "  BALANCE_LEVEL," +
            "  QUOTA_EXPIRY_TIME," +
            "  BILLING_CYCLE_TOTAL_VOLUME," +
            "  BILLING_CYCLE_AVAIL_VOLUME," +
            "  BILLING_CYCLE_TOTAL_TIME," +
            "  BILLING_CYCLE_AVAIL_TIME," +
            "  DAILY_RESET_TIME," +
            "  DAILY_VOLUME," +
            "  DAILY_TIME," +
            "  WEEKLY_RESET_TIME," +
            "  WEEKLY_VOLUME," +
            "  WEEKLY_TIME," +
            "  RESERVATION_VOLUME," +
            "  RESERVATION_TIME," +
            "  LAST_UPDATE_TIME," +
            "  START_TIME," +
            "  STATUS," +
            "  RENEWAL_INTERVAL," +
            "  CARRY_FORWARD_VOLUME," +
            "  CARRY_FORWARD_TIME," +
            "  CARRY_FORWARD_STATUS)" +
            " SELECT" +
            " CURRENT_TIMESTAMP, " +
            " ?," +
            " SUBSCRIBER_ID," +
            " PACKAGE_ID," +
            " SUBSCRIPTION_ID," +
            " QUOTA_PROFILE_ID," +
            " PRODUCT_OFFER_ID," +
            " DATA_SERVICE_TYPE_ID," +
            " RATING_GROUP_ID," +
            " BALANCE_LEVEL," +
            " QUOTA_EXPIRY_TIME," +
            " BILLING_CYCLE_TOTAL_VOLUME," +
            " BILLING_CYCLE_AVAIL_VOLUME," +
            " BILLING_CYCLE_TOTAL_TIME," +
            " BILLING_CYCLE_AVAIL_TIME," +
            " DAILY_RESET_TIME," +
            " DAILY_VOLUME," +
            " DAILY_TIME," +
            " WEEKLY_RESET_TIME," +
            " WEEKLY_VOLUME," +
            " WEEKLY_TIME," +
            " RESERVATION_VOLUME," +
            " RESERVATION_TIME," +
            " LAST_UPDATE_TIME," +
            " START_TIME," +
            " STATUS," +
            " RENEWAL_INTERVAL," +
            " CARRY_FORWARD_VOLUME," +
            " CARRY_FORWARD_TIME," +
            " CARRY_FORWARD_STATUS" +
            " FROM TBLM_DATA_BALANCE" +
            " WHERE SUBSCRIBER_ID = ?";
    protected static final String UPDATE_NEXT_BILL_CYCLE_BALANCE_QUERY = "UPDATE TBLM_DATA_BALANCE SET"
            + " QUOTA_EXPIRY_TIME = ?,"
            + " BILLING_CYCLE_TOTAL_VOLUME = ?,"
            + " BILLING_CYCLE_AVAIL_VOLUME = ?,"
            + " BILLING_CYCLE_TOTAL_TIME = ?,"
            + " BILLING_CYCLE_AVAIL_TIME = ?,"
            + " LAST_UPDATE_TIME = CURRENT_TIMESTAMP "
            + " WHERE ID = ?";
    private static final String MODULE = "ABMF-OPR";
    private static final String INSERT_BALANCE_QUERY = "INSERT INTO TBLM_DATA_BALANCE (ID," +
            "SUBSCRIBER_ID," +
            "PACKAGE_ID," +
            "SUBSCRIPTION_ID," +
            "QUOTA_PROFILE_ID," +
            "DATA_SERVICE_TYPE_ID," +
            "RATING_GROUP_ID," +
            "BALANCE_LEVEL," +
            "BILLING_CYCLE_TOTAL_VOLUME," +
            "BILLING_CYCLE_AVAIL_VOLUME," +
            "BILLING_CYCLE_TOTAL_TIME," +
            "BILLING_CYCLE_AVAIL_TIME," +
            "START_TIME," +
            "QUOTA_EXPIRY_TIME," +
            "STATUS," +
            "DAILY_VOLUME," +
            "DAILY_TIME," +
            "DAILY_RESET_TIME," +
            "WEEKLY_VOLUME," +
            "WEEKLY_TIME," +
            "WEEKLY_RESET_TIME," +
            "RESERVATION_VOLUME," +
            "RESERVATION_TIME," +
            "LAST_UPDATE_TIME," +
            "RENEWAL_INTERVAL," +
            "PRODUCT_OFFER_ID," +
            "CARRY_FORWARD_STATUS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?)";
    private static final String TABLE_NAME = "TBLM_DATA_BALANCE";
    private static final String SELECT_BALANCE_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE SUBSCRIBER_ID = ? ";
    private ABMFResetOperations abmfResetOperations;
    protected AlertListener alertListener;
    private PolicyRepository policyRepository;
    private int queryTimeout;
    private int maxQueryTimeoutCount;
    private AtomicInteger iTotalQueryTimeoutCount;
    private TimeSource timeSource;

    public ABMFOperation(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout) {
        this(alertListener, policyRepository, queryTimeout, 200);
    }

    public ABMFOperation(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout, int maxQueryTimeoutCount) {
        this(alertListener, policyRepository, queryTimeout, maxQueryTimeoutCount, TimeSource.systemTimeSource(), new ABMFResetOperations(new BalanceResetOperation(policyRepository, TimeSource.systemTimeSource()), new CarryForwardOperation(policyRepository, TimeSource.systemTimeSource()), new DataBalanceResetDBOperation(queryTimeout, alertListener), TimeSource.systemTimeSource(),new DataBalanceOperationFactory()));
    }

    public ABMFOperation(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout, int maxQueryTimeoutCount, TimeSource timeSource, ABMFResetOperations abmfResetOperations) {
        this.alertListener = alertListener;
        this.policyRepository = policyRepository;
        this.queryTimeout = queryTimeout;
        this.maxQueryTimeoutCount = maxQueryTimeoutCount;
        this.iTotalQueryTimeoutCount = new AtomicInteger(0);
        this.timeSource = timeSource;
        this.abmfResetOperations = abmfResetOperations;
    }

    public void init() {
        ///no need to initialize. this method is required by ABMFBatchOperation
    }

    public SubscriptionNonMonitoryBalance addBalance(String subscriberIdentity, Subscription subscription, ProductOffer productOffer, Transaction transaction, Integer billingDate) throws OperationFailedException {
        PreparedStatement psforInsert = null;

        try {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Adding non-monetary balance for subscriber-identifier : " + subscriberIdentity);
            }

            if (Objects.isNull(productOffer)) {
                throw new OperationFailedException("Package is null");
            }

            if (productOffer.getDataServicePkgData() == null) {
                return null;
            }

            if (QuotaProfileType.RnC_BASED != productOffer.getDataServicePkgData().getQuotaProfileType()) {
                return null;
            }

            psforInsert = transaction.prepareStatement(INSERT_BALANCE_QUERY);

            List<QuotaProfile> quotaProfiles = productOffer.getDataServicePkgData().getQuotaProfiles();

            SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = new SubscriptionNonMonitoryBalance(productOffer.getDataServicePkgData().getId());

            for (QuotaProfile quotaProfile : quotaProfiles) {

                for (Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails : quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()) {

                    for (Map.Entry<String, QuotaProfileDetail> entry : fupLevelServiceWiseQuotaProfileDetails.entrySet()) {
                        subscriptionNonMonitoryBalance.addBalance(insert(subscriberIdentity, subscription, psforInsert, productOffer.getDataServicePkgData().getId(), quotaProfile, entry, billingDate, productOffer.getId()));
                    }
                }
            }

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Insert balance operation successfully completed for subscriber-identifier : " + subscriberIdentity);
            }

            return subscriptionNonMonitoryBalance;
        } catch (SQLException e) {
            handleGetUsageError(e, transaction);
        } catch (Exception e) {
            handleGetUsageError(e);
        } finally {
            closeQuietly(psforInsert);
        }

        throw new AssertionError("Control should not be reach here");
    }

    public SubscriptionNonMonitoryBalance addBalance(String subscriberIdentity, Subscription subscription, QuotaTopUp quotaTopUp, Transaction transaction) throws OperationFailedException {
        PreparedStatement psforInsert = null;

        try {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Adding non-monetary balance for subscriber-identifier : " + subscriberIdentity);
            }


            psforInsert = transaction.prepareStatement(INSERT_BALANCE_QUERY);

            QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();
            SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = new SubscriptionNonMonitoryBalance(quotaTopUp.getId());

            for (Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails : quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()) {

                for (Map.Entry<String, QuotaProfileDetail> entry : fupLevelServiceWiseQuotaProfileDetails.entrySet()) {
                    subscriptionNonMonitoryBalance.addBalance(insert(subscriberIdentity, subscription, psforInsert, quotaTopUp.getId(), quotaProfile, entry, null, null));
                }
            }

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Insert balance operation successfully completed for subscriber-identifier : " + subscriberIdentity);
            }

            return subscriptionNonMonitoryBalance;
        } catch (SQLException e) {
            handleGetUsageError(e, transaction);
        } catch (Exception e) {
            handleGetUsageError(e);
        } finally {
            closeQuietly(psforInsert);
        }

        throw new AssertionError("Control should not be reach here");
    }

    private NonMonetoryBalance insert(String subscriberIdentity,
                                      Subscription subscription,
                                      PreparedStatement psforInsert,
                                      String packageId,
                                      QuotaProfile quotaProfile,
                                      Map.Entry<String, QuotaProfileDetail> entry,
                                      Integer billDay,
                                      String productOfferId) throws Exception {
        RncProfileDetail rncProfileDetail = (RncProfileDetail) entry.getValue();

        long totalVolume = rncProfileDetail.getUnitType().getVolumeInBytes(rncProfileDetail.getBillingCycleAllowedUsage());
        long totalTime = rncProfileDetail.getBillingCycleAllowedUsage().getTimeInSeconds();

        Timestamp quotaStartTime;
        Timestamp dailyResetTime;
        Timestamp weeklyResetTime;
        Timestamp quotaResetTime;

        if (Objects.isNull(subscription)) {
            quotaStartTime = new Timestamp(timeSource.currentTimeInMillis());
            dailyResetTime = new Timestamp(ResetTimeUtility.calculateDailyResetTime());
            weeklyResetTime = new Timestamp(ResetTimeUtility.calculateWeeklyResetTime());
            if (quotaProfile.getRenewalInterval() != 0) {
                quotaResetTime = new Timestamp(quotaProfile.getRenewalIntervalUnit().addTime(quotaStartTime.getTime(), quotaProfile.getRenewalInterval()));
            } else {
                quotaResetTime = new Timestamp(ResetTimeUtility.calculateQuotaResetTime());
            }
        } else {
            quotaStartTime = subscription.getStartTime();
            dailyResetTime = new Timestamp(ResetTimeUtility.calculateDailyResetTime(subscription.getStartTime().getTime()));
            weeklyResetTime = new Timestamp(ResetTimeUtility.calculateWeeklyResetTime(subscription.getStartTime().getTime()));
            quotaResetTime = subscription.getEndTime();
        }

        Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(quotaProfile.getRenewalInterval(), quotaProfile.getRenewalIntervalUnit(), quotaResetTime, quotaStartTime, billDay);

        if (quotaProfile.getProration()) {
            totalVolume = RenewalIntervalUtility.getProratedQuota(totalVolume, quotaStartTime, balanceExpiry, quotaProfile.getRenewalIntervalUnit(), quotaProfile.getRenewalInterval());
            totalTime = RenewalIntervalUtility.getProratedQuota(totalTime, quotaStartTime, balanceExpiry, quotaProfile.getRenewalIntervalUnit(), quotaProfile.getRenewalInterval());
        }

        String status;
        if (Objects.equals(RenewalIntervalUnit.TILL_BILL_DATE, quotaProfile.getRenewalIntervalUnit()) == false && quotaProfile.getRenewalInterval() == 0) {
            status = ResetBalanceStatus.NOT_RESET.name();
        } else if (quotaResetTime.getTime() < balanceExpiry.getTime()) {
            status = ResetBalanceStatus.NOT_RESET.name();
        } else {
            status = ResetBalanceStatus.RESET.name();
        }

        String carryForwardStatus;
        if (quotaProfile.getCarryForward() &&
                (((totalVolume == CommonConstants.QUOTA_UNDEFINED || totalVolume == CommonConstants.QUOTA_UNLIMITED) == false) ||
                        (totalTime == CommonConstants.QUOTA_UNDEFINED || totalTime == CommonConstants.QUOTA_UNLIMITED) == false
                ) && rncProfileDetail.getFupLevel() <= quotaProfile.getBalanceLevel().getFupLevel()) {
            carryForwardStatus = CarryForwardStatus.CARRY_FORWARD.name();
        }else{
            carryForwardStatus = CarryForwardStatus.NOT_CARRY_FORWARD.name();
        }

        psforInsert.setQueryTimeout(queryTimeout);

        String id = UUID.randomUUID().toString();
        psforInsert.setString(1, id);
        psforInsert.setString(2, subscriberIdentity);
        psforInsert.setString(3, packageId);
        if (Objects.isNull(subscription)) {
            psforInsert.setNull(4, Types.VARCHAR);
        } else {
            psforInsert.setString(4, subscription.getId());
        }
        psforInsert.setString(5, quotaProfile.getId());
        psforInsert.setLong(6, rncProfileDetail.getDataServiceType().getServiceIdentifier());
        psforInsert.setLong(7, rncProfileDetail.getRatingGroup().getIdentifier());
        psforInsert.setInt(8, rncProfileDetail.getFupLevel());
        psforInsert.setLong(9, totalVolume);
        psforInsert.setLong(10, totalVolume);
        psforInsert.setLong(11, totalTime);
        psforInsert.setLong(12, totalTime);
        psforInsert.setTimestamp(13, quotaStartTime);
        psforInsert.setTimestamp(14, balanceExpiry);
        psforInsert.setString(15, status);
        psforInsert.setLong(16, ZERO_VALUE);
        psforInsert.setLong(17, ZERO_VALUE);
        psforInsert.setTimestamp(18, dailyResetTime);
        psforInsert.setLong(19, ZERO_VALUE);
        psforInsert.setLong(20, ZERO_VALUE);
        psforInsert.setTimestamp(21, weeklyResetTime);
        psforInsert.setLong(22, ZERO_VALUE);
        psforInsert.setLong(23, ZERO_VALUE);
        String renewalInterval = quotaProfile.getRenewalInterval() + ":" + quotaProfile.getRenewalIntervalUnit();
        psforInsert.setString(24, renewalInterval);
        psforInsert.setString(25, productOfferId);
        psforInsert.setString(26, carryForwardStatus);

        long queryExecutionTime = getCurrentTime();
        psforInsert.executeUpdate();
        queryExecutionTime = getCurrentTime() - queryExecutionTime;
        if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

            alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                    "DB Query execution time is high while inserting balance information. "
                            + "Last Query execution time: " + queryExecutionTime + " ms");

            if (getLogger().isWarnLogLevel()) {//NOSONAR
                getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                        + queryExecutionTime + " milliseconds.");
            }
        }

        psforInsert.clearParameters();
        return new NonMonetoryBalance(id,
                rncProfileDetail.getDataServiceType().getServiceIdentifier(),
                packageId,
                quotaProfile.getId(),
                rncProfileDetail.getRatingGroup().getIdentifier(),
                rncProfileDetail.getFupLevel(),
                subscriberIdentity,
                Objects.nonNull(subscription) ? subscription.getId() : null,
                totalVolume,
                totalVolume,
                totalTime,
                totalTime,
                0,
                0,
                0,
                0,
                0,
                0,
                dailyResetTime.getTime(),
                weeklyResetTime.getTime(),
                balanceExpiry.getTime(),
                ResetBalanceStatus.valueOf(status),
                0,
                0,
                CarryForwardStatus.valueOf(carryForwardStatus),
                renewalInterval,
                productOfferId,
                quotaStartTime.getTime()
        );
    }

    public void insert(String subscriberId, String subscriptionId, Collection<NonMonetoryBalance> nonMonetoryBalances, Transaction transaction) throws TransactionException, OperationFailedException {

        PreparedStatement psforInsert = null;

        try {

            psforInsert = transaction.prepareStatement(INSERT_BALANCE_QUERY);
            psforInsert.setQueryTimeout(queryTimeout);

            for (NonMonetoryBalance nonMonetoryBalance : nonMonetoryBalances) {

                setBalanceToPSForInsert(psforInsert, nonMonetoryBalance, subscriberId, subscriptionId);

                long queryExecutionTime = getCurrentTime();
                psforInsert.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;

                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while inserting balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
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
            handleGetUsageError(e, transaction);
        } catch (Exception e) {
            handleGetUsageError(e);
        } finally {
            closeQuietly(psforInsert);
        }

    }

    protected void setBalanceToPSForInsert(PreparedStatement psforInsert, NonMonetoryBalance nonMonetoryBalance, String subscriberId, String subscriptionId) throws SQLException {

        psforInsert.setString(1, generateId());
        psforInsert.setString(2, subscriberId);
        psforInsert.setString(3, nonMonetoryBalance.getPackageId());
        psforInsert.setString(4, subscriptionId);
        psforInsert.setString(5, nonMonetoryBalance.getQuotaProfileId());
        psforInsert.setLong(6, nonMonetoryBalance.getServiceId());
        psforInsert.setLong(7, nonMonetoryBalance.getRatingGroupId());
        psforInsert.setInt(8, nonMonetoryBalance.getLevel());
        psforInsert.setLong(9, nonMonetoryBalance.getBillingCycleTotalVolume());
        psforInsert.setLong(10, nonMonetoryBalance.getBillingCycleAvailableVolume());
        psforInsert.setLong(11, nonMonetoryBalance.getBillingCycleTime());
        psforInsert.setLong(12, nonMonetoryBalance.getBillingCycleAvailableTime());
        psforInsert.setTimestamp(13, new Timestamp(timeSource.currentTimeInMillis()));
        psforInsert.setTimestamp(14, new Timestamp(nonMonetoryBalance.getBillingCycleResetTime()));
        psforInsert.setString(15, (nonMonetoryBalance.getStatus() == null ? null : nonMonetoryBalance.getStatus().name()));
        psforInsert.setLong(16, nonMonetoryBalance.getDailyVolume());
        psforInsert.setLong(17, nonMonetoryBalance.getDailyTime());
        psforInsert.setTimestamp(18, new Timestamp(nonMonetoryBalance.getDailyResetTime()));
        psforInsert.setLong(19, nonMonetoryBalance.getWeeklyVolume());
        psforInsert.setLong(20, nonMonetoryBalance.getWeeklyTime());
        psforInsert.setTimestamp(21, new Timestamp(nonMonetoryBalance.getWeeklyResetTime()));
        psforInsert.setLong(22, ZERO_VALUE);
        psforInsert.setLong(23, ZERO_VALUE);
        psforInsert.setString(24, nonMonetoryBalance.getRenewalInterval());
        psforInsert.setString(25, nonMonetoryBalance.getProductOfferId());
        psforInsert.setString(26, Objects.nonNull(nonMonetoryBalance.getCarryForwardStatus()) ? nonMonetoryBalance.getCarryForwardStatus().name() : null);

    }

    protected String generateId() {
        return UUID.randomUUID().toString();
    }

    @Nonnull
    public void reserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceRgNonMonitoryBalances, TransactionFactory transactionFactory) throws OperationFailedException {

        PreparedStatement psforReserve = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reserve balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform reserve balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();

            psforReserve = transaction.prepareStatement(RESERVE_BALANCE_QUERY);


            for (NonMonetoryBalance serviceRgNonMonitoryBalance : serviceRgNonMonitoryBalances) {


                psforReserve.setQueryTimeout(queryTimeout);

                setBalanceToPsForReserve(psforReserve, serviceRgNonMonitoryBalance);

                long queryExecutionTime = getCurrentTime();
                psforReserve.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reserving balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }

                psforReserve.clearParameters();

            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Reserve balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);

        } catch (SQLException e) {
            transaction.rollback();
            handleReservationUsageError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleReservationUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReservationUsageError(e);
        } finally {
            closeQuietly(psforReserve);
            endTransaction(transaction);
        }
    }


    @Nonnull
    public void reportBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceRgNonMonitoryBalances, TransactionFactory transactionFactory) throws OperationFailedException {
        PreparedStatement psForReport = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform report balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform report balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        try {

            transaction.begin();
            psForReport = transaction.prepareStatement(REPORT_BALANCE_QUERY);
            for (NonMonetoryBalance serviceRgNonMonitoryBalance : serviceRgNonMonitoryBalances) {

                psForReport.setQueryTimeout(queryTimeout);

                setBalanceToPsForReport(psForReport, serviceRgNonMonitoryBalance);

                long queryExecutionTime = getCurrentTime();
                psForReport.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reporting balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psForReport.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Report balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleReportUsageError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleReportUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReportUsageError(e);
        } finally {
            closeQuietly(psForReport);
            endTransaction(transaction);
        }

    }

    @Nonnull
    public void resetBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceRgNonMonitoryBalances, TransactionFactory transactionFactory) throws OperationFailedException {
        PreparedStatement psForReset = null;
        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reset balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform reset balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            transaction.begin();
            psForReset = transaction.prepareStatement(RESET_BALANCE_QUERY);
            for (NonMonetoryBalance serviceRgNonMonitoryBalance : serviceRgNonMonitoryBalances) {
                psForReset.setQueryTimeout(queryTimeout);
                setBalanceToPsForReset(psForReset, serviceRgNonMonitoryBalance);
                long queryExecutionTime = getCurrentTime();
                psForReset.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while resetting balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psForReset.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Reset balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleResetUsageError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleResetUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleResetUsageError(e);
        } finally {
            closeQuietly(psForReset);
            endTransaction(transaction);
        }

    }

    @Nonnull
    public void directDebitBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceRgNonMonitoryBalances, TransactionFactory transactionFactory) throws OperationFailedException {
        PreparedStatement psForDirectDebit = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform direct debit balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform direct debit balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        try {
            transaction.begin();
            psForDirectDebit = transaction.prepareStatement(DIRECT_DEBIT_QUERY);
            for (NonMonetoryBalance serviceRgNonMonitoryBalance : serviceRgNonMonitoryBalances) {
                psForDirectDebit.setQueryTimeout(queryTimeout);
                setBalanceToPsFordirectDebit(psForDirectDebit, serviceRgNonMonitoryBalance);
                long queryExecutionTime = getCurrentTime();
                psForDirectDebit.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while direct debitting balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psForDirectDebit.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Direct debit balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleDirectDebitUsageError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleDirectDebitUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleDirectDebitUsageError(e);
        } finally {
            closeQuietly(psForDirectDebit);
            endTransaction(transaction);
        }

    }

    @Nonnull
    public void reportAndReserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceRgNonMonitoryBalances, TransactionFactory transactionFactory) throws OperationFailedException {
        PreparedStatement psForReportAndReserve = null;

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform report and reserve balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform report and reserve balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }
        try {
            transaction.begin();
            psForReportAndReserve = transaction.prepareStatement(REPORT_AND_RESERVE_BALANCE_QUERY);
            for (NonMonetoryBalance serviceRgNonMonitoryBalance : serviceRgNonMonitoryBalances) {
                psForReportAndReserve.setQueryTimeout(queryTimeout);
                setBalanceToPsForReportAndReserve(psForReportAndReserve, serviceRgNonMonitoryBalance);
                long queryExecutionTime = getCurrentTime();
                psForReportAndReserve.executeUpdate();
                queryExecutionTime = getCurrentTime() - queryExecutionTime;
                if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                    alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                            "DB Query execution time is high while reporting and reserving balance information. "
                                    + "Last Query execution time: " + queryExecutionTime + " ms");

                    if (getLogger().isWarnLogLevel()) {//NOSONAR
                        getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                + queryExecutionTime + " milliseconds.");
                    }
                }
                psForReportAndReserve.clearParameters();
            }
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Report and reserve balance operation successfully completed");
            }
            iTotalQueryTimeoutCount.set(0);
        } catch (SQLException e) {
            transaction.rollback();
            handleReportAndReserveUsageError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleReportAndReserveUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReportAndReserveUsageError(e);
        } finally {
            closeQuietly(psForReportAndReserve);
            endTransaction(transaction);
        }

    }

    protected void setBalanceToPsForReport(PreparedStatement psForReport, NonMonetoryBalance serviceRgNonMonitoryBalance) throws SQLException {
        psForReport.setLong(1, serviceRgNonMonitoryBalance.getDailyVolume());
        psForReport.setLong(2, serviceRgNonMonitoryBalance.getDailyTime());
        psForReport.setLong(3, serviceRgNonMonitoryBalance.getWeeklyVolume());
        psForReport.setLong(4, serviceRgNonMonitoryBalance.getWeeklyTime());
        psForReport.setLong(5, serviceRgNonMonitoryBalance.getBillingCycleAvailableVolume());
        psForReport.setLong(6, serviceRgNonMonitoryBalance.getBillingCycleAvailableTime());
        psForReport.setLong(7, serviceRgNonMonitoryBalance.getReservationVolume());
        psForReport.setLong(8, serviceRgNonMonitoryBalance.getReservationTime());
        psForReport.setString(9, serviceRgNonMonitoryBalance.getId());
    }

    protected void setBalanceToPsForReserve(PreparedStatement psForReserve, NonMonetoryBalance serviceRgNonMonitoryBalance) throws SQLException {
        psForReserve.setLong(1, serviceRgNonMonitoryBalance.getReservationVolume());
        psForReserve.setLong(2, serviceRgNonMonitoryBalance.getReservationTime());
        psForReserve.setString(3, serviceRgNonMonitoryBalance.getId());
    }

    protected void setBalanceToPsForReportAndReserve(PreparedStatement psForReportAndReserve, NonMonetoryBalance serviceRgNonMonitoryBalance) throws SQLException {
        psForReportAndReserve.setLong(1, serviceRgNonMonitoryBalance.getDailyVolume());
        psForReportAndReserve.setLong(2, serviceRgNonMonitoryBalance.getDailyTime());
        psForReportAndReserve.setLong(3, serviceRgNonMonitoryBalance.getWeeklyVolume());
        psForReportAndReserve.setLong(4, serviceRgNonMonitoryBalance.getWeeklyTime());
        psForReportAndReserve.setLong(5, serviceRgNonMonitoryBalance.getBillingCycleAvailableVolume());
        psForReportAndReserve.setLong(6, serviceRgNonMonitoryBalance.getBillingCycleAvailableTime());
        psForReportAndReserve.setLong(7, serviceRgNonMonitoryBalance.getReservationVolume());
        psForReportAndReserve.setLong(8, serviceRgNonMonitoryBalance.getReservationTime());
        psForReportAndReserve.setTimestamp(9, new Timestamp(serviceRgNonMonitoryBalance.getDailyResetTime()));
        psForReportAndReserve.setTimestamp(10, new Timestamp(serviceRgNonMonitoryBalance.getWeeklyResetTime()));
        psForReportAndReserve.setString(11, serviceRgNonMonitoryBalance.getId());
    }

    protected void setBalanceToPsForReset(PreparedStatement psForReset, NonMonetoryBalance serviceRgNonMonitoryBalance) throws SQLException {
        psForReset.setLong(1, serviceRgNonMonitoryBalance.getDailyVolume());
        psForReset.setLong(2, serviceRgNonMonitoryBalance.getDailyTime());
        psForReset.setLong(3, serviceRgNonMonitoryBalance.getWeeklyVolume());
        psForReset.setLong(4, serviceRgNonMonitoryBalance.getWeeklyTime());
        psForReset.setLong(5, serviceRgNonMonitoryBalance.getBillingCycleAvailableVolume());
        psForReset.setLong(6, serviceRgNonMonitoryBalance.getBillingCycleAvailableTime());
        psForReset.setTimestamp(7, new Timestamp(serviceRgNonMonitoryBalance.getDailyResetTime()));
        psForReset.setTimestamp(8, new Timestamp(serviceRgNonMonitoryBalance.getWeeklyResetTime()));
        psForReset.setTimestamp(9, new Timestamp(serviceRgNonMonitoryBalance.getBillingCycleResetTime()));
        psForReset.setString(10, serviceRgNonMonitoryBalance.getId());
    }

    protected void setBalanceToPsFordirectDebit(PreparedStatement psForDirectDebit, NonMonetoryBalance serviceRgNonMonitoryBalance) throws SQLException {
        psForDirectDebit.setLong(1, serviceRgNonMonitoryBalance.getDailyVolume());
        psForDirectDebit.setLong(2, serviceRgNonMonitoryBalance.getDailyTime());
        psForDirectDebit.setLong(3, serviceRgNonMonitoryBalance.getWeeklyVolume());
        psForDirectDebit.setLong(4, serviceRgNonMonitoryBalance.getWeeklyTime());
        psForDirectDebit.setLong(5, serviceRgNonMonitoryBalance.getBillingCycleAvailableVolume());
        psForDirectDebit.setLong(6, serviceRgNonMonitoryBalance.getBillingCycleAvailableTime());
        psForDirectDebit.setTimestamp(7, new Timestamp(serviceRgNonMonitoryBalance.getDailyResetTime()));
        psForDirectDebit.setTimestamp(8, new Timestamp(serviceRgNonMonitoryBalance.getWeeklyResetTime()));
        psForDirectDebit.setString(9, serviceRgNonMonitoryBalance.getId());
    }


    /**
     * <PRE>
     * Fetches RG balance for provided subscriber identity.
     * if balance not exist for provided subscriber identity then
     * returns empty subscription map wrapper
     * else
     * balance map wrapper with subscription id or package id(when base package) as an aggregation key
     *
     * </PRE>
     *
     * @param sprInfo            identity of the subscriber
     * @param pkgId              package id(can be null)
     * @param subscriptionId     subscription id(can be null)
     * @param addOnSubscriptions list of addon subscriptions
     * @param transactionFactory source from which NetVertex need to fetch subscriber
     * @throws OperationFailedException when Datasource not available or any exception occurs
     */
    public SubscriberNonMonitoryBalance getNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId,
                                                                                     Map<String, Subscription> addOnSubscriptions, TransactionFactory transactionFactory) throws OperationFailedException {
        if (pkgId == null && subscriptionId == null) {
            //Return all balance
            return getBalance(sprInfo, addOnSubscriptions, transactionFactory);
        } else {
            //Return package and subscription specific balance
            return getBalance(sprInfo, pkgId, subscriptionId, addOnSubscriptions, transactionFactory);
        }

    }

    private SubscriberNonMonitoryBalance getBalance(SPRInfo sprInfo, Map<String, Subscription> addOnSubscriptions,
                                                    TransactionFactory transactionFactory) throws OperationFailedException {

        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        String productOfferName = sprInfo.getProductOffer();
        ProductOffer productOffer;
        UserPackage dataPackage;

        if (productOfferName != null) {
            productOffer = policyRepository.getProductOffer().byName(productOfferName);
            if (productOffer == null) {
                throw new OperationFailedException(
                        "No product offer found with name: " + productOfferName + " for subscriber ID: " + subscriberIdentity, ResultCode.NOT_FOUND);
            }
            dataPackage = productOffer.getDataServicePkgData();

        } else {
            throw new OperationFailedException(
                    "No data package associated with subscriber ID: " + subscriberIdentity, ResultCode.NOT_FOUND);
        }

        long currentTimeInMillis = getCurrentTime();
        List<NonMonetoryBalance> balances = getBalancesFromDB(sprInfo, transactionFactory);
        final Map<String, PromotionalPackage> promotionalPackages = getValidPromotionalPackages(currentTimeInMillis);


        if (Objects.nonNull(dataPackage)) {
            balances = balances.stream().filter(serviceBalance -> (dataPackage.getId().equals(serviceBalance.getPackageId()) && productOffer.getId().equals(serviceBalance.getProductOfferId())) ||
                    (addOnSubscriptions != null && addOnSubscriptions.containsKey(serviceBalance.getSubscriptionId()) == false) ||
                    (promotionalPackages != null && promotionalPackages.containsKey(serviceBalance.getSubscriptionId()) == false)).collect(Collectors.toList());
            resetDailyAndWeeklyEligibleBalance(balances, currentTimeInMillis, transactionFactory);
        } else {
            balances = null;
        }

        return new SubscriberNonMonitoryBalance(balances);
    }

    private SubscriberNonMonitoryBalance getBalance(SPRInfo sprInfo, @Nullable String packageId, @Nullable String subscriptionId,
                                                    @Nonnull Map<String, Subscription> addOnSubscriptions, @Nonnull TransactionFactory transactionFactory) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Fetching balance information for subscriber ID: " + sprInfo.getSubscriberIdentity());
        }

        ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());

        if (Objects.isNull(productOffer)) {
            throw new OperationFailedException("No product offer found for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
        }

        UserPackage dataPackage = null;
        if (Strings.isNullOrBlank(packageId) == false) {
            dataPackage = policyRepository.getPkgDataById(packageId);

            if (dataPackage == null) {
                throw new OperationFailedException("No package found with pkgId: " + packageId + " for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
            }
        }

        Subscription addOnSubscription = null;
        if (Strings.isNullOrBlank(subscriptionId) == false) {
            addOnSubscription = addOnSubscriptions.get(subscriptionId);
            if (addOnSubscription == null) {
                throw new OperationFailedException("No subscription found for subscription ID: " + subscriptionId, ResultCode.NOT_FOUND);
            }
        }

        long currentTimeInMillis = getCurrentTime();
        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(null);

        if (dataPackage != null && addOnSubscription != null) {
            if (PkgType.BASE == dataPackage.getPackageType() || PkgType.PROMOTIONAL == dataPackage.getPackageType()) {
                throw new OperationFailedException("Package : " + dataPackage.getName() + " is of Base/Promotional type for subscriber Id: " + sprInfo.getSubscriberIdentity() + ". It cannot contain subscription Id", ResultCode.INVALID_INPUT_PARAMETER);
            }

            SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());

            if (subscriptionPackage == null) {

                QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(addOnSubscription.getPackageId());

                if (Objects.isNull(quotaTopUp)) {
                    throw new OperationFailedException("Subscription package not found for package id: " + addOnSubscription.getPackageId()
                            + ", subscription id: " + addOnSubscription.getId() + ", subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }

            }

            if (addOnSubscription.getPackageId().equals(packageId) == false) {
                throw new OperationFailedException("SubscriptionId: " + subscriptionId + " and addOnId: " + packageId + " are not related for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
            }
            subscriberNonMonitoryBalance = getSubscriberNonMonetaryBalance(sprInfo, dataPackage,
                    addOnSubscription, transactionFactory, currentTimeInMillis);

        } else if (dataPackage != null && addOnSubscription == null) {

            if (PkgType.BASE == dataPackage.getPackageType()) {
                if (Objects.isNull(productOffer.getDataServicePkgData()) || (Objects.nonNull(productOffer.getDataServicePkgData()) && productOffer.getDataServicePkgData().getId().equals(dataPackage.getId()) == false)) {
                    throw new OperationFailedException("Base Package :" + dataPackage.getName() + " is not associated with subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }
                subscriberNonMonitoryBalance = getSubscriberNonMonetaryBalance(sprInfo, dataPackage,
                        addOnSubscription, transactionFactory, currentTimeInMillis);
            } else if (PkgType.PROMOTIONAL == dataPackage.getPackageType()) {
                subscriberNonMonitoryBalance = getSubscriberNonMonetaryBalance(sprInfo, dataPackage,
                        addOnSubscription, transactionFactory, currentTimeInMillis);
            } else {
                if (Maps.isNullOrEmpty(addOnSubscriptions)) {
                    throw new OperationFailedException("No subscription found for subscriber identity: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }

                Subscription activeSubscription = getAddOnSubscriptionIfExistsForPackageProvided(addOnSubscriptions, dataPackage);
                if (Objects.nonNull(activeSubscription)) {
                    subscriberNonMonitoryBalance = getSubscriberNonMonetaryBalance(sprInfo, dataPackage,
                            activeSubscription, transactionFactory, currentTimeInMillis);
                } else {
                    throw new OperationFailedException("No subscription found for package: " + dataPackage.getName() + "(" + packageId + ") for subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }

            }

        } else if (dataPackage == null && addOnSubscription != null) {

            SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(addOnSubscription.getPackageId());

            if (subscriptionPackage == null) {
                QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(addOnSubscription.getPackageId());

                if (Objects.isNull(quotaTopUp)) {
                    throw new OperationFailedException("Subscription package not found for package id: " + addOnSubscription.getPackageId()
                            + ", subscription id: " + addOnSubscription.getId() + ", subscriber ID: " + sprInfo.getSubscriberIdentity(), ResultCode.NOT_FOUND);
                }

            }
            subscriberNonMonitoryBalance = getSubscriberNonMonetaryBalance(sprInfo, dataPackage,
                    addOnSubscription, transactionFactory, currentTimeInMillis);

        }

        return subscriberNonMonitoryBalance;
    }

    private Subscription getAddOnSubscriptionIfExistsForPackageProvided(Map<String, Subscription> addOnSubscriptions, UserPackage dataPackage) {
        for (Subscription subscription : addOnSubscriptions.values()) {

            if (subscription.getPackageId().equals(dataPackage.getId())) {

                SubscriptionPackage subscriptionPackage = policyRepository.getAddOnById(subscription.getPackageId());

                if (subscriptionPackage == null) {
                    QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(subscription.getPackageId());

                    if (Objects.isNull(quotaTopUp)) {
                        getLogger().warn(MODULE, "Skipping subscription(id:" + subscription.getId() + ") for subscriber ID: " + subscription.getSubscriberIdentity()
                                + ". Reason: Subscription package not found for package id: " + subscription.getPackageId());
                    }

                } else {
                    return subscription;
                }
            }
        }
        return null;
    }

    private SubscriberNonMonitoryBalance getSubscriberNonMonetaryBalance(SPRInfo sprInfo, UserPackage dataPackage,
                                                                         Subscription addOnSubscription,
                                                                         TransactionFactory transactionFactory,
                                                                         long currentTimeInMillis) throws OperationFailedException {
        List<NonMonetoryBalance> balances = getBalancesFromDB(sprInfo, transactionFactory);
        UserPackage finalDataPackage = dataPackage;
        Subscription finalAddOnSubscription = addOnSubscription;

        balances = balances.stream()
                .filter(balance -> finalDataPackage == null || balance.getPackageId().equals(finalDataPackage.getId()))
                .filter(balance -> finalAddOnSubscription == null || ((balance.getSubscriptionId() == null) ? false : balance.getSubscriptionId().equals(finalAddOnSubscription.getId()))).collect(Collectors.toList());

        resetDailyAndWeeklyEligibleBalance(balances, currentTimeInMillis, transactionFactory);

        return new SubscriberNonMonitoryBalance(balances);
    }

    private void resetDailyAndWeeklyEligibleBalance(List<NonMonetoryBalance> serviceRgNonMonitoryBalances, long currentTimeInMillis, TransactionFactory transactionFactory) throws OperationFailedException {

        List<NonMonetoryBalance> balanceToBeUpdated = new ArrayList<>();

        for (NonMonetoryBalance serviceRgNonMonitoryBalance : serviceRgNonMonitoryBalances) {

            boolean isStateChange = false;

            if (serviceRgNonMonitoryBalance.getDailyResetTime() < currentTimeInMillis) {
                serviceRgNonMonitoryBalance.resetDailyUsage();

                long resetTime = ResetTimeUtility.calculateDailyResetTime();

                if (resetTime > serviceRgNonMonitoryBalance.getBillingCycleResetTime()) {
                    resetTime = serviceRgNonMonitoryBalance.getBillingCycleResetTime();
                }

                serviceRgNonMonitoryBalance.setDailyResetTime(resetTime);

                isStateChange = true;
            }

            if (serviceRgNonMonitoryBalance.getWeeklyResetTime() < currentTimeInMillis) {
                serviceRgNonMonitoryBalance.resetWeeklyUsage();

                long resetTime = ResetTimeUtility.calculateWeeklyResetTime();

                if (resetTime > serviceRgNonMonitoryBalance.getBillingCycleResetTime()) {
                    resetTime = serviceRgNonMonitoryBalance.getBillingCycleResetTime();
                }

                serviceRgNonMonitoryBalance.setWeeklyResetTime(resetTime);

                isStateChange = true;
            }

            if (isStateChange == true) {
                balanceToBeUpdated.add(serviceRgNonMonitoryBalance);
            }
        }

        if (balanceToBeUpdated.isEmpty() == false) {
            this.resetBalance(balanceToBeUpdated.get(0).getSubscriberIdentity(), balanceToBeUpdated, transactionFactory);
        }
    }

    /**
     * <PRE>
     * Fetches RG balance for provided subscriber identity.
     * No checks(i.e. validate reset intervals or reset balance) are performed on the result.
     * </PRE>
     *
     * @param sprInfo Subscriber Profile Repository of the subscriber
     * @throws OperationFailedException when Datasource not available or any exception occurs
     */
    public SubscriberNonMonitoryBalance getNonMonitoryBalance(@Nonnull SPRInfo sprInfo, @Nonnull TransactionFactory transactionFactory) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching RnC balance information for subscriber ID: " + sprInfo.getSubscriberIdentity());
        }


        if (transactionFactory == null || transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + sprInfo.getSubscriberIdentity()
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + sprInfo.getSubscriberIdentity()
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<NonMonetoryBalance> nonMonetoryBalances = new ArrayList<>();

        try {
            transaction.begin();
            preparedStatement = transaction.prepareStatement(SELECT_BALANCE_QUERY);
            preparedStatement.setString(1, sprInfo.getSubscriberIdentity());
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

            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String quotaProfileId = resultSet.getString("QUOTA_PROFILE_ID");
                long serviceId = resultSet.getLong("DATA_SERVICE_TYPE_ID");
                String packageId = resultSet.getString("PACKAGE_ID");
                long ratingGroupId = resultSet.getLong("RATING_GROUP_ID");
                String subscriptionId = resultSet.getString("SUBSCRIPTION_ID");
                int level = resultSet.getInt("BALANCE_LEVEL");
                long billingCycleTotalVolume = resultSet.getLong("BILLING_CYCLE_TOTAL_VOLUME");
                long billingCycleAvailableVolume = resultSet.getLong("BILLING_CYCLE_AVAIL_VOLUME");
                long billingCycleTotalTime = resultSet.getLong("BILLING_CYCLE_TOTAL_TIME");
                long billingCycleAvailableTime = resultSet.getLong("BILLING_CYCLE_AVAIL_TIME");
                long dailyVolume = resultSet.getLong("DAILY_VOLUME");
                long dailyTime = resultSet.getLong("DAILY_TIME");
                long weeklyVolume = resultSet.getLong("WEEKLY_VOLUME");
                long weeklyTime = resultSet.getLong("WEEKLY_TIME");
                long reservationVolume = resultSet.getLong("RESERVATION_VOLUME");
                long reservationTime = resultSet.getLong("RESERVATION_TIME");
                Timestamp dailyResetTime = resultSet.getTimestamp("DAILY_RESET_TIME");
                Timestamp weeklyResetTime = resultSet.getTimestamp("WEEKLY_RESET_TIME");
                Timestamp billingCycleResetTime = resultSet.getTimestamp("QUOTA_EXPIRY_TIME");
                Timestamp startTime = resultSet.getTimestamp("START_TIME");
                String status = resultSet.getString("STATUS");
                long carryForwardVolume = resultSet.getLong("CARRY_FORWARD_VOLUME");
                long carryForwardTime = resultSet.getLong("CARRY_FORWARD_TIME");
                String carryForwardStatus = resultSet.getString("CARRY_FORWARD_STATUS");
                String renewalInterval = resultSet.getString("RENEWAL_INTERVAL");
                String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");

                NonMonetoryBalance balance = new NonMonetoryBalance(
                        id, serviceId, packageId, quotaProfileId, ratingGroupId,
                        level, sprInfo.getSubscriberIdentity(), subscriptionId, billingCycleTotalVolume,
                        billingCycleAvailableVolume, billingCycleTotalTime, billingCycleAvailableTime, dailyVolume, dailyTime,
                        weeklyVolume, weeklyTime, reservationVolume, reservationTime,
                        dailyResetTime.getTime(), weeklyResetTime.getTime(), billingCycleResetTime.getTime(), ResetBalanceStatus.fromValue(status),carryForwardVolume, carryForwardTime, CarryForwardStatus.fromValue(carryForwardStatus), renewalInterval, productOfferId, startTime.getTime());
                if (nonMonetoryBalances == null) {
                    nonMonetoryBalances = new ArrayList<>();
                }

                if (System.currentTimeMillis() < balance.getStartTime()) {
                    if(getLogger().isDebugLogLevel()){
                        getLogger().debug(MODULE, "Skipping balances not applicable for current billing cycle. Balance start time of package with id : "+ balance.getPackageId() +" is " + new Timestamp(balance.getStartTime()) +" for subscriber : "+balance.getSubscriberIdentity());
                    }
                    continue;
                }

                nonMonetoryBalances.add(balance);

            }

            if (nonMonetoryBalances.isEmpty() && getLogger().isInfoLogLevel()) {
                if(getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Balance not exist with subscriber ID: " + sprInfo.getSubscriberIdentity());
                }
            }

            abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalances);

            return new SubscriberNonMonitoryBalance(nonMonetoryBalances);


        } catch (SQLException e) {
            handleGetBalanceError(e, transaction);
        } catch (TransactionException e) {
            handleGetBalanceError(e, transaction);
        } catch (Exception e) {
            handleGetBalanceError(e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(preparedStatement);
            endTransaction(transaction);
        }
        return null;
    }

    private Map<String, PromotionalPackage> getValidPromotionalPackages(long currentTimeInMillis) {
        List<PromotionalPackage> promotionalPackages = policyRepository.getPromotionalPackages();
        Map<String, PromotionalPackage> validPromotionalPackages = new HashMap<>();

        for (PromotionalPackage promoPackage : promotionalPackages) {
            if (promoPackage.getAvailabilityEndDate() != null && promoPackage.getAvailabilityEndDate().getTime() < currentTimeInMillis) {
                continue;
            }

            validPromotionalPackages.put(promoPackage.getId(), promoPackage);
        }

        return validPromotionalPackages;
    }


    private List<NonMonetoryBalance> getBalancesFromDB(SPRInfo sprInfo, TransactionFactory transactionFactory) throws OperationFailedException {
        String subscriberIdentity = sprInfo.getSubscriberIdentity();
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching RnC balance information for subscriber ID: " + subscriberIdentity);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<NonMonetoryBalance> subscriptionToSubscriberBalance = new ArrayList<>();

        try {
            transaction.begin();
            preparedStatement = transaction.prepareStatement(SELECT_BALANCE_QUERY);
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

            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String quotaProfileId = resultSet.getString("QUOTA_PROFILE_ID");
                long serviceId = resultSet.getLong("DATA_SERVICE_TYPE_ID");
                String packageId = resultSet.getString("PACKAGE_ID");
                long ratingGroupId = resultSet.getLong("RATING_GROUP_ID");
                String subscriptionId = resultSet.getString("SUBSCRIPTION_ID");
                int level = resultSet.getInt("BALANCE_LEVEL");
                long billingCycleTotalVolume = resultSet.getLong("BILLING_CYCLE_TOTAL_VOLUME");
                long billingCycleAvailableVolume = resultSet.getLong("BILLING_CYCLE_AVAIL_VOLUME");
                long billingCycleTotalTime = resultSet.getLong("BILLING_CYCLE_TOTAL_TIME");
                long billingCycleAvailableTime = resultSet.getLong("BILLING_CYCLE_AVAIL_TIME");
                long dailyVolume = resultSet.getLong("DAILY_VOLUME");
                long dailyTime = resultSet.getLong("DAILY_TIME");
                long weeklyVolume = resultSet.getLong("WEEKLY_VOLUME");
                long weeklyTime = resultSet.getLong("WEEKLY_TIME");
                long reservationVolume = resultSet.getLong("RESERVATION_VOLUME");
                long reservationTime = resultSet.getLong("RESERVATION_TIME");
                long dailyResetTime = resultSet.getTimestamp("DAILY_RESET_TIME").getTime();
                long weeklyResetTime = resultSet.getTimestamp("WEEKLY_RESET_TIME").getTime();
                long billingCycleResetTime = resultSet.getTimestamp("QUOTA_EXPIRY_TIME").getTime();
                long startTime = resultSet.getTimestamp("START_TIME").getTime();
                String status = resultSet.getString("STATUS");
                long carryForwardVolume = resultSet.getLong("CARRY_FORWARD_VOLUME");
                long carryForwardTime = resultSet.getLong("CARRY_FORWARD_TIME");
                String carryForwardStatus = resultSet.getString("CARRY_FORWARD_STATUS");
                String renewalInterval = resultSet.getString("RENEWAL_INTERVAL");
                String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");

                NonMonetoryBalance balance = new NonMonetoryBalance(
                        id,
                        serviceId,
                        packageId,
                        quotaProfileId,
                        ratingGroupId,
                        level,
                        subscriberIdentity,
                        subscriptionId,
                        billingCycleTotalVolume,
                        billingCycleAvailableVolume,
                        billingCycleTotalTime,
                        billingCycleAvailableTime,
                        dailyVolume,
                        dailyTime,
                        weeklyVolume,
                        weeklyTime,
                        reservationVolume,
                        reservationTime,
                        dailyResetTime,
                        weeklyResetTime,
                        billingCycleResetTime,
                        ResetBalanceStatus.fromValue(status),
                        carryForwardVolume,
                        carryForwardTime,
                        CarryForwardStatus.fromValue(carryForwardStatus),
                        renewalInterval,
                        productOfferId,
                        startTime
                );

                if (System.currentTimeMillis() < balance.getStartTime()) {
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skipping balances not applicable for current billing cycle. Balance start time of package with id : "+ balance.getPackageId() +" is " + new Timestamp(balance.getStartTime()) +" for subscriber : "+balance.getSubscriberIdentity());
                    }
                    continue;
                }

                subscriptionToSubscriberBalance.add(balance);
            }

            if (subscriptionToSubscriberBalance.isEmpty() && getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Balance not exist with subscriber ID: " + subscriberIdentity);
            }
        } catch (SQLException e) {
            handleGetBalanceError(e, transaction);
        } catch (TransactionException e) {
            handleGetBalanceError(e, transaction);
        } catch (Exception e) {
            handleGetBalanceError(e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(preparedStatement);
            endTransaction(transaction);
        }
        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, subscriptionToSubscriberBalance);
        return subscriptionToSubscriberBalance;

    }

    private void endTransaction(Transaction transaction) {
        try {
            if (transaction != null) {
                transaction.end();
            }
        } catch (TransactionException e) {
            getLogger().error(MODULE, "Error in ending transaction. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void handleGetBalanceError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to get RnC balance information from " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleGetBalanceError(e);
    }

    private void handleGetBalanceError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while getting RnC balance info, Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while getting RnC balance info. Reason. " + e.getMessage(), e);
    }

    private void handleGetBalanceError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                markTransactionDead(transaction);
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while fetching RnC balance information from table name: "
                            + TABLE_NAME, 0, "fetching data from table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            markTransactionDead(transaction);
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleGetBalanceError(e);
    }

    private void markTransactionDead(Transaction transaction) {
        iTotalQueryTimeoutCount.set(0);
        transaction.markDead();
    }

    protected long getCurrentTime() {
        return timeSource.currentTimeInMillis();
    }

    private void handleGetUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while inserting balance into table name: "
                            + TABLE_NAME, 0, "fetching data from table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleGetUsageError(e);
    }

    private void handleGetUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while subscriber ABMF Operation, Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while subscriber ABMF Operation, Reason. " + e.getMessage(), e);
    }

    private void handleResetUsageError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to reset balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleResetUsageError(e);
    }

    private void handleResetUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while resetting balance into table name: "
                            + TABLE_NAME, 0, "resetting balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleResetUsageError(e);
    }

    private void handleResetUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while resetting balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while resetting balance. Reason. " + e.getMessage(), e);
    }

    private void handleReportAndReserveUsageError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to report and reserve balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleReportAndReserveUsageError(e);
    }

    private void handleReportAndReserveUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while reporting and reservig balance into table name: "
                            + TABLE_NAME, 0, "reporting and reserving balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleReportAndReserveUsageError(e);
    }

    private void handleReportAndReserveUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while reporting and reserving balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while reporting and reserving balance. Reason. " + e.getMessage(), e);
    }

    private void handleReportUsageError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to report balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleReportUsageError(e);
    }

    private void handleReportUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while reporting balance into table name: "
                            + TABLE_NAME, 0, "reporting balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleReportUsageError(e);
    }

    private void handleReportUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while reporting balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while reporting. Reason. " + e.getMessage(), e);
    }

    private void handleDirectDebitUsageError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to direct debit balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }

        handleDirectDebitUsageError(e);
    }

    private void handleDirectDebitUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while direct debitting balance into table name: "
                            + TABLE_NAME, 0, "direct debitting balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleDirectDebitUsageError(e);
    }

    private void handleDirectDebitUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while direct debitting balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while direct debitting. Reason. " + e.getMessage(), e);
    }

    private void handleReservationUsageError(TransactionException e, Transaction transaction)
            throws OperationFailedException {
        if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {

            alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                    "Unable to reserve balance into " + transaction.getDataSourceName() +
                            " database. Reason: connection not available");

        }
        handleReservationUsageError(e);
    }

    private void handleReservationUsageError(SQLException e, Transaction transaction)
            throws OperationFailedException {

        if (e.getErrorCode() == QUERY_TIMEOUT_ERROR_CODE) {
            if (iTotalQueryTimeoutCount.incrementAndGet() > maxQueryTimeoutCount) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger()
                            .warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so SPR marked as DEAD");
                }
                iTotalQueryTimeoutCount.set(0);
                transaction.markDead();
            }
            alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                    "DB Query Timeout while reserving balance into table name: "
                            + TABLE_NAME, 0, "reserving balance into table name: " + TABLE_NAME);
        } else if (transaction.isDBDownSQLException(e)) {
            transaction.markDead();
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
                        + " is Down, SPR marked as dead. Reason: " + e.getMessage());
            }
        }

        handleReservationUsageError(e);
    }

    private void handleReservationUsageError(Exception e)
            throws OperationFailedException {
        if (getLogger().isErrorLogLevel()) {
            getLogger().error(MODULE, "Error while reserving balance. Reason: " + e.getMessage());
        }
        getLogger().trace(MODULE, e);
        throw new OperationFailedException("Error while reserving balance. Reason. " + e.getMessage(), e);
    }

    public void stop() {
        ///Not required here. Override by ABMFBatchOperation
    }

    @Override
    public void process(ABMFBatchOperation.BatchOperationData batchOperationData, TransactionFactory transactionFactory) throws OperationFailedException {
        if (batchOperationData.getOperation() == ABMFBatchOperation.BatchOperation.REPORT) {

            reportBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getNonMonitoryBalance()), transactionFactory);

        } else if (batchOperationData.getOperation() == ABMFBatchOperation.BatchOperation.RESERVE) {

            reserveBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getNonMonitoryBalance()), transactionFactory);

        } else if (batchOperationData.getOperation() == ABMFBatchOperation.BatchOperation.REPORT_AND_RESERVE) {

            reportAndReserveBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getNonMonitoryBalance()), transactionFactory);

        } else if (batchOperationData.getOperation() == ABMFBatchOperation.BatchOperation.RESET) {

            resetBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getNonMonitoryBalance()), transactionFactory);

        } else if (batchOperationData.getOperation() == ABMFBatchOperation.BatchOperation.DIRECT_DEBIT) {
            directDebitBalance(batchOperationData.getSubscriberIdentity(), Arrays.asList(batchOperationData.getNonMonitoryBalance()), transactionFactory);
        }
    }

    public void updateNextBillingCycleBalance(Set<Map.Entry<String, NonMonetoryBalance>> nonMonetoryBalances, Integer newBillDay, TransactionFactory transactionFactory) throws OperationFailedException {

        String subscriberIdentity = nonMonetoryBalances.iterator().next().getValue().getSubscriberIdentity();
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Update next billing cycle balance operation started for subscriber ID: " + subscriberIdentity);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform update next billing cycle balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform update next billing cycle balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement psforUpdate = null;

        try {
            transaction.begin();

            psforUpdate = transaction.prepareStatement(UPDATE_NEXT_BILL_CYCLE_BALANCE_QUERY);

            NonMonetoryBalance nonMonetoryBalance;
            for (Map.Entry<String, NonMonetoryBalance> entry : nonMonetoryBalances) {
                nonMonetoryBalance = entry.getValue();
                if (Objects.equals(ResetBalanceStatus.RESET, nonMonetoryBalance.getStatus()) && nonMonetoryBalance.getRenewalInterval().contains(RenewalIntervalUnit.TILL_BILL_DATE.name())) {
                    Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(null, RenewalIntervalUnit.TILL_BILL_DATE, new Timestamp(nonMonetoryBalance.getBillingCycleResetTime()), new Timestamp(nonMonetoryBalance.getStartTime()), newBillDay);
                    psforUpdate.setQueryTimeout(queryTimeout);

                    if (nonMonetoryBalance.getBillingCycleResetTime() <= balanceExpiry.getTime()) {
                        continue;
                    }

                    long proratedVolume = RenewalIntervalUtility.getProratedQuota(nonMonetoryBalance.getBillingCycleTotalVolume(), new Timestamp(nonMonetoryBalance.getStartTime()), balanceExpiry, RenewalIntervalUnit.TILL_BILL_DATE, 0);
                    long proratedTime = RenewalIntervalUtility.getProratedQuota(nonMonetoryBalance.getBillingCycleTime(), new Timestamp(nonMonetoryBalance.getStartTime()), balanceExpiry, RenewalIntervalUnit.TILL_BILL_DATE, 0);

                    psforUpdate.setTimestamp(1, balanceExpiry);
                    psforUpdate.setLong(2, proratedVolume);
                    psforUpdate.setLong(3, proratedVolume);
                    psforUpdate.setLong(4, proratedTime);
                    psforUpdate.setLong(5, proratedTime);
                    psforUpdate.setString(6, nonMonetoryBalance.getId());

                    long queryExecutionTime = getCurrentTime();
                    psforUpdate.executeUpdate();
                    queryExecutionTime = getCurrentTime() - queryExecutionTime;
                    if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                        alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                                "DB Query execution time is high while update next billing cycle balance operation. "
                                        + "Last Query execution time: " + queryExecutionTime + " ms");

                        if (getLogger().isWarnLogLevel()) {//NOSONAR
                            getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                    + queryExecutionTime + " milliseconds.");
                        }
                    }

                    psforUpdate.clearParameters();
                }
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Update next billing cycle balance operation successfully completed for subscriber ID: " + subscriberIdentity);
            }
            iTotalQueryTimeoutCount.set(0);

        } catch (SQLException e) {
            transaction.rollback();
            handleReservationUsageError(e, transaction);
        } catch (TransactionException e) {
            transaction.rollback();
            handleReservationUsageError(e, transaction);
        } catch (Exception e) {
            transaction.rollback();
            handleReservationUsageError(e);
        } finally {
            closeQuietly(psforUpdate);
            endTransaction(transaction);
        }
    }

    public void resetQuota(String subscriberIdentity, String productOfferId, Integer billingDate, TransactionFactory transactionFactory) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Performing operation to reset quota for subscriber ID: " + subscriberIdentity);
        }

        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform reset quota operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform reset quota operation for subscriber ID: " + subscriberIdentity
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            transaction.begin();

            resetQuota(subscriberIdentity, productOfferId, billingDate, transaction);

        } catch (TransactionException e) {
            transaction.rollback();
            handleTransactionException("reset quota", e, subscriberIdentity, transaction);
        } catch (SQLException e) {
            transaction.rollback();
            throw new OperationFailedException("Error while reset usage operation for subscriber ID: " + subscriberIdentity
                    + " . Reason: " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
        } finally {
            endTransaction(transaction);
        }
    }

    public void resetQuota(String subscriberIdentity, String productOfferId, Integer billingDate, Transaction transaction) throws TransactionException, OperationFailedException, SQLException {
        PreparedStatement psForUpdate = null;

        try {
            ProductOffer productOffer = policyRepository.getProductOffer().byId(productOfferId);
            UserPackage basePackage = productOffer.getDataServicePkgData();

            if (QuotaProfileType.RnC_BASED != basePackage.getQuotaProfileType()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping usage reset operation for subscriber ID: " + subscriberIdentity
                            + ". Reason: Data Package: " + basePackage.getName() + " is not RnC Based");
                }
                return;
            }

            psForUpdate = transaction.prepareStatement(RESET_QUOTA);

            List<QuotaProfile> quotaProfiles = basePackage.getQuotaProfiles();

            for (QuotaProfile quotaProfile : quotaProfiles) {

                for (Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails : quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()) {

                    for (Map.Entry<String, QuotaProfileDetail> entry : fupLevelServiceWiseQuotaProfileDetails.entrySet()) {
                        setQuotaToPSForReset(psForUpdate, productOfferId, subscriberIdentity, quotaProfile, entry, billingDate);

                        long queryExecutionTime = getCurrentTime();
                        psForUpdate.executeUpdate();
                        queryExecutionTime = getCurrentTime() - queryExecutionTime;
                        if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                            alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                                    "DB Query execution time is high while resetting quota information. "
                                            + "Last Query execution time: " + queryExecutionTime + " ms");

                            if (getLogger().isWarnLogLevel()) {//NOSONAR
                                getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                                        + queryExecutionTime + " milliseconds.");
                            }
                        }
                    }
                }
            }

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Reset quota operation successfully completed for subscriber ID: " + subscriberIdentity);
            }
        } finally {
            closeQuietly(psForUpdate);
        }
    }

    public int deleteDataBalance(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Delete data balance operation for subscriber(" + subscriberIdentity + ") started");
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Delete data balance query: " + DELETE_DATA_BALANCE_QUERY);
        }

        PreparedStatement psDeleteUsageStatement = null;

        try {

            psDeleteUsageStatement = transaction.prepareStatement(DELETE_DATA_BALANCE_QUERY);
            psDeleteUsageStatement.setString(1, subscriberIdentity);
            psDeleteUsageStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

            Stopwatch watch = new Stopwatch();
            watch.start();
            setDataBalanceToDataBalanceHistory(transaction, subscriberIdentity);
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
                    getLogger().info(MODULE, "Total(" + deleteCount + ") data balances deleted for subscriber(" + subscriberIdentity + ")");
                } else {
                    getLogger().info(MODULE, "Data balance not deleted for subscriber(" + subscriberIdentity
                            + "). Reason. Subscriber or data balance not found");
                }

            }
            iTotalQueryTimeoutCount.set(0);
            return deleteCount;
        } catch (SQLException e) {
            handleSQLExceptionException("deleting data balance", e, subscriberIdentity, transaction);
        } finally {
            closeQuietly(psDeleteUsageStatement);
        }

        return 0;
    }

    private void setDataBalanceToDataBalanceHistory(Transaction transaction, String subscriberId) throws TransactionException, SQLException {

        PreparedStatement psForUsageHistory = null;
        try {
            psForUsageHistory = transaction.prepareStatement(DATA_BALANCE_HISTORY_QUERY);
            psForUsageHistory.setString(1, generateId());
            psForUsageHistory.setString(2, subscriberId);
            long queryExecutionTime = getCurrentTime();
            psForUsageHistory.execute();
            queryExecutionTime = getCurrentTime() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding data balance information in history table. "
                                + "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }

        } finally {
            closeQuietly(psForUsageHistory);
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
                    "DB Query Timeout while " + operationName + " information in table name: TBLM_DATA_BALANCE, 0, table name: TBLM_DATA_BALANCE");
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

    private void setQuotaToPSForReset(PreparedStatement psForUpdate, String productOfferId, String subscriberIdentity,
                                      QuotaProfile quotaProfile, Map.Entry<String, QuotaProfileDetail> entry, Integer billDay) throws OperationFailedException, SQLException {

        RncProfileDetail rncProfileDetail = (RncProfileDetail) entry.getValue();

        long totalVolume = rncProfileDetail.getUnitType().getVolumeInBytes(rncProfileDetail.getBillingCycleAllowedUsage());
        long totalTime = rncProfileDetail.getBillingCycleAllowedUsage().getTimeInSeconds();

        Timestamp quotaStartTime = new Timestamp(timeSource.currentTimeInMillis());
        Timestamp dailyResetTime = new Timestamp(ResetTimeUtility.calculateDailyResetTime());
        Timestamp weeklyResetTime = new Timestamp(ResetTimeUtility.calculateWeeklyResetTime());
        Timestamp quotaResetTime = new Timestamp(ResetTimeUtility.calculateQuotaResetTime());
        Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(quotaProfile.getRenewalInterval(), quotaProfile.getRenewalIntervalUnit(), quotaResetTime, quotaStartTime, billDay);

        if (quotaProfile.getProration()) {
            totalVolume = RenewalIntervalUtility.getProratedQuota(totalVolume, quotaStartTime, balanceExpiry, quotaProfile.getRenewalIntervalUnit(), quotaProfile.getRenewalInterval());
            totalTime = RenewalIntervalUtility.getProratedQuota(totalTime, quotaStartTime, balanceExpiry, quotaProfile.getRenewalIntervalUnit(), quotaProfile.getRenewalInterval());
        }

        psForUpdate.setLong(1, ZERO_VALUE);
        psForUpdate.setLong(2, ZERO_VALUE);
        psForUpdate.setLong(3, ZERO_VALUE);
        psForUpdate.setLong(4, ZERO_VALUE);

        psForUpdate.setLong(5, totalVolume);
        psForUpdate.setLong(6, totalVolume);
        psForUpdate.setLong(7, totalTime);
        psForUpdate.setLong(8, totalTime);

        psForUpdate.setTimestamp(9, dailyResetTime);
        psForUpdate.setTimestamp(10, weeklyResetTime);
        psForUpdate.setTimestamp(11, balanceExpiry);

        psForUpdate.setString(12, subscriberIdentity);
        psForUpdate.setString(13, productOfferId);
        psForUpdate.setString(14, quotaProfile.getId());
    }

    /**
     * This function will set current time in QUOTA_EXPIRY_TIME column that invalidates quota from processing.
     * Clean up scheduler can use this column value to purge records from Data balance table
     *
     * @param subscriberIdentity
     * @param productOfferId
     * @param transaction
     * @throws OperationFailedException
     * @throws TransactionException
     */
    public void expireQuota(String subscriberIdentity, String productOfferId, Transaction transaction) throws OperationFailedException, TransactionException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Started operation to expire quota for subscriber ID: "
                    + subscriberIdentity + " with product offer Id: " + productOfferId);
        }

        if (transaction == null) {
            throw new OperationFailedException("Unable to expire quota for subscriber ID: "
                    + subscriberIdentity + " with product offer Id: " + productOfferId
                    + ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement psForUpdate = null;
        try {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing Query: " + EXPIRE_QUOTA_QUERY);
            }
            psForUpdate = transaction.prepareStatement(EXPIRE_QUOTA_QUERY);
            Timestamp quotaStartTime = new Timestamp(getCurrentTime());
            psForUpdate.setTimestamp(1, quotaStartTime);
            psForUpdate.setString(2, subscriberIdentity);
            psForUpdate.setString(3, productOfferId);
            long queryExecutionTime = getCurrentTime();
            psForUpdate.executeUpdate();
            queryExecutionTime = getCurrentTime() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while expiring quota for subscriber ID: "
                                + subscriberIdentity + " with product offer Id: " + productOfferId
                                + ". Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Update query execution time getting high, Last Query execution time = "
                            + queryExecutionTime + " milliseconds.");
                }
            }

            iTotalQueryTimeoutCount.set(0);
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Expire quota operation successfully completed for subscriber ID: "
                        + subscriberIdentity + " with product offer Id: " + productOfferId);
            }

        } catch (SQLException e) {
            transaction.rollback();
            handleSQLExceptionException("expire quota", e, subscriberIdentity, transaction);
        } finally {
            closeQuietly(psForUpdate);
        }
    }
}