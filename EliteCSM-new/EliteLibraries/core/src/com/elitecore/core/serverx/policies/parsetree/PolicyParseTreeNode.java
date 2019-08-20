package com.elitecore.core.serverx.policies.parsetree;

import java.io.Serializable;
import java.util.ArrayList;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;

/**
 * @author subhashpunani
 * @version 1.0
 * @created 20-Jan-2009 5:29:01 PM
 */
public class PolicyParseTreeNode extends TreeNode implements Serializable {
	private static final String MODULE = "Parse Tree Node";
	private static final long serialVersionUID = 1L;
	public static final int REQUEST_PACKET = 0;
	public static final int RESPONSE_PACKET = 1;
	boolean bOROptimization;
	private int checkIn;

	public PolicyParseTreeNode(Object data){
		this(data, true);
	}
	public PolicyParseTreeNode(Object strData,boolean bOROptimization){
		setData(strData);
		this.bOROptimization = bOROptimization;
	}
	
	public int evaluate(PolicyTreeData treeData,ArrayList<String> satisfiedPolicies){
		String strOperator = (String)getData();
		if(strOperator == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Operator is NULL. Returning false for expression");
			return PolicyConstants.GENERAL_FAILER;
		}
		ArrayList<String> leftSatisfiedPolicies = new ArrayList<String>();
		ArrayList<String> rightSatisfiedPolicies = new ArrayList<String>();
		
		//TODO Define constant for && and || Operator
		if("&&".equals(strOperator)){
			//And Operation
			int iLeftNodeResult = ((PolicyParseTreeNode)getLeftNode()).evaluate(treeData,leftSatisfiedPolicies);
			if(iLeftNodeResult <= PolicyConstants.SUCCESS){
				int iRightNodeResult = ((PolicyParseTreeNode)getRightNode()).evaluate(treeData,rightSatisfiedPolicies);
				if(iRightNodeResult <= PolicyConstants.SUCCESS){
					satisfiedPolicies.addAll(leftSatisfiedPolicies);
					satisfiedPolicies.addAll(rightSatisfiedPolicies);
					return iRightNodeResult<PolicyConstants.SUCCESS?iLeftNodeResult:iRightNodeResult;
				}
				return PolicyConstants.GENERAL_FAILER;
			}else{
				return PolicyConstants.GENERAL_FAILER;
			}
		}else if("||".equals(strOperator)){
			//OR Operation
			int iLeftNodeResult = ((PolicyParseTreeNode)getLeftNode()).evaluate(treeData,leftSatisfiedPolicies);
			
			if(iLeftNodeResult <= PolicyConstants.SUCCESS){
				satisfiedPolicies.addAll(leftSatisfiedPolicies);
				if(bOROptimization && iLeftNodeResult == PolicyConstants.SUCCESS){
					return PolicyConstants.SUCCESS;
				}else{
					int iRightNodeResult = ((PolicyParseTreeNode)getRightNode()).evaluate(treeData,	rightSatisfiedPolicies);
					if(iRightNodeResult == PolicyConstants.SUCCESS){
						satisfiedPolicies.addAll(rightSatisfiedPolicies);
					}
					if(iLeftNodeResult == PolicyConstants.SUCCESS)
						return PolicyConstants.SUCCESS;
					return iRightNodeResult;
				}
			}else{
				int iRightNodeResult = ((PolicyParseTreeNode)getRightNode()).evaluate(treeData,rightSatisfiedPolicies);
				if(iRightNodeResult <= PolicyConstants.SUCCESS){
					satisfiedPolicies.addAll(rightSatisfiedPolicies);
				}
				return iRightNodeResult;
			}
		}else{
			//Either OR Operation
			int iLeftNodeResult = ((PolicyParseTreeNode)getLeftNode()).evaluate(treeData,leftSatisfiedPolicies);
			
			if(iLeftNodeResult > PolicyConstants.SUCCESS){
				int iRightNodeResult = ((PolicyParseTreeNode)getRightNode()).evaluate(treeData,rightSatisfiedPolicies);
				if(iRightNodeResult == PolicyConstants.SUCCESS){
					satisfiedPolicies.addAll(rightSatisfiedPolicies);
				}
				return iRightNodeResult;
			}else{
				satisfiedPolicies.addAll(leftSatisfiedPolicies);
				return iLeftNodeResult;
			}
		}
	}	
	public void setCheckIn(int checkIn) {
		this.checkIn = checkIn;
	}
	public int getCheckIn() {
		return this.checkIn ;
	}
}