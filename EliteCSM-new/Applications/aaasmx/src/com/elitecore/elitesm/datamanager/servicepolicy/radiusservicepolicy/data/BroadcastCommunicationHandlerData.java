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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

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
@XmlRootElement(name = "broadcast-handler")
public class BroadcastCommunicationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {
	private List<AsyncCommunicationEntryData> externalCommunicatioEntries = new ArrayList<AsyncCommunicationEntryData>();

	@Valid
	@XmlElement(name = "async-communication-entry")
	@NotEmpty(message = "Atleast one Mapping is required in Broadcast(Parallel) Communication Handler")
	public List<AsyncCommunicationEntryData> getProxyCommunicationEntries() {
		return externalCommunicatioEntries;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return null;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
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
		out.println(format(padStart("Broadcast(Parallel) Communication Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		for (AsyncCommunicationEntryData entry : getProxyCommunicationEntries()) {
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
		
		if(Collectionz.isNullOrEmpty(externalCommunicatioEntries) == false){
			JSONArray array = new JSONArray();
			for(AsyncCommunicationEntryData asyncCommunicationEntryData : externalCommunicatioEntries){
				array.add(asyncCommunicationEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("Broadcast(Parallel) Communication Entries", array);
			}
		}
		
		return object;
	}
}
