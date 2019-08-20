package com.elitecore.test;


public class ProcessHalter implements ErrorHandler {
	
	private Process process;

	public ProcessHalter(Process process) {
		super();
		this.process = process;
	}




	@Override
	public void handleError(Throwable ex) {
		process.stop(ex.getMessage());
	}

}
