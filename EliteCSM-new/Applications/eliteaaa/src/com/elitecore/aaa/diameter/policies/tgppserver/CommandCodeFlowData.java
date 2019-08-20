package com.elitecore.aaa.diameter.policies.tgppserver;

import static com.elitecore.commons.base.Strings.repeat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterApplicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRGenerationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterConcurrencyHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPluginHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPostResponseHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterSynchronousCommunicationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "command-code-flow")
public class CommandCodeFlowData implements Differentiable {
	private String commandCode;
	private String interfaceId;
	private String name;
	
	@XmlElementRefs({
		@XmlElementRef(name = "authentication-handler", type = DiameterAuthenticationHandlerData.class),
		@XmlElementRef(name = "authorization-handler", type = DiameterAuthorizationHandlerData.class),
		@XmlElementRef(name = "user-profile-repository", type = DiameterSubscriberProfileRepositoryDetails.class),
		@XmlElementRef(name = "plugin-handler", type = DiameterPluginHandlerData.class),
		@XmlElementRef(name = "proxy-handler", type = DiameterSynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "broadcast-handler", type = DiameterBroadcastCommunicationHandlerData.class),
		@XmlElementRef(name = "cdr-generation", type = DiameterCDRGenerationHandlerData.class),
		@XmlElementRef(name = "dia-concurrency-handler", type = DiameterConcurrencyHandlerData.class)
		})
	private List<DiameterApplicationHandlerData> handlersData = new ArrayList<DiameterApplicationHandlerData>();
	
	private DiameterPostResponseHandlerData postResponseHandlerData;
	
	/* Transient properties */
	private Set<Integer> commandCodes = new HashSet<Integer>();
	private Set<Long> interfaceIds = new HashSet<Long>();
	
	private TGPPServerPolicyData tgppServerPolicyData;
	private AAAConfigurationContext configurationContext;

	
	@XmlElement(name = "command-code")
	public String getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	
	@XmlElement(name = "interface-id")
	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public Set<Integer> getCommandCodes() {
		return commandCodes;
	}
	
	@XmlTransient
	public Set<Long> getInterfaceIds() {
		return interfaceIds;
	}

	public List<DiameterApplicationHandlerData> getHandlersData() {
		return handlersData;
	}
	
	@XmlElement(name = "post-response-handler")
	public DiameterPostResponseHandlerData getPostResponseHandlerData() {
		return postResponseHandlerData;
	}

	public void setPostResponseHandlerData(DiameterPostResponseHandlerData postResponseHandlerData) {
		this.postResponseHandlerData = postResponseHandlerData;
	}

	public void postRead() {
		this.commandCodes = new HashSet<Integer>(Collectionz.map(Splitter.on(',').split(commandCode),
				Strings.toInt()));
		this.interfaceIds = new HashSet<Long>(Collectionz.map(Splitter.on(',').split(interfaceId),
				Strings.toLong()));
		
		for (DiameterApplicationHandlerData handlerData : getHandlersData()) {
			handlerData.setPolicyData(tgppServerPolicyData);
			handlerData.setConfigurationContext(configurationContext);
			handlerData.postRead();
		}
		
		if (postResponseHandlerData != null) {
			postResponseHandlerData.setPolicyData(tgppServerPolicyData);
			postResponseHandlerData.setConfigurationContext(configurationContext);
			postResponseHandlerData.postRead();
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println("Command code(s): " + commandCode + "| Interface id(s): " + interfaceId);
		out.println(repeat("-", 70));
		out.println("Command code flow handlers");
		for (DiameterApplicationHandlerData handlerData : handlersData) {
			out.println(handlerData);
		}
		
		if (postResponseHandlerData != null) {
			out.println(repeat("-", 70));
			out.println("Post response flow");
			out.println(postResponseHandlerData);
		}
		
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	public void setPolicyData(TGPPServerPolicyData tgppServerPolicyData) {
		this.tgppServerPolicyData = tgppServerPolicyData;
	}

	@XmlTransient
	public AAAConfigurationContext getConfigurationContext() {
		return configurationContext;
	}
	
	public void setConfigurationContext(AAAConfigurationContext context) {
		this.configurationContext = context;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Command Code", commandCode);
		object.put("Interface", interfaceId);
		
		List<String> flowOrderList = new  ArrayList<String>();
		if(Collectionz.isNullOrEmpty(handlersData) == false){
			for(DiameterApplicationHandlerData diameterApplicationHandlerData : handlersData){
				object.put(diameterApplicationHandlerData.getHandlerName(), diameterApplicationHandlerData.toJson());
				flowOrderList.add(diameterApplicationHandlerData.getHandlerName());
			}
		}
		
		object.put("Post Response Command Code Flow", postResponseHandlerData.toJson());
		object.put(name + " Handlers Order", EliteUtility.getServicePolicyOrder(flowOrderList));
		
		return object;
	}
}
