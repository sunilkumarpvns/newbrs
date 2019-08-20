package com.elitecore.aaa.core.plugins.conf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlRootElement(name = "plugin-details")
public class PluginDetail {
	
	private Set<PluginInfo> pluginInfos = new HashSet<PluginDetail.PluginInfo>();
	private Map<String, PluginInfo> nameToInfoMap = new HashMap<String, PluginInfo>();
	
	@XmlElement(name = "plugin-info")
	public Set<PluginInfo> getPluginInfos() {
		return pluginInfos;
	}

	@XmlTransient
	public Map<String, PluginInfo> getNameToInfoMap() {
		return nameToInfoMap;
	}
	
	void postRead() {
		for (PluginInfo pluginInfo : pluginInfos) {
			nameToInfoMap.put(pluginInfo.getName(), pluginInfo);
		}
	}

	public static class PluginInfo {
		private String name;
		private String id;
		private PluginType type;
		
		public PluginInfo() {
		}

		public void set(String id, @Nonnull PluginType type) {
			this.id = id;
			this.type = type;
		}
		
		@XmlElement(name = "plugin-name")
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setType(PluginType type) {
			this.type = type;
		}

		@XmlElement(name = "plugin-id")
		public String getId() {
			return id;
		}
		
		@XmlElement(name = "plugin-type")
		public PluginType getType() {
			return type;
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if ((obj instanceof PluginInfo) == false) {
				return false;
			}
			PluginInfo that = (PluginInfo) obj;
			return this.name.equals(that.name);
		}
		
		@Override
		public String toString() {
			return "[id = " + id + ", name = " + this.name + ", type = " + type + "]";
		}
	}

	public void addPlugin(String pluginName) {
		if (Strings.isNullOrBlank(pluginName)) {
			return;
		}
		PluginInfo pluginInfo = nameToInfoMap.get(pluginName);
		if (pluginInfo == null) {
			pluginInfo = new PluginInfo();
			pluginInfo.name = pluginName;
			nameToInfoMap.put(pluginName, pluginInfo);
			pluginInfos.add(pluginInfo);
		}
	}
	
	public void addPlugins(Collection<PluginEntryDetail> names) {
		for (PluginEntryDetail pluginData : names) {
			addPlugin(pluginData.getPluginName());
		}
	}
	
	public Set<String> getPluginNames() {
		return nameToInfoMap.keySet();
	}
	
	public void set(@Nonnull String pluginName, String pluginId,@Nonnull PluginType pluginType) {
		PluginInfo pluginInfo = nameToInfoMap.get(pluginName);
		if (pluginInfo != null) {
			pluginInfo.set(pluginId, pluginType);
		}
	}
	
	public Set<PluginInfo> getTypeSpecific(@Nonnull PluginType type) {
		Set<PluginInfo> infos = new HashSet<PluginInfo>();
		for (PluginInfo info : nameToInfoMap.values()) {
			if (type == info.type) {
				infos.add(info);
			}
		}
		return infos;
	}
	
	@Override
	public String toString() {
		return pluginInfos.toString();
	}

}