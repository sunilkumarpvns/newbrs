package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemMgrPoolRelEntryMBean;

public class JvmMemMgrPoolRelEntryMBeanImpl implements JvmMemMgrPoolRelEntryMBean {

	private int memoryMgrIndex;
	private String memoryMgrName;
	private int memoryPoolIndex;
	private String memoryPoolName;
	
	public JvmMemMgrPoolRelEntryMBeanImpl(int memoryMgrIndex,String memoryMgrName, int memoryPoolIndex, String memoryPoolName) {
		this.memoryMgrIndex = memoryMgrIndex;
		this.memoryMgrName = memoryMgrName;
		this.memoryPoolIndex = memoryPoolIndex;
		this.memoryPoolName = memoryPoolName;
	}

	@Override
	public Byte[] getJvmMemMgrRelPoolName() {
		if(memoryPoolName != null){
			return ArrayUtils.toObject(memoryPoolName.getBytes());
		}
		return new Byte[0];
	}

	@Override
	public Byte[] getJvmMemMgrRelManagerName() {
		if(memoryMgrName != null){
			return ArrayUtils.toObject(memoryMgrName.getBytes());
		}
		return new Byte[0];
	}

	@Override
	public Integer getJvmMemManagerIndex() {
		return memoryMgrIndex;
	}

	@Override
	public Integer getJvmMemPoolIndex() {
		return memoryPoolIndex;
	}
}
