package com.elitecore.aaa.radius.policies.radiuspolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.elitecore.aaa.radius.policies.radiuspolicy.data.RadiusPolicyTreeData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.data.PolicyOverrideData;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;


public final class RadiusParseTreeNode extends PolicyParseTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "Radius Parse Tree Node";
	private static final String JAVA_REGX_CHAR = "~";
	private String key;
	private PolicyType policyType;
	private ArrayList<IRadiusAttribute> policyNodeAttributes=null;
	private ArrayList<char[]> policyNodeRegxComparison=null;
	/*
	 *This list PolicyNodeJavaRegxComparison will contain compiled regular expressions
	 *This list is introduced with JIRA ID 371 - Support for regx in Check items
	 * 
	 */
	private ArrayList<Pattern> policyNodeJavaRegxComparison = null;
	private IRadiusAttribute radAttribute;
	private String strAttribute;
	private String policyName;

	public RadiusParseTreeNode(String[] data, String policyName) {
		this(data,true,PolicyType.CHECK_ITEM_POLICY, policyName);
	}
	public RadiusParseTreeNode(String[] strData, boolean optimization, PolicyType policyType, String policyName) {
		super(strData, optimization);
		this.policyType = policyType;
		this.policyName = policyName;
		strAttribute = strData[0];
		String strValue = strData[2];
		if(strAttribute != null && strAttribute.contains(".")){
					key = strAttribute.substring(strAttribute.lastIndexOf('.') + 1);
					strAttribute = strAttribute.substring(0, strAttribute.lastIndexOf('.'));
		}
		radAttribute = Dictionary.getInstance().getKnownAttribute(strAttribute);
		policyNodeAttributes = new ArrayList<IRadiusAttribute>();
		policyNodeRegxComparison = new ArrayList<char[]>();
		policyNodeJavaRegxComparison = new ArrayList<Pattern>();
		if(radAttribute != null && strValue!=null){
			/*JIRA ID 371 - Support for regx in Check items by narendra.pathai
			 *This is the special check that if the value starts with ~ then the 
			 *whole value is to be considered as Regular expression and [ ] square
			 *brackets are not to be removed
			 */
			if(strValue.trim().startsWith(JAVA_REGX_CHAR)){
				String trueRegxExpression = strValue.trim();
				if(trueRegxExpression.length() > 1){
					trueRegxExpression = strValue.substring(1);
					try{
						policyNodeJavaRegxComparison.add(Pattern.compile(trueRegxExpression));
					}catch(PatternSyntaxException ex){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, policyName + "- Error in compiling regular expression: " + trueRegxExpression);
						}
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, policyName + "- Invalid regualar expression: " + trueRegxExpression);
					}
				}
			}else{
				String[] values = ParserUtility.splitString(strValue.replaceAll("\\[", "").replaceAll("\\]", ""),',');
				for(String attrValue:values){
					if(key!= null){
						policyNodeRegxComparison.add(("*" + key + "=" + attrValue+"*").toCharArray());
					}else if(attrValue.indexOf('*') != -1|| attrValue.indexOf('?') != -1){
						policyNodeRegxComparison.add(attrValue.toCharArray());
					}else{
						IRadiusAttribute newRadAttr = null;
						try {
							newRadAttr = (IRadiusAttribute) radAttribute.clone();
							newRadAttr.setStringValue(attrValue);
							policyNodeAttributes.add(newRadAttr);
						} catch (CloneNotSupportedException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
								LogManager.getLogger().error(MODULE,"Error in cloning Radius Attribute, Reason:" + e.getMessage());
							LogManager.getLogger().trace(MODULE, e);
						}
					}
				}
			}
		}
	}

	@Override
	public int evaluate(PolicyTreeData treeData, ArrayList<String> satisfiedPolicies) {
		return evaluate((RadiusPolicyTreeData)treeData);
	}
	public int evaluate(RadiusPolicyTreeData treeData) {
		
		String[] strRadiusPolicy = (String[])getData();
		if(radAttribute == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				if(strAttribute==null || !(strAttribute.trim().length()>0))
					LogManager.getLogger().warn(MODULE, "Invalid Attribute encountered in policy : "+policyName);
				else {
					LogManager.getLogger().warn(MODULE, "Unknown Attribute : "+strAttribute + " encountered in policy : "+policyName+" , So this check will be ignored");
				}
			}	
			return PolicyConstants.IGNORE;	
		}
		boolean bResult = false;
		try{
			ArrayList<IRadiusAttribute> localAttributeList = policyNodeAttributes;
			ArrayList<char[]> localRegxCompList = policyNodeRegxComparison;
			ArrayList<Pattern> localJavaRegxCompList = policyNodeJavaRegxComparison;
			Map<String,String>overrideValues = null;
			if(this.policyName != "*"){
				PolicyOverrideData overrideData = treeData.getPolicyOverrideData();				
				if(overrideData != null){
					if(policyType == PolicyType.CHECK_ITEM_POLICY){
						overrideValues = treeData.getPolicyOverrideData().getCheckItem(this.policyName);
					}else{
						overrideValues = treeData.getPolicyOverrideData().getRejectItem(this.policyName);
					}
				}
			}
			if(overrideValues!=null){
				String strNewValue = overrideValues.get(strRadiusPolicy[0]);
				if(strNewValue!=null){
					localAttributeList = new ArrayList<IRadiusAttribute>();
					localRegxCompList = new ArrayList<char[]>();
					localJavaRegxCompList = new ArrayList<Pattern>();
					/*JIRA ID 371 - Support for regx in Check items by narendra.pathai
					 *This is the special check that if the value starts with ~ then the 
					 *whole value is to be considered as Regular expression and [ ] square
					 *brackets are not to be removed
					 */
					if(strNewValue.trim().startsWith(JAVA_REGX_CHAR)){
						if(strNewValue.trim().length() > 1 && strNewValue.trim().startsWith(JAVA_REGX_CHAR)){
							String patternStr = strNewValue.trim().substring(1);
							try{
								localJavaRegxCompList.add(Pattern.compile(patternStr));
							}catch(PatternSyntaxException ex){
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, policyName + "- Error in compiling expression: " + patternStr);
								}
							}
						}
					}else{
						String[] values = ParserUtility.splitString(strNewValue.replaceAll("\\[", "").replaceAll("\\]", ""),',');
						for(String attrValue:values){
							if(attrValue.trim().length() > 1 && attrValue.trim().startsWith(JAVA_REGX_CHAR)){
								String patternStr = attrValue.trim().substring(1);
								try{
									localJavaRegxCompList.add(Pattern.compile(patternStr));
								}catch(PatternSyntaxException ex){
									if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
										LogManager.getLogger().warn(MODULE, policyName + "- Error in compiling expression: " + patternStr);
									}
								}
							}else if(attrValue.indexOf('*') != -1|| attrValue.indexOf('?') != -1){
								localRegxCompList.add(attrValue.toCharArray());
							}else{
								IRadiusAttribute localRadAttr = (IRadiusAttribute) radAttribute.clone();
								localRadAttr.setStringValue(attrValue);
								localAttributeList.add(localRadAttr);
							}
						}
					}
				}
			}
			int checkIn = getCheckIn();
				ArrayList <IRadiusAttribute>attributeList = null;
			if(checkIn==REQUEST_PACKET)
				attributeList = (ArrayList<IRadiusAttribute>) treeData.getRadiusServiceRequest().getRadiusAttributes(strAttribute,true);
			else
				attributeList = (ArrayList<IRadiusAttribute>) treeData.getRadiusServiceResponse().getRadiusAttributes(true, strAttribute);

				int listSize = 0;
				if(attributeList ==null || (listSize = attributeList.size())==0){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Configured Attribute("+strRadiusPolicy[0]+") not found in Request Packet.");
					if(policyType == PolicyType.CHECK_ITEM_POLICY){
						bResult = treeData.isRejectOnCheckItemNotFound() ^ true;;
					}else{
						bResult = treeData.isRejectOnRejectItemNotFound();
					}
					if(bResult)
						return PolicyConstants.SUCCESS;
					else
						return PolicyConstants.GENERAL_FAILER;
				}
				final int localAttributeListSize = localAttributeList.size();
				for(int i=0;i<localAttributeListSize && !bResult;i++){
					for(int j=0;j<listSize && !bResult;j++) {
						IRadiusAttribute radiusAttribute = attributeList.get(j);
						bResult = radiusAttribute.equals(localAttributeList.get(i));
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Checking " + strRadiusPolicy[0]+ strRadiusPolicy[1] + localAttributeList.get(i).getStringValue() + ", Result: " + bResult);

					}
				}
				final int localRegxCompListSize = localRegxCompList.size();
				for(int i=0;i<localRegxCompListSize && !bResult;i++){
					for(int j=0;j<listSize && !bResult;j++) {
						IRadiusAttribute radiusAttribute = attributeList.get(j);
						bResult = RadiusUtility.matches(radiusAttribute.getStringValue(),localRegxCompList.get(i));
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Checking " + strRadiusPolicy[0]+ strRadiusPolicy[1] + String.valueOf(localRegxCompList.get(i)) + ", Result: " + bResult);
					}
				}
				final int localJavaRegxCompListSize = localJavaRegxCompList.size();
				for(int i=0;i<localJavaRegxCompListSize && !bResult;i++){
					for(int j=0;j<listSize && !bResult;j++) {
						IRadiusAttribute radiusAttribute = attributeList.get(j);
						bResult = localJavaRegxCompList.get(i).matcher(radiusAttribute.getStringValue()).matches();
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Checking with java regx " + strRadiusPolicy[0]+ strRadiusPolicy[1] + String.valueOf(localJavaRegxCompList.get(i)) + ", Result: " + bResult);
					}
				}
				if(strRadiusPolicy[1].equals("!="))
					bResult ^= true;
			
			
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Error in evaluating node: " + getData() + ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			bResult=false;
		}
		if(bResult)
			return PolicyConstants.SUCCESS;
		else
			return PolicyConstants.GENERAL_FAILER;
	}
	
}
