package com.elitecore.netvertex.gateway.radius.snmp.dynaauth;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.RADIUS_DYNAUTH_CLIENT_MIB;

public class RADIUS_DYNAUTH_CLIENT_MIBImpl extends RADIUS_DYNAUTH_CLIENT_MIB {

	private DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider;

	public RADIUS_DYNAUTH_CLIENT_MIBImpl(DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider) {
		this.dynaAuthClientStatisticsProvider = dynaAuthClientStatisticsProvider;
	}

	@Override
	protected Object createRadiusDynAuthClientScalarsMBean(String groupName,
			String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return dynaAuthClientStatisticsProvider;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
			return new ObjectName(MBeanConstants.STATISTICS_PROTOCOL_RADIUS + "dynaauth,data=" + name);
	}
}