package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.quota.QuotaReservation;

public interface QuotaProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail {

    boolean apply(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation);

    boolean applyRG(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation, long ratingGroup);
}
