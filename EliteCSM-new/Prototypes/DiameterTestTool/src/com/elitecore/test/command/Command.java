package com.elitecore.test.command;

import com.elitecore.test.ExecutionContext;

public interface Command {
	
	public void execute(ExecutionContext context) throws Exception;
	public String getName();

}
