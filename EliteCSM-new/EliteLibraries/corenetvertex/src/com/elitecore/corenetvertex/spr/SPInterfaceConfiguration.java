package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;
import com.elitecore.corenetvertex.util.ToStringable;

public interface SPInterfaceConfiguration extends ToStringable {

	ProfileFieldMapping getProfileFieldMapping();

	String getName();

	SpInterfaceType getType();

	long getMaxQueryTimeoutCount();
}
