package com.elitecore.test.command;

import java.util.List;

import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class Case {
	private LogicalExpression expression;
	
	private List<Command> commands;

	public List<Command> getCommands() {
		return commands;
	}

	public Case(LogicalExpression expression, List<Command> commands) {
		super();
		this.expression = expression;
		this.commands = commands;
	}
	
	public boolean evaluate(ValueProvider valueProvider){
		return expression.evaluate(valueProvider);
	}
 	
}
