package com.elitecore.netvertex.service.notification;

import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;

public interface NotificationServiceContext extends ServiceContext{
	
		NotificationServiceConfigurationImpl getNotificationServiceConfiguration();

	@Override
	NetVertexServerContext getServerContext();
}
