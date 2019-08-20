package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChangeBaseProductOfferRestRequest  {

    private String subscriberId;
    private String alternateId;
    private String currentProductOfferName;
    private String newProductOfferName;
    private String updateAction;


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

    public String getCurrentProductOfferName() {
        return currentProductOfferName;
    }

    public void setCurrentProductOfferName(String currentProductOfferName) {
        this.currentProductOfferName = currentProductOfferName;
    }

    public String getNewProductOfferName() {
        return newProductOfferName;
    }

    public void setNewProductOfferName(String newProductOfferName) {
        this.newProductOfferName = newProductOfferName;
    }

    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }
}
