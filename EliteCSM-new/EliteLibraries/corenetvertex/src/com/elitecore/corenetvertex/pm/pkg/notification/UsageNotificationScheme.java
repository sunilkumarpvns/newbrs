package com.elitecore.corenetvertex.pm.pkg.notification;


import java.io.Serializable;
import java.util.List;



/**
 * 
 * @author Jay Trivedi
 *
 */

public class UsageNotificationScheme implements Serializable{

	private static final long serialVersionUID = 1L;
	static final String MODULE = "USG-NTF-SCHEME";
	private final String description;
	private final List<List<UsageThresholdEvent>> usageThresholdEventsList;
	/*
	 * Maintaining list of list for the scenario described as below,
	 * 
	 * Consider there is threshold events configured for 70%,80% and 90%.
	 * While request, consider, all of them are eligible to be generated.
	 * Its not concise to generate all of them, so the threshold event with highest threshold will be generated.
	 * (Here it will be generated for 90%)  
	 */

	public UsageNotificationScheme(List<List<UsageThresholdEvent>> usageThresholdEvents) {

		//FIXME need to provide description
		description = "";
		this.usageThresholdEventsList = usageThresholdEvents;
	}
	
	public String getDescription() {
		return description;
	}

	public List<List<UsageThresholdEvent>> getUsageThresholdEventsList() {
		return usageThresholdEventsList;
	}
}
