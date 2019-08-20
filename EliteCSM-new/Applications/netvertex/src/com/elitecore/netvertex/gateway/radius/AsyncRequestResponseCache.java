package com.elitecore.netvertex.gateway.radius;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;

public class AsyncRequestResponseCache {

	private static final String MODULE = "RAD-REQ-RSP-CACHE";
	private static final int INITIAL_CAPACITY = 256;
	private ConcurrentHashMap<String, RequestResponseWrapper> reqRespPool;
	private ConcurrentHashMap<String, RequestResponseWrapper> oldReqRespPool;
	private CacheCleanUpTask cacheCleanUpTask;
	private NetVertexServerContext serverContext;
	private int concurrencyLevel = 1;
	
	public AsyncRequestResponseCache(NetVertexServerContext context) {
		this.serverContext = context;
		int maxThread = serverContext.getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getMaximumThread();		
		concurrencyLevel = maxThread < 2 ? maxThread : maxThread / 2 ;
		reqRespPool = new ConcurrentHashMap<String, RequestResponseWrapper>(INITIAL_CAPACITY, CommonConstants.DEFAULT_LOAD_FACTOR, concurrencyLevel);
		oldReqRespPool = new ConcurrentHashMap<String, AsyncRequestResponseCache.RequestResponseWrapper>(INITIAL_CAPACITY, CommonConstants.DEFAULT_LOAD_FACTOR, concurrencyLevel);
		cacheCleanUpTask = new CacheCleanUpTask();
	}

	public void init() throws InitializationFailedException{
		LogManager.getLogger().info(MODULE, "Initializing Asnyc Request Response Cache");
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(cacheCleanUpTask);
		LogManager.getLogger().info(MODULE, "Asnyc Request Response Cache initialization Completed");
	}

	public void addRequestResponse(RadServiceRequest request , RadServiceResponse response, String id){
		if(response != null && request != null && id != null){
			reqRespPool.put(id, new RequestResponseWrapper(request, response));
		}
	}

	public RequestResponseWrapper getRequestResponse(String id){
		RequestResponseWrapper wrapper = reqRespPool.get(id);
		if(wrapper != null) {
			return wrapper;
		}

		wrapper = oldReqRespPool.get(id);
		if(wrapper == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Radius response not found for ID: " + id);
			}
		}
		return wrapper;
	}

	public class RequestResponseWrapper{
		private RadServiceResponse response;
		private RadServiceRequest request;

		public RequestResponseWrapper(RadServiceRequest request ,RadServiceResponse response){
			this.request = request;
			this.response = response;
		}

		public RadServiceResponse getResponse(){
			return response;
		}

		public RadServiceRequest getRequest(){
			return request;
		}
	}

	private class CacheCleanUpTask extends BaseIntervalBasedTask{

		private static final String MODULE = "RAD-CACHE-CLNR";
		private static final int INITIAL_DELAY = 120;
		private static final int INTERVAL = 10;

		@Override
		public void execute(AsyncTaskContext context) {
			try{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Clearing Request Response Cache");
				}

				oldReqRespPool = reqRespPool;
				reqRespPool = new ConcurrentHashMap<String, AsyncRequestResponseCache.RequestResponseWrapper>(INITIAL_CAPACITY, CommonConstants.DEFAULT_LOAD_FACTOR, concurrencyLevel);

			}catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while clearing Request Response Cache. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		@Override
		public long getInitialDelay() {
			return INITIAL_DELAY;
		}

		@Override
		public long getInterval() {
			return INTERVAL;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}
	}
}