package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.*;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import java.util.List;

/**
 * Created by harsh on 5/18/17.
 */
public interface PolicyEventListener {
    public void policyReloaded(List<Package> reloadedPackages);

    void policyReloaded(Package pkg);
}
