package com.elitecore.eureka.client;

import java.io.IOException;

import jline.console.ConsoleReader;

import com.elitecore.eureka.shared.StreamMXBean;

public class TerminalStream implements StreamMXBean {
	private ConsoleReader console;
	
	public TerminalStream(ConsoleReader console) {
		this.console = console;
	}
	
	@Override
	public String read() throws IOException {
		return console.readLine("");
	}

	@Override
	public void write(String data) throws IOException {
		console.getOutput().write(data);
		console.flush();
	}
}
