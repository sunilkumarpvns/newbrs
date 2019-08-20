package com.elitecore.corenetvertex.pm.rnc.notification;


import java.io.Serializable;
import java.util.List;


/**
 * 
 * @author Ishani dave
 *
 */

public class ThresholdNotificationScheme implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String description;
	private final List<ThresholdEvent> thresholdEvents;
	/*
	 * Maintaining list of list for the scenario described as below,
	 *
	 * Consider there is threshold events configured for 70%,80% and 90%.
	 * While request, consider, all of them are eligible to be generated.
	 * Its not concise to generate all of them, so the threshold event with highest threshold will be generated.
	 * (Here it will be generated for 90%)
	 */

	public ThresholdNotificationScheme(List<ThresholdEvent> usageThresholdEvents) {

		description = "";
		this.thresholdEvents = usageThresholdEvents;
	}
	
	public String getDescription() {
		return description;
	}

	public List<ThresholdEvent> getThresholdEvents() {
		return thresholdEvents;
	}
}
