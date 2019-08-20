package com.elitecore.core.systemx.esix.udp.impl;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.SessionLimitReachedException;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;

public abstract class UDPCommunicatorImpl extends ESCommunicatorImpl implements
		UDPCommunicator {
	
	private static final String MODULE = "UDP-COMM-IMPL";
	private static final int MAX_LOCAL_PORT=100;
	private static final int SOCKET_IDLE_TIMEOUT_MS=2000;
	private static final int PACKET_CAPACITY = 4096;
	private static final int RESPONSE_LISTENER_PRIORITY = 7;
	private static final int RANDOM_PORT = 0;
	
	private CommunicationHandler[] communicationHandlers;
	private Selector communicationHandlerSelector;
	protected UDPExternalSystemData externalSystemData;
	private UDPCommunicatorContext communicatorContext;
	private int activeLocalPorts;
	private Object communicationHandlerLock;
	private Future<?> failoverTaskFuture;
	private AtomicInteger timeoutCounter;
	private volatile boolean isShutdown = false;
	private AtomicInteger currentPortIndex;
	
	public UDPCommunicatorImpl(TaskScheduler scheduler, 
			@Nonnull UDPExternalSystemData externalSystem ) {
		super(scheduler);
		communicationHandlers = new  CommunicationHandler[MAX_LOCAL_PORT];
		communicationHandlerLock = new Object();
		timeoutCounter = new AtomicInteger();
		this.externalSystemData = externalSystem;
		currentPortIndex = new AtomicInteger();

	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		communicatorContext = new UDPCommunicatorContext() {
			@Override
			public String getIPAddress(){
				return externalSystemData.getIPAddress().getHostAddress();
			}
			
			@Override
			public int getPort(){
				return externalSystemData.getPort();
			}
			
			@Override
			public void responseReceived(UDPRequest udpRequest,
					UDPResponse udpResponse) {
				//If response listner is null then this request is broadcasted and no need to notify back
				if(udpRequest.getResponseListener() == null)
					return;
				udpRequest.setResponse(udpResponse);
				udpRequest.getResponseListener().responseReceived(udpRequest,udpResponse);
				timeoutCounter.set(0);
			}
			
			@Override
			public UDPExternalSystemData getExternalSystem() {
				return UDPCommunicatorImpl.this.getExternalSystem();
			}

			@Override
			public String getName() {
				return externalSystemData.getName();
			}

			@Override
			public void highResponseTimeReceived(UDPRequest udpRequest, UDPResponse udpResponse, int endToEndResponseTime) {
				UDPCommunicatorImpl.this.actionOnHighResponseTime(udpRequest, udpResponse, endToEndResponseTime);
			}

			@Override
			public void removeAttributes(UDPRequest udpRequest) {
				UDPCommunicatorImpl.this.removeAttributes(udpRequest);
			}

			@Override
			public void incrementSuccessResponseCount() {
				incrementTotalSuccess();
			}

			@Override
			public void incrementErrorResponseCount() {
				incrementTotalErrorResponses();
			}

			@Override
			public void updateAverageResponseTime(long responseTime) {
				UDPCommunicatorImpl.this.updateAverageResponseTime(responseTime);
			}

			@Override
			public void incrementTotalRequests() {
				UDPCommunicatorImpl.this.incrementTotalRequests();
			}
			
		};
		
		try {
			
			communicationHandlerSelector = Selector.open();
			
			for(activeLocalPorts=0;activeLocalPorts < getMinLocalPort() && activeLocalPorts < MAX_LOCAL_PORT; activeLocalPorts ++){
				CommunicationHandler communicationHandler = createCommunicationHandler();
				communicationHandler.init(createDatagramChannel(communicationHandler)); //NOSONAR
				communicationHandlers[activeLocalPorts] = communicationHandler;
			}

			startResponseListener();
			
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Initialization Failed for ESI, " + communicatorContext.getExternalSystem().getName() + ": " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
			try { communicationHandlerSelector.close(); } catch (IOException e1) { ignoreTrace(e);} 
			throw new InitializationFailedException("Initialization Failed for ESI, " + communicatorContext.getExternalSystem().getName() + ": " + e.getLocalizedMessage(), e);
		}
		
		startFailOverThread();
	}

	private void startResponseListener() {
		Thread respListener = new ResponseListner();
		respListener.setPriority(RESPONSE_LISTENER_PRIORITY);
		respListener.start();
	}
	
	private DatagramChannel createDatagramChannel(CommunicationHandler communicationHandler) throws Exception {
		DatagramChannel datagramChannel = DatagramChannel.open(); //NOSONAR - Reason: Resources should be closed
		
		datagramChannel.configureBlocking(false);
		datagramChannel.socket().bind(new InetSocketAddress(RANDOM_PORT));
		datagramChannel.socket().setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);
		datagramChannel.register(communicationHandlerSelector, SelectionKey.OP_READ, communicationHandler);
		
		return datagramChannel;
	}

	protected abstract void actionOnHighResponseTime(UDPRequest udpRequest,UDPResponse udpResponse, int endToEndResponseTime);

	@Override
	public void handleUDPRequest(UDPRequest request) {
		//tmpActiveLocalPorts variable is used to check that during processing time of sending request if any other thread has already 
		//created new Communication Handler then don't create new handler
		int tmpActiveLocalPorts = activeLocalPorts;
		boolean bRequestSentSuccessFully = sendRequest(request);
		
		if (bRequestSentSuccessFully) {
			return;
		}
		
		synchronized (communicationHandlerLock) {
			if(tmpActiveLocalPorts == activeLocalPorts) {
				if (activeLocalPorts < MAX_LOCAL_PORT){
					expandCommunicators(tmpActiveLocalPorts);
				} else {
					LogManager.getLogger().error(MODULE, "Max allowed local port limit : " + MAX_LOCAL_PORT + " reached for external system: " + getName());
				}
			}
		}
		
		/*if(bRequestSentSuccessFully == false){
			if(getLogger().isLogLevel(LogLevel.WARN))
				getLogger().warn(MODULE, "Unable to deliver request to target system " + getCommunicatorContext().getIPAddress() + ":" + 
						getCommunicatorContext() + ". Reason: All Identifier are in use, Active Local Ports: " + activeLocalPorts);
		}*/
		
	}

	private void expandCommunicators(int tmpActiveLocalPorts) {
		CommunicationHandler communicationHandler = createCommunicationHandler();
		try {
			communicationHandler.init(createDatagramChannel(communicationHandler)); //NOSONAR
			communicationHandlers[tmpActiveLocalPorts] = communicationHandler; 
			activeLocalPorts++;
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Unable to deliver request to external system: " + getName() 
					+ ". Reason: Either all identifiers are in use or communication error. "
					+ "So, new local port created: " + communicationHandler.getPortNumber() 
					+ ", active local ports: " + activeLocalPorts);
			}
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) 
				LogManager.getLogger().trace(MODULE, "Failed creation of new handlers, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			try {communicationHandlerSelector.close(); } catch (IOException e1) { ignoreTrace(e);} 
		}
	}
	
	private boolean sendRequest(UDPRequest request){
		if (activeLocalPorts < 1){ 
			return false; 
		}

		boolean bRequestSentSuccessFully = false;
		int cntr = currentPortIndex.incrementAndGet();
		int localIndex = cntr;
		if(cntr >= activeLocalPorts){
			currentPortIndex.set(0);
			cntr = activeLocalPorts;
			localIndex = 0;
		}

		do{
			if (localIndex >= activeLocalPorts){
				localIndex = 0;
			}
			
			try {
				CommunicationHandler commHandler = communicationHandlers[localIndex];
				commHandler.handleRequest(request);
				incrementTotalRequests();
				bRequestSentSuccessFully = true;
				break;
			} catch (SessionLimitReachedException e) {
				//No Free Identifier in current Communication Handler.
				LogManager.getLogger().warn(MODULE, "Session limit reached at port: " + communicationHandlers[localIndex].getPortNumber() + " for communicator: " + getName());
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Unexpected error occured while sending request ID: " + request.getIdentifier() + " using port: " + communicationHandlers[localIndex].getPortNumber() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				//When any exception occurs except IOException, we MUST not try to send with another local port. So that below 'break' statement is mandatory 
				break;
			}

		}while((cntr != ++localIndex) && activeLocalPorts>1);

		return bRequestSentSuccessFully;
	}
	
	@Override
	public int getMinLocalPort() {
		return externalSystemData.getMinLocalPort();
	}

	@Override
	public int getTimeOutRequestCounter() {
		return 0;
	}
	
	protected abstract CommunicationHandler createCommunicationHandler();
	
	@Override
	public UDPCommunicatorContext getCommunicatorContext(){
		return communicatorContext;
	}
	
	public UDPExternalSystemData getExternalSystem() {
		return externalSystemData;
	}
	public int getESIType() {
		return externalSystemData.getEsiType();
	}
	
	@Override
	public String getName() {
		return externalSystemData.getName();
	}
	
	private void startFailOverThread(){
		int interval = (getExternalSystem().getCommunicationTimeout()/2) -10;
		if(interval < 100) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) 
				LogManager.getLogger().warn(MODULE,"Failover thread timeout: " + interval + ". Timeout less than 100ms is not recommended, considering 100ms as timeout");
			interval = 100;
		}else if(interval > 3000) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) 
				LogManager.getLogger().warn(MODULE,"Failover thread timeout: " + interval + ". Timeout greater than 3000ms is not recommended, considering 3000ms as timeout");
			interval = 3000;
		}
		FailOver failoverTask = new FailOver(interval);
		
		if (getTaskScheduler() == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Unable to start FailOver task, Reason: Task scheduler is not available");
			}
		}
		
		failoverTaskFuture = getTaskScheduler().scheduleIntervalBasedTask(failoverTask);
	}

	class FailOver extends BaseIntervalBasedTask {
		
		private final int interval;
		
		public FailOver(int interval) {
			this.interval = interval;
		}
		
		@Override
		public long getInterval() {
			return interval;
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
		}
		
		@Override
		public void execute(AsyncTaskContext context) {
			executeFailOverTask();
		}
		
		private void executeFailOverTask() {
			List<UDPRequest> timeoutRequestList = new ArrayList<UDPRequest>();
			for (CommunicationHandler commHandler : communicationHandlers) {
				// Need to honor interruption so returning task unfinished
				if (Thread.interrupted()) {
					return;
				}
				
				if (commHandler == null) {
					continue;
				}
				timeoutRequestList.addAll(commHandler.getTimeoutRequests());
			}

			for (UDPRequest udpRequest : timeoutRequestList) {
				// Need to honor interruption so returning task unfinished
				if (Thread.interrupted()) {
					return;
				}
				
				if (UDPCommunicatorImpl.this.isAlive() 
						&& !getExternalSystem().getIsAlwaysAlive() 
						&& timeoutCounter.incrementAndGet() >= getExternalSystem().getExpiredRequestLimitCount()) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Marking UDP External System " + getExternalSystem().getIPAddress().getHostAddress() + 
								":" + getExternalSystem().getPort() + " As DEAD. Reason: Timeout request count: " + getExternalSystem().getExpiredRequestLimitCount());
					markDead();
				}
				incrementTotalTimedoutResponses();
				//updating the average response time with the value of timeout in ms
				updateAverageResponseTime(getExternalSystem().getCommunicationTimeout());
				udpRequest.getResponseListener().requestTimeout(udpRequest, UDPCommunicatorImpl.this);
			}
			
			timeoutRequestList.clear();
		}
	}
	
	@Override
	protected int getStatusCheckDuration() {
		return externalSystemData.getStatusCheckDuration();
	}
	
	public void shutdown() {
		isShutdown = true;
		try {
			CommunicationHandler communicationHandler;
			for (int cnt = 0 ; cnt < activeLocalPorts; cnt++) {
				communicationHandler = communicationHandlers[cnt];
				if (communicationHandler != null) {
					communicationHandler.shutdown();
				}
				communicationHandlerSelector.close();
			}
			stopFailoverTask();
		} catch(Exception e) {
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	private void stopFailoverTask() {
		failoverTaskFuture.cancel(true);
	}

	public boolean isShutdown() {
		return isShutdown;
	}
	
	public void removeAttributes(UDPRequest udpRequest) {
	}
	
	@Override
	public final String getTypeName() {
		return UDPCommunicator.COMMAND_KEY;
	}
	
	class ResponseListner extends Thread {
		
		private static final String MODULE = "COMM-RESP-LIST";
		
		public ResponseListner() {
			super("UDP-RL T-" + communicatorContext.getIPAddress() + "/"+ communicatorContext.getPort() + " ESI " + communicatorContext.getName());
		}
		
	
		public void run() {
			DatagramPacket responsePacket = new DatagramPacket(new byte[PACKET_CAPACITY], PACKET_CAPACITY);;
			ByteBuffer buffer = ByteBuffer.allocate(PACKET_CAPACITY);
			while(!isShutdown) {
				
				DatagramSocket datagramSocket = null;
				CommunicationHandlerImpl communicationHandler = null;

				try{
					communicationHandlerSelector.select(100);
					
					if(!communicationHandlerSelector.isOpen()) {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
							LogManager.getLogger().debug(MODULE, "Communication handler selector for ESI: " + communicatorContext.getName() + " is closed");
						}
						return;
					}
					
					Iterator<SelectionKey> keysIterator = communicationHandlerSelector.selectedKeys().iterator();
					
					while(keysIterator.hasNext()) {
						
						SelectionKey selectionKey = keysIterator.next();
						keysIterator.remove();
						
						if(!selectionKey.isValid()) {
							continue;
						}
						
						if(selectionKey.isReadable()) {
							
							DatagramChannel channel = (DatagramChannel) selectionKey.channel();
							datagramSocket = channel.socket();
							
							buffer.clear();
							channel.receive(buffer);
							int noOfBytesRead = buffer.position();
							
							if(noOfBytesRead!=0) {
								
								byte[] readBytes = new byte[(int)noOfBytesRead];
								buffer.flip();
								buffer.get(readBytes, 0, (int)noOfBytesRead);
								
								responsePacket.setData(readBytes);
								
								communicationHandler = (CommunicationHandlerImpl) selectionKey.attachment();
								
								LogManager.getLogger().debug(MODULE, "Response received on channel: localport: " + datagramSocket.getLocalPort());
								
								if(communicationHandler != null) {
									UDPResponse udpResponse = communicationHandler.createUDPResponsePacket(responsePacket.getData(),communicatorContext.getName());
									communicationHandler.handleResponse(udpResponse);
								}
							}
						}
					}
					
				} catch(ClosedSelectorException e) { 
					
					if (isShutdown == false) {
						if (LogManager.getLogger().isWarnLogLevel()) {
							LogManager.getLogger().warn(MODULE, "Communication handler selector for ESI: " + communicatorContext.getName() + " is closed");
						}
					}
					ignoreTrace(e);
				} catch (SocketTimeoutException ste) { 
					if(isShutdown){
						LogManager.getLogger().info(MODULE, "Stop receiving request for CommunicationHandler :"+datagramSocket.getInetAddress()+" : "+datagramSocket.getPort()+", stopping accept new packets.");
					}
					ignoreTrace(ste);
				}catch (IOException e) {
					LogManager.getLogger().trace(MODULE, "UDP Socket Status: ( Closed = " + datagramSocket.isClosed() + " )");
					if(datagramSocket.isClosed()) {
						LogManager.getLogger().warn(MODULE, "Removing this socket from binded port list. There may be more port available.");
						//TODO Take proper Action
						break;
					}
					LogManager.getLogger().trace(MODULE,e);
				}catch (Exception e) {
					LogManager.getLogger().error(MODULE, "Problem after receiving packet from target system for ESI, " + communicatorContext.getExternalSystem().getName() + " , Err Msg: " + e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
				}
			}
		}
	}

}
