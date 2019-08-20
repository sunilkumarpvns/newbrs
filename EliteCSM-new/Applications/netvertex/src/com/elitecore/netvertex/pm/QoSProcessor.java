package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;

public interface QoSProcessor {

    boolean process(QoSProfile qoSProfile, BasePolicyContext policyContext, UserPackage userPackage, Subscription subscription);
}
