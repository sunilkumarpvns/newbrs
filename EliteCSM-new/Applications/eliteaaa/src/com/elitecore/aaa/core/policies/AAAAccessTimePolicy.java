package com.elitecore.aaa.core.policies;

import java.util.List;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.core.serverx.policies.accesstime.EliteAccessTimePolicy;

/**
 * This is a wrapper class that wraps the Access Time Policy of expression library. 
 * @author narendra.pathai
 *
 */
public class AAAAccessTimePolicy implements EliteAccessTimePolicy{
	private AccessTimePolicy accessTimePolicy;
	
	public AAAAccessTimePolicy(){
		accessTimePolicy = new AccessTimePolicy();
	}

	@Override
	public long applyPolicy(int timeUnit) {
		return accessTimePolicy.applyPolicy(timeUnit);
	}
	
	public void setTimeSlots(List<TimeSlot> timeSlots){
		accessTimePolicy.setListTimeSlot(timeSlots);
	}
}
