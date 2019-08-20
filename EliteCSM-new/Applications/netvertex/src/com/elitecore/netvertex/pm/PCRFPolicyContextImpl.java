package com.elitecore.netvertex.pm;


import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nullable;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCRFPolicyContextImpl extends BasePolicyContext {

    @Nullable
    private QoSInformation finalQoSInformation;

    private QoSProcessor qoSProcessor;

    public PCRFPolicyContextImpl(
            PCRFRequest pcrfRequest,
            PCRFResponse pcrfResponse,
            BasePackage basePackage,
            ExecutionContext executionContext,
            QoSProcessor qoSProcessor,
            PolicyRepository policyRepository) {
        super(pcrfResponse, pcrfRequest, basePackage, executionContext, policyRepository);

        this.qoSProcessor = qoSProcessor;
    }

    @Override
    public void preTopUpChecked() {
        // NOT USED IN GX FLOW
    }

    @Override
    public boolean isPreTopUpChecked() {
        return false;
    }

    @Override
    public String getProductOfferId() {
        return null;
    }

    @Override
    public boolean process(QoSProfile qoSProfile, UserPackage userPackage, Subscription subscription) {
        if (subscription == null && userPackage.getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
            SPRInfo sprInfo = getSPInfo();
            if (sprInfo.isUnknownUser()) {
                try {
                    SubscriptionNonMonitoryBalance balance = this.getCurrentBalance().getPackageBalance(userPackage.getId());
                    if (balance == null) {
                        balance = executionContext.getDDFTable().addDataRnCBalance(sprInfo.getSubscriberIdentity(), subscription, getPolicyRepository().getProductOffer().byName(sprInfo.getProductOffer()));
                        this.getCurrentBalance().addBalance(balance);
                    }
                } catch (OperationFailedException e) {
                    LogManager.getLogger().trace(e);
                    return false;
                }
            }
        }
        return qoSProcessor.process(qoSProfile, this, userPackage, subscription);
    }

}
