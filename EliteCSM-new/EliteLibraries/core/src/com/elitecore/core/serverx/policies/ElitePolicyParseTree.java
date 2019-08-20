package com.elitecore.core.serverx.policies;

import java.io.Serializable;

import com.elitecore.core.serverx.policies.parsetree.EliteParseTree;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;

/**
 * @author subhashpunani
 * @version 1.0
 * @created 20-Jan-2009 5:29:00 PM
 */
public class ElitePolicyParseTree extends EliteParseTree implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5316679135239897863L;
	
	private PolicyParseTreeNode rootNode;
	
	public ElitePolicyParseTree(){

	}

	public PolicyParseTreeNode getRootNode(){
		return rootNode;
	}
	
	public void setRootNode(PolicyParseTreeNode node){
		this.rootNode = node;
	}
}