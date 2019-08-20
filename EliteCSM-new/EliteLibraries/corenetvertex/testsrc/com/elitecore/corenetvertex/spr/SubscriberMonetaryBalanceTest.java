package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class SubscriberMonetaryBalanceTest {

    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    @Mock private MonetaryBalance dataBalanceNotExist;
    @Mock private MonetaryBalance mainBalanceNotExist;
    @Mock private MonetaryBalance dataBalanceExist;
    @Mock private MonetaryBalance mainBalanceExist;
    private FixedTimeSource fixedTimeSource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
        this.subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
        when(dataBalanceNotExist.getServiceId()).thenReturn(CommonConstants.MONEY_DATA_SERVICE);
        when(mainBalanceNotExist.getServiceId()).thenReturn(CommonConstants.ALL_MONETARY_SERVICE);
        when(dataBalanceExist.getServiceId()).thenReturn(CommonConstants.MONEY_DATA_SERVICE);
        when(mainBalanceExist.getServiceId()).thenReturn(CommonConstants.ALL_MONETARY_SERVICE);

        when(dataBalanceNotExist.isExist()).thenReturn(false);
        when(mainBalanceNotExist.isExist()).thenReturn(false);
        when(dataBalanceExist.isExist()).thenReturn(true);
        when(mainBalanceExist.isExist()).thenReturn(true);
    }



    public class IsExistFalseWhen {

        public class DataBalanceIsPresentAnd {


            public class MainbalanceIsPresent {

                @Test
                public void dataBalanceNotExistAndMainBalanceNotExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(dataBalanceNotExist);
                    subscriberMonetaryBalance.addMonitoryBalances(mainBalanceNotExist);
                    assertFalse(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(dataBalanceNotExist, times(1)).isExist();
                    verify(mainBalanceNotExist, times(1)).isExist();
                }
            }

            public class MainbalanceIsAbsent {

                @Test
                public void dataBalanceNotExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(dataBalanceNotExist);
                    assertFalse(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(dataBalanceNotExist, times(1)).isExist();
                }
            }
        }

        public class DataBalanceIsAbsentAnd {

            public class MainbalanceIsPresent {

                @Test
                public void mainBalanceIsNotExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(mainBalanceNotExist);
                    assertFalse(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(mainBalanceNotExist, times(1)).isExist();
                }
            }

            @Test
            public void MainBalanceIsAbsent() {
                assertFalse(subscriberMonetaryBalance.isDataBalanceExist());
            }
        }
    }

    public class IsExistTrue {

        public class DataBalanceIsPresentAnd {


            public class MainbalanceIsPresent {

                @Test
                public void dataBalanceExistAndMainBalanceNotExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(dataBalanceExist);
                    subscriberMonetaryBalance.addMonitoryBalances(mainBalanceNotExist);
                    assertTrue(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(mainBalanceNotExist, times(0)).isExist();
                }

                @Test
                public void dataBalanceExistAndMainBalanceExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(dataBalanceExist);
                    subscriberMonetaryBalance.addMonitoryBalances(mainBalanceExist);
                    assertTrue(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(mainBalanceNotExist, times(0)).isExist();
                }
            }

            public class MainbalanceIsAbsent {

                @Test
                public void dataBalanceExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(dataBalanceExist);
                    assertTrue(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(dataBalanceExist, times(1)).isExist();
                }
            }
        }

        public class DataBalanceIsAbsentAnd {

            public class MainbalanceIsPresent {

                @Test
                public void mainBalanceIsExist() {
                    subscriberMonetaryBalance.addMonitoryBalances(mainBalanceExist);
                    assertTrue(subscriberMonetaryBalance.isDataBalanceExist());
                    verify(mainBalanceExist, times(1)).isExist();
                }
            }
        }
    }

    @Test
    public void copyCreateNewInstanceWithAllValueSame() {
        SubscriberMonetaryBalance subscriberMonetaryBalance = createSubscriberMonetaryBalance();
        SubscriberMonetaryBalance subscriberMonetaryBalanceCopy = subscriberMonetaryBalance.copy();
        subscriberMonetaryBalanceCopy.setCurrentTime(subscriberMonetaryBalance.getCurrentTime());

        ReflectionAssert.assertLenientEquals(subscriberMonetaryBalance, subscriberMonetaryBalanceCopy);
    }

    private SubscriberMonetaryBalance createSubscriberMonetaryBalance() {
        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);

        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance());
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance());
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance());
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance());
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance());

        return subscriberMonetaryBalance;
    }

    private MonetaryBalance createMonetaryBalance() {
        return new MonetaryBalance(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                //RandomUtils.nextDouble(),
                RandomUtils.nextDouble(),
                RandomUtils.nextDouble(),
                RandomUtils.nextDouble(),
                RandomUtils.nextLong(),
                RandomUtils.nextLong(),
                RandomUtils.nextLong(),
                RandomUtils.nextLong()%CommonConstants.FUTURE_DATE,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                RandomUtils.nextLong(),
                RandomUtils.nextLong(),
                "",
                "");
    }
}