package com.elitecore.commons.kpi.handler;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.commons.kpi.exception.RegistrationFailedException;
import com.elitecore.commons.kpi.exception.StartupFailedException;
import com.sun.management.snmp.agent.SnmpMib;

public interface MIBSerializer {
	public void init() throws InitializationFailedException;
	public void registerMib(SnmpMib snmpMib) throws RegistrationFailedException;
	public void start() throws StartupFailedException;
	public void stop();
	public void restart() throws InitializationFailedException, StartupFailedException;
	public boolean isKPIServiceRunning();
	public boolean isInitialized();
	
	/**
	 * It flushes all the records existing in all the table that are created using
	 * annotation {@link Table} in MIB implementation. If connection is not available
	 * then it will return the error message. Otherwise it will return the table name
	 * and its status in tabular form, status specifies whether that particular table
	 * is flushed successfully or not.
	 * @return the error message or the table name and its status in tabular form
	 */
	public String flush();
}
