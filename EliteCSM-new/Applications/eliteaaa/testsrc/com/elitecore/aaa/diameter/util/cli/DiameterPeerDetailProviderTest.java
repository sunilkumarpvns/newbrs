package com.elitecore.aaa.diameter.util.cli;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.DuplicateDetectionHandler;
import com.elitecore.diameterapi.mibs.config.DiameterConfiguration;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerIpAddressTable;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;
import com.elitecore.diameterapi.mibs.statistics.MIBIndexRecorder;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DiameterPeerDetailProviderTest {

	private static final  String PEER = "peer";
	private static final String MODULE = "DIA-PEER-DETAIL-PROVIDER-TEST";
	
	private static final String PEER1_HOST_IDENTITY = "peer1.elitecore.com";
	private static final String PEER2_HOST_IDENTITY = "peer2.elitecore.com";
	private static final String PEER3_HOST_IDENTITY = "peer3.elitecore.com";
	private static final String PEER4_HOST_IDENTITY = "peer4.elitecore.com";

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	private DiameterPeerDetailProvider diameterPeerDetailProvider;
	
	private DummyStackContext dummyStackContext = spy(new DummyStackContext(null));
	
	private DiameterConfiguration diameterConfigProvider;
	
	@Mock private MIBIndexRecorder mibIndexRecorder;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		dummyStackContext.addApplicationsIdentifiersList(ApplicationIdentifier.NASREQ);
		diameterConfigProvider = new DiameterConfiguration(mibIndexRecorder);
		diameterConfigProvider.init(getDiameterPeers());
		diameterPeerDetailProvider = new DiameterPeerDetailProvider(diameterConfigProvider);
	}	

	@Test
	public void getKeyReturns_peer() {
		assertThat(diameterPeerDetailProvider.getKey(), is(equalTo(PEER)));
	}
	
	@Test
	public void displaysDescription() {
		assertThat(diameterPeerDetailProvider.getDescription(), is(equalTo("Display Configuration details of Diameter Peer")));
	}
	
	@Test
	@Parameters({"?","-help","-HELP","-HeLp","-HeLP","-hELP","-HELp","-helP"})
	public void helpMessageIsDisplayedIfArgumentIsHelp(String cmdArgument) {
		String cmdOutput = diameterPeerDetailProvider.execute(new String[] { cmdArgument });
		String helpMsg = "\nUsage 	 : show diameter config peer [<Host Identity>]" + "\nDescription: Displays Configuration Details of All Peers."+"\n(If provided with Host Identity, displays details of that Peer.)";
		assertThat(cmdOutput, is(equalTo(helpMsg)));
	}
	
	@Test
	public void displaysAllPeersSummaryIfArgIsNotEqualToOne() {
		
		String cmdOutput = diameterPeerDetailProvider.execute(new String[] {});
		
		LogManager.getLogger().info(MODULE,"\n" + cmdOutput);
		
		assertThat(cmdOutput, is(containsString(PEER1_HOST_IDENTITY)));
		assertThat(cmdOutput, is(containsString(PEER2_HOST_IDENTITY)));
		assertThat(cmdOutput, is(containsString(PEER3_HOST_IDENTITY)));
		
		assertThat(cmdOutput, not(containsString(PEER4_HOST_IDENTITY)));
		
	}
	
	@Test
	public void displaysPeerSummaryUsingHostIdentity() {
		
		String cmdOutput = diameterPeerDetailProvider.execute(new String[] {PEER1_HOST_IDENTITY});
		
		LogManager.getLogger().info(MODULE,"\n" + cmdOutput);
		
		assertThat(cmdOutput, is(containsString(PEER1_HOST_IDENTITY)));
		assertThat(cmdOutput, not(containsString(PEER2_HOST_IDENTITY)));
		assertThat(cmdOutput, not(containsString(PEER3_HOST_IDENTITY)));
		assertThat(cmdOutput, not(containsString(PEER4_HOST_IDENTITY)));
	}
	
	@Test
	public void peerSummaryIsNotDisplayedIfPeerHostIdentityIsInvalid() {
		
		String cmdOutput = diameterPeerDetailProvider.execute(new String[] {PEER4_HOST_IDENTITY});
		LogManager.getLogger().info(MODULE,"\n" + cmdOutput);
		assertThat(cmdOutput, is(equalTo("Peer: "+ PEER4_HOST_IDENTITY + " is not registered.")));
		
	}
	
	private List<DiameterPeer> getDiameterPeers() {
		
		String realmName = "elitecore.com";
		PeerData peerData1 = new PeerDataProvider().withPeerName("testPeer1")
				.withHostIdentity(PEER1_HOST_IDENTITY)
				.withRealmName(realmName)
				.build();
		
		PeerData peerData2 = new PeerDataProvider().withPeerName("testPeer2")
				.withHostIdentity(PEER2_HOST_IDENTITY)
				.withRealmName(realmName)
				.build();
		
		PeerData peerData3 = new PeerDataProvider().withPeerName("testPeer3")
				.withHostIdentity(PEER3_HOST_IDENTITY)
				.withRealmName(realmName)
				.build();
		
		List<DiameterPeer> diameterPeerList = new ArrayList<DiameterPeer>();
		
		DiameterPeerStub diameterPeer1 = spy(new DiameterPeerStub(peerData1));
		when(diameterPeer1.getPeerConfig()).thenReturn(createDummyPeerConfig(PEER1_HOST_IDENTITY));
		diameterPeerList.add(diameterPeer1);
		
		DiameterPeerStub diameterPeer2 = spy(new DiameterPeerStub(peerData2));
		when(diameterPeer2.getPeerConfig()).thenReturn(createDummyPeerConfig(PEER2_HOST_IDENTITY));
		diameterPeerList.add(diameterPeer2);
		
		DiameterPeerStub diameterPeer3 = spy(new DiameterPeerStub(peerData3));
		when(diameterPeer3.getPeerConfig()).thenReturn(createDummyPeerConfig(PEER3_HOST_IDENTITY));
		diameterPeerList.add(diameterPeer3);
		
		
		return diameterPeerList;
	}
	
	private DiameterPeerConfig createDummyPeerConfig(final String hostIdentity) {
		
		return new DiameterPeerConfig() {
			
			@Override
			public boolean isConnectionInitiationEnabled() {
				return false;
			}
			
			@Override
			public long getPeerWatchDogInterval() {
				return 0;
			}
			
			@Override
			public StorageTypes getPeerStorageType() {
				return null;
			}
			
			@Override
			public int getPeerState() {
				return 0;
			}
			
			@Override
			public RowStatus getPeerRowStatus() {
				return null;
			}
			
			@Override
			public String getPeerLocalIpAddresses() {
				return "localhost-3868";
			}
			
			@Override
			public String getPeerIpAddresses() {
				return hostIdentity + "-3868";
			}
			
			@Override
			public DiameterBasePeerIpAddressTable[] getPeerIpAddressIndex() {
				return null;
			}
			
			@Override
			public String getPeerId() {
				return hostIdentity;
			}
			
			@Override
			public int getPeerFirmwareRevison() {
				return 0;
			}
			
			@Override
			public int getPCBState() {
				return 0;
			}
			
			@Override
			public long getDbpPerPeerStatsTimeoutConnAtmpts() {
				return 0;
			}
			
			@Override
			public long getDbpPerPeerInfoStateDuration() {
				return 0;
			}
			
			@Override
			public DiameterBasePeerVendorTable[] getDbpPeerVendorTable() {
				return new DiameterBasePeerVendorTable[]{ new DiameterBasePeerVendorTable(1, "NONE", StorageTypes.NON_VOLATILE, RowStatus.ACTIVE)};
			}
			
			@Override
			public TransportProtocols getDbpPeerTransportProtocol() {
				return TransportProtocols.TCP;
			}
			
			@Override
			public SecurityProtocol getDbpPeerSecurity() {
				return SecurityProtocol.NONE;
			}
			
			@Override
			public int getDbpPeerPortListen() {
				return 0;
			}
			
			@Override
			public int getDbpPeerPortConnect() {
				return 0;
			}
			
			@Override
			public long getDbpPeerIndex() {
				return 0;
			}
			
			@Override
			public Set<ApplicationEnum> getDbpAppAdvToPeer() {
				return null;
			}
			
			@Override
			public Set<ApplicationEnum> getDbpAppAdvFromPeer() {
				return null;
			}
		};
	}
	
	public class DiameterPeerStub extends DiameterPeer {

		public DiameterPeerStub(PeerData peerData) {
			super(peerData,dummyStackContext,new DiameterRouter(dummyStackContext, Collections.<RoutingEntryData>emptyList()),new SessionFactoryManagerImpl(dummyStackContext),new DiameterAppMessageHandler(dummyStackContext),Mockito.mock(ExplicitRoutingHandler.class),new DuplicateDetectionHandler(dummyStackContext));
		}
	}
}	
