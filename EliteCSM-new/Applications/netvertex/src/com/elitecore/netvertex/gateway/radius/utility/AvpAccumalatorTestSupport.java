package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AvpAccumalatorTestSupport implements AvpAccumalator {
    private List<IRadiusAttribute> radiusAttributes = new ArrayList<>();
    private List<IRadiusAttribute> radiusInfoAttributes = new ArrayList<>();

    @Override
    public void add(IRadiusAttribute radiusAttribute) {
        radiusAttributes.add(radiusAttribute);
    }

    @Override
    public void addInfoAttribute(IRadiusAttribute radiusAttribute) {
        radiusInfoAttributes.add(radiusAttribute);
    }

    @Override
    public void add(List<IRadiusAttribute> radiusAttributes) {
        this.radiusAttributes.addAll(radiusAttributes);
    }

    @Override
    public boolean isEmpty() {
        return radiusAttributes.isEmpty();
    }

    @Override
    public void addAttribute(String id, String value) {
        IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(id);
        radiusAttribute.setStringValue(value);
        add(radiusAttribute);
    }

    @Override
    public void addAttribute(String id, int value) {
        IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(id);
        radiusAttribute.setIntValue(value);
        add(radiusAttribute);
    }

    @Override
    public void addAttribute(String id, long value) {
        IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(id);
        radiusAttribute.setLongValue(value);
        add(radiusAttribute);
    }

    public List<IRadiusAttribute> getAll() {
        return radiusAttributes;
    }

    public List<IRadiusAttribute> getAllInfoAttributes() {
        return radiusInfoAttributes;
    }

    public List<IRadiusAttribute> getRadiusAttributes(String id) {
        return radiusAttributes.stream().filter(attribute -> attribute.getIDString().equals(id)).collect(Collectors.toList());
    }
}
