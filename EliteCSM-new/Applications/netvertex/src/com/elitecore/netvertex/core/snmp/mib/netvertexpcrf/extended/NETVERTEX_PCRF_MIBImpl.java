package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.extended;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.NETVERTEX_PCRF_MIB;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.NotificationStatisticsMBean;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.PcrfStatisticsMBean;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.SessionCacheStatisticsMBean;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.UsageStatisticsMBean;

public class NETVERTEX_PCRF_MIBImpl extends NETVERTEX_PCRF_MIB {

	@Nonnull private final PcrfStatisticsMBean pcrfStatistics; //NOSONAR -- All Implementers are serializable
	@Nonnull private final UsageStatisticsMBean usageStatisticsMBean; //NOSONAR
	@Nonnull private final NotificationStatisticsMBean notificationStatisticsMBean; //NOSONAR
	@Nonnull private final SessionCacheStatisticsMBean sessionCacheStatisticsMBean; //NOSONAR

	public NETVERTEX_PCRF_MIBImpl(PcrfStatisticsMBean pcrfStatistics, UsageMonitoringStatisticsProvider monitoringStatisticsProvider
			,NotificationStatisticsMBean notificationStatisticsMBean,
			SessionCacheStatisticsMBean sessionCacheStatisticsMBean) {
		this.pcrfStatistics = pcrfStatistics;
		this.usageStatisticsMBean = monitoringStatisticsProvider;
		this.notificationStatisticsMBean = notificationStatisticsMBean;
		this.sessionCacheStatisticsMBean = sessionCacheStatisticsMBean;
	}

	@Override
	protected Object createPcrfStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return pcrfStatistics;
	}
	
	@Override
	protected Object createUsageStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return usageStatisticsMBean;
	}
	
	@Override
	protected Object createNotificationStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return notificationStatisticsMBean;
	}
	
	@Override
	protected Object createSessionCacheStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return sessionCacheStatisticsMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(MBeanConstants.STATISTICS_PROTOCOL_PCRF + "auth,data=" + name);
	}
}