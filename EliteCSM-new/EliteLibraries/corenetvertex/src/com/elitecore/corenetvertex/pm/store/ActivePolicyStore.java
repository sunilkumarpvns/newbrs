package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ActivePolicyStore<T extends Package> implements PolicyStore<T> {

    @Nonnull private DefaultDataPolicyStore<T> store;
    @Nonnull private DefaultDataPolicyStore<T> livePolicyStore;

    public ActivePolicyStore() {
        this.store = PolicyStore.<T>from(pkg -> PkgStatus.ACTIVE == pkg.getAvailabilityStatus());
        this.livePolicyStore = PolicyStore.<T>from(input -> (PkgMode.LIVE == input.getMode()) || PkgMode.LIVE2 == input.getMode());
    }

    public void create(@Nonnull List<? extends Package> newPackageList) {
        store.create(newPackageList);
        livePolicyStore.create(store.all());
    }

    public DefaultDataPolicyStore<T> live() {
        return livePolicyStore;
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
}
