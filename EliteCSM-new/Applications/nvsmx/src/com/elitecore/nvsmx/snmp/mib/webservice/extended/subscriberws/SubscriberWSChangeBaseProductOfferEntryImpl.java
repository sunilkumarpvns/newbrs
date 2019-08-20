package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeBaseProductOfferEntry;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SubscriberWSChangeBaseProductOfferEntryImpl extends SubscriberWSChangeBaseProductOfferEntry {

    private static final long serialVersionUID = 1L;
    private WebServiceStatistics webServiceStatistics;
    private ResultCode resultCode;

    public SubscriberWSChangeBaseProductOfferEntryImpl(ResultCode resultCode, WebServiceStatistics webServiceStatistis) {
        this.resultCode = resultCode;
        this.webServiceStatistics = webServiceStatistis;
    }

    @Override
    public Integer getChangeBaseProductOfferResultCode() throws SnmpStatusException {
        return resultCode.code;
    }

    @Override
    public String getChangeBaseProductOfferResultCodeName() throws SnmpStatusException {
        return resultCode.name;
    }

    @Override
    public Long getChangeBaseProductOfferResultCodeCounters() throws SnmpStatusException {
        return webServiceStatistics.getResponseCodeCounter(resultCode);
    }
}
