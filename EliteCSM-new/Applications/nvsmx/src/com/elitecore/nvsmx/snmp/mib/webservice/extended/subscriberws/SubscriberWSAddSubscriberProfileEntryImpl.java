package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSAddSubscriberProfileEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class SubscriberWSAddSubscriberProfileEntryImpl extends SubscriberWSAddSubscriberProfileEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public SubscriberWSAddSubscriberProfileEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getAddSubscriberProfileResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getAddSubscriberProfileResultCodeName() throws SnmpStatusException {
        return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getAddSubscriberProfileResultCode() throws SnmpStatusException {
        return resultCode;
    }

}
