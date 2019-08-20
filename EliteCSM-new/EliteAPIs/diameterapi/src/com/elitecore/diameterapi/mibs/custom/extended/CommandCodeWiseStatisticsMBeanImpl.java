package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.CommandCodeWiseStatistics;
import com.elitecore.diameterapi.mibs.custom.autogen.TableCommandCodeStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;

public class CommandCodeWiseStatisticsMBeanImpl extends CommandCodeWiseStatistics {

	private TableCommandCodeStatisticsTable appToPeerCCWStatisticsTable;

	@Override
	@Table(name = "commandCodeStatisticsTable")
	public TableCommandCodeStatisticsTable accessCommandCodeStatisticsTable()
			throws SnmpStatusException {
		return appToPeerCCWStatisticsTable;
	}

	public void setAppToPeerCCWStatisticsTable(
			TableCommandCodeStatisticsTable appToPeerCCWStatisticsTable) {
		this.appToPeerCCWStatisticsTable = appToPeerCCWStatisticsTable;
	}
	
	public String getObjectName(String name, String oid,String defaultName){
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_APP_CC_WISE_STATISTICS + name;
	}
}
