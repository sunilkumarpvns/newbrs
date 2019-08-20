package com.elitecore.nvsmx.ws.subscription.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateCreditLimitRestRequest {
    private String subscriberId;
    private String alternateId;
    private String creditLimit;
    private String applicableBillingCycle;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getApplicableBillingCycle() {
        return applicableBillingCycle;
    }

    public void setApplicableBillingCycle(String applicableBillingCycle) {
        this.applicableBillingCycle = applicableBillingCycle;
    }
}
