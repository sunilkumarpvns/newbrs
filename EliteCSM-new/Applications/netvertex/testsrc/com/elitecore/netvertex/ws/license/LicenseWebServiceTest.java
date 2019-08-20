package com.elitecore.netvertex.ws.license;

import com.elitecore.license.base.SingleLicenseManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LicenseWebServiceTest {

    private SingleLicenseManager singleLicenseManager;
    private boolean licenseManagerObserver;
    private boolean resultFlagForObserver;
    private String result = "License deregistered successfully.";

    @Before
    public void setup(){
        singleLicenseManager = new SingleLicenseManager();
        resultFlagForObserver =false;
        licenseManagerObserver = false;
    }

    @Test
    public void successCallToTheDeregisterServiceAndCheckIfObserverCalled(){
        singleLicenseManager.registerObserver(()->licenseManagerObserver=true);
        LicenseWebService licenseWebService= new LicenseWebService(singleLicenseManager,()-> {});
        String response =licenseWebService.deregisterLicense();
        Assert.assertTrue(licenseManagerObserver);
        Assert.assertTrue(result.equals(response));
    }

    @Test
    public void successCallToTheDeregisterServiceAndCheckIfServiceMemberObserverCalled(){
        LicenseWebService licenseWebService= new LicenseWebService(singleLicenseManager,()-> resultFlagForObserver =true);
        String response =licenseWebService.deregisterLicense();
        Assert.assertTrue(resultFlagForObserver);
        Assert.assertTrue(result.equals(response));
    }
}
