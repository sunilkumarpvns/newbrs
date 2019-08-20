package com.elitecore.netvertex.service.notification;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.corenetvertex.pm.pkg.notification.UsageThresholdEvent;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.netvertex.service.pcrf.NotificationQueue;

import javax.annotation.Nullable;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class UsageNotificationScheme extends com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme{

	private static final String MODULE = "USG-NTF-SCHEME";
	
	public UsageNotificationScheme(List<List<UsageThresholdEvent>> usageThresholdEvents) {
		super(usageThresholdEvents);
	}

	public void queueEligibleEvents(@Nullable Map<String, SubscriberUsage> previousUsages,
									Map<String, SubscriberUsage> currentUsages, NotificationQueue queue) {


		for (int index = 0; index < getUsageThresholdEventsList().size(); index++) {

			List<com.elitecore.corenetvertex.pm.pkg.notification.UsageThresholdEvent> usageThresholdEvents = getUsageThresholdEventsList().get(index);

			for (int childIndex = 0; childIndex < usageThresholdEvents.size(); childIndex++) {

				UsageThresholdEvent usageThresholdEvent = usageThresholdEvents.get(childIndex);
				SubscriberUsage oldSubscriberUsage = null;
				if (previousUsages != null) {
					oldSubscriberUsage = previousUsages.get(usageThresholdEvent.getUsageKey());
				}

				SubscriberUsage newSubscriberUsage = currentUsages.get(usageThresholdEvent.getUsageKey());

				if (usageThresholdEvent.isEligible(oldSubscriberUsage, newSubscriberUsage)) {

					getLogger().debug(MODULE, "Selected eligible event on threshold: " + usageThresholdEvent.getThreshold());
					queue.add(usageThresholdEvent);
					break;
				}
			}
		}
	}
	
	public void queueEligibleEvents(List<Map<String, SubscriberUsage>> previousUsages, 
			List<Map<String, SubscriberUsage>> currentUsages, NotificationQueue queue) {
		
		for (int index = 0; index < getUsageThresholdEventsList().size(); index++) {
			
			List<UsageThresholdEvent> usageThresholdEvents = getUsageThresholdEventsList().get(index);
			
			for (int childIndex = 0; childIndex < usageThresholdEvents.size(); childIndex++) {
				
				UsageThresholdEvent usageThresholdEvent = usageThresholdEvents.get(childIndex);
				Map<SubscriberUsage, SubscriberUsage> subscriberUsages = createPreviousToCurrentUsage(previousUsages, currentUsages, usageThresholdEvent.getUsageKey());
				
				if (usageThresholdEvent.isEligible(subscriberUsages)) {
					
					getLogger().debug(MODULE, "Selected eligible event on threshold: " +  usageThresholdEvent.getThreshold());
					queue.add(usageThresholdEvent);
					break;
				}
			}
		}
	}


	private Map<SubscriberUsage, SubscriberUsage> createPreviousToCurrentUsage(List<Map<String, SubscriberUsage>> previousUsages,
			List<Map<String, SubscriberUsage>> currentUsages, String key) {
		Map<SubscriberUsage, SubscriberUsage> subscriberUsgaes = new IdentityHashMap<SubscriberUsage, SubscriberUsage>();
		
		for (int index = 0; index < currentUsages.size(); index++){
			
			SubscriberUsage previousSubscriberUsage = previousUsages.get(index).get(key);
			SubscriberUsage currentSubscriberUsage = currentUsages.get(index).get(key);
			
			subscriberUsgaes.put(previousSubscriberUsage, currentSubscriberUsage);
		}
		return subscriberUsgaes;
	}

}
