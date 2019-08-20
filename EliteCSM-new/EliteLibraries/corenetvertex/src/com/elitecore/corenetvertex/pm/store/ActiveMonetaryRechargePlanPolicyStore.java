package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;

import javax.annotation.Nonnull;
import java.util.List;

public class ActiveMonetaryRechargePlanPolicyStore<T extends MonetaryRechargePlan> implements PolicyStore<T>  {
    @Nonnull
    private DefaultMonetaryRechargePlanPolicystore<T> store;
    @Nonnull
    private DefaultMonetaryRechargePlanPolicystore<T> livePolicyStore;

    ActiveMonetaryRechargePlanPolicyStore() {
        this.store = new DefaultMonetaryRechargePlanPolicystore<>(pkg -> PkgStatus.ACTIVE == pkg.getAvailabilityStatus());
        this.livePolicyStore = new DefaultMonetaryRechargePlanPolicystore<>(input -> (PkgMode.LIVE == input.getMode()) || PkgMode.LIVE2 == input.getMode());
    }

    public void create(@Nonnull List<? extends MonetaryRechargePlan> monetaryRechargePlans) {
        store.create(monetaryRechargePlans);
        livePolicyStore.create(store.all());
    }

    @Override
    public T byId(String id) {
        return store.byId(id);
    }

    @Override
    public T byName(String name) {
        return store.byName(name);
    }

    @Override
    public List<T> all() {
        return store.all();
    }

    @Nonnull
    public DefaultMonetaryRechargePlanPolicystore<T> live() {
        return livePolicyStore;
    }
}
