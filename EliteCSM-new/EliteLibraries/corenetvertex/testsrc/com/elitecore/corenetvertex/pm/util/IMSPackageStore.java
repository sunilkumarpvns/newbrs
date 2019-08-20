package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IMSPackageStore {

    public Map<String, IMSPackage> idToPackage;
    public Map<String, IMSPackage> nameToPackage;
    public List<IMSPackage> packages;

    public IMSPackageStore() {
        idToPackage = new ConcurrentHashMap<String, IMSPackage>();
        nameToPackage = new ConcurrentHashMap<String, IMSPackage>();
        packages = new ArrayList<>();
    }

    public void add(IMSPackage imsPackage) {
        packages.add(imsPackage);
        idToPackage.put(imsPackage.getId(), imsPackage);
        nameToPackage.put(imsPackage.getName(), imsPackage);
    }

    public IMSPackage getById(String id) {
        return this.idToPackage.get(id);
    }

    public IMSPackage getByName(String name) {
        return this.nameToPackage.get(name);
    }

    public List<IMSPackage> all() {
        return packages;
    }
}
