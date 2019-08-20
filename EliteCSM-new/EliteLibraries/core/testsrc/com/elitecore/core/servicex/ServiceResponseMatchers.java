package com.elitecore.core.servicex;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * 
 * Hamcrest matchers for common {@link ServiceResponse} assertions.
 * 
 * @author narendra.pathai
 *
 */
public class ServiceResponseMatchers {

	/**
	 * @return a matcher that checks whether drop flag is set.
	 */
	public static Matcher<? super ServiceResponse> isDropped() {
		return new TypeSafeDiagnosingMatcher<ServiceResponse>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("a dropped response");
			}

			@Override
			protected boolean matchesSafely(ServiceResponse appResponse,
					Description description) {
				if (!appResponse.isMarkedForDropRequest()) {
					description.appendText(" was not marked for drop.");
					return false;
				}
				return true;
			}
		};
	}
}
