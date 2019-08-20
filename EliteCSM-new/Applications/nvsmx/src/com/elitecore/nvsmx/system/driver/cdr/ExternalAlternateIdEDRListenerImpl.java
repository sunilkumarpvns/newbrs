package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.ExternalAlternateIdEDRListener;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.ws.subscription.data.ExternalAlternateIdentityEDRData;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ExternalAlternateIdEDRListenerImpl implements ExternalAlternateIdEDRListener {
    private static final String MODULE = "EXTRNL-ALTRNT-ID-EDR-LSTNR";
    private BaseCSVDriver externalAlternateIdEDRDriver;

    @Override
    public void addExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String operation) {
        externalAlternateIdEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.EXTERNAL_ALTERNATE_ID_EDR_DRIVER);

        try {
            ExternalAlternateIdentityEDRData externalAlternateIdentityEDRData = new ExternalAlternateIdentityEDRData();
            externalAlternateIdentityEDRData.setSubscriberId(subscriberIdentity);
            externalAlternateIdentityEDRData.setAlternateId(alternateIdentity);
            externalAlternateIdentityEDRData.setStatus(CommonConstants.STATUS_ACTIVE);
            externalAlternateIdentityEDRData.setOperation(operation);
            externalAlternateIdEDRDriver.handleRequest(externalAlternateIdentityEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while adding external alternate identity for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void updateExternalAlternateIdEDR(String subscriberIdentity, String oldAlternateIdentity, String updatedAlternateIdentity, String currentStatus, String updatedStatus, String operation) {
        externalAlternateIdEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.EXTERNAL_ALTERNATE_ID_EDR_DRIVER);

        try {
            ExternalAlternateIdentityEDRData externalAlternateIdentityEDRData = new ExternalAlternateIdentityEDRData();
            externalAlternateIdentityEDRData.setSubscriberId(subscriberIdentity);
            externalAlternateIdentityEDRData.setAlternateId(oldAlternateIdentity);
            externalAlternateIdentityEDRData.setUpdatedAlternateId(updatedAlternateIdentity);
            externalAlternateIdentityEDRData.setStatus(currentStatus);
            externalAlternateIdentityEDRData.setUpdatedStatus(updatedStatus);
            externalAlternateIdentityEDRData.setOperation(operation);
            externalAlternateIdEDRDriver.handleRequest(externalAlternateIdentityEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, "Error while updating external alternate identity for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }

    @Override
    public void removeExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String currentstatus, String operation) {
        externalAlternateIdEDRDriver = EDRDriverManager.getInstance().getDriver(NVSMXCommonConstants.EXTERNAL_ALTERNATE_ID_EDR_DRIVER);

        try {
            ExternalAlternateIdentityEDRData externalAlternateIdentityEDRData = new ExternalAlternateIdentityEDRData();
            externalAlternateIdentityEDRData.setSubscriberId(subscriberIdentity);
            externalAlternateIdentityEDRData.setAlternateId(alternateIdentity);
            externalAlternateIdentityEDRData.setStatus(currentstatus);
            externalAlternateIdentityEDRData.setOperation(operation);
            externalAlternateIdEDRDriver.handleRequest(externalAlternateIdentityEDRData);
        } catch (DriverProcessFailedException e) {
            getLogger().error(MODULE, " Error while writing EDR for remove external alternateId operation for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE,e);
        }
    }
}

