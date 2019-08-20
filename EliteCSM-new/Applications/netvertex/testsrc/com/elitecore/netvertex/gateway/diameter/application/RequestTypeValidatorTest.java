package com.elitecore.netvertex.gateway.diameter.application;

import static com.elitecore.netvertex.gateway.diameter.application.RequestValidatorMatchers.hasFailedAVP;
import static com.elitecore.netvertex.gateway.diameter.application.RequestValidatorMatchers.hasResultCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class RequestTypeValidatorTest {
	
	private RequestTypeValidator requestTypeValidator;

	
	@BeforeClass
	public static void name() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() {
		this.requestTypeValidator = new RequestTypeValidator();
	}

	@Test(expected=NullPointerException.class)
	public void test_validate_ThrowsNPE_When_DiameterRequestIsNull() {
		DiameterRequest request = null;
		requestTypeValidator.validate(request);
	}
	
	@Test
	public void test_validate_ReturnsFailAnswerWithMissingAVPResultCode_When_RequestTypeAVPIsMissing() {
		DiameterRequest request = new DiameterRequest();
		ValidationResult actualResult = requestTypeValidator.validate(request);
		
		assertEquals(ValidationResult.Result.FAIL, actualResult.getResult());
		assertThat(actualResult, hasResultCode(ResultCode.DIAMETER_MISSING_AVP));
		assertThat(actualResult, hasFailedAVP(DiameterAVPConstants.CC_REQUEST_TYPE));
	}
	
	@Test
	public void test_validate_ReturnsFailAnswerWithMissingAVPResultCode_When_RequestedActionIsMissing() {
		DiameterRequest request = new DiameterRequest();
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST+"");
		ValidationResult actualResult = requestTypeValidator.validate(request);
		
		assertEquals(ValidationResult.Result.FAIL, actualResult.getResult());
		assertThat(actualResult, hasResultCode(ResultCode.DIAMETER_MISSING_AVP));
		assertThat(actualResult, hasFailedAVP(DiameterAVPConstants.REQUESTED_ACTION));
	}
	
	@Test
	@Parameters(value={"0","5","6","7"})
	public void test_validate_ReturnsFailAnswerWithInvalidAvpValueResultCode_When_RequestTypeValueIsInvalid(int requestType) {
		DiameterRequest request = new DiameterRequest();
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, requestType+"");
		
		ValidationResult actualResult = requestTypeValidator.validate(request);
		
		assertEquals(ValidationResult.Result.FAIL, actualResult.getResult());
		assertThat(actualResult, hasResultCode(ResultCode.DIAMETER_INVALID_AVP_VALUE));
		assertThat(actualResult, hasFailedAVP(DiameterAVPConstants.CC_REQUEST_TYPE));
	}
	
	@Test
	@Parameters(value={"-1","4"})
	public void test_validate_ReturnsFailAnswerWithInvalidAvpValueResultCode_When_RequestedActionValueIsInvalid(int requestedActionValue) {
		DiameterRequest request = new DiameterRequest();
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST+"");
		addToDiameterPacket(request, DiameterAVPConstants.REQUESTED_ACTION, requestedActionValue+"");
		
		ValidationResult actualResult = requestTypeValidator.validate(request);
		
		assertEquals(ValidationResult.Result.FAIL, actualResult.getResult());
		assertThat(actualResult, hasResultCode(ResultCode.DIAMETER_INVALID_AVP_VALUE));
		assertThat(actualResult, hasFailedAVP(DiameterAVPConstants.REQUESTED_ACTION));
	}
	
	@Test
	@Parameters(value={"0","1","2","3"})
	public void test_validate_ReturnsSuccess_When_EventReqyestHasValidRequestedAction(int requestedActionValue) throws Exception {
		DiameterRequest request = new DiameterRequest();
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST+"");
		addToDiameterPacket(request, DiameterAVPConstants.REQUESTED_ACTION, requestedActionValue+"");
		ValidationResult actualResult = requestTypeValidator.validate(request);	
		assertEquals(ValidationResult.success(), actualResult);
		assertNull(actualResult.getFailedAnswer());	
	}
	
	@Test
	@Parameters(value={"1","2","3","4"})
	public void test_validate_ReturnsSuccess_When_ValidRequestTypeProvided(int requestType) throws Exception {
		DiameterRequest request = new DiameterRequest();
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, requestType+ "");
		if (requestType == 4) {
			addToDiameterPacket(request, DiameterAVPConstants.REQUESTED_ACTION, 1+ "");
		}
		ValidationResult actualResult = requestTypeValidator.validate(request);	
		assertEquals(ValidationResult.success(), actualResult);
		assertNull(actualResult.getFailedAnswer());	
	}
	

	private void addToDiameterPacket(DiameterPacket diameterPacket, String attributeId, String value){
		IDiameterAVP diameterAVP  = DummyDiameterDictionary.getInstance().getKnownAttribute(attributeId);
		diameterAVP.setStringValue(value);
		diameterPacket.addAvp(diameterAVP);
	}
	
}
