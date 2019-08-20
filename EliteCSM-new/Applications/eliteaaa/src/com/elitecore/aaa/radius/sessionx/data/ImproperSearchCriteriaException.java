package com.elitecore.aaa.radius.sessionx.data;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class ImproperSearchCriteriaException extends Exception {
	
	private List<String> missingAttributes;

	public ImproperSearchCriteriaException(String string) {
		super(string);
		this.missingAttributes = Collections.emptyList();
	}
	
	public ImproperSearchCriteriaException(String string, List<String> missingAttributes) {
		super(string);
		this.missingAttributes = missingAttributes;
	}
	
	public List<String> getMissingAttributes() {
		return missingAttributes;
	}
}
