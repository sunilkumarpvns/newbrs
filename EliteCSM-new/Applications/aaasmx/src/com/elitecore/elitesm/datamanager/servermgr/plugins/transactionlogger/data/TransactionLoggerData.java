package com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data;

import java.io.Serializable;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.adapter.TimeBoundryAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlType(propOrder = {"pluginName","pluginDescription","pluginStatus","timeBoundry","logFile","range","pattern","globalization","formatMappingDataSet"})
@XmlRootElement(name = "transaction-logger")
public class TransactionLoggerData extends BaseData implements ITransactionLoggerData,Serializable,Differentiable{

	public TransactionLoggerData() {
		pluginDescription = RestUtitlity.getDefaultDescription();
	}
	
	private static final long serialVersionUID = 1L;
	private String pluginId;
	
	@Expose
	@SerializedName("Plugin Instance Id")
	private String pluginInstanceId;
	
	@Expose
	@SerializedName("Time Boundry")
	@NotNull(message = "Time Boundry for transaction logger must be specified")
	private Long timeBoundry;
	
	@Expose
	@SerializedName("Log File")
	private String logFile;
	
	@Expose
	@SerializedName("Range")
	private String range;
	
	@Expose
	@SerializedName("Pattern")
	@NotEmpty(message = "Sequence Position must be specified")
	@Pattern(regexp = "^$|prefix|suffix", message = "Invalid Sequence Position. It can be prefix or suffix")
	private String pattern;
	
	@Expose
	@SerializedName("Globalization")
	@NotEmpty(message = "Sequence Globalization must be specified")
	@Pattern(regexp = "^$|true|false", message = "Invalid Sequence Globalization. It can be true or false")
	private String globalization;
	
	@Valid
	@NotEmpty(message = "Atleast one mapping is required in Mappings")
	private Set<FormatMappingData> formatMappingDataSet;
	
	// use for REST Web Service only
	private String pluginName;
	private String pluginDescription;
	private String pluginStatus;
	
	@XmlTransient
	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@XmlTransient
	public String getPluginInstanceId() {
		return pluginInstanceId;
	}

	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
	}
	
	@XmlElement(name = "log-file")
	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	
	@XmlElement(name = "time-boundry")
	@XmlJavaTypeAdapter(value = TimeBoundryAdapter.class)
	@Min(value = 0, message = "Invalid Time Boundry parameter. It can be 1 Min,2 Min,3 Min,5 Min,10 Min,15 Min,20 Min,30 Min,Hourly,Daily")
	public Long getTimeBoundry() {
		return timeBoundry;
	}

	public void setTimeBoundry(Long timeBoundry) {
		this.timeBoundry = timeBoundry;
	}

	@XmlElement(name = "sequence-range")
	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	@XmlElement(name = "sequence-position")
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@XmlElement(name = "sequence-globalization")
	public String getGlobalization() {
		return globalization;
	}

	public void setGlobalization(String globalization) {
		this.globalization = globalization;
	}
	
	@XmlElement(name = "mappings")
	public Set<FormatMappingData> getFormatMappingDataSet() {
		return formatMappingDataSet;
	}

	public void setFormatMappingDataSet(Set<FormatMappingData> formatMappingDataSet) {
		this.formatMappingDataSet = formatMappingDataSet;
	}
	
	@XmlElement(name = "name")
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@XmlElement(name = "description")
	public String getPluginDescription() {
		return pluginDescription;
	}

	public void setPluginDescription(String pluginDescription) {
		this.pluginDescription = pluginDescription;
	}

	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getPluginStatus() {
		return pluginStatus;
	}

	public void setPluginStatus(String pluginStatus) {
		this.pluginStatus = pluginStatus;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		if( timeBoundry != null)
			jsonObject.put("Time Boundry", timeBoundry);
		
		if( logFile != null && logFile.length() > 0 )
			jsonObject.put("Log File", logFile);
		if(range != null && range.length() > 0)
			jsonObject.put("Sequence Range",  StringUtils.capitalize(range));
		
			jsonObject.put("Sequnce Position", StringUtils.capitalize(pattern));
			jsonObject.put("Sequence Globalization", StringUtils.capitalize(globalization));
		if ( formatMappingDataSet!=null && formatMappingDataSet.size() > 0 ){
			JSONObject jsonObjectData = new JSONObject();
			for( FormatMappingData formatMappingData : formatMappingDataSet ){
				jsonObjectData.putAll(formatMappingData.toJson());
			}
			jsonObject.put("Mappings", jsonObjectData);
		}
		return jsonObject;
	}
}