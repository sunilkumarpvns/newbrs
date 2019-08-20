package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSGetNonMonetaryBalanceEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSGetNonMonetaryBalanceEntryImpl extends SubscriptionWSGetNonMonetaryBalanceEntry {
    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSGetNonMonetaryBalanceEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getGetNonMonetaryBalanceResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getGetNonMonetaryBalanceResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getGetNonMonetaryBalanceResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
