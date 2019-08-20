package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PCCRuleStore {

    @Nonnull private final Predicate<Package> filter;
    @Nonnull private Map<String, PCCRule> byId;
    @Nonnull private Map<String, PCCRule> byName;
    @Nonnull private List<PCCRule> pccRules;

    PCCRuleStore() {
        this.filter = pkg -> PolicyStatus.FAILURE != pkg.getStatus();
        this.byId = new HashMap<>();
        this.byName = new HashMap<>();
        this.pccRules = new ArrayList<>();
    }

    public void create(@Nonnull List<Package> packagesAdded) {

        List<PCCRule> newPCCRules = new ArrayList<>();
        Map<String, PCCRule> newById = new HashMap<>();
        Map<String, PCCRule> newByName = new HashMap<>();

        packagesAdded.stream().filter(filter).forEach(pkg -> pkg.getQoSProfiles().forEach(qoSProfile -> qoSProfile.getPCCRules().forEach(pccRule -> {
            newPCCRules.add(pccRule);
            newById.put(pccRule.getId(), pccRule);
            newByName.put(pccRule.getName(), pccRule);
        })));

        this.pccRules = newPCCRules;
        this.byId = newById;
        this.byName = newByName;
    }

    @Nullable public PCCRule byId(String id) {
        return byId.get(id);
    }

    @Nullable public PCCRule byName(String name) {
        return byName.get(name);
    }

    @Nonnull public List<PCCRule> all() {
        return pccRules;
    }
}
