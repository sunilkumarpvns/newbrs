package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum ApplicationStates implements IEnum{
	INITIALIZED,
	MSCHAPv2,
	CHAP,
	PAP,
	EAP,
	MSCHAP
}
