package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ChargingRuleBaseNameStore {

    @Nonnull
    final private Predicate<Package> filter;
    @Nonnull
    private Map<String, ChargingRuleBaseName> byId;
    @Nonnull
    private Map<String, ChargingRuleBaseName> byName;
    @Nonnull
    private List<ChargingRuleBaseName> chargingRuleBaseNames;

    ChargingRuleBaseNameStore() {
        this.filter = pkg -> PolicyStatus.FAILURE != pkg.getStatus();
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
        this.chargingRuleBaseNames = new ArrayList<>();
    }

    public void create(@Nonnull List<Package> packages) {
        List<ChargingRuleBaseName> newChargingRuleBaseNames = new ArrayList<>();
        Map<String, ChargingRuleBaseName> newById = new HashMap<>();
        Map<String, ChargingRuleBaseName> newByName = new HashMap<>();

        packages.stream().filter(filter).forEach(pkg -> pkg.getQoSProfiles().forEach(qoSProfile -> qoSProfile.getChargingRuleBaseNames().forEach(crbn -> {
            newChargingRuleBaseNames.add(crbn);
            newById.put(crbn.getId(), crbn);
            newByName.put(crbn.getName(), crbn);
        })));

        this.chargingRuleBaseNames = newChargingRuleBaseNames;
        this.byId = newById;
        this.byName = newByName;
    }

    public @Nullable
    ChargingRuleBaseName byId(String id) {
        return byId.get(id);
    }

    public @Nullable
    ChargingRuleBaseName byName(String name) {
        return byName.get(name);
    }

    public List<ChargingRuleBaseName> all() {
        return chargingRuleBaseNames;
    }
}
