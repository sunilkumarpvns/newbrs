package com.elitecore.eureka.client;

import java.io.IOException;
import java.util.Scanner;

import com.elitecore.eureka.shared.StreamMXBean;

public class ConsoleStream implements StreamMXBean {

	@Override
	public String read() throws IOException {
		return new Scanner(System.in).nextLine();
	}

	@Override
	public void write(String data) throws IOException {
		System.out.println(data);
	}

}
