package com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws;

import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.ResetUsageWSStatistics;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableResetUsageWSMethodWiseStatisticsTable;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class ResetUsageWSStatisticsProvider extends	ResetUsageWSStatistics{
	
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "ResetUsage-WS-STATS-PROVIDER";
	public static final int STATS_RESET = 1;

	private WebServiceStatistics webServiceStatistics;
	private TableResetUsageWSMethodWiseStatisticsTable resetUsageWSMethodWiseStatisticsTable;

	public ResetUsageWSStatisticsProvider(WebServiceStatistics WebServiceStatistics) {
		this.webServiceStatistics = WebServiceStatistics;
	}
	
	public void setWebServiceStatistics(WebServiceStatistics webServiceStatistics){
		this.webServiceStatistics =  webServiceStatistics;
	}
	
	public void setResetUsageWSMethodWiseStatisticsTable(TableResetUsageWSMethodWiseStatisticsTable resetUsageWSMethodWiseStatisticsTable) {
		this.resetUsageWSMethodWiseStatisticsTable = resetUsageWSMethodWiseStatisticsTable;
	}
	
	@Override
	public TableResetUsageWSMethodWiseStatisticsTable accessResetUsageWSMethodWiseStatisticsTable() {
		return resetUsageWSMethodWiseStatisticsTable;
	}
	
	@Override
    public Long getResetUsageWSAvgTPS() throws SnmpStatusException {
        return webServiceStatistics.getTPS();
    }

	@Override
    public Long getResetUsageWSLastMinuteTotalRequests() throws SnmpStatusException {
        return webServiceStatistics.getLastMinutesTotalRequest();
    }

	@Override
    public Long getResetUsageWSTotalResponses() throws SnmpStatusException {
        return webServiceStatistics.getTotalResponseCounter();
    }

	@Override
    public Long getResetUsageWSTotalRequests() throws SnmpStatusException {
    	return webServiceStatistics.getTotalRequestCounter();
    }
}