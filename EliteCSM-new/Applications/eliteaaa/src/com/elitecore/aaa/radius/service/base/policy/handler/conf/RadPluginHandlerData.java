package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.RadAcctPluginHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.RadAuthPluginHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "plugin-handler")
public class RadPluginHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private List<PluginEntryData> pluginEntries = new ArrayList<PluginEntryData>();
	
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryData> getPluginEntries() {
		return pluginEntries;
	}
	
	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new RadAuthPluginHandler(serviceContext, this);
	}
	
	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new RadAcctPluginHandler(serviceContext, this);
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
		
		getServerContext().registerPlugins(plugins);
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
