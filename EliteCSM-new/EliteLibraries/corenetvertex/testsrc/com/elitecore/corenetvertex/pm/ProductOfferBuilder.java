package com.elitecore.corenetvertex.pm;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferAutoSubscriptionRelData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;

public class ProductOfferBuilder {

    public static final String GROUP_1 = "Group1";
    public static final String CURRENCY = "INR";


    public static ProductOfferData newBaseOfferWithDefaultValues() {
        ProductOfferData productOffer = createBaseProductOffer();
        return productOffer;
    }


    public static ProductOfferData newBaseOfferWithDataPackage(String pkgId) {
        ProductOfferData productOffer = createBaseProductOffer();
        productOffer.setDataServicePkgId(pkgId);
        return productOffer;
    }


    private static ProductOfferData createBaseProductOffer() {
        ProductOfferData productOfferData = new ProductOfferData();
        productOfferData.setName("BaseOfferTest");
        productOfferData.setGroups(GROUP_1);
        productOfferData.setType("BASE");
        productOfferData.setId("1");
        productOfferData.setStatus(PkgStatus.ACTIVE.name());
        productOfferData.setPackageMode(PkgMode.DESIGN.name());
        productOfferData.setCurrency(CURRENCY);
        return productOfferData;
    }
}
