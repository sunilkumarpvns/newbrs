package com.elitecore.nvsmx.ws.util;


import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;

import java.util.function.Predicate;

public class DataPackageModePredicate implements Predicate<UserPackage> {

    private PkgMode pkgMode;

    public DataPackageModePredicate(PkgMode pkgMode) {
        this.pkgMode = pkgMode;
    }

    public static DataPackageModePredicate create(PkgMode mode){
        return new DataPackageModePredicate(mode);
    }

    @Override
    public boolean test(UserPackage userPackage) {
        if(pkgMode == null){
            return true;
        }
        if(userPackage == null){
            return false;
        }
        return userPackage.getMode().getOrder() >= pkgMode.getOrder();
    }
}
