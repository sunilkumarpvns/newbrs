package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.pm.pkg.PCCRule;

public class FinalQoSSelectionDataBuilder {

    private FinalQoSSelectionData finalQoSSelectionData;
    private String subscriptionOrPackageId;
    private QoSProfileDetailFactoryExt qoSProfileDetailFactory;
    private boolean isQoSDetailSet = false;

    public FinalQoSSelectionDataBuilder(PackageType packageType, String subscriptionOrPackageId) {
        this.finalQoSSelectionData = new FinalQoSSelectionData(packageType);
        this.subscriptionOrPackageId = subscriptionOrPackageId;
        this.qoSProfileDetailFactory = QoSProfileDetailFactoryExt.createQoSProfile();
    }

    public FinalQoSSelectionDataBuilder withQoSDetail(QoSProfileDetail qoSProfileDetail) {
        isQoSDetailSet = true;
        this.finalQoSSelectionData.setQosProfileDetail(qoSProfileDetail, subscriptionOrPackageId, null);
        return this;
    }

    public FinalQoSSelectionDataBuilder withRandomQoSDetail() {
        this.qoSProfileDetailFactory.withRandomSessionQoS();
        return this;
    }

    public void withPCCRule(PCCRule pccRule, String subscriptionIdOrPackageId, QoSProfile qoSProfile) {
        this.finalQoSSelectionData.add(pccRule, subscriptionIdOrPackageId, qoSProfile);
    }

    public FinalQoSSelectionDataBuilder withPCCRule(PCCRule pccRule, QoSProfile qoSProfile) {
        this.finalQoSSelectionData.add(pccRule, subscriptionOrPackageId, qoSProfile);
        return this;
    }


    public FinalQoSSelectionData build() {
        if (isQoSDetailSet == false) {
            finalQoSSelectionData.setQosProfileDetail((QoSProfileDetail) qoSProfileDetailFactory.build(), subscriptionOrPackageId, null);
        }
        return finalQoSSelectionData;
    }

    public FinalQoSSelectionDataBuilder withManageOrderHigherThan(FinalQoSSelectionData baseQoSData) {
        qoSProfileDetailFactory.withOrderNumberHigherThan(baseQoSData.getQosProfileDetail());
        return this;
    }

    public FinalQoSSelectionDataBuilder withSameFupLevelQoSDetail(FinalQoSSelectionData baseQoSData) {
        qoSProfileDetailFactory.withSameFUPLevel(baseQoSData.getQosProfileDetail());
        return this;
    }

    public FinalQoSSelectionDataBuilder withHigherFUPLevelQoSDetail(FinalQoSSelectionData baseQoSData) {
        qoSProfileDetailFactory.withHigherFUPLevel(baseQoSData.getQosProfileDetail());
        return this;
    }

    public FinalQoSSelectionDataBuilder withLowerFUPLevelQoS(FinalQoSSelectionData baseQoSData) {
        qoSProfileDetailFactory.withLowerFUPLevel(baseQoSData.getQosProfileDetail());
        return this;
    }

    public FinalQoSSelectionDataBuilder withQoSManageOrder(int orderNumber) {
        this.qoSProfileDetailFactory.withOrderNumber(orderNumber);
        return this;
    }

    public FinalQoSSelectionDataBuilder withQoSFupLevel(int fupLevel) {
        qoSProfileDetailFactory.withFupLevel(fupLevel);
        return this;
    }

    public FinalQoSSelectionDataBuilder withQoSFupLevelLowerThan(FinalQoSSelectionData finalQoSSelectionData) {
        qoSProfileDetailFactory.withOrderNumber(finalQoSSelectionData.getQosProfileDetail().getOrderNo());
        qoSProfileDetailFactory.withFupLevel(finalQoSSelectionData.getQosProfileDetail().getFUPLevel()-1);
        return this;
    }

    public FinalQoSSelectionDataBuilder withQoSLowerThan(FinalQoSSelectionData baseQoS) {
        qoSProfileDetailFactory.withSessionQoSLowerThan(baseQoS.getQosProfileDetail().getSessionQoS());
        return this;
    }

    public FinalQoSSelectionDataBuilder withQoSHigherThan(FinalQoSSelectionData baseQoS) {
        qoSProfileDetailFactory.withSessionQoSHigherThan(baseQoS.getQosProfileDetail().getSessionQoS());
        return this;
    }

    public FinalQoSSelectionDataBuilder withQoSEqualAs(FinalQoSSelectionData baseQoS) {
        qoSProfileDetailFactory.withSessionQoSEqualTo(baseQoS.getQosProfileDetail().getSessionQoS());
        return this;
    }
}
