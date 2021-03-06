package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSListAddOnSubscriptionsEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class SubscriptionWSListAddOnSubscriptionsEntryImpl extends SubscriptionWSListAddOnSubscriptionsEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public SubscriptionWSListAddOnSubscriptionsEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getListAddOnSubscriptionsResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getListAddOnSubscriptionsResultCodeName() throws SnmpStatusException {
        return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getListAddOnSubscriptionsResultCode() throws SnmpStatusException {
        return resultCode;
    }

}
