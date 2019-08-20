package com.elitecore.netvertex.usagemetering;

import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.netvertex.core.NetVertexServerContext;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class UsageMonitoringStatisticsCounterTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	@Mock
	private NetVertexServerContext serverContext;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		tempFolder.newFolder("system");
		String serverHome = tempFolder.getRoot().getAbsolutePath();
		Mockito.when(serverContext.getServerHome()).thenReturn(serverHome);

		TaskScheduler taskScheduler = mock(TaskScheduler.class);
		when(serverContext.getTaskScheduler()).thenReturn(taskScheduler);
		when(taskScheduler.scheduleIntervalBasedTask(Mockito.any(IntervalBasedTask.class))).thenReturn(null);
	}

	@Test
	public void test_init_counter_should_be_zero_when_serailzed_counters_not_found() throws Exception {
		UsageMonitoringStatisticsCounter statisticsCounter = new UsageMonitoringStatisticsCounter(serverContext);
		statisticsCounter.init();
		assertEquals(0, statisticsCounter.getCurrentDayUsage());
		assertEquals(0, statisticsCounter.getTotalUsageForLast24Hours());
		assertEquals(0L, statisticsCounter.getTotalUsageReportedInYesterDay());
		assertEquals(0, statisticsCounter.getUsageReportedInLastHour());
	}

	public Object[][] dataProviderFor_test_counterShouldBeZeroWhenNextInitIsMoreThan24Hour() {

		final Calendar currentDate1 = Calendar.getInstance();
		currentDate1.add(Calendar.DAY_OF_MONTH, 2);
		final Calendar currentDate2 = Calendar.getInstance();
		currentDate2.add(Calendar.DAY_OF_MONTH, 3);
		final Calendar currentDate3 = Calendar.getInstance();
		currentDate3.add(Calendar.DAY_OF_MONTH, 4);

		return new Object[][] {
				{
						currentDate1
		},
				{
						currentDate2
		},
				{
						currentDate3
		}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_test_counterShouldBeZeroWhenNextInitIsMoreThan24Hour")
	public void test_init_counter_should_be_zero_when_next_init_is_more_than_48Hour(final Calendar nextInitDate) throws Exception {
		int totalUsageReported = 1000;
		UsageMonitoringStatisticsCounter counter1 = new UsageMonitoringStatisticsCounter(serverContext);
		counter1.init();
		counter1.incrementHourlyUsage(totalUsageReported);
		counter1.serialize();

		UsageMonitoringStatisticsCounter counter2 = new UsageMonitoringStatisticsCounter(serverContext) {
			protected Calendar getCurrentDate() {
				return nextInitDate;
			};
		};

		counter2.init();
		assertEquals(0, counter2.getCurrentDayUsage());
		assertEquals(0, counter2.getTotalUsageForLast24Hours());
		assertEquals(0, counter2.getTotalUsageReportedInYesterDay());
		assertEquals(0, counter2.getUsageReportedInLastHour());
	}

	public Object[][] dataProviderFor_test_counterShouldBeContinueIfNextInitIsWithin1Hour() {

		
		// consecutive init is falling within same hour
		Calendar param1_syncTime = Calendar.getInstance();
		param1_syncTime.set(2016, 1, 1, 10, 30, 30);
		Calendar param1_nextInitTime = Calendar.getInstance();
		param1_nextInitTime.set(2016, 1, 1, 10, 59, 59);
		long reportedUsage = 1000;
		UsageCounterParam param1 = new UsageCounterParam(param1_syncTime, param1_nextInitTime, reportedUsage, reportedUsage, 0, 0);

		// consecutive init is falling within same hour
		Calendar param3_syncTime = Calendar.getInstance();
		param3_syncTime.set(2016, 1, 1, 10, 30, 00);
		Calendar param3_nextInitTime = Calendar.getInstance();
		param3_nextInitTime.set(2016, 1, 1, 11, 29, 59);
		UsageCounterParam param3 = new UsageCounterParam(param3_syncTime, param3_nextInitTime, reportedUsage, reportedUsage, 0, reportedUsage);

		
		// consecutive init falling within hour but day is changed
		Calendar param4_syncTime = Calendar.getInstance();
		param4_syncTime.set(2016, 1, 1, 23, 30, 00);
		Calendar param4_nextInitTime = Calendar.getInstance();
		param4_nextInitTime.set(2016, 1, 2, 0, 15, 59);
		UsageCounterParam param4 = new UsageCounterParam(param4_syncTime, param4_nextInitTime, 0, reportedUsage, reportedUsage, reportedUsage);

		return new Object[][] {
				{
						param1
		},
				{
						param3
		},
		{
			param4
}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_test_counterShouldBeContinueIfNextInitIsWithin1Hour")
	public void test_init_counter_should_be_continue_if_next_init_is_within_1Hour(final UsageCounterParam usageCounterParam)
			throws Exception {

		int totalUsageReported = 1000;
		UsageMonitoringStatisticsCounter counter1 = new UsageMonitoringStatisticsCounter(serverContext) {
			@Override
			protected Calendar getCurrentDate() {
				return usageCounterParam.syncTime;
			}
		};
		counter1.init();
		counter1.incrementHourlyUsage(totalUsageReported);
		counter1.serialize();

		UsageMonitoringStatisticsCounter counter2 = new UsageMonitoringStatisticsCounter(serverContext) {
			protected Calendar getCurrentDate() {
				return usageCounterParam.nextInitTime;
			};
		};

		counter2.init();
		assertEquals(usageCounterParam.getCurrentDayUsage(), counter2.getCurrentDayUsage());
		assertEquals(usageCounterParam.getLast24hourUsage(), counter2.getTotalUsageForLast24Hours());
		assertEquals(usageCounterParam.getYesterDayUsage(), counter2.getTotalUsageReportedInYesterDay());
		assertEquals(usageCounterParam.getLastHourUsage(), counter2.getUsageReportedInLastHour());
	}

	public Object[][] dataProviderFor_test_init_counter_should_be_zero_for_difference_hour_when_next_init_time_is_is_between_1_and_24_hour() {

		Calendar param1_syncTime = Calendar.getInstance();
		param1_syncTime.set(2016, 1, 1, 10, 1, 30);
		Calendar param1_nextInitTime = Calendar.getInstance();
		param1_nextInitTime.set(2016, 1, 1, 10, 21, 00);
		long reportedUsage = 1000;
		UsageCounterParam param1 = new UsageCounterParam(param1_syncTime, param1_nextInitTime, reportedUsage, reportedUsage, 0, 0);

		Calendar param2_syncTime = Calendar.getInstance();
		param2_syncTime.set(2016, 1, 1, 1, 1, 00);
		Calendar param2_nextInitTime = Calendar.getInstance();
		param2_nextInitTime.set(2016, 1, 1, 2, 31, 00);
		UsageCounterParam param2 = new UsageCounterParam(param2_syncTime, param2_nextInitTime, reportedUsage, reportedUsage, 0, reportedUsage);

		Calendar param3_syncTime = Calendar.getInstance();
		param3_syncTime.set(2016, 1, 1, 1, 1, 00);
		Calendar param3_nextInitTime = Calendar.getInstance();
		param3_nextInitTime.set(2016, 1, 1, 3, 31, 00);
		UsageCounterParam param3 = new UsageCounterParam(param3_syncTime, param3_nextInitTime, reportedUsage, reportedUsage, 0, 0);

		
		Calendar param4_syncTime = Calendar.getInstance();
		param4_syncTime.set(2016, 1, 1, 1, 1, 00);
		Calendar param4_nextInitTime = Calendar.getInstance();
		param4_nextInitTime.set(2016, 1, 2, 0, 31, 00);
		UsageCounterParam param4 = new UsageCounterParam(param4_syncTime, param4_nextInitTime, 0, reportedUsage, reportedUsage, 0);

		
		return new Object[][] {
				{
						param1, reportedUsage
		},
				{
						param2, reportedUsage
		},
				{
						param3, reportedUsage
		},
		{
			param4, reportedUsage
}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_test_init_counter_should_be_zero_for_difference_hour_when_next_init_time_is_is_between_1_and_24_hour")
	public void test_init_counter_should_be_zero_for_difference_hour_when_next_init_time_is_is_from_1_to_24_hour(
			final UsageCounterParam usageCounterParam, long usageReport) throws Exception {

		UsageMonitoringStatisticsCounter counter1 = new UsageMonitoringStatisticsCounter(serverContext) {
			protected Calendar getCurrentDate() {
				return usageCounterParam.getSyncTime();
			};
		};

		counter1.init();
		counter1.incrementHourlyUsage(usageReport);
		counter1.serialize();

		assertEquals(usageReport, counter1.getCurrentDayUsage());
		assertEquals(usageReport, counter1.getTotalUsageForLast24Hours());
		assertEquals(0, counter1.getTotalUsageReportedInYesterDay());
		assertEquals(0, counter1.getUsageReportedInLastHour());

		UsageMonitoringStatisticsCounter counter2 = new UsageMonitoringStatisticsCounter(serverContext) {
			protected Calendar getCurrentDate() {
				return usageCounterParam.getNextInitTime();
			};
		};
		counter2.init();
		assertEquals(usageCounterParam.getCurrentDayUsage(), counter2.getCurrentDayUsage());
		assertEquals(usageCounterParam.getLast24hourUsage(), counter2.getTotalUsageForLast24Hours());
		assertEquals(usageCounterParam.getYesterDayUsage(), counter2.getTotalUsageReportedInYesterDay());
		assertEquals(usageCounterParam.getLastHourUsage(), counter2.getUsageReportedInLastHour());
	}
	
	
	public Object[][] dataProviderFor_test_init_previous_counter_should_be_maintained_if_next_init_is_before_end_of_next_day() {

		Calendar param1_syncTime = Calendar.getInstance();
		param1_syncTime.set(2016, 1, 1, 6, 10, 0);
		Calendar param1_nextInitTime = Calendar.getInstance();
		param1_nextInitTime.set(2016, 1, 2, 6, 11, 59);
        param1_nextInitTime.set(Calendar.MILLISECOND, 000);
		long reportedUsage = 1000;
		UsageCounterParam param1 = new UsageCounterParam(param1_syncTime, param1_nextInitTime, 0, 0, reportedUsage, 0);

		Calendar param2_syncTime = Calendar.getInstance();
		param2_syncTime.set(2016, 1, 1, 6, 10, 00);
		Calendar param2_nextInitTime = Calendar.getInstance();
		param2_nextInitTime.set(2016, 1, 2, 23, 59, 59);
        param2_nextInitTime.set(Calendar.MILLISECOND, 000);
		UsageCounterParam param2 = new UsageCounterParam(param2_syncTime, param2_nextInitTime,  0, 0, reportedUsage, 0);

		return new Object[][] {
				{
						param1, reportedUsage
		},
				{
						param2, reportedUsage
		}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_init_previous_counter_should_be_maintained_if_next_init_is_before_end_of_next_day")
	public void test_init_previous_counter_should_be_maintained_if_next_init_is_before_end_of_next_day(
		final UsageCounterParam usageCounterParam, long usageReport) throws Exception {
		UsageMonitoringStatisticsCounter counter1 = new UsageMonitoringStatisticsCounter(serverContext) {
			protected Calendar getCurrentDate() {
				return usageCounterParam.getSyncTime();
			};
		};

		counter1.init();
		counter1.incrementHourlyUsage(usageReport);
		counter1.serialize();

		assertEquals(usageReport, counter1.getCurrentDayUsage());
		assertEquals(usageReport, counter1.getTotalUsageForLast24Hours());
		assertEquals(0, counter1.getTotalUsageReportedInYesterDay());
		assertEquals(0, counter1.getUsageReportedInLastHour());

		UsageMonitoringStatisticsCounter counter2 = new UsageMonitoringStatisticsCounter(serverContext) {
			protected Calendar getCurrentDate() {
				return usageCounterParam.getNextInitTime();
			};
		};
		counter2.init();
		assertEquals(usageCounterParam.getCurrentDayUsage(), counter2.getCurrentDayUsage());
		assertEquals(usageCounterParam.getLast24hourUsage(), counter2.getTotalUsageForLast24Hours());
		assertEquals(usageCounterParam.getYesterDayUsage(), counter2.getTotalUsageReportedInYesterDay());
		assertEquals(usageCounterParam.getLastHourUsage(), counter2.getUsageReportedInLastHour());
	}

	private static class UsageCounterParam {
		private Calendar syncTime;
		private Calendar nextInitTime;
		private long currentDayUsage;
		private long last24hourUsage;
		private long yesterDayUsage;
		private long lastHourUsage;

		public UsageCounterParam(Calendar syncTime, Calendar nextInitTime, long currentDayUsage, long last24hourUsage, long yesterDayUsage,
				long lastHourUsage) {
			this.syncTime = syncTime;
			this.nextInitTime = nextInitTime;
			this.currentDayUsage = currentDayUsage;
			this.last24hourUsage = last24hourUsage;
			this.yesterDayUsage = yesterDayUsage;
			this.lastHourUsage = lastHourUsage;
		}

		public Calendar getSyncTime() {
			return syncTime;
		}

		public Calendar getNextInitTime() {
			return nextInitTime;
		}

		public long getCurrentDayUsage() {
			return currentDayUsage;
		}

		public long getLast24hourUsage() {
			return last24hourUsage;
		}

		public long getYesterDayUsage() {
			return yesterDayUsage;
		}

		public long getLastHourUsage() {
			return lastHourUsage;
		}
	}
}
