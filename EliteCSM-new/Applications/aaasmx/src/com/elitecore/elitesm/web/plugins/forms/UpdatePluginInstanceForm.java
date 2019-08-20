package com.elitecore.elitesm.web.plugins.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author nayana.rathod
 *
 */
public class UpdatePluginInstanceForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String pluginInstanceId;
	private String name;
	private String description;
	
	public String getPluginInstanceId() {
		return pluginInstanceId;
	}
	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
