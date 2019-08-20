package com.elitecore.core.serverx.snmp.mib.jvm.extended;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemPoolCollectThreshdSupport;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemPoolState;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemPoolThreshdSupport;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.EnumJvmMemPoolType;
import com.elitecore.core.serverx.snmp.mib.jvm.autogen.JvmMemPoolEntryMBean;
import com.sun.management.snmp.SnmpDefinitions;
import com.sun.management.snmp.SnmpStatusException;

public class JvmMemPoolEntryMBeanImpl implements JvmMemPoolEntryMBean {

	private final static String MODULE = "JVM-MEMORY-POOL-ENTRY";
	private int jvmMemPoolIndex;
	private MemoryPoolMXBean memoryPoolMXBean;
	private long jvmMemPoolPeakReset = 0L;
	private final Long DEFAULT_VALUE = new Long(0);
	
	public JvmMemPoolEntryMBeanImpl(int jvmMemPoolIndex, MemoryPoolMXBean memoryPoolMXBean) {
		this.jvmMemPoolIndex = jvmMemPoolIndex; 
		this.memoryPoolMXBean = memoryPoolMXBean;
	}
	
	@Override
	@Column(name = "jvmMemPoolCollectMaxSize", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolCollectMaxSize() {
		MemoryUsage jvmMemUsage = memoryPoolMXBean.getCollectionUsage();
		if(jvmMemUsage != null){
			long collectMax = jvmMemUsage.getMax()/1024;
			if(collectMax > -1){
				return new Long(collectMax);
			}
		}
		return DEFAULT_VALUE;
	}

	@Override
	@Column(name = "jvmMemPoolCollectCommitted", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolCollectCommitted() {
		MemoryUsage jvmMemUsage = memoryPoolMXBean.getCollectionUsage();
		if(jvmMemUsage != null){
			long commitedMem = jvmMemUsage.getCommitted()/1024; 
			if(commitedMem > -1){
				return new Long(commitedMem);
			}
		}
		return DEFAULT_VALUE;
	}

	@Override
	@Column(name = "jvmMemPoolCollectUsed", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolCollectUsed() {
		MemoryUsage jvmMemUsage = memoryPoolMXBean.getCollectionUsage();
		if(jvmMemUsage != null){
			long collectUsed = jvmMemUsage.getUsed()/1024;
			if(collectUsed > -1){
				return new Long(collectUsed);
			}
		}
		return DEFAULT_VALUE;
	}

	@Override
	@Column(name = "MemPoolCollectThreshdSupport", type = java.sql.Types.VARCHAR)
	public EnumJvmMemPoolCollectThreshdSupport getJvmMemPoolCollectThreshdSupport(){
		if (memoryPoolMXBean.isCollectionUsageThresholdSupported()){
			return new EnumJvmMemPoolCollectThreshdSupport("supported");
		}else{
			return new EnumJvmMemPoolCollectThreshdSupport("unsupported");
		}
	}

	@Override
	@Column(name = "jvmMemPoolCollectThreshdCount", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolCollectThreshdCount() {
		if(memoryPoolMXBean.isCollectionUsageThresholdSupported()){
			return memoryPoolMXBean.getCollectionUsageThresholdCount();
		}
		return DEFAULT_VALUE;
	}

	@Override
	@Column(name = "JvmMemPoolCollectThreshold", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolCollectThreshold() {
		if(memoryPoolMXBean.isCollectionUsageThresholdSupported()){
			long collectionUsageThreshold = memoryPoolMXBean.getCollectionUsageThreshold();
			if(collectionUsageThreshold > -1){
				return new Long(collectionUsageThreshold);
			}
		}
		return DEFAULT_VALUE;
	}

	@Override
	public void setJvmMemPoolCollectThreshold(Long x) throws SnmpStatusException{
		long poolCollectThresold = x.longValue();
		if(poolCollectThresold < 0){
			throw new SnmpStatusException(SnmpDefinitions.snmpRspWrongValue);
		}
		memoryPoolMXBean.setCollectionUsageThreshold(poolCollectThresold);
	}

	@Override
	public void checkJvmMemPoolCollectThreshold(Long x){
	}

	@Override
	@Column(name = "jvmMemPoolMaxSize", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolMaxSize() {
		return memoryPoolMXBean.getUsage().getMax()/1024;
	}

	@Override
	@Column(name = "jvmMemPoolCommitted", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolCommitted() {
		return memoryPoolMXBean.getUsage().getCommitted()/1024;
	}

	@Override
	@Column(name = "jvmMemPoolUsed", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolUsed() {
		return memoryPoolMXBean.getUsage().getUsed()/1024;
	}

	@Override
	@Column(name = "jvmMemPoolInitSize", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolInitSize() {
		return memoryPoolMXBean.getUsage().getInit()/1024;
	}

	@Override
	@Column(name = "jvmMemPoolThreshdSupport", type = java.sql.Types.VARCHAR)
	public EnumJvmMemPoolThreshdSupport getJvmMemPoolThreshdSupport(){
		if (memoryPoolMXBean.isCollectionUsageThresholdSupported()){
			return new EnumJvmMemPoolThreshdSupport("supported");
		}else{
			return new EnumJvmMemPoolThreshdSupport("unsupported");
		}
	}

	@Override
	@Column(name = "jvmMemPoolThreshdCount", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolThreshdCount() {
		 if (memoryPoolMXBean.isUsageThresholdSupported()){
			 final long usageThresholdCount = memoryPoolMXBean.getUsageThresholdCount();
			 if(usageThresholdCount > -1){
				 return new Long(usageThresholdCount);
			 }
		 }
		 return DEFAULT_VALUE;
	}

	@Override
	@Column(name = "JvmMemPoolThreshold", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolThreshold() {
		 if (memoryPoolMXBean.isUsageThresholdSupported()){
			 final long usageThreshold = memoryPoolMXBean.getUsageThreshold();
			 if(usageThreshold > -1){
				 return new Long(usageThreshold);
			 }
		 }
		 return DEFAULT_VALUE;
	}

	@Override
	public void setJvmMemPoolThreshold(Long x) throws SnmpStatusException {
		long poolThresold = x.longValue();
		if(poolThresold < 0){
			throw new SnmpStatusException(SnmpDefinitions.snmpRspWrongValue);
		}
		memoryPoolMXBean.setUsageThreshold(poolThresold);
	}

	@Override
	public void checkJvmMemPoolThreshold(Long x) {
	}

	@Override
	@Column(name = "JvmMemPoolPeakReset", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolPeakReset() {
		return jvmMemPoolPeakReset;
	}

	@Override
	public void setJvmMemPoolPeakReset(Long x) {
		long poolPeakResetValue = x.longValue();
		if(poolPeakResetValue > 0){
			final long stamp = System.currentTimeMillis();
			memoryPoolMXBean.resetPeakUsage();
			jvmMemPoolPeakReset = stamp;
		}
	}

	@Override
	public void checkJvmMemPoolPeakReset(Long x) {
		
	}

	@Override
	@Column(name = "jvmMemPoolState", type = java.sql.Types.VARCHAR)
	public EnumJvmMemPoolState getJvmMemPoolState() {
		if(memoryPoolMXBean.isValid()){
			return new EnumJvmMemPoolState("valid");
		}else{
			return new EnumJvmMemPoolState("invalid");
		}
	}

	@Override
	@Column(name = "jvmMemPoolType", type = java.sql.Types.VARCHAR)
	public EnumJvmMemPoolType getJvmMemPoolType() throws SnmpStatusException {
		MemoryType memoryType = memoryPoolMXBean.getType();
		if (memoryType.equals(MemoryType.HEAP)){
			return  new EnumJvmMemPoolType("heap");
		}else if (memoryType.equals(MemoryType.NON_HEAP)){
			return new EnumJvmMemPoolType("nonheap");
		}
		throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
	}

	@Override
	public Byte[] getJvmMemPoolName() {
		if(memoryPoolMXBean.getName() != null)
			return ArrayUtils.toObject(memoryPoolMXBean.getName().getBytes());
		return new Byte[0];
	}

	@Column(name = "jvmMemPoolName", type = java.sql.Types.VARCHAR)
	public String getJvmMemPoolNameExt() {
		String poolName = memoryPoolMXBean.getName();
		if(poolName != null){
			return poolName;
		}
		return "";
	}
	
	
	@Override
	@Column(name = "jvmMemPoolPeakMaxSize", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolPeakMaxSize() {
		return memoryPoolMXBean.getPeakUsage().getMax()/1024;
	}

	@Override
	@Column(name = "jvmMemPoolIndex", type = java.sql.Types.INTEGER)
	public Integer getJvmMemPoolIndex() {
		return new Integer(jvmMemPoolIndex);
	}

	@Override
	@Column(name = "jvmMemPoolPeakCommitted", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolPeakCommitted() {
		return memoryPoolMXBean.getPeakUsage().getCommitted()/1024;
	}

	@Override
	@Column(name = "jvmMemPoolPeakUsed", type = java.sql.Types.BIGINT)
	public Long getJvmMemPoolPeakUsed() {
		return memoryPoolMXBean.getPeakUsage().getUsed()/1024;
	}
	
	@Column(name = "jvmSystemLoadAverage",type = java.sql.Types.DOUBLE)
	public double getSystemLoadAverage(){
		double systemLoadAverage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
		return systemLoadAverage;
	}
}
