package com.elitecore.aaa.mibs.radius.dynauth.server.extended;

import java.sql.Types;

import com.elitecore.aaa.mibs.radius.dynauth.server.RadiusDynAuthServerMIB;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RadiusDynAuthServerScalars;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RadiusDynAuthServerScalarsMBean;
import com.elitecore.commons.kpi.annotation.Column;

public class RadiusDynAuthServerScalarsMBeanImpl extends RadiusDynAuthServerScalars {

	@Override
	@Column(name = "radiusDynAuthServerIdentifier", type = Types.VARCHAR)
	public String getRadiusDynAuthServerIdentifier() {
		return RadiusDynAuthServerMIB.getServerIdentifier();
	}

	@Override
	@Column(name = "CoAInvalidClientAddresses", type = Types.BIGINT)
	public Long getRadiusDynAuthServerCoAInvalidClientAddresses() {
		return RadiusDynAuthServerMIB.getDynAuthClientCoAInvalidClientAddresses();
	}

	@Override
	@Column(name = "DisconInvalidClientAddresses", type = Types.BIGINT)
	public Long getRadiusDynAuthServerDisconInvalidClientAddresses() {
		return RadiusDynAuthServerMIB.getDynAuthClientDisconInvalidClientAddresses();
	}

}
