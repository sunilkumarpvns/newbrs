package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.GarbageCollectorMXBean;

import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemGCEntryMBean;

public class JvmMemGCEntryMBeanImpl implements JvmMemGCEntryMBean {

	private int jvmGCIndex;
	private GarbageCollectorMXBean garbageCollectorMXBean;
	
	public JvmMemGCEntryMBeanImpl(int jvmGarbageCollectorIndex,GarbageCollectorMXBean garbageCollectorMXBean) {
		this.jvmGCIndex = jvmGarbageCollectorIndex;
		this.garbageCollectorMXBean = garbageCollectorMXBean;
	}

	@Override
	public Long getJvmMemGCTimeMs() {
		return garbageCollectorMXBean.getCollectionTime();
	}

	@Override
	public Long getJvmMemGCCount() {
		return garbageCollectorMXBean.getCollectionCount();
	}

	@Override
	public Integer getJvmMemManagerIndex() {
		return jvmGCIndex;
	}
}
