package com.elitecore.diameterapi.diameter.common.routerx;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterVirtualPeer;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.routerx.agent.RoutingEntryDataImpl;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;

@RunWith(JUnitParamsRunner.class)
public class DiameterRouterTest extends DiameterTestSupport {
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Mock private ITranslationAgent translationAgent;
	@Mock private RoutingEntry routingEntry;
	private DiameterVirtualPeer virtualPeer;
	@Mock private DiameterStack stack;

	//Concrete Entities 
	private List<RoutingEntryData> routingEntryDataList;
	private DiameterRequest originRequest;
	
	//Entity to test
	private DiameterRouter diameterRouter;
	
	@BeforeClass
	public static void init() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		
		when(translationAgent.isExists(anyString())).thenReturn(true);
		routingEntryDataList = new ArrayList<RoutingEntryData>();
		diameterRouter = new DiameterRouter(getStackContext(), routingEntryDataList, translationAgent);
		
		doAnswer(new Answer<VirtualConnectionHandler>() {

			@Override
			public VirtualConnectionHandler answer(InvocationOnMock invocation) throws Throwable {
				
				Object[] args = invocation.getArguments();
				PeerDataImpl peerData = (PeerDataImpl) args[0];
				peerData.setLocalInetAddress(InetAddress.getLocalHost());
				VirtualOutputStream outpurStream = (VirtualOutputStream) args[1];
				VirtualConnectionHandler virtualConnectionHandler = new VirtualConnectionHandler(stack, peerData, outpurStream);
				virtualPeer = spy(new DiameterVirtualPeer(peerData, virtualConnectionHandler, getStackContext(), diameterRouter, new SessionFactoryManagerImpl(getStackContext()), null, null, null));
				when(virtualPeer.isAlive()).thenReturn(true);
				virtualPeer.start();
				getRouterContext().addPeerData(peerData);
				getStackContext().addPeerData(peerData);
				addPeer(virtualPeer);
				return virtualConnectionHandler;
			}
		}).when(getStackContext()).registerVirtualPeer((PeerData)anyObject(), (VirtualOutputStream) anyObject());
	}
	
	/**
	 * @return Array Of
	 * RoutingEntryData routingEntryData
	 * boolean isEntryAdded
	 */
	public static Object[][] dataProviderFor_testInit() {
		return new Object[][]{
				{createEntryData(1), true},
				{null, 				 false},
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_testInit")
	public void testInit(RoutingEntryData entryData, boolean isEntryAdded){
		setUpInit(entryData);
		diameterRouter.init();
		
		if(isEntryAdded){
			assertEquals("Must Reflect Only One Application As per Entry", 
				1, diameterRouter.getSupportedRemoteApplications().size());
		} else {
			assertEquals("Null Entry Must not add Remote Application", 
				0, diameterRouter.getSupportedRemoteApplications().size());
		}
	}

	/**
	 * @return Array Of
	 * Case Desc
	 * RoutingEntryData routingEntryData
	 * Destination Realm
	 * Destination Host
	 * Result Routing Action
	 */
	public static Object[][] dataProviderFor_testProcessRequest() {
		return new Object[][]{
				{"Local Routing Action with RoutingEntry", 
					createEntryData(RoutingActions.LOCAL.routingAction),  "example.net", null,
					RoutingActions.LOCAL},
				{"Relay Routing Action with RoutingEntry", 
					createEntryData(RoutingActions.RELAY.routingAction), "example.net", null,
					RoutingActions.RELAY},
				{"Proxy Routing Action with RoutingEntry", 
					createEntryData(RoutingActions.PROXY.routingAction), "example.net", null,
					RoutingActions.PROXY},
				{"Redirect Routing Action with RoutingEntry", 
					createEntryData(RoutingActions.REDIRECT.routingAction), "example.net", null,
					RoutingActions.REDIRECT},
				{"Other Routing Action with RoutingEntry", 
					createEntryData(-1), null, null,
					RoutingActions.LOCAL},
				{"No Routing Entry, Eligible for Local Process", 
					null, Parameter.getInstance().getOwnDiameterRealm(), null, 
					RoutingActions.LOCAL},	
				{"No Routing Entry, Eligible for Local Process", 
					null, Parameter.getInstance().getOwnDiameterRealm(), Parameter.getInstance().getOwnDiameterIdentity(),
					RoutingActions.LOCAL},
				{"No Routing Entry, Not Eligible for Local Process", 
					null, "example.net", Parameter.getInstance().getOwnDiameterIdentity(),
					RoutingActions.LOCAL},
				{"No Routing Entry, Not Eligible for Local Process", 
					null, Parameter.getInstance().getOwnDiameterRealm(), "other."+Parameter.getInstance().getOwnDiameterRealm(),
					RoutingActions.LOCAL},
		};
	}
	
	@Test
	@Parameters(method= "dataProviderFor_testProcessRequest")
	public void testProcessRequest(String caseDesc, 
			RoutingEntryData entryData, 
			String destRealm,
			String destHost, 
			RoutingActions routingActionExpected){
		
		System.out.println("Executing Case: " + caseDesc);
		setUpProcessRequest(entryData, destHost, destRealm);
		
		RoutingActions routingActionFound = null;
		try {
			routingActionFound = diameterRouter.processDiameterRequest(originRequest, getSession());
		} catch (RoutingFailedException e) {
			routingActionFound = e.getRoutingAction();
		}finally{
			assertEquals("Routing Agent Not Proper", routingActionExpected, routingActionFound);
		}
	}
	
	@Test
	public void testDummyRouting() throws CloneNotSupportedException, RoutingFailedException, TranslationFailedException {
		
		System.out.println("Executing Case: Dummy Routing");
		setUpDummyRouting();
		diameterRouter.processDiameterRequest(originRequest, getSession());
		
		//verify
		verify(translationAgent).getDummyResponseMap(anyString());
		verify(stack).handleReceivedMessage((Packet)anyObject(), (NetworkConnectionHandler) anyObject());
	}
	
	private void setUpDummyRouting() throws TranslationFailedException {
		
		setUpProcessRequest(createEntryData(RoutingActions.PROXY.routingAction), 
				null, "example.net");
		
		Map<String, String> dummyMappings = new HashMap<String, String>();
		dummyMappings.put("0:258", "2001");
		when(translationAgent.getDummyResponseMap(anyString())).thenReturn(dummyMappings);
		
		doAnswer(new Answer<TranslatorParams>() {

			@Override
			public TranslatorParams answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				TranslatorParams translatorParams = (TranslatorParams) args[1];
				translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "Dummy_Mapping");
				translatorParams.setParam(TranslatorConstants.DUMMY_MAPPING, true);
				//Request Translation
				return translatorParams;
			}
		}).when(translationAgent).translate(anyString(), (TranslatorParams) anyObject(), anyBoolean());
	}

	private void setUpProcessRequest(RoutingEntryData entryData, String destHost, String destRealm) {
		createOriginRequest(destHost, destRealm);
		
		if(entryData != null)
			setUpInit(entryData);
		diameterRouter.init();
	}

	private void setUpInit(RoutingEntryData entryData) {
		routingEntryDataList.add(entryData);
	}

	private void createOriginRequest(String destHost, String destRealm) {
		//Origin Host
		originRequest = createCCInitialRequest();
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_REALM, originRequest, destRealm);
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, originRequest, destHost);
		
	}
	
	private static RoutingEntryData createEntryData(final int routingAction) {
		RoutingEntryDataImpl data = new RoutingEntryDataImpl();
		data.setRoutingAction(routingAction);
		data.setApplicationIds("4");
		data.setRoutingName("RouterTest");
		data.setTransMapName("AnyName");
		data.setPeerGroupList(Collections.<PeerGroupImpl>emptyList());
		return data;
	}

	
	@Test
	public void test_processDiameterRequest_should_not_throw_Exception_when_peer_not_found() throws Exception {		
		setUpProcessRequest(createEntryData(RoutingActions.RELAY.routingAction), "test.elite.com", "elite.com");
		
		RoutingActions routingActionFound = null;
		routingActionFound = diameterRouter.processDiameterRequest(originRequest, getSession());
		assertEquals(RoutingActions.RELAY, routingActionFound);
	}
	
	
	public @Rule ExpectedException exception = ExpectedException.none();
	
	public Object[][] dataProviderFor_test_processDiameterRequest_should_throw_RoutingFailedException_when_routing_entry_not_found() {
		
		return new Object[][] {
				
				/* Routing Entry not found, not eligible for Local processing*/
				{ROUTING_PEER[1], ROUTING_PEER[2], true, RoutingActions.LOCAL},
				
				/* Routing Entry not found, eligible for Local processing */
				{Parameter.getInstance().getOwnDiameterIdentity(), Parameter.getInstance().getOwnDiameterRealm(), false, RoutingActions.LOCAL},
			
		};
	}
	
	/**
	 * 
	 * Routing Entry not found.
	 * 
	 * IF eligible for local processing THEN
	 * 		return RoutingActions.LOCAL	
	 * ELSE
	 * 
	 * 	it will throw RoutingFailedException with
	 * 		Resultcode > DIAMETER_UNABLE_TO_DELIVER
	 * 		RoutingAction > LOCAL
	 * 	
	 * @author Chetan.Sankhala
	 */
	@Test
	@Parameters(method="dataProviderFor_test_processDiameterRequest_should_throw_RoutingFailedException_when_routing_entry_not_found")
	public void test_processDiameterRequest_should_throw_RoutingFailedException_when_routing_entry_not_found(
			String destHostId,
			String destHostRealm,
			boolean isThrowException,
			RoutingActions returnRoutingAction) throws Exception {

		setUpProcessRequest(null,	destHostId, destHostRealm);

		if(isThrowException) {
			exception.expect(RoutingFailedException.class);
		}
		
		try {
			diameterRouter.processDiameterRequest(originRequest, getSession());
		} catch (RoutingFailedException routingFailedException) {
			assertEquals(ResultCode.DIAMETER_UNABLE_TO_DELIVER, routingFailedException.getResultCode());
			assertEquals(RoutingActions.LOCAL, routingFailedException.getRoutingAction());
			throw routingFailedException;
		}
		
		assertEquals(RoutingActions.LOCAL, returnRoutingAction);
	}
}
