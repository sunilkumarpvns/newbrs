package com.elitecore.license.base;

import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.DefaultDecryptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class MultiLicenseManagerTest {

    private boolean isObserverCalled;

    @Before
    public void setup(){
        isObserverCalled =false;
    }

    @Test
    public void addLicenseWithValidDataUsingFile() throws InvalidLicenseKeyException{
        File licenseFile = SingleLicenseManagerTest.getTempFile("2_limit.lic",LicenseKeys.LIMIT_2);
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(licenseFile,"Development Version");
        Assert.assertTrue(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addLicenseWithValidDataUsingStringReadFromFile() throws InvalidLicenseKeyException{
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(LicenseKeys.LIMIT_2,"Development Version");
        Assert.assertTrue(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addLicenseUsingTheDecryptorPassedInAddMethodAndValidateLicenseExpiryAsTrue() throws InvalidLicenseKeyException{
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(LicenseKeys.LIMIT_2,"Development Version", new DefaultDecryptor());
        Assert.assertTrue(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addBothValidAndInvalidLicenseButItMustValidateLicensePositivelyAsOneLicenseIsValid() throws InvalidLicenseKeyException{
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(LicenseKeys.INVALID_EXPIRY_DATE,"Development Version");
        licenseManager.add(LicenseKeys.LIMIT_2,"Development Version");
        Assert.assertTrue(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addLicenseWithInvalidExpiryDateAndCheckSystemExpiryFalse() throws InvalidLicenseKeyException{
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(LicenseKeys.INVALID_EXPIRY_DATE,"Development Version");
        Assert.assertFalse(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addNFVLicenseSuccessfullyAndCheckValidSystemExpiry() throws InvalidLicenseKeyException{
        File licensefile = SingleLicenseManagerTest.getTempFile("nfv_node.lic",LicenseKeys.NFV_NODE);
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn("nfv_node.lic").when(licensefile).getName();
        licenseManager.add(licensefile,"Development Version");
        Assert.assertTrue(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test (expected = InvalidLicenseKeyException.class)
    public void throwInvalidLicenseKeyExceptionWhenFileDoesNotExist() throws InvalidLicenseKeyException{
        File licensefile = SingleLicenseManagerTest.getTempFile("nfv_node.lic",LicenseKeys.NFV_NODE);
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn(false).when(licensefile).exists();
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(licensefile,"Development Version");
    }

    @Test (expected = InvalidLicenseKeyException.class)
    public void throwInvalidLicenseKeyExceptionWhenFileIsNotReadable() throws InvalidLicenseKeyException{
        File licensefile = SingleLicenseManagerTest.getTempFile("nfv_node.lic",LicenseKeys.NFV_NODE);
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn("nfv_node.lic").when(licensefile).getName();
        Mockito.doReturn(false).when(licensefile).canRead();
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.add(licensefile,"Development Version");
    }

    @Test
    public void getRemainedDaysToExpiryWhenExpiryIsIntheYear2117() throws Exception{
        File licensefile = SingleLicenseManagerTest.getTempFile("nfv_node.lic",LicenseKeys.NFV_NODE);
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn("nfv_node.lic").when(licensefile).getName();
        licenseManager.add(licensefile,"Development Version");
        Assert.assertNotNull(licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY));
        Assert.assertTrue(licenseManager.getRemainedDaysToExpire()>365);
    }

    @Test
    public void getLicenseExpiryDateNullWhenNoLicenseIsAdded() throws Exception{
        MultiLicenseManager licenseManager = new MultiLicenseManager();
        Assert.assertNull(licenseManager.getLicenseExpiryDate());
        Assert.assertNull(licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY));
    }

    @Test
    public void observeChangeInLicenseObserverFlagAsUploadLicenseCalled() throws Exception{

        MultiLicenseManager licenseManager = new MultiLicenseManager();
        licenseManager.registerObserver(()-> isObserverCalled =true);
        licenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        Assert.assertTrue(isObserverCalled);
    }
}
