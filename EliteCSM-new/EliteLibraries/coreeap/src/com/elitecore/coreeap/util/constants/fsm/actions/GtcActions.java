package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum GtcActions implements IEnum {
	Initialize,
	DiscardRequest,
	Generate_challenge,
	ValidateGtcResponse
}
