package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class TotalBalanceTest {

    public class BalanceExist {
        @Test
        public void BalanceIsExitWhenAllBalanceIsExit() {
            TotalBalance totalBalance = new TotalBalance(
                    new BalanceImpl(1,1,1,1),
                    new BalanceImpl(1,1,1,1),
                    new BalanceImpl(1,1,1,1),
                    new BalanceImpl(1,1,1,1)
            );

            assertThat(totalBalance.isExist(), is(true));
        }

        public class BalanceIsNotExitWhenAnyOneBalanceIsNotExit{

            @Test
            public void DailyBalanceIsNotExit() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(0,1,1,1),
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(1,1,1,1)
                );

                assertThat(totalBalance.isExist(), is(false));
            }

            @Test
            public void WeeklyBalanceIsNotExit() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(0,1,1,1),
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(1,1,1,1)
                );

                assertThat(totalBalance.isExist(), is(false));
            }

            @Test
            public void CustomBalanceIsNotExit() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(0,1,1,1),
                        new BalanceImpl(1,1,1,1)
                );

                assertThat(totalBalance.isExist(), is(false));
            }

            @Test
            public void BillingCycleBalanceIsNotExit() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(1,1,1,1),
                        new BalanceImpl(0,1,1,1)
                );

                assertThat(totalBalance.isExist(), is(false));
            }
        }
    }

    public class BalanceCheckReturnLowestBalance {

        public class totalBalanceCheck {
            @Test
            public void DailyBalanceIsLowes() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(1,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.total(), is(1l));
            }

            @Test
            public void WeeklyBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(1,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.total(), is(1l));
            }

            @Test
            public void CustomBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(1,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.total(), is(1l));
            }

            @Test
            public void BillingCycleBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(1,2,2,2)
                );

                assertThat(totalBalance.total(), is(1l));
            }

        }

        public class DownloadBalanceCheck {
            @Test
            public void DailyBalanceIsLowes() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,1,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.download(), is(1l));
            }

            @Test
            public void WeeklyBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,1,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.download(), is(1l));
            }

            @Test
            public void CustomBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,1,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.download(), is(1l));
            }

            @Test
            public void BillingCycleBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,1,2,2)
                );

                assertThat(totalBalance.download(), is(1l));
            }

        }

        public class uploadBalanceCheck {
            @Test
            public void DailyBalanceIsLowes() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,1,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.upload(), is(1l));
            }

            @Test
            public void WeeklyBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,1,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.upload(), is(1l));
            }

            @Test
            public void CustomBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,1,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.upload(), is(1l));
            }

            @Test
            public void BillingCycleBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,1,2)
                );

                assertThat(totalBalance.upload(), is(1l));
            }

        }

        public class timeBalanceCheck {
            @Test
            public void DailyBalanceIsLowes() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,1),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.time(), is(1l));
            }

            @Test
            public void WeeklyBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,1),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.time(), is(1l));
            }

            @Test
            public void CustomBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,1),
                        new BalanceImpl(2,2,2,2)
                );

                assertThat(totalBalance.time(), is(1l));
            }

            @Test
            public void BillingCycleBalanceIsLowest() {
                TotalBalance totalBalance = new TotalBalance(
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,2),
                        new BalanceImpl(2,2,2,1)
                );

                assertThat(totalBalance.time(), is(1l));
            }

        }

    }

    public class SubtractBalance {
    	
    	private Balance billingBalance = mock(Balance.class);
    	private Balance dailyBalance = mock(Balance.class);
    	private Balance weeklyBalance = mock(Balance.class);
    	private Balance customBalance = mock(Balance.class);
    	
    	private Balance balanceToSubtract = new BalanceImpl(1, 2, 3, 4);
    	
    	@Test
    	public void subtractsGivenBalanceFromAllComponentBalances() {
    		TotalBalance totalBalance = new TotalBalance(dailyBalance, weeklyBalance, customBalance, billingBalance);
    		
    		totalBalance.subtract(balanceToSubtract);
    		
    		verify(billingBalance).subtract(balanceToSubtract);
    		verify(dailyBalance).subtract(balanceToSubtract);
    		verify(weeklyBalance).subtract(balanceToSubtract);
    		verify(customBalance).subtract(balanceToSubtract);
    	}
    }

}
