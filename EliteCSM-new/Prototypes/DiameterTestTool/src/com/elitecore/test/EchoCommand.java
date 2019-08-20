package com.elitecore.test;

import com.elitecore.test.command.Command;

public class EchoCommand implements Command {
	
	private EchoCommandData data;

	public EchoCommand(EchoCommandData data) {
		this.data = data;
	}
	
	@Override
	public void execute(ExecutionContext context) throws Exception {
		System.out.println(data.getMessage());
	}

	@Override
	public String getName() {
		return "echo";
	}

}
