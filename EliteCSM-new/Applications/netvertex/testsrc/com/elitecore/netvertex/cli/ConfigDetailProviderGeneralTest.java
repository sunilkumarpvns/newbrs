package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashMap;

import static junit.framework.Assert.assertSame;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author chetan.sankhala
 */

@RunWith(value = junitparams.JUnitParamsRunner.class)
public class ConfigDetailProviderGeneralTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl context;

    @Before
    public void setUp() {
        context = DummyNetvertexServerContextImpl.spy();
        context.getServerConfiguration().setAlertListenerConfiguration(new AlertListenerConfiguration(Collections.emptyList(), Collections.emptyList()));
        provider = spy(new ConfigDetailProvider(context));
    }

    @Test
    @Parameters({"?", "-HELP", "-Help", "-help", "-hELP"})
    public void test_helpMessage_should_call_when_help_parameter_is_passed(String parameters) {

        String helpMsg = "help";

        when(provider.getHelpMsg()).thenReturn(helpMsg);

        assertSame(helpMsg, provider.execute(new String[]{parameters}));
    }

    public Object[][] dataProviderFor_test_execute_should_call_getHelpMsg_when_provided_detail_provider_not_exist() {

        final String NOT_REGISTERED_PARAM = "param3";

        return new Object[][]{
                {
                        new DetailProvider[]{new DummyDetailProvider("param1", "Help For param1", new HashMap<String, DetailProvider>(4, 1))},
                        new String[]{NOT_REGISTERED_PARAM}
                },
                {//two detail providers
                        new DetailProvider[]{new DummyDetailProvider("param1", "Help For param2", new HashMap<String, DetailProvider>(4, 1)), new DummyDetailProvider("param2", "Help For param2", new HashMap<String, DetailProvider>(4, 1))},
                        new String[]{NOT_REGISTERED_PARAM},
                },
                {
                        new DetailProvider[]{},
                        new String[]{NOT_REGISTERED_PARAM},
                },
                {
                        new DetailProvider[]{},
                        new String[]{},
                }
        };
    }


    @Test
    @Parameters(method = "dataProviderFor_test_execute_should_call_getHelpMsg_when_provided_detail_provider_not_exist")
    public void test_execute_should_call_getHelpMsg_when_provided_detail_provider_not_exist(
            DetailProvider[] detailProviders,
            String[] parameters) throws Exception {

        registerDetailProviders(provider, detailProviders);

        provider.execute(parameters);
        verify(provider, times(1)).getHelpMsg();
    }

    private void registerDetailProviders(ConfigDetailProvider provider, DetailProvider[] detailProviders) throws Exception {

        for (DetailProvider detailProvider : detailProviders) {
            provider.registerDetailProvider(detailProvider);
        }
    }

    @Test
    public void test_execute_should_call_execute_of_registered_detail_provided() throws Exception {

        DetailProvider mockedDetailProvider = registerMockedDetailProvider();

        provider.execute(new String[]{"dummy"});

        verify(mockedDetailProvider, times(1)).execute(new String[]{});
    }

    private DetailProvider registerMockedDetailProvider() throws Exception {

        DetailProvider detailProvider = mock(DetailProvider.class);
        when(detailProvider.getKey()).thenReturn("dummy");

        provider.registerDetailProvider(detailProvider);

        return detailProvider;
    }

    @Test
    public void test_getKey_should_give_name_of_registered_detail_provider() throws Exception {
        assertEquals("config", provider.getKey());
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

    public Object[][] dataProviderFor_test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered() {
        return new Object[][]{
                {
                        new DetailProvider[]{new DummyDetailProvider("-param1", "Help For param1", new HashMap<String, DetailProvider>(4, 1))},
                        "'-param1':{}"
                },
                {
                        new DetailProvider[]{},
                        ""
                }
        };
    }

    @Test
    @Parameters(method = "dataProviderFor_test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered")
    public void test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered(
            DetailProvider[] dummyDetailProvider,
            String expectedHotKeyMsg) throws RegistrationFailedException {

        for (DetailProvider dummy : dummyDetailProvider) {
            provider.registerDetailProvider(dummy);
        }
        assertThat(provider.getHotkeyHelp(), containsString(expectedHotKeyMsg));
    }

    @Test
    public void sessionManagerToStringShouldBePrintedWhenSessionManagerKeyPassed() {

        String sessionManagerKey = "session-manager";
        DummyNetvertexServerConfiguration netvertexServerConfiguration = context.getServerConfiguration();
        SessionManagerConfiguration sessionManagerConfiguration = netvertexServerConfiguration.spySessionManagerConf();
        String expectedOutput = "expectedOutput";
        when(sessionManagerConfiguration.toString()).thenReturn(expectedOutput);

        String actualOutput = provider.execute(new String[]{sessionManagerKey});

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void sessionManagerToStringShouldBePrintedWhenSessionManagerKeyPassedWithParameter() {
        String sessionManagerKey = "session-manager";
        DummyNetvertexServerConfiguration netvertexServerConfiguration = context.getServerConfiguration();
        SessionManagerConfiguration sessionManagerConfiguration = netvertexServerConfiguration.spySessionManagerConf();
        String expectedOutput = "expectedOutput";
        when(sessionManagerConfiguration.toString()).thenReturn(expectedOutput);

        String actualOutput = provider.execute(new String[]{sessionManagerKey, "param1"});

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void ddfConfigurationToStringShouldBeReturnedWhenDdfKeyPassedWithoutParameter() {
        String key = "ddf";
        DummyNetvertexServerConfiguration netvertexServerConfiguration = context.getServerConfiguration();
        DDFConfiguration configuration = netvertexServerConfiguration.spyDDFConfiguration();
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, "param1"});
        assertEquals(expectedOutput, actualOutput);
    }
}
