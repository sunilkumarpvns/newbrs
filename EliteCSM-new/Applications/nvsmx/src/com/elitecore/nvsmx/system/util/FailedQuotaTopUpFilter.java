package com.elitecore.nvsmx.system.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class FailedQuotaTopUpFilter implements Predicate<QuotaTopUp>{

	private static FailedQuotaTopUpFilter instance;
	
	public static FailedQuotaTopUpFilter getInstance() {
		
		if (instance == null) {
			instance = new FailedQuotaTopUpFilter();
		}
		return instance;
	}
	
	@Override
	public boolean apply(QuotaTopUp pakage) {
		return pakage.getStatus() != PolicyStatus.FAILURE;
	}

	public static <T> List<T> getFilteredCopy(List<T> unfilteredPackages) {
		
		ArrayList<T> filteredPackages = Collectionz.newArrayList();
		if (Collectionz.isNullOrEmpty(unfilteredPackages) == false) {
			
			filteredPackages.addAll(unfilteredPackages);
			
			Iterator<T> iterator = filteredPackages.iterator();
			while (iterator.hasNext()) {
				if (FailedQuotaTopUpFilter.getInstance().apply((QuotaTopUp) iterator.next()) == false) {
					iterator.remove();
				}
			}
		}
		return filteredPackages;
		
	}
	
}
