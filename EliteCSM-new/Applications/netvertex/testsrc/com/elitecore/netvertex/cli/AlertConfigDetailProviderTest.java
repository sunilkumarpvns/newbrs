package com.elitecore.netvertex.cli;

import java.util.ArrayList;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.FileAlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.TrapAlertListenerConfiguration;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static com.elitecore.corenetvertex.constants.CommonConstants.COMMA;
import static com.elitecore.corenetvertex.constants.CommonConstants.SINGLE_QUOTE;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class AlertConfigDetailProviderTest {

    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl context;
    private AlertListenerConfiguration alertListenerConfiguration;

    @Before
    public void setUp() {
        context = DummyNetvertexServerContextImpl.spy();
        provider = spy(new ConfigDetailProvider(context));
        alertListenerConfiguration = spy(new AlertListenerConfiguration(new ArrayList<>(), new ArrayList<>()));
        context.getServerConfiguration().setAlertListenerConfiguration(alertListenerConfiguration);
    }


    @Test
    public void toStringOfAlertListenerConfiguredPrintedWhenAlertKeyKeyPassed() {

        String key = "alert";
        String expectedOutput = "expectedOutput";
        when(alertListenerConfiguration.toString()).thenReturn(expectedOutput);

        String actualOutput = provider.execute(new String[]{key});

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringOfTrapAlertListenerConfiguredPrintedWhenTrapListenerConfigurationNameIsPassed() {

        String alertKey = "alert";
        String trapListenerName = "trap-listener";
        TrapAlertListenerConfiguration configuration = addTrapListenerConfiguration(trapListenerName);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{alertKey, trapListenerName});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringOfFileAlertListenerConfiguredPrintedWhenFileListenerConfigurationNameIsPassed() {

        String alertKey = "alert";
        String trapListenerName = "file-listener";
        FileAlertListenerConfiguration configuration = addFileListenerConfiguration(trapListenerName);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{alertKey, trapListenerName});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void notFoundMessageReceivedWhenConfigurationNotFoundForProvidedAlertsName() {

        String alertKey = "alert";
        String fakeAlertName = "default-alert";
        String expectedOutput = "Alert configuration not found for name: " + fakeAlertName;

        String actualOutput = provider.execute(new String[]{alertKey, fakeAlertName});

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageShouldContainsDescription() {

        String expectedOutput = "View Alert Listener Configuration";

        String actualOutput = provider.execute(new String[]{"-help"});

        assertThat(actualOutput, containsString(expectedOutput));
    }

    @Test
    public void helpMessageShouldContainsUsage() {

        String expectedOutput = "alert [alert-configuration-name]";

        String actualOutput = provider.execute(new String[]{"alert", "-help"});

        assertThat(actualOutput, containsString(expectedOutput));
    }


    public class hotKeyHelpContainsAvailableAlertsNameInSortedOrder {


        @Test
        public void noAlertConfigured() {
            String expectedOutput = SINGLE_QUOTE + "alert" + SINGLE_QUOTE  + ":{}";

            String actualOutput = provider.getHotkeyHelp();

            assertThat(actualOutput, containsString(expectedOutput));

        }

        @Test
        public void fileAlertConfigured() {

            String name = "file-listener";

            addFileListenerConfiguration(name);


            String expectedOutput = SINGLE_QUOTE + "alert" + SINGLE_QUOTE  + ":{" +
                    SINGLE_QUOTE + name + SINGLE_QUOTE  + ":{}}";

            String actualOutput = provider.getHotkeyHelp();

            assertThat(actualOutput, containsString(expectedOutput));
        }

        @Test
        public void trapAlertConfigured() {
            String name = "trap-listener";

            addTrapListenerConfiguration(name);

            String expectedOutput = SINGLE_QUOTE + "alert" + SINGLE_QUOTE  + ":{" +
                    SINGLE_QUOTE + name + SINGLE_QUOTE  + ":{}}";

            String actualOutput = provider.getHotkeyHelp();

            assertThat(actualOutput, containsString(expectedOutput));
        }

        @Test
        public void bothTypeListenerConfigured() {
            String name = "trap-listener";
            addTrapListenerConfiguration(name);

            String fileListenerName = "file-listener";
            addFileListenerConfiguration(fileListenerName);

            String expectedOutput = SINGLE_QUOTE + "alert" + SINGLE_QUOTE  + ":{" +
                            SINGLE_QUOTE + fileListenerName + SINGLE_QUOTE  + ":{}" +
                            COMMA + SINGLE_QUOTE + name + SINGLE_QUOTE  + ":{}" +
                    "}";

            String actualOutput = provider.getHotkeyHelp();

            assertThat(actualOutput, containsString(expectedOutput));
        }
    }

    private TrapAlertListenerConfiguration addTrapListenerConfiguration(String trapListenerName) {
        TrapAlertListenerConfiguration trapAlertListenerConfiguration = mock(TrapAlertListenerConfiguration.class);
        when(trapAlertListenerConfiguration.getName()).thenReturn(trapListenerName);
        alertListenerConfiguration.getTrapAlertListenerConfigurations().add(trapAlertListenerConfiguration);
        return trapAlertListenerConfiguration;
    }

    private FileAlertListenerConfiguration addFileListenerConfiguration(String fileListenerConfiguration) {
        FileAlertListenerConfiguration fileAlertListenerConfiguration = mock(FileAlertListenerConfiguration.class);
        when(fileAlertListenerConfiguration.getName()).thenReturn(fileListenerConfiguration);
        alertListenerConfiguration.getFileAlertListenerConfigurations().add(fileAlertListenerConfiguration);
        return fileAlertListenerConfiguration;
    }
}
