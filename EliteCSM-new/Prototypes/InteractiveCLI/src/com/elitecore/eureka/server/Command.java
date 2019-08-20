package com.elitecore.eureka.server;

import com.elitecore.eureka.shared.FileListenerMXBean;


public interface Command {
	void execute(Stream stream, FileListenerMXBean listener) throws Exception;
}
