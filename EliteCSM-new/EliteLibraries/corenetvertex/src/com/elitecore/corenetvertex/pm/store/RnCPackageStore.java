package com.elitecore.corenetvertex.pm.store;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RnCPackageStore {

    private static final String MODULE = "RNC-PACKAGE-STORE";

    private List<RnCPackage> rncPackages;
    @Nonnull private Map<String, RnCPackage> byName;
    @Nonnull private Map<String, RnCPackage> byId;
    @Nonnull private Map<String, RnCPackage> baseById;
    @Nonnull private Map<String, RnCPackage> baseByName;

    public RnCPackageStore(){
        this.rncPackages = new ArrayList<>(2);
        this.byName = new HashMap<>();
        this.byId = new HashMap<>();
        this.baseByName = new HashMap<>();
        this.baseById = new HashMap<>();
    }

    @Nullable public RnCPackage byId(String id) {
        return byId.get(id);
    }

    @Nullable public RnCPackage byName(String name) {
        return byName.get(name);
    }

    @Nullable public RnCPackage baseById(String id) {
        return baseById.get(id);
    }

    @Nullable public RnCPackage baseByName(String name) {
        return baseByName.get(name);
    }

    @Nonnull public List<RnCPackage> all() {
        return rncPackages;
    }

    public void create(@Nonnull List<RnCPackage> createPackage,@Nonnull List<String> deletedPackages ){

        List<RnCPackage> tempList = new ArrayList(rncPackages);
        Map<String, RnCPackage> byNameTemp = new HashMap<>();
        Map<String, RnCPackage> byIdTemp = new HashMap<>();
        Map<String, RnCPackage> baseByIdTemp = new HashMap<>();
        Map<String, RnCPackage> baseByNameTemp = new HashMap<>();

        for(RnCPackage rnCPackage: createPackage){
            rnCPackage = getLastKnownGoodIfFailure(rnCPackage);

            if(tempList.contains(rnCPackage)==false){
                tempList.add(rnCPackage);
            } else {
                int index = tempList.indexOf(rnCPackage);
                tempList.set(index,rnCPackage);
            }
        }

        Collectionz.filter(tempList, rncPackage-> deletedPackages.contains(rncPackage.getId())==false);

        for(RnCPackage packageData : tempList){
            byNameTemp.put(packageData.getName(), packageData);
            byIdTemp.put(packageData.getId(), packageData);

            if(packageData.getPkgType()== RnCPkgType.BASE){
                baseByNameTemp.put(packageData.getName(),packageData);
                baseByIdTemp.put(packageData.getId(),packageData);
            }
        }

        rncPackages = tempList;
        byId = byIdTemp;
        byName = byNameTemp;
        baseById = baseByIdTemp;
        baseByName = baseByNameTemp;

    }

    private RnCPackage getLastKnownGoodIfFailure(RnCPackage currentPackage) {

        if(currentPackage.getPolicyStatus()== PolicyStatus.SUCCESS || currentPackage.getPolicyStatus()== PolicyStatus.PARTIAL_SUCCESS){
            return currentPackage;
        }

        RnCPackage previousPackage = byId.get(currentPackage.getId());

        if (previousPackage != null && previousPackage.getPolicyStatus()!=PolicyStatus.FAILURE) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Used last known good configuration for RnC Package(" + currentPackage.getName() + "). Reason: RnC Package Status is " + currentPackage.getPolicyStatus());
            }

            previousPackage.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
            previousPackage.setPartialFailReason(currentPackage.getPartialFailReason());
            previousPackage.setFailReason(currentPackage.getFailReason());

            return previousPackage;
        }
        return currentPackage;

    }
}
