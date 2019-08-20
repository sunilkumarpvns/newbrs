package com.elitecore.commons.base;


/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state. This allows constructs such as
 * <pre>
 *     if (count <= 0) {
 *       throw new IllegalArgumentException("must be positive: " + count);
 *     }</pre>
 *
 * to be replaced with the more compact
 * <pre>
 *     checkArgument(count > 0, "must be positive: %s", count);
 * </pre>
 *
 * Use of check nulls can be used before assigning the references for fail fast approach such as
 * <pre>
 *     class MyClass{
 *      public MyClass(Object arg1, Object arg2){
 *       this.arg1 = checkNotNull(arg1,"arg1 is null");
 *       this.arg2 = checkNotNull(arg2,"arg2 is null");
 *      }
 *     }</pre>
 * 
 * <p><b>NOTE:</b> Best when used as static imports.
 * 
 * @author narendra.pathai
 *
 */
public class Preconditions {
	private Preconditions(){}

	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not null.
	 *
	 * @param reference an object reference
	 * @param errorMessage the exception message to use if the check fails; will be converted to a
	 *     string using {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws NullPointerException if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		return reference;
	}

	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 *
	 * @param expression a boolean expression
	 * @param errorMessage the exception message to use if the check fails; will be converted to a
	 *     string using {@link String#valueOf(Object)}
	 * @throws IllegalArgumentException if {@code expression} is false
	 */
	public static void checkArgument(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(String.valueOf(errorMessage));
		}
	}
	
	/**
	 * Ensures the truth of an expression involving the state of the calling instance, but not
	 * involving any parameters to the calling method.
	 *
	 * @param expression a boolean expression
	 * @param errorMessage the exception message to use if the check fails; will be converted to a
	 *     string using {@link String#valueOf(Object)}
	 * @throws IllegalStateException if {@code expression} is false
	 */
	public static void checkState(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new IllegalStateException(String.valueOf(errorMessage));
		}
	}
}
