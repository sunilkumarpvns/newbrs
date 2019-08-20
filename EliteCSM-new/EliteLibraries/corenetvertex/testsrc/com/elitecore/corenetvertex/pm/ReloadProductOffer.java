package com.elitecore.corenetvertex.pm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.pm.util.ProductOfferPredicates;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReloadProductOffer {
    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private ArrayList<String> groupLists = new ArrayList<>();
    private RnCFactory rnCFactory = new RnCFactory();
    private RnCPackageFactory rnCPackageFactory = new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory));
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
	private DeploymentMode deploymentMode = DeploymentMode.PCC;
    private DummyDataReader dummyDataReader;

    @Before
    public void setUp() throws Exception {
        generatePolicyBackUpFile();
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);

        packageFactory = new PackageFactory();
        policyManager = PolicyManager.getInstance();
        groupLists.clear();
        dummyDataReader = new DummyDataReader();
    }

    private List<ProductOfferData> createOfferDataList(ProductOfferData productOfferData){
        List<ProductOfferData> list = new ArrayList<>(2);
        list.add(productOfferData);
        return list;
    }


    public class WhenTableEmpty{
        @Test
        public void getAllOfferReturnsEmptyList() throws InitializationFailedException{
			initPolicyManager();
			assertEquals(0,policyManager.getProductOffer().all().size());
        }

        @Test
        public void byIdReturnsNullValue() throws InitializationFailedException{
			initPolicyManager();
			assertNull(policyManager.getProductOffer().byId("empty"));
        }

        @Test
        public void byNameReturnsNullValue() throws InitializationFailedException{
			initPolicyManager();
			assertNull(policyManager.getProductOffer().byName("invalid"));
        }
    }

	private void initPolicyManager() throws InitializationFailedException {
		policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory, rnCPackageFactory, deploymentMode);
	}


	public class InitWhenTableNotEmpty{
        @Test
        public void allReturnsSameListAsStoredInDB() throws InitializationFailedException{
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name1").withStatus("ACTIVE")
                    .withMode("TEST").build();
            addDefaultValues(productOfferData);
            dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));

			initPolicyManager();

			ArrayList<ProductOffer> list = new ArrayList<>(2);
            list.add(ProductOfferDataFactory.createProductOffer(productOfferData, PolicyStatus.FAILURE, null));

            ReflectionAssert.assertLenientEquals(list,
                    policyManager.getProductOffer().all());

        }

        @Test
        public void byIdReturnsSameObjectAsStoredInDB() throws InitializationFailedException{
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name1").withStatus("ACTIVE")
                    .withMode("TEST").build();
            addDefaultValues(productOfferData);
            dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
			initPolicyManager();

			ReflectionAssert.assertLenientEquals(ProductOfferDataFactory.createProductOffer(productOfferData,PolicyStatus.FAILURE,null), policyManager.getProductOffer().byId("1"));

        }

        @Test
        public void byNameReturnsSameObjectAsStoredInDB() throws InitializationFailedException{

            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("hello").withName("name1").withStatus("ACTIVE")
                    .withMode("TEST").build();
            addDefaultValues(productOfferData);
            dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
			initPolicyManager();

			ReflectionAssert.assertLenientEquals(ProductOfferDataFactory.createProductOffer(productOfferData,PolicyStatus.FAILURE,null), policyManager.getProductOffer().byName("name1"));

        }

        @Test
        public void testsItDoesNotCacheDesignOffers() throws InitializationFailedException{

            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("hello").withName("name1").withStatus("ACTIVE")
                    .withMode("DESIGN").build();
            addDefaultValues(productOfferData);
            dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
			initPolicyManager();

			assertNull(policyManager.getProductOffer().byName("name1"));

        }

        @Test
        public void testsItDoesNotCacheDeletedOffers() throws InitializationFailedException{

            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("hello").withName("name1").withStatus("DELETED")
                    .withMode("TEST").build();
            addDefaultValues(productOfferData);
            dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
			initPolicyManager();

			assertNull(policyManager.getProductOffer().byName("name1"));

        }

        @Test
        public void testsItDoesNotCacheInactiveOffers() throws InitializationFailedException{

            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("hello").withName("name1").withStatus("INACTIVE")
                    .withMode("TEST").build();
            addDefaultValues(productOfferData);
            dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
			initPolicyManager();

			assertNull(policyManager.getProductOffer().byName("name1"));

        }
    }

    public class Reload {


        public class DataDeleted {
            @Test
            public void allReturnsEmptyLit() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

                dummyDataReader.setReadList(ProductOfferData.class, new ArrayList());

                policyManager.reload();

                ReflectionAssert.assertLenientEquals(new ArrayList<ProductOffer>(), policyManager.getProductOffer().all());
            }

            @Test
            public void byIdReturnsNullForOldId() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

                dummyDataReader.setReadList(ProductOfferData.class, new ArrayList());

                policyManager.reload();

                assertNull(policyManager.getProductOffer().byId("hello"));

            }

            @Test
            public void byNameReturnsNullForOldName() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

                dummyDataReader.setReadList(ProductOfferData.class, new ArrayList());

                policyManager.reload();

                assertNull(policyManager.getProductOffer().byName("name1"));

            }
        }

        public class NewDataAdded {
            @Test
            public void allReturnsNewValuesFromDatabase() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				List<ProductOffer> newList = addNewDataAndDeletePassedOne();

                policyManager.reload();

                ReflectionAssert.assertLenientEquals(newList, policyManager.getProductOffer().all());

            }

            @Test
            public void byNameForNewDataReturnsNewAddedObjectsFromDb() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				List<ProductOffer> newList = addNewDataAndDeletePassedOne();

                policyManager.reload();

                ReflectionAssert.assertLenientEquals(newList.get(0), policyManager.getProductOffer().byName("name2"));

            }

            @Test
            public void byIdForNewDataReturnsNewAddedObjectsFromDb() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				List<ProductOffer> newList = addNewDataAndDeletePassedOne();

                policyManager.reload();

                ReflectionAssert.assertLenientEquals(newList.get(0), policyManager.getProductOffer().byId("world"));

            }
        }

        public class ForGroups{

            @Test
            public void testsItDoesNotReloadDataForOtherGroups() throws InitializationFailedException {
				initPolicyManager();
				ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("woo").withName("hoo").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));

                groupLists.add("GROUP2");
                policyManager.reloadProductOffers(ProductOfferPredicates.createGroupFilter(groupLists));
                assertNull(policyManager.getProductOffer().byId("woo"));
            }

            @Test
            public void testsItRemovesDeletedOffers() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				productOfferData.setStatus("DELETED");
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));

                groupLists.add("GROUP_1");
                policyManager.reloadProductOffers(ProductOfferPredicates.createGroupFilter(groupLists));

                assertNull(policyManager.getProductOffer().byId("hello"));
            }


            @Test
            public void testsItRemovesInactiveOffers() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("hello").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				productOfferData.setStatus("INACTIVE");
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));

                groupLists.add("GROUP_1");
                policyManager.reloadProductOffers(ProductOfferPredicates.createGroupFilter(groupLists));

                assertNull(policyManager.getProductOffer().byId("hello"));
            }

            @Test
            public void testActiveAndLiveProductOffer() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("test").withName("testName").withStatus("ACTIVE").withType(PkgType.ADDON.val)
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				assertFalse(policyManager.getActiveAllBaseProductOffers().isEmpty());
            }

            @Test
            public void testsItDoesUpdateOfferBelongingToTheSameGroup() throws InitializationFailedException {
                ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("world").withName("name1").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));
				initPolicyManager();

				productOfferData.setName("name2");
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));

                groupLists.add("GROUP_1");
                policyManager.reloadProductOffers(ProductOfferPredicates.createGroupFilter(groupLists));

                ReflectionAssert.assertLenientEquals(ProductOfferDataFactory.createProductOffer(productOfferData,PolicyStatus.FAILURE,null),
                        policyManager.getProductOffer().byId("world"));
            }

            @Test
            public void testsItDoesAddOfferBelongingToTheSameGroup() throws InitializationFailedException {
				initPolicyManager();
				ProductOfferData productOfferData = new ProductOfferDataFactory()
                        .withId("woo").withName("hoo").withStatus("ACTIVE")
                        .withMode("TEST").build();
                addDefaultValues(productOfferData);
                dummyDataReader.setReadList(ProductOfferData.class,createOfferDataList(productOfferData));

                groupLists.add("GROUP_1");
                policyManager.reloadProductOffers(ProductOfferPredicates.createGroupFilter(groupLists));
                assertNotNull(policyManager.getProductOffer().byId("woo"));
            }
        }
    }

    private List<ProductOffer> addNewDataAndDeletePassedOne(){

        List<ProductOffer> newList = new ArrayList();
        ArrayList<ProductOfferData> dataList = new ArrayList<>();

        ProductOfferData productOfferData = new ProductOfferDataFactory()
                .withId("world").withName("name2").withStatus("ACTIVE")
                .withMode("TEST").build();
        addDefaultValues(productOfferData);
        dataList.add(productOfferData);
        newList.add(ProductOfferDataFactory.createProductOffer(productOfferData,PolicyStatus.FAILURE,null));


        productOfferData = new ProductOfferDataFactory()
                .withId("sample").withName("name3").withStatus("ACTIVE")
                .withMode("TEST").build();
        addDefaultValues(productOfferData);
        dataList.add(productOfferData);
        newList.add(ProductOfferDataFactory.createProductOffer(productOfferData,PolicyStatus.FAILURE,null));

        dummyDataReader.setReadList(ProductOfferData.class,dataList);

        return newList;
    }

    public void addDefaultValues(ProductOfferData offerData){
        offerData.setGroupNames("GROUP_1");
        offerData.setGroups("GROUP_1");
        offerData.setType("BASE");
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        folder.delete();
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }
}
