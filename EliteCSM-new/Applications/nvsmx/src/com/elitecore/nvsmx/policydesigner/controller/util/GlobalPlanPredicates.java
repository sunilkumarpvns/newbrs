package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Predicate;

import java.util.List;

/**
 * Created by aditya on 6/7/17.
 */
public class GlobalPlanPredicates {

    public static NonContainGroupPredicate createNonContainGroupPredicate(List<String> groupList) {
        return new NonContainGroupPredicate(groupList);
    }

    private static class NonContainGroupPredicate implements Predicate<String> {
        private final List<String> groupList;

        public NonContainGroupPredicate(List<String> groupList) {
            this.groupList = groupList;
        }

        public boolean apply(String group) {
            if (groupList.contains(group)) {
                return false;
            }
            return true;
        }
    }

}
