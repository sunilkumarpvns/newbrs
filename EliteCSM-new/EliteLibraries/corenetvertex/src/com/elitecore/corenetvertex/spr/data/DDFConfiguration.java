package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.sm.ddf.DdfData;
import com.elitecore.corenetvertex.sm.ddf.DdfSprRelData;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparingInt;

public class DDFConfiguration implements ToStringable {

    private String id;
    private SubscriberRepositoryConfiguration defaultSPR;
    private List<DDFEntryConfiguration> ddfEntries;
    private String stripPrefixes;
    private FailReason failReason;

    public DDFConfiguration(String id,
                            SubscriberRepositoryConfiguration defaultSPR,
                            List<DDFEntryConfiguration> ddfEntries,
                            String stripPrefixes,
                            FailReason failReason) {
        this.id = id;
        this.defaultSPR = defaultSPR;
        this.ddfEntries = ddfEntries;
        this.stripPrefixes = stripPrefixes;
        this.failReason = failReason;
    }

    public String getId() {
        return id;
    }

    public SubscriberRepositoryConfiguration getDefaultSPR() {
        return defaultSPR;
    }


    public List<DDFEntryConfiguration> getDdfEntries() {
        return ddfEntries;
    }


    public String getStripPrefixes() {
        return stripPrefixes;
    }

    public boolean isSuccess() {
        return failReason.isEmpty() == false;
    }

    public FailReason getFailReason() {
        return failReason;
    }

    public static DDFConfiguration create(DdfData ddfData, DataSourceProvider dataSourceProvider) {

        FailReason failReason = new FailReason("DDF Table");

        SubscriberRepositoryConfiguration defaultSPRConf = null;
        if(ddfData.getDefaultSprData() == null) {
            failReason.add("No default configuration found");
        } else {
            FailReason defaultSPRFailReason = new FailReason("Default SPR Data");
            defaultSPRConf = SubscriberRepositoryConfiguration.create(ddfData.getDefaultSprData(), dataSourceProvider, defaultSPRFailReason);
            failReason.addChildModuleFailReasonIfNotEmpty(defaultSPRFailReason);
        }

        List<DDFEntryConfiguration> patternToSpr = new ArrayList<>();

        if(Collectionz.isNullOrEmpty(ddfData.getDdfSprRelDatas()) == false) {

            ddfData.getDdfSprRelDatas()
                    .stream()
                    .sorted(reverseOrder(comparingInt(DdfSprRelData::getOrderNo)))
                    .forEach(data -> {
                        FailReason sprFailReason = new FailReason("SPR Data");
                        SubscriberRepositoryConfiguration sprConf = SubscriberRepositoryConfiguration.create(data.getSprData(), dataSourceProvider, sprFailReason);
                        failReason.addChildModuleFailReasonIfNotEmpty(sprFailReason);

                        if (sprFailReason.isEmpty()) {
                            patternToSpr.add(new DDFEntryConfiguration(data.getIdentityPattern(), sprConf));
                        }

                    });

        }

        return new DDFConfiguration(ddfData.getDefaultSprDataId(), defaultSPRConf,patternToSpr, ddfData.getStripPrefixes(), failReason);
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- DDF Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.newline()
                .appendChildObject("Default SPR", defaultSPR)
                .append("Strip Prefixes", stripPrefixes)
                .appendChildObject("DDF Entries", ddfEntries)
                .appendChildObject("Fail Reasons", failReason);
    }
}
