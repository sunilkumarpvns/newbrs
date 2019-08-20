package com.elitecore.nvsmx.ws.interceptor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public class WebServiceStatistics implements TPMAware {
	
	private static final String MODULE = "WS-STATISTICS";

	private long tps = 0; 
	private AtomicLong totalRequestCounter 	= new AtomicLong();
	private AtomicLong totalResponseCounter 	= new AtomicLong();
	private AtomicLong lastMinuteRequestCounter = new AtomicLong();
	private long lastResetTime;	
	
	/*
	 * use linked hash map for predictable behavior during iteration
	 */
	private Map<Integer,AtomicLong> responseCodeStatistics = new LinkedHashMap<Integer,AtomicLong>(); 
			
	public WebServiceStatistics() { 
		for(ResultCode resultCode : ResultCode.values()){
			responseCodeStatistics.put(resultCode.code, new AtomicLong());
		}
	}
	
	
	public Set<Entry<Integer, AtomicLong>> getResponseCodeStatistics() {
		return responseCodeStatistics.entrySet();
	}
	 
	public long getTotalRequestCounter() {
		return totalRequestCounter.get();
	}
	
	public long getTotalResponseCounter() {
		return totalResponseCounter.get();
	}
	public long getLastMinuteRequestCounter() {
		return lastMinuteRequestCounter.get();
	}
 	
	public void incrementRequestCounters(){
		totalRequestCounter.incrementAndGet();
		lastMinuteRequestCounter.incrementAndGet();
	}
	
	public void incrementResponseCounters(Integer responseCode){
		totalResponseCounter.incrementAndGet();
	 	AtomicLong responseCodeCounter = responseCodeStatistics.get(responseCode);
	 	responseCodeCounter.incrementAndGet();
	}
	
	public boolean resetStatistics(){
		totalRequestCounter.set(0);
		totalResponseCounter.set(0);
		lastMinuteRequestCounter.set(0);
		tps=0;

		for(AtomicLong responseCodeStats : responseCodeStatistics.values()){
			responseCodeStats.set(0);
		}

		setLastResetTime(System.currentTimeMillis());
		return true;
	}

	@Override
	public long getLastMinutesTotalRequest() {
		return lastMinuteRequestCounter.get();
	}

	@Override
	public void resetLastMinutesTotalRequest() {
		lastMinuteRequestCounter.set(0);
	}

	@Override
	public void setTPS(long tps) {
		this.tps = tps;
	}
	
	public long getTPS(){
		return tps;
	}
	
	public long getResponseCodeCounter(ResultCode resultCode) {
        return responseCodeStatistics.get(resultCode.code).get();
    } 

	@Override
	public String toString() {
		return "WebServiceStatistics [tps=" + tps + ", totalRequestCounter="
				+ totalRequestCounter + ", totalResponseCounter=" + totalResponseCounter
				+ ", lastMinuteRequestCounter=" + lastMinuteRequestCounter 
				+ ", responseCodeStatistics=" + responseCodeStatistics + "]";
	}
 
	public long getLastResetTime() {
		return lastResetTime;
	}

	public void setLastResetTime(long lastResetTime) {
		this.lastResetTime = lastResetTime;
	}
	
 }
