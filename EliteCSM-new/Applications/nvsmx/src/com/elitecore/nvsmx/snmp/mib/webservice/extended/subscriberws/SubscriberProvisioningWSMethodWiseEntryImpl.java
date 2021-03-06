package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberProvisioningWSMethodWiseEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberProvisioningWSMethodWiseEntryImpl extends SubscriberProvisioningWSMethodWiseEntry{

	private static final long serialVersionUID = 1L;

	private WebServiceStatistics webServiceStatistics;
	private Integer methodIndex;
	private String methodName;
	
    public SubscriberProvisioningWSMethodWiseEntryImpl(Integer methodIndex, String methodName, WebServiceStatistics webServiceStatistics) {
    	this.methodIndex = methodIndex;
		this.methodName = methodName;
		this.webServiceStatistics = webServiceStatistics;
    }

    @Override
    public Long getSubscriberWSMethodAvgTPS() throws SnmpStatusException {
        return webServiceStatistics.getTPS();
    }

    @Override
    public Long getSubscriberWSMethodLastMinuteTotalRequests() throws SnmpStatusException {
        return webServiceStatistics.getLastMinutesTotalRequest();
    }

    @Override
    public Long getSubscriberWSMethodTotalResponses() throws SnmpStatusException {
        return webServiceStatistics.getTotalResponseCounter();
    }

    @Override
    public Long getSubscriberWSMethodTotalRequests() throws SnmpStatusException {
    	return webServiceStatistics.getTotalRequestCounter();
    }

    @Override
    public String getSubscriberWSMethodName() throws SnmpStatusException {
        return methodName;
    }

    @Override
    public Integer getSubscriberWSMethodIndex() throws SnmpStatusException {
        return methodIndex;
    }
}
