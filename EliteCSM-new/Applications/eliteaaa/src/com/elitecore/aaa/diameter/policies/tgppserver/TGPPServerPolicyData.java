package com.elitecore.aaa.diameter.policies.tgppserver;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.CommandCodeResponseAttribute;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType.DefaultResponseBehaviorTypeAdapter;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.RejectBehavior;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.BooleanAdapter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@XmlRootElement(name = "tgpp-server-policy")
public class TGPPServerPolicyData implements DiameterServicePolicyConfiguration,Differentiable {
	private static final char MULTI_USER_IDENTITY_SEPARATOR = ',';

	private static final String MODULE = "TGPP-SERVER-POLICY-DATA";
	
	private String id;
	private String name;
	private String description;
	private String userIdentity;
	private String ruleset;
	private Boolean sessionManagementEnabled = false;
	private List<CommandCodeFlowData> commandCodeFlows = new ArrayList<CommandCodeFlowData>();
	private DefaultResponseBehaviorType defaultResponseBehaviorType = DefaultResponseBehaviorType.REJECT;
	private String defaultResponseBehaviorParameter = String.valueOf(RejectBehavior.DEFAULT_RESULT_CODE.code);
	
	/* Transient properties */
	private List<String> userIdentities = new ArrayList<String>(0);
	private Set<String> requiredDriversIds = new HashSet<String>();
	private Set<String> requiredPeerGroupIds = new HashSet<String>();
	
	private AAAConfigurationContext configurationContext;
	private List<CommandCodeResponseAttribute> commandCodeResponseAttributesList = new ArrayList<CommandCodeResponseAttribute>();
	private Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap = Collections.emptyMap();
	


	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleset;
	}
	
	public void setRuleSet(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "user-identity")
	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	@XmlElementWrapper(name = "command-code-flows")
	@XmlElement(name = "command-code-flow")
	public List<CommandCodeFlowData> getCommandCodeFlows() {
		return commandCodeFlows;
	}

	@XmlTransient
	public List<String> getUserIdentities() {
		return userIdentities;
	}

	@XmlTransient
	public Set<String> getRequiredDriversIds() {
		return requiredDriversIds;
	}

	public void registerRequiredDriverId(String driverId) {
		this.requiredDriversIds.add(driverId);
	}
	
	@XmlTransient
	public Set<String> getRequiredPeerGroupIds() {
		return this.requiredPeerGroupIds;
	}

	public void registerRequiredPeerGroupId(String peerId) {
		this.requiredPeerGroupIds.add(peerId);
	}
	
	
	public void postRead() {
		postReadProcessingForUserIdentities();
		postReadProcessingForResponseAttributes();
		
		for (CommandCodeFlowData flow : commandCodeFlows) {
			flow.setPolicyData(this);
			flow.setConfigurationContext(configurationContext);
			flow.postRead();
		}
		
	}
	
	public CommandCodeFlowData createDefaultSTRFlowData() {
		CommandCodeFlowData defaultSTRFlowData = new CommandCodeFlowData();
		defaultSTRFlowData.setCommandCode(CommandCode.SESSION_TERMINATION.code+ "");
		defaultSTRFlowData.setInterfaceId(String.valueOf(TGPPServerPolicy.ANY_INTERFACE_ID));
		defaultSTRFlowData.setName("Default_STR_CommandCodeFlow");
		defaultSTRFlowData.setPolicyData(this);
		defaultSTRFlowData.setConfigurationContext(configurationContext);
		defaultSTRFlowData.postRead();
		return defaultSTRFlowData;
	}


	private void postReadProcessingForUserIdentities() {
		userIdentities = Strings.splitter(MULTI_USER_IDENTITY_SEPARATOR).trimTokens().split(userIdentity);
	}
	
	private void postReadProcessingForResponseAttributes() {
		if (Collectionz.isNullOrEmpty(commandCodeResponseAttributesList)) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No Command-Codes wise response attributes configured for TGPP AAA Service Policy: " + getName());
			}
			return;
		}

		Map<Integer, AdditionalResponseAttributes> commandCodeToAdditionalAttrMap = new HashMap<Integer, AdditionalResponseAttributes>();
		for (CommandCodeResponseAttribute commandCodeWiseResponseAttribute : commandCodeResponseAttributesList) {
			
			String responseAttributes = commandCodeWiseResponseAttribute.getResponseAttributes();
			if (Strings.isNullOrBlank(responseAttributes)) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "No response attributes configured for Command-Codes: " + commandCodeWiseResponseAttribute.getCommandCodes() + 
							" for TGPP AAA Service Policy: " + getName());
				}
				continue;
			}
			
			AdditionalResponseAttributes additionalResponseAttributes = new AdditionalResponseAttributes(responseAttributes.trim());
			String[] commandCodes = ParserUtility.splitString(commandCodeWiseResponseAttribute.getCommandCodes(), ',' , ';');
			for (String commandCode : commandCodes) {

				if (Strings.isNullOrBlank(commandCode)) {
					continue;
				}
				
				try {
					commandCodeToAdditionalAttrMap.put(Integer.parseInt(commandCode), additionalResponseAttributes);
				} catch (NumberFormatException e) {
					if (LogManager.getLogger().isErrorLogLevel()) {
						LogManager.getLogger().error(MODULE, "Skipping invalid Command-Code: " + commandCode + 
								" from Command-Codes: " + commandCodeWiseResponseAttribute.getCommandCodes() + 
								" for TGPP AAA Service Policy: " + getName() + 
								", Reason: " + e.getMessage());
					}
				}
			}
		}
	
		this.commandCodeResponseAttributesMap = commandCodeToAdditionalAttrMap;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart("TGPP Server Policy (Basic Details): " + getName(), 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getName()));
		out.println(format("%-30s: %s", "Ruleset", getRuleSet()));
		out.println(format("%-30s: %s", "Session Management Enabled", isSessionManagementEnabled()));
		out.println(format("%-30s: %s", "Default Response Behavior", defaultResponseBehaviorType));
		out.println(format("%-30s: %s", "Response Behavior Parameter", Strings.valueOf(defaultResponseBehaviorParameter, "")));
		out.println(repeat("-", 70));
		out.println("Command Code Flows");
		for (CommandCodeFlowData commandCodeFlowData : commandCodeFlows) {
			out.println(commandCodeFlowData);
		}
		out.close();
		return writer.toString();
	}

	@Override
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	@XmlJavaTypeAdapter(value = BooleanAdapter.class)
	@XmlElement(name = "session-management")
	public Boolean isSessionManagementEnabled() {
		return sessionManagementEnabled;
	}

	public void setSessionManagementEnabled(Boolean sessionManagementEnabled) {
		this.sessionManagementEnabled = sessionManagementEnabled;
	}

	@Override
	@XmlTransient
	public Map<Integer, AdditionalResponseAttributes> getCommandCodeResponseAttributesMap() {
		return this.commandCodeResponseAttributesMap;
	}
	
	public void setCommandCodeResponseAttributesMap(
			Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap) {
		this.commandCodeResponseAttributesMap = commandCodeResponseAttributesMap;
	}
	
	@XmlTransient
	public AAAConfigurationContext getConfigurationContext() {
		return configurationContext;
	}
	
	public void setConfigurationContext(AAAConfigurationContext context) {
		this.configurationContext = context;
	}

	public void registerRequiredPlugin(List<PluginEntryDetail> plugins) {
		configurationContext.getServerContext().registerPlugins(plugins);	
	}
	
	@Override
	@XmlElementWrapper(name = "command-code-wise-response-attribute-list")
	@XmlElement(name = "command-code-wise-response-attribute")
	public List<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList() {
		return commandCodeResponseAttributesList;
	}

	
	public void setCommandCodeResponseAttributesList(
			ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList) {
		this.commandCodeResponseAttributesList = commandCodeResponseAttributesList;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		if(Collectionz.isNullOrEmpty(commandCodeResponseAttributesList) == false){
			JSONArray array = new JSONArray();
			for(CommandCodeResponseAttribute commandCodeResponseAttribute : commandCodeResponseAttributesList){
				array.add(commandCodeResponseAttribute.toJson());
			}
			if(array.size() > 0){
				object.put("Command Code Wise Response Attribute Entry", array);
			}
		}
		
		if(Collectionz.isNullOrEmpty(commandCodeFlows) == false){
			for(CommandCodeFlowData commandCodeFlowData : commandCodeFlows){
				object.put(commandCodeFlowData.getName(),commandCodeFlowData.toJson());
			}
		}
		return object;
	}

	@Override
	@XmlElement(name = "default-response-behavior-type")
	@XmlJavaTypeAdapter(value = DefaultResponseBehaviorTypeAdapter.class)
	public DefaultResponseBehaviorType getDefaultResponseBehaviorType() {
		return defaultResponseBehaviorType;
	}

	public void setDefaultResponseBehaviorType(DefaultResponseBehaviorType responseBehaviorType) {
		this.defaultResponseBehaviorType = responseBehaviorType;
	}
	
	@Override
	@XmlElement(name = "default-response-behavior-parameter")
	@Nullable
	public String getDefaultResponseBehaviorParameter() {
		return defaultResponseBehaviorParameter;
	}
	
	public void setDefaultResponseBehaviorParameter(String responseBehaviorParameter) {
		this.defaultResponseBehaviorParameter = responseBehaviorParameter;
	}
}
