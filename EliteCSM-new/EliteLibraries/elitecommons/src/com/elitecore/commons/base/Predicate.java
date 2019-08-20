package com.elitecore.commons.base;

/**
 * Determines a true or false value for a given input.
 *  
 * @author Kuldeep Panchal
 * @author narendra.pathai 
 *
 * @param <T> type of input parameter 
 */
public interface Predicate<T> {
	/**
	 * Returns the result of applying this predicate to input. This method is generally expected, but not absolutely required, to have the following properties:
	 * 
	 * - Its execution does not cause any observable side effects.
	 * - The computation is consistent with equals; that is, {@link Equality#areEqual(Object, Object)} implies that predicate.apply(a) == predicate.apply(b)).
	 *  
	 * @param input - object in which predicate is to be applied
	 * @throws NullPointerException - if input is null and this predicate does not accept null arguments 
	 */
	public boolean apply(T input);
}
