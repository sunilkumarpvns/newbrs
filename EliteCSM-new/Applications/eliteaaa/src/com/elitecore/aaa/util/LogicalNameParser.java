package com.elitecore.aaa.util;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;

public class LogicalNameParser {

	private static final String MODULE = "LOGICAL-NAME-PARSER";
	private Map<String, String> valueToLogicalName;
	
	public LogicalNameParser() {
		valueToLogicalName = new HashMap<String, String>();
	}
	
	public void parse(String input) {
		char[] charArray = input.toCharArray();
		
		StringBuilder keyBuilder = new StringBuilder();
		StringBuilder logicalNameBuilder = new StringBuilder();
		
		int balancedParenthesisCount = 0;
		
		
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];

			if (c == '(') {
				balancedParenthesisCount++;
			}

			if (c == ')') {
				balancedParenthesisCount--;
				if (balancedParenthesisCount == 0) {
					logicalNameBuilder.append(c);
					continue;
				}
			}

			if (balancedParenthesisCount != 0) {
				logicalNameBuilder.append(c);
			}

			if (c == ',' && balancedParenthesisCount == 0) {
				
				String value = keyBuilder.toString();
				String alias = logicalNameBuilder.toString();
				
				if (Strings.isNullOrBlank(value) == false || Strings.isNullOrBlank(alias) == false) {
					valueToLogicalName.put(value.trim(), alias);
					keyBuilder = new StringBuilder();
					logicalNameBuilder = new StringBuilder();	
				}
			} else if (c != ')' && c !='(' && balancedParenthesisCount == 0) {
				keyBuilder.append(c);
			}
		}
		
		String value = keyBuilder.toString();
		String alias = logicalNameBuilder.toString();
		if (isParenthesisBalanced(balancedParenthesisCount) == false) {
			LogManager.getLogger().info(MODULE, "Unbalanced parenthesis found in input string: " + input);
			LogManager.getLogger().info(MODULE, "Skipped string is: " + value + alias);
		} else {
			
			if (Strings.isNullOrBlank(value) == false) {
				valueToLogicalName.put(value.trim(), alias);
				keyBuilder = new StringBuilder();
				logicalNameBuilder = new StringBuilder();	
			}
		}
	}

	private boolean isParenthesisBalanced(int balancedParenthesisCount) {
		return balancedParenthesisCount == 0;
	}
	
	public String getValue(String key) {
		return this.valueToLogicalName.get(key);
	}
	
	public Map<String, String> getValueToLogicalName() {
		return valueToLogicalName;
	}
}