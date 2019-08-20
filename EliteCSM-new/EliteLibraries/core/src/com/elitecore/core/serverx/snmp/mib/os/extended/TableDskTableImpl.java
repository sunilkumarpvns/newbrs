package com.elitecore.core.serverx.snmp.mib.os.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.core.serverx.snmp.mib.os.autogen.DskEntryMBean;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableDskTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableDskTableImpl extends TableDskTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TableDskTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(DskEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
	}

}
