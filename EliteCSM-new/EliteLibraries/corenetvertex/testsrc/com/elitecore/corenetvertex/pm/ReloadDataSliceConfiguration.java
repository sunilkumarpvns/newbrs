package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pd.sliceconfig.SliceConfigData;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReloadDataSliceConfiguration {

    public static final String SLICE_CONFIG = "SLICE_CONFIG_1";
    public static final long TIME_MINIMUM_SLICE = 15L;
    public static final long TIME_MAXIMUM_SLICE = 65L;
    public static final int VOLUME_SLICE_THRESHOLD = 15;
    public static final int VOLUME_SLICE_PERCENTAGE = 85;
    public static final int TIME_SLICE_THRESHOLD = 20;
    private PolicyManager policyManager;
    private RnCFactory rnCFactory = new RnCFactory();
    private PackageFactory packageFactory;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private RnCPackageFactory rnCPackageFactory = new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory), rnCFactory), rnCFactory), rnCFactory, new ThresholdNotificationSchemeFactory(rnCFactory));
    private DeploymentMode deploymentMode = DeploymentMode.PCC;

    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private DummyDataReader dummyDataReader;

    @Before
    public void setUp() throws Exception {
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        packageFactory = new PackageFactory();
        policyManager = new PolicyManager();
        dummyDataReader = new DummyDataReader();
    }

    private void initPolicyManager() throws InitializationFailedException {
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory, rnCPackageFactory, deploymentMode);
    }

    @Test
    public void testReloadedDataGettingStoredInPolicyManager() throws InitializationFailedException, LoadConfigurationException {
        initPolicyManager();
        SliceConfigData dataSliceConfiguration = new SliceConfigData();
        createDataSliceConfiguration(dataSliceConfiguration);
        dummyDataReader.setReadList(SliceConfigData.class, createSliceConfigDataList(dataSliceConfiguration));
        policyManager.reloadSliceConfiguration();

        dataSliceConfiguration.setVolumeSliceThreshold(VOLUME_SLICE_THRESHOLD);
        dataSliceConfiguration.setVolumeSlicePercentage(VOLUME_SLICE_PERCENTAGE);

        dataSliceConfiguration.setTimeMaximumSliceUnit(TimeUnit.SECOND.name());
        dataSliceConfiguration.setTimeSliceThreshold(TIME_SLICE_THRESHOLD);

        dummyDataReader.setReadList(SliceConfigData.class, createSliceConfigDataList(dataSliceConfiguration));
        policyManager.reloadSliceConfiguration();

        assertEquals(policyManager.getSliceConfiguration().getTimeMaximumSliceUnit(), TimeUnit.SECOND);
        assertEquals(policyManager.getSliceConfiguration().getTimeSliceThreshold(),TIME_SLICE_THRESHOLD);
        assertEquals(policyManager.getSliceConfiguration().getVolumeSliceThreshold(),VOLUME_SLICE_THRESHOLD);
        assertEquals(policyManager.getSliceConfiguration().getVolumeSlicePercentage(),VOLUME_SLICE_PERCENTAGE);
    }

    private List createSliceConfigDataList(SliceConfigData dataSliceConfiguration) {
        List sliceList = new ArrayList();
        sliceList.add(dataSliceConfiguration);
        return sliceList;
    }

    private void createDataSliceConfiguration(SliceConfigData dataSliceConfiguration) {
        dataSliceConfiguration.setMonetaryReservation(Long.valueOf(10));
        dataSliceConfiguration.setVolumeMinimumSlice(Long.valueOf(10));
        dataSliceConfiguration.setVolumeMaximumSlice(Long.valueOf(400));
        dataSliceConfiguration.setVolumeMinimumSliceUnit(DataUnit.MB.name());
        dataSliceConfiguration.setVolumeMaximumSliceUnit(DataUnit.MB.name());
        dataSliceConfiguration.setTimeMinimumSlice(Long.valueOf(10));
        dataSliceConfiguration.setTimeMaximumSlice(Long.valueOf(1));
        dataSliceConfiguration.setTimeMinimumSliceUnit(TimeUnit.SECOND.name());
        dataSliceConfiguration.setTimeMaximumSliceUnit(TimeUnit.HOUR.name());
        dataSliceConfiguration.setVolumeSlicePercentage(10);
        dataSliceConfiguration.setTimeSlicePercentage(10);
        dataSliceConfiguration.setVolumeSliceThreshold(10);
        dataSliceConfiguration.setTimeSliceThreshold(10);
    }

}
