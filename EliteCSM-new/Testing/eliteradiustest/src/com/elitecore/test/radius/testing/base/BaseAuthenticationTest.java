package com.elitecore.test.radius.testing.base;

public abstract class BaseAuthenticationTest extends BaseEliteRadiusTest {

	public BaseAuthenticationTest(String name) {
		super(name);
	}
	
	public int getServicePort() {
		return 1812;
	}
}
