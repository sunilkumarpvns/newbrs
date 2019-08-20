package com.elitecore.netvertex.service.notification;

import com.elitecore.netvertex.service.notification.data.NotificationEntity;

public interface EmailSender {
    boolean sendEmail(NotificationEntity notificationEntity);

    boolean stop();
}
