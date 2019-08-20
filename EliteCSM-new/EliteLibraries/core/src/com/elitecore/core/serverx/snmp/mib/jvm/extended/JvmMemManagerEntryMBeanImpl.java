package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.MemoryManagerMXBean;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemManagerState;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemManagerEntryMBean;

public class JvmMemManagerEntryMBeanImpl implements JvmMemManagerEntryMBean {

	private int jvmMemManagerIndex;
	private MemoryManagerMXBean memoryManagerMXBean;
	
	public JvmMemManagerEntryMBeanImpl(int jvmMemManagerIndex,MemoryManagerMXBean memoryManagerMXBean) {
		this.jvmMemManagerIndex = jvmMemManagerIndex;
		this.memoryManagerMXBean = memoryManagerMXBean;
	}

	@Override
	@Column(name = "jvmMemManagerState", type = java.sql.Types.VARCHAR)
	public EnumJvmMemManagerState getJvmMemManagerState(){
		if(memoryManagerMXBean.isValid()){
			return new EnumJvmMemManagerState("valid");
		}else{
			return new EnumJvmMemManagerState("invalid");
		}
	}

	@Override
	public Byte[] getJvmMemManagerName() {
		if(memoryManagerMXBean.getName() != null){
			return ArrayUtils.toObject(memoryManagerMXBean.getName().getBytes());
		}
		return new Byte[0];
	}

	@Column(name = "jvmMemManagerName", type = java.sql.Types.VARCHAR)
	public String getJvmMemManagerNameExt() {
		String name = memoryManagerMXBean.getName();
		if(name != null){
			return name;
		}
		return "";
	}
	
	@Override
	@Column(name = "jvmMemManagerIndex", type = java.sql.Types.INTEGER)
	public Integer getJvmMemManagerIndex() {
		return new Integer(jvmMemManagerIndex);
	}
}