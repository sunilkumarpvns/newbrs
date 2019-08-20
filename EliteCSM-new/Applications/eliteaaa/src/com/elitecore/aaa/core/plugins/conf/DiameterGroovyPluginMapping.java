package com.elitecore.aaa.core.plugins.conf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class DiameterGroovyPluginMapping {
private String pluginName;
private List<String> fileNames = new ArrayList<String>();

@XmlElementWrapper(name = "file-names")
@XmlElement(name = "file-name")
public List<String> getFileNames() {
	return fileNames;
}
@XmlElement(name = "plugin-name")
public String getPluginName() {
	return pluginName;
}
public void setPluginName(String pluginName) {
	this.pluginName = pluginName;
}

}
