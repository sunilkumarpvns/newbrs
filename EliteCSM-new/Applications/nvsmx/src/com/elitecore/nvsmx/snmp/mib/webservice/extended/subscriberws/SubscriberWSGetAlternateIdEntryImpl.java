package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSGetAlternateIdEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberWSGetAlternateIdEntryImpl extends SubscriberWSGetAlternateIdEntry {

	private static final long serialVersionUID = 1L;
	private WebServiceStatistics webServiceStatistics;
	private ResultCode resultCode;

	public SubscriberWSGetAlternateIdEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
		this.resultCode = resultCode;
		this.webServiceStatistics = webServiceStatistis;
	}

	@Override
	public Integer getGetAlternateIdResultCode() throws SnmpStatusException {
		return resultCode.code;
	}

	@Override
	public String getGetAlternateIdResultCodeName() throws SnmpStatusException {
		return resultCode.name;
	}

	@Override
	public Long getGetAlternateIdResultCodeCounters() throws SnmpStatusException {
		return webServiceStatistics.getResponseCodeCounter(resultCode);
	}
}
