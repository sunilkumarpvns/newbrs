package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum Md5States implements IEnum{
	INITIALIZED,
	DISCARDED,
	CHALLENGE_GENERATE,
	VALIDATE
}
