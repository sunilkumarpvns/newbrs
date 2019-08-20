package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.ASRAppSpecificMapping;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;

import static com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants.TGPP_ABORT_CAUSE;
import static com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants.TGPP_ABORT_CAUSE_BEARER_RELEASED;
import static org.mockito.Mockito.mock;


public class ASRMappingTest {

	private static final String ABORT_CAUSE = "420";
	private ASRAppSpecificMapping asrMapping;
	private DiameterPacketMappingValueProvider valueProvider;
	private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
	private PCRFResponse pcrfResponse;
	private DiameterPacket diameterPacket;
	private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();

	@Before
	public void before() {

		asrMapping = new ASRAppSpecificMapping();
		pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RX.val);
		diameterPacket = new DiameterRequest();
		DummyDiameterGatewayControllerContext controllerContext = new DummyDiameterGatewayControllerContext();
		DummyNetvertexServerConfiguration serverConfiguration = controllerContext.getServerContext().getServerConfiguration();
		serverConfiguration.spyMiscConf();
		valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterPacket, mock(DiameterGatewayConfiguration.class), controllerContext);
		DummyDiameterDictionary.getInstance();
		pcrfResponse.setAttribute(PCRFKeyConstants.ABORT_CAUSE.getVal(), ABORT_CAUSE);
	}

	@Test
	public void added_InAccumulator_WhenFoundFromPCRFResponse() {

		asrMapping.apply(valueProvider, accumalator);

		ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(TGPP_ABORT_CAUSE, ABORT_CAUSE))
				, accumalator.getDiameterAVPs(TGPP_ABORT_CAUSE));
	}

	@Test
	public void added_WithValueBearerReleasedInAccumulator_WhenValueNotFoundInResponse() {

		pcrfResponse.setAttribute(PCRFKeyConstants.ABORT_CAUSE.getVal(), null);
		asrMapping.apply(valueProvider, accumalator);
		ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(TGPP_ABORT_CAUSE, Integer.toString(TGPP_ABORT_CAUSE_BEARER_RELEASED))), accumalator.getDiameterAVPs(TGPP_ABORT_CAUSE));
	}

}
