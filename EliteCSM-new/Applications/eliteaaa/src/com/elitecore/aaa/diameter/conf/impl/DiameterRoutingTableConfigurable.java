package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;

@XmlType(propOrder = {})
@XmlRootElement(name = "dia-routing-tables")
@ConfigurationProperties(moduleName = "DIAMETER-ROUTING-TBL-CONFIGURABLE", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "routing-table",schemaDirectories = {"system","schema"},configDirectories = {"conf","diameter"})
public class DiameterRoutingTableConfigurable extends RoutingTableConfigurable {
	
	private static String MODULE="DIAMETER-ROUTING-TBL-CONFIGURABLE";

	public DiameterRoutingTableConfigurable() {
	
	}
	
	protected String getQueryForRoutingTableIds() {
		DiameterStackConfigurable diameterStackConfigurable = getConfigurationContext().get(DiameterStackConfigurable.class);
        String routingTable = diameterStackConfigurable.getRoutingTableName();
        return "select * from TBLMROUTINGTABLE WHERE ROUTINGTABLENAME='" + routingTable +"'"; 
    } 

	@Override
	protected String getModule() {
		return MODULE;
	}

}

