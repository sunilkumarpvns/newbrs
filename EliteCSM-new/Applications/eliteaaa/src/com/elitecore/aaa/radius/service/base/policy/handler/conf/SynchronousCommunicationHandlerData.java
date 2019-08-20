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
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctSynchronousCommunicationHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthSynchronousCommunicationHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "proxy-handler")
public class SynchronousCommunicationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private List<ExternalCommunicationEntryData> externalCommunicationEntries = new ArrayList<ExternalCommunicationEntryData>();

	@XmlElement(name = "proxy-communication-entry")
	public List<ExternalCommunicationEntryData> getProxyCommunicatioEntries() {
		return externalCommunicationEntries;
	}
	
	@Override
	public void postRead() {
		super.postRead();
		for(ExternalCommunicationEntryData entry : externalCommunicationEntries) {
			entry.setPolicyName(getPolicyName());
		}
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctSynchronousCommunicationHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthSynchronousCommunicationHandler(serviceContext, this);
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Proxy (Sequential) Communication Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		for (ExternalCommunicationEntryData entry : getProxyCommunicatioEntries()) {
			out.println(entry);
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Enabled", isEnabled());

		if( getProxyCommunicatioEntries() != null ){
			JSONArray array = new JSONArray();
			for (ExternalCommunicationEntryData element : externalCommunicationEntries) {
				array.add(element.toJson());
			}
			jsonObject.put("Proxy(Sequential) Entries", array);
		}
		return jsonObject;
	}
}
