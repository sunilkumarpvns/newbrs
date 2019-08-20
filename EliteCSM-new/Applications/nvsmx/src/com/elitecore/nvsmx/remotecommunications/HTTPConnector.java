package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.remotecommunications.exception.CommunicationException;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceConnector;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceMethods;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class HTTPConnector extends ESCommunicatorImpl implements EndPoint{

	private static final int DEFAULT_STATUS_CHECK_DURATION = 10;
	private static final String STATUS_CHECK_DURATION_PROPERTY = "rmi.statuscheckduration";

	private String MODULE; //NOSONAR
	private WebServiceConnector connector;
	private ServerInformation netServerInstance;
	private ServerInformation serverInstanceGroupData;
	private String baseURI;
	private ExecutorService executorService;
	private int statusCheckDuration = -1;
	private String restServerAddress;


	public HTTPConnector(TaskScheduler taskScheduler,ServerInformation netServerInstance,
						 ServerInformation serverInstanceGroupData,
						 String baseURI,
						 final ExecutorService executorService,
						 String restServerAddress) {
		super(taskScheduler);
		this.netServerInstance = netServerInstance;
		this.serverInstanceGroupData = serverInstanceGroupData;
		this.baseURI = baseURI;
		this.executorService = executorService;
		this.restServerAddress = restServerAddress;
		this.MODULE = "HTTP-CONNECTOR-" + netServerInstance.getName();
	}




	@Override
	public void init() throws InitializationFailedException{

		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Initializing instance: " + netServerInstance.getName());

		readStatusCheckDuration();

		super.init();

		initializeConnectorToEndPoint();

		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Instance: " + netServerInstance.getName() + " initialized successfully");
	}

	private void readStatusCheckDuration() {
		String env = System.getenv(STATUS_CHECK_DURATION_PROPERTY);
		if(Strings.isNullOrBlank(env) == false) {
			try {
				statusCheckDuration = Integer.parseInt(env);
			}catch (Exception e) {
				getLogger().error(MODULE, "Invalid value("+ env +") configured for "+ STATUS_CHECK_DURATION_PROPERTY + " in environment variable");
				getLogger().trace(MODULE, e);
			}
		}

		if(statusCheckDuration == -1) {
			String property = System.getProperty(STATUS_CHECK_DURATION_PROPERTY);
			if(Strings.isNullOrBlank(property) == false) {
				try {
					statusCheckDuration = Integer.parseInt(property);
				}catch (Exception e) {
					getLogger().error(MODULE, "Invalid value("+ property +") configured for "+ STATUS_CHECK_DURATION_PROPERTY + " in system property");
					getLogger().trace(MODULE, e);
				}
			}
		}

		if(statusCheckDuration == -1) {
			statusCheckDuration = DEFAULT_STATUS_CHECK_DURATION;
		}

		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Status check duration is "+ statusCheckDuration +" seconds");
	}

	private void initializeConnectorToEndPoint() {
		WebServiceConnector webServiceConnector = new WebServiceConnector(netServerInstance.getName(), restServerAddress, executorService);
		webServiceConnector.init();
		checkStatus(webServiceConnector);
	}


	@Override
	public void scan() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Checking for aliveness");
		}
		if(connector == null){
			initializeConnectorToEndPoint();
		}else{
			checkStatus(connector);
		}
	}


	private void checkStatus(WebServiceConnector webServiceConnector) {

		boolean isLiveServer = false;
		try {
			Future<String> status = webServiceConnector.call(new RemoteMethod(baseURI,WebServiceMethods.SERVER_STATUS.name(),netServerInstance.getNetServerCode(), HTTPMethodType.GET));
			String string = status.get(3, TimeUnit.SECONDS);
			if (getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, string + " status received from server");

			if (Strings.isNullOrBlank(string)==false) {
				isLiveServer = true;
				markAlive();
				connector = webServiceConnector;
			}
		} catch (InterruptedException e) {//NOSONAR
			getLogger().error(MODULE, "Interruption occurred while checking for server aliveness");
			getLogger().trace(e.getCause());
		} catch (ExecutionException e) {
			getLogger().error(MODULE, "Error while checking for aliveness. Reason: " + e.getCause().getMessage());
			getLogger().trace(e);
		} catch (TimeoutException e) {
			getLogger().error(MODULE, "Timeout occurred while checking for server aliveness");
			getLogger().trace(e);
		} catch (CommunicationException e) {
			getLogger().error(MODULE, "Unable to check status for server aliveness. Reason: " + e.getMessage());
			getLogger().trace(e);
		}

		if (isLiveServer == false) {
			markDead();
		}
	}



	@Override
	public String getName() {
		return MODULE;
	}

	@Override
	public String getTypeName() {
		return "REST";
	}

	@Override
	protected int getStatusCheckDuration() {
		return statusCheckDuration;
	}


	@Override
	public <V> Future<RMIResponse<V>> submit(RemoteMethod method)  {

		try {
			if (isAlive() == false) {
				SettableFuture<RMIResponse<V>> errorFuture = SettableFuture.create();
				errorFuture.set(new ErrorRMIResponse<V>(new CommunicationException("ServerInstance: "+netServerInstance.getName()+" is not alive"), serverInstanceGroupData , netServerInstance));
				return errorFuture;
			}
			final Future<V> future = connector.call(method);
			return new ProxyFuture<V>(future);
		} catch (CommunicationException communicationException) {
			markDead();
			return getErrorRMIResponse(communicationException);
		}
	}

	@Override
	public ServerInformation getGroupData() {
		return serverInstanceGroupData;
	}

	@Override
	public ServerInformation getInstanceData() {
		return netServerInstance;
	}

	private <V> Future<RMIResponse<V>> getErrorRMIResponse(CommunicationException communicationException) {
		SettableFuture<RMIResponse<V>> errorFuture = SettableFuture.create();
		errorFuture.set(new ErrorRMIResponse<V>(communicationException, serverInstanceGroupData, netServerInstance));
		return errorFuture;
	}


	private class ProxyFuture<T> implements Future<RMIResponse<T>> {
		
		private Future<T> future;


		public ProxyFuture(Future<T> rmiResponseFuture) {
			future = rmiResponseFuture;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return future.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return future.isCancelled();
		}

		@Override
		public boolean isDone() {
			return future.isDone();
		}

		@Override
		public RMIResponse<T> get() throws InterruptedException {
			try {
				T response = future.get();
				return new SuccessRMIResponse<T>(response, serverInstanceGroupData, netServerInstance);
             } catch(ExecutionException executionException) {
				markDead();
				return new ErrorRMIResponse<T>(executionException,serverInstanceGroupData, netServerInstance);
			}
		}

		@Override
		public RMIResponse<T> get(long timeout, TimeUnit unit) throws InterruptedException,TimeoutException {
			try {
				T response = future.get(timeout,unit);
				return new SuccessRMIResponse<T>(response, serverInstanceGroupData, netServerInstance);

			} catch(ExecutionException executionException) {
				markDead();
				return new ErrorRMIResponse<T>(executionException,serverInstanceGroupData, netServerInstance);
			}
		}
	}

	@Override
	public void stop() {

		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "Shutting down HTTP Connector for group: " +serverInstanceGroupData.getName());

		super.stop();

		if(connector != null){
			connector.shutDown();
		}

		if(getLogger().isInfoLogLevel())
			getLogger().info(MODULE, "HTTP Connection has shut down");
	}
}