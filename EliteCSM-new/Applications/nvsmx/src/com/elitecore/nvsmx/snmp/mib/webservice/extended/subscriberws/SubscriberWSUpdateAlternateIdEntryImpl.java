package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSUpdateAlternateIdEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberWSUpdateAlternateIdEntryImpl extends SubscriberWSUpdateAlternateIdEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private ResultCode resultCode;

	public SubscriberWSUpdateAlternateIdEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
		this.resultCode = resultCode;
		this.webServiceStatistics = webServiceStatistis;
	}

	@Override
	public Integer getUpdateAlternateIdResultCode() throws SnmpStatusException {
		return resultCode.code;
	}

	@Override
	public String getUpdateAlternateIdResultCodeName() throws SnmpStatusException {
		return resultCode.name;
	}

	@Override
	public Long getUpdateAlternateIdResultCodeCounters() throws SnmpStatusException {
		return webServiceStatistics.getResponseCodeCounter(resultCode);
	}
}
