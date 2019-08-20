package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSChangeDataAddOnSubscriptionEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSChangeDataAddOnSubscriptionEntryImpl extends SubscriptionWSChangeDataAddOnSubscriptionEntry {
    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSChangeDataAddOnSubscriptionEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getChangeDataAddOnSubscriptionResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }


    @Override
    public String getChangeDataAddOnSubscriptionResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getChangeDataAddOnSubscriptionResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
