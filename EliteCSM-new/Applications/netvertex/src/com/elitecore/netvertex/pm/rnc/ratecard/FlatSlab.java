package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.constants.Uom;

import java.math.BigDecimal;

public class FlatSlab extends com.elitecore.corenetvertex.pm.rnc.ratecard.RateSlab {
	public FlatSlab(long slabValue, long pulse, BigDecimal rate, Uom pulseUom, Uom rateUom, Integer discount, String revenueDetail) {
		super(slabValue,pulse,rate,pulseUom,rateUom, TierRateType.FLAT, discount, revenueDetail);
	}
}
