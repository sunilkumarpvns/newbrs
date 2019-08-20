package com.elitecore.netvertex.core.driver.cdr;

import java.util.Objects;
import java.util.function.Function;

public class ValueProviderBaseParameterResolver implements Function<ValueProviderExtImpl, String> {

    private String pcrfKey;

    public ValueProviderBaseParameterResolver(String pcrfKey) {
        this.pcrfKey = pcrfKey;
    }

    @Override
    public String apply(ValueProviderExtImpl valueProviderExt) {

        if(Objects.nonNull(valueProviderExt.getResponse())) {
            String attribute = valueProviderExt.getResponse().getAttribute(pcrfKey);

            if(Objects.nonNull(attribute)) {
                return attribute;
            }
        }

        if(Objects.nonNull(valueProviderExt.getRequest())) {
            String attribute = valueProviderExt.getRequest().getAttribute(pcrfKey);

            if(Objects.nonNull(attribute)) {
                return attribute;
            }
        }


        return  null;
    }
}