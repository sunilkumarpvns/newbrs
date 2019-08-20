package com.elitecore.commons.tests;


import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.elitecore.commons.logging.LogManager;

/**
 * 
 * A JUnit rule that prints the name of test case method being executed. This is useful for debugging purposes
 * when test case execution fails.
 * 
 * @author narendra.pathai
 * @author sanjay.dhamelia
 *
 */
public class PrintMethodRule implements MethodRule {

	private static final String MODULE = "PRINT_METHOD_RULE";

	@Override
	public Statement apply(final Statement arg0, final FrameworkMethod arg1, Object arg2) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				LogManager.getLogger().info(MODULE, "************************************************");
				LogManager.getLogger().info(MODULE, "Starting execution of method: " + arg1.getName());
				LogManager.getLogger().info(MODULE, "************************************************");
				try {
					arg0.evaluate();
				} finally {
					LogManager.getLogger().info(MODULE, "************************************************");
					LogManager.getLogger().info(MODULE, "Ending execution of method: " + arg1.getName());
					LogManager.getLogger().info(MODULE, "************************************************");
				}
			}
		};
	}
}
