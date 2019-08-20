package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Collectionz;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "plugin-handler")
public class RadPluginHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData{

	@Valid
	private List<PluginEntryData> pluginEntries = new ArrayList<PluginEntryData>();
	
	@XmlElement(name = "plugin-entry")
	@NotEmpty(message = "Atleast one mapping is required in Plugin Handler")
	public List<PluginEntryData> getPluginEntries() {
		return pluginEntries;
	}
	
	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}
	
	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return null;
	}
	
	@Override
	public void postRead() {
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Plugin Handler | Enabled: %s", 10, ' '), getEnabled()));
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
		object.put("Enabled", getEnabled());
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
