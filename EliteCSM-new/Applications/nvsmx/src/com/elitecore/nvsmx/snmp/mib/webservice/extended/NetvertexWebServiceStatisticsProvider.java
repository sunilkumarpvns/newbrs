package com.elitecore.nvsmx.snmp.mib.webservice.extended;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.EnumWebServiceStatisticsReset;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.NetvertexWebServiceStatistics;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.sun.management.snmp.SnmpStatusException;

public class NetvertexWebServiceStatisticsProvider extends	NetvertexWebServiceStatistics{
	
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "NV-WS-STATS-PROVIDER";
	public static final int STATS_RESET = 2;

	private WebServiceStatistics webServiceStatistics;

	public NetvertexWebServiceStatisticsProvider(WebServiceStatistics WebServiceStatistics) {
		this.webServiceStatistics = WebServiceStatistics;
	}
	
	public void setWebServiceStatistics(WebServiceStatistics webServiceStatistics){
		this.webServiceStatistics =  webServiceStatistics;
	}
	
    @Override
    public Long getWebServiceAvgTPS() throws SnmpStatusException {
        return webServiceStatistics.getTPS();
    }

    @Override
    public Long getWebServiceLastMinuteTotalRequests() throws SnmpStatusException {
        return webServiceStatistics.getLastMinutesTotalRequest();
    }

    @Override
    public Long getWebServiceTotalResponses() throws SnmpStatusException {
        return webServiceStatistics.getTotalRequestCounter();
    }

    @Override
    public Long getWebServiceTotalRequests() throws SnmpStatusException {
    	return webServiceStatistics.getTotalRequestCounter();
    }
	
	@Override
	public void setWebServiceStatisticsReset(EnumWebServiceStatisticsReset enumWebServiceStatisticsReset) throws SnmpStatusException {
		if(enumWebServiceStatisticsReset == null){
			if (getLogger().isErrorLogLevel()) {
				getLogger().error(MODULE, "Unable to reset WebService statistics. Reason: webservice statistics reset value received is null");
			}
			throw new IllegalArgumentException();
		}

		if(enumWebServiceStatisticsReset.intValue() == STATS_RESET){			
			WebServiceStatisticsManager.getInstance().resetAllStatistics();
		}else{
			getLogger().error(MODULE, "Unable to reset WebService statistics. Reason: Invalid WebService statistics reset value received: " 
					+ enumWebServiceStatisticsReset.intValue());
			throw new IllegalArgumentException("Unable to reset WebService statistics. Reason: Invalid WebService statistics reset value received: " 
					+ enumWebServiceStatisticsReset.intValue());
		}
	}
	
	@Override
	public EnumWebServiceStatisticsReset getWebServiceStatisticsReset() throws SnmpStatusException {
		int statsReset = WebServiceStatisticsManager.getInstance().getStatisticsReset();
		try{
			return new EnumWebServiceStatisticsReset(statsReset);
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error while creating WebService Statistics Reset Enum. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new EnumWebServiceStatisticsReset(WebServiceStatisticsManager.STATS_OTHER);
		}
	};
	
	@Override
	public Long getWebServiceLastResetTime() throws SnmpStatusException {
	        return WebServiceStatisticsManager.getInstance().getStatisicsLastResetTime()/10;
	}
	
	@Override
	public Long getWebServiceUpTime() throws SnmpStatusException {
        return (System.currentTimeMillis() - WebServiceStatisticsManager.getInstance().getWebServiceUpTime())/10;
    }
	 
}