package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.ConnectionStatistics;
import com.elitecore.diameterapi.mibs.custom.autogen.TablePeerIpAddrTable;
import com.sun.management.snmp.SnmpStatusException;

public class ConnectionStatisticsMBeanImpl extends ConnectionStatistics{

	private TablePeerIpAddrTable peerIpAddrTable;

	@Override
	@Table(name = "peerIpAddrTable")
	public TablePeerIpAddrTable accessPeerIpAddrTable()
			throws SnmpStatusException {
		return peerIpAddrTable;
	}

	public void setPeerIPAddressTable(TablePeerIpAddrTable peerIpAddrTable) {
		this.peerIpAddrTable = peerIpAddrTable;
	}
	
	public String getObjectName(String name, String oid,String defaultName){
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_CONN_STATISTICS + name;
	}
}
