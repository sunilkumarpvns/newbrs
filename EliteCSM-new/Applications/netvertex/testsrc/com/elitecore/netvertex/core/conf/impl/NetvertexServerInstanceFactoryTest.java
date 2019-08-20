package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.constants.TimeBasedRollingUnit;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerInstanceDataBuilder;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileDataBuilder;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class NetvertexServerInstanceFactoryTest {

    private NetvertexServerInstanceFactory netvertexServerInstanceFactory;
    private ServerProfileData serverProfileData;
    private ServerInstanceData serverInstanceData;

    @Before
    public void setUp() {
        netvertexServerInstanceFactory = new NetvertexServerInstanceFactory();
        serverProfileData = ServerProfileDataBuilder.create();
        serverInstanceData = ServerInstanceDataBuilder.create();

    }

    public class LoggingParameter {

        public class SizeBaseRolling {

            @Before
            public void setUp() {
                serverProfileData.setRollingType(RollingType.SIZE_BASED.value);
            }

            @Test
            public void setRollingTypeToSizeBaseWhenSizeBaseRollingConfigured() {
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingType(), is(RollingType.SIZE_BASED));

            }

            @Test
            public void setConfiguredRollingUnit() {
                serverProfileData.setRollingUnits(RandomUtils.nextInt(1, Integer.MAX_VALUE));
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingUnit(), is(serverProfileData.getRollingUnits().toString()));
            }

            @Test
            public void setRollingUnitOneGBWhenNotConfigured() {
                serverProfileData.setRollingUnits(null);
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingUnit(), is("1048576"));
            }
        }

        public class TimeBaseRolling {

            @Before
            public void setUp() {
                serverProfileData.setRollingType(RollingType.TIME_BASED.value);
            }

            @Test
            public void setRollingTypeToSizeBaseWhenTimeBaseRollingConfigured() {
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingType(), is(RollingType.TIME_BASED));

            }

            @Test
            public void setRolledUnitAsDailyWhenDailyConfigured() {
                serverProfileData.setRollingUnits(TimeBasedRollingUnit.DAILY.value);
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingUnit(), is(TimeBasedRollingUnit.DAILY.name()));
            }

            @Test
            public void setRolledUnitAsWeeklyWhenHourConfigured() {
                serverProfileData.setRollingUnits(TimeBasedRollingUnit.HOUR.value);
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingUnit(), is(TimeBasedRollingUnit.HOUR.name()));
            }

            @Test
            public void setRolledUnitAsWeeklyWhenMinutesConfigured() {
                serverProfileData.setRollingUnits(TimeBasedRollingUnit.MINUTE.value);
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingUnit(), is(TimeBasedRollingUnit.MINUTE.name()));
            }

            @Test
            public void setRolledUnitAsDailyWhenNotConfigured() {
                serverProfileData.setRollingUnits(null);
                NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

                assertThat(netvertexServerInstanceConfiguration.getRollingUnit(), is(TimeBasedRollingUnit.DAILY.name()));
            }




        }

        @Test
        public void setLogLevelToWarnWhenNotConfigured() {
            serverProfileData.setLogLevel(null);
            NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

            assertThat(netvertexServerInstanceConfiguration.getLogLevel(), is(LogLevel.WARN.name()));
        }

        @Test
        public void setLogLevelToWarnWhenInvalidValueConfigured() {
            serverProfileData.setLogLevel(UUID.randomUUID().toString());
            NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

            assertThat(netvertexServerInstanceConfiguration.getLogLevel(), is(LogLevel.WARN.name()));
        }


        @Test
        public void setMaxRolledUnitAsConfigured() {
            serverProfileData.setMaxRolledUnits(RandomUtils.nextInt(1, 1000));
            NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

            assertThat(netvertexServerInstanceConfiguration.getMaxRollingUnit(), is(serverProfileData.getMaxRolledUnits()));

        }

        @Test
        public void setMaxRolledUnitToTenWhenNotConfigured() {
            serverProfileData.setMaxRolledUnits(null);
            NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

            assertThat(netvertexServerInstanceConfiguration.getMaxRollingUnit(), is(10));

        }

        @Test
        public void setTimeBaseRollingWhenNotConfigured() {
            serverProfileData.setRollingType(null);
            NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

            assertThat(netvertexServerInstanceConfiguration.getRollingType(), is(RollingType.TIME_BASED));

        }

        @Test
        public void setTimeBaseRollingWhenInvalidValueConfigured() {
            serverProfileData.setRollingType(1000);
            NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData, mock(ScriptDataFactory.class));

            assertThat(netvertexServerInstanceConfiguration.getRollingType(), is(RollingType.TIME_BASED));

        }

    }
}
