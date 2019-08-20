package com.elitecore.aaa.mibs.radius.dynauth.client.extended;

import java.sql.Types;

import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RadiusDynAuthClientScalars;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.util.mbean.SnmpCounterUtil;

public class RadiusDynAuthClientScalarsMBeanImpl extends RadiusDynAuthClientScalars{

	@Override
	@Column(name = "CoAInvalidServerAddresses", type = Types.BIGINT) 
	public Long getRadiusDynAuthClientCoAInvalidServerAddresses() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getDynAuthClientCoAInvalidServerAddresses());
	}

	@Override
	@Column(name = "DisconInvalidServerAddresses", type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconInvalidServerAddresses() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getDynAuthClientDisconInvalidServerAddresses());
	}
	
}
