package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.corenetvertex.spr.data.Subscription;

import java.util.Comparator;
import java.util.function.ToIntFunction;

import static com.elitecore.commons.base.Strings.isNullOrBlank;

public class AddOnSubscriptionComparator implements Comparator<Subscription> {

	private static final AddOnSubscriptionComparator ADD_ON_SUBSCRIPTION_COMPARATOR = new AddOnSubscriptionComparator();
	public static final Comparator<Subscription> PRIORITY_COMPARATOR = Comparator.comparingInt((ToIntFunction<Subscription>) value -> value.getPriority());

	public int compare(Subscription addOnSubscription1, Subscription addOnSubscription2) {

		int priorityCompResult = PRIORITY_COMPARATOR.compare(addOnSubscription2, addOnSubscription1);

		if (priorityCompResult != 0) {
			return priorityCompResult;
		}

		int fnfCompResult = Boolean.valueOf(isNullOrBlank(addOnSubscription2.getFnFGroupName()))
				.compareTo(Boolean.valueOf(isNullOrBlank(addOnSubscription1.getFnFGroupName())));

		if(fnfCompResult!=0){
			return fnfCompResult;
		}

		int dateCompResult = addOnSubscription2.getEndTime().compareTo(addOnSubscription1.getEndTime());
		if (dateCompResult != 0) {
			return dateCompResult;
		}

		int startTimeCompResult = addOnSubscription2.getStartTime().compareTo(addOnSubscription1.getStartTime());

		if (startTimeCompResult != 0) {
			return startTimeCompResult;
		}

		return 1;
	}

	public static AddOnSubscriptionComparator instance() {
		return ADD_ON_SUBSCRIPTION_COMPARATOR;
	}
}
