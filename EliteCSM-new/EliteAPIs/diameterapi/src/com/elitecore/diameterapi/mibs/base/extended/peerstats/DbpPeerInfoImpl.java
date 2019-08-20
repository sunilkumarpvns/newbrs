package com.elitecore.diameterapi.mibs.base.extended.peerstats;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerInfo;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPerPeerInfoTable;
import com.sun.management.snmp.SnmpStatusException;

public class DbpPeerInfoImpl extends DbpPeerInfo{

	private TableDbpPerPeerInfoTable tableDbpPerPeerInfoTable;
	
	public void setTableDbpPerPeerInfoTable(TableDbpPerPeerInfoTable tableDbpPerPeerInfoTable) {
		this.tableDbpPerPeerInfoTable = tableDbpPerPeerInfoTable;
	}
	
	@Override
	@Table(name = "dbpPerPeerInfoTable")
	public TableDbpPerPeerInfoTable accessDbpPerPeerInfoTable() throws SnmpStatusException {
		return this.tableDbpPerPeerInfoTable;
	}

}
