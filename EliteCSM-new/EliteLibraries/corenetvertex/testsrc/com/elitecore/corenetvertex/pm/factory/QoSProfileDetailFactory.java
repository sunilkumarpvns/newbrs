package com.elitecore.corenetvertex.pm.factory;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.IPCanQoSFactory;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.UMBaseQoSProfileDetailImpl;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

public class QoSProfileDetailFactory {

    protected QuotaProfileDetialToPCCRuleBuilder internalBuilder;
    protected String name = "qosName1";
    protected IPCANQoS ipcanQoS;
    protected String reason;
    protected List<PCCRule> pccRules;
    protected int fupLevel;
    protected String packageName = "packageName1";
    protected int orderNumber = 0;

    public QoSProfileDetailFactory() {
        ipcanQoS = IPCanQoSFactory.randomQoS();
    }

    public static QoSProfileDetailFactory createQoSProfile() {
        return new QoSProfileDetailFactory();
    }

    public QuotaProfileDetialToPCCRuleBuilder hasQuotaProfileDetail(List<QuotaProfileDetail> quotaProfileDetail) {
        internalBuilder = new QuotaProfileDetialToPCCRuleBuilder(quotaProfileDetail, this);
        return internalBuilder;
    }

    public QuotaProfileDetialToPCCRuleBuilder hasQuotaProfileDetail(QuotaProfileDetail quotaProfileDetail) {
        internalBuilder = new QuotaProfileDetialToPCCRuleBuilder(Arrays.asList(quotaProfileDetail), this);
        return internalBuilder;
    }

    public QoSProfileDetailFactory withFupLevel(int fupLevel) {
        this.fupLevel = fupLevel;
        return this;
    }

    public QoSProfileDetailFactory withRejectAction(String reason) {
        this.reason = reason;
        return this;
    }

    public QoSProfileDetailFactory withOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public QoSProfileDetailFactory withOrderNumberHigherThan(QoSProfileDetail qosProfileDetail) {
        this.orderNumber = qosProfileDetail.getOrderNo()+1;
        return this;
    }

    public QoSProfileDetailFactory withSessionQoSEqualTo(IPCANQoS ipcanQoS) {
        this.ipcanQoS = IPCanQoSFactory.createSessionQoSHasEqualQoSTo(ipcanQoS);
        return this;
    }

    public QoSProfileDetailFactory withSessionQoSHigherThan(IPCANQoS higherBound) {
        this.ipcanQoS = IPCanQoSFactory.createSessionQoSHasHigherQoSThan(higherBound);
        return this;
    }

    public QoSProfileDetailFactory withSessionQoSLowerThan(IPCANQoS lowerBound) {
        this.ipcanQoS = IPCanQoSFactory.createSessionQoSHasLowerQoSThan(lowerBound);
        return this;
    }

    public QoSProfileDetailFactory withRandomSessionQoS() {
        this.ipcanQoS = IPCanQoSFactory.randomQoS();
        return this;
    }

    public QoSProfileDetailFactory withPCCRules(List<PCCRule> pccRules) {
        this.pccRules = pccRules;
        return this;
    }

    public QoSProfileDetailFactory withSameFUPLevel(QoSProfileDetail qosProfileDetail) {
        this.orderNumber = qosProfileDetail.getOrderNo();
        this.fupLevel = qosProfileDetail.getFUPLevel();
        return this;
    }

    public QoSProfileDetailFactory withHigherFUPLevel(QoSProfileDetail qosProfileDetail) {
        this.orderNumber = qosProfileDetail.getOrderNo();
        this.fupLevel = qosProfileDetail.getFUPLevel()+1;
        return this;
    }

    public QoSProfileDetailFactory withLowerFUPLevel(QoSProfileDetail qosProfileDetail) {
        this.orderNumber = qosProfileDetail.getOrderNo();
        this.fupLevel = qosProfileDetail.getFUPLevel()-1;
        return this;
    }


    public static class QuotaProfileDetialToPCCRuleBuilder {


        private List<QuotaProfileDetail> quotaProfileDetails;
        private List<PCCRule> pccRules;

        private int serviceIdentifier;
        private QoSProfileDetailFactory qosProfileDetailBuilder;

        private QuotaProfileDetialToPCCRuleBuilder(List<QuotaProfileDetail> quotaProfileDetail,QoSProfileDetailFactory qosProfileDetailBuilder) {
            this.quotaProfileDetails = quotaProfileDetail;
            this.qosProfileDetailBuilder = qosProfileDetailBuilder;
        }

        public QoSProfileDetailFactory withPCCRules(List<PCCRule> pccRules) {
            this.pccRules = pccRules;
            return qosProfileDetailBuilder;
        }

        public QoSProfileDetailFactory forEachServicesHasPCCRule() {

            if(Collectionz.isNullOrEmpty(quotaProfileDetails)) {
                return qosProfileDetailBuilder;
            }

            RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);

            List<PCCRule> pccRules = new ArrayList<PCCRule>();

            for(QuotaProfileDetail quotaProfileDetail : quotaProfileDetails){

                String serviceId = quotaProfileDetail.getServiceId();
                DataServiceType dataServiceType = new DataServiceType(serviceId,
                        serviceId,
                        serviceIdentifier++,
                        Arrays.asList("PERMIT IP IN"),
                        Arrays.asList(ratingGroup));


                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().
                        withServiceType(dataServiceType).
                        withRatingGroup(dataServiceType.getRatingGroupList().get(0)).
                        withServiceDataFlows(dataServiceType.getServiceDataFlowList()).
                        build();

                pccRules.add(pccRule);
            }

            this.pccRules = pccRules;
            return qosProfileDetailBuilder;
        }


    }

    public QoSProfileDetail build() throws RuntimeException {


        QoSProfileAction action = QoSProfileAction.ACCEPT;

        if(reason != null){
            action = QoSProfileAction.REJECT;
        }


        if(ipcanQoS == null && action != QoSProfileAction.REJECT) {
            ipcanQoS = IPCanQoSFactory.randomQoS();
        }


        try {

            return new UMBaseQoSProfileDetailImpl(name, packageName, action, reason, fupLevel,
                    null,
                    null,
                    false,
                    ipcanQoS,
                    pccRules,
                    false,
                    null,
                    orderNumber,
                    false,
                    null,
                    null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
