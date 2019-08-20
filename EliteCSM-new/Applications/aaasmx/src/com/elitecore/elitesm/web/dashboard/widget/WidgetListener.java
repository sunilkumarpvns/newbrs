package com.elitecore.elitesm.web.dashboard.widget;

import java.util.HashSet;
import java.util.List;
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
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

/**
 * A <strong> Widget Listener </strong> push Widget Data to all register <code>WidgetHandler</code>.
 *
 * Widget Listener listen per configured <code>interval</code>    
 * 
 * @see WidgetHandler
 * @see IntervalBasedTask
 * @author punit.j.patel
 *
 */
public class WidgetListener extends BaseIntervalBasedTask{
	
	private static final String MODULE = "WidgetListener";
	private String name;
	private int interval;
	private WidgetDataProvider widgetDataProvider;
	private Map<String, Set<WidgetHandler>> widgetMap;
	
	public WidgetListener(String name, int interval, WidgetDataProvider widgetDataProvider) {
		this.name = name;
		this.interval = interval;
		this.widgetDataProvider = widgetDataProvider;
		this.widgetMap = new ConcurrentHashMap<String, Set<WidgetHandler>>();
	}

	public void register(String widgetSocketID, WidgetHandler widgetHandler) {
		Set<WidgetHandler> handlerSet = widgetMap.get(widgetSocketID);
		if(handlerSet == null ) {
			handlerSet = new HashSet<WidgetHandler>();
			widgetMap.put(widgetSocketID, handlerSet);
		}
		handlerSet.add(widgetHandler);
	}
	
	public void deRegister(String widgetSocketID) {
		widgetMap.remove(widgetSocketID);
	}
	
	public void deRegisterWidget(String webSocketId,String widgetId){
		Set<WidgetHandler> handlerSet = widgetMap.get(webSocketId);
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
			System.out.println("No handler Found");
		}
		
		handlerSet.remove(deRegisHandler);
	}
	
	public String getName() {
		return name;
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
		
		if(!widgetMap.isEmpty()) {
			List<WidgetJSON> widgetDataList = widgetDataProvider.provideData();
			if( getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "Receive Widget Data : " + widgetDataList );
			}
			
			if(widgetDataList != null) {
				for(Set<WidgetHandler>  widgetHandlerList : widgetMap.values()) {
					for(WidgetHandler widgetHandler : widgetHandlerList) {
						widgetHandler.pushWidgetData(widgetDataList);
					}
				}
			}
		} else {
			if( getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "No Handler Register");
			}
		}
	}
	
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}
	
	private ILogger getLogger() {
		return LogManager.getLogger();
	}
}
