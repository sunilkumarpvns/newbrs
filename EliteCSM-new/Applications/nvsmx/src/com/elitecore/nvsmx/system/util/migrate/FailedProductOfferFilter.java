package com.elitecore.nvsmx.system.util.migrate;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ishani bhatt
 *
 */

public class FailedProductOfferFilter implements Predicate<ProductOffer> {

    private static FailedProductOfferFilter instance;

    public static FailedProductOfferFilter getInstance() {

        if (instance == null) {
            instance = new FailedProductOfferFilter();
        }
        return instance;
    }

    @Override
    public boolean apply(ProductOffer pakage) {
        return pakage.getPolicyStatus() != PolicyStatus.FAILURE;
    }

    //FIXME Make User Package restricted getFilteredPackages
    public static <T> List<T> getFilteredCopy(List<T> unfilteredPackages) {

        ArrayList<T> filteredPackages = Collectionz.newArrayList();
        if (Collectionz.isNullOrEmpty(unfilteredPackages) == false) {

            filteredPackages.addAll(unfilteredPackages);

            Iterator<T> iterator = filteredPackages.iterator();
            while (iterator.hasNext()) {
                if (FailedProductOfferFilter.getInstance().apply((ProductOffer) iterator.next()) == false) {
                    iterator.remove();
                }
            }
        }
        return filteredPackages;

    }

}


