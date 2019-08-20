package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ValueProviderBaseParameterResolverTest {

    private ValueProviderBaseParameterResolver valueProviderBaseParameterResolver = new ValueProviderBaseParameterResolver(PCRFKeyConstants.CS_USERNAME.val);
    private PCRFResponse pcrfResponse = new PCRFResponseImpl();
    private PCRFRequest pcrfRequest = new PCRFRequestImpl();

    private ValueProviderExtImpl valueProvider = new ValueProviderExtImpl(pcrfRequest, pcrfResponse);

    @Test
    public void applyReturnValueFromResponseWhenFoundFromResponse() {
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_USERNAME.val, "test");
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_USERNAME.val, "test1");
        assertThat(valueProviderBaseParameterResolver.apply(valueProvider), is(equalTo("test")));

    }

    @Test
    public void applyReturnValueFromRequestWhenFoundFromRequestAndNotInResponse() {
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_USERNAME.val, "test1");
        assertThat(valueProviderBaseParameterResolver.apply(valueProvider), is(equalTo("test1")));
    }

    @Test
    public void applyReturnValueFromRequestWhenFoundFromRequestAndResponseIsNull() {
        ValueProviderExtImpl valueProvider = new ValueProviderExtImpl(pcrfRequest, null);
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_USERNAME.val, "test1");
        assertThat(valueProviderBaseParameterResolver.apply(valueProvider), is(equalTo("test1")));
    }

    @Test
    public void applyReturnNullValueWhenRequestAndResponseIsNull() {
        ValueProviderExtImpl valueProvider = new ValueProviderExtImpl(null, null);
        assertNull(valueProviderBaseParameterResolver.apply(valueProvider));
    }

    @Test
    public void applyReturnNullValueWhenValueNotFoundFromRequestAndResponse() {
        assertNull(valueProviderBaseParameterResolver.apply(valueProvider));
    }
}