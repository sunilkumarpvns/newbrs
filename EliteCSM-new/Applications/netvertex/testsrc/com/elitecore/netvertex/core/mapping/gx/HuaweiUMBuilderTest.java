package com.elitecore.netvertex.core.mapping.gx;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.HuaweiUMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HuaweiUMBuilderTest {
	private DiameterRequest diameterRequest = null;
	private static UMBuilder huaweiUMBuilder;
	
	@BeforeClass
	public static void testSetup(){
		DummyDiameterDictionary.getInstance();
		huaweiUMBuilder = new HuaweiUMBuilder();
	}
	
	@Before
	public void setup(){
		diameterRequest = new DiameterRequest(false);
		
		createAndAddDiameterAVP(diameterRequest, DiameterAVPConstants.CC_REQUEST_NUMBER, 
				Integer.toString(DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST));
		createAndAddDiameterAVP(diameterRequest,DiameterAVPConstants.CC_REQUEST_TYPE, "1");
		createAndAddDiameterAVP(diameterRequest,DiameterAVPConstants.SESSION_ID, "123");
		createAndAddDiameterAVP(diameterRequest,DiameterAVPConstants.ORIGIN_HOST, "netvertex.elitecore.com");
		createAndAddDiameterAVP(diameterRequest,DiameterAVPConstants.ORIGIN_REALM, "elitecore.com");
		createAndAddDiameterAVP(diameterRequest,DiameterAVPConstants.DESTINATION_REALM, "eliteCSM.com");
	}
	
	@Test
	public void testSessionLevelUsageMeteringAVPFoundInCCRPacket() throws Exception{
		
		int inputOctets = 512000;
		int outOctets = 52000;
		int totalOctets = inputOctets + outOctets;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		//System.out.println(diameterRequest);
		
		
		List<UsageMonitoringInfo> usageMonitoringInfos = huaweiUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(1,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
	}
	
	//TODO provide @Test tag when support for service level metering was provided
	public void testServiceLevelUsageMeteringAVPFoundInCCRPacket() throws Exception{
		
		String monitoringKey = "5";
		int inputOctets = 512000;
		int outOctets = 52000;
		int totalOctets = inputOctets + outOctets;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.RATING_GROUP, monitoringKey);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);		
		
		List<UsageMonitoringInfo> usageMonitoringInfos = huaweiUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(1,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		
		Assert.assertEquals(monitoringKey , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_PCC_RULE_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
	}
	
	//TODO provide @Test tag when support for service level metering was provided
	public void testServiceAndSessionLevelUsageMeteringAVPFoundInCCRPacket() throws Exception{
		
		String monitoringKey = "5";
		int inputOctets = 512000;
		int outOctets = 52000;
		int totalOctets = inputOctets + outOctets;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.RATING_GROUP, monitoringKey);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.RATING_GROUP, monitoringKey + 1);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		
		diameterRequest.addAvp(usageMonitoringAVP);		
		
		List<UsageMonitoringInfo> usageMonitoringInfos = huaweiUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(3,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		Assert.assertEquals(monitoringKey , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_PCC_RULE_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(2);
		
		Assert.assertEquals(monitoringKey + 1 , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_PCC_RULE_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
	}
	
	@Test
	public void testUsageMeteringAVPNotFoundInPacket() throws Exception{
		
		List<UsageMonitoringInfo> usageMonitoringInfos = huaweiUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertNull(usageMonitoringInfos);
		
	}
	
	
	@Test
	public void testOnlyInputOctatsProvidedInUsageMeteringAVP() throws Exception{
		
		int inputOctets = 512000;
		int outOctets = 0;
		int totalOctets = inputOctets + outOctets;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		
		
		//TODO provide @Test tag when support for service level metering was provided
		/*usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.RATING_GROUP, monitoringKey);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_INPUT_OCTETS, Integer.toString(inputOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);*/

		
		diameterRequest.addAvp(usageMonitoringAVP);		
		
		List<UsageMonitoringInfo> usageMonitoringInfos = huaweiUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(1,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
		
		//TODO provide @Test tag when support for service level metering was provided
		/*usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		assertEquals(monitoringKey , usageMonitoringInfo.getMonitoringKey());
		assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_PCC_RULE_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getVolume());	*/
	}
	
	@Test
	public void testOnlyOutputOctatsProvidedInUsageMeteringAVP() throws Exception{
		
		int inputOctets = 0;
		int outOctets = 512000;
		int totalOctets = inputOctets + outOctets;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		
		
		//TODO provide @Test tag when support for service level metering was provided
		/*usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.RATING_GROUP, monitoringKey);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.GX_CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);*/

		
		diameterRequest.addAvp(usageMonitoringAVP);		
		
		List<UsageMonitoringInfo> usageMonitoringInfos = huaweiUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(1,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
		
		//TODO provide @Test tag when support for service level metering was provided
		/*usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		assertEquals(monitoringKey , usageMonitoringInfo.getMonitoringKey());
		assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_PCC_RULE_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getVolume());*/
	}
	

	
	@Test
	public void testPcrfUMtoDiameterUMAVP() throws Exception{
		
		ServiceUnit serviceUnit = new ServiceUnit();
		long inputOctest = Long.MAX_VALUE / 2;
		long outputOctet = Long.MAX_VALUE / 2;
		long totalOctet = Long.MAX_VALUE;
		serviceUnit.setOutputOctets(outputOctet);
		serviceUnit.setInputOctets(inputOctest);
		serviceUnit.setTotalOctets(totalOctet);
		
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
		usageMonitoringInfo.setMonitoringKey("1");
		usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
		usageMonitoringInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		usageMonitoringInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		ServiceUnit serviceUnit2 = new ServiceUnit();
		serviceUnit2.setOutputOctets(100);
		serviceUnit2.setInputOctets(100);
		serviceUnit2.setTotalOctets(100);
		UsageMonitoringInfo usageMonitoringInfo2 = new UsageMonitoringInfo();
		usageMonitoringInfo2.setGrantedServiceUnit(serviceUnit2);
		usageMonitoringInfo2.setMonitoringKey("2");
		usageMonitoringInfo2.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
		usageMonitoringInfo2.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		usageMonitoringInfo2.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setOutputOctets(outputOctet);
		sessionLevelServiceUnit.setInputOctets(inputOctest);
		sessionLevelServiceUnit.setTotalOctets(totalOctet);
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(serviceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(usageMonitoringInfo,usageMonitoringInfo2, sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = huaweiUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
		
		
		
		//pccRuleLevel service unit
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> serviceUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		
		Assert.assertEquals(0,serviceUsageAVPs.size());
		
		//TODO provide @Test tag when support for service level metering was provided
		/*AvpGrouped serviceUsageAVP =(AvpGrouped)  serviceUsageAVPs.get(0);
		
		IDiameterAVP inputOctatesAVP = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
		assertEquals(inputOctest, inputOctatesAVP.getInteger());
		
		IDiameterAVP outputOctatesAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
		assertEquals(outputOctet, outputOctatesAVP.getInteger());
		
		IDiameterAVP ratingGroupAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.RATING_GROUP);
		assertEquals(Long.parseLong(usageMonitoringInfo.getMonitoringKey()), ratingGroupAVP.getInteger());
		
		serviceUsageAVP =(AvpGrouped)  serviceUsageAVPs.get(1);
		
		inputOctatesAVP = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
		assertEquals(100, inputOctatesAVP.getInteger());
		
		outputOctatesAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
		assertEquals(100, outputOctatesAVP.getInteger());
		
		ratingGroupAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.RATING_GROUP);
		assertEquals(Long.parseLong(usageMonitoringInfo2.getMonitoringKey()), ratingGroupAVP.getInteger());*/
		
		
		//Session level service unit
		List<IDiameterAVP> sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		AvpGrouped sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		IDiameterAVP inputOctatesAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
		Assert.assertEquals(totalOctet, inputOctatesAVP.getInteger());
		
		IDiameterAVP outputOctatesAVP  = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
		Assert.assertEquals(totalOctet, outputOctatesAVP.getInteger());
		
	}
	
	//TODO provide @Test tag when support for service level metering was provided
	public void testPcrfPCCRuleLevelUMtoDiameterUMAVP() throws Exception{
		
		ServiceUnit serviceUnit = new ServiceUnit();
		long inputOctest = Long.MAX_VALUE / 2;
		long outputOctet = Long.MAX_VALUE / 2;
		long totalOctet = Long.MAX_VALUE;
		serviceUnit.setOutputOctets(outputOctet);
		serviceUnit.setInputOctets(inputOctest);
		serviceUnit.setTotalOctets(totalOctet);
		
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
		usageMonitoringInfo.setMonitoringKey("1");
		usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
		usageMonitoringInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		usageMonitoringInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setOutputOctets(outputOctet);
		sessionLevelServiceUnit.setInputOctets(inputOctest);
		sessionLevelServiceUnit.setTotalOctets(totalOctet);
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(usageMonitoringInfo));
		
		List<IDiameterAVP> umAVPs = huaweiUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
		
		
		
		//pccRuleLevel service unit
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> serviceUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GX_X_HW_SERVICE_USAGE);
		
		Assert.assertEquals(1,serviceUsageAVPs.size());
		
		AvpGrouped serviceUsageAVP =(AvpGrouped)  serviceUsageAVPs.get(0);
		
		IDiameterAVP inputOctatesAVP = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
		Assert.assertEquals(inputOctest, inputOctatesAVP.getInteger());
		
		IDiameterAVP outputOctatesAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
		Assert.assertEquals(outputOctet, outputOctatesAVP.getInteger());
		
		IDiameterAVP ratingGroupAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.RATING_GROUP);
		Assert.assertEquals(Long.parseLong(usageMonitoringInfo.getMonitoringKey()), ratingGroupAVP.getInteger());
		
	}
	
	
	@Test
	public void testPcrfSessionLevelUMtoDiameterUMAVP() throws Exception{
		
		ServiceUnit serviceUnit = new ServiceUnit();
		long inputOctest = Long.MAX_VALUE / 2;
		long outputOctet = Long.MAX_VALUE / 2;
		long totalOctet = Long.MAX_VALUE;
		serviceUnit.setOutputOctets(outputOctet);
		serviceUnit.setInputOctets(inputOctest);
		serviceUnit.setTotalOctets(totalOctet);
		
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
		usageMonitoringInfo.setMonitoringKey("1");
		usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		usageMonitoringInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		usageMonitoringInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(usageMonitoringInfo));
		
		List<IDiameterAVP> umAVPs = huaweiUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
		
		
		
		//pccRuleLevel service unit
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> serviceUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
		
		Assert.assertEquals(1,serviceUsageAVPs.size());
		
		AvpGrouped serviceUsageAVP =(AvpGrouped)  serviceUsageAVPs.get(0);
		
		IDiameterAVP inputOctatesAVP = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
		Assert.assertEquals(totalOctet, inputOctatesAVP.getInteger());
		
		IDiameterAVP outputOctatesAVP  = serviceUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
		Assert.assertEquals(totalOctet, outputOctatesAVP.getInteger());
		
	}
	
	
	
	
	public void createAndAddDiameterAVP(DiameterPacket diameterPacket, String avpID, String value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setStringValue(value);
		diameterPacket.addAvp(diameterAVP);
	}
	
	public void createAndAddDiameterInfoAVP(DiameterPacket diameterPacket, String avpID, String value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setStringValue(value);
		diameterPacket.addInfoAvp(diameterAVP);
	}
	
	public void createAndAddDiameterAVP(AvpGrouped diameterPacket, String avpID, String value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setStringValue(value);
		diameterPacket.addSubAvp(diameterAVP);
	}
	
	public void createAndAddDiameterAVP(AvpGrouped diameterPacket, String avpID, byte[] value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setValueBytes(value);
		diameterPacket.addSubAvp(diameterAVP);
	}

}
