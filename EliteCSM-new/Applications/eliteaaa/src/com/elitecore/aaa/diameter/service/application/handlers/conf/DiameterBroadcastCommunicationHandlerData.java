package com.elitecore.aaa.diameter.service.application.handlers.conf;

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

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiaToDiaBroadcastHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiaToRadBroadcastHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

@XmlRootElement(name = "broadcast-handler")
public class DiameterBroadcastCommunicationHandlerData extends DiameterApplicationHandlerDataSupport {
	public static final long LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED = 0;
	public static final long DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED = ResultCode.DIAMETER_UNABLE_TO_DELIVER.code;
	private List<DiameterBroadcastCommunicationEntryData> entries = new ArrayList<DiameterBroadcastCommunicationEntryData>();
	private String protocol = ProtocolType.DIAMETER.name();
	private String strPriorityResultCodes;
	private long resultCodeOnNoEntrySelected = DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED;
	
	@XmlElement(name = "broadcast-communication-entry")
	public List<DiameterBroadcastCommunicationEntryData> getEntries() {
		return entries;
	}
	
	@XmlElement(name = "result-code-on-no-entry-selected")
	public long getResultCodeOnNoEntrySelected() {
		return resultCodeOnNoEntrySelected;
	}
	
	public void setResultCodeOnNoEntrySelected(long resultCodeOnNoEntrySelected) {
		this.resultCodeOnNoEntrySelected = resultCodeOnNoEntrySelected;
	}
	
	@Override
	public void postRead() {
		super.postRead();
		// TODO probably we should make it part of policy initialization, for cases when we have incorrect result codes
		List<Integer> priorityResultCodes;
		priorityResultCodes = Collectionz.map(Strings.splitter(',').split(strPriorityResultCodes), Strings.toInt());
		
		for (DiameterExternalCommunicationEntryData data : entries) {
			data.setPriorityResultCodes(priorityResultCodes);
			data.setProtocol(getProtocol());
			data.postRead(getConfigurationContext());
			getPolicyData().registerRequiredPeerGroupId(data.getPeerGroupId());
		}
	}

	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		if (ProtocolType.DIAMETER.name().equals(getProtocol())) {
			return new DiaToDiaBroadcastHandler(context, this, TranslationAgent.getInstance());
		} else {
			return new DiaToRadBroadcastHandler(context, this, TranslationAgent.getInstance());
		}
	}

	@XmlAttribute(name = "protocol")
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@XmlElement(name = "priority-result-codes")
	public String getStrPriorityResultCodes() {
		return strPriorityResultCodes;
	}
	
	public void setStrPriorityResultCodes(String strPriorityResultCodes) {
		this.strPriorityResultCodes = strPriorityResultCodes;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		if (ProtocolType.DIAMETER.name().equals(getProtocol())) {
		out.println(format(padStart("Diameter Broadcast (Parallel) Communication Handler | Enabled: %s", 10, ' '), isEnabled()));
		} else {
			out.println(format(padStart("Radius Broadcast (Parallel) Communication Handler | Enabled: %s", 10, ' '), isEnabled()));
		}
		out.println(repeat("-", 70));
		for (DiameterExternalCommunicationEntryData entry : getEntries()) {
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
		object.put("Priority Result Codes", strPriorityResultCodes);
		object.put("Result Code On No Entry Selected", resultCodeOnNoEntrySelected);
		if(Collectionz.isNullOrEmpty(entries) == false){
			JSONArray array = new JSONArray();
			for(DiameterBroadcastCommunicationEntryData diameterBroadcastCommunicationEntryData : entries){
				array.add(diameterBroadcastCommunicationEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("Proxy Communication entry", array);
			}
		}
		
		return object;
	}
}
