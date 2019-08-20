package com.elitecore.core.servicex;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.commons.threads.EliteThreadFactory.EliteThread;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.core.util.url.SocketDetail;

public abstract class EliteUDPService<T extends UDPServiceRequest, V extends UDPServiceResponse>
extends BaseEliteService implements MultiSocketService<T, V> {

    private static final String MODULE = "BUDP";
    private static final int MAX_PACKET_SIZE = 65535;
    // Idle time out for Datagram Socket
    private static final int SOCKET_IDLE_TIMEOUT_MS = 2000;
    private static final short DEFAULT_BUFFER_SIZE = -1;
    private static final boolean ONLY_CONFIGURED_PORT = false;
    private static final boolean TRY_DEFAULT_PORT = true;
    
    private Map<SocketDetail, RequestListener> listeningSocketDetailToListener;
    
    private ThreadPoolExecutor listeners;
    private Thread requestDispatcherThread;
    private LinkedBlockingQueue<BaseRequestHandler> initialRequestQueue;
    private SynchronousQueue<Runnable> synchronousQueue;
    private SynchronousQueue<Runnable> asynchronousQueue;
    private ThreadPoolExecutor synchronousTaskExecutor;
    private ThreadPoolExecutor asynchronousTaskExecutor;
    
    private AtomicInteger requestCounter = new AtomicInteger();

    private long totalResponseGiven;
    private long totalResponseTime;
    
    private Object perSecondAvgResponseTimeMiliLock; 
    private long perSecondAvgResponseTimeMili;
    private long perSecondTotalRequestRecieved;    	
    private long perSecondTotalResponseTimeNano; 
    
    public EliteUDPService(ServerContext serverContext) {
    	super(serverContext);
    	listeningSocketDetailToListener = new HashMap<SocketDetail, RequestListener>();
    }
    
    @Override
    protected void initService() throws ServiceInitializationException{
    	perSecondTotalRequestRecieved = 0L;    	
    	perSecondTotalResponseTimeNano = 0L;
    	perSecondAvgResponseTimeMili = 0L;
    	perSecondAvgResponseTimeMiliLock = new Object();
    	getServerContext().getTaskScheduler().scheduleIntervalBasedTask(new AverageResponseTimeCalculator(1,1));
    	
    }
    
	/**
	 * 
	 * @return
	 */
    protected synchronized boolean startService() {

    	if (getLogger().isDebugLogLevel()) {
    		getLogger().debug(MODULE, "Starting UDP Service " + getServiceIdentifier());
    	}
    	
        try {

			synchronousQueue = new SynchronousQueue<Runnable>();
	    	synchronousTaskExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), 
	    			getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, synchronousQueue);
	    	

	    	synchronousTaskExecutor.setThreadFactory(new EliteThreadFactory(getServiceIdentifier(), getServiceIdentifier(), getWorkerThreadPriority()));
	        synchronousTaskExecutor.prestartAllCoreThreads();

			asynchronousQueue = new SynchronousQueue<Runnable>();
	    	asynchronousTaskExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), 
	    			getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, asynchronousQueue, new RejectedExecutionHandler() {
						
						@Override
						public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
							if (isStopRequested()) {
								return;
							}
							try {
								getLogger().warn(MODULE, "No Async thread is free, so temporarily pausing listener thread execution.");
								Thread.sleep(150);
								executor.execute(r);
							} catch (InterruptedException e) {
								getLogger().trace(MODULE, e);
							}
						}
					});
	    	asynchronousTaskExecutor.setThreadFactory(new EliteThreadFactory(getServiceIdentifier(), getServiceIdentifier()+"-"+ "RES", getWorkerThreadPriority()));
	        asynchronousTaskExecutor.prestartAllCoreThreads();

	        if (getLogger().isInfoLogLevel()) {
	        	getLogger().info(MODULE, "Service "+ getServiceIdentifier() + " worker threads created");
	        }
	        
            initialRequestQueue = new LinkedBlockingQueue<BaseRequestHandler>(getMaxRequestQueueSize());

            int dispatcherThreadPriority = getMainThreadPriority() - getWorkerThreadPriority();
            if (dispatcherThreadPriority > 0) {
                dispatcherThreadPriority--; // should be one less than main thread priority.
                dispatcherThreadPriority = getWorkerThreadPriority() + dispatcherThreadPriority;
            }else {
                dispatcherThreadPriority = getMainThreadPriority();
            }
            
            requestDispatcherThread = new EliteThread(new RequestDispatcher(), getServiceIdentifier() + "-URDTH", getServiceIdentifier());
            requestDispatcherThread.setPriority(dispatcherThreadPriority);
           
            if (getLogger().isInfoLogLevel()) {
    	        getLogger().info(MODULE, "Request dispatcher thread priority set to " + dispatcherThreadPriority + " for " + getServiceIdentifier());
            }
            startListeners();
            requestDispatcherThread.start();

           return true;
           
        } catch (BindException be){
        	
        	remark = ServiceRemarks.PROBLEM_BINDING_IP_PORT.remark + " (" + be.getMessage() + ")";
        	getLogger().error(MODULE, "Problem starting " + getServiceIdentifier() + ", Reason: " + be);
            getLogger().trace(be);
            stop();
            doFinalShutdown();
        	return false;
		} catch (IOException e) {
			remark = ServiceRemarks.UNKNOWN_PROBLEM.remark;
            getLogger().error(MODULE, "Problem starting " + getServiceIdentifier() + ", Reason: " + e);
            getLogger().trace(e);
            stop();
            doFinalShutdown();
            return false;
		}
	}
	
    private void startListeners() throws UnknownHostException, SocketException {
    	List<SocketDetail> configuredSocketDetails = getSocketDetails();
    	int socketNumbers = configuredSocketDetails.size();
    	EliteThreadFactory listenerThreadFactory = new EliteThreadFactory(getServiceIdentifier(), getServiceIdentifier()+"-LTH", getMainThreadPriority());
    	listeners = new ThreadPoolExecutor(socketNumbers, socketNumbers,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(socketNumbers),
                listenerThreadFactory);
    	
    	listeners.prestartAllCoreThreads();
    	
    	if(isMultiSocketMode(configuredSocketDetails) == false) {
			
    		SocketDetail socketDetail = configuredSocketDetails.get(0);
			DatagramSocket serverSocket = bind(socketDetail, TRY_DEFAULT_PORT);
			socketDetail = new SocketDetail(serverSocket.getLocalAddress().getHostAddress(), serverSocket.getLocalPort());
    		configureSocket(socketDetail, serverSocket);
           	createRequestListener(socketDetail, serverSocket);
    	} else {
    		for (SocketDetail socketDetail : configuredSocketDetails) {
        		
        		DatagramSocket serverSocket = bind(socketDetail, ONLY_CONFIGURED_PORT);
        		socketDetail = new SocketDetail(serverSocket.getLocalAddress().getHostAddress(), serverSocket.getLocalPort());
        		configureSocket(socketDetail, serverSocket);
               	createRequestListener(socketDetail, serverSocket);
        	}
    	}
    	
    	
    }

	private boolean isMultiSocketMode(List<SocketDetail> socketDetails) {
		return socketDetails.size() > 1;
	}

	public void createRequestListener(SocketDetail socketData, DatagramSocket serverSocket) {
		RequestListener requestListener = new RequestListener(serverSocket, socketData);
		listeners.execute(requestListener);
		listeningSocketDetailToListener.put(socketData, requestListener);
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, getServiceIdentifier() +" service started on " + socketData.getIPAddress() 
					+ ":" + socketData.getPort());
		}
	}

	public void configureSocket(SocketDetail socketData, DatagramSocket serverSocket) throws SocketException {
		serverSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);
		int socketReceiveBufferSize = getSocketReceiveBufferSize();
		int socketSendBufferSize = getSocketSendBufferSize();
		try { 
			if (socketReceiveBufferSize == DEFAULT_BUFFER_SIZE) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Configured receive buffer size: " + socketReceiveBufferSize
							+ " for " + socketData.getIPAddress() + ":" + socketData.getPort()
							+ ", so using OS default receive buffer size: " + serverSocket.getReceiveBufferSize());
				}
			} else {
				serverSocket.setReceiveBufferSize(socketReceiveBufferSize);
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Socket receive buffer size for " + socketData.getIPAddress() + ":" + socketData.getPort() + " is " + serverSocket.getReceiveBufferSize());
				}
			}
		} catch (IllegalArgumentException e) { 
			getLogger().warn(MODULE, "Invalid receive buffer size: " + socketReceiveBufferSize 
					+ " configured for " + socketData.getIPAddress() + ":" + socketData.getPort() 
					+ ", so using OS default buffer size: " + serverSocket.getReceiveBufferSize());
			ignoreTrace(e);
		}
		try {
			if (socketSendBufferSize == DEFAULT_BUFFER_SIZE) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Configured send buffer size: " + socketSendBufferSize
							+ " for " + socketData.getIPAddress() + ":" + socketData.getPort()
							+ ", so using OS default send buffer size: " + serverSocket.getSendBufferSize());
				}
			} else {
				serverSocket.setSendBufferSize(socketSendBufferSize);
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Socket send buffer size for " + socketData.getIPAddress() + ":" + socketData.getPort() + " is " + serverSocket.getSendBufferSize());
				}
			}
		} catch (IllegalArgumentException e) { 
				getLogger().warn(MODULE, "Invalid send buffer size: " + socketSendBufferSize 
						+ " configured for " + socketData.getIPAddress() + ":" + socketData.getPort() 
						+ ", so using OS default buffer size: " + serverSocket.getSendBufferSize());
				ignoreTrace(e);
		}
	}
    
    private DatagramSocket bind(SocketDetail socketDetail, boolean tryConnectionOnDefaultPort) throws UnknownHostException, SocketException {
   
    	
    	/// bind on configured ip and port
    	try {
			InetAddress bindAddress = InetAddress.getByName(socketDetail.getIPAddress());
			return new DatagramSocket(socketDetail.getPort(), bindAddress);
			
		} catch (UnknownHostException unknownHostException) { 
			getLogger().warn(MODULE, "Problem while attempting to resolve IP: " 
					+ socketDetail.getIPAddress() 
					+ ", Reason: " + unknownHostException.getMessage()
					+ ", service will attempt to listen on socket: " + CommonConstants.UNIVERSAL_IP + ":" + socketDetail.getPort());
			ignoreTrace(unknownHostException);
			
		} catch (SocketException socketException) { 
				getLogger().warn(MODULE, "Problem while attempting to listen on socket : " 
					+ socketDetail + ", Reason: " + socketException.getMessage()
					+ ",  service will attempt to listen on socket: " + CommonConstants.UNIVERSAL_IP + ":" + socketDetail.getPort());
				ignoreTrace(socketException);
		}
    	// Not handling generic exception because we need to notify calling method about issue
    	
    	/// bind on 0.0.0.0 and configured port
    	try {
			InetAddress bindAddress = InetAddress.getByName(CommonConstants.UNIVERSAL_IP);
			DatagramSocket datagramSocket = new DatagramSocket(socketDetail.getPort(), bindAddress);
			remark = ServiceRemarks.STARTED_ON_UNIVERSAL_IP.remark;
			return datagramSocket;
		} catch(SocketException socketException) {
			
			// process only when only one ip configured
			if (tryConnectionOnDefaultPort == false || socketDetail.getPort() == getDefaultServicePort()) {
				throw socketException;
			} else {
				getLogger().warn(MODULE, "Problem while attempting to listen on socket: " 
						+ CommonConstants.UNIVERSAL_IP  + ":" + socketDetail.getPort() 
						+ ", Reason: " + socketException.getMessage()
						+ ", service will attempt to listen on socket: " + CommonConstants.UNIVERSAL_IP + ":" + getDefaultServicePort());
				
			}
		}
    	
    	/// bind on 0.0.0.0 and default port
    	InetAddress bindAddress = InetAddress.getByName(CommonConstants.UNIVERSAL_IP);
		DatagramSocket datagramSocket = new DatagramSocket(getDefaultServicePort(), bindAddress);
		remark = ServiceRemarks.STARTED_ON_UNIVERSAL_IP.remark;
		return datagramSocket;
    }
	
    @Override
    public final List<SocketDetail> getListeningSocketDetails() {
        return new ArrayList<SocketDetail>(listeningSocketDetailToListener.keySet());
    }

    public abstract int getDefaultServicePort();
	
	public boolean stopService() {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Stop flag set for " + getServiceIdentifier());
		}
		return true;
	}
	
	@Override
	protected void shutdownService() {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Final shutdown process started for " + getServiceIdentifier());
			getLogger().info(MODULE, "Listener thread for " + getServiceIdentifier() + " is still alive, generating exit request");
		}
		
		if (requestDispatcherThread != null && requestDispatcherThread.isAlive()) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Dispatcher thread for " + getServiceIdentifier() + " is still alive, generating interruption");
			}
			try{
				requestDispatcherThread.interrupt();
			}catch(Exception e){
				
			}
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, getServiceIdentifier() + " - stopping dispatcher threads");
			}
		}

		if (listeners != null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, getServiceIdentifier() + " - stopping listener threads");
			}
			
			listeners.shutdownNow();
			closeListenerSockets();
			
			try {
				listeners.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, getServiceIdentifier() + " - stopped listening for new requests, shutting down service with " + initialRequestQueue.size() + " pending requests");
				}
			} catch (InterruptedException e) {
				getLogger().trace(MODULE, e);
			}
		}
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, getServiceIdentifier() + " - stopping worker threads");
		}
		if (synchronousTaskExecutor != null) {
			synchronousTaskExecutor.shutdown();
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, getServiceIdentifier() + " - shutdown requested for synchronous task executor ");
			}
			while (synchronousTaskExecutor.isTerminated() == false) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, getServiceIdentifier() + " - waiting for synchronous task executor to complete execution");
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, getServiceIdentifier() + " - synchronous task executor terminated");
			}
		}
		if (asynchronousTaskExecutor != null) {
			asynchronousTaskExecutor.shutdown();
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, getServiceIdentifier() + " - shutdown requested for async task executor ");
			}
			while (asynchronousTaskExecutor.isTerminated() == false) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, getServiceIdentifier() + " - waiting for async task executor to complete execution");
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, getServiceIdentifier() + " - async task executor terminated");
			}
		}
	}
	
	private void closeListenerSockets() {
		for (RequestListener listener : listeningSocketDetailToListener.values()) {
			listener.serverSocket.close();
		}
		listeningSocketDetailToListener.clear();
	}

	private String getListeningSocketAddress() {
		if (listeningSocketDetailToListener.isEmpty()) {
            return getSocketDetails().toString();
        }
        return listeningSocketDetailToListener.keySet().toString();
	}
	
	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceIdentifier(), getStatus(), 
				getListeningSocketAddress(), getStartDate(), getRemarks());
	}
	
    public class RequestListener implements Runnable {
    	
    	private final DatagramSocket serverSocket;
    	private final SocketDetail listeningSocketDetail;
    	//narendra.pathai added with JIRA-1868 Duplicate detection
    	private DuplicateRequestDetector duplicateRequestDetector = null;
    	
    	public RequestListener(DatagramSocket serverSocket, SocketDetail listeningSocketDetail) {
    		this.serverSocket = serverSocket;
    		this.listeningSocketDetail = listeningSocketDetail;
    		if(isDuplicateDetectionEnabled()) {
    			this.duplicateRequestDetector = new DuplicateRequestDetector(getDuplicateDetectionQueuePurgeInterval());
    		}
    	}

    	public void run() {

    		if (getLogger().isInfoLogLevel()) {
    			getLogger().info(MODULE, getServiceIdentifier() + " waiting for a first request on socket: "
    				+ listeningSocketDetail);
    		}

    		boolean isRequestPlacedInInitialRequestQueue = false;
    		DatagramPacket udpPacket = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);


    		while(true) {
    			try {
    				if (isStopRequested()) {
    					break;
    				}
    				serverSocket.receive(udpPacket);
   					requestCounter.incrementAndGet();

    				getServerContext().addAndGetAverageRequestCount(1);

    				byte[] rawPacketBytes = new byte[udpPacket.getLength()];
    				System.arraycopy(udpPacket.getData(), 0, rawPacketBytes, 0, rawPacketBytes.length);
    				RequestHandler request = new RequestHandler(udpPacket.getAddress(), udpPacket.getPort(), rawPacketBytes, 
    						listeningSocketDetail);
    				incrementRequestReceivedCounter(request.serviceRequest);

    				boolean bDuplicateRequest = false;
    				if(isDuplicateDetectionEnabled()) {
    					bDuplicateRequest = duplicateRequestDetector.isDuplicateRequest(request);
    				}
    				if(bDuplicateRequest) {
    					incrementDuplicateRequestReceivedCounter(request.serviceRequest);
    					byte[] responseBytes = duplicateRequestDetector.getResponseBytes(request);
    					if(responseBytes == null) {
    						//incrementing the DUPLICATE request received counter
    						incrementRequestDroppedCounter(request.serviceRequest);
    						getLogger().warn(MODULE, "Duplicate Request Received on socket " 
    								+ listeningSocketDetail + ". Dropping Request");
    					} else {
    						getLogger().warn(MODULE, "Duplicate Request Received on socket " 
    								+ listeningSocketDetail + ". Sending cached response");
    						incrementResponseCounter(request.serviceRequest.getSourceAddress().getHostAddress(),
    								responseBytes);
    						try {
    							
    							responseBytes = request.buildDuplicateResponse(responseBytes);
    							
    							DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
    					    	responsePacket.setAddress(udpPacket.getAddress());
    					    	responsePacket.setPort(udpPacket.getPort());
    					    	serverSocket.send(responsePacket);
    						} catch(IOException ex) {
    							getLogger().trace(MODULE, ex);
    						}
    					}
    					getServerContext().incrementTPSCounter();
    				} else {
    					isRequestPlacedInInitialRequestQueue = initialRequestQueue.offer(request);

    					if (!isRequestPlacedInInitialRequestQueue) {
    						getLogger().warn(MODULE, "Initial request queue is full, temporarily pausing listener thread execution.");

    						Thread.yield(); //Give chance to other threads
    						isRequestPlacedInInitialRequestQueue = initialRequestQueue.offer(request);

    						if (!isRequestPlacedInInitialRequestQueue) {
    							if(getLogger().isInfoLogLevel()) {
    								getLogger().info(MODULE, "Pausing listener thread using yield did not worked out, " +
    										"forcing listener thread to give up CPU using sleep.");
    							}

    							try {
    								Thread.sleep(100);
    							}catch(InterruptedException interruptedException) {
    								// The stop flag is being checked at top of method so we can rely on it for breaking the flow of execution
    							}

    							isRequestPlacedInInitialRequestQueue = initialRequestQueue.offer(request);

    							if (!isRequestPlacedInInitialRequestQueue) {
    								incrementRequestDroppedCounter(request.getClientAddress().getHostName() , request.serviceRequest);
    								getLogger().warn(MODULE, "!!! Initial request queue is full, " +
    										"could not place request in queue even after maximum attempts, dropping request from " 
    										+ udpPacket.getAddress() + ":" + udpPacket.getPort());
    							}
    						}
    					}
    				}
    			} catch (SocketTimeoutException ste) { 
    				if (isStopRequested()) {
    					break;
    				}
    				ignoreTrace(ste);
    			} catch (Exception exp) {
    				if (isStopRequested()) {
    					getLogger().info(MODULE, "Stop service requested for UDPService, stopping accept new packets.");
    					break;
    				}
    				
    				getLogger().warn(MODULE, "Problem in request listener block, error is : " + exp);
    				getLogger().trace(MODULE,exp);
    			}

    			
        		if (requestCounter.get() > 1500) {
        			try {
        				Thread.sleep(10);
        			}catch(InterruptedException interruptedException) {
        				break;
        			}
        			if (requestCounter.get() > 1500) {
        				requestCounter.set(0);
        			}
        		}
        	}

    		if(getLogger().isInfoLogLevel()) {
    			getLogger().info(MODULE, getServiceIdentifier() + " - stopped accepting new packets on socket: " 
    					+ listeningSocketDetail);
    		}
        }
    }

    protected void incrementRequestReceivedCounter(String clientAddress){
    	getLogger().trace(MODULE, "increment Request received counter of UDP Service is called.");
    }

    protected void incrementRequestReceivedCounter(UDPServiceRequest serviceRequest){
    	incrementRequestReceivedCounter(serviceRequest.getSourceAddress().getHostAddress());
    }

    protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
    	getLogger().trace(MODULE, "increment Request dropped counter of UDP Service is called.");
    }

    protected void incrementRequestDroppedCounter(ServiceRequest request){
    	getLogger().trace(MODULE, "increment Request dropped counter of UDP Service is called.");
    }
    
    protected void incrementResponseCounter(ServiceResponse response){
    	getLogger().trace(MODULE, "increment Response send counter of UDP Service is called.");
    }
    
    /**
     * This hook method is called in case when duplicate detection is enabled and duplicate
     * request is replied with a cached response. At that time we only have cached
     * response bytes from which the implementation needs to extract header information and
     * increment the applicable counter.
     * 
     * @param sourceAddress the address from where request is received
     * @param responseBytes the cached response bytes
     */
    protected void incrementResponseCounter(String sourceAddress, byte[] responseBytes) {
    	getLogger().trace(MODULE, "increment Response send counter of UDP Service is called.");
    }
    
    protected void  incrementResponseCounter(ServiceRequest request , ServiceResponse response) {
    	incrementResponseCounter(response);
    }
    
    protected void incrementDuplicateRequestReceivedCounter(ServiceRequest request){
    	 getLogger().trace(MODULE, "increment Duplicate Request received counter of UDP Service called.");
    }
    
    public class RequestDispatcher implements Runnable {
	        public void run() {
	            int packetDispatchCounter = 0;
	            boolean isRequestSubmittedToWorker = false;
				while(!isStopRequested() || !initialRequestQueue.isEmpty()){
    			try {
	                BaseRequestHandler requestHandler = null;
	                try {
	                    requestHandler = initialRequestQueue.take();

					}catch(InterruptedException e) {
	
	                }
					if (requestHandler != null) {
	
	                    isRequestSubmittedToWorker = executeUDPServiceRequest(requestHandler);
	
	                    if (!isRequestSubmittedToWorker){
                    		getLogger().warn(MODULE, "Worker thread queue is full, temporarily pausing request dispatcher thread execution");
	                    	
	                        Thread.yield(); //Give chance to other threads
	                        isRequestSubmittedToWorker = executeUDPServiceRequest(requestHandler);
	
	                        //int haltCount = 0;
	                    	while (!isRequestSubmittedToWorker /*&& haltCount < 3*/) {
	                    		//if(Logger.isLogLevel(LogLevel.INFO.LEVEL))
	                    			getLogger().warn(MODULE, "Pausing listener thread using yield did not worked out, forcing listener thread to give up CPU using sleep.");
	
	                            try {
	                                Thread.sleep(150);
	                    		}catch(InterruptedException interruptedException) {
	                                //No need to handle or log this exception.
	                            }
	
	                            isRequestSubmittedToWorker = executeUDPServiceRequest(requestHandler);
	                            //haltCount++;
	                        }
	
	                    	
	 					if (packetDispatchCounter > 1000){
								Thread.yield();
								packetDispatchCounter=0;
							}
						}
						if(!isRequestSubmittedToWorker) {
								getLogger().warn(MODULE, "Request is not submitted to Thread Pool");
							incrementRequestDroppedCounter(requestHandler.getClientAddress().getHostAddress() , requestHandler.serviceRequest);
						}
					}
    			} catch (Exception e) {
    				getLogger().trace(MODULE, e);
				}
    		}
				
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE,"Request Dispatching stopped.");
			}
	    }
    }
    
    public abstract class BaseRequestHandler implements Runnable{

        protected long requestReceivedTimeNano;
        protected long responseSentTimeNano;
        private long clientRequestExpiryTime;
        protected T serviceRequest;
        protected V serviceResponse;
        private InetAddress clientAddress;
                
        public BaseRequestHandler(InetAddress clientAddress, int clientPort, byte[] packetBytes, SocketDetail serverSocket) {
        	requestReceivedTimeNano = System.nanoTime();
        	this.setClientAddress(clientAddress);
        	/*
        	 *narendra.pathai
        	 *making this change for duplicate detection JIRA-1868
        	 * 
        	 */
        	serviceRequest = formServiceSpecificRequest(getClientAddress(), clientPort, packetBytes, serverSocket);
        	serviceResponse = formServiceSpecificResposne(serviceRequest);
        	clientRequestExpiryTime = getClientRequestExpiryTime(this.getClientAddress()) * NANO_TO_MILLI;
        }
        
        /**
         * 
         */
        @Override
        public final void run() {
        	if (getLogger().isDebugLogLevel()) {
        		getLogger().debug(MODULE, "Request handler processing started");
        	}
        	
        	if(isRequestExpired()) {
        		getLogger().warn(MODULE, "Request expired in initial wait queue, total wait time : " + ((System.nanoTime() - requestReceivedTimeNano ) / NANO_TO_MILLI) +"ms.");
        		incrementRequestDroppedCounter(clientAddress.getHostAddress() , serviceRequest);
        	} else {
        		handleRequest();
        	}
        	
        }
        protected abstract void handleRequest();
        
		public boolean isRequestExpired() {
		    return (System.nanoTime() - requestReceivedTimeNano) > clientRequestExpiryTime;
		}

		/**
		 * @param clientAddress the clientAddress to set
		 */
		public void setClientAddress(InetAddress clientAddress) {
			this.clientAddress = clientAddress;
		}

		/**
		 * @return the clientAddress
		 */
		public InetAddress getClientAddress() {
			return clientAddress;
		}
		
    } 
    
    class RequestHandler extends BaseRequestHandler {
    	
		public RequestHandler(InetAddress clientAddress, int clientPort,
				byte[] packetBytes, SocketDetail serverSocket) {
			super(clientAddress, clientPort, packetBytes, serverSocket);
		}
		
		
		@Override
		public void handleRequest() {
			if (serviceRequest != null) {
        		try {
        			/*
        			 * Request handling process is protected by holding lock on service request, so that
        			 * multiple threads do not concurrently start processing the same request which can cause
        			 * race conditions and fail the logic in many different ways. This occurs in cases
        			 * when external communication is done and the request handler thread starts leaving and 
        			 * async request handler thread also starts executing the same request then race condition
        			 * occurs between those threads. So to circumvent those issues service request acts as
        			 * mutex for request handling.
        			 */
        			synchronized (serviceRequest) {
        				applyMonitoryLogLevel(serviceRequest, serviceResponse);
        				handleServiceRequest(serviceRequest, serviceResponse);
        				sendResponse(serviceRequest, serviceResponse);
        			}
				 } catch(Throwable exp) {
					 getLogger().error(MODULE, "Error handling request for " + getServiceIdentifier() + " service, Reason: " + exp.getMessage());
					 getLogger().trace(MODULE,exp);
					 incrementRequestDroppedCounter(serviceRequest);
					 removeMonitoryLogLevel(serviceRequest, serviceResponse);
				 }
        	} else {
        		getLogger().error(MODULE, "CRITICAL!!!, problem creating service request context, could not continue further processing of request");
        	}
		}
    	
		public IPacketHash getPacketHash(){
			return serviceRequest.getPacketHash();
		}
		
		public byte[] buildDuplicateResponse(byte[] responseByte) {
			return EliteUDPService.this.buildDuplicateResponse(serviceRequest, serviceResponse, responseByte);
    }
    }
    
    public class LocalRequestHandler extends BaseRequestHandler {
    	
    	/*
    	  	LocalResponseListener is mainly used for In-Memory requests.
    	  	Its significance is to notify interface listener(i.e Database,web service)
    	  	when processing for this particular request get complete. 
    	 */
    	private ILocalResponseListener responseListener;
    	
		public LocalRequestHandler(InetAddress clientAddress, int clientPort,
				byte[] packetBytes,ILocalResponseListener responseListener) {
			super(clientAddress, clientPort, packetBytes, new SocketDetail("0.0.0.0", 0));
			this.responseListener = responseListener;
		}
		
		@Override
		public void handleRequest() {
			if (serviceRequest != null) {
				getServerContext().addAndGetAverageRequestCount(1);
        		try {
                    /*
        			 * Request handling process is protected by holding lock on service request, so that
        			 * multiple threads do not concurrently start processing the same request which can cause
        			 * race conditions and fail the logic in many different ways. This occurs in cases
        			 * when external communication is done and the request handler thread starts leaving and 
        			 * async request handler thread also starts executing the same request then race condition
        			 * occurs between those threads. So to circumvent those issues service request acts as
        			 * mutex for request handling.
        			 */
                    synchronized (serviceRequest) {
                    	
                    	/* Adding LocalResponseListener to request Parameter to
        			  make it available to service at time of notifying interface.
        			  listener(i.e Database,web service).   
                    	 */
                    	serviceRequest.setParameter("LocalResponseListener", responseListener);
                    	applyMonitoryLogLevel(serviceRequest, serviceResponse);
                    	handleServiceRequest(serviceRequest, serviceResponse);
                    	sendResponse(serviceRequest, serviceResponse);
                    }
				 } catch(Throwable exp) {
					getLogger().error(MODULE, "Error handling request for " + getServiceIdentifier() + " service, Reason: " + exp.getMessage());
        			getLogger().trace(MODULE,exp);
        			removeMonitoryLogLevel(serviceRequest, serviceResponse);
				 }
        	} else {
        		getLogger().warn(MODULE, "CRITICAL!!!, problem creating service request context, could not continue further processing of request");
        	}
		}
    	
    }
    
    public class AsyncRequestHandler implements Runnable {
    	T serviceRequest;
    	V serviceResponse;
    	AsyncRequestExecutor<T, V> requestExecutor;
    	
    	public AsyncRequestHandler(T serviceRequest, V  serviceResponse,
    			AsyncRequestExecutor<T, V> requestExecutor) {
    		this.serviceRequest = serviceRequest;
    		this.serviceResponse = serviceResponse;
    		this.requestExecutor = requestExecutor;
    	}
    	
		@Override
		public void run() {
			getServerContext().addAndGetAverageRequestCount(1);
			try {
				/*
    			 * Request handling process is protected by holding lock on service request, so that
    			 * multiple threads do not concurrently start processing the same request which can cause
    			 * race conditions and fail the logic in many different ways. This occurs in cases
    			 * when external communication is done and the request handler thread starts leaving and 
    			 * async request handler thread also starts executing the same request then race condition
    			 * occurs between those threads. So to circumvent those issues service request acts as
    			 * mutex for request handling.
    			 */
				synchronized (serviceRequest) {
					serviceResponse.setFurtherProcessingRequired(true);
					serviceResponse.setProcessingCompleted(true);
					applyMonitoryLogLevel(serviceRequest, serviceResponse);
					requestExecutor.handleServiceRequest(serviceRequest, serviceResponse);
					handleAsyncServiceRequest(serviceRequest, serviceResponse);
					sendResponse(serviceRequest, serviceResponse);
				}
			} catch(Throwable exp) {
				getLogger().error(MODULE, "Error handling request for " + getServiceIdentifier() + " service, Reason: " + exp.getMessage());
				getLogger().trace(MODULE,exp);
				removeMonitoryLogLevel(serviceRequest, serviceResponse);
			}
		}
    	
    }
    
    protected final void submitAsyncRequest(T serviceRequest, V serviceResponse,
    		AsyncRequestExecutor<T, V> requestExecutor) {
    	AsyncRequestHandler asyncRequestHandler = new AsyncRequestHandler(serviceRequest, serviceResponse, requestExecutor);
    	asynchronousTaskExecutor.execute(asyncRequestHandler);
    }
        
    private void sendResponse(T serviceRequest, V serviceResponse) throws IOException {
    	if (serviceResponse != null && !serviceResponse.isMarkedForDropRequest() && serviceResponse.isProcessingCompleted()) {
    		finalPreResponseProcess(serviceRequest, serviceResponse);
    	}
    	
    	
    	ILocalResponseListener localResponseListener = getLocalResponseListener(serviceRequest);
    	if (serviceResponse != null && !serviceResponse.isMarkedForDropRequest() 
    			&& serviceResponse.isProcessingCompleted()) {
    		
    		byte [] responseBytes = serviceResponse.getResponseBytes();
    		
    		if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Sending Response to " + serviceRequest.getSourceAddress().getHostAddress() + ":"
						+ serviceRequest.getSourcePort()
						+ serviceResponse);

			}
    	   	
    		if (localResponseListener != null) {
    	   		sendLocalResponse(localResponseListener,responseBytes);
    		} else {
    			sendRemoteResponse(serviceRequest, serviceResponse, responseBytes);
    		}
    		
    		getServerContext().incrementTPSCounter();
            incrementResponseCounter(serviceRequest, serviceResponse);
            
            int endToEndResponseTime = (int) (( System.nanoTime()-serviceRequest.getRequestReceivedNano() )/NANO_TO_MILLI);
            
            if (endToEndResponseTime > CommonConstants.MAX_RESPONSE_TIME_MS) {
            	generateAlertOnHighResponseTime(endToEndResponseTime);
            }
            
            if (getLogger().isInfoLogLevel()) {
            	getLogger().info(MODULE,"Total response time : " + endToEndResponseTime  + "ms.");
            }
            
        	getServerContext().addTotalResponseTime((long)endToEndResponseTime * NANO_TO_MILLI);
            totalResponseTime += endToEndResponseTime;
            totalResponseGiven++;
    	} else if(serviceResponse.isMarkedForDropRequest()) {
    		incrementRequestDroppedCounter(serviceRequest);
    	   	if (localResponseListener != null) {
    	   		sendLocalResponse(localResponseListener, null);
    		}
    		if(getLogger().isInfoLogLevel()) {
    			getLogger().info(MODULE, "Request Dropped");
    		}
    	}
    	removeMonitoryLogLevel(serviceRequest, serviceResponse);
    }

	private void sendRemoteResponse(T serviceRequest, V serviceResponse,
			byte[] responseBytes) throws IOException {
		RequestListener requestListener = listeningSocketDetailToListener.get(serviceRequest.getServerSocket());
		
		cacheResponse(serviceRequest, serviceResponse, requestListener);
		
		DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
		responsePacket.setAddress(serviceRequest.getSourceAddress());
		responsePacket.setPort(serviceRequest.getSourcePort());
		requestListener.serverSocket.send(responsePacket);
		
		postResponseProcessing(serviceRequest, serviceResponse);
	}

	private void cacheResponse(T serviceRequest, V serviceResponse,
			RequestListener requestListener) {
		if (isDuplicateDetectionEnabled()) {
			requestListener.duplicateRequestDetector.setResponseBytes(serviceRequest,serviceResponse.getResponseBytes());
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	public abstract void handleServiceRequest(T request, V response);

	protected byte[] buildDuplicateResponse(T request, V response, byte[] cachedResponseBytes) {
		return cachedResponseBytes;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	public abstract void handleAsyncServiceRequest(T request, V response);
	
	/**
	 * 
	 * @param serviceRequest
	 * @param serviceResponse
	 */
	protected void postResponseProcessing(T serviceRequest, V serviceResponse) {

	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	protected void finalPreResponseProcess(T request, V response){

	}
	
    public long getClientRequestExpiryTime(InetAddress address) {
    	return 3000;
    }
 
    private final boolean executeUDPServiceRequest(BaseRequestHandler runnable){

        /* Null check for threadExecutor is required, in case default thread pool,
         * implementation is disabled by the sub implementation. */
    	if (runnable != null) {
    		if (runnable.isRequestExpired()) {
    			getLogger().warn(MODULE, "Request expired in initial wait queue, total wait time : " + ((System.nanoTime() - runnable.serviceRequest.getRequestReceivedNano() ) / NANO_TO_MILLI) +"ms.");
        		incrementRequestDroppedCounter(runnable.getClientAddress().getHostAddress() , runnable.serviceRequest);
        		return true;
        	}
    		
            try {
                synchronousTaskExecutor.execute(runnable);
                return true;
    		}catch(RejectedExecutionException rejExp){
    		   	generateAlertOnThreadNotAvailable();
            }
        } else {
        	getLogger().warn(MODULE, "Null command received for execution, ignoring the request.");
        }

        return false;
    }
    
	protected abstract int getMinThreadPoolSize();
	protected abstract int getMaxThreadPoolSize();
	protected abstract int getMainThreadPriority();
	protected abstract int getWorkerThreadPriority();
	protected abstract int getThreadKeepAliveTime();
	protected abstract int getMaxRequestQueueSize();
	protected abstract int getSocketReceiveBufferSize();
	protected abstract int getSocketSendBufferSize();
	
	class AverageResponseTimeCalculator extends BaseIntervalBasedTask{

		private long initialDelay;
		private long intervalSeconds;
		
		public AverageResponseTimeCalculator(long initialDelay,long intervalSeconds){
			this.initialDelay = initialDelay;
			this.intervalSeconds = intervalSeconds;			
		}
		
		@Override
		public long getInitialDelay() {
			return initialDelay;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public long getInterval() {
			return intervalSeconds;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			if(perSecondTotalRequestRecieved != 0){
				synchronized(perSecondAvgResponseTimeMiliLock){
					perSecondAvgResponseTimeMili = (perSecondTotalResponseTimeNano/perSecondTotalRequestRecieved)/NANO_TO_MILLI;
					perSecondTotalRequestRecieved = (long)0;
					perSecondTotalResponseTimeNano = (long)0;    				
				}
			}else{
				synchronized(perSecondAvgResponseTimeMiliLock){
	    			perSecondAvgResponseTimeMili = (long)0;
	    		}
			}    					
		}
		
	}
    public long getAvgResponseTime(){
    	return perSecondAvgResponseTimeMili;
    }
    
    /* --- Service Sync Thread Summary -- */   
    public final int getMinThreadCount(){
		if (synchronousTaskExecutor != null)
            return synchronousTaskExecutor.getCorePoolSize();
        return 0;
    }
	public final int getMaxThreadCount(){
		if (synchronousTaskExecutor != null)
            return synchronousTaskExecutor.getMaximumPoolSize();
        return 0;
    }
	public final int getPeakThreadCount(){
		if (synchronousTaskExecutor != null)
            return synchronousTaskExecutor.getLargestPoolSize();
        return 0;
    }
	public final int getCurrentPoolSize(){
		if (synchronousTaskExecutor != null)
            return synchronousTaskExecutor.getPoolSize();
        return 0;
    }
	public final int getActiveThreadCount(){
		if (synchronousTaskExecutor != null)
            return synchronousTaskExecutor.getActiveCount();
        return 0;
    }
	public int getRequestQueueSize(){
        return initialRequestQueue.size();
    }
	
	
	/* --- Service Async Thread Summary -- */
	public final int getAsyncActiveThreadCount(){
		if (asynchronousTaskExecutor != null)
            return asynchronousTaskExecutor.getActiveCount();
        return 0;
    }

    public final int getAsyncMinThreadCount(){
		if (asynchronousTaskExecutor != null)
            return asynchronousTaskExecutor.getCorePoolSize();
        return 0;
    }
	public final int getAsyncMaxThreadCount(){
		if (asynchronousTaskExecutor != null)
            return asynchronousTaskExecutor.getMaximumPoolSize();
        return 0;
    }
	public final int getAsyncPeakThreadCount(){
		if (asynchronousTaskExecutor != null)
            return asynchronousTaskExecutor.getLargestPoolSize();
        return 0;
    }
	public final int getAsyncCurrentPoolSize(){
		if (asynchronousTaskExecutor != null)
            return asynchronousTaskExecutor.getPoolSize();
        return 0;
    }
	public int getAsyncRequestQueueSize(){
        return asynchronousQueue.size();
    }

	protected void handleLocalRequest(LocalRequestHandler localRequestHandler,
			ILocalResponseListener responseListener) {
		
		incrementRequestReceivedCounter(localRequestHandler.serviceRequest);
		
        boolean isRequestPlacedInLocalQueue = initialRequestQueue.offer(localRequestHandler);

        if(!isRequestPlacedInLocalQueue) {

        	getLogger().warn(MODULE, "Local request queue is full, temporarily pausing listener thread execution.");

        	try {
                Thread.sleep(100);
            } catch(InterruptedException ex) {  }

            isRequestPlacedInLocalQueue = initialRequestQueue.offer(localRequestHandler);
            if(!isRequestPlacedInLocalQueue) {
            	getLogger().warn(MODULE, "Local request dropped due to queue overflow");

            	if(responseListener!=null){
            		responseListener.requestTimedout(null);
            	}
            }
        }
		
	}

	protected abstract void applyMonitoryLogLevel(T request, V response);

	protected abstract void removeMonitoryLogLevel(T request, V response);

	protected void generateAlertOnThreadNotAvailable(){
		getLogger().trace(MODULE, "Generate Alert while worker Thread not available.");
	}
	protected void generateAlertOnHighResponseTime(int endToEndResponseTime){
		getLogger().trace(MODULE, "Generate Alert on High Response Time.");
	}

	public int getRequestCounter(){
		return requestCounter.get();
	}
	
	private ILocalResponseListener getLocalResponseListener(T request) {
		if (request.getParameter("LocalResponseListener") == null) {
			return null;
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Notifying Local Response Listener as this request received from " +
					"web service or in-memory interface.");
		}
		return (ILocalResponseListener)request.getParameter("LocalResponseListener");
	}
	
	private void sendLocalResponse(ILocalResponseListener localResponseListener,
			byte[] responseBytes) {
	   	if (responseBytes != null) {
	   		localResponseListener.responseReceived(responseBytes);
	   	} else {
	   		localResponseListener.requestDropped(responseBytes);
	   	}
	}
	
	public String getThreadSummary(){
		StringBuilder responseBuilder = new StringBuilder();	
		responseBuilder.append("\n" +getServiceName()+" Thread Summary");
		responseBuilder.append("\n-------------------------------------------");
		responseBuilder.append("\n Service");
		responseBuilder.append("\n   Thread Pool              : ["+ getMinThreadCount() + "-" + getMaxThreadCount() + "]");
		responseBuilder.append("\n   Active Threads           : "+ getActiveThreadCount());
		responseBuilder.append("\n   Current Threads in Pool  : "+ getCurrentPoolSize());
		responseBuilder.append("\n   Peak Threads             : " + getPeakThreadCount());
		responseBuilder.append("\n   Request Queue            : "+ getRequestQueueSize());
		responseBuilder.append("\n");
		responseBuilder.append("\n Async Tasks");
		responseBuilder.append("\n   Thread Pool              : ["+ getAsyncMinThreadCount() + "-" + getAsyncMaxThreadCount() + "]");
		responseBuilder.append("\n   Active Threads           : "+ getAsyncActiveThreadCount());
		responseBuilder.append("\n   Current Threads in Pool  : "+ getAsyncCurrentPoolSize());
		responseBuilder.append("\n   Peak Threads             : " + getAsyncPeakThreadCount());
		responseBuilder.append("\n   Request Queue            : "+ getAsyncRequestQueueSize());
		responseBuilder.append("\n");
		
		return responseBuilder.toString();	
	}
	
	/**
	 * This is the class for duplicate request detection
	 * This class contains multiple Duplicate request detection handlers which are created based on the
	 * CLIENT ADDRESS. 
	 * Each Handler contains three maps which are rolled over based on PURGE interval timings
	 * On each rollover NEW->MAP1->MAP2->MAP3->DELETED
	 * 
	 * @author narendra.pathai
	 */
	public class DuplicateRequestDetector {
		private ConcurrentHashMap<String,DuplicateDetectionHandler> duplicateDetectionMap; 
		private int purgeInterval;

		public DuplicateRequestDetector(int purgeInterval){
			this.duplicateDetectionMap = new ConcurrentHashMap<String, DuplicateDetectionHandler>();
			this.purgeInterval = purgeInterval;
		}

		public boolean isDuplicateRequest(RequestHandler request){
			IPacketHash hash = request.getPacketHash();
			DuplicateDetectionHandler duplicateDetHandler = this.duplicateDetectionMap.get(request.getClientAddress().getHostAddress());
			if(duplicateDetHandler == null){
				duplicateDetHandler = new DuplicateDetectionHandler(purgeInterval);
				duplicateDetectionMap.put(request.getClientAddress().getHostAddress(), duplicateDetHandler);
			}
			return duplicateDetHandler.isDuplicateRequest(hash);
		}

		public byte[] getResponseBytes(RequestHandler request){
			IPacketHash packetHash = request.getPacketHash();
			DuplicateDetectionHandler duplicateRequestHandler = null;
			duplicateRequestHandler = duplicateDetectionMap.get(request.getClientAddress().getHostAddress());

			if(duplicateRequestHandler != null){
				return duplicateRequestHandler.getResponseBytes(packetHash);
			}
			return null;
		}

		public void setResponseBytes(UDPServiceRequest request, byte[] responseBytes){
			IPacketHash packetHash = request.getPacketHash();
			DuplicateDetectionHandler duplicateRequestHandler = null;
			duplicateRequestHandler = duplicateDetectionMap.get(request.getSourceAddress().getHostAddress());

			if(duplicateRequestHandler != null){
				duplicateRequestHandler.setResponseBytes(packetHash, responseBytes);
			}
		}




		/*---------------Inner Classes----------------*/

		public class DuplicateDetectionHandler {
			private int rollingTimeInterval;
			private HashMap<IPacketHash,byte[]> duplicateRequestPool1;
			private HashMap<IPacketHash,byte[]> duplicateRequestPool2;
			private HashMap<IPacketHash,byte[]> duplicateRequestPool3;
			private Lock poolLock = new ReentrantLock();

			public DuplicateDetectionHandler(int purgeInterval){
				rollingTimeInterval = (int) (purgeInterval/3.0) + 1;
				duplicateRequestPool1 = new HashMap<IPacketHash, byte[]>(20000);
				duplicateRequestPool2 = new HashMap<IPacketHash, byte[]>(20000);
				duplicateRequestPool3 = new HashMap<IPacketHash, byte[]>(20000);
				getServerContext().getTaskScheduler().scheduleIntervalBasedTask(new DuplicateRequestRollerScheduler());
			}

			public boolean isDuplicateRequest(IPacketHash hash){
				if(duplicateRequestPool2.containsKey(hash))
					return true;
				else if(duplicateRequestPool1.containsKey(hash))
					return true;
				else if(duplicateRequestPool3.containsKey(hash))
					return true;
				duplicateRequestPool1.put(hash, null);
				return false;
			}

			public void doRollover(){
				lock();
				HashMap<IPacketHash,byte[]> newDuplicateReqeustPool = duplicateRequestPool3;
				duplicateRequestPool3 = duplicateRequestPool2;
				duplicateRequestPool2 = duplicateRequestPool1;
				newDuplicateReqeustPool.clear();
				duplicateRequestPool1 = newDuplicateReqeustPool;
				release();
			}

			public void setResponseBytes(IPacketHash hash, byte[] responseBytes){
				lock();
				if(duplicateRequestPool1.containsKey(hash)){
					duplicateRequestPool1.put(hash, responseBytes);
				}else if(duplicateRequestPool2.containsKey(hash)){
					duplicateRequestPool2.put(hash, responseBytes);
				}else if(duplicateRequestPool3.containsKey(hash)){
					duplicateRequestPool3.put(hash, responseBytes);
				}else{
					duplicateRequestPool1.put(hash, responseBytes);
				}
				release();
			}

			public byte[] getResponseBytes(IPacketHash hash){
				byte[] responseBytes = duplicateRequestPool1.get(hash);
				if(responseBytes != null)
					return responseBytes;
				responseBytes = duplicateRequestPool2.get(hash);
				if(responseBytes != null)
					return responseBytes;
				responseBytes = duplicateRequestPool3.get(hash);
				return responseBytes;
			}

			private void lock(){
				poolLock.lock();
			}

			private void release(){
				poolLock.unlock();
			}

			/*-------------Inner Classes-------------*/
			class DuplicateRequestRollerScheduler extends BaseIntervalBasedTask{

				@Override
				public long getInterval() {
					return rollingTimeInterval;
				}

				@Override
				public void execute(AsyncTaskContext context) {
					doRollover();
				}
			}
		}
	}
}