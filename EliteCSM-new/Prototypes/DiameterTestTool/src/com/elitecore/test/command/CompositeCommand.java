package com.elitecore.test.command;

import java.util.List;

public abstract class CompositeCommand implements Command {
	
	protected final List<Command> commands;
	protected final String name;

	public CompositeCommand(String name, List<Command> commands) {
		super();
		this.name = name;
		this.commands = commands;
	}

	

}
