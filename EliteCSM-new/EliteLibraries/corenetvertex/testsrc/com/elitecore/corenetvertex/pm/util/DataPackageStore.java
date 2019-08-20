package com.elitecore.corenetvertex.pm.util;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by harsh on 5/18/17.
 */
public class DataPackageStore implements PolicyEventListener {

    public Map<String, Package> idToPackage;
    public Map<String, Package> nameToPackage;
    public List<Package> packages;


    public DataPackageStore() {
        idToPackage = new ConcurrentHashMap<String, Package>();
        nameToPackage = new ConcurrentHashMap<String, Package>();
        packages = new ArrayList<>();
    }

    @Override
    public void policyReloaded(List<Package> reloadedPackages) {
        for(Package pkg : reloadedPackages) {
            policyReloaded(pkg);
        }
    }

    @Override
    public void policyReloaded(Package pkg) {
        idToPackage.put(pkg.getId(), pkg);
        nameToPackage.put(pkg.getName(), pkg);
        packages.add(pkg);
    }

    public Package getByName(@Nonnull String name, @Nonnull Predicate<Package> predicate) {
        final Package aPackage = nameToPackage.get(name);
        return apply(predicate, aPackage);
    }

    public Package getByid(@Nonnull String id, @Nonnull Predicate<Package> predicate) {
        final Package aPackage = idToPackage.get(id);
        return apply(predicate, aPackage);
    }

    private Package apply(@Nonnull Predicate<Package> predicate, Package aPackage) {
        if(aPackage == null || predicate.apply(aPackage) == false) {
            return null;
        } else {
            return aPackage;
        }
    }

    public List<? extends Package> all(java.util.function.Predicate<Package> EMERGENCY) {
        return packages.stream().filter(EMERGENCY).collect(Collectors.toList());
    }

    public List<? extends Package> all() {
        return packages;
    }

}
