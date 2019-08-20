package com.elitecore.netvertex.service.notification;

import com.elitecore.netvertex.service.notification.data.NotificationEntity;

public interface SMSSender {
    boolean sendSMS(NotificationEntity notificationEntity);

    boolean stop();
}
