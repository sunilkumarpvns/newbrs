package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum EapStates implements IEnum{
	INITIALIZE,
	IDLE,
	DISCARD,
	FAILURE,
	SUCCESS
}
