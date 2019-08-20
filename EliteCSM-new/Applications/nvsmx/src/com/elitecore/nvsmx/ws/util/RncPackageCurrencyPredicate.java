package com.elitecore.nvsmx.ws.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RncPackageCurrencyPredicate implements Predicate<RnCPackage> {
    private String currency;

    private RncPackageCurrencyPredicate(String currency){
        this.currency=currency;
    }

    public static RncPackageCurrencyPredicate create(String currency){
        return new RncPackageCurrencyPredicate(currency);
    }

    @Override
    public boolean test(RnCPackage rnCPackage) {
        if(StringUtils.isEmpty(currency) || currency.equals(rnCPackage.getCurrency()) == true){
            return true;
        }
        return false;
    }
}
