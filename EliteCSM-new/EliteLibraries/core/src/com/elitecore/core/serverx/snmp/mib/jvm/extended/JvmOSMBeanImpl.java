package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmOSMBean;

public class JvmOSMBeanImpl implements JvmOSMBean{

	private OperatingSystemMXBean operatingSystemMXBean;
	
	public JvmOSMBeanImpl() {
		operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public Integer getJvmOSProcessorCount(){
		return operatingSystemMXBean.getAvailableProcessors();
	}

	@Override
	public String getJvmOSVersion(){
		return operatingSystemMXBean.getVersion();
	}

	@Override
	public String getJvmOSArch(){
		return operatingSystemMXBean.getArch();
	}

	@Override
	public Byte[] getJvmOSName(){
		String osName = operatingSystemMXBean.getName();
		if(osName != null){
			return ArrayUtils.toObject(osName.getBytes());
		}
		return new Byte[0];		
	}
}
