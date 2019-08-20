package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlRootElement(name = "plugin-entry")
@XmlType(propOrder={"pluginName","pluginArgument"})
public class PluginEntryDetailData{

	
	private String pluginName;
	private String pluginArgument;
	
	public PluginEntryDetailData() {
		pluginName =RestValidationMessages.NONE_WITH_HYPHEN;
	}
	
	@XmlElement(name = "plugin-name")
	public String getPluginName() {
		if(Strings.isNullOrBlank(pluginName)){
			return RestValidationMessages.NONE_WITH_HYPHEN;
		}
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@XmlElement(name = "argument")
	public String getPluginArgument() {
		return pluginArgument;
	}

	public void setPluginArgument(String pluginArgument) {
		this.pluginArgument = pluginArgument;
	}

}
