package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTClassPathEntryMBean;

public class JvmRTClassPathEntryMBeanImpl implements JvmRTClassPathEntryMBean{

	private int classPathIndex;
	private String classPath;
	
	public JvmRTClassPathEntryMBeanImpl(int classPathIndex,String classPath) {
		this.classPathIndex = classPathIndex;
		this.classPath = classPath;
	}
	
	@Override
	public Byte[] getJvmRTClassPathItem(){
		if(classPath != null){
			return ArrayUtils.toObject(classPath.getBytes());
		}
		return null;
	}

	@Override
	public Integer getJvmRTClassPathIndex(){
		return classPathIndex;
	}
}