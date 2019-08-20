package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.quota.QuotaReservation;

public interface RateCardVersion extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion {
    boolean isApplicable(PolicyContext policyContext, String keyOne, String keyTwo);

    boolean applyReservation(PolicyContext policyContext, String keyOne, String keyTwo, String packageId, Subscription subscription,
                             QuotaReservation quotaReservation);
}
