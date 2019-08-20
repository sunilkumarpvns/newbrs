package com.elitecore.corenetvertex.pm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.pkg.PkgData;
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

/**
 * For purged packages, reload test cases
 * @author Chetan.Sankhala
 */
@RunWith(HierarchicalContextRunner.class)
public class ReloadDataPackages {
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
		policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
	}


	public class byGroups {
        public class WhenBasePackageIsDeleted {
            @Test
            public void policyCacheShouldRemoved() {
                PkgData basePkg1 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
                basePkg1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(basePkg1));
                policyManager.reload();
                dummyDataReader.setReadList(PkgData.class, new ArrayList());

                policyManager.reloadDataPackagesOfGroups(Arrays.asList(GROUP_ID));

                assertNull(policyManager.getPkgDataById(basePkg1.getId()));
                assertTrue(policyManager.getPkgDatasByName(basePkg1.getName()).isEmpty());
                assertNull(policyManager.getBasePackageDataById(basePkg1.getId()));
                assertNull(policyManager.getBasePackageDataByName(basePkg1.getName()));
                assertNull(policyManager.getActiveBasePackageById(basePkg1.getId()));
                assertNull(policyManager.getActiveBasePackageByName(basePkg1.getName()));
                assertTrue(policyManager.getActiveLiveBasePkgDatas().isEmpty());
                assertTrue(policyManager.getActiveAllBasePkgDatas().isEmpty());
            }

            @Test
            public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
                PkgData basePkg1 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
                basePkg1.setId("id_5");
                basePkg1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(basePkg1));
                policyManager.reload();
                PkgData basePkg2 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
                basePkg2.setId("id_6");
                basePkg2.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(basePkg2));
                policyManager.reloadDataPackagesOfGroups(Arrays.asList(GROUP_ID));

                assertNull(policyManager.getPkgDataById(basePkg1.getId()));
                assertNotNull(policyManager.getPkgDataById(basePkg2.getId()));
                assertNull(policyManager.getBasePackageDataById(basePkg1.getId()));
                assertNotNull(policyManager.getBasePackageDataById(basePkg2.getId()));
                assertNull(policyManager.getActiveBasePackageById(basePkg1.getId()));
                assertEquals(1, policyManager.getPkgDatasByName(basePkg2.getName()).size());
                assertEquals(basePkg2.getId(), policyManager.getPkgDatasByName(basePkg2.getName()).get(0).getId());
                assertEquals(basePkg2.getId(), policyManager.getActiveBasePackageByName(basePkg2.getName()).getId());
                assertEquals(1, policyManager.getActiveAllBasePkgDatas().size());
                assertEquals(1, policyManager.getActiveLiveBasePkgDatas().size());
            }
        }

        public class WhenAddOnPackageIsDeleted {
            @Test
            public void policyCacheShouldRemoved() {
                PkgData addOn1 = PkgDataBuilder.newAddOnWithDefaultValues();
                addOn1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(addOn1));
                policyManager.reload();
                dummyDataReader.setReadList(PkgData.class, new ArrayList());

                policyManager.reloadDataPackagesOfGroups(Arrays.asList(GROUP_ID));

                assertNull(policyManager.getPkgDataById(addOn1.getId()));
                assertTrue(policyManager.getPkgDatasByName(addOn1.getName()).isEmpty());
                assertNull(policyManager.getAddOnById(addOn1.getId()));
                assertNull(policyManager.getAddOnByName(addOn1.getName()));
                assertNull(policyManager.getActiveAddOnById(addOn1.getId()));
                assertNull(policyManager.getActiveAddOnByName(addOn1.getName()));
                assertTrue(policyManager.getActiveLiveAddOnDatas().isEmpty());
            }

            @Test
            public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
                PkgData addOnPkg1 = PkgDataBuilder.newAddOnWithDefaultValues();
                addOnPkg1.setGroups(GROUP_ID);
                addOnPkg1.setId("Id_3");
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(addOnPkg1));
                policyManager.reload();
                PkgData addOnPkg2 = PkgDataBuilder.newAddOnWithDefaultValues();
                addOnPkg2.setGroups(GROUP_ID);
                addOnPkg1.setId("Id_4");
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(addOnPkg2));
                policyManager.reloadDataPackagesOfGroups(Arrays.asList(GROUP_ID));

                assertNull(policyManager.getPkgDataById(addOnPkg1.getId()));
                assertNull(policyManager.getAddOnById(addOnPkg1.getId()));
                assertNull(policyManager.getActiveAddOnById(addOnPkg1.getId()));
                assertEquals(1, policyManager.getActiveLiveAddOnDatas().size());

                assertNull(policyManager.getPkgDataById(addOnPkg1.getId()));
                assertNotNull(policyManager.getPkgDataById(addOnPkg2.getId()));
                assertNull(policyManager.getAddOnById(addOnPkg1.getId()));
                assertNotNull(policyManager.getAddOnById(addOnPkg2.getId()));
                assertNull(policyManager.getActiveAddOnById(addOnPkg1.getId()));
                assertEquals(1, policyManager.getPkgDatasByName(addOnPkg2.getName()).size());
                assertEquals(addOnPkg2.getId(), policyManager.getPkgDatasByName(addOnPkg2.getName()).get(0).getId());
                assertEquals(addOnPkg2.getId(), policyManager.getActiveAddOnByName(addOnPkg2.getName()).getId());
                assertEquals(1, policyManager.getActiveAllAddOnPkgDatas(false).size());
                assertEquals(1, policyManager.getActiveLiveAddOnDatas().size());
            }
        }
    }

    public class byName {
        public class WhenBasePackageIsDeleted {
            @Test
            public void policyCacheShouldRemoved() {
                PkgData basePkg1 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
                basePkg1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(basePkg1));
                policyManager.reload();
                dummyDataReader.setReadList(PkgData.class, new ArrayList());

                policyManager.reloadByName(basePkg1.getName());

                assertNull(policyManager.getPkgDataById(basePkg1.getId()));
                assertTrue(policyManager.getPkgDatasByName(basePkg1.getName()).isEmpty());
                assertNull(policyManager.getBasePackageDataById(basePkg1.getId()));
                assertNull(policyManager.getBasePackageDataByName(basePkg1.getName()));
                assertNull(policyManager.getActiveBasePackageById(basePkg1.getId()));
                assertNull(policyManager.getActiveBasePackageByName(basePkg1.getName()));
                assertTrue(policyManager.getActiveLiveBasePkgDatas().isEmpty());
                assertTrue(policyManager.getActiveAllBasePkgDatas().isEmpty());
            }

            @Test
            public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
                PkgData basePkg1 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
                basePkg1.setId("id_9");
                basePkg1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(basePkg1));
                policyManager.reload();
                PkgData basePkg2 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
                basePkg2.setId("id_10");
                basePkg2.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(basePkg2));

                policyManager.reloadByName(basePkg1.getName());

                assertNull(policyManager.getPkgDataById(basePkg1.getId()));
                assertNotNull(policyManager.getPkgDataById(basePkg2.getId()));
                assertNull(policyManager.getBasePackageDataById(basePkg1.getId()));
                assertNotNull(policyManager.getBasePackageDataById(basePkg2.getId()));
                assertNull(policyManager.getActiveBasePackageById(basePkg1.getId()));
                assertEquals(1, policyManager.getPkgDatasByName(basePkg2.getName()).size());
                assertEquals(basePkg2.getId(), policyManager.getPkgDatasByName(basePkg2.getName()).get(0).getId());
                assertEquals(basePkg2.getId(), policyManager.getActiveBasePackageByName(basePkg2.getName()).getId());
                assertEquals(1, policyManager.getActiveAllBasePkgDatas().size());
                assertEquals(1, policyManager.getActiveLiveBasePkgDatas().size());
            }
        }

        public class WhenAddOnPackageIsDeleted {
            @Test
            public void policyCacheShouldRemoved() {
                PkgData addOn1 = PkgDataBuilder.newAddOnWithDefaultValues();
                addOn1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(addOn1));
                policyManager.reload();
                dummyDataReader.setReadList(PkgData.class, new ArrayList());

                policyManager.reloadByName(addOn1.getName());

                assertNull(policyManager.getPkgDataById(addOn1.getId()));
                assertTrue(policyManager.getPkgDatasByName(addOn1.getName()).isEmpty());
                assertNull(policyManager.getAddOnById(addOn1.getId()));
                assertNull(policyManager.getAddOnByName(addOn1.getName()));
                assertNull(policyManager.getActiveAddOnById(addOn1.getId()));
                assertNull(policyManager.getActiveAddOnByName(addOn1.getName()));
                assertTrue(policyManager.getActiveLiveAddOnDatas().isEmpty());
            }

            @Test
            public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
                PkgData addOnPkg1 = PkgDataBuilder.newAddOnWithDefaultValues();
                addOnPkg1.setId("id_7");
                addOnPkg1.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(addOnPkg1));
                policyManager.reload();
                PkgData addOnPkg2 = PkgDataBuilder.newAddOnWithDefaultValues();
                addOnPkg2.setId("id_8");
                addOnPkg2.setGroups(GROUP_ID);
                dummyDataReader.setReadList(PkgData.class, createPkgDataList(addOnPkg2));

                policyManager.reloadByName(addOnPkg1.getName());

                assertNull(policyManager.getPkgDataById(addOnPkg1.getId()));
                assertNull(policyManager.getAddOnById(addOnPkg1.getId()));
                assertNull(policyManager.getActiveAddOnById(addOnPkg1.getId()));
                assertEquals(1, policyManager.getActiveLiveAddOnDatas().size());

                assertNull(policyManager.getPkgDataById(addOnPkg1.getId()));
                assertNotNull(policyManager.getPkgDataById(addOnPkg2.getId()));
                assertNull(policyManager.getAddOnById(addOnPkg1.getId()));
                assertNotNull(policyManager.getAddOnById(addOnPkg2.getId()));
                assertNull(policyManager.getActiveAddOnById(addOnPkg1.getId()));
                assertEquals(1, policyManager.getPkgDatasByName(addOnPkg2.getName()).size());
                assertEquals(addOnPkg2.getId(), policyManager.getPkgDatasByName(addOnPkg2.getName()).get(0).getId());
                assertEquals(addOnPkg2.getId(), policyManager.getActiveAddOnByName(addOnPkg2.getName()).getId());
                assertEquals(1, policyManager.getActiveAllAddOnPkgDatas(false).size());
                assertEquals(1, policyManager.getActiveLiveAddOnDatas().size());
            }
        }
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        hibernateSessionFactory.shutdown();
        policyManager.stop();
    }

    private List<PkgData> createPkgDataList(PkgData pkgData) {
        List<PkgData> list = new ArrayList<>(2);
        list.add(pkgData);
        return list;
    }

}
