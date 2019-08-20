package com.elitecore.aaa.mibs.radius.accounting.server.extended;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.RADIUS_ACC_SERVER_MIB;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.RadiusAccServMBean;
import com.elitecore.commons.kpi.annotation.Table;

public class RADIUS_ACC_SERVER_MIBImpl extends RADIUS_ACC_SERVER_MIB {

	RadiusAccServMBean accServMBean;
	
	public RADIUS_ACC_SERVER_MIBImpl() {
		accServMBean = new RadiusAccServImpl(); 
	}
	
	@Override
	@Table(name = "radiusAccServ")
	protected Object createRadiusAccServMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return accServMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.RAD_ACCT_SERVER_MIB+",name=" + name);
	}
}
