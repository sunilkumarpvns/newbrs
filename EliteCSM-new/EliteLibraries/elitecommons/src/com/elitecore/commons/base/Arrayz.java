package com.elitecore.commons.base;

/**
 * A set of common utilities for arrays
 * 
 * @author narendra.pathai
 *
 */
public class Arrayz {

	/**
	 * Returns true if the array passed is {@code null} or has no elements
	 * 
	 * @param array a nullable array to be checked
	 * @return true if array is null or has no elements
	 */
	public static <T> boolean isNullOrEmpty(T...array) {
		return array == null || array.length == 0;
	}
}
