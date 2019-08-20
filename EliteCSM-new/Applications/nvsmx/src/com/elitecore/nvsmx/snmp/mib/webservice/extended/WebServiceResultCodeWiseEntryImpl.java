package com.elitecore.nvsmx.snmp.mib.webservice.extended;
 
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.WebServiceResultCodeWiseEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class WebServiceResultCodeWiseEntryImpl extends WebServiceResultCodeWiseEntry{
 
	private static final long serialVersionUID = 1L;
	protected Integer webServiceResultCode;
    private WebServiceStatistics webServiceStatistics;

    public WebServiceResultCodeWiseEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistics) {
    	this.webServiceResultCode 			= resultCode;
    	this.webServiceStatistics 			= webServiceStatistics;
    }

    @Override
    public Long getWebServiceResultCodeCounters() throws SnmpStatusException {
    	return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(webServiceResultCode));
    }
 
    @Override
    public String getWebServiceResultCodeName() throws SnmpStatusException {
        return ResultCode.fromVal(webServiceResultCode).name;
    }
    
    @Override
    public Integer getWebServiceResultCode() throws SnmpStatusException {
        return webServiceResultCode;
    }
}
