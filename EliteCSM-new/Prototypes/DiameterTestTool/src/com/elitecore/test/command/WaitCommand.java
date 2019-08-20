package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.DataInputStream;
import java.util.concurrent.TimeUnit;

import com.elitecore.test.ExecutionContext;

public class WaitCommand implements Command {
	
	private static final String MODULE = "WAIT-CMD";
	private long timeInMillis;
	private String consoleResponse;
	private String name;

	public WaitCommand(long duration,TimeUnit timeUnit, String name) {
		this.name = name;
		this.timeInMillis = timeUnit.toMillis(duration);
	}

	public WaitCommand(String consoleResponse, String name){
		this.consoleResponse = consoleResponse;
		this.name = name;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(ExecutionContext context) throws Exception {

		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing wait("+ getName() +") command");
		
		if(consoleResponse == null) {
			Thread.sleep(timeInMillis);
		} else {

			DataInputStream reader = null;
			try {
				reader = new DataInputStream(System.in);


				if(consoleResponse.equals("*")) {
					reader.readUTF();
				} else {

					String consoleOutput = null;
					do {
						consoleOutput = reader.readLine();
					} while(consoleOutput.equals(consoleResponse) == false);
				}
			}finally {
				if(reader != null) {
					reader.close();
				}
			}

		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Wait("+ getName() +") command execution completed");
	}

	@Override
	public String getName() {
		return name;
	}




}
