package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum MSCHAPv2States implements IEnum {
	INITIALIZED,
	RECEIVED,
	DISCARDED,
	SUCCESS_BUILDED,
	FAILURE_BUILDED,
	SEND,
	CHALLENGE_GENERATE,
	VALIDATE
}
