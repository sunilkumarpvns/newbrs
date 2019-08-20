package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.elitecore.test.ErrorHandler;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.ProcessHalter;
import com.elitecore.test.util.AsynchTask;

public class AnyOrderedCommand extends CompositeCommand implements Command {

	private static final String MODULE = "ANY-ORDER-CMD";

	public AnyOrderedCommand(List<Command> commands,String name) {
		super(name, commands);
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing any-order("+ getName() +") command");
		
		CountDownLatch latch = new CountDownLatch(commands.size());
		for (Command command : commands) {
			if(context.getProcess().isStopRequested()){
				return;
			}
			context.getTaskScheduler().scheduleSingleExecutionTask(new UnOrderedTask(0,TimeUnit.SECONDS,new ProcessHalter(context.getProcess()),context,command,latch));
		}
		
		boolean result = latch.await(10,TimeUnit.SECONDS);
		if(result == false){
			context.getProcess().stop("Timeout for UnOrdered Task");
		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Any-order("+ getName() +") command execution completed");
	}
	
	private class UnOrderedTask extends AsynchTask {
		private ExecutionContext contex;
		private Command commad;
		private CountDownLatch countDownLatch;

		

		public UnOrderedTask(long delay, TimeUnit timeUnit,
				ErrorHandler handler, ExecutionContext contex, Command commad,
				CountDownLatch countDownLatch) {
			super(delay, timeUnit, handler);
			this.contex = contex;
			this.commad = commad;
			this.countDownLatch = countDownLatch;
		}


		@Override
		public void run() throws Exception{
			try {
				this.commad.execute(contex);
			}catch(Exception ex) {
				this.contex.getProcess().stop(ex.getMessage());
			} finally {
				countDownLatch.countDown();				
			}
		}

	
		
	}

	@Override
	public String getName() {
		return name;
	}

}
