package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;


//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSResultCodeWiseEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberWSResultCodeWiseEntryImpl extends SubscriberWSResultCodeWiseEntry{
	
	private static final long serialVersionUID = 1L;
	private Integer resultCode;
	private WebServiceStatistics webServiceStatistics;

	public SubscriberWSResultCodeWiseEntryImpl(Integer resultCode,WebServiceStatistics webServiceStatistics){
		this.resultCode = resultCode;
		this.webServiceStatistics = webServiceStatistics;
	}
	
	@Override
    public Long getSubscriberWSResultCodeCounters() throws SnmpStatusException {
    	return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

	@Override
    public String getSubscriberWSResultCodeName() throws SnmpStatusException {
    	return ResultCode.fromVal(resultCode).name;
    }

	@Override
    public Integer getSubscriberWSResultCode() throws SnmpStatusException {
        return resultCode;
    }

}
