package com.elitecore.core.systemx.esix.http;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.core.systemx.esix.TaskScheduler;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.mockito.Mockito.*;
public class HTTPConnectorTest {

    private InetAddress inetAddress;
    private HttpClient poolableHttpClient;
    private ProtocolVersion protocolVersion= new ProtocolVersion( "http", 1, 1);
    private TaskScheduler scheduler = new FakeTaskScheduler();
    private Scanner scanner;

    @Before
    public void setUp() {
        inetAddress = mock(InetAddress.class);
        poolableHttpClient= mock(HttpClient.class);
        scanner = new HTTPScanner();
        scanner = Mockito.spy(scanner);
    }

    @Test
    public void create_connector_with_single_ip_and_call_init_successfully() throws UnknownHostException, InitializationFailedException{
        HTTPConnector connector = new HTTPConnector("",InetAddress.getByName("0.0.0.0"), 80,"", poolableHttpClient,scheduler,5,scanner);
        connector.init();
    }

    @Test
    public void create_connector_with_single_ip_and_add_end_point_detail_successfully() throws UnknownHostException, InitializationFailedException{
        HTTPConnector connector = new HTTPConnector("",InetAddress.getByName("0.0.0.0"), 80,"", poolableHttpClient,scheduler,5,scanner);
        connector.addEndPointDetail("name","NetVertex");
        connector.init();
    }

    @Test
    public void call_isAlive_and_check_true_success() throws UnknownHostException, InitializationFailedException{
        HTTPConnector connector = new HTTPConnector("",InetAddress.getByName("0.0.0.0"), 80,"", poolableHttpClient,scheduler,5,scanner);
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        connector.init();
        Assert.assertTrue(connector.isAlive());
    }

    @Test
    public void create_connector_with_multiple_ip_and_call_init_successfully() throws UnknownHostException, InitializationFailedException{

        InetAddress addresses[] = {InetAddress.getByName("0.0.0.0"), InetAddress.getByName("1.2.3.4")};

        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, addresses);
        connector.init();
    }

    @Test
    public void multiple_ip_aliveness_check_status_true_successfully() throws UnknownHostException, InitializationFailedException{
        InetAddress addresses[] = {InetAddress.getByName("0.0.0.0"), InetAddress.getByName("1.2.3.4")};
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, addresses);
        connector.init();

        //Because 0.0.0.0 is accessible
        Assert.assertTrue(connector.isAlive());
    }

    @Test
    public void any_alpha_numeric_host_name_must_work_as_an_inetaddress() throws UnknownHostException, InitializationFailedException{
        InetAddress addresses[] = {InetAddress.getByName("localhost")};
        doReturn(true).when(scanner).isAccessible("127.0.0.1",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, addresses);
        connector.init();

        //Because 0.0.0.0 is accessible
        Assert.assertTrue(connector.isAlive());
    }

    @Test
    public void isAlive_check_for_fail_condition_as_non_existing_ip_given() throws UnknownHostException, InitializationFailedException {
        InetAddress addresses[] = {InetAddress.getByName("1.2.3.4")};

        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, addresses);
        connector.init();

        Assert.assertFalse(connector.isAlive());
    }

    @Test
    public void mark_the_connection_dead_and_re_invoke_scan_method_and_it_must_return_alive() throws UnknownHostException, InitializationFailedException {
        InetAddress addresses[] = {InetAddress.getByName("0.0.0.0")};
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, addresses);
        connector.init();
        connector.markDead();
        connector.scan();
        Assert.assertTrue(connector.isAlive());
    }

    @Test
    public void throw_communication_excption_when_network_error_occurres_and_isAlive_must_return_false() throws InitializationFailedException,IOException {
        when(inetAddress.isReachable(3000)).thenThrow(new IOException());
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, inetAddress);
        connector.scan();

        Assert.assertFalse(connector.isAlive());
    }

    @Test
    public void type_name_should_return_http_for_getTypeName_method() throws InitializationFailedException,IOException {
        when(inetAddress.isReachable(3000)).thenThrow(new IOException());
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, inetAddress);

        Assert.assertTrue("HTTP".equalsIgnoreCase(connector.getTypeName()));
    }

    @Test
    public void get_request_with_valid_parameters_it_should_execute_successfully() throws InitializationFailedException,IOException, CommunicationException {
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        BasicHttpResponse responseStubbed = new BasicHttpResponse(protocolVersion,200,"Success");
        when(poolableHttpClient.execute(any(HttpGet.class))).thenReturn(responseStubbed);
        connector.init();
        HttpResponse response = (HttpResponse) connector.submit(remoteMethod);
        Assert.assertEquals(responseStubbed.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode());
    }

    @Test
    public void get_request_with_valid_parameters_and_argument_it_should_execute_successfully() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        remoteMethod.addArgument("foo","bar");
        BasicHttpResponse responseStubbed = new BasicHttpResponse(protocolVersion,200,"Success");
        doReturn(responseStubbed).when(poolableHttpClient).execute(any(HttpGet.class));
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        connector.init();
        HttpResponse response = (HttpResponse) connector.submit(remoteMethod);
        Assert.assertEquals(responseStubbed.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode());
    }

    @Test
    public void get_request_with_valid_parameters_and_basic_authentication_it_should_execute_successfully() throws InitializationFailedException,IOException, CommunicationException {
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        BasicHttpResponse responseStubbed = new BasicHttpResponse(protocolVersion,200,"Success");
        when(poolableHttpClient.execute(any(HttpGet.class))).thenReturn(responseStubbed);
        connector.withBasicAuthentication("username","password");
        connector.init();
        HttpResponse response = (HttpResponse) connector.submit(remoteMethod);
        Assert.assertEquals(responseStubbed.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode());
    }

    @Test(expected = CommunicationException.class)
    public void when_ioexception_occurs_in_get_then_it_must_be_handled_and_communication_exception_should_be_thrown() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        HttpClient httpClient = mock(HttpClient.class);
        when(poolableHttpClient.execute(any(HttpGet.class))).thenThrow(new IOException());
        connector.init();
        connector.submit(remoteMethod);
    }

    @Test
    public void post_request_with_valid_parameters_and_argument_and_basic_authentication_it_should_execute_successfully() throws InitializationFailedException,IOException, CommunicationException {
        doReturn(true).when(scanner).isAccessible("0.0.0.0",80);
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.POST);
        remoteMethod.addArgument("foo","bar");
        BasicHttpResponse responseStubbed = new BasicHttpResponse(protocolVersion,200,"Success");
        when(poolableHttpClient.execute(any(HttpPost.class))).thenReturn(responseStubbed);
        connector.withBasicAuthentication("username","password");
        connector.init();
        HttpResponse response = (HttpResponse) connector.submit(remoteMethod);
        Assert.assertEquals(responseStubbed.getStatusLine().getStatusCode(), response.getStatusLine().getStatusCode());
    }

    @Test(expected = CommunicationException.class)
    public void when_any_unsupportedencodingexception_occurs_in_post_then_it_must_be_handled_and_communication_exception_should_be_thrown() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.POST);
        when(poolableHttpClient.execute(any(HttpGet.class))).thenThrow(new UnsupportedEncodingException());
        connector.init();
        connector.submit(remoteMethod);
    }

    @Test(expected = CommunicationException.class)
    public void when_any_ioexception_occurs_in_post_then_it_must_be_handled_and_communication_exception_should_be_thrown() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.POST);
        when(poolableHttpClient.execute(any(HttpGet.class))).thenThrow(new IOException());
        connector.init();
        connector.submit(remoteMethod);
    }

    @Test(expected = CommunicationException.class)
    public void try_to_execute_get_method_without_initializing_an_it_must_fail_with_communicationexception() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        connector.submit(remoteMethod);
    }

    @Test(expected = CommunicationException.class)
    public void invoke_submit_on_dead_connection_and_it_must_fail_with_communicationexception() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        RemoteMethod remoteMethod = new RemoteMethod("/service", "/method", HTTPMethodType.GET);
        connector.init();
        connector.markDead();
        connector.submit(remoteMethod);
    }

    @Test(expected = CommunicationException.class)
    public void pass_null_remoteMethod_to_submit_and_it_must_fail_with_communicationexception() throws InitializationFailedException,IOException, CommunicationException {
        HTTPConnector connector = new HTTPConnector("",80,"", 5, poolableHttpClient, scheduler,scanner, InetAddress.getByName("0.0.0.0"));
        connector.init();
        connector.submit(null);
    }
}
