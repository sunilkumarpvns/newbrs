package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by aditya on 5/25/17.
 */
public class RMIGroupStore {
    private final @Nonnull Map<String, RMIGroup> netEngineServerCodeToGroup;
    private final @Nonnull List<RMIGroup> netvertexInstanceRMIGroups;

    public RMIGroupStore() {
        netvertexInstanceRMIGroups = Collectionz.newArrayList();
        netEngineServerCodeToGroup = Maps.newHashMap();
    }


    public boolean contains(@Nonnull String id) {
        for (RMIGroup rmiGroup : netvertexInstanceRMIGroups) {
            if (rmiGroup.id().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void addAll(Map<String, RMIGroup> toBeAdded) {

        for (Map.Entry<String, RMIGroup> netServerIdToGroup : toBeAdded.entrySet()) {
            if (netEngineServerCodeToGroup.containsKey(netServerIdToGroup.getKey()) == false) {
                netEngineServerCodeToGroup.put(netServerIdToGroup.getKey(), netServerIdToGroup.getValue());
            }
        }
    }

    private void addAll(@Nonnull Collection<RMIGroup> toBeAdded, @Nonnull Predicate<RMIGroup> rmiGroupPredicate) {
        for (RMIGroup rmiGroup : toBeAdded) {
            if (rmiGroupPredicate.apply(rmiGroup)) {
                netvertexInstanceRMIGroups.add(rmiGroup);
            }
        }
    }

    public void add(@Nonnull RMIGroup rmiGroup,
                    @Nonnull ServerInstanceData primaryServerInstanceData,
                    @Nullable ServerInstanceData secondaryServerInstanceData) {

        netEngineServerCodeToGroup.put(primaryServerInstanceData.getId(), rmiGroup);

        if (secondaryServerInstanceData != null) {
            netEngineServerCodeToGroup.put(secondaryServerInstanceData.getId(), rmiGroup);
        }

        netvertexInstanceRMIGroups.add(rmiGroup);
    }


    public @Nonnull Map<String, RMIGroup> getNetEngineServerCodeToGroup() {
        return netEngineServerCodeToGroup;
    }

    public @Nonnull List<RMIGroup> getNetvertexInstanceRMIGroups() {
        return netvertexInstanceRMIGroups;
    }

    public void addIfAbsent(Map<String, RMIGroup> codeToRMIGroup) {
        Predicate<RMIGroup> rmiGroupPredicate = input -> {

            for (RMIGroup rmiGroup : netvertexInstanceRMIGroups) {
                if (rmiGroup.id().equals(input.id())) {
                    return false;
                }
            }

            return true;
        };


        addAll(codeToRMIGroup.values(), rmiGroupPredicate);
        addAll(codeToRMIGroup);
    }
}
