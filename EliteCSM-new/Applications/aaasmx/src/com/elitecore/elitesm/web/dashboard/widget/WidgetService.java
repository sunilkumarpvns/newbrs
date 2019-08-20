package com.elitecore.elitesm.web.dashboard.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.elitesm.util.EliteSchedular;
import com.elitecore.elitesm.web.dashboard.widget.conf.WidgetConfiguration;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

/**
 * Provide service for handle Widgets Request
 * 
 * @author punit.j.patel
 *
 */
public class WidgetService {
	
	private static final String MODULE = "WIDGETSERVICE";
	private static final String INTERVAL_KEY = "interval";
	private static final String FREEZE_KEY="freeze";
	private static final String SERVER_KEY="aaaServerIds";  
	private static final String DEREGISTER_KEY="deRegister";
	
	
	private Map<String, WidgetListener> widgetListenerMap;
	private WebSocketListener webSocketListener;
	private WidgetConfiguration widgetConfiguration;
	private EliteSchedular widgetSchedular;
	private EliteSchedular socketSchedular;
	
	public WidgetService() {
		widgetListenerMap = new HashMap<String, WidgetListener>();
		widgetConfiguration = new WidgetConfiguration();
	}
	
	public void init() throws InitializationFailedException {
		widgetConfiguration.init();
		
		widgetSchedular = new EliteSchedular(5);
		socketSchedular = new EliteSchedular(5);
		
		for(String widgetType : widgetConfiguration.getWidgetTypes()) {
			WidgetListener widgetListener = new WidgetListener(widgetType, widgetConfiguration.getInterval(), widgetConfiguration.getWidgetDataProvider(widgetType));
			widgetListenerMap.put(widgetType, widgetListener);
			widgetSchedular.scheduleIntervalBasedTask(widgetListener);
		}
		
		webSocketListener = new WebSocketListener(widgetConfiguration.getInterval());
		socketSchedular.scheduleIntervalBasedTask(webSocketListener);
		
	}
	
	/**
	 * Parse Widget Request,  create Widget Handler Object and register it in WidgetListener
	 * and WebSocketListener 
	 * 
	 * @param widgetRequest : the <code>WidgetRequest</code> object that contains the
	 * 						  client's request	
	 */
	public void processWidgetRequest(WidgetRequest widgetRequest) {
		if( getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Received widget request : " + widgetRequest);
		}
		try {
			JSONObject jsonObject = JSONObject.fromObject(widgetRequest.getRequestData());
			WidgetJSON widgetJSON = (WidgetJSON) JSONObject.toBean(jsonObject, WidgetJSON.class);
			
			if( widgetJSON == null || widgetJSON.getHeader() == null) {
				getLogger().error(MODULE, "Widget request failed, Reason : Invalid request");
				return;
			}
			
			String widgetType = widgetJSON.getHeader().getType();
			jsonObject = JSONObject.fromObject(widgetJSON.getBody());
			
			if( jsonObject.has( DEREGISTER_KEY ) ) {
				deRegisterWidget(widgetRequest.getSocketID(),widgetJSON.getHeader().id);
				getLogger().info(MODULE, "Succesfully De Register Widget Listerner for ID " +widgetJSON.getHeader().id);
			}
			
			if( jsonObject.has( INTERVAL_KEY ) ) {
				
				int interval = jsonObject.getInt(INTERVAL_KEY);
				WidgetDataProvider widgetDataProvider = widgetConfiguration.getWidgetDataProvider(widgetType);
				
				String serverKey = null;
				if(jsonObject.has( SERVER_KEY )){
					serverKey = jsonObject.getString(SERVER_KEY);
				}
				
				if(widgetDataProvider != null) {
					List<WidgetJSON> initialJSONData =  widgetDataProvider.provideInitialData(serverKey); 
					if(initialJSONData != null && !initialJSONData.isEmpty()) {
						for(WidgetJSON initialJSON : initialJSONData) {
							initialJSON.getHeader().setId(widgetJSON.getHeader().getId());
						}
						String data = JSONArray.fromObject(initialJSONData).toString();
						widgetRequest.getWidgetDataSender().send(data);
					}
					
					
					
					WidgetHandler widgetHandler = new WidgetHandler(widgetJSON.getHeader().getId(), interval, widgetRequest.getWidgetDataSender(),serverKey);
				
					
					register(widgetRequest.getSocketID(), widgetType, widgetHandler);
				} else {
					getLogger().error(MODULE, "Widget request failed, reason : Receive Invalid Widget Type : " + widgetType );
				}
			}
			
			
			if( jsonObject.has( FREEZE_KEY ) ) {
				
				getLogger().info(MODULE,"Processing request for Freeze or Unfreeze the Widget");
				
				boolean isFreeze = jsonObject.getBoolean(FREEZE_KEY);
				getLogger().info(MODULE, isFreeze?" Freezing  the Widget":" Unfreezing  the Widget");
				 if( isFreeze ) {
					 webSocketListener.freezeWidget(widgetRequest.getSocketID(), widgetJSON.getHeader().getId());
				 } else {
					 webSocketListener.unFreezeWidget(widgetRequest.getSocketID(), widgetJSON.getHeader().getId());
				 }
				
			}
					
			
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while process widget request, reason : " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}
	

	/**
	 * register Widget in Widget Listener and WebSocketListener
	 * 
	 * @param socketID a unique socket ID
	 * @param widgetType a Widget Type
	 * @param widgetHandler a Widget Handler Object for register
	 */
	public void register(String socketID, String widgetType, WidgetHandler widgetHandler) {
		 WidgetListener widgetListener = widgetListenerMap.get(widgetType);
		 if(widgetListener != null) {
			 widgetListener.register(socketID, widgetHandler);
			 webSocketListener.register(socketID, widgetHandler);
		 } else {
			 getLogger().error(MODULE, "No Widget Listerner found for Widget Type : " + widgetType);
		 }
		 
	}

	
	/**
	 * deRegister all Widgets of socket
	 *  
	 * @param socketID a unique socket ID
	 */
	public void deRegister(String socketID) {
		if( getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "De Register Widget Listerner for ID : " + socketID);
		}
		webSocketListener.deRegister(socketID);
		for(WidgetListener widgetListener : widgetListenerMap.values()) {
			widgetListener.deRegister(socketID);
		}
	}
	
	/**
	 * deRegister perticular Widget of socket
	 *  
	 * @param socketID a unique socket ID
	 */
	public void deRegisterWidget(String socketID,String widgetId) {
		if( getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "De Register Widget Listerner for ID : " + socketID);
		}
		webSocketListener.deRegisterWidget(socketID, widgetId);
		for(WidgetListener widgetListener : widgetListenerMap.values()) {
			if(widgetListener != null) {
				widgetListener.deRegisterWidget(socketID, widgetId);
			}
		}
	}
	
	/**
 	 * clean up resources and stop listener threads of service 
     * state in memory.
     */
	public void destroy() {
		widgetSchedular.stop();
		socketSchedular.stop();
	}
	
	private ILogger getLogger() {
		return LogManager.getLogger();
	}
	
	public static void main(String[] args) {
		String jsonStr = "{ \"name\" : \"true\"}";
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		System.out.println(jsonObject.getBoolean("name"));
	}
	
}