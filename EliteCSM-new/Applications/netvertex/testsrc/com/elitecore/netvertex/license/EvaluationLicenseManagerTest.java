package com.elitecore.netvertex.license;

import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.DefaultDecryptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;

public class EvaluationLicenseManagerTest {

    @Mock
    File file;

    @Before
    public void setup(){
        file = Mockito.mock(File.class);
    }

    @Test
    public void addLicenseUsingFileAndTPSIsAlways10AsThereIsNoMethodImplementaton() throws InvalidLicenseKeyException {
        EvaluationLicenseManager evaluationLicenseManager = new EvaluationLicenseManager(10,1);
        evaluationLicenseManager.add(file,"developmentVersion");
        Assert.assertTrue(evaluationLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_TPS,Integer.toString(10)));
    }

    @Test
    public void addLicenseUsingStringAndTPSIsAlways10AsThereIsNoMethodImplementaton() throws InvalidLicenseKeyException{
        EvaluationLicenseManager evaluationLicenseManager = new EvaluationLicenseManager(10,1);
        evaluationLicenseManager.add("Nothing","Development Version");
        Assert.assertTrue(evaluationLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_TPS,Integer.toString(10)));
    }

    @Test
    public void addLicenseUsingTheDecryptorPassedInAddMethodAndTPSIsAlways10AsThereIsNoMethodImplementaton() throws InvalidLicenseKeyException{
        EvaluationLicenseManager evaluationLicenseManager = new EvaluationLicenseManager(10,1);
        evaluationLicenseManager.add("AlwaysInvalidString","Development Version", new DefaultDecryptor());
        Assert.assertTrue(evaluationLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_TPS,Integer.toString(9)));
    }

    @Test
    public void tpsAbove11MustReturnFalseWhenCalledForLicenseValidity() throws InvalidLicenseKeyException{
        EvaluationLicenseManager evaluationLicenseManager = new EvaluationLicenseManager(10,1);
        Assert.assertFalse(evaluationLicenseManager.validateLicense(LicenseNameConstants.SYSTEM_TPS,Integer.toString(11)));
    }

    @Test
    public void createLicenseAndCheckForGxLicenseWhichMustBeValid() throws InvalidLicenseKeyException{
        EvaluationLicenseManager evaluationLicenseManager = new EvaluationLicenseManager(10,1);
        evaluationLicenseManager.add("No Value","Development Version");
        Assert.assertTrue(evaluationLicenseManager.validateLicense(LicenseNameConstants.NV_GX_INTERFACE,"Value"));
    }
}
