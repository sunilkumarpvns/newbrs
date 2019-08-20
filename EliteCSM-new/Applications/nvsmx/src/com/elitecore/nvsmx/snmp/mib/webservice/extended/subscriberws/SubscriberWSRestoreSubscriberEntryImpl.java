package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSRestoreSubscriberEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class SubscriberWSRestoreSubscriberEntryImpl extends SubscriberWSRestoreSubscriberEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public SubscriberWSRestoreSubscriberEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getRestoreSubscriberResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getRestoreSubscriberResultCodeName() throws SnmpStatusException {
        return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getRestoreSubscriberResultCode() throws SnmpStatusException {
        return resultCode;
    }

}
