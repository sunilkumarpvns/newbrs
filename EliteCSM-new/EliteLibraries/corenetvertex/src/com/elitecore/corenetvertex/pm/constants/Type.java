package com.elitecore.corenetvertex.pm.constants;

import javax.xml.bind.annotation.XmlEnumValue;

public enum Type {
    

	@XmlEnumValue("exclusive")
    EXCLUSIVE, 
    @XmlEnumValue("non-exclusive")
    NON_EXCLUSIVE, 
    @XmlEnumValue("replacable-by-true")
    REPLACABLE_BY_ADD_ON_POLICY,
    @XmlEnumValue("not-replacable-by-true")
    NOT_REPLACABLE_BY_ADD_ON_POLICY,
	@XmlEnumValue("emergency")
    EMERGENCY,;

}
