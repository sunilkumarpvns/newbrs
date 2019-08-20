package com.elitecore.netvertex.core;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.constants.TimeBasedRollingUnit;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupInstanceConfiguration;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceConfigurationImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class ServerStatusScannerTest {

    private DummyNetvertexServerContextImpl dummyNetvertexServerContext;

    @Before
    public void setUp() {
        dummyNetvertexServerContext = DummyNetvertexServerContextImpl.spy();
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();


    public class ServerStatusScannerCreate {

        private NetvertexServerInstanceConfigurationImpl primaryInstance;
        private NetvertexServerGroupInstanceConfiguration primaryGroupInstanceConf;

        @Before
        public void setUp() {


            primaryInstance = new NetvertexServerInstanceConfigurationImpl(
                    UUID.randomUUID().toString()
                    , LogLevel.DEBUG.name()
                    , RollingType.TIME_BASED
                    , TimeBasedRollingUnit.DAILY.name()
                    , 1
                    , false
                    , null
                    , 0
                    , 0
                    , null
                    , 0
                    , null,null);
            primaryGroupInstanceConf = new NetvertexServerGroupInstanceConfiguration(UUID.randomUUID().toString()
                    , primaryInstance
                    , null
                    , null
                    , null, null, null,null,false);


            NetvertexServerGroupConfiguration netvertexServerGroupConfiguration = dummyNetvertexServerContext.getServerConfiguration().spyGroupConfiguration();


            when(netvertexServerGroupConfiguration.getPrimaryInstanceConfiguration()).thenReturn(primaryGroupInstanceConf);


        }


        @Test
        public void throwInitializationFailExceptionWhenRestAddressNotConfiguredInPrimaryServer() throws InitializationFailedException {
            primaryInstance.setRestIpAddress(null);

            exception.expect(InitializationFailedException.class);
            exception.expectMessage("Rest address not provided of " + primaryInstance.getName() + " server");
            ServerStatusScanner.create(dummyNetvertexServerContext,  null, null, null);
        }


        @Test
        public void createdHTTPConnectorRegisterShutdownHook() throws InitializationFailedException {

            primaryInstance.setRestIpAddress("127.0.0.1");

            HTTPConnectorFactory clientFactory = mock(HTTPConnectorFactory.class);

            when(clientFactory.create(primaryInstance.getName(), primaryInstance.getRestIpAddress(), primaryInstance.getRestPort())).thenReturn(mock(HTTPConnector.class));

            Runtime runtime = mock(Runtime.class);

            ServerStatusScanner.create(dummyNetvertexServerContext, null, clientFactory, runtime);

            verify(runtime, times(1)).addShutdownHook(any(Thread.class));

        }
    }

    public class ServerStatusScannerExecute {

        private HTTPConnector httpConnector;
        private ServerStatusScanner serverStatusScanner;
        private Consumer<Boolean> booleanConsumer;
        private boolean isPrimary = false;

        @Before
        public void setUp() throws InitializationFailedException {

            NetvertexServerInstanceConfigurationImpl primaryInstance = new NetvertexServerInstanceConfigurationImpl(
                    UUID.randomUUID().toString()
                    , LogLevel.DEBUG.name()
                    , RollingType.TIME_BASED
                    , TimeBasedRollingUnit.DAILY.name()
                    , 1
                    , false
                    , null
                    , 0
                    , 0
                    , null
                    , 0
                    , null,null);
            NetvertexServerGroupInstanceConfiguration primaryGroupInstanceConf = new NetvertexServerGroupInstanceConfiguration(UUID.randomUUID().toString()
                    , primaryInstance
                    , null
                    , null
                    , null, null, null,null,false);


            NetvertexServerGroupConfiguration netvertexServerGroupConfiguration = dummyNetvertexServerContext.getServerConfiguration().spyGroupConfiguration();


            when(netvertexServerGroupConfiguration.getPrimaryInstanceConfiguration()).thenReturn(primaryGroupInstanceConf);

            primaryInstance.setRestIpAddress("127.0.0.1");

            HTTPConnectorFactory clientFactory = mock(HTTPConnectorFactory.class);

            httpConnector = mock(HTTPConnector.class);

            when(clientFactory.create(anyString(), anyString(), anyInt())).thenReturn(httpConnector);

            Runtime runtime = mock(Runtime.class);

            /// do not convert into lamda
            booleanConsumer = spy(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                    isPrimary = aBoolean;
                }
            });
            serverStatusScanner = ServerStatusScanner.create(dummyNetvertexServerContext, booleanConsumer, clientFactory, runtime);

        }

        @Test
        public void updateStatusToPrimaryWhenHttpConnectorIsNotLive() throws IOException {
            markingHttpConnectorDown();
            serverStatusScanner.execute(mock(AsyncTaskContext.class));

            assertTrue(isPrimary);

        }

        @Test
        public void updateStatusToPrimaryWhenHttpConnectorCommunicationFail() throws CommunicationException, IOException {

            httpWillThrowCommunicationException();
            serverStatusScanner.execute(mock(AsyncTaskContext.class));
            verify(httpConnector, times(1)).submit(any(RemoteMethod.class));
            assertTrue(isPrimary);

        }

        @Test
        public void updateStatusToPrimaryWhenResponseFromPrimaryThrowsIOException() throws CommunicationException, IOException {
            StringEntity entity = spy(new StringEntity(LifeCycleState.RUNNING_WITH_LAST_CONF.message));
            httpWillReturnSuccessResult(entity);
            when(entity.getContent()).thenThrow(IOException.class);
            serverStatusScanner.execute(mock(AsyncTaskContext.class));
            verify(httpConnector, times(1)).submit(any(RemoteMethod.class));
            assertTrue(isPrimary);

        }

        @Test
        public void updateStatusToPrimaryWhenPrimaryServerIsNotRunning() throws CommunicationException, IOException {
            httpWillReturnSuccessResult(LifeCycleState.STARTUP_IN_PROGRESS);
            serverStatusScanner.execute(mock(AsyncTaskContext.class));
            verify(httpConnector, times(1)).submit(any(RemoteMethod.class));
            assertTrue(isPrimary);

        }

        @Test
        public void updateStatusToPrimaryWhenResponseStatusIsNot200() throws CommunicationException, IOException {
            httpWillReturnFailResult();
            serverStatusScanner.execute(mock(AsyncTaskContext.class));
            verify(httpConnector, times(1)).submit(any(RemoteMethod.class));
            assertTrue(isPrimary);

        }

        @Test
        public void updateStatusToSecondaryWhenPrimaryServerIsRunning() throws CommunicationException, IOException {
            httpWillReturnSuccessResult(LifeCycleState.RUNNING);
            serverStatusScanner.execute(mock(AsyncTaskContext.class));
            verify(httpConnector, times(1)).submit(any(RemoteMethod.class));
            assertFalse(isPrimary);

        }

        @Test
        public void updateStatusToSecondaryWhenPrimaryServerIsRunningWithLastConf() throws CommunicationException, IOException {
            httpWillReturnSuccessResult(LifeCycleState.RUNNING_WITH_LAST_CONF);
            serverStatusScanner.execute(mock(AsyncTaskContext.class));
            verify(httpConnector, times(1)).submit(any(RemoteMethod.class));
            assertFalse(isPrimary);

        }


        private void httpWillReturnFailResult() throws IOException, CommunicationException {
            markingHttpConnectorUp();

            final BasicHttpResponse basicHttpResponse = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion( "http", 1, 1), 201, "Fail"));

            StringEntity fakeContent = new StringEntity("From Test");
            basicHttpResponse.setEntity(fakeContent);

            doReturn(basicHttpResponse) .when(httpConnector).submit(any(RemoteMethod.class));
        }

        private void httpWillReturnSuccessResult(LifeCycleState lifeCycleState) throws IOException, CommunicationException {
            httpWillReturnSuccessResult(new StringEntity(lifeCycleState.message));
        }

        private void httpWillReturnSuccessResult(StringEntity entity) throws IOException, CommunicationException {
            markingHttpConnectorUp();

            final BasicHttpResponse basicHttpResponse = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion( "http", 1, 1), 200, "SUCCESS"));

            basicHttpResponse.setEntity(entity);

            doReturn(basicHttpResponse) .when(httpConnector).submit(any(RemoteMethod.class));
        }

        private void httpWillThrowCommunicationException() throws IOException, CommunicationException {
            markingHttpConnectorUp();
            doThrow(CommunicationException.class).when(httpConnector).submit(any(RemoteMethod.class));
        }

        private void markingHttpConnectorDown() {
            when(httpConnector.isAlive()).thenReturn(false);
        }
        private void markingHttpConnectorUp() {

            when(httpConnector.isAlive()).thenReturn(true);
        }


    }

}