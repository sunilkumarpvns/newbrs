package com.elitecore.core.imdg.impl;

import com.elitecore.core.imdg.autogen.MapDetailEntry;
import com.elitecore.core.imdg.autogen.TableMapStatisticsTable;
import com.hazelcast.core.IMap;
import com.sun.management.snmp.SnmpStatusException;

public class TableMapStatisticsTableImpl extends TableMapStatisticsTable {

	
	public TableMapStatisticsTableImpl(IMDG_MIBImpl imdgMIBImpl) {
		super(imdgMIBImpl);
	}

	public void addMapStatisticsToTableEntry(IMap<Object, Object> map, int index) throws SnmpStatusException {
		MapDetailEntry entry = new MapDetailEntryMBeanImpl(map, index);
		addEntry(entry);
	}

}
