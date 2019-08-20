package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.AddOn;

public class SubscriptionFactory {

	public static Builder createSubscriptionFor(AddOn addOn) {
		return new Builder(addOn);
	}
	
	public static class Builder {

		private AddOn addOn;

		public Builder(AddOn addOn) {
			this.addOn = addOn;
		}
		
		public Subscription build() {
			
			long startTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(startTime);
			
			addTime(calendar, addOn.getValidity(), addOn.getValidityPeriodUnit());
			
			long endTime = calendar.getTimeInMillis();
			
			return null;
			
		}
		
		private void addTime(Calendar calendar, int period, ValidityPeriodUnit validityPeriodUnit) {
			
			switch (validityPeriodUnit) {
				case DAY:
					calendar.add(Calendar.DATE, period);
					break;
				case MID_NIGHT:
					calendar.add(Calendar.DATE, period);
					calendar.set(Calendar.HOUR_OF_DAY, 00);
					calendar.set(Calendar.MINUTE, 00);
					calendar.set(Calendar.SECOND, 00);
					calendar.set(Calendar.MILLISECOND, 00);
					break;
				case HOUR:
					calendar.add(Calendar.HOUR, period);
					break;
				case MINUTE:
					calendar.add(Calendar.MINUTE, period);
					break;
				default:
					throw new RuntimeException("Invalid validity period unit:" + validityPeriodUnit);
			}
		}
		
	}
	
	

}
