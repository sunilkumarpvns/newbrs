package com.elitecore.aaa.core.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.ParamDetails;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public abstract class BaseUniversalPlugin extends BasePlugin {

	public static final String CHECKANDREJECTEXPRESSION = "CHECKANDREJECTEXPRESSION";
	protected static final int DEFAULT_PACKET_TYPE = 0;
	private static final String CHECK_ITEM = "C";
	private static final String REJECT_ITEM = "J";
	private static final String REPLY_ITEM = "R";
	private static final String FILTER_ITEM = "F";
	private static final String UPDATE_ITEM = "U";
	private static final String VALUE_REPLACE_ITEM = "V";
	private static final String ASSIGN_ITEM = "A";	

	public BaseUniversalPlugin(PluginContext pluginContext,PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);	
	}

	public abstract class PacketUtil implements ValueProvider{

		public abstract IRadiusAttribute getRadiusAttribute(String attrib, boolean addInfoAttrib,int packetType);

		public abstract void addAttribute(int iPacketType, IRadiusAttribute radiusAttribute);

		public abstract void removeAttribute(String policyName, int iPacketType, IRadiusAttribute radiusAttribute, boolean bRadiusAttribute) throws PolicyInvalidException;

		public abstract IRadiusAttribute getAttribute(String attributeId);

		public abstract int getDefaultPacketType();

		//this method is introduced for the support of multiple attributes check item in expression library
		public abstract Collection<IRadiusAttribute> getAttributes(int packetType, String attributeID, boolean includeInfoAttributes);

		
		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {

			StringHelper helper = new StringHelper(identifier);

			IRadiusAttribute radiusAttribute = getRadiusAttribute(helper.getStringValue(), true, getDefaultPacketType());
			if(radiusAttribute == null)
				return null;
			
			if(helper.isAVPairString()){
				radiusAttribute = getRadiusAttribute(helper.getStringValue(), true, getDefaultPacketType());
				if(radiusAttribute == null)
					return null;
				if(!(radiusAttribute.isAvpair())){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(getModuleName(), "Configured attribute : "+ helper.getStringValue() + " is avPair type ,while received attribute :" + radiusAttribute + " is not avPair type.");
					return null;
	}
				return radiusAttribute.getKeyValue(helper.getKey());
			}
			return radiusAttribute.getStringValue();
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			
			StringHelper helper = new StringHelper(identifier);
			
			IRadiusAttribute radiusAttribute = getRadiusAttribute(helper.getStringValue(), true, getDefaultPacketType());
			if(radiusAttribute == null)
				return 0;
			
			if(helper.isAVPairString()){
				radiusAttribute = getRadiusAttribute(helper.getStringValue(), true, getDefaultPacketType());
				if(radiusAttribute == null)
					return 0;
				if(!(radiusAttribute.isAvpair())){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(getModuleName(), "Configured attribute : "+ helper.getStringValue() +" is avPair type ,while received attribute :" + radiusAttribute + "is not avPair type.");
					return 0;
				}
				return radiusAttribute.getLongValue();			
			}
			return radiusAttribute.getLongValue();
		}

		@Override
		public List<String> getStringValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			StringHelper stringHelper = new StringHelper(identifier);
			List<String> stringValues = new ArrayList<String>();

			Collection<IRadiusAttribute> allAttributes = getAttributes(getDefaultPacketType(), stringHelper.getStringValue(), true);
			if(allAttributes.size() == 0){
				throw new MissingIdentifierException("Configured attribute not found: " + identifier);
			}
			
			if(stringHelper.isAVPairString()){
				for(IRadiusAttribute radiusAttribute : allAttributes){
					String keyValue = radiusAttribute.getKeyValue(stringHelper.getKey());
					if(keyValue != null){
						stringValues.add(keyValue);
					}
				}
			}else{
				for(IRadiusAttribute radiusAttribute : allAttributes){
					stringValues.add(radiusAttribute.getStringValue(false));
				}
			}
			return stringValues;
		}

		@Override
		public List<Long> getLongValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			StringHelper helper = new StringHelper(identifier);
			List<Long> longValues = new ArrayList<Long>();
			
			Collection<IRadiusAttribute> allAttribute = getAttributes(getDefaultPacketType(), helper.getStringValue(), true);
			if(allAttribute.size() == 0)
				throw new MissingIdentifierException("Configure Attribute : "+ identifier + " is not found.");

			for (IRadiusAttribute attributelist : allAttribute) {
				longValues.add(attributelist.getLongValue());
			}
			return longValues;
		}
	}

	public abstract String getModuleName();

	public class PolicyInvalidException extends Exception{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PolicyInvalidException(String policyName, String message){  
			super(policyName + message);
		}

	}
	public int applyUniversalPluginPolicies(PacketUtil packeUtil,List<AAAUniversalPluginPolicyDetail> policyList) {

		int iPolicyAction = UniversalPluginActionConstants.NO_ACTION.value;

		for(AAAUniversalPluginPolicyDetail universalPluginPolicy : policyList){

			try{
				Integer policyActionInteger = universalPluginPolicy.getPolicyAction();
								
					if(policyActionInteger != null)
						iPolicyAction = policyActionInteger;
					if(iPolicyAction != UniversalPluginActionConstants.NO_ACTION.value){

						List<? extends ParamDetails> parameterList = universalPluginPolicy.getParameterDetailsForPlugin();
						
						if(applyUniversalPluginPolicy(packeUtil, universalPluginPolicy)){
							if(iPolicyAction != UniversalPluginActionConstants.DROP.value){
								LogManager.getLogger().debug(getModuleName(), universalPluginPolicy.getName()+ "- Adding,Filtering,Update or Value replacement of Attribute into Radius Packet");
								addFilterUpdatePacket(universalPluginPolicy.getName(),packeUtil ,parameterList);
							}
							LogManager.getLogger().debug(getModuleName(),"Policy "+universalPluginPolicy.getName()+" applied successfully.");
							if(iPolicyAction != UniversalPluginActionConstants.NONE.value && iPolicyAction != UniversalPluginActionConstants.NO_ACTION.value){
								break;
							}
						}else{
							iPolicyAction = UniversalPluginActionConstants.NO_ACTION.value;
							LogManager.getLogger().debug(getModuleName(),universalPluginPolicy.getName().toString() + " not satisfied.");
						}
					}else{
						iPolicyAction = UniversalPluginActionConstants.NO_ACTION.value;
						LogManager.getLogger().warn(getModuleName(), universalPluginPolicy.getName() + "- Policy action name configured incorrectly. Skipping this policy");
					}
				
			}catch(Exception ex){
				iPolicyAction = UniversalPluginActionConstants.NO_ACTION.value;
				LogManager.getLogger().warn(getModuleName(), ex.getMessage());
			}
		}
		return iPolicyAction;
	}


	//adding filtering or Updating the packet
	private void addFilterUpdatePacket(String policyName,PacketUtil packetUtil, List<? extends ParamDetails> parameterInfoList) throws PolicyInvalidException, Exception {
		RadiusParamDetails paramDetails;
		for(int i=0; i<parameterInfoList.size(); i++){
			paramDetails =(RadiusParamDetails) parameterInfoList.get(i);

			int ipacketType =  paramDetails.getPacket_type();
			String strAttrValue = paramDetails.getAttribute_value();
			String strParameterUsage = paramDetails.getParameter_usage();
			String strParameterActiver = paramDetails.getActive();
			if(strParameterActiver.equalsIgnoreCase("YES")){
				if(paramDetails.getAttr_id() != null){
					String attributIdStr = paramDetails.getAttr_id();
					StringHelper helper = new StringHelper(attributIdStr);
					if(strParameterUsage != null && strParameterUsage.trim().length() != 0 
							&& strAttrValue != null && strAttrValue.trim().length() != 0){
						if(strParameterUsage.equalsIgnoreCase(REPLY_ITEM)){
							if(addReplyAttribute(helper, packetUtil, ipacketType, policyName,paramDetails)){
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
									LogManager.getLogger().debug(getModuleName(), policyName + "- Radius Attribute " + Dictionary.getInstance().getAttributeName(helper.getStringValue()) + " has been added");
								}
							}
						}else if(strParameterUsage.equalsIgnoreCase(FILTER_ITEM)){
							packetUtil.removeAttribute(policyName,ipacketType,packetUtil.getRadiusAttribute(helper.getStringValue(),true,ipacketType),true);
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
								LogManager.getLogger().debug(getModuleName(), policyName + "- Radius Attribute " + Dictionary.getInstance().getAttributeName(helper.getStringValue()) + " has been removed");
							}
						}else if(strParameterUsage.equalsIgnoreCase(UPDATE_ITEM)){
								if(updateRadiusAttributeValue(attributIdStr,packetUtil,ipacketType,policyName,paramDetails)) 
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
									LogManager.getLogger().debug(getModuleName(), policyName + "- Radius Attribute " + Dictionary.getInstance().getAttributeName(helper.getStringValue()) + " has been updated");
								}
						}else if(strParameterUsage.equalsIgnoreCase(VALUE_REPLACE_ITEM)){
							if(replaceRadiusAttributeValue(attributIdStr,packetUtil,ipacketType,policyName,paramDetails))
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
									LogManager.getLogger().debug(getModuleName(), policyName + "- Radius Attribute " + Dictionary.getInstance().getAttributeName(helper.getStringValue()) + " has been replaced");
								}
						}else if(strParameterUsage.equalsIgnoreCase(ASSIGN_ITEM)){
							if(dynamicallyAssignAttributeValue(attributIdStr,packetUtil,ipacketType,policyName, paramDetails))
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
									LogManager.getLogger().debug(getModuleName(), policyName + "- Radius Atribute " + Dictionary.getInstance().getAttributeName(helper.getStringValue()) + " assigned new value");
								}
						}
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(getModuleName(), policyName + "- Either parameter usage or Attribute value is blank.");
						}	
					}
				}
			}
		}
	}


	private boolean addReplyAttribute(StringHelper attributeIDStringHelper, PacketUtil packetUtil, int packetType,String policyName ,RadiusParamDetails paramdetails){
		
		String attributeValueString = null;
		IRadiusAttribute radiusAttribute =  Dictionary.getInstance().getKnownAttribute(attributeIDStringHelper.getStringValue());
		if(radiusAttribute == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModuleName(), policyName + "- Attribute with attribute id: " + attributeIDStringHelper.getStringValue() + " not found in dictionary, so will not be added.");
			}
			return false;
		}

		try {
			attributeValueString = paramdetails.getExpression().getStringValue(packetUtil);
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModuleName(), "Error in executing expression: " + paramdetails.getAttribute_value() + " for Reply item. Reason: "+e.getMessage());
			}
			return false;
		}

		if(attributeValueString == null || attributeValueString.equals("")){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), policyName + "- Invalid value: " + attributeValueString + " configured for Reply item. Attribute: " + attributeIDStringHelper.getStringValue() + " will not be added.");
			}
			return false;
		}
		
		if(attributeIDStringHelper.isAVPairString()){
			radiusAttribute.setStringValue(attributeIDStringHelper.getKey() + "=" + attributeValueString);
		}else{
			radiusAttribute.setStringValue(attributeValueString);
		}
		packetUtil.addAttribute(packetType,radiusAttribute);
		return true;
	}

	private boolean dynamicallyAssignAttributeValue(String attributeIdString,PacketUtil packetUtil,int iPacketType,String policyName,RadiusParamDetails paramDetails) {
		
		StringHelper attributeIDHelper = new StringHelper(attributeIdString);
		String attributeValueString = null;
		if(attributeIDHelper.isStaticString()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModuleName(), policyName + "- Static string in attribute id not supported. Dynamic Assign to Attribute: " + attributeIdString + "will not take place. Remove \"\"");
			}
			return false;
		}

		try {
			attributeValueString = paramDetails.getExpression().getStringValue(packetUtil);
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModuleName(), "Error in executing expression: " + paramDetails.getAttribute_value() + " for Dynamic Assign. Reason: "+e.getMessage());
			}
			return false;
		}

		if(attributeValueString == null || attributeValueString.equals("")){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), policyName + "- Configured Attribute Value for Dynamic Assignment: " + attributeValueString + " not found. Dynamic assign to Attribute: " + attributeIdString + " will not take place.");
				return false;
			}
		}

		String attributeID = attributeIDHelper.getStringValue();

		//fetching the attribute to which the value is to be assigned
		IRadiusAttribute toAttribute = packetUtil.getRadiusAttribute(attributeID, true,iPacketType);
		if(toAttribute == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), policyName + "- Attribute for Dynamic Assignment: " + attributeID + " not found will be added.");
			}
			toAttribute = Dictionary.getInstance().getKnownAttribute(attributeID);
			if(toAttribute == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(getModuleName(), policyName + "- Unknown Attribute ID for Dynamic Assignment: " + attributeID + ". Check for availability of Dictionary.");
				}
				return false;
			}
			packetUtil.addAttribute(iPacketType, toAttribute);
		}

		if(attributeIDHelper.isAVPairString()){
			String key = attributeIDHelper.getKey();
			if(!toAttribute.isAvpair()){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(getModuleName(), policyName + "- Configured Attribute ID for Dynamic Assignment: " + attributeIdString + " is invalid as attribute: " + attributeID + " not of AVPair type. Check Dictionary.");
				}
				return false;
			}

			toAttribute.setStringValue(key + "=" + attributeValueString);
		}else{
			toAttribute.setStringValue(attributeValueString);
		}
		return true;
	}

	/*
	 * VALUE REPLACE PARAMETER - This method will replace the value of the attribute only if the attribute is present
	 * If the attribute is not found then the attribute will not be added.
	 */
	private boolean replaceRadiusAttributeValue(String attributeIdStr,PacketUtil packetUtil, int iPacketType, String policyName,RadiusParamDetails paramDetails) {

		StringHelper attributeIDHelper = new StringHelper(attributeIdStr);
		
		String attributeValueString = null;
		if(attributeIDHelper.isStaticString()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModuleName(), policyName + "- Static string in attribute id not supported. Value Replace to Attribute: " + attributeIdStr + "will not take place. Remove \"\"");
			}
			return false;
		}

		try {
			attributeValueString = paramDetails.getExpression().getStringValue(packetUtil);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModuleName(), "Error in executing expression: " + paramDetails.getAttribute_value() + " for Value Replace. Reason: "+e.getMessage());
			}
			return false;
		}

		if(attributeValueString == null || attributeValueString.equals("")){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), policyName + "- Configured Attribute Value for Value Replace: " + attributeValueString + " not found in " + UniversalPacketTypeConstants.get(packetUtil.getDefaultPacketType()).name() + ". Value replace to Attribute: " + attributeIdStr + " will not take place.");
				return false;
			}
		}

		//fetching the attribute to which the replace operation is to be done
		String attributeID = attributeIDHelper.getStringValue();
		IRadiusAttribute toAttribute = packetUtil.getRadiusAttribute(attributeID, true,iPacketType);
		if(toAttribute == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModuleName(), policyName + "- Configured Attribute ID for Value Replace: " + attributeID + " not found in " + UniversalPacketTypeConstants.get(iPacketType).name() + ". Value replace to Attribute: " + attributeIdStr + " will not take place.");
			}
			return false;
		}

		if(attributeIDHelper.isAVPairString()){
			String key = attributeIDHelper.getKey();
			if(!toAttribute.isAvpair()){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(getModuleName(), policyName + "- Configured Attribute ID for Value Replace: " + attributeIdStr + " is invalid as attribute: " + attributeID + " is not of AVPair type. Check dictionary.");
				}
				return false;
			}
				toAttribute.setStringValue(key + "=" +attributeValueString);
		}else{
			toAttribute.setStringValue(attributeValueString);
		}
		return true; 
	}

	/*
	 * This method first tries to fetch the attribute from packet based on the configured attribute id.
	 * Returns without performing any action if the attribute is not found.
	 * If the attribute is found then 
	 * 		(a) - It tries to check if the value provided for update is a static value i.e. enclosed within "".
	 *            If YES then it checks whether the value ends with * if YES then the value of attribute is appended behind the configured string, else
	 *            the configured value is appended to the attribute.
	 *      (b) - If the value is not enclosed within "" then it considers it as an attribute ID token and tries to fetch corresponding attribute from packet
	 *            If the attribute is found in packet and the string ends with *, it fetches value of the configured attribute from it and appends with the other attribute value.
	 *            CONSIDER an example:
	 *            Suppose, attributIdStr = 0:31 and value of attribute is 0A-0B-0C-0D
	 *                     strAttrValue = 0:1* and value of attribute is eliteaaa
	 *                     then the final value of 0:31 will become eliteaaa0A-0B-0C-0D
	 *            Suppose, attributIdStr = 0:31 and value of attribute is 0A-0B-0C-0D
	 *                     strAttrValue = 0:1 and value of attribute is eliteaaa
	 *                     then the final value of 0:31 will become 0A-0B-0C-0Deliteaaa
	 * 
	 */
	private boolean updateRadiusAttributeValue(String attributIdStr,PacketUtil packetUtil, int iPacketType, String policyName,RadiusParamDetails paramDetails) {
		
		boolean isPacketModified = false;
		String attributeValueString = null;
		StringHelper attributeIDHelper = new StringHelper(attributIdStr);
		if(attributeIDHelper.isStaticString()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModuleName(), policyName + "- Invalid attribute ID: " + attributIdStr + ". Remove \"\". Update will not take place.");
			}
			return false;
		}

		if(attributeIDHelper.isAVPairString()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModuleName(), policyName + "- Invalid attribute ID: " + attributIdStr + ". Update to AVPair type attribute is not supported.");
			}
			return false;
		}

		try {
			attributeValueString = paramDetails.getExpression().getStringValue(packetUtil);
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(getModuleName(), "Error in executing expression: " + paramDetails.getAttribute_value() + " for Update. Reason: "+e.getMessage());
			}
					return false;
				}

		if(attributeValueString == null || attributeValueString.equals("")){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), policyName + "- Configured Attribute for Update: " + attributIdStr + " not found in " + UniversalPacketTypeConstants.get(packetUtil.getDefaultPacketType()).name() + ". Update to Attribute: " + attributIdStr + " will not take place.");
					}
					return false;
				}

		IRadiusAttribute toBeUpdatedAttribute = packetUtil.getRadiusAttribute(attributeIDHelper.getStringValue(),true,iPacketType);

		if(toBeUpdatedAttribute != null) {
			if(paramDetails.getAttribute_value().endsWith("*")){
				attributeValueString = attributeValueString + toBeUpdatedAttribute.getStringValue();
				toBeUpdatedAttribute.setStringValue(attributeValueString);
			}else{
				toBeUpdatedAttribute.doPlus(attributeValueString);
					}
			isPacketModified = true;
		}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(getModuleName(), policyName + "- Configured Attribute for Update: " + attributIdStr + " not found in " + UniversalPacketTypeConstants.get(iPacketType).name() + ". Update to Attribute: " + attributIdStr + " will not take place.");
					}
				}
		return isPacketModified;
					}

	//checking whether the policy follows required things
	private boolean applyUniversalPluginPolicy(final PacketUtil packetUtil,AAAUniversalPluginPolicyDetail universalPluginPolicyDetail){

		boolean bResult = true;
		List<? extends ParamDetails> parmasDetailList = universalPluginPolicyDetail.getParameterDetailsForPlugin();
		String policyName = universalPluginPolicyDetail.getName();
		if(parmasDetailList.size()> 0){
			RadiusParamDetails paramDetails ;
			for(int i=0;i<parmasDetailList.size();i++){
				paramDetails =(RadiusParamDetails) parmasDetailList.get(i);
				String strAttrValue = paramDetails.getAttribute_value();
				String strParameterUsage = paramDetails.getParameter_usage();
				String strActive = paramDetails.getActive();
					try{

					String attributIdStr = paramDetails.getAttr_id();
						if(attributIdStr != null && attributIdStr.trim().length() > 0){

							if(strActive == null || strActive.trim().length() == 0){
								strActive = "YES";
							}
							if(strParameterUsage != null && strParameterUsage.trim().length() > 0
									&&strAttrValue != null && strAttrValue.trim().length() > 0){

								if(strActive.equalsIgnoreCase("YES")){
									StringHelper helper = new StringHelper(attributIdStr);
									if(helper.isStaticString()){
										if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
											LogManager.getLogger().warn(getModuleName(), policyName + "- Policy Skipped. Attribute ID configured: " + attributIdStr + " is invalid. Remove \"\"");
										}
										bResult = false;
										break;
									}
									if(helper.isAVPairString()){
										attributIdStr = helper.getStringValue();
									}
									if(strParameterUsage.equalsIgnoreCase(CHECK_ITEM)){
										LogicalExpression expression = paramDetails.getLogicalExpression();
										
											if(expression == null){
												if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
											LogManager.getLogger().warn(getModuleName(), policyName + "- Policy failed as check item expression: " + paramDetails.getAttr_id() + "=" + paramDetails.getAttribute_value() + " is invalid.");
												}
												bResult = false;
												break;
											}
											
											boolean bExpressionResult = evaluateLogicalExpression(expression,packetUtil);
									if(!bExpressionResult){
												bResult = false;
												break;
											}
											
									}else if(strParameterUsage.equalsIgnoreCase(REJECT_ITEM)){
										
										LogicalExpression expression =paramDetails.getLogicalExpression();
											if(expression == null){
												if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
											LogManager.getLogger().warn(getModuleName(), policyName + "- Policy failed as reject item expression: " + paramDetails.getAttr_id() + "=" + paramDetails.getAttribute_value() + " is invalid.");
												}
												bResult = false;
												break;
											}
											
											boolean bExpressionResult = evaluateLogicalExpression(expression,packetUtil);
											//in case of REJECT ITEM policy will be rejected if expression is satisfied
									if(bExpressionResult){
												bResult = false;
												break;
											}
											}
												}
											}
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(getModuleName(), policyName + "- Attribute ID is blank for parameter #" + (i+1) + ". Skipping this policy.");
							}
							bResult = false;
							break;
						}
					}catch(Exception ex){
						LogManager.getLogger().trace(getModuleName(),ex.getMessage());
					}


				}
		}else{
			bResult = true;
			LogManager.getLogger().warn(getModuleName(), "Parameter list configured in the policy :" + policyName + " is empty.");
		}

		return bResult;
	
	}
	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getPluginName());
		cacheDetail.setResultCode(CacheConstants.SUCCESS);
		cacheDetail.setSource("--");		
		/*try {
			BaseUniversalPluginConfImpl config = (BaseUniversalPluginConfImpl)getPluginConfiguration();
			if(config == null){
				cacheDetail.setResultCode(CacheConstants.FAIL);
				cacheDetail.setDescription("Failed, reason: Universal Plugin configuration is null");
			}else{
				config.readConfiguration();
				cacheDetail.setSource(config.getFileName());
				policyList = config.getPolicyList();
				setPolicyList(policyList);			
				setPrePolicyList(policyList);
				setPostPolicyList(policyList);	
			}

		} catch (LoadConfigurationException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed, reason: " + e.getMessage());
		}				
		 */		
		return cacheDetail;
	}
	private boolean evaluateLogicalExpression(LogicalExpression expression, final PacketUtil packetUtil){
		return expression.evaluate(new ValueProvider() {

			@Override
			public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
				StringHelper helper = new StringHelper(identifier);
				if(helper.isAVPairString()){
					IRadiusAttribute radiusAttribute = packetUtil.getRadiusAttribute(helper.getStringValue(), true, packetUtil.getDefaultPacketType());
					if(radiusAttribute == null)
						return null;
					
					return radiusAttribute.getKeyValue(helper.getKey());
				}
				IRadiusAttribute radiusAttribute = packetUtil.getRadiusAttribute(identifier, true, packetUtil.getDefaultPacketType());
				if(radiusAttribute == null)
					return null;
				
				return radiusAttribute.getStringValue();
			}

			@Override
				public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
				return 0;
			}

			@Override
			public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
				StringHelper stringHelper = new StringHelper(identifier);
				List<String> stringValues = new ArrayList<String>();
				
				if(stringHelper.isAVPairString()){
					Collection<IRadiusAttribute> allAttributes = packetUtil.getAttributes(packetUtil.getDefaultPacketType(), stringHelper.getStringValue(), true);
					if(allAttributes.size() == 0){
						throw new MissingIdentifierException("Configured attribute not found: " + identifier);
					}
					for(IRadiusAttribute radiusAttribute : allAttributes){
						String keyValue = radiusAttribute.getKeyValue(stringHelper.getKey());
						if(keyValue != null){
							stringValues.add(keyValue);
						}
					}
				}else{
					Collection<IRadiusAttribute> allAttributes = packetUtil.getAttributes(packetUtil.getDefaultPacketType(), stringHelper.getStringValue(), true);
					if(allAttributes.size() == 0){
						throw new MissingIdentifierException("Configured attribute not found: " + identifier);
					}
					for(IRadiusAttribute radiusAttribute : allAttributes){
						stringValues.add(radiusAttribute.getStringValue(false));
					}
				}

				return stringValues;
			}

			@Override
			public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
				return new ArrayList<Long>(1);
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		});
	}
	
	protected abstract void setPrePolicyList(List<AAAUniversalPluginPolicyDetail> prePolicyList);
	protected abstract void setPostPolicyList(List<AAAUniversalPluginPolicyDetail> postPolicyList);

	public enum UniversalPluginActionConstants{
		NO_ACTION(0,"NO_ACTION"),
		STOP(5,"STOP"),
		NONE(4,"NONE"),
		DROP(3,"DROP"),
		ACCEPT(1,"ACCEPT"),
		REJECT(2,"REJECT");

		public final int value;
		public final String name;
		private static final Map<Integer,UniversalPluginActionConstants> map;
		public static final UniversalPluginActionConstants[] VALUES = values();
		static {
			map = new HashMap<Integer,UniversalPluginActionConstants>(14);
			for (UniversalPluginActionConstants type : VALUES) {
				map.put(type.value, type);
			}
		}	

		UniversalPluginActionConstants(int value,String name){
			this.value = value;
			this.name = name;
		}	
		public int getValue(){
			return this.value;
		}
		public static boolean isValid(int value){
			return map.containsKey(value);	
		}

		public static UniversalPluginActionConstants get(int key){
			return map.get(key);
		}
		public static String getName(int value){
			return map.get(value).name;
		}
	}

	/*
	 * This is a helper class which helps in parsing the string that is configured in the Attribute value configuration
	 * of UPDATE ITEM. 
	 * CASES: 1) Enclosed in "" --> then the value is considered as static value and sets the isStatic property of this class
	 * 		  2) Not enclosed in "" and contains . --> Then the string is considered as dynamic of AVPAIR type and the value after . is considered as KEY
	 * 												   this sets the isAVPair property of this class and the key and values are populated
	 * 		  3) Not enclosed in "" and not contains . --> Then the string is considered as dynamic and the value is fetched from the packet and added
	 */
	static class StringHelper{
		private boolean bIsAVPair;
		private String key;
		private boolean bIsStatic;
		private String value;
		private String originalString;

		public StringHelper(String originalString) {
			this.originalString = originalString;
			parse(originalString);
		}

		private void parse(String str){
			str = str.trim();
			int startQuoteIndex = str.indexOf('\"');
			int endQuoteIndex = str.lastIndexOf('\"');
			if(startQuoteIndex == 0){ 
				if(endQuoteIndex == str.length() - 1){
					bIsStatic = true;
					value = str.substring(1,str.lastIndexOf("\""));
				}else{
					value = str;
				}
			}else{
				int dotIndex = str.indexOf('.');
				if(dotIndex == -1){
					value = str;
				}else{
					key = str.substring(dotIndex + 1);
					value = str.substring(0,dotIndex);
					bIsAVPair = true;
				}
			}
		}

		public boolean isAVPairString(){
			return bIsAVPair;
		}

		public boolean isStaticString(){
			return bIsStatic;
		}

		public String getKey(){
			return key;
		}

		public String getStringValue(){
			return value;
		}
		
		public String getOriginalString(){
			return originalString;
		}
	}

	protected enum UniversalPacketTypeConstants{
		DEFAULT_PACKET_TYPE(0),
		ACCESS_REQUEST(1),
		ACCESS_ACCEPT(2),
		ACCESS_REJECT(3),
		ACCOUNTING_REQUEST(4),
		ACCOUNTING_RESPONSE(5);

		private int value;
		private UniversalPacketTypeConstants(int value) {
			this.value = value;
		}

		public int getValue(){
			return this.value;
		}

		private static final Map<Integer,UniversalPacketTypeConstants> map;
		public static final UniversalPacketTypeConstants[] VALUES = values();
		static {
			map = new HashMap<Integer,UniversalPacketTypeConstants>(14);
			for (UniversalPacketTypeConstants type : VALUES) {
				map.put(type.value, type);
			}
		}

		public static UniversalPacketTypeConstants get(int key){
			return map.get(key);
		}
	}
	
	
}
