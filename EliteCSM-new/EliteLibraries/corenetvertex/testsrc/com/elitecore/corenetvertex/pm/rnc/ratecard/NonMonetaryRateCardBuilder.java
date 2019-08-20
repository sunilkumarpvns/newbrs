package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;

public class NonMonetaryRateCardBuilder {

    public static NonMonetaryRateCardData createNonMonetaryRateCardData(){
        NonMonetaryRateCardData nonMonetaryRateCardData = new NonMonetaryRateCardData();
        nonMonetaryRateCardData.setId("id");
        nonMonetaryRateCardData.setEvent(100L);
        nonMonetaryRateCardData.setTime(100L);
        nonMonetaryRateCardData.setTimeUom(Uom.SECOND.name());
        nonMonetaryRateCardData.setPulse(2L);
        nonMonetaryRateCardData.setPulseUom(Uom.SECOND.name());
        nonMonetaryRateCardData.setRenewalInterval(1);
        nonMonetaryRateCardData.setRenewalIntervalUnit(RenewalIntervalUnit.MONTH.name());
        return nonMonetaryRateCardData;
    }
}
