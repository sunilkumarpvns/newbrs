package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.netvertex.cli.db.VoltDBDetailProvider;
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
public class VoltDBDetailProviderTest {

    private static final String HELP = "-help";
    private static final String NO_SUCH_DB_DATASOURCE_FOUND = "NO SUCH DB DATASOURCE FOUND";
    private static final String VIEW = "-view";
    private VoltDBDetailProvider provider;
    @Mock private VoltDBClientManager voltDBClientManager;

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

        dataSourceMap = new HashMap<>();
        dataSourceMap.put(dummyDataSource1.getDataSourceName(), dummyDataSource1);
        dataSourceMap.put(dummyDataSource2.getDataSourceName(), dummyDataSource2);

        provider = spy(new DummyVoltDBScanDetailProvider(context, dataSourceMap));
    }

    @Test
    public void test_DataSourceMap_Is_NULL(){

        assertEquals(NO_SUCH_DB_DATASOURCE_FOUND, provider.execute(new String[]{"-view", "jhsffka"}));
    }

    @Test
    public void getVoltDBNamesOfServerConfigurationCalledWhenGatewayKeyPassedWithoutAnyParameters() {

        provider.execute(new String[]{VIEW, DATASOURCENAME1, DATASOURCENAME2});
        verify(provider).dbFormat(dataSourceMap);

    }

    public Object[][] listOfParams(){
        return new Object[][]{
                { new DetailProvider[] {} ,
                        "'statistics':{'-help':{},'-param1':{}}"

                }

        };

    }

    @Test
    @junitparams.Parameters(method = "listOfParams")
    public void test_hotKeyHelp(DetailProvider[] detailProviders , String expectedOutput ) throws RegistrationFailedException {

        for(DetailProvider detailProvider : detailProviders){
            provider.registerDetailProvider(detailProvider);
        }
        assertEquals(expectedOutput, provider.getHotkeyHelp());
    }

    @Test
    @junitparams.Parameters({"",HELP,"?","-Help","-HElp","-heLp" , "-helP", "-HElp", "-heLP", "-hELp", "-HelP", "-hElP", "-HeLp"})
    public void test_execute_ForHelpMsg_with0DetailProviders(String args) throws RegistrationFailedException{
        provider.execute(new String[]{args});
        verify(provider, atLeastOnce()).getHelpMsg();
    }

    private class DummyVoltDBScanDetailProvider extends VoltDBDetailProvider{

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
            return null;
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
