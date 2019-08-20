package com.elitecore.aaa.statistics;

import java.util.Observer;

public interface StatisticsObserver extends Observer{
	public boolean isExpired();
}
