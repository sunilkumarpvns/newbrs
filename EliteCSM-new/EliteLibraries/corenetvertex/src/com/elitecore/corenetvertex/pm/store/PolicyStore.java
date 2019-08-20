package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

import java.util.List;
import java.util.function.Predicate;

public interface PolicyStore<T> {

    T byId(String id);

    T byName(String name);

    List<T> all();

    static <T extends Package> DefaultDataPolicyStore from(Predicate<Package> filter) {
        return new DefaultDataPolicyStore<T>(filter);
    }

    static DefaultIMSStore fromImsFilter(Predicate<IMSPackage> filter) {
        return new DefaultIMSStore(filter);
    }

    static DefaultQuotaTopUpStore fromQuotaTopUpStore(Predicate<QuotaTopUp> filter) {
        return new DefaultQuotaTopUpStore(filter);
    }

    static DefaultMonetaryRechargePlanPolicystore fromMonetaryRechargePlanPolicystore(Predicate<MonetaryRechargePlan> filter) {
        return new DefaultMonetaryRechargePlanPolicystore(filter);
    }
}
