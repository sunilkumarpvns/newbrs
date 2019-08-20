package com.elitecore.netvertex.pm;

import com.elitecore.acesstime.TimePeriod;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.Lazy;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.AddOnSubscriptionComparator;
import com.elitecore.netvertex.service.pcrf.util.PCRFValueProvider;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeSet;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class BasePolicyContext implements PolicyContext {
    private static final String MODULE = "POLICY-CNTX";
    protected final ExecutionContext executionContext;
    private PolicyRepository policyRepository;
    protected final PCRFRequest pcrfRequest;
    protected final PCRFResponse pcrfResponse;
    protected BasePackage basePackage;
    @Nullable
    private TimePeriod timePeriod;
    @Nullable
    @Lazy
    private PCRFValueProvider valueProvider;
    private long timeOutInSec;
    private boolean haltProcess;
    @Nullable
    private Timestamp revalidationTime;
    private IndentingPrintWriter policyHuntingTrace;
    private StringWriter stringWriter;
    private Collection<Subscription> preTopUpSubscriptions;
    private Collection<Subscription> spareTopUpSubscriptions;
    private Collection<Subscription> boDPkgSubscriptions;

    public BasePolicyContext(PCRFResponse pcrfResponse,
                             PCRFRequest pcrfRequest,
                             BasePackage basePackage,
                             ExecutionContext executionContext, PolicyRepository policyRepository) {
        this.pcrfResponse = pcrfResponse;
        this.pcrfRequest = pcrfRequest;
        this.basePackage = basePackage;
        this.executionContext = executionContext;
        this.policyRepository = policyRepository;
    }

    @Override
    public PolicyRepository getPolicyRepository() {
        return policyRepository;
    }

    @Override
    public PCRFValueProvider getValueProvider() {
        if (valueProvider == null) {
            valueProvider = new PCRFValueProvider(pcrfRequest, pcrfResponse);
        }

        return valueProvider;

    }

    @Override
    public boolean haltProcess() {
        return this.haltProcess;
    }

    @Override
    public void resetHaltPrecess() {
        this.haltProcess = false;

    }

    @Override
    public void setHaltPrecess() {
        this.haltProcess = true;
    }

    @Override
    public Calendar getCurrentTime() {
        return executionContext.getCurrentTime();
    }

    @Override
    public TimePeriod getCurrentTimePeriod() {
        if (this.timePeriod == null) {
            Calendar calendar = executionContext.getCurrentTime();
            timePeriod = new TimePeriod(calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
            );
        }

        return this.timePeriod;
    }

    @Override
    public SPRInfo getSPInfo() {
        return pcrfRequest.getSPRInfo();
    }

    @Override
    public void setTimeout(long timeOutInSec) {
        if (timeOutInSec > 0 && (this.timeOutInSec <= 0 || this.timeOutInSec > timeOutInSec)) {
            this.timeOutInSec = timeOutInSec;
        }
    }

    @Override
    public void setRevalidationTime(Timestamp newRevalidationTime) {
        if (newRevalidationTime != null && (this.revalidationTime == null || this.revalidationTime.after(newRevalidationTime))) {
            this.revalidationTime = newRevalidationTime;
        } else {
            if (newRevalidationTime == null) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Skiping revalidation time set. Reason: Revalidation time is null");
                }
            } else {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Skiping revalidation time set. Reason: Revalidation time(" + newRevalidationTime + ") is greater than existing time(" + this.revalidationTime + ")");
                }
            }
        }
    }

    @Override
    public Timestamp getRevalidationTime() {
        return revalidationTime;
    }

    @Override
    public long getTimeout() {
        return timeOutInSec;
    }

    @Override
    public java.util.Date getSessionStartTime() {
        return pcrfRequest.getSessionStartTime();
    }

    @Override
    public BasePackage getBasePackage() {
        return basePackage;
    }

    @Override
    public ServiceUsage getCurrentUsage() throws OperationFailedException {
        return executionContext.getCurrentUsage();
    }

    @Override
    public SubscriberNonMonitoryBalance getCurrentBalance() throws OperationFailedException {
        return executionContext.getCurrentNonMonetoryBalance();
    }

    @Override
    public SubscriberRnCNonMonetaryBalance getCurrentRnCBalance() throws OperationFailedException {
        return executionContext.getCurrentRnCNonMonetaryBalance();
    }

    @Override
    public SubscriberMonetaryBalance getCurrentMonetaryBalance() throws OperationFailedException {
        return executionContext.getCurrentMonetaryBalance();
    }

    @Override
    public LinkedHashMap<String, Subscription> getSubscriptions() throws OperationFailedException {
        return executionContext.getSubscriptions();
    }

    @Override
    public Collection<Subscription> getPreTopUpSubscriptions() {

        if (preTopUpSubscriptions != null) {
            return preTopUpSubscriptions;
        }

        LinkedHashMap<String, Subscription> subscriptions = getSubscriptionSilent();

        if (subscriptions == null || subscriptions.isEmpty()) {
            return Collections.emptyList();
        }

        filterSubscription(subscriptions);
        return preTopUpSubscriptions;
    }

    @Override
    public Collection<Subscription> getBoDPkgSubscriptions() {

        if (Objects.nonNull(boDPkgSubscriptions)) {
            return boDPkgSubscriptions;
        }

        LinkedHashMap<String, Subscription> subscriptions = getSubscriptionSilent();

        if (subscriptions == null || subscriptions.isEmpty()) {
            return Collections.emptyList();
        }

        filterBoDSubscription(subscriptions);
        return boDPkgSubscriptions;
    }

    @Override
    public Collection<Subscription> getSpareTopUpSubscriptions() {
        if (spareTopUpSubscriptions != null) {
            return spareTopUpSubscriptions;
        }

        LinkedHashMap<String, Subscription> subscriptions = getSubscriptionSilent();

        if (subscriptions == null || subscriptions.isEmpty()) {
            return Collections.emptyList();
        }

        filterSubscription(subscriptions);
        return spareTopUpSubscriptions;
    }

    private LinkedHashMap<String, Subscription> getSubscriptionSilent() {
        LinkedHashMap<String, Subscription> subscriptions = null;
        try {
            subscriptions = getSubscriptions();
        } catch (OperationFailedException e) {
            getLogger().warn(MODULE, "Error while fetching subscription for subscriber id: "
                    + pcrfRequest.getSPRInfo().getSubscriberIdentity() + ". Reason: " + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
        }
        return subscriptions;
    }

    private void filterSubscription(LinkedHashMap<String, Subscription> subscriptions) {

        this.preTopUpSubscriptions = new TreeSet<>(AddOnSubscriptionComparator.instance().reversed());
        this.spareTopUpSubscriptions = new TreeSet<>(AddOnSubscriptionComparator.instance().reversed());

        for (Subscription subscription : subscriptions.values()) {

            QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(subscription.getPackageId());

            if (quotaTopUp == null) {
                getLogger().warn(MODULE, "Skipping subscription( " + subscription.getId()
                        + "). Addon(id: " + subscription.getPackageId() + ") not found for subscriber ID: " + subscription.getSubscriberIdentity());
                continue;
            }

            if (quotaTopUp.getStatus() == PolicyStatus.FAILURE) {
                getLogger().warn(MODULE, "Skip subscription(id: " + subscription.getId() + ") for subscriber ID: " + subscription.getSubscriberIdentity()
                        + ". Reason: Subscription package(name:" + quotaTopUp.getName() + ") has status FAILURE. Reason: " + quotaTopUp.getFailReason());
                continue;
            }

            if (TopUpType.TOP_UP == quotaTopUp.getPackageType()) {
                preTopUpSubscriptions.add(subscription);
                continue;
            }

            if (TopUpType.SPARE_TOP_UP == quotaTopUp.getPackageType()) {
                spareTopUpSubscriptions.add(subscription);
            }
        }
    }

    private void filterBoDSubscription(LinkedHashMap<String, Subscription> subscriptions) {

        this.boDPkgSubscriptions= new TreeSet<>(AddOnSubscriptionComparator.instance().reversed());

        for (Subscription subscription : subscriptions.values()) {

            BoDPackage boDPackage = policyRepository.getBoDPackage().byId(subscription.getPackageId());

            if (Objects.isNull(boDPackage)) {
                getLogger().warn(MODULE, "Skipping subscription( " + subscription.getId()
                        + "). BoD Package (id: " + subscription.getPackageId() + ") not found for subscriber ID: " + subscription.getSubscriberIdentity());
                continue;
            }

            if (Objects.equals(PolicyStatus.FAILURE, boDPackage.getPolicyStatus())) {
                getLogger().warn(MODULE, "Skip subscription(id: " + subscription.getId() + ") for subscriber ID: " + subscription.getSubscriberIdentity()
                        + ". Reason: Subscription package(name:" + boDPackage.getName() + ") has status FAILURE. Reason: " + boDPackage.getFailReason());
                continue;
            }

            boDPkgSubscriptions.add(subscription);
        }
    }

    @Override
    public PCRFRequest getPCRFRequest() {
        return pcrfRequest;
    }

    @Override
    public PCRFResponse getPCRFResponse() {
        return pcrfResponse;
    }

    @Override
    public void setBasePackage(BasePackage basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public IndentingPrintWriter getTraceWriter() {
        if(policyHuntingTrace == null) {
            stringWriter = new StringWriter();
            policyHuntingTrace = new IndentingPrintWriter(new PrintWriter(stringWriter));
        }
        return policyHuntingTrace;
    }

    @Override
    public String getTrace() {
        if(policyHuntingTrace == null) {
            return "";
        }
        return stringWriter.toString();
    }

    @Override
    public void append(String trace) {
        if(policyHuntingTrace == null) {
            policyHuntingTrace = new IndentingPrintWriter(new PrintWriter(new StringWriter()));
        }

        policyHuntingTrace.println(trace);
    }
}
