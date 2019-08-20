package com.elitecore.commons.base;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A set of commonly used utilities for {@link String}s
 * 
 * @author narendra.pathai
 * 
 */
public class Strings {

	/**
	 * A function that encodes any Object string value i.e.
	 * {@code String.valueOf(object)}, within single-quotes (')
	 */
	public static final Function<Object, String> WITHIN_SINGLE_QUOTES = new WithinSingleQuoteFunction();
	private static final Function<Object, String> TO_STRING_FUNCTION = new ToStringFunction();
	public static final String NOT_APPLICABLE = "-NA-";

	/**
	 * Returns {@code true} if the string is {@code null} or is an empty string. <br/>
	 * <br/>
	 * <b>NOTE:</b> Does not consider any whitespace characters in the string as
	 * empty. If you want to consider whitespace characters as effectively empty
	 * then use {@link Strings#isNullOrBlank(String)} instead.
	 * 
	 * <br/>
	 * <br/>
	 * Example: <br/> {@code Strings.isNullOrEmpty(null)} or
	 * {@code Strings.isNullOrEmpty("")} will return true
	 * 
	 * @param inputString
	 *            string reference to check
	 * @return {@code true} if the string is null or is an empty string
	 */
	public static boolean isNullOrEmpty(String inputString) {
		return inputString == null || inputString.length() == 0;
	}

	/**
	 * Returns {@code true} if the optional string is <i>absent</i> or is an
	 * empty string. <br/>
	 * <br/>
	 * <b>NOTE:</b> Does not consider any whitespace characters in the string as
	 * empty. If you want to consider whitespace characters as effectively empty
	 * then use {@link Strings#isAbsentOrBlank(String)} instead.
	 * 
	 * <br/>
	 * <br/>
	 * Example: <br/> {@code Strings.isAbsentOrEmpty(Optional.absent())} or
	 * {@code Strings.isNullOrEmpty(Optional.of("")} will return true
	 * 
	 * @param a
	 *            non-null optional inputString string reference to check
	 * @return {@code true} if the optional string is absent or is an empty
	 *         string
	 */
	public static boolean isAbsentOrEmpty(Optional<String> optionalInputString) {
		return optionalInputString.isPresent() == false
				|| optionalInputString.get().length() == 0;
	}

	/**
	 * Returns {@code true} if the string is {@code null} or is a blank string.
	 * Considers any whitespace characters as blank. Trims the string to check
	 * for blankness.
	 * 
	 * <br/>
	 * <br/>
	 * <b>NOTE:</b> Does consider any whitespace characters in the string as
	 * empty. If you do not want to consider whitespace characters as
	 * effectively empty then use {@link Strings#isNullOrEmpty(String)} instead.
	 * 
	 * <br/>
	 * <br/>
	 * Example: <br/> {@code Strings.isNullOrBlank(null)} or
	 * {@code Strings.isNullOrBlank(" ")} will return true
	 * 
	 * @param inputString
	 *            string reference to check
	 * @return {@code true} if the string is null or is a blank string
	 *         (considering space, \t or other whitespace characters as blank)
	 */
	public static boolean isNullOrBlank(String inputString) {
		return inputString == null || inputString.trim().length() == 0;
	}

	/**
	 * Returns {@code true} if the optional string is <i>absent</i> or is a
	 * blank string. Considers any whitespace characters as blank. Trims the
	 * string to check for blankness.
	 * 
	 * <br/>
	 * <br/>
	 * <b>NOTE:</b> Does consider any whitespace characters in the string as
	 * empty. If you do not want to consider whitespace characters as
	 * effectively empty then use {@link Strings#isAbsentOrEmpty(String)}
	 * instead.
	 * 
	 * <br/>
	 * <br/>
	 * Example: <br/> {@code Strings.isAbsentOrBlank(Optinal.absent())} or
	 * {@code Strings.isAbsentOrBlank(Optional.of(" ")} will return true
	 * 
	 * @param a
	 *            non-null optional inputString string reference to check
	 * @return {@code true} if the optional string is absent or is a blank
	 *         string (considering space, \t or other whitespace characters as
	 *         blank)
	 * @throws NullPointerException
	 *             if inputString is null
	 */
	public static boolean isAbsentOrBlank(Optional<String> optionalInputString) {
		return optionalInputString.isPresent() == false
				|| optionalInputString.get().trim().length() == 0;
	}

	/**
	 * Returns a string consisting of a specific number of concatenated copies
	 * of an input string. For example, {@code repeat("hey", 3)} returns the
	 * string {@code "heyheyhey"}.
	 * 
	 * @param string
	 *            any non-null string
	 * @param count
	 *            the number of times to repeat it; a nonnegative integer
	 * @return a string containing {@code string} repeated {@code count} times
	 *         (the empty string if {@code count} is zero)
	 * @throws IllegalArgumentException
	 *             if {@code count} is negative
	 */
	public static String repeat(String string, int count) {
		checkNotNull(string, "string is null");

		if (count <= 1) {
			checkArgument(count >= 0, "invalid count: " + count);
			return (count == 0) ? "" : string;
		}

		final int len = string.length();
		final long longSize = (long) len * (long) count;
		final int size = (int) longSize;
		if (size != longSize) {
			throw new ArrayIndexOutOfBoundsException(
					"Required array size too large: " + longSize);
		}

		final char[] array = new char[size];
		string.getChars(0, len, array, 0);
		int n;
		for (n = len; n < size - n; n <<= 1) {
			System.arraycopy(array, 0, array, n, n);
		}
		System.arraycopy(array, 0, array, n, size - n);
		return new String(array);
	}

	/**
	 * Joins the String representation ( {@code String.valueOf(..)} ) of each
	 * part given in {@code parts} array using the separator provided.
	 * 
	 * <p>
	 * Example: <code>
	 *   Strings.join(",", new String[]{"a","b"})
	 * }</code> will return {@code a,b}
	 * <p>
	 * <b>NOTE:</b> This method skips any {@code null} parts encountered.
	 * 
	 * <p>
	 * Example: {@code Strings.join(",", new String[] "a",null,"b"})} will
	 * return {@code a,b}
	 * 
	 * @param separator
	 *            any non-null string
	 * @param parts
	 *            are the objects whose string representation will be joined
	 *            using separator
	 * @return a string containing the string representation of each part with
	 *         separator in between each
	 */
	public static String join(String separator, Object[] parts) {
		return join(separator, parts, TO_STRING_FUNCTION);
	}

	/**
	 * Joins the String representation of each part in {@code parts} by applying
	 * {@code partFunction} on it, using the separator provided.
	 * 
	 * <p>
	 * Example: <code>
	 *   Strings.join(",", new String[]{"a","b"}, Strings.WITHIN_SINGLE_QUOTES)
	 * }</code> will return {@code 'a','b'}
	 * <p>
	 * <b>NOTE:</b> This method skips any {@code null} parts encountered.
	 * 
	 * <p>
	 * Example: {@code Strings.join(",", new String[] "a",null,"b"},
	 * Strings.WITHIN_SINGLE_QUOTES)} will return {@code 'a','b'}
	 * 
	 * @param separator
	 *            any non-null string
	 * @param parts
	 *            are the objects whose string representation will be joined
	 *            using separator
	 * @param partFunction
	 *            a non-null function that will be applied to all non-null parts
	 * @return a string containing the string representation of each part with
	 *         separator in between each
	 */
	public static <T> String join(String separator, T[] parts,
			Function<? super T, String> partFunction) {
		checkNotNull(parts, "parts are null");
		return join(separator, Arrays.asList(parts), partFunction);
	}

	/**
	 * @see Strings#join(String, Object[])
	 */
	public static <T> String join(String string, Iterable<T> parts) {
		return join(string, parts, TO_STRING_FUNCTION);
	}

	/**
	 * @see Strings#join(String, Object[], Function)
	 */
	public static <T> String join(String string, Iterable<T> parts,
			Function<? super T, String> partFunction) {
		checkNotNull(parts, "parts are null");
		return join(string, parts.iterator(), partFunction);
	}

	private static <T> String join(String separator, Iterator<T> parts,
			Function<? super T, String> partFunction) {
		checkNotNull(separator, "separator is null");
		checkNotNull(partFunction, "partFunction is null");

		StringBuilder builder = new StringBuilder();

		while (parts.hasNext()) {
			T part = parts.next();
			if (part != null) {
				builder.append(partFunction.apply(part));
				break;
			}
		}

		while (parts.hasNext()) {
			T part = parts.next();
			if (part != null) {
				builder.append(separator);
				builder.append(partFunction.apply(part));
			}
		}
		return builder.toString();
	}

	/**
	 * Returns a string, of length at least {@code minLength}, consisting of
	 * {@code string} prepended with as many copies of {@code padChar} as are
	 * necessary to reach that length. For example,
	 * 
	 * <ul>
	 * <li>{@code padStart("7", 3, '0')} returns {@code "007"}
	 * <li>{@code padStart("2010", 3, '0')} returns {@code "2010"}
	 * </ul>
	 * 
	 * @param string
	 *            the string which should appear at the end of the result
	 * @param minLength
	 *            the minimum length the resulting string must have. Can be zero
	 *            or negative, in which case the input string is always
	 *            returned.
	 * @param padChar
	 *            the character to insert at the beginning of the result until
	 *            the minimum length is reached
	 * @return the padded string
	 */
	public static String padStart(String string, int minLength, char padChar) {
		checkNotNull(string, "string is null");
		if (string.length() >= minLength) {
			return string;
		}

		int padCount = minLength - string.length();
		StringBuilder builder = new StringBuilder(minLength);
		for (int i = 0; i < padCount; i++) {
			builder.append(padChar);
		}

		builder.append(string);
		return builder.toString();
	}

	/**
	 * Returns a string, of length at least {@code minLength}, consisting of
	 * {@code string} appended with as many copies of {@code padChar} as are
	 * necessary to reach that length. For example,
	 * 
	 * <ul>
	 * <li>{@code padEnd("4.", 5, '0')} returns {@code "4.000"}
	 * <li>{@code padEnd("2010", 3, '!')} returns {@code "2010"}
	 * </ul>
	 * 
	 * @param string
	 *            the string which should appear at the beginning of the result
	 * @param minLength
	 *            the minimum length the resulting string must have. Can be zero
	 *            or negative, in which case the input string is always
	 *            returned.
	 * @param padChar
	 *            the character to append to the end of the result until the
	 *            minimum length is reached
	 * @return the padded string
	 */
	public static String padEnd(String string, int minLength, char padChar) {
		checkNotNull(string, "string is null");
		if (string.length() >= minLength) {
			return string;
		}

		int padCount = minLength - string.length();
		StringBuilder builder = new StringBuilder(minLength);
		builder.append(string);
		for (int i = 0; i < padCount; i++) {
			builder.append(padChar);
		}

		return builder.toString();
	}

	static class WithinSingleQuoteFunction implements Function<Object, String> {

		private static final String SINGLE_QUOTE = "'";

		@Override
		public String apply(Object input) {
			return SINGLE_QUOTE + TO_STRING_FUNCTION.apply(input)
					+ SINGLE_QUOTE;
		}
	}

	static class ToStringFunction implements Function<Object, String> {

		@Override
		public String apply(Object input) {
			return String.valueOf(input);
		}
	}

	/**
	 * Provides a splitter for given separator.
	 * 
	 * @param separator
	 *            on which split is done.
	 * 
	 * @return Splitter on Separator.
	 * @see Splitter
	 */
	public static Splitter splitter(final char separator) {
		return Splitter.on(separator);
	}

	/**
	 * Returns predicate which evaluates to true if the string passed is
	 * non-null and non-empty. It can be used in other utilities such as
	 * {@link Collectionz#filter(java.util.Collection, Predicate)}
	 * 
	 * <p>
	 * Usage:
	 * 
	 * <pre>
	 * <code>Collectionz.filter(unfilteredCollection, Strings.nonNullAndNonEmpty())
	 * </code>
	 * </pre>
	 * 
	 * @return predicate which evaluates to true if the string passed is
	 *         non-null and non-empty
	 * @see Predicate
	 * @see Collectionz
	 */
	public static Predicate<String> nonNullAndNonEmpty() {
		return NonNullAndNonEmpty.INSTANCE;
	}

	private static enum NonNullAndNonEmpty implements Predicate<String> {
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return isNullOrEmpty(input) == false;
		}
	}

	/**
	 * Returns predicate which evaluates to true if the string passed is
	 * non-null and non-blank (whitespace characters are considered as empty).
	 * It can be used in other utilities such as
	 * {@link Collectionz#filter(java.util.Collection, Predicate)}
	 * 
	 * <p>
	 * Usage:
	 * 
	 * <pre>
	 * <code>Collectionz.filter(unfilteredCollection, Strings.nonNullAndNonBlank())
	 * </code>
	 * </pre>
	 * 
	 * @return predicate which evaluates to true if the string passed is
	 *         non-null and non-blank
	 * @see Predicate
	 * @see Collectionz
	 */
	public static Predicate<String> nonNullAndNonBlank() {
		return NonNullAndNonBlank.INSTANCE;
	}

	private static enum NonNullAndNonBlank implements Predicate<String> {
		INSTANCE;

		@Override
		public boolean apply(String input) {
			return isNullOrBlank(input) == false;
		}
	}

	/**
	 * Filters all null or empty elements from the underlying {@code collection}
	 * 
	 * <p>
	 * <b>NOTE: </b>Does not remove blank elements, use
	 * {@link Strings#filterNullOrBlank(Collection)} for that.
	 * 
	 * <p>
	 * It has the same effect as calling
	 * {@code Collectionz.filter(collection, Strings.nonNullAndNonEmpty());}
	 * 
	 * @param collection
	 *            a non-null collection of strings
	 * @throws NullPointerException
	 *             if collection passed is null
	 */
	public static void filterNullOrEmpty(Collection<String> collection) {
		Collectionz.filter(collection, nonNullAndNonEmpty());
	}

	/**
	 * Filters all null or blank elements from the underlying {@code collection}
	 * , elements containing just whitespace characters are also removed from
	 * the collection.
	 * 
	 * <p>
	 * <b>NOTE: </b>For removing just empty elements, use
	 * {@link Strings#filterNullOrEmpty(Collection)} for that.
	 * 
	 * <p>
	 * It has the same effect as calling
	 * {@code Collectionz.filter(collection, Strings.nonNullAndNonBlank());}
	 * 
	 * @param collection
	 *            a non-null collection of strings
	 * @throws NullPointerException
	 *             if collection passed is null
	 */
	public static void filterNullOrBlank(Collection<String> collection) {
		Collectionz.filter(collection, nonNullAndNonBlank());
	}

	/**
	 * Returns {@code true} if trimmed input is true or yes, false otherwise.
	 * <p>
	 * Note that this method trims the input and performs a case insensitive match.
	 */
	public static boolean toBoolean(String booleanInString) {
		if (booleanInString == null) {
			return false;
		}
		
		String trimmedString = booleanInString.trim();
		return "true".equalsIgnoreCase(trimmedString)
				|| "yes".equalsIgnoreCase(trimmedString);
	}

	/**
	 * Returns a function that converts the string to integer. 
	 * <p>
	 * Note that the returned function throws {@code NumberFormatException} if the
	 * string does not contain a parseable integer.
	 * 
	 * @see Integer#parseInt(String)
	 */
	public static Function<String, Integer> toInt() {
		return ToIntFunction.INSTANCE;
	}
	
	private static enum ToIntFunction implements Function<String, Integer> {
		INSTANCE;
		
		@Override
		public Integer apply(String input) {
			return Integer.parseInt(input);
		}
	}

	/**
	 * Returns a function that converts the string to long. 
	 * <p>
	 * Note that the returned function throws {@code NumberFormatException} if the
	 * string does not contain a parseable long.
	 * 
	 * @see Long#parseLong(String)
	 */
	public static Function<String, Long> toLong() {
		return ToLongFunction.INSTANCE;
	}
	
	private static enum ToLongFunction implements Function<String, Long> {
		INSTANCE;
		
		@Override
		public Long apply(String input) {
			return Long.parseLong(input);
		}
	}

	/**
	 * @param input an object
	 * @return the string representation of <code>input</code> parameter. If the argument is <code>null</code>,
	 * then returns blank string <code>""</code>; otherwise the value of <code>input.toString()
	 */
	public static String valueOf(Object input) {
		return valueOf(input, "");
	}

	/**
	 * @param input an object
	 * @param placeholder a placeholder object
	 * @return the string representation of <code>input</code> parameter. If the argument is <code>null</code>,
	 * then returns the value of <code>placeholder.toString()</code>; otherwise the value of <code>input.toString()
	 */
	public static String valueOf(Object input, Object placeholder) {
		return input == null ? placeholder.toString() : input.toString();
	}
}
