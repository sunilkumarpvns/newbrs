package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;

public class IMSPackageDataModeFilter implements Predicate<IMSPkgData> {

	private static IMSPackageDataModeFilter modeFilter;

	private IMSPackageDataModeFilter() {
	}

	static {
		modeFilter = new IMSPackageDataModeFilter();
	}

	public static IMSPackageDataModeFilter getInstance() {
		return modeFilter;
	}

	@Override
	public boolean apply(IMSPkgData imsPkgData) {
		/*
		 * Only DESIGN mode package should not skipped.
		 *  
		 */
		return (PkgMode.DESIGN.name().equalsIgnoreCase(imsPkgData.getPackageMode())) == false;
	}
}
