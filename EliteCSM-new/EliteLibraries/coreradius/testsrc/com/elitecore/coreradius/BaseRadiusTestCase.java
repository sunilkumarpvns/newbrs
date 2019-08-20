package com.elitecore.coreradius;


import junit.framework.TestCase;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;

public abstract class BaseRadiusTestCase extends TestCase {
	
	public BaseRadiusTestCase(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		//Setting NULL logger
		LogManager.setDefaultLogger(new NullLogger());
	}
	
	public void logInfo(String strMessage){
		System.out.println(strMessage);
	}
}
