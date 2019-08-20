package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import org.apache.commons.collections.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class CarryForwardOperation {

    private static final String MODULE = "CRY-FWD-OPR";
    private PolicyRepository policyRepository;
    private TimeSource timeSource;

    public CarryForwardOperation(PolicyRepository policyRepository, TimeSource timeSource) {
        this.timeSource = timeSource;
        this.policyRepository = policyRepository;
    }

    public void performCarryForwardOperation(NonMonetoryBalance nonMonetoryBalance, DataBalanceOperation dataBalanceOperation, List<NonMonetoryBalance> nonMonetaryBalances) throws OperationFailedException, SQLException, TransactionException {

        if (nonMonetoryBalance.isNotExpired(timeSource) || CarryForwardStatus.CARRY_FORWARD != nonMonetoryBalance.getCarryForwardStatus()
                || policyRepository.getQuotaProfile(nonMonetoryBalance.getPackageId(), nonMonetoryBalance.getQuotaProfileId()).getCarryForward() == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().info(MODULE, "Skipping carry forward for quota profile with id: " + nonMonetoryBalance.getQuotaProfileId() + " in package with id: " + nonMonetoryBalance.getPackageId());
            }
            return;
        }

        updateCarryForwardDoneStatusForExpiredQuota(nonMonetoryBalance, dataBalanceOperation);
        carryForwardDataOrTimeForProvisionedQuota(nonMonetoryBalance, dataBalanceOperation, nonMonetaryBalances);
    }

    private void carryForwardDataOrTimeForProvisionedQuota(NonMonetoryBalance expiredNonMonetaryBalance, DataBalanceOperation dataBalanceOperation,
                                                           List<NonMonetoryBalance> nonMonetaryBalances) {
        List<NonMonetoryBalance> nonMonetoryBalancesToInsert = dataBalanceOperation.getNonMonetaryBalanceInsertOperation();
        long balanceLevel = policyRepository.getQuotaProfile(expiredNonMonetaryBalance.getPackageId(), expiredNonMonetaryBalance.getQuotaProfileId()).getBalanceLevel().getFupLevel();
        if (CollectionUtils.isNotEmpty(nonMonetoryBalancesToInsert)) {
            for (NonMonetoryBalance nonMonetoryBalanceToProvision : nonMonetoryBalancesToInsert) {
                if ((Strings.isNullOrEmpty(nonMonetoryBalanceToProvision.getSubscriptionId()) ? true : nonMonetoryBalanceToProvision.getSubscriptionId().equals(expiredNonMonetaryBalance.getSubscriptionId()))
                        && nonMonetoryBalanceToProvision.getQuotaProfileId().equals(expiredNonMonetaryBalance.getQuotaProfileId())
                        && nonMonetoryBalanceToProvision.getPackageId().equals(expiredNonMonetaryBalance.getPackageId())
                        && nonMonetoryBalanceToProvision.getLevel() == expiredNonMonetaryBalance.getLevel()
                        && nonMonetoryBalanceToProvision.getServiceId() == expiredNonMonetaryBalance.getServiceId()
                        && nonMonetoryBalanceToProvision.getLevel() <= balanceLevel) {
                    setCarryForwardData(expiredNonMonetaryBalance, nonMonetoryBalanceToProvision);
                    return;
                }
            }
        }

        //already provisioned data
        for (NonMonetoryBalance provisionedNonMonetaryBalanceUpdate : nonMonetaryBalances) {
            //avoid current and expired balances
            if (provisionedNonMonetaryBalanceUpdate.isNotExpired(timeSource) == false || provisionedNonMonetaryBalanceUpdate.getStatus().equals(ResetBalanceStatus.RESET_DONE)
                    || provisionedNonMonetaryBalanceUpdate.getLevel() > balanceLevel) {
                continue;
            }

            if ((Strings.isNullOrEmpty(provisionedNonMonetaryBalanceUpdate.getSubscriptionId()) ? true : provisionedNonMonetaryBalanceUpdate.getSubscriptionId().equals(expiredNonMonetaryBalance.getSubscriptionId()))
                    && provisionedNonMonetaryBalanceUpdate.getQuotaProfileId().equals(expiredNonMonetaryBalance.getQuotaProfileId())
                    && provisionedNonMonetaryBalanceUpdate.getPackageId().equals(expiredNonMonetaryBalance.getPackageId())
                    && provisionedNonMonetaryBalanceUpdate.getServiceId() == expiredNonMonetaryBalance.getServiceId()
                    && provisionedNonMonetaryBalanceUpdate.getLevel() == expiredNonMonetaryBalance.getLevel()) {
                if (setCarryForwardData(expiredNonMonetaryBalance, provisionedNonMonetaryBalanceUpdate)) {
                    dataBalanceOperation.setNonMonetaryBalanceUpdateOperations(provisionedNonMonetaryBalanceUpdate);
                }
                break;
            }
        }
    }

    private void updateCarryForwardDoneStatusForExpiredQuota(NonMonetoryBalance nonMonetoryBalance, DataBalanceOperation dataBalanceOperation) {
        List<NonMonetoryBalance> nonMonetoryBalancesToUpdate = dataBalanceOperation.getNonMonetaryBalanceUpdateOperation();
        setCarryForwardDoneStatus(nonMonetoryBalance);
        //already provisioned data
        if (CollectionUtils.isEmpty(nonMonetoryBalancesToUpdate) ||
                (CollectionUtils.isNotEmpty(nonMonetoryBalancesToUpdate) && nonMonetoryBalancesToUpdate.contains(nonMonetoryBalance) == false)) {
            dataBalanceOperation.setNonMonetaryBalanceUpdateOperations(nonMonetoryBalance);
            return;
        }
    }

    private boolean setCarryForwardData(NonMonetoryBalance expiredNonMonetaryBalance, NonMonetoryBalance provisionedNonMonetaryBalance) {
        List<Map<String, QuotaProfileDetail>> carryForwardLimitAllLimitServiceWise =
                policyRepository.getQuotaProfile(expiredNonMonetaryBalance.getPackageId(), expiredNonMonetaryBalance.getQuotaProfileId()).getAllLevelServiceWiseQuotaProfileDetails();

        if (isUnlimitedOrUndefined(provisionedNonMonetaryBalance.getBillingCycleTotalVolume()) && isUnlimitedOrUndefined(provisionedNonMonetaryBalance.getBillingCycleTime())) {
            return false;
        }

        volumeCarryForwardBasedOnCarryForwardLimit(expiredNonMonetaryBalance, provisionedNonMonetaryBalance, carryForwardLimitAllLimitServiceWise);
        timeCarryForwardBasedOnCarryForwardLimit(expiredNonMonetaryBalance, provisionedNonMonetaryBalance, carryForwardLimitAllLimitServiceWise);

        return true;
    }

    private void timeCarryForwardBasedOnCarryForwardLimit(NonMonetoryBalance expiredNonMonetaryBalance, NonMonetoryBalance provisionedNonMonetaryBalance, List<Map<String, QuotaProfileDetail>> carryForwardLimitAllLimitServiceWise) {
        long quotaToCarryForwardBasedOnCap;
        if (isUnlimitedOrUndefined(provisionedNonMonetaryBalance.getBillingCycleTime()) == false) {
            quotaToCarryForwardBasedOnCap = expiredNonMonetaryBalance.getBillingCycleAvailableTime();
            boolean continueTraverse = true;

            for(int index = 0; index < carryForwardLimitAllLimitServiceWise.size() && continueTraverse; index++) {
                for (Map.Entry<String, QuotaProfileDetail> serviceData : carryForwardLimitAllLimitServiceWise.get(index).entrySet()) {
                    RncProfileDetail quotaProfileDetail = (RncProfileDetail) serviceData.getValue();
                    if ((isUnlimitedOrUndefined(quotaProfileDetail.getTimeCarryForwardLimit()) == false)
                            && (expiredNonMonetaryBalance.getServiceId() == quotaProfileDetail.getDataServiceType().getServiceIdentifier())
                            && (expiredNonMonetaryBalance.getLevel() == quotaProfileDetail.getFupLevel())) {
                        quotaToCarryForwardBasedOnCap = expiredNonMonetaryBalance.getBillingCycleAvailableTime() < quotaProfileDetail.getTimeCarryForwardLimit() ? expiredNonMonetaryBalance.getBillingCycleAvailableTime() : quotaProfileDetail.getTimeCarryForwardLimit();
                        continueTraverse = false;
                        break;
                    }
                }
            }
            provisionedNonMonetaryBalance.setCarryForwardTime(quotaToCarryForwardBasedOnCap);
            provisionedNonMonetaryBalance.setBillingCycleAvailableTime(provisionedNonMonetaryBalance.getBillingCycleAvailableTime() + quotaToCarryForwardBasedOnCap);
        }
    }

    private void volumeCarryForwardBasedOnCarryForwardLimit(NonMonetoryBalance expiredNonMonetaryBalance, NonMonetoryBalance provisionedNonMonetaryBalance, List<Map<String, QuotaProfileDetail>> carryForwardLimitAllLimitServiceWise) {
        long quotaToCarryForwardBasedOnCap;

        if (isUnlimitedOrUndefined(provisionedNonMonetaryBalance.getBillingCycleTotalVolume()) == false) {
            quotaToCarryForwardBasedOnCap = expiredNonMonetaryBalance.getBillingCycleAvailableVolume();

            boolean continueTraverse = true;
            for(int index = 0; index < carryForwardLimitAllLimitServiceWise.size() && continueTraverse; index++) {
                for (Map.Entry<String, QuotaProfileDetail> serviceData : carryForwardLimitAllLimitServiceWise.get(index).entrySet()) {
                    RncProfileDetail quotaProfileDetail = (RncProfileDetail) serviceData.getValue();
                    if ((isUnlimitedOrUndefined(quotaProfileDetail.getVolumeCarryForwardLimit()) == false)
                            && (expiredNonMonetaryBalance.getServiceId() == quotaProfileDetail.getDataServiceType().getServiceIdentifier())
                            && (expiredNonMonetaryBalance.getLevel() == quotaProfileDetail.getFupLevel()) ) {
                        quotaToCarryForwardBasedOnCap = expiredNonMonetaryBalance.getBillingCycleAvailableVolume() < quotaProfileDetail.getVolumeCarryForwardLimit() ? expiredNonMonetaryBalance.getBillingCycleAvailableVolume() : quotaProfileDetail.getVolumeCarryForwardLimit();
                        continueTraverse = false;
                        break;
                    }
                }
            }
            provisionedNonMonetaryBalance.setCarryForwardVolume(quotaToCarryForwardBasedOnCap);
            provisionedNonMonetaryBalance.setBillingCycleAvailableVolume(provisionedNonMonetaryBalance.getBillingCycleAvailableVolume() + quotaToCarryForwardBasedOnCap);
        }
    }

    private boolean isUnlimitedOrUndefined(long units) {
        return units == CommonConstants.UNLIMITED_QCF_QUOTA || units == CommonConstants.QUOTA_UNDEFINED;
    }

    private void setCarryForwardDoneStatus(NonMonetoryBalance nonMonetoryBalance) {
        if (Objects.nonNull(nonMonetoryBalance)) {
            nonMonetoryBalance.setCarryForwardStatus(CarryForwardStatus.CARRY_FORWARD_DONE);
        }
    }
}
