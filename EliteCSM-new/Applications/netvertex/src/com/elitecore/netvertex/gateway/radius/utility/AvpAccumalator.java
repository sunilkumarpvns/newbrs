package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import java.util.List;

public interface AvpAccumalator {
    public void add(IRadiusAttribute radiusAttribute);
    public void addInfoAttribute(IRadiusAttribute radiusAttribute);
    public void add(List<IRadiusAttribute> radiusAttributes);
    public boolean isEmpty();

    void addAttribute(String id, String value);
    void addAttribute(String id, int value);
    void addAttribute(String id, long value);
}
