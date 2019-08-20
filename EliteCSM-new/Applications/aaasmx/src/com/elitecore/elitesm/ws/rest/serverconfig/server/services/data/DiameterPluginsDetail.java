package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data.InPluginsDetailData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data.OutPluginsDetailData;

@XmlType(propOrder={})
public class DiameterPluginsDetail {
	
	private InPluginsDetailData inPlugins;
	private OutPluginsDetailData outPlugins;
	
	public DiameterPluginsDetail() {
		this.inPlugins = new InPluginsDetailData();
		this.outPlugins  = new OutPluginsDetailData();
	}

	@XmlElement(name = "in-plugins")
	public InPluginsDetailData getInPlugins() {
		return inPlugins;
	}

	public void setInPlugins(InPluginsDetailData inPlugins) {
		this.inPlugins = inPlugins;
	}

	@XmlElement(name = "out-plugins")
	public OutPluginsDetailData getOutPlugins() {
		return outPlugins;
	}

	public void setOutPlugins(OutPluginsDetailData outPlugins) {
		this.outPlugins = outPlugins;
	}

}
