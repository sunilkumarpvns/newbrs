package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberProvisioningWSStatistics;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSMethodWiseStatisticsTable;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberProvisioningWSStatisticsProvider extends	SubscriberProvisioningWSStatistics{
	
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "Subscription-WS-STATS-PROVIDER";
	public static final int STATS_RESET = 1;

	private WebServiceStatistics webServiceStatistics;
	private TableSubscriberProvisioningWSMethodWiseStatisticsTable subscriberProvisioningWSMethodWiseStatisticsTable;
	
	public SubscriberProvisioningWSStatisticsProvider(WebServiceStatistics WebServiceStatistics) {
		this.webServiceStatistics = WebServiceStatistics;
	}
	
	public void setWebServiceStatistics(WebServiceStatistics webServiceStatistics){
		this.webServiceStatistics =  webServiceStatistics;
	}
	
	public void setSubscriberProvisioningWSMethodWiseStatisticsTable(TableSubscriberProvisioningWSMethodWiseStatisticsTable subscriberProvisioningWSMethodWiseStatisticsTable){
		this.subscriberProvisioningWSMethodWiseStatisticsTable = subscriberProvisioningWSMethodWiseStatisticsTable;
	}
	
	@Override
	public TableSubscriberProvisioningWSMethodWiseStatisticsTable accessSubscriberProvisioningWSMethodWiseStatisticsTable() throws SnmpStatusException {
        return SubscriberProvisioningWSMethodWiseStatisticsTable;
    }	

	@Override
    public Long getSubscriberWSAvgTPS() throws SnmpStatusException {
        return webServiceStatistics.getTPS();
    }

	@Override
    public Long getSubscriberWSLastMinuteTotalRequests() throws SnmpStatusException {
        return webServiceStatistics.getLastMinutesTotalRequest();
    }

	@Override
    public Long getSubscriberWSTotalResponses() throws SnmpStatusException {
        return webServiceStatistics.getTotalResponseCounter();
    }

	@Override
    public Long getSubscriberWSTotalRequests() throws SnmpStatusException {
        return webServiceStatistics.getTotalRequestCounter();
    }
}