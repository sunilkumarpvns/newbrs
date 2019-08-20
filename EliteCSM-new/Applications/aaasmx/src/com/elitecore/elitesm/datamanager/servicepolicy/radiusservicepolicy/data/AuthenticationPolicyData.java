package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlRootElement(name = "authentication-policy")
@XmlType(propOrder = {"prePluginDataList", "handlersData", "postPluginDataList", "postResponseHandlerData"})
public class AuthenticationPolicyData implements Differentiable, Validator {

	@XmlElementRefs({
		@XmlElementRef(name = "proxy-handler", type = SynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "broadcast-handler", type = BroadcastCommunicationHandlerData.class),
		@XmlElementRef(name = "authentication-handler", type = AuthenticationHandlerData.class),
		@XmlElementRef(name = "authorization-handler", type = AuthorizationHandlerData.class),
		@XmlElementRef(name = "user-profile-repository", type = RadiusSubscriberProfileRepositoryDetails.class),
		@XmlElementRef(name = "plugin-handler", type = RadPluginHandlerData.class),
		@XmlElementRef(name = "concurrency-handler", type = ConcurrencyHandlerData.class),
		@XmlElementRef(name = "cdr-generation", type = CdrGenerationHandlerData.class),
		@XmlElementRef(name = "coa-dm-generation-handler", type = CoADMGenerationHandlerData.class),
		@XmlElementRef(name = "concurrency-imdg-handler", type = ConcurrencyIMDGHandlerData.class),
		@XmlElementRef(name = "stateful-proxy-sequential-handler", type = StatefulProxySequentialHandlerData.class),
		@XmlElementRef(name = "stateful-proxy-broadcast-handler", type = StatefulProxyBroadcastHandlerData.class)
	})
	
	
	@Valid
	@Size(min = 1, message = "Specify atleast one handler in authentication flow")
	private List<AuthServicePolicyHandlerData> handlersData = new ArrayList<AuthServicePolicyHandlerData>();
	
	@Valid
	private AuthPostResponseHandlerData postResponseHandlerData = new AuthPostResponseHandlerData();
	
	@Valid
	private List<PluginEntryDetail> prePluginDataList = new ArrayList<PluginEntryDetail>();
	
	@Valid
	private List<PluginEntryDetail> postPluginDataList = new ArrayList<PluginEntryDetail>();
	
	private RadiusServicePolicyData radiusServicePolicyData;
	
	@XmlTransient
	public AuthResponseBehaviors getDefaultResponseBehavior() {
		return null;
	}

	@XmlTransient
	public String getPolicyName() {
		return this.radiusServicePolicyData.getName();
	}
	
	public List<AuthServicePolicyHandlerData> getHandlersData() {
		return this.handlersData;
	}

	@XmlTransient
	public String getRuleset() {
		return this.radiusServicePolicyData.getAuthenticationRuleset();
	}

	@XmlElement(name = "post-response-handler")
	public AuthPostResponseHandlerData getPostResponseHandlerData() {
		return postResponseHandlerData;
	}
	
	public void setPostResponseHandlerData(
			AuthPostResponseHandlerData postResponseHandlerData) {
		this.postResponseHandlerData = postResponseHandlerData;
	}
	
	@XmlTransient
	public String isValidatePacket() {
		return this.radiusServicePolicyData.getValidatePacket();
	}

	@XmlTransient
	public Optional<String> getHotlinePolicy() {
		return Optional.of(radiusServicePolicyData.getHotlinePolicy());
	}
	
	@XmlTransient
	public String getAuthResponseAttributes() {
		return radiusServicePolicyData.getAuthResponseAttributes();
	}
	
	public void postRead() {
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
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart("Authentication policy (Basic Details): " + getPolicyName(), 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getPolicyName()));
		out.println(format("%-30s: %s", "Ruleset", getRuleset()));
		out.println(format("%-30s: %s", "Packet Validation", isValidatePacket()));
		out.println(format("%-30s: %s", "Default Response Behaviour", getDefaultResponseBehavior()));
		out.println(format("%-30s: %s", "Hotline Policy", 
				getHotlinePolicy().isPresent() ? getHotlinePolicy() : ""));
		out.println(format("%-30s: %s", "Pre Plugins",getPrePluginDataList()));
		out.println(format("%-30s: %s", "Post Plugins", getPostPluginDataList()));
		out.println(format("%-30s: %s", "Authentication Response attributes",
				getAuthResponseAttributes() != null ? getAuthResponseAttributes() : ""));
		out.println(format("%-30s: %s", "User Identities", getUserIdentities()));
		out.println(format("%-30s: %s", "CUI", getCuiConfiguration().getCui()));
		out.println(format("%-30s: %s", "Advanced CUI Expression", 
				getCuiConfiguration().getExpression() != null ? getCuiConfiguration().getExpression() : ""));
		out.println(format("%-30s: %s", "Authentication CUI Attributes", 
				getCuiConfiguration().getAuthenticationCuiAttribute()));
		out.println(repeat("-", 70));
		out.println("Service Handlers");
		for (AuthServicePolicyHandlerData handlerData : handlersData) {
			out.println(handlerData);
		}
		out.close();
		return writer.toString();
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

	public void setPostPluginDataList(List<PluginEntryDetail> pluginDataList) {
		this.postPluginDataList = pluginDataList;
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
			for(AuthServicePolicyHandlerData authServicePolicyHandlerData : handlersData){
				object.put(authServicePolicyHandlerData.getHandlerName(), authServicePolicyHandlerData.toJson());
				flowOrderList.add(authServicePolicyHandlerData.getHandlerName());
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
	

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		int count = 0;
		boolean isValid = true;
		
		Set<String> handlerNameSet = new HashSet<String>();

		/**
		 * Validate Pre-Plugin of Authentication Flow
		 */
		List<PluginEntryDetail> prePlugins = getPrePluginDataList();
		
		if (Collectionz.isNullOrEmpty(prePlugins) == false) {

			boolean isValidPrePlugin = false;
			PluginBLManager blManager = new PluginBLManager();
			
			try {
				List<PluginInstData> authPluginList = blManager.getAuthPluginList();
				if (Collectionz.isNullOrEmpty(authPluginList) == false) {
					for (PluginEntryDetail prePlugin : prePlugins) {
						String pluginName = prePlugin.getPluginName();
						if (Strings.isNullOrBlank(pluginName) == false) {
							for (PluginInstData pluginInstData : authPluginList) {
								isValidPrePlugin = pluginInstData.getName().equals(pluginName) ? true : false;
								if (isValidPrePlugin == true) {
									break;
								}
							}
							
							if (isValidPrePlugin == false) {
								RestUtitlity.setValidationMessage(context, "Plugin: " + pluginName + " in " + "Pre Plugin is not of Auth type");
								isValid = false;
							}
						}
					}
				} else {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Auth Type of plugins does not available in system");
				}
			} catch (DataManagerException e) {
				e.printStackTrace();
				RestUtitlity.setValidationMessage(context, "Failed to retrieve Auth Type of plugins");
				isValid = false;
			}
		}
 
		/**
		 * Validate Post-Plugin of Authentication Flow
		 */
		List<PluginEntryDetail> postPlugins = getPostPluginDataList();
		
		if (Collectionz.isNullOrEmpty(postPlugins) == false) {
			
			boolean isValidPostPlugin = false;
			PluginBLManager blManager = new PluginBLManager();
			try {
				List<PluginInstData> authPluginList = blManager.getAuthPluginList();

				if (Collectionz.isNullOrEmpty(authPluginList) == false) {
					for (PluginEntryDetail postPlugin : postPlugins) {
						String pluginName = postPlugin.getPluginName();
						if (Strings.isNullOrBlank(pluginName) == false) {
							for (PluginInstData data : authPluginList) {
								isValidPostPlugin = data.getName().equals(pluginName) ? true : false;
								if (isValidPostPlugin == true) {
									break;
								}
							}
							
							if (isValidPostPlugin == false) {
								RestUtitlity.setValidationMessage(context, "Plugin: " + pluginName + " in " + "Post Plugin is not of Auth type");
								isValid = false;
							}
						}
					}
				} else {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Auth Type of plugins does not available in system");
				}
			} catch (DataManagerException e) {
				e.printStackTrace();
				RestUtitlity.setValidationMessage(context, "Failed to retrieve Auth Type of plugins");
				isValid = false;
			}
		}
		
		for (AuthServicePolicyHandlerData handler : getHandlersData()) {		
			
			if (handler instanceof AuthenticationHandlerData) {
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.AUTHENTICATION_HANDLER,
						handlerNameSet, context);
				if(isHandlerValid == false){
					isValid = false;
				}
				
				count++;
			}
			
			if(handler instanceof AuthorizationHandlerData){
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(),  HandlerConstants.AUTHORIZATION_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
				
			}else if(handler instanceof RadiusSubscriberProfileRepositoryDetails){
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.PROFILE_LOOKUP_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
				
			}else if(handler instanceof ConcurrencyHandlerData){
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.CONCURRENCY_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
				
			}else if (handler instanceof RadPluginHandlerData) { 
				RadPluginHandlerData pluginData = (RadPluginHandlerData)handler;
				PluginBLManager blManager = new PluginBLManager();

				List<PluginEntryData> configuredPlugins = pluginData.getPluginEntries();

				boolean isValidPluginInPluginHandler = false;
				List<PluginInstData> authPluginList;
				try {
					authPluginList = blManager.getAuthPluginList();
				
					if (Collectionz.isNullOrEmpty(authPluginList) == false) {
						for (PluginEntryData configurePlugin : configuredPlugins) {
							String pluginName = configurePlugin.getPluginName();
							if (Strings.isNullOrBlank(pluginName) == false) {
								for (PluginInstData data : authPluginList) {
									isValidPluginInPluginHandler = data.getName().equals(pluginName) ? true : false;
									if (isValidPluginInPluginHandler == true) {
										break;
									}
								}
							}
							
							if (isValidPluginInPluginHandler == false) {
								RestUtitlity.setValidationMessage(context, "Plugin: " + pluginName + " in " + 
										HandlerConstants.PLUGIN_HANDLER + " is not of Auth type");
								isValid = false;
							}
						}
					}
				} catch (DataManagerException e) {
					e.printStackTrace();
					RestUtitlity.setValidationMessage(context, "Failed to retrieve Auth Type of plugins");
					isValid = false;
				}

				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.PLUGIN_HANDLER,
						handlerNameSet, context);

				if(isHandlerValid == false){
					isValid = false;
				}

			}else if(handler instanceof SynchronousCommunicationHandlerData){
				SynchronousCommunicationHandlerData handlerData = (SynchronousCommunicationHandlerData)handler;
				
				if (handlerData != null) {
					if(Collectionz.isNullOrEmpty(handlerData.getProxyCommunicatioEntries()) == false){
						
						boolean isValidProxyHandler =  checkValidExternalCommunicationEntryData(handlerData.getProxyCommunicatioEntries(),context);
						
						if( isValidProxyHandler == false ){
							isValid = false;
						}
					}
				}
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.PROXY_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
				
			}else if(handler instanceof BroadcastCommunicationHandlerData){
				BroadcastCommunicationHandlerData handlerData = (BroadcastCommunicationHandlerData)handler;
				if (handlerData != null) {
					
					if(Collectionz.isNullOrEmpty(handlerData.getProxyCommunicationEntries()) == false){
					
						boolean isValidProxyHandler = checkValidAsynchCommunicationEntryData(
								handlerData.getProxyCommunicationEntries(),
								context);

						if (isValidProxyHandler == false) {
							isValid = false;
						}
					}
				}
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.BROADCAST_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
			}else if(handler instanceof CdrGenerationHandlerData){
				CdrGenerationHandlerData handlerData = (CdrGenerationHandlerData)handler;
				if(handlerData != null){
					if(Collectionz.isNullOrEmpty(handlerData.getCdrHandlers()) == false){
						
						boolean isValidCDRHandler = checkCDRHandlerEntryData(
								handlerData.getCdrHandlers(),
								context);

						if (isValidCDRHandler == false) {
							isValid = false;
						}
					}
				}
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.CDR_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
				
			} else if (handler instanceof CoADMGenerationHandlerData) {
				CoADMGenerationHandlerData handlerData = (CoADMGenerationHandlerData)handler;
				if(handlerData != null){
					if(Collectionz.isNullOrEmpty(handlerData.getEntries()) == false){
						
						boolean isValidCoADMHandler = checkCoADMEntryData(
								handlerData.getEntries(),
								context);

						if (isValidCoADMHandler == false) {
							isValid = false;
						}
					}
					
					if( Strings.isNullOrBlank(handlerData.getScheduleAfterInMillis()) == false ){
						try{
							Integer.parseInt(handlerData.getScheduleAfterInMillis());
						}catch(NumberFormatException e){
							RestUtitlity.setValidationMessage(context, "Schedule After must be numeric in COA/DM Handler of Authentication handler");
							isValid = false;
						}
					}
				}
				
				boolean isHandlerValid = handleValidation(
						handler.getHandlerName(), handler.getEnabled(), HandlerConstants.COADM_HANDLER,
						handlerNameSet, context);
				
				if(isHandlerValid == false){
					isValid = false;
				}
			}
		}
		
		if (this.postResponseHandlerData != null) {
			
			for (AuthServicePolicyHandlerData handler : getPostResponseHandlerData().getHandlersData()) {
				
				if (handler instanceof RadPluginHandlerData) {

					RadPluginHandlerData pluginData = (RadPluginHandlerData)handler;
					List<PluginEntryData> configuredPlugins = pluginData.getPluginEntries();
					PluginBLManager blManager = new PluginBLManager();

					try {
						boolean isValidPostPluginHandler = false; 
						List<PluginInstData> acctPluginList = blManager.getAuthPluginList();

						if (Collectionz.isNullOrEmpty(acctPluginList) == false) {
							for (PluginEntryData configurePlugin : configuredPlugins) {
								String pluginName = configurePlugin.getPluginName();
								if (Strings.isNullOrBlank(pluginName) == false) {
									for (PluginInstData data : acctPluginList) {
										isValidPostPluginHandler = data.getName().equals(pluginName) ? true : false;

										if (isValidPostPluginHandler == true) {
											break;
										}
									}
									
									if (isValidPostPluginHandler == false) {
										RestUtitlity.setValidationMessage(context,  "Plugin: " + pluginName + 
												" in Post Response " + HandlerConstants.PLUGIN_HANDLER + " is not of Auth type");
										isValid = false;
									}
								}
							}
						}
					} catch (DataManagerException e) {
						e.printStackTrace();
						RestUtitlity.setValidationMessage(context, "Failed to retrieve Auth Type of plugins");
						isValid = false;
					}

					boolean isHandlerNameValid = handleValidation(
							handler.getHandlerName(), handler.getEnabled(), HandlerConstants.PLUGIN_HANDLER,
							handlerNameSet, context);

					if(isHandlerNameValid == false){
						isValid = false;
					}

				} else if(handler instanceof CdrGenerationHandlerData){
					CdrGenerationHandlerData handlerData = (CdrGenerationHandlerData)handler;
					if(handlerData != null){
						if(Collectionz.isNullOrEmpty(handlerData.getCdrHandlers()) == false){
							
							boolean isValidProxyHandler = checkCDRHandlerEntryData(
									handlerData.getCdrHandlers(),
									context);

							if (isValidProxyHandler == false) {
								isValid = false;
							}
						}
					}
					
					boolean isHandlerValid = handleValidation(
							handler.getHandlerName(), handler.getEnabled(), HandlerConstants.CDR_HANDLER,
							handlerNameSet, context);
					
					if(isHandlerValid == false){
						isValid = false;
					}
					
				} else if (handler instanceof CoADMGenerationHandlerData) {
					CoADMGenerationHandlerData handlerData = (CoADMGenerationHandlerData)handler;
					if(handlerData != null){
						if(Collectionz.isNullOrEmpty(handlerData.getEntries()) == false){
							
							boolean isValidCoADMHandler = checkCoADMEntryData(
									handlerData.getEntries(),
									context);

							if (isValidCoADMHandler == false) {
								isValid = false;
							}
						}
						
						if( Strings.isNullOrBlank(handlerData.getScheduleAfterInMillis()) == false ){
							try{
								Integer.parseInt(handlerData.getScheduleAfterInMillis());
							}catch(NumberFormatException e){
								RestUtitlity.setValidationMessage(context, "Schedule After must be numeric in COA/DM Handler of Authentication handler");
								isValid = false;
							}
						}
					}
				
					boolean isHandlerValid = handleValidation(
							handler.getHandlerName(), handler.getEnabled(), HandlerConstants.COADM_HANDLER,
							handlerNameSet, context);
					
					if(isHandlerValid == false){
						isValid = false;
					}
				} else if (handler instanceof AuthenticationHandlerData) {
					RestUtitlity.setValidationMessage(context, "Authentication Handler is not supported in post response of authentication flow");
					isValid = false;
				} else if (handler instanceof AuthorizationHandlerData) {
					RestUtitlity.setValidationMessage(context, "Authorization Handler is not supported in post response of authentication flow");
					isValid = false;
				} else if (handler instanceof ConcurrencyHandlerData) {
					RestUtitlity.setValidationMessage(context, "Concurrency Handler is not supported in post response of authentication flow");
					isValid = false;
				} else if (handler instanceof RadiusSubscriberProfileRepositoryDetails) {
					RestUtitlity.setValidationMessage(context, "Profile Look Up Handler is not supported in post response of authentication flow");
					isValid = false;
				} else if (handler instanceof SynchronousCommunicationHandlerData) {
					RestUtitlity.setValidationMessage(context, "Proxy(Serial) Handler is not supported in post response of authentication flow");
					isValid = false;
				} else if (handler instanceof BroadcastCommunicationHandlerData) {
					RestUtitlity.setValidationMessage(context, "Broadcast(Parellel) Handler is not supported in post response of authentication flow");
					isValid = false;
				}
			}
		}
		
		if (count > 1) {
			RestUtitlity.setValidationMessage(context, "Only single Authentication Handler is allowed");
			isValid = false;
		}
		return isValid;
	}

	private boolean handleValidation(String handlerName, String enabledHandler, String handlerType,
			Set<String> handlerNameList, ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if( Strings.isNullOrBlank(handlerName) ){
			RestUtitlity.setValidationMessage(context, handlerType + " name must be specified in Authentication Service flow");
			isValid = false;
		} else {
			boolean isDuplicate = handlerNameList.add(handlerName);
			if(isDuplicate == false){
				
				RestUtitlity.setValidationMessage(context, "Duplicate Handler Name found in Authetication Service flow");
				isValid = false;
			} else {
				
				Pattern BOOLEAN_REGEX = Pattern.compile(RestValidationMessages.BOOLEAN_REGEX);
				
				if (Strings.isNullOrBlank(enabledHandler)) {
					RestUtitlity.setValidationMessage(context, "[ " + handlerName + " ] Handler's enabled/disable value must be specified");
					isValid = false;
				}else if(BOOLEAN_REGEX.matcher(enabledHandler).matches() == false){
					
					RestUtitlity.setValidationMessage(context,
							"Invalid value of enabled/disable field of [ " + handlerName+" ] handler, It could be 'true' or 'false'");
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
	
	private boolean checkCoADMEntryData(List<CoADMHandlerEntryData> entries,
			ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		for(CoADMHandlerEntryData coaDMEntryData : entries ){
			
			if (coaDMEntryData != null) {
				
				if( Strings.isNullOrBlank(coaDMEntryData.getPacketType()) == false ){
					if ("COA Request".equals(coaDMEntryData.getPacketType()) == false
							&& "Disconnect Request".equals(coaDMEntryData.getPacketType()) == false) {
						RestUtitlity.setValidationMessage(context, "Invalid Packet type is specified in COA/DM Handler of Authentication Flow. It could be 'COA Request' or 'Disconnect Request' ");
						isValid = false;
					}
				}else{
					RestUtitlity.setValidationMessage(context, "Packet Type must be specified in COA/DM Handler of Authentication Flow");
					isValid = false;
				}
				
				boolean isValidTranslationMapping = checkValidTransaltionMapping(coaDMEntryData.getTranslationMapping(), context, HandlerConstants.COADM_HANDLER);
				
				if( isValidTranslationMapping == false ){
					isValid = false;
				}
			}
			
		}
		
		return isValid;
	}

	private boolean checkCDRHandlerEntryData(
			List<CdrHandlerEntryData> cdrHandlers,
			ConstraintValidatorContext context) {
		
		boolean isValid = true;
		

		if( Collectionz.isNullOrEmpty(cdrHandlers) == false ){
			
			for(CdrHandlerEntryData cdrHandlerEntryData : cdrHandlers){
				
				if(cdrHandlerEntryData.getDriverDetails() != null){
					
					RadiusProfileDriversDetails driversDetails = cdrHandlerEntryData.getDriverDetails();
					if( driversDetails.getPrimaryDriverGroup() != null ){
						 
						List<PrimaryDriverDetail> primaryDriverDataList = driversDetails.getPrimaryDriverGroup();
						List<SecondaryAndCacheDriverDetail> cacheDriverDetails = driversDetails.getSecondaryDriverGroup();
						DriverBLManager driverBLManager = new DriverBLManager();
						
						try{
							List<DriverInstanceData> listOfAcctDriver = driverBLManager.getDriverInstanceList(ServiceTypeConstants.ACCOUNTING_SERVICE);
						
							for(PrimaryDriverDetail primaryDriverDetail : primaryDriverDataList){
								 
								String primaryDriverName = primaryDriverDetail.getDriverInstanceId();
								
								if( Strings.isNullOrBlank(primaryDriverName) ){
									 RestUtitlity.setValidationMessage(context, "Primary Driver name must be specified in CDR hanlder of Authentication Flow");
									 isValid = false;
								}else{
									boolean isDriverMatched = false;
									 
									 for(DriverInstanceData driverInstanceData : listOfAcctDriver){
										 if(driverInstanceData.getName().equals(primaryDriverName.trim())){
											 isDriverMatched = true;
											 break;
										 }
									 }
									 
									 if( isDriverMatched == false ){
										 RestUtitlity.setValidationMessage(context, "Invalid type of Primary Driver name in CDR hanlder of Authentication Flow");
										 isValid = false;
									 }
								}
							}
							
							if( Collectionz.isNullOrEmpty(cacheDriverDetails) == false ){
								
								for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail : cacheDriverDetails){
									String secondaryDriverName = secondaryAndCacheDriverDetail.getSecondaryDriverId();
									
									if( Strings.isNullOrBlank(secondaryDriverName) == false ){
										 boolean isDriverMatched = false;
										 
										 for(DriverInstanceData driverInstanceData : listOfAcctDriver){
											 if(driverInstanceData.getName().equals(secondaryDriverName.trim())){
												 isDriverMatched = true;
												 break;
											 }
										 }
										 
										 if( isDriverMatched == false ){
											 RestUtitlity.setValidationMessage(context, "Invalid type of Secondary Driver name in CDR hanlder of Authentication Flow");
											 isValid = false;
										 }
									}
								}
							}
						
						}catch(Exception e){
							e.printStackTrace();
						}
						
					}else{
						RestUtitlity.setValidationMessage(context, "Primary Driver must be specified in CDR hanlder of Authentication Flow");
						isValid = false;
					}
				}else{
					RestUtitlity.setValidationMessage(context, "Please specify drivers details in CDR hanlder of Authentication Flow");
					isValid = false;
				}
			}
		}else{
			RestUtitlity.setValidationMessage(context, "Please specify drivers details in CDR hanlder of Authentication Flow");
			isValid = false;
		}
		
		return isValid;
	}

	private boolean checkValidExternalCommunicationEntryData(List<ExternalCommunicationEntryData> communicationEntryDatas,ConstraintValidatorContext context){
		boolean isValid = true;
		
		for(ExternalCommunicationEntryData externalCommunicationEntryData : communicationEntryDatas ){
			
			if (externalCommunicationEntryData != null) {
				
				boolean isValidEsi = checkValidESI(externalCommunicationEntryData.getCommunicatorGroupData(), context, HandlerConstants.PROXY_HANDLER);
				boolean isValidTranslationMapping = checkValidTransaltionMapping(externalCommunicationEntryData.getTranslationMapping(), context,  HandlerConstants.PROXY_HANDLER);
				
				Pattern BOOLEAN_REGEX = Pattern.compile(RestValidationMessages.BOOLEAN_REGEX);
				
				/* Accept On Timeout */
				if( Strings.isNullOrBlank(externalCommunicationEntryData.getAcceptOnTimeout())){
					RestUtitlity.setValidationMessage(context,
							"Accept On Timeout must be specified  in "+ HandlerConstants.PROXY_HANDLER +" Handler of Authentication Flow, It could be 'true' or 'false'");
					isValid = false;
				}else if(BOOLEAN_REGEX.matcher(externalCommunicationEntryData.getAcceptOnTimeout()).matches() == false){
					RestUtitlity.setValidationMessage(context,
							"Invalid value of Accept On Timeout [ " + externalCommunicationEntryData.getAcceptOnTimeout()+" ] in "+ HandlerConstants.PROXY_HANDLER +" of Authentication Flow, It could be 'true' or 'false'");
					isValid = false;
				} 	
				
				if( isValidEsi == false || isValidTranslationMapping == false){
					isValid = false;
				}
			}
			
		}
		
		return isValid;
	}
	
	private boolean checkValidAsynchCommunicationEntryData(List<AsyncCommunicationEntryData> communicationEntryDatas,ConstraintValidatorContext context){
		boolean isValid = true;
		
		for(AsyncCommunicationEntryData asynchCommunicationEntryData : communicationEntryDatas ){
			if (asynchCommunicationEntryData != null) {
				
				boolean isValidEsi = checkValidESI(asynchCommunicationEntryData.getCommunicatorGroupData(), context, HandlerConstants.BROADCAST_HANDLER );
				boolean isValidTranslationMapping = checkValidTransaltionMapping(asynchCommunicationEntryData.getTranslationMapping(), context, HandlerConstants.BROADCAST_HANDLER);
				
				Pattern BOOLEAN_REGEX = Pattern.compile(RestValidationMessages.BOOLEAN_REGEX);
				
				/* Accept On Timeout */
				if( Strings.isNullOrBlank(asynchCommunicationEntryData.getAcceptOnTimeout())){
					RestUtitlity.setValidationMessage(context,
							"Accept On Timeout must be specified  in "+ HandlerConstants.BROADCAST_HANDLER +" of Authentication Flow, It could be 'true' or 'false'");
					isValid = false;
				}else if(BOOLEAN_REGEX.matcher(asynchCommunicationEntryData.getAcceptOnTimeout()).matches() == false){
					RestUtitlity.setValidationMessage(context,
							"Invalid value of Accept On Timeout [ " + asynchCommunicationEntryData.getAcceptOnTimeout()+" ] in "+ HandlerConstants.BROADCAST_HANDLER +" of Authentication Flow, It could be 'true' or 'false'");
					isValid = false;
				} 	
				
				/* Wait for Response */
				if( Strings.isNullOrBlank(asynchCommunicationEntryData.getWait())){
					RestUtitlity.setValidationMessage(context,
							"Wait for Response must be specified  in "+ HandlerConstants.BROADCAST_HANDLER +" of Authentication Flow, It could be 'true' or 'false'");
					isValid = false;
				}else if(BOOLEAN_REGEX.matcher(asynchCommunicationEntryData.getWait()).matches() == false){
					RestUtitlity.setValidationMessage(context,
							"Invalid value of Wait for Response [ " + asynchCommunicationEntryData.getWait()+" ] in "+ HandlerConstants.BROADCAST_HANDLER +" of Authentication Flow, It could be 'true' or 'false'");
					isValid = false;
				} 	
				
				if( isValidEsi == false || isValidTranslationMapping == false){
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
	
	private boolean checkValidESI(CommunicatorGroupData communicatorGroupData, ConstraintValidatorContext context, String flowType) {
		boolean isValid = true;

		if (communicatorGroupData != null) {

			ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();

			if( Collectionz.isNullOrEmpty(communicatorGroupData.getCommunicatorDataList()) == false ){
				for (CommunicatorData communicatorData : communicatorGroupData.getCommunicatorDataList()) {
					String esiIId = communicatorData.getId();

					try {
						
						if( Strings.isNullOrBlank(esiIId) ){
							RestUtitlity.setValidationMessage(context, "Server name must be specified in Server/Group of " + flowType+ " of Authentication Flow");
							isValid = false;
						}else{
							externalSystemBLmanager.getExternalSystemInterfaceInstanceDataByName(esiIId);
						}
					} catch (DataManagerException de) {
						RestUtitlity.setValidationMessage(context, "Configured ESI ["+esiIId+"] is invalid in " + flowType+ " of Authentication Flow");
						isValid = false;
					}
					
					String loadFactor = communicatorData.getLoadFactor();
					
					if( Strings.isNullOrBlank(loadFactor) ){
						RestUtitlity.setValidationMessage(context, "Load Factor name must be specified in Server/Group of " + flowType+ " of Authentication Flow");
						isValid = false;
					}else{
						
						try {
							int loadFactorVal = Integer.parseInt(loadFactor);
							if (loadFactorVal < 0 || loadFactorVal > 10) {
								RestUtitlity
										.setValidationMessage(
												context,
												"Invalid value of Load factor [ "+loadFactor+" ] in "+ flowType+" of Authentication Flow. It must be between 0 to 10");
								isValid = false;
							}
						} catch (NumberFormatException numberFormatException) {
							RestUtitlity
									.setValidationMessage(
											context,
											"Invalid value of Load factor [ "+loadFactor+" ]  in "+flowType+" of Authentication Flow. It must be Digit only[between 0 to 10]");
							isValid = false;
						}
					}
					
				}
			}else{
				RestUtitlity.setValidationMessage(context, "Aleast one ESI mapping is required in "+ flowType +" of Authentication Flow");
				isValid = false;
			}
		} else {
			RestUtitlity.setValidationMessage(context, "Atleast one ESI Mapping is Required in "+ flowType +" of Authentication Flow");
			isValid = false;
		}

		return isValid;
	}
	
	
	private boolean checkValidTransaltionMapping(String translationMapping, ConstraintValidatorContext context, String flowType){
		
		boolean isValid = true;
		try {
			
			if( Strings.isNullOrBlank(translationMapping) == false){
				TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> r2rTranslationMapping = blManager.getRadiusToRadiusTranslationMapping();
				
				boolean isValidTranslationMapping = false;
				for (TranslationMappingConfData data : r2rTranslationMapping) {
					isValidTranslationMapping = data.getName().equals(translationMapping) ? true : false;
					if (isValidTranslationMapping == true) {
						break;
					}
				}
				
				if (isValidTranslationMapping == false) {
					CopyPacketTransMapConfBLManager copyPktBLManager = new CopyPacketTransMapConfBLManager();
					List<CopyPacketTranslationConfData> r2rCopyPktMapping = copyPktBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);

					boolean isValidCopyPacketMapping = false;
					for (CopyPacketTranslationConfData cpPktdata : r2rCopyPktMapping) {
						isValidCopyPacketMapping = cpPktdata.getName().equals(translationMapping) ? true : false;

						if (isValidCopyPacketMapping == true) {
							break;
						}	
					}
					
					if (isValidTranslationMapping == false && isValidCopyPacketMapping == false) {
						RestUtitlity.setValidationMessage(context, "Translation or Copy Packet Mapping: " + translationMapping + " is not of type Radius To Radius in " + flowType);
						isValid = false;
					}
				}
			}			
		} catch (DataManagerException e) {
			e.printStackTrace();
			isValid = false;
		}
		return isValid;
	}
}
