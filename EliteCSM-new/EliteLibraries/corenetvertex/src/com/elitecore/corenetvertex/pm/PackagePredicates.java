package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

/**
 * Created by jaidiptrivedi on 19/5/17.
 */
public class PackagePredicates {

	public static final Predicate<Package> ADD_ON = new PackageType(PkgType.ADDON);
	public static final Predicate<Package> BASE = new PackageType(PkgType.BASE);
	public static final Predicate<Package> EMERGENCY = new PackageType(PkgType.EMERGENCY);

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

	public static Predicate<IMSPackage> filterId(String id) {
		return new IdFilterPredicate(id);
	}

	private static class IdFilterPredicate implements Predicate<IMSPackage> {

		private String imsPkgId;

		public IdFilterPredicate(String imsPkgId) {
			this.imsPkgId = imsPkgId;
		}

		@Override
		public boolean apply(IMSPackage input) {
			return input.getId().equals(imsPkgId) == false;
		}
	}
}
