package com.elitecore.corenetvertex.pm.pkg;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
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

public class PromotionalPackageStore extends PackageStore<PromotionalPackage> {

    private @Nonnull List<PromotionalPackage> promotionalPackages;
    private @Nonnull Map<String, PromotionalPackage> promotionalPackageById;
    private @Nonnull Map<String, PromotionalPackage> promotionalPackageByName;
    private @Nonnull Map<String, ArrayList<PromotionalPackage>> promotionalPackagesByGroupId;
	private @Nonnull Map<String, List<GroupManageOrder>> groupOrdersByGroupId;

    public PromotionalPackageStore(@Nonnull Map<String, List<GroupManageOrder>> groupOrdersByGroupId) {
        this.groupOrdersByGroupId = new HashMap<String, List<GroupManageOrder>>(groupOrdersByGroupId);;
		this.promotionalPackages = new ArrayList<PromotionalPackage>();
        this.promotionalPackageById = new HashMap<String, PromotionalPackage>();
        this.promotionalPackageByName = new HashMap<String, PromotionalPackage>();
        this.promotionalPackagesByGroupId = new HashMap<String, ArrayList<PromotionalPackage>>();
    }

    @Override
    public void add(PromotionalPackage promotionalPackage) {
        promotionalPackages.add(promotionalPackage);
        promotionalPackageById.put(promotionalPackage.getId(), promotionalPackage);
        promotionalPackageByName.put(promotionalPackage.getName(), promotionalPackage);

        Map<String, Comparator<PromotionalPackage>> comparatorByGroupId = Maps.newHashMap();
        
        List<String> groupIds = promotionalPackage.getGroupIds();
        
        for (int i=0; i < groupIds.size(); i++) {
	        String groupId = groupIds.get(i);

	        ArrayList<PromotionalPackage> promotionalPackages = promotionalPackagesByGroupId.get(groupId);

	        if (promotionalPackages == null) {
		        promotionalPackages = new ArrayList<PromotionalPackage>();
		        promotionalPackages.add(promotionalPackage);
		        promotionalPackagesByGroupId.put(groupId, promotionalPackages);
	        } else {
		        promotionalPackages.add(promotionalPackage);
		        Collections.sort(promotionalPackages, getComparator(groupId, comparatorByGroupId));
	        }

        }
    }

    @Override
    public @Nullable PromotionalPackage getById(String id) {
        return promotionalPackageById.get(id);
    }

    @Override
    public void removeById(@Nonnull String id) {
        PromotionalPackage removedPackage = this.promotionalPackageById.remove(id);

        if (removedPackage == null) {
            return;
        }

        this.promotionalPackages.remove(removedPackage);
        this.promotionalPackageByName.remove(removedPackage.getName());

        for (Entry<String, ArrayList<PromotionalPackage>> entry : promotionalPackagesByGroupId.entrySet()) {
	        ArrayList<PromotionalPackage> packages = entry.getValue();
	        packages.remove(removedPackage);
        }
    }

    @Override
    public Iterator<PromotionalPackage> iterator() {
        return promotionalPackages.iterator();
    }

    @Override
    public @Nullable PromotionalPackage getByName(@Nonnull String name) {
        return promotionalPackageByName.get(name);
    }

    public @Nonnull List<PromotionalPackage> getPromotionalPackages() {
        return promotionalPackages;
    }

    private Comparator<? super PromotionalPackage> getComparator(String groupId, Map<String, Comparator<PromotionalPackage>> comparatoryByGroupId) {

        Comparator<PromotionalPackage> comparator = comparatoryByGroupId.get(groupId);

        if (comparator == null) {
            comparator = new GroupWiseOrderNumComparator(groupId);
            comparatoryByGroupId.put(groupId, comparator);
        }

        return comparator;
    }

    private class GroupWiseOrderNumComparator implements Comparator<PromotionalPackage> {

		private String groupId;

		public GroupWiseOrderNumComparator(String groupId) {
			this.groupId = groupId;
		}

		@Override
		public int compare(PromotionalPackage o1, PromotionalPackage o2) {

			int orderNumber1 = getOrderNumber(o1);
			int orderNumber2 = getOrderNumber(o2);

			if (orderNumber1 < orderNumber2) {
				return -1;
			} else if (orderNumber1 > orderNumber2) {
				return 1;
			}
			return 0;
		}

		private int getOrderNumber(PromotionalPackage pkg) {
			
			List<GroupManageOrder> groupOrders = groupOrdersByGroupId.get(groupId);
			
			if (Collectionz.isNullOrEmpty(groupOrders)) {
				return 0;
			}
			
			for (int i=0; i < groupOrders.size(); i++) {
				GroupManageOrder groupManageOrder = groupOrders.get(i);
				if (pkg.getId().equals(groupManageOrder.getPackageId())) {
					return groupManageOrder.getOrderNumber();
				}
			}
			
			return 0;
		}

	}
    
    public Iterator<PromotionalPackage> getPackagesOfGroups(List<String> groupIds) {

        final Iterator<String> groupIdsIterator = groupIds.iterator();

        return new MultiCollectionIterator<PromotionalPackage>(new Supplier<Collection<PromotionalPackage>>() {
            @Override
            public Collection<PromotionalPackage> supply() {

	            while (groupIdsIterator.hasNext()) {

		            Collection<PromotionalPackage> collection = getPackagesOfGroup(groupIdsIterator.next());

		            if (Collectionz.isNullOrEmpty(collection)) {
			            continue;
		            }

		            return collection;
	            }
	            
                return null;
            }
        });
    }

    public @Nullable  ArrayList<PromotionalPackage> getPackagesOfGroup(String groupId) {
		return promotionalPackagesByGroupId.get(groupId);
    }
}
