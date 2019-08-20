package com.elitecore.netvertex.core;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import com.elitecore.core.systemx.esix.http.HTTPMethodType;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupInstanceConfiguration;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceConfigurationImpl;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class ServerStatusScanner extends BaseIntervalBasedTask {

	private static final String MODULE = "SRVR-TYPE-SCANNER";
	private static final long INTERVAL_IN_SECONDS = 20;
	private static final long INITIAL_DELAY_IN_SECONDS = 1;
	public static final int SUCCESS = 200;
	private NetVertexServerContext serverContext;
	private NetvertexServerGroupInstanceConfiguration netvertexServerGroupInstanceConfiguration;
	private HTTPConnector httpConnector;
	private Consumer<Boolean> statusUpdater;

	public ServerStatusScanner(NetVertexServerContext serverContext,
							   NetvertexServerGroupInstanceConfiguration netvertexServerGroupInstanceConfiguration,
							   HTTPConnector httpConnector,
							   Consumer<Boolean>  statusUpdater) {

		this.serverContext = serverContext;
		this.netvertexServerGroupInstanceConfiguration = netvertexServerGroupInstanceConfiguration;
		this.httpConnector = httpConnector;

		this.statusUpdater = statusUpdater;
	}

	public static ServerStatusScanner create(NetVertexServerContext serverContext,
											 Consumer<Boolean> statusUpdater,
											 HTTPConnectorFactory httpConnectorFactory,
											 Runtime runtime) throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Scheduled server status scanner. Reason: Running instance is secondary server");
		}

		NetvertexServerConfiguration serverConfiguration = serverContext.getServerConfiguration();

		NetvertexServerGroupInstanceConfiguration primaryInstanceConfiguration = serverConfiguration.getNetvertexServerGroupConfiguration().getPrimaryInstanceConfiguration();

		NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = primaryInstanceConfiguration.getNetvertexServerInstanceConfiguration();
		String name = netvertexServerInstanceConfiguration.getName();
		String restIpAddress = netvertexServerInstanceConfiguration.getRestIpAddress();

		if (restIpAddress == null) {
			throw new InitializationFailedException("Rest address not provided of " + name + " server");
		}

		HTTPConnector httpConnector = httpConnectorFactory.create(name, restIpAddress, netvertexServerInstanceConfiguration.getRestPort());

		runtime.addShutdownHook(new Thread(httpConnector::stop));

		return  new ServerStatusScanner(serverContext,
				primaryInstanceConfiguration,
				httpConnector,
				statusUpdater);
	}

	@Override
	public boolean isFixedDelay() {
		return true;
	}

	@Override
	public long getInterval() {
		return INTERVAL_IN_SECONDS;
	}

	@Override
	public long getInitialDelay() {
		return INITIAL_DELAY_IN_SECONDS;
	}

	@Override
	public void execute(AsyncTaskContext context) {
		boolean isPrimary = true;

		String name = netvertexServerGroupInstanceConfiguration.getNetvertexServerInstanceConfiguration().getName();

		if (httpConnector.isAlive() == false) {
			if (getLogger().isLogLevel(LogLevel.WARN)) {
				getLogger().warn(MODULE, "Unable to connect to " + name + " server. Reason: server is not live");
			}
		} else {
			isPrimary = isPrimaryServerRunning(name) == false ? true : false;
		}

		if (serverContext.isPrimaryServer() == false && isPrimary) {
			if (getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Marking current server as primary");
		} else if (serverContext.isPrimaryServer() && isPrimary == false) {
			if (getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Marking current server as secondary");
		}

		statusUpdater.accept(isPrimary);
	}

	private boolean isPrimaryServerRunning(String name) {
		String methodName = "/serverStatus/" + netvertexServerGroupInstanceConfiguration.getId();
		RemoteMethod remoteMethod = new RemoteMethod(CommonConstants.SERVER_INSTANCE_WS_CONTEXT_PATH, methodName, HTTPMethodType.GET);

		try {

			HttpResponse httpResponse = (HttpResponse) httpConnector.submit(remoteMethod);


			int statusCode = httpResponse.getStatusLine().getStatusCode();

			HttpEntity entity = httpResponse.getEntity();
			String responseString = new BufferedReader(new InputStreamReader(entity.getContent())).readLine();


			if (statusCode == SUCCESS) {

				getLogger().debug(MODULE, "Primary server status is " + responseString);

				if (LifeCycleState.RUNNING.message.equalsIgnoreCase(responseString)
						|| LifeCycleState.RUNNING_WITH_LAST_CONF.message.equalsIgnoreCase(responseString)) {
					return true;
				}

			} else {

				getLogger().warn(MODULE, name + " status failed. " +
						"Reason: Request failed with code " + statusCode + " and response " + responseString);
			}

		} catch (IOException e) {
			if (getLogger().isLogLevel(LogLevel.WARN)) {
				getLogger().warn(MODULE, "Error while reading response from " + name + " server. Reason:" + e.getMessage());
			}
			getLogger().trace(MODULE, e);
		} catch (CommunicationException e) {
			if (getLogger().isLogLevel(LogLevel.WARN)) {
				getLogger().warn(MODULE, "Unable to connect to to " + name + " server. Reason:" + e.getMessage());
			}
			getLogger().trace(MODULE, e);

		}
		return false;
	}

}
