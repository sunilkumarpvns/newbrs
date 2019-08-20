package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JVM_MANAGEMENT_MIB;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTBootClassPathEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTClassPathEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmRTInputArgsEntryMBean;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTBootClassPathTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTClassPathTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTInputArgsTable;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.TableJvmRTLibraryPathTable;
import com.sun.management.snmp.SnmpStatusException;

public class JvmRunTimeStatisticsProvider {

	private static final String MODULE = "JVM-RUNTIME-STATISTICS-PROVIDER";
	
	private TableJvmRTLibraryPathTable jvmRTLibraryPathTable;
	private TableJvmRTClassPathTable jvmRTClassPathTable;
	private TableJvmRTBootClassPathTable jvmRTBootClassPathTable;
	private TableJvmRTInputArgsTable jvmRTInputArgsTable;
	
	private int jvmRTLibraryPathIndex = 1;
	private int jvmRTClassPathIndex = 1;
	private int jvmRTBootClassPathIndex = 1;
	private int jvmRTInputArgsIndex = 1;
	
	public JvmRunTimeStatisticsProvider(JVM_MANAGEMENT_MIB jvmMIB) {
		
		this.jvmRTLibraryPathTable = new TableJvmRTLibraryPathTable(jvmMIB);   
		this.jvmRTClassPathTable = new TableJvmRTClassPathTable(jvmMIB);       
		this.jvmRTBootClassPathTable = new TableJvmRTBootClassPathTable(jvmMIB);
		this.jvmRTInputArgsTable = new TableJvmRTInputArgsTable(jvmMIB);
	}

	public void addJvmRunTimeStatistics(){
		addJvmRTLibraryPathEntry();
		
		addJvmRTClassPathEntry();
		
		addJvmRTBootClassPathEntry();
		
		addJvmRTInputArgsEntry();
	}
	
	private void addJvmRTLibraryPathEntry() {
		String libraryPath = ManagementFactory.getRuntimeMXBean().getLibraryPath();
		String[] libraryPathArry = libraryPath.split(File.pathSeparator);
		for (int i = 0; i < libraryPathArry.length; i++) {
			try {
				JvmRTLibraryPathEntryMBeanImpl libraryPathEntry = new JvmRTLibraryPathEntryMBeanImpl(jvmRTLibraryPathIndex, libraryPathArry[i]);
				jvmRTLibraryPathTable.addEntry(libraryPathEntry);
				jvmRTLibraryPathIndex++;
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Failed to add entry for library path: "+libraryPathArry[i]+" at index: " + jvmRTLibraryPathIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, (jvmRTLibraryPathIndex - 1) + " entries added into RT librarypath table.");
		}
	}

	private void addJvmRTClassPathEntry() {
		String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
		String[] classPathArry = classPath.split(File.pathSeparator);
		for (int i = 0; i < classPathArry.length; i++) {
			try {
				JvmRTClassPathEntryMBean classPathEntry = new JvmRTClassPathEntryMBeanImpl(jvmRTClassPathIndex, classPathArry[i]);
				jvmRTClassPathTable.addEntry(classPathEntry);
				jvmRTClassPathIndex++;
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Failed to add entry for class path: "+classPathArry[i]+" at index: " + jvmRTClassPathIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, (jvmRTClassPathIndex - 1) + " entries added into RT classpath table.");
		}
	}
	
	private void addJvmRTBootClassPathEntry() {
		String bootClassPath = ManagementFactory.getRuntimeMXBean().getBootClassPath();
		String[] bootClassPathArry = bootClassPath.split(File.pathSeparator);
		for (int i = 0; i < bootClassPathArry.length; i++) {
			try {
				JvmRTBootClassPathEntryMBean bootClassPathEntry = new JvmRTBootClassPathEntryMBeanImpl(jvmRTBootClassPathIndex, bootClassPathArry[i]);
				jvmRTBootClassPathTable.addEntry(bootClassPathEntry);
				jvmRTBootClassPathIndex++;
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Failed to add entry for boot class path: "+bootClassPathArry[i]+" at index: " + jvmRTBootClassPathIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, (jvmRTBootClassPathIndex - 1) + " entries added into boot class path table.");
		}
	}
	
	private void addJvmRTInputArgsEntry() {
		List<String> inputArgList = ManagementFactory.getRuntimeMXBean().getInputArguments();
		
		for (String inputArgListStr : inputArgList) {
			try {
				JvmRTInputArgsEntryMBean inputArgsEntry = new JvmRTInputArgsEntryMBeanImpl(jvmRTInputArgsIndex, inputArgListStr);
				jvmRTInputArgsTable.addEntry(inputArgsEntry);
				jvmRTInputArgsIndex++;
			} catch (SnmpStatusException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Failed to add entry for VM argument string: "+inputArgListStr+" at index: " + jvmRTInputArgsIndex);
				}
				LogManager.getLogger().trace(e);
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, (jvmRTInputArgsIndex - 1) + " entries added into VM argument table.");
		}
	}
}