package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import org.apache.commons.lang.StringUtils;

import java.util.function.Predicate;

public class ProductOfferCurrencyPredicate implements Predicate<ProductOffer> {
    private String currency;

    private ProductOfferCurrencyPredicate(String currency){
        this.currency=currency;
    }

    public static ProductOfferCurrencyPredicate create(String currency){
        return new ProductOfferCurrencyPredicate(currency);
    }

    @Override
    public boolean test(ProductOffer productOffer) {
        if(StringUtils.isEmpty(currency) || currency.equals(productOffer.getCurrency()) == true){
            return true;
        }
        return false;
    }
}
