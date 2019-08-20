package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.elitecore.corenetvertex.pm.store.PolicyStore.fromQuotaTopUpStore;

public class QuotaTopUpPolicyStore <T extends QuotaTopUp> implements PolicyStore<T> {

    @Nonnull
    private DefaultQuotaTopUpStore<T> store;
    @Nonnull
    private ActiveQuotaTopUpPolicyStore<T> activeQuotaTopUpPolicyStore;

    QuotaTopUpPolicyStore() {
        this.store = fromQuotaTopUpStore(pkg->true);
        this.activeQuotaTopUpPolicyStore = new ActiveQuotaTopUpPolicyStore<>();
    }

    public void create(@Nonnull List<QuotaTopUp> quotaTopUps) {
        store.create(quotaTopUps);
        activeQuotaTopUpPolicyStore.create(store.all());

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
    public ActiveQuotaTopUpPolicyStore<T> active() {
        return activeQuotaTopUpPolicyStore;
    }
}
