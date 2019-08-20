package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;

import java.io.Serializable;
import java.util.List;
import com.elitecore.corenetvertex.util.ToStringable;

public interface RateCardVersion extends Serializable, ToStringable {
    String getName();
    List<VersionDetail> getVersionDetails();
    String getRateCardId();
}
