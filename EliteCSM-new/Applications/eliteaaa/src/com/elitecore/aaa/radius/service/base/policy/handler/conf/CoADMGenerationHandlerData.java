package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctCoADMGenerationHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthCoADMGenerationHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "coa-dm-generation-handler")
public class CoADMGenerationHandlerData extends ServicePolicyHandlerDataSupport 
implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private List<CoADMHandlerEntryData> entries = new ArrayList<CoADMHandlerEntryData>();
	private int scheduleAfterInMillis = 2000;
	
	@XmlElement(name = "coa-dm-handler")
	public List<CoADMHandlerEntryData> getEntries() {
		return entries;
	}

	@XmlAttribute(name = "schedule-after-in-ms")
	public int getScheduleAfterInMillis() {
		return scheduleAfterInMillis;
	}

	public void setScheduleAfterInMillis(int scheduleAfterInMillis) {
		this.scheduleAfterInMillis = scheduleAfterInMillis;
	}

	@Override
	public void postRead() {
		super.postRead();
		for (CoADMHandlerEntryData entryData : getEntries()) {
			entryData.setScheduleAfterInMillis(getScheduleAfterInMillis());
		}
	}
	
	@Override
	public RadAcctServiceHandler createHandler(
			RadAcctServiceContext serviceContext) {
		return new AcctCoADMGenerationHandler(serviceContext, this);
	}

	@Override
	public RadAuthServiceHandler createHandler(
			RadAuthServiceContext serviceContext) {
		return new AuthCoADMGenerationHandler(serviceContext, this);
	}

	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("CoA/DM Generation Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Schedule after (in ms)", getScheduleAfterInMillis()));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", isEnabled());
		object.put("Schedule After", scheduleAfterInMillis);
		if(Collectionz.isNullOrEmpty(entries) == false){
			JSONArray array = new JSONArray();
			for(CoADMHandlerEntryData coADMHandlerEntryData : entries){
				array.add(coADMHandlerEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("COA/DM Entry", array);
			}
		}
		return object;
	}
}
