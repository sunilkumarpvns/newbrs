package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSUpdateSubscriberProfileEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class SubscriberWSUpdateSubscriberProfileEntryImpl extends SubscriberWSUpdateSubscriberProfileEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public SubscriberWSUpdateSubscriberProfileEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getUpdateSubscriberProfileResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getUpdateSubscriberProfileResultCodeName() throws SnmpStatusException {
        return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getUpdateSubscriberProfileResultCode() throws SnmpStatusException {
        return resultCode;
    }

}
