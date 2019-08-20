package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "proxy-handler")
@ValidObject
@XmlType(propOrder = {"strPriorityResultCodes", "resultCodeOnNoEntrySelected", "entries"})
public class DiameterSynchronousCommunicationHandlerData extends DiameterApplicationHandlerDataSupport implements Validator {
	/**
	 * A special value that signifies that in case when no ruleset is satisfied then the handler will not
	 * update the result code value and leave the result code value as is.
	 */
	public static final long LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED = 0;
	public static final long DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED = ResultCode.DIAMETER_UNABLE_TO_DELIVER.code;
	
	@XmlEnum
	public enum ProxyMode {
		@XmlEnumValue(value = "SINGLE")
		SINGLE,
		@XmlEnumValue(value = "MULTIPLE")
		MULTIPLE;
	}
	
	private String protocol = ProtocolType.DIAMETER.name();
	private String strPriorityResultCodes;
	private ProxyMode proxyMode = ProxyMode.MULTIPLE;
	private Long resultCodeOnNoEntrySelected;
	
	@NotEmpty(message = "Atleast one Mapping is required in Proxy(Sequential) Communication Handler")
	private List<DiameterExternalCommunicationEntryData> entries = new ArrayList<DiameterExternalCommunicationEntryData>();

	
	@XmlElement(name = "proxy-communication-entry")
	public List<DiameterExternalCommunicationEntryData> getEntries() {
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
	
	@XmlAttribute(name = "proxy-mode")
	public ProxyMode getProxyMode() {
		return proxyMode;
	}

	public void setProxyMode(ProxyMode proxyMode) {
		this.proxyMode = proxyMode;
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
		object.put("Result Code When No Entry Selected", resultCodeOnNoEntrySelected);
		
		if (ProtocolType.DIAMETER.name().equals(getProtocol())) {
			if(proxyMode.equals(ProxyMode.MULTIPLE)){
				object.put("Proxy Mode", "MULTIPLE");
			} else {
				object.put("Proxy Mode", "SINGLE");
			}
		}
		
		if(Collectionz.isNullOrEmpty(entries) == false){
			JSONArray array = new JSONArray();
			for(DiameterExternalCommunicationEntryData diameterExternalCommunicationEntryData : entries){
				array.add(diameterExternalCommunicationEntryData.toJson());
			}
			if(array.size() > 0){
				object.put("Proxy Communication entry", array);
			}
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if (getProxyMode() == null) {
			RestUtitlity.setValidationMessage(context, "Proxy Mode must be specified and Supported values are Single and Multiple only");
			isValid = false;
		}
		return isValid;
	}
}
