package com.elitecore.aaa.mibs.radius.dynauth.client.extended;

import java.sql.Types;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.EnumRadiusDynAuthServerAddressType;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RadiusDynAuthServerEntry;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.util.mbean.SnmpCounterUtil;

public class RadiusDynAuthServerEntryMBeanImpl extends RadiusDynAuthServerEntry {

	private int index;
	private String extSysName;
	
	public RadiusDynAuthServerEntryMBeanImpl(int index, String name) {
		this.index = index;
		this.extSysName = name;
	}
	
	@Override
	@Column(name = "ClientCounterDiscontinuity" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCounterDiscontinuity() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCounterDiscontinuity(extSysName));
	}

	@Override
	@Column(name = "ClientUnknownTypes" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientUnknownTypes() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientUnknownTypes(extSysName));
	}

	@Override
	@Column(name = "ClientCoAPacketsDropped" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoAPacketsDropped() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoAPacketsDropped(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthClientCoARequests" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoARequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoARequests(extSysName));
	}

	@Override
	@Column(name = "ClientDisconPacketsDropped" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconPacketsDropped() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconPacketsDropped(extSysName));
	}

	@Override
	@Column(name = "ClientDisconTimeouts" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconTimeouts() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconTimeouts(extSysName));
	}

	@Override
	@Column(name = "ClientDisconPendingRequests" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconPendingRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconPendingRequests(extSysName));
	}

	@Override
	@Column(name = "ClientDisconBadAuthenticators" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconBadAuthenticators() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconBadAuthenticators(extSysName));
	}

	@Override
	@Column(name = "ClientMalformedDisconResponses" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientMalformedDisconResponses() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientMalformedDisconResponses(extSysName));
	}

	@Override
	@Column(name = "ClientDisconNakSessNoContext" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconNakSessNoContext() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconNakSessNoContext(extSysName));
	}

	@Override
	@Column(name = "ClientDisconNakAuthOnlyRequest" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconNakAuthOnlyRequest() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconNakAuthOnlyRequest(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthClientDisconNaks" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconNaks() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconNaks(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthClientDisconAcks" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconAcks() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconAcks(extSysName));
	}

	@Override
	@Column(name = "ClientDisconRetransmissions" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconRetransmissions() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconRetransmissions(extSysName));
	}

	@Override
	@Column(name = "ClientDisconAuthOnlyRequests" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconAuthOnlyRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconAuthOnlyRequests(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthClientCoATimeouts" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoATimeouts() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoATimeouts(extSysName));
	}

	@Override
	@Column(name = "ClientDisconRequests" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientDisconRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientDisconRequests(extSysName));
	}

	@Override
	@Column(name = "ClientCoAPendingRequests" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoAPendingRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoAPendingRequests(extSysName));
	}

	@Override
	@Column(name = "ClientRoundTripTime" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientRoundTripTime() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientRoundTripTime(extSysName) / 10);
	}

	@Override
	@Column(name = "ClientCoABadAuthenticators" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoABadAuthenticators() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoABadAuthenticators(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthServerID" , type = Types.VARCHAR)
	public String getRadiusDynAuthServerID() {
		return RadiusDynAuthClientMIB.getRadiusDynAuthServerID(extSysName);
	}

	@Override
	@Column(name = "ClientMalformedCoAResponses" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientMalformedCoAResponses() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientMalformedCoAResponses(extSysName));
	}

	@Override
	@Column(name = "ClientPortNumber" , type = Types.BIGINT)
	public Long getRadiusDynAuthServerClientPortNumber() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthServerClientPortNo(extSysName));
	}

	@Override
	@Column(name = "ClientCoANakSessNoContext" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoANakSessNoContext() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoANakSessNoContext(extSysName));
	}

	@Override
	public Byte[] getRadiusDynAuthServerAddress()   {
		byte[] addressBytes = RadiusDynAuthClientMIB.getRadiusDynAuthServerAddress(extSysName).getBytes();
		Byte[] wrapperBytes = new Byte[addressBytes.length];
		for (int i=0 ; i<wrapperBytes.length ; i++) {
			wrapperBytes[i] = addressBytes[i]; 
		}
		return wrapperBytes;
	}

	@Column(name = "radiusDynAuthServerAddress", type = Types.VARCHAR)
	public String getDynAuthServerAddress() {
		return RadiusDynAuthClientMIB.getRadiusDynAuthServerAddress(extSysName);
	}
	
	@Override
	@Column(name = "ClientCoANakAuthOnlyRequest" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoANakAuthOnlyRequest() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoANakAuthOnlyRequest(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthServerAddressType",type = Types.VARCHAR)
	public EnumRadiusDynAuthServerAddressType getRadiusDynAuthServerAddressType() {
		return new EnumRadiusDynAuthServerAddressType(RadiusDynAuthClientMIB.getRadiusDynAuthServerAddressType(extSysName));
	}
	
	@Override
	@Column(name = "radiusDynAuthClientCoANaks" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoANaks() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoANaks(extSysName));
	}

	@Override
	@Column(name = "radiusDynAuthServerIndex" , type = Types.INTEGER)
	public Integer getRadiusDynAuthServerIndex() {
		return index;
	}

	@Override
	@Column(name = "radiusDynAuthClientCoAAcks" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoAAcks() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoAAcks(extSysName));
	}

	@Override
	@Column(name = "ClientCoARetransmissions" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoARetransmissions() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoARetransmissions(extSysName));
	}

	@Override
	@Column(name = "ClientCoAAuthOnlyRequest" , type = Types.BIGINT)
	public Long getRadiusDynAuthClientCoAAuthOnlyRequest() {
		return SnmpCounterUtil.convertToCounter32(RadiusDynAuthClientMIB.getRadiusDynAuthClientCoAAuthOnlyRequests(extSysName));
	}

	public String getObjectName(){
		return SnmpAgentMBeanConstant.DYNA_AUTH_SERVER_TABLE + extSysName;
	}
	
	@Column(name = "dynauthservername" , type = Types.VARCHAR)
	public String getExtSysName() {
		return extSysName;
	}
}
