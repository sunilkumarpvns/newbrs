package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.util.util.Predicates;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(HierarchicalContextRunner.class)
public class PredicatesTest {
    @Before
    public void setup(){

    }

    public class AllBalance{
        @Test
        public void getsPassedForFutureBalance(){
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                    //,0
                    ,1,1,1,0,0, System.currentTimeMillis()+3600*1000, System.currentTimeMillis()+3600*3000,"INR", MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), System.currentTimeMillis(),"", "");
            Assert.assertTrue(Predicates.ALL_MONETARY_BALANCE.test(monetaryBalance));
        }

        @Test
        public void getsFailedForExpiredBalance(){
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                    //,0
                    ,1,1,1,0,0, System.currentTimeMillis()-3600*1000, System.currentTimeMillis()-3600*3000,"INR", MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), System.currentTimeMillis(),"", "");
            Assert.assertFalse(Predicates.ALL_MONETARY_BALANCE.test(monetaryBalance));
        }

        @Test
        public void getsPassedForCurrentBalance(){
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                    //,0
                    ,1,1,1,0,0, System.currentTimeMillis()-3600*1000, System.currentTimeMillis()+3600*3000,"INR", MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), System.currentTimeMillis(),"", "");
            Assert.assertTrue(Predicates.ALL_MONETARY_BALANCE.test(monetaryBalance));
        }
    }

    public class RecentBalance{
        @Test
        public void getsFailedForFutureBalance(){
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                    //,0
                    ,1,1,1,0,0, System.currentTimeMillis()+3600*1000, System.currentTimeMillis()+3600*3000,"INR", MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), System.currentTimeMillis(),"", "");
            Assert.assertFalse(Predicates.RECENT_MONETARY_BALANCE.test(monetaryBalance));
        }

        @Test
        public void getsFailedForExpiredBalance(){
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                    ,1,1,1,0,0, System.currentTimeMillis()-3600*1000, System.currentTimeMillis()-3600*3000,"INR", MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), System.currentTimeMillis(),"", "");
            Assert.assertFalse(Predicates.RECENT_MONETARY_BALANCE.test(monetaryBalance));
        }

        @Test
        public void getsPassedForCurrentBalance(){
            MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                    ,1,1,1,0, 0,System.currentTimeMillis()-3600*1000, System.currentTimeMillis()+3600*3000,"INR", MonetaryBalanceType.DEFAULT.name(), System.currentTimeMillis(), System.currentTimeMillis(),"", "");
            Assert.assertTrue(Predicates.RECENT_MONETARY_BALANCE.test(monetaryBalance));
        }
    }
}
