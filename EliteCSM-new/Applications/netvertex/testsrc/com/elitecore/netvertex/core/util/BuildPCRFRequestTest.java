package com.elitecore.netvertex.core.util;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS.IPCANQoSBuilder;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.corenetvertex.util.SessionUsageParser;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Date;
import java.util.LinkedHashMap;

@RunWith(JUnitParamsRunner.class)
public class BuildPCRFRequestTest {

	private static final String DEFAULT_SCHEMA = "Schema1";
	private static final String DEFAULT_PACKAGE_USAGE = "{\"baseId\":{\"p\":\"baseId\",\"q\":{\"ccac9ef2-63d3-4db3-bc02-4b989e0d39c8\":{\"DATA_SERVICE_TYPE_1\":{\"t\":100,\"d\":0,\"u\":0,\"ti\":15}}}}}";
	private static final String DEFAULT_REQUESTED_QOS = "{\"AAMBRDL\":100,\"AAMBRUL\":100,\"QCI\":\"QCI_NON_GBR_9\",\"PRIORITY_LEVEL_1\":\"PRIORITY_LEVEL_1\",\"PE_CAPABILITIY\":true,\"PE_VULENERABILITY\":true,\"QOS_UPGRADE\":false,\"MBRDL\":100,\"MBRUL\":100,\"GBRDL\":0,\"GBRUL\":0,\"mbrdl\":0,\"mbrul\":0,\"gbrdl\":0,\"gbrul\":0,\"aambrdl\":0,\"aambrul\":0}";
	private SessionData sessionData;
	private PCRFRequest pcrfRequest;
	
	public BuildPCRFRequestTest() {

		sessionData = new SessionDataImpl(DEFAULT_SCHEMA);
		pcrfRequest = new PCRFRequestImpl();
	}
		
	@Test
	public void testStoresPCRFKeyValuesBasedOnPackageUsage() {
		sessionData.addValue(PCRFKeyConstants.CS_PACKAGE_USAGE.val,DEFAULT_PACKAGE_USAGE);
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		ReflectionAssert.assertLenientEquals(SessionUsageParser.deserialize(DEFAULT_PACKAGE_USAGE).getAllSubscriptionWiseUsage(), pcrfRequest.getSessionUsage().getAllSubscriptionWiseUsage());
	}
	
	@Test
	public void testStoresRequestedIpCanQoS() {
		sessionData.addValue(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val, DEFAULT_REQUESTED_QOS);
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		Assert.assertEquals(new IPCANQoSBuilder().withJson(DEFAULT_REQUESTED_QOS).build().toString(), pcrfRequest.getRequestedQoS().toString());
	}
	
	@Test
	public void testStoresUsageReservationKey() {
		sessionData.addValue(PCRFKeyConstants.CS_USAGE_RESERVATION.val, "pccName:baseId");
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		LinkedHashMap<String, String> pccToPackageId = new LinkedHashMap<String, String>();
		pccToPackageId.put("pccName", "baseId");
		ReflectionAssert.assertLenientEquals(pccToPackageId, pcrfRequest.getUsageReservations());
	}
	
	@Test
	public void testStoresActivePCCRules() {
		sessionData.addValue(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val,"baseId:pccId");
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		ReflectionAssert.assertLenientEquals(ActivePCCRuleParser.deserialize("baseId:pccId"), pcrfRequest.getActivePccRules());
	}
	
	@Test
	public void testStoresActiveChargingRuleBaseNames() {
		sessionData.addValue(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val,"base1:crbn1");
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		ReflectionAssert.assertLenientEquals(ActivePCCRuleParser.deserialize("base1:crbn1"), pcrfRequest.getActiveChargingRuleBaseNames());
	}
	
	@Test
	public void testStoresSessionStartTime() {
		
		long creationTime = System.currentTimeMillis();
		sessionData = new SessionDataImpl(DEFAULT_SCHEMA, new Date(creationTime), new Date(creationTime));
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		Assert.assertEquals(new Date(creationTime), pcrfRequest.getSessionStartTime());
	}
	
	@Test
	public void testStoresSessionFoundTrue() {
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		Assert.assertEquals(true, pcrfRequest.isSessionFound());
	}
	
	@Test
	public void testBuildPCRFRequestWithOverrideStoresSessionLoadTime() {
		sessionData.setSessionLoadTime(200);
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest, false);
		Assert.assertEquals(200, pcrfRequest.getSessionLoadTime());
	}

	@Test
	public void testBuildPCRFRequestStoresSessionLoadTime() {
		sessionData.setSessionLoadTime(200);
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		Assert.assertEquals(200, pcrfRequest.getSessionLoadTime());
	}

	
	public Object[][] dataProviderFor_testBuildPCRFRequestStoresGatewayTypeInPCRFRequest() {
		
		return new Object[][] {
				{
					SessionTypeConstant.GX.val, GatewayTypeConstant.DIAMETER.val
				},
				{
					SessionTypeConstant.RX.val, GatewayTypeConstant.DIAMETER.val
				},
				{
					SessionTypeConstant.S9.val, GatewayTypeConstant.DIAMETER.val
				},
				{
					SessionTypeConstant.SY.val, GatewayTypeConstant.DIAMETER.val
				},
				{
					SessionTypeConstant.CISCO_GX.val, GatewayTypeConstant.DIAMETER.val
				},
				{
					SessionTypeConstant.CISCO_GY.val, GatewayTypeConstant.DIAMETER.val
				},
				{
					SessionTypeConstant.RADIUS.val, GatewayTypeConstant.RADIUS.val
				},
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_testBuildPCRFRequestStoresGatewayTypeInPCRFRequest")
	public void testStoresGatewayTypeBasedOnSessionType(String interfaceType, String protocol) {
		
		sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), interfaceType);
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		Assert.assertEquals(protocol, pcrfRequest.getAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal()));
	}
	
	@Test
	public void testDoNotStoreGatewayTypeIfCS_SESSION_TYPEKeyDoesNotExistsInSessionData() {
		
		PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);
		Assert.assertEquals(null, pcrfRequest.getAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal()));
	}
	
}
