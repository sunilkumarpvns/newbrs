package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class DDFEntryConfiguration implements ToStringable {
        private String pattern;
        private SubscriberRepositoryConfiguration configuration;


    public DDFEntryConfiguration(String pattern, SubscriberRepositoryConfiguration configuration) {
        this.pattern = pattern;
        this.configuration = configuration;
    }

    public SubscriberRepositoryConfiguration getSPRConfiguration() {
        return configuration;
    }

    public String getPattern() {
        return pattern;
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.append("Pattern", pattern);
        out.appendChildObject("SPR", configuration);
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- DDF Entry -- ");
        toString(builder);
        return builder.toString();
    }
}
