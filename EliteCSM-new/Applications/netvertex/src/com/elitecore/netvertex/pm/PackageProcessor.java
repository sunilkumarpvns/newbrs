package com.elitecore.netvertex.pm;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;


public class PackageProcessor {


	public static void apply(PolicyContext policyContext, UserPackage dataPackage, Subscription subscription) {

		for (com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile qosProfile : dataPackage.getQoSProfiles()) {
			
			if (((QoSProfile)qosProfile).selectRule(policyContext, dataPackage, subscription)) {
				break;
			}
		}
		
		long timeOutInSec = dataPackage.getNextSessionTimeOut(policyContext.getCurrentTimePeriod());
		
		if(timeOutInSec > AccessTimePolicy.NO_TIME_OUT) {
			policyContext.setTimeout(timeOutInSec);
		}
	}
}