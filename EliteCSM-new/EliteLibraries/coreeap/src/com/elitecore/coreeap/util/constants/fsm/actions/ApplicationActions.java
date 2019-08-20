package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum ApplicationActions implements IEnum{
	HandleEAPAuth,
	HandleMSCHAPv2Auth,
	HandleCHAPAuth,
	HandlePAPAuth,
	HandleMSCHAPAuth
}
