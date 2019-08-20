package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import com.elitecore.netvertex.pm.PolicyContext;

public interface VersionDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail {
    boolean isApplicable(PolicyContext policyContext, String keyOne, String keyTwo);
}
