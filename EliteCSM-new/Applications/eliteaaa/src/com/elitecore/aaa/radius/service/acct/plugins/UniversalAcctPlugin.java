package com.elitecore.aaa.radius.service.acct.plugins;

import java.util.ArrayList;
import java.util.Collection;
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
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class UniversalAcctPlugin extends BaseUniversalPlugin implements RadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "UNIVERSAL-ACCT-PLUGIN";
	public static final String UNIVERSAL_ACCT_PLUGIN = "UNIVERSAL_ACCT_PLUGIN";
	private List<AAAUniversalPluginPolicyDetail> preAcctPolicyList;
	private List<AAAUniversalPluginPolicyDetail> postAcctPolicyList;
	private UniversalPluginConfigurationImpl data;
	
	public UniversalAcctPlugin(PluginContext pluginContext,PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);		
	}

	public UniversalAcctPlugin(PluginContext pluginContext, UniversalPluginConfigurationImpl pluginConf) {
		super(pluginContext, pluginConf.getPluginInfo());
		this.data = pluginConf;
		
		this.preAcctPolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>(data.getPrePolicyLists());
		this.postAcctPolicyList = new ArrayList<AAAUniversalPluginPolicyDetail>(data.getPostPolicyLists());
	}

	@Override
	public void init() throws InitializationFailedException {

		if(this.data == null){
			throw new InitializationFailedException("Universal Acct Plugin configuration is null.");
		}

		LogManager.getLogger().info(MODULE, "Initializing Universal Acct Plugin: " + data.getName());
		try{
			
			Collectionz.filter(preAcctPolicyList, AAAUniversalPluginPolicyDetail.POLICY_ENABLED);
			
			Collectionz.filter(postAcctPolicyList, AAAUniversalPluginPolicyDetail.POLICY_ENABLED);
			
			LogManager.getLogger().info(MODULE, "Universal Acct Plugin Initialized successfully: " +data.toString());
		}catch(Exception ex){
			LogManager.getLogger().trace(MODULE, "Error while loading the configuration");
		}
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
			public IRadiusAttribute getRadiusAttribute(String attribIdStr,boolean addInfoAttrib,int iPacketType) {
				if(iPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radRequestPacket.getRadiusAttribute(attribIdStr,addInfoAttrib);
				}else if(iPacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
					return radServiceResponse.getRadiusAttribute(attribIdStr);
				}
				return null;
			}

			@Override
			public Collection<IRadiusAttribute> getAttributes(int packetType,String attributeID, boolean bIncludeInfoAttributes) {
				if(packetType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || packetType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radRequestPacket.getRadiusAttributes(attributeID,bIncludeInfoAttributes);
				}else if(packetType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
					return radServiceResponse.getRadiusAttributes(bIncludeInfoAttributes,attributeID);
				}
				return Collections.emptyList();
			}
			@Override
			public void addAttribute(int iPacketType,IRadiusAttribute attribute) {
				if(iPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radRequestPacket.addAttribute(attribute);
				}else if(iPacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
					radServiceResponse.addAttribute(attribute);
				}else{
					LogManager.getLogger().warn(MODULE, "Policy configured incorrectly. Skipping this policy");
				}
			}

			@Override
			public void removeAttribute(String policyName, int iPacketType,IRadiusAttribute radiusAttribute,boolean includeRadiusAttribute) throws PolicyInvalidException {
				if(iPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radRequestPacket.removeAttribute(radiusAttribute);
					radRequestPacket.removeInfoAttribute(radiusAttribute);
				}else if(iPacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
					radServiceResponse.removeAttribute(radiusAttribute, includeRadiusAttribute);
				}else{
					throw new PolicyInvalidException(policyName, " Policy configured incorrectly.");
				}
				
			}

			@Override
			public IRadiusAttribute getAttribute(String attributeId) {
				return radRequestPacket.getRadiusAttribute(attributeId);
			}

			@Override
			public int getDefaultPacketType() {
				return RadiusConstants.ACCOUNTING_REQUEST_MESSAGE;
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		};
		
		int policyAction = applyUniversalPluginPolicies(attribObject,preAcctPolicyList);
		
		
		//now here after applying all the policies copying the attributes from the request packet to radServiceRequest
		radRequestPacket.refreshPacketHeader();
		radRequestPacket.refreshInfoPacketHeader();
		radServiceRequest.setRequestBytes(radRequestPacket.getBytes());
		
		copyInfoAttributes(radServiceRequest, radRequestPacket);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "The action to be taken is :" + BaseUniversalPlugin.UniversalPluginActionConstants.getName(policyAction));
		}
		if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.ACCEPT.value){
			radServiceResponse.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
			radServiceResponse.setFurtherProcessingRequired(false);
		}else if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.DROP.value){
			radServiceResponse.markForDropRequest();
			radServiceResponse.setFurtherProcessingRequired(false);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "The request packet has been dropped");
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

		PacketUtil packetUtil = new PacketUtil() {
			
			@Override
			public IRadiusAttribute getRadiusAttribute(String attribIdStr,boolean addInfoAttrib,int iPacketType) {
				if(iPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					return radServiceRequest.getRadiusAttribute(attribIdStr,addInfoAttrib);
				}else if(iPacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radServiceResponse.getRadiusAttribute(addInfoAttrib,attribIdStr);
				}
				return null;
			}

			@Override
			public Collection<IRadiusAttribute> getAttributes(int packetType,String attributeID, boolean includeInfoAttributes) {
				if(packetType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					return radServiceRequest.getRadiusAttributes(attributeID, includeInfoAttributes);
				}else if(packetType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE || packetType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					return radServiceResponse.getRadiusAttributes(includeInfoAttributes, attributeID);
				}
				return Collections.emptyList();
			}

			@Override
			public void addAttribute(int iPacketType, IRadiusAttribute attribute) {
				if(iPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
				radServiceRequest.addInfoAttribute(attribute);
				}else if(iPacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radServiceResponse.addAttribute(attribute);
				}
			}

			@Override
			public void removeAttribute(String policyName,int iPacketType,IRadiusAttribute radiusAttribute,boolean includeRadiusAttribute) throws PolicyInvalidException {
				
				if(iPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
					radServiceRequest.removeAttribute(radiusAttribute,includeRadiusAttribute);
				}else if(iPacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE || iPacketType == BaseUniversalPlugin.DEFAULT_PACKET_TYPE){
					radServiceResponse.removeAttribute(radiusAttribute, includeRadiusAttribute);
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
				return RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE;
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		};
		int policyAction = applyUniversalPluginPolicies(packetUtil,postAcctPolicyList);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(getModuleName(), "Action to be taken is :" + BaseUniversalPlugin.UniversalPluginActionConstants.getName(policyAction));
		}
		if(policyAction == BaseUniversalPlugin.UniversalPluginActionConstants.DROP.value){
			radServiceResponse.markForDropRequest();
			radServiceResponse.setFurtherProcessingRequired(false);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "The response packet has been dropped");
			}
		}
		
		LogManager.getLogger().info(MODULE, "RadServiceResponse:" + radServiceResponse.toString());
	}

	@Override
	public String getModuleName() {
		return MODULE;
	}


	@Override
	protected void setPostPolicyList(List<AAAUniversalPluginPolicyDetail> postAcctPolicyList) {		
		this.postAcctPolicyList = postAcctPolicyList;
	}

	@Override
	protected void setPrePolicyList(List<AAAUniversalPluginPolicyDetail> preAcctPolicyList) {
		this.preAcctPolicyList = preAcctPolicyList;
	}

}
