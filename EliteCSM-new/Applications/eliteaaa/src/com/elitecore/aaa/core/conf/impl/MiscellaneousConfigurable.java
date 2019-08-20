package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.data.ParamsDetail;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "miscellaneous-data")
@ConfigurationProperties(moduleName ="MISCELLANEOUS_CONFIGURABLE",synchronizeKey ="MISCELLANEOUS_DATA", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"system","misc"},name = "misc-config")
public class MiscellaneousConfigurable extends Configurable {
	
	private static final String MODULE = "MISCELLANEOUS_CONFIGURABLE";
	public static final String BATCH_SYSTEM_PROPERTY = "sql.transaction.batch";
	public static final String NOWAIT_SYSTEM_PROPERTY = "sql.transaction.nowait";
	public static final String OS_SNMP_ADDRESS = "os.snmp.address";

	private List<ParamsDetail> paramsDetails;
	
	public MiscellaneousConfigurable(){
		paramsDetails = new ArrayList<ParamsDetail>();	
	}
	
	@Reloadable(type = ParamsDetail.class)
	@XmlElement(name = "param")
	public List<ParamsDetail> getParamsList() {
		return paramsDetails;
	}

	public void setParamsList(List<ParamsDetail> paramsList) {
		this.paramsDetails = paramsList;
	}

	@PostRead
	public void postReadProcessing() {
		setMiscellaneousConfigurationAsSystemProperties();
	}
	
	private void setMiscellaneousConfigurationAsSystemProperties() {
		for (ParamsDetail paramsDetail : paramsDetails) {
		
			if (System.getProperty(paramsDetail.getName()) != null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Overwriting old value: " + System.getProperty(paramsDetail.getName()) + " with new value: " + paramsDetail.getValue() + " for Miscellaneous configuration: " + paramsDetail.getName());
				}
			}
			System.setProperty(paramsDetail.getName(), paramsDetail.getValue());
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){

	}
	
	@PostReload
	public void postReloadProcessing(){
		setMiscellaneousConfigurationAsSystemProperties();
	}
}