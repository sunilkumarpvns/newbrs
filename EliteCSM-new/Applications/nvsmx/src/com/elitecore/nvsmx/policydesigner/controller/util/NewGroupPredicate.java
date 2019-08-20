package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Predicate;

import java.util.List;

/**
 * Created by jaidiptrivedi on 22/8/17.
 */
public class NewGroupPredicate implements Predicate<String> {

    private List<String> oldGroups;

    public NewGroupPredicate(List<String> oldGroups) {
        this.oldGroups = oldGroups;
    }

    @Override
    public boolean apply(String entityGroup) {
        return oldGroups.contains(entityGroup) == false;
    }
}