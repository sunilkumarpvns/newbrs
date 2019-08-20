package com.elitecore.corenetvertex.pm.store;

import static com.elitecore.corenetvertex.pm.store.PolicyStore.fromImsFilter;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

public class IMSPolicyStore<T extends IMSPackage> implements PolicyStore<T> {

    @Nonnull
    private DefaultIMSStore<T> store;
    @Nonnull
    private ActiveIMSPolicyStore<T> activeIMSPolicyStore;

    IMSPolicyStore() {
        this.store = fromImsFilter(pkg->true);
        this.activeIMSPolicyStore = new ActiveIMSPolicyStore<>();
    }

    public void create(@Nonnull List<IMSPackage> imsPackages) {
        store.create(imsPackages);
        activeIMSPolicyStore.create(store.all());

    }

    @Override
    @Nullable
    public T byId(String id) {
        return store.byId(id);
    }

    @Override
    @Nullable
    public T byName(String name) {
        return store.byName(name);
    }

    @Override
    @Nonnull
    public List<T> all() {
        return store.all();
    }

    @Nonnull
    public ActiveIMSPolicyStore<T> active() {
        return activeIMSPolicyStore;
    }
}
