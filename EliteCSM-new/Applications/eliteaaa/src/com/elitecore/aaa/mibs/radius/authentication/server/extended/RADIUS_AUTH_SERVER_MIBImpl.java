package com.elitecore.aaa.mibs.radius.authentication.server.extended;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.RADIUS_AUTH_SERVER_MIB;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.RadiusAuthServMBean;
import com.elitecore.commons.kpi.annotation.Table;

public class RADIUS_AUTH_SERVER_MIBImpl extends RADIUS_AUTH_SERVER_MIB {

	RadiusAuthServMBean authServMBean;
	
	public RADIUS_AUTH_SERVER_MIBImpl() {
		authServMBean = new RadiusAuthServMBeanImpl();
	}
	
	@Override
	@Table(name = "radiusAuthServ")
	protected Object createRadiusAuthServMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return authServMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.RAD_AUTH_SERVER_MIB + ",name=" + name);
	}
}
