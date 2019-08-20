package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.JMXConstant;

/**
 * Created by harsh on 6/15/16.
 */
public class SuccessFailResultPredicate implements Predicate<String> {

    private static final  SuccessFailResultPredicate PREDICATE = new SuccessFailResultPredicate();

    public static Predicate<String> getInstance() {
        return PREDICATE;
    }

    private SuccessFailResultPredicate() {

    }
    @Override
    public boolean apply(String s) {
        return JMXConstant.SUCCESS.equalsIgnoreCase(s) || JMXConstant.PARTIAL_SUCCESS.equalsIgnoreCase(s);
    }
}
