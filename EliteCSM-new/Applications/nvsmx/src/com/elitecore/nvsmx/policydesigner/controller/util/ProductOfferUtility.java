package com.elitecore.nvsmx.policydesigner.controller.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class ProductOfferUtility {
    public static boolean doesBelongToGroup(List<String> dataGroupIds, List<String> groupIdsFromRequest) {
        if (dataGroupIds == null || dataGroupIds.isEmpty()) {
            return false;
        }

        if (CollectionUtils.isEmpty(groupIdsFromRequest)) {
            return true;
        }

        for (String inputGroupId : groupIdsFromRequest) {
            if (dataGroupIds.contains(inputGroupId)) {
                return true;
            }
        }
        return false;
    }
}
