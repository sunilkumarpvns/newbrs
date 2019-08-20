package com.elitecore.corenetvertex.spr.util;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;

import java.sql.Timestamp;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.nonNull;

public class SubscriptionPackagePredicates {

    private static final String MODULE = "SUBSCRIPTION-PREDICATE";
    public static final Predicate<Subscription> STATE_PREDICATE = new SubscriptionStatePredicate();


    public static class SubscriptionPackageIdPredicate implements Predicate<Subscription> {
        private PolicyRepository policyRepository;

        public SubscriptionPackageIdPredicate(PolicyRepository policyRepository) {
            this.policyRepository = policyRepository;
        }

        @Override
        public boolean test(Subscription subscription) {
            SubscriptionPackage subscribedPackage = policyRepository.getAddOnById(subscription.getPackageId());
            if (nonNull(subscribedPackage)) {
                return true;
            }

            QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(subscription.getPackageId());
            if (nonNull(quotaTopUp)) {
                return true;
            }

            BoDPackage bodPackage = policyRepository.getBoDPackage().byId(subscription.getPackageId());
            if (nonNull(bodPackage)) {
                return true;
            }

            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Skipping subscription: " + subscription.getId()
                        + ", subscriberId: " + subscription.getSubscriberIdentity()
                        + ". Reason: Addon or Quota TopUp not found for id: " + subscription.getPackageId());
            }
            return false;

        }
    }


    public static class SubscriptionEndTimePredicate implements Predicate<Subscription> {


        private TimeSource timeSource;

        public SubscriptionEndTimePredicate(TimeSource timeSource) {
            this.timeSource = timeSource;
        }

        @Override
        public boolean test(Subscription subscription) {
            Timestamp endTime = subscription.getEndTime();
            SubscriptionState state = subscription.getStatus();
            if (endTime == null) {
                if (SubscriptionState.APPROVAL_PENDING == state) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Approval pending for subscription:" + subscription.getId()
                                + ", id:" + subscription.getPackageId() + ", subscriberId:" + subscription.getSubscriberIdentity());
                    }
                } else if (SubscriptionState.REJECTED == state) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Subscription:" + subscription.getId() + " rejected for id: "
                                + subscription.getPackageId() + ", subscriberId: " + subscription.getSubscriberIdentity());
                    }
                } else {
                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "End Time is not provided for subscription:" + subscription.getId()
                                + ", addon:" + subscription.getPackageId() + ", subscriberId: " + subscription.getSubscriberIdentity());
                    }

                }
                return false;
            }

            if (endTime.getTime() <= timeSource.currentTimeInMillis()) {
                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Skipping subscription:" + subscription.getId()
                            + ", id:" + subscription.getPackageId() + " for subscriberId:" + subscription.getSubscriberIdentity()
                            + ". Reason:  subscription is expired on Endtime:" + endTime + " ");
                }
                return false;
            }
            return true;
        }
    }

    public static class SubscriptionStatePredicate implements Predicate<Subscription> {

        @Override
        public boolean test(Subscription subscription) {
            if (subscription.getStatus() == null) {
                getLogger().error(MODULE, "Error while getting active subscriptions:" + subscription.getId()
                        + ", id:" + subscription.getPackageId() + ", subscriberId:" + subscription.getSubscriberIdentity()
                        + ". Reason: Illegal status determined, state: " + subscription.getStatus());
                return false;
            }
            if (SubscriptionState.UNSUBSCRIBED == subscription.getStatus()) {
                if (getLogger().isLogLevel(LogLevel.INFO))
                    getLogger().info(MODULE, "Skipping subscription:" + subscription.getId()
                            + ", id:" + subscription.getPackageId() + " for subscriberId:" + subscription.getSubscriberIdentity()
                            + ". Reason: Subscription is Un subscribed");
                return false;
            }
            return true;
        }
    }
}