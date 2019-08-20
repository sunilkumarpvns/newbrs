package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSListRnCPackagesEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSListRnCPackagesEntryImpl extends SubscriptionWSListRnCPackagesEntry {
    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSListRnCPackagesEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getListRnCPackagesResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getListRnCPackagesResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getListRnCPackagesResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
