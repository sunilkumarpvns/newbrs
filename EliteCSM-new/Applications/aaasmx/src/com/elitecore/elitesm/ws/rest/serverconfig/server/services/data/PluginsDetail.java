package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlRootElement(name ="plugin-list")
@XmlType(propOrder = {"prePlugins", "postPlugins"})
public class PluginsDetail {
	
	@Valid
	private PrePluginsDetail prePlugins;
	
	@Valid
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
