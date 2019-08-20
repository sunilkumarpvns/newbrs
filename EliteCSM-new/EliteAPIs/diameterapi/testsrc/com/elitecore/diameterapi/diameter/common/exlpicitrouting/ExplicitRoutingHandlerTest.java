package com.elitecore.diameterapi.diameter.common.exlpicitrouting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.explicitrouting.exception.ExplicitRoutingFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class ExplicitRoutingHandlerTest {

	private static ExplicitRoutingHandler explicitRoutingHandler = new ExplicitRoutingHandler();
	/**
	 * This method is for initializes Router Context and loads Diameter Dictionary
	 */
	@BeforeClass
	public static void init() {
		DummyDiameterDictionary.getInstance();
	}
	
	/**
	 * Test Diameter Answer handling
	 */
	@Test
	public void testAnswerPacketPassed() {
		DiameterAnswer answer = new DiameterAnswer();
		byte [] reqBytesBefore = answer.getBytes();
		
		try {
			explicitRoutingHandler.handle(answer);
			byte[] reqBytesAfter = answer.getBytes();
			
			assertTrue("Answer must not  be modified bt ER Handler", 
					Arrays.equals(reqBytesBefore, reqBytesAfter));
			
		} catch (ExplicitRoutingFailedException e) {
			fail("Explicit Routing Handling not proper, it should not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * Test when ER Path AVP not Found
	 * =>  Must not perform ER, hence do not edit request
	 */
	@Test
	public void testExplicitPathAVPNotFound() {
		DiameterRequest request = createRequest("example.com", "example.net", null);
		byte [] reqBytesBefore = request.getBytes();
		
		try {
		
			explicitRoutingHandler.handle(request);
			byte[] reqBytesAfter = request.getBytes();
			
			assertTrue("Request not expected to be modified if ER Path AVP does not exists", 
					Arrays.equals(reqBytesBefore, reqBytesAfter));
		
		} catch (ExplicitRoutingFailedException e) {
			fail("Explicit Routing Handling not proper, it should not result in Exception" + e.getMessage());
		}
	}
	
	
	/**
	 * Test when ER Path AVP present but contains no Records
	 * => Exception with Result Code = 4501
	 */
	@Test
	public void testExpPathRecordsAvailability() {
		DiameterRequest request = createRequest("example.com", "example.net", null);
		IDiameterAVP expPathAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		request.addAvp(expPathAvp);
		try {
			explicitRoutingHandler.handle(request);
			fail("It should result in Exception with Result-Code " + ResultCode.DIAMETER_ER_NOT_AVAILABLE 
					+ " as Empty Explicit Path AVP is Sent");
			
		} catch (ExplicitRoutingFailedException e) {
			assertEquals("Must Send " + ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
					e.getResultCode(), ResultCode.DIAMETER_ER_NOT_AVAILABLE);
		}
	}
	
	/**
	 * Dest Host Not Available --> Must Add Own Record as On Going Discovery is Found
	 */
	@Test
	public void testOnGoingDiscoveryW_oDestHost() {
		DiameterRequest request = createRequest("example.com", "example.net", null);
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_REALM));
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		int noPathRecords = expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD).size(); 
		try {
			explicitRoutingHandler.handle(request);
			expPathAvp = (AvpGrouped) request.getAVP(DiameterAVPConstants.HW_ELIPLICIT_PATH);
			assertEquals("Successfull Ongoing Discovery, Must Append One Record", 
					expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD).size(), noPathRecords + 1);
			
			expPathRecord = (AvpGrouped) expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD).get(1);
			
			proxyAvp = expPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_HOST);
			assertNotNull("Proxy Host must not be null", proxyAvp);
			assertEquals("Own Proxy-Host value MUST be added", proxyAvp.getStringValue(), Parameter.getInstance().getOwnDiameterIdentity());

			proxyAvp = expPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_REALM);
			assertNotNull("Proxy Realm must not be null", proxyAvp);
			assertEquals("Own Proxy-Realm value MUST be added", proxyAvp.getStringValue(), Parameter.getInstance().getOwnDiameterRealm());

		} catch (ExplicitRoutingFailedException e) {
			fail("Must not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	
	/**
	 * Own Record Available but Not First --> Must Result in Result Code 3501 (Invalid Proxy Path Stack)
	 */
	@Test
	public void testOwnPathRecordFoundAndNotFirst() {
		DiameterRequest request = createRequest("example.com", "example.net", "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Other Record Dummy
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("ER-Proxy.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Own Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			fail("Must Result in ExplicitRoutingFailedException with Result-Code" + ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK);

		} catch (ExplicitRoutingFailedException e) {
			assertEquals("Own record found but Not First, Must Send "+ ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK, 
					ResultCode.DIAMETER_INVALID_PROXY_PATH_STACK, e.getResultCode());
		}
	}
	
	
	/**
	 * Test: Own Path Record is available so must be popped without Disturbing other Records.
	 */
	@Test
	public void testOwnPathRecordPopped() {
		DiameterRequest request = createRequest("example.com", "example.net", "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		int noPathRecords = expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD).size(); 
		
		try {
			explicitRoutingHandler.handle(request);
			
			expPathAvp = (AvpGrouped) request.getAVP(DiameterAVPConstants.HW_ELIPLICIT_PATH);
			List<IDiameterAVP> explicitRecords = expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
			assertEquals("Own record Found, So must Pop Top Record", explicitRecords.size(), noPathRecords - 1);
			
			expPathRecord = (AvpGrouped) explicitRecords.get(0);
			assertEquals("Must Not edit Proxy Host in Other Exp Path Records", 
					expPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_HOST).getStringValue()	, "Er-Destination.example.net");
			
			assertEquals("Must Not edit Proxy Realm in Other Exp Path Records", 
					expPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_REALM).getStringValue()	, "example.net");

		} catch (ExplicitRoutingFailedException e) {
			fail("Must not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 *  This test validated Populated Destination AVPs from Next Path Record, 
	 *  Request has Both Dest. Host & Realm
	 */
	@Test
	public void testPopulatedDestAVPsBothAvailable() {
		DiameterRequest request = createRequest("example.com", "example.net", "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Proxy.example.com");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.co");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			
			assertEquals("Destination Realm Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_REALM), "example.co");
			
			assertEquals("Destination Host Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_HOST), "Er-Proxy.example.com");
			
			assertEquals("Must Have only One Dest-Host AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_HOST).size() , 1);

			assertEquals("Must Have only One Dest-Realm AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_REALM).size() , 1);

		} catch (ExplicitRoutingFailedException e) {
			fail("Must Not Cause Exception, " + e.getMessage());
		}
	}
	/**
	 * This test validated Populated Destination AVPs from Next Path Record, 
	 * Request has only Dest Realm
	 * Dest-Host should be added and Dest-Realm Should be replaced
	 * 
	 */
	@Test
	public void testPopulatedDestAVPsNoDestHost() {
		DiameterRequest request = createRequest("example.com", "example.net", null);
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Proxy.example.com");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.co");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			
			assertEquals("Destination Realm Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_REALM), "example.co");
			
			assertEquals("Destination Host Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_HOST), "Er-Proxy.example.com");

			assertEquals("Must Have only One Dest-Host AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_HOST).size() , 1);

			assertEquals("Must Have only One Dest-Realm AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_REALM).size() , 1);

		} catch (ExplicitRoutingFailedException e) {
			fail("Must Not Cause Exception: " + e.getMessage());
		}
	}
	
	/**
	 * This test validated Populated Destination AVPs from Next Path Record, 
	 * Request has Both Dest. Host & Realm
	 */
	@Test
	public void testPopulatedDestAVPsNoDestRealm() {
		DiameterRequest request = createRequest("example.com", null, "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Proxy.example.com");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.co");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			
			assertNull("Destination Realm should not be Populated as it is not present in Original Request", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_REALM));
			
			assertEquals("Destination Host Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_HOST), "Er-Proxy.example.com");
			
			assertEquals("Must Have only One Dest-Host AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_HOST).size() , 1);

		} catch (ExplicitRoutingFailedException e) {
			fail("Must Not Cause Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This test validated Populated Destination AVPs from Next Path Record, 
	 * Request does not have Dest. Host & Realm
	 */
	@Test
	public void testPopulatedDestAVPsNoDestAVP() {
		DiameterRequest request = createRequest("example.com", null, null);
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Proxy.example.com");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.co");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			
			assertNull("Destination Realm Not  provided in Request, hence should not be populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_REALM));
			
			assertEquals("Destination Host Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_HOST), "Er-Proxy.example.com");
			
			assertEquals("Must Have only One Dest-Host AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_HOST).size() , 1);

		} catch (ExplicitRoutingFailedException e) {
			fail("Must Not Cause Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This test validates Populated Destination AVPs from Next Path Record into Request, 
	 * Next Path Record does not Have Proxy Host 
	 * So must return with Result Code =4501(Dia ER Not Available)
	 */
	@Test
	public void testPopulatedDestAVPsNoProxyHost() {
		DiameterRequest request = createRequest("example.com", null, null);
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.co");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			fail("Must Result in Exception with Result Code " + ResultCode.DIAMETER_ER_NOT_AVAILABLE);

		} catch (ExplicitRoutingFailedException e) {
			assertEquals(" As Proxy Host Missing, Must Send " + ResultCode.DIAMETER_ER_NOT_AVAILABLE, 
					e.getResultCode(), ResultCode.DIAMETER_ER_NOT_AVAILABLE);
		}
	}
	
	/**
	 * This test validated Populated Destination AVPs from Next Path Record into Request, 
	 * Next Path Record does not Have Proxy Realm
	 * 
	 * This will not Edit Dest Realm
	 */
	@Test
	public void testPopulatedDestAVPsNoProxyRealm() {
		DiameterRequest request = createRequest("example.com", Parameter.getInstance().getOwnDiameterRealm(), Parameter.getInstance().getOwnDiameterIdentity());
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Proxy.example.com");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			assertEquals("Destination Realm should not Change", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_REALM), Parameter.getInstance().getOwnDiameterRealm());
			
			assertEquals("Destination Host Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_HOST), "Er-Proxy.example.com");

			assertEquals("Must Have only One Dest-Host AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_HOST).size() , 1);

			assertEquals("Must Have only One Dest-Realm AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_REALM).size() , 1);
			
		} catch (Exception e) {
			fail("As Proxy-Realm is Optional AVP Must Not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This test validated Populated Destination AVPs from Next Path Record into Request, 
	 * Next Path Record does not Have Proxy Host ///
	 * Request Does not have Dest Realm
	 * 
	 * This will not add Dest Realm AVP
	 */
	@Test
	public void testPopulatedDestAVPsNoProxyRealmNoDestRealm() {
		DiameterRequest request = createRequest("example.com", null, Parameter.getInstance().getOwnDiameterIdentity());
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		//Adding Own Record
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Other Record Dummy
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Proxy.example.com");
		expPathRecord.addSubAvp(proxyAvp);
		expPathAvp.addSubAvp(expPathRecord);
		
		//Adding Dest Record
		expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			assertNull("Destination Realm should not be added", 
					request.getAVP(DiameterAVPConstants.DESTINATION_REALM));
			
			assertEquals("Destination Host Not Properly Populated", 
					request.getAVPValue(DiameterAVPConstants.DESTINATION_HOST), "Er-Proxy.example.com");

			assertEquals("Must Have only One Dest-Host AVP", 
					request.getAVPList(DiameterAVPConstants.DESTINATION_HOST).size() , 1);

		} catch (Exception e) {
			fail("As Proxy-Realm is Optional AVP Must Not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This is Case of Ongoing Discovery, but Proxy Host is Missing
	 * ResultCode = DIAMETER_ER_NOT_AVAILABLE(4501)
	 */
	@Test
	public void testOnGoingDiscoveryProxyHostMissing() {
		
		DiameterRequest request = createRequest("example.com", "example.net", "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_REALM));
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			fail("It should result in Exception with Result-Code " + ResultCode.DIAMETER_ER_NOT_AVAILABLE 
					+ " asProxy-Host is Not Provided.");	
		} catch (ExplicitRoutingFailedException e) {
			assertEquals("Must Send " + ResultCode.DIAMETER_ER_NOT_AVAILABLE, e.getResultCode(), ResultCode.DIAMETER_ER_NOT_AVAILABLE);
		}
	}
	
	/**
	 * This is Case of Ongoing Discovery, given is Destination Host
	 * So Own Path Record MUST be added
	 * 
	 */
	@Test
	public void testOnGoingDiscoveryWithDestHost() {
		
		DiameterRequest request = createRequest("example.com", "example.net", "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_REALM));
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		try {
			explicitRoutingHandler.handle(request);
			expPathAvp = (AvpGrouped) request.getAVP(DiameterAVPConstants.HW_ELIPLICIT_PATH);
			List<IDiameterAVP>  explicitPathRecords = expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);

			assertEquals("Exp Path must contain two path records", explicitPathRecords.size(), 2);
			
			expPathRecord = (AvpGrouped) explicitPathRecords.get(explicitPathRecords.size() -1);
			assertEquals("Own Path Record Must Contain Only Two AVPs", expPathRecord.getGroupedAvp().size(), 2);
			
		} catch (ExplicitRoutingFailedException e) {
			fail("Must Not Result In Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This tests Discovered Route, but we are not present in Route
	 * 
	 * --> Must not edit Packet
	 */
	@Test
	public void testDiscoverdAlready() {
		
		DiameterRequest request = createRequest("example.com", "example.net", "Er-Destination.example.net");
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue("Er-Destination.example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue("example.net");
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		
		byte [] reqBytesBefore = request.getBytes();
		
		try {
			explicitRoutingHandler.handle(request);
			byte[] reqBytesAfter = request.getBytes();
			
			assertTrue("As Path is Discovered and We are not Included in it " +
					"Length of Bytes before after process should be same", 
					Arrays.equals(reqBytesBefore, reqBytesAfter));
			
		} catch (ExplicitRoutingFailedException e) {
			fail("Path Diacovered and Not participated not proper handled, it should not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * Discovery Mode detected --> Validate Own Proxy AVPs 
	 */
	@Test
	public void testOwnRecordProxyHostAdded() {
		DiameterRequest request = createRequest("example.com", "example.net", null);
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		AvpGrouped expPathRecord = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		
		IDiameterAVP proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_HOST);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		expPathRecord.addSubAvp(proxyAvp);
		
		proxyAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_PROXY_REALM);
		proxyAvp.setStringValue(request.getAVPValue(DiameterAVPConstants.ORIGIN_REALM));
		expPathRecord.addSubAvp(proxyAvp);
		
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);
		try {
			explicitRoutingHandler.handle(request);
			
			expPathAvp = (AvpGrouped) request.getAVP(DiameterAVPConstants.HW_ELIPLICIT_PATH);
			List<IDiameterAVP>  explicitPathRecords = expPathAvp.getSubAttributeList(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
			expPathRecord = (AvpGrouped) explicitPathRecords.get(explicitPathRecords.size() -1);
			
			assertEquals(explicitPathRecords.size(), 2);
			proxyAvp = expPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_HOST);
			assertNotNull("Proxy Host must not be null", proxyAvp);
			assertEquals("Own Proxy-Host value MUST be added", proxyAvp.getStringValue(), Parameter.getInstance().getOwnDiameterIdentity());
			
			proxyAvp = expPathRecord.getSubAttribute(DiameterAVPConstants.HW_PROXY_REALM);
			assertNotNull("Proxy Realm must not be null", proxyAvp);
			assertEquals("Own Proxy-Realm value MUST be added", proxyAvp.getStringValue(), Parameter.getInstance().getOwnDiameterRealm());
			
		} catch (ExplicitRoutingFailedException e) {
			fail("Must not result in Exception, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * Test when ER Path Record AVP present but is Empty
	 * => Exception with Result Code = 4501
	 */
	@Test
	public void testEmptyExpPathRecords() {
		DiameterRequest request = createRequest("example.com", "example.net", null);
		AvpGrouped expPathAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH);
		IDiameterAVP expPathRecord = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HW_ELIPLICIT_PATH_RECORD);
		expPathAvp.addSubAvp(expPathRecord);
		request.addAvp(expPathAvp);

		try {
			explicitRoutingHandler.handle(request);
			fail("It should result in Exception with Result-Code: "+ResultCode.DIAMETER_ER_NOT_AVAILABLE+
					" as Empty Explicit Path AVP is Sent");	
			
		} catch (ExplicitRoutingFailedException e) {
			assertEquals("Must Send " + ResultCode.DIAMETER_ER_NOT_AVAILABLE, e.getResultCode(), ResultCode.DIAMETER_ER_NOT_AVAILABLE);
		}
	}
	
	/**
	 * 
	 * @param originRealm is Origin Realm in Request
	 * @param ip is IP address obtained form connection handler.
	 * 				Here as we do not have live Request, explicitly adding Info AVP EC_SOURCE_IP_ADDRESS.
	 * @param destRealm is Dest Realm in Request
	 * @param authAppID is Auth Application ID of BASE in Request
	 * @param commandCode  in Request
	 * @return DiameterRequest
	 */
	private DiameterRequest createRequest(String originRealm, String destRealm, String destHost){
		DiameterRequest diameterPacket = new DiameterRequest(false);

		//Origin Host
		IDiameterAVP originHostAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ORIGIN_HOST);
		originHostAvp.setStringValue("ER-Originator."+originRealm);
		diameterPacket.addAvp(originHostAvp);

		//Origin Realm
		IDiameterAVP originRealmAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ORIGIN_REALM);
		originRealmAVP.setStringValue(originRealm);
		diameterPacket.addAvp(originRealmAVP);

		//Destination realm
		if(destRealm != null){
			IDiameterAVP destinationRealmAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.DESTINATION_REALM);
			destinationRealmAVP.setStringValue(destRealm);
			diameterPacket.addAvp(destinationRealmAVP);
		}
		
		//Destination realm
		if(destHost != null){
			IDiameterAVP destinationHostAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.DESTINATION_HOST);
			destinationHostAVP.setStringValue(destHost);
			diameterPacket.addAvp(destinationHostAVP);
		}

		//Adding Application ID..
		IDiameterAVP applicationIDAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.AUTH_APPLICATION_ID);
		applicationIDAVP.setStringValue("4");
		diameterPacket.addAvp(applicationIDAVP);
		diameterPacket.setApplicationID(4);
		diameterPacket.setCommandCode(272);
		return diameterPacket;
	}

}
