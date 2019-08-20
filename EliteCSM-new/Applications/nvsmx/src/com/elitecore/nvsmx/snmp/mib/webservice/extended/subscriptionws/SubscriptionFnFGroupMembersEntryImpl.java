package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionFnFGroupMembersEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionFnFGroupMembersEntryImpl extends SubscriptionFnFGroupMembersEntry {

    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionFnFGroupMembersEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getFnFGroupMembersResultCodeCounters() throws SnmpStatusException {
        return  webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getFnFGroupMembersResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getFnFGroupMembersResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
