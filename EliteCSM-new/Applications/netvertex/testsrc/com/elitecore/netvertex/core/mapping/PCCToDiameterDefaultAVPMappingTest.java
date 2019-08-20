package com.elitecore.netvertex.core.mapping;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterDefaultAVPMapping;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactories;
import com.elitecore.netvertex.gateway.diameter.utility.AvpAccumalatorTestSupport;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.util.Arrays;

@RunWith(HierarchicalContextRunner.class)
public class PCCToDiameterDefaultAVPMappingTest {

	public static final String TEST_RESOURCE_PATH = "testsrc/resources/resultcodemappingscenarios/";
	private static final String SUCCESS_RESULT_CODE = "SUCCESS";
	private static final String DIAMETER_GATEWAY = "DIAMETER_GATEWAY";
	private PCCToDiameterDefaultAVPMapping pccToDiameterDefaultAVPMapping;
	private DiameterPacketMappingValueProvider valueProvider;
	private PCRFResponse pcrfResponse;
	private DiameterPacket diameterPacket;
	@Rule public TemporaryFolder mappingFolder = new TemporaryFolder();
	
	@Before
	public void before() throws Exception {
		DummyDiameterDictionary.getInstance();
		pccToDiameterDefaultAVPMapping = new PCCToDiameterDefaultAVPMapping();
		pcrfResponse = new PCRFResponseImpl();
		diameterPacket = new DiameterAnswer();
		valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterPacket, null, null);

	}


	@Test
	public void addGatewayNameInECOriginatorPeerNameWhenFoundFromPCRFResponse() {

		pcrfResponse.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, DIAMETER_GATEWAY);

		AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();

		pccToDiameterDefaultAVPMapping.apply(valueProvider, accumalator);

		ReflectionAssert.assertLenientEquals(Arrays.asList(createECOriginatorPeerName()), accumalator.getAllInfoAVPs());
	}


	public class ResultCodeAvp {

		@Before
		public void initializeResultCodeMapping() throws Exception {
			pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), SUCCESS_RESULT_CODE);
			mappingFolder.create();
			mappingFolder.newFolder("system", "diameter");
			File resultCodeMappingFile = mappingFolder.newFile("system/diameter/result-code-mapping.xml");
			File scenarioFile = new File(TEST_RESOURCE_PATH + "success_1RecordWithFullDetail.xml");
			FileUtils.copyFile(scenarioFile, resultCodeMappingFile);

			DummyNetvertexServerContextImpl serverContext = new DummyNetvertexServerContextImpl();
			serverContext.setServerHome(mappingFolder.getRoot().getAbsolutePath());
			ResultCodeMapping.getInstance().init(serverContext);
		}

		@Test
		public void test_applyAdds_defaultDiameterAVPsInDiameterPacket() throws Exception {

			AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();


			pccToDiameterDefaultAVPMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Arrays.asList(createResultCodeAVP()), accumalator.getAll());
		}


		@Test
		public void test_applyDoNotAddResultCode_WhenExperimentalResultAVPFound() {

			AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();

			diameterPacket.addAvp(AttributeFactories.fromDummyDictionary().create(DiameterAVPConstants.EXPERIMENTAL_RESULT));

			pccToDiameterDefaultAVPMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Arrays.asList(), accumalator.getAll());
		}



		private IDiameterAVP createResultCodeAVP() throws Exception {

			IDiameterAVP resultCodeAVP = DummyDiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			resultCodeAVP.setInteger(2001l);
			return (IDiameterAVP) resultCodeAVP.clone();

		}
	}

	private IDiameterAVP createECOriginatorPeerName() {
		IDiameterAVP avpECOriginPeer = AttributeFactories.fromDummyDictionary().create(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME);
		avpECOriginPeer.setStringValue(DIAMETER_GATEWAY);
		return avpECOriginPeer;
	}


}
