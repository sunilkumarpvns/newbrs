package com.elitecore.nvsmx.ws.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DataPackageGroupPredicate implements Predicate<UserPackage> {
    private  Map<String,String> idWiseGroupNameMap;

    public DataPackageGroupPredicate(Map<String,String>  idWiseGroupNameMap){
        this.idWiseGroupNameMap=idWiseGroupNameMap;
    }

    public static DataPackageGroupPredicate create(Map<String,String>  idWiseGroupNameMap){
        return new DataPackageGroupPredicate(idWiseGroupNameMap);
    }

    @Override
    public boolean test(UserPackage userPackage) {
        List<String> groupDataList = Collectionz.newArrayList();
        if(idWiseGroupNameMap == null){
            return true;
        }
        for (Map.Entry<String, String> groupData : idWiseGroupNameMap.entrySet()) {
            if (userPackage.getGroupIds().stream().anyMatch(s -> groupData.getKey().contains(s))){
                groupDataList.add(groupData.getValue());
            }
        }
        return !(groupDataList.isEmpty());
    }

}
