package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.bod.BoDFactory;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.bod.BoDPackageFactory;
import com.elitecore.corenetvertex.pm.bod.BodDataDummyBuilder;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elitecore.corenetvertex.pm.util.BoDDataPredicates.createGroupFilter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class BoDPackagesTest {

    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private ArrayList<String> groupLists = new ArrayList<>();
    private RnCFactory rnCFactory;
    private RnCPackageFactory rnCPackageFactory;
    private int counter;
    private DeploymentMode deploymentMode;
    private BoDPackageFactory boDPackageFactory;
    private File backupFile;

    private static final String BOD_PKG_BKP_FILE_NAME = "bod_packages.bkp";
    private DummyDataReader dummyDataReader;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        counter=0;
        deploymentMode = DeploymentMode.PCC;
        backupFile = generateBoDBackUpFile();
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        this.packageFactory = new PackageFactory();
        this.rnCFactory = new RnCFactory();
        this.rnCPackageFactory = new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory)
                ,rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory));

        this.boDPackageFactory = new BoDPackageFactory(new BoDFactory(), deploymentMode);
        this.policyManager = new PolicyManager();
        this.groupLists.clear();
        dummyDataReader = new DummyDataReader();
    }

    public class BodPackageInit {
        public class WhenBoDNotConfigured {
            @Test
            public void getAllBoDPackageReturnsEmptyList() throws InitializationFailedException {
                initPolicyManager();
                assertEquals(0,policyManager.getBoDPackage().all().size());
            }

            @Test
            public void byIdReturnsNullValue() throws InitializationFailedException{
                initPolicyManager();
                assertNull(policyManager.getBoDPackage().byId("empty"));
            }

            @Test
            public void byNameReturnsNullValue() throws InitializationFailedException{
                initPolicyManager();
                assertNull(policyManager.getBoDPackage().byName("invalid"));
            }

            @Test
            public void bodBackupFileShouldBeEmpty() throws IOException {
                assertEquals(0, backupFile.length());
            }
        }

        public class WhenBoDConfigured {

            private BoDData bodData;

            @Before
            public void setup(){
                bodData = createBoDPackageData();
            }

            @Test
            public void allReturnsSameListAsStoredInDB() throws InitializationFailedException{
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                ArrayList<BoDPackage> list = new ArrayList<>(2);
                list.add(createExpectedBoDPackage(bodData));

                ReflectionAssert.assertLenientEquals(list,
                        policyManager.getBoDPackage().all());
            }

            @Test
            public void byIdReturnsSameObjectAsStoredInDB() throws InitializationFailedException{
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                ArrayList<BoDPackage> list = new ArrayList<>(2);
                list.add(createExpectedBoDPackage(bodData));

                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData),
                        policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void byNameReturnsSameObjectAsStoredInDB() throws InitializationFailedException{
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                ArrayList<BoDPackage> list = new ArrayList<>(2);
                list.add(createExpectedBoDPackage(bodData));

                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData),
                        policyManager.getBoDPackage().byName(bodData.getName()));
            }

            @Test
            public void doesNotCacheDesignBoDs() throws InitializationFailedException{
                bodData.setPackageMode(PkgMode.DESIGN.name());
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void doesNotCacheInactiveBoDs() throws InitializationFailedException{
                bodData.setStatus(PkgStatus.INACTIVE.name());
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void returnFailureWhenWrongBoDConfigured() throws InitializationFailedException{
                bodData.setBodQosMultiplierDatas(null);
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertEquals(PolicyStatus.FAILURE, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());
            }

            @Test
            public void bodBackupFileSizeIncreaseAfterNewBoDConfiguration() throws InitializationFailedException{
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                long previousFileSize = backupFile.length();
                initPolicyManager();
                long currentFileSize = backupFile.length();
                assertTrue(currentFileSize > previousFileSize);
            }

        }

        public class AfterUpdateBodAndRestartWithoutPolicyReload {

            private BoDData bodData;

            @Before
            public void setup(){
                bodData = createBoDPackageData();
            }

            @Test
            public void lastKnownGoodIfUpdatedCorrectBoDWrongly() throws InitializationFailedException {
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertEquals(PolicyStatus.SUCCESS, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());

                BoDPackage expectedLastKnownBoDPackage = createExpectedLastKnownBoDPackage(bodData);

                counter=0;
                bodData = createBoDPackageData();
                bodData.setBodQosMultiplierDatas(null);
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));

                initPolicyManager();

                assertEquals(PolicyStatus.LAST_KNOWN_GOOD, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());
                ReflectionAssert.assertLenientEquals(expectedLastKnownBoDPackage, policyManager.getBoDPackage().byName(bodData.getName()));
            }

            @Test
            public void successIfUpdatedFailedBoDCorrectly() throws InitializationFailedException {
                bodData.setBodQosMultiplierDatas(null);
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertEquals(PolicyStatus.FAILURE, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());

                counter=0;
                bodData = createBoDPackageData();
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));

                initPolicyManager();

                assertEquals(PolicyStatus.SUCCESS, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());
                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData), policyManager.getBoDPackage().byName(bodData.getName()));
            }

            @Test
            public void remainSuccessIfUpdatedCorrectly() throws InitializationFailedException {
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertEquals(PolicyStatus.SUCCESS, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());

                counter=0;
                bodData = createBoDPackageData();
                bodData.setValidityPeriod(120);
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));

                initPolicyManager();

                assertEquals(PolicyStatus.SUCCESS, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());
                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData), policyManager.getBoDPackage().byName(bodData.getName()));
            }

            @Test
            public void doesNotCacheIfUpdateBoDtoInactive() throws InitializationFailedException{
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertEquals(PolicyStatus.SUCCESS, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());

                counter=0;
                bodData = createBoDPackageData();
                bodData.setStatus(PkgStatus.INACTIVE.name());
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void doesNotCacheIfBoDdeleted() throws InitializationFailedException{
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                initPolicyManager();

                assertEquals(PolicyStatus.SUCCESS, policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());

                dummyDataReader.setReadList(BoDData.class,new ArrayList());
                initPolicyManager();

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }


            private BoDPackage createExpectedLastKnownBoDPackage(BoDData boDData){
                BoDPackage lastKnownBodPackage = boDPackageFactory.create(boDData);
                lastKnownBodPackage.setFailReason("BoD package parsing failed. Reason: [No BoD QoS Profile Multiplier Found]");
                lastKnownBodPackage.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
                return lastKnownBodPackage;
            }
        }

    }


    public class Reload{
        private BoDData bodData;
        private long initialFileSize;

        @Before
        public void setUp() throws InitializationFailedException {
            initialFileSize = backupFile.length();
            bodData = createBoDPackageData();
            dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
            initPolicyManager();
        }
        public class WhenDataDeleted {

            @Test
            public void allReturnsEmptyList() throws InitializationFailedException {
                dummyDataReader.setReadList(BoDData.class,new ArrayList());
                policyManager.reloadBoDPackages(boDData -> true);

                Assert.assertEquals(0,policyManager.getBoDPackage().all().size());
            }

            @Test
            public void byIdReturnsNullForOldId() throws InitializationFailedException {
                dummyDataReader.setReadList(BoDData.class,new ArrayList());
                policyManager.reloadBoDPackages(boDData -> true);

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void byNameReturnsNullForOldName() throws InitializationFailedException {
                dummyDataReader.setReadList(BoDData.class,new ArrayList());
                policyManager.reloadBoDPackages(boDData -> true);

                assertNull(policyManager.getBoDPackage().byName(bodData.getName()));
            }

            @Test
            public void bodBackupFileSizeWillDecrease() throws InitializationFailedException{
                long previousFileSize = backupFile.length();
                dummyDataReader.setReadList(BoDData.class,new ArrayList());
                policyManager.reloadBoDPackages(boDData -> true);

                long currentFileSize = backupFile.length();
                assertTrue(currentFileSize < previousFileSize);
            }
        }

        public class WhenNewDataAdded {
            @Test
            public void allReturnsNewValuesFromDatabase() throws InitializationFailedException {
                bodData = createBoDPackageData();
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(boDData -> true);

                ArrayList<BoDPackage> list = new ArrayList<>(2);
                list.add(createExpectedBoDPackage(bodData));

                ReflectionAssert.assertLenientEquals(list, policyManager.getBoDPackage().all());
            }

            @Test
            public void byNameReturnsSameObjectAsStoredInDB() throws InitializationFailedException {
                bodData = createBoDPackageData();
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(boDData -> true);

                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData),
                        policyManager.getBoDPackage().byName(bodData.getName()));}

            @Test
            public void byIdReturnsSameObjectAsStoredInDB() throws InitializationFailedException {
                bodData = createBoDPackageData();
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(boDData -> true);

                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData),
                        policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void bodBackupFileSizeWillIncrease() throws InitializationFailedException{
                policyManager.reloadBoDPackages(boDData -> true);

                long currentFileSize = backupFile.length();
                assertTrue(currentFileSize > initialFileSize);
            }
        }


        public class WhenDataUpdated {
            @Test
            public void returnSameBoDAsUpdated(){
                counter=0;
                bodData = createBoDPackageData();
                bodData.setValidityPeriod(90);
                bodData.setDescription("Updated Description");

                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(boDData -> true);

                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData),
                        policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void doesNotCacheWhenExistingBoDInactivated(){
                counter=0;
                bodData = createBoDPackageData();
                bodData.setStatus(PkgStatus.INACTIVE.name());

                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(boDData -> true);

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void statusLastKnownGoodWhenWronglyUpdated(){
                counter=0;
                bodData = createBoDPackageData();
                bodData.setBodQosMultiplierDatas(null);

                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(boDData -> true);

                assertEquals(PolicyStatus.LAST_KNOWN_GOOD,
                        policyManager.getBoDPackage().byId(bodData.getId()).getPolicyStatus());
            }
        }


        public class CheckForPolicyCacheDetails{
            private PolicyCacheDetail policyCacheDetail;
            BoDData successBoDData, failedBoDData, lastKnownGoodBoDData;

            @Before
            public void setup(){
                successBoDData = createBoDPackageData();
                failedBoDData = createBoDPackageData();
                failedBoDData.setBodQosMultiplierDatas(null);

                lastKnownGoodBoDData = createBoDPackageData();

                List<BoDData> boDDatas = Collectionz.newArrayList();
                boDDatas.add(successBoDData);
                boDDatas.add(failedBoDData);
                boDDatas.add(lastKnownGoodBoDData);

                dummyDataReader.setReadList(BoDData.class,boDDatas);
                policyManager.reloadBoDPackages(boDData -> true);


                counter--;
                lastKnownGoodBoDData = createBoDPackageData();
                lastKnownGoodBoDData.setBodQosMultiplierDatas(null);

                dummyDataReader.setReadList(BoDData.class,createBoDDataList(successBoDData,failedBoDData,lastKnownGoodBoDData));
                policyCacheDetail = policyManager.reloadBoDPackages(boDData -> true);

            }


            @Test
            public void policyDetailCountersWillUpdateCorrectly(){
                assertEquals(1, policyCacheDetail.getSuccessCounter());
                assertEquals(1, policyCacheDetail.getFailureCounter());
                assertEquals(1, policyCacheDetail.getLastKnownGoodCounter());
            }

            @Test
            public void policyDetailListWillUpdateCorrectly(){
                assertEquals(policyManager.getBoDPackage().byId(successBoDData.getId()).getPolicyStatus(),
                        policyCacheDetail.getSuccessPolicyList().get(0).getStatus());

                assertEquals(policyManager.getBoDPackage().byId(failedBoDData.getId()).getPolicyStatus(),
                        policyCacheDetail.getFailurePolicyList().get(0).getStatus());

                assertEquals(policyManager.getBoDPackage().byId(lastKnownGoodBoDData.getId()).getPolicyStatus(),
                        policyCacheDetail.getLastKnownGoodPolicyList().get(0).getStatus());
            }
        }

        public class ForGroups{

            @Test
            public void doesNotReloadDataForOtherGroups() throws InitializationFailedException {
                bodData = createBoDPackageData();
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(createGroupFilter(Arrays.asList(new String[]{"Group_12"})));

                assertNull(policyManager.getBoDPackage().byId(bodData.getId()));
            }

            @Test
            public void doesUpdateBoDBelongingToTheSameGroup() throws InitializationFailedException {
                counter=0;
                bodData = createBoDPackageData();
                dummyDataReader.setReadList(BoDData.class,createBoDDataList(bodData));
                policyManager.reloadBoDPackages(createGroupFilter(Arrays.asList(new String[]{"GROUP_1"})));

                ReflectionAssert.assertLenientEquals(createExpectedBoDPackage(bodData),
                        policyManager.getBoDPackage().byId(bodData.getId()));
            }

        }
    }


    private void initPolicyManager() throws InitializationFailedException {
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory()
                , dummyDataReader, packageFactory, rnCPackageFactory, deploymentMode);
    }

    private BoDData createBoDPackageData(){
        counter++;
        return BodDataDummyBuilder.createBoDDataWithAllValues(String.valueOf(counter));
    }

    private BoDPackage createExpectedBoDPackage(BoDData boDData){
        return boDPackageFactory.create(boDData);
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        folder.delete();
    }

    private List<BoDData> createBoDDataList(BoDData... boDData){
        List<BoDData> list = new ArrayList<>(2);
        for(BoDData data: boDData){
            list.add(data);
        }
        return list;
    }

    private File generateBoDBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), BOD_PKG_BKP_FILE_NAME);
        file.createNewFile();
        return file;
    }
}