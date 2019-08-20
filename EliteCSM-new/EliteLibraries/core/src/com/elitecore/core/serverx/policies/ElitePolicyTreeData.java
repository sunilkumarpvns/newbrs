package com.elitecore.core.serverx.policies;

import java.io.Serializable;

import com.elitecore.core.serverx.policies.accesstime.EliteAccessTimePolicy;

/**
 * @author Subhash Punani
 * @version 1.0
 * @created 20-Jan-2009 5:29:00 PM
 */
public class ElitePolicyTreeData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -12149825024487398L;
	
	private ElitePolicyParseTree checkItemTree;
	private ElitePolicyParseTree rejectItemTree;
	private IReplyItems replyItem; //NOSONAR
	private IReplyItems addItem; //NOSONAR
	transient private EliteAccessTimePolicy accessTimePolicy;
	
	private String policyName;

	public ElitePolicyParseTree getCheckItemTree() {
		return checkItemTree;
	}

	public void setCheckItemTree(ElitePolicyParseTree checkItemTree) {
		this.checkItemTree = checkItemTree;
	}

	public ElitePolicyParseTree getRejectItemTree() {
		return rejectItemTree;
		
	}

	public void setRejectItemTree(ElitePolicyParseTree rejectItemTree) {
		this.rejectItemTree = rejectItemTree;
	}

	public IReplyItems getReplyItem() {
		return replyItem;
	}

	public void setReplyItem(IReplyItems replyItem) {
		this.replyItem = replyItem;
	}
	
	public IReplyItems getAddItem() {
		return addItem;
	}

	public void setAddItem(IReplyItems addItem) {
		this.addItem = addItem;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	public void setAccessTimePolicy(EliteAccessTimePolicy accessTimePolicy){
		this.accessTimePolicy = accessTimePolicy;
	}
	
	public EliteAccessTimePolicy getAccessTimePolicy(){
		return this.accessTimePolicy;
	}
}