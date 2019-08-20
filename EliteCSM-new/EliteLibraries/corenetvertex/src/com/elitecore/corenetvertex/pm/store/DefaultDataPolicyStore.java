package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultDataPolicyStore<T extends Package> implements PolicyStore<T> {

    @Nonnull
    private final Predicate<Package> filter;
    @Nonnull
    private Map<String, T> byName;
    @Nonnull
    private Map<String, T> byId;
    @Nonnull
    private List<T> packages;

    DefaultDataPolicyStore(@Nonnull Predicate<Package> filter) {
        this.filter = filter;
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
        this.packages = new ArrayList<>();
    }

    public void create(@Nonnull List<? extends Package> packages) {

        List<T> newPackages = new ArrayList<>();
        HashMap<String, T> newById = new HashMap<>();
        HashMap<String, T> newByName = new HashMap<>();

        packages.stream().filter(filter).map(pkg -> (T) pkg).forEach(pkg -> {
            newPackages.add(pkg);
            newById.put(pkg.getId(), pkg);
            newByName.put(pkg.getName(), pkg);
        });

        this.packages = newPackages;
        this.byId = newById;
        this.byName = newByName;
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

    @Override
    @Nonnull
    public List<T> all() {
        return packages;
    }
}
