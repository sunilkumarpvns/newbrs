package com.elitecore.aaa.rm.service.chargingservice.snmp;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.chargingservice.server.RMChargingServiceMIBListener;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.CHARGING_SERVICE_MIB;

public class CHARGING_SERVICE_MIBImpl extends CHARGING_SERVICE_MIB{

	private final static String MODULE = "CHARGING-SERVICE-MIB-IMPL";
	private ChargingServMIBMBeanImpl chargingSrvMBean;
	
	public CHARGING_SERVICE_MIBImpl(RMChargingServiceMIBListener chargingListener){
		chargingSrvMBean = new ChargingServMIBMBeanImpl(chargingListener);
	}
	
	@Override
	protected Object createChargingServMIBObjectsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return chargingSrvMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.CHARGING_SERVER_MIB+",name="+name);
	}
}
