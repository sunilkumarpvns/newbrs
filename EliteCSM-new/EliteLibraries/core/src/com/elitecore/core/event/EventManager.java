package com.elitecore.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventManager {
	
	private static EventManager instance;
	private List<IServiceEventListener> serviceEventListenerList;
	private List<IServerEventListener> serverEventListenerList;
	
	private EventManager(){
		serviceEventListenerList = new ArrayList <IServiceEventListener>();
		serverEventListenerList = new ArrayList <IServerEventListener>();
	}
	
	static {
		instance = new EventManager();
	}
	public static EventManager getInstance() {
		return instance;
	}
	
	public void addServerEventListener (IServerEventListener serverEventLister) {
		serverEventListenerList.add(serverEventLister);
	}
	
	public void addServiceEventListener (IServiceEventListener serviceEventListener) {
		serviceEventListenerList.add(serviceEventListener);
	}
	
	public void generateServerEvent(ServerEvent serverEvent) {
		if(!serverEventListenerList.isEmpty()) {
			Iterator<IServerEventListener> itr = serverEventListenerList.iterator();
			while(itr.hasNext()) {
				itr.next().serverEvent(serverEvent);
			}
		}
	}
	
	public void generateServiceEvent(ServiceEvent serviceEvent) {
		if(!serviceEventListenerList.isEmpty()) {
			Iterator<IServiceEventListener> itr = serviceEventListenerList.iterator();
			while(itr.hasNext()) {
				itr.next().serviceEvent(serviceEvent);
			}
		}
	}
}
