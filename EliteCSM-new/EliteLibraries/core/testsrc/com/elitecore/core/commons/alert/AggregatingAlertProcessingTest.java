package com.elitecore.core.commons.alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.alert.Alerts;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.serverx.alert.listeners.AggregatingAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;
import com.elitecore.core.systemx.esix.TaskScheduler;

@RunWith(JUnitParamsRunner.class)
public class AggregatingAlertProcessingTest {
	
	private static final String MODULE = "AGGREGATING_ALERT_PROCESSING_TEST";
	private AggregatingAlertProcessor aggregatingAlertProcessor;
	private List<IAlertEnum> alerts = new ArrayList<IAlertEnum>(Arrays.asList(Alerts.values()));
	private IAlertEnum testAlert = new TestAlert();

	@Mock private SystemAlertProcessor alertProcessor;
	@Mock private TaskScheduler taskScheduler;
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws InitializationFailedException {
		MockitoAnnotations.initMocks(this);
		alerts.add(testAlert);
		aggregatingAlertProcessor = new AggregatingAlertProcessor(alertProcessor, taskScheduler, alerts);
		aggregatingAlertProcessor.init();
	}
	
	@Test
	public void testConstructor_ShouldThrowNullPointerException_WhenAlertProcessorIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("alertProcessor is null");
		aggregatingAlertProcessor = new AggregatingAlertProcessor(null, taskScheduler, alerts);
	}
	
	@Test
	public void testConstructor_ShouldThrowNullPointerException_WhenAlertsPassedInConstructorIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("aggregationEnabledAlerts is null");
		aggregatingAlertProcessor = new AggregatingAlertProcessor(alertProcessor, taskScheduler, null);
	}
	
	@Test
	public void testConstructor_ShouldThrowNullPointerException_WhenServerContextIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("taskScheduler is null");
		aggregatingAlertProcessor = new AggregatingAlertProcessor(alertProcessor, null, alerts);
	}
	
	@Test
	public void testInit_ShouldNotThrowAnyException_WhenSomeOfTheAlertsAreNullInConstructor() throws InitializationFailedException {
		List<IAlertEnum> alerts = new ArrayList<IAlertEnum>();
		alerts.add(testAlert);
		alerts.add(null);
		alerts.add(Alerts.DATABASEUP);
		alerts.add(null);
		alerts.add(null);
		aggregatingAlertProcessor = new AggregatingAlertProcessor(alertProcessor, taskScheduler, alerts);
		aggregatingAlertProcessor.init();
		Mockito.verify(alertProcessor,Mockito.times(2)).init();			// 2 times because one here and another in setup
	}
	
	@Test
	@Parameters(method = "dataProviderFor_testHandleSystemAlert_ShouldCallHandleSystemAlertOfBaseAlertProcessor_WhenHandleSystemAlertOfFloodAlertProcessorIsCalled")
	public void testHandleSystemAlert_ShouldCallHandleSystemAlertOfBaseAlertProcessor_WhenHandleSystemAlertOfFloodAlertProcessorIsCalled(String stringValue) {
		SystemAlert systemAlert = new SystemAlert(testAlert, MODULE, AlertSeverity.INFO.name(), MODULE, 0, stringValue);
		aggregatingAlertProcessor.handleSystemAlert(systemAlert);
		Mockito.verify(alertProcessor, Mockito.times(1)).handleSystemAlert(systemAlert);
	}
	
	public Object[][] dataProviderFor_testHandleSystemAlert_ShouldCallHandleSystemAlertOfBaseAlertProcessor_WhenHandleSystemAlertOfFloodAlertProcessorIsCalled() {
		return new Object[][] {
			{"ABC"},
			{"123"},
			{"A1"},
			{null},
			{""}
		};
	}
	
	@Test
	public void testGetProcessorType_ShouldCallGetProcessorTypeOfBaseAlertProcessor_WhenGetProcessorTypeOfFloodAlertProcessorIsCalled() {
		aggregatingAlertProcessor.getAlertProcessorType();
		Mockito.verify(alertProcessor, Mockito.times(1)).getAlertProcessorType();
	}
	
	@Test
	public void testGetProcessorId_ShouldCallGetProcessorIdOfBaseAlertProcessor_WhenGetProcessorIdOfFloodAlertProcessorIsCalled() {
		aggregatingAlertProcessor.getAlertProcessorId();
		Mockito.verify(alertProcessor, Mockito.times(1)).getAlertProcessorId();
	}
	
	/*
	 * init() of Base Alert Processor is called in setUp() of this class
	 */
	@Test
	public void testInit_ShouldCallInitOfBaseAlertProcessor_WhenInitOfFloodAlertProcessorIsCalled() throws InitializationFailedException {
		Mockito.verify(alertProcessor, Mockito.times(1)).init();
	}
	
	@Test
	@Parameters(method = "dataProviderFor_testHandleSystemAlert_ShouldGenerateOnlyOneAlert_WhenTwoAlertWithSameIdIsGenerated")
	public void testHandleSystemAlert_ShouldGenerateOnlyOneAlert_WhenTwoAlertWithSameIdIsGenerated(String stringValue) {
		SystemAlert systemAlert = new SystemAlert(testAlert, MODULE, AlertSeverity.INFO.name(), "test alert", 0, stringValue);
		aggregatingAlertProcessor.handleSystemAlert(systemAlert);

		systemAlert = new SystemAlert(testAlert, MODULE, AlertSeverity.INFO.name(), "test alert", 0, stringValue);
		aggregatingAlertProcessor.handleSystemAlert(systemAlert);
		Mockito.verify(alertProcessor, Mockito.times(1)).handleSystemAlert((SystemAlert)Mockito.anyObject());
	}
	
	public Object[][] dataProviderFor_testHandleSystemAlert_ShouldGenerateOnlyOneAlert_WhenTwoAlertWithSameIdIsGenerated() {
		return new Object[][] {
				{"ABC"},
				{"123"},
				{"A1"},
				{null},
				{""}	
		};
	}
	
	@Test
	public void testHandleAlertFlooding_ShouldGenerateOneAlert_WhenMoreThanOneSameAlertsAreGenerated() {
		generateTenSameAlerts();
		aggregatingAlertProcessor.aggregateAndSendAlerts();
		Mockito.verify(alertProcessor, Mockito.times(2)).handleSystemAlert((SystemAlert)Mockito.anyObject());
	}

	@Test
	public void testHandleAlertFlooding_ShouldContainDescriptionForTestAlert_AsGeneratedInAAAAlertsEnum() {
		ArgumentCaptor<SystemAlert> alertCapture = ArgumentCaptor.forClass(SystemAlert.class);
		generateTenSameAlerts();
		aggregatingAlertProcessor.aggregateAndSendAlerts();
		Mockito.verify(alertProcessor, Mockito.times(2)).handleSystemAlert(alertCapture.capture());
		
		List<SystemAlert> capturedAlerts = alertCapture.getAllValues();
		Assert.assertEquals(2, capturedAlerts.size());
		Assert.assertTrue(capturedAlerts.get(1).getDescription().contains("9 occurances"));
	}
	
	/*
	 * 10 alert will be passed, from them one will be generated and other (9) alerts will be grouped
	 */
	private void generateTenSameAlerts() {
		SystemAlert systemAlert = null;
		for(int i=0 ; i<10 ; i++) {
			systemAlert = new SystemAlert(testAlert, MODULE, AlertSeverity.INFO.name(), MODULE, 0, "XYZ");
			aggregatingAlertProcessor.handleSystemAlert(systemAlert);
		}
	}
	
	@Test
	public void testHandleSystemAlert_shouldThrowNullPointerException_WhenSystemAlertIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("systemAlert is null");
		aggregatingAlertProcessor.handleSystemAlert(null);
	}
	
	@Test
	public void test() {
		aggregatingAlertProcessor.aggregateAndSendAlerts();
	}
	
	class TestAlert implements IAlertEnum {

		@Override
		public String id() {
			return "A0000001";
		}

		@Override
		public String name() {
			return "alertName";
		}

		@Override
		public String oid() {
			return "1.1.1.0.1.0.1";
		}

		@Override
		public String getName() {
			return "ALERT_NAME";
		}

		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return alerts.size() + " occurances";
		}
	}
}
