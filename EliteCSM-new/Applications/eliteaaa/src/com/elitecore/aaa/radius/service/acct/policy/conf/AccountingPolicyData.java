package com.elitecore.aaa.radius.service.acct.policy.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.acct.policy.AcctResponseBehaviors;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctPostResponseHandlerData;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.BroadcastCommunicationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupBroadcastCommunicationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadPluginHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupSynchronousCommunicationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.SynchronousCommunicationHandlerData;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlRootElement(name = "accounting-policy")
public class AccountingPolicyData implements Differentiable{
	
	@XmlElementRefs({
		@XmlElementRef(name = "proxy-handler", type = SynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "broadcast-handler", type = BroadcastCommunicationHandlerData.class),
		@XmlElementRef(name = "user-profile-repository", type = RadiusSubscriberProfileRepositoryDetails.class),
		@XmlElementRef(name = "plugin-handler", type = RadPluginHandlerData.class),
		@XmlElementRef(name = "cdr-generation", type = CdrGenerationHandlerData.class),
		@XmlElementRef(name = "coa-dm-generation-handler", type = CoADMGenerationHandlerData.class),
		@XmlElementRef(name = "stateful-proxy-handler", type = GroupSynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "stateful-broadcast-handler", type = GroupBroadcastCommunicationHandlerData.class)
	})
	
	private List<AcctServicePolicyHandlerData> handlersData = new ArrayList<AcctServicePolicyHandlerData>();

	private AcctPostResponseHandlerData postResponseHandlerData = new AcctPostResponseHandlerData();

	private List<PluginEntryDetail> prePluginDataList = new ArrayList<PluginEntryDetail>();
	private List<PluginEntryDetail> postPluginDataList = new ArrayList<PluginEntryDetail>();
	
	private RadiusServicePolicyData radiusServicePolicyData;
	
	@XmlTransient
	public AcctResponseBehaviors getDefaultResponseBehaviour() {
		return radiusServicePolicyData.getDefaultAcctResponseBehavior();
	}

	@XmlTransient
	public String getPolicyName() {
		return radiusServicePolicyData.getName();
	}
	
	public List<AcctServicePolicyHandlerData> getHandlersData() {
		return this.handlersData;
	}

	@XmlTransient
	public Optional<String> getSessionManagerId() {
		return Optional.of(radiusServicePolicyData.getSessionManagerId());
	}
	
	@XmlTransient
	public String getRuleset() {
		return radiusServicePolicyData.getAccountingRuleset();
	}

	@XmlElement(name = "post-response-handler")
	public AcctPostResponseHandlerData getPostResponseHandler() {
		return postResponseHandlerData;
	}

	public void setPostResponseHandler(AcctPostResponseHandlerData postResponseHandler) {
		this.postResponseHandlerData = postResponseHandler;
	}
	
	@XmlTransient
	public boolean isValidatePacket() {
		return radiusServicePolicyData.isValidatePacket();
	}

	@XmlTransient
	public String getAcctResponseAttributes() {
		return radiusServicePolicyData.getAcctResponseAttributes();
	}

	public void postRead() {
		for (AcctServicePolicyHandlerData handlerData : handlersData) {
			handlerData.setRadiusServicePolicyData(radiusServicePolicyData);
			handlerData.setServerContext(radiusServicePolicyData.getServerContext());
			handlerData.postRead();
		}
		
		if (postResponseHandlerData != null) {
			postResponseHandlerData.setRadiusServicePolicyData(radiusServicePolicyData);
			postResponseHandlerData.postRead();
		}
	}

	@XmlTransient
	public List<String> getUserIdentities() {
		return radiusServicePolicyData.getUserIdentities();
	}

	@XmlTransient
	public ChargeableUserIdentityConfiguration getCuiConfiguration() {
		return radiusServicePolicyData.getCuiConfiguration();
	}
	
	@XmlTransient
	public RadiusServicePolicyData getRadiusServicePolicyData() {
		return radiusServicePolicyData;
	}
	
	public void setRadiusServicePolicyData(
			RadiusServicePolicyData radiusServicePolicyData) {
		this.radiusServicePolicyData = radiusServicePolicyData;
	}

	@XmlElementWrapper(name = "pre-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPrePluginDataList() {
		return prePluginDataList;
	}

	public void setPrePluginDataList(List<PluginEntryDetail> prePluginDataList) {
		this.prePluginDataList = prePluginDataList;
	}

	@XmlElementWrapper(name = "post-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPostPluginDataList() {
		return postPluginDataList;
	}

	public void setPostPluginDataList(List<PluginEntryDetail> postPluginDataList) {
		this.postPluginDataList = postPluginDataList;
	}
	
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart("Accounting policy (Basic Details): " + getPolicyName(), 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getPolicyName()));
		out.println(format("%-30s: %s", "Ruleset", getRuleset()));
		out.println(format("%-30s: %s", "Packet Validation", isValidatePacket()));
		out.println(format("%-30s: %s", "Default Response Behaviour", getDefaultResponseBehaviour()));
		out.println(format("%-30s: %s", "Pre Plugins",getPrePluginDataList()));
		out.println(format("%-30s: %s", "Post Plugins", getPostPluginDataList()));
		out.println(format("%-30s: %s", "Session Manager Id",
				getSessionManagerId().isPresent() ? getSessionManagerId() : ""));
		out.println(format("%-30s: %s", "Accounting Response attributes",
				getAcctResponseAttributes() != null ? getAcctResponseAttributes() : ""));
		out.println(format("%-30s: %s", "User Identities", getUserIdentities()));
		out.println(format("%-30s: %s", "Accounting CUI Attributes", 
				getCuiConfiguration().getAccountingCuiAttribute()));
		out.println(repeat("-", 70));
		out.println("Service Handlers");
		for (AcctServicePolicyHandlerData handlerData : handlersData) {
			out.println(handlerData);
		}
		out.println(repeat("-", 70));
		out.println("Post Response Service Flow");
		out.println(postResponseHandlerData.toString());
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		JSONArray prePluginArray = new JSONArray();
		if(Collectionz.isNullOrEmpty(prePluginDataList) == false){
			int prePluginDataListSize = prePluginDataList.size();
			for (int i = 0; i < prePluginDataListSize; i++) {
				prePluginArray.add(prePluginDataList.get(i).toJson());
			}
			if(prePluginArray.size() > 0){
				JSONObject prePluginObject = new JSONObject();
				prePluginObject.put("Pre Plug-in Entry", prePluginArray);
				object.put("Pre Plug-in", prePluginObject);
			}
		}
		
		List<String> flowOrderList = new  ArrayList<String>();
		if(Collectionz.isNullOrEmpty(handlersData) == false){
			for(AcctServicePolicyHandlerData acctServicePolicyHandlerData : handlersData){
				object.put(acctServicePolicyHandlerData.getHandlerName(), acctServicePolicyHandlerData.toJson());
				flowOrderList.add(acctServicePolicyHandlerData.getHandlerName());
			}
		}
		object.put("Handlers Order", EliteUtility.getServicePolicyOrder(flowOrderList));
		
		JSONArray postPluginArray = new JSONArray();
		if(Collectionz.isNullOrEmpty(postPluginDataList) == false){
			int postPluginDataListSize = postPluginDataList.size();
			for (int i = 0; i < postPluginDataListSize; i++) {
				postPluginArray.add(postPluginDataList.get(i).toJson());
			}
			if(postPluginArray.size() > 0){
				JSONObject poatPluginObject = new JSONObject();
				poatPluginObject.put("Post Plug-in Entry", postPluginArray);
				object.put("Post Plug-in", poatPluginObject);
			}
		}
		
		object.put("Post response Service Flow", postResponseHandlerData.toJson());
		return object;
	}
}
