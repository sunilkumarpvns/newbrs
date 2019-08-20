package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.pm.BasePolicyContext;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class GyPolicyContextImpl extends BasePolicyContext{
    private static final String MODULE = "GY-POLICY-CONTEXT";

    private static final Random RANDOM = new Random();
    private PCRFRequest request;
    private PCRFResponse response;
    private QuotaReservation reservations;
    private int revalidationTimeDelta;
    private boolean isPreTopUpChecked = false;
    private String productOfferId;

    public GyPolicyContextImpl(PCRFRequest pcrfRequest,
                               PCRFResponse pcrfResponse,
                               BasePackage basePackage,
                               ExecutionContext executionContext,
                               PolicyRepository policyRepository, int revalidationTimeDelta, String productOfferId) {
        super(pcrfResponse, pcrfRequest, basePackage, executionContext, policyRepository);

        this.request = pcrfRequest;
        this.response = pcrfResponse;
        this.revalidationTimeDelta = revalidationTimeDelta;
        this.reservations = new QuotaReservation();
        this.productOfferId = productOfferId;
    }



    @Override
    public boolean process(QoSProfile qosProfile, UserPackage subscriptionPackage, Subscription subscription) {

        if (Objects.isNull(qosProfile)) {
            return false;
        }

        if (QuotaProfileType.RnC_BASED != subscriptionPackage.getQuotaProfileType()) {
            return false;
        }

        SPRInfo sprInfo = request.getSPRInfo();
        if (qosProfile.getDataRateCard() != null ) {

            if((PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val.equals(response.getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val))
                    && sprInfo.getPaygInternationalDataRoaming() == false)){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "QOS Profile " + qosProfile.getName() + " processing skipped. Reason: Subscriber is roaming internationally and has opted for not applying PAYG International Data Roaming");
                }
                return false;
            }

            return ((com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard)qosProfile.getDataRateCard()).applyReservation(this,
                    subscriptionPackage.getId(), subscription, reservations);

        } else {

            /**
             * FIXME: Please take care below handling of NPE when deliver user story NETVERTEX-2546
             * START - NETVERTEX-3102
             */
            try {
                SubscriptionNonMonitoryBalance balance = null;
                if (subscription == null) {
                    balance = this.getCurrentBalance().getPackageBalance(qosProfile.getPackageId());
                } else {
                    balance = this.getCurrentBalance().getPackageBalance(subscription.getId());
                }

                if (balance == null) {
                    if(sprInfo.isUnknownUser()){
                        balance = executionContext.getDDFTable().addDataRnCBalance(sprInfo.getSubscriberIdentity(),subscription,getPolicyRepository().getProductOffer().byName(sprInfo.getProductOffer()));
                        this.getCurrentBalance().addBalance(balance);
                    }else {
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, "Rejecting request. Reason: balance not found");
                        }
                        return false;
                    }
                }
            } catch (OperationFailedException e) {
                LogManager.getLogger().trace(e);
            }
            /**
             * END - NETVERTEX-3102
             */

            return ((com.elitecore.netvertex.pm.QuotaProfile)qosProfile.getQuotaProfile()).apply(this, subscriptionPackage, subscription, reservations);
        }
    }

    @Override
    public void preTopUpChecked() {
        isPreTopUpChecked = true;
    }

    @Override
    public boolean isPreTopUpChecked() {
        return isPreTopUpChecked;
    }

    public void setGrantedMSCCs(){

        List<MSCC> requestedMSCC = request.getReportedMSCCs();

        if (Collectionz.isNullOrEmpty(response.getGrantedMSCCs())) {
            response.setGrantedMSCCs(new ArrayList<>());
        }

        if(Collectionz.isNullOrEmpty(requestedMSCC)){
            for(Map.Entry<Long, MSCC> entry : reservations.get()){

                MSCC mscc = entry.getValue();
                if(revalidationTimeDelta > 0){
                    mscc.setValidityTime(mscc.getValidityTime() + RANDOM.nextInt(revalidationTimeDelta));
                }
                response.getGrantedMSCCs().add(mscc);
            }

            response.setQuotaReservation(reservations);
            return;
        }

        for(MSCC mscc : requestedMSCC){

            MSCC grantedMSCC = reservations.get(mscc.getRatingGroup());

            if(Objects.isNull(grantedMSCC)){
                grantedMSCC = new MSCC();
                grantedMSCC.setRatingGroup(mscc.getRatingGroup());
                if(ReportingReason.FINAL == mscc.getReportingReason()){
                    grantedMSCC.setResultCode(ResultCode.SUCCESS);
                }else {
                    grantedMSCC.setValidityTime(0);
                    grantedMSCC.setResultCode(ResultCode.BALANCE_DOES_NOT_EXISTS);
                }
            }else{
                if(revalidationTimeDelta > 0){
                    grantedMSCC.setValidityTime(grantedMSCC.getValidityTime() + RANDOM.nextInt(revalidationTimeDelta));
                }

                grantedMSCC.setResultCode(ResultCode.SUCCESS);
            }

            grantedMSCC.setServiceIdentifiers(mscc.getServiceIdentifiers());
            response.getGrantedMSCCs().add(grantedMSCC);

            if(ReportingReason.FINAL != mscc.getReportingReason()) {
                if(Objects.isNull(response.getQuotaReservation())) {
                    response.setQuotaReservation(new QuotaReservation());
                }

                response.getQuotaReservation().put(grantedMSCC);
            }
        }
    }

    public QuotaReservation getReservations() {
        return reservations;
    }

    @Override
    public String getProductOfferId(){
        return productOfferId;
    }
}
