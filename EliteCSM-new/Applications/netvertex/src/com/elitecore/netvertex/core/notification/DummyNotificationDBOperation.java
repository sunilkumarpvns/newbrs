package com.elitecore.netvertex.core.notification;

import com.elitecore.netvertex.core.Notification;

public class DummyNotificationDBOperation  implements NotificationDBOperation {
    @Override
    public void insertIntoNotificationQueue(Notification notification) {
        //just dummy implementation to be used in notification when initialization fail
    }

    @Override
    public void insertIntoNotificationHistory(Notification notification) {
        //just dummy implementation to be used in notification when initialization fail
    }
}
