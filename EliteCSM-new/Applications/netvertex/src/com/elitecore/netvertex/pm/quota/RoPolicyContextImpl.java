
package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.BasePolicyContext;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoPolicyContextImpl extends BasePolicyContext{

    private PCRFRequest request;
    private PCRFResponse response;
    private QuotaReservation reservations;
    private boolean isPreTopUpChecked = false;
    private RnCPackage rncPackage;
    private String productOfferId;

    public RoPolicyContextImpl(PCRFRequest pcrfRequest,
                               PCRFResponse pcrfResponse,
                               RnCPackage rncPackage,
                               ExecutionContext executionContext,
                               PolicyRepository policyRepository, String productOfferId) {
        super(pcrfResponse, pcrfRequest, null, executionContext, policyRepository);

        this.request = pcrfRequest;
        this.response = pcrfResponse;
        this.reservations = new QuotaReservation();
        this.rncPackage = rncPackage;
        this.productOfferId = productOfferId;
    }

    @Override
    public boolean process(QoSProfile qosProfile, UserPackage subscriptionPackage, Subscription subscription) {
    	return false;
    }


    @Override
    public LinkedHashMap<String, Subscription> getSubscriptions() throws OperationFailedException {
        LinkedHashMap<String, Subscription> roSubscriptions = new LinkedHashMap<>();

        executionContext.getSubscriptions().values().stream().filter(subscription -> SubscriptionType.RO_ADDON.equals(subscription.getType()))
                .forEach(subscription -> roSubscriptions.put(subscription.getId(),subscription) );

        return roSubscriptions;
    }


    @Override
    public void preTopUpChecked() {
        isPreTopUpChecked = true;
    }

    @Override
    public boolean isPreTopUpChecked() {
        return isPreTopUpChecked;
    }

    public QuotaReservation getGrantedAllMSCC(){

        List<MSCC> requestedMSCC = request.getReportedMSCCs();

        if (Collectionz.isNullOrEmpty(response.getGrantedMSCCs())) {
            response.setGrantedMSCCs(new ArrayList<>());
        }

        if(Collectionz.isNullOrEmpty(requestedMSCC)){
            for(Map.Entry<Long, MSCC> entry : reservations.get()){

                MSCC mscc = entry.getValue();
                response.getGrantedMSCCs().add(mscc);
            }
            return reservations;
        }

        for(MSCC mscc : requestedMSCC){

            MSCC grantedMSCC = reservations.get(mscc.getRatingGroup());

            if(Objects.isNull(grantedMSCC)){
                grantedMSCC = new MSCC();
                grantedMSCC.setRatingGroup(mscc.getRatingGroup());
                if(ReportingReason.FINAL == mscc.getReportingReason()){
                    grantedMSCC.setResultCode(ResultCode.SUCCESS);
                }else {
                    grantedMSCC.setResultCode(ResultCode.BALANCE_DOES_NOT_EXISTS);
                }
                reservations.put(grantedMSCC);
            }else{
                grantedMSCC.setResultCode(ResultCode.SUCCESS);
            }

            grantedMSCC.setServiceIdentifiers(mscc.getServiceIdentifiers());
            response.getGrantedMSCCs().add(grantedMSCC);
        }

        return reservations;
    }

    public QuotaReservation getReservations() {
        return reservations;
    }
    public RnCPackage getRnCPackage() {
        return rncPackage;
    }
    @Override
    public String getProductOfferId(){
        return productOfferId;
    }
}
