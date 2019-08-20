package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import com.elitecore.test.ExecutionContext;
import com.elitecore.test.ExecutionContextValueProvider;

public class ConditionCommand extends CompositeCommand {
	
	private final List<Case> cases;
	private static final String MODULE = "CONDITION-CMD";
	
	public ConditionCommand(List<Case> cases, List<Command> commands, String name) {
		super(name, commands);
		this.cases = cases;
	}



	@Override
	public void execute(ExecutionContext context) throws Exception {
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing condition("+ getName() +") command");
		
		if(context.getProcess().isStopRequested()){
			return;
		}
		ExecutionContextValueProvider valueProvider = new ExecutionContextValueProvider(context);
		
		List<Command> commands = this.commands;
		for(Case casse : cases){
			if(context.getProcess().isStopRequested()){
				return;
			}
			if(casse.evaluate(valueProvider)){
				commands = casse.getCommands();
			}
		}
		
		for(Command command : commands){
			if(context.getProcess().isStopRequested()){
				return;
			}
			
			command.execute(context);
		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Condition("+ getName() +") command execution completed");
	}



	@Override
	public String getName() {
		return name;
	}

}
