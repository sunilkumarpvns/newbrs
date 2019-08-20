package com.elitecore.nvsmx.ws.subscription;

import com.elitecore.nvsmx.ws.subscription.request.FnFOperationRequest;
import com.elitecore.nvsmx.ws.subscription.response.FnFOperationResponse;

public interface SubscriptionRestWS extends  ISubscriptionWS {
    String WS_FNF_GROUP_MEMBERS = "fnFGroupMembers";

    FnFOperationResponse addFnFMembers(FnFOperationRequest request);
    FnFOperationResponse removeFnFMembers(FnFOperationRequest request);
}
