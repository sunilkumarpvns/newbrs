package com.elitecore.netvertex.gateway.radius.snmp.auth;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.RadiusAuthClientEntry;

public class RadiusAuthClientEntryImpl extends RadiusAuthClientEntry{

	private String ipAddress;
	private transient AuthServerClientStatisticsProvider authServerClientStatisticsProvider;
	private int clientIndex;

	public RadiusAuthClientEntryImpl(String ipAddress , AuthServerClientStatisticsProvider authServerClientStatisticsProvider ,int clientIndex) {
		this.ipAddress = ipAddress;
		this.authServerClientStatisticsProvider = authServerClientStatisticsProvider;
		this.clientIndex = clientIndex;
	}

	@Override
	public Long getRadiusAuthServMalformedAccessRequests(){
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServMalformedAccessRequests(ipAddress));
	}

	@Override
	public Long getRadiusAuthServAccessChallenges()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServAccessChallenges(ipAddress));
	}

	@Override
	public Long getRadiusAuthServAccessRejects()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServAccessRejects(ipAddress));
	}

	@Override
	public Long getRadiusAuthServAccessAccepts()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServAccessAccepts(ipAddress));
	}

	@Override
	public Long getRadiusAuthServDupAccessRequests()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServDupAccessRequests(ipAddress));
	}

	@Override
	public Long getRadiusAuthServAccessRequests()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServAccessRequests(ipAddress));
	}

	@Override
	public Long getRadiusAuthServUnknownTypes()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServUnknownTypes(ipAddress));
	}

	@Override
	public String getRadiusAuthClientID()  {
		return authServerClientStatisticsProvider.getRadiusAuthClientID(ipAddress);
	}

	@Override
	public String getRadiusAuthClientAddress()  {
		return ipAddress;
	}

	@Override
	public Long getRadiusAuthServPacketsDropped()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServPacketsDropped(ipAddress));
	}

	@Override
	public Long getRadiusAuthServBadAuthenticators()  {
		return SnmpCounterUtil.convertToCounter32(authServerClientStatisticsProvider.getRadiusAuthServBadAuthenticators(ipAddress));
	}

	@Override
	public Integer getRadiusAuthClientIndex()  {
		return clientIndex;
	}

	public String getObjectName() {
		return MBeanConstants.STATISTICS_PROTOCOL_RADIUS +  "auth,table=RadiusAuthClientTable,entry=" + "RadiusAuthClientEntry-" + ipAddress;
	}
}