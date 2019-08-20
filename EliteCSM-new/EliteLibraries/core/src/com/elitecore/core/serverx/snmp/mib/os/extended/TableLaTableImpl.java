package com.elitecore.core.serverx.snmp.mib.os.extended;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.core.serverx.snmp.mib.os.autogen.LaEntryMBean;
import com.elitecore.core.serverx.snmp.mib.os.autogen.TableLaTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableLaTableImpl extends TableLaTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8432567172769024024L;

	public TableLaTableImpl(SnmpMib myMib, MBeanServer server) {
		super(myMib, server);
	}
	
	@Override
	public synchronized void addEntry(LaEntryMBean entry, ObjectName name) throws SnmpStatusException {
		super.addEntry(entry, name);
	}

}
