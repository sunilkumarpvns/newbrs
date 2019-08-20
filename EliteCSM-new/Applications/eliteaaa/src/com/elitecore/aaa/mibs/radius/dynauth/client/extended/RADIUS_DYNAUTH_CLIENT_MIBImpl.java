package com.elitecore.aaa.mibs.radius.dynauth.client.extended;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RADIUS_DYNAUTH_CLIENT_MIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RadiusDynAuthClientMIBObjectsMBean;
import com.elitecore.aaa.mibs.radius.dynauth.client.autogen.RadiusDynAuthClientScalarsMBean;
import com.elitecore.commons.kpi.annotation.Table;

public class RADIUS_DYNAUTH_CLIENT_MIBImpl extends RADIUS_DYNAUTH_CLIENT_MIB {

	RadiusDynAuthClientMIBObjectsMBean authClientMIBObjectsMBean;
	RadiusDynAuthClientScalarsMBean authClientScalarsMBean;
	
	public RADIUS_DYNAUTH_CLIENT_MIBImpl() {
		authClientMIBObjectsMBean = new RadiusDynAuthClientMIBObjectsMBeanImpl();
		authClientScalarsMBean = new RadiusDynAuthClientScalarsMBeanImpl();
	}
	
	@Override
	@Table(name = "")
	protected Object createRadiusDynAuthClientMIBObjectsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return authClientMIBObjectsMBean;
	}
	
	@Override
	@Table(name = "radiusDynAuthClientScalars")
	protected Object createRadiusDynAuthClientScalarsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return authClientScalarsMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.DYNAUTH_CLIENT_MIB+",name="+name);
	}
}
