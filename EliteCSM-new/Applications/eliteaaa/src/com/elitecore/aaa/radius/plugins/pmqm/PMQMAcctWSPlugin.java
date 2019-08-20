package com.elitecore.aaa.radius.plugins.pmqm;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.aaa.radius.plugins.core.RadPlugin;
import com.elitecore.aaa.radius.plugins.pmqm.conf.PMQMAcctWSPluginConfiguration;
import com.elitecore.aaa.radius.plugins.pmqm.conf.impl.PMQMAcctWSPluginConfigurationImpl;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.webservices.ssscsce.SSSC_SCE_Interface;
import com.elitecore.webservices.ssscsce.SSSC_SCE_InterfaceServiceLocator;

public class PMQMAcctWSPlugin extends BasePlugin implements RadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "PMQM_ACCT_PLUGIN";
	//private com.elitecore.aaa.radius.service.acct.plugins.pmqm.SSSC_SCE_InterfaceServiceLocator locator;
	//private com.elitecore.aaa.radius.service.acct.plugins.pmqm.SSSC_SCE_Interface sceInterface;
	private SSSC_SCE_InterfaceServiceLocator locator;
	private SSSC_SCE_Interface sceInterface;
	private PMQMAcctWSPluginConfiguration pluginConfiguration;
	
	public PMQMAcctWSPlugin(PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}

	@Override
	public void init() throws InitializationFailedException {
		pluginConfiguration = (PMQMAcctWSPluginConfiguration) getPluginConfiguration();
		if(pluginConfiguration == null)
			throw new InitializationFailedException("Plugin Configuration not found");
		try{
			String webServerURL = (String)pluginConfiguration.getConfigValue(PMQMAcctWSPluginConfiguration.URL);
			/*String webServiceUserName = (String)((HashMap)PMQMPluginDetail.get(API_DETAIL)).get("username");
		String webServicePassword = (String)((HashMap)PMQMPluginDetail.get(API_DETAIL)).get("password");*/
			locator = new SSSC_SCE_InterfaceServiceLocator();
			locator.setSSSCSCEServiceEndpointAddress(webServerURL);
			sceInterface = locator.getSSSCSCEService();
		}catch (Exception e) {
			throw new InitializationFailedException(e);
		}
	}

	//Same work is done in both pre and post calls
	@SuppressWarnings("unchecked")
	@Override
	public void handlePreRequest(RadServiceRequest radServiceRequest, RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
		if(radiusAttribute != null){			
			int iStatusType = radiusAttribute.getIntValue();
			if(iStatusType == RadiusAttributeValuesConstants.STOP){
			//	taskScheduler.schedule(new EliteInternalTask("PMQMTask"){
			//		public void run() {
						try{
							sceInterface.processLogoutEvent(formRequestParameters((ArrayList<Object>)(pluginConfiguration.getConfigValue(PMQMAcctWSPluginConfiguration.ACCT_FIELD_MAPPING)), radServiceRequest));
						}catch (Exception e) {
							e.printStackTrace();
						}		           
			//		}
			//	});
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handlePostRequest(RadServiceRequest radServiceRequest, RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
		if(radiusAttribute != null){			
			int iStatusType = radiusAttribute.getIntValue();
			if(iStatusType == RadiusAttributeValuesConstants.STOP){
			//	taskScheduler.schedule(new EliteInternalTask("PMQMTask"){
			//		public void run() {
						try{
							sceInterface.processLogoutEvent(formRequestParameters((ArrayList<Object>)(pluginConfiguration.getConfigValue(PMQMAcctWSPluginConfiguration.ACCT_FIELD_MAPPING)), radServiceRequest));
						}catch (Exception e) {
							e.printStackTrace();
						}		           
			//		}
			//	});
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private HashMap<Object, Object> formRequestParameters(ArrayList<Object> fieldMapping, RadServiceRequest radiusPacket){

		HashMap<Object, Object> requestParameters = new HashMap<Object, Object>();
		StringBuilder sb = new StringBuilder("\n");
		String strPMField = null;
		String strDefaultValue = null;
		String strAttributeValue = null;
		Map<Object, Object> radiusPMValueMapping = null;

		if(fieldMapping != null){
			int iMainAttributeId = 0;
			int[] iAttributeIds = null;
			long lVendorId = 0;
			int listSize = 0 ;
			int valueMappingListSize = 0;
			ArrayList<HashMap<Object,Object>> multiAttrIdList = new ArrayList<HashMap<Object,Object>>();
			ArrayList<Map<Object, Object>> valueMappingList = new ArrayList<Map<Object, Object>>();
			final int fieldMappingSize = fieldMapping.size();
			for(int i=0; i<fieldMappingSize; i++){
				strAttributeValue = null;
				strDefaultValue = null;
				radiusPMValueMapping = null;
				iMainAttributeId = 0;
				iAttributeIds = null;
				lVendorId = 0;
				HashMap<Object, Object> authAttributeMap = (HashMap<Object, Object>)fieldMapping.get(i);
				multiAttrIdList = (ArrayList<HashMap<Object,Object>>)authAttributeMap.get(PMQMAcctWSPluginConfiguration.MULTIPLE_ATTRIBUTE_ID);
				valueMappingList = (ArrayList<Map<Object, Object>>)authAttributeMap.get(PMQMAcctWSPluginConfiguration.VALUE_MAPPING);
				listSize = multiAttrIdList.size();
				valueMappingListSize = valueMappingList.size();

				for(int cnt=0;cnt<listSize;cnt++){
					HashMap<Object, Object> attrMap = multiAttrIdList.get(cnt);
					iAttributeIds = (int[])attrMap.get(PMQMAcctWSPluginConfiguration.ATTRIBUTE_ID);				
					iMainAttributeId = iAttributeIds[0];
					try{
						lVendorId = Long.parseLong(attrMap.get(PMQMAcctWSPluginConfiguration.VENDOR_ID).toString());
						if(lVendorId==0 && radiusPacket.getRadiusAttribute(iMainAttributeId)!=null){
							if(valueMappingListSize > 0)
								radiusPMValueMapping = (HashMap<Object, Object>)valueMappingList.get(cnt);
							break;
						}else if(radiusPacket.getRadiusAttribute(lVendorId, iAttributeIds)!= null){
							if(valueMappingListSize > 0)
								radiusPMValueMapping = (HashMap<Object, Object>)valueMappingList.get(cnt);
							break;
						}
					}catch(NumberFormatException nfe){
						LogManager.getLogger().trace(MODULE, nfe);
					}catch(Exception e){
						LogManager.getLogger().trace(MODULE, e);
					}
				}

				strPMField = (String)authAttributeMap.get(PMQMAcctWSPluginConfiguration.PM_FIELD);
				strDefaultValue = (String)authAttributeMap.get(PMQMAcctWSPluginConfiguration.DEFAULT_VALUE);

				/*				if(getLogger().isLogLevel(LogLevel.TRACE)){
					getLogger().trace(MODULE, "value-mapping: " + radiusPMValueMapping);
				}*/

				if(lVendorId == 0){
					BaseRadiusAttribute radiusAttribute = (BaseRadiusAttribute)radiusPacket.getRadiusAttribute(iMainAttributeId);
					if(radiusAttribute != null){
						if(iAttributeIds != null){
							radiusAttribute = getFinalAttribute(radiusAttribute, iAttributeIds);
							if(radiusAttribute != null){
								if(radiusPMValueMapping != null && !radiusPMValueMapping.isEmpty()){
									strAttributeValue = String.valueOf(radiusAttribute.getIntValue());
									if(radiusPMValueMapping.containsKey(strAttributeValue)){
										strAttributeValue = (String)radiusPMValueMapping.get(strAttributeValue);
									}else{
										strAttributeValue = (String)strDefaultValue;
									}
								}else
									strAttributeValue = radiusAttribute.getStringValue();
							}else{
								if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(lVendorId,iAttributeIds)+" attribute not found in request packet, use default value: " + strDefaultValue);
								strAttributeValue = (String)strDefaultValue;
							}
						}else{
							if(iMainAttributeId == RadiusAttributeConstants.EVENT_TIMESTAMP){
								strAttributeValue = String.valueOf(radiusAttribute.getLongValue() * 1000);
							}else if(radiusPMValueMapping != null && !radiusPMValueMapping.isEmpty()){
								strAttributeValue = String.valueOf(radiusAttribute.getIntValue());
								if(radiusPMValueMapping.containsKey(strAttributeValue)){
									strAttributeValue = (String)radiusPMValueMapping.get(strAttributeValue);
								}else{
									strAttributeValue = (String)strDefaultValue;
								}
							}else{
								strAttributeValue = radiusAttribute.getStringValue();
							}
						}
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(iMainAttributeId)+" attribute is not found in request packet, use default value : "+strDefaultValue);
						if(iMainAttributeId == RadiusAttributeConstants.EVENT_TIMESTAMP){
							strAttributeValue = String.valueOf(System.currentTimeMillis());
						}else{
							strAttributeValue = (String)strDefaultValue;
						}
					}
				}else if(lVendorId == RadiusConstants.ELITECORE_VENDOR_ID){
					if(radiusPacket.getRadiusAttribute(lVendorId, iMainAttributeId)!= null){
						strAttributeValue = null;
						ArrayList<IRadiusAttribute> vendorSpecAvpList  = (ArrayList<IRadiusAttribute>)radiusPacket.getRadiusAttributes(lVendorId, iMainAttributeId);
						final int vsaListSize = vendorSpecAvpList.size();
						for(int j=0; j<vsaListSize; j++){
							IRadiusAttribute radiusAttribute = (IRadiusAttribute)vendorSpecAvpList.get(j);
							if(radiusAttribute.getType() == RadiusAttributeConstants.ELITE_RESOURCE_MANAGER_AVPAIR && radiusAttribute.getStringValue().contains(strPMField)){
								StringTokenizer st = new StringTokenizer(radiusAttribute.getStringValue(),"=");
								if(st.hasMoreTokens()){
									st.nextToken();
									strAttributeValue = st.nextToken();
								} 
								break;
							}else{
								strAttributeValue = radiusAttribute.getStringValue();
							}
						}
					}else{					
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(lVendorId,iMainAttributeId)+" attribute is not found in request packet, use default value : "+ strDefaultValue);
						strAttributeValue = strDefaultValue;
					}
					/*if(strAttributeValue == null)
						continue;
					 */				}else {
						 BaseRadiusAttribute radiusAttribute = null;
						 if(radiusPacket.getRadiusAttributes(lVendorId, iMainAttributeId)!= null){
							 ArrayList<IRadiusAttribute> vendorAttributes = (ArrayList<IRadiusAttribute>)radiusPacket.getRadiusAttributes(lVendorId, iMainAttributeId);
							 if(vendorAttributes != null){
								 final int vendorAttributeSize = vendorAttributes.size();
								 for(int j=0; j<vendorAttributeSize; j++){
									 radiusAttribute = (BaseRadiusAttribute)vendorAttributes.get(j);
								 }
							 }
							 if(radiusAttribute != null){
								 if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									 LogManager.getLogger().trace(MODULE, "Radius attribute from packet: " + radiusAttribute);
								 if(iAttributeIds != null){
									 radiusAttribute = getFinalAttribute(radiusAttribute, iAttributeIds);
									 if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
										 LogManager.getLogger().trace(MODULE, "Final radius attribute from packet: " + radiusAttribute);
									 if(radiusAttribute != null){
										 if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
											 LogManager.getLogger().trace(MODULE, "Value of attribute from packet: " + strAttributeValue);
										 if(radiusPMValueMapping != null && !radiusPMValueMapping.isEmpty()){
											 strAttributeValue = String.valueOf(radiusAttribute.getIntValue());
											 if(radiusPMValueMapping.containsKey(strAttributeValue)){
												 strAttributeValue = (String)radiusPMValueMapping.get(strAttributeValue);
											 }else{
												 strAttributeValue = (String)strDefaultValue;
											 }
										 }else
											 strAttributeValue = radiusAttribute.getStringValue();
									 }else{
										 if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
											 LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(lVendorId,iAttributeIds)+" attribute is not found in request packet, use default value : "+strDefaultValue);
										 strAttributeValue = (String)strDefaultValue;
									 }
								 }else{								
									 if(radiusPMValueMapping != null && !radiusPMValueMapping.isEmpty()){
										 strAttributeValue = String.valueOf(radiusAttribute.getIntValue());
										 if(radiusPMValueMapping.containsKey(strAttributeValue)){
											 strAttributeValue = (String)radiusPMValueMapping.get(strAttributeValue);
										 }else{
											 strAttributeValue = (String)strDefaultValue;
										 }
									 }else
										 strAttributeValue = radiusAttribute.getStringValue();
								 }
							 }else{
								 if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									 LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(lVendorId,iMainAttributeId)+" attribute is not found in request packet, use default value : "+strDefaultValue);
								 strAttributeValue = (String)strDefaultValue;
							 }
						 }else{
							 if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								 LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(lVendorId,iMainAttributeId)+" attribute is not found in request packet, use default value : "+strDefaultValue);
							 strAttributeValue = (String)strDefaultValue;
						 }
					 }
				sb.append("   " + strPMField + " = " + strAttributeValue + "\n");
				requestParameters.put((String)strPMField, strAttributeValue);
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE,sb.toString());
		return requestParameters;
	}

	private BaseRadiusAttribute getFinalAttribute(BaseRadiusAttribute radiusAttribute, int[] attributeIds) {
		for(int i=1; i<attributeIds.length; i++){
			if(radiusAttribute.getAttribute(attributeIds[i]) != null){
				radiusAttribute = (BaseRadiusAttribute)radiusAttribute.getAttribute(attributeIds[i]);
			}else{
				radiusAttribute = null;
				break;
			}
		}
		return radiusAttribute;
	}

	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getPluginName());
		cacheDetail.setResultCode(CacheConstants.SUCCESS);
		cacheDetail.setSource("--");
		try {
			PMQMAcctWSPluginConfigurationImpl config = (PMQMAcctWSPluginConfigurationImpl)getPluginConfiguration();
			if(config == null){
				cacheDetail.setResultCode(CacheConstants.FAIL);
				cacheDetail.setDescription("Failed, reason: PM_QM Plugin configuration is null");
			}else{
				config.readConfiguration();
				cacheDetail.setSource(config.getConfigFileName());
				this.pluginConfiguration = config;
			}

		} catch (LoadConfigurationException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed, reason: " + e.getMessage());
		}				
		return cacheDetail;

	}

}
