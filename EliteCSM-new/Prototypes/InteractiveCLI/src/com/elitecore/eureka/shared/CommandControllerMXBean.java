package com.elitecore.eureka.shared;


public interface CommandControllerMXBean {
	void execute(String mbeanName, String jmxUrl, String input);
}
