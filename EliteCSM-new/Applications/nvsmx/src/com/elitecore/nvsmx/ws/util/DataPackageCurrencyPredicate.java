package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import org.apache.commons.lang.StringUtils;

import java.util.function.Predicate;

public class DataPackageCurrencyPredicate implements Predicate<UserPackage> {
    private String currency;

    private DataPackageCurrencyPredicate(String currency){
        this.currency=currency;
    }

    public static DataPackageCurrencyPredicate create(String currency){
        return new DataPackageCurrencyPredicate(currency);
    }

    @Override
    public boolean test(UserPackage userPackage) {
        if(StringUtils.isEmpty(currency) || currency.equals(userPackage.getCurrency()) == true){
            return true;
        }
        return false;
    }
}
