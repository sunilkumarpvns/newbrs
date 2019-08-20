package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuotaTopUpStore {

    public Map<String, QuotaTopUp> idToPackage;
    public Map<String, QuotaTopUp> nameToPackage;
    public List<QuotaTopUp> packages;

    public QuotaTopUpStore() {
        idToPackage = new ConcurrentHashMap<String, QuotaTopUp>();
        nameToPackage = new ConcurrentHashMap<String, QuotaTopUp>();
        packages = new ArrayList<>();
    }

    public void add(QuotaTopUp quotaTopUp) {
        packages.add(quotaTopUp);
        idToPackage.put(quotaTopUp.getId(), quotaTopUp);
        nameToPackage.put(quotaTopUp.getName(), quotaTopUp);
    }

    public QuotaTopUp getById(String id) {
        return this.idToPackage.get(id);
    }

    public QuotaTopUp getByName(String name) {
        return this.nameToPackage.get(name);
    }

    public List<QuotaTopUp> all() {
        return packages;
    }
}
