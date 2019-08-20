package com.elitecore.aaa.diameter.service.drivers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.drivers.cdr.DiameterCSVDriver;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.NasClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterCDRDriverFactoryImplTest {

	private static final String TEST_DRIVER_ID = "1";
	private static final String TEST_DRIVER_NAME = "test";
	private static final String ANY = "any";

	private DiameterCDRDriverFactoryImplStub diameterCDRDriverFactoryImpl; 
	private DummyAAAServerConfigurationImpl dummyAAAServerConfiguration;
	private Map<String, DriverConfiguration> driverConfigurationMap;

	@Mock private AAAServerContext serverContext;
	@Mock private DriverConfigurationProvider driverConfigurationProvider;

	@Rule public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		driverConfigurationMap = new HashMap<String, DriverConfiguration>(); 

		dummyAAAServerConfiguration = spy(new DummyAAAServerConfigurationImpl());
		diameterCDRDriverFactoryImpl = new DiameterCDRDriverFactoryImplStub(serverContext);

		when(serverContext.getServerConfiguration()).thenReturn(dummyAAAServerConfiguration);
		when(dummyAAAServerConfiguration.getDiameterDriverConfiguration()).thenReturn(driverConfigurationProvider);
		when(driverConfigurationProvider.getDriverConfigurations()).thenReturn(driverConfigurationMap.values());
	}

	public class FetchingDriverByName {

		@Test
		public void throwsDriverNotFoundExceptionIfDriverConfigurationIsNull() throws Exception {
			when(driverConfigurationProvider.getDriverConfigurations()).thenReturn(null);

			exception.expect(DriverNotFoundException.class);

			diameterCDRDriverFactoryImpl.getDriver(ANY);
		}

		@Test
		public void throwsDriverNotFoundExceptionIfDriverConfigurationIsEmpty() throws Exception {
			exception.expect(DriverNotFoundException.class);

			diameterCDRDriverFactoryImpl.getDriver(ANY);
		}

		@Test
		public void returnsLazilyIntializedDriverIfConfigurationExists() throws Exception {
			driverConfigurationWithDriverName(TEST_DRIVER_NAME);

			assertThat(diameterCDRDriverFactoryImpl.getDriver(TEST_DRIVER_NAME), 
					is(notNullValue()));
		}

		@Test
		public void onceInitializedReturnsSameInstanceOnSubsequentCalls() throws Exception {
			driverConfigurationWithDriverName(TEST_DRIVER_NAME);

			CDRDriver<DiameterPacket> driver1 = diameterCDRDriverFactoryImpl.getDriver(TEST_DRIVER_NAME);

			CDRDriver<DiameterPacket> driver2 = diameterCDRDriverFactoryImpl.getDriver(TEST_DRIVER_NAME);

			assertThat(driver1, is(sameInstance(driver2)));
		}
	}

	private void driverConfigurationWithDriverName(String driverName) {
		NasClassicCSVAcctDriverConfigurationImpl config = new NasClassicCSVAcctDriverConfigurationImpl();
		config.setDriverInstanceId(TEST_DRIVER_ID);
		config.setDriverName(driverName);
		driverConfigurationMap.put(driverName, config);

		when(driverConfigurationProvider.getDriverConfiguration(TEST_DRIVER_ID)).thenReturn(config);
	}

	class DiameterCDRDriverFactoryImplStub extends DiameterCDRDriverFactoryImpl {

		public DiameterCDRDriverFactoryImplStub(AAAServerContext serverContext) {
			super(serverContext);
		}

		@Override
		CDRDriver<DiameterPacket> createCDRDriver(DriverConfiguration driverConfiguration)
				throws DriverInitializationFailedException {
			DiameterCSVDriver csvDriver = new DiameterCSVDriver(null, null, null);
			return csvDriver;
		}
	}
}
