package com.elitecore.eureka.server;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;


public class Server {
	public static void main(String[] args) throws IOException, MalformedObjectNameException, NullPointerException {
		try {
			new Server().start();
			Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MBeanServer mbeanServer;

	private void start() throws Exception {
		startMBeanServer();
		registerCommandController();
	}

	private void registerCommandController() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException {
		mbeanServer.registerMBean(new CommandController(), new ObjectName("Elitecore:type=EliteAdmin,data=AAA"));
	}

	private void startMBeanServer() {
		mbeanServer = ManagementFactory.getPlatformMBeanServer();
	}
}
