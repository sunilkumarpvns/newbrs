package com.elitecore.aaa.mibs.radius.authentication.client.extended;

import java.sql.Types;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.RadiusAuthServerEntry;
import com.elitecore.commons.kpi.annotation.Column;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class RadiusAuthServerEntryMBeanImpl extends RadiusAuthServerEntry{

	private int index;
	private String extSysName;

	public RadiusAuthServerEntryMBeanImpl(int index, String extSysName) {
		this.index = index;
		this.extSysName = extSysName;
	}
	
	@Override
	@Column(name = "ClientAccessChallenges", type = Types.BIGINT)
	public Long getRadiusAuthClientAccessChallenges() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientAccessChallenge(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientAccessRejects", type = Types.BIGINT)
	public Long getRadiusAuthClientAccessRejects() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientAccessReject(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientAccessAccepts", type = Types.BIGINT)
	public Long getRadiusAuthClientAccessAccepts() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientAccessAccept(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientPacketsDropped", type = Types.BIGINT)
	public Long getRadiusAuthClientPacketsDropped() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientPacketsDropped(extSysName));
	}

	@Override
	@Column(name = "ClientAccessRetransmissions", type = Types.BIGINT)
	public Long getRadiusAuthClientAccessRetransmissions() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientAccessRetransmissions(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientUnknownTypes", type = Types.BIGINT)
	public Long getRadiusAuthClientUnknownTypes() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientUnknownTypes(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientAccessRequests", type = Types.BIGINT)
	public Long getRadiusAuthClientAccessRequests() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientAccessRequests(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientTimeouts", type = Types.BIGINT)
	public Long getRadiusAuthClientTimeouts() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientTimeouts(extSysName));
	}

	@Override
	@Column(name = "radiusAuthClientRoundTripTime", type = Types.BIGINT)
	public Long getRadiusAuthClientRoundTripTime() {
		return (RadiusAuthClientMIB.getRadiusAuthClientRoundTripTime(extSysName) / 10);
	}

	@Override
	@Column(name = "ClientPendingRequests", type = Types.BIGINT)
	public Long getRadiusAuthClientPendingRequests() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientPendingRequests(extSysName));
	}

	@Override
	@Column(name = "ClientServerPortNumber", type = Types.INTEGER)
	public Integer getRadiusAuthClientServerPortNumber() {
		return RadiusAuthClientMIB.getRadiusAuthClientServerPort(extSysName);
	}

	@Override
	@Column(name = "radiusAuthServerAddress", type = Types.VARCHAR)
	public String getRadiusAuthServerAddress() {
		return RadiusAuthClientMIB.getRadiusAuthServerAddress(extSysName);
	}

	@Override
	@Column(name = "ClientBadAuthenticators", type = Types.BIGINT)
	public Long getRadiusAuthClientBadAuthenticators() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientBadAuthenticators(extSysName));
	}

	@Override
	@Column(name = "radiusAuthServerIndex", type = Types.INTEGER)
	public Integer getRadiusAuthServerIndex()   {
		return index;
	}

	@Override
	@Column(name = "ClientMalformedAccessResponses", type = Types.BIGINT)
	public Long getRadiusAuthClientMalformedAccessResponses() {
		return convertToCounter32(RadiusAuthClientMIB.getRadiusAuthClientMalformedAccessResponse(extSysName));
	}

	public String getObjectName() {
		return SnmpAgentMBeanConstant.RAD_AUTH_SERVER_TABLE + extSysName;
	}
	
	@Column(name = "authservername", type = Types.VARCHAR)
	public String getExtSysName() {
		return extSysName;
	}
}
