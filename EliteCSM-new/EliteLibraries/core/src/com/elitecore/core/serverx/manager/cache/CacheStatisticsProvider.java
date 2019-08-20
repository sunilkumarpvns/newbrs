package com.elitecore.core.serverx.manager.cache;

import java.util.Date;


public class CacheStatisticsProvider implements CacheStatistics{
	private CacheDetail cacheDetail;
	private Date lastRefreshDate;
	private Date lastSuccessfulRefreshDate;
	
	public CacheStatisticsProvider(CacheDetail cacheDetail){
		this.cacheDetail = cacheDetail;
	}

	public void setCacheDetail(CacheDetail cacheDetail) {
		this.cacheDetail = cacheDetail;
	}
	
	public void setLastRefreshDate(Date lastRefreshDate) {
		this.lastRefreshDate = lastRefreshDate;
	}
	@Override
	public Date getLastRefreshDate() {
		return lastRefreshDate;
	}

	public void setLastSuccessfulRefreshDate(Date lastSuccessfulRefreshDate) {
		this.lastSuccessfulRefreshDate = lastSuccessfulRefreshDate;
	}
	@Override
	public Date getLastSuccessfulRefreshDate() {		
		return lastSuccessfulRefreshDate;
	}
	
	@Override
	public String getName(){
		return this.cacheDetail.getName();
	}

	@Override
	public String getDescription() {
		return cacheDetail.getDescription();
	}

	@Override
	public int getResultCode() {
		return cacheDetail.getResultCode();
	}

	@Override
	public String getSource() {
		return this.cacheDetail.getSource();
	}

}
