package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmThreadInstanceEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmThreadInstanceTable;
import com.sun.management.snmp.SnmpStatusException;

public class JVMThreadStatisticsProvider implements IntervalBasedTask{
	
	private final static String MODULE = "JVM-THREAD-INFO-LISTENER";
	
	private ThreadMXBean threadMXMbean;
	private ConcurrentMap<String, ThreadInfo> threadInfoMap;
	private TableJvmThreadInstanceTable threadInstancetable;
	
	private int tableEntryIndex = 1;
	
	public JVMThreadStatisticsProvider(JVM_MANAGEMENT_MIBImpl jvmMib) {
		threadMXMbean = ManagementFactory.getThreadMXBean();
		threadInfoMap = new ConcurrentHashMap<String, ThreadInfo>();
		this.threadInstancetable = new TableJvmThreadInstanceTable(jvmMib);
		jvmMib.setTableJvmThreadInstanceTable(this.threadInstancetable);
	}
	
	@Override
	public long getInitialDelay() {
		return 0;
	}

	@Override
	public long getInterval() {
		return 1;
	}

	@Override
	public boolean isFixedDelay() {
		return false;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MINUTES;
	}


	@Override
	public void preExecute(AsyncTaskContext context) {

	}

	@Override
	public void execute(AsyncTaskContext context) {
		ThreadEntryUpdater updater = new ThreadEntryUpdater();
		updater.update();
		
		if (updater.isUpdated() == false) {
			return;
		}
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, updater.getUpdateCount() + " new entries added to thread instance table.");
		}
	}

	@Override
	public void postExecute(AsyncTaskContext context) {
		
	}
	
	class ThreadEntryUpdater {
		private int updateCount = 0;
		
		private void update() {
			for (long threadId : threadMXMbean.getAllThreadIds()) {			
				ThreadInfo threadInfo = threadMXMbean.getThreadInfo(threadId);

				if (isNew(threadInfo)) {
					addThreadEntry(threadInfo);
				}
				
			}
		}

		private void addThreadEntry(ThreadInfo threadInfo) {
			threadInfoMap.put(threadInfo.getThreadName(), threadInfo);
			try {
				addMBeanEntryFor(threadInfo);
				tableEntryIndex++;
				updateCount++;
			} catch (SnmpStatusException e) {
				if (LogManager.getLogger().isErrorLogLevel()) {
					LogManager.getLogger().error(MODULE, "Failed to add entry for thread instance: " 
							+ threadInfo.getThreadName() + " into thread instance table, at Index: " + tableEntryIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}

		private void addMBeanEntryFor(ThreadInfo threadInfo)
				throws SnmpStatusException {
			JvmThreadInstanceEntryMBean threadInstanceEntry = 
					new JvmThreadInstanceEntryMBeanImpl(tableEntryIndex, threadInfo);

			threadInstancetable.addEntry(threadInstanceEntry);
		}
		
		private boolean isUpdated() {
			return updateCount > 0;
		}
		
		private int getUpdateCount() {
			return updateCount;
		}
	}
	
	private boolean isNew(ThreadInfo threadInfo) {
		return !threadInfoMap.containsKey(threadInfo.getThreadName());
	}
}
