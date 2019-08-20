package com.elitecore.commons.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Predicates;

/**
 * <p>
 * JUnit rule that helps developers in troubleshooting selected hierarchical test cases. It should only be enabled
 * in local code and must never be committed as enabled in production test code. 
 * 
 * <p>
 * For running a single test case, we had a few options:
 * 1) Comment all other test cases in the class but that is so tedious and error prone.
 * 2) Use IDE feature to run select single test case and run, but that does not work with Hierarchical test cases.
 * 
 * <p>
 * Troubleshooter helps here by allowing you to enable single test case by just placing an annotation {@link Enabled} 
 * on the methods that must be run. The rule will not run other cases and just <i>fail</i> them. 
 * 
 * <pre>
 * <code>
 * public class MyTest {
 * 
 * @Rule public Troubleshooter troubleshooter = Troubleshooter.enabled();
 * 	
 * @Enabled
 * @Test
 * public void testCausingTrouble() {
 * 	// some trouble causing code
 * }
 *  
 * @Test
 * public void disabledTest() {
 * 	// will not be run
 * }
 * }
 * </code>
 * </pre>
 * 
 * @author narendra.pathai
 */
public class Troubleshooter implements MethodRule {

	private Predicate<FrameworkMethod> predicate;

	public Troubleshooter(Predicate<FrameworkMethod> predicate) {
		this.predicate = predicate;
	}
	
	/**
	 * @return a disabled troubleshooter instance. It stays inactive and all test cases are run.
	 */
	public static Troubleshooter disabled() {
		return new Troubleshooter(Predicates.<FrameworkMethod>alwaysTrue());
	}

	/**
	 * @return an enabled troubleshooter instance that only runs methods that are marked with {@link Enabled}
	 * annotation. Note that it fails other test cases forcefully to avoid mistakenly being committed in enabled 
	 * state.
	 */
	public static Troubleshooter enabled() {
		return new Troubleshooter(new Predicate<FrameworkMethod>() {

			@Override
			public boolean apply(FrameworkMethod input) {
				return input.getAnnotation(Enabled.class) != null;
			}
		});
	}

	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				if (predicate.apply(method)) {
					base.evaluate();
				} else {
					throw new AssertionError("Disabled due to troubleshooting");
				}
			}
		};
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Enabled {
		
	}
}
