package com.elitecore.corenetvertex.util;

import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class TimeSourceChainTest {
    private long initialValue = RandomUtils.nextLong();
    private long lapValue = RandomUtils.nextLong();
    private int nofLinkInTimeSourceChain = RandomUtils.nextInt(1,10);

    private TimeSourceChain timeSourceChain;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        timeSourceChain = new TimeSourceChain(nofLinkInTimeSourceChain, initialValue, lapValue);
    }

    @Test
    public void returnIncreasedValueBasedOnConfiguredLapValueOnMultipleAccess() {
        for (int i = 0; i < nofLinkInTimeSourceChain; i++) {
            Assert.assertEquals(initialValue + (lapValue* i), timeSourceChain.currentTimeInMillis());
        }
    }

    @Test
    public void throwIllegalStateExceptionWhenNoOfAccessIncreasedThenNoOfLinkInTimeSourceChain(){
        for (int i = 0; i <= nofLinkInTimeSourceChain; i++) {
            if (i < nofLinkInTimeSourceChain) {
                timeSourceChain.currentTimeInMillis();
                continue;
            }
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("no of steps in time chain increases");
            Assert.assertEquals(initialValue + (lapValue * i), timeSourceChain.currentTimeInMillis());
        }
    }
}

