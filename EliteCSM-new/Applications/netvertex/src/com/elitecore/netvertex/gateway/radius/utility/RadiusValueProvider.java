package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.elitecore.commons.base.Preconditions.checkArgument;

public class RadiusValueProvider implements ValueProvider {
    private RadServiceRequest radiusRequest;
    private RadServiceResponse radiusResponse;
    private RadiusGatewayConfiguration configuration;

    public RadiusValueProvider(RadServiceRequest radiusRequest,
                               RadServiceResponse radiusResponse,
                               RadiusGatewayConfiguration configuration){
        checkArgument(radiusRequest != null || radiusResponse != null, "Either RADIUS request or RADIUS response should be provided");
        this.radiusRequest = radiusRequest;
        this.radiusResponse = radiusResponse;
        this.configuration = configuration;
    }

    @Override
    public String getStringValue(String identifier) {
        IRadiusAttribute radiusAttribute = null;
        if(radiusRequest != null){
            radiusAttribute = radiusRequest.getRadiusAttribute(identifier);
        }

        if(radiusAttribute == null){
            if(radiusResponse != null){
                radiusAttribute = radiusResponse.getRadiusAttribute(identifier);
            }
        }

        if(radiusAttribute == null)
            return null;
        else
            return radiusAttribute.getStringValue();
    }

    @Override
    public long getLongValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
        IRadiusAttribute radiusAttribute = null;
        long value=0;
        if(radiusRequest != null){
            radiusAttribute = radiusRequest.getRadiusAttribute(identifier);
        }

        if(radiusAttribute == null){
            if(radiusResponse != null){
                radiusAttribute = radiusResponse.getRadiusAttribute(identifier);
            }
        }

        if (radiusAttribute == null)
            throw new MissingIdentifierException("Configure identifier not found: "+identifier);
        if((value = radiusAttribute.getIntValue()) == -1)
            throw new NumberFormatException();

        return value;
    }

    @Override
    public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
        Collection<IRadiusAttribute> radiusAttributeList= null;

        if(radiusRequest != null){
            radiusAttributeList = radiusRequest.getRadiusAttributes(identifier);
        }

        if(radiusAttributeList==null || radiusAttributeList.isEmpty()){
            if(radiusResponse != null){
                radiusAttributeList = radiusResponse.getRadiusAttributes(identifier);
            }
        }
        if(radiusAttributeList!= null){
            List<String> stringValues=new ArrayList<String>();
            radiusAttributeList.forEach(attribute -> stringValues.add(attribute.getStringValue()));
            return stringValues;
        }else
            throw new MissingIdentifierException("Configured identifier not found: "+identifier);
    }

    @Override
    public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
        Collection<IRadiusAttribute> radiusAttributeList= null;

        if(radiusRequest != null){
            radiusAttributeList = radiusRequest.getRadiusAttributes(identifier);
        }

        if(radiusAttributeList == null || radiusAttributeList.isEmpty()){
            if(radiusResponse != null){
                radiusAttributeList = radiusResponse.getRadiusAttributes(identifier);
            }
        }
        if(radiusAttributeList != null){
            List<Long> longValues=new ArrayList<Long>();
            for(IRadiusAttribute iRadiusAttribute : radiusAttributeList){
                longValues.add(iRadiusAttribute.getLongValue());
            }
            return longValues;
        }else{
            throw new MissingIdentifierException("Configured identifier not found: "+identifier);
        }
    }

    public RadiusGatewayConfiguration getConfiguration() {
        return configuration;
    }

    public @Nullable RadServiceRequest getRADIUSRequest() {
        return radiusRequest;
    }

    public @Nullable RadServiceResponse getRADIUSResponse() {
        return radiusResponse;
    }

    @Override
    public Object getValue(String key) {
        return null;
    }
}
