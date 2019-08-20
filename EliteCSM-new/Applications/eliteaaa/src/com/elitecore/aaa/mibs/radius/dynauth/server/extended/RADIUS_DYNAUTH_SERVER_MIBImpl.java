package com.elitecore.aaa.mibs.radius.dynauth.server.extended;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RADIUS_DYNAUTH_SERVER_MIB;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RadiusDynAuthServerMIBObjectsMBean;
import com.elitecore.aaa.mibs.radius.dynauth.server.autogen.RadiusDynAuthServerScalarsMBean;
import com.elitecore.commons.kpi.annotation.Table;

public class RADIUS_DYNAUTH_SERVER_MIBImpl extends RADIUS_DYNAUTH_SERVER_MIB {

	RadiusDynAuthServerMIBObjectsMBean authServerMIBObjectsMBean;
	RadiusDynAuthServerScalarsMBean authServerScalarsMBean;
	
	public RADIUS_DYNAUTH_SERVER_MIBImpl() {
		authServerMIBObjectsMBean = new RadiusDynAuthServerMIBObjectsMBeanImpl();
		authServerScalarsMBean = new RadiusDynAuthServerScalarsMBeanImpl();
	}
	
	@Override
	@Table(name = "")
	protected Object createRadiusDynAuthServerMIBObjectsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return authServerMIBObjectsMBean;
	}
	
	@Override
	@Table(name = "radiusDynAuthServerScalars")
	protected Object createRadiusDynAuthServerScalarsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return authServerScalarsMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.DYNAUTH_SERVER_MIB+",name="+name);
	}
}
