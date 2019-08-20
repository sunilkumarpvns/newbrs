package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum AkaActions implements IEnum {
	ProcessResponseIdentity,
	CheckForIsIdReqRequired,
	Validate_Response,
	GenerateStart,
	GenerateResponse,
	Resynchronize,
	DiscardRequest,
	BuildSuccess,
	BuildFailure
}
