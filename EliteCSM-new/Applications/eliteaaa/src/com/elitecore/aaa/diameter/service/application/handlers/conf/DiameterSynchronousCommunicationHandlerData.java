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
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiaToDiaProxyHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiaToRadProxyHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

@XmlRootElement(name = "proxy-handler")
public class DiameterSynchronousCommunicationHandlerData extends DiameterApplicationHandlerDataSupport {
	/**
	 * A special value that signifies that in case when no ruleset is satisfied then the handler will not
	 * update the result code value and leave the result code value as is.
	 */
	public static final long LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED = 0;
	public static final long DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED = ResultCode.DIAMETER_UNABLE_TO_DELIVER.code;
	private static final String MODULE = "PROXY-COMM-HNDLR-DATA";
	
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
	private long resultCodeOnNoEntrySelected = DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED;
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
	public long getResultCodeOnNoEntrySelected() {
		return resultCodeOnNoEntrySelected;
	}
	
	public void setResultCodeOnNoEntrySelected(long resultCodeOnNoEntrySelected) {
		this.resultCodeOnNoEntrySelected = resultCodeOnNoEntrySelected;
	}

	@Override
	public void postRead() {
		super.postRead();
		validateResultCodeOnNoEntrySelected();
		// TODO probably we should make it part of policy initialization, for cases when we have incorrect result codes
		List<Integer> priorityResultCodes;
		priorityResultCodes = Collectionz.map(Strings.splitter(',').split(strPriorityResultCodes), Strings.toInt());
		
		for (DiameterExternalCommunicationEntryData data : entries) {
			data.setProtocol(getProtocol());
			data.setPriorityResultCodes(priorityResultCodes);
			data.postRead(getConfigurationContext());
			getPolicyData().registerRequiredPeerGroupId(data.getPeerGroupId());
		}
	}
	
	
	private void validateResultCodeOnNoEntrySelected() {
		ResultCode resultCode = ResultCode.fromCode((int) getResultCodeOnNoEntrySelected());
		if (resultCode == null && getResultCodeOnNoEntrySelected() != LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED) {
			LogManager.getLogger().warn(MODULE, "Unknown result code value: " + getResultCodeOnNoEntrySelected()
					+ " configured. Will use default result code value of: " + DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED);
			setResultCodeOnNoEntrySelected(DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED);
		}
	}

	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		if (ProtocolType.DIAMETER.name().equals(getProtocol())) {
			return new DiaToDiaProxyHandler(context, this, TranslationAgent.getInstance());
		} else {
			return new DiaToRadProxyHandler(context, this, TranslationAgent.getInstance());
		}
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		if (ProtocolType.DIAMETER.name().equals(getProtocol())) {
			out.println(format(padStart("Diameter Proxy (Sequential) Communication Handler | Enabled: %s | Proxy Mode: %s", 10, ' '), isEnabled(), getProxyMode()));
		} else {
			out.println(format(padStart("Radius Proxy (Sequential) Communication Handler | Enabled: %s", 10,' '), isEnabled()));
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
}
