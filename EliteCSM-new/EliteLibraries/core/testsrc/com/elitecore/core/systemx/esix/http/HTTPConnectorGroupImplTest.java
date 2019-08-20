package com.elitecore.core.systemx.esix.http;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;

public class HTTPConnectorGroupImplTest {
    private HttpClient poolableHttpClient = new HTTPClientFactory().getPoolableHttpClient(100,
            3000,3000);

    private Scanner scanner;

    @Before
    public void setup(){
        poolableHttpClient = Mockito.spy(poolableHttpClient);
        scanner = new HTTPScanner();
        scanner = Mockito.spy(scanner);
    }

    @Test
    public void submitOnGroupCallConnectorSubmitMethodWhenConnectorIsAlive() throws InitializationFailedException,IOException, CommunicationException {
        Mockito.doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, new FakeTaskScheduler(),scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.POST);
        connector = Mockito.spy(connector);
        connector.init();
        Mockito.doReturn(null).when(connector).submit(remoteMethod);

        HTTPConnectorGroupImpl group = new HTTPConnectorGroupImpl();
        group.addCommunicator(connector);
        group.submit(remoteMethod);
        Mockito.verify(connector,Mockito.times(1)).submit(remoteMethod);
    }

    @Test (expected = CommunicationException.class)
    public void throwCommunicationExceptionOnSubmitWhenGroupIsEmpty() throws InitializationFailedException,IOException, CommunicationException {
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        HTTPConnectorGroupImpl group = new HTTPConnectorGroupImpl();
        group.submit(remoteMethod);
    }

    @Test (expected = CommunicationException.class)
    public void throwCommminicatiExceptionOnSubmitWhenAllConnectorsAreDead() throws InitializationFailedException,IOException, CommunicationException {
            HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, new FakeTaskScheduler(),new HTTPScanner(), InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        HTTPConnectorGroupImpl group = new HTTPConnectorGroupImpl();
        group.addCommunicator(connector);
        connector.markDead();
        group.submit(remoteMethod); // Here connector will be marked dead
    }
}
