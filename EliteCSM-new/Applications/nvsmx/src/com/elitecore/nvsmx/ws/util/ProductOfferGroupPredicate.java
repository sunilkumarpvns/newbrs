package com.elitecore.nvsmx.ws.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ProductOfferGroupPredicate implements Predicate<ProductOffer> {
    private Map<String,String> idWiseGroupNameMap;

    public ProductOfferGroupPredicate(Map<String,String>  idWiseGroupNameMap){
        this.idWiseGroupNameMap=idWiseGroupNameMap;
    }

    public static ProductOfferGroupPredicate create(Map<String,String>  idWiseGroupNameMap){
        return new ProductOfferGroupPredicate(idWiseGroupNameMap);
    }

    @Override
    public boolean test(ProductOffer productOffer) {
        List<String> groupDataList = Collectionz.newArrayList();
        if(idWiseGroupNameMap == null){
            return true;
        }
        for (Map.Entry<String, String> groupData : idWiseGroupNameMap.entrySet()) {
            if (productOffer.getGroups().stream().anyMatch(s -> groupData.getKey().contains(s))){
                groupDataList.add(groupData.getValue());
            }
        }
        return !(groupDataList.isEmpty());
    }

}
