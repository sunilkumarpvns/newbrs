package com.elitecore.aaa.radius.service.auth.plugins;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.elitecore.aaa.core.plugins.AAAUniversalPluginPolicyDetail;
import com.elitecore.aaa.core.plugins.BaseUniversalPlugin;
import com.elitecore.aaa.core.plugins.UniversalPluginConfigurationImpl;
import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class UniversalAuthPlugin extends BaseUniversalPlugin implements RadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "UNIVERSAL-AUTH-PLUGIN";
	
	public static final String UNIVERSAL_AUTH_PLUGIN = "UNIVERSAL_AUTH_PLUGIN";
	
	private List<AAAUniversalPluginPolicyDetail> preAuthPolicyList;
	private List<AAAUniversalPluginPolicyDetail> postAuthPolicyList;

	private UniversalPluginConfigurationImpl data;
	
	public UniversalAuthPlugin(PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}

	public UniversalAuthPlugin(PluginContext pluginContext, UniversalPluginConfigurationImpl pluginConf) {
		super(pluginContext, pluginConf.getPluginInfo());
		this.data = pluginConf;
		this.preAuthPolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>(data.getPrePolicyLists());
		this.postAuthPolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>(data.getPostPolicyLists());
	}
	
	@Override
	public void init() throws InitializationFailedException {
		
		if(data == null){
			throw new InitializationFailedException("Universal Auth Plugin configuration is null.");
		}

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Universal Auth Plugin: " + data.getName());
		}
		
		Collectionz.filter(preAuthPolicyList, AAAUniversalPluginPolicyDetail.POLICY_ENABLED);
		
		Collectionz.filter(postAuthPolicyList, AAAUniversalPluginPolicyDetail.POLICY_ENABLED);

		LogManager.getLogger().info(MODULE, "Universal Auth Plugin Initialized successfully: "+data.toString());
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		init();
	}

	@Override
	public void handlePreRequest(final RadServiceRequest radServiceRequest,
			final RadServiceResponse radServiceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		
		LogManager.getLogger().info(MODULE, "Request will be processed by plugin: " + getPluginName());

		final RadiusPacket radRequestPacket = new RadiusPacket();
		radRequestPacket.setBytes(radServiceRequest.getRequestBytes());
		radRequestPacket.addInfoAttributes(radServiceRequest.getInfoAttributes());
		
		PacketUtil attribObject = new PacketUtil() {

			@Override
			public IRadiusAttribute getRadiusAttribute(String attribIdStr,boolean addInfoAttrib, int iPacketType) {
				if(iPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radRequestPacket.getRadiusAttribute(attribIdStr,addInfoAttrib);
				}else if(iPacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || iPacketType == RadiusConstants.ACCESS_REJECT_MESSAGE){
					return radServiceResponse.getRadiusAttribute(true,attribIdStr);
				}
				return null;
			}

			@Override
			public Collection<IRadiusAttribute> getAttributes(int packetType,String attributeID, boolean bIncludeInfoAttributes) {
				if(packetType == RadiusConstants.ACCESS_REQUEST_MESSAGE || packetType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radRequestPacket.getRadiusAttributes(attributeID,bIncludeInfoAttributes);
				}else if(packetType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || packetType == RadiusConstants.ACCESS_REJECT_MESSAGE){
					return radServiceResponse.getRadiusAttributes(bIncludeInfoAttributes,attributeID);
				}
				return Collections.emptyList();
			}

			@Override
			public void addAttribute(int iPacketType,IRadiusAttribute attribute) {
				if(iPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radRequestPacket.addAttribute(attribute);
				}else if(iPacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || iPacketType == RadiusConstants.ACCESS_REJECT_MESSAGE){
					radServiceResponse.addAttribute(attribute);
				}
			}

			@Override
			public void removeAttribute(String policyName,int iPacketType,IRadiusAttribute radiusAttribute,boolean includeRadiusAttribute) throws PolicyInvalidException {
				if(iPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radRequestPacket.removeAttribute(radiusAttribute);
					radRequestPacket.removeInfoAttribute(radiusAttribute);
				}else if(iPacketType == RadiusConstants.ACCESS_REJECT_MESSAGE || iPacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
					radServiceResponse.removeAttribute(radiusAttribute, includeRadiusAttribute);
				}else{
					throw new PolicyInvalidException(policyName, " Policy configured incorrectly.");
				}

			}

			@Override
			public IRadiusAttribute getAttribute(String attributeId) {
				return radServiceRequest.getRadiusAttribute(attributeId);
			}

			@Override
			public int getDefaultPacketType() {
				return RadiusConstants.ACCESS_REQUEST_MESSAGE;
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		};
		
		int policyAction = applyUniversalPluginPolicies(attribObject,preAuthPolicyList);
		
		radRequestPacket.refreshPacketHeader();
		radRequestPacket.refreshInfoPacketHeader();
		radServiceRequest.setRequestBytes(radRequestPacket.getBytes());
		
		copyInfoAttributes(radServiceRequest, radRequestPacket);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "The action to be taken is :" + BaseUniversalPlugin.UniversalPluginActionConstants.getName(policyAction));

		if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.ACCEPT.value){
			radServiceResponse.setFurtherProcessingRequired(false);
			radServiceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
			radServiceResponse.setProcessingCompleted(true);
		}else if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.REJECT.value){
			radServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			radServiceResponse.setFurtherProcessingRequired(false);
			radServiceResponse.setProcessingCompleted(true);
		}else if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.DROP.value){
			radServiceResponse.markForDropRequest();
		}
		//checking for the EAP attributes
		if(radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null 
				&& radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE) != null){
			final byte [] EAPSUCCESS ={03,01,00,04};
			final byte [] EAPFAILURE ={04,01,00,04};

			IRadiusAttribute eapMessageAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EAP_MESSAGE);
			if(eapMessageAttribute != null) {
				if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.ACCEPT.value) {
					eapMessageAttribute.setValueBytes(EAPSUCCESS);
					radServiceResponse.setFurtherProcessingRequired(false);
					radServiceResponse.addAttribute(eapMessageAttribute);
				} else if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.REJECT.value) {
					eapMessageAttribute.setValueBytes(EAPFAILURE);
					radServiceResponse.setFurtherProcessingRequired(false);
					radServiceResponse.addAttribute(eapMessageAttribute);
				}
			}
		}

		LogManager.getLogger().info(MODULE, "RadServiceRequest:" + radServiceRequest.toString());
	}

	private void copyInfoAttributes(final RadServiceRequest radServiceRequest, final RadiusPacket radRequestPacket) {
		for(IRadiusAttribute attrib : radServiceRequest.getInfoAttributes()){
			radServiceRequest.removeAttribute(attrib, false);
		}
		
		for(IRadiusAttribute attrib : radRequestPacket.getRadiusInfoAttributes()){
			if(attrib.isVendorSpecific()){
				VendorSpecificAttribute vsa = (VendorSpecificAttribute) attrib;
				for(IRadiusAttribute radiusAttribute : (List<IRadiusAttribute>)vsa.getAttributes()){
					radServiceRequest.addInfoAttribute(radiusAttribute);
				}
			}else{
			radServiceRequest.addInfoAttribute(attrib);
		}
	}
	}

	@Override
	public void handlePostRequest(final RadServiceRequest radServiceRequest,
			final RadServiceResponse radServiceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		
		PacketUtil attribObject = new PacketUtil() {
			
			@Override
			public IRadiusAttribute getRadiusAttribute(String attribIdStr,boolean addInfoAttrib, int iPacketType) {
				if(iPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					return radServiceRequest.getRadiusAttribute(attribIdStr,true);
				}else if(iPacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || iPacketType == RadiusConstants.ACCESS_REJECT_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radServiceResponse.getRadiusAttribute(addInfoAttrib,attribIdStr);
				}
				return null;
			}

			@Override
			public Collection<IRadiusAttribute> getAttributes(int packetType,String attributeID, boolean bIncludeInfoAttributes) {
				if(packetType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					return radServiceRequest.getRadiusAttributes(attributeID,bIncludeInfoAttributes);
				}else if(packetType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || packetType == RadiusConstants.ACCESS_REJECT_MESSAGE || packetType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radServiceResponse.getRadiusAttributes(bIncludeInfoAttributes,attributeID);
				}
				return Collections.emptyList();
			}

			@Override
			public void addAttribute(int iPacketType,IRadiusAttribute attribute) {
				if(iPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					radServiceRequest.addInfoAttribute(attribute);
				}else if(iPacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || iPacketType == RadiusConstants.ACCESS_REJECT_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radServiceResponse.addAttribute(attribute);
				}
			}

			@Override
			public void removeAttribute(String policyName,int iPacketType,IRadiusAttribute radiusAttribute,boolean includeRadiusAttribute) throws PolicyInvalidException {
				if(iPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
					radServiceRequest.removeAttribute(radiusAttribute,includeRadiusAttribute);
				}else if(iPacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE || iPacketType == RadiusConstants.ACCESS_REJECT_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radServiceResponse.removeAttribute(radiusAttribute,includeRadiusAttribute);
				}else{
					throw new PolicyInvalidException(policyName, " Policy configured incorrectly.");
				}
				
			}

			@Override
			public IRadiusAttribute getAttribute(String attributeId) {
				return radServiceResponse.getRadiusAttribute(attributeId);
				
			}

			@Override
			public int getDefaultPacketType() {
				return RadiusConstants.ACCESS_ACCEPT_MESSAGE;
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		};
		
		int policyAction = applyUniversalPluginPolicies(attribObject,postAuthPolicyList);
		
		if(policyAction != BaseUniversalPlugin.UniversalPluginActionConstants.NO_ACTION.value && policyAction != BaseUniversalPlugin.UniversalPluginActionConstants.NONE.value){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
	    		LogManager.getLogger().info(MODULE,"The Action to be taken is " + BaseUniversalPlugin.UniversalPluginActionConstants.getName(policyAction) + ".");
	    	//for the action when it is accept
			if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.ACCEPT.value){
				radServiceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
				radServiceResponse.setFurtherProcessingRequired(true);
			}else if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.REJECT.value){
				radServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				radServiceResponse.setFurtherProcessingRequired(false);
			}else if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.DROP.value){
				radServiceResponse.markForDropRequest();
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "The request packet has been dropped");
				}
			}
		}
		LogManager.getLogger().info(MODULE, "RadServiceResponse:" + radServiceResponse.toString());
	}

	@Override
	public String getModuleName() {
		return MODULE;
	}



	@Override
	protected void setPostPolicyList(List<AAAUniversalPluginPolicyDetail> postAuthpolicyList) {		
		this.postAuthPolicyList = postAuthpolicyList;
	}

	@Override
	protected void setPrePolicyList(List<AAAUniversalPluginPolicyDetail> preAuthPolicyList) {
		this.preAuthPolicyList = preAuthPolicyList;
	}
}
