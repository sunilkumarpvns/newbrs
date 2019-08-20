package com.elitecore.nvsmx.ws.subscription.blmanager;

import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.response.SubscriptionResponse;

public interface WSRequestProcessor {
    void preProcess(WebServiceRequest request);
    SubscriptionResponse process(WebServiceRequest request, StaffData staffData);
    void postProcess(WebServiceRequest request, WebServiceResponse response);
}
