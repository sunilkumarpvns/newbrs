package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmClassesVerboseLevel;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmClassLoadingMBean;

public class JvmClassLoadingMBeanImpl implements JvmClassLoadingMBean{

	private ClassLoadingMXBean classLoadingMXBean;
	
	public JvmClassLoadingMBeanImpl() {
		classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
	}
	
	@Override
	public EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel(){
		if(classLoadingMXBean.isVerbose()){
			return new EnumJvmClassesVerboseLevel("verbose");
		}else{
			return new EnumJvmClassesVerboseLevel("silent");
		}
	}

	@Override
	public void setJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel x){
		
	}

	@Override
	public void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel x){
		
	}

	@Override
	public Long getJvmClassesUnloadedCount() {
		return classLoadingMXBean.getUnloadedClassCount();
	}

	@Override
	public Long getJvmClassesTotalLoadedCount() {
		return classLoadingMXBean.getTotalLoadedClassCount();
	}

	@Override
	public Long getJvmClassesLoadedCount() {
		return new Long(classLoadingMXBean.getLoadedClassCount());
	}
}
