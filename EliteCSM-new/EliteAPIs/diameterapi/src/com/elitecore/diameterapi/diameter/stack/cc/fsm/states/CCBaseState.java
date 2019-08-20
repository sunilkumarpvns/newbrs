package com.elitecore.diameterapi.diameter.stack.cc.fsm.states;

import com.elitecore.diameterapi.core.common.fsm.StateBase;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.CCActionExecutor;

public abstract class CCBaseState extends StateBase {
	protected CCActionExecutor actionExecutor;
	public CCBaseState(CCActionExecutor actionExecutor){
		this.actionExecutor = actionExecutor;
	}
	
}
