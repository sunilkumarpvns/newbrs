package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.RenewalIntervalUtility;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.ZERO_VALUE;

public class BalanceResetOperation {

    private static final String MODULE = "BAL-RST-OPR";
    private PolicyRepository policyRepository;
    private TimeSource timeSource;

    public BalanceResetOperation(PolicyRepository policyRepository,
                                 TimeSource timeSource) {
        this.policyRepository = policyRepository;
        this.timeSource = timeSource;
    }

    public NonMonetoryBalance performDataBalanceOperations(NonMonetoryBalance nonMonetoryBalance, SPRInfo sprInfo) throws OperationFailedException, SQLException, TransactionException {
        LinkedHashMap<String, Subscription> activeSubscriptions = null;

            if (timeSource.currentTimeInMillis() <= nonMonetoryBalance.getBillingCycleResetTime() - CommonConstants.RESET_DAY_INTERVAL || ResetBalanceStatus.RESET != nonMonetoryBalance.getStatus()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().info(MODULE, "Skipping provisioning of new balance because renewal interval is not configured for quota profile with id: " + nonMonetoryBalance.getQuotaProfileId() + " in package with id: " + nonMonetoryBalance.getPackageId());
                }
                return null;
            }

            long packageExpiryTime;

            if (Objects.isNull(nonMonetoryBalance.getSubscriptionId())) {
                packageExpiryTime = Objects.nonNull(sprInfo.getExpiryDate()) ? sprInfo.getExpiryDate().getTime() : ResetTimeUtility.calculateQuotaResetTime();
            } else {
                if (Objects.isNull(activeSubscriptions)) {
                    activeSubscriptions = sprInfo.getActiveSubscriptions(timeSource.currentTimeInMillis());
                }

                Subscription subscription = activeSubscriptions.get(nonMonetoryBalance.getSubscriptionId());
                packageExpiryTime = subscription.getEndTime().getTime();

            }


            if (Strings.isNullOrBlank(nonMonetoryBalance.getRenewalInterval())) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping provisioning of new balance because renewal interval is not configured for quota profile with id: " + nonMonetoryBalance.getQuotaProfileId() + " in package with id: " + nonMonetoryBalance.getPackageId());
                }
                return null;
            }

            return addNextBillingCycleBalance(nonMonetoryBalance, packageExpiryTime, sprInfo.getBillingDate());
    }

    private NonMonetoryBalance addNextBillingCycleBalance(NonMonetoryBalance nonMonetoryBalance, long packageExpiryTime, Integer billingDay) throws OperationFailedException, SQLException, TransactionException {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Add next billing cycle balance operation started for subscriber ID: " + nonMonetoryBalance.getSubscriberIdentity());
        }

        NonMonetoryBalance copyOfNonMonetaryBalance = nonMonetoryBalance.copy();

        String renewalInterval = copyOfNonMonetaryBalance.getRenewalInterval();

        String[] arrRenewalInterval = renewalInterval.split(CommonConstants.USAGE_KEY_SEPARATOR);
        RenewalIntervalUnit renewalIntervalUnit = RenewalIntervalUnit.fromRenewalIntervalUnit(arrRenewalInterval[1]);
        int renewalIntervalValue = Integer.parseInt(arrRenewalInterval[0]);

        Timestamp billingCycleResetTime = new Timestamp(copyOfNonMonetaryBalance.getBillingCycleResetTime());
        if (RenewalIntervalUnit.MONTH_END == renewalIntervalUnit || RenewalIntervalUnit.TILL_BILL_DATE == renewalIntervalUnit || RenewalIntervalUnit.MID_NIGHT == renewalIntervalUnit) {
            billingCycleResetTime = new Timestamp(copyOfNonMonetaryBalance.getBillingCycleResetTime() + 1);
        }

        long balanceExpiry = RenewalIntervalUtility
                .calculateExpiryTime(renewalIntervalValue, renewalIntervalUnit, new Timestamp(packageExpiryTime), billingCycleResetTime, billingDay).getTime();

        QuotaProfile quotaProfile = policyRepository.getPkgDataById(copyOfNonMonetaryBalance.getPackageId()).getQuotaProfile(copyOfNonMonetaryBalance.getQuotaProfileId());
        Map<String, QuotaProfileDetail> serviceWiseQuotaProfileDetails = quotaProfile.getServiceWiseQuotaProfileDetails(copyOfNonMonetaryBalance.getLevel());

        for (QuotaProfileDetail quotaProfileDetail : serviceWiseQuotaProfileDetails.values()) {
            RncProfileDetail rncProfileDetail = (RncProfileDetail) quotaProfileDetail;
            if (Objects.equals(rncProfileDetail.getDataServiceType().getServiceIdentifier(), copyOfNonMonetaryBalance.getServiceId())) {
                copyOfNonMonetaryBalance.setBillingCycleTotalVolume(rncProfileDetail.getUnitType().getVolumeInBytes(rncProfileDetail.getBillingCycleAllowedUsage()));
                copyOfNonMonetaryBalance.setBillingCycleTime(rncProfileDetail.getBillingCycleAllowedUsage().getTimeInSeconds());
            }
        }

        if (quotaProfile.getProration()) {
            copyOfNonMonetaryBalance.setBillingCycleTotalVolume(RenewalIntervalUtility.getProratedQuota(copyOfNonMonetaryBalance.getBillingCycleTotalVolume(), new Timestamp(copyOfNonMonetaryBalance.getBillingCycleResetTime()), new Timestamp(balanceExpiry), renewalIntervalUnit, renewalIntervalValue));
            copyOfNonMonetaryBalance.setBillingCycleTime(RenewalIntervalUtility.getProratedQuota(copyOfNonMonetaryBalance.getBillingCycleTime(), new Timestamp(copyOfNonMonetaryBalance.getBillingCycleResetTime()), new Timestamp(balanceExpiry), renewalIntervalUnit, renewalIntervalValue));
        }

        ResetBalanceStatus status;
        if (balanceExpiry >= packageExpiryTime) {
            status = ResetBalanceStatus.NOT_RESET;
        } else {
            status = ResetBalanceStatus.RESET;
        }

        CarryForwardStatus carryForwardStatus = CarryForwardStatus.NOT_CARRY_FORWARD;
        if (quotaProfile.getCarryForward() && copyOfNonMonetaryBalance.getLevel() <= quotaProfile.getBalanceLevel().getFupLevel() && balanceExpiry < packageExpiryTime &&
                ((nonMonetoryBalance.getBillingCycleTotalVolume() == CommonConstants.QUOTA_UNDEFINED || nonMonetoryBalance.getBillingCycleTotalVolume() == CommonConstants.QUOTA_UNLIMITED) == false ||
                        (nonMonetoryBalance.getBillingCycleTime() == CommonConstants.QUOTA_UNDEFINED || nonMonetoryBalance.getBillingCycleTime() == CommonConstants.QUOTA_UNLIMITED) == false)
                ) {
            carryForwardStatus = CarryForwardStatus.CARRY_FORWARD;
        }

        String nonMonetaryBalanceId = UUID.randomUUID().toString();
        long startTime = copyOfNonMonetaryBalance.getBillingCycleResetTime() + 1;

        return new NonMonetoryBalance(
                nonMonetaryBalanceId,
                copyOfNonMonetaryBalance.getServiceId(),
                copyOfNonMonetaryBalance.getPackageId(),
                copyOfNonMonetaryBalance.getQuotaProfileId(),
                copyOfNonMonetaryBalance.getRatingGroupId(),
                copyOfNonMonetaryBalance.getLevel(),
                copyOfNonMonetaryBalance.getSubscriberIdentity(),
                copyOfNonMonetaryBalance.getSubscriptionId(),
                copyOfNonMonetaryBalance.getBillingCycleTotalVolume(),
                copyOfNonMonetaryBalance.getBillingCycleTotalVolume(),
                copyOfNonMonetaryBalance.getBillingCycleTime(),
                copyOfNonMonetaryBalance.getBillingCycleTime(),
                ZERO_VALUE,
                ZERO_VALUE,
                ZERO_VALUE,
                ZERO_VALUE,
                ZERO_VALUE,
                ZERO_VALUE,
                ResetTimeUtility.calculateDailyResetTime(startTime),
                ResetTimeUtility.calculateWeeklyResetTime(startTime),
                balanceExpiry,
                status,
                ZERO_VALUE,
                ZERO_VALUE,
                carryForwardStatus,
                copyOfNonMonetaryBalance.getRenewalInterval(),
                copyOfNonMonetaryBalance.getProductOfferId(),
                startTime);
    }
}
