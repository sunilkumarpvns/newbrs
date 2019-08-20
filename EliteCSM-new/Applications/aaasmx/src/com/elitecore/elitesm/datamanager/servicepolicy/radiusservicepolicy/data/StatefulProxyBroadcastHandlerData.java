package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import net.sf.json.JSONObject;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "stateful-broadcast-handler")
public class StatefulProxyBroadcastHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData,AcctServicePolicyHandlerData {

    List<StatefulProxyBroadcastHandlerEntryData> statefulProxyHandlerEntryDataList = new ArrayList<>();

    @Valid
    @XmlElementWrapper(name = "async-communication-entries")
    @XmlElement(name = "async-communication-entry")
    public List<StatefulProxyBroadcastHandlerEntryData> getStatefulProxyHandlerEntryDataList() {
        return statefulProxyHandlerEntryDataList;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
        return null;
    }

    @Override
    public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
        return null;
    }
}
