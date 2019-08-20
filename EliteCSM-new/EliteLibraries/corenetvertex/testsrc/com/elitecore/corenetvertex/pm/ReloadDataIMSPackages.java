package com.elitecore.corenetvertex.pm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReloadDataIMSPackages {

    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public static final String GROUP_ID = "Guj";
    private DeploymentMode deploymentMode = DeploymentMode.PCC;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private DummyDataReader dummyDataReader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        packageFactory = new PackageFactory();
        generatePolicyBackUpFile();
        policyManager = new PolicyManager();
        RnCFactory rnCFactory = new RnCFactory();
        dummyDataReader = new DummyDataReader();
		initPolicyManager(rnCFactory);
	}

	private void initPolicyManager(RnCFactory rnCFactory) throws InitializationFailedException {
		policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory, new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
	}

	private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    public class byNames {
        @Test
        public void policyCacheShouldRemovedWhenPolicyIsDeleted() {
            IMSPkgData ims1 = PkgDataBuilder.newImsPackageWithDefaultValue();
            ims1.setGroups(GROUP_ID);
            dummyDataReader.setReadList(IMSPkgData.class, createIMSPackageDataList(ims1));
            policyManager.reload();
            //Returning empty list is as same as deleting all packages
            dummyDataReader.setReadList(IMSPkgData.class, new ArrayList());

            policyManager.reloadIMSPackages(ims1.getName());

            assertTrue(policyManager.getIMSPackageByName(ims1.getName()).isEmpty());
            assertNull(policyManager.getIMSPkgById(ims1.getId()));
            assertNull(policyManager.getIMSPkgByName(ims1.getName()));
            assertNull(policyManager.getActiveIMSPackageByName(ims1.getName()));
            assertTrue(policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertTrue(policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertTrue(policyManager.getAllIMSPackageNames().isEmpty());
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
            IMSPkgData ims1 = PkgDataBuilder.newImsPackageWithDefaultValue();
            ims1.setId("id_1");
            ims1.setGroups(GROUP_ID);
            dummyDataReader.setReadList(IMSPkgData.class, createIMSPackageDataList(ims1));
            policyManager.reload();
            IMSPkgData ims2 = PkgDataBuilder.newImsPackageWithDefaultValue();
            ims1.setId("id_2");
            ims2.setGroups(GROUP_ID);
            dummyDataReader.setReadList(IMSPkgData.class, createIMSPackageDataList(ims2));

            policyManager.reloadIMSPackages(ims2.getName());

            assertNull(policyManager.getIMSPkgById(ims1.getId()));

            assertNotNull(policyManager.getIMSPkgById(ims2.getId()));
            assertEquals(ims2.getId(), policyManager.getActiveIMSPackageByName(ims1.getName()).getId());
            assertEquals(ims2.getId(), policyManager.getIMSPackageByName(ims1.getName()).get(0).getId());
            assertEquals(ims2.getId(), policyManager.getIMSPkgByName(ims1.getName()).getId());
            assertEquals(ims2.getId(), policyManager.getActiveAllImsPkgDatas().get(0).getId());
            assertEquals(ims2.getId(), policyManager.getActiveLiveImsPkgDatas().get(0).getId());
            assertEquals(1, policyManager.getAllIMSPackageNames().size());
        }

    }

    private List createIMSPackageDataList(IMSPkgData ims) {
        List sliceList = new ArrayList();
        sliceList.add(ims);
        return sliceList;
    }

    public class byGroupIds {

        @Test
        public void policyCacheShouldRemovedWhenPolicyIsDeleted() {
            IMSPkgData ims1 = PkgDataBuilder.newImsPackageWithDefaultValue();
            ims1.setGroups(GROUP_ID);
            dummyDataReader.setReadList(IMSPkgData.class, createIMSPackageDataList(ims1));
            policyManager.reload();
            dummyDataReader.setReadList(IMSPkgData.class, new ArrayList());

            policyManager.reloadIMSPackagesOfGroups(Arrays.asList(GROUP_ID));

            assertTrue(policyManager.getIMSPackageByName(ims1.getName()).isEmpty());
            assertNull(policyManager.getIMSPkgById(ims1.getId()));
            assertNull(policyManager.getIMSPkgByName(ims1.getName()));
            assertNull(policyManager.getActiveIMSPackageByName(ims1.getName()));
            assertTrue(policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertTrue(policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertTrue(policyManager.getAllIMSPackageNames().isEmpty());
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
            IMSPkgData ims1 = PkgDataBuilder.newImsPackageWithDefaultValue();
            ims1.setId("id_1");
            ims1.setGroups(GROUP_ID);
            dummyDataReader.setReadList(IMSPkgData.class, createIMSPackageDataList(ims1));
            policyManager.reload();
            IMSPkgData ims2 = PkgDataBuilder.newImsPackageWithDefaultValue();
            ims2.setGroups(GROUP_ID);
            ims1.setId("id_2");
            dummyDataReader.setReadList(IMSPkgData.class, createIMSPackageDataList(ims2));

            policyManager.reloadIMSPackagesOfGroups(Arrays.asList(GROUP_ID));

            assertNull(policyManager.getIMSPkgById(ims1.getId()));

            assertNotNull(policyManager.getIMSPkgById(ims2.getId()));
            assertEquals(ims2.getId(), policyManager.getActiveIMSPackageByName(ims1.getName()).getId());
            assertEquals(ims2.getId(), policyManager.getIMSPackageByName(ims1.getName()).get(0).getId());
            assertEquals(ims2.getId(), policyManager.getIMSPkgByName(ims1.getName()).getId());
            assertEquals(ims2.getId(), policyManager.getActiveAllImsPkgDatas().get(0).getId());
            assertEquals(ims2.getId(), policyManager.getActiveLiveImsPkgDatas().get(0).getId());
            assertEquals(1, policyManager.getAllIMSPackageNames().size());
        }
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        policyManager.stop();
    }
}
