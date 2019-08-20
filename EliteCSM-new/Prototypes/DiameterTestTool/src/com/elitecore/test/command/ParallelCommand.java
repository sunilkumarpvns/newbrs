package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.elitecore.test.ErrorHandler;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.ProcessHalter;
import com.elitecore.test.util.AsynchTask;

public class ParallelCommand extends CompositeCommand implements Command {

	private static final String MODULE = "PARALLEL-CMD";
	public boolean haltOnError= true;
	
	public ParallelCommand(String name, List<Command> commands) {
		super(name, commands);
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing parallel("+getName()+") command");
		
		if(context.getProcess().isStopRequested()){
			return;
		}
		context.getTaskScheduler().scheduleSingleExecutionTask(new ParellelTask(0,TimeUnit.SECONDS,
				haltOnError == true ? new ProcessHalter(context.getProcess()) : null,
				context, 
				commands)
		);
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Parallel("+getName()+") command execution completed");
	}
	
	private class ParellelTask extends AsynchTask {
		private ExecutionContext contex;
		private List<Command> commands;

		public ParellelTask(long delay, TimeUnit timeUnit,
				ErrorHandler handler, ExecutionContext contex,
				List<Command> commands) {
			super(delay, timeUnit, handler);
			this.contex = contex;
			this.commands = commands;
		}

		@Override
		public long getInitialDelay() {
			return 0;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}

		@Override
		public void run() throws Exception{
			for (Command command : commands) {
				command.execute(contex);
			}
		}
		
	}

	@Override
	public String getName() {
		return name;
	}

}
