package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ActiveProductOfferPolicyStore<T extends ProductOffer> implements PolicyStore<T> {
    @Nonnull
    private DefaultProductOfferStore<T> store;
    @Nonnull
    private DefaultProductOfferStore<T> livePolicyStore;

    ActiveProductOfferPolicyStore() {
        this.store = new DefaultProductOfferStore<>(pkg -> PkgStatus.ACTIVE == pkg.getStatus());
        this.livePolicyStore = new DefaultProductOfferStore<>(input -> (PkgMode.LIVE == input.getPackageMode()) || PkgMode.LIVE2 == input.getPackageMode());
    }

    public void create(@Nonnull List<T> productOffers) {
        store.create(productOffers);
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

    @Nullable
    public List<T> all() {
        return store.all();
    }

    @Nonnull
    public DefaultProductOfferStore<T> live() {
        return livePolicyStore;
    }
}
