package com.elitecore.corenetvertex.pm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jaidiptrivedi on 17/5/17.
 */
public class ReloadData {

    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private PkgData pkg;
    private PkgData basePkg1, basePkg2, basePkg3;
    private PkgData addOnPkg1, addOnPkg2, addOnPkg3;
    private RnCFactory rnCFactory = new RnCFactory();
    private RnCPackageFactory rnCPackageFactory = new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory));
    private DeploymentMode deploymentMode = DeploymentMode.PCRF;
    private DummyDataReader dummyDataReader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        packageFactory = new PackageFactory();
        generatePolicyBackUpFile();
        pkg = PkgDataBuilder.newBasePackageWithDefaultValues(true);
        policyManager = new PolicyManager();
        dummyDataReader = new DummyDataReader();
    }

    private void createBasePackageDatas(List<PkgData> pkgDatas){
        basePkg1 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
        basePkg1.setName("basePkg1");
        basePkg1.setId("BaseId1");
        basePkg2 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
        basePkg2.setName("basePkg2");
        basePkg2.setId("BaseId2");
        basePkg3 = PkgDataBuilder.newBasePackageWithDefaultValues(true);
        basePkg3.setName("basePkg3");
        basePkg3.setId("BaseId3");

        pkgDatas.add(basePkg1);
        pkgDatas.add(basePkg2);

    }

    private void createAddonPackageDatas(List<PkgData> pkgDatas){
        addOnPkg1 = PkgDataBuilder.newAddOnWithDefaultValues();
        addOnPkg1.setName("addOnPkg1");
        addOnPkg1.setId("AddonId1");
        addOnPkg2 = PkgDataBuilder.newAddOnWithDefaultValues();
        addOnPkg2.setName("addOnPkg2");
        addOnPkg2.setId("AddonId2");
        addOnPkg3 = PkgDataBuilder.newAddOnWithDefaultValues();
        addOnPkg3.setName("addOnPkg3");
        addOnPkg3.setId("AddonId3");

        pkgDatas.add(addOnPkg1);
        pkgDatas.add(addOnPkg2);
    }

    @Test
    public void storePackageWithSuccessStateWhenNewBasePackageFoundWithProperConfiguration() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
        policyManager.reloadDataPackages("basePkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(basePkg3.getId());

        assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());

        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + pkg.getId() + " should not be found in activeBasePackages", 3, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + pkg.getId() + " should not be found in activeAndLiveBasePackages", 3, policyManager.getActiveLiveBasePkgDatas().size());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));
    }

	private void initPolicyManager() throws InitializationFailedException {
		policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory, rnCPackageFactory, deploymentMode);
	}

	@Test
    public void retainPreviousPackageWhenNewBasePackageFound() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
		initPolicyManager();

        BasePackage pkg1BasePackage = policyManager.getActiveBasePackageById(basePkg1.getId());

        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);

        policyManager.reloadDataPackages("basePkg3");

        assertSame("Previous package with id: " + pkg1BasePackage.getId() + " should retain in activeBasePackageById", pkg1BasePackage, policyManager.getActiveBasePackageById(basePkg1.getId()));
        assertSame("Previous package with name: " + pkg1BasePackage.getName() + " should retain in activeBasePackageById", pkg1BasePackage, policyManager.getActiveBasePackageByName(basePkg1.getName()));
        assertSame("Previous package with id: " + pkg1BasePackage.getId() + " should retain in basePackageById", pkg1BasePackage, policyManager.getBasePackageDataById(basePkg1.getId()));
        assertSame("Previous package with name: " + pkg1BasePackage.getName() + " should retain in basePackageByName", pkg1BasePackage, policyManager.getBasePackageDataByName(basePkg1.getName()));

    }

    @Test
    public void storePackageWithFailureStateWhenNewBasePackageFoundWithFailuerConfiguration() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        List<QosProfileData> qosProfileDataList = basePkg3.getQosProfiles();
        qosProfileDataList.get(0).setAdvancedCondition("abc");

        pkgDatas = new ArrayList<>(pkgDatas);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("basePkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(basePkg3.getId());

        assertEquals(PolicyStatus.FAILURE, newPkgData.getStatus());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", 3, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", 3, policyManager.getActiveLiveBasePkgDatas().size());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));
    }

    @Test
    public void storePolicyWithLastKnownGoodState_WhenPolicyReloadFailAndPreviousInstanceOfBasePolicyIsStoreWithSuccessState() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
		initPolicyManager();

        List<QosProfileData> qosProfileDataList = basePkg3.getQosProfiles();
        qosProfileDataList.get(0).setAdvancedCondition("abc");
        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("basePkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(basePkg3.getId());

        assertEquals(PolicyStatus.LAST_KNOWN_GOOD, newPkgData.getStatus());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", 3, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", 3, policyManager.getActiveLiveBasePkgDatas().size());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));
    }

    @Test
    public void removePolicyWhenActiveBasePolicyStatusIsChangeToInactive() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        basePkg3.setStatus(PkgStatus.INACTIVE.name());
        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg1);
        pkgDatas.add(basePkg2);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("basePkg3");

        assertNull("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNull("Base package with name:" + basePkg3.getName() + " should not be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", 2, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", 2, policyManager.getActiveLiveBasePkgDatas().size());
        assertNull("Base package with id:" + basePkg3.getId() + " should not be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNull("Base package with name:" + basePkg3.getName() + " should not be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));
    }

    @Test
    public void storePolicyWithSuccessStateWhenInactiveBasePolicyIsChangeToActive() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        basePkg3.setStatus(PkgStatus.INACTIVE.name());
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        basePkg3.setStatus(PkgStatus.ACTIVE.name());
        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
        policyManager.reloadDataPackages("basePkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(basePkg3.getId());

        assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", 3, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", 3, policyManager.getActiveLiveBasePkgDatas().size());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));
    }

    @Test
    public void removePolicyWhenBasePolicyStatusIsChangedToDeleted() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        basePkg3.setStatus(CommonConstants.STATUS_DELETED);
        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("basePkg3");

        assertNull("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNull("Base package with name:" + basePkg3.getName() + " should not be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", 2, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", 2, policyManager.getActiveLiveBasePkgDatas().size());
        assertNull("Base package with id:" + basePkg3.getId() + " should not be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNull("Base package with name:" + basePkg3.getName() + " should not be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));
    }

    @Test
    public void removePolicyWhenActiveBasePolicyStatusIsChangeToRetired() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createBasePackageDatas(pkgDatas);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        basePkg3.setStatus(PkgStatus.RETIRED.name());
        pkgDatas = new ArrayList<>();
        pkgDatas.add(basePkg1);
        pkgDatas.add(basePkg2);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("basePkg3");
        BasePackage pkg3BasePackage = policyManager.getActiveBasePackageById(basePkg3.getId());

        assertNull("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg3.getId()));
        assertNull("Base package with name:" + basePkg3.getName() + " should not be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg3.getName()));
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", 2, policyManager.getActiveAllBasePkgDatas().size());
        assertEquals("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", 2, policyManager.getActiveLiveBasePkgDatas().size());
        assertNotNull("Base package with id:" + basePkg3.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg3.getId()));
        assertNotNull("Base package with name:" + basePkg3.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg3.getName()));

        assertFalse("Base package with id:" + basePkg3.getId() + " should not be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().contains(pkg3BasePackage));
        assertFalse("Base package with id:" + basePkg3.getId() + " should not be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().contains(pkg3BasePackage));
    }

    @Test
    public void storePackageWithSuccessStateWhenNewAddOnPackageFoundWithProperConfiguration() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        pkgDatas = new ArrayList<>(pkgDatas);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
        policyManager.reloadDataPackages("addOnPkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg3.getId());

        assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());

        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + pkg.getId() + " should not be found in activeAddOns", 3, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + pkg.getId() + " should not be found in activeAndLiveAddOns", 3, policyManager.getActiveLiveAddOnDatas().size());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());

    }


    @Test
    public void retainPreviousPackageWhenNewAddOnPackageFound() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        AddOn pkg1AddOnPackage = policyManager.getActiveAddOnById(addOnPkg1.getId());

        pkgDatas = new ArrayList<>(pkgDatas);
        pkgDatas.add(basePkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("addOnPkg3");

        assertSame("Previous package with id: " + pkg1AddOnPackage.getId() + " should retain in activeAddOnById", pkg1AddOnPackage, policyManager.getActiveAddOnById(addOnPkg1.getId()));
        assertSame("Previous package with name: " + pkg1AddOnPackage.getName() + " should retain in activeAddOnById", pkg1AddOnPackage, policyManager.getActiveAddOnByName(addOnPkg1.getName()));
        assertSame("Previous package with id: " + pkg1AddOnPackage.getId() + " should retain in addOnPackageById", pkg1AddOnPackage, policyManager.getAddOnById(addOnPkg1.getId()));
        assertSame("Previous package with name: " + pkg1AddOnPackage.getName() + " should retain in addOnPackageByName", pkg1AddOnPackage, policyManager.getAddOnByName(addOnPkg1.getName()));

    }

    @Test
    public void storePackageWithFailureStateWhenNewAddOnPackageFoundWithFailuerConfiguration() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        List<QosProfileData> qosProfileDataList = addOnPkg3.getQosProfiles();
        qosProfileDataList.get(0).setAdvancedCondition("abc");

        pkgDatas = new ArrayList<>();
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("addOnPkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg3.getId());

        assertEquals(PolicyStatus.FAILURE, newPkgData.getStatus());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", 3, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", 3, policyManager.getActiveLiveAddOnDatas().size());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());

    }

    @Test
    public void storePolicyWithLastKnownGoodState_WhenPolicyReloadFailAndPreviousInstanceOfAddOnPolicyIsStoreWithSuccessState() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
		initPolicyManager();

        List<QosProfileData> qosProfileDataList = addOnPkg3.getQosProfiles();
        qosProfileDataList.get(0).setAdvancedCondition("abc");

        pkgDatas = new ArrayList<>();
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("addOnPkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg3.getId());

        assertEquals(PolicyStatus.LAST_KNOWN_GOOD, newPkgData.getStatus());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", 3, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", 3, policyManager.getActiveLiveAddOnDatas().size());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());

    }

    @Test
    public void removePolicyWhenActiveAddOnPolicyStatusIsChangeToInactive() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

        addOnPkg3.setStatus(PkgStatus.INACTIVE.name());
        pkgDatas = new ArrayList<>();
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("addOnPkg3");

        assertNull("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNull("AddOn package with name:" + addOnPkg3.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", 2, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", 2, policyManager.getActiveLiveAddOnDatas().size());
        assertNull("AddOn package with id:" + addOnPkg3.getId() + " should not be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNull("AddOn package with name:" + addOnPkg3.getName() + " should not be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

//        assertNull("Base package with name:" + addOnPkg3.getId() + " should not be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNull("Base package with name:" + addOnPkg3.getName() + " should not be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNull("Base package with name:" + addOnPkg3.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNull("Base package with name:" + addOnPkg3.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertEquals("Base package with name:" + addOnPkg3.getName() + " should not be found ", 2, policyManager.getActiveAllAddOnOrTopUpPkgDatas().size());

    }

    @Test
    public void storePolicyWithSuccessStateWhenInactiveAddOnPolicyIsChangeToActive() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        pkg.setStatus(PkgStatus.INACTIVE.name());
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

		addOnPkg3.setStatus(PkgStatus.ACTIVE.name());
        pkgDatas = new ArrayList<>();
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
        policyManager.reloadDataPackages("addOnPkg3");

        UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg3.getId());

        assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", 3, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", 3, policyManager.getActiveLiveAddOnDatas().size());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());

    }

    @Test
    public void removePolicyWhenAddOnPolicyStatusIsChangedToDeleted() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

		addOnPkg3.setStatus(CommonConstants.STATUS_DELETED);
        pkgDatas = new ArrayList<>();
        pkgDatas.add(addOnPkg1);
        pkgDatas.add(addOnPkg2);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("addOnPkg3");

        assertNull("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNull("AddOn package with name:" + addOnPkg3.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", 2, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", 2, policyManager.getActiveLiveAddOnDatas().size());
        assertNull("AddOn package with id:" + addOnPkg3.getId() + " should not be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNull("AddOn package with name:" + addOnPkg3.getName() + " should not be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

//        assertNull("Base package with name:" + addOnPkg3.getId() + " should not be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNull("Base package with name:" + addOnPkg3.getName() + " should not be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNull("Base package with name:" + addOnPkg3.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNull("Base package with name:" + addOnPkg3.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertEquals("Base package with name:" + addOnPkg3.getName() + " should not be found ", 2, policyManager.getActiveAllAddOnOrTopUpPkgDatas().size());
    }

    @Test
    public void removePolicyWhenActiveAddOnPolicyStatusIsChangeToRetired() throws InitializationFailedException {

        List<PkgData> pkgDatas = new ArrayList<>();
        createAddonPackageDatas(pkgDatas);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
		initPolicyManager();

		addOnPkg3.setStatus(PkgStatus.RETIRED.name());
        pkgDatas = new ArrayList<>();
        pkgDatas.add(addOnPkg1);
        pkgDatas.add(addOnPkg2);
        pkgDatas.add(addOnPkg3);
        dummyDataReader.setReadList(PkgData.class, pkgDatas);
        policyManager.reloadDataPackages("addOnPkg3");
        AddOn pkg3AddOnPackage = policyManager.getActiveAddOnById(addOnPkg3.getId());

        assertNull("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg3.getId()));
        assertNull("AddOn package with name:" + addOnPkg3.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg3.getName()));
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", 2, policyManager.getActiveAllAddOnPkgDatas(false).size());
        assertEquals("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", 2, policyManager.getActiveLiveAddOnDatas().size());
        assertNotNull("AddOn package with id:" + addOnPkg3.getId() + " should be found in addOnPackageById", policyManager.getAddOnById(addOnPkg3.getId()));
        assertNotNull("AddOn package with name:" + addOnPkg3.getName() + " should be found in addOnPackageByName", policyManager.getAddOnByName(addOnPkg3.getName()));

        assertFalse("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false).contains(pkg3AddOnPackage));
        assertFalse("AddOn package with id:" + addOnPkg3.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().contains(pkg3AddOnPackage));

//        assertNotNull("Base package with name:" + addOnPkg3.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNotNull("Base package with name:" + addOnPkg3.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertNull("Base package with name:" + addOnPkg3.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg3.getId()));
//        assertNull("Base package with name:" + addOnPkg3.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg3.getName()));
//        assertEquals("Base package with name:" + addOnPkg3.getName() + " should not be found ", 2, policyManager.getActiveAllAddOnOrTopUpPkgDatas().size());
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        hibernateSessionFactory.shutdown();
    }
}
