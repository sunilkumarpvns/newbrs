package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

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
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctCdrGenerationHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthCdrGenerationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.commons.base.Collectionz;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "cdr-generation")
public class CdrGenerationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {
	private List<CdrHandlerEntryData> cdrHandlers = new ArrayList<CdrHandlerEntryData>();

	@XmlElement(name = "cdr-handler-entry")
	public List<CdrHandlerEntryData> getCdrHandlers() {
		return cdrHandlers;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthCdrGenerationHandler(serviceContext, this);
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctCdrGenerationHandler(serviceContext, this);
	}
	
	@Override
	public void postRead() {
		super.postRead();
		for (CdrHandlerEntryData cdrHandlerEntryData : getCdrHandlers()) {
			cdrHandlerEntryData.setRadiusServicePolicyData(getRadiusServicePolicyData());
			cdrHandlerEntryData.postRead();
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("CDR Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		for (CdrHandlerEntryData entry : getCdrHandlers()) {
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
		if(Collectionz.isNullOrEmpty(cdrHandlers) == false){
			JSONArray array = new JSONArray();
			for(CdrHandlerEntryData cdrHandlerEntryData : cdrHandlers){
				array.add(cdrHandlerEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("CDR Handler entry", array);
			}
		}
		return object;
	}
}
