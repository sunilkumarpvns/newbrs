package com.elitecore.elitesm.ws.rest.serverconfig.server.services.dynauth;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlRootElement(name="scan-from-database")
@XmlType(propOrder={"enabled", "dataSourceName", "maxRecordPerScan", "scanningPeriod", "delatBetweenSubsequentRequest"})
@ValidObject
public class DynAuthDBScanDetail implements Validator {

	@Pattern(regexp = "true|false|True|False|TRUE|FALSE", message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "'enabled' of scan from database." )
	private String enabled;
	private String dataSourceName;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Max Records Per Scan.")
	private String maxRecordPerScan = "1000";
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Scanning Period.")
	private String scanningPeriod = "300000"; // 1000 * 60 * 5
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Delay Between Subsequent Requests.")
	private String delatBetweenSubsequentRequest = "10";

	public DynAuthDBScanDetail() {
	}

	@XmlElement(name="enabled",type=String.class) 
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled.toLowerCase();
	}

	@XmlElement(name="datasource-name",type=String.class)
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@XmlElement(name="max-records-per-scan",type=String.class)
	public String getMaxRecordPerScan() {
		return maxRecordPerScan;
	}

	public void setMaxRecordPerScan(String maxRecordPerScan) {
		this.maxRecordPerScan = maxRecordPerScan;
	}

	@XmlElement(name="scanning-period",type=String.class)
	public String getScanningPeriod() {
		return scanningPeriod;
	}

	public void setScanningPeriod(String scanningPeriod) {
		this.scanningPeriod = scanningPeriod;
	}

	@XmlElement(name="delay-between-subsequent-requests",type=String.class)	
	public String getDelatBetweenSubsequentRequest() {
		return delatBetweenSubsequentRequest;
	}

	public void setDelatBetweenSubsequentRequest(String delatBetweenSubsequentRequest) {
		this.delatBetweenSubsequentRequest = delatBetweenSubsequentRequest;
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("DB Scan Detail");
		out.println("    Enabled                           = "+enabled);
		out.println("    DatasourceName                    = "+getDataSourceName());
		out.println("    Max Records Per Scan              = "+getMaxRecordPerScan());
		out.println("    Scanning Period      			   = "+getScanningPeriod());
		out.println("    Delay Between Subsequent Requests = "+getDelatBetweenSubsequentRequest());
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		DatabaseDSBLManager dsblManager =  new DatabaseDSBLManager();
		try {
			dsblManager.getDatabaseDSDataByName(dataSourceName);
		} catch (DataManagerException e) {
			RestUtitlity.setValidationMessage(context, "Configured Database Datasource not found");
			isValid = false;
		}
		return isValid;
	}

}
