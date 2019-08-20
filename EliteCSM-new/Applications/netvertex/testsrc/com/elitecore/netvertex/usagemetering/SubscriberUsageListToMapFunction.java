package com.elitecore.netvertex.usagemetering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Function;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

public class SubscriberUsageListToMapFunction implements Function<List<SubscriberUsage>, Map<String, Map<String, SubscriberUsage>>> {

	@Override
	public Map<String, Map<String, SubscriberUsage>> apply(List<SubscriberUsage> subscriberUsages) {

		if(subscriberUsages == null) {
			return null;
		}
		
		Map<String,Map<String,SubscriberUsage>> packageWiseSubscription = new HashMap<String, Map<String,SubscriberUsage>>();
		
		for(SubscriberUsage subscriberUsage : subscriberUsages) {
			Map<String,SubscriberUsage> serviceUsages;
			if(subscriberUsage.getSubscriptionId() == null) {
				serviceUsages = packageWiseSubscription.get(subscriberUsage.getPackageId());
			} else {
				serviceUsages = packageWiseSubscription.get(subscriberUsage.getSubscriptionId());
			}


			
			if(serviceUsages == null){
				serviceUsages = new HashMap<String, SubscriberUsage>();

				if(subscriberUsage.getSubscriptionId() == null) {
					packageWiseSubscription.put(subscriberUsage.getPackageId(), serviceUsages);
				} else {
					packageWiseSubscription.put(subscriberUsage.getSubscriptionId(), serviceUsages);
				}

			}
			
			serviceUsages.put(subscriberUsage.getQuotaProfileId()+ CommonConstants.USAGE_KEY_SEPARATOR +subscriberUsage.getServiceId(), subscriberUsage);
		}
		
		return packageWiseSubscription;
		
	}
	
}
