package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum MSCHAPv2Actions implements IEnum {
	Initialize,
	ProcessRequest,
	DiscardRequest,
	BuildSuccess,
	BuildFailure,
	SendResponse,
	Generate_challenge,
	ValidateMSCHAPv2Response
}
