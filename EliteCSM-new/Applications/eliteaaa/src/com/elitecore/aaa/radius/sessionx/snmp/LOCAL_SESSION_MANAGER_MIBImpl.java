package com.elitecore.aaa.radius.sessionx.snmp;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen.LOCAL_SESSION_MANAGER_MIB;

public class LOCAL_SESSION_MANAGER_MIBImpl extends LOCAL_SESSION_MANAGER_MIB{

	private LocalSessionManagerMIBObjectsMBeanImpl localSmMBeanImpl;
	
	public LOCAL_SESSION_MANAGER_MIBImpl() {
		localSmMBeanImpl = new LocalSessionManagerMIBObjectsMBeanImpl();
	}
	
	@Override
	protected Object createLocalSessionManagerMIBObjectsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return localSmMBeanImpl;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.LOCAL_SM_MIB + ",name=" + name);
	}
}
