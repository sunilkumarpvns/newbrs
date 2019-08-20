package com.elitecore.aaa.mibs.radius.dynauth.server.extended;

import com.elitecore.aaa.mibs.radius.dynauth.server.RadiusDynAuthServerMIB;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RadiusDynAuthServerMIBObjects;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.TableRadiusDynAuthClientTable;
import com.elitecore.commons.kpi.annotation.Table;

public class RadiusDynAuthServerMIBObjectsMBeanImpl extends RadiusDynAuthServerMIBObjects {

	@Override
	@Table(name = "radiusDynAuthClientTable")
	public TableRadiusDynAuthClientTable accessRadiusDynAuthClientTable() {
		return RadiusDynAuthServerMIB.getDynAuthClientTable();
	}

}
