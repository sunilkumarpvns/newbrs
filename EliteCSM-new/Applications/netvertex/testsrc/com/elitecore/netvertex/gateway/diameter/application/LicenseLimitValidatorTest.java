package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.elitecore.netvertex.gateway.diameter.application.RequestValidatorMatchers.hasResultCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class LicenseLimitValidatorTest {
	
	private LicenseLimitValidator licenseLimitValidator;
	private DummyDiameterGatewayControllerContext context;
	
	@Before
	public void setUp() {
		
		this.context = spy(new DummyDiameterGatewayControllerContext());
		this.licenseLimitValidator = new LicenseLimitValidator(context);
	}

	@Test
	@Parameters(value={"0","2","3","4"})
	public void test_validate_ReturnsSuccess_when_RequestIsNotInitial(int requestType) {
		DiameterRequest request = new DiameterRequest();
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, requestType+"");
		ValidationResult actualResult = licenseLimitValidator.validate(request);
		assertEquals(ValidationResult.success(), actualResult);
	}
	
	@Test
	public void test_validate_ReturnsSuccess_When_LicenceMPMIsZero() throws Exception {
		NetVertexServerContext serverContext2 = mock(NetVertexServerContext.class);
		when(serverContext2.getLicencedMessagePerMinute()).thenReturn(0L);
		DiameterRequest request = new DiameterRequest();
		setRequestTypeInitial(request);
		ValidationResult actualResult = licenseLimitValidator.validate(request);
		assertEquals(ValidationResult.success(), actualResult);
	}
	
	@Test
	public void test_validate_ReturnsSuccess_When_LicenceMPMIsEqualToCurrentMPM() throws Exception {
		NetVertexServerContext serverContext2 = mock(NetVertexServerContext.class);
		when(serverContext2.getLicencedMessagePerMinute()).thenReturn(10L);
		when(context.getCurrentMessagePerMinute()).thenReturn(10L);
		
		DiameterRequest request = new DiameterRequest();
		setRequestTypeInitial(request);
		ValidationResult actualResult = licenseLimitValidator.validate(request);
		assertEquals(ValidationResult.success(), actualResult);
	}
	
	@Test
	public void test_validate_ReturnsSuccess_When_LicenceMPMIsGreatorToCurrentMPM() throws Exception {
		
		DummyNetvertexServerContextImpl serverContext2 = mock(DummyNetvertexServerContextImpl.class);
		when(serverContext2.getLicencedMessagePerMinute()).thenReturn(11L);
		when(context.getServerContext()).thenReturn(serverContext2);
		when(context.getCurrentMessagePerMinute()).thenReturn(10L);
		
		DiameterRequest request = new DiameterRequest();
		setRequestTypeInitial(request);
		ValidationResult actualResult = licenseLimitValidator.validate(request);
		assertEquals(ValidationResult.success(), actualResult);
	}
	
	@Test
	public void test_validate_ReturnsFailResultWithConfiguredOverloadResultCode_When_LicenceMPMIsLessToCurrentMPM_And_OverloadActionIsReject() throws Exception {
		DummyNetvertexServerContextImpl serverContext2 = mock(DummyNetvertexServerContextImpl.class);
		when(serverContext2.getLicencedMessagePerMinute()).thenReturn(10L);
		when(context.getServerContext()).thenReturn(serverContext2);
		when(context.getCurrentMessagePerMinute()).thenReturn(11L);
		when(context.getActionOnOverload()).thenReturn(OverloadAction.REJECT);
		when(context.getOverloadResultCode()).thenReturn(ResultCode.DIAMETER_UNABLE_TO_DELIVER.code);
		
		DiameterRequest request = new DiameterRequest();
		setRequestTypeInitial(request);
		
		ValidationResult actualResult = licenseLimitValidator.validate(request);
		
		assertEquals(ValidationResult.Result.FAIL, actualResult.getResult());
		assertThat(actualResult, hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER));
	}
	
	@Test
	public void test_validate_ReturnsDropResult_When_LicenceMPMIsLessToCurrentMPM_And_OverloadActionIsNotReject() throws Exception {
		DummyNetvertexServerContextImpl serverContext2 = mock(DummyNetvertexServerContextImpl.class);
		when(serverContext2.getLicencedMessagePerMinute()).thenReturn(10L);
		when(context.getServerContext()).thenReturn(serverContext2);
		when(context.getCurrentMessagePerMinute()).thenReturn(11L);
		when(context.getActionOnOverload()).thenReturn(OverloadAction.DROP);
		when(context.getOverloadResultCode()).thenReturn(ResultCode.DIAMETER_UNABLE_TO_DELIVER.code);
		DiameterRequest request = new DiameterRequest();
		setRequestTypeInitial(request);
		
		ValidationResult actualResult = licenseLimitValidator.validate(request);
		
		assertEquals(ValidationResult.drop(), actualResult);
	}

	private void setRequestTypeInitial(DiameterRequest request) {
		addToDiameterPacket(request, DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST_STR);
	}
	
	private void addToDiameterPacket(DiameterPacket diameterPacket, String attributeId, String value){
		IDiameterAVP diameterAVP  = DummyDiameterDictionary.getInstance().getKnownAttribute(attributeId);
		diameterAVP.setStringValue(value);
		diameterPacket.addAvp(diameterAVP);
	}

}
