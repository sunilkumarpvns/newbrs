package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;

import java.io.Serializable;
import java.util.List;
import com.elitecore.corenetvertex.constants.Uom;

public interface RateCard extends Serializable {
    String getId();
    String getName();
    String getKeyOne();
    String getKeyTwo();
    Uom getPulseUom();
    Uom getRateUom();
    List<RateCardVersion> getRateCardVersions();
}
