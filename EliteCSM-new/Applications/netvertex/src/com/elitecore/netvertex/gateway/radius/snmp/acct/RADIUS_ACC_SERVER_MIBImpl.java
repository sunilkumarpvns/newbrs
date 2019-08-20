package com.elitecore.netvertex.gateway.radius.snmp.acct;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertex.gateway.radius.snmp.acct.autogen.RADIUS_ACC_SERVER_MIB;

public class RADIUS_ACC_SERVER_MIBImpl extends RADIUS_ACC_SERVER_MIB {

	private AcctServerStatisticsProvider acctServerStatisticsProvider;

	public RADIUS_ACC_SERVER_MIBImpl(AcctServerStatisticsProvider statisticsProvider) {
		this.acctServerStatisticsProvider = statisticsProvider;

	}

	@Override
	protected Object createRadiusAccServMBean(String groupName,
			String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return acctServerStatisticsProvider;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(MBeanConstants.STATISTICS_PROTOCOL_RADIUS + "acct,data=" + name);
	}
}