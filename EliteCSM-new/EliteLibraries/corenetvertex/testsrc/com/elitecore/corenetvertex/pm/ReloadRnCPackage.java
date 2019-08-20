package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.pm.util.RnCPackagePredicates;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReloadRnCPackage {
    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private ArrayList<String> groupLists = new ArrayList<>();
    private RnCFactory rnCFactory = new RnCFactory();
    private RnCPackageFactory rnCPackageFactory = new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory), rnCFactory), rnCFactory), rnCFactory, new ThresholdNotificationSchemeFactory(rnCFactory));
    private DeploymentMode deploymentMode = DeploymentMode.PCC;
    private DummyDataReader dummyDataReader;
    private String currency="INR";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        generatePolicyBackUpFile();
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        packageFactory = new PackageFactory();
        policyManager = new PolicyManager();
        groupLists.clear();
        dummyDataReader = new DummyDataReader();
    }

    public class WhenTableEmpty {
        @Test
        public void getAllRnCPackageReturnsEmptyList() throws InitializationFailedException {
            initPolicyManager(dummyDataReader);
            assertEquals(0, policyManager.getService().all().size());
        }

        @Test
        public void byIdReturnsNullValue() throws InitializationFailedException {
            initPolicyManager(dummyDataReader);
            assertNull(policyManager.getRnCPackage().byId("empty"));
        }

        @Test
        public void byNameReturnsNullValue() throws InitializationFailedException {
            initPolicyManager(dummyDataReader);
            assertNull(policyManager.getRnCPackage().byName("invalid"));
        }

        @Test
        public void baseByIdReturnsNullValue() throws InitializationFailedException {
            initPolicyManager(dummyDataReader);
            assertNull(policyManager.getRnCPackage().baseById("empty"));
        }

        @Test
        public void baseByNameReturnsNullValue() throws InitializationFailedException {
            initPolicyManager(dummyDataReader);
            assertNull(policyManager.getRnCPackage().baseByName("invalid"));
        }
    }

    public class InitWhenTableNotEmpty {
        @Test
        public void allReturnsSameListAsStoredInDB() throws InitializationFailedException {
            RncPackageData rncPackageData = createRnCpackageData("1");
            dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
            initPolicyManager(dummyDataReader);

            ArrayList<RnCPackage> list = new ArrayList<>(2);
            list.add(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE));

            ReflectionAssert.assertLenientEquals(list,
                    policyManager.getRnCPackage().all());
        }

        @Test
        public void byIdReturnsSameObjectAsStoredInDB() throws InitializationFailedException {
            RncPackageData rncPackageData = createRnCpackageData("1");
            dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
            initPolicyManager(dummyDataReader);

            ArrayList<RnCPackage> list = new ArrayList<>(2);
            list.add(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE));

            ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE),
                    policyManager.getRnCPackage().byId(rncPackageData.getId()));
        }

        @Test
        public void byNameReturnsSameObjectAsStoredInDB() throws InitializationFailedException {
            RncPackageData rncPackageData = createRnCpackageData("1");
            dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
            initPolicyManager(dummyDataReader);

            ArrayList<RnCPackage> list = new ArrayList<>(2);
            list.add(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE));

            ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE),
                    policyManager.getRnCPackage().byName(rncPackageData.getName()));
        }

        @Test
        public void testsItDoesNotCacheDesignOffers() throws InitializationFailedException {
            RncPackageData rncPackageData = createRnCpackageData("1");
            rncPackageData.setMode(PkgMode.DESIGN.name());
            dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
            initPolicyManager(dummyDataReader);

            Assert.assertNull(policyManager.getRnCPackage().byId(rncPackageData.getId()));
        }

        @Test
        public void testsItDoesNotCacheDeletedOffers() throws InitializationFailedException {
            RncPackageData rncPackageData = createRnCpackageData("1");
            rncPackageData.setStatus(CommonConstants.STATUS_DELETED);
            dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
            initPolicyManager(dummyDataReader);

            Assert.assertNull(policyManager.getRnCPackage().byId(rncPackageData.getId()));
        }

        @Test
        public void testsItDoesNotCacheInactiveOffers() throws InitializationFailedException {
            RncPackageData rncPackageData = createRnCpackageData("1");
            rncPackageData.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
            initPolicyManager(dummyDataReader);

            Assert.assertNull(policyManager.getRnCPackage().byId(rncPackageData.getId()));
        }
    }

    public class Reload {

        public class DataDeleted {
            @Test
            public void allReturnsEmptyLit() throws InitializationFailedException {
                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                dummyDataReader.setReadList(RncPackageData.class, new ArrayList());

                policyManager.reload();

                Assert.assertEquals(0, policyManager.getRnCPackage().all().size());
            }

            @Test
            public void byIdReturnsNullForOldId() throws InitializationFailedException {

                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                dummyDataReader.setReadList(RncPackageData.class, new ArrayList());

                policyManager.reload();

                Assert.assertNull(policyManager.getRnCPackage().byId(rncPackageData.getId()));
            }

            @Test
            public void byNameReturnsNullForOldName() throws InitializationFailedException {

                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                dummyDataReader.setReadList(RncPackageData.class, new ArrayList());

                policyManager.reload();

                Assert.assertNull(policyManager.getRnCPackage().byName(rncPackageData.getName()));
            }
        }

        public class NewDataAdded {
            @Test
            public void allReturnsNewValuesFromDatabase() throws InitializationFailedException {
                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                rncPackageData = createRnCpackageData("2");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                ArrayList<RnCPackage> list = new ArrayList<>(2);
                list.add(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE));

                ReflectionAssert.assertLenientEquals(list,
                        policyManager.getRnCPackage().all());
            }

            @Test
            public void byNameReturnsSameObjectAsStoredInDB() throws InitializationFailedException {
                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                rncPackageData = createRnCpackageData("2");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                policyManager.reload();

                ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE),
                        policyManager.getRnCPackage().byName(rncPackageData.getName()));
            }

            @Test
            public void byIdReturnsSameObjectAsStoredInDB() throws InitializationFailedException {
                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                rncPackageData = createRnCpackageData("2");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                policyManager.reload();

                ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE),
                        policyManager.getRnCPackage().byId(rncPackageData.getId()));
            }
        }

        public class ForGroups {

            @Test
            public void testsItDoesNotReloadDataForOtherGroups() throws InitializationFailedException {
                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                rncPackageData = createRnCpackageData("2");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                policyManager.reloadRnCPackages(RnCPackagePredicates.createGroupFilter(Arrays.asList(new String[]{"Group_22"})));

                Assert.assertNull(policyManager.getRnCPackage().byId(rncPackageData.getId()));
            }

            @Test
            public void testsItDoesUpdateRnCPackageBelongingToTheSameGroup() throws InitializationFailedException {
                RncPackageData rncPackageData = createRnCpackageData("1");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                initPolicyManager(dummyDataReader);

                rncPackageData = createRnCpackageData("1");
                rncPackageData.setName("foo");
                dummyDataReader.setReadList(RncPackageData.class, createRnCDataList(rncPackageData));
                policyManager.reloadRnCPackages(RnCPackagePredicates.createGroupFilter(Arrays.asList(new String[]{"GROUP_1"})));

                ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(rncPackageData, PolicyStatus.FAILURE),
                        policyManager.getRnCPackage().byId(rncPackageData.getId()));
            }

        }
    }

    private void initPolicyManager(DummyDataReader dummyDataReader) throws InitializationFailedException {
        policyManager.init(folder.getRoot().getAbsolutePath(), sessionFactory, dummyDataReader, packageFactory, rnCPackageFactory, deploymentMode);
    }

    private RncPackageData createRnCpackageData(String id) {
        RncPackageData rncPackageData = new RncPackageData();
        rncPackageData.setId("id" + id);
        rncPackageData.setName("name" + id);
        rncPackageData.setType(PkgType.BASE.name());
        rncPackageData.setStatus(PkgStatus.ACTIVE.name());
        rncPackageData.setMode(PkgMode.LIVE.name());
        rncPackageData.setGroups("GROUP_1");
        rncPackageData.setChargingType(ChargingType.SESSION.name());
        rncPackageData.setCurrency(currency);

        return rncPackageData;
    }

    private RnCPackage createExpectedRnCPackage(RncPackageData rncPackageData, PolicyStatus policyStatus) {
        RnCPackage rnCPackage = new RnCPackage(rncPackageData.getId(), rncPackageData.getName(), rncPackageData.getDescription(),
                Arrays.asList(rncPackageData.getGroups().split(",")), new ArrayList<>(), null,
                rncPackageData.getTag(), RnCPkgType.fromName(rncPackageData.getType()),
                PkgMode.getMode(rncPackageData.getMode()), PkgStatus.fromVal(rncPackageData.getStatus()), policyStatus,
                null, null, ChargingType.SESSION,currency);

        return rnCPackage;
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        folder.delete();
    }

    private List<RncPackageData> createRnCDataList(RncPackageData rncPackageData) {
        List<RncPackageData> list = new ArrayList<>(2);
        list.add(rncPackageData);
        return list;
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "rnc_packages.bkp");
        file.createNewFile();
    }

}
