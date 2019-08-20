package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum MD5Actions implements IEnum{
	Initialize,
	DiscardRequest,
	Generate_challenge,
	ValidateMD5Response
}
