package com.elitecore.netvertex.pm.exception;

import java.util.List;

public class BuildFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	private List<String> failReasons;
	
	public BuildFailedException(List<String> failReasons, Throwable cause) {
		super(failReasons.toString(), cause);
		this.failReasons = failReasons;
	}

	public BuildFailedException(List<String> failReasons) {
		super(failReasons.toString());
		this.failReasons = failReasons;
	}
	
	public List<String> getFailResons() {
		return failReasons;
	}

}
