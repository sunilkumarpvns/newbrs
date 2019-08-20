package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JVM_MANAGEMENT_MIB;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemManagerTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemPoolTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmThreadInstanceTable;

public class JVM_MANAGEMENT_MIBImpl extends JVM_MANAGEMENT_MIB{

	transient private JvmThreadingMBeanImpl jvmThreadingMBeanImpl;
	transient private JvmMemoryMBeanImpl jvmMemoryMBeanImpl;
	transient private JvmOSMBeanImpl jvmOSMBeanImpl;
	transient private JvmCompilationMBeanImpl jvmCompilationMBeanImpl;
	transient private JvmClassLoadingMBeanImpl jvmClassLoadingMBeanImpl;
	transient private JvmRuntimeMBeanImpl jvmRuntimeMBeanImpl;
	
	public JVM_MANAGEMENT_MIBImpl() {
		jvmThreadingMBeanImpl = new JvmThreadingMBeanImpl();
		jvmMemoryMBeanImpl = new JvmMemoryMBeanImpl();
		jvmOSMBeanImpl = new JvmOSMBeanImpl();
		jvmCompilationMBeanImpl = new JvmCompilationMBeanImpl();
		jvmClassLoadingMBeanImpl = new JvmClassLoadingMBeanImpl();
		jvmRuntimeMBeanImpl = new JvmRuntimeMBeanImpl();
	}
	
	@Override
	protected Object createJvmClassLoadingMBean(String arg0, String arg1,
			ObjectName arg2, MBeanServer arg3) {
		return jvmClassLoadingMBeanImpl;
	}

	@Override
	protected Object createJvmCompilationMBean(String arg0, String arg1,
			ObjectName arg2, MBeanServer arg3) {
		return jvmCompilationMBeanImpl;
	}

	@Override
	@Table(name = "jvmMemory")
	protected Object createJvmMemoryMBean(String arg0, String arg1,
			ObjectName arg2, MBeanServer arg3) {
		return jvmMemoryMBeanImpl;
	}

	@Override
	protected Object createJvmOSMBean(String arg0, String arg1,
			ObjectName arg2, MBeanServer arg3) {
		return jvmOSMBeanImpl;
	}

	@Override
	protected Object createJvmRuntimeMBean(String arg0, String arg1,
			ObjectName arg2, MBeanServer arg3) {
		return jvmRuntimeMBeanImpl;
	}

	@Override
	@Table(name = "jvmThreading")
	protected Object createJvmThreadingMBean(String arg0, String arg1,ObjectName arg2, MBeanServer arg3) {
		return jvmThreadingMBeanImpl;
	}

	public void setJvmMemManagerTable(TableJvmMemManagerTable jvmMemManagerTable) {
		jvmMemoryMBeanImpl.setJvmMemManagerTable(jvmMemManagerTable);
	}

	public void setJvmMemPoolTable(TableJvmMemPoolTable jvmMemPoolTable) {
		jvmMemoryMBeanImpl.setJvmMemPoolTable(jvmMemPoolTable);
	}

	public void setTableJvmThreadInstanceTable(TableJvmThreadInstanceTable jvmThreadInstancetable) {
		jvmThreadingMBeanImpl.setTableJvmThreadInstanceTable(jvmThreadInstancetable);
	}
}