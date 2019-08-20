package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum SimActions implements IEnum {	
	ProcessResponseIdentity,
	CheckForIsIdReqRequired,
	Validate_Response,
	GenerateStart,
	GenerateResponse,	
	DiscardRequest,
	BuildSuccess,
	BuildFailure	
}
