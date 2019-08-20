package com.elitecore.aaa.core.diameter.conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.conf.impl.RoutingEntryDataImpl;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
/**
 * JUnit Test cases for {@link RoutingEntry}
 * 
 * @author monica.lulla
 *
 */
public class RoutingEntryTest {

	private static final int INVALID_FAILURE_ACTION = 10;
	private static RouterContext routerContext = null;
	private DiameterPacket diameterPacket = null;
	private RoutingEntry rountingEntry;
	
	@Mock private ITranslationAgent translationAgent;
	@Rule public ExpectedException thrown = ExpectedException.none();

	/**
	 * This method is for initializes Router Context and loads Diameter Dictionary
	 */
	@BeforeClass
	public static void init() {
		routerContext = createRouterContext();		
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	/**
	 * 
	 * @param realm is Origin Realm in Packet
	 * @param ip is IP address obtained form connection handler.
	 * 				Here as we do not have live packet, explicitly adding Info AVP EC_SOURCE_IP_ADDRESS.
	 * @param destRealm is Dest Realm in Packet
	 * @param authAppID is Auth Application ID of BASE in Packet
	 * @param commandCode  in Packet
	 * @return Diameter Packet
	 */
	private DiameterPacket createPacket(String ip, String realm, String destRealm, 
			String authAppID, int commandCode){
		diameterPacket = new DiameterRequest(false);

		//Origin Host
		IDiameterAVP originHostAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ORIGIN_HOST);
		originHostAvp.setStringValue("peer1."+realm);
		diameterPacket.addAvp(originHostAvp);

		//Origin Realm
		IDiameterAVP originRealm = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ORIGIN_REALM);
		originRealm.setStringValue(realm);
		diameterPacket.addAvp(originRealm);

		//Origin IP Address
		IDiameterAVP sourceIpAddr = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SOURCE_IP_ADDRESS);
		sourceIpAddr.setStringValue(ip);
		diameterPacket.addInfoAvp(sourceIpAddr);

		//Destination realm
		IDiameterAVP destinationRealmAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.DESTINATION_REALM);
		destinationRealmAVP.setStringValue(destRealm);
		diameterPacket.addAvp(destinationRealmAVP);

		//Adding Application ID..
		IDiameterAVP applicationIDAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.AUTH_APPLICATION_ID);
		applicationIDAVP.setStringValue(authAppID);
		diameterPacket.addAvp(applicationIDAVP);
		diameterPacket.setApplicationID(Integer.parseInt(authAppID));
		diameterPacket.setCommandCode(commandCode);
		return diameterPacket;
	}

	/**
	 * This method represents a success test case with proper values
	 * and MUST Not Fail.
	 */
	@Test
	public void testSuccessCase(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Success_RE1", 
					"0:283=\"elitecore.com\"", "*example.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "1" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "12071" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents RE with * in destination Realm 
	 * and MUST Not Fail.
	 */
	@Test
	public void testDestinationRealms(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("DestRealm_RE", 
					"0:283=\"*\"", "*example.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "*") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "eliteaaa.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "example.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123xmple.com" , "example.com" , "4" , 272 )) == false);
		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents RE with NULL in destination Realm 
	 * and MUST Not Fail.
	 */
	@Test
	public void testDestinationRealmNull(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("DestRealm_RE", 
					"0:283=\"*\"", "*example.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, null) , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "eliteaaa.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "example.com" , "4" , 272 )));
		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents RE with NULL in origin Host IP 
	 * and MUST Not Fail.
	 */
	@Test
	public void testOriginHostIPNull(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("DestRealm_RE", 
					"0:283=\"*\"", "*example.com", null, "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, null) , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "eliteaaa.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "example.com" , "4" , 272 )));
		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents RE with NULL in origin Realm 
	 * and MUST Not Fail.
	 */
	@Test
	public void testOriginRealmNull(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("DestRealm_RE", 
					"0:283=\"*\"", null, "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "*") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "eliteaaa.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "example.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123xmple.com" , "example.com" , "4" , 272 )));
		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * Multiple comma separated Dest Realms.
	 * and MUST Not Fail.
	 */
	@Test
	public void testDestRealmMultiple1(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Comma_Dest_Relams_RE", 
					"0:283=\"*\"", "*example.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "eliteaaa.com , elitecore.com, example.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "eliteaaa.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elit.com" , "4" , 272 )) == false);

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	
	/**
	 * Multiple semicolon separated Dest Realms.
	 * and MUST Not Fail.
	 */
	@Test
	public void testDestRealmMultiple2(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Semicolon_Dest_Relams_RE", 
					"0:283=\"*\"", "*example.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "elitecore.com ; eliteaaa.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "eliteaaa.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elite.com" , "4" , 272 )) == false);

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * Expression in Dest Realm
	 * and MUST Not Fail.
	 */
	@Test
	public void testDestRealmExp(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Exp_Dest_Relams_RE", 
					null, "*example.com", "10.106.1.120", "1 ,4 , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "*elitecore.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "123elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "*elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.comelitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.comelitecore" , "4" , 272 )) == false);
		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents a success test case with Origin Realm as * (ANY)
	 * and MUST Not Fail.
	 */
	@Test
	public void testOriginRealm(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Origin_Relams_RE", 
					null, "*", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "elitecore.com" , "elitecore.com" , "4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * Comma and Semi-colon separated  origin Realm
	 * and MUST Not Fail.
	 */
	@Test
	public void testOriginRealmMultiple(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Multiple_Origin_Relams_RE", 
					"0:283=\"elitecore.com\"", "123example.com , elitecore.com ; 123.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "elitecore.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "456.com" , "elitecore.com" , "4" , 272 )) == false);

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * Comma and Semi-colon separated  origin Realm with Expressions
	 * and MUST Not Fail.
	 */
	@Test
	public void testOriginRealmMultipleExp(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Exp_origin_Relams_RE", 
					"0:283=\"elitecore.com\"", "*example.com , elite*.com ; 123.com", "10.106.1.120", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "elitecore.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "eliteaaa.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123.com" , "elexampl.com" , "4" , 272 )) == false);

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
     * This method represents a failure test case with improper values
     * and must not Fail but will display Warning Messages.
     */
    @Test
    public void testFailureCase(){
        RoutingEntry failureRoutingEntry = null;
        try{
            failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failure_RE",
                    "0:283=\"*\"", "example.com", "10.106.1.120",
                    "0:1 , -676:-3003 , 1010101010101:39939393939 ,invalid_App_Id ,  , : , invalid_Vendor_Id:12071",
                    DiameterFailureConstants.REDIRECT.failureAction,
                    "5001, -3003 , 78798789789798 ,   , ihj78", "0:293 = 0.0.0.0 , 0:261 = 100 , 0: , 00 = 19 , 990kjk: 90 = 90jklm", "elitecore.com") , routerContext, translationAgent);
            failureRoutingEntry.init();
            assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
                    failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
                            "4" , 272 )) == false);

        }catch(Exception e){
            e.printStackTrace();
            fail("Success Test Case failed. Reason: " + e.getMessage());
        }

    }
    
	/**
	 * Comma and Semi-colon separated  origin Host ip
	 * and MUST Not Fail.
	 */
	@Test
	public void testOriginHost(){
		RoutingEntry successRoutingEntry = null;
		try{
			successRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Origin_IP_RE", 
					"0:283=\"elitecore.com\"", "*example.com", "10.106.1.120, 10.106.1.10 ; 10.106.1.24", "1 ,4  , 12071", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			successRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.120", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.10", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.24", "123example.com" , "elitecore.com" , "4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					successRoutingEntry.isApplicable(createPacket("10.106.1.z", "123example.com" , "elitecore.com" , "4" , 272 )) == false);

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents a test case with Proper values
	 * for Failover Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testFailoverInit(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "1 , 4", 
					DiameterFailureConstants.FAILOVER.failureAction, "3003", "0.0.0.0", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with Proper values
	 * for Redirect Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testRedirectInit(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Redirect_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "1 , 4", 
					DiameterFailureConstants.REDIRECT.failureAction, "3003", 
					"0:292 = 255.255.255.255 , 0:262 = 2, 0:263 = 200", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", 
							"example.com" , "elitecore.com" , "4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with Proper values
	 * for Redirect Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testRedirectInit_WO_RedirectHost(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.REDIRECT.failureAction, "3003", " 0:263 = 1000, 0:262 = 2 ", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with Improper values
	 * for Redirect Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testRedirectInitInvalidAVPId(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Redirect_AVPId_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "1 , 4", 
					DiameterFailureConstants.REDIRECT.failureAction, "3003", "0: = jkljkl, 0:263 = 1000, 0:262 = 2 ", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with improper values
	 * for Redirect Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testRedirectInitAVPValueBlank(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Redirect_AVPId_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.REDIRECT.failureAction, "3003", "0:292=   , 0:263 = 1000, 0:262 = 2 ", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with improper values
	 * for Redirect Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testRedirectInitNull(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Redirect_AVPId_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.REDIRECT.failureAction, "3003", null, "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with Proper values
	 * for PassThrough Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testPassThroughInit(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "1 , 4", 
					DiameterFailureConstants.PASSTHROUGH.failureAction, "3003", null, "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with Null Trans mapping
	 * for Translate Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testTranslateInitNull(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.TRANSLATE.failureAction, "3003", null, "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with Blank Trans mapping
	 * for Translate Failure Action <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testTranslateInitBlank(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.TRANSLATE.failureAction, "3003", "			", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}
	}
	
	/**
	 * This method represents a test case with improper values
	 * for Failover Failure Handler <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testFailoverInitNull(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_Null_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.FAILOVER.failureAction, "3003", null, "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents a test case with improper values
	 * for Failover Failure Handler <code>init()</code> method
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testFailoverInitBlank(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Failover_Blank_RE", 
					"0:283=\"*\"", "example.com", "10.106.1.120", "0:1 , 4", 
					DiameterFailureConstants.FAILOVER.failureAction, "3003", "   ", "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents a failure test case with improper values
	 * and must not Fail but will display Warning Messages.
	 */
	@Test
	public void testAllNullCase(){
		RoutingEntry failureRoutingEntry = null;
		try{
			failureRoutingEntry = new RoutingEntry(getRoutingEntryDataValues("Null_RE", 
					null, "example.com", "10.106.1.120", null, DiameterFailureConstants.PASSTHROUGH.failureAction , "2001", null, "elitecore.com") , routerContext, translationAgent);
			failureRoutingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					failureRoutingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));

		}catch(Exception e){
			e.printStackTrace();
			fail("Success Test Case failed. Reason: " + e.getMessage());
		}

	}
	
	/**
	 * This method represents test case for Advanced Condition / RuleSet.
	 * As this takes in Buggy Input, this method also test that
	 * <code>routingEntry.init()</code> must not add appropriate Warning Message.
	 * 
	 */
	@Test
	public void testSuccessAdvancedCondition(){
		RoutingEntry routingEntry = null;
		try {
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Success_Adv_Cond_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "1 ,4  , 12399", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elite.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			fail("Handling of Advanced Condition. It must not throw Exception");
		}		
	}

	/**
	 * This method represents test case for Null Advanced Condition / RuleSet.
	 * As this takes in Buggy Input, this method also test that
	 * <code>routingEntry.init()</code> must add appropriate Warning Message.
	 * 
	 */
	@Test
	public void testNullAdvancedCondition(){
		RoutingEntry routingEntry = null;
		try {
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Null_Adv_Cond_RE", 
					null, "example.com", "10.106.1.120", "1 ,4  , 12399", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));
		} catch (Exception e) {
			fail("Handling of Null Advanced Condition is not proper. It must not throw Exception");
		}		
	}

	/**
	 *  This method represents test case for Application ID invalid and Vendor ID valid.
	 * <br />As this takes in Buggy Input, this method also test that
	 * <code>routingEntry.init()</code> must add appropriate Warning Message.
	 * 
	 */
	@Test
	public void testInvalidAppID(){
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "App_Id", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			fail("Handling of Application ID is not proper. It must not throw Exception");
		}	
	}

	/**
	 *  This method represents test case for Application ID invalid and Vendor ID valid.
	 * <br />As this takes in Buggy Input, this method also test that
	 * <code>routingEntry.init()</code> must add appropriate Warning Message.
	 * 
	 */
	@Test
	public void testMultipleInvalidAppID(){
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Multiple_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1 , 4, 0:7897J , ", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry is Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));
		}catch (Exception e) {
			fail("Handling of Application ID is not proper. It must not throw Exception");
		}	
	}


	/**
	 *  This method represents test case for valid Application ID String.
	 */
	@Test
	public void testSuccessAppID(){
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Success_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "1 ,4  , 12399", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"1" , 272 )));
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"12399" , 272 )));
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Null Application ID is not proper, It should not throw Initialization Failed Exception");
		}
	}

	/**
	 *  This method represents test case for Negative Application ID.
	 * <br />As this takes in Negative, this method also test that
	 * <code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testNegativeAppID(){
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Negetive_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "-10001", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Negetive Application ID is not proper, It should not throw Exception");
		}
	}

	/**
	 *  This method represents test case for blank values in Application ID.
	 *  This will result in NOT NULL Application ID String but must not parse Application Ids.
	 *  <br />
	 * As this takes in Buggy Input, this method also test that
	 * <code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testBlankAppID(){
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Blank_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", " , ", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Blank Application ID is not proper, It should not throw Initialization Failed Exception");			
		}	
	}
	
	
	/**
	 *  This method represents test case for blank values in Application ID.
	 *  This will result in NOT NULL Application ID String but must not parse Application Ids.
	 *  <br />
	 * As this takes in Buggy Input, this method also test that
	 * <code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testBlankAppID2(){
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Blank_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "  ", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == true);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Blank Application ID is not proper, It should not throw Initialization Failed Exception");			
		}	
	}

	/**
	 *  This method represents test case for NULL value in Application ID.<br />
	 * As this takes in Null Advanced Condition, hence there will be routing Entry
	 * but will not be applicable for the any Diameter Packet 
	 * and must add appropriate Warning Message.
	 */
	@Test
	public void testNullAppID() {
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Null_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", null, 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();			
			assertTrue("As Application Id is Null, Routing Entry Must be applicable",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )));

		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Null Application ID is null, It should not throw Exception");
		}	
	}
	
	/**
	 * This Tests magic no 0
	 */
	@Test
	public void testZeroAppID() {
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("ZEro_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();			
			assertTrue("As Application Id is 0, Routing Entry Must be applicable",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )));
			assertNull("As Application Id is 0, App Enum List must be Null", routingEntry.getSupportedApplications());

		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Null Application ID is null, It should not throw Exception");
		}	
	}
	
	/**
	 * This tests Invalid Token AppId 0:4:9
	 */
	@Test
	public void testExcessTokenAppID() {
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Excess_token_App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:4:9", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Invalid Application Id MUST not be Applicable",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Invalid Application ID must not fail, It should not throw Exception");
		}	
	}
	
	
	/**
	 * This tests AppId not Satisfied
	 */
	@Test
	public void testApplicationNotSatisfied() {
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("App_Id_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "5", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Invalid Application Id MUST not be Applicable",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Application ID must not fail, It should not throw Exception");
		}	
	}
	
	
	/**
	 * This tests Ruleset not Satisfied
	 */
	@Test
	public void testRuleSetNotSatisfied() {
		RoutingEntry routingEntry = null;
		try{
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Ruleset_RE", 
					"0:283=\"elitecore.com\"", "*", "*", "0", 
					DiameterFailureConstants.PASSTHROUGH.failureAction	 , "5001, 3003 , 3000", null, "*") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Invalid Application Id MUST not be Applicable",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "eliteaaa.com" ,
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Ruleset must not fail, It should not throw Exception");
		}	
	}


	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is invalid 
	 * hence default Failure Action, i.e. PASSTHOUGH must be set for ErrorCode 
	 * configured for this Entry.<br />
	 * Besides, This test case checks <code>routingEntry.isApplicable</code> method,
	 * such that Routing Entry should be not applicable to assigned Diameter Packet 
	 * as invalid Failure Handler
	 */
	@Test
	public void testFailureConf_Case1(){
		RoutingEntry routingEntry = null;
		try{
			//testing Invalid Failure Action and Valid Error Code
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Fail_Conf_RE", 
					"0:283=\"elitecore.com\"", "*", "*", "1 , 4", 
					INVALID_FAILURE_ACTION	 , "3003", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));
		}catch(Exception e){	
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		} 

	}

	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is invalid 
	 * hence default Failure Action, i.e. PASSTHOUGH must be set for ErrorCode List 
	 * configured for this Entry.<br />
	 * Besides, This test case checks <code>routingEntry.isApplicable</code> method,
	 * such that Routing Entry should be applicable to assigned Diameter Packet 
	 * as all the criteria match for the assigned Diameter Packet.
	 * 
	 * 
	 */
	@Test
	public void testFailureConf_Case2(){
		RoutingEntry routingEntry = null;
		try{
			//testing Invalid Failure Action and Valid Error Code List
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Fail_Conf_RE", 
					"0:283=\"elitecore.com\"", "*", "*", "1, 4", 
					INVALID_FAILURE_ACTION	 , "3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();
			assertTrue("Routing Entry must  be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )));
		}catch(Exception e){	
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}

	}

	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is Valid and ErrorCode is invalid, 
	 * hence <code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testFailureConf_Case3() throws InitializationFailedException{
		RoutingEntry routingEntry = null;
		try{
			//testing Valid Failure Action and Invalid Error Code
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Fail_Conf_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1", 
					DiameterFailureConstants.DROP.failureAction, "3klkj003", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();			
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" ,
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}	
	}

	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is Valid and ErrorCode List is provided containing all malformed values
	 * hence <code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testFailureConf_Case4() {
		try{
			RoutingEntry routingEntry = null;
			//testing Invalid Failure Action and Invalid Error Code List
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Fail_Conf_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1", 
					DiameterFailureConstants.FAILOVER.failureAction, "3klkj003 , jkljk908", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();			
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}	
	}

	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is Valid and ErrorCode List is provided containing some malformed values
	 * hence<code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testFailureConf_Case5(){
		RoutingEntry routingEntry = null;
		try{
			//testing Invalid Failure Action and Partially Correct Error Code List
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Success_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1", 
					DiameterFailureConstants.REDIRECT.failureAction, "3003 , jlkjlk0978", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}	
	}
	
	
	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is Valid and ErrorCode List is provided containing some negetive values
	 * hence<code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testNegativeErrorCode(){
		RoutingEntry routingEntry = null;
		try{
			//testing Invalid Failure Action and Partially Correct Error Code List
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Negetive_Error_Code_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1", 
					DiameterFailureConstants.REDIRECT.failureAction, "-3003 , 3000", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}	
	}
	
	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is Valid and ErrorCode in NULL
	 * hence<code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testNullErrorCode(){
		RoutingEntry routingEntry = null;
		try{
			//testing Invalid Failure Action and Partially Correct Error Code List
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Null_Error_Code_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1", 
					DiameterFailureConstants.REDIRECT.failureAction, null, null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}	
	}
	
	/**
	 * This method represents test case for Failure Configuration.<br />
	 * Case: Failure Action Value is Valid and ErrorCode List is Blank
	 * hence<code>routingEntry.init()</code> must add appropriate Warning Message.
	 */
	@Test
	public void testBlankErrorCode(){
		RoutingEntry routingEntry = null;
		try{
			//testing Invalid Failure Action and Partially Correct Error Code List
			routingEntry = new RoutingEntry(getRoutingEntryDataValues("Blank_Error_Code_RE", 
					"0:283=\"elitecore.com\"", "example.com", "10.106.1.120", "0:1", 
					DiameterFailureConstants.REDIRECT.failureAction, "  ,  ", null, "elitecore.com") , routerContext, translationAgent);
			routingEntry.init();	
			assertTrue("Routing Entry must not be Applicable to given Diameter Packet",
					routingEntry.isApplicable(createPacket("10.106.1.120", "example.com" , "elitecore.com" , 
							"4" , 272 )) == false);	
		}catch (Exception e) {
			e.printStackTrace();
			fail("Handling of Buggy Failure Configuration is not proper"  + e.getMessage());
		}	
	}

	@Test
	public void isRoutingEntryExecutableReturnsFalseWhenConfiguredApplicationIdLessThanZero() throws InitializationFailedException {

		rountingEntry = new RoutingEntry(getRoutingEntryDataValues("routing_entry_name", "0:283=\"elitecore.com\"", 
				"example.com", "10.106.10.120", "-1", DiameterFailureConstants.RECORD.failureAction, "3000", 
				"diameter_accounting_driver", "elitecore.com"), routerContext, translationAgent);
		rountingEntry.init();
		assertRoutingEntryIsNotExecutable();
	}

	@Test
	public void isRoutingEntryExecutableReturnsFalseWhenConfiguredApplicationIdIsInValid() throws InitializationFailedException {

		rountingEntry = new RoutingEntry(getRoutingEntryDataValues("routing_entry_name", "0:283=\"elitecore.com\"", 
				"example.com", "10.106.10.120", "abc", DiameterFailureConstants.RECORD.failureAction, "3000", 
				"diameter_accounting_driver", "elitecore.com"), routerContext, translationAgent);
		rountingEntry.init();
		assertRoutingEntryIsNotExecutable();

	}

	@Test
	public void isRoutingEntryExecutableReturnsFalseWhenConfiguredFailureArgumentIsInvalid() throws InitializationFailedException {

		rountingEntry = new RoutingEntry(getRoutingEntryDataValues("routing_entry_name", "", 
				"example.com", "10.106.10.120", "1", DiameterFailureConstants.RECORD.failureAction, "3000", 
				"", "elitecore.com"), routerContext, translationAgent);
		rountingEntry.init();
		assertRoutingEntryIsNotExecutable();
	}

	@Test
	public void isRoutingEntryExecutableReturnsFalseWhenInvalidRuleSetIsConfiguredInRoutingPeerConfiguration() throws InitializationFailedException {

		RoutingEntryDataImpl routingEntryDataImpl = getRoutingEntryDataValues("routing_entry_name","0:283=\"elitecore.com\"", 
				"example.com", "10.106.10.120", "1", DiameterFailureConstants.RECORD.failureAction, "3000", 
				"driver_name", "elitecore.com");
		List<PeerGroupImpl> peerGroupImpls = routingEntryDataImpl.getPeerGroupList();
		PeerGroupImpl peerGroupImpl = peerGroupImpls.get(0);
		peerGroupImpl.setAdvancedConditionStr("*");
		rountingEntry = new RoutingEntry(routingEntryDataImpl, routerContext, translationAgent);
		rountingEntry.init();
		assertRoutingEntryIsNotExecutable();
	}

	@Test
	public void isRoutingEntryExecutableReturnsFalseIfConfiguredTranslationMappingDoesNotExist() throws InitializationFailedException {

		RoutingEntryDataImpl routingEntryDataImpl = getRoutingEntryDataValues("routing_entry_name","0:283=\"elitecore.com\"", 
				"example.com", "10.106.10.120", "1", DiameterFailureConstants.RECORD.failureAction, "3000", 
				"driver_name", "elitecore.com");
		routingEntryDataImpl.setTransMapName("translation_mapping");
		Mockito.when(translationAgent.isExists(routingEntryDataImpl.getTransMapName())).thenReturn(false);
		rountingEntry = new RoutingEntry(routingEntryDataImpl, routerContext, translationAgent);
		rountingEntry.init();
		assertRoutingEntryIsNotExecutable();
	}
	
	@Test
	public void initThrowsInitializationFailedExceptionIfInvalidRuleSetConfigured() throws InitializationFailedException {
		thrown.expect(InitializationFailedException.class);
		thrown.expectMessage("Incorrect Advanced Condition: 0:283=* configured.");
		rountingEntry = new RoutingEntry(getRoutingEntryDataValues("routing_entry_name", "0:283=*", 
				"example.com", "10.106.10.120", "1", DiameterFailureConstants.RECORD.failureAction, "3000", 
				"diameter_accounting_driver", "elitecore.com"), routerContext, translationAgent);
		rountingEntry.init();
		
	}

	private void assertRoutingEntryIsNotExecutable() {
		assertFalse("Rounting entry must not be Applicable to given Diameter Packet", rountingEntry.isRoutingEntryExecutable());
	}
	
	/**
	 * @param routingEntryName 
	 * @param advCondition
	 * @param originRealm
	 * @param originIP
	 * @param appIdStr
	 * @param failureAction
	 * @param errorCodeStr
	 * @param failureAgrs 
	 * @param destRealmStr 
	 * 
	 */
	private RoutingEntryDataImpl getRoutingEntryDataValues(String routingEntryName,
			String advCondition, String originRealm, String originIP, 
			String appIdStr,int failureAction, String errorCodeStr, String failureAgrs, 
			String destRealmStr){
		RoutingEntryDataImpl routingEntryData3 = new RoutingEntryDataImpl(); 
		routingEntryData3.setRoutingName(routingEntryName);
		routingEntryData3.setAdvancedCondition(advCondition);
		routingEntryData3.setApplicationIds(appIdStr);
		routingEntryData3.setAttachedRedirection(false);
		routingEntryData3.setDestRealm(destRealmStr);
		routingEntryData3.setRoutingAction(RoutingActions.RELAY.routingAction);

		List<DiameterFailoverConfigurationImpl> failConfigs = new ArrayList<DiameterFailoverConfigurationImpl>();		
		DiameterFailoverConfigurationImpl failConf1 = new DiameterFailoverConfigurationImpl();
		failConf1.setAction(failureAction);
		failConf1.setErrorCodes(errorCodeStr);
		failConf1.setFailoverArguments(failureAgrs);
		failConfigs.add(failConf1);

		routingEntryData3.setFailoverDataList(failConfigs);
		routingEntryData3.setOriginHostIp(originIP);
		routingEntryData3.setOriginRealm(originRealm);

		//for setting PeerGroupList
		List<PeerGroupImpl> peerGroupImpls = new ArrayList<PeerGroupImpl>();
		//creating element for peerGroupImpls List 
		PeerGroupImpl peerGroupImpl = new PeerGroupImpl();
		peerGroupImpl.setAdvancedConditionStr("0:283=\"elitecore.com\"");

		//for setting peerGroupImpls
		List<PeerInfoImpl> peerInfoList = new ArrayList<PeerInfoImpl>();
		//creating element for peerInfoList List 
		PeerInfoImpl peerInfoImpl = new PeerInfoImpl();
		peerInfoImpl.setLoadFactor(10);
		peerInfoImpl.setPeerName("diameter_peer");

		//	peerInfoList.add(peerInfoImpl);
		peerGroupImpl.setPeerInfoList(peerInfoList);

		peerGroupImpls.add(peerGroupImpl);

		routingEntryData3.setPeerGroupList(peerGroupImpls);

		return routingEntryData3;

	}

	/**
	 * 
	 * @return <code>DiameterRouterContext</code>
	 */
	private static RouterContext createRouterContext(){
		RouterContext routerContext = new RouterContext(){

			@Override
			public PeerData getPeerData(String hostIdentity) {
				return null;
			}

			@Override
			public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
				return null;
			}

			@Override
			public String getVirtualRoutingPeerName() {
				return null;
			}

			@Override
			public void updateUnknownH2HDropStatistics(DiameterAnswer answer,
					String hostIdentity, String realmName, RoutingActions routeAction) {
				
			}

			@Override
			public void updateDiameterStatsPacketDroppedStatistics(
					DiameterPacket packet, String hostIdentity,
					String realmName, RoutingActions routeAction) {
				
			}

			@Override
			public void updateRealmInputStatistics(DiameterPacket packet,
					String realmName, RoutingActions routeAction) {
				
			}

			@Override
			public void updateRealmOutputStatistics(DiameterPacket packet,
					String realmName, RoutingActions routeAction) {
				
			}

			@Override
			public void postRequestRouting(DiameterRequest originRequest,
					DiameterRequest destinationRequest, String originPeerId,
					String destPeerId, String routingEntryName) {
				
			}

			@Override
			public void preAnswerRouting(DiameterRequest originRequest,
					DiameterRequest destinationRequest,
					DiameterAnswer originAnswer, String originPeerId,
					String routingEntryName) {
				
			}

			@Override
			public void postAnswerRouting(DiameterRequest originRequest,
					DiameterRequest destinationRequest,
					DiameterAnswer originAnswer,
					DiameterAnswer destinationAnswer, String originPeerId,
					String destPeerId, String routingEntryName) {
				
			}

			@Override
			public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name)
					throws DriverInitializationFailedException,
					DriverNotFoundException {
				return null;
			}

			@Override
			public void updateRealmTimeoutRequestStatistics(DiameterRequest destinationRequest, String realmName,
					RoutingActions routingAction) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public RoutingEntry getRoutingEntry(String routingEntryName) {
				// TODO Auto-generated method stub
				return null;
			}

			

		};
		return routerContext;
	}

}
