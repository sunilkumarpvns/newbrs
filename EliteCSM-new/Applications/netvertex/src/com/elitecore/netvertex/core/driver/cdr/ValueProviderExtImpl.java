package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.ValueProviderExt;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.List;

public class ValueProviderExtImpl implements ValueProviderExt {

    private PCRFRequest request;
    private PCRFResponse response;

    public ValueProviderExtImpl(PCRFRequest request, PCRFResponse response){
        this.request = request;
        this.response = response;
    }

    public PCRFRequest getRequest() {
        return request;
    }

    public PCRFResponse getResponse() {
        return response;
    }

    @Override
    public String getStringValue(String identifier) throws Exception {
        return null;
    }

    @Override
    public List<String> getStringValues(String identifier) throws Exception {
        return null;
    }
}
