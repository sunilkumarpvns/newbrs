package com.elitecore.nvsmx.system.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class FailedIMSPackageFilter implements Predicate<IMSPackage>{

	private static FailedIMSPackageFilter instance;
	
	public static FailedIMSPackageFilter getInstance() {
		
		if (instance == null) {
			instance = new FailedIMSPackageFilter();
		}
		return instance;
	}
	
	@Override
	public boolean apply(IMSPackage pakage) {
		return pakage.getStatus() != PolicyStatus.FAILURE;
	}

	public static List<IMSPackage> getFilteredCopy(List<IMSPackage> unfilteredPackages) {
		
		ArrayList<IMSPackage> filteredPackages = Collectionz.newArrayList();
		if (Collectionz.isNullOrEmpty(unfilteredPackages) == false) {
			
			filteredPackages.addAll(unfilteredPackages);
			
			Iterator<IMSPackage> iterator = filteredPackages.iterator();
			while (iterator.hasNext()) {
				if (FailedIMSPackageFilter.getInstance().apply(iterator.next()) == false) {
					iterator.remove();
				}
			}
		}
		return filteredPackages;
		
	}
	
}
