package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultProductOfferStore<T extends ProductOffer> implements PolicyStore<T> {
    @Nonnull
    private final Predicate<ProductOffer> filter;
    @Nonnull
    private Map<String, T> byName;
    @Nonnull
    private Map<String, T> byId;
    @Nonnull
    private List<T> productOffers;

    DefaultProductOfferStore(Predicate<ProductOffer> filter) {
        this.filter = filter;
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
        this.productOffers = new ArrayList<>();
    }

    public void create(@Nonnull List<T> createdOffers) {

        List<T> tempList = new ArrayList<>();
        HashMap<String, T> byNameTemp = new HashMap<>();
        HashMap<String, T> byIdTemp = new HashMap<>();

        createdOffers.stream().filter(filter).map(t -> (T) t).forEach(pkg -> {
            tempList.add(pkg);
            byIdTemp.put(pkg.getId(), pkg);
            byNameTemp.put(pkg.getName(), pkg);
        });

        this.productOffers = tempList;
        this.byId = byIdTemp;
        this.byName = byNameTemp;
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
        return productOffers;
    }
}
