package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSAddMonetaryBalanceEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSAddMonetaryBalanceEntryImpl extends SubscriptionWSAddMonetaryBalanceEntry {

    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSAddMonetaryBalanceEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }


    @Override
    public Long getAddMonetaryBalanceResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }


    @Override
    public String getAddMonetaryBalanceResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getAddMonetaryBalanceResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
