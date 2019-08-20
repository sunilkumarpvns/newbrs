package com.elitecore.commons.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * <p>Splitter is a Utility for splitting Strings. 
 * This is an alternative to using StringTokenizer.</p>
 * 
 * @author monica.lulla
 *
 */
public class Splitter {

	private final char separator;
	private final boolean trimTokens;
	private final boolean preserveTokens;

	private Splitter(char separator, boolean trimTokens, boolean preserveTokens) {
		this.separator = separator;
		this.preserveTokens = preserveTokens;
		this.trimTokens = trimTokens;
	}

	/**
	 * Provides a Splitter on the given Separator.
	 * 
	 * @param separator the separate character for Splitting. 
	 * The separator is not included in the returned list of Strings.
	 * @return Splitter for provided separator.
	 */
	public static Splitter on(char separator) {
		return new Splitter(separator, false, false);	
	}

	/**
	 * Enables trim token property in Splitter.
	 * 
	 * Splitting with such Splitter, gives, 
	 * (where separator = ',' preserveToken = false)
	 * <pre>
	 * split(" ")               = []
	 * split(" a , b , c ")     = ["a", "b", "c"]
	 * </pre>
	 *  
	 * @return Splitter that trims all the Parsed Strings. 
	 */
	public Splitter trimTokens() {
		return new Splitter(this.separator, true, this.preserveTokens);
	}

	/**
	 * Enables preserve token property in Splitter, 
	 * such that adjacent separators are
	 * treated as empty token separators.
	 * 
	 * Splitting with such Splitter, gives,
	 * (where separator = ',' trimToken = false)
	 * <pre>
	 * split(",,")         = ["", "", ""]
	 * split("a,,b,c")     = ["a", "", "b", "c"]
	 * </pre>
	 *  
	 * @return Splitter that preserves all the Parsed Strings.
	 */
	public Splitter preserveTokens() {
		return new Splitter(this.separator, this.trimTokens, true);
	}

	/**
	 * Splits the provided string into a list 
	 * based on provided Separator and Parameters. 
	 * If there is no separator in input string, 
	 * List containing only one token (input string itself) is returned.
	 * 
	 * <pre>
	 * where separator = ',' trimToken = false, preserveToken = false
	 * split(null)        = []
	 * split("")          = []
	 * split("a,b,c")     = ["a", "b", "c"]
	 * </pre>
	 * @param input the String to split
	 * @return  a list of parsed Strings. Empty immutable list is returned 
	 * if input string is {@code null} or empty
	 */
	public List<String> split(String input) {

		if (Strings.isNullOrEmpty(input)) {
			return Collections.emptyList();
		}
		final List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		boolean hasSeparator = false;
		while (i < input.length()) {
			if (input.charAt(i) == separator) {
				String token = input.substring(start, i);
				if (trimTokens) {
					token = token.trim();
				}
				if(token.isEmpty() == false || preserveTokens){
					list.add(token);
				}
				hasSeparator = true;
				start = i + 1;
			}
			i++;
		}
		String token = input.substring(start, i);
		if (trimTokens) {
			token = token.trim();
		}
		if (token.isEmpty() == false || (hasSeparator && preserveTokens)) {
			list.add(token);
		}
		return list; 
	}

	/**
	 * Splits the provided string into a array of Strings 
	 * based on provided Separator and Parameters. 
	 * <pre>
	 * where separator = ',' trimToken = false, preserveToken = false
	 * split(null)        = []
	 * split("")          = []
	 * split("a,b,c")     = ["a", "b", "c"]
	 * </pre>
	 * @param input the String to split
	 * @return  a array of parsed Strings. Empty array is returned 
	 * if input string is {@code null} or empty
	 */
	public String[] splitToArray(String input) {
		return split(input).toArray(new String[] {});
	}
}