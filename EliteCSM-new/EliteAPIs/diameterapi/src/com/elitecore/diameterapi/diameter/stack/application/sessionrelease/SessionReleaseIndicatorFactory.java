package com.elitecore.diameterapi.diameter.stack.application.sessionrelease;

import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

public class SessionReleaseIndicatorFactory {

	private static SessionReleaseIndiactor defaultSessionReleaseIndiactor;
	
	static{
		defaultSessionReleaseIndiactor = new AppDefaultSessionReleaseIndicator();
	}
	


	public static SessionReleaseIndiactor getDefaultSessionReleaseIndiactor() {
		return defaultSessionReleaseIndiactor;
	}

	
	

}
