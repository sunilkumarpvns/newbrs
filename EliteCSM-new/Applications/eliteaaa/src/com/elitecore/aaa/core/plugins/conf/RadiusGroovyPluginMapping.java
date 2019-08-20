package com.elitecore.aaa.core.plugins.conf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class RadiusGroovyPluginMapping {
	private String pluginName;
	private List<String> filenames = new ArrayList<String>();

	@XmlElementWrapper(name = "filenames")
	@XmlElement(name = "filename")
	public List<String> getFilenames() {
		return filenames;
	}

	@XmlElement(name = "name")
	public String getPluginName() {
		return pluginName;
	}
	
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	
	
}
