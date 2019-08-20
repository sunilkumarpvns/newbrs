package com.elitecore.aaa.mibs.radius.accounting.client.extended;

import java.sql.Types;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.accounting.client.RadiusAcctClientMIB;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.RadiusAccServerEntry;
import com.elitecore.commons.kpi.annotation.Column;

public class RadiusAccServerEntryMBeanImpl extends RadiusAccServerEntry {

	private int index;
	private String extSysName;
	
	public RadiusAccServerEntryMBeanImpl(int index, String extSysName) {
		this.index = index;
		this.extSysName = extSysName;
	}
	
	@Override
	@Column(name = "ClientBadAuthenticators" , type = Types.BIGINT)
	public Long getRadiusAccClientBadAuthenticators() {
		return RadiusAcctClientMIB.getRadiusAcctClientBadAuthenticators(extSysName);
	}

	@Override
	@Column(name = "ClientMalformedResponses" , type = Types.BIGINT)
	public Long getRadiusAccClientMalformedResponses() {
		return RadiusAcctClientMIB.getRadiusAcctClientMalformedResponses(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientResponses" , type = Types.BIGINT)
	public Long getRadiusAccClientResponses()   {
		return RadiusAcctClientMIB.getRadiusAcctClientResponses(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientRetransmissions" , type = Types.BIGINT)
	public Long getRadiusAccClientRetransmissions()   {
		return RadiusAcctClientMIB.getRadiusAcctClientRetransmissions(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientRequests" , type = Types.BIGINT)
	public Long getRadiusAccClientRequests()   {
		return RadiusAcctClientMIB.getRadiusAcctClientRequests(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientPacketsDropped" , type = Types.BIGINT)
	public Long getRadiusAccClientPacketsDropped()   {
		return RadiusAcctClientMIB.getRadiusAcctClientPacketsDropped(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientRoundTripTime" , type = Types.BIGINT)
	public Long getRadiusAccClientRoundTripTime()   {
		return RadiusAcctClientMIB.getRadiusAccClientRoundTripTime(extSysName) / 10;
	}

	@Override
	@Column(name = "ClientServerPortNumber" , type = Types.INTEGER)
	public Integer getRadiusAccClientServerPortNumber() {
		return RadiusAcctClientMIB.getRadiusAccClientServerPortNumber(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientUnknownTypes" , type = Types.BIGINT)
	public Long getRadiusAccClientUnknownTypes() {
		return RadiusAcctClientMIB.getRadiusAcctClientUnknownTypes(extSysName);
	}

	@Override
	@Column(name = "radiusAccClientTimeouts" , type = Types.BIGINT)
	public Long getRadiusAccClientTimeouts() {
		return RadiusAcctClientMIB.getRadiusAcctClientTimeouts(extSysName);
	}

	@Override
	@Column(name = "radiusAccServerAddress" , type = Types.VARCHAR)
	public String getRadiusAccServerAddress() {
		return RadiusAcctClientMIB.getRadiusAccServerAddress(extSysName);
	}

	@Override
	@Column(name = "radiusAccServerIndex" , type = Types.INTEGER)
	public Integer getRadiusAccServerIndex()   {
		return index;
	}

	@Override
	@Column(name = "radiusAccClientPendingRequests" , type = Types.BIGINT)
	public Long getRadiusAccClientPendingRequests() {
		return RadiusAcctClientMIB.getRadiusAcctClientPendingRequests(extSysName);
	}

	public String getObjectName(){
		return SnmpAgentMBeanConstant.RAD_ACCT_SERVER_TABLE + extSysName;
	}
	
	@Column(name = "acctservername" , type = Types.VARCHAR)
	public String getExtSysName() {
		return extSysName;
	}
	
	
}
