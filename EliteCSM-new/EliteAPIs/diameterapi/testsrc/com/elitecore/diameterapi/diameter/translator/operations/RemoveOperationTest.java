package com.elitecore.diameterapi.diameter.translator.operations;
import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.translator.delegator.DiameterPacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.diameterapi.diameter.translator.parser.DiameterCopyPacketParser;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

@RunWith(JUnitParamsRunner.class)
public class RemoveOperationTest {
	private static final String POLICY_NAME = "test";
	private TranslatorParams translatorParams;
	private DiameterPacket packetToOperate;

	static {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp(){

		packetToOperate = new DiameterAnswer();
		packetToOperate.setBytes(DiameterUtility.getBytesFromHexValue("0x0100032c80000110010000160000000000000000000000016000000d61646d696e000000000001256000001f6e65747665727465782e656c697465636f72652e636f6d0000000128600000156e65747665727465782e636f6d0000000000012860000015656c697465636f72652e636f6d0000000000012860000010706372662e636f6d0000012860000015656c697465636f72652e636f6d0000000000012860000015656c697465636f72652e636f6d0000000000010a6000000c000028af000003edc0000018000028af4d7950434352756c653132330000042ac0000018000028af4d7950434352756c654b657900000404c0000010000028af0000000a00000204c0000010000028af0000006400000203c0000010000028af0000006400000401c0000010000028af0000000a00000402c0000010000028af0000000a000003e9c000009c000028af000003ebc0000090000028af000003edc0000015000028af4d7950434352756c650000000000042ac000000f000028af6d6b3100000003f8c000005c000028af00000404c0000010000028af0000000a00000204c0000010000028af0000006400000203c0000010000028af0000006400000401c0000010000028af0000000a00000402c0000010000028af0000000a000003e9c00000b4000028af000003ebc00000a8000028af000003edc0000016000028af4d7950434352756c653100000000042ac000000f000028af6d6b3100000003f8c0000074000028af0000012860000015656c697465636f72652e636f6d00000000000404c0000010000028af0000000a00000204c0000010000028af0000006400000203c0000010000028af0000006400000401c0000010000028af0000000a00000402c0000010000028af0000000a000003e9c000009c000028af000003ebc0000090000028af000003edc0000016000028af4d7950434352756c653200000000042ac000000f000028af6d6b3100000003f8c000005c000028af00000404c0000010000028af0000000a00000204c0000010000028af0000006400000203c0000010000028af0000006400000401c0000010000028af0000000a00000402c0000010000028af0000000a"));
		packetToOperate.refreshInfoPacketHeader();

		translatorParams = new TranslatorParamsImpl(null, packetToOperate);
	}

	@Test
	@Parameters(method = "dataProviderFor_testRemoveOperation")
	public void testRemoveOperation(int caseId, 
			OperationData operationData, 
			List<IDiameterAVP> expectedAVPs, 
			String targetAttribute) {

		new RemoveOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(POLICY_NAME, operationData, DiameterPacketDelegator.getInstance())
		.execute(translatorParams);
		
		List<IDiameterAVP> actualAVPs = packetToOperate.getAVPList(targetAttribute);
		
		if(expectedAVPs == null) {
			Assert.assertNull(packetToOperate.getAVPList(targetAttribute));

		} else { 
			Assert.assertEquals(expectedAVPs.size(), actualAVPs.size());
			for (int i = 0 ; i < expectedAVPs.size() ; i++) {
				Assert.assertEquals(expectedAVPs.get(i), actualAVPs.get(i));
			}
		}
	}

	public static Object[][] dataProviderFor_testRemoveOperation() throws InvalidExpressionException {

		return new Object[][]{

			{0, DiameterCopyPacketParser.getRequestInstance().parse(null, "{ 'with': '10415:1001' ; 'when' : 'this.10415:1003.10415:1005=\"MyPCCRule\"'; 'do' : 'this.10415:1003.10415:1016.10415:1028'}", null, null, null), 
				createAVPs(new String[]{ "10415:1028" , "10"}, new String[]{ "10415:1028" , "10"}),
				"10415:1001.10415:1003.10415:1016.10415:1028"},
			
			{1, DiameterCopyPacketParser.getRequestInstance().parse(null, "{ 'with': '10415:1001' ; 'when' : 'this.10415:1003.10415:1005=\"MyPCCRule\"'}", null, null, null), 
				createAVPs(new String[]{ "10415:1001" , "{'10415:1003': {'10415:1005': 'MyPCCRule1', '10415:1066':'mk1' , '10415:1016':{ '0:296':'elitecore.com' , '10415:1028':'10' , '10415:516':'100' , '10415:515':'100', '10415:1025':'10' , '10415:1026':'10' } } }"},
						new String[]{ "10415:1001" , "{'10415:1003': {'10415:1005': 'MyPCCRule2', '10415:1066':'mk1' , '10415:1016':{'10415:1028':'10' , '10415:516':'100' , '10415:515':'100', '10415:1025':'10' , '10415:1026':'10' } } }"}),
				"10415:1001"},
			{2, DiameterCopyPacketParser.getRequestInstance().parse(null, "{ 'with': '0:296' ; 'when' : 'this=\"elitecore.com\"' }", null, null, null), 
				createAVPs(new String[]{"0:296", "netvertex.com"}, new String[]{"0:296", "pcrf.com"}),
				"0:296"},
				
			{3, DiameterCopyPacketParser.getRequestInstance().parse(null, "10415:1001.10415:1003.10415:1005", null, null, null), 
				null,
				"10415:1001.10415:1003.10415:1005"},
			
			{4, DiameterCopyPacketParser.getRequestInstance().parse(null, "0:293", null, null, null), 
				null,
				"0:293"},
		
			{5, DiameterCopyPacketParser.getRequestInstance().parse(null, "{ 'with': '10415:1001' ; 'when' : 'sub(this.10415:1003.10415:1016.10415:515 , \"100\")=\"0\"' ; 'do' : 'this.10415:1003.10415:1016.10415:516' }", null, null, null), 
				null,
				"10415:1001.10415:1003.10415:1016.10415:516"},
			
			{6, DiameterCopyPacketParser.getRequestInstance().parse(null, "{ 'with': '10415:1001' ; 'when' : '0:296=\"netvertex.com\"' ; 'do' : 'this' }", null, null, null), 
				null,
				"10415:1001"},
			
			{7, DiameterCopyPacketParser.getRequestInstance().parse(null, "{ 'with': '0:266' ; 'when' : 'this=\"10415\"'; }", null, null, null), 
				null,
				"0:266"},

				//Limitation in current architecture.
//			{8, DiameterCopyPacketParser.getDiameterCopyPacketParser().parse(null, "{ 'with': '10415:1001.10415:1003.10415:1016' ; 'when' : 'this.10415:515=\"100\"' }", null, null, null), 
//				null,
//				"10415:1001.10415:1003.10415:1016"},
				
		};
	}
	
	private static List<IDiameterAVP> createAVPs(String[]... avpData) {

		List<IDiameterAVP> avps = new ArrayList<IDiameterAVP>();
		
		for(String[] avpDatum : avpData) {
			IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(avpDatum[0]);
			if(avp == null) {
				continue;
			}
			avp.setStringValue(avpDatum[1]);
			avps.add(avp);
		}
		return avps;
	}

}
