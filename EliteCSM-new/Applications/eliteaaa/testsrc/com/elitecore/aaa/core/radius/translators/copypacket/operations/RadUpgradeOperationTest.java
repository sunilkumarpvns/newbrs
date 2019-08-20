package com.elitecore.aaa.core.radius.translators.copypacket.operations;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.aaa.radius.translators.copypacket.RadiusPacketDelegator;
import com.elitecore.aaa.radius.translators.copypacket.parser.RadiusCopyPacketParser;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.UpgradeOperation;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

@RunWith(JUnitParamsRunner.class)
public class RadUpgradeOperationTest {
	private static final String POLICY_NAME = "test";
	private TranslatorParams translatorParams;
	private RadiusPacket packetToOperate;
	private static PacketDelegator<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> packetDelegator;

	static {
		
		RadiusDictionaryTestHarness.getInstance();
		packetDelegator = new RadiusPacketDelegator();
	}

	@Before
	public void setUp(){

		RadiusAuthRequestImpl sourceRequest = null;
		try {
			sourceRequest = new RadiusAuthRequestImpl(RadiusUtility.getBytesFromHexValue("0x011f014f95e6caaef59177eeff74ac3a2993e73b011335633a66383a61313a62323a32623a37640212c007431ca92d29cabf6c697ce4b37b2c060600000005200d4e4f492d424e472d5352310406dfefb0010506010400001a0c000009303e0614000fb73d0600000005571f312f3420766c616e2d6964203430323320636c697073203333303132371a0c0000093026060000000b1a1900000930911335633a66383a61313a62323a32623a37641f3a4167656e742d436972637569742d4964204e6f742050726573656e742352656d6f74652d4167656e742d4964204e6f742050726573656e741a0c000009306206000000051a1200000930700c31312e312e322e3470321a1200000930ca0c3d3d07015cf8a1b22b7d1a2300000930ca1d0c0c18616e64726f69642d383135303536313466303630316635391a14000009307d0e6468637063642d352e352e36"), 
					InetAddress.getLocalHost(), 0, null, null);
		} catch (UnknownHostException e) {
		}
		RadiusPacket destinationRequest = new RadiusPacket();
		destinationRequest.setBytes(DiameterUtility.getBytesFromHexValue("0x014c00dfee9621e730085c527c7cd1b06c07eae7011134303431303032333133373030303704060ad06ffa201f6d63633d3434342c6d6e633d3535352c7261693d303030393a303030381e1e30302d30442d36372d33452d35322d36343a455747205365637572653d06000000130506000000001f1331432d39392d34432d42312d33392d41464d17434f4e4e45435420304d627073203830322e3131622c1335333937453732372d30303030303030360c06000005784f1602b20014013430343130303233313337303030375012e047d843131dd486590697b3e2fc682d"));

		packetToOperate = new RadiusPacket();
		packetToOperate.setBytes(RadiusUtility.getBytesFromHexValue("0x021f00c50a121960b3d6c4de834921a9640ebd80121841757468656e7469636174696f6e20537563636573731a1400000930be0e494e5445524e45545f4143541a0c00000930bf06000000011a0c000009300306000000011a1200000930680c444843505f44656c68691b0600000e101955454c495445434c4153532c31303431353a313d3430343130303131383630323038362c303a33313d3931393831303139353737362c303a313d3430343130303131383630323038362c31303431353a31323d30"));

		GroupedAttribute ecDtaMsccAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.EC_DTA_MSCC);
		IRadiusAttribute ecAcctTotalOctetsAttr = null;
		// ELITE_ACCT_TOTAL_USED_UNITS 
		ecAcctTotalOctetsAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+ RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_TOTAL_OCTETS);
		ecAcctTotalOctetsAttr.setStringValue("362767236");
		ecDtaMsccAttr.addTLVAttribute(ecAcctTotalOctetsAttr);

		IRadiusAttribute ecAcctInOctetsAttr = null;
		ecAcctInOctetsAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+ RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_IN_OCTETS);
		ecAcctInOctetsAttr.setStringValue("82789174");
		ecDtaMsccAttr.addTLVAttribute(ecAcctInOctetsAttr);

		IRadiusAttribute ecAcctOutOctetsAttr = null;
		ecAcctOutOctetsAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID +":"+ RadiusAttributeConstants.EC_DTA_MSCC + ":" + RadiusAttributeConstants.EC_DTA_MSCC_OUT_OCTETS);
		ecAcctOutOctetsAttr.setStringValue(String.valueOf("48784"));
		ecDtaMsccAttr.addTLVAttribute(ecAcctOutOctetsAttr);

		packetToOperate.addInfoAttribute(ecDtaMsccAttr);
		
		
		IRadiusAttribute uliAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.VENDOR_3GPP_ID + ":" + 22);
		uliAttribute.setValueBytes(RadiusUtility.getBytesFromHexValue("0x0204041000010005"));
		
		packetToOperate.addAttribute(uliAttribute);

		packetToOperate.refreshPacketHeader();
		translatorParams = new TranslatorParamsImpl(null, packetToOperate, sourceRequest, destinationRequest);
	}

	@Test
	@Parameters(method = "dataProviderFor_testUpgradeOperation")
	public void testUpgradeOperation(int caseId, 
			OperationData operationData, 
			List<IRadiusAttribute> expectedAVPs, 
			String targetAttribute) {

		new UpgradeOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(POLICY_NAME, operationData, packetDelegator).execute(translatorParams);

		ArrayList<IRadiusAttribute> actualAVPs = (ArrayList<IRadiusAttribute>) packetToOperate.getRadiusAttributes(targetAttribute, true);

		if(expectedAVPs == null) {
			Assert.assertNull(packetToOperate.getRadiusAttributes(targetAttribute, true));

		} else { 
			Assert.assertEquals(expectedAVPs.size(), actualAVPs.size());
			for (int i = 0 ; i < expectedAVPs.size() ; i++) {
				Assert.assertEquals(expectedAVPs.get(i), actualAVPs.get(i));
			}
		}
	}

	public static Object[][] dataProviderFor_testUpgradeOperation() throws InvalidExpressionException {

		return new Object[][]{

				{0, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:1", "\"someUserName\"", null, null), 
					createAVPs(new String[]{ "0:1" , "someUserName"}),
				"0:1"},
				
				{1, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:27", "\"4000\"", null, null), 
					createAVPs(new String[]{ "0:27" , "4000"}),
				"0:27"},

				{2, RadiusCopyPacketParser.getResponseInstance().parse(null, "2352:190", "\"CHANGED\"", null, null), 
					createAVPs(new String[]{ "2352:190" , "CHANGED"}),
				"2352:190"},
				
				{3, RadiusCopyPacketParser.getResponseInstance().parse(null, "2352:190", "0:27", null, null), 
					createAVPs(new String[]{ "2352:190" , "3600"}),
				"2352:190"},
				
				{4, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:27", "${DSTREQ}:0:12", null, null), 
					createAVPs(new String[]{ "0:27" , "1400"}),
				"0:27"},
				
				{5, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:27", "${SRCREQ}:0:5", null, null), 
					createAVPs(new String[]{ "0:27" , "17039360"}),
				"0:27"},
				
				{6, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:27", "add(${SRCREQ}:0:5 , ${DSTREQ}:0:12)", null, null), 
					createAVPs(new String[]{ "0:27" , "17040760"}),
				"0:27"},
				
				{7, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:1", "${SRCREQ}:0:1", null, null), 
					createAVPs(new String[]{ "0:1" , "5c:f8:a1:b2:2b:7d"}),
				"0:1"},
				
				{8, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:1", "${DSTREQ}:0:1", null, null), 
					createAVPs(new String[]{ "0:1" , "404100231370007"}),
				"0:1"},
				
				{9, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:1", "mac2tgpp(\"IMSI\", ${SRCREQ}:0:1, \"\", \"\")", null, null), 
					createAVPs(new String[]{ "0:1" , "102222934453117"}),
				"0:1"},
				{10, RadiusCopyPacketParser.getResponseInstance().parse(null, "0:1", "10415:22.mnc", null, null), 
					createAVPs(new String[]{ "0:1" , "010"}),
					"0:1"
				},
				
				{11, RadiusCopyPacketParser.getResponseInstance().parse(null, "21067:117:1", "\"some_val\"", null, null), 
					createAVPs(new String[]{ "21067:117" , "{'1': 'some_val'}"}),
					"21067:117"
				},
				
				{12, RadiusCopyPacketParser.getResponseInstance().parse(null, "21067:117", "{'with' = '21067:134' , 'do' = {'1' = 'this:4' ; '2' = 'this:3' ; '3' = 'this:2'}}", null, null), 
					createAVPs(new String[]{"21067:117" , "{'1':'362767236' ; '2':'48784' ; '3' : '82789174'}"}),
					"21067:117"
				}
		};
	}

	private static List<IRadiusAttribute> createAVPs(String[]... avpData) {

		List<IRadiusAttribute> avps = new ArrayList<IRadiusAttribute>();

		for(String[] avpDatum : avpData) {
			IRadiusAttribute avp = Dictionary.getInstance().getKnownAttribute(avpDatum[0]);
			if(avp == null) {
				continue;
			}
			avp.setStringValue(avpDatum[1]);
			avps.add(avp);
		}
		return avps;
	}

}
