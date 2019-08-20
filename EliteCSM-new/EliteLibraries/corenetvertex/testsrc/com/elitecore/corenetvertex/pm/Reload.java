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
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class will provide test cases for policy manager
 * @author ishani.bhatt
 */
@RunWith(HierarchicalContextRunner.class)
public class Reload {

    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private RnCFactory rnCFactory;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
	private DeploymentMode deploymentMode = DeploymentMode.PCRF;
    private DummyDataReader dummyDataReader;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        packageFactory = new PackageFactory();
        generatePolicyBackUpFile();
        policyManager = new PolicyManager();
        rnCFactory = new RnCFactory();
        dummyDataReader = new DummyDataReader();
	}

	private void initPolicyManager(RnCFactory rnCFactory) throws InitializationFailedException {
		policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
	}
    private void initPolicyManager(RnCFactory rnCFactory,DeploymentMode deploymentMode) throws InitializationFailedException {
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
    }

	public class BasePackageReload {

        private PkgData basePkg;

        @Before
        public void setUp() throws Exception {
            basePkg = PkgDataBuilder.newBasePackageWithDefaultValues(true);
            initPolicyManager(rnCFactory);
        }

        @Test
        public void successWhenServiceTypeConfiguredWithoutRatingGroup() {
            RatingGroupData ratingGroupData = PkgDataBuilder.createRatingGroupData();
            dummyDataReader.setReadList(RatingGroupData.class, createRatingGroupDataList(ratingGroupData));
            PCCRuleData pccRule = basePkg.getQosProfiles().get(0).getQosProfileDetailDataList().get(0).getPccRules().get(0);
            pccRule.setChargingKey(ratingGroupData.getId());
            pccRule.setDataServiceTypeData(createAndGetServiceTypeWithoutRatingGroup());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            PolicyCacheDetail actual = policyManager.reload();
            assertEquals(PolicyStatus.SUCCESS, actual.getStatus());
            assertEquals(1, actual.getSuccessCounter());
        }

        @Test
        public void successWhenServiceTypeConfiguredWithRatingGroup() {
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail actual = policyManager.reload();
            assertEquals(PolicyStatus.SUCCESS, actual.getStatus());
            assertEquals(1, actual.getSuccessCounter());
        }

        @Test
        public void storePackageWithSuccessStateWhenNewBasePackageFoundWithProperConfiguration() throws InitializationFailedException {
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(basePkg.getId());

            assertEquals(PolicyStatus.SUCCESS,newPkgData.getStatus());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        @Test
        public void storePackageWithFailureStateWhenNewBasePackageFoundWithFailuerConfiguration() throws InitializationFailedException {
            List<QosProfileData> qosProfileDataList =  basePkg.getQosProfiles();
            qosProfileDataList.get(0).setAdvancedCondition("abc");

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(basePkg.getId());

            assertEquals(PolicyStatus.FAILURE,newPkgData.getStatus());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        @Test
        public void storePolicyWithLastKnownGoodState_WhenBasePolicyReloadFailAndPreviousInstanceOfPolicyIsStoreWithSuccessState() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            List<QosProfileData> qosProfileDataList =  basePkg.getQosProfiles();
            qosProfileDataList.get(0).setAdvancedCondition("abc");
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(basePkg.getId());

            assertEquals(PolicyStatus.LAST_KNOWN_GOOD,newPkgData.getStatus());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        @Test
        public void removePolicyWhenActiveBasePolicyStatusIsChangeToInactive() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            basePkg.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            assertNull("Base package with id:" + basePkg.getId() + " should not be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNull("Base package with name:" + basePkg.getName() + " should not be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertTrue("Base package with id:" + basePkg.getId() + " should not be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertTrue("Base package with id:" + basePkg.getId() + " should not be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNull("Base package with id:" + basePkg.getId() + " should not be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNull("Base package with name:" + basePkg.getName() + " should not be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        @Test
        public void storePolicyWithSuccessStateWhenInactiveBasePolicyIsChangeToActive() throws InitializationFailedException {

            basePkg.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            basePkg.setStatus(PkgStatus.ACTIVE.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(basePkg.getId());

            assertEquals(PolicyStatus.SUCCESS,newPkgData.getStatus());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertFalse("Base package with id:" + basePkg.getId() + " should be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        @Test
        public void removePolicyWhenBasePolicyStatusIsChangedToDeleted() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            basePkg.setStatus(CommonConstants.STATUS_DELETED);
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            assertNull("Base package with id:" + basePkg.getId() + " should not be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNull("Base package with name:" + basePkg.getName() + " should not be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertTrue("Base package with id:" + basePkg.getId() + " should not be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertTrue("Base package with id:" + basePkg.getId() + " should not be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNull("Base package with id:" + basePkg.getId() + " should not be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNull("Base package with name:" + basePkg.getName() + " should not be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        @Test
        public void removePolicyWhenActiveBasePolicyStatusIsChangeToRetired() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            basePkg.setStatus(PkgStatus.RETIRED.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            policyManager.reload();

            assertNull("Base package with id:" + basePkg.getId() + " should not be found in activeBasePackageById", policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNull("Base package with name:" + basePkg.getName() + " should not be found in activeBasePackageByName", policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertTrue("Base package with id:" + basePkg.getId() + " should not be found in activeBasePackages", policyManager.getActiveAllBasePkgDatas().isEmpty());
            assertTrue("Base package with id:" + basePkg.getId() + " should not be found in activeAndLiveBasePackages", policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertNotNull("Base package with id:" + basePkg.getId() + " should be found in basePackageById", policyManager.getBasePackageDataById(basePkg.getId()));
            assertNotNull("Base package with name:" + basePkg.getName() + " should be found in basePackageByName", policyManager.getBasePackageDataByName(basePkg.getName()));
        }

        private DataServiceTypeData createAndGetServiceTypeWithoutRatingGroup() {
            DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
            dataServiceTypeData.setGroups("1");
            dataServiceTypeData.setName("ServiceTypeTest");
            dataServiceTypeData.setServiceIdentifier(1L);
            return dataServiceTypeData;
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() throws InitializationFailedException {
            String oldPackageId = basePkg.getId();
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            basePkg = PkgDataBuilder.newBasePackageWithDefaultValues(true);
            basePkg.setId("bsPkg4");
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail afterReloadStatus = policyManager.reload();

            assertSame(1, afterReloadStatus.getSuccessCounter());
            assertNull(policyManager.getPkgDataById(oldPackageId));
            assertNull(policyManager.getBasePackageDataById(oldPackageId));
            assertNull(policyManager.getActiveBasePackageById(oldPackageId));
            assertEquals(1, policyManager.getActiveLiveBasePkgDatas().size());
            assertEquals(1, policyManager.getActiveAllBasePkgDatas().size());
        }

        @Test
        public void policyCacheShouldBeRemovedIfPackageIsPurged() {
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(basePkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            dummyDataReader.setReadList(PkgData.class, new ArrayList());
            PolicyCacheDetail afterReloadStatus = policyManager.reload();

            assertSame(0, afterReloadStatus.getSuccessCounter());
            assertNull(policyManager.getPkgDataById(basePkg.getId()));
            assertTrue(policyManager.getPkgDatasByName(basePkg.getName()).isEmpty());
            assertNull(policyManager.getBasePackageDataById(basePkg.getId()));
            assertNull(policyManager.getBasePackageDataByName(basePkg.getName()));
            assertNull(policyManager.getActiveBasePackageById(basePkg.getId()));
            assertNull(policyManager.getActiveBasePackageByName(basePkg.getName()));
            assertTrue(policyManager.getActiveLiveBasePkgDatas().isEmpty());
            assertTrue(policyManager.getActiveAllBasePkgDatas().isEmpty());
        }
    }

    public class AddOnPackageReload {

        private PkgData addOnPkg;

        @Before
        public void setUp() throws Exception {
            addOnPkg = PkgDataBuilder.newAddOnWithDefaultValues();
            initPolicyManager(rnCFactory);
        }

        @Test
        public void storePackageWithSuccessStateWhenNewAddOnFoundWithProperConfiguration() throws InitializationFailedException {
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg.getId());

            assertEquals(PolicyStatus.SUCCESS,newPkgData.getStatus());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false));
            assertFalse("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));

//            assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg.getId()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg.getName()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg.getId()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg.getName()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());

        }

        @Test
        public void storePackageWithFailureStateWhenNewAddOnFoundWithFailuerConfiguration() throws InitializationFailedException {
            List<QosProfileData> qosProfileDataList =  addOnPkg.getQosProfiles();
            qosProfileDataList.get(0).setAdvancedCondition("abc");

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg.getId());

            assertEquals(PolicyStatus.FAILURE,newPkgData.getStatus());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false));
            assertFalse("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));

           // assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg.getId()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg.getName()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg.getId()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg.getName()));
           // assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());
        }

        @Test
        public void storePolicyWithLastKnownGoodState_WhenAddOnPolicyReloadFailAndPreviousInstanceOfPolicyIsStoreWithSuccessState() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            List<QosProfileData> qosProfileDataList =  addOnPkg.getQosProfiles();
            qosProfileDataList.get(0).setAdvancedCondition("abc");
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg.getId());

            assertEquals(PolicyStatus.LAST_KNOWN_GOOD,newPkgData.getStatus());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false));
            assertFalse("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));

            //assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg.getId()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg.getName()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg.getId()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg.getName()));
           // assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());
        }

        @Test
        public void removePolicyWhenActiveAddOnPolicyStatusIsChangeToInactive() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            addOnPkg.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            assertNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertTrue("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false).isEmpty());
            assertTrue("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));
        }

        @Test
        public void storePolicyWithSuccessStateWhenInactiveAddOnPolicyIsChangeToActive() throws InitializationFailedException {

            addOnPkg.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            addOnPkg.setStatus(PkgStatus.ACTIVE.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            policyManager.reload();

            UserPackage newPkgData = policyManager.getPkgDataById(addOnPkg.getId());

            assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false));
            assertFalse("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));

//            assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg.getId()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg.getName()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg.getId()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg.getName()));
//            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());
        }

        @Test
        public void removePolicyWhenAddOnPolicyStatusIsChangedToDeleted() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            addOnPkg.setStatus(CommonConstants.STATUS_DELETED);
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            assertNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertTrue("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false).isEmpty());
            assertTrue("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));

//            assertNull("AddOn package with name:" + addOnPkg.getId() + " should not be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg.getId()));
//            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg.getName()));
//            assertNull("AddOn package with name:" + addOnPkg.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg.getId()));
//            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg.getName()));
//            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());
        }

        @Test
        public void removePolicyWhenActiveAddOnPolicyStatusIsChangeToRetired() throws InitializationFailedException {

            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            addOnPkg.setStatus(PkgStatus.RETIRED.name());
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            policyManager.reload();

            assertNull("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertTrue("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAddOns", policyManager.getActiveAllAddOnPkgDatas(false).isEmpty());
            assertTrue("AddOn package with id:" + addOnPkg.getId() + " should not be found in activeAndLiveAddOns", policyManager.getActiveLiveAddOnDatas().isEmpty());
            assertNotNull("AddOn package with id:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnById(addOnPkg.getId()));
            assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnByName(addOnPkg.getName()));

            //assertNotNull("AddOn package with name:" + addOnPkg.getId() + " should be found in addOnById", policyManager.getAddOnOrTopUpDataById(addOnPkg.getId()));
            //assertNotNull("AddOn package with name:" + addOnPkg.getName() + " should be found in addOnByName", policyManager.getAddOnOrTopUpDataByName(addOnPkg.getName()));
            //assertNull("AddOn package with name:" + addOnPkg.getId() + " should not be found in activeAddOnById", policyManager.getActiveAddOnOrTopUpDataById(addOnPkg.getId()));
            //assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found in activeAddOnByName", policyManager.getActiveAddOnOrTopUpDataByName(addOnPkg.getName()));
            //assertNull("AddOn package with name:" + addOnPkg.getName() + " should not be found ", policyManager.getActiveAllAddOnOrTopUpPkgDatas());
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() throws InitializationFailedException {
            String oldPackageId = addOnPkg.getId();
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            addOnPkg = PkgDataBuilder.newAddOnWithDefaultValues();
            addOnPkg.setId("adon123");
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail afterReloadStatus = policyManager.reload();
            assertSame(1, afterReloadStatus.getSuccessCounter());

            assertSame(1, afterReloadStatus.getSuccessCounter());
            assertNull(policyManager.getPkgDataById(oldPackageId));
            assertNull(policyManager.getAddOnById(oldPackageId));
            assertNull(policyManager.getActiveAddOnById(oldPackageId));
            assertEquals(1, policyManager.getActiveLiveAddOnDatas().size());
        }

        @Test
        public void policyCacheShouldBeRemovedIfPackageIsPurged() {
            dummyDataReader.setReadList(PkgData.class, createPackageDataList(addOnPkg));
            dummyDataReader.setReadList(RatingGroupData.class, PkgDataBuilder.createRatingGroupDatas("RATING_TYPE_1","RATING_TYPE_1"));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            dummyDataReader.setReadList(PkgData.class, new ArrayList());
            PolicyCacheDetail afterReloadStatus = policyManager.reload();
            assertSame(0, afterReloadStatus.getSuccessCounter());

            assertNull(policyManager.getPkgDataById(addOnPkg.getId()));
            assertTrue(policyManager.getPkgDatasByName(addOnPkg.getName()).isEmpty());
            assertNull(policyManager.getAddOnById(addOnPkg.getId()));
            assertNull(policyManager.getAddOnByName(addOnPkg.getName()));
            assertNull(policyManager.getActiveAddOnById(addOnPkg.getId()));
            assertNull(policyManager.getActiveAddOnByName(addOnPkg.getName()));
            assertTrue(policyManager.getActiveLiveAddOnDatas().isEmpty());
        }
    }

    public class IMSPackageReload {

        private IMSPkgData imsPkg;

        @Before
        public void setUp() throws Exception {
            imsPkg = PkgDataBuilder.newImsPackageWithDefaultValue();
            initPolicyManager(rnCFactory);
        }

        @Test
        public void storePackageWithSuccessStateWhenNewIMSPackageFoundWithProperConfiguration() throws InitializationFailedException {

            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));

            policyManager.reload();

            IMSPackage newPkgData = policyManager.getIMSPkgById(imsPkg.getId());

            assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNotNull("IMS package with id:" + imsPkg.getId() + " should be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }


        @Test
        public void storePackageWithFailureStateWhenNewIMSPackageFoundWithFailuerConfiguration() throws InitializationFailedException {
            List<IMSPkgServiceData> imsPkgServiceDatas =  imsPkg.getImsPkgServiceDatas();
            imsPkgServiceDatas.get(0).setExpression("abc");

            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            IMSPackage newPkgData = policyManager.getIMSPkgById(imsPkg.getId());

            assertEquals(PolicyStatus.FAILURE,newPkgData.getStatus());
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNotNull("IMS package with id:" + imsPkg.getId() + " should be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }

        @Test
        public void storePolicyWithLastKnownGoodState_WhenIMSPolicyReloadFailAndPreviousInstanceOfPolicyIsStoreWithSuccessState() throws InitializationFailedException {

            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            List<IMSPkgServiceData> imsPkgServiceDatas =  imsPkg.getImsPkgServiceDatas();
            imsPkgServiceDatas.get(0).setExpression("abc");
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            IMSPackage newPkgData = policyManager.getIMSPkgById(imsPkg.getId());

            assertEquals(PolicyStatus.LAST_KNOWN_GOOD,newPkgData.getStatus());
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNotNull("IMS package with id:" + imsPkg.getId() + " should be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }

        @Test
        public void removePolicyWhenActiveIMSPolicyStatusIsChangeToInactive() throws InitializationFailedException {

            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            imsPkg.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            assertNull("IMS package with name:" + imsPkg.getName() + " should not be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertTrue("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertTrue("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNull("IMS package with id:" + imsPkg.getId() + " should not be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNull("IMS package with name:" + imsPkg.getName() + " should not be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }

        @Test
        public void storePolicyWithSuccessStateWhenInactiveIMSPolicyIsChangeToActive() throws InitializationFailedException {

            imsPkg.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            imsPkg.setStatus(PkgStatus.ACTIVE.name());
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            IMSPackage newPkgData = policyManager.getIMSPkgById(imsPkg.getId());

            assertEquals(PolicyStatus.SUCCESS,newPkgData.getStatus());
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertFalse("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNotNull("IMS package with id:" + imsPkg.getId() + " should be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }

        @Test
        public void removePolicyWhenIMSPolicyStatusIsChangedToDeleted() throws InitializationFailedException {

            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            imsPkg.setStatus(CommonConstants.STATUS_DELETED);
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            assertNull("IMS package with name:" + imsPkg.getName() + " should not be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertTrue("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertTrue("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNull("IMS package with id:" + imsPkg.getId() + " should not be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNull("IMS package with name:" + imsPkg.getName() + " should not be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }

        @Test
        public void removePolicyWhenActiveIMSPolicyStatusIsChangeToRetired() throws InitializationFailedException {

            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            imsPkg.setStatus(PkgStatus.RETIRED.name());
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            policyManager.reload();

            assertNull("IMS package with name:" + imsPkg.getName() + " should not be found in activeIMSPackageByName", policyManager.getActiveIMSPackageByName(imsPkg.getName()));
            assertTrue("IMS package with id:" + imsPkg.getId() + " should not be found in activeIMSPackages", policyManager.getActiveAllImsPkgDatas().isEmpty());
            assertTrue("IMS package with id:" + imsPkg.getId() + " should not be found in activeAndLiveIMSPackages", policyManager.getActiveLiveImsPkgDatas().isEmpty());
            assertNotNull("IMS package with id:" + imsPkg.getId() + " should be found in imsPackageById", policyManager.getIMSPkgById(imsPkg.getId()));
            assertNotNull("IMS package with name:" + imsPkg.getName() + " should be found in imsPackageByName", policyManager.getIMSPkgByName(imsPkg.getName()));
        }

        @Test
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() throws InitializationFailedException {
            String oldPackageId = imsPkg.getId();
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            imsPkg = PkgDataBuilder.newImsPackageWithDefaultValue();
            imsPkg.setId("imsId1");
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            PolicyCacheDetail afterReloadStatus = policyManager.reload();
            assertSame(1, afterReloadStatus.getSuccessCounter());

            assertNull(policyManager.getIMSPkgById(oldPackageId));
            assertNull(policyManager.getActiveBasePackageById(oldPackageId));
            assertEquals(1, policyManager.getActiveLiveImsPkgDatas().size());
        }

        @Test
        public void policyCacheShouldBeRemovedIfPackageIsPurged() {
            dummyDataReader.setReadList(IMSPkgData.class, createImsPackageDataList(imsPkg));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            dummyDataReader.setReadList(IMSPkgData.class, new ArrayList());
            PolicyCacheDetail afterReloadStatus = policyManager.reload();
            assertSame(0, afterReloadStatus.getSuccessCounter());

            assertNull(policyManager.getIMSPkgById(imsPkg.getId()));
            assertNull(policyManager.getIMSPkgByName(imsPkg.getName()));
            assertNull(policyManager.getActiveIMSPackageByName(imsPkg.getId()));
            assertTrue(policyManager.getActiveLiveImsPkgDatas().isEmpty());
        }

    }

    public class QuotaTopUpReload {

        private DataTopUpData dataTopUpData;


        @Before
        public void setUp() throws Exception {
            dataTopUpData = PkgDataBuilder.createDataTopUpBasicInfo();
            initPolicyManager(rnCFactory,DeploymentMode.PCC);

        }

        @Test
        public void storePackageWithSuccessStateWhenNewPackageFoundWithProperConfiguration() throws InitializationFailedException {

            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));

            policyManager.reload();

            QuotaTopUp newPkgData = policyManager.getQuotaTopUpById(dataTopUpData.getId());

            assertEquals(PolicyStatus.SUCCESS, newPkgData.getStatus());
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNotNull("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }


        @Test
        public void storePackageWithFailureStateWhenNewPackageFoundWithFailuerConfiguration() throws InitializationFailedException {

            dataTopUpData.setPackageMode("LIVE3");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            QuotaTopUp newPkgData = policyManager.getQuotaTopUpById(dataTopUpData.getId());

            assertEquals(PolicyStatus.FAILURE,newPkgData.getStatus());

            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertTrue(policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNull(policyManager.quotaTopUp().active().live().byId(dataTopUpData.getId()));
            assertNotNull(policyManager.quotaTopUp().active().byId(dataTopUpData.getId()));
            assertNotNull("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }

        @Test
        public void storePolicyWithLastKnownGoodState_WhenPolicyReloadFailAndPreviousInstanceOfPolicyIsStoreWithSuccessState() throws InitializationFailedException {

            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            dataTopUpData.setPackageMode("LIVE3");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));

            policyManager.reload();

            QuotaTopUp newPkgData = policyManager.getQuotaTopUpById(dataTopUpData.getId());

            assertEquals(PolicyStatus.LAST_KNOWN_GOOD,newPkgData.getStatus());
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNotNull("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }

        @Test
        public void removePolicyWhenActivePolicyStatusIsChangeToInactive() throws InitializationFailedException {

            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            dataTopUpData.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            assertNull("Quota TopUp with name:" + dataTopUpData.getName() + " should not be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNull("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNull("Quota TopUp with name:" + dataTopUpData.getName() + " should not be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }

        @Test
        public void storePolicyWithSuccessStateWhenInactivePolicyIsChangeToActive() throws InitializationFailedException {

            dataTopUpData.setStatus(PkgStatus.INACTIVE.name());
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            dataTopUpData.setStatus(PkgStatus.ACTIVE.name());
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            QuotaTopUp newPkgData = policyManager.getQuotaTopUpById(dataTopUpData.getId());

            assertEquals(PolicyStatus.SUCCESS,newPkgData.getStatus());
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertFalse("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNotNull("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }

        @Test
        public void removePolicyWhenPolicyStatusIsChangedToDeleted() throws InitializationFailedException {

            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            dataTopUpData.setStatus(CommonConstants.STATUS_DELETED);
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            assertNull("Quota TopUp with name:" + dataTopUpData.getName() + " should not be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNull("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNull("Quota TopUp with name:" + dataTopUpData.getName() + " should not be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }

        @Test
        public void removePolicyWhenActivePolicyStatusIsChangeToRetired() throws InitializationFailedException {

            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            dataTopUpData.setStatus(PkgStatus.RETIRED.name());
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            policyManager.reload();

            assertNull("Quota TopUp with name:" + dataTopUpData.getName() + " should not be found in activeQuotaTopUpByName",
                    policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeQuotaTopUp",
                    policyManager.getActiveAllQuotaTopUpDatas().isEmpty());
            assertTrue("Quota TopUp with id:" + dataTopUpData.getId() + " should not be found in activeAndLiveQuotaTopUps",
                    policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
            assertNotNull("Quota TopUp with id:" + dataTopUpData.getId() + " should be found in QuotaTopUpById",
                    policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNotNull("Quota TopUp with name:" + dataTopUpData.getName() + " should be found in QuotaTopUpByName",
                    policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
        }

        @Test // added for JIRA: NETVERTEX-2549
        public void oldPackageCacheShouldBeRemovedIfPackageDeletedAndInsertedWithSameName() {
            String oldPackageId = dataTopUpData.getId();
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            dataTopUpData = PkgDataBuilder.newDataTopUpDataWithDefaultValue();
            dataTopUpData.setId("tpupId");
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            PolicyCacheDetail afterReloadStatus = policyManager.reload();
            assertSame(1, afterReloadStatus.getSuccessCounter());

            assertNull(policyManager.getQuotaTopUpById(oldPackageId));
            assertNull(policyManager.getActiveQuotaTopUpById(oldPackageId));
            assertEquals(1, policyManager.getActiveAllQuotaTopUpDatas().size());
            assertEquals(1, policyManager.getActiveLiveQuotaTopUpDatas().size());
        }

        @Test
        public void policyCacheShouldBeRemovedIfPackageIsPurged() {
            dummyDataReader.setReadList(DataTopUpData.class, createDataTopUpDataList(dataTopUpData));
            PolicyCacheDetail beforeReloadStatus = policyManager.reload();
            assertSame(1, beforeReloadStatus.getSuccessCounter());
            dummyDataReader.setReadList(DataTopUpData.class, new ArrayList());
            PolicyCacheDetail afterReloadStatus = policyManager.reload();
            assertSame(0, afterReloadStatus.getSuccessCounter());

            assertNull(policyManager.getQuotaTopUpById(dataTopUpData.getId()));
            assertNull(policyManager.getQuotaTopUpByName(dataTopUpData.getName()));
            assertNull(policyManager.getActiveQuotaTopUpById(dataTopUpData.getId()));
            assertNull(policyManager.getActiveQuotaTopUpByName(dataTopUpData.getName()));
            assertTrue(policyManager.getActiveLiveQuotaTopUpDatas().isEmpty());
        }

    }

    private List<PkgData> createPackageDataList(PkgData pkgData){
        List<PkgData> pkgDatas = new ArrayList<>();
        pkgDatas.add(pkgData);
        return pkgDatas;
    }

    private List<RatingGroupData> createRatingGroupDataList(RatingGroupData ratingGroupData){
        List<RatingGroupData> ratinggroupDatas = new ArrayList<>();
        ratinggroupDatas.add(ratingGroupData);
        return ratinggroupDatas;
    }

    private List<IMSPkgData> createImsPackageDataList(IMSPkgData imsPkgData){
        List<IMSPkgData> imsPackageData = new ArrayList<>();
        imsPackageData.add(imsPkgData);
        return imsPackageData;
    }

    private List<DataTopUpData> createDataTopUpDataList(DataTopUpData imsPkgData){
        List<DataTopUpData> imsPackageData = new ArrayList<>();
        imsPackageData.add(imsPkgData);
        return imsPackageData;
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        hibernateSessionFactory.shutdown();
        folder.delete();
    }
}