package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.PluginInfo;

@XmlType(propOrder = {})
@XmlRootElement(name = "mapping")
@ConfigurationProperties(moduleName = "AAA_PLUGIN_MNGR_CONFIGURABLE", synchronizeKey ="SYSTEM_MAPPING", readWith = XMLReader.class)
@XMLProperties(configDirectories = {"conf","system"}, name = "system-mapping", schemaDirectories = {"system","schema"})
public class AAAPluginConfManagerConfigurable extends Configurable {

	private List<PluginInfo> radPluginInfoList;
	private List<PluginInfo> diameterPluginInfoList;
	
	public AAAPluginConfManagerConfigurable() {
		radPluginInfoList = new ArrayList<PluginInfo>();
		diameterPluginInfoList = new ArrayList<PluginInfo>();
	}
	
	@XmlElementWrapper(name ="rad-plugins")
	@XmlElement(name ="plugin")
	public List<PluginInfo> getRadPluginInfoList() {
		return radPluginInfoList;
	}

	public void setRadPluginInfoList(List<PluginInfo> radPluginInfoList) {
		this.radPluginInfoList = radPluginInfoList;
	}

	@XmlElementWrapper(name ="diameter-plugins")
	@XmlElement(name ="plugin")
	public List<PluginInfo> getDiameterPluginInfoList() {
		return diameterPluginInfoList;
	}

	public void setDiameterPluginInfoList(List<PluginInfo> diameterPluginInfoList) {
		this.diameterPluginInfoList = diameterPluginInfoList;
	}	
		
	@PostRead
	public void postReadProcessing() {
		Iterator<PluginInfo> radPlugInListIterator = radPluginInfoList.iterator();
		if(radPlugInListIterator !=null){
			while (radPlugInListIterator.hasNext()) {
				PluginInfo pluginInfo = radPlugInListIterator.next();
				if (isValidPlugin(pluginInfo) == false) {
					radPlugInListIterator.remove();
				}
			}
		}
		Iterator<PluginInfo> diameterPlugInListIterator = diameterPluginInfoList.iterator();
		
		if(diameterPlugInListIterator != null){
			while (diameterPlugInListIterator.hasNext()) {
				PluginInfo diaPluginInfo = diameterPlugInListIterator.next();

				if (isValidPlugin(diaPluginInfo) == false) {
					diameterPlugInListIterator.remove();
				}
			}
		}	
	}

	/**
	 * Checks if Plugin Info is valid
	 * 
	 * @param pluginInfo
	 * @return true if plugin has some non-empty name and non-empty class name
	 */
	private boolean isValidPlugin(PluginInfo pluginInfo) {
		return (Strings.isNullOrBlank(pluginInfo.getPluginName()) == false &&
				Strings.isNullOrBlank(pluginInfo.getPluginClass()) == false);
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}
}
