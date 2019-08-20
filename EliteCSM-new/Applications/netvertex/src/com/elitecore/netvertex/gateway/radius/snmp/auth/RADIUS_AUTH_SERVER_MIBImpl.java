package com.elitecore.netvertex.gateway.radius.snmp.auth;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertex.gateway.radius.snmp.auth.autogen.RADIUS_AUTH_SERVER_MIB;

public class RADIUS_AUTH_SERVER_MIBImpl extends RADIUS_AUTH_SERVER_MIB {

	private AuthServerStatisticsProvider authServerStatisticsProvider;

	public RADIUS_AUTH_SERVER_MIBImpl(AuthServerStatisticsProvider authServerStatisticsProvider) {
		this.authServerStatisticsProvider = authServerStatisticsProvider;

	}

	@Override
	protected Object createRadiusAuthServMBean(String groupName,
			String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return authServerStatisticsProvider;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
			return new ObjectName(MBeanConstants.STATISTICS_PROTOCOL_RADIUS + "auth,data=" + name);
	}
}