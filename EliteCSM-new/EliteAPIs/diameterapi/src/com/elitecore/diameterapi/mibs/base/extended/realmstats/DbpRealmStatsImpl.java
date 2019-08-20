package com.elitecore.diameterapi.mibs.base.extended.realmstats;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmStats;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpRealmMessageRouteTable;
import com.sun.management.snmp.SnmpStatusException;

public class DbpRealmStatsImpl extends DbpRealmStats {

	private TableDbpRealmMessageRouteTable tableDbpRealmMessageRouteTable;
	
	public void setTableDbpRealmMessageRouteTable(TableDbpRealmMessageRouteTable tableDbpRealmMessageRouteTable) {
		this.tableDbpRealmMessageRouteTable = tableDbpRealmMessageRouteTable;
	}
	
	@Override
	@Table(name = "dbpRealmMessageRouteTables")
	public TableDbpRealmMessageRouteTable accessDbpRealmMessageRouteTable() throws SnmpStatusException {
		return this.tableDbpRealmMessageRouteTable;
	}

}
