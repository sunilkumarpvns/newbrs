package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum AkaStates implements IEnum {
	INITIALIZED,
	DISCARDED,
	SUCCESS_BUILDED,
	FAILURE_BUILDED,
	START_GENERATED,
	ANY_IDENTITY_REQUESTED,
	PERMANENT_IDENTITY_REQUESTED,
	FULL_AUTH_ID_REQ,
	CHALLENGE_GENERATE,

}
