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
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctGroupsSynchronousCommunicationHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthGroupsSynchronousCommunicationHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@XmlRootElement(name = "stateful-proxy-handler")
public class GroupSynchronousCommunicationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private List<GroupExternalCommunicationEntryData> externalCommunicationEntries = new ArrayList<>();
	
	@XmlElementWrapper(name = "proxy-communication-entries")
	@XmlElement(name = "proxy-communication-entry")
	public List<GroupExternalCommunicationEntryData> getExternalCommunicationEntries() {
		return externalCommunicationEntries;
	}
	
	public void setExternalCommunicationEntries(
			List<GroupExternalCommunicationEntryData> externalCommunicationEntries) {
		this.externalCommunicationEntries = externalCommunicationEntries;
	}

	@Override
	public void postRead() {
		for(GroupExternalCommunicationEntryData entry : externalCommunicationEntries) {
			entry.setPolicyName(getPolicyName());
		}
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctGroupsSynchronousCommunicationHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthGroupsSynchronousCommunicationHandler(serviceContext, this);
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Enabled", isEnabled());

		JSONArray array = new JSONArray();
		for (GroupExternalCommunicationEntryData element : externalCommunicationEntries) {
			array.add(element.toJson());
		}
		jsonObject.put("Stateful Proxy (Sequential) Entries", array);
		return jsonObject;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Stateful Proxy (Sequential) Communication Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		for (GroupExternalCommunicationEntryData data : getExternalCommunicationEntries()) {
			out.println(data);
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}
}
