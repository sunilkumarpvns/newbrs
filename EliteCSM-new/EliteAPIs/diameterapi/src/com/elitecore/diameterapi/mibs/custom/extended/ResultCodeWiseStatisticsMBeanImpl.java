package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.ResultCodeWiseStatistics;
import com.elitecore.diameterapi.mibs.custom.autogen.TableResultCodeStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;

public class ResultCodeWiseStatisticsMBeanImpl extends ResultCodeWiseStatistics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TableResultCodeStatisticsTable appToPeerRCWStatisticsTable;

	@Override
	@Table(name = "appPeerResultCodeStats")
	public TableResultCodeStatisticsTable accessResultCodeStatisticsTable()
			throws SnmpStatusException {
		return appToPeerRCWStatisticsTable;
	}

	public void setAppToPeerRCWStatisticsTable(
			TableResultCodeStatisticsTable appToPeerRCWStatisticsTable) {
		this.appToPeerRCWStatisticsTable = appToPeerRCWStatisticsTable;
	}
	
	public String getObjectName(String name, String oid,String defaultName){
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_APP_RESULT_CODE_WISE_STATISTICS + name;
	}

}
