package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemGCEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemManagerEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemMgrPoolRelEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemPoolEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemGCTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemManagerTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemMgrPoolRelTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmMemPoolTable;
import com.sun.management.snmp.SnmpStatusException;

public class JvmMemoryStatisticsProvider{

	private final static String MODULE = "JVM-MEMORY-MANAGER-LISTENER";
	
	private TableJvmMemManagerTable jvmMemManagerTable;
	private TableJvmMemPoolTable jvmMemPoolTable; 
	private TableJvmMemGCTable jvmMemGCTable;
	private TableJvmMemMgrPoolRelTable jvmMemMgrPoolRelTable; 
	
	private int jvmMemManagerIndex = 1;
	private int jvmMemPoolIndex = 1;
	
	private Map<String, Integer> memMgrNameToIndexMap;
	private Map<String, Integer> memPoolNameToIndexMap;
	
	public JvmMemoryStatisticsProvider(JVM_MANAGEMENT_MIBImpl jvmMIB) {
		
		this.jvmMemManagerTable = new TableJvmMemManagerTable(jvmMIB);
		this.jvmMemPoolTable = new TableJvmMemPoolTable(jvmMIB);
		this.jvmMemGCTable = new TableJvmMemGCTable(jvmMIB);
		this.jvmMemMgrPoolRelTable = new TableJvmMemMgrPoolRelTable(jvmMIB);
		
		jvmMIB.setJvmMemManagerTable(this.jvmMemManagerTable);
		jvmMIB.setJvmMemPoolTable(this.jvmMemPoolTable);

		memMgrNameToIndexMap = new HashMap<String, Integer>();
		memPoolNameToIndexMap = new HashMap<String, Integer>();
	}
	
	public void addMemoryStatistics(){
		
		addMemoryManagerEntry();
		
		addMemoryPoolEntry();
		
		addGarbageCollectorEntry();
		
		addJvmMemMgrPoolRelEntry();
	}
	
	private void addJvmMemMgrPoolRelEntry(){
		List<MemoryManagerMXBean> memoryManagerMXBeansList = ManagementFactory.getMemoryManagerMXBeans();
		
		String memoryMgrName;
		int memoryMgrIndex = 1;
		String memoryPoolName;
		int memoryMgrPoolIndex = 1;
		
		for (MemoryManagerMXBean memoryManagerMXBean : memoryManagerMXBeansList) {
			memoryMgrName = memoryManagerMXBean.getName();
			
			if(memMgrNameToIndexMap.containsKey(memoryMgrName)){
				memoryMgrIndex = memMgrNameToIndexMap.get(memoryMgrName); 
			}
			
			String[] memoryPoolNames = memoryManagerMXBean.getMemoryPoolNames();
			for (String poolName : memoryPoolNames) {
				memoryPoolName = poolName;
				if(memPoolNameToIndexMap.containsKey(poolName)){
					memoryMgrPoolIndex = memPoolNameToIndexMap.get(poolName);
				}

				try {
					JvmMemMgrPoolRelEntryMBean jvmMemMgrPoolRelEntry = new JvmMemMgrPoolRelEntryMBeanImpl(memoryMgrIndex,memoryMgrName,memoryMgrPoolIndex,memoryPoolName);
					jvmMemMgrPoolRelTable.addEntry(jvmMemMgrPoolRelEntry);
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Entry for MemoryManager: "+memoryMgrName+" to Pool : "+memoryPoolName+" Successfully added");
					}
				} catch (SnmpStatusException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Fail to add Entry for MemoryManager: "+memoryMgrName+" to Pool : "+memoryPoolName+" Successfully added");
					}
					LogManager.getLogger().trace(e);
				}
			}
		}
	}
	
	private void addGarbageCollectorEntry() {
		List<GarbageCollectorMXBean> garbageCollectorList = ManagementFactory.getGarbageCollectorMXBeans();
		
		String garbageCollectorName = null;
		int jvmMemGCIndex = 1;
		try{
			for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorList) {
				
				garbageCollectorName = garbageCollectorMXBean.getName();
				
				if(memMgrNameToIndexMap.containsKey(garbageCollectorName)){

					jvmMemGCIndex = memMgrNameToIndexMap.get(garbageCollectorName);
					
					JvmMemGCEntryMBean jvmMemGCEntry = new JvmMemGCEntryMBeanImpl(jvmMemGCIndex, garbageCollectorMXBean);
					jvmMemGCTable.addEntry(jvmMemGCEntry);
					
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Entry for GC: "+garbageCollectorName+" Successfully added at index: "+jvmMemGCIndex);
					}
				}
			}
		}catch(SnmpStatusException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Failed to add Entry for GC: "+garbageCollectorName+" at index: "+jvmMemGCIndex);
			}
			LogManager.getLogger().trace(e);
		}
	}

	private void addMemoryPoolEntry() {
		List<MemoryPoolMXBean> memoryPoolMXBeansList = ManagementFactory.getMemoryPoolMXBeans();

		for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeansList) {
			try {
				
				JvmMemPoolEntryMBean jvmMemPoolEntry = new JvmMemPoolEntryMBeanImpl(jvmMemPoolIndex,memoryPoolMXBean);
				jvmMemPoolTable.addEntry(jvmMemPoolEntry);
				memPoolNameToIndexMap.put(memoryPoolMXBean.getName(), jvmMemPoolIndex);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Entry for Memory Pool: "+ memoryPoolMXBean.getName() +" Successfully added at index: "+jvmMemPoolIndex);
				}
				jvmMemPoolIndex++;
				
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Failed to add Entry for Memory Pool: "+memoryPoolMXBean.getName()+" at index: "+jvmMemPoolIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}
	}

	private void addMemoryManagerEntry() {
		
		List<MemoryManagerMXBean> memoryManagerMXBeansList = ManagementFactory.getMemoryManagerMXBeans();
		for (MemoryManagerMXBean memoryManagerMXBean : memoryManagerMXBeansList) {
		
			try {
			
				JvmMemManagerEntryMBean jvmMemMgrEntry = new JvmMemManagerEntryMBeanImpl(jvmMemManagerIndex,memoryManagerMXBean);
				jvmMemManagerTable.addEntry(jvmMemMgrEntry);
				memMgrNameToIndexMap.put(memoryManagerMXBean.getName(), jvmMemManagerIndex);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Entry for Memory Manager: "+memoryManagerMXBean.getName()+" Successfully added at index: "+jvmMemManagerIndex);
				}
				jvmMemManagerIndex++;
				
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Failed to add Entry for Memory Manager: "+memoryManagerMXBean.getName()+" at index: "+jvmMemManagerIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}
	}
}
