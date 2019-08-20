package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.util.StringMatcher;

import java.util.List;

/**
 * DDFEntry contains single entry of (identity pattern,
 * SubscriberRepository)
 *
 * @author Chetan.Sankhala
 */
public class DDFEntry {

    private String identityPatternsStr;
    private List<char[]> identityPatterns;
    private SubscriberRepository subscriberRepository;

    public DDFEntry(List<char[]> identityPatterns, String identityPatternStr, SubscriberRepository subscriberRepository) {
        this.identityPatterns = identityPatterns;
        this.identityPatternsStr = identityPatternStr;
        this.subscriberRepository = subscriberRepository;
    }

    public String getIdentityPattern() {
        return identityPatternsStr;
    }

    public SubscriberRepository getSubscriberRepository() {
        return subscriberRepository;
    }

    public boolean isApplicable(String identityValue) {

        if (Collectionz.isNullOrEmpty(identityPatterns) == true) {
            return true;
        }

        for (int i = 0; i < identityPatterns.size(); i++) {
            if (StringMatcher.matches(identityValue, identityPatterns.get(i))) {
                return true;
            }
        }

        return false;
    }
}