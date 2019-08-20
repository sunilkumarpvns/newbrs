package com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws;

import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SessionManagementWSStatistics;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSessionManagementWSMethodWiseStatisticsTable;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SessionManagementWSStatisticsProvider extends	SessionManagementWSStatistics{
	
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "Subscription-WS-STATS-PROVIDER";
	public static final int STATS_RESET = 1;

	private WebServiceStatistics webServiceStatistics;
	private TableSessionManagementWSMethodWiseStatisticsTable sessionManagementWSMethodWiseStatisticsTable;
	
	public SessionManagementWSStatisticsProvider(WebServiceStatistics webServiceStatistics) {
		this.webServiceStatistics = webServiceStatistics;
	}
		
	public void setWebServiceStatistics(WebServiceStatistics webServiceStatistics){
		this.webServiceStatistics =  webServiceStatistics;
	}

	public void setSessionManagementWSMethodWiseStatisticsTable(TableSessionManagementWSMethodWiseStatisticsTable sessionManagementWSMethodWiseStatisticsTable){
		this.sessionManagementWSMethodWiseStatisticsTable = sessionManagementWSMethodWiseStatisticsTable;
	}
	
	@Override
	public TableSessionManagementWSMethodWiseStatisticsTable accessSessionManagementWSMethodWiseStatisticsTable() throws SnmpStatusException {
	        return sessionManagementWSMethodWiseStatisticsTable;
	}
	 
	@Override
    public Long getSessionWSAvgTPS() throws SnmpStatusException {
        return webServiceStatistics.getTPS();
    }

	@Override
	public Long getSessionWSLastMinuteTotalRequests() throws SnmpStatusException {
		return webServiceStatistics.getLastMinutesTotalRequest();
    }

	@Override
	public Long getSessionWSTotalResponses() throws SnmpStatusException {
		return webServiceStatistics.getTotalResponseCounter();
    }

	@Override
	public Long getSessionWSTotalRequests() throws SnmpStatusException {
		return webServiceStatistics.getTotalRequestCounter();
    }
 
}