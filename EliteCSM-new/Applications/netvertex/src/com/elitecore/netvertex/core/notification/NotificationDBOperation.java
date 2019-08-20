package com.elitecore.netvertex.core.notification;

import com.elitecore.netvertex.core.Notification;

public interface NotificationDBOperation {
    void insertIntoNotificationQueue(Notification notification);

    void insertIntoNotificationHistory(Notification notification);
}
