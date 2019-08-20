package com.elitecore.core.util.cli.cmd;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Future;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.cmd.LogMonitorCommandExt;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;
import com.elitecore.core.util.cli.cmd.logmonitorext.impl.BaseMonitor;

/**
 *
 * @author chetan.sankhala
 */
@RunWith(JUnitParamsRunner.class)
public class LogMonitorCommandExtTest {

	private LogMonitorCommandExt logMonitorCommand;
	private DummyMonitor diameterMonitor;
	private DummyMonitor radiusMonitor;
	private DummyMonitor pcrfMonitor;
	
	@Before
	public void beforeClassSetUp() throws Exception {

		diameterMonitor = new DummyMonitor("diameter");
		radiusMonitor = new DummyMonitor("radius");
		pcrfMonitor = new DummyMonitor("pcrf");
		
		/* spied to throw exception of add*/
		diameterMonitor = spy(diameterMonitor);
		radiusMonitor = spy(radiusMonitor);
		pcrfMonitor = spy(pcrfMonitor);

		LogMonitorManager logMonitorManager = new LogMonitorManager();

		logMonitorManager.registerMonitor(diameterMonitor);
		logMonitorManager.registerMonitor(radiusMonitor);
		logMonitorManager.registerMonitor(pcrfMonitor);

		logMonitorCommand = new LogMonitorCommandExt(logMonitorManager);
		logMonitorCommand = spy(logMonitorCommand);
	}

	@Test
	@Parameters({"?","-help","-HELP","-HeLp","-HeLP","-hELP","-HELp","-helP","help","HELP","HeLp","HeLP","hELP","HELp","helP"})
	public void test_helpMessage_should_call_when_help_parameter_is_passed(String parameter){
		logMonitorCommand.execute(parameter);
		verify(logMonitorCommand).getHelpMsg();
		
	}
	
	
	/**
	 * 
	 * getHotKey help show all monitor with predefined operation: ADD,CLEAR,CLEARALL,LIST
	 */
	@Test
	public void test_hotkeyhelp_for_reload_cmd_should_show_all_monitor_with_child_if_registered() {
		
		String expectedHotKeyMsg = "{'" + logMonitorCommand.getCommandName() + "':{"
				+ "'add':{'" + diameterMonitor.getType()	+ "':{},'" + radiusMonitor.getType() + "':{},'" + pcrfMonitor.getType() + "':{}},"
				+ "'clear':{'" + diameterMonitor.getType()	+ "':{},'" + radiusMonitor.getType() + "':{},'" + pcrfMonitor.getType() + "':{}},"
				+ "'list':{'" + diameterMonitor.getType()	+ "':{},'" + radiusMonitor.getType() + "':{},'" + pcrfMonitor.getType() + "':{}},"
				+ "'clearall':{'" + diameterMonitor.getType()	+ "':{},'" + radiusMonitor.getType() + "':{},'" + pcrfMonitor.getType() + "':{}}}}";
		
		assertEquals(expectedHotKeyMsg, logMonitorCommand.getHotkeyHelp());
	}
	
	public Object[][] dataProviderFor_helpMessage_should_call_when_no_parameter_passed() {
		
		return new Object[][] {
				{
					null
				},
				{
					""
				},
				{
					"      "
				},
				{
					"\t"
				},
				{
					"\n"
				},
				{
					"   \t    \n"
				}
		};
			
	}
	
	@Test
	@Parameters(method="dataProviderFor_helpMessage_should_call_when_no_parameter_passed")
	public void test_helpMessage_should_call_when_no_parameter_passed(String parameters) throws Exception {
		logMonitorCommand.execute(parameters);
		verify(logMonitorCommand).getHelpMsg();
	}
	
	/*
	 * scenario like no monitor passed or monitor not exist
	 * */
	@Test
	public void test_ADD_should_provide_proper_message_when_unregistered_monitor_provided() throws Exception {
		String actualMessage = logMonitorCommand.execute("add xyz");
		String expectedMessage = "Invalid Monitor." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void test_ADD_should_provide_proper_message_when_monitor_not_provided() throws Exception {
		String actualMsg = logMonitorCommand.execute("add");
		String expectedMsg = "Monitor not provided." + logMonitorCommand.getHelpMsg();
		
		assertEquals(actualMsg, expectedMsg);
	}

	@Test
	public void test_ADD_MONITOR_should_provide_proper_message_when_no_condition_provided() throws Exception {
		
		String actualMsg = logMonitorCommand.execute("add diameter");
		String expectedMsg = "Condition not provided." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMsg, actualMsg);
		
		//1 for actual and 1 for expected msg generation
		verify(logMonitorCommand,times(2)).getHelpMsg();
	}

	@Test
	public void test_ADD_MONITOR_CONDITION_should_use_default_timePeriod_when_no_time_period_is_provided() throws Exception {
		String actualMsg = logMonitorCommand.execute("add diameter 1=1");
		String expectedMessage = "Condition: 1=1 added successfully in diameter monitor";
		
		assertEquals(expectedMessage, actualMsg);
		verify(diameterMonitor, times(1)).add("1=1", 1);
	}
	
	@Test
	public void test_ADD_MONITOR_CONDITION_should_use_provided_timeperiod() throws Exception {
		String actualMsg = logMonitorCommand.execute("add diameter 1=1 12");
		String expectedMessage = "Condition: 1=1 added successfully in diameter monitor";
		
		assertEquals(expectedMessage, actualMsg);
		verify(diameterMonitor, times(1)).add("1=1", 12);
	}
	
	/*
	 * below test case is commented because in actual scenario, 1=1 xyz becomes a condition considering time period is not provided.
	 * logmonitor expression is actually a logical expression so the parser will throw the exception as here the condition(1=1 xyz) is wrong.
	 * So the actual message should be failed to add condition. In the Unit testing dummy monitor expression is created which doesn't check
	 * for the expression validation so it successfully adds the condition which is wrong behavior.      
	 * 
	 */
	
	/*@Test
	public void test_ADD_MONITOR_CONDITION_should_use_default_timePeriod_when_invalid_time_period_is_provided() throws Exception {
		 xyz is invalid time period  
		String actualMsg = logMonitorCommand.execute("add diameter 1=1 xyz");
		String expectedMessage = "Condition: 1=1 added successfully in diameter monitor";
		
		assertEquals(expectedMessage, actualMsg);
		verify(diameterMonitor, times(1)).add("1=1", 1);
	}
*/	
	/*  adding to throw exception when add Called */
	private void setUpFor_test_ADD_should_provide_proper_message_when_wrong_condition_provided() throws Exception {
		doThrow(new Exception("condition is invalid")).when(diameterMonitor).add(Mockito.anyString(), Mockito.anyLong());
	}
	
	@Test
	public void test_ADD_MONITOR_CONDITION_should_provide_proper_message_when_wrong_condition_provided() throws Exception {
		
		setUpFor_test_ADD_should_provide_proper_message_when_wrong_condition_provided();
		
		String actualMsg = logMonitorCommand.execute("add       diameter xyz");
		String expectedMessage = "Fail to add Condition: xyz. Reason: condition is invalid";
		
		assertEquals(expectedMessage, actualMsg);
	}

	
	Object[][] dataProviderFor_test_Add_operation_should_work_with_extra_space_between_parameters() {
		return new Object[][] {
				{
					"add diameter 1=1          10"
				},
				{
					"add diameter     1=1    10"
				},
				{
					"add      diameter     1=1    10"
				},
				{
					"    add      diameter     1=1    10    "
				},
				{
					"    add      diameter     1=1    10 "
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_Add_operation_should_work_with_extra_space_between_parameters")
	public void test_Add_operation_should_work_with_extra_space_between_parameters(
			String parametersWithSpaces) throws Exception {
		String actualMsg = logMonitorCommand.execute(parametersWithSpaces);
		String expectedMsg = "Condition: 1=1 added successfully in diameter monitor";

		assertEquals(expectedMsg, actualMsg);
		
		verify(diameterMonitor, times(1)).add("1=1", 10);
	}
	
	
	@Test
	public void test_CLEAR_should_provide_proper_message_when_no_parameters_provided() throws Exception {
		String actualMsg = logMonitorCommand.execute("clear");
		String expectedMsg = "Monitor or Condition not provided." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMsg, actualMsg);
		
		verify(diameterMonitor, never()).clear(anyString());
		verify(radiusMonitor, never()).clear(anyString());
		verify(pcrfMonitor, never()).clear(anyString());
	}
	
	@Test
	
	public void test_CLEAR_MONITOR_should_provide_proper_message_when_no_condition_provided() throws Exception {
		String actualMsg = logMonitorCommand.execute("clear diameter");
		String expectedMsg = "Condition not provided." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMsg, actualMsg);
		
		verify(diameterMonitor, never()).clear(anyString());
	}
	
	
	/**
	 * Monitors     conditions
	 * ++++++++     +++++++++++
	 *  diameter 	1=2,1=3
	 * 
	 */
	private void setUp_test_CLEAR_MONITOR_CONDITION_should_call_clear_of_provided_monitor() throws Exception {
		diameterMonitor.add("1=2", 20);
		diameterMonitor.add("1=3", 20);
	}
	
	
	
	@Test
	public void test_CLEAR_MONITOR_CONDITION_should_call_clear_of_provided_monitor() throws Exception {
		
		setUp_test_CLEAR_MONITOR_CONDITION_should_call_clear_of_provided_monitor();
		
		String actualMsg = logMonitorCommand.execute("clear diameter 1=2");
		String expectedMsg = "Condition: 1=2 removed successfully from diameter monitor";
		
		assertEquals(expectedMsg, actualMsg);
		
		verify(diameterMonitor, times(1)).clear("1=2");
		verify(radiusMonitor, never()).clear("1=2");
	}
	
	
	
	/**
	 * Monitors     conditions
	 * ++++++++     +++++++++++
	 *  diameter 	1=2
	 * 	radius      1=2, 1=3
	 *  pcrf        1=4
	 */
	private void setUp_test_CLEAR_CONDITION_should_call_clear_all_monitor_which_have_provided_condition() throws Exception {
		diameterMonitor.add("1=2", 20);
		radiusMonitor.add("1=2", 20);
		radiusMonitor.add("1=3", 20);
		pcrfMonitor.add("1=4", 20);
	}
	
	@Test
	public void test_CLEAR_CONDITION_should_call_clear_all_monitor_which_have_provided_condition() throws Exception {
		
		setUp_test_CLEAR_CONDITION_should_call_clear_all_monitor_which_have_provided_condition();
		
		String actualMsg = logMonitorCommand.execute("clear 1=2");
		
		String expectedMsg = "\nCondition: 1=2 removed successfully from diameter monitor\n"
				+ "Condition: 1=2 removed successfully from radius monitor\n"
				+ "No match found for condition: 1=2 in pcrf monitor\n\n";
		
		assertEquals(expectedMsg, actualMsg);
		verify(diameterMonitor, times(1)).clear("1=2");
		verify(radiusMonitor, times(1)).clear("1=2");
		verify(pcrfMonitor, times(1)).clear("1=2");
	}
	
	
	Object[][] dataProviderFor_test_CLEAR_MONITOR_should_work_with_extra_space_between_parameters() {
		return new Object[][] {
				{
					"clear diameter 1=1       "
				},
				{
					"clear diameter      1=1 "
				},
				{
					"clear      diameter    1=1  "
				},
				{
					"    clear     diameter     1=1  "
				}
		};
	}
	
	/**
	 * Monitors     conditions
	 * ++++++++     +++++++++++
	 *  diameter 	1=1
	 */
	public void setUp_test_CLEAR_MONITOR_should_work_with_extra_space_between_parameters() throws Exception {
		diameterMonitor.add("1=1", 20);
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_CLEAR_MONITOR_should_work_with_extra_space_between_parameters")
	public void test_CLEAR_MONITOR_should_work_with_extra_space_between_parameters(
			String parametersWithSpaces) throws Exception {
		
		setUp_test_CLEAR_MONITOR_should_work_with_extra_space_between_parameters();
		String actualMsg = logMonitorCommand.execute(parametersWithSpaces);
		
		String expectedMsg = "Condition: 1=1 removed successfully from diameter monitor";
		
		assertEquals(expectedMsg, actualMsg);
		verify(diameterMonitor, times(1)).clear("1=1");
	}
	
	Object[][] dataProviderFor_test_CLEAR_should_work_with_extra_space_between_parameters() {
		return new Object[][] {
				{
					"clear 1=2   "
				},
				{
					"clear        1=2   "
				},
				{
					"         clear        1=2   "
				}
		};
	}
	
	/**
	 * Monitors     conditions
	 * ++++++++     +++++++++++
	 *  diameter 	1=2
	 */
	public void setUp_test_CLEAR_should_work_with_extra_space_between_parameters() throws Exception {
		diameterMonitor.add("1=2", 20);
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_CLEAR_should_work_with_extra_space_between_parameters")
	public void test_CLEAR_should_work_with_extra_space_between_parameters(
			String parametersWithSpaces) throws Exception {
		
		setUp_test_CLEAR_should_work_with_extra_space_between_parameters();
		String actualMsg = logMonitorCommand.execute(parametersWithSpaces);
		String expectedMsg = "\nCondition: 1=2 removed successfully from diameter monitor\n"
				+ "No match found for condition: 1=2 in radius monitor\n"
				+ "No match found for condition: 1=2 in pcrf monitor\n\n";
		

		assertEquals(expectedMsg, actualMsg);
		
		verify(diameterMonitor, times(1)).clear("1=2");
		verify(radiusMonitor, times(1)).clear("1=2");
		verify(pcrfMonitor, times(1)).clear("1=2");
	}
	
	@Test
	public void test_LIST_should_call_list_of_all_monitor_registered() {
		
		logMonitorCommand.execute("list");
		
		verify(diameterMonitor, times(1)).list();
		verify(radiusMonitor, times(1)).list();
		verify(pcrfMonitor, times(1)).list();
	}
	
	@Test
	
	public void test_LIST_MONITOR_should_call_list_of_provided_monitor() {
		logMonitorCommand.execute("list diameter");
		
		verify(diameterMonitor, times(1)).list();
		verify(radiusMonitor, never()).list();
		verify(pcrfMonitor, never()).list();
	}
	
	@Test
	public void test_LIST_MONITOR_should_provide_proper_message_when_invalid_monitor_provided() {
		String actualMsg = logMonitorCommand.execute("list xyz");
		String expectedMsg = "Invalid Monitor." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMsg, actualMsg);
	}
	
	@Test
	public void test_LIST_operation_should_show_proper_message_when_extra_parameters_provided() throws Exception {
		
		String actualMsg = logMonitorCommand.execute("list diameter extra");
		String expectedMsg = "LIST operation must not have more than 1 parameters." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMsg, actualMsg);	
	}
	
	Object[][] dataProviderFor_test_LIST_MONITOR_should_work_with_extra_space_between_parameters() {
		return new Object[][] {
				{
					"list diameter   "
				},
				{
					"list        diameter   "
				},
				{
					"         list        diameter   "
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_LIST_MONITOR_should_work_with_extra_space_between_parameters")
	public void test_LIST_MONITOR_should_work_with_extra_space_between_parameters(
			String parametersWithSpaces) throws Exception {
		
		logMonitorCommand.execute(parametersWithSpaces);
		
		verify(diameterMonitor, times(1)).list();
	}
	
	Object[][] dataProviderFor_test_LIST_should_work_with_extra_space_between_parameters() {
		return new Object[][] {
				{
					"list    "
				},
				{
					"         list       "
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_LIST_should_work_with_extra_space_between_parameters")
	public void test_LIST_should_work_with_extra_space_between_parameters(
			String parametersWithSpaces) throws Exception {
		
		logMonitorCommand.execute(parametersWithSpaces);

		verify(diameterMonitor, times(1)).list();
		verify(pcrfMonitor, times(1)).list();
		verify(radiusMonitor, times(1)).list();
	}
	
	@Test
	public void test_CLEARALL_should_provide_proper_message_when_no_parameter_provided() {
		String actualMsg = logMonitorCommand.execute("clearall");
		String expectedMsg = "Monitor not provided." + logMonitorCommand.getHelpMsg();
		
		assertEquals(actualMsg, expectedMsg);
		
		verify(diameterMonitor, never()).clear(anyString());
		verify(radiusMonitor, never()).clear(anyString());
		verify(pcrfMonitor, never()).clear(anyString());
	}
	
	@Test
	
	public void test_CLEARALL_MONITOR_should_provide_proper_message_when_unregistered_monitor_provided() {
		String actualMsg = logMonitorCommand.execute("clearall xyz");
		String expectedMsg = "Invalid Monitor." + logMonitorCommand.getHelpMsg();
		
		assertEquals(actualMsg, expectedMsg);
		
		verify(diameterMonitor, never()).clearAll();
		verify(radiusMonitor, never()).clearAll();
		verify(pcrfMonitor, never()).clearAll();
	}
	
	@Test
	public void test_CLEARALL_MONITOR_should_call_clearAll_method_of_provided_monitor() {
		logMonitorCommand.execute("clearall diameter");
		
		verify(diameterMonitor, times(1)).clearAll();
		verify(radiusMonitor, never()).clearAll();
		verify(pcrfMonitor, never()).clearAll();
	}
	
	@Test
	public void test_CLEARALL_operation_should_show_proper_message_when_extra_parameters_provided() throws Exception {
		
		String actualMsg = logMonitorCommand.execute("clearall diameter extra");
		String expectedMsg = "CLEARALL operation must not have more than 1 parameters." + logMonitorCommand.getHelpMsg();
		
		assertEquals(expectedMsg, actualMsg);	
	}
	
	Object[][] dataProviderFor_test_CLEARALL_should_work_with_extra_space_between_parameters() {
		return new Object[][] {
				{
					"clearall diameter   "
				},
				{
					"clearall        diameter   "
				},
				{
					"         clearall        diameter   "
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_CLEARALL_should_work_with_extra_space_between_parameters")
	public void test_CLEARALL_should_work_with_extra_space_between_parameters(
			String parametersWithSpaces) throws Exception {
		
		logMonitorCommand.execute(parametersWithSpaces);
		
		verify(diameterMonitor, times(1)).clearAll();
	}
	
	Object[][] dataProviderFor_should_provide_proper_message_when_invalid_operation_provided() {
		return new Object[][] {
				{
					"a"
				},
				{
					"addd"
				},
				{
					"adding"
				},
				{
					"24234 cdf"
				},
				{
					"cler"
				},
				{
					"clearr"
				},
				{
					"llist"
				},
				{
					"cler all"
				},
				{
					"clearrall"
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_should_provide_proper_message_when_invalid_operation_provided")
	public void test_logmonitor_should_provide_proper_message_when_invalid_operation_provided(String parameters) throws Exception {
		
		String actualMsg = logMonitorCommand.execute(parameters);
		String expectedMsg = "Invalid Operation" + logMonitorCommand.getHelpMsg();
		
		
		assertEquals(expectedMsg, actualMsg);
	}
	
	private static class DummyMonitor extends BaseMonitor<String, String> {
		
		private String type;
		
		public DummyMonitor(String type) {
			super(new TaskScheduler() {
				
				@Override
				public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
					return null;
				}
				
				@Override
				public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
					return null;
				}

				@Override
				public void execute(Runnable command) {
					
				}
			});
			
			this.type = type;
		}
		
		@Override
		public String getType() {
			return type;
		}

		@Override
		protected MonitorExpression<String, String> createMonitorExpression(
				String condition, long time) throws Exception {
			return new MonitorExpression<String, String>(null, new LogMonitorInfo("1=1", 0, 0, 0));
		}
		
	}
}
