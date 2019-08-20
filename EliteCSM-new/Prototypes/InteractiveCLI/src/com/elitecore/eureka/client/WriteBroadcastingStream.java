package com.elitecore.eureka.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.eureka.shared.StreamMXBean;

public class WriteBroadcastingStream implements StreamMXBean {
	List<StreamMXBean> streams = new ArrayList<StreamMXBean>();
	private final StreamMXBean readStream;

	public WriteBroadcastingStream(StreamMXBean readStream) {
		this.readStream = readStream;
		registerStream(readStream);
	}
	
	@Override
	public String read() throws IOException {
		return readStream.read();
	}

	@Override
	public void write(String data) throws IOException {
		for (StreamMXBean stream : streams) {
			stream.write(data);
		}
	}

	public void registerStream(StreamMXBean writeStream) {
		streams.add(writeStream);
	}

	public void unregisterStream(StreamMXBean stream) {
		streams.remove(stream);
	}
}
