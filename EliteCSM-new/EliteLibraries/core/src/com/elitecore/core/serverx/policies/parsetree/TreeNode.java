package com.elitecore.core.serverx.policies.parsetree;


/**
 * @author subhashpunani
 * @version 1.0
 * @created 20-Jan-2009 5:29:02 PM
 */
public abstract class TreeNode {

	private Object data;
	private TreeNode leftNode;
	private TreeNode rightNode;

//	public void finalize() throws Throwable {
//
//	}

	public void displayNode(){
		
	}

	public TreeNode getLeftNode(){
		return leftNode;
	}

	public TreeNode getRightNode(){
		return rightNode;
	}
	public void setLeftNode(TreeNode leftNode){
		this.leftNode = leftNode;
	}
	public void setRightNode(TreeNode rightNode){
		this.rightNode = rightNode;
	}
	public Object getData(){
		return data;
	}
	
	public void setData(Object data){
		this.data = data;
	}

	public boolean isLeaf(){
		return(leftNode == null && rightNode == null);
	}
}