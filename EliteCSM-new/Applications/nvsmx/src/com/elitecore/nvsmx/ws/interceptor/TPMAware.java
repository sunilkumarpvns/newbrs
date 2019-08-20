package com.elitecore.nvsmx.ws.interceptor;

public interface TPMAware {
	long getLastMinutesTotalRequest();
	void resetLastMinutesTotalRequest();
	void setTPS(long tps);
}
