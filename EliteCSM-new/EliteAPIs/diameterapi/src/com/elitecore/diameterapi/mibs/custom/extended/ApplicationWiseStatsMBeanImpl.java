package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.ApplicationWiseStatistics;
import com.elitecore.diameterapi.mibs.custom.autogen.TableAppWiseStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;

public class ApplicationWiseStatsMBeanImpl extends ApplicationWiseStatistics {

	private TableAppWiseStatisticsTable applicationToPeerStatisticsTable;

	@Override
	@Table(name = "appWiseStatisticsTable")
	public TableAppWiseStatisticsTable accessAppWiseStatisticsTable()
			throws SnmpStatusException {
		return applicationToPeerStatisticsTable;
	}

	public void setApplicationWiseToPeerStatisticsTable(
			TableAppWiseStatisticsTable applicationToPeerStatisticsTable) {
		this.applicationToPeerStatisticsTable = applicationToPeerStatisticsTable;
	}
	
	public String getObjectName(String name, String oid,String defaultName){
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_APP_WISE_STATISTICS + name;
	}
}
