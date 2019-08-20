package com.elitecore.diameterapi.diameter.common.peers.capabilityexchange;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.fsm.peer.DiameterPeerStateMachine;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;
import com.elitecore.diameterapi.diameter.common.peers.capabilityexchange.ApplicationProviderFactory;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class PeerApplicationProviderTest {

	//Constants
	private static final String[] ORIGIN_PEER_CREDS = {"originator_peer", "originator.example.com", "example.com"};
	
	//Entity to test
	private PeerApplicationProvider peerApplicationProvider;

	@Mock private IDiameterStackContext stackContext;
	private DiameterPeerStateMachineStub peerStateMachine = new DiameterPeerStateMachineStub(createPeer());

	DiameterPacket capabilityExchangeAnswer;

	private static Set<ApplicationEnum> stackApplications = new HashSet<ApplicationEnum>();
	private static Set<ApplicationEnum> stackAuthApplications = new HashSet<ApplicationEnum>();
	private static Set<ApplicationEnum> stackAcctApplications = new HashSet<ApplicationEnum>();
	
	static{
		
		//All 
		stackApplications = addAllToApplictaionSet(
				ApplicationIdentifier.CC, 
				ApplicationIdentifier.NASREQ,
				ApplicationIdentifier.TGPP_S9,
				ApplicationIdentifier.BASEACCOUNTING);
		
		//Separating Auth
		stackAuthApplications.add(ApplicationIdentifier.CC);
		stackAuthApplications.add(new ApplicationEnum() {
			public long getVendorId() {
				return ApplicationIdentifier.NASREQ.getVendorId();
			}
			public ServiceTypes getApplicationType() {
				return ServiceTypes.AUTH;
			}
			public long getApplicationId() {
				return ApplicationIdentifier.NASREQ.getApplicationId();
			}
			public Application getApplication() {
				return ApplicationIdentifier.NASREQ.getApplication();
			}
			
			@Override
			public String toString() {
				return new StringBuilder()
				.append(getVendorId())
				.append(":")
				.append(getApplicationId())
				.append(" [").append(getApplication().getDisplayName()).append("]").toString();
			}
		});
		stackAuthApplications.add(new ApplicationEnum() {
			public long getVendorId() {
				return ApplicationIdentifier.TGPP_S9.getVendorId();
			}
			public ServiceTypes getApplicationType() {
				return ServiceTypes.AUTH;
			}
			public long getApplicationId() {
				return ApplicationIdentifier.TGPP_S9.getApplicationId();
			}
			public Application getApplication() {
				return ApplicationIdentifier.TGPP_S9.getApplication();
			}
			
			@Override
			public String toString() {
				return new StringBuilder()
				.append(getVendorId())
				.append(":")
				.append(getApplicationId())
				.append(" [").append(getApplication().getDisplayName()).append("]").toString();
			}
		});
		//Separating ACCT
		stackAcctApplications.add(ApplicationIdentifier.BASEACCOUNTING);
		stackAcctApplications.add(new ApplicationEnum() {
			public long getVendorId() {
				return ApplicationIdentifier.NASREQ.getVendorId();
			}
			public ServiceTypes getApplicationType() {
				return ServiceTypes.ACCT;
			}
			public long getApplicationId() {
				return ApplicationIdentifier.NASREQ.getApplicationId();
			}
			public Application getApplication() {
				return ApplicationIdentifier.NASREQ.getApplication();
			}
			
			@Override
			public String toString() {
				return new StringBuilder()
				.append(getVendorId())
				.append(":")
				.append(getApplicationId())
				.append(" [").append(getApplication().getDisplayName()).append("]").toString();
			}
		});
		stackAcctApplications.add(new ApplicationEnum() {
			public long getVendorId() {
				return ApplicationIdentifier.TGPP_S9.getVendorId();
			}
			public ServiceTypes getApplicationType() {
				return ServiceTypes.ACCT;
			}
			public long getApplicationId() {
				return ApplicationIdentifier.TGPP_S9.getApplicationId();
			}
			public Application getApplication() {
				return ApplicationIdentifier.TGPP_S9.getApplication();
			}
			
			@Override
			public String toString() {
				return new StringBuilder()
						.append(getVendorId())
						.append(":")
						.append(getApplicationId())
						.append(" [").append(getApplication().getDisplayName()).append("]").toString();
			}
		});
	}
	
	@BeforeClass
	public static void init() {
		DummyDiameterDictionary.getInstance();
		LogManager.setDefaultLogger(new NullLogger());
	}

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
		setUp();
	}
	
	/**
	 * @return Array of
	 * String strExclusiveAuthAppIds, 
	 * String strExclusiveAcctAppIds, 
	 * Set<ApplicationEnum> remoteApplications, 
	 * Set<ApplicationEnum> expectedCommonApplications
	 */
	public static Object[][] dataProviderFor_testDefaultCase_NoExclusiveApplicationProvided(){
		return new Object[][]{
				{null, null, addAllToApplictaionSet(ApplicationIdentifier.NASREQ, ApplicationIdentifier.MOBILE_IPV4),
					addAllToApplictaionSet(ApplicationIdentifier.NASREQ)},
				{"", "", addAllToApplictaionSet(ApplicationIdentifier.NASREQ), 
					addAllToApplictaionSet(ApplicationIdentifier.NASREQ)},	
				{null, null, addAllToApplictaionSet(ApplicationIdentifier.NASREQ, ApplicationIdentifier.RELAY), 
					 stackApplications},
				{"  ", "  ", addAllToApplictaionSet(ApplicationIdentifier.NASREQ), 
					addAllToApplictaionSet(ApplicationIdentifier.NASREQ)},
		};
	}

	@Test
	@Parameters(method="dataProviderFor_testDefaultCase_NoExclusiveApplicationProvided")
	public void testDefaultCase_NoExclusiveApplicationProvided(
			String strExclusiveAuthAppIds, String strExclusiveAcctAppIds, 
			Set<ApplicationEnum> remoteApplications, 
			Set<ApplicationEnum> expectedCommonApplications) {
		
		setUpDefaultCase(strExclusiveAuthAppIds, strExclusiveAcctAppIds, 
				remoteApplications);
		
		List<IDiameterAVP> expectedApplications = peerStateMachine.createApplicationIdAVPs(stackApplications);
		List<IDiameterAVP> generatedApplications = peerStateMachine.createApplicationIdAVPs(peerApplicationProvider.getApplications());
		
		peerApplicationProvider.addRemoteApplication(capabilityExchangeAnswer);
		
		List<IDiameterAVP> expectedCommonApplicationAVPs = peerStateMachine.createApplicationIdAVPs(expectedCommonApplications);
		List<IDiameterAVP> generatedCommonApplicationAVPs = peerStateMachine.createApplicationIdAVPs(peerApplicationProvider.getCommonApplications());
		
		/// Verifying expected Applications
		Assert.assertEquals("Default behavior breached, No of App Id AVPs should be same", 
				expectedApplications.size(), generatedApplications.size());
		
		Assert.assertEquals("Default behavior breached, No of Applictaion Id AVPs should be same", 
				generatedApplications.size(), stackAuthApplications.size() + stackAcctApplications.size());
		
		for(int i = 0 ; i < expectedApplications.size() ; i++) {
			Assert.assertNotNull("Generated and Expected Applictaion ID AVPs must be same",
					generatedApplications.remove(expectedApplications.get(i)));
		}
		
		Assert.assertEquals("Default behavior breached, No of Common Application Id AVPs should be same", 
				expectedCommonApplicationAVPs.size(), generatedCommonApplicationAVPs.size());
		
		//Verifying Common Applications		
		for(int i = 0 ; i < expectedCommonApplicationAVPs.size() ; i++) {
			Assert.assertNotNull("Generated and Expected Applictaion ID AVPs must be same",
					generatedCommonApplicationAVPs.remove(expectedCommonApplicationAVPs.get(i)));
		}
	}
	
	/**
	 * @return Array of
	 * String strExclusiveAuthAppIds, 
	 * String strExclusiveAcctAppIds, 
	 * Set<ApplicationEnum> remoteApplications, 
	 * Set<ApplicationEnum> expectedApplications
	 * Set<ApplicationEnum> expectedCommonApplications
	 */
	public static Object[][] dataProviderFor_testExclusiveApplication(){
		return new Object[][]{
				{" 0x0 ", " 0X0 ", addAllToApplictaionSet(ApplicationIdentifier.NASREQ), 
					Collections.emptySet(),
					Collections.emptySet()},
					
				{" 1, 5", "3 , 1", addAllToApplictaionSet(ApplicationIdentifier.NASREQ), 
					addAllToApplictaionSet(ApplicationIdentifier.NASREQ, ApplicationIdentifier.EAP, ApplicationIdentifier.BASEACCOUNTING),
					addAllToApplictaionSet(ApplicationIdentifier.NASREQ, ApplicationIdentifier.EAP, ApplicationIdentifier.BASEACCOUNTING)},
					
				{"10415:16777302", "10415:16777302", addAllToApplictaionSet(ApplicationIdentifier.NASREQ), 
					addAllToApplictaionSet(ApplicationIdentifier.TGPP_SY),
					addAllToApplictaionSet(ApplicationIdentifier.TGPP_SY)},
					
				{"5", null, addAllToApplictaionSet(ApplicationIdentifier.BASEACCOUNTING), 
					addSetToApplictaionSet(stackAcctApplications, ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP, ApplicationIdentifier.BASEACCOUNTING)},
				
				{"5", null, addAllToApplictaionSet(ApplicationIdentifier.CC), 
					addSetToApplictaionSet(stackAcctApplications, ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP)},
					
				{"5", null, addAllToApplictaionSet(ApplicationIdentifier.RELAY), 
					addSetToApplictaionSet(stackAcctApplications, ApplicationIdentifier.EAP),
					addSetToApplictaionSet(stackAcctApplications, ApplicationIdentifier.EAP)},
				
				{"5", "0x0", addAllToApplictaionSet(ApplicationIdentifier.RELAY),
					addAllToApplictaionSet(ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP)},
					
				{"5", "0x0", addAllToApplictaionSet(ApplicationIdentifier.CC),
					addAllToApplictaionSet(ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP)},
					
				{" 0x0 ", null, addAllToApplictaionSet(ApplicationIdentifier.CC, ApplicationIdentifier.BASEACCOUNTING),
					stackAcctApplications,
					addAllToApplictaionSet(ApplicationIdentifier.BASEACCOUNTING)},
				
				{"-2", "5", addAllToApplictaionSet(ApplicationIdentifier.EAP), 
					addAllToApplictaionSet(ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP)},
					
				{"-2:1", "5", addAllToApplictaionSet(ApplicationIdentifier.EAP), 
					addAllToApplictaionSet(ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP)},
					
				{"2-1", "5", addAllToApplictaionSet(ApplicationIdentifier.EAP), 
					addAllToApplictaionSet(ApplicationIdentifier.EAP),
					addAllToApplictaionSet(ApplicationIdentifier.EAP)},
		};
	}

	@Test
	@Parameters(method="dataProviderFor_testExclusiveApplication")
	public void testExclusiveApplication(
			String strExclusiveAuthAppIds, String strExclusiveAcctAppIds, 
			Set<ApplicationEnum> remoteApplications, 
			Set<ApplicationEnum> expectedApplications,
			Set<ApplicationEnum> expectedCommonApplications) {
		
		setUpDefaultCase(strExclusiveAuthAppIds, strExclusiveAcctAppIds, 
				remoteApplications);
		
		List<IDiameterAVP> generatedApplicationIdAVPs = peerStateMachine.createApplicationIdAVPs(peerApplicationProvider.getApplications());
		List<IDiameterAVP> expectedApplicationIdAVPs = peerStateMachine.createApplicationIdAVPs(expectedApplications);
		
		peerApplicationProvider.addRemoteApplication(capabilityExchangeAnswer);
		
		List<IDiameterAVP> expectedCommonApplicationAVPs = peerStateMachine.createApplicationIdAVPs(expectedCommonApplications);
		List<IDiameterAVP> generatedCommonApplicationAVPs = peerStateMachine.createApplicationIdAVPs(peerApplicationProvider.getCommonApplications());
		
		//Verifying Applications
		Assert.assertEquals("Exclusive Application behavior breached for: " + 
				strExclusiveAuthAppIds + " or " + strExclusiveAcctAppIds, 
				expectedApplicationIdAVPs.size(), generatedApplicationIdAVPs.size());
		
		for(int i = 0 ; i < expectedApplicationIdAVPs.size() ; i++) {
			Assert.assertNotNull("Generated and Expected Applictaion ID AVPs must be same",
					generatedApplicationIdAVPs.remove(expectedApplicationIdAVPs.get(i)));
		}
		
		//Verifying Common Applications
		Assert.assertEquals("No of Common Application Id AVPs should be same", 
				expectedCommonApplicationAVPs.size(), generatedCommonApplicationAVPs.size());
		
		for(int i = 0 ; i < expectedCommonApplicationAVPs.size() ; i++) {
			Assert.assertNotNull("Generated and Expected Applictaion ID AVPs must be same",
					generatedCommonApplicationAVPs.remove(expectedCommonApplicationAVPs.get(i)));
		}
	}
	
	private void setUpDefaultCase(String strExclusiveAuthAppIds,
			String strExclusiveAcctAppIds,
			Set<ApplicationEnum> remoteApplications) {
		
		PeerData peerData = new PeerDataProvider()
								.withPeerName(ORIGIN_PEER_CREDS[0])
								.withHostIdentity(ORIGIN_PEER_CREDS[1])
								.withRealmName(ORIGIN_PEER_CREDS[2])
								.withExclusiveAuthAppIDs(strExclusiveAuthAppIds)
								.withExclusiveAcctAppIDs(strExclusiveAcctAppIds)
								.build();
		peerApplicationProvider = new PeerApplicationProvider(peerData,
				new ApplicationProviderFactory(stackContext));
		peerApplicationProvider.init();
		createDiameterPacket(remoteApplications);

	}

	/**
	 * Creates a CER/CEA with provided Auth AppIDs, Acct AppIds, 
	 * VendorSpecficAuthAppId and VendorSpecficAcctAppId AVPs 
	 * @param authAppIds
	 * @param acctAppIs
	 * @param acctAppIs2 
	 * @param vendorId
	 * @param vendorSpeceficAuthAppId
	 * @param vendorSpeceficAcctId
	 */
	private void createDiameterPacket(Set<ApplicationEnum> remoteApplications) {
		
		capabilityExchangeAnswer = new DiameterAnswer();
		capabilityExchangeAnswer.setCommandCode(CommandCode.CAPABILITIES_EXCHANGE.code);
		capabilityExchangeAnswer.setApplicationID(0);
		capabilityExchangeAnswer.setHop_by_hopIdentifier(HopByHopPool.get());
		capabilityExchangeAnswer.setEnd_to_endIdentifier(EndToEndPool.get());
		
		IDiameterAVP originHost = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_HOST));
		originHost.setStringValue(ORIGIN_PEER_CREDS[1]);
		capabilityExchangeAnswer.addAvp(originHost);

		// Adding Origin Host as must for all types of Diameter Message
		IDiameterAVP originRealm = (DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_REALM));
		originRealm.setStringValue(ORIGIN_PEER_CREDS[2]);
		capabilityExchangeAnswer.addAvp(originRealm);
		
		capabilityExchangeAnswer.addAvps(peerStateMachine.createApplicationIdAVPs(remoteApplications));
		
	}

	private static Set<ApplicationEnum> addAllToApplictaionSet(ApplicationEnum ... applicationEnums) {
		Set<ApplicationEnum> applications = new HashSet<ApplicationEnum>();
		for (int i = 0; i < applicationEnums.length; i++) {
			applications.add(applicationEnums[i]);
		}
		return applications;
	}
	
	private static Set<ApplicationEnum> addSetToApplictaionSet(Set<ApplicationEnum> applicationsSet, 
			ApplicationEnum ... applicationEnums) {
		Set<ApplicationEnum> applications = addAllToApplictaionSet(applicationEnums);
		applications.addAll(applicationsSet);
		return applications;
	}
	
	private void setUp() {
		Mockito.when(stackContext.getApplicationsIdentifiersList()).thenReturn(stackApplications);
	}
	
	private DiameterPeer createPeer() {
		DiameterPeer diameterPeer = Mockito.mock(DiameterPeer.class);
		Mockito.when(diameterPeer.getPeerData()).thenReturn(new PeerDataImpl());
		return diameterPeer;
	}
	
	private class DiameterPeerStateMachineStub extends DiameterPeerStateMachine {

		public DiameterPeerStateMachineStub(DiameterPeer diameterPeer) {
			super(diameterPeer, null, null, null, null, null, null, null, TimeSource.systemTimeSource());
		}
		public DiameterPeerStateMachineStub(DiameterPeer peer,
				DiameterRouter diameterRouter,
				SessionFactoryManager sessionFactoryManager,
				DiameterAppMessageHandler appMessageHandler,
				IDiameterStackContext stackContext,
				ExplicitRoutingHandler explicitRoutingHandler) {
			super(peer, diameterRouter, sessionFactoryManager, appMessageHandler,
					stackContext, explicitRoutingHandler, null, null, TimeSource.systemTimeSource());
		}
		
		@Override
		public List<IDiameterAVP> createApplicationIdAVPs(Set<ApplicationEnum> diameterApplicationIdentifiers){
			return super.createApplicationIdAVPs(diameterApplicationIdentifiers);
		}

		@Override
		public void addAdditionalAVPs(List<IDiameterAVP> additionalAvps,
				DiameterPacket diameterPacket) {
			
		}

		@Override
		protected void setPeerInitConnection(boolean peerInitConnection) {
			
		}

		@Override
		protected String getPeerName() {
			return ORIGIN_PEER_CREDS[0];
		}
		@Override
		protected void triggerDWR() {
			// Nothing to do
			
		}
		@Override
		public void sendBasePacket(DiameterPacket basePacket) throws IOException {
			
		}
		@Override
		public void sendRequest(DiameterRequest diameterRequest, ResponseListener listener) throws IOException {
			
		}
		@Override
		public void sendAnswer(DiameterAnswer diameterAnswer) throws IOException {
			
		}
	}

}
