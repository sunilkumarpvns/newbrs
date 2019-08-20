package com.elitecore.aaa.mibs.radius.authentication.client.extended;

import java.sql.Types;

import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.RadiusAuthClient;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.TableRadiusAuthServerTable;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;

public class RadiusAuthClientMBeanImpl extends RadiusAuthClient {

	@Override
	@Table(name = "radiusAuthServerTable")
	public TableRadiusAuthServerTable accessRadiusAuthServerTable() {
		return RadiusAuthClientMIB.getAuthClientTable();
	}

	@Override
	@Column(name = "radiusAuthClientIdentifier", type = Types.VARCHAR)
	public String getRadiusAuthClientIdentifier() {
		return " [ EliteRadius Client ] ";
	}

	@Override
	@Column(name = "InvalidServerAddresses", type = Types.BIGINT)
	public Long getRadiusAuthClientInvalidServerAddresses() {
		return RadiusAuthClientMIB.getRadiusAuthClientInvalidServerAddresses();
	}

}
