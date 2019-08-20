package com.elitecore.aaa.mibs.radius.authentication.client.extended;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.RADIUS_AUTH_CLIENT_MIB;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.RadiusAuthClientMBean;
import com.elitecore.commons.kpi.annotation.Table;


public class RADIUS_AUTH_CLIENT_MIBImpl extends RADIUS_AUTH_CLIENT_MIB {
	
	RadiusAuthClientMBean authClientMBean;
	
	public RADIUS_AUTH_CLIENT_MIBImpl() {
		authClientMBean = new RadiusAuthClientMBeanImpl();
	}
	
	@Override
	@Table(name = "radiusAuthClient")
	protected Object createRadiusAuthClientMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return authClientMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.RAD_AUTH_CLIENT_MIB + ",name=" + name);
	}
}
