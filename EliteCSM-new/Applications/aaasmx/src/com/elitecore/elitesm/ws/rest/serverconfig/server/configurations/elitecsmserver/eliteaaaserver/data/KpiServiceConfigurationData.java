package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"dsName","batchSize","batchInterval","kpiQueryInterval"})
@ValidObject
public class KpiServiceConfigurationData implements Validator{
	
	private String batchInterval;
	private String kpiQueryInterval;
	private String batchSize;
	
	@NotEmpty(message="Datasource Name must be specified in KPI service configuration")
	private String dsName;
	
	public KpiServiceConfigurationData() {
		batchInterval = "60";
		kpiQueryInterval = "10";
		batchSize = "200";
	}
	
	@XmlElement(name = "batch-interval")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Batch interval must be numeric  in KPI service configuration")
	public String getBatchInterval() {
		return this.batchInterval;
	}

	public void setBatchInterval(String batchInterval) {
		this.batchInterval = batchInterval;
	}
	
	@XmlElement(name = "kpi-query-interval")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="KPI Query Interval must be numeric  in KPI service configuration")
	public String getKpiQueryInterval() {
		return this.kpiQueryInterval;
	}
	public void setKpiQueryInterval(String kpiQueryInterval) {
		this.kpiQueryInterval = kpiQueryInterval;
	}

	@XmlElement(name = "batch-size")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Batch Size must be numeric  in KPI service configuration")
	public String getBatchSize() {
		return this.batchSize;
	}
	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}

	@XmlElement(name = "datasource-name")
	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(Strings.isNullOrBlank(dsName) == false && RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(dsName) == false){
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			try{
				IDatabaseDSData databaseDsData = databaseDSBLManager.getDatabaseDSDataByName(dsName);
			}catch(Exception e){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Configured "+dsName+" Database DataSource does not exists");
			}
		}
		return isValid;
	}
}
