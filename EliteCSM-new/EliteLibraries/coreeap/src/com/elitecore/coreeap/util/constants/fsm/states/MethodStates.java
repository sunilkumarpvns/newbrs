package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum MethodStates implements IEnum{	
	PROPOSED(1),
	CONTINUE(2),
	END(3),
	ERROR(4);
	public final int id;
	MethodStates(int id) {
		this.id = id;
	}
}
