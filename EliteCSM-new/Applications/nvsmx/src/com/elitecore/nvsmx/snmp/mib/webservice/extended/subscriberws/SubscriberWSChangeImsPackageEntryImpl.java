package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeImsPackageEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class SubscriberWSChangeImsPackageEntryImpl extends SubscriberWSChangeImsPackageEntry {
    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriberWSChangeImsPackageEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Integer getChangeImsPackageResultCode() throws SnmpStatusException {
        return resultCode.code;
    }

    @Override
    public Long getChangeImsPackageResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getChangeImsPackageResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }
}
