package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;

import java.util.function.Predicate;

public class RncPackageModePredicate implements Predicate<RnCPackage> {

    private PkgMode pkgMode;

    public RncPackageModePredicate(PkgMode pkgMode) {
        this.pkgMode = pkgMode;
    }

    public static RncPackageModePredicate create(PkgMode pkgMode){
        return new RncPackageModePredicate(pkgMode);
    }

    @Override
    public boolean test(RnCPackage rnCPackage) {
        if(pkgMode == null){
            return true;
        }
        if(rnCPackage.getPackageMode() == null){
            return false;
        }
        return rnCPackage.getPackageMode().getOrder() >= pkgMode.getOrder();
    }
}
