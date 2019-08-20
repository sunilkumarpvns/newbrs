package com.elitecore.netvertex.gateway.diameter.utility;

import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterRequestMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class PCCToDiameterRequestMappingTest {

	private PCCToDiameterRequestMapping pccToDiameterRequestMapping;
	private DiameterPacketMappingValueProvider valueProvider;
	private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
	private PCRFResponse pcrfResponse;
	private DiameterRequest diameterPacket;
	private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();
	private DiameterGatewayConfiguration gatewayConfiguration;


	@Before
	public void before() {

		pccToDiameterRequestMapping = new PCCToDiameterRequestMapping();
		pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_ID.val, "SessionId:Tx");
		diameterPacket = new DiameterRequest();
		DummyDiameterGatewayControllerContext controllerContext = new DummyDiameterGatewayControllerContext();
		DummyNetvertexServerConfiguration serverConfiguration = controllerContext.getServerContext().getServerConfiguration();
		MiscellaneousConfiguration miscellaneousConfiguration = serverConfiguration.spyMiscConf();
		when(miscellaneousConfiguration.getServerInitiatedDestinationHost()).thenReturn(true);

		gatewayConfiguration = mock(DiameterGatewayConfiguration.class);
		valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterPacket, gatewayConfiguration, controllerContext);
		DummyDiameterDictionary.getInstance();
	}

	public class GatewayAddress {

		private static final String GATEWAY_ADDRESS = "GATEWAY_ADDRESS";

		@Test
		public void added_InAccumulator_WhenSendDestinationHostSetToFalse() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), GATEWAY_ADDRESS);

			MiscellaneousConfiguration miscConf = valueProvider.getServerConfiguration().getMiscellaneousParameterConfiguration();
			when(miscConf.getServerInitiatedDestinationHost()).thenReturn(true);
			diameterPacket.setCommandCode(CommandCode.RE_AUTHORIZATION.code);

			pccToDiameterRequestMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.DESTINATION_HOST, GATEWAY_ADDRESS))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_HOST));
		}

		@Test
		public void added_InAccumulator_WhenCommandCodeIsNotServerInitiated() {
			MiscellaneousConfiguration miscConf = valueProvider.getServerConfiguration().getMiscellaneousParameterConfiguration();
			when(miscConf.getServerInitiatedDestinationHost()).thenReturn(false);
			diameterPacket.setCommandCode(CommandCode.CREDIT_CONTROL.code);

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), GATEWAY_ADDRESS);
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.DESTINATION_HOST, GATEWAY_ADDRESS))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_HOST));
		}

		@Test
		public void notAdded_InAccumulator_WhenSendDestinationHostSetToFalseAndCommandCodeIsServerInitiated() {

			setUpFailCondition();

			pccToDiameterRequestMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_HOST));
		}

		@Test
		public void notAdded_InAccumulator_WhenGatewayAddressNotFoundInResponse() {

			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_HOST));
		}

		private void setUpFailCondition() {
			MiscellaneousConfiguration miscConf = valueProvider.getServerConfiguration().getMiscellaneousParameterConfiguration();
			when(miscConf.getServerInitiatedDestinationHost()).thenReturn(false);
			pcrfResponse.setAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), GATEWAY_ADDRESS);
			diameterPacket.setCommandCode(258);
		}

	}

	public class SySessionType {

		private long syApplicationId = ApplicationIdentifier.TGPP_SY.applicationId;
		private long syVendorId = ApplicationIdentifier.TGPP_SY.vendorId;

		@Before
		public void setUp() {
			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.SY.val);
			when(gatewayConfiguration.getSyApplicationId()).thenReturn(syApplicationId);
			when(gatewayConfiguration.getSyVendorId()).thenReturn(syVendorId);
		}

		@Test
		public void destinationRealm_notAdded_InAccumulator() {
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_HOST));
		}

		@Test
		public void destinationHost_notAdded_InAccumulator() {
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_REALM));
		}

		@Test
		public void applicationIds_Added_InAccumulator() {
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			assertEquals(syApplicationId, valueProvider.getDiameterPacket().getApplicationID());
			List<IDiameterAVP> authAppIdAVPs = accumalator.getDiameterAVPs(DiameterAVPConstants.AUTH_APPLICATION_ID);
			assertEquals(syApplicationId, authAppIdAVPs.get(0).getInteger());
		}
	}

	public class GatewayRealm {

		private static final String GATEWAY_REALM = "GATEWAY_REALM";

		@Before
		public void setUp() {
			pcrfResponse.setAttribute(PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), GATEWAY_REALM);	
		}
		
		@Test
		public void added_InAccumulator() {
			
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.DESTINATION_REALM, GATEWAY_REALM))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_REALM));
		}

		@Test
		public void notAdded_InAccumulator_WhenGatewayAddressNotFoundInResponse() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), null);
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_REALM));
		}

	}

	public class SessionIdAVP {

		@Test
		public void sessionIdShouldNotBeAddedIfSessionTypeIsSy() {
			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.SY.val);
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			assertTrue(accumalator.getDiameterAVPs(DiameterAVPConstants.SESSION_ID).isEmpty());

		}

		@Test
		public void sessionIdShouldBeAddedIfSessionTypeIsNotSy() {
			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
			pccToDiameterRequestMapping.apply(valueProvider, accumalator);
			assertTrue(accumalator.getDiameterAVPs(DiameterAVPConstants.SESSION_ID).size() == 1);
			assertEquals(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val), accumalator.getDiameterAVPs(DiameterAVPConstants.SESSION_ID).get(0).getStringValue());
		}
	}
}
