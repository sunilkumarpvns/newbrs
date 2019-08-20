package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;

import javax.xml.bind.annotation.XmlType;
import java.io.StringWriter;

@XmlType(propOrder={"subscriberId", "alternateId", "updatedAlternateId", "status", "updatedStatus", "operation"})
public class ExternalAlternateIdentityEDRData {

    private String alternateId;
    private String updatedAlternateId;
    private String subscriberId;
    private String status;
    private String updatedStatus;
    private String operation;

    public String getAlternateId() {
        return alternateId;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getUpdatedAlternateId() {
        return updatedAlternateId;
    }

    public void setUpdatedAlternateId(String updatedAlternateId) {
        this.updatedAlternateId = updatedAlternateId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUpdatedStatus() {
        return updatedStatus;
    }

    public void setUpdatedStatus(String updatedStatus) {
        this.updatedStatus = updatedStatus;
    }

    public static ExternalAlternateIdentityEDRData from(String subscriberId, String alternateId, String updatedAlternateId, String currentStatus, String updatedStatus, String operation) {
        ExternalAlternateIdentityEDRData expectedExternalAlternateIdentityEDRData = new ExternalAlternateIdentityEDRData();
        expectedExternalAlternateIdentityEDRData.setSubscriberId(subscriberId);
        expectedExternalAlternateIdentityEDRData.setAlternateId(alternateId);
        expectedExternalAlternateIdentityEDRData.setUpdatedAlternateId(updatedAlternateId);
        expectedExternalAlternateIdentityEDRData.setStatus(currentStatus);
        expectedExternalAlternateIdentityEDRData.setUpdatedStatus(updatedStatus);
        expectedExternalAlternateIdentityEDRData.setOperation(operation);
        return expectedExternalAlternateIdentityEDRData;
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingPrintWriter out = new IndentingPrintWriter(stringWriter);
        toString(out);
        return stringWriter.toString();
    }

    public void toString(IndentingPrintWriter out) {
        out.println();
        out.println("Subscriber Id = " + subscriberId);
        out.println("Alternate Id = " + alternateId);
        out.println("Updated Alternate Id = " + updatedAlternateId);
        out.println("Status = " + status);
        out.println("Updated Status = " + updatedStatus);
        out.println("Operation = " + operation);
    }
}

