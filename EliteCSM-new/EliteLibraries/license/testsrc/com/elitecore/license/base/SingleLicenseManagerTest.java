package com.elitecore.license.base;

import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.DefaultDecryptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SingleLicenseManagerTest {

    private boolean isObserverCalled;

    @Before
    public void setup(){
        isObserverCalled =false;
    }

    public static File getTempFile(String name,String content){
        File temp=null;
        try {
            // Create temp file.
            temp = File.createTempFile(name, "");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(content);
            out.close();
        } catch (IOException e) {
            //Sonar ignore
        }
        return temp;
    }

    @Test
    public void addLicenseWithValidDataUsingFileAndValidateLicenseExpiryAsTrue() throws InvalidLicenseKeyException {
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        File file = getTempFile("2_limit.lic", LicenseKeys.LIMIT_2);
        singleLicenseManager.add(file,"Development Version");
        Assert.assertTrue(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
        file.delete();
    }

    @Test
    public void addLicenseWithValidDataUsingStringReadFromFileAndValidateLicenseExpiryAsTrue() throws InvalidLicenseKeyException{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.add(LicenseKeys.LIMIT_2,"Development Version");
        Assert.assertTrue(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addLicenseUsingTheDecryptorPassedInAddMethodAndValidateLicenseExpiryAsTrue() throws InvalidLicenseKeyException{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.add(LicenseKeys.LIMIT_2,"Development Version", new DefaultDecryptor());
        Assert.assertTrue(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addLicenseWithInvalidExpiryDateAndValidateLicenseExpiryAsFalse() throws InvalidLicenseKeyException{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.add(LicenseKeys.INVALID_EXPIRY_DATE,"Development Version");
        Assert.assertFalse(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void addNFVLicenseSuccessfullyAndCheckValidSystemExpiryTrue() throws InvalidLicenseKeyException{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        File file = getTempFile("nfv_node.lic", LicenseKeys.NFV_NODE);
        file = Mockito.spy(file);
        Mockito.doReturn("nfv_node.lic").when(file).getName();
        singleLicenseManager.add(file,"Development Version");
        Assert.assertTrue(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
        file.delete();
    }

    @Test (expected = InvalidLicenseKeyException.class)
    public void throwInvalidLicenseKeyExceptionWhenFileDoesNotExist() throws InvalidLicenseKeyException{
        File licensefile = getTempFile("nfv_node.lic", LicenseKeys.NFV_NODE);
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn(false).when(licensefile).exists();
        licensefile.delete();
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.add(licensefile,"Development Version");
    }

    @Test (expected = InvalidLicenseKeyException.class)
    public void throwInvalidLicenseKeyExceptionWhenFileIsNotReadable() throws InvalidLicenseKeyException{
        File licensefile = getTempFile("nfv_node.lic", LicenseKeys.NFV_NODE);
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn(false).when(licensefile).canRead();
        licensefile.delete();
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.add(licensefile,"Development Version");
    }

    @Test
    public void handleInvalidLicenseKeyExceptionWhenSomeRandomFileIsTriedToUpload() throws InvalidLicenseKeyException{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.uploadLicense("InvalidTextTriedToUpload","Development Version");
    }

    @Test
    public void getRemainedDaysToExpiryWhenExpiryIsIntheYear2117() throws Exception{
        File licensefile = getTempFile("nfv_node.lic", LicenseKeys.NFV_NODE);
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        licensefile = Mockito.spy(licensefile);
        Mockito.doReturn("nfv_node.lic").when(licensefile).getName();
        singleLicenseManager.add(licensefile,"Development Version");
        Assert.assertNotNull(singleLicenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY));
        Assert.assertTrue(singleLicenseManager.getRemainedDaysToExpire()>365);
        licensefile.delete();
    }

    @Test
    public void getLicenseExpiryDateNullWhenNoLicenseIsAdded() throws Exception{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        Assert.assertNull(singleLicenseManager.getLicenseExpiryDate());
        Assert.assertNull(singleLicenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY));
    }

    @Test
    public void getLicenseExpiryDateNotNullWhenLicenseIsAddedUsingUploadLicense() throws Exception{
        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        Assert.assertNotNull(singleLicenseManager.getLicenseExpiryDate());
    }

    @Test
    public void observeChangeInLicenseObserverFlagAsAddObserverAndNotifyObserversAreImplemented() throws Exception{

        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.registerObserver(()-> isObserverCalled =true);
        singleLicenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        Assert.assertTrue(isObserverCalled);
    }

    @Test
    public void deregisterLicenseAfterUoloadingItThenCheckObserverStateChange() throws Exception{

        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        singleLicenseManager.registerObserver(()-> isObserverCalled =true);
        singleLicenseManager.deregisterLicense();
        Assert.assertTrue(isObserverCalled);
    }

    @Test
    public void afterDeregisteringLicenseValidateExpiryDatemutReturnFalse() throws Exception{

        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        singleLicenseManager.deregisterLicense();
        Assert.assertFalse(singleLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_EXPIRY,Long.toString(System.currentTimeMillis())));
    }

    @Test
    public void afterDeregisteringLicenseValuesFetchedFromLicenseUsingGetValueMustReturnNull() throws Exception{

        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        singleLicenseManager.registerObserver(()-> isObserverCalled =true);
        singleLicenseManager.deregisterLicense();
        Assert.assertNull(singleLicenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY));
        Assert.assertNull(singleLicenseManager.getLicenseKey());
    }

    @Test
    public void checkLicenseKeyIsNotNullAferLicenseIsUploaded() throws Exception{

        SingleLicenseManager singleLicenseManager = new SingleLicenseManager();
        singleLicenseManager.uploadLicense(LicenseKeys.LIMIT_2,"Development Version");
        singleLicenseManager.registerObserver(()-> isObserverCalled =true);
        Assert.assertNotNull(singleLicenseManager.getLicenseKey());
    }
}
