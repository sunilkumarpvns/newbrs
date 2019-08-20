package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.AppStatistics;
import com.elitecore.diameterapi.mibs.custom.autogen.TableAppStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;

public class AppStatsMBeanImpl extends AppStatistics {

	private TableAppStatisticsTable applicationStatisticsTable;

	@Override
	@Table(name = "appStatisticsTable")
	public TableAppStatisticsTable accessAppStatisticsTable() throws SnmpStatusException { 
		return this.applicationStatisticsTable; 
	}

	public void setApplicationStatisticsTable(TableAppStatisticsTable applicationStatisticsTable) {
		this.applicationStatisticsTable = applicationStatisticsTable;
	}
	
	
	
	public String getObjectName(String name, String oid,String defaultName){
		return SnmpAgentMBeanConstant.DIAMETER_STACK_APP_STATISTICS + name;
	}
}
