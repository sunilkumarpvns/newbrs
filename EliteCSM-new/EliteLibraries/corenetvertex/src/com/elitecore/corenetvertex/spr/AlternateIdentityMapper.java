package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.data.AlternateIdActiveStatusPredicate;
import com.elitecore.corenetvertex.spr.data.AlternateIdType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.ADD_EXTERNAL_ALTERNATE_ID;
import static com.elitecore.corenetvertex.constants.CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR;

public class AlternateIdentityMapper {
    private static final String MODULE = "ALTERNATE-ID-MAPPER";
    private static final String DATASOURCE_NOT_AVAILABLE = "Datasource not available";
    private static final String SELECT_SUBSCRIBERID_QUERY = "SELECT SUBSCRIBER_ID,STATUS FROM TBLM_ALTERNATE_IDENTITY WHERE ALTERNATE_ID = ?";
    private static final String SELECT_ALTERNATEID_QUERY = "SELECT ALTERNATE_ID FROM TBLM_ALTERNATE_IDENTITY WHERE SUBSCRIBER_ID = ? AND TYPE= '"+ AlternateIdType.SPR.name()+"'";
    private static final String INSERT_MAPPING = "INSERT INTO TBLM_ALTERNATE_IDENTITY (SUBSCRIBER_ID,ALTERNATE_ID,TYPE,STATUS) VALUES (?,?,?,?)";
    private static final String UPDATE_MAPPING = "UPDATE TBLM_ALTERNATE_IDENTITY SET ALTERNATE_ID = ? WHERE SUBSCRIBER_ID = ? AND TYPE= '"+ AlternateIdType.SPR.name()+"'";
    private static final String REMOVE_MAPPING = "DELETE FROM TBLM_ALTERNATE_IDENTITY WHERE SUBSCRIBER_ID = ? ";



    private static final String EXTERNAL_ALTERNATEID_COUNT = "SELECT COUNT(1) FROM TBLM_ALTERNATE_IDENTITY WHERE SUBSCRIBER_ID = ? AND TYPE= '"+ AlternateIdType.EXTERNAL.name()+"'";
    private static final String SELECT_ALTERNATEID_WITH_STATUS_QUERY = "SELECT ALTERNATE_ID,STATUS,TYPE FROM TBLM_ALTERNATE_IDENTITY WHERE SUBSCRIBER_ID = ?";
    private static final String REMOVE_ALTERNATE_MAPPING_BY_TYPE = "DELETE FROM TBLM_ALTERNATE_IDENTITY WHERE SUBSCRIBER_ID = ? AND ALTERNATE_ID=? AND TYPE= ?";
    private static final String UPDATE_EXTERNAL_ALTERNATE_ID_STATUS = "UPDATE TBLM_ALTERNATE_IDENTITY SET STATUS=? WHERE SUBSCRIBER_ID = ? AND ALTERNATE_ID = ? AND TYPE= '"+ AlternateIdType.EXTERNAL.name()+"'";
    private static final String UPDATE_EXTERNAL_ALTERNATE_ID = "UPDATE TBLM_ALTERNATE_IDENTITY SET ALTERNATE_ID = ? WHERE SUBSCRIBER_ID = ? and ALTERNATE_ID=? AND TYPE= '"+ AlternateIdType.EXTERNAL.name()+"'";
    private static final int MAX_ALTERNATE_ID = 10;



    private TransactionFactory transactionFactory;
    private ExternalAlternateIdEDRListener externalAlternateIdEDRListener;

    public AlternateIdentityMapper(TransactionFactory transactionFactory, ExternalAlternateIdEDRListener externalAlternateIdEDRListener) {
        this.transactionFactory = transactionFactory;
        this.externalAlternateIdEDRListener = externalAlternateIdEDRListener;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Insert Alternate ID mapping query: " + INSERT_MAPPING);
            getLogger().debug(MODULE, "Select Alternate ID by Subscriber ID query: " + SELECT_ALTERNATEID_QUERY);
            getLogger().debug(MODULE, "Select Subscriber ID by Alternate ID query: " + SELECT_SUBSCRIBERID_QUERY);
            getLogger().debug(MODULE, "Update Alternate ID mapping query: " + UPDATE_MAPPING);
            getLogger().debug(MODULE, "Remove Alternate ID mapping query: " + REMOVE_MAPPING);
            getLogger().debug(MODULE, "Select alternate id with status query: " + SELECT_ALTERNATEID_WITH_STATUS_QUERY);
            getLogger().debug(MODULE, "Update external alternate id status query: " + UPDATE_EXTERNAL_ALTERNATE_ID_STATUS);
            getLogger().debug(MODULE, "Update external alternate id  query: " + UPDATE_EXTERNAL_ALTERNATE_ID);
            getLogger().debug(MODULE, "Remove external alternate id  query: " + REMOVE_ALTERNATE_MAPPING_BY_TYPE);
        }
    }


    public @Nullable String getSubscriberId(String alternateId) throws OperationFailedException {
        return getSubscriberId(alternateId, AlternateIdActiveStatusPredicate.getInstance());
    }

    public @Nullable String getSubscriberId(String alternateId,Predicate<String> statusPredicate) throws OperationFailedException {

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to fetch subscriber identity mapping for alternate Id: " + alternateId
                        + ". Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
        if (dbTransaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to fetch subscriber identity mapping for alternate Id: " + alternateId
                        + " . Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;
        try {
            dbTransaction.begin();
            prepareStatement = dbTransaction.prepareStatement(SELECT_SUBSCRIBERID_QUERY);
            prepareStatement.setString(1, alternateId);
            resultSet = prepareStatement.executeQuery();

            if (resultSet.next()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscriber Id fetched successfully for alternate id: " + alternateId);
                }
                if(statusPredicate.test(resultSet.getString("STATUS"))){
                    return resultSet.getString("SUBSCRIBER_ID");
                }
            }
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Subscriber id not found for alternate id: " + alternateId);
            }
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to fetch subscriber identity mapping for alternate id: " + alternateId + ". Reason: "
                    + e.getMessage());
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to fetch subscriber identity mapping for alternate id: " + alternateId + ". Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to fetch subscriber identity mapping for alternate id: " + alternateId + ". Reason: "
                    + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to fetch subscriber identity mapping for alternate id: " + alternateId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(prepareStatement);
            endTransaction(dbTransaction);
        }
        return null;
    }


    private void endTransaction(DBTransaction transaction) {
        try {
            transaction.end();
        } catch (Exception e) {
            getLogger().error(MODULE, "Error in ending transaction while alternate identity mapping. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    public String getAlternateId(String subscriberId) throws OperationFailedException {
        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to fetch alternate identity mapping for subscriber Id: " + subscriberId
                        + " . Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
        if (dbTransaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to fetch alternate identity mapping for subscriber Id: " + subscriberId
                        + " . Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;
        try {
            dbTransaction.begin();
            prepareStatement = dbTransaction.prepareStatement(SELECT_ALTERNATEID_QUERY);
            prepareStatement.setString(1, subscriberId);
            resultSet = prepareStatement.executeQuery();

            if (resultSet.next()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate Id fetched successfully for subscriber id: " + subscriberId);
                }
                return resultSet.getString("ALTERNATE_ID");
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Alternate id not found for subscriber id: " + subscriberId);
            }
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage());
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(prepareStatement);
            endTransaction(dbTransaction);
        }
        return null;
    }

    public void addMapping(String subscriberId, String alternateId) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Add alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            addMapping(subscriberId, alternateId,AlternateIdType.SPR, CommonConstants.STATUS_ACTIVE,transaction);
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to add alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to add alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            endTransaction(transaction);
        }
    }

    private void addMapping(String subscriberId, String alternateId,AlternateIdType type,String status,Transaction transaction) throws TransactionException, SQLException {
        PreparedStatement prepareStatement = null;
        try {

            prepareStatement = transaction.prepareStatement(INSERT_MAPPING);
            prepareStatement.setString(1, subscriberId);
            prepareStatement.setString(2, alternateId);
            prepareStatement.setString(3, type.name());
            prepareStatement.setString(4, status);
            int result = prepareStatement.executeUpdate();

            if (result > 0) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate Identity(" + subscriberId + "," + alternateId + ") mapping added successfully");
                }

            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate Identity(" + subscriberId + "," + alternateId + ") mapping not added");
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Add alternate id mapping operation completed for Subscriber Id("+subscriberId +")");
            }
        } finally {
            closeQuietly(prepareStatement);
        }
    }

    private SubscriberAlternateIds getAlternateIds(String subscriberId,DBTransaction transaction) throws TransactionException,SQLException{
        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;
        try {

            prepareStatement = transaction.prepareStatement(SELECT_ALTERNATEID_WITH_STATUS_QUERY);
            prepareStatement.setString(1, subscriberId);
            resultSet = prepareStatement.executeQuery();
            List<SubscriberAlternateIdStatusDetail> alternateIdStatusDetails = new ArrayList<>();
            while (resultSet.next()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate Id fetched successfully for subscriber id: " + subscriberId);
                }
                String alternateId = resultSet.getString(1);
                String status = resultSet.getString(2);
                String type =  resultSet.getString(3);
                alternateIdStatusDetails.add(new SubscriberAlternateIdStatusDetail(alternateId,status,type));
            }

            if(CollectionUtils.isEmpty(alternateIdStatusDetails)){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id not found for subscriber id: " + subscriberId);
                }
                return null;
            }
            return new SubscriberAlternateIds(subscriberId,alternateIdStatusDetails);
        }  finally {
            closeQuietly(resultSet);
            closeQuietly(prepareStatement);
        }



    }

    public void replaceMapping(String oldSubscriberId, String newSubscriberId, String alternateId) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Replacing alternate id mapping operation started for old subscriber Id: "  + oldSubscriberId + " to new subscriber Id: " + newSubscriberId);
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to replace new alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + ". Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to replace new alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + ". Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        try{
            transaction.begin();
            SubscriberAlternateIds alternateIds = getAlternateIds(oldSubscriberId, transaction);
            if(Objects.isNull(alternateIds)){
                return;
            }
            removeMapping(oldSubscriberId, transaction);

            if (Strings.isNullOrBlank(alternateId) == false) {
                addMapping(newSubscriberId, alternateId, AlternateIdType.SPR, CommonConstants.STATUS_ACTIVE, transaction);
            }
            if (Objects.nonNull(alternateIds) && CollectionUtils.isEmpty(alternateIds.getSubscriberAlternateIdStatusList())) {
                return;
            }
            for (SubscriberAlternateIdStatusDetail detail:alternateIds.getSubscriberAlternateIdStatusList()) {
                if(AlternateIdType.EXTERNAL.name().equalsIgnoreCase(detail.getType())){
                    addMapping(newSubscriberId,detail.getAlternateId(),AlternateIdType.EXTERNAL,detail.getStatus(),transaction);
                }
            }
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to replace new alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + ". Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to replace new alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + ". Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to replace new alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + ". Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to replace new alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            endTransaction(transaction);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Replacing alternate identity mapping(" + newSubscriberId + ", " + alternateId + ") with old subscriber Id: " + oldSubscriberId + " completed");
        }
    }

    public int updateMapping(String subscriberId, String alternateId) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to update alternate id mapping(" + subscriberId + ", " + alternateId
                        + "). Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to update alternate id mapping(" + subscriberId + ", " + alternateId
                        + "). Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement prepareStatement = null;

        try {
            transaction.begin();
            prepareStatement = transaction.prepareStatement(UPDATE_MAPPING);
            prepareStatement.setString(1, alternateId);
            prepareStatement.setString(2, subscriberId);
            int result = prepareStatement.executeUpdate();

            if (result > 0) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id mapping(" + subscriberId + ", " + alternateId
                            + ") updated successfully");
                }
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id mapping(" + subscriberId + ", " + alternateId
                            + ") not updated");
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update alternate id mapping operation completed for Subscriber Id("+subscriberId +")");
            }

            return result;
        }catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage(),
                    resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage(),
                    ResultCode.INTERNAL_ERROR);
        } finally {
            closeQuietly(prepareStatement);
            endTransaction(transaction);
        }
    }

    public int removeMapping(String subscriberId) throws OperationFailedException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Remove alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            return removeMapping(subscriberId, transaction);
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            endTransaction(transaction);
        }
    }

    private int removeMapping(String subscriberId,Transaction transaction) throws TransactionException, SQLException {

        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = transaction.prepareStatement(REMOVE_MAPPING);
            prepareStatement.setString(1, subscriberId);
            int result = prepareStatement.executeUpdate();

            if (result > 0) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate identity mapping for subscriber(" + subscriberId + ") removed successfully");
                }
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate identity mapping for subscriber(" + subscriberId + ") not exist.");
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Remove alternate id mapping operation completed for Subscriber Id("+subscriberId +")");
            }

            return result;
        } finally {
            closeQuietly(prepareStatement);
        }
    }

    public SubscriberAlternateIds getAlternateIds(String subscriberId) throws OperationFailedException {

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to fetch alternate identity mapping for subscriber Id: " + subscriberId
                        + " . Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
        if (dbTransaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to fetch alternate identity mapping for subscriber Id: " + subscriberId
                        + " . Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }


        try {
            dbTransaction.begin();
            return getAlternateIds(subscriberId,dbTransaction);
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;
            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage());
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            endTransaction(dbTransaction);
        }

    }

    public void addExternalAlternateIdMapping(String subscriberId, String alternateId) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Add alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            if(getExternalAlternateIdentityCount(subscriberId,transaction) >= MAX_ALTERNATE_ID){
                throw new OperationFailedException("Max "+MAX_ALTERNATE_ID+" alternate Ids can be configured");
            }
            addMapping(subscriberId, alternateId,AlternateIdType.EXTERNAL,CommonConstants.STATUS_ACTIVE,transaction);
            addExternalAlternateIdEDR(subscriberId, alternateId, ADD_EXTERNAL_ALTERNATE_ID);
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to add alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to add alternate identity mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to add alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            endTransaction(transaction);
        }
    }

    private int getExternalAlternateIdentityCount(String subscriberId, Transaction transaction) throws TransactionException, SQLException {
        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;
        try {

            prepareStatement = transaction.prepareStatement(EXTERNAL_ALTERNATEID_COUNT);
            prepareStatement.setString(1, subscriberId);
            resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(prepareStatement);
        }
    }

    public int removeAlternateIdMappingByType(String subscriberId, String alternateId, AlternateIdType type, String currentStatus) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Remove alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: Datasource not available" );
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        try {
            transaction.begin();
            return removeMapping(subscriberId,alternateId,type, currentStatus, transaction);
        } catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: "
                    + e.getMessage(), resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to remove alternate identity mapping for subscriber Id("+subscriberId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to fetch alternate identity mapping for subscriber id: " + subscriberId + ". Reason: "
                    + e.getMessage(), ResultCode.INTERNAL_ERROR);
        } finally {
            endTransaction(transaction);
        }
    }

    private int removeMapping(String subscriberId, String alternateId, AlternateIdType type, String currentStatus, Transaction transaction) throws TransactionException, SQLException {

        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = transaction.prepareStatement(REMOVE_ALTERNATE_MAPPING_BY_TYPE);
            prepareStatement.setString(1, subscriberId);
            prepareStatement.setString(2, alternateId);
            prepareStatement.setString(3, type.name());
            int result = prepareStatement.executeUpdate();

            if (result > 0) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate identity mapping for subscriber(" + subscriberId + ") removed successfully");
                }

                removeExternalAlternateIdEDR(subscriberId, alternateId, currentStatus, CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate identity mapping for subscriber(" + subscriberId + ") not exist.");
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Remove alternate id mapping operation completed for Subscriber Id("+subscriberId +")");
            }

            return result;
        } finally {
            closeQuietly(prepareStatement);
        }
    }

    public int changeExternalAlternateIdentityStatus(String subscriberId, String alternateId, String currentStatus, String updatedStatus) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to update alternate id mapping(" + subscriberId + ", " + alternateId
                        + "). Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to update alternate id mapping(" + subscriberId + ", " + alternateId
                        + "). Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement prepareStatement = null;

        try {
            transaction.begin();
            prepareStatement = transaction.prepareStatement(UPDATE_EXTERNAL_ALTERNATE_ID_STATUS);
            prepareStatement.setString(1, updatedStatus);
            prepareStatement.setString(2, subscriberId);
            prepareStatement.setString(3, alternateId);
            int result = prepareStatement.executeUpdate();

            if (result > 0) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id mapping(" + subscriberId + ", " + alternateId
                            + ") updated successfully");
                }

                updateExternalAlternateIdEDR(subscriberId, alternateId, "", currentStatus, updatedStatus, CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);

            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id mapping(" + subscriberId + ", " + alternateId
                            + ") not updated");
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update alternate id mapping operation completed for Subscriber Id("+subscriberId +")");
            }

            return result;
        }catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage(),
                    resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to update alternate id mapping("+subscriberId+", "+alternateId+"). Reason: " + e.getMessage(),
                    ResultCode.INTERNAL_ERROR);
        } finally {
            closeQuietly(prepareStatement);
            endTransaction(transaction);
        }
    }

    public int updateExternalAlternateIdentity(String subscriberId, String oldAlternateId, String updatedAlternateId, String currentStatus) throws OperationFailedException {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Update alternate id mapping operation started for Subscriber Id("+subscriberId +")");
        }

        if (transactionFactory.isAlive() == false) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to update alternate id mapping(" + subscriberId + ", " + oldAlternateId
                        + "). Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        if (transaction == null) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Unable to update alternate id mapping(" + subscriberId + ", " + oldAlternateId
                        + "). Reason: Datasource not available");
            }
            throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
        }

        PreparedStatement prepareStatement = null;

        try {
            transaction.begin();
            prepareStatement = transaction.prepareStatement(UPDATE_EXTERNAL_ALTERNATE_ID);
            prepareStatement.setString(1, updatedAlternateId);
            prepareStatement.setString(2, subscriberId);
            prepareStatement.setString(3, oldAlternateId);
            int result = prepareStatement.executeUpdate();

            if (result > 0) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id mapping(" + subscriberId + ", " + oldAlternateId
                            + ") updated successfully");
                }

                updateExternalAlternateIdEDR(subscriberId, oldAlternateId, updatedAlternateId, currentStatus, "", UPDATE_EXTERNAL_ALTERNATE_ID_EDR);

            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Alternate id mapping(" + subscriberId + ", " + oldAlternateId
                            + ") not updated");
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Update alternate id mapping operation completed for Subscriber Id("+subscriberId +")");
            }

            return result;
        }catch (TransactionException e) {
            ResultCode resultCode = ResultCode.INTERNAL_ERROR;

            if (e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND) {
                resultCode = ResultCode.SERVICE_UNAVAILABLE;
            }
            getLogger().error(MODULE, "Unable to update alternate id mapping("+subscriberId+", "+oldAlternateId+"). Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            throw new OperationFailedException("Unable to update alternate id mapping("+subscriberId+", "+oldAlternateId+"). Reason: " + e.getMessage(),
                    resultCode);
        } catch (Exception e) {
            getLogger().error(MODULE, "Unable to update alternate id mapping("+subscriberId+", "+oldAlternateId+"). Reason: " + e.getMessage() );
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Unable to update alternate id mapping("+subscriberId+", "+oldAlternateId+"). Reason: " + e.getMessage(),
                    ResultCode.INTERNAL_ERROR);
        } finally {
            closeQuietly(prepareStatement);
            endTransaction(transaction);
        }

    }

    private void addExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String operation) {
        if (Objects.nonNull(externalAlternateIdEDRListener)) {
            externalAlternateIdEDRListener.addExternalAlternateIdEDR(subscriberIdentity, alternateIdentity, operation);
        }
    }

    private void updateExternalAlternateIdEDR(String subscriberIdentity, String oldAlternateIdentity, String updatedAlternateIdentity, String currentStatus, String updatedStatus, String operation) {
        if (Objects.nonNull(externalAlternateIdEDRListener)) {
            externalAlternateIdEDRListener.updateExternalAlternateIdEDR(subscriberIdentity, oldAlternateIdentity, updatedAlternateIdentity, currentStatus, updatedStatus, operation);
        }
    }

    private void removeExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String currentStatus, String operation) {
        if (Objects.nonNull(externalAlternateIdEDRListener)) {
            externalAlternateIdEDRListener.removeExternalAlternateIdEDR(subscriberIdentity, alternateIdentity, currentStatus, operation);
        }
    }
}
