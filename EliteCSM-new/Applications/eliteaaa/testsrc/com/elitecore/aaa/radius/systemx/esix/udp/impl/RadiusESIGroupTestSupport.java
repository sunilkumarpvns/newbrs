package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.esi.radius.conf.impl.CorrelatedRadiusConfigurable;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ActivePassiveCommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.ConsoleLogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.commons.tests.Troubleshooter;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.util.url.InvalidURLException;

public class RadiusESIGroupTestSupport {

	protected static final String SESSION_ID_1 = "1";
	protected static final String SESSION_ID_2 = "2";
	protected static final String SHARED_SECRET = "secret";
	protected static final int LOAD_FACTOR_1 = 1;

	/**
	 * Listener using which the group layer will notify API user of either response or dropped or timeout events.
	 */
	protected AnsweringListener userListener;
	protected DummyAAAServerContext dummyServerContext;
	private DummyAAAServerConfigurationImpl serverConfiguration = new DummyAAAServerConfigurationImpl();
	protected FixedTimeSource fixedTimeSource;

	@Mock protected RadESConfiguration radESConfiguration;
	@Mock protected RadUDPCommunicatorManagerImpl radUDPCommunicatorManagerImpl;
	@Mock protected CorrelatedRadiusConfigurable correatedRadiusConfiguration;

	@Rule public ExpectedException expectedException = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	@Rule public Troubleshooter troubleshooter = Troubleshooter.disabled();

	@BeforeClass
	public static void loadDictionary() {
		RadiusDictionaryTestHarness.getInstance();
		LogManager.setDefaultLogger(new ConsoleLogger());
	}

	@Before
	public void setUp() throws InvalidURLException, InitializationFailedException {
		MockitoAnnotations.initMocks(this);

		userListener = spy(new AnsweringListener());
		dummyServerContext = spy(new DummyAAAServerContext());

		dummyServerContext.setServerConfiguration(serverConfiguration);
		serverConfiguration.setRadESConfiguration(radESConfiguration);
		serverConfiguration.setCorrelatedRadiusConfiguration(correatedRadiusConfiguration);
		dummyServerContext.setRadUdpCommunicatorManager(radUDPCommunicatorManagerImpl);

		fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
	}

	protected void verifyUserListenerResponseReceivedEvent(int i) {
		verify(userListener, times(i)).responseReceived(Mockito.any(RadUDPRequest.class), Mockito.any(RadUDPResponse.class), Mockito.any(HazelcastRadiusSession.class));
	}

	protected void verifyUserListenerResponseReceivedEvent() {
		verify(userListener).responseReceived(Mockito.any(RadUDPRequest.class), Mockito.any(RadUDPResponse.class), Mockito.any(HazelcastRadiusSession.class));

	}
	
	protected void verifyUserListenerRequestDroppedEvent() {
		verify(userListener).requestDropped(Mockito.any(RadUDPRequest.class));
	}
	
	protected void verifyUserListenerRequestTimeOutEvent() {
		verify(userListener).requestTimeout(Mockito.any(RadUDPRequest.class));

	}

	protected void verifyRequestDropped() {
		verify(userListener).requestDropped(Mockito.any(RadUDPRequest.class));
	}

	protected void verifyRequestTimeOut() {
		verify(userListener).requestTimeout(Mockito.any(RadUDPRequest.class));
	}

	protected void requestIsNotForwardedTo(UDPCommunicatorSpy...communicatorExtns) {

		for (UDPCommunicatorSpy op : communicatorExtns) {
			op.verifyRequestNotReceived();
		}
	}
	
	protected String getName(UDPCommunicatorSpy primaryEsi1AuthCommunicator) {
		return primaryEsi1AuthCommunicator.getCommunicator().getName();
	}

	public UDPCommunicatorSpy getCommunicator(DefaultExternalSystemData defaultExternalSystemData) {
		return new UDPCommunicatorSpy(defaultExternalSystemData);
	}

	protected void verifySessionIsNotRetrivedOnResponseReceived() {
		Mockito.verify(dummyServerContext, times(0)).getOrCreateRadiusSession(SESSION_ID_1);
	}

	public static ExternalSystemInterfaceBuilder createESI() {
		return new ExternalSystemInterfaceBuilder();
	}

	public static class ExternalSystemInterfaceBuilder {

		private DefaultExternalSystemData esiData = new DefaultExternalSystemData();

		public ExternalSystemInterfaceBuilder setEsiName(String esiName) {
			this.esiData.setName(esiName);
			return this;
		}
		
		public ExternalSystemInterfaceBuilder setUUID(String esiId) {
			this.esiData.setUUID(esiId);
			return this;
		}

		public ExternalSystemInterfaceBuilder setStringIpAddress(String esiName) {
			this.esiData.setStringIpAddress(esiName);
			return this;
		}

		public ExternalSystemInterfaceBuilder setESIType(int esiType) {
			this.esiData.setEsiType(esiType);
			return this;
		}
		
		public DefaultExternalSystemData getEsiData() {
			return this.esiData;
		}

	}


	public static RadiusESIGroupDataBuilder createESIGroupData() {
		return new RadiusESIGroupDataBuilder();
	}

	public static class RadiusESIGroupDataBuilder {
		public RadiusEsiGroupData radiusGroupData = new RadiusEsiGroupData();

		public RadiusESIGroupDataBuilder groupName(String esiName) {
			this.radiusGroupData.setName(esiName);
			return this;
		}

		public RadiusESIGroupDataBuilder isStatefulEnable(boolean isStateful) {
			this.radiusGroupData.setStateful(isStateful);
			return this;
		}

		public RadiusESIGroupDataBuilder addPrimaryEsiIdWithLoadFactor(String esiName, int loadFactor) {
			CommunicatorData esiEntry = new CommunicatorData();
			esiEntry.setLoadFactor(loadFactor);
			esiEntry.setName(esiName);
			radiusGroupData.getPrimaryEsiList().add(esiEntry);
			return this;
		}

		public RadiusESIGroupDataBuilder addFailOverEsiIdWithLoadFactor(String esiName, int loadFactor) {
			CommunicatorData esiEntry = new CommunicatorData();
			esiEntry.setLoadFactor(loadFactor);
			esiEntry.setName(esiName);
			radiusGroupData.getFailOverEsiList().add(esiEntry);
			return this;
		}

		public RadiusESIGroupDataBuilder isSwitchBackEnable(boolean switchBackEnable) {
			radiusGroupData.setSwitchBackEnable(switchBackEnable);
			return this;
		}

		public RadiusESIGroupDataBuilder redundancyMode(String redundancyMode) {
			radiusGroupData.setRedundancyMode(redundancyMode);
			return this;
		}

		public RadiusESIGroupDataBuilder esiType(String esiType) {
			radiusGroupData.setEsiType(esiType);
			return this;
		}
		
		public RadiusESIGroupDataBuilder addActivePassiveEsiAndLoadFactor(String activeEsi, String passiveEsi, int loadFactor) {
			radiusGroupData.getActivePassiveEsiList().add(new ActivePassiveCommunicatorData(activeEsi, passiveEsi, loadFactor));
			return this;
		}
	}

}
