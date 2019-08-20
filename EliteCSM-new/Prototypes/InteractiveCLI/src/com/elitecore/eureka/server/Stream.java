package com.elitecore.eureka.server;

import java.io.IOException;

import com.elitecore.eureka.shared.StreamMXBean;

/**
 * Facade over {@link StreamMXBean}
 * @author narendra
 *
 */
public class Stream implements StreamMXBean {
	private final StreamMXBean stream;

	public Stream(StreamMXBean stream) {
		this.stream = stream;
	}
	
	@Override
	public String read() throws IOException {
		return stream.read();
	}

	@Override
	public void write(String data) throws IOException {
		stream.write(data);
	}

}
