package com.elitecore.aaa.mibs.radius.dynauth.server.extended;

import java.sql.Types;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.dynauth.server.RadiusDynAuthServerMIB;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.EnumRadiusDynAuthClientAddressType;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RadiusDynAuthClientEntry;
import com.elitecore.commons.kpi.annotation.Column;

public class RadiusDynAuthClientEntryMBeanImpl extends RadiusDynAuthClientEntry {

	private int index;
	private String clientAddress;

	public RadiusDynAuthClientEntryMBeanImpl(int index, String name) {
		this.index = index;
		this.clientAddress = name;
	}
	
	@Override
	@Column(name = "radiusDynAuthServCoANaks", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoANaks()  {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoANaks(clientAddress);
	}

	@Override
	@Column(name = "radiusDynAuthServCoAAcks", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoAAcks()  {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoAAcks(clientAddress);
	}

	@Override
	@Column(name = "ServDupCoARequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServDupCoARequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDupCoARequests(clientAddress);
	}

	@Override
	@Column(name = "ServCoAAuthOnlyRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoAAuthOnlyRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoAAuthOnlyRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusDynAuthServCoARequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoARequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoARequests(clientAddress);
	}

	@Override
	@Column(name = "ServDisconPacketsDropped", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconPacketsDropped() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconPacketsDropped(clientAddress);
	}

	@Override
	@Column(name = "ServDisconBadAuthenticators", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconBadAuthenticators() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconBadAuthenticators(clientAddress);
	}

	@Override
	@Column(name = "ServMalformedDisconRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServMalformedDisconRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServMalformedDisconRequests(clientAddress);
	}

	@Override
	@Column(name = "ServDisconUserSessRemoved", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconUserSessRemoved() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconUserSessRemoved(clientAddress);
	}

	@Override
	@Column(name = "ServDisconNakSessNoContext", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconNakSessNoContext() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconNakSessNoContext(clientAddress);
	}

	@Override
	@Column(name = "ServDisconNakAuthOnlyRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconNakAuthOnlyRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconNakAuthOnlyRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusDynAuthServDisconNaks", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconNaks() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconNaks(clientAddress);
	}

	@Override
	@Column(name = "radiusDynAuthServDisconAcks", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconAcks() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconAcks(clientAddress);
	}

	@Override
	@Column(name = "ServerCounterDiscontinuity", type = Types.BIGINT)
	public Long getRadiusDynAuthServerCounterDiscontinuity() {
		return RadiusDynAuthServerMIB.getDynAuthServerCounterDiscontinuity(clientAddress);
	}

	@Override
	@Column(name = "ServDupDisconRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServDupDisconRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDupDisconRequests(clientAddress);
	}

	@Override
	@Column(name = "ServDisconAuthOnlyRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconAuthOnlyRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconAuthOnlyRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusDynAuthServUnknownTypes", type = Types.BIGINT)
	public Long getRadiusDynAuthServUnknownTypes() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServUnknownTypes(clientAddress);
	}

	@Override
	@Column(name = "ServDisconRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServDisconRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServDisconRequests(clientAddress);
	}

	@Override
	@Column(name = "ServCoAPacketsDropped", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoAPacketsDropped() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoAPacketsDropped(clientAddress);
	}

	@Override
	@Column(name = "ServCoABadAuthenticators", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoABadAuthenticators() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoABadAuthenticators(clientAddress);
	}

	@Override
	public Byte[] getRadiusDynAuthClientAddress() {
		byte[] addressBytes = clientAddress.getBytes();
		Byte[] wrapperbytes = new Byte[addressBytes.length];
		for (int i=0 ; i<wrapperbytes.length ; i++) {
			wrapperbytes[i] = addressBytes[i];
		}
		return wrapperbytes;
	}

	@Column(name = "radiusDynAuthClientAddress", type = Types.VARCHAR)
	public String getDynAuthClientAddress() {
		return clientAddress;
	}
	
	@Override
	@Column(name = "radiusDynAuthClientAddressType", type = Types.VARCHAR)
	public EnumRadiusDynAuthClientAddressType getRadiusDynAuthClientAddressType() {
		return new EnumRadiusDynAuthClientAddressType(RadiusDynAuthServerMIB.getRadiusDynAuthClientAddressType(clientAddress));
	}

	@Override
	@Column(name = "ServMalformedCoARequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServMalformedCoARequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServMalformedCoARequests(clientAddress);
	}

	@Override
	@Column(name = "ServCoAUserSessChanged", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoAUserSessChanged() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoAUserSessChanged(clientAddress);
	}

	@Override
	@Column(name = "radiusDynAuthClientIndex", type = Types.INTEGER)
	public Integer getRadiusDynAuthClientIndex() {
		return index;
	}

	@Override
	@Column(name = "ServCoANakSessNoContext", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoANakSessNoContext() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoANakSessNoContext(clientAddress);
	}

	@Override
	@Column(name = "ServCoANakAuthOnlyRequests", type = Types.BIGINT)
	public Long getRadiusDynAuthServCoANakAuthOnlyRequests() {
		return RadiusDynAuthServerMIB.getRadiusDynAuthServCoANakAuthOnlyRequests(clientAddress);
	}

	public String getObjectName() {
		return SnmpAgentMBeanConstant.DYN_AUTH_CLIENT_TABLE + clientAddress;
	}
}
