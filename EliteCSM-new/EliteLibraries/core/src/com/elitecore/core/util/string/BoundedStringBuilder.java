package com.elitecore.core.util.string;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.io.IOException;

public class BoundedStringBuilder implements Appendable, CharSequence {
	
	private StringBuilder stringBuilder;
	private int maxLimit;

	public BoundedStringBuilder(int maxLimit) {
		this.maxLimit = maxLimit;
		this.stringBuilder = new StringBuilder();
	}

	/**
	 * Appends the specified string to this string builder without trimming.<br><br>
	 * <p>
     * The characters of the <code>String</code> argument are appended, in 
     * order, increasing the length of this sequence by the length of the 
     * argument. If <code>str</code> is <code>null</code>, then the four 
     * characters <code>"null"</code> are appended.
     * <p>
     * Let <i>n</i> be the length of this character sequence just prior to 
     * execution of the <code>append</code> method. Then the character at 
     * index <i>k</i> in the new character sequence is equal to the character 
     * at index <i>k</i> in the old character sequence, if <i>k</i> is less 
     * than <i>n</i>; otherwise, it is equal to the character at index 
     * <i>k-n</i> in the argument <code>str</code>.
     * <p>
     * <i>Note: check the accomodability of <code>str</code> before calling this method</i>.
     * 
	 * @param str - a string
	 * @return a reference to this object
	 * 
	 * 
	 */
	public BoundedStringBuilder append(String str) {
		stringBuilder.append(str);
		return this;
	}

	public int length() {
		return stringBuilder.length();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
     * <i>Note: check the accomodability of <code>str</code> before calling this method</i>.
	 */
	@Override
	public Appendable append(CharSequence csq) throws IOException {
		return stringBuilder.append(csq);
	}

	/**
	 * {@inheritDoc}
	 * <p>
     * <i>Note: check the accomodability of <code>str</code> before calling this method</i>.
	 */
	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		return stringBuilder.append(csq, start, end);
	}

	/**
	 * {@inheritDoc}
	 * <p>
     * <i>Note: check the accomodability of <code>str</code> before calling this method</i>.
	 */
	@Override
	public Appendable append(char c) throws IOException {
		return stringBuilder.append(c);
	}

	@Override
	public char charAt(int index) {
		return stringBuilder.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return stringBuilder.subSequence(start, end);
	}
	
	@Override
	public String toString() {
		return stringBuilder.toString();
	}

	/**
	 * Checks whether the specified string is accomodable in this string builder based on the given maximum limit
	 * and the size of the strings that are appended till now.
	 * @param str - a string
	 * @return true - if <code>str</code> is accomodable in this string builder<br>
	 * false - if <code>str</code> is not accomodable in this string builder
	 */
	public boolean isAccomodable(String str) {
		checkNotNull(str, "str is null");
		return maxLimit >= (stringBuilder.length() + str.length());
	}
	
	/**
	 * Checks whether the specified character sequence is accomodable in this string builder based on the given maximum limit
	 * and the size of the character sequences that are appended till now.
	 * @param csq - character sequence
	 * @return true - if <code>csq</code> is accomodable in this string builder<br>
	 * false - if <code>csq</code> is not accomodable in this string builder
	 */
	public boolean isAccomodable(CharSequence csq) {
		checkNotNull(csq, "csq is null");
		return maxLimit >= (stringBuilder.length() + csq.length());
	}
	
	public int indexOf(String str) {
		checkNotNull(str, "str is null");
		return stringBuilder.indexOf(str, 0);
	}

	/**
	 * refer to
	 * {@link AbstractStringBuilder #delete(int, int)}
	 * 
	 */
	public BoundedStringBuilder delete (int start, int end) {
	    stringBuilder.delete(start, end);
	    return this;
	}
}


