package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.util.voltdb.VoltDBClient;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.netvertex.cli.db.VoltDBScanDetailProvider;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(JUnitParamsRunner.class)
public class VoltDBScanDetailProviderTest {

    private static final String HELP = "-help";
    private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";
    private static final String SCAN = "-scan";
    private VoltDBScanDetailProvider provider;
    @Mock private VoltDBClientManager voltDBClientManager;
    @Mock
    VoltDBClient voltDBClient;

    private static final String DATASOURCENAME1 = "DummyDatasource1";
    private static final String DATASOURCENAME2 = "DummyDatasource2";

    DBDataSource dummyDataSource1 = new DBDataSourceImpl(DATASOURCENAME1);
    DBDataSource dummyDataSource2 = new DBDataSourceImpl(DATASOURCENAME2);

    private HashMap<String, DBDataSource> dataSourceMap;


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        DummyNetvertexServerContextImpl context = DummyNetvertexServerContextImpl.spy();
        when(context.getVoltDBClientManager()).thenReturn(voltDBClientManager);
        ArrayList<String> dbDataSources = new ArrayList<>();
        dbDataSources.add(dummyDataSource1.getDataSourceName());
        dbDataSources.add(dummyDataSource2.getDataSourceName());
        when(voltDBClientManager.getDataSources()).thenReturn(dbDataSources);
        when(voltDBClientManager.isExist(DATASOURCENAME1)).thenReturn(true);
        when(voltDBClientManager.getVoltDBClient(DATASOURCENAME1)).thenReturn(voltDBClient);
        when(voltDBClientManager.getVoltDBClient(DATASOURCENAME2)).thenReturn(voltDBClient);

        dataSourceMap = new HashMap<>();
        dataSourceMap.put(dummyDataSource1.getDataSourceName(), dummyDataSource1);
        dataSourceMap.put(dummyDataSource2.getDataSourceName(), dummyDataSource2);

        provider = spy(new DummyVoltDBScanDetailProvider(context, dataSourceMap));
    }

    @Test
    public void NoDatasourceFoundWhenInvalidDatasourceNameIsPassed(){

        assertEquals(NO_SUCH_DB_DATASOURCE_FOUND, provider.execute(new String[]{"jhsffka"}));
    }

    @Test
    public void scanDataSourceSuccessfully() {

        String actualMessage = provider.execute(new String[]{DATASOURCENAME1, DATASOURCENAME2});
        assertEquals(actualMessage, "SCANNED SUCCESSFULLY \nCurrent Satus: DEAD");

    }

    public Object[][] listOfParams(){
        return new Object[][]{
                { new DetailProvider[] {} ,
                        "'-scan':{'-help':{}, 'DummyDatasource1':{} , 'DummyDatasource2':{} }"

                }

        };

    }

    @Test
    @junitparams.Parameters(method = "listOfParams")
    public void testHotKeyHelp(DetailProvider[] detailProviders , String expectedOutput ) throws RegistrationFailedException {

        for(DetailProvider detailProvider : detailProviders){
            provider.registerDetailProvider(detailProvider);
        }
        assertEquals(expectedOutput, provider.getHotkeyHelp());
    }

    @Test
    @junitparams.Parameters({HELP,"?","-Help","-HElp","-heLp" , "-helP", "-HElp", "-heLP", "-hELp", "-HelP", "-hElP", "-HeLp"})
    public void executeForHelpMsgWithDetailProviders(String args) throws RegistrationFailedException{
        provider.execute(new String[]{args});
        verify(provider, atLeastOnce()).getHelpMsg();
    }

    private class DummyVoltDBScanDetailProvider extends VoltDBScanDetailProvider{

        private Map<String, DBDataSource> dataSourceMap;
        public DummyVoltDBScanDetailProvider(NetVertexServerContext serverContext, Map<String, DBDataSource> dataSourceMap) {
            super(serverContext);
            this.dataSourceMap = dataSourceMap;
        }

        @Override
        protected Map<String, DBDataSource> getDBDatasourceMap() {
            return dataSourceMap;
        }

    }

    private class DBDataSourceImpl implements DBDataSource{

        private String name;

        public DBDataSourceImpl(String name) {
            this.name = name;
        }

        @Override
        public int getStatusCheckDuration() {
            return 0;
        }

        @Override
        public String getDatasourceID() {
            return null;
        }

        @Override
        public String getConnectionURL() {
            return "jdbc:voltdb:127.0.0.1:7777";
        }

        @Override
        public String getUsername() {
            return null;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getPlainTextPassword() {
            return null;
        }

        @Override
        public String getDataSourceName() {
            return name;
        }

        @Override
        public int getMinimumPoolSize() {
            return 0;
        }

        @Override
        public int getMaximumPoolSize() {
            return 0;
        }

        @Override
        public int getNetworkReadTimeout() {
            return 0;
        }
    }
}
