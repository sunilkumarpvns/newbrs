package com.elitecore.netvertex.core;

import com.elitecore.commons.net.FakeAddressResolver;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.http.HTTPClientFactory;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import org.apache.http.client.HttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class HTTPConnectorFactoryTest {

    private HTTPConnectorFactory httpConnectionFactory;
    private FakeAddressResolver addressResolver;
    private DummyNetvertexServerContextImpl context;
    private HTTPClientFactory httpClientFactory;
    private String name = "test";
    private int port = 0;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void create() {
        httpClientFactory = mock(HTTPClientFactory.class);
        addressResolver = new FakeAddressResolver();
        context = new DummyNetvertexServerContextImpl();

        httpConnectionFactory = new HTTPConnectorFactory(context.getTaskScheduler(), addressResolver, httpClientFactory,"netvertex");
    }

    @Test
    public void throwInitializationFailExceptionWhenRestAddressNotResolvedInPrimaryServer() throws InitializationFailedException {

        String ipAddress = UUID.randomUUID().toString();

        exception.expect(InitializationFailedException.class);

        exception.expectMessage("Unable to resolve rest address: " + ipAddress + " of " + name + " server");

        httpConnectionFactory.create(name, ipAddress, port);

    }


    @Test
    public void createHttpConnectorOnIfRestIPisReachable() throws InitializationFailedException {

        String ipAddress = "127.0.0.1";

        when(httpClientFactory.getPoolableHttpClient(anyInt(), anyInt(), anyInt())).thenReturn(mock(HttpClient.class));

        HTTPConnector httpConnector = httpConnectionFactory.create(name, ipAddress, port);
        Assert.assertNotNull(httpConnector);

        verify(httpClientFactory, timeout(1)).getPoolableHttpClient(anyInt(), anyInt(), anyInt());
    }

}