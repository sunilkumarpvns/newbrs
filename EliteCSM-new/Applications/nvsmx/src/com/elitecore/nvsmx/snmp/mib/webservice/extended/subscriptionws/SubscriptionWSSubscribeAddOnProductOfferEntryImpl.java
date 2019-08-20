package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSSubscribeAddOnProductOfferEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriptionWSSubscribeAddOnProductOfferEntryImpl extends SubscriptionWSSubscribeAddOnProductOfferEntry {
    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriptionWSSubscribeAddOnProductOfferEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Long getSubscribeAddOnProductOfferResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }

    @Override
    public String getSubscribeAddOnProductOfferResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Integer getSubscribeAddOnProductOfferResultCode() throws SnmpStatusException {
        return resultCode.code;
    }
}
