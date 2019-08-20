package com.elitecore.netvertex.core.mapping.gx;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.MSCCUMBuilder;
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

public class MSCCUMBuilderTest {
	private DiameterRequest diameterRequest = null;
	private static UMBuilder msccUMBuilder;
	
	@BeforeClass
	public static void testSetup(){
		DummyDiameterDictionary.getInstance();
		msccUMBuilder = new MSCCUMBuilder();
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
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TOTAL_OCTETS, Integer.toString(totalOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		//System.out.println(diameterRequest);
		
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(1,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		
	}
	
	@Test
	public void testMultipleMSCC() throws Exception{
		
		int inputOctets = 512000;
		int outOctets = 52000;
		int totalOctets = inputOctets + outOctets;
		int time = 500;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_INPUT_OCTETS, Integer.toString(inputOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TOTAL_OCTETS, Integer.toString(totalOctets));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TIME, Integer.toString(time));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		
		usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_INPUT_OCTETS, Integer.toString(inputOctets * 2));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_OUTPUT_OCTETS, Integer.toString(outOctets * 2));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TOTAL_OCTETS, Integer.toString(totalOctets * 2));
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TIME, Integer.toString(time * 2));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(2,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		

		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(time, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(time * 2, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
	}
	
	
	@Test
	public void testUsageMeteringAVPNotFoundInPacket() throws Exception{
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertNull(usageMonitoringInfos);
		
	}
	
	
	@Test
	public void testOnlyOutputOctetsInMSCC() throws Exception{
		

		int outOctets = 52000;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_OUTPUT_OCTETS, Integer.toString(outOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		
		usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_OUTPUT_OCTETS, Integer.toString(outOctets * 2));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(2,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		

		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(outOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(outOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(outOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
	}
	
	@Test
	public void testOnlyInputOctetsInMSCC() throws Exception{
		
		int inputOctets = 512000;
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_INPUT_OCTETS, Integer.toString(inputOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		
		usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_INPUT_OCTETS, Integer.toString(inputOctets * 2));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(2,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		

		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(inputOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(inputOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(inputOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
	}
	
	@Test
	public void testOnlyTotalOctetsInMSCC() throws Exception{
		
		int inputOctets = 512000;
		int outOctets = 52000;
		int totalOctets = inputOctets + outOctets;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TOTAL_OCTETS, Integer.toString(totalOctets));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		
		usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);		
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TOTAL_OCTETS, Integer.toString(totalOctets * 2));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(2,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		

		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(totalOctets * 2, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
	}
	
	@Test
	public void testOnlyTimeInMSCC() throws Exception{
		
		int time = 500;
		
		//Usage-Monitoring-Info
		AvpGrouped usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		AvpGrouped usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TIME, Integer.toString(time));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		
		usageMonitoringAVP = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		usedServiceUnit = (AvpGrouped) DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
		createAndAddDiameterAVP(usedServiceUnit, DiameterAVPConstants.CC_TIME, Integer.toString(time * 2));
		usageMonitoringAVP.addSubAvp(usedServiceUnit);
		diameterRequest.addAvp(usageMonitoringAVP);
		
		List<UsageMonitoringInfo> usageMonitoringInfos = msccUMBuilder.buildUsageMonitoringInfo(diameterRequest);
		
		Assert.assertEquals(2,usageMonitoringInfos.size());
		
		UsageMonitoringInfo usageMonitoringInfo = usageMonitoringInfos.get(0);
		

		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(time, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
		
		usageMonitoringInfo = usageMonitoringInfos.get(1);
		
		Assert.assertEquals(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) , usageMonitoringInfo.getMonitoringKey());
		Assert.assertEquals(DiameterAttributeValueConstants.TGPP_USAGE_METERING_SESSION_LEVEL,usageMonitoringInfo.getUsageMonitoringLevel().val);
		
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getInputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getOutputOctets());
		Assert.assertEquals(0, usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		Assert.assertEquals(time * 2, usageMonitoringInfo.getUsedServiceUnit().getTime());
		
	}
	

	
	@Test
	public void testMultipleUMtoDiameterUMAVP() throws Exception{
		
		ServiceUnit serviceUnit = new ServiceUnit();
		long inputOctest = 512000;
		long outputOctet = 512000;
		long totalOctet = inputOctest + outputOctet;
		long time = 1000;
		serviceUnit.setOutputOctets(outputOctet);
		serviceUnit.setInputOctets(inputOctest);
		serviceUnit.setTotalOctets(totalOctet);
		serviceUnit.setTime(time);
		
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
		serviceUnit2.setTime(100);
		UsageMonitoringInfo usageMonitoringInfo2 = new UsageMonitoringInfo();
		usageMonitoringInfo2.setGrantedServiceUnit(serviceUnit2);
		usageMonitoringInfo2.setMonitoringKey("2");
		usageMonitoringInfo2.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		usageMonitoringInfo2.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		usageMonitoringInfo2.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setOutputOctets(outputOctet);
		sessionLevelServiceUnit.setInputOctets(inputOctest);
		sessionLevelServiceUnit.setTotalOctets(totalOctet);
		sessionLevelServiceUnit.setTotalOctets(time);
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(serviceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(usageMonitoringInfo,usageMonitoringInfo2, sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(2,umAVPs.size());
	
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		AvpGrouped sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		IDiameterAVP inputOctatesAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
		Assert.assertEquals(100, inputOctatesAVP.getInteger());
		
		IDiameterAVP outputOctatesAVP  = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
		Assert.assertEquals(100, outputOctatesAVP.getInteger());
		
		IDiameterAVP totalOctatesAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
		Assert.assertEquals(100, totalOctatesAVP.getInteger());
		
		IDiameterAVP ccTimeAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TIME);
		Assert.assertEquals(100, ccTimeAVP.getInteger());
		
		//
		usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(1);
		
		sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		inputOctatesAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
		Assert.assertEquals(inputOctest, inputOctatesAVP.getInteger());
		
		outputOctatesAVP  = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
		Assert.assertEquals(outputOctet, outputOctatesAVP.getInteger());
		
		totalOctatesAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
		Assert.assertEquals(totalOctet, totalOctatesAVP.getInteger());
		
		ccTimeAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TIME);
		Assert.assertEquals(time, ccTimeAVP.getInteger());
		
	}
	
	@Test
	public void testUMtoDiameterUMAVPWithOnlyOutputOctets() throws Exception{
		

		long outputOctet = 512000;

		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setOutputOctets(outputOctet);
		
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(sessionLevelServiceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
	
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		AvpGrouped sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS));
	
		
		IDiameterAVP outputOctatesAVP  = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
		Assert.assertEquals(outputOctet, outputOctatesAVP.getInteger());
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS));
		
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TIME));
	}
	
	@Test
	public void testUMtoDiameterUMAVPWithOnlyInputOctets() throws Exception{
		
		long inputOctest = 512000;

		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setInputOctets(inputOctest);
		
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(sessionLevelServiceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
	
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		AvpGrouped sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS));
	
		
		IDiameterAVP inputOctatesAVP  = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
		Assert.assertEquals(inputOctest, inputOctatesAVP.getInteger());
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS));
		
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TIME));
	}
	
	@Test
	public void testUMInfoWithPCCRuleLevelOnly() throws Exception{
		
		long totalOctet = 1000;

		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setTotalOctets(totalOctet);
		
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(sessionLevelServiceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertNull(umAVPs);
	}
	
	@Test
	public void testUMtoDiameterUMAVPWithOnlyTotalOctets() throws Exception{
		
		long totalOctet = 1000;

		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setTotalOctets(totalOctet);
		
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(sessionLevelServiceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
	
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		AvpGrouped sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS));
	
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS));
		
		IDiameterAVP totalOctatesAVP  = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
		Assert.assertEquals(totalOctet, totalOctatesAVP.getInteger());
		
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TIME));
	}
	
	@Test
	public void testNoUMInfoInPCRFResponse() throws Exception{
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertNull(umAVPs);
	}
	
	@Test
	public void testUMtoDiameterUMAVPWithOnlyTime() throws Exception{
		
		long time = 100;

		ServiceUnit sessionLevelServiceUnit = new ServiceUnit();
		sessionLevelServiceUnit.setTime(time);
		
		
		UsageMonitoringInfo sessionLevelUmInfo = new UsageMonitoringInfo();
		sessionLevelUmInfo.setGrantedServiceUnit(sessionLevelServiceUnit);
		sessionLevelUmInfo.setMonitoringKey("1");
		sessionLevelUmInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		sessionLevelUmInfo.setUsageMonitoringReport(UsageMonitoringReport.USAGE_MONITORING_REPORT_REQUIRED);
		sessionLevelUmInfo.setUsageMonitoringSupport(UsageMonitoringSupport.USAGE_MONITORING_DISABLED);
		
		
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		pcrfResponse.setUsageMonitoringInfoList(Arrays.asList(sessionLevelUmInfo));
		
		List<IDiameterAVP> umAVPs = msccUMBuilder.buildUsageMonitoringAVP(pcrfResponse);
		
		Assert.assertEquals(1,umAVPs.size());
	
		AvpGrouped usageMonitoringInfoAVP =(AvpGrouped) umAVPs.get(0);
		
		List<IDiameterAVP> sessionUsageAVPs = usageMonitoringInfoAVP.getSubAttributeList(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
		
		Assert.assertEquals(1,sessionUsageAVPs.size());
		
		AvpGrouped sessionUsageAVP =(AvpGrouped)  sessionUsageAVPs.get(0);
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS));
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS));
		
		Assert.assertNull(sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS));
		
		IDiameterAVP ccTimeAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.CC_TIME);
		Assert.assertEquals(time, ccTimeAVP.getInteger());
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
