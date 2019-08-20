package com.elitecore.core.imdg.impl;

import com.elitecore.core.imdg.autogen.MapDetailEntry;
import com.hazelcast.core.IMap;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class MapDetailEntryMBeanImpl extends MapDetailEntry{

	private static final String MODULE = "MAP-DETAIL-ENTRY-MBEAN";
	private static SnmpMib myMib;
	private IMap<Object, Object> map;
	private int index ;
	
	public MapDetailEntryMBeanImpl(IMap<Object, Object> map, int index) {
		super(myMib);
		this.map = map;
		this.index = index;
	}
	
	@Override
	public Long getTotalGetOperations() throws SnmpStatusException {
		return this.map.getLocalMapStats().getGetOperationCount();
	}

	@Override
	public Long getTotalPutOperations() throws SnmpStatusException {
		return this.map.getLocalMapStats().getPutOperationCount();
	}

	@Override
	public Long getMemoryUsedForBackUp() throws SnmpStatusException {
		return this.map.getLocalMapStats().getBackupEntryMemoryCost();
	}

	@Override
	public Long getMemoryUsedbyMap() throws SnmpStatusException {
		return this.map.getLocalMapStats().getOwnedEntryMemoryCost();
	}

	@Override
	public Long getAvgRemoveLatency() throws SnmpStatusException {
		long avgRemoveLatency = 0;
		long removeOperationCount = this.map.getLocalMapStats().getRemoveOperationCount();
		if(removeOperationCount > 0) {
			avgRemoveLatency = this.map.getLocalMapStats().getTotalRemoveLatency()/removeOperationCount;
		}
		return avgRemoveLatency;
	}

	@Override
	public Long getBackupSessions() throws SnmpStatusException {
		return this.map.getLocalMapStats().getBackupEntryCount();
	}

	@Override
	public Long getAvgGetLatency() throws SnmpStatusException {
		long avgGetLatency = 0;
		long getOperationCount = this.map.getLocalMapStats().getGetOperationCount();
		if(getOperationCount > 0) {
			avgGetLatency = this.map.getLocalMapStats().getTotalGetLatency()/getOperationCount;
		}
		return avgGetLatency;
	}

	@Override
	public Long getActiveSessions() throws SnmpStatusException {
		return this.map.getLocalMapStats().getOwnedEntryCount();
	}

	@Override
	public Long getAvgPutLatency() throws SnmpStatusException {
		long avgPutLatency = 0;
		long putOperationCount = this.map.getLocalMapStats().getPutOperationCount();
		if(putOperationCount > 0) {
			avgPutLatency = this.map.getLocalMapStats().getTotalPutLatency()/putOperationCount;
		} 
		return avgPutLatency;
	}

	@Override
	public String getMapName() throws SnmpStatusException {
		return this.map.getName();
	}

	@Override
	public Long getTotalRemoveOperations() throws SnmpStatusException {
		return this.map.getLocalMapStats().getRemoveOperationCount();
	}

	@Override
	public Long getMapIndex() throws SnmpStatusException {
		return Long.valueOf(this.index);
	}
	
	@Override
	public Long getNoOfLockedSessions(){
		return this.map.getLocalMapStats().getLockedEntryCount();
		
	}
}
