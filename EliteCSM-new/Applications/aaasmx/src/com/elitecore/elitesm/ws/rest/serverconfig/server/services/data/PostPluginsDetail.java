package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;


@XmlRootElement(name ="post-plugins")
@XmlType(propOrder = {"postPlugins"})
public class PostPluginsDetail {
	
	@Valid
	@NotEmpty(message = "Atleast one Post Plugin Entry must be specified." )
	private List<PluginEntryDetail> postPlugins;

	public PostPluginsDetail(){
		postPlugins = new ArrayList<PluginEntryDetail>();
	}

	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPostPlugins() {
		return postPlugins;
	}

	public void setPostPlugins(List<PluginEntryDetail> postPlugins) {
		this.postPlugins = postPlugins;
	}
}
