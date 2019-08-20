package com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SessionWSGetSessionsBySubscriberIDEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class SessionWSGetSessionsBySubscriberIDEntryImpl extends SessionWSGetSessionsBySubscriberIDEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public SessionWSGetSessionsBySubscriberIDEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getGetSessionsBySubscriberIDResultCodeCounters() throws SnmpStatusException {
    	 return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getGetSessionsBySubscriberIDResultCodeName() throws SnmpStatusException {
    	return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getGetSessionsBySubscriberIDResultCode() throws SnmpStatusException {
    	return resultCode;
    }
    
}