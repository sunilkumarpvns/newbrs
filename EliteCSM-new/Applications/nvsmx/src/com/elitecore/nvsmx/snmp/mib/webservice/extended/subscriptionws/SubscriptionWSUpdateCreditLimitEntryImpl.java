package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSUpdateCreditLimitEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSUpdateCreditLimitEntryImpl extends SubscriptionWSUpdateCreditLimitEntry {

    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSUpdateCreditLimitEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getUpdateCreditLimitResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getUpdateCreditLimitResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getUpdateCreditLimitResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
