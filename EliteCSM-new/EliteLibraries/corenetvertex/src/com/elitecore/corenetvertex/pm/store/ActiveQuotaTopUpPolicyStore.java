package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;

import javax.annotation.Nonnull;
import java.util.List;

public class ActiveQuotaTopUpPolicyStore<T extends QuotaTopUp> implements PolicyStore<T> {

    @Nonnull
    private DefaultQuotaTopUpStore<T> store;
    @Nonnull
    private DefaultQuotaTopUpStore<T> livePolicyStore;

    ActiveQuotaTopUpPolicyStore() {
        this.store = new DefaultQuotaTopUpStore<>(pkg -> PkgStatus.ACTIVE == pkg.getAvailabilityStatus());
        this.livePolicyStore = new DefaultQuotaTopUpStore<>(input -> (PkgMode.LIVE == input.getMode()) || PkgMode.LIVE2 == input.getMode());
    }

    public void create(@Nonnull List<? extends QuotaTopUp> quotaTopUps) {
        store.create(quotaTopUps);
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
    public DefaultQuotaTopUpStore<T> live() {
        return livePolicyStore;
    }
}
