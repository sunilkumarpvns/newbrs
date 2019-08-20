package com.elitecore.core.serverx.alert;

import java.util.concurrent.atomic.AtomicLong;

public class AlertStatistics {

	
	private AtomicLong totalCounter;
	private AtomicLong dailyCounter;
	private AtomicLong weeklyCounter;
	private AtomicLong monthlyCounter;
	
	public AlertStatistics() {
		
		totalCounter = new AtomicLong();
		dailyCounter = new AtomicLong();
		weeklyCounter = new AtomicLong();
		monthlyCounter = new AtomicLong();
	}
	
	public void incrementCounters() { 
		
		totalCounter.getAndIncrement();
		dailyCounter.getAndIncrement();
		weeklyCounter.getAndIncrement();
		monthlyCounter.getAndIncrement();
	}
	
	public void resetAllCounters() {
		
		totalCounter.set(0);
		dailyCounter.set(0);
		weeklyCounter.set(0);
		monthlyCounter.set(0);
	}
	
	public long getTotalCounter() {
		return totalCounter.get();
	}
	
	public long getDailyCounter() {
		return dailyCounter.get();
	}
	
	public long getWeeklyCounter() {
		return weeklyCounter.get();
	}

	public long getMonthlyCounter() {
		return monthlyCounter.get();
	}

	public void resetDailyCounter() {
		this.dailyCounter.set(0);
	}

	public void resetWeeklyCounter() {
		this.weeklyCounter.set(0);
	}

	public void resetMonthlyCounter() {
		this.monthlyCounter.set(0);
	}

}
