package com.elitecore.aaaclient.ui.session.radius.request.event;

public class RequestDataUpdateEvent {
	
	private Object source;
	
	public RequestDataUpdateEvent(Object source) {
		this.source = source;
	}
	
	public Object getSoruce(){
		return this.source;
	}
	
}
