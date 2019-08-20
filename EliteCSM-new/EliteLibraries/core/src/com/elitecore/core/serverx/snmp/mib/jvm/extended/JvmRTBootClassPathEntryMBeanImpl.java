package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTBootClassPathEntryMBean;

public class JvmRTBootClassPathEntryMBeanImpl implements JvmRTBootClassPathEntryMBean{

	private int jvmRTBootClassPathIndex;
	private String bootClassPath;
	
	public JvmRTBootClassPathEntryMBeanImpl(int jvmRTBootClassPathIndex,String bootClassPath) {
		this.jvmRTBootClassPathIndex = jvmRTBootClassPathIndex;
		this.bootClassPath = bootClassPath;
	}

	@Override
	public Byte[] getJvmRTBootClassPathItem() {
		if(bootClassPath !=  null){
			return ArrayUtils.toObject(bootClassPath.getBytes());
		}
		return new Byte[0];
	}

	@Override
	public Integer getJvmRTBootClassPathIndex() {
		return jvmRTBootClassPathIndex;
	}
}
