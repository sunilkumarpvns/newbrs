package com.elitecore.aaa.radius.plugins.quotamgr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.plugins.quotamgr.conf.impl.QuotaManagerPluginConfigurable;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.system.comm.ILocalResponseListener;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class QuotaManagerPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "QTA-MGR-PLGN";
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private String disconnectURL;
	private String attributeId ="";
	private String getAttributeFromUrl(String url){		
		StringBuffer buffer = new StringBuffer();
		int fromIndex = buffer.indexOf("{");
		int endIndex  = buffer.indexOf("}");
		if(fromIndex != -1 && endIndex != -1){
			 
			return buffer.substring(fromIndex+1,endIndex);
		}
		return null;
	}
	@Override
	public void init() throws InitializationFailedException {
		QuotaManagerPluginConfigurable quotaManagerPluginConfImpl = (QuotaManagerPluginConfigurable)getPluginConfiguration();
		if(quotaManagerPluginConfImpl == null){
			throw new InitializationFailedException("Quota Plugin configuration is null");
		}
		disconnectURL = quotaManagerPluginConfImpl.getDiconnectURL();
		attributeId = getAttributeFromUrl(disconnectURL);
	}
	@Override
	public void reInit() throws InitializationFailedException {
		init();
	}

	public QuotaManagerPlugin(PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(15, new EliteThreadFactory("PLG-SCH", "PLG-SCH", Thread.NORM_PRIORITY));
	}

	@Override
	public void handlePostRequest(RadServiceRequest request,
			RadServiceResponse response, String argument, PluginCallerIdentity callerID, ISession session) {

		String sessionId = getSessionIdFrom(request);
		
		if(sessionId == null)
			return;
		
		IRadiusAttribute timeThresholdAttribute = response.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID + ":" + RadiusAttributeConstants.ELITE_TIME_QUOTA_THRESHOLD);
		if(QuotaManager.getInstance().get(sessionId)== null)
			if(timeThresholdAttribute!= null){
				RequestSession requestSession = new RequestSession(request);
				QuotaManager.getInstance().put(sessionId, requestSession);
				int timeQuota = timeThresholdAttribute.getIntValue();
				scheduleSingleExecutionTask(new QuotaReachedExecutionTask(timeQuota,sessionId));		
			}
	}

	@Override
	public void handlePreRequest(RadServiceRequest request,
			RadServiceResponse response, String argument, PluginCallerIdentity callerID, ISession session) {

		String sessionId = getSessionIdFrom(request);
		RequestSession requestSession =  QuotaManager.getInstance().get(sessionId);
		if(requestSession != null){
			if(attributeId != null && attributeId.length() > 0){
				IRadiusAttribute attribute = request.getRadiusAttribute(attributeId,true);
				if(attribute != null)
					requestSession.urlAttributeValue = attribute.getStringValue(); 
			}
		}
	}

	private void scheduleSingleExecutionTask(final SingleExecutionAsyncTask task) {
    	
    	if (task != null) {
    		if (task.getInitialDelay() > 0) {
    			scheduledThreadPoolExecutor.schedule(new Runnable(){
					@Override
					public void run() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl(getPluginContext().getServerContext());
	    				try {
	    					task.execute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
					}}, task.getInitialDelay(), task.getTimeUnit());
    		} else {
    			scheduledThreadPoolExecutor.execute(new Runnable() {

					@Override
					public void run() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl(getPluginContext().getServerContext());
	    				try {
	    					task.execute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
					}});
    		}
    	} 
    }
    
    private class AsyncTaskContextImpl implements AsyncTaskContext {
		
		private Map<String, Object> attributes;
		
		public AsyncTaskContextImpl(ServerContext serverContext) {
		}

		@Override
		public void setAttribute(String key, Object attribute) {
			if (attributes == null) {
				synchronized (attributes) { //NOSONAR - Reason: Double-checked locking should not be used
					if (attributes == null) {
						attributes = new HashMap<String, Object>();
					}
				}
			}
			attributes.put(key, attribute);
		}

		@Override
		public Object getAttribute(String key) {
			if (attributes != null) {
				return attributes.get(key);
			}
			return null;
		}
		
	}
    
    private String getSessionIdFrom(RadServiceRequest request){
    	Collection<IRadiusAttribute> classAttributs = request.getRadiusAttributes(RadiusAttributeConstants.CLASS,true);
    	if(classAttributs == null)
    		return null;
		for(IRadiusAttribute classAttribute: classAttributs){
			if(classAttribute != null){
				String classAttributeValue = classAttribute.getStringValue();
				String avps[] = classAttributeValue.split(",");
				for(String avp : avps ){
					String avpVlaues[] = avp.split("=");
					if(avpVlaues.length == 2){
						if("Session-Id".equalsIgnoreCase(avpVlaues[0]))						
							return avpVlaues[1];
					}
				}
			}
    	}
		return null;				
	}
    private void callDisconnectUrl(String sessionId){
		String disconnectUrl = new String(this.disconnectURL);
		StringBuffer buffer = new StringBuffer();
		int fromIndex = buffer.indexOf("{");
		int endIndex  = buffer.indexOf("}");
		if(fromIndex != -1 && endIndex != -1){
			String attributeId = buffer.substring(fromIndex+1,endIndex);
			RequestSession session = QuotaManager.getInstance().get(sessionId);			
			
			if(session != null){
				disconnectUrl.replaceFirst(attributeId, session.urlAttributeValue);
				disconnectUrl = disconnectUrl.replaceAll("{", "");
				disconnectUrl = disconnectUrl.replaceAll("}", "");
			}
		}
		try {
			URL url = new URL(disconnectUrl);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(2000);
			LogManager.getLogger().info(MODULE, "Trying to disconnect using URL: " + disconnectUrl);			
			connection.connect();
		} catch (MalformedURLException e) {
			LogManager.getLogger().error(MODULE, "error while calling disconnect url reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}catch(SocketTimeoutException e){
			LogManager.getLogger().error(MODULE, "error while calling disconnect url reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}catch (IOException e) {
			LogManager.getLogger().error(MODULE, "error while calling disconnect url reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}
	
    private void thresholdReached(String sessionId){
    	try {
    		final RequestSession session  = QuotaManager.getInstance().remove(sessionId);
    		if(session == null)
    			return;
    		LogManager.getLogger().info(MODULE, "Threashold reached for session id: " + sessionId);
			((RadAuthServiceContext)getPluginContext().getServerContext()).submitLocalRequest(session.packet.getBytes(true),new ILocalResponseListener() {
				
				@Override
				public void responseReceived(byte[] response) {
					
					RadiusPacket radiusPacket = new RadiusPacket();					
					radiusPacket.setBytes(response);
					LogManager.getLogger().info(MODULE, "Response received: " + radiusPacket.toString());
					if(radiusPacket.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE){
						callDisconnectUrl(session.urlAttributeValue);						
					}					
				}
				
				@Override
				public void requestTimedout(byte[] request) {
				}
				
				@Override
				public void requestDropped(byte[] request) {
				}
			});
		} catch (UnknownHostException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
    }
    
    private class QuotaReachedExecutionTask extends BaseSingleExecutionAsyncTask{
    	private int initialDelay;
    	private String sessionId;
    	private QuotaReachedExecutionTask(int initialDelay,String sessionId){
    		this.initialDelay = initialDelay;
    		this.sessionId = sessionId;    		
    	}
		@Override
		public void execute(AsyncTaskContext context) {
			thresholdReached(sessionId);
		}
		@Override
		public long getInitialDelay() {		
			return initialDelay;
		} 
    	
    }
    
    public class RequestSession{
		private RadiusPacket packet;
		private String urlAttributeValue = "";
		public RequestSession(RadServiceRequest request){
			RadiusPacket packet = new RadiusPacket();
			packet.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute tempAttribute = request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME); 
			if( tempAttribute != null){
				try {
					packet.addAttribute((IRadiusAttribute)tempAttribute.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().error(MODULE, e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
				}		
			}			
			tempAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS); 
			if( tempAttribute != null){
				try {
					packet.addAttribute((IRadiusAttribute)tempAttribute.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().error(MODULE, e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
				}		
			}			
			tempAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT); 
			if( tempAttribute != null){
				try {
					packet.addAttribute((IRadiusAttribute)tempAttribute.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().error(MODULE, e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
				}		
			}			
			Collection<IRadiusAttribute>tempAttributes = request.getRadiusAttributes(RadiusAttributeConstants.CLASS,true); 
			if( tempAttributes != null){
				try {
					for(IRadiusAttribute temp : tempAttributes)
						packet.addAttribute((IRadiusAttribute)temp.clone());
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().error(MODULE, e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
				}		
			}
			// Adding ServiceType attribute with value Authorize-Only. 
			tempAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			tempAttribute.setIntValue(17);// <value id="17" name="Authorize-Only"/>
			packet.addAttribute(tempAttribute);
		}			
	}
    
    
    
    
    /*
    private void scheduleIntervalBasedTask(final IntervalBasedTask task) {
    	if (task != null) {
    		if (task.isFixedDelay()) {
	    		scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){
	    			@Override
					public void run() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl(getPluginContext().getServiceContext().getServerContext());
	    				
	    				try {
	    					task.preExecute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
						
	    				try {
	    					task.execute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
						
	    				try {
	    					task.postExecute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
						
					}}, task.getInitialDelay(), task.getIntervalSeconds(), TimeUnit.SECONDS);
    		}else {
	    		scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable(){
	    			@Override
					public void run() {
	    				AsyncTaskContextImpl taskContext = new AsyncTaskContextImpl(getPluginContext().getServiceContext().getServerContext());
	    				try {
	    					task.preExecute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
						
	    				try {
	    					task.execute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
						
	    				try {
	    					task.postExecute(taskContext);
	    				}catch(Throwable t) {
	    					
	    				}
						
					}}, task.getInitialDelay(), task.getIntervalSeconds(), TimeUnit.SECONDS);
    		}
    	}
    }
    */
}
