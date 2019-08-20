package com.elitecore.eureka.client;

import java.io.File;
import java.io.IOException;

import com.elitecore.eureka.shared.FileListenerMXBean;

public class CLILogListener implements FileListenerMXBean {
	
	private final WriteBroadcastingStream stream;
	private FileStream fileStream;

	public CLILogListener(WriteBroadcastingStream stream) {
		this.stream = stream;
	}
	
	@Override
	public void start() {
		File out = new File("cli-out.dat");
		try {
			if (out.exists() == false) {
				System.out.println("File not found, so creating new file");
				out.createNewFile();
			}
			fileStream = new FileStream(out);
			stream.registerStream(fileStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		stream.unregisterStream(fileStream);
		fileStream.close();
	}

}
