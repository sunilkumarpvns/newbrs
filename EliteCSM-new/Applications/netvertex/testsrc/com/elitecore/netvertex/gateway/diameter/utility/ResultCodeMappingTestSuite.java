package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.ReloadData;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * -
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ResultCodeMappingInitTest.class,
        ResultCodeMappingReloadTest.class
})
public class ResultCodeMappingTestSuite {

    public static Matcher<IDiameterAVP> hasSameResultCode(int resultCode) {
        return new TypeSafeDiagnosingMatcher<IDiameterAVP>() {

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(IDiameterAVP avp, Description mismatchDescription) {
                return avp.getInteger() == resultCode;
            }
        };
    }

    //Checks Experimental Result Code AVP
    public static Matcher<IDiameterAVP> hasSameResultCodeAndVendorId(int resultCode, int vendorId) {
        return new TypeSafeDiagnosingMatcher<IDiameterAVP>() {

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(IDiameterAVP avp, Description mismatchDescription) {

                if (avp instanceof AvpGrouped == false) {
                    fail("Created AVP is not grouped AVP");
                }

                IDiameterAVP vendorIdAVP = ((AvpGrouped) avp).getSubAttribute(DiameterAVPConstants.VENDOR_ID);
                IDiameterAVP experimentalResultCodeAVP = ((AvpGrouped) avp).getSubAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);

                Assert.assertEquals("Vendor Id Not Matched", vendorId, vendorIdAVP.getInteger());
                Assert.assertEquals("Experimental Result Code Not Matched", resultCode, experimentalResultCodeAVP.getInteger());
                return vendorIdAVP.getInteger() == vendorId && experimentalResultCodeAVP.getInteger() == resultCode;
            }
        };
    }

    public static void checkAllDefaultResultCodes(ResultCodeMapping resultCodeMapping) {

        IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_SUCCESS.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_USER_UNKNOWN.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_USER_UNKNOWN.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_CREDIT_LIMIT_REACH.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_CREDIT_LIMIT_REACHED.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_RESPONSE_DROPPED.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_EXPIRED.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_INACTIVE.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code));
        resultCodeAVP = resultCodeMapping.getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_INTERNAL_ERROR.val, 0);
        assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));

    }
}