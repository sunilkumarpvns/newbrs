package com.elitecore.netvertex.core.session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.voltdb.VoltDBClient;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import com.elitecore.netvertex.core.session.voltdb.VoltDBSessionDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SessionDaoFactoryTest {
    private DummyNetvertexServerContextImpl netVertexServerContext;
    private NetvertexServerGroupConfiguration netvertexServerGroupConfiguration;
    @Mock private DummyNetvertexServerConfiguration serverConfiguration;
    @Mock private VoltDBClientManager voltDBClientManager;
    @Mock private VoltDBClient voltDBClient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        netVertexServerContext = new DummyNetvertexServerContextImpl();
        netVertexServerContext.setServerInstanceId("101");

        serverConfiguration = netVertexServerContext.getServerConfiguration();
        netvertexServerGroupConfiguration = serverConfiguration.spyGroupConfiguration();
    }

    @Test
    public void returnVoltDBSessionDaoWhenDSContainsVolt() throws InitializationFailedException {
        DBDataSourceImpl voltDS = new DBDataSourceImpl("1", "VoltDS",
                "jdbc:voltdb:127.0.0.1:7777", "", "", 1,
                5, 0, 10, 100);

        when(serverConfiguration.getSessionManagerConfiguration()).thenReturn(new DummySessionManagerConfiguration());
        when(netvertexServerGroupConfiguration.getSessionDS()).thenReturn(voltDS);
        netVertexServerContext.setVoltDBClientManager(voltDBClientManager);
        when(voltDBClientManager.getOrCreateClient(any(), any())).thenReturn(voltDBClient);
        assertTrue(new SessionDaoFactory().create(netVertexServerContext) instanceof VoltDBSessionDao);
    }

    @Test
    public void returnRelationalDBSessionDaoWhenDSnotContainsVolt() throws InitializationFailedException {
        DBDataSourceImpl relationalDS = new DBDataSourceImpl("1", "VoltDS",
                "dbc:oracle:thin:@127.0.0.1:1521/csmdb", "", "", 1,
                5, 0, 10, 100);

        when(serverConfiguration.getSessionManagerConfiguration()).thenReturn(new DummySessionManagerConfiguration());
        when(netvertexServerGroupConfiguration.getSessionDS()).thenReturn(relationalDS);
        assertTrue(new SessionDaoFactory().create(netVertexServerContext) instanceof RelationalDBSessionDao);
    }


    private class DummySessionManagerConfiguration implements SessionManagerConfiguration {

        @Override
        public List<FieldMapping> getCoreSessionFieldMappings() {
            return Collectionz.newArrayList();
        }

        @Override
        public List<FieldMapping> getSessionRuleFieldMappings() {
            return Collectionz.newArrayList();
        }

        @Override
        public boolean isBatchUpdateEnable() {
            return false;
        }

        @Override
        public long getBatchSize() {
            return 0;
        }

        @Override
        public long getBatchUpdateIntervalInSec() {
            return 0;
        }

        @Override
        public int getBatchQueryTimeout() {
            return 0;
        }

        @Override
        public boolean isSaveInBatch() {
            return false;
        }

        @Override
        public boolean isUpdateInBatch() {
            return false;
        }

        @Override
        public boolean isDeleteInBatch() {
            return false;
        }

        @Override
        public void toString(IndentingToStringBuilder builder) {}
    }
}

