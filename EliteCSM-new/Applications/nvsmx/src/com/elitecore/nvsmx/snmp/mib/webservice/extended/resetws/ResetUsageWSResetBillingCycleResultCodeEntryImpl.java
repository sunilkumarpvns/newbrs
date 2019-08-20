package com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB.
//

// java imports
//
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.ResetUsageWSResetBillingCycleResultCodeEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
 
public class ResetUsageWSResetBillingCycleResultCodeEntryImpl extends ResetUsageWSResetBillingCycleResultCodeEntry{

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private Integer resultCode;
	
    public ResetUsageWSResetBillingCycleResultCodeEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistis) {    	
    	this.resultCode = resultCode;
    	this.webServiceStatistics = webServiceStatistis;
    }

    @Override 
    public Long getResetUsageWSResetBillingCycleResultCodeCounters() throws SnmpStatusException {
    	return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
    }

    @Override
    public String getResetUsageWSResetBillingCycleResultCodeName() throws SnmpStatusException {
    	return ResultCode.fromVal(resultCode).name;
    }

    @Override
    public Integer getResetUsageWSResetBillingCycleResultCode() throws SnmpStatusException {
        return resultCode;
    }

}