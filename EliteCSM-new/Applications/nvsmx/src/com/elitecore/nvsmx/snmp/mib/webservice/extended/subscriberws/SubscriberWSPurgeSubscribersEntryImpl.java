package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSPurgeSubscribersEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class SubscriberWSPurgeSubscribersEntryImpl extends SubscriberWSPurgeSubscribersEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public SubscriberWSPurgeSubscribersEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getPurgeSubscribersResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getPurgeSubscribersResultCodeName() throws SnmpStatusException {
        return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getPurgeSubscribersResultCode() throws SnmpStatusException {
        return resultCode;
    }

}