package com.elitecore.commons.base;

import static com.elitecore.commons.base.Preconditions.checkArgument;

import com.elitecore.commons.logging.LogManager;


/**
 * 
 * A set of common number related utility methods.
 * 
 * @author narendra.pathai
 *
 */
public class Numbers {

	/**
	 * A predicate for positive integers. Evaluates to false if the Integer is null.
	 */
	public static final Predicate<Integer> POSITIVE_INT = new PositiveIntPredicate();
	/**
	 * A predicate for positive long value. Evaluates to false if the Long is null or zero.
	 */
	public static final Predicate<Long> POSITIVE_LONG = new PositiveLongPredicate();

	/**
	 * Returns a big-endian representation of {@code value} as an n byte array, where
	 * n is the {@code noOfBytes}. 
	 * 
	 * <p>Returns an empty array if {@code noOfBytes} is {@code 0}
	 * 
	 * <p> Example: {@code Numbers.toByteArray(0x1213,3)} will return a byte array
	 * {@code {0x00, 0x12, 0x13}}.
	 * 
	 * @param value any integer value
	 * @param noOfBytes non-negative number of bytes
	 * @return a big-endian representation of value as a byte array.
	 * @throws IllegalArgumentException if {@code noOfBytes} is negative
	 */
	public static byte[] toByteArray(int value, int noOfBytes) {
		checkArgument(noOfBytes >= 0, "noOfBytes cannot be negative");
		
		if (noOfBytes == 0) {
			return new byte[] {};
		}
		
		byte[] intBytes = new byte[noOfBytes];
		for (int i = noOfBytes-1; i >= 0; i--, value >>>= 8) {
			intBytes[i] = (byte)value;
		}
		return intBytes;
	}
	
	/**
	 * Returns a big-endian representation of {@code value} as an n byte array, where
	 * n is the {@code noOfBytes}. 
	 * 
	 * <p>Returns an empty array if {@code noOfBytes} is {@code 0}
	 * 
	 * <p> Example: {@code Numbers.toByteArray(0x12131415,4)} will return a byte array
	 * {@code {0x12, 0x13, 0x14, 0x15}}.
	 * 
	 * @param value any long value
	 * @param noOfBytes non-negative number of bytes
	 * @return a big-endian representation of value as a byte array.
	 * @throws IllegalArgumentException if {@code noOfBytes} is negative
	 */
	public static byte[] toByteArray(long value, int noOfBytes) {
		checkArgument(noOfBytes >= 0, "noOfBytes cannot be negative");
		
		if (noOfBytes == 0) {
			return new byte[] {};
		}
		
		byte[] longBytes = new byte[noOfBytes];
		for (int i = noOfBytes-1; i >= 0; i--, value >>>= 8) {
			longBytes[i] = (byte)value;
		}
		return longBytes;
	}
	
	/**
	 * Parses the string {@code value} using {@link Integer#parseInt(String)} and returns
	 * the parsed value if parsing was successful or returns {@code otherwise} instead.
	 * 
	 * @param value string containing integer representation to be parsed
	 * @param otherwise value to be returned if parsing causes {@code NumberFormatException}
	 * @return parsed integer value on successful parsing or otherwise instead 
	 */
	public static int parseInt(String value, int otherwise) {
		return parseInt(value, Predicates.<Integer>alwaysTrue(), otherwise);
	}
	
	/**
	 * Parses the string {@code value} using {@link Integer#parseInt(String)} and returns
	 * the parsed value if parsing was successful or returns {@code otherwise} instead.
	 * 
	 * @param value string containing integer representation to be parsed
	 * @param otherwise value to be returned if parsing causes {@code NumberFormatException}
	 * @return parsed integer value on successful parsing or otherwise instead
	 */
	public static Integer parseInt(String value, Integer otherwise) {
		return parseInt(value, Predicates.<Integer>alwaysTrue(), otherwise);
	}
	
	/**
	 * Parses the string {@code value} using {@link Long#parseLong(String)} and returns
	 * the parsed value if parsing was successful or returns {@code otherwise} instead.
	 * 
	 * @param value string containing long representation to be parsed
	 * @param otherwise value to be returned if parsing causes {@code NumberFormatException}
	 * @return parsed long value on successful parsing or otherwise instead
	 */
	public static long parseLong(String value, long otherwise) {
		long result = otherwise;
		try {
			result = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			LogManager.getLogger().trace(nfe);
		}
		return result;
	}

	/**
	 * Parses the string {@code value} using {@link Integer#parseInt(String)} and returns
	 * the parsed value if parsing was successful and the parsed value satisfies the
	 * predicate else returns {@code otherwise}.
	 * 
	 * <p>
	 * Usage: Parse a string which should be a positive integer
	 * <pre>
	 *  int parsedPositiveValue = Numbers.parseInt("100", Numbers.POSITIVE_INT, DEFAULT_VALUE);
	 * </pre>
	 * @param value string containing integer representation to be parsed
	 * @param predicate a non-null predicate that the parsed value must satisfy
	 * @param otherwise value to be returned if parsing causes {@code NumberFormatException}
	 * or the parsed value does not satisfy the predicate
	 * @return parsed integer value on successful parsing or otherwise instead 
	 */
	public static int parseInt(String value, Predicate<Integer> predicate, int otherwise) {
		int result = otherwise;
		try {
			result = Integer.parseInt(value);
			result = predicate.apply(result) ? result : otherwise;
		} catch (NumberFormatException nfe) {
			LogManager.getLogger().trace(nfe);
		}
		return result;
	}
	
	/**
	 * Parses the string {@code value} using {@link Integer#parseInt(String)} and returns
	 * the parsed value if parsing was successful and the parsed value satisfies the
	 * predicate else returns {@code otherwise}.
	 * 
	 * <p>
	 * Usage: Parse a string which should be a positive integer
	 * <pre>
	 *  Integer parsedPositiveValue = Numbers.parseInt("100", Numbers.POSITIVE_INT, DEFAULT_VALUE);
	 * </pre>
	 * @param value string containing integer representation to be parsed
	 * @param predicate a non-null predicate that the parsed value must satisfy
	 * @param otherwise value to be returned if parsing causes {@code NumberFormatException}
	 * or the parsed value does not satisfy the predicate
	 * @return parsed integer value on successful parsing or otherwise instead 
	 */
	public static Integer parseInt(String value, Predicate<Integer> predicate, Integer valueOtherwise) {
		Integer result = valueOtherwise;
		try {
			result = Integer.parseInt(value);
			result = predicate.apply(result) ? result : valueOtherwise;
		} catch (NumberFormatException nfe) {
			LogManager.getLogger().trace(nfe);
		}
		return result;
	}
	
	private static class PositiveIntPredicate implements Predicate<Integer> {
		@Override
		public boolean apply(Integer input) {
			return input != null ? input > 0 : false;
		}
	}
	
	private static class PositiveLongPredicate implements Predicate<Long> {
		@Override
		public boolean apply(Long input) {
			return input != null ? input.longValue() > 0 : false;
		}
	}
}
