package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemoryGCCall;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemoryGCVerboseLevel;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemoryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemGCTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemManagerTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemMgrPoolRelTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemPoolTable;

public class JvmMemoryMBeanImpl implements JvmMemoryMBean{

	private MemoryMXBean memoryMXBean;
	private TableJvmMemPoolTable jvmMemPoolTable;
	private TableJvmMemManagerTable jvmMemManagerTable;

	public JvmMemoryMBeanImpl() {
		memoryMXBean = ManagementFactory.getMemoryMXBean();
	}
	
	@Override
	public TableJvmMemMgrPoolRelTable accessJvmMemMgrPoolRelTable(){
		return null;
	}

	@Override
	@Column(name = "jvmMemoryNonHeapMaxSize" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryNonHeapMaxSize(){
		return memoryMXBean.getNonHeapMemoryUsage().getMax()/1024;
	}

	@Override
	@Column(name="jvmMemoryNonHeapCommitted" , type = java.sql.Types.BIGINT)
	public Long getJvmMemoryNonHeapCommitted(){
		return  memoryMXBean.getNonHeapMemoryUsage().getCommitted()/1024;
	}

	@Override
	@Column(name = "jvmMemoryNonHeapUsed" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryNonHeapUsed(){
		return  memoryMXBean.getNonHeapMemoryUsage().getUsed()/1024;
	}

	@Override
	@Table(name = "jvmMemPoolTable")
	public TableJvmMemPoolTable accessJvmMemPoolTable() {
		return jvmMemPoolTable;
	}

	@Override
	@Column(name = "jvmMemoryNonHeapInitSize" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryNonHeapInitSize(){
		return  memoryMXBean.getNonHeapMemoryUsage().getInit()/1024;
	}

	@Override
	@Column(name = "jvmMemoryHeapMaxSize" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryHeapMaxSize(){
		return memoryMXBean.getHeapMemoryUsage().getMax()/1024;
	}

	@Override
	@Column(name = "jvmMemoryHeapCommitted" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryHeapCommitted(){
		return memoryMXBean.getHeapMemoryUsage().getCommitted()/1024;
	}

	@Override
	public EnumJvmMemoryGCCall getJvmMemoryGCCall(){
		return null;
	}

	@Override
	public void setJvmMemoryGCCall(EnumJvmMemoryGCCall x){
		
	}

	@Override
	public void checkJvmMemoryGCCall(EnumJvmMemoryGCCall x){
		
	}

	@Override
	@Column(name = "jvmMemoryHeapUsed" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryHeapUsed(){
		return memoryMXBean.getHeapMemoryUsage().getUsed()/1024;
	}

	@Override
	public EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() {
		if(memoryMXBean.isVerbose()){
			return new EnumJvmMemoryGCVerboseLevel("verbose");
		}else{
			return new EnumJvmMemoryGCVerboseLevel("silent");
		}
	}

	@Override
	public void setJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel x){
		
	}

	@Override
	public void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel x){
		
	}

	@Override
	public TableJvmMemGCTable accessJvmMemGCTable(){
		return null;
	}

	@Override
	@Column(name = "jvmMemoryHeapInitSize" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryHeapInitSize(){
		return memoryMXBean.getHeapMemoryUsage().getInit()/1024;
	}

	@Override
	@Column(name = "jvmMemoryPendingFinalCount" ,type = java.sql.Types.BIGINT)
	public Long getJvmMemoryPendingFinalCount(){
		return (long) memoryMXBean.getObjectPendingFinalizationCount();
	}

	@Override
	@Table(name = "jvmMemManagerTable")
	public TableJvmMemManagerTable accessJvmMemManagerTable() {
		return jvmMemManagerTable;
	}

	public void setJvmMemPoolTable(TableJvmMemPoolTable jvmMemPoolTable) {
		this.jvmMemPoolTable = jvmMemPoolTable;
	}

	public void setJvmMemManagerTable(TableJvmMemManagerTable jvmMemManagerTable) {
		this.jvmMemManagerTable = jvmMemManagerTable;
	}
}
