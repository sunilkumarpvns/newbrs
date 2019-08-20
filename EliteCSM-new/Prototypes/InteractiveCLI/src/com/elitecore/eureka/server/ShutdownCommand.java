package com.elitecore.eureka.server;

import java.io.IOException;

import com.elitecore.eureka.shared.FileListenerMXBean;

public class ShutdownCommand implements Command {

	@Override
	public void execute(Stream stream, FileListenerMXBean fileListener) throws Exception {
		shutdownCommand(stream);
		System.out.println(Thread.currentThread().getName());
	}
	
	private void stop(Stream stream) throws Exception {
		stream.write("Stopping all services..\n");
		
		stream.write("Stopping Authentication service\n");
		progressBar(stream);
		stream.write("Successfully stopped Authentication service\n");

		stream.write("Stopping Accounting service\n");
		progressBar(stream);
		stream.write("Successfully stopped Accounting service\n");
		
		stream.write("Stopping Dynauth service\n");
		progressBar(stream);
		stream.write("Successfully stopped Dynauth service\n");
		
		stream.write("Server stopped successfully\n");
	}

	private void progressBar(Stream stream) throws IOException, InterruptedException {
		stream.write("/");
		for (int i = 0; i < 3; i++) {
			Thread.sleep(500);
			stream.write("\r-");
			Thread.sleep(500);
			stream.write("\r\\");
			Thread.sleep(500);
			stream.write("\r|");
		}
		stream.write("\r");
	}

	private void shutdownCommand(Stream stream) throws Exception {
		stream.write("Are you sure? Y or N: ");
		if ("Y".equalsIgnoreCase(stream.read())) {
			stream.write("OK Mogambo! Kaboom... \n");
			stop(stream);
		} else {
			stream.write("Cancelled shutdown process :( \n");
		}
	}
}
