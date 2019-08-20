package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class AddOnProductOfferStore<T extends ProductOffer> implements PolicyStore<T> {
    @Nonnull
    private DefaultProductOfferStore<T> store;
    @Nonnull
    private ActiveProductOfferPolicyStore<T> activeProductOfferPolicyStore;

    AddOnProductOfferStore(@Nonnull Predicate<ProductOffer> filter) {
        this.store = new DefaultProductOfferStore<>(filter);
        this.activeProductOfferPolicyStore = new ActiveProductOfferPolicyStore<>();
    }

    public void create(@Nonnull List<T> productOffers) {
        store.create(productOffers);
        activeProductOfferPolicyStore.create(store.all());
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
    public ActiveProductOfferPolicyStore<T> active() {
        return activeProductOfferPolicyStore;
    }
}
