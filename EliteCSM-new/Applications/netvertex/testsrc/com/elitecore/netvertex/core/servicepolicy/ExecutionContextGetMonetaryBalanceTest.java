package com.elitecore.netvertex.core.servicepolicy;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class ExecutionContextGetMonetaryBalanceTest {


    private static final String SUBSCRIBER_IDENTITY_VAL = "1020";
    public static final String INR = "INR";
    @Mock private CacheAwareDDFTable ddfTable;
	@Mock private BalanceProvider balanceProvider;
	private SPRInfoImpl sprInfo;
	private PCRFRequestImpl request = new PCRFRequestImpl();
	private PCRFResponseImpl response = new PCRFResponseImpl();
	private FixedTimeSource fixedTimeSource;

	@Before
	public void setUp() throws OperationFailedException {
		MockitoAnnotations.initMocks(this);
		sprInfo = getExpectedSPRInfo();
		fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
		when(ddfTable.getProfile(request)).thenReturn(sprInfo);
	}

	@Rule  public ExpectedException exception = ExpectedException.none();
	
	private @Nonnull SPRInfoImpl getExpectedSPRInfo() {
		SPRInfoImpl sprInfo = spy(new SPRInfoImpl());
		sprInfo.setSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL);
		sprInfo.setBalanceProvider(balanceProvider);
		return sprInfo; 
	}

	@Test
	public void setMonetaryBalanceToPCRFResponse() throws Exception {

		SubscriberMonetaryBalance expectedMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);

		doReturn(expectedMonetaryBalance).when(balanceProvider).getMonetaryBalance(eq(SUBSCRIBER_IDENTITY_VAL), any());

		ExecutionContext executionContext = new ExecutionContext(request, response, ddfTable, INR);

		executionContext.getCurrentMonetaryBalance();

		assertSame(response.getCurrentMonetaryBalance(), expectedMonetaryBalance);
	}


	public class ThrowExceptionWhen {
		@Test
		public void getSPRFail() throws Exception {

			String expectedExceptionMessage = "Generated from Test";
            doThrow(new OperationFailedException(expectedExceptionMessage)).when(ddfTable).getProfile(request);

			ExecutionContext executionContext = new ExecutionContext( request, response, ddfTable, INR);

			exception.expect(OperationFailedException.class);

			exception.expectMessage(expectedExceptionMessage);

			executionContext.getCurrentMonetaryBalance();
		}

		@Test
		public void getMonetaryBalanceFromSPRFail() throws Exception {

			String expectedExceptionMessage = "Generated from Test";

            doThrow(new OperationFailedException(expectedExceptionMessage)).when(balanceProvider).getMonetaryBalance(eq(SUBSCRIBER_IDENTITY_VAL), any());

			ExecutionContext executionContext = new ExecutionContext( request, response, ddfTable, INR);

			exception.expect(OperationFailedException.class);

			exception.expectMessage(expectedExceptionMessage);

			executionContext.getCurrentMonetaryBalance();
		}
	}


	@Test
	public void throwOperationFailExceptionOnSubsequentCallAfterOperationFailExceptionThrows() throws OperationFailedException {
		String expectedExceptionMessage = "Generated from Test";

        doThrow(new OperationFailedException(expectedExceptionMessage)).when(balanceProvider).getMonetaryBalance(eq(SUBSCRIBER_IDENTITY_VAL), any());

		ExecutionContext executionContext = new ExecutionContext( request, response, ddfTable, INR);

		try {
			executionContext.getCurrentMonetaryBalance();
			fail("Should generate Operation fail Exception");
		} catch (OperationFailedException ex) {
			verify(balanceProvider, times(1)).getMonetaryBalance(eq(SUBSCRIBER_IDENTITY_VAL), any());
		}


		for (int noOfSubsequestTry = 0; noOfSubsequestTry < 5; noOfSubsequestTry++) {
			try {
				executionContext.getCurrentMonetaryBalance();
				fail("Should generate Operation fail Exception");
			} catch (OperationFailedException ex) {
				verify(balanceProvider, times(1)).getMonetaryBalance(eq(SUBSCRIBER_IDENTITY_VAL), any());
			}
		}
	}

	
	@Test
	public void notFetchMonetaryBalanceFromRepositoryWhenFoundInResponse() throws Exception {

		SubscriberMonetaryBalance expectedMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
		response.setCurrentMonetaryBalance(expectedMonetaryBalance);

		ExecutionContext executionContext = new ExecutionContext( request, response, CacheAwareDDFTable.getInstance(), INR);
		
		SubscriberMonetaryBalance actualSubscriberMonetaryBalance = executionContext.getCurrentMonetaryBalance();
		
		assertSame(expectedMonetaryBalance, actualSubscriberMonetaryBalance);
		verifyZeroInteractions(ddfTable);
	}

	
	@Test
	public void fetchBalanceFromSPRWhenNotFoundInPCRFResponse() throws Exception {

		SubscriberMonetaryBalance expectedMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
		doReturn(expectedMonetaryBalance).when(balanceProvider).getMonetaryBalance(eq(SUBSCRIBER_IDENTITY_VAL), any());
		ExecutionContext executionContext = new ExecutionContext(request, response, ddfTable, INR);
		
		SubscriberMonetaryBalance actualBalance = executionContext.getCurrentMonetaryBalance();
		
		verify(ddfTable, times(1)).getProfile(request);
		verify(sprInfo, times(1)).getMonetaryBalance(any());
		
		assertSame(expectedMonetaryBalance, actualBalance);
	}



}

