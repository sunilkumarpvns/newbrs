package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.ExecutionContextValueProvider;
import com.elitecore.test.command.data.LoopCommandData.LoopCondition;

import java.util.List;

public class LoopCommand extends CompositeCommand {

	private static final String MODULE = null;
	private LoopCondition condition;
	private String name;

	public LoopCommand(List<Command> commands,LoopCondition condition,String name) {
		super(name, commands);
		this.condition = condition;
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing loop("+ getName() +") command");
		
		if(context.getProcess().isStopRequested()){
			return;
		}
		int count=0;
		ValueProvider valueProvider = new ExecutionContextValueProvider(context);
		while(condition.cotinuee(count, valueProvider)){
			for(Command command : commands){
				if(context.getProcess().isStopRequested()){
					getLogger().debug(MODULE, "stop requested. Reason:" + context.getProcess().getResonse());
					return;
				}
				command.execute(context);
			}
			count++;
		}
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Loop("+ getName() +") command execution completed");

	}

	@Override
	public String getName() {
		return name;
	}

}
