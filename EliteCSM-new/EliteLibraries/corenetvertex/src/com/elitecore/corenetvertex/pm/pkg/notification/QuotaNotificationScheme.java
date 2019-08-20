package com.elitecore.corenetvertex.pm.pkg.notification;


import java.io.Serializable;
import java.util.List;

public class QuotaNotificationScheme implements Serializable{

    private static final long serialVersionUID = 1L;
	private final List<List<QuotaThresholdEvent>> quotaThresoldEvents;
	private final List<List<QuotaLimitEvent>> quotaLimitEvents;

	/*
	 * Maintaining list of list for the scenario described as below,
	 *
	 * Consider there is threshold events configured for 70%,80% and 90%.
	 * While request, consider, all of them are eligible to be generated.
	 * Its not concise to generate all of them, so the threshold event with highest threshold will be generated.
	 * (Here it will be generated for 90%)
	 */

	public QuotaNotificationScheme(List<List<QuotaThresholdEvent>> quotaThresoldEvents, List<List<QuotaLimitEvent>> quotaLimitEvents) {
		this.quotaThresoldEvents = quotaThresoldEvents;
		this.quotaLimitEvents = quotaLimitEvents;
	}

	public List<List<QuotaThresholdEvent>> getQuotaThresgoldEvents() {
		return quotaThresoldEvents;
	}

	public List<List<QuotaLimitEvent>> getQuotaLimitEvents() {
		return quotaLimitEvents;
	}
}
