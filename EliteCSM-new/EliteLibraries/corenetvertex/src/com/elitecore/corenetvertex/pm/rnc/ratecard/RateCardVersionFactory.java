package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.pm.rnc.RnCFactory;

import java.util.List;

public class RateCardVersionFactory {

    private RnCFactory rnCFactory;

    public RateCardVersionFactory(RnCFactory rnCFactory){
        this.rnCFactory = rnCFactory;
    }

    public MonetaryRateCardVersion create(String id, String name, List<MonetaryRateCardVeresionDetail> monetaryRateCardVeresionDetails,
                                          String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName,
                                          String rateCardId,String rateCardName){
        return rnCFactory.createMonetaryRateCardVersion(id,name, monetaryRateCardVeresionDetails,
                rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName,
                rateCardId, rateCardName);
    }
}
