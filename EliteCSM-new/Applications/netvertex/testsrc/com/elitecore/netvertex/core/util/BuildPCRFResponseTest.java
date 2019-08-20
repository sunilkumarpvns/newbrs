package com.elitecore.netvertex.core.util;

import java.util.Date;
import java.util.LinkedHashMap;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.corenetvertex.util.SessionUsageParser;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

@RunWith(JUnitParamsRunner.class)
public class BuildPCRFResponseTest {

	private static final String DEFAULT_SCHEMA = "Schema1";
	private static final String DEFAULT_PACKAGE_USAGE = "{\"baseId\":{\"p\":\"baseId\",\"q\":{\"ccac9ef2-63d3-4db3-bc02-4b989e0d39c8\":{\"DATA_SERVICE_TYPE_1\":{\"t\":100,\"d\":0,\"u\":0,\"ti\":15}}}}}";
	private SessionData sessionData;
	private PCRFResponse pcrfResponse;
	
	public BuildPCRFResponseTest() {

		sessionData = new SessionDataImpl(DEFAULT_SCHEMA);
		pcrfResponse = new PCRFResponseImpl();
	}
	
	@Test
	public void testStoresSessionStartTimeFromSessionCreationTime() {
		
		Date creationTime = new Date(System.currentTimeMillis());
		sessionData = new SessionDataImpl(DEFAULT_SCHEMA, creationTime, creationTime);
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		Assert.assertEquals(creationTime, pcrfResponse.getSessionStartTime());
	}
	
	@Test
	public void testStoresSessionKeyValuesAsPCRFKeyValues() {
		
		sessionData.addValue(PCRFKeyConstants.REQUEST_NUMBER.val, "2");
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		Assert.assertEquals("2", pcrfResponse.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.val));
	}
	
public Object[][] dataProviderFor_testBuildPCRFResponseStoresGatewayType() {
		
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
	@Parameters(method="dataProviderFor_testBuildPCRFResponseStoresGatewayType")
	public void testStoresGatewayTypeBasedOnSessionType(String interfaceType, String protocol) {
		
		sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), interfaceType);
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		Assert.assertEquals(protocol, pcrfResponse.getAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal()));
	}
	
	@Test
	public void testDoesNotStoreGatewayTypeWhenInvalidSessionTypeReceived() {
		
		sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), "Xx");
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		Assert.assertNull(pcrfResponse.getAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal()));
	}
	
	@Test
	public void testStoresPCRFKeyValuesFromPackageUsage() {
		sessionData.addValue(PCRFKeyConstants.CS_PACKAGE_USAGE.val,DEFAULT_PACKAGE_USAGE);
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		ReflectionAssert.assertLenientEquals(SessionUsageParser.deserialize(DEFAULT_PACKAGE_USAGE).getAllSubscriptionWiseUsage(), pcrfResponse.getSessionUsage().getAllSubscriptionWiseUsage());
	}
	
	@Test
	public void testStoresUsageReservationKey() {
		sessionData.addValue(PCRFKeyConstants.CS_USAGE_RESERVATION.val, "pccName:baseId");
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		LinkedHashMap<String, String> pccToPackageId = new LinkedHashMap<String, String>();
		pccToPackageId.put("pccName", "baseId");
		ReflectionAssert.assertLenientEquals(pccToPackageId, pcrfResponse.getUsageReservations());
	}
	
	@Test
	public void testStoresActivePCCRules() {
		sessionData.addValue(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val,"baseId:pccId");
		PCRFPacketUtil.buildPCRFResponse(sessionData, pcrfResponse);
		ReflectionAssert.assertLenientEquals(ActivePCCRuleParser.deserialize("baseId:pccId"), pcrfResponse.getActivePccRules());
	}
	
}
