  package com.elitecore.aaa.radius.policies.radiuspolicy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.radius.policies.radiuspolicy.data.RadiusPolicyTreeData;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ElitePolicyParseTree;
import com.elitecore.core.serverx.policies.ElitePolicyTreeData;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;


public class RadiusPolicyParseTreeNode extends PolicyParseTreeNode {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "Radius Policy Parse Tree Node";
	
	public RadiusPolicyParseTreeNode(ElitePolicyTreeData data,boolean optimization) {
		super(data,optimization);
	}

	@Override
	public int evaluate(PolicyTreeData treeData,
			ArrayList<String> satisfiedPolicies) {
		return evaluate((RadiusPolicyTreeData)treeData, satisfiedPolicies);
	}
	public int evaluate(RadiusPolicyTreeData treeData,ArrayList<String> satisfiedPolicies) {
		ElitePolicyTreeData policyParseTreeData = (ElitePolicyTreeData)getData();
		if(policyParseTreeData==null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Configured policy not found, returning " + treeData.isContinueOnPolicyNotFound());
			if(treeData.isContinueOnPolicyNotFound())
				return PolicyConstants.SUCCESS;
			else
				return PolicyConstants.POLICY_NOT_FOUND;
		}
		String policyName = null;
		policyName = policyParseTreeData.getPolicyName();
		Map<String,String> customerLevelCheckItems = treeData.getPolicyOverrideData().getCheckItem(policyName);
		Map<String,String> customerLevelRejectItems = treeData.getPolicyOverrideData().getRejectItem(policyName);
		
		int iCheckItemResult = PolicyConstants.SUCCESS;
		int iRejectItemResult = PolicyConstants.GENERAL_FAILER;
		boolean bPortalRequest = false;
		
		if(!treeData.isbApplyCheckItemForPortalRequest() && (treeData.getClientType() == ClientTypeConstant.PORTAL.typeId || treeData.getClientType()== ClientTypeConstant.WIMAX_PORTAL.typeId)){
			bPortalRequest = true;
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "The request is received from Portal or WimaxPortal, hence not applying Check-Item(s) and Reject-Item(s).");
		}
		

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE,"Applying Policy: " + policyName);
		
		//Special handling of default policy because there is no tree for default policy.
		if(policyName.equals("*") && !bPortalRequest){
			if(customerLevelCheckItems!=null){
				Iterator<String> checkItemIterator = customerLevelCheckItems.keySet().iterator();
				while(checkItemIterator.hasNext()){
					String strAttribute = checkItemIterator.next();
					String strValue = customerLevelCheckItems.get(strAttribute);
					String[] arguments = new String[3];
					arguments[0]=strAttribute;
					arguments[1]="=";
					arguments[2]=strValue;
					RadiusParseTreeNode parseTreeNode = new RadiusParseTreeNode(arguments,true, PolicyType.CHECK_ITEM_POLICY, "*");
					iCheckItemResult=parseTreeNode.evaluate(treeData);
					if(iCheckItemResult > PolicyConstants.SUCCESS)
						break;
				}
			}
			
			if(iCheckItemResult <= PolicyConstants.SUCCESS && customerLevelRejectItems!=null && customerLevelRejectItems.size()>0){
				Iterator<String> rejectItemIterator = customerLevelRejectItems.keySet().iterator();
				while(rejectItemIterator.hasNext()){
					String strAttribute = rejectItemIterator.next();
					String strValue = customerLevelRejectItems.get(strAttribute);
					String[] arguments = new String[3];
					arguments[0]=strAttribute;
					arguments[1]="=";
					arguments[2]=strValue;
					RadiusParseTreeNode parseTreeNode = new RadiusParseTreeNode(arguments,true, PolicyType.REJECT_ITEM_POLICY, "*");
					iRejectItemResult=parseTreeNode.evaluate(treeData);
					if(iRejectItemResult == PolicyConstants.SUCCESS)
						break;
				}
			}
		}
		
		//narendra.pathai TIME AUTHORIZATION ELITEAAA-1865
		long sessionTimeout = policyParseTreeData.getAccessTimePolicy().applyPolicy(AccessTimePolicy.SECOND);
		if(!policyName.equals("*") && !bPortalRequest && sessionTimeout == AccessTimePolicy.FAILURE){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,policyName + "- Time Authorization Result: NOT ALLOWED");
			
			return PolicyConstants.GENERAL_FAILER;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,policyName + "- Time Authorization Result: ALLOWED");
			
			/* If the session timeout is not 0 then it means that time slot is provided and some
			 * non zero session timeout value is remaining. So if response already contains
			 * session timeout and the session timeout received from access policy is less than 
			 * already containing, then value will be replaced. If session timeout attribute is 
			 * not present then it will be added.
			 */
			if(sessionTimeout > 0){
				RadServiceResponse response = treeData.getRadiusServiceResponse();
				IRadiusAttribute sessionTimeoutAttribute = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				if(sessionTimeoutAttribute != null){
					if(sessionTimeoutAttribute.getLongValue() > sessionTimeout){
						sessionTimeoutAttribute.setLongValue(sessionTimeout);
					}
				}else{
					sessionTimeoutAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
					if(sessionTimeoutAttribute != null){
						sessionTimeoutAttribute.setLongValue(sessionTimeout);
						response.addAttribute(sessionTimeoutAttribute);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, policyName + "- SESSION_TIMEOUT(0:27) not found in dictionary.");
						}
					}
				}
			}
		}
		
		ElitePolicyParseTree checkItemTree = policyParseTreeData.getCheckItemTree();
		if(checkItemTree!=null && !policyName.equals("*") && !bPortalRequest){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,policyName + "- Applying Check Item");
			iCheckItemResult = ((PolicyParseTreeNode)checkItemTree.getRootNode()).evaluate(treeData, satisfiedPolicies);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,policyName + "- Check-Item Result: " + iCheckItemResult);
		}
		if(iCheckItemResult <= PolicyConstants.SUCCESS && !policyName.equals("*") && !bPortalRequest){
			ElitePolicyParseTree rejectItemTree = policyParseTreeData.getRejectItemTree();
			if(rejectItemTree!=null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,policyName + "- Applying Reject Item");
				iRejectItemResult = ((PolicyParseTreeNode)rejectItemTree.getRootNode()).evaluate(treeData,satisfiedPolicies);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,policyName + "- Reject-Item Result: " + iRejectItemResult);
			}
		}
		if(iCheckItemResult <= PolicyConstants.SUCCESS && iRejectItemResult != PolicyConstants.SUCCESS){
			satisfiedPolicies.add(policyName);
			return PolicyConstants.SUCCESS;
		}else{
			return PolicyConstants.GENERAL_FAILER;
		}
	}
}
