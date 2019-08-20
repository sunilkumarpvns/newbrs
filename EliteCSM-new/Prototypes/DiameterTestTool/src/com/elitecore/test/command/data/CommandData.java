package com.elitecore.test.command.data;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;


public interface CommandData {
	
	public Command create(ScenarioContext context) throws Exception;
	public String getName();
}
