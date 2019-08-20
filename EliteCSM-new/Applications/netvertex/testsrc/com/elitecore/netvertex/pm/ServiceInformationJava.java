package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilderJava;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import groovy.transform.TypeChecked;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@TypeChecked
class ServiceInformationJava {


    public String serviceName;
    public PCCRule pccRule;
    public RatingGroup ratingGroup;
    public DataServiceType serviceType;
    public BalanceInformation balanceInformation;
    public String pkgId;
    public  RnCQuotaProfileDetail rncQuota;
    public NonMonetoryBalance serviceBalance;
    public String quotaProfileId;
    public double rate;


    public ServiceInformationJava(String quotaProfileName, String serviceName, String packageId, double rate) {
        this.serviceName = serviceName;
        this.quotaProfileId = quotaProfileName + "Id";
        this.pkgId = packageId;
        this.rate = rate;

        if(serviceName.equals(CommonConstants.ALL_SERVICE_ID)) {
            ratingGroup = new RatingGroup("0", serviceName, "", 0);
            serviceType = new DataServiceType(serviceName, serviceName, 5, Arrays.asList(""), Arrays.asList(ratingGroup));
        } else {
            ratingGroup = new RatingGroup(serviceName + "RgId", serviceName, "", new Random().nextInt());
            serviceType = new DataServiceType(serviceName + "Id", serviceName, 5, Arrays.asList(""), Arrays.asList(ratingGroup));
        }
    }

    public static ServiceInformationJava service(String quotaProfileName, String serviceName, String packageId, double rate) {
        ServiceInformationJava serviceInformation = new ServiceInformationJava(quotaProfileName, serviceName, packageId, rate);
        return serviceInformation;
    }



    public ServiceInformationJava pcc() {
        pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(serviceType).withRatingGroup(ratingGroup).build();
        return this;
    }

    public BalanceInformation randomQuota() {
        rncQuota = new RnCQuotaProfileFactory(UUID.randomUUID().toString(), "name:" + UUID.randomUUID().toString())
                .dataServiceType(serviceType)
                .ratingGroup(ratingGroup)
                .randomBalanceWithRate().create();

        balanceInformation = new BalanceInformation();
        return balanceInformation;
    }

    private class BalanceInformation {

        public BalanceInformation outOfThat() {
            return this;
        }

        public ServiceInformationJava outOfThat(NonMonitoryBalanceBuilderJava balance) {
            balance.info().packageId(pkgId);
            balance.info().leftShift(rncQuota);
            serviceBalance = balance.build();
            return ServiceInformationJava.this;
        }
    }
}
