package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aditya on 7/6/17.
 */
@RunWith(JUnitParamsRunner.class)
public class PkgTypeValidateMethodTest {

    private PkgData pkgData;
    private PkgTypeValidator pkgTypeValidator;



    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        pkgData = new PkgData();
        pkgData.setId("1111");
        pkgData.setName("Pkg");
        ACLModules aclModule = ACLModules.DATAPKG;
        pkgTypeValidator = new PkgTypeValidator(aclModule);

    }

    @Test
    public void return_Non_Empty_FailedReason_if_Null_PkgTypeIsPassed(){
        pkgData.setType(null);
        assertFalse(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }


    @Test
    public void return_Non_Empty_FailedReason_if_Empty_PkgTypeIsPassed(){
        pkgData.setType(CommonConstants.EMPTY_STRING);
        assertFalse(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }

    @Test
    public void return_Non_Empty_FailedReason_if_Blank_PkgTypeIsPassed(){
        pkgData.setType(" ");
        assertFalse(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }




    @Test
    @Parameters(value = {"ABCD","Promotional","PROMOTIONAL","EMERGENCY","emergency","IMS","ims"})
    public void return_Non_Empty_FailedReason_if_Invalid_PkgTypeIsPassed_for_DATA_Pkg(String pkgType){
        pkgData.setType(pkgType);
        assertFalse(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }

    @Test
    @Parameters(value= {"BASE","bAsE","AddOn","ADDON"})
    public void return_Empty_Failed_Reason_if_Valid_PkgTypeIsPassed_for_DATA_Pkg(String pkgType){
        pkgData.setType(pkgType);
        assertTrue(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }


    @Test
    @Parameters(value = {"ABCD","BASE","bAsE","AddOn","ADDON","EMERGENCY","emergency","IMS","ims"})
    public void return_Non_Empty_FailedReason_if_Invalid_PkgTypeIsPassed_for_Promotional_Pkg(String pkgType) {
        pkgData.setType(pkgType);
        pkgTypeValidator  = new PkgTypeValidator(ACLModules.PROMOTIONALPKG);
        assertFalse(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }

    @Test
    @Parameters(value= {"PROMOTIONAL","promotional"})
    public void return_Empty_Failed_Reason_if_Valid_PkgTypeIsPassed_for_Promotional_Pkg(String pkgType) {
        pkgData.setType(pkgType);
        pkgTypeValidator  = new PkgTypeValidator(ACLModules.PROMOTIONALPKG);
        assertTrue(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }


    @Test
    @Parameters(value = {"ABCD","BASE","bAsE","AddOn","ADDON","PROMOTIONAL","promotional","IMS","ims"})
    public void return_Non_Empty_FailedReason_if_Invalid_PkgTypeIsPassed_for_Emergency_Pkg(String pkgType)  {
        pkgData.setType(pkgType);
        pkgTypeValidator  = new PkgTypeValidator(ACLModules.EMERGENCYPKG);
        assertFalse(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }

    @Test
    @Parameters(value= {"EMERGENCY","EMERGENCY"})
    public void return_Empty_Failed_Reason_if_Valid_PkgTypeIsPassed_for_Emergency_Pkg(String pkgType) {
        pkgData.setType(pkgType);
        pkgTypeValidator  = new PkgTypeValidator(ACLModules.EMERGENCYPKG);
        assertTrue(Collectionz.isNullOrEmpty(pkgTypeValidator.validate(pkgData)));
    }

    @Test
    public void failed_to_created_pkgType_validator_if_null_Acl_Module_is_passed() {
        pkgData.setType("DATAPKG");
        exception.expect(NullPointerException.class);
        exception.expectMessage("Acl Module Type can't be null");
        PkgTypeValidator pkgTypeValidator = new PkgTypeValidator(null);
    }

}