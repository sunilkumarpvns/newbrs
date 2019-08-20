package com.elitecore.core.serverx.alert.listeners;

import com.elitecore.core.serverx.alert.SnmpRequestType;
import com.elitecore.core.serverx.alert.TrapVersion;
import com.sun.management.snmp.manager.SnmpPeer;

public interface SnmpAlertProcessor {

	String getName();
	SnmpPeer getSnmpPeer();
	TrapVersion getVersion();
	boolean isAdvanceTrap();
	SnmpRequestType getSnmpRequestType();
}
