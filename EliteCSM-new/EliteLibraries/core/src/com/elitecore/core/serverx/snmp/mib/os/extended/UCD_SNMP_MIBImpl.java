package com.elitecore.core.serverx.snmp.mib.os.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.core.serverx.snmp.mib.os.autogen.MemoryMBean;
import com.elitecore.core.serverx.snmp.mib.os.autogen.SystemStatsMBean;
import com.elitecore.core.serverx.snmp.mib.os.autogen.UCD_SNMP_MIB;
import com.elitecore.core.serverx.snmp.mib.os.autogen.UcdavisMBean;
import com.elitecore.core.serverx.snmp.mib.os.data.SystemDetailProvider;

public class UCD_SNMP_MIBImpl extends UCD_SNMP_MIB{

	private static final long serialVersionUID = 1L;
	
	private UcdavisMBean ucdavisMBean; //NOSONAR
	private MemoryMBean memoryMBean; //NOSONAR
	private SystemStatsMBean systemStatsMBean; //NOSONAR
	
	public UCD_SNMP_MIBImpl(SystemDetailProvider systemDetailProvider) {
		this.memoryMBean = new MemoryMBeanImpl(systemDetailProvider);
		this.ucdavisMBean = new UcdavisImpl(systemDetailProvider);
		this.systemStatsMBean = new SystemStatsImpl(systemDetailProvider);
	}
	
	@Override
	@Table(name = "memory")
	protected Object createMemoryMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return memoryMBean;
	}
	
	@Override
	@Table(name = "")
	protected Object createUcdavisMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return ucdavisMBean;
	}
	
	@Override
	@Table(name = "systemStats")
	protected Object createSystemStatsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return systemStatsMBean;
	}
	
}