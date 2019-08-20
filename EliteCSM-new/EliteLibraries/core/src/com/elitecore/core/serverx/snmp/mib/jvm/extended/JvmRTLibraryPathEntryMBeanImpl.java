package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTLibraryPathEntryMBean;

public class JvmRTLibraryPathEntryMBeanImpl implements JvmRTLibraryPathEntryMBean{

	private int libraryPathIndex;
	private String libraryPath;
	
	public JvmRTLibraryPathEntryMBeanImpl(int libraryPathIndex,String libraryPath) {
		this.libraryPathIndex = libraryPathIndex;
		this.libraryPath = libraryPath;
	}
	
	@Override
	public Byte[] getJvmRTLibraryPathItem(){
		if(libraryPath != null){
			return ArrayUtils.toObject(libraryPath.getBytes());
		}
		return new Byte[0];
	}

	@Override
	public Integer getJvmRTLibraryPathIndex(){
		return libraryPathIndex;
	}
}