package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.sy.PCCToDiameterSyMapping;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class PCCToDiameterSyMappingTest {

	private PCCToDiameterSyMapping slrMapping;
	private DiameterPacketMappingValueProvider valueProvider;
	private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
	private PCRFResponse pcrfResponse;
	private DiameterRequest diameterPacket;
	private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();

	@Before
	public void before() {

		slrMapping = new PCCToDiameterSyMapping();
		pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.SY.val);
		diameterPacket = new DiameterRequest();
		DummyDiameterGatewayControllerContext controllerContext = new DummyDiameterGatewayControllerContext();
		DummyNetvertexServerConfiguration serverConfiguration = controllerContext.getServerContext().getServerConfiguration();
		serverConfiguration.spyMiscConf();
		valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterPacket, mock(DiameterGatewayConfiguration.class), controllerContext);
		DummyDiameterDictionary.getInstance();
	}

	public class DestinationRealm {

		private static final String SY_GW_REALM = "SY_GW_REALM";

		@Test
		public void added_InAccumulator_WhenFoundFromPCRFResponse() {

			pcrfResponse.setAttribute(PCRFKeyConstants.SY_GATEWAY_REALM.val, SY_GW_REALM);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.DESTINATION_REALM, SY_GW_REALM))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_REALM));
		}

		@Test
		public void added_InAccumulator_withValueAsterisk_WhenValueNotFoundFromResponse() {

			pcrfResponse.setAttribute(PCRFKeyConstants.SY_GATEWAY_REALM.val, null);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.DESTINATION_REALM, "*"))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.DESTINATION_REALM));
		}

	}

	public class RequestType {

		@Test
		public void added_InAccumulator_asUpdateRequest_WhenSessionIdFound() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val, "session_id");
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE, "1"))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE));
		}

		@Test
		public void added_InAccumulator_AsInitialRequest_WhenValueNotFoundFromResponse() {

			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE, "0"))
							, accumalator.getDiameterAVPs(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE));
		}

	}

	public class MSISDN {

		private static final String MSISDN = "007";

		@Test
		public void added_InAccumulator_FromCS_SUBSCRIPTION_ID_TYPE_MSISDN() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.getVal(), MSISDN);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_E164, MSISDN)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}

		@Test
		public void added_InAccumulator_FromSUB_MSISDN() {

			pcrfResponse.setAttribute(PCRFKeyConstants.SUB_MSISDN.getVal(), MSISDN);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_E164, MSISDN)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
		@Test
		public void added_InAccumulator_WhenValueNotFoundFromResponse() {

			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collections.emptyList(), accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
	}
	
	public class IMSI {

		private static final String IMSI = "007";

		@Test
		public void added_InAccumulator_FromCS_SUBSCRIPTION_ID_TYPE_IMSI() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), IMSI);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_IMSI, IMSI)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}

		@Test
		public void added_InAccumulator_FromSUB_IMSI() {

			pcrfResponse.setAttribute(PCRFKeyConstants.SUB_IMSI.getVal(), IMSI);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_IMSI, IMSI)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
		@Test
		public void added_InAccumulator_WhenValueNotFoundFromResponse() {

			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collections.emptyList(), accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
	}	
	
	public class SIPURI {

		private static final String SIPURI = "sip@uri.com";

		@Test
		public void added_InAccumulator_FromCS_SUBSCRIPTION_ID_TYPE_SIPURI() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.getVal(), SIPURI);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_SIP_URI, SIPURI)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}

		@Test
		public void added_InAccumulator_FromSUB_SIP_URL() {

			pcrfResponse.setAttribute(PCRFKeyConstants.SUB_SIP_URL.getVal(), SIPURI);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_SIP_URI, SIPURI)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
		@Test
		public void notAdded_InAccumulator_WhenValueNotFoundFromResponse() {

			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collections.emptyList(),  accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
	}
	
	public class NAI {

		private static final String NAI = "nai";

		@Test
		public void added_InAccumulator() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.getVal(), NAI);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(createExpectedAVPs(DiameterAttributeValueConstants.END_USER_NAI, NAI)
					, accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
		@Test
		public void notAdded_InAccumulator_WhenValueNotFoundFromResponse() {

			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collections.emptyList(), accumalator.getDiameterAVPs(DiameterAVPConstants.SUBSCRIPTION_ID));
		}
		
	}
	
	public class FramedIPAddress {

		private static final String IPV4 = "10.10.10.10";
		private static final String IPV6 = "0:0:0:0:0:0:0:0";

		@Test
		public void added_InAccumulator_WithIPV4() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_IPV4.val, IPV4);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.FRAMED_IP_ADDRESS, IPV4))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.FRAMED_IP_ADDRESS));
		}
		
		@Test
		public void added_InAccumulator_WithIPV6() {

			pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_IPV6.val, IPV6);
			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(DiameterAVPConstants.FRAMED_IPV6_PREFIX, IPV6))
					, accumalator.getDiameterAVPs(DiameterAVPConstants.FRAMED_IPV6_PREFIX));
		}

		@Test
		public void not_added_InAccumulator_WhenNotFoundFromResponse() {


			slrMapping.apply(valueProvider, accumalator);
			ReflectionAssert.assertLenientEquals(Collections.emptyList()
					, accumalator.getDiameterAVPs(DiameterAVPConstants.FRAMED_IPV6_PREFIX));
			ReflectionAssert.assertLenientEquals(Collections.emptyList()
					, accumalator.getDiameterAVPs(DiameterAVPConstants.FRAMED_IP_ADDRESS));
		}

	}
	
	private List<IDiameterAVP> createExpectedAVPs(int subscriberIdType, String subscriberIdValue) {
		AvpGrouped subscriptionId = (AvpGrouped) attributeFactory.create(DiameterAVPConstants.SUBSCRIPTION_ID);
		IDiameterAVP subscriptionIdType = attributeFactory.create(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		IDiameterAVP subscriptionIdData = attributeFactory.create(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		subscriptionIdType.setInteger(subscriberIdType);
		subscriptionIdData.setStringValue(subscriberIdValue);

		subscriptionId.addSubAvp(subscriptionIdType);
		subscriptionId.addSubAvp(subscriptionIdData);
		return Arrays.asList(subscriptionId);

	}
}
