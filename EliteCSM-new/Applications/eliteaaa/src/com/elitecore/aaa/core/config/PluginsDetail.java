package com.elitecore.aaa.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class PluginsDetail {

	
	private PrePluginsDetail prePlugins;
	private PostPluginsDetail postPlugins;
	
	public PluginsDetail(){
		prePlugins = new PrePluginsDetail();
		postPlugins = new PostPluginsDetail();
	}
	@XmlElement(name = "pre-plugins")
	public PrePluginsDetail getPrePlugins() {
		return prePlugins;
	}
	public void setPrePlugins(PrePluginsDetail prePlugins) {
		this.prePlugins = prePlugins;
	}
	
	@XmlElement(name ="post-plugins")
	public PostPluginsDetail getPostPlugins() {
		return postPlugins;
	}
	public void setPostPlugins(PostPluginsDetail postPlugins) {
		this.postPlugins = postPlugins;
	}
	


}
