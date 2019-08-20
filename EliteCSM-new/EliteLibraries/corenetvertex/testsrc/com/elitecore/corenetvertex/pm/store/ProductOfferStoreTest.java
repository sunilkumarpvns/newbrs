package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.BaseProductOfferFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class ProductOfferStoreTest {

    public static final String GROUP_1 = "GROUP_1";
    private List offerList = new ArrayList();
    private PolicyManager policyManager = PolicyManager.getInstance();
    @Mock
    private Package aPackage;
    @Mock
    private ServiceStore serviceStore;
    private BaseProductOfferFactory offerFactory;

    @Before
    public void setup(){
        List<String> groups = new ArrayList<>();
        groups.add(GROUP_1);
        MockitoAnnotations.initMocks(this);
        offerList.clear();
        policyManager = Mockito.spy(policyManager);
        aPackage.setPolicyStatus(PolicyStatus.SUCCESS);

        Mockito.doReturn(aPackage).when(policyManager).getPkgDataById("packageId");
        Mockito.doReturn(null).when(policyManager).getPkgDataById(null);
        Mockito.doReturn(serviceStore).when(policyManager).getService();
        Mockito.when(serviceStore.byId(Mockito.anyString())).thenReturn(null);
        Mockito.doReturn(groups).when(aPackage).getGroupIds();
    }

    private ProductOffer createProductOffer(ProductOfferData productOfferData, PolicyStatus policyStatus){
        UserPackage dataPackage = policyManager.getPkgDataById(productOfferData.getDataServicePkgId());

        return new ProductOffer(productOfferData.getId(),
                productOfferData.getName(),
                productOfferData.getDescription(),
                PkgType.valueOf(productOfferData.getType()),
                PkgMode.valueOf(productOfferData.getPackageMode()),
                productOfferData.getValidityPeriod(),
                ValidityPeriodUnit.valueOf(productOfferData.getValidityPeriodUnit()),
                productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
                productOfferData.getStatus()==null? PkgStatus.ACTIVE:PkgStatus.fromVal(productOfferData.getStatus()),
                createProductOffferServicePkgRel(productOfferData.getProductOfferServicePkgRelDataList()),null,
                Objects.nonNull(dataPackage)?dataPackage.getId():null,
                productOfferData.getGroupList(),
                productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(),
                policyStatus,
                null,
                null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(),
                productOfferData.getParam2(),
                policyManager,null,null,new HashMap<>(),productOfferData.getCurrency()
        );
    }

    private List<ProductOfferServicePkgRel> createProductOffferServicePkgRel(List<ProductOfferServicePkgRelData> productOfferServicePkgRelData){

        List<ProductOfferServicePkgRel> productOfferServicePkgRels = new ArrayList<>();

        for(ProductOfferServicePkgRelData rel: productOfferServicePkgRelData){
            ProductOfferServicePkgRel productOfferServicePkgRel = new ProductOfferServicePkgRel(
                    rel.getId(),
                    policyManager.getService().byId(rel.getServiceId()),
                    rel.getRncPackageId(),
                    policyManager
            );
            productOfferServicePkgRels.add(productOfferServicePkgRel);
        }

        return productOfferServicePkgRels;
    }

    public class EmptyStore {

        @Test
        public void allWillReturnEmptyArray(){
            ProductOfferStore serviceStore = new ProductOfferStore();
            assertEquals(0,serviceStore.all().size());
        }

        @Test
        public void byIdWillReturnNull(){
            ProductOfferStore serviceStore = new ProductOfferStore();
            assertNull(serviceStore.byId("hello"));
        }

        @Test
        public void byNameWillReturnNull(){
            ProductOfferStore serviceStore = new ProductOfferStore();
            assertNull(serviceStore.byName("name"));
        }

    }

    public class InitializedOnce{
        @Test
        public void testsAllReturnsAllTheOffersCreatedWithCreateMethod(){

            ProductOfferData productOfferData =  new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withDataServicePkgId("packageId")
                    .withMode("TEST").withType("BASE").withGroups(GROUP_1).build();

            Mockito.when(aPackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            List<ProductOffer> expectedList = new ArrayList<>();
            expectedList.add(createProductOffer(productOfferData, PolicyStatus.SUCCESS));

            productOfferData = new ProductOfferDataFactory()
                    .withId("2").withName("name2").withStatus("ACTIVE")
                    .withDataServicePkgId("packageId")
                    .withMode("LIVE").withType("BASE").withGroups(GROUP_1).build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            expectedList.add(createProductOffer(productOfferData, PolicyStatus.SUCCESS));


            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(expectedList, offerStore.all());

        }

        @Test
        public void testsGetByIdReturnsTheSameOfferCreatedDuringCreate(){
            ProductOfferData productOfferData =  new ProductOfferDataFactory()
                    .withId("good").withName("name").withStatus("ACTIVE").withDataServicePkgId("packageId")
                    .withMode("DESIGN").withType("BASE").build();

            Mockito.when(aPackage.getStatus()).thenReturn(PolicyStatus.FAILURE);
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(createProductOffer(productOfferData, PolicyStatus.FAILURE), offerStore.byId("good"));
        }

        @Test
        public void testsGetByNameReturnsTheSameOfferCreatedDuringCreate(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("good").withName("name").withStatus("ACTIVE")
                    .withMode("DESIGN").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(offerStore.byId("name"), productOfferData);

        }

    }

    public class Reload{
        @Test
        public void byNameAndIdWillNotReturnOldValues(){
            ProductOfferDataFactory factory = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("DESIGN").withType("BASE");
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(factory.build()));

            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            offerList = new ArrayList();

            factory = new ProductOfferDataFactory()
                    .withId("2").withName("name2").withStatus("ACTIVE")
                    .withMode("DESIGN").withType("BASE");

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(factory.build()));

            List<String> purgedObjects = new ArrayList<>();
            purgedObjects.add("1");
            offerStore.create(offerList, purgedObjects);

            assertNull(offerStore.byId("1"));
            assertNull(offerStore.byName("name"));
        }
        @Test
        public void allWillReturnNewListWhichWasAdded(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("DESIGN").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            offerList = new ArrayList();

            productOfferData = new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("DESIGN").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            List<String> purgedObjects = new ArrayList<>();
            purgedObjects.add("1");
            offerStore.create(offerList, purgedObjects);

            ReflectionAssert.assertLenientEquals(createProductOffer(productOfferData, PolicyStatus.FAILURE),offerStore.all().get(0));
        }

        @Test
        public void itReturnsNullByIdForDeletedOffersWhenCreatedagainWithDeletedOffers(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            ArrayList<String> list = new ArrayList<>();
            list.add("1");

            offerList = new ArrayList();
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            offerStore.create(offerList, list);


            assertNull(offerStore.byId("1"));
        }

        @Test
        public void itReturnsNullByNameForDeletedOffersWhenCreatedagainWithDeletedOffers(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            productOfferData = new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();

            offerStore.create(offerList, new ArrayList<>());
            ArrayList<String> list = new ArrayList<>();
            list.add("1");

            offerList = new ArrayList();
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            offerStore.create(offerList, list);

            assertNull(offerStore.byName("name"));
        }

        @Test
        public void itReturnsLastKnownGoodIfItWasSuccessWhenOfferFailsToReload(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withDataServicePkgId("packageId")
                    .withMode("TEST").withType("BASE").withGroups(GROUP_1).build();
            Mockito.when(aPackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            ProductOffer expected = createProductOffer(productOfferData, PolicyStatus.LAST_KNOWN_GOOD);

            ProductOfferStore offerStore = new ProductOfferStore();

            offerStore.create(offerList, new ArrayList<>());

            offerList.clear();

            productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("ADDON")
                    .withDataServicePkgId("packageId").withGroups(GROUP_1).build();
            Mockito.when(aPackage.getStatus()).thenReturn(PolicyStatus.FAILURE);
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            offerStore.create(offerList, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(expected,offerStore.byId("1"));
        }

        @Test
        public void itReturnsCurrentOfferIfLastKnownGoodWasFailureWhenCurrentOfferFailsToReload(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();
            Mockito.when(aPackage.getStatus()).thenReturn(PolicyStatus.FAILURE);
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            ProductOffer expected = createProductOffer(productOfferData, PolicyStatus.FAILURE);

            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            offerList.clear();

            productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE")
                    .withDataServicePkgId("packageId").build();
            Mockito.when(aPackage.getStatus()).thenReturn(PolicyStatus.FAILURE);
            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            offerStore.create(offerList, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(expected,offerStore.byId("1"));
        }
    }

    public class BaseProductOffer {
        @Test
        public void itReturnsNotNullByIdAndByNameForActiveAllBaseProductOffers(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertNotNull(offerStore.base().active().byId("1"));
            assertNotNull(offerStore.base().active().byId("3"));
            assertNotNull(offerStore.base().active().byName("name"));
            assertNotNull(offerStore.base().active().byName("name3"));

            assertTrue(offerStore.base().active().live().all().isEmpty());
            assertTrue(offerStore.addOn().active().all().isEmpty());

            assertNull(offerStore.addOn().active().byId("1"));
            assertNull(offerStore.addOn().active().byId("3"));
            assertNull(offerStore.addOn().active().byName("name"));
            assertNull(offerStore.addOn().active().byName("name3"));

        }

        @Test
        public void itReturnsNotNullByIdAndByNameForLiveBaseProductOffers(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("LIVE").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("LIVE").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertNotNull(offerStore.base().active().live().byId("1"));
            assertNotNull(offerStore.base().active().live().byId("3"));
            assertNotNull(offerStore.base().active().live().byName("name"));
            assertNotNull(offerStore.base().active().live().byName("name3"));

            assertNull(offerStore.addOn().active().live().byId("1"));
            assertNull(offerStore.addOn().active().live().byId("3"));
            assertNull(offerStore.addOn().active().live().byName("name"));
            assertNull(offerStore.addOn().active().live().byName("name3"));

            assertFalse(offerStore.base().active().live().all().isEmpty());
            assertTrue(offerStore.addOn().active().live().all().isEmpty());

        }

        @Test
        public void itReturnsEmptyActiveAllBaseProductOffersWhenCreatedWithInActiveProductOfferData(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("INACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("INACTIVE")
                    .withMode("TEST").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertTrue(offerStore.base().active().all().isEmpty());
            assertNull(offerStore.base().active().byId("1"));
            assertNull(offerStore.base().active().byId("3"));
            assertNull(offerStore.base().active().byName("name"));
            assertNull(offerStore.base().active().byName("name3"));
        }

        @Test
        public void itReturnsEmptyActiveLiveBaseProductOffersWhenCreatedWithInActiveProductOfferData(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("INACTIVE")
                    .withMode("LIVE").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("INACTIVE")
                    .withMode("LIVE").withType("BASE").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertTrue(offerStore.base().active().live().all().isEmpty());
            assertNull(offerStore.base().active().live().byId("1"));
            assertNull(offerStore.base().active().live().byId("3"));
            assertNull(offerStore.base().active().live().byName("name"));
            assertNull(offerStore.base().active().live().byName("name3"));
        }


    }

    public class AddOnProductOffer {
        @Test
        public void itReturnsNotNullByIdAndByNameForActiveAllAddOnProductOffers(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("TEST").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("TEST").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertNotNull(offerStore.addOn().active().byId("1"));
            assertNotNull(offerStore.addOn().active().byId("3"));
            assertNotNull(offerStore.addOn().active().byName("name"));
            assertNotNull(offerStore.addOn().active().byName("name3"));

            assertTrue(offerStore.addOn().active().live().all().isEmpty());
            assertTrue(offerStore.base().active().all().isEmpty());

            assertNull(offerStore.base().active().byId("1"));
            assertNull(offerStore.base().active().byId("3"));
            assertNull(offerStore.base().active().byName("name"));
            assertNull(offerStore.base().active().byName("name3"));

        }

        @Test
        public void itReturnsNotNullByIdAndByNameForLiveBaseProductOffers(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("ACTIVE")
                    .withMode("LIVE").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("ACTIVE")
                    .withMode("LIVE").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertNotNull(offerStore.addOn().active().live().byId("1"));
            assertNotNull(offerStore.addOn().active().live().byId("3"));
            assertNotNull(offerStore.addOn().active().live().byName("name"));
            assertNotNull(offerStore.addOn().active().live().byName("name3"));

            assertNull(offerStore.base().active().live().byId("1"));
            assertNull(offerStore.base().active().live().byId("3"));
            assertNull(offerStore.base().active().live().byName("name"));
            assertNull(offerStore.base().active().live().byName("name3"));

            assertFalse(offerStore.addOn().active().live().all().isEmpty());
            assertTrue(offerStore.base().active().live().all().isEmpty());

        }

        @Test
        public void itReturnsEmptyActiveAllaDDoNproductOffersWhenCreatedWithInActiveProductOfferData(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("INACTIVE")
                    .withMode("TEST").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("INACTIVE")
                    .withMode("TEST").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertTrue(offerStore.addOn().active().all().isEmpty());
            assertNull(offerStore.addOn().active().byId("1"));
            assertNull(offerStore.addOn().active().byId("3"));
            assertNull(offerStore.addOn().active().byName("name"));
            assertNull(offerStore.addOn().active().byName("name3"));
        }

        @Test
        public void itReturnsEmptyActiveLiveBaseProductOffersWhenCreatedWithInActiveProductOfferData(){
            ProductOfferData productOfferData = new ProductOfferDataFactory()
                    .withId("1").withName("name").withStatus("INACTIVE")
                    .withMode("LIVE").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));

            productOfferData =new ProductOfferDataFactory()
                    .withId("3").withName("name3").withStatus("INACTIVE")
                    .withMode("LIVE").withType("ADDON").build();

            offerList.add(new BaseProductOfferFactory(policyManager).createProductOffer(productOfferData));
            ProductOfferStore offerStore = new ProductOfferStore();
            offerStore.create(offerList, new ArrayList<>());

            assertTrue(offerStore.addOn().active().live().all().isEmpty());
            assertNull(offerStore.addOn().active().live().byId("1"));
            assertNull(offerStore.addOn().active().live().byId("3"));
            assertNull(offerStore.addOn().active().live().byName("name"));
            assertNull(offerStore.addOn().active().live().byName("name3"));
        }
    }
}
