package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "stateful-proxy-handler")
public class StatefulProxySequentialHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData,AcctServicePolicyHandlerData {

    List<StatefulProxyHandlerEntryData> statefulProxyHandlerEntryDataList = new ArrayList<>();

    @Valid
    @XmlElementWrapper(name = "proxy-communication-entries")
    @XmlElement(name = "proxy-communication-entry")
    public List<StatefulProxyHandlerEntryData> getStatefulProxyHandlerEntryDataList() {
        return statefulProxyHandlerEntryDataList;
    }

    @Override
    public JSONObject toJson() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Enabled", getEnabled());
        return jsonObject;
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
