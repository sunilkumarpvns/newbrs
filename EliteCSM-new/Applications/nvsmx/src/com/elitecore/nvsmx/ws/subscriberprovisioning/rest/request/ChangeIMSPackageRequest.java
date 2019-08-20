package com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChangeIMSPackageRequest {

    private String subscriberID;
    private String alternateID;
    private String currentPackageName;
    private String newPackageName;
    private String packageType;
    private String updateAction;


    public String getSubscriberID() {
        return subscriberID;
    }

    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }

    public String getAlternateID() {
        return alternateID;
    }

    public void setAlternateID(String alternateID) {
        this.alternateID = alternateID;
    }

    public String getCurrentPackageName() {
        return currentPackageName;
    }

    public void setCurrentPackageName(String currentPackageName) {
        this.currentPackageName = currentPackageName;
    }

    public String getNewPackageName() {
        return newPackageName;
    }

    public void setNewPackageName(String newPackageName) {
        this.newPackageName = newPackageName;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }
}
