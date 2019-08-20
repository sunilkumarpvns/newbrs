package com.elitecore.corenetvertex.pm.rnc.notification;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

@RunWith(JUnitParamsRunner.class)
public class ThresholdEventTest {

	private ThresholdEvent thresholdEvent;

	@Before
	public void setUp() {
		thresholdEvent = spy(new ThresholdEvent("rateCardId",
		50, new Template("templateId", "name", "subject", "data"),
				new Template("templateId", "name", "subject", "data")));
	}

	public Object[][] dataProviderForIsEligibleShouldBeCalledWithLastUsedSubscriptionBalance() {
		RnCNonMonetaryBalance previous1 = createBalance(50);
		RnCNonMonetaryBalance current1 = previous1.copy();
		current1.setBillingCycleAvailable(50);
		RnCNonMonetaryBalance previous2 = createBalance(50);
		RnCNonMonetaryBalance current2 = previous2.copy();
		current2.setBillingCycleAvailable(40);
		Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> scenario1 = new IdentityHashMap<>();
		scenario1.put(previous1, current1);
		scenario1.put(previous2, current2);

		RnCNonMonetaryBalance previous3 = createBalance(50);
		RnCNonMonetaryBalance current3 = previous3.copy();
		current3.setBillingCycleAvailable(40);
		RnCNonMonetaryBalance previous4 = createBalance(50);
		RnCNonMonetaryBalance current4 = previous4.copy();
		current4.setBillingCycleAvailable(50);
		Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> scenario2 = new IdentityHashMap<>();
		scenario2.put(previous3, current3);
		scenario2.put(previous4, current4);

		return new Object[][] {
				{
						scenario1, previous2, current2
				},
				{
						scenario2, previous3, current3
				},

		};
	}


	@Test
	@Parameters(method = "dataProviderForIsEligibleShouldBeCalledWithLastUsedSubscriptionBalance")
	public void isEligibleShouldBeCalledWithLastUsedSubscriptionBalance(Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> prevToCurrBalance,
																		RnCNonMonetaryBalance expectedPrev,
																		RnCNonMonetaryBalance expectedCurr) {


		thresholdEvent.isEligible(prevToCurrBalance);

		Mockito.verify(thresholdEvent, Mockito.times(1)).isEligible(same(expectedPrev), same(expectedCurr));

	}


	public Object[][] dataProviderForisEligibleShouldNotCalledWhenSubscriptionBalanceIsNotUsed() {
		RnCNonMonetaryBalance previous1 = createBalance(50);
		RnCNonMonetaryBalance current1 = previous1.copy();
		current1.setBillingCycleAvailable(50);
		RnCNonMonetaryBalance previous2 = createBalance(50);
		RnCNonMonetaryBalance current2 = previous2.copy();
		current2.setBillingCycleAvailable(50);
		Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> scenario1 = new IdentityHashMap<>();
		scenario1.put(previous1, current1);
		scenario1.put(previous2, current2);

		RnCNonMonetaryBalance previous3 = createBalance(100);
		RnCNonMonetaryBalance current3 = previous3.copy();
		current3.setBillingCycleAvailable(100);
		RnCNonMonetaryBalance previous4 = createBalance(100);
		RnCNonMonetaryBalance current4 = previous4.copy();
		current4.setBillingCycleAvailable(100);
		Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> scenario2 = new IdentityHashMap<>();
		scenario2.put(previous3, current3);
		scenario2.put(previous4, current4);

		return new Object[][] {
				{
						scenario1
				},
				{
						scenario2
				},

		};
	}

	@Test
	@Parameters(method = "dataProviderForisEligibleShouldNotCalledWhenSubscriptionBalanceIsNotUsed")
	public void isEligibleShouldNotCalledWhenSubscriptionBalanceIsNotUsed(Map<RnCNonMonetaryBalance, RnCNonMonetaryBalance> prevToCurrBalance) {


		thresholdEvent.isEligible(prevToCurrBalance);

		Mockito.verify(thresholdEvent, never()).isEligible(any(RnCNonMonetaryBalance.class), any(RnCNonMonetaryBalance.class));

	}

	private RnCNonMonetaryBalance createBalance(long balance) {
		return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),
				"rateCardId",
				ResetBalanceStatus.NOT_RESET,
				"0",
				ChargingType.EVENT)
				.withBillingCycleTimeBalance(100, balance)
				.withBillingCycleResetTime(System.currentTimeMillis()+100000)
				.build();
	}
}