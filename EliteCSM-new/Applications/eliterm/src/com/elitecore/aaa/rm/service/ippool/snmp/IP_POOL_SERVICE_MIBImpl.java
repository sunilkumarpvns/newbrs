package com.elitecore.aaa.rm.service.ippool.snmp;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IP_POOL_SERVICE_MIB;

public class IP_POOL_SERVICE_MIBImpl extends IP_POOL_SERVICE_MIB{

	private IPPoolServMIBObjectsMBeanImpl ipPoolServiceMbeanImpl;
	
	public IP_POOL_SERVICE_MIBImpl(RMIPPoolServiceMIBListener ipPoolServiceListener) {
		ipPoolServiceMbeanImpl = new IPPoolServMIBObjectsMBeanImpl(ipPoolServiceListener);
	}
	
	@Override
	protected Object createIpPoolServMIBObjectsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return ipPoolServiceMbeanImpl;
	}

	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.IP_POOL_SERVER_MIB +",name="+ name);
	}
}
