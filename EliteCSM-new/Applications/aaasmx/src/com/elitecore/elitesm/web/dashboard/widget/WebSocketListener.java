package com.elitecore.elitesm.web.dashboard.widget;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

/**
 * A <strong>WebSocketListener</strong> send Widget Data to all register <code>WidgetHandler</code>.
 *
 * WebSocket Listener listen per configured <code>interval</code>    
 * 
 * @see WidgetHandler
 * @see IntervalBasedTask
 *
 */
public class WebSocketListener extends BaseIntervalBasedTask {
	
	private static final String MODULE = "WebSocketListener";
	private Map<String, Set<WidgetHandler>> widgetMap;
	private int interval;
	
	public WebSocketListener(int interval) {
		this.interval = interval;
		this.widgetMap = new ConcurrentHashMap<String, Set<WidgetHandler>>();
	}
	
	
	/**
	 * register Widget Handler in Web Socket Listener 
	 * 
	 * @param widgetSocketID a unique Web Socket ID 
	 * @param widgetHandler a WidgetHandler of Widget 
	 */
	public void register(String widgetSocketID, WidgetHandler widgetHandler) {
		Set<WidgetHandler> handlerList = widgetMap.get(widgetSocketID);
		if(handlerList == null ) {
			handlerList = new HashSet<WidgetHandler>();
			widgetMap.put(widgetSocketID, handlerList);
		}
		handlerList.add(widgetHandler);
		
	}
	
	
	
	
	/**
	 * deRegister all WidgetHandler of WebSocket from Listener
	 *  
	 * @param widgetSocketID a unique Web Socket ID 
	 */
	public void deRegister(String widgetSocketID) {
		widgetMap.remove(widgetSocketID);
	}
	
	public void freezeWidget(String widgetSocketID, String widgetID) {
		WidgetHandler widgetHandler = getWidgetHandler(widgetSocketID, widgetID);
		if( widgetHandler != null) {
			widgetHandler.setFreeze(true);
		}
	}
	
	public void unFreezeWidget(String widgetSocketID, String widgetID) {
		WidgetHandler widgetHandler = getWidgetHandler(widgetSocketID, widgetID);
		if( widgetHandler != null) {
			widgetHandler.setFreeze(false);
		}
	}
	
	private WidgetHandler getWidgetHandler(String widgetSocketID, String widgetID) {
		Set<WidgetHandler> handlerList = widgetMap.get(widgetSocketID);
		if(handlerList != null && !handlerList.isEmpty() ) {
			for( WidgetHandler widgetHandler : handlerList) {
				if(widgetID.equals(widgetHandler.getWidgetID()) ) {
					return widgetHandler;
				}
			}
		}
		return null;
	}

	@Override
	public long getInterval() {
		return interval;
	}

	@Override
	public void execute(AsyncTaskContext context) {
		if( getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Execute Listener");
		}
		for(Set<WidgetHandler>  widgetHandlerList : widgetMap.values()) {
			for(WidgetHandler widgetHandler : widgetHandlerList) {
				if(!widgetHandler.isFreeze()) {
					widgetHandler.sendWidgetData();
				} else {
					getLogger().debug(MODULE, "Widget Handler ( id  :  "+ widgetHandler.getWidgetID() +") is Freezed" );
				}
			}
		}
	}
	
	@Override
	public long getInitialDelay() {
		return 3;
	}
	
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}
	
	private ILogger getLogger() {
		return LogManager.getLogger();
	}


	public void deRegisterWidget(String socketID, String widgetId) {
		Set<WidgetHandler> handlerSet = widgetMap.get(socketID);
		if( handlerSet == null || handlerSet.isEmpty()) {
			System.out.println("No handler found");
			return;
		}
		
		WidgetHandler deRegisHandler = null;
		for( WidgetHandler handler : handlerSet) {
			if( handler.getWidgetID().equals(widgetId)) {
				deRegisHandler = handler;
			}
		}
		
		if( deRegisHandler == null ) {
			System.out.println("No handler found");
		}
		
		handlerSet.remove(deRegisHandler);
	}
}
