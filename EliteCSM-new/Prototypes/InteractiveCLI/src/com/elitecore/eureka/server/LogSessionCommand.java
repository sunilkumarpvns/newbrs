package com.elitecore.eureka.server;

import java.util.Date;

import com.elitecore.eureka.shared.FileListenerMXBean;

public class LogSessionCommand implements Command {

	@Override
	public void execute(Stream stream, FileListenerMXBean listener)
			throws Exception {
		stream.write("Enter choice 1) Start 2) End :");
		String choice = stream.read();
		if ("1".equals(choice)) {
			listener.start();
			stream.write("Log session started on: " + new Date() + "\n");
		} else if ("2".equals(choice)) {
			stream.write("Log session ended on: " + new Date() + "\n");
			listener.stop();
		}
	}
}
