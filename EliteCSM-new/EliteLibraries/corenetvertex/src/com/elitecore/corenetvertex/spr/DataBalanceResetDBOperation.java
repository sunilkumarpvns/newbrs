package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DataBalanceResetDBOperation {

    private static final String MODULE = "BALRST-DB-OPR";
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
            "CARRY_FORWARD_VOLUME," +
            "CARRY_FORWARD_TIME," +
            "CARRY_FORWARD_STATUS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?,?)";
    protected static final String CHANGE_STATUS_AND_CARRY_FORWARD_DATA = "UPDATE TBLM_DATA_BALANCE SET"
            + " STATUS = ?, CARRY_FORWARD_STATUS = ?, CARRY_FORWARD_VOLUME = ?, CARRY_FORWARD_TIME = ?, BILLING_CYCLE_AVAIL_VOLUME = ?,"
            + " BILLING_CYCLE_AVAIL_TIME = ? WHERE ID = ?";

    private int queryTimeout;
    protected AlertListener alertListener;

    public DataBalanceResetDBOperation(int queryTimeout, AlertListener alertListener) {
        this.queryTimeout = queryTimeout;
        this.alertListener = alertListener;
    }

    public void executeInsertOperations(List<NonMonetoryBalance> nonMonetaryBalanceInsertOperation, Transaction transaction, int batchSize) throws TransactionException, SQLException {
        PreparedStatement insertBalanceStmt = transaction.prepareStatement(INSERT_BALANCE_QUERY);
        insertBalanceStmt.setQueryTimeout(CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT);
        try {
            int batchCnt = 0;
            for(int index = 0;index<nonMonetaryBalanceInsertOperation.size(); index++) {
                if (batchCnt >= batchSize) {
                    long startTime = System.currentTimeMillis();
                    flush(insertBalanceStmt);
                    long endTime = System.currentTimeMillis();
                    getLogger().warn(MODULE, "DB Update time(ms): " + (endTime - startTime) + " Total records : " + batchCnt);
                    batchCnt = 0;
                }

                insertBalance(insertBalanceStmt, nonMonetaryBalanceInsertOperation.get(index));
                batchCnt++;
            }

            if(batchCnt > 0)  {
                flush(insertBalanceStmt);
            }
        } catch (SQLException e) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Error performing Data Balance Insert Operation: " + "Reason: " + e.getMessage());
            }
            throw e;
        } finally {
            insertBalanceStmt.close();
        }


    }

    public void executeUpdateOperations(List<NonMonetoryBalance> nonMonetaryBalanceUpdateOperation, Transaction transaction, int batchSize) throws TransactionException, SQLException {
        PreparedStatement updateStatusStmt = transaction.prepareStatement(CHANGE_STATUS_AND_CARRY_FORWARD_DATA);
        updateStatusStmt.setQueryTimeout(CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT);

        try {
            int batchCnt = 0;
            for(int index = 0;index<nonMonetaryBalanceUpdateOperation.size(); index++) {
                if (batchCnt >= batchSize) {
                    long startTime = System.currentTimeMillis();
                    flush(updateStatusStmt);
                    long endTime = System.currentTimeMillis();
                    getLogger().warn(MODULE, "DB Update time(ms): " + (endTime - startTime) + " Total records : " + batchCnt);
                    batchCnt = 0;
                }

                updateBalance(updateStatusStmt, nonMonetaryBalanceUpdateOperation.get(index));
                batchCnt++;
            }

            if(batchCnt > 0) {
                flush(updateStatusStmt);
            }
        } catch (SQLException e) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Error performing Data Balance Update Operation. Reason: " + e.getMessage());
            }
            throw e;
        } finally {
            updateStatusStmt.close();
        }

    }

    private void insertBalance(PreparedStatement psForInsert, NonMonetoryBalance provisionedNonMonetaryBalance) throws SQLException {
        psForInsert.setQueryTimeout(queryTimeout);
        psForInsert.setString(1, provisionedNonMonetaryBalance.getId());
        psForInsert.setString(2, provisionedNonMonetaryBalance.getSubscriberIdentity());
        psForInsert.setString(3, provisionedNonMonetaryBalance.getPackageId());
        psForInsert.setString(4, provisionedNonMonetaryBalance.getSubscriptionId());
        psForInsert.setString(5, provisionedNonMonetaryBalance.getQuotaProfileId());
        psForInsert.setLong(6, provisionedNonMonetaryBalance.getServiceId());
        psForInsert.setLong(7, provisionedNonMonetaryBalance.getRatingGroupId());
        psForInsert.setInt(8, provisionedNonMonetaryBalance.getLevel());
        psForInsert.setLong(9, provisionedNonMonetaryBalance.getBillingCycleTotalVolume());
        psForInsert.setLong(10, provisionedNonMonetaryBalance.getBillingCycleAvailableVolume());
        psForInsert.setLong(11, provisionedNonMonetaryBalance.getBillingCycleTime());
        psForInsert.setLong(12, provisionedNonMonetaryBalance.getBillingCycleAvailableTime());
        psForInsert.setTimestamp(13, new Timestamp(provisionedNonMonetaryBalance.getStartTime()));
        psForInsert.setTimestamp(14, new Timestamp(provisionedNonMonetaryBalance.getBillingCycleResetTime()));
        psForInsert.setString(15, provisionedNonMonetaryBalance.getStatus().name());
        psForInsert.setLong(16, provisionedNonMonetaryBalance.getDailyVolume());
        psForInsert.setLong(17, provisionedNonMonetaryBalance.getDailyTime());
        psForInsert.setTimestamp(18, new Timestamp(provisionedNonMonetaryBalance.getDailyResetTime()));
        psForInsert.setLong(19, provisionedNonMonetaryBalance.getWeeklyVolume());
        psForInsert.setLong(20, provisionedNonMonetaryBalance.getWeeklyTime());
        psForInsert.setTimestamp(21, new Timestamp(provisionedNonMonetaryBalance.getWeeklyResetTime()));
        psForInsert.setLong(22, provisionedNonMonetaryBalance.getReservationVolume());
        psForInsert.setLong(23, provisionedNonMonetaryBalance.getReservationTime());
        psForInsert.setString(24, provisionedNonMonetaryBalance.getRenewalInterval());
        psForInsert.setString(25, provisionedNonMonetaryBalance.getProductOfferId());
        psForInsert.setLong(26, provisionedNonMonetaryBalance.getCarryForwardVolume());
        psForInsert.setLong(27, provisionedNonMonetaryBalance.getCarryForwardTime());
        psForInsert.setString(28, provisionedNonMonetaryBalance.getCarryForwardStatus().name());

        psForInsert.addBatch();
        psForInsert.clearParameters();
    }

    private void updateBalance(PreparedStatement psForUpdate, NonMonetoryBalance nonMonetoryBalance) throws SQLException {
        psForUpdate.setQueryTimeout(queryTimeout);
        psForUpdate.setString(1, nonMonetoryBalance.getStatus().name());
        psForUpdate.setString(2, nonMonetoryBalance.getCarryForwardStatus().name());
        psForUpdate.setLong(3, nonMonetoryBalance.getCarryForwardVolume());
        psForUpdate.setLong(4, nonMonetoryBalance.getCarryForwardTime());
        psForUpdate.setLong(5, nonMonetoryBalance.getBillingCycleAvailableVolume());
        psForUpdate.setLong(6, nonMonetoryBalance.getBillingCycleAvailableTime());
        psForUpdate.setString(7, nonMonetoryBalance.getId());

        psForUpdate.addBatch();
        psForUpdate.clearParameters();
    }

    private void flush(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.executeBatch();
    }

}
