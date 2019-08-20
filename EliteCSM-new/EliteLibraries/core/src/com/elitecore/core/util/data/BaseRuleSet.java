package com.elitecore.core.util.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.policies.ElitePolicyParseTree;
import com.elitecore.core.serverx.policies.IRuleSet;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyConstants;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.PolicyNotFoundException;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;

public abstract class BaseRuleSet implements IRuleSet {
	
	private static final String MODULE = "Base Rule Set";
	private String ruleSetName;
	private String checkItem;
	private String rejectItem;
	private ElitePolicyParseTree checkItemTree;

	public BaseRuleSet(String ruleSetName, String checkItem, String rejectItem) {
		this.ruleSetName = ruleSetName;
		this.checkItem = checkItem;
		this.rejectItem = rejectItem;
	}
	
	@Override
	public void applyRuleSet(PolicyTreeData policyTreeData) throws PolicyFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE,"Applying Check Item");
		
		int iCheckItemResult = ((PolicyParseTreeNode)checkItemTree.getRootNode()).evaluate(policyTreeData,new ArrayList<String>());
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE,"Check-Item Result: " + iCheckItemResult);
		if(iCheckItemResult != PolicyConstants.SUCCESS){
			throw new PolicyFailedException(CommonConstants.POLICY_FAILED);
		}
	}

	@Override
	public void parseRuleSet() throws ParserException {
		if(checkItem != null){
			try{
				List<String> checkItems = ParserUtility.convertToPostFixNotation(checkItem);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Parsed Check Items: " + checkItems);
				checkItemTree = buildParseTree(checkItems, PolicyType.CHECK_ITEM_POLICY);
			}catch(Exception e){
				throw new ParserException("Error in parsing Check-Item for policy " + ruleSetName,e);
			}
		}

		if(rejectItem != null){
			try{
				List<String> rejectItems = ParserUtility.convertToPostFixNotation(rejectItem);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Parsed Reject Items: " + rejectItems);
			}catch(Exception e){
				throw new ParserException("Error in parsing Reject-Item for policy " + ruleSetName,e);
			}
		}

	}
	
	private ElitePolicyParseTree buildParseTree(List<String>policyTokens,PolicyType policyType) throws ParserException, PolicyNotFoundException{
		Stack<PolicyParseTreeNode> parseTreeStack = new Stack<PolicyParseTreeNode>();
		ElitePolicyParseTree policyParseTree = null;
		if(policyTokens!=null && !policyTokens.isEmpty()){
			try{
				for(int i=0;i<policyTokens.size();i++){
					String strPolicyToken = policyTokens.get(i);
					if("&&".equals(strPolicyToken)|| "||".equals(strPolicyToken)|| "^".equals(strPolicyToken)){
						PolicyParseTreeNode operatorNode = new PolicyParseTreeNode(strPolicyToken);
						PolicyParseTreeNode rightNode = parseTreeStack.pop();
						PolicyParseTreeNode leftNode = parseTreeStack.pop();
						operatorNode.setLeftNode(leftNode);
						operatorNode.setRightNode(rightNode);
						parseTreeStack.push(operatorNode);
					}else{
						parseTreeStack.push(getAttributeParseTreeNode(strPolicyToken, policyType, this.ruleSetName));
					}
				}
			}catch (EmptyStackException e) {
				throw new ParserException("Invalid Expression", e);
			}
			policyParseTree = new ElitePolicyParseTree();
			policyParseTree.setRootNode(parseTreeStack.pop());
			if(!parseTreeStack.empty())
				throw new ParserException("Invalid Expression");
		}
		return policyParseTree;
	}
	
	protected abstract PolicyParseTreeNode getAttributeParseTreeNode(String strPolicyToken,PolicyType policyType, String policyName);
	
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();		
		out.println("      Name = " + ruleSetName);		
		out.println("        Check Expr = " + checkItem);		
		out.println("        Reject Expr = " + rejectItem);		

		out.close();
		return stringBuffer.toString();
	}
	
	public String getName(){
		return ruleSetName;
	}
	
}
