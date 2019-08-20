package com.elitecore.eureka.server;

import java.util.concurrent.TimeUnit;

import com.elitecore.eureka.shared.FileListenerMXBean;

public class LoopCommand implements Command {
	private final CommandRepository repo;

	public LoopCommand(CommandRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public void execute(Stream stream, FileListenerMXBean listener)
			throws Exception {
		stream.write("Enter command to loop: ");
		String commandToLoop = stream.read();
		Command command = repo.getCommand(commandToLoop);
		if (command != null) {
			stream.write("Enter times: ");
			int loop = Integer.parseInt(stream.read());
			stream.write("Enter interval in secs: ");
			int interval = Integer.parseInt(stream.read());
			for (int i = 0; i < loop; i++) {
				command.execute(stream, listener);
				Thread.sleep(TimeUnit.SECONDS.toMillis(interval));
			}
		} else {
			stream.write("Unknown command: " + commandToLoop);
		}
	}
}
