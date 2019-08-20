package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.constants.Uom;

import java.math.BigDecimal;

public class IncrementalSlab extends com.elitecore.corenetvertex.pm.rnc.ratecard.RateSlab {

	public IncrementalSlab(long slabValue, long pulse, BigDecimal rate, Uom pulseUom, Uom rateUom, Integer discount, String revenueDetail) {
		super(slabValue, pulse, rate, pulseUom, rateUom, TierRateType.INCREMENTAL, discount, revenueDetail);
	}

}
