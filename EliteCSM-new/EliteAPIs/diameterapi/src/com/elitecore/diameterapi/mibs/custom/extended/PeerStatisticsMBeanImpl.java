package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.PeerStatistics;
import com.elitecore.diameterapi.mibs.custom.autogen.TablePeerInfoTable;
import com.sun.management.snmp.SnmpStatusException;

public class PeerStatisticsMBeanImpl extends PeerStatistics {

	private TablePeerInfoTable peerInfoTable;

	@Override
	@Table(name = "peerInfoTable")
	public TablePeerInfoTable accessPeerInfoTable() throws SnmpStatusException {
		return peerInfoTable;
	}

	public void setPeerInfoTable(TablePeerInfoTable peerInfoTable) {
		this.peerInfoTable = peerInfoTable;
	}
	
	public String getObjectName(String name, String oid,String defaultName){
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_STATISTICS + name;
	}
}
