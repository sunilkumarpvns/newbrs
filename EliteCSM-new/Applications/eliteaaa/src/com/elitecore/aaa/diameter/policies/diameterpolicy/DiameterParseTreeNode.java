package com.elitecore.aaa.diameter.policies.diameterpolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.diameter.policies.diameterpolicy.data.DiameterPolicyTreeData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;

public class DiameterParseTreeNode extends PolicyParseTreeNode {
	private static final long serialVersionUID = 1L;	
	private static final String MODULE = "Diameter Parse Tree Node";
	private String key;
	private PolicyType policyType;
	private ArrayList<IDiameterAVP> policyNodeAttributes=null;
	private ArrayList<char[]> policyNodeRegxComparison=null;
	private IDiameterAVP diameterAvp;
	private String strAttribute;
	private String policyName;
	
	public DiameterParseTreeNode(String[] data, String policyName) {
		this(data,true,PolicyType.CHECK_ITEM_POLICY, policyName);
	}
	public DiameterParseTreeNode(String[] strData, boolean optimization, PolicyType policyType, String policyName) {
		super(strData, optimization);
		this.policyName = policyName;
		this.policyType = policyType;
		strAttribute = strData[0];
		String strValue = strData[2];
		
		diameterAvp = DiameterDictionary.getInstance().getAttribute(strAttribute);
		policyNodeAttributes = new ArrayList<IDiameterAVP>();
		policyNodeRegxComparison = new ArrayList<char[]>();
		if(diameterAvp != null && strValue!=null){
			String[] values = ParserUtility.splitString(strValue.replaceAll("\\[", "").replaceAll("\\]", ""),',');
			for(String attrValue:values){
				if(key!= null){
					policyNodeRegxComparison.add(("*" + key + "=" + attrValue+"*").toCharArray());
				}else if(attrValue.indexOf('*') != -1|| attrValue.indexOf('?') != -1){
					policyNodeRegxComparison.add(attrValue.toCharArray());
				}else{
					IDiameterAVP newDiaAvp = null;
					try {
						newDiaAvp = (IDiameterAVP) diameterAvp.clone();
						newDiaAvp.setStringValue(attrValue);
						policyNodeAttributes.add(newDiaAvp);
					} catch (CloneNotSupportedException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE,"Error in cloning Diameter AVP, Reason:" + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
			}
		}
	}
	@Override
	public int evaluate(PolicyTreeData treeData, ArrayList<String> satisfiedPolicies) {
		return evaluate((DiameterPolicyTreeData)treeData);
	}
	public int evaluate(DiameterPolicyTreeData treeData) {
		
		String[] strDiameterPolicy = (String[])getData();
		if(diameterAvp  == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Unknown AVP encountered in policy application.  Ignoring Unknown AVP: " + strDiameterPolicy[0]);
			return PolicyConstants.IGNORE;	
		}
		boolean bResult = false;
		try{
			ArrayList<IDiameterAVP> localAvpList = policyNodeAttributes;
			ArrayList<char[]> localRegxCompList = policyNodeRegxComparison;

			Map<String,String>overrideValues = null;
			if(this.policyName != "*"){
				if(treeData.getPolicyOverrideData() != null){
					if(policyType == PolicyType.CHECK_ITEM_POLICY){
						overrideValues = treeData.getPolicyOverrideData().getCheckItem(this.policyName);
					}else{
						overrideValues = treeData.getPolicyOverrideData().getRejectItem(this.policyName);
					}
				}				
			}
 
			if(overrideValues!=null){
				String strNewValue = overrideValues.get(strDiameterPolicy[0]);
				if(strNewValue!=null){
					localAvpList = new ArrayList<IDiameterAVP>();
					localRegxCompList = new ArrayList<char[]>();
					String[] values = ParserUtility.splitString(strNewValue.replaceAll("\\[", "").replaceAll("\\]", ""),',');
					for(String attrValue:values){
						if(attrValue.indexOf('*') != -1|| attrValue.indexOf('?') != -1){
							localRegxCompList.add(attrValue.toCharArray());
						}else{
							IDiameterAVP localAvp = (IDiameterAVP) diameterAvp.clone();
							localAvp.setStringValue(attrValue);
							localAvpList.add(localAvp);
						}
					}
				}
			}
			DiameterRequest diaSerReq = treeData.getDiameterRequest();
			if(diaSerReq != null){
				List<IDiameterAVP>attributeList = null;
				attributeList = diaSerReq.getAVPList(strAttribute,true);
				int listSize = 0;
				if(attributeList ==null || (listSize = attributeList.size())==0){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Configured Attribute("+strDiameterPolicy[0]+") not found in Request Packet.");
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
				final int localAttributeListSize = localAvpList.size();
				for(int i=0;i<localAttributeListSize && !bResult;i++){
					for(int j=0;j<listSize && !bResult;j++) {
						IDiameterAVP diameterAvp = attributeList.get(j);
						bResult = diameterAvp.equals(localAvpList.get(i));
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Checking " + strDiameterPolicy[0]+ strDiameterPolicy[1] + localAvpList.get(i).getStringValue() + ", Result: " + bResult);

					}
				}
				final int localRegxCompListSize = localRegxCompList.size();
				for(int i=0;i<localRegxCompListSize && !bResult;i++){
					for(int j=0;j<listSize && !bResult;j++) {
						IDiameterAVP diameterAvp = attributeList.get(j);
						bResult = DiameterUtility.matches(diameterAvp.getStringValue(),localRegxCompList.get(i));
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Checking " + strDiameterPolicy[0]+ strDiameterPolicy[1] + String.valueOf(localRegxCompList.get(i)) + ", Result: " + bResult);
					}
				}
				if(strDiameterPolicy[1].equals("!="))
					bResult ^= true;
			}
			
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
