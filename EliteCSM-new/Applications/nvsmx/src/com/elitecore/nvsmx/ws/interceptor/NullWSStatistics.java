package com.elitecore.nvsmx.ws.interceptor;

public class NullWSStatistics extends WebServiceStatistics {

	@Override
	public long getLastMinutesTotalRequest() {
		return 0;
	}

	@Override
	public void resetLastMinutesTotalRequest() {
		// no-op	
	}

	@Override
	public void setTPS(long tps) {
		// no-op
	}
	
	@Override
	public long getLastResetTime() {
		return 0;
	}
	
	@Override
	public long getTotalRequestCounter() {
		return 0;
	}
	
	@Override
	public long getTotalResponseCounter() {
		return 0;
	}
	
	@Override
	public long getTPS() {
		return 0;
	}
	
	@Override
	public boolean resetStatistics() {
		return false;
	}
}