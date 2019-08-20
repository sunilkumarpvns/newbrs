package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.util.MultiCollectionIterator;
import com.elitecore.corenetvertex.util.commons.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EmergencyPackageStore extends PackageStore<EmergencyPackage> {

	private @Nonnull List<EmergencyPackage> emergencyPackages;
	private @Nonnull Map<String, EmergencyPackage> emergencyPackageByName;
	private @Nonnull Map<String, EmergencyPackage> emergencyPackageById;
	private @Nonnull Map<String, ArrayList<EmergencyPackage>> emergencyPackagesByGroupId;
	private @Nonnull Map<String, List<GroupManageOrder>> groupManageOrderByGroupId;

	public EmergencyPackageStore(@Nonnull Map<String, List<GroupManageOrder>> groupManageOrderByGroupId) {
		this.groupManageOrderByGroupId = new HashMap<String, List<GroupManageOrder>>(groupManageOrderByGroupId);
		this.emergencyPackages = new ArrayList<EmergencyPackage>();
		this.emergencyPackageByName = new HashMap<String, EmergencyPackage>();
		this.emergencyPackageById = new HashMap<String, EmergencyPackage>();
		this.emergencyPackagesByGroupId = new HashMap<String, ArrayList<EmergencyPackage>>();
	}

	@Override
	public void add(EmergencyPackage emergencyPackage) {
		emergencyPackages.add(emergencyPackage);
		emergencyPackageById.put(emergencyPackage.getId(), emergencyPackage);
		emergencyPackageByName.put(emergencyPackage.getName(), emergencyPackage);

		List<String> groupIds = emergencyPackage.getGroupIds();
		Map<String, Comparator<EmergencyPackage>> comparatorByGroupId = Maps.newHashMap();


		for (int i = 0; i < groupIds.size(); i++) {
			String groupId = groupIds.get(i);

			ArrayList<EmergencyPackage> emergencyPackages = emergencyPackagesByGroupId.get(groupId);

			if (emergencyPackages == null) {
				emergencyPackages = new ArrayList<EmergencyPackage>();
				emergencyPackages.add(emergencyPackage);
				emergencyPackagesByGroupId.put(groupId, emergencyPackages);
			} else {
				emergencyPackages.add(emergencyPackage);
				Collections.sort(emergencyPackages, getComparator(groupId, comparatorByGroupId));
			}

		}
	}

	@Override
	public void removeById(String packageId) {
		EmergencyPackage removedPackage = this.emergencyPackageById.remove(packageId);

		if (removedPackage == null) {
			return;
		}

		this.emergencyPackages.remove(removedPackage);
		this.emergencyPackageByName.remove(removedPackage.getName());

		for (Entry<String, ArrayList<EmergencyPackage>> entry : emergencyPackagesByGroupId.entrySet()) {
			ArrayList<EmergencyPackage> packages = entry.getValue();
			packages.remove(removedPackage);
		}
	}

	private Comparator<? super EmergencyPackage> getComparator(String groupId, Map<String, Comparator<EmergencyPackage>> comparatoryByGroupId) {

		Comparator<EmergencyPackage> comparator = comparatoryByGroupId.get(groupId);

		if (comparator == null) {
			comparator = new GroupWiseOrderNumComparator(groupId);
			comparatoryByGroupId.put(groupId, comparator);
		}

		return comparator;
	}

	private class GroupWiseOrderNumComparator implements Comparator<EmergencyPackage> {

		private String groupId;

		public GroupWiseOrderNumComparator(String groupId) {
			this.groupId = groupId;
		}

		@Override
		public int compare(EmergencyPackage o1, EmergencyPackage o2) {

			int orderNumber1 = getOrderNumber(o1);
			int orderNumber2 = getOrderNumber(o2);

			if (orderNumber1 < orderNumber2) {
				return -1;
			} else if (orderNumber1 > orderNumber2) {
				return 1;
			}
			return 0;
		}

		private int getOrderNumber(EmergencyPackage pkg) {

			List<GroupManageOrder> groupOrders = groupManageOrderByGroupId.get(groupId);

			if (Collectionz.isNullOrEmpty(groupOrders)) {
				return 0;
			}

			for (int i = 0; i < groupOrders.size(); i++) {
				GroupManageOrder groupManageOrder = groupOrders.get(i);
				if (pkg.getId().equals(groupManageOrder.getPackageId())) {
					return groupManageOrder.getOrderNumber();
				}
			}

			return 0;
		}

	}

	public @Nonnull List<EmergencyPackage> getEmergencyPackages() {
		return emergencyPackages;
	}

	public @Nullable EmergencyPackage getByName(@Nonnull String name) {
		return emergencyPackageByName.get(name);
	}

	@Override
	public @Nullable EmergencyPackage getById(String id) {
		return emergencyPackageById.get(id);
	}

	@Override
	public @Nonnull	Iterator<EmergencyPackage> iterator() {
		return emergencyPackages.iterator();
	}

	public Iterator<EmergencyPackage> getPackagesOfGroups(List<String> groupIds) {

		final Iterator<String> groupIdsIterator = groupIds.iterator();

		return new MultiCollectionIterator<EmergencyPackage>(new Supplier<Collection<EmergencyPackage>>() {


			@Override
			public Collection<EmergencyPackage> supply() {

				while (groupIdsIterator.hasNext()) {

					Collection<EmergencyPackage> collection = getPackagesOfGroup(groupIdsIterator.next());

					if (Collectionz.isNullOrEmpty(collection)) {
						continue;
					}

					return collection;
				}

				return null;
			}
		});
	}

	public @Nullable ArrayList<EmergencyPackage> getPackagesOfGroup(String groupId) {
		return emergencyPackagesByGroupId.get(groupId);
	}
}