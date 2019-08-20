package com.elitecore.nvsmx.system.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class FailedDataPackageFilter implements Predicate<UserPackage>{

	private static FailedDataPackageFilter instance;
	
	public static FailedDataPackageFilter getInstance() {
		
		if (instance == null) {
			instance = new FailedDataPackageFilter();
		}
		return instance;
	}
	
	@Override
	public boolean apply(UserPackage pakage) {
		return pakage.getStatus() != PolicyStatus.FAILURE;
	}

	//FIXME Make User Package restricted getFilteredPackages 
	public static <T> List<T> getFilteredCopy(List<T> unfilteredPackages) {
		
		ArrayList<T> filteredPackages = Collectionz.newArrayList();
		if (Collectionz.isNullOrEmpty(unfilteredPackages) == false) {
			
			filteredPackages.addAll(unfilteredPackages);
			
			Iterator<T> iterator = filteredPackages.iterator();
			while (iterator.hasNext()) {
				if (FailedDataPackageFilter.getInstance().apply((UserPackage)iterator.next()) == false) {
					iterator.remove();
				}
			}
		}
		return filteredPackages;
		
	}
	
}
