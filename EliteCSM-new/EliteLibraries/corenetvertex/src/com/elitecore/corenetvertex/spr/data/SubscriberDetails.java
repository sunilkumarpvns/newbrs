package com.elitecore.corenetvertex.spr.data;

public class SubscriberDetails {

    SPRInfo sprInfo;
    Long creditLimit;

    public SubscriberDetails(SPRInfo sprInfo, Long creditLimit) {
        this.sprInfo = sprInfo;
        this.creditLimit = creditLimit;
    }

    public SPRInfo getSprInfo() {
        return sprInfo;
    }

    public void setSprInfo(SPRInfo sprInfo) {
        this.sprInfo = sprInfo;
    }

    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }
}
