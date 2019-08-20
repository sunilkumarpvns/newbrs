package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import java.util.function.Predicate;

public class ProductOfferModePredicate implements Predicate<ProductOffer> {
    private PkgMode pkgMode;

    public ProductOfferModePredicate(PkgMode pkgMode) {
        this.pkgMode = pkgMode;
    }

    public static ProductOfferModePredicate create(PkgMode pkgMode){ return new ProductOfferModePredicate(pkgMode);}

    @Override
    public boolean test(ProductOffer productOffer) {
        if(pkgMode == null){
            return true;
        }
        if(productOffer.getPackageMode() == null){
            return false;
        }
        return productOffer.getPackageMode().getOrder() >= pkgMode.getOrder();
    }
}
