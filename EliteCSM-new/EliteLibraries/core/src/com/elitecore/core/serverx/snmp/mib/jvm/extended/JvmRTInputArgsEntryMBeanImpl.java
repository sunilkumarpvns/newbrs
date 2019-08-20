package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTInputArgsEntryMBean;

public class JvmRTInputArgsEntryMBeanImpl implements JvmRTInputArgsEntryMBean{

	private int libraryPathIndex;
	private String jvmRTInputArgsItem;

	public JvmRTInputArgsEntryMBeanImpl(int libraryPathIndex,String jvmRTInputArgsItem) {
		this.libraryPathIndex = libraryPathIndex;
		this.jvmRTInputArgsItem = jvmRTInputArgsItem;
	}
	
	@Override
	public Byte[] getJvmRTInputArgsItem(){
		if(jvmRTInputArgsItem != null){
			return ArrayUtils.toObject(jvmRTInputArgsItem.getBytes());
		}
		return new Byte[0];
	}

	@Override
	public Integer getJvmRTInputArgsIndex(){
		return libraryPathIndex;
	}
}
