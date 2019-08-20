package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ABMFResetOperations {
    private static final String MODULE = "ABMF-RESET-OPR";
    private CarryForwardOperation carryForwardOperation;
    private BalanceResetOperation balanceResetOperation;
    private DataBalanceResetDBOperation dataBalanceResetDBOperation;
    private TimeSource timeSource;
    private DataBalanceOperationFactory dataBalanceOperationFactory;

    public ABMFResetOperations(BalanceResetOperation balanceResetOperation, CarryForwardOperation carryForwardOperation,
                               DataBalanceResetDBOperation dataBalanceResetDBOperation, TimeSource timeSource, DataBalanceOperationFactory dataBalanceOperationFactory) {
        this.balanceResetOperation = balanceResetOperation;
        this.carryForwardOperation = carryForwardOperation;
        this.dataBalanceResetDBOperation = dataBalanceResetDBOperation;
        this.timeSource = timeSource;
        this.dataBalanceOperationFactory = dataBalanceOperationFactory;
    }

    public void performBalanceOperations(@Nonnull SPRInfo sprInfo, TransactionFactory transactionFactory, List<NonMonetoryBalance> nonMonetaryBalances) throws OperationFailedException {
        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to fetch balance for subscriber ID: " + sprInfo.getSubscriberIdentity()
                    + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();
        try {
            if (transaction == null) {
                throw new OperationFailedException("Unable to perform data balance operation for subscriber ID: " + sprInfo.getSubscriberIdentity()
                        + " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
            }
            transaction.begin();

            DataBalanceOperation dataBalanceOperation = dataBalanceOperationFactory.createDataBalanceOperation();
            for (NonMonetoryBalance nonMonetoryBalance : nonMonetaryBalances) {
                NonMonetoryBalance newlyProvisionedBalance = balanceResetOperation.performDataBalanceOperations(nonMonetoryBalance, sprInfo);
                if (Objects.isNull(newlyProvisionedBalance) == false) {
                    dataBalanceOperation.setNonMonetaryBalanceInsertOperations(newlyProvisionedBalance);

                    //reset statuses
                    nonMonetoryBalance.setStatus(ResetBalanceStatus.RESET_DONE);
                    dataBalanceOperation.setNonMonetaryBalanceUpdateOperations(nonMonetoryBalance);
                }
            }

            for(NonMonetoryBalance nonMonetoryBalance : nonMonetaryBalances) {
                //Skip balances that are still active, carry forward not applied on package
                if(nonMonetoryBalance.isNotExpired(timeSource)) {
                    continue;
                }
                //Process the expired balances and determine whether it requires quota carry forward or not
                carryForwardOperation.performCarryForwardOperation(nonMonetoryBalance,dataBalanceOperation, nonMonetaryBalances);
            }

            int batchSize = CommonConstants.DEFAULT_BATCH_SIZE;

            if (CollectionUtils.isNotEmpty(dataBalanceOperation.getNonMonetaryBalanceInsertOperation())) {
                dataBalanceResetDBOperation.executeInsertOperations(dataBalanceOperation.getNonMonetaryBalanceInsertOperation(), transaction, batchSize);
            }

            if (CollectionUtils.isNotEmpty(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation())) {
                dataBalanceResetDBOperation.executeUpdateOperations(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation(), transaction, batchSize);
            }

            //To filter out future balances from current balance
            List<NonMonetoryBalance> newlyProvisionedBalances = dataBalanceOperation.getNonMonetaryBalanceInsertOperation();
            if(Collectionz.isNullOrEmpty(newlyProvisionedBalances)){
                return;
            }

            for(NonMonetoryBalance nonMonetoryBalance : newlyProvisionedBalances) {
                if(getCurrentTime() >= nonMonetoryBalance.getStartTime()) {
                    nonMonetaryBalances.add(nonMonetoryBalance);
                }
            }

        } catch (SQLException | TransactionException e) {
            getLogger().error(MODULE, "Error while performing DB Operation for reset data balance. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new OperationFailedException("Error while performing DB Operation for reset data balance. Reason: " + e.getMessage(), e);
        } catch (Exception ex) {
            getLogger().error(MODULE, "Error while reset operation. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
            throw new OperationFailedException("Error while reset operation. Reason: " + ex.getMessage(), ex);
        } finally {
            endTransaction(transaction);
        }
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

    protected long getCurrentTime() {
        return timeSource.currentTimeInMillis();
    }
}
