package com.elitecore.aaa.mibs.radius.accounting.client.extended;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.RADIUS_ACC_CLIENT_MIB;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.RadiusAccClientMBean;
import com.elitecore.commons.kpi.annotation.Table;

public class RADIUS_ACC_CLIENT_MIBImpl extends RADIUS_ACC_CLIENT_MIB {

	private RadiusAccClientMBean radiusAccClientMBean;
	
	public RADIUS_ACC_CLIENT_MIBImpl() {
		radiusAccClientMBean = new RadiusAccClientImpl();
	}

	@Override
	@Table(name = "radiusAccClient")
	protected Object createRadiusAccClientMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return radiusAccClientMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.RAD_ACCT_CLIENT_MIB + ",name=" + name);
	}
}
