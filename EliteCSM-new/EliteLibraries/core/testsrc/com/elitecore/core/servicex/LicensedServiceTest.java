package com.elitecore.core.servicex;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class LicensedServiceTest {

	private static final String ANY_REMARK = ServiceRemarks.PROBLEM_BINDING_IP_PORT.remark;
	private static final String SERVICE_IDENTIFIER = "Licensed Service";
	
	private ServerContext context;
	private EliteService wrappedService;
	private LicensedService licensedService;
	
	@Rule public ExpectedException exception = ExpectedException.none(); 
	
	@Before
	public void setUp() {
		context = mock(ServerContext.class);
		wrappedService = mock(EliteService.class);
		when(wrappedService.getServiceIdentifier()).thenReturn(SERVICE_IDENTIFIER);
		when(wrappedService.getServiceName()).thenReturn(SERVICE_IDENTIFIER);
	}
	
	public class InvalidLicenseContext {
		
		private static final String NON_LICENSED_SERVICE = "NON_LICENSED_SERVICE";

		@Before
		public void setUp() {
			licensedService = new LicensedService(context, wrappedService, NON_LICENSED_SERVICE);
			when(context.isLicenseValid(Mockito.eq(NON_LICENSED_SERVICE), Mockito.anyString())).thenReturn(false);

		}

		private void swallowInitException(LicensedService licensedService) {
			try {
				licensedService.init();
				throw new AssertionError("Wanted but not thrown, instance of " + ServiceInitializationException.class);
			} catch (ServiceInitializationException ex) {
				// chomp
			}
		}
		
		@Test
		public void init_ShouldThrowServiceInitializationException_AndShouldNotCallInitOfWrappedService() throws ServiceInitializationException {
			exception.expect(ServiceInitializationException.class);
			exception.expectMessage(SERVICE_IDENTIFIER + " will not start, "
					+ "Reason: License for "+ NON_LICENSED_SERVICE +" is not acquired or invalid.");
			
			licensedService.init();
		}
		
		@Test
		public void init_ShouldNotCallInitOfWrappedService() throws ServiceInitializationException {
			swallowInitException(licensedService);
			
			verify(wrappedService, never()).init();
		}
		
		@Test
		public void getDescription_ShouldReturnDescriptionWithInvalidLicenseRemark_AndStateShouldBeNotStarted() throws ServiceInitializationException {
			swallowInitException(licensedService);
			
			ServiceDescription description = licensedService.getDescription();
			assertEquals(LifeCycleState.NOT_STARTED.message, description.getStatus());
			assertEquals(ServiceRemarks.INVALID_LICENSE.remark, description.getRemarks());
		}
		
		@Test
		public void stop_ShouldReturnTrue_AndNotCallStopOfWrappedService() throws ServiceInitializationException {
			swallowInitException(licensedService);
			
			boolean result = licensedService.stop();
			
			assertTrue(result);
			verify(wrappedService, never()).stop();
		}
		
		@Test
		public void getRemark_ShouldReturnInvalidLicenseRemark_AndNotCallGetRemarkOfWrappedService() throws ServiceInitializationException {
			swallowInitException(licensedService);
			
			String remark = licensedService.getRemarks();
			
			assertEquals(ServiceRemarks.INVALID_LICENSE.remark, remark);
			
			verify(wrappedService, never()).getRemarks();
		}
		
	}
	
	public class ValidLicenseContext {
		private static final String LICENSED_SERVICE = "LICENSED_SERVICE";
		
		@Before
		public void setUp() {
			licensedService = new LicensedService(context, wrappedService, LICENSED_SERVICE);
			when(context.isLicenseValid(Mockito.eq(LICENSED_SERVICE), Mockito.anyString())).thenReturn(true);

		}

		@Test
		public void init_ShouldCallInitOfWrappedService() throws ServiceInitializationException {
			licensedService.init();
			
			verify(wrappedService, times(1)).init();
		}
		
		@Test
		public void getDescription_ShouldReturnDescriptionOfWrappedService() throws ServiceInitializationException {
			licensedService.init();
			
			ServiceDescription description = licensedService.getDescription();
			ServiceDescription expectedDescription = wrappedService.getDescription();
			
			assertEquals(expectedDescription, description);
		}
		
		@Test
		public void stop_ShouldCallStopOfWrappedService_AndReturnTheResultOfTheCall() throws ServiceInitializationException {
			licensedService.init();
			
			when(wrappedService.stop()).thenReturn(false);
			boolean result = licensedService.stop();
			assertFalse(result);
			
			when(wrappedService.stop()).thenReturn(true);
			result = licensedService.stop();
			assertTrue(result);
			
			verify(wrappedService, times(2)).stop();
		}
		
		@Test
		public void getRemark_ShouldReturnRemarkOfWrappedService() throws ServiceInitializationException {
			licensedService.init();
			
			String expectedRemark = ANY_REMARK;
			when(wrappedService.getRemarks()).thenReturn(expectedRemark);
			
			String remark = licensedService.getRemarks();
			
			assertEquals(expectedRemark, remark);
			
		}
	}
}
