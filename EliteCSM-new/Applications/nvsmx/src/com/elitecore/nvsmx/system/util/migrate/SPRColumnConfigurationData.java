package com.elitecore.nvsmx.system.util.migrate;

import com.elitecore.commons.base.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="spr-column-configuration")
public class SPRColumnConfigurationData {

	private ColumnNameMappingData columnNameMapping;
	private List<String> ignoreColumnNames;
	private List<ColumnNameData> csvHeaders;
	private List<ColumnNameData> subscriptionCsvHeader;
	private String dateFormat;


	public SPRColumnConfigurationData() {
		this.ignoreColumnNames = new ArrayList<String>();
	}

	@XmlElementWrapper(name="ignore-columns")
    @XmlElement(name="column-name")
	public List<String> getIgnoreColumnNames() {
		return ignoreColumnNames;
	}

	public void setIgnoreColumnNames(List<String> ignoreColumnNames) {
		this.ignoreColumnNames = ignoreColumnNames;
	}

	@XmlElement(name="column-name-mapping")
	public ColumnNameMappingData getColumnNameMapping() {
		return columnNameMapping;
	}

	public void setColumnNameMapping(ColumnNameMappingData columnNameMapping) {
		this.columnNameMapping = columnNameMapping;
	}

	@XmlElementWrapper(name="csv-headers")
	@XmlElement(name="column-name")
	public List<ColumnNameData> getCSVHeaders() {
		return csvHeaders;
	}

	public void setCSVHeaders(List<ColumnNameData> csvHeaders) {
		this.csvHeaders = csvHeaders;
	}

	@XmlElementWrapper(name="subscription-csv-headers")
	@XmlElement(name="column-name")
	public List<ColumnNameData> getSubscriptionCsvHeader() {
		return subscriptionCsvHeader;
	}

	public void setSubscriptionCsvHeader(List<ColumnNameData> subscriptionCsvHeader) {
		this.subscriptionCsvHeader = subscriptionCsvHeader;
	}

	@XmlElement(name="date-format")
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		if(Strings.isNullOrBlank(dateFormat) == false) {
			this.dateFormat = dateFormat;
		}
	}

}
