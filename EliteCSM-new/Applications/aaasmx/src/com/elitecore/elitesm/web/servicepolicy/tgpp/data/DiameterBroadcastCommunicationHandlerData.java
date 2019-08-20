package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

@XmlRootElement(name = "broadcast-handler")
@XmlType(propOrder = {"strPriorityResultCodes", "resultCodeOnNoEntrySelected", "entries"})
public class DiameterBroadcastCommunicationHandlerData extends DiameterApplicationHandlerDataSupport {
	
	public static final long LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED = 0;
	public static final long DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED = ResultCode.DIAMETER_UNABLE_TO_DELIVER.code;
	private List<DiameterBroadcastCommunicationEntryData> entries = new ArrayList<DiameterBroadcastCommunicationEntryData>();
	private String protocol = ProtocolType.DIAMETER.name();
	private String strPriorityResultCodes;
	private Long resultCodeOnNoEntrySelected;
	
	@XmlElement(name = "broadcast-communication-entry")
	@NotEmpty(message = "Atleast one Mapping is required in Broadcast(Parallel) Communication Handler")
	public List<DiameterBroadcastCommunicationEntryData> getEntries() {
		return entries;
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
	
	@XmlElement(name = "result-code-on-no-entry-selected")
	public Long getResultCodeOnNoEntrySelected() {
		return resultCodeOnNoEntrySelected;
	}
	
	public void setResultCodeOnNoEntrySelected(Long resultCodeOnNoEntrySelected) {
		this.resultCodeOnNoEntrySelected = resultCodeOnNoEntrySelected;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Enabled", getEnabled());
		object.put("Priority Result Codes", strPriorityResultCodes);
		
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