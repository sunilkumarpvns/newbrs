package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

/**
 * Created by harsh on 5/19/17.
 */
public class PackagePredicates {

    public static Predicate<Package> ADD_ON = new PackageType(PkgType.ADDON);
    public static Predicate<Package> BASE = new PackageType(PkgType.BASE);


    public Predicate<Package> add() {
        return ADD_ON;
    }


    private static class PackageType implements Predicate<Package> {

        private final PkgType pkgType;

        private PackageType(PkgType pkgType) {
            this.pkgType = pkgType;
        }

        @Override
        public boolean apply(Package pkg) {
            return pkg.getPackageType() == pkgType;
        }
    }
}
