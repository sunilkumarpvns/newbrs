package com.elitecore.eureka.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.elitecore.eureka.shared.StreamMXBean;

public class FileStream implements StreamMXBean {

	private FileOutputStream fileOutputStream;
	
	public FileStream(File file) throws FileNotFoundException {
		this.fileOutputStream = new FileOutputStream(file, true);
	}
	
	@Override
	public String read() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(String data) throws IOException {
		fileOutputStream.write(data.getBytes());
	}

	public void close() {
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
