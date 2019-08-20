package com.elitecore.aaa.core.drivers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.base.SettableTicker;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.DriverProcessTimeoutException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * Tests the contract of base auth driver and the service it provides to the implementing classes,
 * such as counter management.
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class BaseAuthDriverTest {
	private static final String TEST_USER = "TEST_USER";
	private static final long DRIVER_PROCESSING_TIME = TimeUnit.SECONDS.toNanos(1);
	
	private AuthDriverSpy driverSpy;
	private RadAuthRequest request;
	private AAAServerContext mockContext;
	private FakeTaskScheduler fakeTaskScheduler;
	private SettableTicker ticker;
	private long initialTime = System.nanoTime();	
	
	@Before
	public void setUp() throws DriverInitializationFailedException {
		fakeTaskScheduler = new FakeTaskScheduler();
		ticker = new SettableTicker(initialTime);
		mockContext = mock(AAAServerContext.class);
		when(mockContext.getTaskScheduler()).thenReturn(fakeTaskScheduler);
		driverSpy = AuthDriverSpy.create(new BaseAuthDriverStub(mockContext, ticker));
		driverSpy.spy.init();
		request = new RadAuthRequestBuilder().build();
	}
	
	public class ESIStatisticsManagement {

		public class TotalRequestsStatistic {
			
			public class EAPRequest {
				
				@Test
				public void isIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
				}
				
				@Test
				public void isIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
				}
				
				@Test
				public void isIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
					}
				}
				
				@Test
				public void isIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
					}
				}
			}
			
			public class NonEAPRequest {
				
				@Test
				public void isIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
				}
				
				@Test
				public void isIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
				}
				
				@Test
				public void isIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
					}
				}
				
				@Test
				public void isIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalRequests(), is(equalTo(1L)));
					}
				}
				
			}
		}
		
		public class TotalSuccessStatistic {
			
			public class EAPRequest {
				
				@Test
				public void isIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(1L)));
				}
				
				@Test
				public void isIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(1L)));
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(0L)));
					}
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(0L)));
					}
				}
			}
			
			public class NonEAPRequest {
				
				@Test
				public void isIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(1L)));
				}
				
				@Test
				public void isIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(1L)));
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(0L)));
					}
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalSuccesses(), is(equalTo(0L)));
					}
				}
			}
		}
		
		public class TotalTimedoutStatistic {
			
			public class EAPRequest {
				
				@Test
				public void isNotIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(0L)));
				}
				
				@Test
				public void isNotIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(0L)));
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(0L)));
					}
				}
				
				@Test
				public void isIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(1L)));
					}
				}
			}
			
			public class NonEAPRequest {
				
				@Test
				public void isNotIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(0L)));
				}
				
				@Test
				public void isNotIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(0L)));
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(0L)));
					}
				}
				
				@Test
				public void isIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalTimedouts(), is(equalTo(1L)));
					}
				}
			}
		}
		
		public class TotalErrorsStatistic {
			
			public class EAPRequest {
				
				@Test
				public void isNotIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(0L)));
				}
				
				@Test
				public void isNotIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(0L)));
				}
				
				@Test
				public void isIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(1L)));
					}
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(0L)));
					}
				}
			}
			
			public class NonEAPRequest {
				
				@Test
				public void isNotIncrementedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(0L)));
				}
				
				@Test
				public void isNotIncrementedAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(0L)));
				}
				
				@Test
				public void isIncrementedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(1L)));
					}
				}
				
				@Test
				public void isNotIncrementedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeout();
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						assertThat(driverSpy.spy.getStatistics().getTotalErrors(), is(equalTo(0L)));
					}
				}
			}
		}
		
		public class LastMinAverageResponseTimeStatistic {

			public class EAPRequest {
				
				@Test
				public void isMaintainedInMillisecondsUnit() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					executeBackgroundScheduledTasks();
					
					assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
							is(equalTo((float)TimeUnit.NANOSECONDS.toMillis(DRIVER_PROCESSING_TIME))));
				}

				@Test
				public void isCalculatedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					executeBackgroundScheduledTasks();
					
					assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
							is(equalTo((float)TimeUnit.NANOSECONDS.toMillis(DRIVER_PROCESSING_TIME))));
				}

				@Test
				public void isCalculatedIfAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					
					executeBackgroundScheduledTasks();
					
					assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
							is(equalTo((float)TimeUnit.NANOSECONDS.toMillis(DRIVER_PROCESSING_TIME))));
				}
				
				@Test
				public void isNotCalculatedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {

						executeBackgroundScheduledTasks();
						
						assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
								is(equalTo((float)0)));
					}
				}
				
				@Test
				public void isCalculatedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeoutWithDurationInMillisOf(TimeUnit.SECONDS.toMillis(1));
					
					try {
						driverSpy.spy.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						executeBackgroundScheduledTasks();
						
						assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
								is(equalTo((float)TimeUnit.SECONDS.toMillis(1))));
					}
				}
			}
			
			public class NonEAPRequest {
				
				@Test
				public void isMaintainedInMilliseconds() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					executeBackgroundScheduledTasks();
					
					assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
							is(equalTo((float)TimeUnit.NANOSECONDS.toMillis(DRIVER_PROCESSING_TIME))));
				}
				
				@Test
				public void isCalculatedIfAccountDataIsFound() throws DriverProcessFailedException {
					driverSpy.simulateEapAccountData(new AccountData(), request);
					
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					executeBackgroundScheduledTasks();
					
					assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
							is(equalTo((float)TimeUnit.NANOSECONDS.toMillis(DRIVER_PROCESSING_TIME))));
				}
				
				@Test
				public void isCalculatedIfAccountDataIsNotFound() throws DriverProcessFailedException {
					driverSpy.simulateEapNoAccountData(request);
					
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					
					executeBackgroundScheduledTasks();
					
					assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
							is(equalTo((float)TimeUnit.NANOSECONDS.toMillis(DRIVER_PROCESSING_TIME))));
				}
				
				@Test
				public void isNotCalculatedIfDriverProcessingFails() throws DriverProcessFailedException {
					driverSpy.simulateProcessingFailed();
					ticker.setNanoTime(initialTime + DRIVER_PROCESSING_TIME);
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						executeBackgroundScheduledTasks();
						
						assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
								is(equalTo((float)0)));
					}
				}
				
				@Test
				public void isCalculatedIfDriverProcessingTimesOut() throws DriverProcessFailedException {
					driverSpy.simulateProcessingTimeoutWithDurationInMillisOf(TimeUnit.SECONDS.toMillis(1));
					
					try {
						driverSpy.spy.getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
						fail("Expected exception");
					} catch (DriverProcessFailedException ex) {
						executeBackgroundScheduledTasks();
						
						assertThat(driverSpy.spy.getStatistics().getLastMinAvgResponseTime(), 
								is(equalTo((float)TimeUnit.SECONDS.toMillis(1))));
					}
				}
			}
		}
	}
	
	private void executeBackgroundScheduledTasks() {
		fakeTaskScheduler.tick(); // execute background scheduled tasks
	}
		
	private class BaseAuthDriverStub extends BaseAuthDriver {

		public BaseAuthDriverStub(AAAServerContext mockContext, SettableTicker ticker) {
			super(mockContext, ticker);
		}

		@Override
		public String getDriverInstanceId() {
			return "";
		}

		@Override
		public void scan() {
			
		}

		@Override
		public String getName() {
			return "FAKE-DRIVER";
		}

		@Override
		public String getTypeName() {
			return "FAKE-TYPE";
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		protected AccountData fetchAccountData(ServiceRequest serviceRequest, ChangeCaseStrategy caseStrategy,
				boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator,
				String userIdentity) throws DriverProcessFailedException {
			return null;
		}

		@Override
		protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity) {
			return null;
		}

		@Override
		protected int getStatusCheckDuration() {
			return 0;
		}
	}
	
	public static class AuthDriverSpy {
		
		private BaseAuthDriver spy;

		public AuthDriverSpy(BaseAuthDriver driver, BaseAuthDriver spy) {
			this.spy = spy;
		}

		@SuppressWarnings("unchecked")
		public void simulateProcessingTimeout() throws DriverProcessFailedException {
			doThrow(DriverProcessTimeoutException.class).when(spy).fetchAccountData(Mockito.any(ServiceRequest.class), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString(), Mockito.anyString());
			when(spy.fetchAccountData(Mockito.any(ServiceRequest.class), Mockito.anyListOf(String.class), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString())).thenThrow(DriverProcessTimeoutException.class);
		}
		
		public void simulateProcessingTimeoutWithDurationInMillisOf(long duration) throws DriverProcessFailedException {
			doThrow(new DriverProcessTimeoutException(duration, null)).when(spy).fetchAccountData(Mockito.any(ServiceRequest.class), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString(), Mockito.anyString());
			when(spy.fetchAccountData(Mockito.any(ServiceRequest.class), Mockito.anyListOf(String.class), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString())).thenThrow(new DriverProcessTimeoutException(duration, null));
		}

		@SuppressWarnings("unchecked")
		public void simulateProcessingFailed() throws DriverProcessFailedException {
			doThrow(DriverProcessFailedException.class).when(spy).fetchAccountData(Mockito.any(ServiceRequest.class), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString(), Mockito.anyString());
			when(spy.fetchAccountData(Mockito.any(ServiceRequest.class), Mockito.anyListOf(String.class), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString())).thenThrow(DriverProcessFailedException.class);
		}

		public void simulateEapNoAccountData(RadAuthRequest request) throws DriverProcessFailedException {
			doReturn(null).when(spy).fetchAccountData(Mockito.eq(request), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString(), Mockito.anyString());
		}

		public void simulateEapAccountData(AccountData accountData, ServiceRequest request) throws DriverProcessFailedException {
			doReturn(accountData).when(spy).fetchAccountData(Mockito.eq(request), Mockito.any(ChangeCaseStrategy.class), Mockito.anyBoolean(), Mockito.any(StripUserIdentityStrategy.class), Mockito.anyString(), Mockito.anyString());
		}

		public static AuthDriverSpy create(BaseAuthDriver driver) {
			return new AuthDriverSpy(driver, spy(driver));
		}
	}
}
