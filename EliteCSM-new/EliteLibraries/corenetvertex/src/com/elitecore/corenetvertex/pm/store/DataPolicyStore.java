package com.elitecore.corenetvertex.pm.store;

import java.util.List;
import java.util.function.Predicate;

import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DataPolicyStore<T extends Package> implements PolicyStore<T> {

    @Nonnull private DefaultDataPolicyStore<T> store;
    @Nonnull private ActivePolicyStore<T> activePolicyStore;

    DataPolicyStore(Predicate<Package> filter) {
        store = PolicyStore.from(filter);
        this.activePolicyStore = new ActivePolicyStore<>();
    }

    public void create(@Nonnull List<Package> newPackageList) {
        store.create(newPackageList);
        activePolicyStore.create(store.all());
    }

    @Override
    @Nullable public T byId(String id) {
        return store.byId(id);
    }

    @Override
    @Nullable public T byName(String name) {
        return store.byName(name);
    }

    @Override
    @Nonnull public List<T> all() {
        return store.all();
    }

    @Nonnull public ActivePolicyStore<T> active() {
        return activePolicyStore;
    }
}
