package com.elitecore.aaa.mibs.rm.charging.client.snmp;

import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.ChargingServerTable;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.TableChargingServerStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;

public class ChargingServerTableMBeanImpl extends ChargingServerTable{

	@Override
	public TableChargingServerStatisticsTable accessChargingServerStatisticsTable()
			throws SnmpStatusException {
		return null;
	}
}
