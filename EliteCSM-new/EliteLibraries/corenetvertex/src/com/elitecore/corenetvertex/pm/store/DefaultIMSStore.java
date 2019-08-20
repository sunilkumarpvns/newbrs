package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultIMSStore<T extends IMSPackage> implements PolicyStore<T> {

    @Nonnull
    private final Predicate<IMSPackage> filter;
    @Nonnull
    private Map<String, T> byName;
    @Nonnull
    private Map<String, T> byId;
    @Nonnull
    private List<T> packages;

    DefaultIMSStore(Predicate<IMSPackage> filter) {
        this.filter = filter;
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
        this.packages = new ArrayList<>();
    }

    public void create(@Nonnull List<? extends IMSPackage> packages) {

        List<T> newPackages = new ArrayList<>();
        HashMap<String, T> newById = new HashMap<>();
        HashMap<String, T> newByName = new HashMap<>();

        packages.stream().filter(filter).map(t -> (T) t).forEach(pkg -> {
            newPackages.add(pkg);
            newById.put(pkg.getId(), pkg);
            newByName.put(pkg.getName(), pkg);
        });

        this.packages = newPackages;
        this.byId = newById;
        this.byName = newByName;
    }

    @Nullable
    public T byId(String id) {
        return byId.get(id);
    }

    @Nullable
    public T byName(String name) {
        return byName.get(name);
    }

    @Nonnull
    public List<T> all() {
        return packages;
    }
}
