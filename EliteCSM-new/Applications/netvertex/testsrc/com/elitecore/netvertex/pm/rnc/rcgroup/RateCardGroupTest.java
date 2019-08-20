package com.elitecore.netvertex.pm.rnc.rcgroup;

import java.util.ArrayList;
import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.rnc.ratecard.MonetaryRateCard;
import com.elitecore.netvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@Ignore
@RunWith(HierarchicalContextRunner.class)
public class RateCardGroupTest {
    @Mock
    private RoPolicyContextImpl policyContext;
    @Mock
    private QuotaReservation quotaReservation;
    @Mock
    private NonMonetaryRateCard nonMonetaryRateCard;
    @Mock
    private MonetaryRateCard monetaryRateCard;
    @Mock
    private AccessTimePolicy accessTimePolicy;

    @Before
    public void setup(){
        policyContext = mock(RoPolicyContextImpl.class);
        quotaReservation = mock(QuotaReservation.class);
        nonMonetaryRateCard = mock(NonMonetaryRateCard.class);
        monetaryRateCard = mock(MonetaryRateCard.class);
        accessTimePolicy = mock(AccessTimePolicy.class);

        when(nonMonetaryRateCard.getType()).thenReturn(RateCardType.NON_MONETARY);
        when(monetaryRateCard.getType()).thenReturn(RateCardType.MONETARY);

        IndentingPrintWriter indentingPrintWriter = mock(IndentingPrintWriter.class);

        when(policyContext.getTraceWriter()).thenReturn(indentingPrintWriter);
        when(indentingPrintWriter.append(Mockito.any())).thenReturn(indentingPrintWriter);
    }

    private RateCardGroup createRateCardGroup(NonMonetaryRateCard rateCard) {
        return new RateCardGroup("id", "name", null, null, null,
                rateCard, null, null, 2, accessTimePolicy,
                new ArrayList<>());
    }

    private RateCardGroup createRateCardGroup(MonetaryRateCard rateCard) {
        return new RateCardGroup("id", "name", null, null, null,
                rateCard, null, null, 2, accessTimePolicy,
                new ArrayList<>());
    }

    public class OffPeak{
        @Before
        public void setup() {
            when(accessTimePolicy.applyPolicy()).thenReturn(100L);
        }

        public class MonetaryRC{
            @Test
            public void applyWhenSlotIsApplicableButThereIsNoBalanceAvailableThenReturnFalse(){
                when(monetaryRateCard.apply(policyContext,quotaReservation,null)).thenReturn(false);
                RateCardGroup rateCardGroup = createRateCardGroup(monetaryRateCard);
                Assert.assertFalse(rateCardGroup.apply(policyContext,quotaReservation,null));
            }

            @Test
            public void applyWhenSlotIsApplicableAndBalanceAvailableThenReturnTrue(){
                when(monetaryRateCard.apply(policyContext,quotaReservation,null)).thenReturn(true);
                RateCardGroup rateCardGroup = createRateCardGroup(monetaryRateCard);
                Assert.assertTrue(rateCardGroup.apply(policyContext,quotaReservation,null));
            }
        }

        public class NonMonetaryRC{
            @Test
            public void applyWhenSlotIsApplicableButThereIsNoBalanceAvailableThenReturnFalse(){
                when(nonMonetaryRateCard.apply(policyContext,quotaReservation,null)).thenReturn(false);
                RateCardGroup rateCardGroup = createRateCardGroup(nonMonetaryRateCard);
                Assert.assertFalse(rateCardGroup.apply(policyContext,quotaReservation,null));
            }

            @Test
            public void applyWhenSlotIsApplicableAndBalanceAvailableThenReturnTrue(){
                when(nonMonetaryRateCard.apply(policyContext,quotaReservation,null)).thenReturn(true);
                RateCardGroup rateCardGroup = createRateCardGroup(nonMonetaryRateCard);
                Assert.assertTrue(rateCardGroup.apply(policyContext,quotaReservation,null));
            }
        }
    }
}
