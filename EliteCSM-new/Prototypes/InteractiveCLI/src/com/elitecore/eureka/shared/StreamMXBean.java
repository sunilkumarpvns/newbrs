package com.elitecore.eureka.shared;

import java.io.IOException;

public interface StreamMXBean {
	String read() throws IOException;
	void write(String data) throws IOException;
}
