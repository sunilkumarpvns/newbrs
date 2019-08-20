package com.elitecore.core.serverx.snmp.mib.mib2.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.core.serverx.snmp.mib.mib2.autogen.RFC1213_MIB;
import com.elitecore.core.serverx.snmp.mib.mib2.autogen.SnmpMBean;
import com.elitecore.core.serverx.snmp.mib.mib2.autogen.SystemMBean;

public class RFC1213_MIBImpl extends RFC1213_MIB {
	
	transient private SnmpMBean snmp;
	transient private SystemMBean system;
	public RFC1213_MIBImpl(SnmpMBean snmp , SystemMBean system) {
		this.snmp = snmp;
		this.system = system;
	}

	@Override
	protected Object createSnmpMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return snmp;
	}
	
	@Override
	protected Object createSystemMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return system;
	}
}
