package com.elitecore.netvertex.gateway.util;

import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.List;

public class PCRFResponseValueProvider implements ValueProvider {

    private PCRFResponse pcrfResponse;
    private PCCRule pccRule;

    public PCRFResponseValueProvider(PCRFResponse pcrfResponse, PCCRule pccRule) {
        this.pcrfResponse = pcrfResponse;
        this.pccRule = pccRule;
    }


    public PCRFResponse getPcrfResponse() {
        return pcrfResponse;
    }

    @Override
    public String getStringValue(String identifier) {

        String value;
        if(pccRule != null) {
            value = PCRFResponseValueProvideUtil.fromPCCRule(identifier, pccRule);
            if(value != null) {
                return value;
            }
        }

        value = pcrfResponse.getAttribute(identifier);

        if( value != null) {
            return value;
        }

        IPCANQoS ipcQoS = null;
        if (pcrfResponse.getSessionQoS() != null) {
            ipcQoS = pcrfResponse.getSessionQoS();
        }

        value = PCRFResponseValueProvideUtil.valueFromIpCanQoS(identifier, ipcQoS);


        return value;
    }


    @Override
    public long getLongValue(String identifier) {
        return Long.parseLong(getStringValue(identifier));
    }

    @Override
    public List<String> getStringValues(String identifier) throws MissingIdentifierException {
        String value = getStringValue(identifier);
        List<String> stringValues;
        if(value!=null){
            stringValues=new ArrayList<String>(1);
            stringValues.add(value);
        }else
            throw new MissingIdentifierException("Configured identifier not found: "+identifier);

        return stringValues;
    }

    @Override
    public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
        String value = getStringValue(identifier);
        List<Long> longValues;
        if(value != null)
            try{
                longValues=new ArrayList<Long>(1);
                longValues.add(Long.parseLong(value));
            }catch(Exception e){
                throw new InvalidTypeCastException(e.getMessage(), e);
            }
        else
            throw new MissingIdentifierException("Configured identifier not found: "+identifier);

        return longValues;
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    public PCCRule getPccRule() {
        return pccRule;
    }
}
