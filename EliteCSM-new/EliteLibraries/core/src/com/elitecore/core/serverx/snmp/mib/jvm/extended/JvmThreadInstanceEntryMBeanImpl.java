package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmThreadInstanceEntryMBean;


public class JvmThreadInstanceEntryMBeanImpl implements JvmThreadInstanceEntryMBean{

	private int tableEntryIndex;
	private ThreadInfo threadInfo;
	
	public JvmThreadInstanceEntryMBeanImpl(int tableEntryIndex,ThreadInfo threadInfo) {
		this.tableEntryIndex = tableEntryIndex;
		this.threadInfo = threadInfo;
	}

	@Override	
	public Byte[] getJvmThreadInstName() {
		if(threadInfo.getThreadName() != null){
			return ArrayUtils.toObject(threadInfo.getThreadName().getBytes());
		}
		return new Byte[0];
	}

	@Column(name = "jvmThreadInstName", type = java.sql.Types.VARCHAR)
	public String getJvmThreadInstNameExt() {
		if(threadInfo.getThreadName() != null){
			return threadInfo.getThreadName();
		}
		return "";
	}

	@Override
	@Column(name = "jvmThreadInstCpuTimeNs", type = java.sql.Types.BIGINT)
	public Long getJvmThreadInstCpuTimeNs() {
		long threadCpuTime = ManagementFactory.getThreadMXBean().getThreadCpuTime(threadInfo.getThreadId());
		if(threadCpuTime == -1L){
			threadCpuTime = 0L;
		}
		return new Long(threadCpuTime);
	}

	@Override
	@Column(name = "jvmThreadInstWaitTimeMs", type = java.sql.Types.BIGINT)
	public Long getJvmThreadInstWaitTimeMs() {
		long waitedTime = threadInfo.getWaitedTime();
		if(waitedTime == -1L){
			waitedTime = 0L;
		}
		return new Long(waitedTime);
	}

	@Override
	@Column(name = "jvmThreadInstWaitCount", type = java.sql.Types.BIGINT)
	public Long getJvmThreadInstWaitCount() {
		return new Long(threadInfo.getWaitedCount());
	}

	@Override
	@Column(name = "jvmThreadInstBlockTimeMs", type = java.sql.Types.BIGINT)
	public Long getJvmThreadInstBlockTimeMs() {
		long blockedTime = threadInfo.getBlockedTime();
		if(blockedTime ==  -1L){
			blockedTime =  0L;
		}
		return new Long(blockedTime);
	}

	@Override
	@Column(name = "jvmThreadInstBlockCount", type = java.sql.Types.BIGINT)
	public Long getJvmThreadInstBlockCount() {
		return new Long(threadInfo.getBlockedCount());
	}

	@Override
	public Byte[] getJvmThreadInstState() {
		String stateName = threadInfo.getThreadState().name();
		
		if(stateName != null){
			return new Byte[]{(byte)threadInfo.getThreadState().ordinal()};
		}
		return new Byte[0];
	}

	@Column(name = "jvmThreadInstState", type = java.sql.Types.VARCHAR)
	public String getJvmThreadInstStateExt() {
		String stateName = threadInfo.getThreadState().name();
		if(stateName != null){
			switch(threadInfo.getThreadState()){
				case BLOCKED:	return "Blocked";
				case NEW: return "New";
				case RUNNABLE: return "Runnable";
				case TERMINATED: return "Terminated";
				case TIMED_WAITING: return "Timed-Waiting";
				case WAITING: return "Waiting";
			}
		}
		return "";
	}

	@Override
	public String getJvmThreadInstLockOwnerPtr() {
		long id = threadInfo.getLockOwnerId();
		
		if(id == -1L){
			return new String("0.0");
		}
		return String.valueOf(id);
	}

	@Column(name = "jvmThreadInstLockOwnerPtr", type = java.sql.Types.VARCHAR)
	public String getJvmThreadInstLockOwnerPtrExt() {
		String lockOwnerName = threadInfo.getLockOwnerName();
		if(lockOwnerName != null){
			return lockOwnerName;
		}
		return "";
	}
	
	@Override
	@Column(name = "jvmThreadInstId", type = java.sql.Types.BIGINT)
	public Long getJvmThreadInstId() {
		return new Long(threadInfo.getThreadId());
	}

	@Override
	public Byte[] getJvmThreadInstLockName() {
		String lockName = threadInfo.getLockName();
		if(lockName != null){			
			return ArrayUtils.toObject(lockName.getBytes());
			
		}
		return new Byte[0];
	}

	
	@Column(name = "jvmThreadInstLockName", type = java.sql.Types.VARCHAR)
	public String getJvmThreadInstLockNameExt() {
		String lockName = threadInfo.getLockName();
		if(lockName != null){			
			return lockName;
			
		}
		return "";
	}

	@Override	
	public Byte[] getJvmThreadInstIndex() {
		Byte[] instanceWrapperIndxByte= wrap(Numbers.toByteArray(tableEntryIndex, 8));
		return instanceWrapperIndxByte;
	}
	
	private Byte[] wrap(byte[] primitiveBytes) {
		Byte[] wrappedBytes = new Byte[primitiveBytes.length];
		for (int i=0 ; i < primitiveBytes.length ; i++) {
			wrappedBytes[i] = primitiveBytes[i];
		}
		return wrappedBytes;
	}
	
	@Column(name = "jvmThreadInstIndex", type = java.sql.Types.INTEGER)
	public Integer getJvmThreadInstIndexExt() {
		return tableEntryIndex;
	}

	@Column(name = "jvmThreadLockedMonitor", type = java.sql.Types.VARCHAR)
	public String getMonitorInfo(){
		MonitorInfo[] monitorInfoArry = threadInfo.getLockedMonitors();
		String monitorInfoString = ""; 
		if(monitorInfoArry != null && monitorInfoArry.length > 0){
			for (MonitorInfo monitorInfo : monitorInfoArry) {
				monitorInfoString = monitorInfo +",";
			}
			monitorInfoString = monitorInfoString.substring(0,monitorInfoString.length()-1);
		}
		return monitorInfoString;
	}
	
	@Column(name = "jvmThreadLockInfo", type = java.sql.Types.VARCHAR)
	public String getLockInfo(){
		LockInfo[] lockInfoArry = threadInfo.getLockedSynchronizers();
		String lockInfoString = "";
		if(lockInfoArry != null && lockInfoArry.length > 0){
			for (LockInfo lockInfo : lockInfoArry) {
				lockInfoString = lockInfo +",";
			}
			lockInfoString = lockInfoString.substring(0, lockInfoString.length()-1);
		}
		return lockInfoString;
	}
}
