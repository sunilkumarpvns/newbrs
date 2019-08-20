package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.ExternalAlternateIdEDRListener;
import com.elitecore.nvsmx.ws.subscription.data.ExternalAlternateIdentityEDRData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ExternalAlternateIdEDRListenerTest {
    private ExternalAlternateIdEDRListener externalAlternateIdEDRListener;
    private BaseCSVDriver externalAlternateIdEDRDriver;

    @Before
    public void setUp() {
        externalAlternateIdEDRListener = new ExternalAlternateIdEDRListenerImpl();
        EDRDriverManager edrDriverManager = EDRDriverManager.getInstance();
        edrDriverManager.registerDriver(createMockDriver("externalAlternateIdEDRDriver","$"));
    }

    @Test
    public void testHandleRequestMethodOfExternalAlternateIdEDRDriverIsCalledWithTheSameParametersThatArePassedInMethodForCreatingEDRforAddExternalAlternateIdOperation() throws DriverProcessFailedException {
        String subscriberId = "TEST";
        String alternateId = "CS.IMSI";
        externalAlternateIdEDRListener.addExternalAlternateIdEDR(subscriberId, alternateId, CommonConstants.ADD_EXTERNAL_ALTERNATE_ID);
        ExternalAlternateIdentityEDRData expectedExternalAlternateIdentityEDRData = ExternalAlternateIdentityEDRData.from(subscriberId, alternateId, "", CommonConstants.STATUS_ACTIVE, "", CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        Mockito.verify(externalAlternateIdEDRDriver, Mockito.atMost(1)).handleRequest(expectedExternalAlternateIdentityEDRData);
    }

    @Test
    public void testHandleRequestMethodOfExternalAlternateIdEDRDriverIsCalledWithTheSameParametersThatArePassedInMethodForCreatingEDRforUpdateExternalAlternateIdOperation() throws DriverProcessFailedException {
        String subscriberId = "TEST";
        String alternateId = "CS.IMSI";
        String updatedAlternateId = "CS.MSISDN";
        externalAlternateIdEDRListener.updateExternalAlternateIdEDR(subscriberId, alternateId, updatedAlternateId, CommonConstants.STATUS_ACTIVE, "", CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        ExternalAlternateIdentityEDRData expectedExternalAlternateIdentityEDRData = ExternalAlternateIdentityEDRData.from(subscriberId, alternateId, updatedAlternateId, CommonConstants.STATUS_ACTIVE, "", CommonConstants.UPDATE_EXTERNAL_ALTERNATE_ID_EDR);
        Mockito.verify(externalAlternateIdEDRDriver, Mockito.atMost(1)).handleRequest(expectedExternalAlternateIdentityEDRData);
    }

    @Test
    public void testHandleRequestMethodOfExternalAlternateIdEDRDriverIsCalledWithTheSameParametersThatArePassedInMethodForCreatingEDRforRemoveExternalAlternateIdOperation() throws DriverProcessFailedException {
        String subscriberId = "TEST";
        String alternateId = "CS.IMSI";
        externalAlternateIdEDRListener.removeExternalAlternateIdEDR(subscriberId, alternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
        ExternalAlternateIdentityEDRData expectedExternalAlternateIdentityEDRData = ExternalAlternateIdentityEDRData.from(subscriberId, alternateId, "", CommonConstants.STATUS_ACTIVE, "", CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
        Mockito.verify(externalAlternateIdEDRDriver, Mockito.atMost(1)).handleRequest(expectedExternalAlternateIdentityEDRData);
    }

    @Test
    public void testHandleRequestMethodOfExternalAlternateIdEDRDriverIsCalledWithTheSameParametersThatArePassedInMethodForCreatingEDRforChangeExternalAlternateIdStatusOperation() throws DriverProcessFailedException {
        String subscriberId = "TEST";
        String alternateId = "CS.IMSI";
        String updatedAlternateId = "CS.MSISDN";
        externalAlternateIdEDRListener.updateExternalAlternateIdEDR(subscriberId, alternateId, updatedAlternateId, CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE, CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
        ExternalAlternateIdentityEDRData expectedExternalAlternateIdentityEDRData = ExternalAlternateIdentityEDRData.from(subscriberId, alternateId, "", CommonConstants.STATUS_ACTIVE, CommonConstants.STATUS_INACTIVE, CommonConstants.REMOVE_EXTERNAL_ALTERNATE_ID);
        Mockito.verify(externalAlternateIdEDRDriver, Mockito.atMost(1)).handleRequest(expectedExternalAlternateIdentityEDRData);
    }

    private BaseCSVDriver createMockDriver(String name, String delimeter){
        externalAlternateIdEDRDriver = Mockito.mock(BaseCSVDriver.class);
        Mockito.when(externalAlternateIdEDRDriver.getDriverName()).thenReturn(name);
        Mockito.when(externalAlternateIdEDRDriver.getDelimiter()).thenReturn(delimeter);
        return externalAlternateIdEDRDriver;
    }
}
