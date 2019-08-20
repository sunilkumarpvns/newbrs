package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.util.voltdb.VoltDBClient;
import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.netvertex.cli.db.VoltDBStatisticsDetailProvider;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(value = junitparams.JUnitParamsRunner.class)
public class VoltDBStatisticsDetailProviderTest {

    private static final String HELP = "-help";
    private static final String STATISTICS = "-statistics";
    private VoltDBStatisticsDetailProvider provider;
    @Mock private VoltDBClientManager voltDBClientManager;
    @Mock private VoltDBClient voltDBClient;

    private static final String DATASOURCENAME1 = "DummyDatasource1";
    private static final String DATASOURCENAME2 = "DummyDatasource2";

    DBDataSource dummyDataSource1 = new DBDataSourceImpl(DATASOURCENAME1);
    DBDataSource dummyDataSource2 = new DBDataSourceImpl(DATASOURCENAME2);

    private HashMap<String, DBDataSource> dataSourceMap;

    @Rule
    public ExpectedException exception = ExpectedException.none();


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

        provider = spy(new DummyVoltDBStatisticsDetailProvider(context, dataSourceMap));

    }

    @Test
    @Parameters({"?","-HELP","-Help","-help","-hELP"})
    public void test_helpMessage_should_call_when_help_parameter_is_passed(String parameters){

        provider.execute(new String[]{parameters});

        verify(provider).getHelpMsg();
    }

    public Object[][] dataProviderFor_test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered(){

        String prefix = "'-statistics':{'-help':{}";
        String suffix = "}";

        return new Object[][] {
                {
                        new DetailProvider[]{new DummyDetailProvider("-param1","Help For param1", new HashMap<String, DetailProvider>(4,1))},
                        prefix + ", 'DummyDatasource1':{} , 'DummyDatasource2':{} ,'-param1':{}" + suffix
                },
                {
                        new DetailProvider[]{},
                        prefix + ", 'DummyDatasource1':{} , 'DummyDatasource2':{} " + suffix
                }
        };
    }

    @Test
    @Parameters(method="dataProviderFor_test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered")
    public void test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered(
            DetailProvider[] dummyDetailProvider,
            String expectedHotKeyMsg ) throws RegistrationFailedException {

        for(DetailProvider dummy : dummyDetailProvider){
            provider.registerDetailProvider(dummy);
        }

        assertEquals(expectedHotKeyMsg, provider.getHotkeyHelp());
    }

    @Test
    public void test_getKey_should_give_name_of_registered_detail_provider() throws Exception {
        assertEquals(STATISTICS, provider.getKey());
    }


    @Test
    public void test_registerDetailProvider_should_throw_RegistrationFailedException_when_detail_provider_with_null_key_passed() throws Exception {

        //detail provider for null key
        DummyDetailProvider detailProvider = new DummyDetailProvider(null, "help", new HashMap<String, DetailProvider>());

        exception.expect(RegistrationFailedException.class);
        exception.expectMessage("Failed to register detail provider. Reason : key is not specified.");

        provider.registerDetailProvider(detailProvider);
    }

    @Test
    public void test_registerDetailProvider_should_throw_RegistrationFailedException_when_detail_provider_already_exist() throws Exception {
        DummyDetailProvider detailProvider1 = new DummyDetailProvider("dummy1", "help", new HashMap<String, DetailProvider>());
        DummyDetailProvider detailProvider2 = new DummyDetailProvider("dummy1", "help", new HashMap<String, DetailProvider>());

        provider.registerDetailProvider(detailProvider1);

        exception.expect(RegistrationFailedException.class);
        exception.expectMessage("Failed to register detail provider. Reason : Detail Provider already contains detail provider with Key : " + detailProvider2.getKey());

        provider.registerDetailProvider(detailProvider2);
    }

    private class DummyDetailProvider extends DetailProvider {

        private String key;
        private String helpMessage;
        private HashMap<String, DetailProvider> detailProviderMap;


        public DummyDetailProvider(String key, String helpMessage,
                                   HashMap<String, DetailProvider> map){
            this.key = key;
            this.helpMessage = helpMessage;
            this.detailProviderMap = map;
        }


        @Override
        public String execute(String[] parameters) {
            return key + " executed";
        }

        @Override
        public String getHelpMsg() {
            return helpMessage;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public HashMap<String, DetailProvider> getDetailProviderMap() {
            return detailProviderMap;
        }

    }

    private class DummyVoltDBStatisticsDetailProvider extends VoltDBStatisticsDetailProvider{

        private Map<String, DBDataSource> dataSourceMap;
        public DummyVoltDBStatisticsDetailProvider(NetVertexServerContext serverContext, Map<String, DBDataSource> dataSourceMap) {
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
