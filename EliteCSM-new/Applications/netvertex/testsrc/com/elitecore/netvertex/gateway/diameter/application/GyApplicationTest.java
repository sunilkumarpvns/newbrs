package com.elitecore.netvertex.gateway.diameter.application;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.elitecore.diameterapi.diameter.DiameterMatchers;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.TGPPGyAppHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

public class GyApplicationTest {

	private GyApplication gyApplication;
	private TGPPGyAppHandler tgppGyAppHandler;
	private DummyDiameterGatewayControllerContext context;
	private static DiameterDictionary diameterDictionary;
	private DiameterGatewayConfiguration gatewayConf;
	private String gatewayName = "gatewayName";
	private DiameterSession session;
	private DiameterRequest request;
	
	static {
		diameterDictionary = DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() {
		this.context = spy(new DummyDiameterGatewayControllerContext());
		this.tgppGyAppHandler = spy(new TGPPGyAppHandler(context));
		this.gyApplication = spy(new GyApplication(context, Collections.emptyList(), Collections.emptyList(), tgppGyAppHandler));
		this.gatewayConf = spy(DiameterGatewayConfiguration.class);
		doReturn(gatewayConf).when(context).getGatewayConfigurationByHostId(anyString());
		doReturn(gatewayName).when(gatewayConf).getName();
		this.session = new DiameterSession("sessionId", null);
		this.request = new DiameterRequest();
		String originatorPeerName = "GGSN";
		request.setRequestingHost(originatorPeerName);
		request.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, originatorPeerName);
		request.addInfoAvp(DiameterAVPConstants.EC_PROXY_AGENT_NAME, originatorPeerName);
	}
	
	@Test
	public void test_init_SuperInitShouldCalled() throws Exception {
		gyApplication.init();
		verify(gyApplication, times(1)).superInit();
	}
	
	@Test
	public void test_init_ApplicationShouldBeInitializedOnceOnly() throws Exception {
		gyApplication.init();
		gyApplication.init();
		
		verify(gyApplication, times(1)).superInit();
	}
	
	@Test
	public void test_processApplicationRequest_sendDiameterUnableToComply_When_GatewayconfigurationIsNotFound() throws Exception {
		doReturn(null).when(context).getGatewayConfigurationByHostId(anyString());
		ArgumentCaptor<DiameterAnswer> captureAnswer = ArgumentCaptor.forClass(DiameterAnswer.class);

		gyApplication.processApplicationRequest(session, request);
		
		verify(gyApplication).sendAnswer(eq(session), eq(request), captureAnswer.capture());
		assertThat(captureAnswer.getValue(), DiameterMatchers.PacketMatchers.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY));
	}
	
	@Test
	public void test_processApplicationRequest_ShouldSearchGatewayByHostIdentity_When_GatewayNotFoundByInfoAvpValue() throws Exception {
		String hostName = "GGSNHostName";
		setOriginHost(request, hostName);
		
		gyApplication.processApplicationRequest(session, request);
		
		verify(context, times(1)).getGatewayConfigurationByHostId(hostName);
	}
	
	private void setOriginHost(DiameterRequest request, String hostName) {
		IDiameterAVP originHostAVP = request.getAVP(DiameterAVPConstants.ORIGIN_HOST);
		if (originHostAVP == null) {
			originHostAVP = diameterDictionary.getKnownAttribute(DiameterAVPConstants.ORIGIN_HOST);
			request.addAvp(originHostAVP);
		}
		originHostAVP.setStringValue(hostName);
	}

	@Test
	public void test_processApplicationRequest_AllValidatorsShouldBeProcessed_When_SuccessResultReceived() throws Exception {
		
		DummyRequestValidator validator1 = createDummyRequestValidator();
		validator1.setSuccessValidationResult();
		DummyRequestValidator validator2 = createDummyRequestValidator();
		validator2.setSuccessValidationResult();
		this.gyApplication = spy(new GyApplication(context, Arrays.asList(validator1, validator2), Collections.emptyList(), tgppGyAppHandler));
		
		this.gyApplication.processApplicationRequest(session, request);
		
		verify(validator1, times(1)).validate(request);
		verify(validator2, times(1)).validate(request);
	}

	private DummyRequestValidator createDummyRequestValidator() {
		return spy(new DummyRequestValidator());
	}
	
	@Test
	public void test_processApplicationRequest_ProcessShouldBeSkipped_When_ValidatorGivesDropResult() throws Exception {
		DummyRequestValidator validator1 = createDummyRequestValidator();
		validator1.setDropValidationResult();
		DummyRequestValidator validator2 = createDummyRequestValidator();
		validator2.setSuccessValidationResult();
		this.gyApplication = spy(new GyApplication(context, Arrays.asList(validator1, validator2), Collections.emptyList(), tgppGyAppHandler));
		
		this.gyApplication.processApplicationRequest(session, request);
		
		verify(validator1, times(1)).validate(request);
		verify(validator2, times(0)).validate(request);
		verify(gyApplication, times(0)).sendAnswer(eq(session), eq(request), any());
		verify(tgppGyAppHandler, times(0)).handleReceivedRequest(eq(session), eq(request));
	}
	
	@Test
	public void test_processApplicationRequest_SendFailedAnswerCreatedByValidator_When_ValidationGivesFailedResult() throws Exception {
		DummyRequestValidator validator1 = createDummyRequestValidator();
		DiameterAnswer failAnswer = new DiameterAnswer();
		validator1.setFailValidationResult(failAnswer);
		this.gyApplication = spy(new GyApplication(context, Arrays.asList(validator1), Collections.emptyList(), tgppGyAppHandler));
		
		this.gyApplication.processApplicationRequest(session, request);
		
		verify(gyApplication, times(1)).sendAnswer(eq(session), eq(request), eq(failAnswer));
	}
	
	@Test
	//FIXME: Suggest required for name
	public void test_processApplicationRequest_AllPreprocesserShouldBeProcessed() throws Exception {
		DummyRequestProprocessor preprocessor1 = new DummyRequestProprocessor();
		DummyRequestProprocessor preprocessor2 = new DummyRequestProprocessor();
		this.gyApplication = new GyApplication(context, Collections.emptyList(), Arrays.asList(preprocessor1, preprocessor2), tgppGyAppHandler);
		
		this.gyApplication.processApplicationRequest(session, request);
		
		preprocessor1.checkForProcessCall();
		preprocessor2.checkForProcessCall();
	}
	
	@Test
	public void test_processApplicationRequest_RequestShouldPassedToHandler() throws Exception {
		
		this.gyApplication.processApplicationRequest(session, request);
		
		verify(tgppGyAppHandler, times(1)).handleReceivedRequest(eq(session), eq(request), any());
	}
	
	@Test
	public void test_processApplicationRequest_SuperProcessApplicationRequestShouldCalled() {
	
		this.gyApplication.processApplicationRequest(session, request);
		
		verify(gyApplication, times(1)).superProcessApplicationRequest(eq(session), eq(request));
	}
	
	
}
