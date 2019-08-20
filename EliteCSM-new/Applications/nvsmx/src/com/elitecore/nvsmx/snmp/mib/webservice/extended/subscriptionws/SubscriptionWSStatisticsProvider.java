package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSStatistics;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSMethodWiseStatisticsTable;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSStatisticsProvider extends	SubscriptionWSStatistics{
	
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "Subscription-WS-STATS-PROVIDER";
	public static final int STATS_RESET = 1;

	private WebServiceStatistics webServiceStatistics;
	private TableSubscriptionWSMethodWiseStatisticsTable subscriptionWSMethodWiseStatisticsTable;
	
	public SubscriptionWSStatisticsProvider(WebServiceStatistics WebServiceStatistics) {
		this.webServiceStatistics = WebServiceStatistics;
	}
	
	public void setWebServiceStatistics(WebServiceStatistics webServiceStatistics){
		this.webServiceStatistics =  webServiceStatistics;
	}
	
	public void setSubscriptionWSMethodWiseStatisticsTable(TableSubscriptionWSMethodWiseStatisticsTable subscriptionWSMethodWiseStatisticsTable){
		this.subscriptionWSMethodWiseStatisticsTable = subscriptionWSMethodWiseStatisticsTable;
	}
	
	@Override
	public TableSubscriptionWSMethodWiseStatisticsTable accessSubscriptionWSMethodWiseStatisticsTable() throws SnmpStatusException {
        return this.subscriptionWSMethodWiseStatisticsTable;
    }
	 
	@Override
    public Long getSubscriptionWSAvgTPS() throws SnmpStatusException {
        return webServiceStatistics.getTPS();
    }
 
	@Override
    public Long getSubscriptionWSLastMinuteTotalRequests() throws SnmpStatusException {
    	return webServiceStatistics.getLastMinutesTotalRequest();
    }
    
	@Override
    public Long getSubscriptionWSTotalResponses() throws SnmpStatusException {
    	return webServiceStatistics.getTotalResponseCounter();
    }

	@Override
    public Long getSubscriptionWSTotalRequests() throws SnmpStatusException {
    	return webServiceStatistics.getTotalRequestCounter();
    }
 
}