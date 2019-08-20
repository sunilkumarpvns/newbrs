package com.elitecore.aaa.diameter.service.application;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * 
 * Hamcrest matchers for common {@link ApplicationResponse} assertions.
 * 
 * @author narendra.pathai
 *
 */
public class ApplicationResponseMatchers {

	/**
	 * @return a matcher that checks whether application response is proxiable.
	 */
	public static Matcher<? super ApplicationResponse> isProxiable() {
		return new TypeSafeDiagnosingMatcher<ApplicationResponse>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("a proxiable answer");
			}

			@Override
			protected boolean matchesSafely(ApplicationResponse appResponse,
					Description description) {
				if (!appResponse.getDiameterAnswer().isProxiable()) {
					description.appendText(" was not proxiable.");
					return false;
				}
				return true;
			}
		};
	}
}
