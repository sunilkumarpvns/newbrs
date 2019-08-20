package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class DDFTableImplNonDBTest {

    private DDFTableImpl ddfTable;

    @Mock
    private RepositorySelector repositorySelector;

    @Mock
    private SubscriberRepository subscriberRepository;

    @Before
    public void setUp() {

    MockitoAnnotations.initMocks(this);
        ddfTable = new DDFTableImpl(null,
                null,
                null,
                null,
                repositorySelector);

        when(repositorySelector.select(anyString())).thenReturn(subscriberRepository);

    }

    public class checkForResetBillDate {

        private SPRInfoImpl.SPRInfoBuilder sprInfoBuilder;
        private int billingDate;

        @Before
        public void setUp() {
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.DAY_OF_MONTH,12);
            billingDate = instance.get(Calendar.DATE);

            sprInfoBuilder = new SPRInfoImpl.SPRInfoBuilder();
            sprInfoBuilder.withBillChangeDate(new Timestamp(System.currentTimeMillis() -1));
            sprInfoBuilder.withBillingDate(billingDate);
            sprInfoBuilder.withSubscriberIdentity(UUID.randomUUID().toString());

            Timestamp nextBillDate = new Timestamp(instance.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
            sprInfoBuilder.withNextBillDate(nextBillDate);
        }

        public class NotChangeBillingDateWhen {

            @Test
            public void billChangeDateIsNotFound() throws OperationFailedException, UnauthorizedActionException {
                SPRInfo build = sprInfoBuilder.withBillChangeDate(null).build();
                ddfTable.checkForBillDateChange(build);
                verify(repositorySelector, never()).select(anyString());
            }

            @Test
            public void billResetDateIsNotReached() throws OperationFailedException, UnauthorizedActionException {
                SPRInfo build = sprInfoBuilder
                        .withBillChangeDate(new Timestamp(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)))
                        .build();
                ddfTable.checkForBillDateChange(build);
                verify(repositorySelector, never()).select(anyString());
            }

            @Test
            public void nextBillDateNotFound() throws OperationFailedException, UnauthorizedActionException {
                SPRInfo build = sprInfoBuilder.withNextBillDate(null).build();
                ddfTable.checkForBillDateChange(build);
                verify(repositorySelector, never()).select(anyString());
            }

            @Test
            public void newBillDateIsSameAsPrevious() throws OperationFailedException, UnauthorizedActionException {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2018, 1, 5);
                Timestamp nextBillDate = new Timestamp(calendar.getTimeInMillis());
                sprInfoBuilder.withNextBillDate(nextBillDate);
                sprInfoBuilder.withBillingDate(5);
                SPRInfo build = sprInfoBuilder.build();
                ddfTable.checkForBillDateChange(build);
                verify(repositorySelector, never()).select(anyString());
            }
        }

        @Test
        public void changeBillDateInSprInfoWhenNextBillDateIsChangedAndBillChangeDateIsElapsed() throws OperationFailedException, UnauthorizedActionException {
            SPRInfo sprInfo = sprInfoBuilder.build();
            when(subscriberRepository.getRGNonMonitoryBalance(sprInfo)).thenReturn(new SubscriberNonMonitoryBalance(emptyList()));
            ddfTable.checkForBillDateChange(sprInfo);
            assertThat(sprInfo.getBillingDate(), is(not(billingDate + 1)));
        }

        @Test
        public void notChangeBalanceWhenQuotaNotFound() throws OperationFailedException, UnauthorizedActionException {
            SPRInfo sprInfo = sprInfoBuilder.build();
            when(subscriberRepository.getRGNonMonitoryBalance(sprInfo)).thenReturn(new SubscriberNonMonitoryBalance(emptyList()));
            ddfTable.checkForBillDateChange(sprInfo);
            assertThat(sprInfo.getBillingDate(), is(not(billingDate + 1)));
            verify(subscriberRepository, times(1)).getRGNonMonitoryBalance(sprInfo);
            verify(subscriberRepository, times(0)).updateNextBillingCycleBalance(anySet(), anyInt());
        }

        @Test
        public void callUpdateNextBillingCycleBalanceWithExistingBalanceAndNewBillDateWhenBalanceFound() throws OperationFailedException, UnauthorizedActionException {
            SPRInfo sprInfo = sprInfoBuilder.build();
            NonMonetoryBalance nonMonetoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder()
                    .withBillingCycleResetTime(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1))
                    .withStartTime(System.currentTimeMillis() - 1)
                    .withProductOfferId(UUID.randomUUID().toString())
                    .withQuotaProfileId(UUID.randomUUID().toString())
                    .withPackageId(UUID.randomUUID().toString())
                    .build();
            SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(emptyList());
            subscriberNonMonitoryBalance.addBalance(nonMonetoryBalance);
            when(subscriberRepository.getRGNonMonitoryBalance(sprInfo)).thenReturn(subscriberNonMonitoryBalance);
            ddfTable.checkForBillDateChange(sprInfo);
            assertThat(sprInfo.getBillingDate(), is(not(billingDate + 1)));
            verify(subscriberRepository, times(1)).getRGNonMonitoryBalance(sprInfo);
            verify(subscriberRepository, times(1)).updateNextBillingCycleBalance(subscriberNonMonitoryBalance.getBalances(), billingDate + 1);
        }
    }
}
