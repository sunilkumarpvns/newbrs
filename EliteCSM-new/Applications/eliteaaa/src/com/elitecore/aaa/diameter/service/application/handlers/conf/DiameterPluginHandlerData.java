package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterPluginHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.PluginEntryData;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlRootElement(name = "plugin-handler")
public class DiameterPluginHandlerData extends DiameterApplicationHandlerDataSupport {

	private List<PluginEntryData> pluginEntries = new ArrayList<PluginEntryData>();
	
	@XmlTransient
	private List<PluginEntryDetail> plugins = new ArrayList<PluginEntryDetail>();
	
	
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryData> getPluginEntries() {
		return pluginEntries;
	}
	
	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		return new DiameterPluginHandler(context, this);
	}
	
	@Override
	public void postRead() {
		super.postRead();
		
		List<PluginEntryDetail> plugins = new ArrayList<PluginEntryDetail>();
		
		for (int index = 0; index < pluginEntries.size(); index++) {
			PluginEntryData data = pluginEntries.get(index);
			
			PluginEntryDetail detail = new PluginEntryDetail();
			detail.setPluginName(data.getPluginName());
			detail.setPluginArgument(data.getPluginArgument());
			plugins.add(detail);
		}
		
		getPolicyData().registerRequiredPlugin(plugins);
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Plugin Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		for (PluginEntryData entry : getPluginEntries()) {
			out.println(entry);
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", isEnabled());
		if(Collectionz.isNullOrEmpty(pluginEntries) == false){
			JSONArray array = new JSONArray();
			for(PluginEntryData pluginEntryData : pluginEntries){
				array.add(pluginEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("Plugin entry", array);
			}
		}
		return object;
	}
}
