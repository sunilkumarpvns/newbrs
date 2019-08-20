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
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.translator.delegator.DiameterPacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.diameterapi.diameter.translator.parser.DiameterCopyPacketParser;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

@RunWith(JUnitParamsRunner.class)
public class AddOperationTest {
	private static final String POLICY_NAME = "test";
	
	private TranslatorParams translatorParams;
	private DiameterPacket packetToOperate;
	
	static {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp(){

		DiameterPacket sourceRequest = new DiameterRequest(false);
		sourceRequest.setBytes(DiameterUtility.getBytesFromHexValue("0x01000208c0000110010000160c468aec5081bb5b00000107400000307361736e2d64656c2d332d362e6572696373736f6e2e636f6d3b313335303938303835313b3932340000011b40000015656c697465636f72652e636f6d000000000001024000000c010000160000012540000014616161532d756c7469636f6d000000374000000cd430d3730000019f4000000c00000000000001a04000000c00000001000001bb40000028000001c24000000c00000000000001bc40000014393139393130323232353331000001bb4000002c000001c24000000c00000001000001bc4000001734303431303032313230343131393700000001ca4000002c000001cb4000000c00000000000001cc40000018393233333439373032303431313237320000012280000014000000c15341504372756c650000042480000010000000c100003b980000000140000017343034313030323132303431313937000000000d80000010000028af30313030000000084000000cdfefbe3d00000015c000000d000028af030000000000001e4000001661697274656c2068616e676f7574000000000006c0000010000028af0ae2cd0100000016c0000014000028af02040401270f000100000108400000217361736e2d64656c2d332d362e6572696373736f6e2e636f6d00000000000128400000146572696373736f6e2e636f6d000001164000000c50819cbc"));

		DiameterPacket destinationRequest = new DiameterRequest(false);
		destinationRequest.setBytes(DiameterUtility.getBytesFromHexValue("0x01000200c000011001000016253dcd0f5081bb5b00000107600000307361736e2d64656c2d332d362e6572696373736f6e2e636f6d3b313335303938303835313b39323400000108600000217361736e2d64656c2d332d362e6572696373736f6e2e636f6d00000000000128600000146572696373736f6e2e636f6d0000011b600000126875617765692e636f6d0000000000376000000cd430d3730000019f6000000c00000000000001a06000000c0000000100000125600000107465737470637266000001166000000c50819cbc000000084000000cdfefbe3d000001bb60000028000001c26000000c00000000000001bc60000014393139393130323232353331000001bb6000002c000001c26000000c00000001000001bc6000001734303431303032313230343131393700000001ca4000002c000001cb2000000c00000000000001cc40000018393233333439373032303431313237320000001e6000001661697274656c2068616e676f75740000000001026000000c0100001600000001600000173430343130303231323034313139370000000016e0000014000028af02040401270f000100000006e0000010000028af0ae2cd0100000015e000000d000028af030000000000000de0000010000028af303130300000011a400000217361736e2d64656c2d332d362e6572696373736f6e2e636f6d000000"));

		packetToOperate = new DiameterAnswer();
		packetToOperate.setBytes(DiameterUtility.getBytesFromHexValue("0x010001b44000011001000016253dcd0f5081bb5b00000107400000307361736e2d64656c2d332d362e6572696373736f6e2e636f6d3b313335303938303835313b393234000001024000000c010000160000010c4000000c000007d1000001a04000000c000000010000019f4000000c00000000000003eec0000010000028af00000002000003eec0000010000028af0000001a000003eec0000010000028af00000011000003e9c000002c000028af000003ecc000001e000028af4248415254495f564f4c554d455f504c414e0000000003f1c0000010000028af00000000000003f0c0000010000028af00000001000003f8c000004c000028af00000204c0000010000028af0148200000000203c0000010000028af0148200000000402c0000010000028af0001f40000000401c0000010000028af0001f4000000042b80000044000028af0000042a8000000d000028af31000000000001af40000018000001a540000010000000000cccd0000000042c80000010000028af00000001000003e880000010000028af000000000000010840000010746573747063726600000128400000126875617765692e636f6d0000"));

		translatorParams = new TranslatorParamsImpl(null, packetToOperate, sourceRequest, destinationRequest);
	}

	@Test
	@Parameters(method = "dataProviderFor_testAddOperation")
	public void testAddOperation(int caseId, 
			OperationData operationData, 
			List<IDiameterAVP> expectedAVPs, 
			String targetAttribute) {


		new AddOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(POLICY_NAME, operationData, DiameterPacketDelegator.getInstance())
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

	public static Object[][] dataProviderFor_testAddOperation() throws InvalidExpressionException {

		return new Object[][]{

			{0, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:1", "\"someUserName\"", null, null), 
				createAVPs(new String[]{ "0:1" , "someUserName"}),
				"0:1"},
				
//			{1, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:1", "*10415:1006", null, null), 
//				createAVPs(new String[]{ "0:1" , "RAT_CHANGE"}, new String[]{ "0:1" , "TAI_CHANGE"}, new String[]{ "0:1" , "REVALIDATION_TIMEOUT"}),
//				"0:1"},
				
			{2, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:456", "{'0:443' : ' ${DSTREQ}:0:443' }", null, null), 
				createAVPs(new String[]{ "0:456" , "{'0:443' : [ {'0:450':'0' , '0:444':'919910222531'}, {'0:450':'1' , '0:444':'404100212041197'} ]} }"}),
				"0:456"},
			
			{3, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:456", "{'with':'10415:1067' , 'when' : 'this.10415:1066 = \"1\"' , " +
					"'do': { '0:431' : { '0:421':'this.0:431.0:421' ; " +
										"'0:412': {'with' : '10415:1016' ; 'do': 'this.10415:1026' } ; " +
										"'0:414': {'with' : '10415:1016' ; 'do': 'this.10415:516'} } ; " +
							"'0:268': '0:268' } }", null, null), 
				createAVPs(new String[]{ "0:456" , "{'0:431' : { '0:421':'214749184'; '0:412':'128000' ; '0:414':'21504000' } ; '0:268':'2001' }"}),
				"0:456"},
				
			{4, DiameterCopyPacketParser.getResponseInstance().parse("0:1 = \"*\"", "0:1", "\"someUserName\"", null, null), 
				null,
				"0:1"},
				
			{5, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:1", "{ 'with' : '10415:1006' ; 'when':'this = \"10\"'}", null, null), 
				null,
				"0:1"},
			
			{6, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:1", "{ '10415:1006' : { 'with' : '10415:1006' ; 'when':'this = \"10\"'}}", null, null), 
				null,
				"0:1"},
				
			{7, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:1", "{ 'with' : '0:1' ; 'when':'this = \"10\"'}}", null, null), 
				null,
				"0:1"},
			
			{8, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:456", "{'with':'10415:1067' , 'when' : 'this.10415:1066 = \"1\"' , " +
					"'do': { '0:431' : { '0:421':'this.0:431.0:421' ; " +
										"'0:412': {'with' : '10415:1016' ; 'do': 'this.10415:1026' } ; " +
										"'0:414': {'with' : '10415:1015' ; 'do': 'this.10415:516'} } ; " +
							"'0:268': '0:268' } }", null, null), 
				createAVPs(new String[]{ "0:456" , "{'0:431' : { '0:421':'214749184'; '0:412':'128000' } ; '0:268':'2001' }"}),
				"0:456"},
			
			{9, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:456", " { '0:431' : { '0:421':'this.0:431.0:421' ; " +
										"'0:412': '0:1' ; " +
										"'0:414': '0:2' } } ", null, null), 
				null,
				"0:456"},
			{10, DiameterCopyPacketParser.getResponseInstance().parse(null, "10415:1001.0:431.0:421", "\"19902939\"", null, null), 
				createAVPs(
						new String[]{ "10415:1001" , "{'10415:1004' : 'BHARTI_VOLUME_PLAN' }"},
						new String[]{ "10415:1001" , "{'0:431' : { '0:421':'19902939' } }"}),
				"10415:1001"},
				
			{11, DiameterCopyPacketParser.getResponseInstance().parse(null, "10415:1001.0:431.0:421", "0:1", null, null), 
				createAVPs(new String[]{ "10415:1001" , "{'10415:1004' : 'BHARTI_VOLUME_PLAN' }"} ),
				"10415:1001"},
			
			{13, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:456.0:431", " { '0:421':'this.0:431.0:421' ; " +
						"'0:412': '0:1' ; " +
						"'0:414': '0:2' } ", null, null), 
				null,
				"0:456"},

			{14, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:456.0:431", " { '0:421':'this.0:431.0:421' ; " +
						"'0:412': '\"10101010\"' ; " +
						"'0:414': '0:2' } ", null, null), 
						createAVPs(new String[]{ "0:456" , "{'0:431' : { '0:412':'10101010' } }"}),
				"0:456"},
				
			{15, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:431.0:421", "0:1", "1024", null), 
					createAVPs(new String[]{ "0:431" , "{'0:421' : '1024' }"}),
			"0:431"},
			
			{16, DiameterCopyPacketParser.getResponseInstance().parse(null, "0:421", "0:1", "1024", null), 
				createAVPs(new String[]{ "0:421" , "1024"}),
			"0:421"},
					
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
