package com.elitecore.netvertex.service.pcrf;

/**
 * 
 * @author milan
 *
 */
public abstract class  PCCRuleExpiryListener {
	
	public abstract void reAuthSession(PCRFRequest request);
}
