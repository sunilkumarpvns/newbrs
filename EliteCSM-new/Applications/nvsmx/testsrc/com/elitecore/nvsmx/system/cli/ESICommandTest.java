package com.elitecore.nvsmx.system.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.elitecore.nvsmx.remotecommunications.NVSMXEndPoint;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.nvsmx.remotecommunications.EndPoint;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;

@RunWith(JUnitParamsRunner.class)
public class ESICommandTest {

	private static final String OPTION_VIEW = "-view";
	private static final String OPTION_SCAN = "-scan";
	private static final String MSG_NO_ESI_INSTANCE_FOUND = "NO ESI INSTANCE FOUND";
	private static final String MSG_NO_INSTANCE_FOUND_WITH_NAME = "NO INSTANCE FOUND WITH NAME: ";
	private static final String COMMAND_NAME = "esi";

	@Test
	public void test_getCommandName_returns_CommandName() {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		assertSame("esi", command.getCommandName());
	}

	@Test
	@Parameters({ "?", "-help", "-HELP", "-HeLp", "-HeLP", "-hELP", "-HELp", "-helP", "help", "HELP", "HeLp", "HeLP", "hELP", "HELp", "helP" })
	public void test_getHelpMsg_should_call_when_help_parameter_is_passed(String parameter) {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		command.execute(parameter);
		verify(command).getHelpMsg();
	}

	@Test
	@Parameters({ "", "   ", "\t", "\n", "   \t    \n" })
	public void test_getHelpMsg_should_call_when_no_parameter_passed(String parameter) throws Exception {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		command.execute(parameter);
		verify(command).getHelpMsg();
	}

	@Test
	@Parameters({ "view", "scan", "xyz" })
	public void test_getHelpMsg_should_call_when_InvalidArgument_provided(String parameter) {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		command.execute(parameter);
		verify(command).getHelpMsg();
	}

	@Test
	@Parameters({ "-view d", "-view a b" })
	public void test_getHelpMsg_should_call_when_view_option_passed_with_argument(String argument) {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		command.execute(argument);
		verify(command).getHelpMsg();
	}
	
	@Test
	public void test_getHelpMsg_should_call_when_scan_option_passed_with_no_argument() {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		command.execute("-scan");
		verify(command).getHelpMsg();
	}
	
	@Test
	public void test_getHelpMsg_should_call_when_scan_option_passed_with_more_than_one_argument() {
		ESICommand command = spy(new ESICommand(mock(EndPointManager.class)));
		command.execute("-scan a b");
		verify(command).getHelpMsg();
	}

	@Test
	public void test_view_ProperMessageShouldBeReturned_When_NoInstanceExistInSystem() throws Exception {
		EndPointManager endPointManager = createEndPointManagerWithoutAnyInstances();
		ESICommand command = new ESICommand(endPointManager);
		assertEquals(MSG_NO_ESI_INSTANCE_FOUND, command.execute(OPTION_VIEW));
	}

	@Test
	public void test_view_EndPointGetInstanceShouldCall_When_ViewOptionIsProvided() throws Exception {
		// setup
		EndPointManager endPointManager = mock(EndPointManager.class);
		EndPoint endPoint1 = createEndPoint();
		EndPoint endPoint2 = createEndPoint();
		NVSMXEndPoint endPoint3 = createNVSMXEndPoint();
		NVSMXEndPoint endPoint4 = createNVSMXEndPoint();
		when(endPointManager.getAllNetvertexEndPoint()).thenReturn(Arrays.asList(endPoint1, endPoint2));
		when(endPointManager.getALLNvsmxEndPoints()).thenReturn(Arrays.asList(endPoint3, endPoint4));
		ESICommand command = new ESICommand(endPointManager);
		command.execute(OPTION_VIEW);

		verify(endPoint1, times(1)).getInstanceData();
		verify(endPoint2, times(1)).getInstanceData();
		verify(endPoint3, times(1)).getInstanceData();
		verify(endPoint4, times(1)).getInstanceData();
	}


	@Test
	public void test_scan_ProperMessageShouldBeReturned_When_NoInstanceExistInSystem() throws Exception {
		EndPointManager endPointManager = createEndPointManagerWithoutAnyInstances();
		ESICommand command = new ESICommand(endPointManager);
		String parameter = "instanceName";
		assertEquals(MSG_NO_INSTANCE_FOUND_WITH_NAME + parameter, command.execute(OPTION_SCAN + " " + parameter));
	}

	@Test
	@Parameters({"instance","Instance","INSTANCE","inSTANCE"})
	public void test_scan_EndPointScanShouldCall_When_ScanOptionIsProvidedForPerticularInstanceName(String scanOption) throws Exception {
		// setup
		EndPointManager endPointManager = mock(EndPointManager.class);
		String INSTANCE_NAME1 = "instance";
		String INSTANCE_NAME2 = "instance2";
		EndPoint endPoint1 = createEndPoint(INSTANCE_NAME1);
		EndPoint endPoint2 = createEndPoint(INSTANCE_NAME2);
		when(endPointManager.getAllNetvertexEndPoint()).thenReturn(Arrays.asList(endPoint1, endPoint2));
		when(endPoint1.getInstanceData()).thenReturn(new ServerInformation(INSTANCE_NAME1, "ID1"));
		when(endPoint2.getInstanceData()).thenReturn(new ServerInformation(INSTANCE_NAME2, "ID2"));

		// execute
		ESICommand command = new ESICommand(endPointManager);
		command.execute(OPTION_SCAN + " " + scanOption);

		// assert
		verify(endPoint1, times(1)).scan();
		verify(endPoint2, times(0)).scan();
	}
	
	private EndPoint createEndPoint() {
		return createEndPoint("INSTANCE1");
	}

	private NVSMXEndPoint createNVSMXEndPoint(){
		return createNVXMEndPoint("INSTANCE1");
	}

	private NVSMXEndPoint createNVXMEndPoint(String instanceName1) {
		NVSMXEndPoint endPoint1 = mock(NVSMXEndPoint.class);
		when(endPoint1.getInstanceData()).thenReturn(new ServerInformation(instanceName1, "ID1"));
		when(endPoint1.getStatistics()).thenReturn(new DummyStatistics());
		when(endPoint1.getStatus()).thenReturn(EndPointStatus.SHUT_DOWN.getVal());
		return endPoint1;
	}

	private EndPoint createEndPoint(String instanceName1) {
		EndPoint endPoint1 = mock(EndPoint.class);
		when(endPoint1.getInstanceData()).thenReturn(new ServerInformation(instanceName1, "ID1"));
		when(endPoint1.getStatistics()).thenReturn(new DummyStatistics());
		return endPoint1;
	}

	private EndPointManager createEndPointManagerWithoutAnyInstances() {
		EndPointManager endPointManager = mock(EndPointManager.class);
		List<EndPoint> emptyList = Collectionz.newArrayList();
		List<NVSMXEndPoint> emptyNVSMXEndPointList = Collectionz.newArrayList();

		when(endPointManager.getAllNetvertexEndPoint()).thenReturn(emptyList);
		when(endPointManager.getALLNvsmxEndPoints()).thenReturn(emptyNVSMXEndPointList);

		return endPointManager;
	}

	@Test
	public void test_GetHotkeyHelp_GiveDefaultHotKeyOption_When_NoInstanceFound() {
		EndPointManager endPointManager = createEndPointManagerWithoutAnyInstances();
		ESICommand command = new ESICommand(endPointManager);

		String expectedHotKey = "{'" + COMMAND_NAME + "':{'-help':{},'?':{},'" + OPTION_VIEW + "':{},'" + OPTION_SCAN + "':{}}}";
		assertEquals(expectedHotKey, command.getHotkeyHelp());
	}
	
	@Test
	public void test_GetHotkeyHelp_GiveInstanceNamesInScanOption() {
		EndPointManager endPointManager = mock(EndPointManager.class);
		EndPoint endPoint1 = mock(EndPoint.class);
		when(endPointManager.getAllNetvertexEndPoint()).thenReturn(Arrays.asList(endPoint1));

		NVSMXEndPoint endPoint2 = mock(NVSMXEndPoint.class);
		String INSTANCE_NAME1 = "instance1";
		when(endPoint1.getInstanceData()).thenReturn(new ServerInformation(INSTANCE_NAME1, "ID"));

		when(endPointManager.getALLNvsmxEndPoints()).thenReturn(Arrays.asList(endPoint2));
		String INSTANCE_NAME2 = "instance2";
		when(endPoint2.getInstanceData()).thenReturn(new ServerInformation(INSTANCE_NAME2, "ID"));

		ESICommand command = new ESICommand(endPointManager);
		String expectedHotKey = "{'" + COMMAND_NAME + "':{'-help':{},'?':{},'" + OPTION_VIEW + "':{},'" + OPTION_SCAN + "':{'" + INSTANCE_NAME1
				+ "':{},'"+ INSTANCE_NAME2
				+ "':{}}}}";
				
		assertEquals(expectedHotKey, command.getHotkeyHelp());
	}

	private static class DummyStatistics implements ESIStatistics {

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getTypeName() {
			return null;
		}

		@Override
		public String currentStatus() {
			return null;
		}

		@Override
		public float getLastMinAvgResponseTime() {
			return 0;
		}

		@Override
		public float getLastTenMinAvgResponseTime() {
			return 0;
		}

		@Override
		public float getLastHourAvgResponseTime() {
			return 0;
		}

		@Override
		public long getLastScanTimestamp() {
			return 0;
		}

		@Override
		public long getLastDeadTimestamp() {
			return 0;
		}

		@Override
		public long getTotalTimedouts() {
			return 0;
		}

		@Override
		public long getTotalRequests() {
			return 0;
		}

		@Override
		public long getTotalSuccesses() {
			return 0;
		}

		@Override
		public long getTotalErrors() {
			return 0;
		}

		@Override
		public long getDeadCount() {
			return 0;
		}

		@Override
		public long getLastMarkDeadTimestamp() {
			return 0;
		}
	}
}
