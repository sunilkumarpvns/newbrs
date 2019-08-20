package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.quota.QuotaReservation;

public interface RateCard extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCard {
    boolean isApplicable(PolicyContext policyContext, QoSInformation qoSInformation);

    boolean applyReservation(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation);
}
