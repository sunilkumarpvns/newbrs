package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmThreadContentionMonitoring;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmThreadCpuTimeMonitoring;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmThreadingMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmThreadInstanceTable;

public class JvmThreadingMBeanImpl implements JvmThreadingMBean {

	private ThreadMXBean threadMXBean;
	private TableJvmThreadInstanceTable jvmThreadInstancetable;
	
	public JvmThreadingMBeanImpl() {
		threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
	}
	
	@Override
	public EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring(){
		return null;
	}

	@Override
	public void setJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring x){
	}

	@Override
	public void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring x){
	}

	@Override
	public EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring(){
		return null;
	}

	@Override
	public void setJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring x){
		
	}

	@Override
	public void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring x){
		
	}

	@Override
	@Column(name = "jvmThreadTotalStartedCount", type=java.sql.Types.BIGINT)
	public Long getJvmThreadTotalStartedCount(){
		return threadMXBean.getTotalStartedThreadCount();
	}

	@Override
	@Column(name = "jvmThreadPeakCount", type=java.sql.Types.BIGINT)
	public Long getJvmThreadPeakCount(){
		return (long) threadMXBean.getPeakThreadCount();
	}

	@Override
	@Column(name = "jvmThreadDaemonCount", type=java.sql.Types.BIGINT)
	public Long getJvmThreadDaemonCount(){
		return (long) threadMXBean.getDaemonThreadCount();
	}

	@Override
	@Column(name = "jvmThreadCount" , type = java.sql.Types.BIGINT)
	public Long getJvmThreadCount(){
		return (long) threadMXBean.getThreadCount();
	}

	@Override
	public TableJvmThreadInstanceTable accessJvmThreadInstanceTable(){
		return this.jvmThreadInstancetable;
	}

	@Override
	public Long getJvmThreadPeakCountReset(){
		return null;
	}

	@Override
	public void setJvmThreadPeakCountReset(Long x){
		
	}

	@Override
	public void checkJvmThreadPeakCountReset(Long x){
		
	}

	public void setTableJvmThreadInstanceTable(TableJvmThreadInstanceTable jvmThreadInstancetable) {
		this.jvmThreadInstancetable = jvmThreadInstancetable;
	}
}
