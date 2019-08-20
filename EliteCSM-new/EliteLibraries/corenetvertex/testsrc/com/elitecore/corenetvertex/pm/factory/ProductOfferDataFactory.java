package com.elitecore.corenetvertex.pm.factory;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Deprecated
public class ProductOfferDataFactory {

    private String id;
    private String name;
    private String status;
    private String packageMode;
    private String type;
    private String dataServicePkgId;
    private String groups;

    public ProductOfferDataFactory(){

    }

    public ProductOfferDataFactory withId(String id){
        this.id = id;
        return this;
    }

    public ProductOfferDataFactory withGroups(String groups){
        this.groups = groups;
        return this;
    }

    public ProductOfferDataFactory withName(String name){
        this.name = name;
        return this;
    }

    public ProductOfferDataFactory withStatus(String status){
        this.status = status;
        return this;
    }

    public ProductOfferDataFactory withType(String type){
        this.type = type;
        return this;
    }

    public ProductOfferDataFactory withMode(String mode){
        this.packageMode = mode;
        return this;
    }

    public ProductOfferDataFactory withDataServicePkgId(String dataServicePkgId){
        this.dataServicePkgId = dataServicePkgId;
        return this;
    }

    public ProductOfferData build(){
        ProductOfferData productOfferData = new ProductOfferData();
        productOfferData.setId(id);
        productOfferData.setName(name);
        productOfferData.setType(type);
        productOfferData.setPackageMode(packageMode);
        productOfferData.setStatus(status);
        productOfferData.setDataServicePkgId(dataServicePkgId);
        productOfferData.setGroups(groups);
        return productOfferData;
    }

    public static ProductOffer createProductOffer(ProductOfferData productOfferData, PolicyStatus policyStatus, UserPackage dataPackage){

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
                createProductOfferServicePkgRel(productOfferData.getProductOfferServicePkgRelDataList()),null,
                Objects.nonNull(dataPackage)?dataPackage.getId():null,
                CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups()),
                productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(),
                policyStatus,
                null,null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(),
                productOfferData.getParam2(),
                PolicyManager.getInstance(),null,null,new HashMap<>(),productOfferData.getCurrency()
        );
    }

    private static List<ProductOfferServicePkgRel> createProductOfferServicePkgRel(List<ProductOfferServicePkgRelData> productOfferServicePkgRelData){

        List<ProductOfferServicePkgRel> productOfferServicePkgRels = new ArrayList<>();

        for(ProductOfferServicePkgRelData rel: productOfferServicePkgRelData){
            ProductOfferServicePkgRel productOfferServicePkgRel = new ProductOfferServicePkgRel(
                    rel.getId(),
                    null,
                    rel.getRncPackageId(),
                    PolicyManager.getInstance()
            );
            productOfferServicePkgRels.add(productOfferServicePkgRel);
        }

        return productOfferServicePkgRels;
    }
}
