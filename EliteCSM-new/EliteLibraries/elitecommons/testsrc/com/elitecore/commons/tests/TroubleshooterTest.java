package com.elitecore.commons.tests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.StringCollectionLogger;
import com.elitecore.commons.tests.Troubleshooter.Enabled;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class TroubleshooterTest {

	private StringCollectionLogger logger = new StringCollectionLogger();
	
	@Before
	public void setDefaultLogger() {
		LogManager.setDefaultLogger(logger);
	}

	public class TroubleshooterEnabled {
		
		@Test
		public void executesOnlyTheMethodsThatAreEnabled() {
			
			JUnitCore.runClasses(TroubleshooterEnabledHostClass.class);
			
			assertThat(logger.contents(), is(equalTo("enabledMethod\n")));
		}
		
		@Test
		public void failsMethodsThatAreDisabled() {
			Result result = JUnitCore.runClasses(TroubleshooterEnabledHostClass.class);
			
			assertThat(result.getFailureCount(), is(equalTo(1)));
			
			assertThat(logger.contents(), not(containsString("disabledMethod")));
		}
	}
	
	public class TroubleshooterDisabled {
		
		@Test
		public void executesAllMethodsIrrespectiveOfEnabledAnnotation() {
			Result result = JUnitCore.runClasses(TroubleshooterDisabledHostClass.class);
			
			assertThat(result.getFailureCount(), is(equalTo(0)));
			assertThat(logger.contents(), containsString("enabledMethod"));
			assertThat(logger.contents(), containsString("disabledMethod"));
		}
	}
	
	public static class TroubleshooterEnabledHostClass {
		
		@Rule public Troubleshooter troubleshooter = Troubleshooter.enabled();

		@Test
		@Enabled
		public void enabledMethod() {
			LogManager.getLogger().warn("", "enabledMethod");
		}
		
		@Test
		public void disabledMethod() {
			LogManager.getLogger().warn("", "disabledMethod");
		}
	}
	
	public static class TroubleshooterDisabledHostClass {
		@Rule public Troubleshooter troubleshooter = Troubleshooter.disabled();

		@Test
		@Enabled
		public void enabledMethod() {
			LogManager.getLogger().warn("", "enabledMethod");
		}
		
		@Test
		public void disabledMethod() {
			LogManager.getLogger().warn("", "disabledMethod");
		}
	}
}
