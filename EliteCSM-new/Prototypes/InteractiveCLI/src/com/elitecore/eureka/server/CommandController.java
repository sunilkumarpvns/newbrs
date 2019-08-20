package com.elitecore.eureka.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.elitecore.eureka.shared.CommandControllerMXBean;
import com.elitecore.eureka.shared.FileListenerMXBean;
import com.elitecore.eureka.shared.StreamMXBean;

public class CommandController implements CommandControllerMXBean, CommandRepository {

	Map<String, Command> commandNameToCommand = new HashMap<String, Command>();
	
	public CommandController() {
		commandNameToCommand.put("shutdown", new ShutdownCommand());
		commandNameToCommand.put("listen", new ListenCommand());
		commandNameToCommand.put("loop", new LoopCommand(this));
		commandNameToCommand.put("logsession", new LogSessionCommand());
	}
	
	@Override
	public void execute(String mbeanName, String jmxUrl, String commandName) {
		JMXConnector connector = null;
		try {
			JMXServiceURL url = new JMXServiceURL(jmxUrl);
			connector = JMXConnectorFactory.connect(url);
			MBeanServerConnection connection = connector.getMBeanServerConnection();
			StreamMXBean stream = JMX.newMBeanProxy(connection, new ObjectName(mbeanName), StreamMXBean.class);
			
			FileListenerMXBean fileListener = JMX.newMBeanProxy(connection, new ObjectName("Elitecore:type=EliteAdmin,data=LOG"), FileListenerMXBean.class);
			System.out.println("Listener: " + fileListener);
			
			Command command = commandNameToCommand.get(commandName);
			if (command != null) {
				command.execute(new Stream(stream), fileListener);
			} else {
				stream.write("Unknown command: " + commandName + "\n");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				connector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Command getCommand(String name) {
		return commandNameToCommand.get(name);
	}
}
