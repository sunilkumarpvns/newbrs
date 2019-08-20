package com.elitecore.netvertex.gateway.diameter.application;

import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elitecore.diameterapi.diameter.DiameterMatchers;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class RequestValidatorMatchers {
	
	public static Matcher<ValidationResult> hasFailedAVP(String expectedFailedAVP) {
		return new TypeSafeDiagnosingMatcher<ValidationResult>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("Failed AVP: ").appendValue(expectedFailedAVP);
			}

			@Override
			protected boolean matchesSafely(ValidationResult actualResult, Description description) {
				DiameterAnswer failedAnswer = actualResult.getFailedAnswer();
				AvpGrouped failedAVP = (AvpGrouped) failedAnswer.getAVP(DiameterAVPConstants.FAILED_AVP);
				
				if (failedAVP == null) {
					description.appendText("FAILED AVP not found");
					return false;
				}
				
				if (failedAVP.getSubAttribute(expectedFailedAVP) == null) {
					description.appendText("FAILED AVP not contains ").appendValue(expectedFailedAVP);
					return false;
				}
				
				return true;
			}
		};
	}

	public static Matcher<ValidationResult> hasResultCode(ResultCode expectedResultCode) {
		
		return new TypeSafeDiagnosingMatcher<ValidationResult>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("Result code: ").appendValue(expectedResultCode);
			}

			@Override
			protected boolean matchesSafely(ValidationResult actualResult, Description description) {
				DiameterAnswer failedAnswer = actualResult.getFailedAnswer();
				
				if (failedAnswer == null) {
					description.appendText("Diameter Answer is null");
					return false;
				}
				
				///TODO check for how to use Matchers inside Matchers --chetan
				assertThat(failedAnswer, DiameterMatchers.PacketMatchers.hasResultCode(expectedResultCode));
				
				return true;
			}
		};
	}

}
