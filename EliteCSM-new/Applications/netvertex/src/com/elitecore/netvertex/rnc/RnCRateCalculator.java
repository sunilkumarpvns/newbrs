package com.elitecore.netvertex.rnc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.elitecore.corenetvertex.constants.CommonConstants;

public final class RnCRateCalculator {
    
    public static double calculateMoney(long unit, double rate, double rateMinorUnit) {
          
    	return BigDecimal.valueOf(unit).multiply(BigDecimal.valueOf(rate))
    			.divide(BigDecimal.valueOf(rateMinorUnit), CommonConstants.RATE_PRECESION, RoundingMode.HALF_UP).doubleValue();

    }

    public static double calculateMoney(long unit, double rate){
        return BigDecimal.valueOf(unit).multiply(BigDecimal.valueOf(rate)).doubleValue();
    }

    public static long calculateUnit(double amount, double rate, double rateUnitVal) {
        return(long)(amount * rateUnitVal / rate);
    }
}
