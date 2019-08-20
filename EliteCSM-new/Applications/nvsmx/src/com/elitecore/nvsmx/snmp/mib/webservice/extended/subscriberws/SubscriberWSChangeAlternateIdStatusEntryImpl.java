package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeAlternateIdStatusEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberWSChangeAlternateIdStatusEntryImpl extends SubscriberWSChangeAlternateIdStatusEntry {
	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private ResultCode resultCode;

	public SubscriberWSChangeAlternateIdStatusEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
		this.resultCode = resultCode;
		this.webServiceStatistics = webServiceStatistis;
	}

	@Override
	public Integer getChangeAlternateIdStatusResultCode() throws SnmpStatusException {
		return resultCode.code;
	}

	@Override
	public String getChangeAlternateIdStatusResultCodeName() throws SnmpStatusException {
		return resultCode.name;
	}

	@Override
	public Long getChangeAlternateIdStatusResultCodeCounters() throws SnmpStatusException {
		return webServiceStatistics.getResponseCodeCounter(resultCode);
	}
}
