package com.elitecore.netvertex.pm

import com.elitecore.corenetvertex.constants.CommonConstants
import com.elitecore.corenetvertex.pm.pkg.DataServiceType
import com.elitecore.corenetvertex.pm.pkg.PCCRule
import com.elitecore.corenetvertex.pm.pkg.RatingGroup
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance
import com.elitecore.corenetvertex.spr.NonMonetoryBalance
import com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail
import groovy.transform.TypeChecked

@TypeChecked
class ServiceInformation {


    public String serviceName
    public PCCRule pccRule;
    public RatingGroup ratingGroup
    public DataServiceType serviceType;
    @Delegate public BalanceInformation balanceInformation;
    public String pkgId;
    public  RnCQuotaProfileDetail rncQuota;
    public  NonMonetoryBalance serviceBalance;
    public String quotaProfileId;
    public double rate;


    public ServiceInformation(String quotaProfileName, String serviceName, String packageId, double rate) {
        this(quotaProfileName,quotaProfileName + "Id",serviceName,packageId,rate);
    }

    public ServiceInformation(String quotaProfileName,String quotaProfileId, String serviceName, String packageId, double rate) {
        this.serviceName = serviceName
        this.quotaProfileId = quotaProfileId;
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

    public static ServiceInformation service(String quotaProfileName, String serviceName, String packageId, double rate, @DelegatesTo(ServiceInformation)Closure closure) {
        ServiceInformation serviceInformation = new ServiceInformation(quotaProfileName, serviceName, packageId, rate);
        def code = closure.rehydrate(serviceInformation, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY;
        code()
        return serviceInformation
    }


    public static ServiceInformation service(String quotaProfileName,String quotaProfileId, String serviceName, String packageId, double rate, @DelegatesTo(ServiceInformation)Closure closure) {
        ServiceInformation serviceInformation = new ServiceInformation(quotaProfileName,quotaProfileId, serviceName, packageId, rate);
        def code = closure.rehydrate(serviceInformation, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY;
        code()
        return serviceInformation
    }



    public ServiceInformation pcc() {
        pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceType(serviceType).withRatingGroup(ratingGroup).build();
        return this;
    }

    public BalanceInformation randomQuota() {
        rncQuota = QuotaProfileDetailFactory.randomBalanceWithRate(name:quotaProfileId, dataservice: serviceType, service:serviceType.dataServiceTypeID, rg:ratingGroup, rate)
        balanceInformation = new BalanceInformation();
        return balanceInformation;
    }

    private class BalanceInformation {



        public BalanceInformation outOfThat() {
            return this;
        }

        public ServiceInformation outOfThat(NonMonitoryBalanceBuilder balance) {
            balance.info().fromDetail(rncQuota).setPackageId(pkgId);
            serviceBalance = balance.build()
            return ServiceInformation.this;

        }


    }

    public TotalBalance getTotalBalance(PolicyContext policyContext){
        return rncQuota.getTotalBalance(serviceBalance, Balance.ZERO, policyContext.getCurrentTime())
    }

}
