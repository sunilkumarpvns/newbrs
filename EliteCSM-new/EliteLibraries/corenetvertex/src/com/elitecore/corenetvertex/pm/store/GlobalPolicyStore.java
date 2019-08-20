package com.elitecore.corenetvertex.pm.store;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;

public class GlobalPolicyStore<T extends Package> implements PolicyStore<T> {

    @Nonnull
    private final Predicate<Package> packageFilter;
    @Nonnull
    private final Predicate<GroupManageOrder> groupManageOrderFilter;

    @Nonnull
    private List<T> packages;
    @Nonnull
    private Map<String, ArrayList<T>> groupWisePackages;
    @Nonnull
    private Map<String, T> byId;
    @Nonnull
    private Map<String, T> byName;

    GlobalPolicyStore(@Nonnull Predicate<Package> packageFilter, @Nonnull Predicate<GroupManageOrder> manageOrderFilter) {
        this.packageFilter = packageFilter;
        this.groupManageOrderFilter = manageOrderFilter;
        this.packages = new ArrayList<>();
        this.groupWisePackages = new HashMap<>();
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
    }

    public void create(@Nonnull List<? extends Package> packages, @Nonnull List<GroupManageOrder> manageOrders) {

        List<T> newPackages = new ArrayList<>();
        Map<String, T> newById = new HashMap<>();
        Map<String, T> newByName = new HashMap<>();
        HashMap<String, ArrayList<T>> newGroupWisePackages = new HashMap<>();
        HashMap<String, List<GroupManageOrder>> groupWiseGroupManageOrders = new HashMap<>();

        manageOrders.stream().filter(groupManageOrderFilter).forEach(order -> addGroupWiseManageOrder(groupWiseGroupManageOrders, order));

        packages.stream().filter(packageFilter).map(pkg -> (T) pkg).forEach(pkg -> {

            newPackages.add(pkg);

            List<String> groupIds = pkg.getGroupIds();
            Map<String, Comparator<T>> comparatorByGroupId = Maps.newHashMap();

            groupIds.forEach(groupId -> {
                Comparator<T> comparator = getComparator(groupId, comparatorByGroupId, groupWiseGroupManageOrders);
                addGroupWisePackages(groupId, pkg, newGroupWisePackages, comparator);
            });
        });

        newPackages.forEach(pkg -> {
            newById.put(pkg.getId(), pkg);
            newByName.put(pkg.getName(), pkg);
        });

        this.packages = newPackages;
        this.byId = newById;
        this.byName = newByName;
        this.groupWisePackages = newGroupWisePackages;
    }

    private void addGroupWisePackages(String groupId,
                                      T pkg,
                                      HashMap<String, ArrayList<T>> groupWisePackages,
                                      Comparator<T> comparator) {

        ArrayList<T> tempPackages = groupWisePackages.get(groupId);
        if (tempPackages == null) {
            tempPackages = new ArrayList<>();
            tempPackages.add(pkg);
            groupWisePackages.put(groupId, tempPackages);
        } else {
            tempPackages.add(pkg);
            tempPackages.sort(comparator);
        }
    }

    private Comparator<T> getComparator(String groupId,
                                        Map<String, Comparator<T>> comparatoryByGroupId,
                                        HashMap<String, List<GroupManageOrder>> groupWiseGroupManageOrders) {

        Comparator<T> comparator = comparatoryByGroupId.get(groupId);
        if (comparator == null) {
            comparator = new GroupWiseOrderNumComparator(groupId, groupWiseGroupManageOrders);
            comparatoryByGroupId.put(groupId, comparator);
        }

        return comparator;
    }

    private void addGroupWiseManageOrder(HashMap<String, List<GroupManageOrder>> groupWiseGroupManageOrders, GroupManageOrder order) {
        List<GroupManageOrder> tempGroupManageOrders = groupWiseGroupManageOrders.computeIfAbsent(order.getGroupId(), k -> new ArrayList<>());
        tempGroupManageOrders.add(order);
    }

    private class GroupWiseOrderNumComparator implements Comparator<T> {

        private String groupId;
        private HashMap<String, List<GroupManageOrder>> groupWiseGroupManageOrders;

        GroupWiseOrderNumComparator(String groupId, HashMap<String, List<GroupManageOrder>> groupWiseGroupManageOrders) {
            this.groupId = groupId;
            this.groupWiseGroupManageOrders = groupWiseGroupManageOrders;
        }

        @Override
        public int compare(Package o1, Package o2) {

            int orderNumber1 = getOrderNumber(o1);
            int orderNumber2 = getOrderNumber(o2);

            if (orderNumber1 < orderNumber2) {
                return -1;
            } else if (orderNumber1 > orderNumber2) {
                return 1;
            }
            return 0;
        }

        private int getOrderNumber(Package pkg) {

            List<GroupManageOrder> groupOrders = groupWiseGroupManageOrders.get(groupId);

            if (isNullOrEmpty(groupOrders)) {
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

    @Override
    @Nonnull
    public List<T> all() {
        return packages;
    }

    @Nullable
    public List<T> byGroupId(String groupId) {
        return groupWisePackages.get(groupId);
    }

    @Override
    @Nullable
    public T byId(String id) {
        return byId.get(id);
    }

    @Override
    @Nullable
    public T byName(String name) {
        return byName.get(name);
    }
}
