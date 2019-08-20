package com.elitecore.eureka.server;

import java.util.Date;

import com.elitecore.eureka.shared.FileListenerMXBean;

public class ListenCommand implements Command {

	@Override
	public void execute(final Stream stream, final FileListenerMXBean fileListener) throws Exception {
		stream.write(new Date(System.currentTimeMillis()).toString() + "\n");
	}
}
