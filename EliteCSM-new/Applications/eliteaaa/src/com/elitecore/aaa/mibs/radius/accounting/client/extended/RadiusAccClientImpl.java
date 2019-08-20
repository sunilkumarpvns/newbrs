package com.elitecore.aaa.mibs.radius.accounting.client.extended;

import java.sql.Types;

import com.elitecore.aaa.mibs.radius.accounting.client.RadiusAcctClientMIB;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.RadiusAccClient;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.TableRadiusAccServerTable;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;

public class RadiusAccClientImpl extends RadiusAccClient {

	@Override
	@Table(name = "radiusAccServerTable")
	public TableRadiusAccServerTable accessRadiusAccServerTable() {
		return RadiusAcctClientMIB.getAccServerTable();
	}

	@Override
	@Column(name = "radiusAccClientIdentifier", type = Types.VARCHAR)
	public String getRadiusAccClientIdentifier() {
		return " [ EliteRadius Client ] ";
	}

	@Override
	@Column(name = "InvalidServerAddresses", type = Types.BIGINT)
	public Long getRadiusAccClientInvalidServerAddresses() {
		return RadiusAcctClientMIB.getRadiusAcctClientInvalidServerAddresses();
	}

}
