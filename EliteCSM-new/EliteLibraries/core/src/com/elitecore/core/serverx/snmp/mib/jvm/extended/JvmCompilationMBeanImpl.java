package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmJITCompilerTimeMonitoring;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmCompilationMBean;

public class JvmCompilationMBeanImpl implements JvmCompilationMBean{

	private CompilationMXBean compilationMXBean;
	
	public JvmCompilationMBeanImpl() {
		compilationMXBean = ManagementFactory.getCompilationMXBean();
	}
	
	@Override
	public EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring(){
		if(compilationMXBean.isCompilationTimeMonitoringSupported()){
			return new EnumJvmJITCompilerTimeMonitoring("supported");
		}else{
			return new EnumJvmJITCompilerTimeMonitoring("unsupported");
		}
	}

	@Override
	public Long getJvmJITCompilerTimeMs() {
		return compilationMXBean.getTotalCompilationTime();
	}

	@Override
	public Byte[] getJvmJITCompilerName(){
		String compilationName = compilationMXBean.getName();
		if(compilationName != null){
			return ArrayUtils.toObject(compilationName.getBytes());
		}
		return new Byte[0];
	}

}
