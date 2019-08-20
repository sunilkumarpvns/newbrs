package com.elitecore.nvsmx.ws.subscriberprovisioning.blmanager;

import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;

public interface WSRequestProcessor {
    void preProcess(WebServiceRequest request);
    SubscriberProvisioningResponse process(WebServiceRequest request, StaffData staffData);
    void postProcess(WebServiceRequest request, WebServiceResponse response);
}
