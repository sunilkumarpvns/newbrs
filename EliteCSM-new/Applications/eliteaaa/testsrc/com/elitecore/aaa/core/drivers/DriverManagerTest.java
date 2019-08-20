package com.elitecore.aaa.core.drivers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.cli.ESIScanCommand;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.driverx.IEliteDriver;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.statistics.PermanentFailureStatistics;
import com.elitecore.core.util.cli.cmd.BaseESIScanCommand.ESIStatus;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DriverManagerTest {
	private static final String UNKNOWN_VALUE = "-1";
	private static final DriverTypes ANY_DRIVER_TYPE = DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER;
	private static final String ANY_DRIVER_ID = "1";
	private static final String ANY_DRIVER_NAME = "";
	
	@Rule public ExpectedException exception = ExpectedException.none();
	@Mock private DriverConfigurationProvider driverConfigurationProvider;
	@Mock private DriverConfiguration driverConfiguration;
	@Mock private DriverFactory driverFactory;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testConstructor_ShouldThrowNullPointerException_IfDriverConfigurationProviderIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("configurationProvider is null");
		
		new DriverManager(null, driverFactory);
	}
	
	@Test
	public void testConstructor_ShouldThrowNullPointerException_IfDriverFactoryIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("driverFactory is null");
		
		new DriverManager(driverConfigurationProvider, null);
	}
	
	@Test
	public void testGetDriver_ShouldReturnNull_WhenDriverIdIsUnknown() {
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		assertNull(driverManager.getDriver(UNKNOWN_VALUE));
	}
	
	@Test
	public void testGetDriver_ShouldReturnNull_WhenTheDriverTypeIsUnknownToFactory() {
		when(driverConfiguration.getDriverType()).thenReturn(null);
		when(driverConfiguration.getDriverInstanceId()).thenReturn(ANY_DRIVER_ID);
		when(driverConfigurationProvider.getDriverConfiguration(ANY_DRIVER_ID))
			.thenReturn(driverConfiguration);
		when(driverConfigurationProvider.getDriverConfigurations())
		.thenReturn(Arrays.asList(driverConfiguration));
		when(driverFactory.createDriver(null, ANY_DRIVER_ID))
			.thenReturn(null);
		
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		assertNull(driverManager.getDriver(ANY_DRIVER_ID));
	}
	
	@Test
	public void testGetDriver_ShouldReturnCreatedDriver_WhenDriverTypeIsKnownAndDriverSuccessfullyInitializes() {
		prepareDriverConfiguration(ANY_DRIVER_ID, ANY_DRIVER_NAME);
		IEliteDriver expectedDriver = prepareDriver();
		prepareDriverFactory(expectedDriver, ANY_DRIVER_ID);

		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		IEliteDriver actualDriver = driverManager.getDriver(ANY_DRIVER_ID);
		
		assertNotNull(actualDriver);
		assertSame(expectedDriver, actualDriver);
	}
	
	@Test
	public void testGetDriver_ShouldReturnTheSameDriverInstanceOnSubsequentInvocations_AfterSuccessfullCreationOfDriver() {
		prepareDriverConfiguration(ANY_DRIVER_ID, ANY_DRIVER_NAME);
		prepareDriverFactory(prepareDriver(), ANY_DRIVER_ID);
		
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		IEliteDriver expectedDriver = driverManager.getDriver(ANY_DRIVER_ID);
		assertSame(expectedDriver, driverManager.getDriver(ANY_DRIVER_ID));
	}
	
	@Test
	public void testGetDriver_ShouldReturnNull_WhenDriverFailsToInitialize() throws DriverInitializationFailedException {
		prepareDriverConfiguration(ANY_DRIVER_ID, ANY_DRIVER_NAME);
		IEliteDriver driverStub = prepareFailingDriver();
		prepareDriverFactory(driverStub, ANY_DRIVER_ID);
		
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		assertNull(driverManager.getDriver(ANY_DRIVER_ID));
		verify(driverStub, times(1)).init();
	}
	
	@Test
	public void testGetDriver_ShouldNotCreateDriverOnSubsequentInvocationsAndReturnNull_WhenDriverCreationFailedDuringFirstInvocation() throws DriverInitializationFailedException {
		prepareDriverConfiguration(ANY_DRIVER_ID, ANY_DRIVER_NAME);
		IEliteDriver mockDriver = prepareFailingDriver();
		prepareDriverFactory(mockDriver, ANY_DRIVER_ID);
		
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		driverManager.getDriver(ANY_DRIVER_ID);
		driverManager.getDriver(ANY_DRIVER_ID);
		
		verify(driverFactory, times(1)).createDriver(ANY_DRIVER_TYPE, ANY_DRIVER_ID);
		verify(mockDriver, times(1)).init();
	}

	
	@Test
	public void testGetDriver_ShouldRegisterDriverToESIScanCommandWithFailedStatus_IfDriverInitializationFails_TestForELITEAAA_2858() throws DriverInitializationFailedException {
		String FAILING_DRIVER_ID = "2";
		String FAILING_DRIVER_NAME = "Failing Driver";
		new ESIScanCommandStub();
		
		prepareDriverConfiguration(FAILING_DRIVER_ID, FAILING_DRIVER_NAME);
		IEliteDriver failingDriver = prepareFailingDriver();
		when(failingDriver.getName()).thenReturn(FAILING_DRIVER_NAME);
		prepareDriverFactory(failingDriver, FAILING_DRIVER_ID);
		
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		driverManager.getDriver(FAILING_DRIVER_ID);
		
		assertEquals(ESIStatus.FAIL, ESIScanCommandStub.getESIStatus(FAILING_DRIVER_NAME).getESStatus());
	}
	
	@Test
	public void testGetDriver_ShouldRegisterESIStatisticsWithFailedStatus_IfDriverInitializationFails_TestForELITEAAA_2858() throws DriverInitializationFailedException {
		String FAILING_DRIVER_ID = "3";
		String FAILING_DRIVER_NAME = "Failing Driver 1";
		
		prepareDriverConfiguration(FAILING_DRIVER_ID, FAILING_DRIVER_NAME);
		IEliteDriver failingDriver = prepareFailingDriver();
		when(failingDriver.getName()).thenReturn(FAILING_DRIVER_NAME);
		prepareDriverFactory(failingDriver, FAILING_DRIVER_ID);
		
		DriverManager driverManager = new DriverManager(driverConfigurationProvider, driverFactory);
		driverManager.init();
		
		driverManager.getDriver(FAILING_DRIVER_ID);
		
		assertEquals(PermanentFailureStatistics.FAIL, 
				DriverManager.getDriverStatisticsByName(FAILING_DRIVER_NAME).currentStatus());
	}
	
	/*------------ Helper methods ------------*/
	
	private void prepareDriverFactory(IEliteDriver driverStub, String driverInstanceId) {
		when(driverFactory.createDriver(ANY_DRIVER_TYPE, driverInstanceId))
			.thenReturn(driverStub);
	}

	private IEliteDriver prepareDriver() {
		IEliteDriver driverStub = mock(IEliteDriver.class);
		when(driverStub.getType()).thenReturn(ANY_DRIVER_TYPE.value);
		when(driverStub.getName()).thenReturn(ANY_DRIVER_NAME);
		
		ESIStatistics statisticsStub = mock(ESIStatistics.class);
		when(statisticsStub.getName()).thenReturn(ANY_DRIVER_NAME);
		when(driverStub.getStatistics()).thenReturn(statisticsStub);
		return driverStub;
	}
	
	private IEliteDriver prepareFailingDriver()
	throws DriverInitializationFailedException {
		IEliteDriver mockDriver = prepareDriver();
		doThrow(InitializationFailedException.class).when(mockDriver).init();
		return mockDriver;
	}

	private void prepareDriverConfiguration(String driverInstanceId, String driverName) {
		when(driverConfiguration.getDriverInstanceId()).thenReturn(driverInstanceId);
		when(driverConfiguration.getDriverType()).thenReturn(ANY_DRIVER_TYPE);
		when(driverConfiguration.getDriverName()).thenReturn(driverName);
		when(driverConfigurationProvider.getDriverConfiguration(driverInstanceId))
			.thenReturn(driverConfiguration);
		when(driverConfigurationProvider.getDriverConfigurations()).thenReturn(Arrays.asList(driverConfiguration));
	}
	
	static class ESIScanCommandStub extends ESIScanCommand {
		
		public static ESIStatus getESIStatus(String esiName) {
			for (Map<String, ESIStatus> typeWiseMap : esiStatusMap.values()) {
				for (ESIStatus esiStatus : typeWiseMap.values()) {
					if (esiStatus.getESName().equals(esiName)) {
						return esiStatus;
					}
				}
			}
			return null;
		}
		
		@Override
		public String execute(String parameter) {
			return null;
		}

		@Override
		public String getCommandName() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getHotkeyHelp() {
			return null;
		}
	}
}
