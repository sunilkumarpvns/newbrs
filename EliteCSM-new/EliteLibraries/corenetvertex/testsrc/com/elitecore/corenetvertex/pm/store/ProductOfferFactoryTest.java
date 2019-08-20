package com.elitecore.corenetvertex.pm.store;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferAutoSubscriptionRelData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.ProductOfferBuilder;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.BaseProductOfferFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.pm.util.MockProductOffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProductOfferFactoryTest {
    public static final String GROUP_1 = "Group1";
    private DummyPolicyRepository policyRepository;

    private BasePackage basePackage;
    private ProductOffer addOnOffer1;
    private ProductOffer addOnOffer2;

    @Before
    public void setup() {
        List<String> groups = new ArrayList<>();
        groups.add(GROUP_1);
        policyRepository = new DummyPolicyRepository();
        basePackage = MockBasePackage.create("packageId", "packageName").addGroups(groups);
        policyRepository.addBasePackage(basePackage);
        addOnOffer1 = MockProductOffer.create(policyRepository, "addOnOffer1", "addOnOffer1Name",PolicyStatus.SUCCESS, PkgStatus.ACTIVE).setTypeAddOn().addGroups(groups);
        addOnOffer2 = MockProductOffer.create(policyRepository, "addOnOffer2", "addOnOffer2Name",PolicyStatus.SUCCESS, PkgStatus.ACTIVE).setTypeAddOn().addGroups(groups);
        policyRepository.addProductOffers(addOnOffer1, addOnOffer2);

        //Mockito.doReturn(null).when(policyRepository).getPkgDataById(null);
    }


    private ProductOffer createExpectedProductOffer(ProductOfferData productOfferData, PkgStatus pkgStatus, PolicyStatus policyStatus, FailReason failReason, FailReason partialFailReason){
        return new ProductOffer(
                productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
                PkgType.BASE, PkgMode.DESIGN, productOfferData.getValidityPeriod(),
                null, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d, productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
                pkgStatus, createProductOfferServicePkgRel(productOfferData.getProductOfferServicePkgRelDataList()), Collectionz.newArrayList(),
                productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(), policyStatus,  failReason, partialFailReason, (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()), productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,null,null,new HashMap<>(),productOfferData.getCurrency()
        );

    }

    private List<ProductOfferServicePkgRel> createProductOfferServicePkgRel(List<ProductOfferServicePkgRelData> productOfferServicePkgRelData){

        List<ProductOfferServicePkgRel> productOfferServicePkgRels = new ArrayList<>();

        for(ProductOfferServicePkgRelData rel: productOfferServicePkgRelData){
            ProductOfferServicePkgRel productOfferServicePkgRel = new ProductOfferServicePkgRel(
                    rel.getId(),
                    null,
                    null,
                    policyRepository
            );
            productOfferServicePkgRels.add(productOfferServicePkgRel);
        }

        return productOfferServicePkgRels;
    }

    public List<ProductOfferServicePkgRelData> createRelList(String servId, String pkgId, String id){
        ProductOfferServicePkgRelData rel = new ProductOfferServicePkgRelData();

        rel.setId(id);
        rel.setServiceId(servId);

        RncPackageData rncPackageData = new RncPackageData();
        rncPackageData.setId(pkgId);
        rel.setRncPackageId(pkgId);
        rel.setRncPackageData(rncPackageData);

        ArrayList<ProductOfferServicePkgRelData> list = new ArrayList();
        list.add(rel);
        return list;
    }


    @Test
    public void testsItCreatesActivePolicyWhenStatusIsNull(){
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("name").withStatus(null)
                .withMode("DESIGN").withType("BASE")
                .withDataServicePkgId("packageId").withGroups(GROUP_1).build();

        Mockito.when(basePackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        ReflectionAssert.assertLenientEquals(createExpectedProductOffer(productOfferData,PkgStatus.ACTIVE, PolicyStatus.SUCCESS,null, null),
                new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData));

    }

    @Test
    public void testsItCreatesActivePolicyWhenThereIsRandomValueInPolicyStatus(){
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("name").withStatus("Random")
                .withMode("DESIGN").withType("BASE")
                .withDataServicePkgId("packageId").withGroups(GROUP_1).build();

        Mockito.when(basePackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        ReflectionAssert.assertLenientEquals(createExpectedProductOffer(productOfferData,PkgStatus.ACTIVE, PolicyStatus.SUCCESS,null, null),
                new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData));

    }

    @Test
    public void testsItCreatesFailedOfferWhenThereIsNoDataPackageAttached(){
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("name").withStatus("Random")
                .withMode("DESIGN").withType("BASE").build();

        Mockito.when(basePackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        ReflectionAssert.assertLenientEquals(createExpectedProductOffer(productOfferData,PkgStatus.ACTIVE, PolicyStatus.FAILURE,null, null),
                new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData));

    }

    @Test
    public void testsItCreatesFailOfferWhenThePackageHasFailedPolicyStatus(){
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("name").withStatus("Random")
                .withMode("DESIGN").withType("BASE")
                .withDataServicePkgId("packageId").build();

        Mockito.when(basePackage.getStatus()).thenReturn(PolicyStatus.FAILURE);
        FailReason failReason = new FailReason("Product Offer");
        ReflectionAssert.assertLenientEquals(createExpectedProductOffer(productOfferData,PkgStatus.ACTIVE, PolicyStatus.FAILURE,failReason,null),
                new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData));

    }
    public void testsThatThereIsNullRncRelDetailInCacheAsItHasBeenPutNullThere(){
        ProductOfferData productOfferData = new ProductOfferDataFactory()
                .withId("good").withName("name").withStatus("ACTIVE")
                .withMode("DESIGN").withType("BASE").build();

        productOfferData.setProductOfferServicePkgRelDataList(createRelList("serIc","pkgId","idId"));

        Mockito.when(basePackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        ReflectionAssert.assertLenientEquals(createExpectedProductOffer(productOfferData, PkgStatus.ACTIVE, PolicyStatus.SUCCESS, null, null),
                new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData));
    }


    @Test
    public void testsThatProductOfferWillAllowToAddMultipleAutoSubscriptions(){
        ProductOfferData productOfferData = ProductOfferBuilder.newBaseOfferWithDataPackage("packageId");
        List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDataList = Collectionz.newArrayList();
        productOfferAutoSubscriptionRelDataList.add(getProductOfferAutoSubscription("autoSubscription1","addOnOffer1","1=1"));
        productOfferAutoSubscriptionRelDataList.add(getProductOfferAutoSubscription("autoSubscription2","addOnOffer2",null));
        productOfferData.setProductOfferAutoSubscriptionRelDatas(productOfferAutoSubscriptionRelDataList);


        ProductOffer expectedProductOffer = createExpectedProductOffer(productOfferData, PkgStatus.ACTIVE, PolicyStatus.SUCCESS, null, null);
        expectedProductOffer.getProductOfferAutoSubscriptions().addAll(createProductOfferAutoSubscriptionRel(productOfferData.getProductOfferAutoSubscriptionRelDatas()));
        ReflectionAssert.assertLenientEquals(expectedProductOffer,
                new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData));
    }


    @Test
    public void testsThatProductOfferFailsWhenAutoSubscirptionHasInvalidAdvancedCondition(){
        ProductOfferData productOfferData = ProductOfferBuilder.newBaseOfferWithDataPackage("packageId");
        List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDataList = Collectionz.newArrayList();
        productOfferAutoSubscriptionRelDataList.add(getProductOfferAutoSubscription("autoSubscription1","addOnOffer1","1=1="));
        productOfferAutoSubscriptionRelDataList.add(getProductOfferAutoSubscription("autoSubscription2","addOnOffer2","true>"));
        productOfferData.setProductOfferAutoSubscriptionRelDatas(productOfferAutoSubscriptionRelDataList);

        FailReason partialFailReason = getPartialFailReason();
        ProductOffer expectedProductOffer = createExpectedProductOffer(productOfferData, PkgStatus.ACTIVE, PolicyStatus.PARTIAL_SUCCESS, null, partialFailReason);
        ProductOffer productOffer = new BaseProductOfferFactory(policyRepository).createProductOffer(productOfferData);
        Assert.assertEquals(productOffer.getPartialFailReason().toString(),expectedProductOffer.getPartialFailReason().toString());
    }

    private FailReason getPartialFailReason() {
        FailReason partialFailReason = new FailReason("Product Offer");
        FailReason failReason1 = new FailReason("autoSubscription1");
        failReason1.add("Invalid condition: 1=1=");
        FailReason failReason2 = new FailReason("autoSubscription2");
        failReason2.add("Invalid condition: true>");
        partialFailReason.addChildModuleFailReasonIfNotEmpty(failReason1);
        partialFailReason.addChildModuleFailReasonIfNotEmpty(failReason2);
        return partialFailReason;
    }


    private ProductOfferAutoSubscriptionRelData getProductOfferAutoSubscription(String autoSubscriptionId, String addOnProductOfferId,String advancedCondition) {
        ProductOfferAutoSubscriptionRelData productOfferAutoSubscription = new ProductOfferAutoSubscriptionRelData();

        productOfferAutoSubscription.setId(autoSubscriptionId);
        ProductOfferData addOnOffer = new ProductOfferData();
        addOnOffer.setId(addOnProductOfferId);
        addOnOffer.setGroups(GROUP_1);
        productOfferAutoSubscription.setAddOnProductOfferData(addOnOffer);
        productOfferAutoSubscription.setAdvanceCondition(advancedCondition);
        return  productOfferAutoSubscription;
    }

    private List<ProductOfferAutoSubscription> createProductOfferAutoSubscriptionRel(List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelList){

        List<ProductOfferAutoSubscription> productOfferAutoSubscriptionRels = new ArrayList<>();

        for(ProductOfferAutoSubscriptionRelData rel: productOfferAutoSubscriptionRelList){
            ProductOfferAutoSubscription productOfferServicePkgRel = null;
            productOfferServicePkgRel = new ProductOfferAutoSubscription(
                    rel.getId(),
                    null,
                    rel.getAdvanceCondition(),
                    rel.getAddOnProductOfferId(),
                    policyRepository,null
            );
            productOfferAutoSubscriptionRels.add(productOfferServicePkgRel);
        }

        return productOfferAutoSubscriptionRels;
    }

}
