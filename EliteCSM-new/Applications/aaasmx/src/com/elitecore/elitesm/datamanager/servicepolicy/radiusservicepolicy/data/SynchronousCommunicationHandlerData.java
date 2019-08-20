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

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "proxy-handler")
public class SynchronousCommunicationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private List<ExternalCommunicationEntryData> externalCommunicationEntries = new ArrayList<ExternalCommunicationEntryData>();

	@Valid
	@XmlElement(name = "proxy-communication-entry")
	@NotEmpty(message = "Atleast one Mapping is required in Proxy(Sequential) Communication Handler")
	public List<ExternalCommunicationEntryData> getProxyCommunicatioEntries() {
		return externalCommunicationEntries;
	}
	
	@Override
	public void postRead() {
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
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Proxy (Sequential) Communication Handler | Enabled: %s", 10, ' '), getEnabled()));
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
		jsonObject.put("Enabled", getEnabled());

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
