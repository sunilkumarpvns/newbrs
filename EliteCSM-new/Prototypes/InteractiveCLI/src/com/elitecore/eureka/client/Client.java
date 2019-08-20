package com.elitecore.eureka.client;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

import jline.console.ConsoleReader;

import com.elitecore.eureka.shared.CommandControllerMXBean;
import com.elitecore.eureka.shared.StreamMXBean;



public class Client {
	public static void main(String[] args) throws InterruptedException, IOException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException, IntrospectionException, InstanceNotFoundException, ReflectionException {
		new Client().start();
	}

	private CommandControllerMXBean commandController;
	private JMXServiceURL myURL;
	private String mbeanName;
	private StreamMXBean stream;
	private ConsoleReader consoleReader;

	private void start() throws IOException,
			MalformedURLException, InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException,
			MalformedObjectNameException, InterruptedException {
		
		connectToServer();
		registerCLIMbeans();
		commandLoop();
	}

	private void commandLoop() throws IOException {
		do {
			String input = consoleReader.readLine("AAACLI >");
			if ("q".equalsIgnoreCase(input)) {
				System.exit(0);
				return;
			} 
			commandController.execute(mbeanName, myURL.toString(), input);
		} while (true);
	}

	private void registerCLIMbeans() throws IOException, MalformedURLException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		
		consoleReader = new ConsoleReader();
		
		TerminalStream terminalStream = new TerminalStream(consoleReader);
		WriteBroadcastingStream stream = new WriteBroadcastingStream(terminalStream);
		
		this.stream = stream;
		
		
		MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer("Elitecore");
		
		final ServerSocket serverSocket = new ServerSocket(0);
		int port = serverSocket.getLocalPort();
		System.out.println("Chosen port: " + port);
		
		RMIServerSocketFactory ssf = new RMIServerSocketFactory() {
			
			@Override
			public ServerSocket createServerSocket(int port) throws IOException {
				System.out.println("Create server socket called");
				return serverSocket;
			}
		};
		
		Map<String, Object> env = new HashMap<String, Object>();
		env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, ssf);
		
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + getOwnHostname() + ":" + port);
		JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbeanServer);
		connectorServer.start();
		
		myURL = connectorServer.getAddress();
		System.out.println("URL: " + myURL);
		
		mbeanName = "Elitecore:type=test,data=CLI";
		
		
		mbeanServer.registerMBean(stream, new ObjectName(mbeanName));
		System.out.println("Registered stream with name: " + mbeanName);
		
		mbeanServer.registerMBean(new CLILogListener(stream), new ObjectName("Elitecore:type=EliteAdmin,data=LOG"));
	}

	private String getOwnHostname() {
		return "localhost";
	}

	private void connectToServer() throws IOException, MalformedObjectNameException, NullPointerException {
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ getServerHostname() + ":" + getAdminPort() + "/jmxrmi");
		JMXConnector aaaConnector = JMXConnectorFactory.newJMXConnector(url, null);
		aaaConnector.connect();
		MBeanServerConnection serverConnection = aaaConnector.getMBeanServerConnection();
		commandController = JMX.newMBeanProxy(serverConnection, 
				new ObjectName("Elitecore:type=EliteAdmin,data=AAA"),
				CommandControllerMXBean.class);
		System.out.println("Connected to server");
	}

	private int getAdminPort() {
		return 4444;
	}

	private String getServerHostname() {
		return "localhost";
	}
}
