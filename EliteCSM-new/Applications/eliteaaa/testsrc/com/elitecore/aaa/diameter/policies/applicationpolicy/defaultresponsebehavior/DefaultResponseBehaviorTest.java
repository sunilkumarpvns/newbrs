package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.commons.tests.PrintMethodRule;

/**
 * 
 * @author vicky.singh
 * @author narendra.pathai
 *
 */
public class DefaultResponseBehaviorTest {
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	private static final String ANY_RESPONSE_BEHAVIOR_PARAMETER = "arbitraryString";
	private DefaultResponseBehavior behavior;

	@Test
	public void returnsAnInstanceOfRejectBehaviorOnReceivingRejectBehaviorCodeAndAnyResponseBehaviorParameter() {
		createBehavior(DefaultResponseBehaviorType.REJECT, ANY_RESPONSE_BEHAVIOR_PARAMETER);
		
		assertBehaviorIsInstanceOf(RejectBehavior.class);
	}
	
	@Test
	public void returnsAnInstanceOfDropBehaviorOnReceivingDropBehaviorCodeAndAnyResponseBehaviorParameter() {
		createBehavior(DefaultResponseBehaviorType.DROP, ANY_RESPONSE_BEHAVIOR_PARAMETER);
		
		assertBehaviorIsInstanceOf(DropBehavior.class);
	}
	
	@Test
	public void returnsAnInstanceOfHotlineBehaviorOnReceivingHotlineBehaviorCodeAndAnyResponseBehaviorParameter() {
		createBehavior(DefaultResponseBehaviorType.HOTLINE, ANY_RESPONSE_BEHAVIOR_PARAMETER);
		
		assertBehaviorIsInstanceOf(HotlineBehavior.class);
	}

	private void createBehavior(DefaultResponseBehaviorType responseBehavior, String responseBehaviorParameter) {
		behavior = DefaultResponseBehavior.create(responseBehavior, ANY_RESPONSE_BEHAVIOR_PARAMETER);
	}

	private void assertBehaviorIsInstanceOf(Class<? extends DefaultResponseBehavior> expectedBehavior) {
		assertThat(behavior, CoreMatchers.instanceOf(expectedBehavior));
	}
}


