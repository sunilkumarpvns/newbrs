package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;

import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.math.BigDecimal;

public interface RateSlab extends ToStringable, Serializable{

    long getSlabValue();

    long getPulse();

    BigDecimal getRate();

    Uom getPulseUom();

    Uom getRateUom();

    default boolean isFree() {
        return getRate().doubleValue() <= 0.0d;
    }

    long getPulseInBytesOrSeconds();

    boolean isVolumeBasedRateDefined();

    long getRateUomInBytesOrSeconds();
}
