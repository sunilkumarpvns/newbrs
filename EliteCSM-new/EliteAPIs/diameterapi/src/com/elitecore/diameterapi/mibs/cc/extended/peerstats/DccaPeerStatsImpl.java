package com.elitecore.diameterapi.mibs.cc.extended.peerstats;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerStats;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPerPeerStatsTable;

public class DccaPeerStatsImpl extends DccaPeerStats{

	private TableDccaPerPeerStatsTable tableDccaPerPeerStatsTable;
	
	public void setTableDccaPerPeerStatsTable(TableDccaPerPeerStatsTable tableDccaPerPeerStatsTable) {
		this.tableDccaPerPeerStatsTable = tableDccaPerPeerStatsTable;
	}
	
	@Override
	@Table(name = "dccaPerPeerStatsTable")
	public TableDccaPerPeerStatsTable accessDccaPerPeerStatsTable(){
		return this.tableDccaPerPeerStatsTable;
	}
}
