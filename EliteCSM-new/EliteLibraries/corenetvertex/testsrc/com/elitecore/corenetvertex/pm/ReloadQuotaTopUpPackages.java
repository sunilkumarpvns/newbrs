package com.elitecore.corenetvertex.pm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
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
 * For purged packages, reload quota topup test cases
 * @author Chetan.Sankhala
 */
@RunWith(HierarchicalContextRunner.class)
public class ReloadQuotaTopUpPackages {

    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public static final String GROUP_ID = "Guj";
	private DeploymentMode deploymentMode = DeploymentMode.PCC;
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
		initPolicyManager(rnCFactory);
	}

	private void initPolicyManager(RnCFactory rnCFactory) throws InitializationFailedException {
        dummyDataReader = new DummyDataReader();
		policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
	}

	private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    public class byNames {
        @Test
        public void policyCacheShouldRemovedWhenPolicyIsDeleted() {
            DataTopUpData topup1 = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopupDataList(topup1));
            policyManager.reload();
            dummyDataReader.setReadList(DataTopUpData.class, new ArrayList());

            policyManager.reloadQuotaTopUps(topup1.getName());

            assertNull(policyManager.getQuotaTopUpById(topup1.getId()));
            assertNull(policyManager.getQuotaTopUpByName(topup1.getName()));
            assertNull(policyManager.getActiveQuotaTopUpById(topup1.getId()));
            assertNull(policyManager.getActiveQuotaTopUpByName(topup1.getName()));
            assertTrue(policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertTrue(policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
            DataTopUpData topup1 = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            topup1.setGroups(GROUP_ID);
            topup1.setId("foo");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopupDataList(topup1));
            policyManager.reload();
            DataTopUpData topup2 = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            topup2.setGroups(GROUP_ID);
            topup1.setId("bar");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopupDataList(topup2));

            policyManager.reloadQuotaTopUps(topup1.getName());

            assertNull(policyManager.getQuotaTopUpById(topup1.getId()));
            assertNotNull(policyManager.getQuotaTopUpById(topup2.getId()));
            assertEquals(topup2.getId(), policyManager.getQuotaTopUpByName(topup2.getName()).getId());
            assertEquals(topup2.getId(), policyManager.getActiveQuotaTopUpByName(topup2.getName()).getId());
            assertEquals(1, policyManager.getActiveAllQuotaTopUpDatas().size());
            assertEquals(1, policyManager.getActiveLiveQuotaTopUpDatas().size());
            assertEquals(topup2.getId(), policyManager.getActiveAllQuotaTopUpDatas().get(0).getId());
            assertEquals(topup2.getId(), policyManager.getActiveLiveQuotaTopUpDatas().get(0).getId());
        }

    }

    public class byGroupIds {

        @Test
        public void policyCacheShouldRemovedWhenPolicyIsDeleted() {
            DataTopUpData topup1 = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            topup1.setGroups(GROUP_ID);
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopupDataList(topup1));
            policyManager.reload();
            dummyDataReader.setReadList(DataTopUpData.class, new ArrayList());

            policyManager.reloadQuotaTopUpsOfGroups(Arrays.asList(GROUP_ID));

            assertNull(policyManager.getQuotaTopUpById(topup1.getId()));
            assertNull(policyManager.getQuotaTopUpByName(topup1.getName()));
            assertNull(policyManager.getActiveQuotaTopUpById(topup1.getId()));
            assertNull(policyManager.getActiveQuotaTopUpByName(topup1.getName()));
            assertTrue(policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertTrue(policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
            DataTopUpData topup1 = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            topup1.setGroups(GROUP_ID);
            topup1.setId("foo");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopupDataList(topup1));
            policyManager.reload();
            DataTopUpData topup2 = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            topup2.setGroups(GROUP_ID);
            topup1.setId("bar");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopupDataList(topup2));

            policyManager.reloadQuotaTopUpsOfGroups(Arrays.asList(GROUP_ID));

            assertNull(policyManager.getQuotaTopUpById(topup1.getId()));
            assertNotNull(policyManager.getQuotaTopUpById(topup2.getId()));
            assertEquals(topup2.getId(), policyManager.getQuotaTopUpByName(topup2.getName()).getId());
            assertEquals(topup2.getId(), policyManager.getActiveQuotaTopUpByName(topup2.getName()).getId());
            assertEquals(1, policyManager.getActiveAllQuotaTopUpDatas().size());
            assertEquals(1, policyManager.getActiveLiveQuotaTopUpDatas().size());
            assertEquals(topup2.getId(), policyManager.getActiveAllQuotaTopUpDatas().get(0).getId());
            assertEquals(topup2.getId(), policyManager.getActiveLiveQuotaTopUpDatas().get(0).getId());
        }
    }

    public List<DataTopUpData> createDataTopupDataList(DataTopUpData data){
        List<DataTopUpData> dataTopUpData = new ArrayList<>();
        dataTopUpData.add(data);
        return dataTopUpData;
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        policyManager.stop();
    }

}
