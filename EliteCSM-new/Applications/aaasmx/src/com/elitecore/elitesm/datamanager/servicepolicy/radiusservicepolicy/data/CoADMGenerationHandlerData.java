package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

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

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "coa-dm-generation-handler")
public class CoADMGenerationHandlerData extends ServicePolicyHandlerDataSupport 
implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private List<CoADMHandlerEntryData> entries = new ArrayList<CoADMHandlerEntryData>();
	private String scheduleAfterInMillis = "2000";
	
	@XmlElement(name = "coa-dm-handler")
	@NotEmpty(message = "Atleast one Mapping is required in COA/DM Handler")
	public List<CoADMHandlerEntryData> getEntries() {
		return entries;
	}

	@XmlAttribute(name = "schedule-after-in-ms")
	public String getScheduleAfterInMillis() {
		return scheduleAfterInMillis;
	}

	public void setScheduleAfterInMillis(String scheduleAfterInMillis) {
		this.scheduleAfterInMillis = scheduleAfterInMillis;
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

	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("CoA/DM Generation Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Schedule after (in ms)", getScheduleAfterInMillis()));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
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
