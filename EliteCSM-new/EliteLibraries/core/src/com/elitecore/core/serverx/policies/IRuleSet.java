package com.elitecore.core.serverx.policies;

import com.elitecore.core.serverx.policies.data.PolicyTreeData;

/**
 * The <code>IRuleSet</code> interface represents different Check-Items and
 * Reject Items which will be applied on request packet.
 * 
 * @author Subhash Punani
 */

public interface IRuleSet {
	public void applyRuleSet(PolicyTreeData policyTreeData)throws PolicyFailedException;
	public void parseRuleSet() throws ParserException;
}
