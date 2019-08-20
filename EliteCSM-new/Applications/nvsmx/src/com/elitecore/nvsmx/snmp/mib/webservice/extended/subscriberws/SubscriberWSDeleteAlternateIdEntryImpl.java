package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSDeleteAlternateIdEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberWSDeleteAlternateIdEntryImpl extends SubscriberWSDeleteAlternateIdEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private ResultCode resultCode;

	public SubscriberWSDeleteAlternateIdEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
		this.resultCode = resultCode;
		this.webServiceStatistics = webServiceStatistis;
	}

	@Override
	public Integer getDeleteAlternateIdResultCode() throws SnmpStatusException {
		return resultCode.code;
	}

	@Override
	public String getDeleteAlternateIdResultCodeName() throws SnmpStatusException {
		return resultCode.name;
	}

	@Override
	public Long getDeleteAlternateIdResultCodeCounters() throws SnmpStatusException {
		return webServiceStatistics.getResponseCodeCounter(resultCode);
	}
}
