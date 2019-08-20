package com.elitecore.aaa.mibs.rm.ippool.client.snmp;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IP_POOL_SERVICE_CLIENT_MIB;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IpPoolClientMIBObjectsMBean;

public class IP_POOL_SERVICE_CLIENT_MIBImpl extends IP_POOL_SERVICE_CLIENT_MIB {

	private IpPoolClientMIBObjectsMBean ipPoolClientMIBMbean;
	
	public IP_POOL_SERVICE_CLIENT_MIBImpl() {
		ipPoolClientMIBMbean = new IpPoolClientMIBObjectsMBeanImpl();
	}
	
	@Override
	protected Object createIpPoolClientMIBObjectsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return ipPoolClientMIBMbean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.IP_POOL_CLIENT_MIB + ",name=" +name);
	}
}
