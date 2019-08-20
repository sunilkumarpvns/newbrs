package com.elitecore.nvsmx.ws.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;

import java.util.function.Predicate;
import java.util.List;
import java.util.Map;

public class RncPackageGroupPredicate implements Predicate<RnCPackage> {
    private Map<String,String> idWiseGroupNameMap;

    public RncPackageGroupPredicate(Map<String,String>  idWiseGroupNameMap){
        this.idWiseGroupNameMap=idWiseGroupNameMap;
    }

    public static RncPackageGroupPredicate create(Map<String,String>  idWiseGroupNameMap){
        return new RncPackageGroupPredicate(idWiseGroupNameMap);
    }

    @Override
    public boolean test(RnCPackage rnCPackage) {
        List<String> groupDataList = Collectionz.newArrayList();
        if(idWiseGroupNameMap == null){
            return true;
        }
        for (Map.Entry<String, String> groupData : idWiseGroupNameMap.entrySet()) {
            if (rnCPackage.getGroupIds().stream().anyMatch(s -> groupData.getKey().contains(s))){
                groupDataList.add(groupData.getValue());
            }
        }
        return !(groupDataList.isEmpty());
    }

}
