package com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlType(propOrder = { "pluginName","pluginDescription","pluginStatus","groovyFiles"})
@XmlRootElement(name = "groovy-plugin")
public class GroovyPluginData extends BaseData implements IGroovyPluginData,Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String pluginId;
	
	@Expose
	@SerializedName("Plugin Instance Id")
	private String pluginInstanceId;
	
	@Valid
	private Set<GroovyPluginFile> groovyPluginFileSet;
	
	private List<String> groovyFiles ;
	
	@NotEmpty(message="Groovy Plugin name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String pluginName;
	
	private String pluginDescription;
	
	@NotEmpty(message = "Plugin Status must be specified")
    @Pattern(regexp = "CST01|CST02", message = "Invalid value of Status parameter. Value could be 'ACTIVE' or 'INACTIVE'.")
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
	
	@XmlTransient
	public Set<GroovyPluginFile> getGroovyPluginFileSet() {
		return groovyPluginFileSet;
	}

	public void setGroovyPluginFileSet(Set<GroovyPluginFile> groovyPluginFileSet) {
		this.groovyPluginFileSet = groovyPluginFileSet;
	}
	
	@XmlElementWrapper(name = "groovy-files")
	@XmlElement(name = "groovy-filename")
	public List<String> getGroovyFiles() {
		return groovyFiles;
	}

	public void setGroovyFiles(List<String> groovyFiles) {
		this.groovyFiles = groovyFiles;
	}

	@XmlElement(name = "plugin-name")
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@XmlElement(name = "plugin-description")
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
		
		if ( groovyPluginFileSet!=null && groovyPluginFileSet.size() > 0 ){
			JSONObject jsonObjectData = new JSONObject();
			for( GroovyPluginFile groovyPluginFile : groovyPluginFileSet ){
				jsonObjectData.putAll(groovyPluginFile.toJson());
			}
			jsonObject.put("Groovy Plugin", jsonObjectData);
		}
		return jsonObject;
	}
}