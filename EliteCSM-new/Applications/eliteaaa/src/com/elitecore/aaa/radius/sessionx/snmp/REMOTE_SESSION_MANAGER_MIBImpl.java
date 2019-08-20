package com.elitecore.aaa.radius.sessionx.snmp;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen.REMOTE_SESSION_MANAGER_MIB;

public class REMOTE_SESSION_MANAGER_MIBImpl extends REMOTE_SESSION_MANAGER_MIB{

	private RemoteSessionManagerMIBObjectsMBeanImpl remoteSmMbeanImpl;
	
	public REMOTE_SESSION_MANAGER_MIBImpl() {
		remoteSmMbeanImpl = new RemoteSessionManagerMIBObjectsMBeanImpl();
	}
	
	@Override
	protected Object createRemoteSessionManagerMIBObjectsMBean(
			String groupName, String groupOid, ObjectName groupObjname,
			MBeanServer server) {
		return remoteSmMbeanImpl;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.REMOTE_SM_MIB + ",name=" + name);
	}
}
