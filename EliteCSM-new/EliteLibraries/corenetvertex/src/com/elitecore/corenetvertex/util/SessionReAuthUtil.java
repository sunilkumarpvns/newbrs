package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.constants.SessionTypeConstant;

public class SessionReAuthUtil {

	public static boolean isOCSSession(SessionTypeConstant sessionTypeConstant) {
		return SessionTypeConstant.GY == sessionTypeConstant;
	}

	public static boolean isPCRFSession(SessionTypeConstant sessionTypeConstant) {
		// here RADIUS session is considered as PCRF Session because Radius will not have separate session for PCRF and OCS,
		// so NetVertex should do reauth on every RADIUS session
		return SessionTypeConstant.GX == sessionTypeConstant || SessionTypeConstant.CISCO_GX == sessionTypeConstant
				|| SessionTypeConstant.RADIUS == sessionTypeConstant;
	}
}
