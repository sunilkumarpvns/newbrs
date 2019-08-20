package com.elitecore.corenetvertex.pm.store;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class BoDPackageStore {
        private static final String MODULE = "BOD-PACKAGE-STORE";

        private List<BoDPackage> boDPackages;
        @Nonnull private Map<String, BoDPackage> byName;
        @Nonnull private Map<String, BoDPackage> byId;

    public BoDPackageStore(){
        this.boDPackages = new ArrayList<>(2);
        this.byName = new HashMap<>();
        this.byId = new HashMap<>();
    }

    @Nullable public BoDPackage byId(String id) {
        return byId.get(id);
    }
    @Nullable public BoDPackage byName(String name) {
        return byName.get(name);
    }

    @Nonnull public List<BoDPackage> all() {
        return boDPackages;
    }

    public void create(@Nonnull List<BoDPackage> createPackage, @Nonnull List<String> deletedOrIncativePackages ){

        List<BoDPackage> tempList = new ArrayList(boDPackages);
        Map<String, BoDPackage> byNameTemp = new HashMap<>();
        Map<String, BoDPackage> byIdTemp = new HashMap<>();


        for(BoDPackage boDPackage : createPackage){
            boDPackage = getLastKnownGoodIfFailure(boDPackage);

            if(tempList.contains(boDPackage)==false){
                tempList.add(boDPackage);
            } else {
                int index = tempList.indexOf(boDPackage);
                tempList.set(index, boDPackage);
            }
        }

        Collectionz.filter(tempList, boDPkg-> deletedOrIncativePackages.contains(boDPkg.getId())==false);

        for(BoDPackage packageData : tempList){
            byNameTemp.put(packageData.getName(), packageData);
            byIdTemp.put(packageData.getId(), packageData);
        }

        boDPackages = tempList;
        byId = byIdTemp;
        byName = byNameTemp;
    }

    private BoDPackage getLastKnownGoodIfFailure(BoDPackage currentBoDPackage) {

        if(currentBoDPackage.getPolicyStatus()== PolicyStatus.SUCCESS || currentBoDPackage.getPolicyStatus()== PolicyStatus.PARTIAL_SUCCESS){
            return currentBoDPackage;
        }

        BoDPackage previousBoDPackage = byId.get(currentBoDPackage.getId());

        if (previousBoDPackage != null && previousBoDPackage.getPolicyStatus()!=PolicyStatus.FAILURE) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Used last known good configuration for BoD Package("
                        + currentBoDPackage.getName() + "). Reason: BoD Package Status is "
                        + currentBoDPackage.getPolicyStatus());
            }

            previousBoDPackage.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
            previousBoDPackage.setFailReason(currentBoDPackage.getFailReason());

            return previousBoDPackage;
        }
        return currentBoDPackage;

    }

}
