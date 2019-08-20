package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import java.math.BigDecimal;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class FlatSlabTest {
    private final long slabValue = 1;
    private final long pulse = 1;
    private final Uom pulseUom = Uom.SECOND;
    private final Uom rateUom = Uom.SECOND;

    public class isFreeTrue {

        @Test
        public void rateIsGreatorIsZero() {
            FlatSlab flatSlab = createFlatSlabWithRate(new BigDecimal(0));
            assertTrue(flatSlab.isFree());
        }

        @Test
        public void rateIsGreatorIsLessThanZero() {
            FlatSlab flatSlab = createFlatSlabWithRate(new BigDecimal(-1));
            assertTrue(flatSlab.isFree());
        }
    }

    public FlatSlab createFlatSlabWithRate(BigDecimal rate) {
        return new FlatSlab(slabValue, pulse, rate, pulseUom, rateUom);
    }

    public class isFreeFalseWhen {
        @Test
        public void rateIsGreatorThanZero() {
            FlatSlab flatSlab = createFlatSlabWithRate(new BigDecimal(RandomUtils.nextInt(1,Integer.MAX_VALUE)));
            assertFalse(flatSlab.isFree());
        }
    }
}