package com.elitecore.aaa.radius.service.dynauth.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;

public class StaticNasCommunicationHandlerData {
	
	private List<ExternalCommunicationEntryData> externalCommunicationEntries = new ArrayList<ExternalCommunicationEntryData>();
	private String policyName;


	@XmlElement(name = "proxy-communication-entry")
	public List<ExternalCommunicationEntryData> getProxyCommunicatioEntries() {
		return externalCommunicationEntries;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart("Proxy Communication Handler", 10, ' '));
		out.println(repeat("-", 70));
		for (ExternalCommunicationEntryData entry : getProxyCommunicatioEntries()) {
			out.println(entry);
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	public String getPolicyName() {
		return policyName;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
}
