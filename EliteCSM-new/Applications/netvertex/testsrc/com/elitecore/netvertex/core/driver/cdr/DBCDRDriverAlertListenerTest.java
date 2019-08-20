package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.commons.alert.Events;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.Alerts;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DBCDRDriverAlertListenerTest {

    private DummyNetvertexServerContextImpl serverContext = DummyNetvertexServerContextImpl.spy();
    private DBCDRDriverAlertListener alertListener = new DBCDRDriverAlertListener(serverContext);

    @Test
    public void testGenerateAlertNotGenerateAlertOnDbConnectionNotAvailableEvent() {

        String message = "DB no connection available";
        Events event = Events.DB_CONNECTION_NOT_AVAILABLE;
        alertListener.generateAlert(event, message);

        verify(serverContext, times(1)).generateSystemAlert(anyString(), any(Alerts.class), anyString(), anyString());
    }

    @Test
    public void testGenerateAlertGenerateAlertOnEventOtherThanDbConnectionNotAvailable() {

        String message = "DB no connection available";

        Arrays.stream(Events.values()).filter(events -> events != Events.DB_CONNECTION_NOT_AVAILABLE).forEach(event -> {
            alertListener.generateAlert(event, message);

            verify(serverContext, times(0)).generateSystemAlert(anyString(), any(Alerts.class), anyString(), anyString());
        });

    }
}