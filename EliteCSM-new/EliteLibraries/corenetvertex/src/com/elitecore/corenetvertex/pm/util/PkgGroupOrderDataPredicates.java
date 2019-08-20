package com.elitecore.corenetvertex.pm.util;

import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;

public class PkgGroupOrderDataPredicates {

	public static Predicate<PkgGroupOrderData> createGroupFilter(String... groupIds) {
		return GroupIdFilter.create(Arrays.asList(groupIds));
	}
	
	public static Predicate<PkgGroupOrderData> createGroupFilter(List<String> groupIds) {
		return GroupIdFilter.create(groupIds);
	}

	public static Predicate<PkgGroupOrderData> createPackageNameFilter(String... packageNames) {
		return PackageNameFilter.create(Arrays.asList(packageNames));
	}

	
	private static class GroupIdFilter implements Predicate<PkgGroupOrderData> {
		private List<String> groupIds;

		private GroupIdFilter(List<String> groupIds) {
			this.groupIds = groupIds;
		}

		@Override
		public boolean apply(PkgGroupOrderData pkgGroupOrderData) {

			for (String groupId : groupIds) {
				if (pkgGroupOrderData.getGroupId().equals(groupId)) {
					return true;
				}
			}

			return false;
		}

		public static GroupIdFilter create(List<String> groupIds) {
			return new GroupIdFilter(groupIds);
		}
	}
	
	private static class PackageNameFilter implements Predicate<PkgGroupOrderData> {
		private List<String> packageNames;

		private PackageNameFilter(List<String> packageNames) {
			this.packageNames = packageNames;
		}

		@Override
		public boolean apply(PkgGroupOrderData pkgGroupOrderData) {

			for (String packageName : packageNames) {
				if (packageName.equals(pkgGroupOrderData.getPkgData().getName())) {
					return true;
				}
			}

			return false;
		}

		public static PackageNameFilter create(List<String> packageNames) {
			return new PackageNameFilter(packageNames);
		}
	}
}
