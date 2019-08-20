package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MonetaryRechargePlanPolicyStore<T extends MonetaryRechargePlan> implements PolicyStore<T> {
    @Nonnull
    private DefaultMonetaryRechargePlanPolicystore<T> store;
    @Nonnull
    private ActiveMonetaryRechargePlanPolicyStore<T> activeMonetaryRechargePlanPolicyStore;

    MonetaryRechargePlanPolicyStore() {
        this.store = PolicyStore.fromMonetaryRechargePlanPolicystore(pkg->true);
        this.activeMonetaryRechargePlanPolicyStore = new ActiveMonetaryRechargePlanPolicyStore<>();
    }

    public void create(@Nonnull List<MonetaryRechargePlan> monetaryRechargePlans) {
        store.create(monetaryRechargePlans);
        activeMonetaryRechargePlanPolicyStore.create(store.all());

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
    @Nullable
    public List<T> all() {
        return store.all();
    }

    @Nonnull
    public ActiveMonetaryRechargePlanPolicyStore<T> active() {
        return activeMonetaryRechargePlanPolicyStore;
    }
}
