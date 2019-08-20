package com.elitecore.aaa.mibs.radius.dynauth.client.extended;

import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RadiusDynAuthClientMIBObjects;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.TableRadiusDynAuthServerTable;
import com.elitecore.commons.kpi.annotation.Table;

public class RadiusDynAuthClientMIBObjectsMBeanImpl extends	RadiusDynAuthClientMIBObjects {

	@Override
	@Table(name = "radiusDynAuthServerTable")
	public TableRadiusDynAuthServerTable accessRadiusDynAuthServerTable() {
		return RadiusDynAuthClientMIB.getDynAuthServerTable();
	}

}
