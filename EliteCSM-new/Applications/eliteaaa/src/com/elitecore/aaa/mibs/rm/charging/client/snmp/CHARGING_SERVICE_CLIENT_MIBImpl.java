package com.elitecore.aaa.mibs.rm.charging.client.snmp;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.CHARGING_SERVICE_CLIENT_MIB;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.ChargingServerTableMBean;

public class CHARGING_SERVICE_CLIENT_MIBImpl extends CHARGING_SERVICE_CLIENT_MIB{

	private ChargingServerTableMBean chargingServerTableMbean;
	
	public CHARGING_SERVICE_CLIENT_MIBImpl() {
		chargingServerTableMbean = new ChargingServerTableMBeanImpl();
	}
	
	@Override
	protected Object createChargingServerTableMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return chargingServerTableMbean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.CHARGING_CLIENT_MIB + ",name=" +name);
	}
}
