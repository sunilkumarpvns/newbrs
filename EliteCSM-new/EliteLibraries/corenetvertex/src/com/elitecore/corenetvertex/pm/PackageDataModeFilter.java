package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;

public class PackageDataModeFilter implements Predicate<PkgData> {

	private static PackageDataModeFilter modeFilter;

	private PackageDataModeFilter() {
	}

	static {
		modeFilter = new PackageDataModeFilter();
	}

	public static PackageDataModeFilter getInstance() {
		return modeFilter;
	}

	@Override
	public boolean apply(PkgData pkgData) {
		/*
		 * Only DESIGN mode package should not skipped.
		 *  
		 */
		
		return (PkgMode.DESIGN.name().equalsIgnoreCase(pkgData.getPackageMode())) == false;
	}
}
