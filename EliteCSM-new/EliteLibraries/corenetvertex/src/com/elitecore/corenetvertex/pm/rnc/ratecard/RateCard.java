package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;

public interface RateCard extends ToStringable, Serializable {
    RateCardType getType();
}
