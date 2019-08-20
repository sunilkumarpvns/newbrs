package com.elitecore.netvertex.service.pcrf;

import com.elitecore.corenetvertex.service.notification.NotificationEvent;


public interface NotificationQueue {
	
	public void add(NotificationEvent event);

}
