package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmRTBootClassPathSupport;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRuntimeMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTBootClassPathTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTClassPathTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTInputArgsTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTLibraryPathTable;

public class JvmRuntimeMBeanImpl implements JvmRuntimeMBean{

	private RuntimeMXBean runtimeMXBean;
	
	private TableJvmRTLibraryPathTable jvmRTLibraryPathTable;
	private TableJvmRTClassPathTable jvmRTClassPathTable;
	private TableJvmRTBootClassPathTable jvmRTBootClassPathTable;
	private TableJvmRTInputArgsTable jvmRTInputArgsTable;
	
	public JvmRuntimeMBeanImpl() {
		runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	}
	
	@Override
	public TableJvmRTLibraryPathTable accessJvmRTLibraryPathTable(){
		return jvmRTLibraryPathTable;
	}

	@Override
	public TableJvmRTClassPathTable accessJvmRTClassPathTable(){
		return jvmRTClassPathTable;
	}

	@Override
	public TableJvmRTBootClassPathTable accessJvmRTBootClassPathTable(){
		return jvmRTBootClassPathTable;
	}

	@Override
	public EnumJvmRTBootClassPathSupport getJvmRTBootClassPathSupport(){
		if(runtimeMXBean.isBootClassPathSupported()){
			return new EnumJvmRTBootClassPathSupport("supported");
		}else{
			return new EnumJvmRTBootClassPathSupport("unsupported");
		}
	}

	@Override
	public TableJvmRTInputArgsTable accessJvmRTInputArgsTable(){
		return jvmRTInputArgsTable;
	}

	@Override
	public String getJvmRTManagementSpecVersion(){
		return runtimeMXBean.getManagementSpecVersion();
	}

	@Override
	public String getJvmRTSpecVersion(){
		return runtimeMXBean.getSpecVersion();
	}

	@Override
	public String getJvmRTSpecVendor(){
		return runtimeMXBean.getSpecVendor();
	}

	@Override
	public String getJvmRTSpecName(){
		return runtimeMXBean.getSpecName();
	}

	@Override
	public String getJvmRTVMVersion(){
		return runtimeMXBean.getVmVersion();
	}

	@Override
	public String getJvmRTVMVendor(){
		return runtimeMXBean.getVmVendor();
	}

	@Override
	public Long getJvmRTStartTimeMs(){
		return runtimeMXBean.getStartTime();
	}

	@Override
	public Long getJvmRTUptimeMs(){
		return runtimeMXBean.getUptime();
	}

	@Override
	public Byte[] getJvmRTVMName(){
		String vmName = runtimeMXBean.getVmName();
		if(vmName != null){
			return ArrayUtils.toObject(vmName.getBytes());
		}
		return new Byte[0];
	}

	@Override
	public String getJvmRTName(){
		return runtimeMXBean.getName();
	}

	@Override
	public Integer getJvmRTInputArgsCount(){
		return runtimeMXBean.getInputArguments().size();
	}
}
