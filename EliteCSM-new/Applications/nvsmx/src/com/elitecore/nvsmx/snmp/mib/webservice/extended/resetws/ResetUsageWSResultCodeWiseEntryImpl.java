package com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.ResetUsageWSResultCodeWiseEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class ResetUsageWSResultCodeWiseEntryImpl extends ResetUsageWSResultCodeWiseEntry {

	private static final long serialVersionUID = 1L;

	private Integer resultCode;
	private WebServiceStatistics webServiceStatistics;

	public ResetUsageWSResultCodeWiseEntryImpl(Integer resultCode, WebServiceStatistics webServiceStatistics) {
		this.resultCode = resultCode;
		this.webServiceStatistics = webServiceStatistics;
	}

	@Override
	public Long getResetUsageWSResultCodeCounters() throws SnmpStatusException {
		return webServiceStatistics.getResponseCodeCounter(ResultCode.fromVal(resultCode));
	}

	@Override
	public String getResetUsageWSResultCodeName() throws SnmpStatusException {
		return ResultCode.fromVal(resultCode).name;
	}

	@Override
	public Integer getResetUsageWSResultCode() throws SnmpStatusException {
		return resultCode;
	}

}
