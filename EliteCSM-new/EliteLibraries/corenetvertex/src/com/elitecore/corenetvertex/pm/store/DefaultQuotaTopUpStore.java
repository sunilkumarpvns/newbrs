package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultQuotaTopUpStore<T extends QuotaTopUp> implements PolicyStore<T> {

    @Nonnull
    private final Predicate<QuotaTopUp> filter;
    @Nonnull
    private Map<String, T> byName;
    @Nonnull
    private Map<String, T> byId;
    @Nonnull
    private List<T> packages;

    DefaultQuotaTopUpStore(Predicate<QuotaTopUp> filter) {
        this.filter = filter;
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
        this.packages = new ArrayList<>();
    }

    public void create(@Nonnull List<? extends QuotaTopUp> packages) {

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

    @Override
    public T byId(String id) {
        return byId.get(id);
    }

    @Override
    public T byName(String name) {
        return byName.get(name);
    }

    @Override
    public List<T> all() {
        return packages;
    }
}
