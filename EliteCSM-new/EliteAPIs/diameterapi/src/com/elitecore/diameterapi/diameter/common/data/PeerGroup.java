package com.elitecore.diameterapi.diameter.common.data;

import java.util.List;

import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public interface PeerGroup {

	public LogicalExpression getRuleSet();
	public List<PeerInfoImpl> getPeerList();
	public String getAdvancedConditionStr();

}
