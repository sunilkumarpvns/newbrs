package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard;

import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.util.List;

public interface VersionDetail extends Serializable, ToStringable {
    String getKeyOneValue();
    String getKeyTwoValue();
    List<RateSlab> getSlabs();
    String revenueDetail();}

