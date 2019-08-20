package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.constants.CommonConstants;
import org.apache.commons.lang.StringUtils;

import java.util.function.Predicate;

public class AlternateIdActiveStatusPredicate implements Predicate<String> {

    private static AlternateIdActiveStatusPredicate instance = new AlternateIdActiveStatusPredicate();

    public static AlternateIdActiveStatusPredicate getInstance() {
        return instance;
    }

    @Override
    public boolean test(String status) {
        if(StringUtils.isEmpty(status)){
            return false;
        }
        return CommonConstants.STATUS_ACTIVE.equalsIgnoreCase(status);
    }
}
