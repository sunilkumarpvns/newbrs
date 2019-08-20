package com.elitecore.core.serverx.policies;

/**
 * The <code>IAttributeSet</code> interface represents different Attributes which
 * will be replied back to client or added in request packet.
 * 
 * @author Subhash Punani
 */
public interface IAttributeSet {
	public void applyAttributeSet();
	public void applyReplyItems();
	public void applyAddItems();
}
