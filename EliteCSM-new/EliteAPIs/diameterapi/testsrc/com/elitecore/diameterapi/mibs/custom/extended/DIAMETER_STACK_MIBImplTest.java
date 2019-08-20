package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.config.DiameterConfiguration;
import com.elitecore.diameterapi.mibs.custom.autogen.ApplicationStatisticsEntryMBean;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatistic;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.elitecore.diameterapi.mibs.statistics.MIBIndexRecorder;
import com.sun.management.snmp.SnmpStatusException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DIAMETER_STACK_MIBImplTest {

    @Mock
    private MIBIndexRecorder mibIndexRecorder;
    @Mock
    private DiameterStatistic diameterStatistic;
    @Mock
    DiameterStatisticListener diameterStatisticListener;
    @Mock
    MBeanServer mBeanServer;

    private DiameterConfiguration diameterConfigProvider;

    private DummySNMPTableFactoryImpl dummySNMPTableFactory;
    private DIAMETER_STACK_MIBImpl diameter_stack_mib;

    @Before
    public void setup() throws SnmpStatusException {
        MockitoAnnotations.initMocks(this);
        diameterConfigProvider = new DiameterConfiguration(mibIndexRecorder);
        doReturn(diameterConfigProvider).when(diameterStatisticListener).getDiameterConfigProvider();
        doReturn(diameterStatistic).when(diameterStatisticListener).getDiameterStatisticProvider();
        dummySNMPTableFactory = DummySNMPTableFactoryImpl.create();
        diameter_stack_mib = new DIAMETER_STACK_MIBImpl(diameterStatisticListener, Status.INITIALIZED, dummySNMPTableFactory);
    }

    @Test
    public void test_populate_checkForValidApplicationEntries() throws Exception {
        Map<ApplicationStatsIdentifier, GroupedStatistics> appEntries = generateDummyAppStatMapwithValidEntries();
        doReturn(appEntries).when(diameterStatistic).getApplicationMap();

        diameter_stack_mib.populate(mBeanServer, null);
        verify(dummySNMPTableFactory.getTableAppStatisticsTable()
                , times(appEntries.size())).addEntry(Mockito.any(ApplicationStatisticsEntryMBean.class), Mockito.any(ObjectName.class));

    }

    @Test
    public void test_populate_checkForDuplicateApplicationEntries() throws Exception {
        Map<ApplicationStatsIdentifier, GroupedStatistics> appEntries = generateDummyAppStatMapwithDuplicateEntries();
        doReturn(generateDummyAppStatMapwithDuplicateEntries()).when(diameterStatistic).getApplicationMap();
        diameter_stack_mib.populate(mBeanServer, null);
        verify(dummySNMPTableFactory.getTableAppStatisticsTable()
                , times(appEntries.size()-1)).addEntry(Mockito.any(ApplicationStatisticsEntryMBean.class), Mockito.any(ObjectName.class));

    }

    private Map<ApplicationStatsIdentifier, GroupedStatistics> generateDummyAppStatMapwithDuplicateEntries(){

        List<ApplicationStatsIdentifier> applicationStatsIdentifiers = new ArrayList<ApplicationStatsIdentifier>();

        applicationStatsIdentifiers.add(new ApplicationStatsIdentifier(16777238, 9, "3GPP-GX"));
        applicationStatsIdentifiers.add(new ApplicationStatsIdentifier(16777302, 10415, "3GPP-SY"));
        applicationStatsIdentifiers.add(new ApplicationStatsIdentifier(16777238, 10415, "3GPP-GX"));

        return generateDummyAppStatMap(applicationStatsIdentifiers);
    }

    private Map<ApplicationStatsIdentifier, GroupedStatistics> generateDummyAppStatMapwithValidEntries(){

        List<ApplicationStatsIdentifier> applicationStatsIdentifiers = new ArrayList<ApplicationStatsIdentifier>();

        applicationStatsIdentifiers.add(new ApplicationStatsIdentifier(16777238, 9, "3GPP-GX"));
        applicationStatsIdentifiers.add(new ApplicationStatsIdentifier(16777302, 10415, "3GPP-SY"));
        applicationStatsIdentifiers.add(new ApplicationStatsIdentifier(16777239, 10415, "3GPP-GX"));

        return generateDummyAppStatMap(applicationStatsIdentifiers);
    }


    private Map<ApplicationStatsIdentifier, GroupedStatistics> generateDummyAppStatMap(List<ApplicationStatsIdentifier> applicationStatsIdentifiers){

        Map<ApplicationStatsIdentifier, GroupedStatistics> appStats = new HashMap<>();

        for(ApplicationStatsIdentifier applicationStatsIdentifier : applicationStatsIdentifiers){
            appStats.put(applicationStatsIdentifier, mock(GroupedStatistics.class));
        }
        return appStats;
    }

}
