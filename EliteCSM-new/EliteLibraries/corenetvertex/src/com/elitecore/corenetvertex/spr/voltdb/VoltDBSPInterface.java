package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.spr.SPInterface;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.voltdb.util.VoltDBUtil;
import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltDBSPInterface implements SPInterface {


    private static final String MODULE = "VOLT-DB-SP-INTERFACE";
    private static final String SELECT_PROFILE_BY_ID = "SubscriberSelectStoredProcedure";
    private static final String MARK_DELETE_PROFILE="SubscriberMarkDeleteStoredProcedure";
    private static final String FETCH_DELETE_MARKED_SUBSCRIBERS = "SubscriberFetchDeletedMarkProfileStoredProcedure";
    public static final String ADD_SUBSCRIBER_PROFILE = "SubscriberAddStoredProcedure";
    public static final String SUBSCRIBER_ADD_WITH_UM_STORED_PROCEDURE = "SubscriberAddWithUMStoredProcedure";
    public static final String SUBSCRIBER_ADD_WITH_MONETARY_BALANCE_STORED_PROCEDURE = "SubscriberAddWithMonetaryBalanceProcedure";
    public static final String SUBSCRIBER_CHANGE_DATAPACKAGE_STORED_PROCEDURE = "SubscriberChangeDataPackageStoredProcedure";
    public static final String SUBSCRIBER_CHANGE_DATAPACKAGE_AND_NEW_USAGE_INSERT_STORED_PROCEDURE = "SubscriberChangeDataPackageAndNewUsageInsertStoredProcedure";
    public static final String SUBSCRIBER_CHANGE_DATAPACKAGE_AND_SCHEDULE_OLD_USAGE_DELETE_STORED_PROCEDURE = "SubscriberChangeDataPackageAndScheduleOldUsageDeleteStoredProcedure";
    public static final String SUBSCRIBER_CHANGE_DATAPACKAGE_AND_SCHEDULE_OLD_USAGE_DELETE_AND_NEW_USAGE_INSERT_STORED_PROCEDURE = "SubscriberChangeDataPackageAndScheduleOldUsageDeleteAndNewUsageInsertStoredProcedure";
    private static final String UPDATE_SUBSCRIBER_PROFILE = "SubscriberUpdateStoredProcedure";
    private static final String RESTORE_SUBSCRIBER_PROFILE = "SubscriberRestoreStoredProcedure";
    public static final String CHANGE_IMS_PACKAGE = "ChangeIMSpackageStoredProcedure";
    private static final String SUBSCRIBER_PURGE_AND_DELETE_USAGE = "SubscriberPurgeAndDeleteUsageStoredProcedure";
    public static final String CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE = "ChangeBaseProductOfferStoredProcedure";
    private final AlertListener alertListener;
    private final VoltDBClient voltDBClient;
    private TimeSource timeSource;
    private final AtomicInteger totalQueryTimeoutCount;

    public VoltDBSPInterface(AlertListener alertListener, VoltDBClient voltDBClient, @Nonnull TimeSource timeSource) {
        this.alertListener = alertListener;
        this.voltDBClient = voltDBClient;
        this.timeSource = timeSource;
        this.totalQueryTimeoutCount = new AtomicInteger(0);
    }

    @Override
     public SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException, DBDownException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching subscriber profile for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new DBDownException("Unable to fetch profile for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available");
        }

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(SELECT_PROFILE_BY_ID, subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while fetching subscriber profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                logHighQueryExecutionTime(queryExecutionTime);
            }

            totalQueryTimeoutCount.set(0);

            VoltTable voltTable = clientResponse.getResults()[0];

            if (voltTable.getRowCount() == 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile does not exist with subscriber ID: " + subscriberIdentity);
                }
                return null;
            }

            voltTable.advanceRow();
            long sprReadStartTime = System.currentTimeMillis();
            SPRInfo profile = createProfile(voltTable);
            long sprReadTime = System.currentTimeMillis() - sprReadStartTime ;
            profile.setSprReadTime(sprReadTime);

            profile.setSprLoadTime(queryExecutionTime);

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Profile found for subscriber ID: " + subscriberIdentity);
            }

            return profile;

        } catch (IOException | OperationFailedException e) {
            throw new OperationFailedException("Error while fetching subscriber profile for ID: " + subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch(ProcCallException e){
            handleProcCallExceptionForGetProfile(e, subscriberIdentity);
        }

        return null;
    }


    @Override
    public void addProfile(SPRInfo profile) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Adding profile with subscriber ID: " + profile.getSubscriberIdentity());
        }

        if(voltDBClient.isAlive() == false){
            throw new OperationFailedException("Unable to add subscriber profile for subscriber ID: " + profile.getSubscriberIdentity()
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();
            voltDBClient.callProcedure(ADD_SUBSCRIBER_PROFILE, profile.getSubscriberIdentity(),
                    VoltDBUtil.createVoltDBArgsForSubscriberProfile(profile), profile.getBirthdate(), profile.getExpiryDate());
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                logHighQueryExecutionTime(queryExecutionTime);
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Profile added successfully with ID: " + profile.getSubscriberIdentity());
            }

        }catch (IOException e) {
            throw new OperationFailedException("Error while adding subscriber profile for ID: " +
                    profile.getSubscriberIdentity() + ". Reason: " + e.getMessage(), e);
        }catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "adding Profile for subscriber Id: " + profile.getSubscriberIdentity(), MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

    }

    private Object createUpdateProfileArray(EnumMap<SPRFields, String> updatedProfile, SPRInfo sprInfo) throws OperationFailedException, DBDownException {

        String[] array = new String[SPRFields.values().length - 4];

        int i = 0;

        for (SPRFields sprFields : SPRFields.values()) {

            /*
            * These fields will be skipped from here
            * modified & created date add directly from stored procedure
            * birth date & expiry date will be passed as parameter for procedure call
            */
            if (sprFields == SPRFields.MODIFIED_DATE
                    || sprFields == SPRFields.CREATED_DATE
                    || sprFields == SPRFields.EXPIRY_DATE
                    || sprFields == SPRFields.BIRTH_DATE) {
                continue;
            }

            if(updatedProfile.containsKey(sprFields)){
                String updatedValue = updatedProfile.get(sprFields);

                if (sprFields.type == Types.NUMERIC) {
                    if (updatedValue == null) {
                        sprFields.setNumericValue((SPRInfoImpl) sprInfo, null, false);
                    } else {
                        Long numericValue = parseLong(sprFields, updatedValue);
                        sprFields.setNumericValue((SPRInfoImpl) sprInfo, numericValue, false);
                    }
                } else if (sprFields.type == Types.VARCHAR) {
                    updatedValue = Strings.isNullOrBlank(updatedValue) ? null : updatedValue;
                    sprFields.setStringValue((SPRInfoImpl) sprInfo,updatedValue, false);
                } else if (sprFields.type == Types.TIMESTAMP) {
                    if (updatedValue == null) {
                        sprFields.setTimestampValue((SPRInfoImpl) sprInfo,null, false);
                    } else {
                        Long time = parseLong(sprFields, updatedValue);
                        sprFields.setTimestampValue((SPRInfoImpl) sprInfo,new Timestamp(time), false);
                    }
                }
            }

            array[i++] = sprFields.getStringValue(sprInfo);
        }

        return array;
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

    @Override
    public void addProfile(SPRInfo info, Transaction transaction) throws OperationFailedException, TransactionException {
        throw new UnsupportedOperationException("addProfile");
    }

    @Override
    public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Updating subscriber profile for subscriber ID: " + subscriberIdentity);
        }

        if(voltDBClient.isAlive() == false){
            throw new OperationFailedException("Unable to update subscriber profile for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            SPRInfo existingProfile = getProfile(subscriberIdentity);
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(UPDATE_SUBSCRIBER_PROFILE, subscriberIdentity, createUpdateProfileArray(updatedProfile, existingProfile), existingProfile.getBirthdate(), existingProfile.getExpiryDate(), existingProfile.getCreatedDate());
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while adding profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "DB update query execution time getting high, Last Query execution time was = " +
                            queryExecutionTime + " milliseconds.");
                }
            }

            totalQueryTimeoutCount.set(0);

            VoltTable vt = clientResponse.getResults()[0];
            long updateCount = vt.asScalarLong();

            if (updateCount > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile updated successfully for Subscriber ID: " + subscriberIdentity);
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile not updated. Reason: Subscriber(" + subscriberIdentity + ") not found");
                }
            }

            return (int) updateCount;

        }catch (DBDownException e) {
            throw new OperationFailedException("Error while updating subscriber profile for ID:"+
                    subscriberIdentity+". Reason: Datasource not available",ResultCode.SERVICE_UNAVAILABLE,e);
        }catch (IOException e) {
            throw new OperationFailedException("Error while updating subscriber profile for ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e) {
            VoltDBUtil.handleProcedureCallException(e, "update Profile for subscriber Id: " + subscriberIdentity,MODULE,alertListener, totalQueryTimeoutCount, voltDBClient);
        } catch (OperationFailedException e) {
            throw new OperationFailedException("Error while updating subscriber profile for ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e.getErrorCode(), e);
        }

        return 0;
    }

    @Override
    public int changeIMSpackage(String subscriberIdentity, String packageName) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Changing IMS package subscribed by subscriber ID: " + subscriberIdentity);
        }

        if(voltDBClient.isAlive() == false){
            throw new OperationFailedException("Unable to update subscriber profile for subscriber ID: " + subscriberIdentity
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        long updateCount;

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();

            ClientResponse clientResponse = voltDBClient.callProcedure(CHANGE_IMS_PACKAGE, subscriberIdentity,
                    packageName);

            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while changing IMS package. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");

                logHighQueryExecutionTime(queryExecutionTime);
            }

            totalQueryTimeoutCount.set(0);

            VoltTable vt = clientResponse.getResults()[0];
            updateCount = vt.asScalarLong();

            if (updateCount > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "IMS package changed successfully with package name: " + packageName);
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "IMS package not changed. Reason: Subscriber(" + subscriberIdentity + ") not found");
                }
            }

            return (int) updateCount;

        }catch (IOException e) {
            throw new OperationFailedException("Error while changing IMS package for subscriber ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "Changing IMS package for subscriber Id: " + subscriberIdentity,MODULE,alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return 0;
    }

    @Override
    public int purgeProfile(String subscriberIdentity) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Purging profile for subscriber ID: " + subscriberIdentity);
        }

        if(voltDBClient.isAlive() == false){
            throw new OperationFailedException("Unable to purge profile for subscriber(" + subscriberIdentity + ")"
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(SUBSCRIBER_PURGE_AND_DELETE_USAGE, subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while purging subscriber profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");
                logHighQueryExecutionTime(queryExecutionTime);
            }

            totalQueryTimeoutCount.set(0);

            VoltTable vt = clientResponse.getResults()[0];
            long noOfPurgedProfiles = vt.asScalarLong();

            if (noOfPurgedProfiles > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile purged successfully for subscriber(" + subscriberIdentity + ")");
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile can't be purged. Reason: Subscriber(" + subscriberIdentity + ") not found");
                }
            }

            return (int) noOfPurgedProfiles;

        } catch (IOException e) {
            throw new OperationFailedException("Error while purging subscriber profile for ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "purging subscriber profile for ID: "+subscriberIdentity,MODULE,alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return 0;
    }

    @Override
    public int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {
        throw new UnsupportedOperationException("purgeProfile");

    }

    @Override
    public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Marking profile as deleted for subscriber ID: " + subscriberIdentity);
        }

        if(voltDBClient.isAlive() == false){
            throw new OperationFailedException("Unable to mark delete subscriber(" + subscriberIdentity + ")."
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(MARK_DELETE_PROFILE,subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while marking subscriber profile as deleted. "+
                                "Last Query execution time: " + queryExecutionTime + " ms");
                logHighQueryExecutionTime(queryExecutionTime);
            }

            totalQueryTimeoutCount.set(0);

            VoltTable vt = clientResponse.getResults()[0];
            long noOfMarkDeletedProfiles = vt.asScalarLong();

            if (noOfMarkDeletedProfiles > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile marked deleted successfully for subscriber(" + subscriberIdentity + ")");
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile not marked deleted. Reason. Subscriber(" + subscriberIdentity + ") not found");
                }
            }

            return (int) noOfMarkDeletedProfiles;

        }catch (IOException e) {
            throw new OperationFailedException("Error while marking subscriber profile as deleted for ID: " +
                 subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "marking subscriber profile as delete for ID: "+subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return 0;
    }

    @Override
    public int restoreProfile(String subscriberIdentity) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Restoring profile for subscriber ID: " + subscriberIdentity);
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to restore subscriber(" + subscriberIdentity + ")."
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(RESTORE_SUBSCRIBER_PROFILE,subscriberIdentity);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;
            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while restoring subscriber profile. "+
                                "Last Query execution time: " + queryExecutionTime + " ms");
                logHighQueryExecutionTime(queryExecutionTime);
            }

            totalQueryTimeoutCount.set(0);

            VoltTable vt = clientResponse.getResults()[0];
            long noOfRestoredProfiles = vt.asScalarLong();

            if (noOfRestoredProfiles > 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile restored successfully for subscriber(" + subscriberIdentity + ")");
                }
            } else {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Subscriber profile can't be restored. Reason. Subscriber(" + subscriberIdentity + ") not found");
                }
            }

            return (int) noOfRestoredProfiles;

        }catch (IOException e) {
            throw new OperationFailedException("Error while restoring subscriber profile for ID: " +
                    subscriberIdentity + ". Reason: " + e.getMessage(), e);
        }catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "restoring subscriber profile for ID: "+subscriberIdentity, MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return 0;
    }

    @Override
    public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Restoring subscriber profiles");
        }
        Map<String, Integer> subscriberRestoresMap = new HashMap<>(subscriberIdentities.size());
        for (String subscriberIdentity : subscriberIdentities) {
            subscriberRestoresMap.put(subscriberIdentity, restoreProfile(subscriberIdentity));
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Subscribers restored successfully");
        }
        return subscriberRestoresMap;
    }

    @Override
    public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Fetching deleted subscriber profiles");
        }

        if (voltDBClient.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch delete marked subscribers."
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        try {

            long queryExecutionTime = timeSource.currentTimeInMillis();
            ClientResponse clientResponse = voltDBClient.callProcedure(FETCH_DELETE_MARKED_SUBSCRIBERS);
            queryExecutionTime = timeSource.currentTimeInMillis() - queryExecutionTime;

            if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

                alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
                        "DB Query execution time is high while fetching mark deleted subscriber profile. " +
                                "Last Query execution time: " + queryExecutionTime + " ms");
                logHighQueryExecutionTime(queryExecutionTime);
            }

            totalQueryTimeoutCount.set(0);

            VoltTable deletedSubscriberProfileResults = clientResponse.getResults()[0];


            List<SPRInfo> subscriberProfiles = new ArrayList<>();
            if (deletedSubscriberProfileResults.getRowCount() == 0) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "No subscriber profile exist with DELETED status");
                }
                return subscriberProfiles;
            }
            while (deletedSubscriberProfileResults.advanceRow()) {
                subscriberProfiles.add(createProfile(deletedSubscriberProfileResults));
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Deleted Subscriber profile fetched successfully");
            }

            return subscriberProfiles;

        } catch (IOException e) {
            throw new OperationFailedException("Error while fetching deleted. Reason: " + e.getMessage(), e);
        } catch (OperationFailedException e) {
            throw new OperationFailedException("Error while fetching deleted. Reason: " + e.getMessage(), e.getErrorCode(), e);
        } catch (ProcCallException e){
            VoltDBUtil.handleProcedureCallException(e, "fetching mark deleted subscriber profiles", MODULE, alertListener, totalQueryTimeoutCount, voltDBClient);
        }

        return null;
    }

    @Override
    public Transaction createTransaction() throws OperationFailedException {
        throw new UnsupportedOperationException("createTransaction");
    }

    @Override
    public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile, Transaction transaction) throws OperationFailedException, TransactionException {
        throw new OperationFailedException("updateProfile");
    }


    private SPRInfo createProfile(VoltTable voltTable) throws OperationFailedException {

        SPRInfoImpl sprInfo = new SPRInfoImpl();
        for (SPRFields sprField : SPRFields.values()) {
            if (sprField.type == Types.NUMERIC) {
                long val = voltTable.getLong(sprField.columnName);
                if (voltTable.wasNull()) {
                    sprField.setNumericValue(sprInfo, null, false);
                } else {
                    sprField.setNumericValue(sprInfo, val, false);
                }
            } else if (sprField.type == Types.VARCHAR) {
                sprField.setStringValue(sprInfo, voltTable.getString(sprField.columnName), false);
            } else if (sprField.type == Types.TIMESTAMP) {

                Timestamp val = voltTable.getTimestampAsSqlTimestamp(sprField.columnName);
                if (voltTable.wasNull()) {
                    sprField.setTimestampValue(sprInfo, null, false);
                } else {
                    sprField.setTimestampValue(sprInfo, val, false);
                }
            }
        }

        return sprInfo;
    }


    private void handleProcCallExceptionForGetProfile(ProcCallException e, String subscriberIdentity) throws OperationFailedException, DBDownException {

        ClientResponse clientResponse = e.getClientResponse();
        switch (clientResponse.getStatus()) {
            case ClientResponse.CONNECTION_LOST:
                alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
                        "Connection lost while fetching subscriber profile with ID: " + subscriberIdentity);
                throw new DBDownException("Connection is lost, "+VoltDBUtil.createErrorMessage(clientResponse));

            case ClientResponse.CONNECTION_TIMEOUT:
                verifyAndLogTotalQueryTimeOutCount();
                alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
                        "DB query timeout while fetching subscriber profile with ID: " + subscriberIdentity);
                throw new DBDownException("Connection timeout, "+VoltDBUtil.createErrorMessage(clientResponse));

            case ClientResponse.SERVER_UNAVAILABLE:
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "VoltDB is Dead. Reason: Service is not available");
                }
                throw new DBDownException("Service is not available, "+VoltDBUtil.createErrorMessage(clientResponse));
            default:
                throw new OperationFailedException("INTERNAL ERROR, Error Code: " + clientResponse.getStatus()
                        + ". Reason: " + clientResponse.getStatusString());
        }
    }

    private void verifyAndLogTotalQueryTimeOutCount() {
        if (totalQueryTimeoutCount.incrementAndGet() > CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
            }
            voltDBClient.markDead();
            totalQueryTimeoutCount.set(0);
        }
    }

    private void logHighQueryExecutionTime(long queryExecutionTime){
        if (getLogger().isWarnLogLevel()) {
            getLogger().warn(MODULE, "DB query execution time getting high, Last Query execution time was = " + queryExecutionTime + " milliseconds.");
        }
    }
}
