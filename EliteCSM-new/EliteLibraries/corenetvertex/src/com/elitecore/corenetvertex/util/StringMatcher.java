package com.elitecore.corenetvertex.util;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class StringMatcher {

	/**
	 * 
	 * 
	 * @param sourceString
	 * @param pattern
	 * @return
	 * 
	 * @throws NullPointerException
	 *             , when any parameter is null
	 */
	public static boolean matches(String sourceString, String pattern) {
		return matches(sourceString, pattern.toCharArray());
	}

	public static boolean matches(String sourceString, char[] pattern) {
		int stringOffset = 0;
		char[] stringCharArray = sourceString.toCharArray();
		final int stringLen = stringCharArray.length;
		final int patternLen = pattern.length;
		int currentPos = 0;
		try {
			for (currentPos = 0; currentPos < patternLen; currentPos++, stringOffset++) {
				if (stringOffset == stringLen) {
					while (currentPos < patternLen) {
						if (pattern[currentPos] != '*')
							return false;
						currentPos++;
					}
					return true;
				}

				if (pattern[currentPos] != stringCharArray[stringOffset]) {
					if (pattern[currentPos] == '\\') {
						currentPos++;
						if (pattern[currentPos] != stringCharArray[stringOffset])
							return false;
						else
							continue;
					}
					if (pattern[currentPos] == '*') {
						boolean bStar = true;
						currentPos++;
						if (currentPos == patternLen)
							return true;
						while (bStar) {
							int tmpCurrentPos = currentPos;
							// go to first matching occurrence
							while (stringCharArray[stringOffset] != pattern[tmpCurrentPos]) {
								stringOffset++;
								if (stringOffset == stringLen) {
									while (tmpCurrentPos < patternLen) {
										if (pattern[tmpCurrentPos] != '*')
											return false;
										tmpCurrentPos++;
									}
									return true;
								}
							}
							// match whole string until next * come
							while (tmpCurrentPos < patternLen) {
								if (pattern[tmpCurrentPos] != stringCharArray[stringOffset]) {
									if (pattern[tmpCurrentPos] == '*') {
										bStar = false;
										currentPos = tmpCurrentPos - 1;
										stringOffset--;
										break;
									} else if (pattern[tmpCurrentPos] != '?') {
										break;
									}
								}
								tmpCurrentPos++;
								stringOffset++;
								if (stringOffset == stringLen) {
									while (tmpCurrentPos < patternLen) {
										if (pattern[tmpCurrentPos] != '*') {
											return false;
										}
										tmpCurrentPos++;
									}
									return true;
								}
							}
							if (stringOffset == stringLen && tmpCurrentPos == patternLen)
								return true;
						}
						continue;
					}
					if (pattern[currentPos] == '?') {
						continue;
					}
					return false;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) { 
			ignoreTrace(e);
			return false;
		}
		if (currentPos < patternLen) {
			while (pattern[currentPos] == '*')
				currentPos++;
		}
		return (currentPos == patternLen && stringOffset == stringLen);
	}
}
