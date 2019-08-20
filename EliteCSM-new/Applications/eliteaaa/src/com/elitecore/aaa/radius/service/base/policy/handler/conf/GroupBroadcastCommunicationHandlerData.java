package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctGroupBroadcastHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthGroupBroadcastHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.commons.base.Collectionz;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author soniya.patel
 *
 */
@XmlRootElement(name = "stateful-broadcast-handler")
public class GroupBroadcastCommunicationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {
	private List<AsyncGroupCommunicationEntryData> externalCommunicatioEntries = new ArrayList<>();

	@XmlElementWrapper(name = "async-communication-entries")
	@XmlElement(name = "async-communication-entry")
	public List<AsyncGroupCommunicationEntryData> getProxyCommunicationEntries() {
		return externalCommunicatioEntries;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctGroupBroadcastHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthGroupBroadcastHandler(serviceContext, this);
	}
	
	@Override
	public void postRead() {
		super.postRead();
		for(AsyncGroupCommunicationEntryData entry : getProxyCommunicationEntries()) {
			entry.setPolicyName(getPolicyName());
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Stateful Broadcast(Parallel) Communication Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		for (AsyncGroupCommunicationEntryData entry : getProxyCommunicationEntries()) {
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
			for(AsyncGroupCommunicationEntryData asyncCommunicationEntryData : externalCommunicatioEntries){
				array.add(asyncCommunicationEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("Stateful Broadcast(Parallel) Communication Entries", array);
			}
		}
		
		return object;
	}
}
