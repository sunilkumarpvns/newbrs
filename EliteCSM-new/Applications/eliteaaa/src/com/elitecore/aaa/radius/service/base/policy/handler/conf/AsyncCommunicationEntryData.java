package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;

import net.sf.json.JSONObject;


public class AsyncCommunicationEntryData extends ExternalCommunicationEntryData {
	private boolean wait = true;

	@XmlAttribute(name = "wait")
	public boolean isWait() {
		return wait;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(super.toString());
		out.println(format("%-30s: %s", "Wait for response", isWait()));
		out.close();
		return writer.toString();
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = super.toJson();
		object.put("Wait for Response", wait);
		return object;
	}
}
