package com.elitecore.netvertex.gateway.radius.snmp.acct;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.RadiusAccClientEntry;

public class RadiusAccClientEntryImpl extends RadiusAccClientEntry{

	private String ipAddress;
	private transient AcctServerClientStatisticsProvider statisticsProvider;
	private int clientIndex;

	public RadiusAccClientEntryImpl(String ipAddress , AcctServerClientStatisticsProvider statisticsProvider , int clientIndex) {
		this.ipAddress = ipAddress;
		this.statisticsProvider = statisticsProvider;
		this.clientIndex = clientIndex;
	}
	@Override
	public Long getRadiusAccServMalformedRequests() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServMalformedRequests(ipAddress));
	}
	@Override
	public Long getRadiusAccServBadAuthenticators() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServBadAuthenticators(ipAddress));
	}
	@Override
	public Long getRadiusAccServResponses() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServResponses(ipAddress));
	}
	@Override
	public Long getRadiusAccServDupRequests() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServDupRequests(ipAddress));
	}
	@Override
	public Long getRadiusAccServRequests() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServRequests(ipAddress));
	}
	@Override
	public Long getRadiusAccServPacketsDropped() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServPacketsDropped(ipAddress));
	}
	@Override
	public String getRadiusAccClientID() {
		return statisticsProvider.getRadiusAccClientID(ipAddress);
	}
	@Override
	public String getRadiusAccClientAddress() {
		return ipAddress;
	}
	@Override
	public Long getRadiusAccServUnknownTypes() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServUnknownTypes(ipAddress));
	}
	@Override
	public Long getRadiusAccServNoRecords() {
		return SnmpCounterUtil.convertToCounter32(statisticsProvider.getRadiusAccServNoRecords(ipAddress));
	}

	@Override
	public Integer getRadiusAccClientIndex() {
		return clientIndex;
	}
	public String getObjectName() {
		return MBeanConstants.STATISTICS_PROTOCOL_RADIUS +  "acct,table=RadiusAccClientTable,entry=" + "RadiusAccClientEntry-" + ipAddress;
	}
}
