package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSChangeRnCAddOnSubscriptionEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSChangeRnCAddOnSubscriptionEntryImpl extends SubscriptionWSChangeRnCAddOnSubscriptionEntry {

    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSChangeRnCAddOnSubscriptionEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getChangeRnCAddOnSubscriptionResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getChangeRnCAddOnSubscriptionResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getChangeRnCAddOnSubscriptionResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}