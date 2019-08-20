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
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctBroadcastHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthBroadcastHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.commons.base.Collectionz;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "broadcast-handler")
public class BroadcastCommunicationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {
	private List<AsyncCommunicationEntryData> externalCommunicatioEntries = new ArrayList<AsyncCommunicationEntryData>();

	@XmlElement(name = "async-communication-entry")
	public List<AsyncCommunicationEntryData> getProxyCommunicationEntries() {
		return externalCommunicatioEntries;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctBroadcastHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthBroadcastHandler(serviceContext, this);
	}
	
	@Override
	public void postRead() {
		super.postRead();
		for(AsyncCommunicationEntryData entry : getProxyCommunicationEntries()) {
			entry.setPolicyName(getPolicyName());
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Broadcast(Parallel) Communication Handler | Enabled: %s", 10, ' '), isEnabled()));
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
		object.put("Enabled", isEnabled());
		
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
