package com.elitecore.corenetvertex.pm.store;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

public class ActiveIMSPolicyStore<T extends IMSPackage> implements PolicyStore<T> {

    @Nonnull
    private DefaultIMSStore<T> store;
    @Nonnull
    private DefaultIMSStore<T> livePolicyStore;

    ActiveIMSPolicyStore() {
        this.store = new DefaultIMSStore<>(pkg -> PkgStatus.ACTIVE == pkg.getAvailabilityStatus());
        this.livePolicyStore = new DefaultIMSStore<>(input -> (PkgMode.LIVE == input.getMode()) || PkgMode.LIVE2 == input.getMode());
    }

    public void create(@Nonnull List<? extends IMSPackage> imsPackages) {
        store.create(imsPackages);
        livePolicyStore.create(store.all());
    }

    @Nullable
    public T byId(String id) {
        return store.byId(id);
    }

    @Nullable
    public T byName(String name) {
        return store.byName(name);
    }

    @Nonnull
    public List<T> all() {
        return store.all();
    }

    @Nonnull
    public DefaultIMSStore<T> live() {
        return livePolicyStore;
    }

}
