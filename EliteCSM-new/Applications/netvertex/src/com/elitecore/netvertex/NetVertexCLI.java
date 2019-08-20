package com.elitecore.netvertex;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.BaseCLI;
import com.elitecore.core.util.cli.CLIExitStatusCodes;
import com.elitecore.core.util.logger.EliteRollingFileLogger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetVertexCLI extends BaseCLI {

	public NetVertexCLI(String serverHome, String serverName, int defaultPort, String defaultHostname) {
		super(serverHome, serverName, defaultPort, defaultHostname);	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String netVertexHome = System.getenv("NETVERTEX_HOME");
		if (netVertexHome == null) {
			System.out.println("set NETVERTEX_HOME env variable point to NetVertex server.");
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);
		}
		try {
			if (("..").equals(netVertexHome.trim())) {
				File serverHome = new File(netVertexHome);
				netVertexHome = serverHome.getCanonicalPath();
			}
		} catch (IOException e) {
			LogManager.ignoreTrace(e);
			System.out.println("NETVERTEX_HOME not valid : " + netVertexHome);
			System.exit(CLIExitStatusCodes.INVALID_HOME_PATH.exitStatusCode);

		}
		String localHost;
		try {
			localHost = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			localHost = "unknown_host";
			LogManager.ignoreTrace(ex);
		}
		EliteRollingFileLogger netVertexCLILogger =
				new EliteRollingFileLogger.Builder(localHost,
						netVertexHome + File.separator + "logs" + File.separator + "netvertex-cli")
						.build();

		LogManager.setDefaultLogger(netVertexCLILogger);

		CLIExitStatusCodes exitStatus = new NetVertexCLI(netVertexHome, "NetVertex Server", 3454, localHost).start(args);
		System.exit(exitStatus.exitStatusCode);
	}

	@Override
	public String getServerDescription(){
		return Version.getModuleName() + " [" + Version.getVersion() +"]";
	}
    @Override
	protected String getConsolePrompt(){
		return "NVX> ";
	}

}
